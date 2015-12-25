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

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

public class DatabaseCalls implements Serializable {
	
	//Record the last date of the most recent sms already stored in the db, only new sms must be stored.
	private Calendar lastOutgoingCallDateTime;
	
	//List of all the sms
	public ArrayList<DailyCallsTo> calls;
	
	//The constructor is called only if the database in new, or corrupted
	public DatabaseCalls(){
		
		lastOutgoingCallDateTime=Calendar.getInstance();
		
		//Set the lastSmsDateTime to the days_of_memory ago from now
		Date dt=new Date();
		long l = dt.getTime() - (((long)Constants.days_of_memory)*1000l*60l*60l*24l);
		lastOutgoingCallDateTime.setTimeInMillis(l);
		
		//Initialize the array of sms
		calls=new ArrayList<DailyCallsTo>();
		
		//System.out.println(lastSmsDateTime.toString());
		
		save(this);
		
	}
	
	private void save(DatabaseCalls smsDB){
		Serialize.saveObject(smsDB, new File(Constants.applicationFilesPath+Constants.callsDatabasePath));
		
		Serialize.saveObject(smsDB, new File(Constants.applicationFilesPath+Constants.callsDatabasePath1));
	}
	
	//get the date and time of the last sended sms
	public Calendar getLastSmsDateTime(){
		return lastOutgoingCallDateTime;
	}
	
	//add a list of valid new sms to the list of all sms.
	public void insertCalls(ArrayList<DailyCallsTo> newCalls){
		
		//Preprocessing parsing
		for(int i=0;i<newCalls.size();i++){
			if(newCalls.contains("+39")) newCalls.get(i).number=newCalls.get(i).number.substring(3);
		}
				
		int numberOfCalls=calls.size();
		boolean changed=false,removed=false;
		
		for(int i=0;i<newCalls.size();i++){
			
			//search the call in the list of all calls, comparing his date and number, if he find it, it means that there is at least another call
			//made to that number today.
			int index = DailyCallsTo.findByNumberAndDate(calls, newCalls.get(i).number, newCalls.get(i).day);
			
			//If finded, update the existing record
			if(index!=-1){
				
				calls.get(index).seconds+=newCalls.get(i).seconds;
				calls.get(index).n++;
			}
			//else add a new record
			else
				calls.add(newCalls.get(i));
			
			//if the new sms is more recent, update the last sms date.
			if(newCalls.get(i).day.after(lastOutgoingCallDateTime)){
				lastOutgoingCallDateTime=(newCalls.get(i).day);
			}
		}
		
		if(numberOfCalls!=calls.size())
			changed=true;
		
		//After adding the new calls, check if some elements of the list have exceed the maximum days number, and if yes delete the oldest record.
		Date dt=new Date();
		long l = dt.getTime() - (((long)Constants.days_of_memory)*1000l*60l*60l*24l);
		dt.setTime(l);
		
		for(int i=0;i<calls.size();i++){
			if(calls.get(i).day.before(dt)){
				calls.remove(i--);
				
				removed=true;
			}
		}
		
		
		
		//save the changes to file
		if(changed || removed) 
			save(this);
		
	}
	
	
	
	public static DatabaseCalls loadDBCalls(){
		return (DatabaseCalls) Serialize.loadSerializedObject(new File(Constants.applicationFilesPath+Constants.callsDatabasePath));
	}
	
	
	//return the newest date, if calls empty, throw IndexOutOfBound exception
	public Calendar getNewest(){
		
		Calendar newest=calls.get(0).day;
		
		for(int i=1;i<calls.size();i++){
			if(newest.before(calls.get(i).day))
				newest=calls.get(i).day;
		}
		
		return newest;
	}
	
	//return the oldest date, if calls empty, throw IndexOutOfBound exception
	public Calendar getOldest(){
		
		Calendar oldest=calls.get(0).day;
		
		for(int i=1;i<calls.size();i++){
			if(oldest.after(calls.get(i).day))
				oldest=calls.get(i).day;
		}
		
		return oldest;
	}
	
	
	
	
	
	
}
