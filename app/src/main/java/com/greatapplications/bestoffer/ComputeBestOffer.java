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

import android.content.Context;
import android.content.res.AssetManager;

public class ComputeBestOffer {
	
	/*
	 * Middleware class for computing all the offers, rates and options combinations.
	 * Contains also methods for verify if the system is ready for the calculus.
	 * And methods to update the @CacheOfNumbersToOperators.
	 *
	 * Bortoli Tomas
	 * */

	static final int minimumDaysOfMonitoring=-1;
	
	public static String INVALID="INVALID";
	
	//return true, if the service has gather enough info for calculating the best offers
	//handles all exceptions. NO OTHER CASES!!!!!!!!!!!
	public static boolean haveSuffInfo(){
		
		try{
        	long daysDt=MainService.dth.getDaysOfMonitoring();
    		//long daysSms=MainService.hs.getDaysOfMonitoring();
    		//long daysCalls=MainService.hc.getDaysOfMonitoring();
    		
    		if(daysDt<minimumDaysOfMonitoring) //|| daysSms<minimumDaysOfMonitoring || daysCalls<minimumDaysOfMonitoring)
    			return false;
    		else return true;
		}
		catch(Exception e){
			return false;
		}
		
	}
	
	
	//Check if there are unknow operators in local database
	//if any argument is null, throw NullReferenceException. 
	public static boolean checkForUnknownOperators(HandleSms hs, HandleCalls hc, CacheOfNumbersToOperators c){
		
		ArrayList<String> numbers=hs.getNumbers();
		ArrayList<String> numberOfOperatorsUnknown=new ArrayList<String>();
		
		for(int i=0;i<numbers.size();i++){
			if(c.getValue(numbers.get(i))==null)
				if(!numberOfOperatorsUnknown.contains(numbers.get(i)))
					numberOfOperatorsUnknown.add(numbers.get(i));
		}
		
		
		numbers=hc.getNumbers();
		ArrayList<String> numberOfOperatorsUnknown1=new ArrayList<String>();
		
		for(int i=0;i<numbers.size();i++){
			if(c.getValue(numbers.get(i))==null)
				if(!numberOfOperatorsUnknown.contains(numbers.get(i)))
					numberOfOperatorsUnknown.add(numbers.get(i));
		}
		
		if(numberOfOperatorsUnknown.size()==0 && numberOfOperatorsUnknown1.size()==0){
			return true;
		}
		else
			return false;
		
		
	}

	public static Pair<Integer,Integer> getKnownAndUnknownOperators(HandleSms hs, HandleCalls hc, CacheOfNumbersToOperators c){

		Integer tot;
		ArrayList<String> numbers=hs.getNumbers();
		tot=numbers.size();
		ArrayList<String> numberOfOperatorsUnknown=new ArrayList<String>();

		for(int i=0;i<numbers.size();i++){
			if(c.getValue(numbers.get(i))==null)
				if(!numberOfOperatorsUnknown.contains(numbers.get(i)))
					numberOfOperatorsUnknown.add(numbers.get(i));
		}


		numbers=hc.getNumbers();
		tot+=numbers.size();
		ArrayList<String> numberOfOperatorsUnknown1=new ArrayList<String>();

		for(int i=0;i<numbers.size();i++){
			if(c.getValue(numbers.get(i))==null)
				if(!numberOfOperatorsUnknown.contains(numbers.get(i)))
					numberOfOperatorsUnknown.add(numbers.get(i));
		}

		Integer diff=numberOfOperatorsUnknown.size()+numberOfOperatorsUnknown1.size();

		return new Pair<Integer,Integer>(tot,diff);
	}
	
	
	
	//to check if a number start with prefix, if this is true, this is a settled number
	private static boolean startWithPrefix(String number){
		
		if(number.charAt(0)=='0' && (number.charAt(1)>='1' && number.charAt(1)<='9'))
			return true;
		else
			return false;
		
	}

	//Connect with the server and update the operators that are unknown, N.B unknown!=INVALID
	//If any argument is null, throw NullReferenceException
	//May launch exception for socket implication, these are caught externally.
	public static void checkAndUpdateForUnknownOperators(NetworkManager nm, HandleSms hs, HandleCalls hc, CacheOfNumbersToOperators c) throws Exception{
		
		//block that gather and translate all the numbers
		try{
			ArrayList<String> numbers=hs.getNumbers(); //get the numbers of messages
			ArrayList<String> numberOfOperatorsUnknown=new ArrayList<String>(); //list for number of unknown operator for sms
			
			//if is a settled number, add it to the cache
			for(int i=0;i<numbers.size();i++){
				if(startWithPrefix(numbers.get(i))) 
					if(c.getValue(numbers.get(i))==null)
						c.addValue(numbers.get(i),INVALID);
			}
			
			//if is a number of unknown operator, add it to the list
			for(int i=0;i<numbers.size();i++){
				if(c.getValue(numbers.get(i))==null)
					if(!numberOfOperatorsUnknown.contains(numbers.get(i)))
						numberOfOperatorsUnknown.add(numbers.get(i));
			}
			
			//then do the same thing for the numbers of the calls
			numbers=hc.getNumbers();
			ArrayList<String> numberOfOperatorsUnknown1=new ArrayList<String>(); //create a list for the numbers with unknown operator
			
			//if is a settled number, add it to the cache
			for(int i=0;i<numbers.size();i++){
				if(startWithPrefix(numbers.get(i)))
					if(c.getValue(numbers.get(i))==null)
						c.addValue(numbers.get(i),INVALID);
			}
			
			//if is a number of unknown operator, add it to the list
			for(int i=0;i<numbers.size();i++){
				if(c.getValue(numbers.get(i))==null)
					if(!numberOfOperatorsUnknown1.contains(numbers.get(i)))
						numberOfOperatorsUnknown1.add(numbers.get(i));
			}
			
			//add the list of the numbers of calls to the list of the numbers of sms, so we have a list with all unknown numbers
			numberOfOperatorsUnknown.addAll(numberOfOperatorsUnknown1);
			
			
			
			if(numberOfOperatorsUnknown.size()>0){
				String[] operators = nm.numberToOperators(numberOfOperatorsUnknown);
				
				for(int i=0;i<operators.length-1;i++)
					c.addValue(numberOfOperatorsUnknown.get(i), operators[i]);
			}
			
		}
		catch(Exception e){}
		
	}
	
	
	//Generate various "nessuna offerta" and "nessuna opzione" and add it to the main lists
	//The null options generated will have ids minor than zero, starting from -2, this to simplify the detection further 
	private static void generateEmptyOffersAndOptions(ArrayList<Offer> offers, ArrayList<Option> options){
		
		//Extract the operators
		ArrayList<String> operators=new ArrayList<String>();
		for(int i=0;i<offers.size();i++){
			if(!operators.contains(offers.get(i).operator))
				operators.add(new String(offers.get(i).operator));
		}
		
		//Create one empty offer for each operator
		for(int i=0;i<operators.size();i++){
			offers.add(new Offer(operators.get(i)+" 0 0 0 0 0 0 0 0 0 0 0 0 "+Constants.nessunaOfferta+" http://"));
		}
		
		//Compute how many options there are for each operator
		int[] nOptions=new int[operators.size()];
		for(int i=0;i<operators.size();i++){
			int n=0;
			for(int j=0;j<options.size();j++){
				if(operators.get(i).equals(options.get(j).operator))
					n++;
			}
			nOptions[i]=n;
		}
		
		int z=-2;
		//for each option of each operator create an empty option. this to be sure that the best combination will be find correctly.
		for(int i=0;i<nOptions.length;i++){
			for(int j=0;j<nOptions[i];j++)
				options.add(new Option(operators.get(i)+" "+z--+" 0 0 0 0 0 0 0 0 "+Constants.nessunaOpzione+" http://"));
		}
		
		
	}
	
	
	private static Rate[] rateListToArray(ArrayList<Rate> list){
		Rate[] ret=new Rate[list.size()];
		for(int i=0;i<list.size();i++)
			ret[i]=list.get(i);
		return ret;
	}
	
	private static Option[] optionListToArray(ArrayList<Option> list){
		Option[] ret=new Option[list.size()];
		for(int i=0;i<list.size();i++)
			ret[i]=list.get(i);
		return ret;
	}
	
	//return null if the fileOfOffers is empty or not existing
	//return an array with 2 elements with cost -1 if the parsing went wrong
	//return an array with a value with cost -1 if the date of the phone is wrong
	//Otherwise return the array list of results.
	public static Pair<ArrayList<Result>,String> findBestOffer() throws IOException{
		String path=Constants.applicationFilesPath+Constants.offersFilePath;
		
		String str=null;
		try{
			MainService.fileOfOffers.lock();
			
			//Read the file of offers
			File file = new File(path);
		    FileInputStream fis = new FileInputStream(file);
		    byte[] data = new byte[(int)file.length()];
		    fis.read(data);
		    fis.close();
		    //
		    str = new String(data);


		}
		catch(Exception e){}
		finally{
			MainService.fileOfOffers.unlock();
		}

        if(str==null)	return null;
		
	    //Initially, split the file in his three parts, rates, offers and options.
	    str=str.replace('\r', ' ');
	    String[] parts=str.split("&");
	    
	    if(parts.length<3) return null;
	    
	    boolean error=false;
	    /***************rates************/
	    String[] tmp=parts[0].split("\n");
	    ArrayList<Rate> rates = new ArrayList<Rate>();
	    
	    for(int i=0;i<tmp.length;i++){
	    	rates.add(new Rate(tmp[i]));
	    	error|=rates.get(i).error;
	    }
	    
	    /***************offers************/
	    tmp=parts[1].split("\n");
	    ArrayList<Offer> offers = new ArrayList<Offer>();
	    
	    for(int i=1;i<tmp.length;i++){
	    	offers.add(new Offer(tmp[i]));
	    	error|=offers.get(i-1).error;
	    }
	    
	    /***************options************/
	    tmp=parts[2].split("\n");
	    ArrayList<Option> options = new ArrayList<Option>();
	    
	    for(int i=1;i<tmp.length;i++){
	    	options.add(new Option(tmp[i]));
	    	error|=options.get(i-1).error;
	    }
	    
	    /**********************************/
	    
	    
	    if(error) {
			ArrayList<Result> ret=new ArrayList<Result>();
			ret.add(new Result(null,null,null,-1,null,null));
			ret.add(new Result(null,null,null,-1,null,null));
			return new Pair<ArrayList<Result>,String>(ret,null);
		}
	    
	    generateEmptyOffersAndOptions(offers,options);
	    
	    
	    Pair<String,Float> p= MainService.hc.youAndMeMinutes();
        float dailyMinutesToYouAndMe=p.getSecond();
	    float smsPerDaySameOperator=MainService.hs.getAverageSmsPerDay(true);
	    float smsPerDayOtherOperator=MainService.hs.getAverageSmsPerDay(false);
	    float callsPerDaySameOperator=MainService.hc.getAverageCallsPerDay(true);
	    float minutesPerDaySameOperator=MainService.hc.getAverageMinutesPerDay(true);
	    float callPerDayOtherOperator=MainService.hc.getAverageCallsPerDay(false);
	    float minutesPerDayOtherOperator=MainService.hc.getAverageMinutesPerDay(false);
	    float mbPerDay=MainService.dth.getAverageMbPerDay();
	    
	    
	    ArrayList<Result> ret=new ArrayList<Result>();
	    
	    //Wrong date of the phone, this values can't be minor than zero
	    if(dailyMinutesToYouAndMe<0 || smsPerDaySameOperator<0 || smsPerDayOtherOperator<0 || callsPerDaySameOperator<0 || minutesPerDaySameOperator<0 ||
	    		callPerDayOtherOperator<0 || minutesPerDayOtherOperator<0 || mbPerDay<0){
	    	ret.add(new Result(null,null,null,-1,null,null));
	    	return new Pair<ArrayList<Result>,String>(ret,null);
	    }
	    
	    
	    
	    
	    for(int i=0;i<offers.size();i++){
	    	
	    	ret.addAll(offers.get(i).getCost(rateListToArray(rates) ,optionListToArray(options), 
	    			smsPerDaySameOperator, smsPerDayOtherOperator, 
	    			callsPerDaySameOperator, minutesPerDaySameOperator, callPerDayOtherOperator, minutesPerDayOtherOperator, 
	    			mbPerDay, dailyMinutesToYouAndMe));
	    	
	    }
	    
	    //Remove the price of the free offers, and add the bill without change if have to
	    for(int i=0;i<ret.size();i++){
	    	if(ret.get(i).options!=null)
	    		for(int z=0;z<ret.get(i).options.size();z++) //for each option
	    			if(Utility.contains(ret.get(i).offer.freeOptions,ret.get(i).options.get(z).id)) //and if the option is a free option for this offer
	    				ret.get(i).cost-=ret.get(i).options.get(z).price*10; //remove the cost of the option from the total price, n.b price of option is in cent not millicent
	    	
	    	//if the local operator is the same of the offer operator, have to apply additional cost of billWithoutChange (of operator) (may be 0)
	    	if(Constants.operator.contains(ret.get(i).offer.operator)){
	    		ret.get(i).cost+=ret.get(i).offer.billWithoutChange*10; //price of billWithoutChange is in cent must be converted to millicent
	    	}
	    }
	    
	    //order by cost
		for(int i=ret.size()-1;i>0;i--){
			for(int j=i-1;j>=0;j--){
				if(ret.get(i).cost<ret.get(j).cost){
					Result tmpR=ret.get(i);
					ret.set(i, ret.get(j));
					ret.set(j, tmpR);
				}
			}
		}
	    
		
		
		//check if there are two entry with the same options, same cost and same offer, remove one of them. And set the other rate to null
		boolean guard;
	    for(int i=0;i<ret.size();i++){
            int j=1;
	    	while(i-j>=0 && ret.get(i).cost==ret.get(i-j).cost){

	    			if(ret.get(i).offer.offerName.equals(ret.get(i-j).offer.offerName)){
	    				guard=false;
	    				if(ret.get(i).options==null && ret.get(i-j).options==null){
	    					guard=true;
	    				}
	    				else if(ret.get(i).options!=null && ret.get(i-j).options!=null){
	    					if(ret.get(i).options.size()==ret.get(i-j).options.size()){
	    						for(int z=0;z<ret.get(i-j).options.size();z++){
	    							if(ret.get(i).options.get(z).id==ret.get(i-j).options.get(z).id)
	    								guard=true;
	    							else{
	    								guard=false;
	    								break;
	    							}
	    						}
	    					}
	    				}
	    				
	    				if(guard){
	    					ret.get(i-j).rate=null;
		    				ret.remove(i--);
                            j=0;
	    				}
	    			}
                    j++;
	    	}
	    }
	    
	    return new Pair<ArrayList<Result>,String>(ret,p.getFirst());
	}
	
}
