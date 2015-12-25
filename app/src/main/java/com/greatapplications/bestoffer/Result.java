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

public class Result {
	
	public Offer offer;
    public Rate rate;
    public ArrayList<Option> options;
    public float cost;
	
	//vector that contains the credits
	//or null if offer have no credits
    public int[] credits_dist;
	//at 0 sms index, at 1  call index, at 2 data traffic index
    public int[] indexes_credits;
	public Result(Offer offer, Rate rate, ArrayList<Option> option, float cost, int[] credits, int[] indexes_credits){
		this.offer=offer;
		this.rate=rate;
		this.options=option;
		this.cost=cost;
		this.credits_dist=credits;
		this.indexes_credits=indexes_credits;
	}
}
