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

/* 
 * Client BestOffer 
 * 
 * 
 * The cache that contains the association between numbers and operators
 * It handles initialization, added of values, and retrievement of it. 
 * Also the expiration time of the data, setted by a constant.
 * 
 * 
 * Developer: Bortoli Tomas
 * 
 * */

package com.greatapplications.bestoffer;

import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

public class CacheOfNumbersToOperators {
	/*
	 * Cache of numbers to operators. Implemented with a simple hash table.
	 * Contains methods for handling the cache.
	 * 
	 * Bortoli Tomas
	 * */
	
	//Maximum time an element can be kept in cache (in days)
	private final int EXPIRATION_CACHE_PERIOD=90;
	
	//The hash table that is basically the cash. Contains a number, and a pair, that is formed of an operator and a date of expiration
	private Hashtable<String,Pair<String,Date>> htCache;
	private File cacheFile,cacheFile1; //the cache file on the filesystem
	
	//Construct a cache, if one exist, load it from file, if not create an empty one.
	public CacheOfNumbersToOperators(){
		
		cacheFile=new File(Constants.applicationFilesPath+Constants.cacheFilePath);
		cacheFile1=new File(Constants.applicationFilesPath+Constants.cacheFilePath1);
		
		if(!cacheFile.exists())
			System.out.println("File of the cache does not exist, it will be create.");
		
		htCache=(Hashtable<String,Pair<String,Date>>)Serialize.loadSerializedObject(cacheFile);
		
		if(htCache==null)
			htCache=(Hashtable<String,Pair<String,Date>>)Serialize.loadSerializedObject(cacheFile1);
		
		if(htCache==null){
			htCache=new Hashtable<String,Pair<String,Date>>();
			System.out.println("New empty cache generated.");
		}
		else
			System.out.println("Cache with "+htCache.size()+" elements loaded.");
	}
	
	//add a value into cache and save the file
	public void addValue(String key, String value){
		Date d= new Date();
		d=addDays(d,EXPIRATION_CACHE_PERIOD);
		htCache.put(key, new Pair<String,Date>(value,d));
		
		Serialize.saveObject(htCache, cacheFile );
		Serialize.saveObject(htCache, cacheFile1 );
		
	}
	
	//return a value from the cache. If it is expired, delete it and return null.
	//If the value is not present, return null.
	public String getValue(String key){
		Pair<String,Date> v=htCache.get(key);
		if(v!=null){
			Date now=new Date();
			if(now.before(v.getSecond()))
				return new String(v.getFirst());
			else
				htCache.remove(key);
		}
		
		return null;
	}
	
	
	//Utility function used only inside this class
	private static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
	
	
}
