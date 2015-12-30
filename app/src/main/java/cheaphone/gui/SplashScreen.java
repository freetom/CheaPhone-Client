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
package cheaphone.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import cheaphone.core.MainService;
import cheaphone.core.R;

/**
 * Created by jack on 12/18/2015.
 */
public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        startService(new Intent(this, MainService.class));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    while(!MainService.ready) {
                        Thread.sleep(100);
                    }
                    if(MainService.ready /*&& MainService.dth.getDaysOfMonitoring()>0 && MainService.haveOffers *//*&& ComputeBestOffer.checkForUnknownOperators(MainService.hs, MainService.hc, MainService.cache)*/)
                    // Constants.validOperator() &&
                    //MainService.haveSuffInfo &&

                    {
                                Intent main = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(main);
                    }

                }
                catch(Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "errore2", Toast.LENGTH_LONG).show();
                        }
                    });
                    System.out.println(e.toString());
                }

            }

        }).start();


    }


}
