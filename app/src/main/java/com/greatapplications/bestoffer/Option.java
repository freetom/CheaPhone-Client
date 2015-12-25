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

public class Option {


    public String operator;
    public short id;
    public short min_vs_same_operator;
    public short min;
    public short sms_vs_same_operator;
    public short sms;
    public short dataTraffic;
    public short maxAge;
    public boolean youAndMe;
    public short price; //cent
    public String nameOptions;
    public String urlOptions;
    public String moreInfos;

    public boolean error;
	public Option(String option){
		error=false;
		
		String[] fields=option.split(" ");
		
		try{
			operator=fields[0];
			id=Short.parseShort(fields[1]);
			min_vs_same_operator=Short.parseShort(fields[2]);
			min=Short.parseShort(fields[3]);
			sms_vs_same_operator=Short.parseShort(fields[4]);
			sms=Short.parseShort(fields[5]);
			dataTraffic=Short.parseShort(fields[6]);
			maxAge=Short.parseShort(fields[7]);
			youAndMe=(Short.parseShort(fields[8])==1);
			price=Short.parseShort(fields[9]);
			nameOptions=fields[10].replace('_', ' ');;
			urlOptions=fields[11];
			if(12<fields.length)	moreInfos=fields[12].replace('_', ' ');
		}
		catch(Exception e){
			error=true;
		}
	}
	
	public Option(Option o){
		
		operator=o.operator;
		id=o.id;
		min_vs_same_operator=o.min_vs_same_operator;
		min=o.min;
		sms_vs_same_operator=o.sms_vs_same_operator;
		sms=o.sms;
		dataTraffic=o.dataTraffic;
		maxAge=o.maxAge;
		youAndMe=o.youAndMe;
		price=o.price;
		nameOptions=o.nameOptions;
		urlOptions=o.urlOptions;
		moreInfos=o.moreInfos;
	}
	
	@Override
	public boolean equals(Object o){
		return this.id==((Option)o).id;
	}
}
