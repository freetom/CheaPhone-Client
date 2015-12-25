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

public class Offer {

    public String operator;
    public int credits;
    public short min_vs_same_operator;
    public short min;
    public short sms_vs_same_operator;
    public short sms;
    public short dataTraffic;
    public short maxAge;
    public boolean youAndMe;
    public short portability;
    public short billWithoutChange; //in cent
    public short[] freeOptions;

    public short price; //in cent
    public short riseOfPriceAfterPeriod; //in cent
    public float monthBeforeIncreasing;
    public String offerName;
    public String urlOffer;
    public short[] options; //indicates the ids of the options
    public String moreInfos;

    public boolean error;
	
	//constructor by cloning
	public Offer(Offer o){
		this.operator=o.operator;
		this.credits=o.credits;
		this.min_vs_same_operator=o.min_vs_same_operator;
		this.min=o.min;
		this.sms_vs_same_operator=o.sms_vs_same_operator;
		this.sms=o.sms;
		this.dataTraffic=o.dataTraffic;
		this.maxAge=o.maxAge;
		this.youAndMe=o.youAndMe;
		this.portability=o.portability;
		this.billWithoutChange=o.billWithoutChange;
		if(o.freeOptions!=null){
			this.freeOptions=new short[o.freeOptions.length];
			for(int i=0;i<o.freeOptions.length;i++)
				this.freeOptions[i]=o.freeOptions[i];
		}
		this.price=o.price;
		this.riseOfPriceAfterPeriod=o.riseOfPriceAfterPeriod;
		this.monthBeforeIncreasing=o.monthBeforeIncreasing;
		this.offerName=o.offerName;
		this.urlOffer=o.urlOffer;
		if(o.options!=null){
			this.options=new short[o.options.length];
			for(int i=0;i<o.options.length;i++)
				this.options[i]=o.options[i];
		}
		this.error=o.error;
	}
	

	public Offer(String offer){
		error=false;
		
		String[] fields=offer.split(" ");
		try{
			operator=fields[0];
			min_vs_same_operator=Short.parseShort(fields[1]);
			min=Short.parseShort(fields[2]);
			sms_vs_same_operator=Short.parseShort(fields[3]);
			sms=Short.parseShort(fields[4]);
			dataTraffic=Short.parseShort(fields[5]);
			maxAge=Short.parseShort(fields[6]);
			youAndMe=(Short.parseShort(fields[7])==1);
			portability=Short.parseShort(fields[8]);
			
			short i,counter=0;
			if(fields[9].charAt(0)=='*'){
				billWithoutChange=Short.parseShort(fields[9].substring(1));
				i=10;
			}
			else{
				billWithoutChange=0;
				i=9;
			}
			
			while(fields[i].charAt(0)=='$'){
				counter++; i++;
			}
			if(counter>0){
				freeOptions=new short[counter];
				short j=0;
				while(j<counter){
					freeOptions[j]=Short.parseShort(fields[i-counter+j].substring(1));
					j++;
				}
			}
			else freeOptions=new short[0];
			
			
			price=Short.parseShort(fields[i++]);
			riseOfPriceAfterPeriod=Short.parseShort(fields[i++]);
			monthBeforeIncreasing=Float.parseFloat(fields[i++]);
			
			
			credits=Integer.parseInt(fields[i++]);
			
			
			offerName=fields[i++].replace('_', ' ');
			urlOffer=fields[i++];
			
			short j=i; short cont=0;
			if(j<fields.length)
				while(fields[j++].charAt(0)=='$'){
					cont++;
					
					if(j<fields.length)
						;
					else
						break;
				}
			
			if(cont<=0) options=new short[0];
			else{
				options=new short[cont];
				j=0;
				while(j<cont){
					options[j++]=Short.parseShort(fields[i++].substring(1));
				}
			}
			
			//concat the free options, with the options, to semplify the code that calculate all the offers
			options=concat(options,freeOptions);
			
			
			if(i>=fields.length)
				return;
			
			moreInfos=fields[i];
			
		}
		catch(Exception e){
			
			error=true;
		}
		
		
	}
	
	private static short[] concat(short[] array1, short[] array2){
		
		short[] ret=new short[array1.length+array2.length];
		int i;
		for(i=0;i<array1.length;i++)
			ret[i]=array1[i];
		
		for(int j=0;j<array2.length;j++)
			ret[i++]=array2[j];
		
		return ret;
	}
	
	private static int contains(short[] array, short value){
		
		//special case for options always included
		if(value<-1)
			return value;
		
		for(int i=0;i<array.length;i++){
			if(array[i]==value)
				return i;
		}
		return -1;
	}
	
	private static ArrayList<Option> copy(ArrayList<Option> al){
		ArrayList<Option> ret=new ArrayList<Option>();
		for(int i=0;i<al.size();i++) ret.add(al.get(i));
		return ret;
	}
	
	//return an object that contain the minor cost, and the best combination between rate offer and option, plus two array to identify,
	//in case the offer have credits, how to distribute the credits.
	//The function has a complexity of O(r*o*(opt+opt^3))
	//where r are the rates, o are the offers and opt are the options.
	public ArrayList<Result> getCost(Rate[] rates,
			Option[] optionsO,
			float smsPerDaySameOperator, float smsPerDayOtherOperator,
			float callsPerDaySameOperator, float minutesPerDaySameOperator, float callPerDayOtherOperator, float minutesPerDayOtherOperator,
			float mbPerDay,
			float dailyMinutesToYouAndMe
			){
		
		ArrayList<Result> results=new ArrayList<Result>();
		
		float totalCost=0,minCost=Float.MAX_VALUE;
		
		Offer ret=new Offer(this);
		
		int [] credits = null;
		int[] credits_indexes=null;
		
		for(Rate r:rates){
			
			totalCost=0;
			
			if(r.operator.equals(operator)){
				totalCost=(float)r.price*10f;
				
				/*Compute how many uses of the phones have been in a month averagely*/
				int sms_tot_SameOp=(int)(smsPerDaySameOperator*30.0f);
				int sms_tot_OtherOp=(int)(smsPerDayOtherOperator*30.0f);
				
				int minutes_tot_SameOp;
				if(youAndMe)
					minutes_tot_SameOp=(int)(((minutesPerDaySameOperator-dailyMinutesToYouAndMe)*30.0f));
				else
					minutes_tot_SameOp=(int)(minutesPerDaySameOperator*30.0f);
				int minutes_tot_OtherOp=(int)(minutesPerDayOtherOperator*30.0f);
				int calls_tot_SameOp=(int)(callsPerDaySameOperator*30.0f);
				int calls_tot_OtherOp=(int)(callPerDayOtherOperator*30.0f);
				
				int tot_mb=(int)(mbPerDay*30.0f);
				
				//if infinite call vs same operator or others and for sms
				if(this.min_vs_same_operator==-1){
					minutes_tot_SameOp=0;
					calls_tot_SameOp=0;
				}
				if(this.min==-1){
					minutes_tot_SameOp=0;
					calls_tot_SameOp=0;
					minutes_tot_OtherOp=0;
					calls_tot_OtherOp=0;
				}
				if(this.sms_vs_same_operator==-1){
					sms_tot_SameOp=0;
				}
				if(this.sms==-1){
					sms_tot_SameOp=0;
					sms_tot_OtherOp=0;
				}
				
				//if is an offer of classical style, then without credits
				if(this.credits==0){
					totalCost+=(float)this.price*10.0f; //add to the total cost the price of the offer, converting it into milli cent!
					
					/**********************sms******************************/
					int payed_sms=0;
					
					if(sms_vs_same_operator!=-1){
						payed_sms=sms_tot_SameOp-sms_vs_same_operator;
						if(payed_sms<=0)
							payed_sms=0;
						else{
							int tmp=payed_sms-sms;
							if(tmp<=0){
								sms=(short) Math.abs(tmp);
								payed_sms=0;
							}
							else{
								payed_sms=tmp;
								sms=0;
							}
						}
					}
					totalCost+=((float)payed_sms*(float)r.sms_cost);
					
					payed_sms=0;
					//if not infinite sms
					if(sms!=-1){
						payed_sms=sms_tot_OtherOp-sms;
						if(payed_sms<=0)
							payed_sms=0;
					}
					totalCost+=((float)payed_sms*(float)r.sms_cost);
					
					
					/***********************calls*seconds*****************************/
					float total_payed_minutes=0;
					int payed_seconds=0;
					
					if(min_vs_same_operator!=-1){
						payed_seconds=(minutes_tot_SameOp-min_vs_same_operator)*60;
						if(payed_seconds<=0)
							payed_seconds=0;
						else{
							int tmp=payed_seconds-(int)(min*60.0f);
							if(tmp<=0){
								min=(short)Math.abs((float)tmp/60.0f);
								payed_seconds=0;
							}
							else{
								payed_seconds=tmp;
								min=0;
							}
						}
					}
					total_payed_minutes=payed_seconds;
					
					totalCost+=(((float)payed_seconds)/r.sizeCalls)*(float)r.callsPrice_for_size;
					
					payed_seconds=0;
					//if not infinite minutes
					if(min!=-1){
						payed_seconds=(minutes_tot_OtherOp-min)*60;
						if(payed_seconds<=0)
							payed_seconds=0;
					}
					total_payed_minutes+=payed_seconds;
					
					totalCost+=(((float)payed_seconds)/r.sizeCalls)*(float)r.callsPrice_for_size;
					
					/***********************rush*calls*******************************************/
					total_payed_minutes/=60.0f;
					float totalMinutes=(float)minutes_tot_SameOp+(float)minutes_tot_OtherOp;
					float weight=total_payed_minutes/totalMinutes;
					if(total_payed_minutes==0.0f || totalMinutes==0.0f)
						weight=0;
					totalCost+=((float)(calls_tot_SameOp+calls_tot_OtherOp)*(float)r.rush_answer)*weight;
					
					
					/***********************data*traffic*******************************************/
					int payed_mb=tot_mb-dataTraffic;
					if(payed_mb<=0)
						payed_mb=0;
					//totalCost+=((float)(payed_mb)/(float)r.sizeDataTraffic)*(float)r.priceDataTraffic;
					else if(tot_mb>0){
						float rapport=(float)payed_mb/(float)tot_mb;
						int days=(int)Math.ceil(rapport*30.0f);
						float dailyDataTraffic=payed_mb/(float)days;
						int chunks=(int)Math.ceil(dailyDataTraffic/r.sizeDataTraffic);
						totalCost+=chunks*days*r.priceDataTraffic;
					}
					
					
					//add this result to all the combinations
					results.add(new Result(ret,r,null,totalCost,null,null));
					
					minCost=totalCost;
					ArrayList<Option> opt=new ArrayList<Option>(); //the list of good option to combine with
					for(Option oo:optionsO){
						
						int index=contains(options,oo.id);
						if(index>-1){
							
							
							totalCost=tryOption(oo,r,opt,
							sms_tot_SameOp, sms_tot_OtherOp,
							minutes_tot_SameOp, minutes_tot_OtherOp,
							dailyMinutesToYouAndMe, minutesPerDaySameOperator,
							calls_tot_SameOp, calls_tot_OtherOp,
							tot_mb);
							
							//if with the option selected, the price is going down, add the option to the set of best combination!
							if(totalCost<minCost){
								minCost=totalCost;
								results.add(new Result(ret,r,copy(opt),totalCost,null,null));
							}
							else
								opt.remove(oo);
							
						}
					}
					
					
					
					//If there is a way to enhance finding better options, try!
					//For each option try to remove it and substitute with another one and see if the price is better!
					Result prec=null,res=null;
					do{
						prec=res;
						if(prec!=null) totalCost=prec.cost;
						res=areTheBestOptions(totalCost, ret, optionsO, r, opt,
								sms_tot_SameOp, sms_tot_OtherOp,
								minutes_tot_SameOp, minutes_tot_OtherOp,
								dailyMinutesToYouAndMe, minutesPerDaySameOperator,
								calls_tot_SameOp, calls_tot_OtherOp,
								tot_mb);
					}while(res!=null);
					if(prec!=null)	results.add(prec);
					
				}
				else{
					totalCost+=(float)this.price*10.0f;
					
					float costPerMinute=((60f/(float)r.sizeCalls)*(float)r.callsPrice_for_size);
					float costSms=r.sms_cost;
					float costMb=((float)r.priceDataTraffic/(float)r.sizeDataTraffic);
					
					int smsIndex,minutesIndex,mbIndex;
					float[] practice=new float[3];
					if(costPerMinute>costSms && costPerMinute>costMb){
						practice[2]=costPerMinute; minutesIndex=2;
						if(costSms>costMb){
							practice[1]=costSms; smsIndex=1;
							practice[0]=costMb; mbIndex=0;
						}
						else{
							practice[0]=costSms; smsIndex=0;
							practice[1]=costMb; mbIndex=1;
						}
					}
					else if(costPerMinute>costSms && costPerMinute<costMb){
						practice[0]=costSms; smsIndex=0;
						practice[1]=costPerMinute; minutesIndex=1;
						practice[2]=costMb; mbIndex=2;
					}
					else if(costPerMinute<costSms && costPerMinute>costMb){
						practice[0]=costMb; mbIndex=0;
						practice[1]=costPerMinute; minutesIndex=1;
						practice[2]=costSms; smsIndex=2;
					}
					else{
						practice[0]=costPerMinute; minutesIndex=0;
						if(costSms>costMb){
							practice[2]=costSms; smsIndex=2;
							practice[1]=costMb; mbIndex=1;
						}
						else{
							practice[1]=costSms; smsIndex=1;
							practice[2]=costMb; mbIndex=2;
						}
					}
					
					credits_indexes=new int[3];
					credits_indexes[0]=smsIndex; credits_indexes[1]=minutesIndex; credits_indexes[2]=mbIndex;
					
					int totSms=sms_tot_SameOp+sms_tot_OtherOp;
					int totMinutes=minutes_tot_SameOp+minutes_tot_OtherOp;
					
					
					
					Pair<int[],Float> res=useCredits(this.credits,
							practice,smsIndex,minutesIndex,mbIndex,
							totSms,totMinutes,tot_mb,calls_tot_SameOp+calls_tot_OtherOp,
							r);
					credits=res.getFirst();
					totalCost+=res.getSecond();
					
					
					results.add(new Result(ret,r,null,totalCost,cloneArray(credits),cloneArray(credits_indexes)));
					
					minCost=totalCost;
					ArrayList<Option> opt=new ArrayList<Option>(); //list of the "good" options
					for(Option oo:optionsO){
						
						if(oo.operator.equals(this.operator)){
							
							int index=contains(options,oo.id);
							
							if(index>-1){
								int[] credits_;
								Pair<int[],Float> p=tryOptionWithCredits(oo,r,opt,
										sms_tot_SameOp, sms_tot_OtherOp,
										minutes_tot_SameOp, minutes_tot_OtherOp,
										dailyMinutesToYouAndMe, minutesPerDaySameOperator,
										calls_tot_SameOp, calls_tot_OtherOp,
										tot_mb,
										practice,smsIndex,minutesIndex,mbIndex
										);
								credits_=p.getFirst();
								totalCost=p.getSecond();
								
								//if the new option is good, in the sense that the price decrease, hold it!
								if(totalCost<minCost){
									minCost=totalCost;
									results.add(new Result(ret,r,copy(opt),totalCost,cloneArray(credits_),cloneArray(credits_indexes)));
								}
								else
									opt.remove(oo);
								
							}
						}
					}
					
					//If there is a way to enhance finding better options, try!
					//For each option try to remove it and substitute with another one and see if the price is better!
					Result prec=null,result=null;
					do{
						prec=result;
						if(prec!=null) totalCost=prec.cost;
						result=areTheBestOptionsWithCredits(totalCost, ret, optionsO, r, opt,
								sms_tot_SameOp, sms_tot_OtherOp,
								minutes_tot_SameOp, minutes_tot_OtherOp,
								dailyMinutesToYouAndMe, minutesPerDaySameOperator,
								calls_tot_SameOp, calls_tot_OtherOp,
								tot_mb,
								practice,smsIndex,minutesIndex,mbIndex,
								credits_indexes);
					}while(result!=null);
					if(prec!=null)
						results.add(prec);
				}
				
				
				
			}
		}
		return results;
	}
	
	//Control if the set of options found is the best possible subset using a simple algorithm.
	//Given a subset: try to remove an option and substitute it with any other possibility. If the cost is minor take this choice as better and return.
	//Complexity: O(n^2)
	private Result areTheBestOptions(float totalCost, Offer ret, Option[] options, Rate r, ArrayList<Option> opt,
			int sms_tot_SameOp, int sms_tot_OtherOp,
			int minutes_tot_SameOp, int minutes_tot_OtherOp,
			float dailyMinutesToYouAndMe, float minutesPerDaySameOperator,
			int calls_tot_SameOp, int calls_tot_OtherOp,
			int tot_mb){
		
		for(int j=0;j<opt.size();j++){
			Option o=opt.get(j);
			for(int i=0;i<options.length;i++){
				int index=contains(this.options,options[i].id);
				if(!o.equals(options[i]) && index!=-1 && !opt.contains(options[i])){
					opt.remove(o);
					
					float currentCost=tryOption(options[i],r,opt,
							sms_tot_SameOp, sms_tot_OtherOp,
							minutes_tot_SameOp, minutes_tot_OtherOp,
							dailyMinutesToYouAndMe, minutesPerDaySameOperator,
							calls_tot_SameOp, calls_tot_OtherOp,
							tot_mb
							);
					if(currentCost<totalCost)
						return new Result(ret,r,copy(opt),currentCost,null,null);
					else{
						opt.remove(options[i]);
						opt.add(o);
					}
				}
			}
		}
		
		return null;
		
	}
	
	//Same as the previous one but includes the handling of credits
	private Result areTheBestOptionsWithCredits(float totalCost, Offer ret, Option[] options, Rate r, ArrayList<Option> opt,
			int sms_tot_SameOp, int sms_tot_OtherOp,
			int minutes_tot_SameOp, int minutes_tot_OtherOp,
			float dailyMinutesToYouAndMe, float minutesPerDaySameOperator,
			int calls_tot_SameOp, int calls_tot_OtherOp,
			int tot_mb,
			float[] practice, int smsIndex, int minutesIndex, int mbIndex,
			int[] credits_indexes){
		
		for(int j=0;j<opt.size();j++){
			Option o=opt.get(j);
			for(int i=0;i<options.length;i++){
				int index=contains(this.options,options[i].id);
				if(!o.equals(options[i]) && index!=-1 && !opt.contains(options[i])){
					opt.remove(o);
					
					Pair<int[], Float> p=tryOptionWithCredits(options[i],r,opt,
							sms_tot_SameOp, sms_tot_OtherOp,
							minutes_tot_SameOp, minutes_tot_OtherOp,
							dailyMinutesToYouAndMe, minutesPerDaySameOperator,
							calls_tot_SameOp, calls_tot_OtherOp,
							tot_mb,
							practice,smsIndex,minutesIndex,mbIndex);
					if(p.getSecond()<totalCost)
						return new Result(ret,r,copy(opt),p.getSecond(),cloneArray(p.getFirst()),cloneArray(credits_indexes));
					else{
						opt.remove(options[i]);
						opt.add(o);
					}
				}
			}
		}
		
		return null;
		
	}
	
	
	//Try an option and find the resulting cost. (The option passed as argument is added to the list of all the options in the state).
	//More options can be at the same time used, inserting them into the list of options
	private float tryOption(Option oo, Rate r,
			ArrayList<Option> opt,
			int sms_tot_SameOp, int sms_tot_OtherOp,
			int minutes_tot_SameOp, int minutes_tot_OtherOp,
			float dailyMinutesToYouAndMe, float minutesPerDaySameOperator,
			int calls_tot_SameOp, int calls_tot_OtherOp,
			int tot_mb
			){
		//make a temporary copy of the options, to return this in case is needed, because the other one (o)  will be modificated
		Option oCopy=new Option(oo);
		Option o=new Option(oo);
		
		opt.add(new Option(oCopy)); //temporary add the new discovered option to the list of good option
		
		//create a fake option, with all the attributes of the other options merged   ******** simulate all the options working together
		o.sms=0; o.sms_vs_same_operator=0; o.min=0; o.min_vs_same_operator=0; o.dataTraffic=0;
		o.price=0;
		for(int i=0;i<opt.size();i++){
			if(opt.get(i).sms==-1)
				o.sms=-1;
			else if(o.sms!=-1) o.sms+=opt.get(i).sms;
			
			if(opt.get(i).sms_vs_same_operator==-1)
				o.sms_vs_same_operator=-1;
			else if(o.sms_vs_same_operator!=-1) o.sms_vs_same_operator+=opt.get(i).sms_vs_same_operator;
			
			if(opt.get(i).min==-1)
				o.min=-1;
			else if(o.min!=-1)
				o.min+=opt.get(i).min;
			
			if(opt.get(i).min_vs_same_operator==-1)
				o.min_vs_same_operator=-1;
			else if(o.min_vs_same_operator!=-1)
				o.min_vs_same_operator+=opt.get(i).min_vs_same_operator;
			
			o.dataTraffic+=opt.get(i).dataTraffic;
			o.youAndMe|=opt.get(i).youAndMe;
			o.price+=opt.get(i).price;
		}
		
		
		float totalCost=((float)r.price*10.0f)+((float)this.price*10.0f)+((float)o.price)*10.0f;
		
		
		/**********************sms******************************/
		int payed_sms=0;
		
		if(sms_vs_same_operator!=-1 && o.sms_vs_same_operator!=-1 ){
			payed_sms=sms_tot_SameOp-sms_vs_same_operator;
			for(int i=0;i<opt.size();i++) payed_sms-=opt.get(i).sms_vs_same_operator;
			if(payed_sms<=0)
				payed_sms=0;
			else{
				int tmp=payed_sms-sms;
				if(tmp<=0){
					sms=(short) Math.abs(tmp);
					payed_sms=0;
				}
				else{
					payed_sms=tmp;
					sms=0;
					
					tmp=payed_sms-o.sms;
					if(tmp<=0){
						o.sms=(short) Math.abs(tmp);
						payed_sms=0;
					}
					else{
						payed_sms=tmp;
						o.sms=0;
					}
				}
			}
		}
		totalCost+=((float)payed_sms*(float)r.sms_cost);
		
		payed_sms=0;
		if(sms!=-1 && o.sms!=-1){
			payed_sms=sms_tot_OtherOp-sms-o.sms;
			if(payed_sms<0)
				payed_sms=0;
		}
		totalCost+=((float)payed_sms*(float)r.sms_cost);
		
		
		/***********************calls*seconds*****************************/
		if(!youAndMe && o.youAndMe)
			minutes_tot_SameOp=(int)((minutesPerDaySameOperator-dailyMinutesToYouAndMe)*30.0f);
		
		int payed_seconds=0, total_payed_minutes=0;
		
		if(min_vs_same_operator!=-1 && o.min_vs_same_operator!=-1){
			payed_seconds=(minutes_tot_SameOp-min_vs_same_operator-o.min_vs_same_operator)*60;
			
			if(payed_seconds<=0)
				payed_seconds=0;
			else{
				int tmp=payed_seconds-(int)(min*60.0f);
				if(tmp<=0){
					min=(short)Math.abs((float)tmp/60.0f);
					payed_seconds=0;
				}
				else{
					payed_seconds=tmp;
					min=0;
					
					tmp=payed_seconds-(int)(o.min*60.0f);
					if(tmp<=0){
						o.min=(short) Math.abs((float)tmp/60.0f);
						payed_seconds=0;
					}
					else{
						payed_seconds=tmp;
						o.min=0;
					}
				}
			}
			total_payed_minutes=payed_seconds;
		}
		totalCost+=(((float)payed_seconds)/r.sizeCalls)*(float)r.callsPrice_for_size;
		
		
		payed_seconds=0;
		
		if(min!=-1 && o.min!=-1){
			payed_seconds=(minutes_tot_OtherOp-min-o.min)*60;
			
			if(payed_seconds<=0)
				payed_seconds=0;
			total_payed_minutes+=payed_seconds;
		}
		totalCost+=(((float)payed_seconds)/r.sizeCalls)*(float)r.callsPrice_for_size;
		
		/***********************rush*calls*******************************************/
		total_payed_minutes/=60.0f;
		float totalMinutes=(float)minutes_tot_SameOp+(float)minutes_tot_OtherOp;
		float weight=total_payed_minutes/totalMinutes;
		if(total_payed_minutes==0.0f || totalMinutes==0.0f)
			weight=0;
		totalCost+=((float)(calls_tot_SameOp+calls_tot_OtherOp)*(float)r.rush_answer)*weight;
		
		
		/***********************data*traffic*******************************************/
		int payed_mb=tot_mb-dataTraffic-o.dataTraffic;
		if(payed_mb<=0)
			payed_mb=0;
		//totalCost+=((float)(payed_mb)/(float)r.sizeDataTraffic)*(float)r.priceDataTraffic;
		else if(tot_mb>0){
			float rapport=(float)payed_mb/(float)tot_mb;
			int days=(int)Math.ceil(rapport*30.0f);
			float dailyDataTraffic=payed_mb/(float)days;
			int chunks=(int)Math.ceil(dailyDataTraffic/r.sizeDataTraffic);
			totalCost+=chunks*days*r.priceDataTraffic;
		}
		
		return totalCost;
	}
	
	private Pair<int[], Float> tryOptionWithCredits(Option oo, Rate r,
			ArrayList<Option> opt,
			int sms_tot_SameOp, int sms_tot_OtherOp,
			int minutes_tot_SameOp, int minutes_tot_OtherOp,
			float dailyMinutesToYouAndMe, float minutesPerDaySameOperator,
			int calls_tot_SameOp, int calls_tot_OtherOp,
			int tot_mb,
			float[] practice, int smsIndex, int minutesIndex, int mbIndex
			){
		
		Option copyO=new Option(oo);
		Option o=new Option(oo);
		
		opt.add(new Option(copyO)); //temporary add the new discovered option to the list of good option
		
		o.sms=0; o.sms_vs_same_operator=0; o.min=0; o.min_vs_same_operator=0; o.dataTraffic=0;
		o.price=0;
		for(int i=0;i<opt.size();i++){
			if(opt.get(i).sms==-1)
				o.sms=-1;
			else if(o.sms!=-1) o.sms+=opt.get(i).sms;
			
			if(opt.get(i).sms_vs_same_operator==-1)
				o.sms_vs_same_operator=-1;
			else if(o.sms_vs_same_operator!=-1) o.sms_vs_same_operator+=opt.get(i).sms_vs_same_operator;
			
			if(opt.get(i).min==-1)
				o.min=-1;
			else if(o.min!=-1)
				o.min+=opt.get(i).min;
			
			if(opt.get(i).min_vs_same_operator==-1)
				o.min_vs_same_operator=-1;
			else if(o.min_vs_same_operator!=-1)
				o.min_vs_same_operator+=opt.get(i).min_vs_same_operator;
			
			o.dataTraffic+=opt.get(i).dataTraffic;
			o.youAndMe|=opt.get(i).youAndMe;
			o.price+=opt.get(i).price;
		}
		
		float totalCost=(r.price*10.0f)+(this.price*10.0f)+(o.price*10.0f);
		
		
		int restSms=0;
		
		if(o.sms_vs_same_operator!=-1){
			//Use free sms
			restSms=sms_tot_SameOp-o.sms_vs_same_operator;
			if(restSms<=0){
				restSms=0;
			}
			else{
				int tmp=restSms-o.sms;
				if(tmp<=0){
					o.sms=(short)-(tmp);
					restSms=0;
				}
				else{
					o.sms=0;
					restSms=tmp;
				}
			}
		}
		
		int restSms1=0;
		
		if(o.sms!=-1){
			restSms1=sms_tot_OtherOp-o.sms;
			if(restSms1<=0){
				restSms1=0;
			}
		}
		int totSms=restSms+restSms1;
		
		int restMinutes=0;
		
		if(o.min_vs_same_operator!=-1){
			//Use free minutes
			restMinutes=minutes_tot_SameOp-o.min_vs_same_operator;
			if(restMinutes<=0){
				restMinutes=0;
			}
			else{
				int tmp=restMinutes-o.min;
				if(tmp<=0){
					o.min=(short)-(tmp);
					restMinutes=0;
				}
				else{
					o.min=0;
					restMinutes=tmp;
				}
			}
		}
		
		int restMinutes1=0;
		if(o.min!=-1){
			restMinutes1=minutes_tot_OtherOp-o.min;
			if(restMinutes1<=0){
				restMinutes1=0;
			}
		}
		int totMinutes=restMinutes+restMinutes1; //tot minutes payed
		
		float weight=(float)totMinutes/(float)(minutes_tot_SameOp+minutes_tot_OtherOp);
		if(minutes_tot_SameOp+minutes_tot_OtherOp==0.0f || totMinutes==0.0f)
			weight=0;
		int totCalls=(int)(((float)(calls_tot_SameOp+calls_tot_OtherOp))*weight);
		
		
		//Use free mb
		tot_mb=tot_mb-o.dataTraffic;
		if(tot_mb<=0){
			tot_mb=0;
		}
		
		Pair<int[],Float> res=useCredits(this.credits,
				practice,smsIndex,minutesIndex,mbIndex,
				totSms,totMinutes,tot_mb,totCalls,
				r);
		int[] credits_=res.getFirst();
		totalCost+=res.getSecond();
		
		return new Pair<int[],Float>(credits_ , (totalCost));
		
	}
	
	private int[] cloneArray(int [] array){
		int[] ret=new int[array.length];
		for(int i=0;i<array.length;i++) ret[i]=array[i];
		return ret;
	}
	
	private Pair<int[], Float> useCredits(int tot_credits,
			float[] practice, int smsIndex,int minutesIndex, int mbIndex,
			int totSms, int totMinutes, int tot_mb, int callsTot,
			Rate r){
	
		float totalCost=0;
		int[] credits= new int[3];
		credits[0]=0; credits[1]=0; credits[2]=0;
		for(int i=2;i>=0;i--){
			
		if(smsIndex==i){
			
			int rest=totSms-tot_credits;
			if(rest<=0){
				credits[0]=tot_credits-(-rest);
				tot_credits=-rest;
			}
			else{
				credits[0]=tot_credits;
				totalCost+=(float)rest*practice[smsIndex];
				tot_credits=0;
			}
				
			
		}
		else if(minutesIndex==i){
			int rest=totMinutes-tot_credits;
			if(rest<=0){
				credits[1]=tot_credits-(-rest);
				tot_credits=-rest;
			}
			else{
				credits[1]=tot_credits;
				totalCost+=rest*practice[minutesIndex];
				tot_credits=0;
				
				float weight=(float)rest/(float)totMinutes;
				if(rest==0.0f || totMinutes==0.0f)
					weight=0;
				totalCost+=(((float)(callsTot))*(float)r.rush_answer)*weight;
			}
		}
		else{
			int rest=tot_mb-tot_credits;
			if(rest<=0){
				credits[2]=tot_credits-(-rest);
				tot_credits=-rest;
			}
			else{
				credits[2]=tot_credits;
				//totalCost+=rest*practice[mbIndex];
				if(tot_mb>0){
					float rapport=(float)rest/(float)tot_mb;
					int days=(int)Math.ceil(rapport*30.0f);
					float dailyDataTraffic=rest/(float)days;
					int chunks=(int)Math.ceil(dailyDataTraffic/r.sizeDataTraffic);
					totalCost+=chunks*days*r.priceDataTraffic;
				}
				tot_credits=0;
			}
		}
	}
		
	return new Pair<int[],Float>(credits,totalCost);
	
	
	}
}
