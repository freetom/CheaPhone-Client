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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

import android.content.res.AssetManager;

public class Utility {
	
	public static Boolean existsFile(String filepath) {
		
		try {
	        File file = new File(filepath);
	        return file.exists();
	    } 
	    catch(Exception e) { 
	        
	        return false;
	    }
	}
	
	
	public static boolean areSameDay(Calendar c1, Calendar c2){
		
		return
				(
					c1.get(Calendar.YEAR)==c2.get(Calendar.YEAR) &&
					c1.get(Calendar.MONTH)==c2.get(Calendar.MONTH) &&
					c1.get(Calendar.DAY_OF_MONTH)==c2.get(Calendar.DAY_OF_MONTH)
				);
	}
	
	public static long getDaysOfDifference(Calendar newest, Calendar oldest){
		long days=(newest.getTimeInMillis()-oldest.getTimeInMillis())/86400000l;
		if(days==0) days=1;
		return days;
	}
	
	public static void CreateEmptyFile(String path) throws FileNotFoundException{
		PrintWriter out = new PrintWriter(path);
		out.write(" ");
		out.flush();
		out.close();
	}
	
	public static boolean contains(short[] vett, short id){
		for(int i=0;i<vett.length;i++)
			if(vett[i]==id)
				return true;
		return false;
	}
	
	public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
	
	public static String readAsset(AssetManager mgr, String path) {
	    String contents = "";
	    InputStream is = null;
	    BufferedReader reader = null;
	    try {
	        is = mgr.open(path);
	        reader = new BufferedReader(new InputStreamReader(is));
	        contents = reader.readLine();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            contents += '\n' + line;
	        }
	    } catch (final Exception e) {
	        e.printStackTrace();
	    } finally {
	        if (is != null) {
	            try {
	                is.close();
	            } catch (IOException ignored) {
	            }
	        }
	        if (reader != null) {
	            try {
	                reader.close();
	            } catch (IOException ignored) {
	            }
	        }
	    }
	    return contents;
	}
	
	
	public static String fileToString(String path){
		String fileContent="";
		try {
			File f = new File(path);
	        FileInputStream inp = new FileInputStream(f);
	        byte[] bf = new byte[(int)f.length()];
	        inp.read(bf);
	        fileContent = new String(bf, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
	      	e.printStackTrace();
		}
		return fileContent;
	}
	
	public static void stringToFile( String text, String fileName )
	{
	 try
	 {
	    File file = new File( fileName );

	    // if file doesnt exists, then create it 
	    if ( ! file.exists( ) )
	    {
	        file.createNewFile( );
	    }

	    FileWriter fw = new FileWriter( file.getAbsoluteFile( ) );
	    BufferedWriter bw = new BufferedWriter( fw );
	    bw.write( text );
	    bw.close( );
	    
	 }
	 catch( IOException e )
	 {
	 System.out.println("Error: " + e);
	 e.printStackTrace( );
	 }
	} 
	
}
