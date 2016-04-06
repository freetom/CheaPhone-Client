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

/**
 * Created by giacomo on 11/12/2014.
 *
 * Modified by Tomas Bortoli
 */
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cheaphone.core.ComputeBestOffer;
import cheaphone.core.MainService;
import cheaphone.core.Pair;
import cheaphone.core.R;


//Pagina principale con bottone
public class Home extends Fragment {


    private static final int PROGRESS = 0x1;
    private TextView giorni_calls;
    private TextView giorni_sms;
   private TextView giorni_dth;
    int text_color = Color.rgb(74, 134, 232);
    long data;
    long sms;
    long calls;


    public Home(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.home, container, false);


        Point P = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(P);



        final TextView destinatari=(TextView) rootView.findViewById(R.id.frase3);
        LinearLayout l=(LinearLayout) rootView.findViewById(R.id.numeri);
        l.removeAllViews();
        giorni_calls=new TextView(getActivity());
        giorni_sms=new TextView(getActivity());
        giorni_dth=new TextView(getActivity());


        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean Iflag=true;
                while(Iflag) {
                    if (MainService.ready) {
                        Iflag = false;
                    }
                }
                    try {
                        Thread.sleep(300);

                        sms = MainService.hs.getDaysOfMonitoring();
                        data = MainService.dth.getDaysOfMonitoring();
                        calls = MainService.hc.getDaysOfMonitoring();

                        if (sms == -1)
                            sms = 0;
                        if (calls == -1)
                            calls = 0;
                        if (data == -1)
                            data = 0;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(calls>=0 && sms>=0 && data>=0) {
                                    giorni_calls.setText(String.valueOf(calls) + " giorni");
                                    giorni_sms.setText(String.valueOf(sms) + " giorni");
                                    giorni_dth.setText(String.valueOf(data) + " giorni");
                                }
                                else {
                                    giorni_calls.setText("N/A");
                                    giorni_sms.setText("N/A");
                                    giorni_dth.setText("N/A");
                                }
                            }
                        });


                        final Pair<Integer,Integer> unknowNumbers = ComputeBestOffer.getKnownAndUnknownOperators(MainService.hs,MainService.hc,MainService.cache);




                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                destinatari.setText("Operatori dei destinatari riconosciuti: " + String.valueOf(unknowNumbers.getFirst()-unknowNumbers.getSecond()) + " su " + String.valueOf(unknowNumbers.getFirst()));
                            }
                        });



                        if (MainService.hs != null && MainService.dth != null && MainService.hc != null) {
                            Home home = (Home) getFragmentManager().findFragmentById(R.id.home);
                            home.onResume();

                        }
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }

        }).start();

        giorni_calls.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        giorni_sms.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        giorni_dth.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));

        giorni_calls.setGravity(Gravity.CENTER);
        giorni_sms.setGravity(Gravity.CENTER);
        giorni_dth.setGravity(Gravity.CENTER);

        giorni_calls.setTextColor(text_color);
        giorni_sms.setTextColor(text_color);
        giorni_dth.setTextColor(text_color);

        giorni_calls.setTextSize(14);
        giorni_sms.setTextSize(14);
        giorni_dth.setTextSize(14);


        l.addView(giorni_calls);
        l.addView(giorni_sms);
        l.addView(giorni_dth);









        //Simboli sms-chiamate-traffico dati
        ImageView calls = new ImageView(getActivity());
        ImageView sms =new ImageView(getActivity());
        ImageView td=new ImageView(getActivity());

       LinearLayout r=(LinearLayout) rootView.findViewById(R.id.immagini);

        calls.setImageResource(R.drawable.ic_phone_forward_grey600_36dp);
        sms.setImageResource(R.drawable.ic_comment_text_outline_grey600_36dp);
        td.setImageResource(R.drawable.traffic_arrows);

        calls.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        sms.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        td.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));

        calls.setPadding(8, 8, 8, 8);
        sms.setPadding(8, 8, 8, 8);
        td.setPadding(8, 8, 8, 8);

        r.addView(calls);
        r.addView(sms);
        r.addView(td);

        return rootView;

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/helvetica-neue-bold.ttf");
        //Fragment params
        TextView t=(TextView) getActivity().findViewById(R.id.frase);
        t.setTextSize(30);
        t.setTypeface(type);




    }

}
