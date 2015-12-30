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
import java.io.File;
import java.util.Calendar;

import android.os.SystemClock;

/*
 * 
 * Class that monitor the usage of data traffics. Contains a function used to update the app's db.
 * A constructor that initializes the appropriate data structures, or load it from a file if already present.
 * Two functions to get reports, one that returns the average of mb usage per day, the other, returns the number of days
 * of monitoring.
 * This class is intended to handle the data traffics in a high level way. Simple functions to do everything I need.
 * 
 * Bortoli Tomas
 * */
public class HandleDataTraffic {
	
	//Mutex on the db
	private MutualExclusion me=new MutualExclusion();
	
	//The DB
	private DatabaseDataTraffic dtdb=null;
	
	//moment of system startup N.B android unload classes with free policy, so also this static variable will be lost almost periodically.
	static Calendar momentOfSystemStartup=null;
	
	LocalElapsedTimeFromBoot letb=null;
	
	//Construct the object cresting the database, it will be read from the file, if the file doesn't exist or doesn't have it, 
	//it will be created empty. Also compute the startup system time
	public HandleDataTraffic(){
		
		//read the db from the main file
		dtdb=(DatabaseDataTraffic) Serialize.loadSerializedObject(new File(Constants.applicationFilesPath+Constants.dataTrafficDatabasePath));
		
		//if the first file does not exist, or is corrupted, read the second one, if this also does not contain a valid data traffic database, create a new one
		if(dtdb==null) dtdb=(DatabaseDataTraffic) Serialize.loadSerializedObject(new File(Constants.applicationFilesPath+Constants.dataTrafficDatabasePath1));
		
		//if at the end, no database is still loaded, create a new one!
		if(dtdb==null) dtdb=new DatabaseDataTraffic();
		
		//Initialize the local elapsed time from boot
		letb=new LocalElapsedTimeFromBoot();
		
	}
	
	
	//check if the db have to be freezed
	public boolean haveToFreeze(){
		Calendar now=Calendar.getInstance(),last;
		if(dtdb.dataTraffics.size()>0)
			last=dtdb.getNewest();
		else
			return false;
		
		return last.after(now);
	}
	
	//Handles space temporal trips
	//back in time:
	//if exist a record with moment after now, space temporal gap!
	//back in time, I can't handle this cases. Think why.
	//Actually, if someone change the date to go ahead in the time, the app will monitor normally the traffic data usage, creating non sense records
	//If after that the user change again the time to return to reality, or in any case, to go back, the app will be freezed until the current time 
	//will surpass the newest record time. Shit, no way to handle this case cause is impossible to know what the exact time is, locally. Need
	//an external signal.
	public void updateDataTraffic() throws InterruptedException{
		
		me.lock();
		
		
		
		//get system timer since startup
		long systemTime = SystemClock.elapsedRealtime();
		long days=systemTime/86400000; //compute the days
		
		
		
		/***********for space-time travel backward************/
		
		//hey, space temporal gap detected, freeze db
		if(haveToFreeze()){
			me.unlock();
			return;
		}
		
		/****************************************************/
		
		
		//ok continue..
		HandleDataTraffic.momentOfSystemStartup=Calendar.getInstance();
		HandleDataTraffic.momentOfSystemStartup.setTimeInMillis(System.currentTimeMillis()-SystemClock.elapsedRealtime());
		
		
		//get total data traffic used in the last session
		long totalTraffic=android.net.TrafficStats.getMobileTxBytes()+android.net.TrafficStats.getMobileRxBytes();
		
		//get the moment of system startup
		Calendar date=(Calendar)momentOfSystemStartup.clone();
		
		//get the index of the most recent element in the correspondent day
		int index=dtdb.mostRecentDateOf(date);
		
		//compute the average data traffic per day
		totalTraffic=totalTraffic/(days+1);
		
		//special case for a monitoring that is during less than one day 
		//if the record for the day doesn't exist, or we are in another session, 
		//create a new record
		boolean guard=letb.isSameSession(); //and update the local system time from boot
		if(index==-1 || !guard)
			dtdb.dataTraffics.add(new DailyDataTraffic((Calendar)date.clone(),totalTraffic));
		else{
			dtdb.dataTraffics.get(index).trafficInBytes=totalTraffic;
			dtdb.dataTraffics.get(index).day=(Calendar)date.clone();
		}
		days--;
		//for each day interested in the db
		while(days>=0){
			//go ahead 1 day
			date.add(Calendar.DAY_OF_MONTH, +1);
			
			//If a record already exist, update
			index=dtdb.existDataOf(date);
			if(index>=0){
				dtdb.dataTraffics.get(index).trafficInBytes=totalTraffic;
				dtdb.dataTraffics.get(index).day=(Calendar)date.clone();
			}
			//else add new record
			else{
				dtdb.dataTraffics.add(new DailyDataTraffic((Calendar)date.clone(),totalTraffic));
			}
			
			days--;
		}
		
				
		dtdb.checkForExpiredRecords(); //check if there are records that must be deleted for temporal vioa
		dtdb.save(); //save the data traffics database
		
		me.unlock();
		
	}
	
	//return the days of monitoring.
	//Cases: db with 0 records, return -1. Db with n records, return the number of days since the app was monitoring.
	public long getDaysOfMonitoring(){
		
		long days=-1;
		try{
			me.lock();
			
			Calendar newest=dtdb.getNewest();
			Calendar oldest=dtdb.getOldest();
			
			days=(newest.getTimeInMillis()-oldest.getTimeInMillis())/86400000l;
			
		}
		catch(Exception e){
		}
		finally{
			me.unlock();
		}
		
		return days;
	}
	
	//return the average mb per day. If empty db return 0. No other cases
	public float getAverageMbPerDay(){
		
		float res=0;
		
		try{
			me.lock();
			
			Calendar oldest=dtdb.getOldest();
			Calendar newest=dtdb.getNewest();
			
			long tot=0;
			for (DailyDataTraffic d:dtdb.dataTraffics){
				if(Utility.areSameDay(newest, d.day))
					continue;
				tot+=d.trafficInBytes;
			}
			long days=/*(newest.getTimeInMillis()-oldest.getTimeInMillis())/86400000l*/Utility.getDaysOfDifference(newest, oldest);
			res=(float)tot/(float)(days);
			res/=1024.0f; //convert in kb
			res/=1024.0f; //convert in mb
		}
		catch(Exception e){}
		finally{
			me.unlock();
		}
		return res;
	}
}
