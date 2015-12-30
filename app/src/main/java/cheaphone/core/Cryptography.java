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

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

//This class involved cryptography over symmetric and asymmetric algorithms and authentication, over signing (asymmetric algorithm)
public class Cryptography {
	
	/*
	 * Class that encrypt, decrypt data from and to RSA (asymmetric) and AES (symmetric) algorithms.
	 * Also can verify data. The public key (to sign) of the server is hardcoded into the class!
	 * Can also create sha-1 hash of arbitrary data.
	 * Create an instance for each cryptography end point you need.
	 * 
	 * Bortoli Tomas 2014-2015
	 * 
	 * */
	MessageDigest sha1;
	
	//Key pair used for symmetric key exchange
	PublicKey puk;
	PrivateKey pvk;
	
	//Aes key
	SecretKey aesKey;
	//Aes ciphers
	Cipher aesEnc,aesDec;
	
	//Length of the keys
	final short rsaKeyLenBits=1024;
	final short aesKeyLenBits=128;
	
	//Modulus and exponent of the public key used for verify signing
	final BigInteger modulus=new BigInteger("141481861035130607011505549770096650440863673990900858849793390853478886225235528724629637723456080260407282172175466589443641525668484686444190880184067913310638157908495621332268960954738287034864331069425159539084463205289909954333401876114508208166786207172671579771329941608071138956292291345127145316887");
	final BigInteger e=new BigInteger("65537");
	
	
	//Object used to sign
	Signature sig;
	//Public key for verify signing, static cause is shared from all instances of cryptography class
	static PublicKey sigPuk;
	
	final String rsaCipher="RSA/NONE/PKCS1Padding";
	
	public Cryptography(){
		try {
			sha1 = MessageDigest.getInstance("SHA-1");
			
			aesEnc= Cipher.getInstance("AES");
			aesDec= Cipher.getInstance("AES");
			
			
			sig = Signature.getInstance("SHA1WithRSA");
			
			init_sig_pk();
			sig.initVerify(sigPuk);
			
		} catch (Exception e) {
			
		}
		
	}
	
	private static boolean init_sig=false;
	private void init_sig_pk() throws NoSuchAlgorithmException, InvalidKeySpecException{
		if(init_sig) return;
		
		RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(modulus,e);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        sigPuk = keyFactory.generatePublic(publicKeySpec);
        
        init_sig=true;
	}
	
	public boolean verifySign(byte[] message, byte[] signature) throws SignatureException{
		sig.update(message);
		return sig.verify(signature);
	}
	
	public String Sha1Hash(String s){
		
        try {
        	sha1.reset();
			sha1.update(s.getBytes("utf8"));
			return (new String(sha1.digest()));
		} catch (Exception e) {
			return null;
		}
        
	}
	
	public byte[] Sha1Hash(byte[] s){
		
        try {
        	sha1.reset();
			sha1.update(s);
			return (sha1.digest());
		} catch (Exception e) {
			return null;
		}
        
	}
	
	public void setupAes() throws Exception{
		aesEnc.init(Cipher.ENCRYPT_MODE, aesKey);
		aesDec.init(Cipher.DECRYPT_MODE, aesKey);
	}
	
	public void setPublicKeyFromString(String s){
		
		try {
			
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			KeySpec publicKeySpec = new X509EncodedKeySpec(s.getBytes());
			puk = keyFactory.generatePublic(publicKeySpec);
			
		} catch (Exception e) {
		}
		
		
	}
	
public void setPublicKeyFromString(byte[] a){
		
		try {
			
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			KeySpec publicKeySpec = new X509EncodedKeySpec(a);
			puk = keyFactory.generatePublic(publicKeySpec);
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		
	}
	
	/**
     * Generate an RSA key pair
     */
    public void genKeys() throws Exception {
    	
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(rsaKeyLenBits);
        KeyPair kp = kpg.generateKeyPair();
        pvk=kp.getPrivate();
        puk=kp.getPublic();
    }
    
    public byte[] generateAESKeyAndEncryptWithRSA() throws Exception {
        Cipher rsa = Cipher.getInstance(rsaCipher);

        // create new AES key
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(aesKeyLenBits);
        aesKey = gen.generateKey();
        
        // RSA encrypt AES key
        byte[] keyEnc = aesKey.getEncoded();
        rsa.init(Cipher.ENCRYPT_MODE, puk);
        byte[] keySec = rsa.doFinal(keyEnc);
        
        return keySec;
    }
    
    
    public String decryptRSA(String input) throws Exception {
        Cipher rsa = Cipher.getInstance(rsaCipher);
        rsa.init(Cipher.DECRYPT_MODE, pvk);
        return (rsa.doFinal(input.getBytes())).toString();
    }
    
    public byte[] decryptRSA(byte[] input) throws Exception {
        Cipher rsa = Cipher.getInstance(rsaCipher);
        rsa.init(Cipher.DECRYPT_MODE, pvk);
        return (rsa.doFinal(input));
    }
    
    
    public String encryptAES(String input) throws Exception{
    	return (aesEnc.doFinal(input.getBytes())).toString();
    }
    
    public byte[] encryptAES(byte[] input) throws Exception{
    	return (aesEnc.doFinal(input));
    }
    
    public String decryptAES(String input) throws Exception{
    	return (aesDec.doFinal(input.getBytes())).toString();
    }
    
    public byte[] decryptAES(byte[] input) throws Exception{
    	return (aesDec.doFinal(input));
    }
    

}

