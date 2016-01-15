/**

 Copyright 2014 Bortoli Tomas

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package cheaphone.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.spec.SecretKeySpec;

public class NetworkManager {
	/*
	 * 
	 * This class handles the networking. Protocol, connection establishment, key exchange, sign verification, everything about network. 
	 * 
	 * 
	 * 
	 * Developed by Bortoli Tomas
	 * 
	 * */
	
	/*Network protocol definition. In the server there are more infos*/
	public final static String TRANSLATE_NUMBERS_TO_OPERATORS="TRANSLATE_NUMBERS_TO_OPERATORS";
	public final static String UPDATE_FILE_OF_OFFERS="UPDATE_FILE_OF_OFFERS";
	public final static String OK="OK";
	public final static String SEPARATOR="~";
	
	public final static String filler=" ";
	/*******************************************************************/
	
	//handles the cryptography, sign verification, hash generation.
	static Cryptography c;
	
	//The network socket and the associated streams
	static Socket s;
	static DataOutputStream dataOut;
	static DataInputStream dataIn;
	
	//Random padding function create a random pad in length and content to make communications more indecipherable
	private static String randomPadding(){
		Random r=new Random();
		int len=(Math.abs(r.nextInt())%48)+16;
		
		char[] pad=new char[len];
		for(int i=0;i<len;i++)
			pad[i]=(char)((Math.abs(r.nextInt())%94)+32);
		
		return String.copyValueOf(pad);
	}
	

	
	//Function that handle the sending of a message, including cryptography. (does not handle exception, must be handled from external)
	/**
	 * The send message function add a crypto layer made of random padding around the original message to
	 * increase entropy. Then the message is encrypted and sent. receiveMessage do the inverse.
	 *
	 * @param message
	 * @throws Exception
	 */
	private static void sendMessage(String message) throws Exception{
		
		message=randomPadding()+SEPARATOR+message+SEPARATOR+randomPadding();

		//If not multiple of 32, fill with padding (could also be 16) ___	AES-256
		int rest=message.length()%32;
		if(rest!=0)	rest=32-rest;
		while(rest>0){
			message+=" ";
			rest--;
		}
		
		//encrypt the message
		byte[] enc=c.encryptAES(message.getBytes());
		
		//send the encrypted message
		dataOut.writeInt(enc.length);
	    dataOut.write(enc);
		
	}
		
	//Send a not encrypted message
	private static void sendMessageNotEncrypted(byte[] message) throws Exception{
		
		dataOut.writeInt(message.length);
	    dataOut.write(message);
	}
	
	//Function that receive a message, decrypt it, check if the sign is true, and return it!
	private static String receiveMessage() throws Exception{
		
		//byte[] temp=new byte[Integer.parseInt(in.readLine())];
		//for(int i=0;i<temp.length;i++) temp[i]=Byte.parseByte(in.readLine());
		byte[] message=null;
		int length = dataIn.readInt();                    // read length of incoming message
		if(length>0) {
		    message = new byte[length];
		    dataIn.readFully(message, 0, message.length); // read the message
		    
		}
		
		message=(c.decryptAES(message));
		
		if(c.verifySign(message, receiveMessageNotEncrypted())) {
			String msg=new String(message);
			return msg.substring(msg.indexOf(SEPARATOR)+1,msg.lastIndexOf(SEPARATOR));
		}
		else
			return null;
		
	}
	
	
		
	//Function that receive a message not encrypted
	private static byte[] receiveMessageNotEncrypted() throws NumberFormatException, IOException, SignatureException{
		
		byte[] message=null;
		int length = dataIn.readInt();                    // read length of incoming message
		if(length>0) {
		    message = new byte[length];
		    dataIn.readFully(message, 0, message.length); // read the message
		    
		}
		
		return message;
		
	}	
	
	private static boolean keyCaching(CacheOfCryptoKey key) throws Exception{
		
		boolean ret=false;
		
		byte[] k=key.getKey();
		if(k==null){
			k=new byte[16];
			for(int i=0;i<k.length;i++) k[i]=0;
		}
		else{
			
			ret=true;
			
			c.aesKey= new SecretKeySpec(k, 0, k.length, "AES");
			c.setupAes();
			
			k=key.getHash();
		}
		
		sendMessageNotEncrypted(k);
		
		return ret;
	}
	
	//initial Key exchange, client side
	private static void keyExchangeClientSide(CacheOfCryptoKey keyCache) throws Exception{
		//receive the public key, generate the aes key and send it encrypted by rsa
		byte[] key=receiveMessageNotEncrypted();
	    byte[] sign=receiveMessageNotEncrypted();
	    if(c.verifySign(key, sign))
	    	;//System.out.println("OK");
	    else
	    	return;
	    
	    
		c.setPublicKeyFromString(key);
		
		key=c.generateAESKeyAndEncryptWithRSA();
		
		sendMessageNotEncrypted(key);
		c.setupAes();
		
		keyCache.setKey(c.aesKey.getEncoded());
	}
	
	//Constructor, connect to the server, timeout is setted to 5 sec for the connection, and to 100 seconds for receive delay
	public NetworkManager(CacheOfCryptoKey key) throws Exception{
		//Connect
		s=new Socket();
		s.connect(new InetSocketAddress(Constants.serverIP, Constants.serverPort), 5000);
		
		//100 sec of max timeout
		this.s.setSoTimeout(100000);
		
		dataOut= new DataOutputStream(s.getOutputStream());
		dataIn = new DataInputStream(s.getInputStream());
		
		
		
		c=new Cryptography();
		
		if(!keyCaching(key))	keyExchangeClientSide(key);
	}
	
	//Close the connection to the server
	public void closeConnection() throws IOException{
		s.close();
	}
	
	//Ask to the server to convert the local numbers into operators
	public String[] numberToOperators(ArrayList<String> numbers) throws Exception{
		
		String message=TRANSLATE_NUMBERS_TO_OPERATORS+SEPARATOR;
		for(int i=0;i<numbers.size()-1;i++)
			message+=numbers.get(i)+SEPARATOR;
		message+=numbers.get(numbers.size()-1);
		
		sendMessage(message);
		
		message=receiveMessage().trim();
		
		return message.split(SEPARATOR);
	}
	
	//Update the file of offers, return true if new file downloaded, else false
	public boolean updateFileOfOffers() throws Exception{
		
		
		String offers="";
		
		try{
			offers=new Scanner( new File(
					Constants.applicationFilesPath+Constants.offersFilePath
					) ).useDelimiter("\\A").next();
		}
		catch(Exception e){}
		
		sendMessage(UPDATE_FILE_OF_OFFERS);
		
		
		sendMessageNotEncrypted(c.Sha1Hash(offers.getBytes()));
		
		String response=receiveMessage();
		
		if(response.contains(OK))
			return false;
		else{
			
			response=response.replaceAll(" ", "");
			int length=Integer.parseInt(response);
			response=receiveMessage();
			offers=response.substring(0,length);
			
			try{
				MainService.fileOfOffers.lock();
				
				//write the new file of offers
				PrintWriter out = new PrintWriter(Constants.applicationFilesPath+Constants.offersFilePath);
				out.write(offers);
				out.flush();
				out.close();
			}
			catch(Exception e){
			}
			finally{
				MainService.fileOfOffers.unlock();
			}
		}
		
		return true;
	}
}
