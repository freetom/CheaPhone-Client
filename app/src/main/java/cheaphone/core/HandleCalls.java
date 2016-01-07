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


import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import java.io.*;

public class HandleCalls {
	
	
	/*
	 * 
	 * Class that handles the calls. Update the db and get high level information on it.
	 * Note that every function that want to acces the database in write mode, must lock the relative mutex.
	 * 
	 * Bortoli Tomas
	 * */
	
	private MutualExclusion me=new MutualExclusion();
	private DatabaseCalls callDB=null;
	
	public HandleCalls(){

		try {
			callDB = (DatabaseCalls) Serialize.loadSerializedObject(new File(Constants.applicationFilesPath + Constants.callsDatabasePath));
		}
		catch(Exception e){
			callDB=null;
		}

		if(callDB==null)	callDB=(DatabaseCalls) Serialize.loadSerializedObject(new File(Constants.applicationFilesPath+Constants.callsDatabasePath1));
		
		if(callDB==null) callDB=new DatabaseCalls();
		
	}
	
	//update the database of calls
	public void updateCalls(ContentResolver c) throws InterruptedException{
		
		me.lock();
		
		String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
		//get a pointer to the calls log
		Cursor cursor=c.query(Uri.parse("content://call_log/calls"), null, null, null, strOrder);
		
		cursor.moveToFirst();
		
		//extract the calls that I need, only those more recent than the last call analyzed 
		ArrayList<DailyCallsTo> al=getCallsData(cursor, callDB.getLastSmsDateTime());
		
		//insert this calls in database, in the right place
		callDB.insertCalls(al);
		
		me.unlock();
		
	}
	
	//extract the calls that I need
	private ArrayList<DailyCallsTo> getCallsData(Cursor cur, Calendar lastCallDate) {
		
		
		//analyze the calls log
	    ArrayList<Pair<Pair<String,Integer>,Calendar>> exportBuffer = new ArrayList<Pair<Pair<String,Integer>,Calendar>>();
	    try {
	        if (cur.moveToFirst()) {
	            String date;
	            String phoneNumber;
	            String type;
	            String duration;
	            
	            int dateColumn = cur.getColumnIndex(CallLog.Calls.DATE);
	            int numberColumn = cur.getColumnIndex(CallLog.Calls.NUMBER);
	            int typeOfCallColumn = cur.getColumnIndex(CallLog.Calls.TYPE);
	            int durationColumn = cur.getColumnIndex(CallLog.Calls.DURATION);
	            
	            
	            
	            
	            do {
	                date = cur.getString(dateColumn);
	                phoneNumber = cur.getString(numberColumn);
	                type = cur.getString(typeOfCallColumn);
	                duration = cur.getString(durationColumn);
	                
	                //if this call is not an outgoing call, jump it
	                if(Integer.parseInt(type) != CallLog.Calls.OUTGOING_TYPE)
		            	continue;
	                
	                if(Long.parseLong(date)<=lastCallDate.getTimeInMillis())
	                	break;
	                
	                
	                Calendar calendar= Calendar.getInstance();
	                calendar.setTimeInMillis(Long.parseLong(date));
	                
	                //If the call duration is greater than 0, add it!
	                if(Integer.parseInt(duration)!=0)
	                	exportBuffer.add(new Pair(new Pair(phoneNumber,Integer.parseInt(duration)),calendar));
	                
	                
	                //Debug.out(date + ","+ phoneNumber);
	                
	            } while (cur.moveToNext());
	        }
	        
	    } catch (Exception exp) {     
	        CharSequence text = "An Error Occurred, Code:104, Ex:"+exp.toString();  
	        System.out.println(text);
	        //Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
	        //toast.show();
	    }
	    
	    
	    //filter the calls
	    ArrayList<DailyCallsTo> al=new ArrayList<DailyCallsTo>();
	    
	    for(int i=0;i<exportBuffer.size();i++){
	    	
	    	if(exportBuffer.get(i).getFirst().getFirst().contains("#31#"))
	    		exportBuffer.set( i , new Pair(new Pair(exportBuffer.get(i).getFirst().getFirst().substring(4),exportBuffer.get(i).getFirst().getSecond()),exportBuffer.get(i).getSecond()));
	    	if(exportBuffer.get(i).getFirst().getFirst().charAt(0)=='0' && 
	    			exportBuffer.get(i).getFirst().getFirst().charAt(1)=='0' && 
	    					exportBuffer.get(i).getFirst().getFirst().charAt(2)=='3' && 
	    							exportBuffer.get(i).getFirst().getFirst().charAt(3)=='9')
	    		exportBuffer.set( i , new Pair(new Pair(exportBuffer.get(i).getFirst().getFirst().substring(4),exportBuffer.get(i).getFirst().getSecond()),exportBuffer.get(i).getSecond()));
	    	if(exportBuffer.get(i).getFirst().getFirst().contains("+"))
	    		exportBuffer.set( i , new Pair(new Pair(exportBuffer.get(i).getFirst().getFirst().substring(3),exportBuffer.get(i).getFirst().getSecond()),exportBuffer.get(i).getSecond()));
	    	exportBuffer.set(i, new Pair(new Pair(exportBuffer.get(i).getFirst().getFirst().replace(" ", ""),exportBuffer.get(i).getFirst().getSecond()),exportBuffer.get(i).getSecond()));
	    	
	    	if(exportBuffer.get(i).getFirst().getFirst().length()!=10){
	    		exportBuffer.remove(i--);
	    		continue;
	    	}
	    	
	    	//if the number is already present, increment only the number of seconds used
	    	int cond=DailyCallsTo.findByNumberAndDate(al, exportBuffer.get(i).getFirst().getFirst(),exportBuffer.get(i).getSecond());
	    	if(cond>=0){
	    		
	    		al.get(cond).seconds+=exportBuffer.get(i).getFirst().getSecond();
	    		al.get(cond).n++;
	    		
	    	}
	    	else{
	    		al.add(new DailyCallsTo(exportBuffer.get(i).getFirst().getFirst(),exportBuffer.get(i).getFirst().getSecond(),exportBuffer.get(i).getSecond()));
	    	}
	    }
	    
	    return al;
	    
	}
	
	//return the total days of monitoring. return -1 in the case the database is empty
	public long getDaysOfMonitoring(){
		long days=-1;
		try{
			me.lock();
			
			Calendar newest=Calendar.getInstance();
			Calendar oldest=callDB.getOldest();
			
			days=(newest.getTimeInMillis()-oldest.getTimeInMillis())/86400000l;
			
		}
		catch(Exception e){
			days=(Calendar.getInstance().getTimeInMillis()-callDB.creationTime.getTimeInMillis())/86400000l;
		}
		finally{
			me.unlock();
		}
		
		return days;
	}
	
	//utility function that check if an element is present in a specific kind of array list
	private int contains(ArrayList<Pair<String,Integer>> list, String number){
		
		for(int i=0;i<list.size();i++){
			if(list.get(i).getFirst().contains(number))
				return i;
		}
		
		return -1;
		
	}
	
	//return all the numbers that are present in the calls database
	public ArrayList<String> getNumbers(){
		
		ArrayList<String> ret=new ArrayList<String>();
		
		try{
			me.lock();
			
			for(int i=0;i<callDB.calls.size();i++)
				ret.add(callDB.calls.get(i).number);
		
		}
		catch(Exception e){}
		finally{
			me.unlock();
		}
		
		return ret;
	}

	
	//this function operate in mutual exclusion on the database of calls, it find the most called number of the same operator, potentially the 
	//you and me partner. Returns the number of minutes in total per day that you are using with him.
	public Pair<String,Float> youAndMeMinutes(){
		
		int max=0; long days=0;
		String youAndMeNumber=null;
		try{
			me.lock();
			
			CacheOfNumbersToOperators cache=MainService.cache;
			
			ArrayList<Pair<String,Integer>> allSeconds= new ArrayList<Pair<String,Integer>>();
			
			String myOperator=Constants.operator;
			
			for(int i=0;i<callDB.calls.size();i++){
				String v=cache.getValue(callDB.calls.get(i).number);
				
				
				if(v!=null && myOperator.contains(v)){
					int index=contains(allSeconds,callDB.calls.get(i).number);
					
					if(index!=-1){
						Integer value=allSeconds.get(index).getSecond();
						value+=callDB.calls.get(i).seconds;
					}
					else{
						allSeconds.add(new Pair<String,Integer>(callDB.calls.get(i).number,callDB.calls.get(i).seconds));
					}
				}
				
			}
			
			
			for(int i=0;i<allSeconds.size();i++){
				if(allSeconds.get(i).getSecond()>max){
					max=allSeconds.get(i).getSecond();
					youAndMeNumber=allSeconds.get(i).getFirst();
				}
			}
			
			Calendar newest=Calendar.getInstance(); Calendar oldest=callDB.getOldest();
			days=Utility.getDaysOfDifference(newest,oldest);
		}
		catch(Exception e){}
		finally{
			me.unlock();
		}
		
		return new Pair<String,Float>(youAndMeNumber,(((float)max)/60.0f)/(float)days);
	}
	
	
	//Returns the average minutes of calls per day
	public float getAverageMinutesPerDay(){
		float tot=0;
		try{
			
			me.lock();
			
			Calendar newest=Calendar.getInstance();
			Calendar oldest=callDB.getOldest();
			
			for(int i=0;i<callDB.calls.size();i++){
				
				if(!Utility.areSameDay(newest, callDB.calls.get(i).day)){
					tot+=callDB.calls.get(i).seconds;
				}
			}
			
			long days= Utility.getDaysOfDifference(newest,oldest);
			tot/=days;
			tot/=60f;
		}
		catch(Exception e){}
		finally{
			me.unlock();
		}
		return tot;
		
	}
	
	//return the average minutes per day that the user make with the same operator or not, this depends on the sameOperator boolean parameter
	//Need that the cache contain all the operators of the local numbers.
	public float getAverageMinutesPerDay(Boolean sameOperator){
		
		float avg=0;
		try{
			me.lock();
			
			Calendar newest=Calendar.getInstance(); Calendar oldest=callDB.getOldest();
			long days=Utility.getDaysOfDifference(newest,oldest);
			
			
			CacheOfNumbersToOperators cache=MainService.cache;
			
			String myOperator=Constants.operator;
			
			
			for(int i=0;i<callDB.calls.size();i++){
				
				if(!Utility.areSameDay(newest, callDB.calls.get(i).day)){
					
					String op=cache.getValue(callDB.calls.get(i).number);
					if(sameOperator && op!=null && myOperator.contains(op)){
						avg+=callDB.calls.get(i).seconds;
					}
					else if(!sameOperator && op!=null && !myOperator.contains(op)){
						avg+=callDB.calls.get(i).seconds;
					}
					else if(!sameOperator && op==null)
						avg+=callDB.calls.get(i).seconds;
					
				}
			}
			
			avg/=(float)days;
			avg/=60.0f;
		}
		catch(Exception e){}
		finally{
			me.unlock();
		}
		return avg;
	}
	
	//return the average number of calls that the user do every day. With the same, or other operators.
	public float getAverageCallsPerDay(Boolean sameOperator){
		
		float avg=0;
		
		try{
			me.lock();
			
			Calendar newest=Calendar.getInstance(); Calendar oldest=callDB.getOldest();
			long days=Utility.getDaysOfDifference(newest,oldest);
			
			CacheOfNumbersToOperators cache=MainService.cache;
			
			String myOperator=Constants.operator;
			
			
			for(int i=0;i<callDB.calls.size();i++){
				
				if(!Utility.areSameDay(newest, callDB.calls.get(i).day)){
					
					String op=cache.getValue(callDB.calls.get(i).number);
					if(sameOperator && op!=null && myOperator.contains(op)){
						avg+=callDB.calls.get(i).n;
					}
					else if(!sameOperator && op!=null && !myOperator.contains(op)){
						avg+=callDB.calls.get(i).n;
					}
					else if(!sameOperator && op==null)
						avg+=callDB.calls.get(i).n;
					
				}
			}
			
			avg/=(float)days;
			
			
		}
		catch(Exception e){}
		finally{
			me.unlock();
		}
		
		return avg;
	}

    public ArrayList<DailyCallsTo> getDBCopy(){
        ArrayList<DailyCallsTo> ret=new ArrayList<DailyCallsTo>();

        try {
            me.lock();

            for (int i = 0; i < callDB.calls.size(); i++) {
                ret.add(callDB.calls.get(i));
            }
        }
        catch(Exception e){}
        finally {
            me.unlock();
        }

        return ret;
    }

    public ArrayList<DailyCallsTo> getMostCalled( ArrayList<Pair<Calendar,Calendar>> excludedPeriods ){
        ArrayList<DailyCallsTo> ret=new ArrayList<DailyCallsTo>();

        try {
            me.lock();

            for (int i = 0; i < callDB.calls.size(); i++) {
                boolean add=true;
                for(int j=0;excludedPeriods!=null && j<excludedPeriods.size() && add;j++)
                    if(callDB.calls.get(i).day.after(excludedPeriods.get(j).getFirst())
                            &&
                            callDB.calls.get(i).day.before(excludedPeriods.get(j).getSecond())
                            )
                        add=false;
                if(add)
                    ret.add(callDB.calls.get(i));
            }

            for(int i=0;i<ret.size();i++){
                for(int j=i+1;j<ret.size();j++){
                    if(ret.get(i).number.equals(ret.get(j).number)) {
                        ret.get(i).seconds += ret.get(j).seconds;
                        ret.get(i).n += ret.get(j).n;
			ret.remove(j--);
                    }
                }
            }

            //Collections.sort(ret);
        }
        catch(Exception e){}
        finally {
            me.unlock();
        }

        return ret;
    }
}
