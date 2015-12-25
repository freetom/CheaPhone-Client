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

import android.os.SystemClock;

/*
 * Class used to check if we are in the same session as before, or in a new session. To release the system from the wrong system boot time.
 * 
 * Bortoli Tomas
 * 
 * */
public class LocalElapsedTimeFromBoot implements Serializable{
	
	Long time;
	
	public LocalElapsedTimeFromBoot(){
		
		time=(Long)Serialize.loadSerializedObject(new File(Constants.applicationFilesPath+Constants.localTime));
		
		if(time==null)
			time=new Long(Long.MAX_VALUE);
		
	}
	
	public void save(){
		Serialize.saveObject(time, new File(Constants.applicationFilesPath+Constants.localTime));
	}
	
	//Check if we are in the same session as before (last launch of isSameSession) or not. And update the file with the local elapsed time from boot
	public boolean isSameSession(){
		
		boolean ret;
		
		long systemTime=SystemClock.elapsedRealtime();
		
		//new session detected
		if(systemTime<time){
			ret=false;
		}
		//same session
		else{
			ret=true;
		}
		
		time=systemTime;
		save();
		
		return ret;
		
	}
}
