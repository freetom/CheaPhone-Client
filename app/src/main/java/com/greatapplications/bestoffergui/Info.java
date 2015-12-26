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

/**
 * Created by giacomo on 11/12/2014. Modified by Tomas
 */
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greatapplications.bestoffer.Constants;
import com.greatapplications.bestoffer.R;
import com.greatapplications.bestoffer.Serialize;
import com.greatapplications.bestoffer.Utility;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.util.Calendar;
import java.util.Random;


//Informazioni sull'app
public class Info extends Fragment {

    private ViewGroup mContainerView;
    private ViewGroup mContainerView2;
    private ViewGroup mContainerView3;
    private boolean check=true;
    private boolean check2=true;
    private boolean check3=true;
    private ViewGroup newView;
    private ViewGroup newView2;
    private ViewGroup newView3;

    public Info(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.info, container, false);

        mContainerView = (ViewGroup) rootView.findViewById(R.id.container);
        mContainerView2 = (ViewGroup) rootView.findViewById(R.id.container2);
        mContainerView3 = (ViewGroup) rootView.findViewById(R.id.container3);

        RelativeLayout uno=(RelativeLayout) rootView.findViewById(R.id.r1);
       RelativeLayout due=(RelativeLayout) rootView.findViewById(R.id.r2);
        RelativeLayout tre=(RelativeLayout) rootView.findViewById(R.id.r3);


        uno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(check)
                        addItem();
                    else
                        mContainerView.removeView(newView);

                    check=!check;

            }
        });

        tre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check3)
                    addItem3();
                else
                    mContainerView3.removeView(newView3);

                    check3 = !check3;

            }
        });

        due.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check2)
                    addItem2();
                else
                    mContainerView2.removeView(newView2);

                check2 = !check2;

            }
        });





        return rootView;

    }

    private void addItem() {
        // Instantiate a new "row" view.
        newView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(
                R.layout.item_example, mContainerView, false);


        // Set a click listener for the "X" button in the row that will remove the row.
        newView.findViewById(R.id.item1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove the row from its parent (the container view).
                // Because mContainerView has android:animateLayoutChanges set to true,
                // this removal is automatically animated.
                mContainerView.removeView(newView);
                check=true;

            }
        });

        // Because mContainerView has android:animateLayoutChanges set to true,
        // adding this view is automatically animated.
        mContainerView.addView(newView, 0);

    }

    private void addItem2() {
        // Instantiate a new "row" view.
        newView2 = (ViewGroup) LayoutInflater.from(getActivity()).inflate(
                R.layout.item_example2, mContainerView2, false);



        // Set a click listener for the "X" button in the row that will remove the row.
        newView2.findViewById(R.id.item2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove the row from its parent (the container view).
                // Because mContainerView has android:animateLayoutChanges set to true,
                // this removal is automatically animated.
                mContainerView2.removeView(newView2);
                check2=true;

            }
        });

        // Because mContainerView has android:animateLayoutChanges set to true,
        // adding this view is automatically animated.
        mContainerView2.addView(newView2, 0);

    }

    private void addItem3() {
        // Instantiate a new "row" view.
        newView3 = (ViewGroup) LayoutInflater.from(getActivity()).inflate(
                R.layout.item_example3, mContainerView3, false);





        // Set a click listener for the "X" button in the row that will remove the row.
        newView3.findViewById(R.id.item3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove the row from its parent (the container view).
                // Because mContainerView has android:animateLayoutChanges set to true,
                // this removal is automatically animated.
                mContainerView3.removeView(newView3);
                check3=true;

            }
        });

        // Because mContainerView has android:animateLayoutChanges set to true,
        // adding this view is automatically animated.
        mContainerView3.addView(newView3, 0);

    }
}