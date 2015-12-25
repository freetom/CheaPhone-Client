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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;
import android.app.Activity;
import java.io.*;

/*
 * This class handle sms.
 * It inizialize his data from a file of serialized data, if the file does not exist a new instance is created.
 * After that, this class provide the update method, called to check if there are new messages and save them.
 * Methods to report the days of monitoring, or all the numbers, or an average of sms usage per day, choosing to get only numbers with the same or with other operators.
 * 
 * Developed by Bortoli Tomas
 * */
public class HandleSms {
	
	private MutualExclusion me=new MutualExclusion();
	
	private DatabaseSms smsDB=null;
	
	public HandleSms(){
		
		smsDB=(DatabaseSms) Serialize.loadSerializedObject(new File(Constants.applicationFilesPath+Constants.smsDatabasePath));
		
		if(smsDB==null)	smsDB=(DatabaseSms) Serialize.loadSerializedObject(new File(Constants.applicationFilesPath+Constants.smsDatabasePath1));
		
		if(smsDB==null) smsDB=new DatabaseSms();
		
	}
	
	public void updateSms(ContentResolver c) throws InterruptedException{
		
		me.lock();
		
		Cursor cursor=c.query(Uri.parse("content://sms/sent"), null, null, null, "date DESC");
		cursor.moveToFirst();
		
		ArrayList<DailySmsTo> al=getSmsData(cursor, smsDB.getLastSmsDateTime());
		
		smsDB.insertSms(al);
		
		me.unlock();
		
	}
	
	
	private ArrayList<DailySmsTo> getSmsData(Cursor cur, Calendar lastSmsDate) {
		
		
		
	    ArrayList<Pair<String,Calendar>> exportBuffer = new ArrayList<Pair<String,Calendar>>();
	    try {
	        if (cur.moveToFirst()) {
	            String date;
	            String phoneNumber;

	            int dateColumn = cur.getColumnIndex("date");
	            int numberColumn = cur.getColumnIndex("address");

	            do {
	                date = cur.getString(dateColumn);
	                phoneNumber = cur.getString(numberColumn);

	                
	                if(Long.parseLong(date)<=lastSmsDate.getTimeInMillis())
	                	break;
	                
	                
	                Calendar calendar= Calendar.getInstance();
	                calendar.setTimeInMillis(Long.parseLong(date));
	                
	                exportBuffer.add(new Pair(phoneNumber,calendar));
	                
	                
	                //Debug.out(date + ","+ phoneNumber);
	                
	            } while (cur.moveToNext());
	        }
	        
	    } catch (Exception exp) {     
	        CharSequence text = "An Error Occurred, Code:104, Ex:"+exp.toString();  
	        //Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
	        //toast.show();
	    }
	    
	    
	    //Crea associazione numero, n messaggi inviati al numero in questione
	    ArrayList<DailySmsTo> al=new ArrayList<DailySmsTo>();
	    
	    for(int i=0;i<exportBuffer.size();i++){
	    	
	    	if(exportBuffer.get(i).getFirst().contains("#31#"))
	    		exportBuffer.set( i , new Pair(exportBuffer.get(i).getFirst().substring(4),exportBuffer.get(i).getSecond()));
	    	if(exportBuffer.get(i).getFirst().charAt(0)=='0' && 
	    			exportBuffer.get(i).getFirst().charAt(1)=='0' && 
	    					exportBuffer.get(i).getFirst().charAt(2)=='3' && 
	    							exportBuffer.get(i).getFirst().charAt(3)=='9')
	    		exportBuffer.set( i , new Pair(exportBuffer.get(i).getFirst().substring(4),exportBuffer.get(i).getSecond()));
	    	if(exportBuffer.get(i).getFirst().contains("+"))
	    		exportBuffer.set( i , new Pair(exportBuffer.get(i).getFirst().substring(3),exportBuffer.get(i).getSecond()));
	    	exportBuffer.set(i, new Pair(exportBuffer.get(i).getFirst().replace(" ", ""),exportBuffer.get(i).getSecond()));
	    	
	    	
	    	if(exportBuffer.get(i).getFirst().length()!=10){
	    		exportBuffer.remove(i--);
	    		continue;
	    	}
	    	
	    	//Se il numero c'è già, incrementa il contatore dei messaggi inviati.
	    	int cond=DailySmsTo.findByNumberAndDate(al, exportBuffer.get(i).getFirst(), exportBuffer.get(i).getSecond());
	    	if(cond>=0){
	    		
	    		al.get(cond).n++;
	    		
	    		
	    	}
	    	else{
	    		al.add(new DailySmsTo(exportBuffer.get(i).getFirst(),1,exportBuffer.get(i).getSecond()));
	    	}
	    }
	    
	    return al;
	    
	}
	
	
	public long getDaysOfMonitoring(){
			
		long days=-1;
		try{
			me.lock();
			
			Calendar newest=Calendar.getInstance();
			Calendar oldest=smsDB.getOldest();
			
			days=(newest.getTimeInMillis()-oldest.getTimeInMillis())/86400000l;
			
		}
		catch(Exception e){
		}
		finally{
			me.unlock();
		}
		
		return days;
	}
	
	public ArrayList<String> getNumbers(){
		
		ArrayList<String> ret=new ArrayList<String>();
		try{
			me.lock();
			
			for(int i=0;i<smsDB.sms.size();i++)
				ret.add(smsDB.sms.get(i).number);
		}
		catch(Exception e){}
		finally{
			me.unlock();
		}
		return ret;
	}
	
	

	//Function that return the average sms usage per day, ** it consider also the day that is now, maybe to be change **
	public float getAverageSmsPerDay(){
		
		float tot=0;
		try{
			me.lock();
			
			Calendar newest=Calendar.getInstance();
			Calendar oldest=smsDB.getOldest();
			
			for(int i=0;i<smsDB.sms.size();i++){
				
				if(!Utility.areSameDay(newest, smsDB.sms.get(i).day)){
					tot+=smsDB.sms.get(i).n;
				}
			}
			
			long days= Utility.getDaysOfDifference(newest,oldest);
			tot/=days;
		}
		catch(Exception e){}
		finally{
			me.unlock();
		}
		return tot;
		
	}
	
	//Need that the cache contain all the operators of the local numbers.
	public float getAverageSmsPerDay(Boolean sameOperator){
		
		float avg=0;
		try{
			me.lock();
			
			Calendar newest=Calendar.getInstance(); Calendar oldest=smsDB.getOldest();
			long days=Utility.getDaysOfDifference(newest,oldest);
			
			CacheOfNumbersToOperators cache=MainService.cache;
			
			String myOperator=Constants.operator;
			
			
			for(int i=0;i<smsDB.sms.size();i++){
				if(!Utility.areSameDay(newest, smsDB.sms.get(i).day)){
					String op=cache.getValue(smsDB.sms.get(i).number);
					if(sameOperator && op!=null && myOperator.contains(op)){
						avg+=smsDB.sms.get(i).n;
					}
					else if(!sameOperator && op!=null && !myOperator.contains(op)){
						avg+=smsDB.sms.get(i).n;
					}
					else if(!sameOperator && op==null)
						avg+=smsDB.sms.get(i).n;
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


    public ArrayList<DailySmsTo> getDBCopy(){
        ArrayList<DailySmsTo> ret=new ArrayList<DailySmsTo>();

        try {
            me.lock();

            for (int i = 0; i < smsDB.sms.size(); i++) {
                ret.add(smsDB.sms.get(i));
            }
        }
        catch(Exception e){}
        finally {
            me.unlock();
        }

        return ret;
    }

    public ArrayList<DailySmsTo> getMostSent( ArrayList<Pair<Calendar,Calendar>> excludedPeriods ){
        ArrayList<DailySmsTo> ret=new ArrayList<DailySmsTo>();

        try {
            me.lock();

            for (int i = 0; i < smsDB.sms.size(); i++) {
                boolean add=true;
                for(int j=0;excludedPeriods!=null && j<excludedPeriods.size() && add;j++)
                    if(smsDB.sms.get(i).day.after(excludedPeriods.get(j).getFirst())
                            &&
                            smsDB.sms.get(i).day.before(excludedPeriods.get(j).getSecond())
                            )
                        add=false;
                if(add)
                    ret.add(smsDB.sms.get(i));
            }

            for(int i=0;i<ret.size();i++){
                for(int j=i+1;j<ret.size();j++){
                    if(ret.get(i).number.equals(ret.get(j).number)) {
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
