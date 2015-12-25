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

public class Rate {

    public String operator;
    public short rush_answer; //milli euro
    public short callsPrice_for_size; //milli euro
    public short sizeCalls; //seconds
    public short sms_cost; //milli euro
    public short priceDataTraffic; //millieuro/size
    public short sizeDataTraffic; //mb
    public short price; //cent
    public String rateName;
    public String urlRate;
    public String moreInfos;

    public boolean error;
	public Rate(String rate){
		error=false;
		
		String[] fields=rate.split(" ");
		try{
			operator=fields[0];
			rush_answer=Short.parseShort(fields[1]);
			callsPrice_for_size=Short.parseShort(fields[2]);
			sizeCalls=Short.parseShort(fields[3]);
			sms_cost=Short.parseShort(fields[4]);
			sizeDataTraffic=Short.parseShort(fields[5]);
			priceDataTraffic=Short.parseShort(fields[6]);
			price=Short.parseShort(fields[7]);
			rateName=fields[8].replace('_', ' ');
			urlRate=fields[9];
			if(10<fields.length)	moreInfos=fields[10].replace('_', ' ');
			
		}
		catch(Exception e){
			error=true;
		}
		
		
	}
}
