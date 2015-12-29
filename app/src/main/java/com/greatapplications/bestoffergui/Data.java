/**
 * Disclaimer for Giacomo Todesco
 * This file is not GOOD code.
 * You're pleased to remove all the useless *code* and *comments*. You also have to group
 * all the pieces of code that you've repeated more times around the program in functions.
 * You've to insert good quality comments; or at least something that make sense.
 * Rename files with names that describe them. PLEASE
 * USE *variables*, do not hardcode colors or other attributes; so everything becomes more
 * flexible.
 * You use almost no functions. Almost no class variables, almost nothing; everything is
 * assembled there as a messy puzzle.
 * Patience is gone and now the stormshit come..
 *
 * This shitty code is unacceptable, especially after a year that the project has started.
 * You have to do this huge refactoring before the 15 of january or you will be removed
 * from your position and you're code will eventually follow you.
 * I will check you're work this time.
 *
 * Thanks.
 *
 * Sincerely, Tomas Bortoli
 *
 */
package com.greatapplications.bestoffergui;

import android.media.Image;
import android.widget.ImageView;

/**
 * Created by giacomo on 20/02/2015. modified by tomas
 */
public class Data {
    public String text;
    public float prezzo;
    public int img;

    public Data(String offerta, float prezzo, int img) {
        this.text = offerta;
        this.prezzo = prezzo;
        this.img=img;
    }

    @Override
    public Object clone(){
        return new Data(this.text,this.prezzo,this.img);
    }
}
