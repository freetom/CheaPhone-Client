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
package com.greatapplications.bestoffer;

import java.util.Calendar;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class MainService extends IntentService
{
	
	/*
	 * 
	 * Main Service class represent the main service of the app, 
	 * started at the boot up of the system and restarted every time a user open the app,
	 * or when the service is stopped. N.B if the user kill all the processes of the app,
	 * there's no way to restart the service. No other execution points.
	 * Below you can find the constructor, destroyer, various override function, and the 
	 * most important function the main loop of the service. Brief description of main loop 
	 * can be find down.
	 * 
	 * Designed and Developed by Bortoli Tomas
	 * 
	 * */
	public MainService() 
    {
        super("LogService");
        ready=false;
    }
	
	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) { 
		
		ready=false;
		//Explanation of a bug. in the case the application or the service is killed while the thread is having the mutex
		//of the offers, in that case the mutex, being static remain locked for the rest of the time of the universe.
		//So, if at the startup of the service the mutex is locked, unlock it.
		//This mutex is used from the service and from the activity, so will it be possible that the activity lock the
		//mutex for using it not being killed, and at the same time the service see it locked?
		//No because the activity wait a while and then check if the service is ready through the static ready variable.
		//That mean that if this condition is true, the mutex must be freed.
		//Is also possible that when android kill the service, it may also unload this class, and with it, also the statics variable associated.
		if(fileOfOffers.islock())
			fileOfOffers.unlock();
		
		new Thread(new Runnable(){
	        public void run() {
		        onHandleIntent(intent);

	        }
	    }).start();
		
	    return START_STICKY  ;
	}
	static public HandleSms hs;
	static public HandleCalls hc;
	static public HandleDataTraffic dth;
	
	//mutex for mutual exclusion on the file of offers, this for prevent corruption of data, example:
	//A thread read a part of the file, another thrad write the next part, the reader read the new part and have corrupted information!
	//(Used in NetworkManager.java)
	static MutualExclusion fileOfOffers=new MutualExclusion();
	
	//cache
	static public CacheOfNumbersToOperators cache;
	static CacheOfCryptoKey key;
	//ready indicates when the service is ready to provide datas to external threads that want to use it
	//haveSuffInfo is true when the service have sufficient information to compute the best offer
	//have offers is true when the client has a copy of the offers in local
	//variables needed by external thread that want to check if the service is ready
	//and variables needed to put the notification in the right moment
	static public boolean ready=false, haveSuffInfo=false, haveOffers=false;
	
	//indicates how many tentatives must be jumped before another connection will be retried
	//this to not create much overhead to the server, the server is slow in converting numbers to operators
	//so it has a limit in the number of connected clients
	static short connection_delay=0,update_delay=0,operator_delay=24;
	
	
	/*
	 * Main loop of the service. First it initializes all the needed variables and objects.
	 * After, it update all the dbs with the new informations, then is the moment to connect to server (with conditions) 
	 * and update the file of offers and converts the local numbers into operators.
	 * After a brief check to verify if is the first time the app is ready to compute the offers, if yes show a notification to the lazy user.
	 * Then sleep and restart.
	 * */
    @Override
    protected void onHandleIntent(Intent i) 
    {
    	
    	try{
    		
	    	//Set the application files path
			Constants.applicationFilesPath=this.getApplicationContext().getFilesDir().getAbsolutePath()+"/";
			
	    	//Sms, calls and data traffic handlers initialization
			dth=new HandleDataTraffic();
	    	hs=new HandleSms();
	    	hc=new HandleCalls();
	    	
	    	
	    	//Initialize the cache
	    	cache=new CacheOfNumbersToOperators();
	    	
	    	key=new CacheOfCryptoKey();
	    	
	    	
	    	//setup guards varible that indicates if bf have enough information and if have the file of offers
	    	haveSuffInfo=ComputeBestOffer.haveSuffInfo();
	    	haveOffers=Utility.existsFile(Constants.applicationFilesPath+Constants.offersFilePath);
	    	
	    	Constants.updateOperator(this.getApplicationContext());
	    	
	    	ready=true;
	        while(true)
	        {
	        	try {
					
	        		//update the data traffic database
					dth.updateDataTraffic();
					
					
			    	//Update the sms database
					hs.updateSms(this.getContentResolver());
					
					
					//Update the calls database
					hc.updateCalls(this.getContentResolver());
					
	        	}
	        	catch (InterruptedException e) {
					return;
				}
	        	
	        	try{
	        		//If is time to update the file of offers, connect and update
	        		if(update_delay==0){
	        			NetworkManager nm=new NetworkManager(key);
	        			if(nm.updateFileOfOffers()) //if new offers downloaded
	    					//if the first calculus was already done
	        				//if(Utility.existsFile(Constants.applicationFilesPath+Constants.isFirstCalculus))
	        					Notification.showNotification(this.getApplicationContext(), Constants.message_new_offers); //show notification that new offers are available
	        			nm.closeConnection();
	        			//60 times of delay = 5 hours
	        			update_delay=60;
	        		}
	        		else
	        			update_delay--;
	        		
	        		//is the moment to connect to the server to discover my operators of the numbers?
	        		if(connection_delay==0){
	        			//if not have all numbers into cache
		        		if(!ComputeBestOffer.checkForUnknownOperators(hs,hc,cache)){
			        		//Initialize the network manager and translate the number of unknown operator
			        		NetworkManager nm=new NetworkManager(key);
			        		//Update the cache
			        		ComputeBestOffer.checkAndUpdateForUnknownOperators(nm, hs, hc, cache);
			        		nm.closeConnection();
		        		}
	        		}
	        		else
	        			connection_delay--;
		        		
	        		
	        	}
	        	catch(Exception e){
	        		//set the connection_delay, meaning the delay for translating numbers to operators to 4 = 20 minutes
	        		connection_delay=4;
	        		
	        		//System.out.println(e.toString());
	        	}
	        	
	        	/*
	        	try{
	        		if(!Utility.existsFile(Constants.applicationFilesPath+Constants.isFirstCalculus)){
		        		//if have all the operators
		        		if(ComputeBestOffer.checkForUnknownOperators(hs,hc,cache)){
				        	//Check if is ready "like first time" to compute the offers, in that case, show a notification to the user
				        	haveOffers=Utility.existsFile(Constants.applicationFilesPath+Constants.offersFilePath);
			        		haveSuffInfo=ComputeBestOffer.haveSuffInfo();
			        		if(haveOffers && haveSuffInfo)
			        			Notification.showNotification(this.getApplicationContext(),Constants.message_ready_first_calculus);
				        	
		        		}
			        	
	        		}
	        	}
	        	catch(Exception e){}
	        	*/
	        	
	        	if(operator_delay==0){
	        		Constants.updateOperator(this.getApplicationContext());
	        		operator_delay=24;
	        	}
	        	else
	        		operator_delay--;
	        	
	        	
	    		try {
	    			//sleep for 300 seconds = 5 minutes
					Thread.sleep(300000);
				} catch (InterruptedException e) {
					//System.out.println(e.toString());
					//e.printStackTrace();
				}
	    		
	        }
        }
        catch(Exception e){
        	return;
        }
    }
 
    @Override
    public void onDestroy() 
    {
        Log.i(Constants.applicationName+" service", "Destroying Service");
    }
}
