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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cheaphone.core.Option;
import cheaphone.core.R;
import cheaphone.core.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 12/21/2015.
 *
 * Modified by Tomas Bortoli
 */
public class Stampa extends Fragment {


    public int indice;//indice che indica la pozione dell'offerta selezionata

    private ArrayList<Result> ret=new ArrayList<Result>();

    private String YandMnumber;
    private float sforo;
    int size;

    private RecyclerView mRecyclerView=null;
    private CustomRecyclerAdapter mAdapter=null;
    private RecyclerView.LayoutManager mLayoutManager=null;

    MainActivity m;

    int text_color= Color.rgb(74, 134, 232);

    public static String err_msg="";

    public static Stampa current;

    private List<Option> data_o= new ArrayList<>();
    private List<Data> mData=new ArrayList<Data>();

    RecyclerItemClickListener.OnItemClickListener onClickListener=new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            // do whatever
            indice = position + 1;
            Intent offerta = new Intent(getActivity(), Offerta.class);
            offerta.putExtra("pos", indice);
            startActivity(offerta);
        }
    };
    RecyclerItemClickListener recyclerClickListener=null;

    View.OnClickListener xxx=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            indice = 0;
            Intent offerta = new Intent(getActivity(), Offerta.class);
            offerta.putExtra("pos", indice);
            startActivity(offerta);
        }
    };

    public Stampa(){

        current=this;

    }

    public LayoutInflater inflater;
    public ViewGroup container;


    TextView text=null,cos = null;
    LinearLayout oper;
    ImageView image;
    LinearLayout tab = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        while(MainActivity.is_computing)
            ;

        View rootView = inflater.inflate(R.layout.stampa, container, false);
        this.inflater=inflater; this.container=container;
        //mData.removeAll(mData);
        mData.clear();

        //ret.removeAll(ret);
        ret.clear();

        m = (MainActivity) getActivity();

        RelativeLayout logo = (RelativeLayout) rootView.findViewById(R.id.logo);
        RelativeLayout of = (RelativeLayout) rootView.findViewById(R.id.offerta);
        RelativeLayout pre = (RelativeLayout) rootView.findViewById(R.id.prezzo);

        if(!err_msg.equals("")){

            if(text==null) {
                text = new TextView(getActivity());
                of.addView(text);
            }
            text.setText(err_msg);
            text.setTextSize(20);
            text.setTextColor(text_color);
            text.setGravity(Gravity.CENTER);



            m.resetRet();
        }



        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        // If the size of views will not change as the data changes.
        mRecyclerView.setHasFixedSize(true);

        // Setting the LayoutManager.
        if(mLayoutManager==null) {
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
        }

        for (int h = 0; h < m.getRet().size(); h++)
            ret.add(m.getRet().get(h));

        YandMnumber = m.getYu();

        size = m.getRet().size();

        for (int j = 1; j < size; j++)
            addItem(ret, j);

        //stampo la prima
        if (text == null) {
            text = new TextView(getActivity());
            of.addView(text);
        }
        if (ret.size() > 0) text.setText(String.valueOf(ret.get(0).offer.offerName));
        text.setTextSize(20);
        text.setTextColor(text_color);
        text.setGravity(Gravity.CENTER);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        if(mAdapter==null) {
            mAdapter = new CustomRecyclerAdapter(m.getApplicationContext(), getActivity());
            mRecyclerView.setAdapter(mAdapter);
        }

        if (oper == null) {
            oper = new LinearLayout(getActivity());
            logo.addView(oper);
        }
        if (image == null) {
            image = new ImageView(getActivity());
            oper.addView(image);
        }

        if (ret.size() > 0) {
            if (String.valueOf(ret.get(0).offer.operator).equals("tre")) {
                image.setImageResource(R.drawable.tre);

            }
            if (String.valueOf(ret.get(0).offer.operator).equals("postemobile")) {
                image.setImageResource(R.drawable.postemobile);

            }
            if (String.valueOf(ret.get(0).offer.operator).equals("wind")) {
                image.setImageResource(R.drawable.wind);

            }
            if (String.valueOf(ret.get(0).offer.operator).equals("vodafone")) {
                image.setImageResource(R.drawable.vodafone);

            }
            if (String.valueOf(ret.get(0).offer.operator).equals("fastweb")) {
                image.setImageResource(R.drawable.fastweb);

            }
            if (String.valueOf(ret.get(0).offer.operator).equals("coopvoce")) {
                image.setImageResource(R.drawable.coopvoce);

            }
            if (String.valueOf(ret.get(0).offer.operator).equals("tim")) {
                image.setImageResource(R.drawable.tim);

            }
        }
        oper.setGravity(Gravity.CENTER);


        if (cos == null) {
            cos = new TextView(getActivity());
            pre.addView(cos);
        }
        if (!err_msg.equals(""))
            cos.setVisibility(View.GONE);
        else
            cos.setVisibility(View.VISIBLE);
        if (ret.size() > 0)
            cos.setText(approssimato(String.valueOf(ret.get(0).cost / 1000f)) + "€");
        cos.setTextSize(30);
        cos.setGravity(Gravity.CENTER);

        Typeface t = Typeface.createFromAsset(m.getAssets(), "fonts/helvetica-neue-bold.ttf");

        cos.setTypeface(t);

        //dimensioni schermo
        Point P = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(P);
        cos.setTextColor(text_color);

        if (err_msg.equals("")) {
            text.setLayoutParams(new RelativeLayout.LayoutParams(P.x / 3, RelativeLayout.LayoutParams.MATCH_PARENT));
            oper.setLayoutParams(new RelativeLayout.LayoutParams(P.x / 3, RelativeLayout.LayoutParams.MATCH_PARENT));
            cos.setLayoutParams(new RelativeLayout.LayoutParams(P.x / 3, RelativeLayout.LayoutParams.MATCH_PARENT));
        } else
            text.setLayoutParams(new RelativeLayout.LayoutParams(P.x, RelativeLayout.LayoutParams.MATCH_PARENT));




        // If the size of views will not change as the data changes.
        mRecyclerView.setHasFixedSize(true);


        mLayoutManager.scrollToPosition(indice - 4);

        for (int k = 0; k < mAdapter.getItemCount(); k++)
            mAdapter.removeItem(k--);
        int x=mAdapter.getItemCount();

        for (int k = 0; k < size - 1; k++)
            mAdapter.addItem(k, (Data)mData.get(k).clone());


        if (tab == null) {
            tab = (LinearLayout) rootView.findViewById(R.id.ling);

            if(recyclerClickListener==null)
                recyclerClickListener=new RecyclerItemClickListener(m.getApplicationContext(), onClickListener);
            mRecyclerView.addOnItemTouchListener(
                    recyclerClickListener
            );
        }

        if(!err_msg.equals("")){
            tab.setOnClickListener(null);
            //mRecyclerView.removeOnItemTouchListener(recyclerClickListener);
        }
        else{
            tab.setOnClickListener(xxx);
        }


        return rootView;
    }

    public void addItem(ArrayList<Result> r, int i) {

        // Add data locally to the list.
        int image=0;

        if (String.valueOf(ret.get(i).offer.operator).equals("tre")) {
            image=1;

        }
        if (String.valueOf(ret.get(i).offer.operator).equals("postemobile")) {
            image=2;

        }
        if (String.valueOf(ret.get(i).offer.operator).equals("wind")) {
            image=3;

        }
        if (String.valueOf(ret.get(i).offer.operator).equals("vodafone")) {
            image=4;

        }
        if (String.valueOf(ret.get(i).offer.operator).equals("fastweb")) {
            //image.setImageResource(R.drawable.fastweb);
            image=5;
        }
        if (String.valueOf(ret.get(i).offer.operator).equals("coopvoce")) {
            image=6;

        }
        if (String.valueOf(ret.get(i).offer.operator).equals("tim")) {
            image=7;

        }

        Data dataToAdd = new Data(r.get(i).offer.offerName,r.get(i).cost,image);
        mData.add(dataToAdd);


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
