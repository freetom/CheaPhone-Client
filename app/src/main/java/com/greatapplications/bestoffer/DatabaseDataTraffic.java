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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DatabaseDataTraffic implements Serializable {
	
	/*
	 * 
	 * The database of data traffics. Serializable to file, contains constructor, save method, 
	 * and various utilities functions.
	 * 
	 * Bortoli Tomas
	 * 
	 * */
	
	static MutualExclusion me=new MutualExclusion();
	
	//List of all the use of data traffics
	public ArrayList<DailyDataTraffic> dataTraffics;
	
	public DatabaseDataTraffic(){
		
		dataTraffics=new ArrayList<DailyDataTraffic>();
		
		save();
	}
	
	public void save(){
		Serialize.saveObject(this, new File(Constants.applicationFilesPath+Constants.dataTrafficDatabasePath));
		
		//save the object two times, so the possibility to lose all data for interruption when writing the file disappears
		Serialize.saveObject(this, new File(Constants.applicationFilesPath+Constants.dataTrafficDatabasePath1));
		
	}
	
	
	public int existDataOf(Calendar day){
		
		for(int i=0;i<dataTraffics.size();i++){
			
			if(
					(dataTraffics.get(i).day.get(Calendar.YEAR) == day.get(Calendar.YEAR) && 
					dataTraffics.get(i).day.get(Calendar.MONTH) == day.get(Calendar.MONTH) &&
					dataTraffics.get(i).day.get(Calendar.DAY_OF_MONTH) == day.get(Calendar.DAY_OF_MONTH))
			)
				return i;
			
			
		}
		
		return -1;	
		
	}
	
	public int mostRecentDateOf(Calendar day){
		
		int ret=-1;
		for(int i=0;i<dataTraffics.size();i++){
			
			if(
					(dataTraffics.get(i).day.get(Calendar.YEAR) == day.get(Calendar.YEAR) && 
					dataTraffics.get(i).day.get(Calendar.MONTH) == day.get(Calendar.MONTH) &&
					dataTraffics.get(i).day.get(Calendar.DAY_OF_MONTH) == day.get(Calendar.DAY_OF_MONTH))
			){
				if(ret==-1)
					ret=i;
				else if(dataTraffics.get(ret).day.before(dataTraffics.get(i).day))
						ret=i;
			}
			
		}
		
		return ret;
	}
	
	public void checkForExpiredRecords(){
		Date dt=new Date();
		long l = dt.getTime() - (((long)Constants.days_of_memory)*1000l*60l*60l*24l);
		dt.setTime(l);
		
		
		
		for(int i=0;i<dataTraffics.size();i++){
			if(dataTraffics.get(i).day.before(dt)){
				dataTraffics.remove(i--);
			}
		}
	
	}
	
	public static DatabaseDataTraffic readDB(){
		return (DatabaseDataTraffic) Serialize.loadSerializedObject(new File(Constants.applicationFilesPath+Constants.dataTrafficDatabasePath));
	}
	
	
	public Calendar getNewest(){
		
		Calendar newest=dataTraffics.get(0).day;
		
		for(int i=1;i<dataTraffics.size();i++){
			if(newest.before(dataTraffics.get(i).day))
				newest=dataTraffics.get(i).day;
		}
		
		return newest;
	}
	
	public Calendar getOldest(){
		
		Calendar oldest=dataTraffics.get(0).day;
		
		for(int i=1;i<dataTraffics.size();i++){
			if(oldest.after(dataTraffics.get(i).day))
				oldest=dataTraffics.get(i).day;
		}
		
		return oldest;
	}
	
	//Check out if exists records that have registered moment after the current moment, is impossible, so, destroy the records in that case.
	//this may only happen if the user experiment a space temporal trip ahead in time. When it goes ahead, new records are created. When
	//he return to real time, the records must be destroyed.
	public void checkForImpossibleRecords() {
		
		Calendar now=Calendar.getInstance();
		
		for(int i=0;i<dataTraffics.size();i++){
			if(dataTraffics.get(i).day.after(now))
				dataTraffics.remove(i--);
		}
		
		
	}
	
	
	
}
