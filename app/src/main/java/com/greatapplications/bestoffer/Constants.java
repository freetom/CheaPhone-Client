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

import android.content.Context;
import android.telephony.TelephonyManager;

public class Constants {
	/*
	 * Constants used by the app, always needed
	 * 
	 * Bortoli Tomas
	 * */
	//how many days have the service to keep information in memory
	public final static short days_of_memory=120;
	
	//path of sms db, call db, data traffic db, cache file's path, offers's path and is first calculus file path
	public final static String smsDatabasePath="smsDB";
	public final static String smsDatabasePath1="smsDB1";
	public final static String callsDatabasePath="callsDB";
	public final static String callsDatabasePath1="callsDB1";
	public final static String dataTrafficDatabasePath="dataTrafficDB";
	public final static String dataTrafficDatabasePath1="dataTrafficDB1";
	public final static String cacheFilePath="cacheNumbersToOperators";
	public final static String cacheFilePath1="cacheNumbersToOperators1";
	public final static String cacheKey="cacheKey";
	public final static String offersFilePath="offers";
	public final static String isFirstCalculus="firstCalculus";
	public final static String localTime="localtime";
	public final static String operatorFile="operator";
	
	//path of the app's file
	public static String applicationFilesPath="";
	
	//name of the app
	public static String applicationName="Cheaphone";
	
	//message to show in a notification when the app is ready for the first calculus
	public static String message_ready_first_calculus=applicationName+" è pronto per il calcolo dell'offerta";
	//message to show when new file of offers is downloaded
	public static String message_new_offers="Le offerte sono state aggiornate!";
	
	/********Here is the statement of the server's ip or hostname*****/
	public static String serverIP="cheaphone.rocks";
	public static int serverPort=1003;
	
	public static String nessunaOfferta="nessuna_offerta";
	public static String nessunaOpzione="nessuna_opzione";

    public final static String installationDate="installationDate";
    public final static String installationDate2="installationDate2";




    public static String operator="";
	
	//update the local operator, in upper case to facilitate matching with other operators (in different format)
	public static void updateOperator(Context c){
		TelephonyManager tm = (TelephonyManager) c.getSystemService( "phone" );
		String tmp_operator=tm.getNetworkOperatorName().toUpperCase();
		if(!tmp_operator.equals("")){
			Utility.stringToFile(tmp_operator,Constants.applicationFilesPath+operatorFile);
			operator=tmp_operator;
		}
	}
	
	public static boolean validOperator(){
		String tmp_operator=Utility.fileToString(Constants.applicationFilesPath+operatorFile);
		if(!tmp_operator.equals("")){
			operator=tmp_operator;
			return true;
		}
		else
			return false;
	}
}
