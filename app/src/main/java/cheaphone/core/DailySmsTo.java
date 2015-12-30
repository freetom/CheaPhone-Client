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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;


public class DailySmsTo implements Serializable{
	
	String number;
	Integer n;
	Calendar day;
	
	public DailySmsTo(String number,Calendar day){
		this.number=number;
		this.day=day;
	}
	
	public DailySmsTo(String number, Integer nOrMinutes, Calendar day){
		this.number=number;
		this.n=nOrMinutes;
		this.day=day;
	}
	
	@Override
	public String toString(){
		return number+" "+n;
	}
	
	//Are equals if corresponds the data and number.
	@Override
	public boolean equals(Object o){
		if(o instanceof DailySmsTo)
			return this.number.compareTo(((DailySmsTo)o).number)==0 && 
					this.day.get(Calendar.YEAR) == ((DailySmsTo)o).day.get(Calendar.YEAR) && 
					this.day.get(Calendar.MONTH) == ((DailySmsTo)o).day.get(Calendar.MONTH) &&
					this.day.get(Calendar.DAY_OF_MONTH) == ((DailySmsTo)o).day.get(Calendar.DAY_OF_MONTH);
					   
		else return false;
	}
	
	//Static function that find the index of the value that have the number and same day, month and year passed as parameter
	//In an array of DailySmsOrCallsTo
	public static int findByNumberAndDate(ArrayList<DailySmsTo> al, String number, Calendar date){
		for(int i=0;i<al.size();i++){
			if(al.get(i).number.equals(number)){
				if(al.get(i).day.get(Calendar.YEAR) == date.get(Calendar.YEAR) && 
				   al.get(i).day.get(Calendar.MONTH) == date.get(Calendar.MONTH) &&
				   al.get(i).day.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH))
					return i;
			}
		}
		return -1;
	}
}
