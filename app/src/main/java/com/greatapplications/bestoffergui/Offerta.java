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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greatapplications.bestoffer.Option;
import com.greatapplications.bestoffer.R;
import com.greatapplications.bestoffer.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 12/16/2015. Modified by Bortoli Tomas
 */
public class Offerta extends ActionBarActivity {


    public int indice;//indice che indica la pozione dell'offerta selezionata

    private String YandMnumber;
    private float sforo;
    MainActivity m;
    int size;

    int text_color=Color.rgb(74,134,232);

    private List<Option> data_o= new ArrayList<>();
    private List<Data> mData=new ArrayList<Data>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offerta);

        Intent mIntent = getIntent();
        int indice = mIntent.getIntExtra("pos", 0);

        m = MainActivity.getInstance();

        //toolbarFront.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        Typeface t = Typeface.createFromAsset(getAssets(), "fonts/helvetica-neue-bold.ttf");

        YandMnumber=m.getYu();

        sforo=0f;
        data_o.clear();
        if(m.getRet().get(indice).options!=null) {
            for (int i = 0; i < m.getRet().get(indice).options.size(); i++)
                data_o.add(m.getRet().get(indice).options.get(i));
        }
        sforo=(m.getRet().get(indice).cost/1000f)-(m.getRet().get(indice).offer.price/100f);
        for(int k=0;k<data_o.size();k++)
            sforo=sforo-(m.getRet().get(indice).options.get(k).price/100f);

        //dimensioni schermo
        Point P = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(P);

        LinearLayout finestra= (LinearLayout) findViewById(R.id.ff);

        if(m.getRet().get(indice).offer.offerName.compareTo("Nessuna offerta")!=0 || m.getRet().get(indice).offer.offerName.compareTo("nessuna offerta")!=0)
            setOffert(indice);

        if(m.getRet().get(indice).options!=null) {

            for (int i = 0; i < m.getRet().get(indice).options.size(); i++) {
                setOption(indice, i);
            }
        }

        if(m.getRet().get(indice).rate!=null && sforo!=0)
            setRate(indice,sforo);

        //uguale

        RelativeLayout quad = new RelativeLayout(this);
        quad.setBackgroundColor(text_color);
        quad.setGravity(Gravity.CENTER);

        RelativeLayout quad2 = new RelativeLayout(this);
        quad2.setBackgroundColor(text_color);
        quad2.setGravity(Gravity.CENTER);

        TextView te = new TextView(this);
        te.setText("= " + approssimato(String.valueOf(m.getRet().get(indice).cost/1000)));
        te.setTextSize(30);
        te.setTextColor(text_color);
        te.setGravity(Gravity.CENTER);

        LinearLayout tabl = new LinearLayout(this);
        tabl.setOrientation(LinearLayout.HORIZONTAL);

        RelativeLayout.LayoutParams testLP = new RelativeLayout.LayoutParams(P.x / 3, 2);

        testLP.addRule(RelativeLayout.CENTER_IN_PARENT);

        quad.setLayoutParams(testLP);
        te.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.WRAP_CONTENT));
        quad2.setLayoutParams(testLP);

        RelativeLayout quad3 = new RelativeLayout(this);
        quad3.setBackgroundColor(Color.WHITE);

        RelativeLayout quad4 = new RelativeLayout(this);
        quad4.setBackgroundColor(Color.WHITE);

        quad3.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        quad4.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        quad3.addView(quad);
        quad4.addView(quad2);


        tabl.addView(quad3);
        tabl.addView(te);
        tabl.addView(quad4);

        finestra.addView(tabl);



    }

    public void setOffert(final int index){

        //logo-nome-costo

        //NOME:
        TextView text = new TextView(this);
        text.setText(String.valueOf(m.getRet().get(index).offer.offerName));
        text.setTextSize(20);
        text.setTextColor(text_color);
        text.setGravity(Gravity.CENTER);

        //LOGO:
        LinearLayout oper = new LinearLayout(this);
        ImageView image = new ImageView(this);

        if (String.valueOf(m.getRet().get(index).offer.operator).equals("tre")) {
            image.setImageResource(R.drawable.tre);

        }
        if (String.valueOf(m.getRet().get(index).offer.operator).equals("postemobile")) {
            image.setImageResource(R.drawable.postemobile);

        }
        if (String.valueOf(m.getRet().get(index).offer.operator).equals("wind")) {
            image.setImageResource(R.drawable.wind);

        }
        if (String.valueOf(m.getRet().get(0).offer.operator).equals("vodafone")) {
            image.setImageResource(R.drawable.vodafone);

        }
        if (String.valueOf(m.getRet().get(index).offer.operator).equals("fastweb")) {
            image.setImageResource(R.drawable.fastweb);

        }
        if (String.valueOf(m.getRet().get(index).offer.operator).equals("coopvoce")) {
            image.setImageResource(R.drawable.coopvoce);

        }
        if (String.valueOf(m.getRet().get(index).offer.operator).equals("tim")) {
            image.setImageResource(R.drawable.tim);

        }

        oper.setGravity(Gravity.CENTER);
        oper.addView(image);

        //COSTO:
        TextView cos = new TextView(this);
        cos.setText(approssimato(String.valueOf(m.getRet().get(index).offer.price / 100f)) + "€");
        cos.setTextSize(14);
        cos.setGravity(Gravity.CENTER);

        Typeface t = Typeface.createFromAsset(getAssets(), "fonts/helvetica-neue-bold.ttf");

        cos.setTypeface(t);

        //dimensioni schermo
        Point P = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(P);
        cos.setTextColor(text_color);


        text.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        oper.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        cos.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));

        text.setPadding(8,8,8,8);
        oper.setPadding(8, 8, 8, 8);
        cos.setPadding(8, 8, 8, 8);

        LinearLayout finestra = (LinearLayout) findViewById(R.id.ff);

        LinearLayout tab= new LinearLayout(this);
        tab.setOrientation(LinearLayout.HORIZONTAL);

        tab.addView(oper);
        tab.addView(text);
        tab.addView(cos);


        tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(m.getRet().get(index).offer.urlOffer));
                startActivity(browserIntent);
            }
        });

        finestra.addView(tab);

        //Simboli sms-chiamate-traffico dati
        ImageView calls = new ImageView(this);
        ImageView sms =new ImageView(this);
        ImageView td=new ImageView(this);

        calls.setImageResource(R.drawable.ic_phone_forward_grey600_36dp);
        sms.setImageResource(R.drawable.ic_comment_text_outline_grey600_36dp);
        td.setImageResource(R.drawable.traffic_arrows);

        calls.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        sms.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        td.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));

        calls.setPadding(8, 8, 8, 8);
        sms.setPadding(8, 8, 8, 8);
        td.setPadding(8, 8, 8, 8);

        LinearLayout tab2= new LinearLayout(this);
        tab2.setOrientation(LinearLayout.HORIZONTAL);

        tab2.addView(sms);
        tab2.addView(calls);
        tab2.addView(td);

        finestra.addView(tab2);

        //Numero sms-chiamate-td
        TextView sms_text=new TextView(this);
        sms_text.setTextSize(14);
        sms_text.setTextColor(text_color);
        sms_text.setGravity(Gravity.CENTER);

        TextView calls_text=new TextView(this);
        calls_text.setTextSize(14);
        calls_text.setTextColor(text_color);
        calls_text.setGravity(Gravity.CENTER);

        TextView td_text=new TextView(this);
        td_text.setTextSize(14);
        td_text.setTextColor(text_color);
        td_text.setGravity(Gravity.CENTER);


        //controllo non sia acrediti
        if(m.getRet().get(index).credits_dist!=null){

            sms_text.setText(String.valueOf(m.getRet().get(index).credits_dist[0]));
            calls_text.setText(String.valueOf(m.getRet().get(index).credits_dist[1]));
            td_text.setText(String.valueOf(m.getRet().get(index).credits_dist[2]));

            calls_text.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
            sms_text.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
            td_text.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));

            calls_text.setPadding(8, 8, 8, 8);
            sms_text.setPadding(8, 8, 8, 8);
            td_text.setPadding(8, 8, 8, 8);

            LinearLayout tab3 = new LinearLayout(this);
            tab3.setOrientation(LinearLayout.HORIZONTAL);

            tab3.addView(sms_text);
            tab3.addView(calls_text);
            tab3.addView(td_text);

            finestra.addView(tab3);

        }else {
            if (m.getRet().get(index).offer.sms == 0) {
                sms_text.setText(String.valueOf(0));
            } else if (m.getRet().get(index).offer.sms == -1) {
                sms_text.setText("Illimitati");
            } else {
                sms_text.setText(String.valueOf(m.getRet().get(index).offer.sms));
            }
            if (m.getRet().get(index).offer.min == 0) {
                calls_text.setText(String.valueOf(0));
            } else if (m.getRet().get(index).offer.min == -1) {
                calls_text.setText("Illimitati");
            } else {
                calls_text.setText(String.valueOf(m.getRet().get(index).offer.min));
            }


            if (m.getRet().get(index).offer.dataTraffic == 0) {
                td_text.setText(String.valueOf(0));
            } else {
                td_text.setText(String.valueOf(m.getRet().get(index).offer.dataTraffic / 1000f));
            }

            calls_text.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
            sms_text.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
            td_text.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));

            calls_text.setPadding(8,8,8,8);
            sms_text.setPadding(8, 8, 8, 8);
            td_text.setPadding(8, 8, 8, 8);

            LinearLayout tab3 = new LinearLayout(this);
            tab3.setOrientation(LinearLayout.HORIZONTAL);

            tab3.addView(sms_text);
            tab3.addView(calls_text);
            tab3.addView(td_text);

            finestra.addView(tab3);

            //Altre info
            if (m.getRet().get(index).offer.sms_vs_same_operator == -1) {
                TextView sms_s = new TextView(this);
                sms_s.setTextSize(12);
                sms_s.setTextColor(text_color);
                sms_s.setGravity(Gravity.CENTER_VERTICAL);
                sms_s.setText("●  Sms illimitati verso " + String.valueOf(m.getRet().get(index).offer.operator));
                sms_s.setPadding(8,8,8,8);
                finestra.addView(sms_s);
            } else if (m.getRet().get(index).offer.sms_vs_same_operator != 0) {
                TextView sms_s = new TextView(this);
                sms_s.setTextSize(12);
                sms_s.setTextColor(text_color);
                sms_s.setGravity(Gravity.CENTER_VERTICAL);
                sms_s.setText("●  " + String.valueOf(m.getRet().get(index).offer.sms_vs_same_operator) + " sms verso " + String.valueOf(m.getRet().get(index).offer.operator));
                sms_s.setPadding(8,8,8,8);
                finestra.addView(sms_s);
            }

            if (m.getRet().get(index).offer.min_vs_same_operator == -1) {
                TextView calls_s = new TextView(this);
                calls_s.setTextSize(12);
                calls_s.setTextColor(text_color);
                calls_s.setGravity(Gravity.CENTER_VERTICAL);
                calls_s.setText("●  Minuti illimitati verso " + String.valueOf(m.getRet().get(index).offer.operator));
                calls_s.setPadding(8,8,8,8);
                finestra.addView(calls_s);
            } else if (m.getRet().get(index).offer.min_vs_same_operator != 0) {
                TextView calls_s = new TextView(this);
                calls_s.setTextSize(12);
                calls_s.setTextColor(text_color);
                calls_s.setGravity(Gravity.CENTER_VERTICAL);
                calls_s.setText("●  " + String.valueOf(m.getRet().get(index).offer.min_vs_same_operator) + " minuti verso " + String.valueOf(m.getRet().get(index).offer.operator));
                calls_s.setPadding(8,8,8,8);
                finestra.addView(calls_s);
            }


            if (m.getRet().get(index).offer.monthBeforeIncreasing != 0) {
                TextView incremento = new TextView(this);
                incremento.setTextSize(12);
                incremento.setTextColor(text_color);
                incremento.setGravity(Gravity.CENTER_VERTICAL);
                incremento.setText("●  Prezzo mensile dopo " + String.valueOf(m.getRet().get(index).offer.monthBeforeIncreasing) + " " + String.valueOf((float) (((float) m.getRet().get(index).offer.price) + (float) m.getRet().get(index).offer.riseOfPriceAfterPeriod) / 100f));
                incremento.setPadding(8,8,8,8);
                finestra.addView(incremento);
            }



        }
        if (m.getRet().get(index).offer.maxAge != 0) {
            TextView eta=new TextView(this);
            eta.setTextSize(12);
            eta.setTextColor(text_color);
            eta.setGravity(Gravity.CENTER_VERTICAL);
            eta.setText("●  Età massima " + String.valueOf(m.getRet().get(index).offer.maxAge));
            eta.setPadding(8,8,8,8);
            finestra.addView(eta);
        }

        if (m.getRet().get(index).offer.youAndMe || YandMnumber!=null) {
            TextView yam=new TextView(this);
            yam.setTextSize(12);
            yam.setTextColor(text_color);
            yam.setGravity(Gravity.CENTER_VERTICAL);
            yam.setText("●  You and me verso " + YandMnumber);
            yam.setPadding(8,8,8,8);
            finestra.addView(yam);
        }

        if (m.getRet().get(index).offer.moreInfos!=null){
            TextView info=new TextView(this);
            info.setTextSize(12);
            info.setTextColor(text_color);
            info.setGravity(Gravity.CENTER_VERTICAL);
            info.setText("●  Informazioni " + m.getRet().get(index).offer.moreInfos);
            info.setPadding(8,8,8,8);
            finestra.addView(info);
        }


    }

    public void setOption(final int index,final int position){

        //+ per ogni opzione
        LinearLayout finestra = (LinearLayout) findViewById(R.id.ff);
        //dimensioni schermo
        Point P = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(P);

        RelativeLayout quad = new RelativeLayout(this);
        quad.setBackgroundColor(text_color);
        quad.setGravity(Gravity.CENTER);

        RelativeLayout quad2 = new RelativeLayout(this);
        quad2.setBackgroundColor(text_color);
        quad2.setGravity(Gravity.CENTER);

        TextView te = new TextView(this);
        te.setText("+");
        te.setTextSize(72);
        te.setTextColor(text_color);
        te.setGravity(Gravity.CENTER);

        LinearLayout tabl = new LinearLayout(this);
        tabl.setOrientation(LinearLayout.HORIZONTAL);

        RelativeLayout.LayoutParams testLP = new RelativeLayout.LayoutParams(P.x / 3, 2);

        testLP.addRule(RelativeLayout.CENTER_IN_PARENT);

        quad.setLayoutParams(testLP);
        te.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.WRAP_CONTENT));
        quad2.setLayoutParams(testLP);

        RelativeLayout quad3 = new RelativeLayout(this);
        quad3.setBackgroundColor(Color.WHITE);

        RelativeLayout quad4 = new RelativeLayout(this);
        quad4.setBackgroundColor(Color.WHITE);

        quad3.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        quad4.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        quad3.addView(quad);
        quad4.addView(quad2);


        tabl.addView(quad3);
        tabl.addView(te);
        tabl.addView(quad4);

        finestra.addView(tabl);

        //opzioni da includere-nome-costo
        TextView text = new TextView(this);
        text.setText(String.valueOf(m.getRet().get(index).options.get(position).nameOptions));
        text.setTextSize(20);
        text.setTextColor(text_color);
        text.setGravity(Gravity.CENTER);

        TextView oper = new TextView(this);
        oper.setText("Opzione da includere");
        oper.setTextSize(14);
        oper.setTextColor(text_color);
        oper.setGravity(Gravity.CENTER);


        TextView cos = new TextView(this);
        cos.setText(approssimato(String.valueOf(m.getRet().get(index).options.get(position).price / 100f)) + "€");
        cos.setTextSize(14);
        cos.setGravity(Gravity.CENTER);

        Typeface t = Typeface.createFromAsset(getAssets(), "fonts/helvetica-neue-bold.ttf");

        cos.setTypeface(t);


        cos.setTextColor(text_color);


        text.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        oper.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        cos.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));

        text.setPadding(8,8,8,8);
        oper.setPadding(8,8,8,8);
        cos.setPadding(8, 8, 8, 8);



        LinearLayout tab= new LinearLayout(this);
        tab.setOrientation(LinearLayout.HORIZONTAL);

        tab.addView(oper);
        tab.addView(text);
        tab.addView(cos);

        tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(m.getRet().get(index).options.get(position).urlOptions));
                startActivity(browserIntent);
            }
        });

        finestra.addView(tab);

        //sms-calls-td
        ImageView calls = new ImageView(this);
        ImageView sms =new ImageView(this);
        ImageView td=new ImageView(this);

        calls.setImageResource(R.drawable.ic_phone_forward_grey600_36dp);
        sms.setImageResource(R.drawable.ic_comment_text_outline_grey600_36dp);
        td.setImageResource(R.drawable.traffic_arrows);

        calls.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        sms.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        td.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));

        calls.setPadding(8, 8, 8, 8);
        sms.setPadding(8, 8, 8, 8);
        td.setPadding(8, 8, 8, 8);

        LinearLayout tab2= new LinearLayout(this);
        tab2.setOrientation(LinearLayout.HORIZONTAL);

        tab2.addView(sms);
        tab2.addView(calls);
        tab2.addView(td);

        finestra.addView(tab2);

        //valori sms calls td
        TextView sms_text=new TextView(this);
        sms_text.setTextSize(14);
        sms_text.setTextColor(text_color);
        sms_text.setGravity(Gravity.CENTER);

        TextView calls_text=new TextView(this);
        calls_text.setTextSize(14);
        calls_text.setTextColor(text_color);
        calls_text.setGravity(Gravity.CENTER);

        TextView td_text=new TextView(this);
        td_text.setTextSize(14);
        td_text.setTextColor(text_color);
        td_text.setGravity(Gravity.CENTER);


        if(m.getRet().get(index).options.get(position).sms==0){
            sms_text.setText(String.valueOf(0));
        }
        else if(m.getRet().get(index).options.get(position).sms==-1){
            sms_text.setText("Illimitati");
        }
        else{
            sms_text.setText(String.valueOf(m.getRet().get(index).options.get(position).sms));
        }



        if(m.getRet().get(index).options.get(position).min==0){
            calls_text.setText(String.valueOf(0));
        }
        else if(m.getRet().get(index).options.get(position).min==-1){
            calls_text.setText("Illimitati");
        }
        else{
            calls_text.setText(String.valueOf(m.getRet().get(index).options.get(position).min));
        }



        if(m.getRet().get(index).options.get(position).dataTraffic==0){
            td_text.setText(String.valueOf(0));
        }
        else{
            td_text.setText(String.valueOf(m.getRet().get(index).options.get(position).dataTraffic/ 1000f));
        }

        calls_text.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        sms_text.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        td_text.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));

        calls_text.setPadding(8, 8, 8, 8);
        sms_text.setPadding(8, 8, 8, 8);
        td_text.setPadding(8, 8, 8, 8);

        LinearLayout tab3=new LinearLayout(this);
        tab3.setOrientation(LinearLayout.HORIZONTAL);

        tab3.addView(sms_text);
        tab3.addView(calls_text);
        tab3.addView(td_text);

        finestra.addView(tab3);

        //altre info
        if(m.getRet().get(index).options.get(position).sms_vs_same_operator==-1){
            TextView sms_s=new TextView(this);
            sms_s.setTextSize(12);
            sms_s.setTextColor(text_color);
            sms_s.setGravity(Gravity.CENTER_VERTICAL);
            sms_s.setText("●  Sms illimitati verso " + String.valueOf(m.getRet().get(index).options.get(position).operator));
            sms_s.setPadding(8,8,8,8);
            finestra.addView(sms_s);
        }
        else if(m.getRet().get(index).options.get(position).sms_vs_same_operator!=0){
            TextView sms_s=new TextView(this);
            sms_s.setTextSize(12);
            sms_s.setTextColor(text_color);
            sms_s.setGravity(Gravity.CENTER_VERTICAL);
            sms_s.setText("●  " + String.valueOf(m.getRet().get(index).options.get(position).sms_vs_same_operator) + " sms verso " + String.valueOf(m.getRet().get(index).options.get(position).operator));
            sms_s.setPadding(8,8,8,8);
            finestra.addView(sms_s);
        }

        if(m.getRet().get(index).options.get(position).min_vs_same_operator==-1){
            TextView calls_s=new TextView(this);
            calls_s.setTextSize(12);
            calls_s.setTextColor(text_color);
            calls_s.setGravity(Gravity.CENTER_VERTICAL);
            calls_s.setText("●  Minuti illimitati verso " + String.valueOf(m.getRet().get(index).options.get(position).operator));
            calls_s.setPadding(8, 8, 8, 8);
            finestra.addView(calls_s);
        }
        else if(m.getRet().get(index).options.get(position).min_vs_same_operator!=0){
            TextView calls_s=new TextView(this);
            calls_s.setTextSize(12);
            calls_s.setTextColor(text_color);
            calls_s.setGravity(Gravity.CENTER_VERTICAL);
            calls_s.setText("●  " + String.valueOf(m.getRet().get(index).options.get(position).min_vs_same_operator) + " minuti verso " + String.valueOf(m.getRet().get(index).options.get(position).operator));
            calls_s.setPadding(8,8,8,8);
            finestra.addView(calls_s);
        }

        if (m.getRet().get(index).options.get(position).maxAge != 0) {
            TextView eta=new TextView(this);
            eta.setTextSize(12);
            eta.setTextColor(text_color);
            eta.setGravity(Gravity.CENTER_VERTICAL);
            eta.setText("●  Età massima " + String.valueOf(m.getRet().get(index).options.get(position).maxAge));
            eta.setPadding(8, 8, 8, 8);
            finestra.addView(eta);
        }

        if (m.getRet().get(index).options.get(position).youAndMe || YandMnumber!=null) {
            TextView yam=new TextView(this);
            yam.setTextSize(12);
            yam.setTextColor(text_color);
            yam.setGravity(Gravity.CENTER_VERTICAL);
            yam.setText("●  You and me verso " + YandMnumber);
            yam.setPadding(8, 8, 8, 8);
            finestra.addView(yam);
        }

        if (m.getRet().get(index).options.get(position).moreInfos!=null){
            TextView info=new TextView(this);
            info.setTextSize(12);
            info.setTextColor(text_color);
            info.setGravity(Gravity.CENTER_VERTICAL);
            info.setText("●  Informazioni " + m.getRet().get(index).options.get(position).moreInfos);
            info.setPadding(8, 8, 8, 8);
            finestra.addView(info);
        }

    }

    public void setRate(final int index, float s){

        //piu tariffa in caso sfori
        LinearLayout finestra = (LinearLayout) findViewById(R.id.ff);
        //dimensioni schermo
        Point P = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(P);

        RelativeLayout quad = new RelativeLayout(this);
        quad.setBackgroundColor(text_color);
        quad.setGravity(Gravity.CENTER);

        RelativeLayout quad2 = new RelativeLayout(this);
        quad2.setBackgroundColor(text_color);
        quad2.setGravity(Gravity.CENTER);

        TextView te = new TextView(this);
        te.setText("+");
        te.setTextSize(72);
        te.setTextColor(text_color);
        te.setGravity(Gravity.CENTER);

        LinearLayout tabl = new LinearLayout(this);
        tabl.setOrientation(LinearLayout.HORIZONTAL);

        RelativeLayout.LayoutParams testLP = new RelativeLayout.LayoutParams(P.x / 3, 2);

        testLP.addRule(RelativeLayout.CENTER_IN_PARENT);

        quad.setLayoutParams(testLP);
        te.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.WRAP_CONTENT));
        quad2.setLayoutParams(testLP);

        RelativeLayout quad3 = new RelativeLayout(this);
        quad3.setBackgroundColor(Color.WHITE);

        RelativeLayout quad4 = new RelativeLayout(this);
        quad4.setBackgroundColor(Color.WHITE);

        quad3.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        quad4.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        quad3.addView(quad);
        quad4.addView(quad2);

        tabl.addView(quad3);
        tabl.addView(te);
        tabl.addView(quad4);

        finestra.addView(tabl);

        //tariffa-nome-sforo
        TextView cos = new TextView(this);
        cos.setText(approssimato(String.valueOf(s)) + "€");
        cos.setTextSize(14);
        cos.setTextColor(text_color);
        cos.setGravity(Gravity.CENTER);

        TextView text = new TextView(this);
        text.setText(String.valueOf(m.getRet().get(index).rate.rateName));
        text.setTextSize(20);
        text.setTextColor(text_color);
        text.setGravity(Gravity.CENTER);

        TextView oper = new TextView(this);
        oper.setText("Tariffa da utilizzare");
        oper.setTextSize(14);
        oper.setTextColor(text_color);
        oper.setGravity(Gravity.CENTER);

        Typeface t = Typeface.createFromAsset(getAssets(), "fonts/helvetica-neue-bold.ttf");

        cos.setTypeface(t);

        text.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        oper.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        cos.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));

        text.setPadding(8, 8, 8, 8);
        oper.setPadding(8, 8, 8, 8);
        cos.setPadding(8, 8, 8, 8);

        LinearLayout tab= new LinearLayout(this);
        tab.setOrientation(LinearLayout.HORIZONTAL);

        tab.addView(oper);
        tab.addView(text);
        tab.addView(cos);

        tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(m.getRet().get(index).rate.urlRate));
                startActivity(browserIntent);
            }
        });

        finestra.addView(tab);

        //immagini solite
        ImageView calls = new ImageView(this);
        ImageView sms =new ImageView(this);
        ImageView td=new ImageView(this);

        calls.setImageResource(R.drawable.ic_phone_forward_grey600_36dp);
        sms.setImageResource(R.drawable.ic_comment_text_outline_grey600_36dp);
        td.setImageResource(R.drawable.traffic_arrows);

        calls.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        sms.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        td.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));

        calls.setPadding(8, 8, 8, 8);
        sms.setPadding(8, 8, 8, 8);
        td.setPadding(8, 8, 8, 8);

        LinearLayout tab2= new LinearLayout(this);
        tab2.setOrientation(LinearLayout.HORIZONTAL);

        tab2.addView(sms);
        tab2.addView(calls);
        tab2.addView(td);

        finestra.addView(tab2);

        //valori
        TextView  costo_sms= new TextView(this);
        costo_sms.setTextSize(14);
        costo_sms.setTextColor(text_color);
        costo_sms.setGravity(Gravity.CENTER);

        TextView costo_calls = new TextView(this);
        costo_calls.setTextSize(14);
        costo_calls.setTextColor(text_color);
        costo_calls.setGravity(Gravity.CENTER);

        TextView costo_td = new TextView(this);
        costo_td.setTextSize(14);
        costo_td.setTextColor(text_color);
        costo_td.setGravity(Gravity.CENTER);

        costo_sms.setText(String.valueOf(m.getRet().get(index).rate.sms_cost / 1000f) + " € a sms");

        float min_cost=Float.valueOf(m.getRet().get(index).rate.callsPrice_for_size);
        min_cost=(min_cost/1000f)*(float)(60/m.getRet().get(index).rate.sizeCalls);
        String a=approssimato(String.valueOf(min_cost));
        costo_calls.setText(a + " € al minuto");

        float traffico=Float.valueOf(m.getRet().get(index).rate.priceDataTraffic);
        traffico=traffico/1000f;
        costo_td.setText(String.valueOf(traffico) + " € ogni " + m.getRet().get(index).rate.sizeDataTraffic + " mb");

        costo_calls.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        costo_sms.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));
        costo_td.setLayoutParams(new ViewGroup.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));

        costo_calls.setPadding(8, 8, 8, 8);
        costo_sms.setPadding(8, 8, 8, 8);
        costo_td.setPadding(8, 8, 8, 8);


        LinearLayout tab3= new LinearLayout(this);
        tab3.setOrientation(LinearLayout.HORIZONTAL);

        tab3.addView(costo_sms);
        tab3.addView(costo_calls);
        tab3.addView(costo_td);

        finestra.addView(tab3);
    }

    String approssimato(String a){
        int k=0;
        for(int i=0; i<a.length(); i++){
            if(a.charAt(i)=='.') {
                k=i+2;
                break;
            }
        }
        if(k>=a.length()){
            return a;
        }
        else {
            return a.substring(0, k + 1);
        }

    }

  
}
