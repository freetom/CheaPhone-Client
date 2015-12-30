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

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DatabaseSms implements Serializable {
	
	//Record the last date of the most recent sms already stored in the db, only new sms must be stored.
	private Calendar lastSmsDateTime;
	
	//List of all the sms
	public ArrayList<DailySmsTo> sms;
	
	//The constructor is called only if the database in new, or corrupted
	public DatabaseSms(){
		
		lastSmsDateTime=Calendar.getInstance();
		
		//Set the lastSmsDateTime to the days_of_memory ago from now
		Date dt=new Date();
		long l = dt.getTime() - (((long)Constants.days_of_memory)*1000l*60l*60l*24l);
		lastSmsDateTime.setTimeInMillis(l);
		
		//Initialize the array of sms
		sms=new ArrayList<DailySmsTo>();
		
		//System.out.println(lastSmsDateTime.toString());
		
		save(this);
		
	}
	
	private void save(DatabaseSms smsDB){
		Serialize.saveObject(smsDB, new File(Constants.applicationFilesPath+Constants.smsDatabasePath));
		
		Serialize.saveObject(smsDB, new File(Constants.applicationFilesPath+Constants.smsDatabasePath1));
	}
	
	//get the date and time of the last sended sms
	public Calendar getLastSmsDateTime(){
		return lastSmsDateTime;
	}
	
	//add a list of valid new sms to the list of all sms.
	public void insertSms(ArrayList<DailySmsTo> newSms){
		
		//Preprocessing parsing
		for(int i=0;i<newSms.size();i++){
			if(newSms.contains("+39")) newSms.get(i).number=newSms.get(i).number.substring(3);
		}
		
		int numberOfSms=sms.size();
		boolean changed=false,removed=false;
		
		for(int i=0;i<newSms.size();i++){
			
			//search the sms in the list of all sms, comparing his date and number, if he find it, it means that there is at least another message
			//sended to that number today.
			int index = DailySmsTo.findByNumberAndDate(sms, newSms.get(i).number, newSms.get(i).day);
			
			//If finded, update the existing record
			if(index!=-1){
				
				sms.get(index).n+=newSms.get(i).n;
				
			}
			//else add a new record
			else
				sms.add(newSms.get(i));
			
			//if the new sms is more recent, update the last sms date.
			if(newSms.get(i).day.after(lastSmsDateTime)){
				lastSmsDateTime=(newSms.get(i).day);
			}
		}
		
		if(numberOfSms!=sms.size())
			changed=true;
		
		
		//After adding the new sms, check if some elements of the list have exceed the maximum days number, and if yes delete the oldest record.
		Date dt=new Date();
		long l = dt.getTime() - (((long)Constants.days_of_memory)*1000l*60l*60l*24l);
		dt.setTime(l);
		
		for(int i=0;i<sms.size();i++){
			if(sms.get(i).day.before(dt)){
				sms.remove(i--);
				
				removed=true;
			}
		}
		
		
		
		//save the changes to file
		if(changed || removed) 
			save(this);
		
	}
	
	
	
	public static DatabaseSms loadDBSms(){
		return (DatabaseSms) Serialize.loadSerializedObject(new File(Constants.applicationFilesPath+Constants.smsDatabasePath));
	}
	
	
	public Calendar getNewest(){
		
		Calendar newest=sms.get(0).day;
		
		for(int i=1;i<sms.size();i++){
			if(newest.before(sms.get(i).day))
				newest=sms.get(i).day;
		}
		
		return newest;
	}
	
	public Calendar getOldest(){
		
		Calendar oldest=sms.get(0).day;
		
		for(int i=1;i<sms.size();i++){
			if(oldest.after(sms.get(i).day))
				oldest=sms.get(i).day;
		}
		
		return oldest;
	}
	
	
}
