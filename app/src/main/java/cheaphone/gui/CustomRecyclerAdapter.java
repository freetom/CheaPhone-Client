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
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cheaphone.core.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giacomo on 02/03/2015.
 */
public class CustomRecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder>
{
    private List<Data> mData = new ArrayList<>();
    private Context mCtx;
    private Activity activity;

    public CustomRecyclerAdapter(Context context, Activity activity) {
        // Pass context or other static stuff that will be needed.
        mCtx=context;
        this.activity=activity;
    }

    //public void updateList(List<Data> data) {
    //    mData = data;
    //    notifyDataSetChanged();
    //}
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.list_item, viewGroup, false);

        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder viewHolder, int position) {
        Typeface type = Typeface.createFromAsset(mCtx.getAssets(), "fonts/helvetica-neue-bold.ttf");

        Point P = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(P);
        viewHolder.title.setLayoutParams(new LinearLayout.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout.LayoutParams lay=new LinearLayout.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT);
        lay.weight=32; lay.height=32;
        viewHolder.icon.setLayoutParams(lay);

        viewHolder.price.setLayoutParams(new LinearLayout.LayoutParams(P.x / 3, ViewGroup.LayoutParams.MATCH_PARENT));


        viewHolder.title.setTypeface(type);
        viewHolder.price.setTypeface(type);

        viewHolder.title.setText(mData.get(position).text);
        viewHolder.price.setText(approssimato(String.valueOf(mData.get(position).prezzo/1000f))+" €");

        if (mData.get(position).img==1) {
            viewHolder.icon.setImageResource(R.drawable.tre);

        }
        if (mData.get(position).img==2) {
            viewHolder.icon.setImageResource(R.drawable.postemobile);

        }
        if (mData.get(position).img==3) {
            viewHolder.icon.setImageResource(R.drawable.wind);

        }
        if (mData.get(position).img==4) {
            viewHolder.icon.setImageResource(R.drawable.vodafone);

        }
        if (mData.get(position).img==5) {
            viewHolder.icon.setImageResource(R.drawable.fastweb);

        }
        if (mData.get(position).img==6) {
            viewHolder.icon.setImageResource(R.drawable.coopvoce);

        }
        if (mData.get(position).img==7) {
            viewHolder.icon.setImageResource(R.drawable.tim);

        }
    }

    public void addItem(int position, Data data) {
        mData.add(position, data);
        notifyItemInserted(position);
    }

    String approssimato(String a) {
        int k = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) == '.') {
                k = i + 2;
                break;
            }
        }
        if (k >= a.length()) {
            return a;
        }
        else {
            return a.substring(0, k + 1);
        }

    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }



}
