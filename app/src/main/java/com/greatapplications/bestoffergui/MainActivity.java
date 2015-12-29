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


import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ActionMenuView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.greatapplications.bestoffer.ComputeBestOffer;
import com.greatapplications.bestoffer.Constants;
import com.greatapplications.bestoffer.MainService;
import com.greatapplications.bestoffer.Pair;
import com.greatapplications.bestoffer.R;
import com.greatapplications.bestoffer.Result;
import com.greatapplications.bestoffer.Serialize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

//Made by Giacomo Todesco
//Modified to correct, cleanup, try to make it working by Bortoli Tomas

public class MainActivity extends ActionBarActivity {

    RelativeLayout h,i,p;
    private Toolbar toolbar;
    Point P;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    public boolean check=true;

    int selected=Color.rgb(162, 190, 238);
    int text_color=Color.rgb(230, 255, 255);
    int an_selected=Color.rgb(74, 134, 232);


    public static MainActivity current;
    public volatile static Calendar last_time;
    public volatile static boolean is_computing=false;

    public static String YandMnumber;
    public static ArrayList<Result> ret;

    public static MainActivity activity;


    SectionsPagerAdapter mSectionsPagerAdapter;

    /*
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
        current=this;

        updateOffers();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity=this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Calendar c= (Calendar) Serialize.loadSerializedObject(new File(Constants.applicationFilesPath + Constants.installationDate));
        if(c==null)
            c= (Calendar) Serialize.loadSerializedObject(new File(Constants.applicationFilesPath+Constants.installationDate2));
        if(c==null)
            c=Calendar.getInstance();

         if(ret==null)
            ret = new ArrayList<Result>();

		//startService(new Intent(this,MainService.class));

        toolbar.setNavigationIcon(null);


		/*try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/





        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //dimensioni schermo
        P=new Point();
        this.getWindowManager().getDefaultDisplay().getSize(P);

        //font
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/helvetica-neue-bold.ttf");

        //layout home
        h=(RelativeLayout) findViewById(R.id.home);
        TextView home=new TextView(this);
        home.setText("Home");
        home.setTypeface(type);
        home.setTextColor(text_color);
        home.setTextSize(18);
        home.setGravity(Gravity.CENTER);
        home.setLayoutParams(new LayoutParams(P.x/3, LayoutParams.MATCH_PARENT));
        //h.setBackgroundColor(Color.rgb(81, 94, 145));
        h.setBackgroundColor(selected);
        h.addView(home);

        //layout info
        i=(RelativeLayout) findViewById(R.id.info);
        TextView info=new TextView(this);
        info.setLayoutParams(new LayoutParams(P.x/3, LayoutParams.MATCH_PARENT));
        info.setGravity(Gravity.CENTER);
        info.setText("Offerte");
        info.setTypeface(type);
        info.setTextColor(text_color);
        info.setTextSize(18);
        i.addView(info);

        //layout privacy
        p=(RelativeLayout) findViewById(R.id.privacy);
        TextView privacy=new TextView(this);
        privacy.setLayoutParams(new LayoutParams(P.x / 3, LayoutParams.MATCH_PARENT));
        privacy.setText("Info");
        privacy.setTypeface(type);
        privacy.setGravity(Gravity.CENTER);
        privacy.setTextColor(text_color);
        privacy.setTextSize(18);
        p.addView(privacy);

        //listener change adapter
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Calendar nearNow = Calendar.getInstance();
                nearNow.add(Calendar.MILLISECOND, -5000);
                if (MainActivity.last_time != null && nearNow.after(MainActivity.last_time)) {
                    MainActivity.current.updateOffers();
                }

                if (position == 0) {
                    h.setBackgroundColor(selected);
                    p.setBackgroundColor(an_selected);
                    i.setBackgroundColor(an_selected);
                }
                if (position == 1) {
                    try {
                        Stampa.current.onCreateView(Stampa.current.inflater, Stampa.current.container, null);
                    }
                    catch(Exception e){}
                    i.setBackgroundColor(selected);
                    h.setBackgroundColor(an_selected);
                    p.setBackgroundColor(an_selected);
                }

                if (position == 2) {
                    p.setBackgroundColor(selected);
                    h.setBackgroundColor(an_selected);
                    i.setBackgroundColor(an_selected);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //listener for change the tab
        p.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    p.setBackgroundColor(selected);
                    h.setBackgroundColor(an_selected);
                    i.setBackgroundColor(selected);
                    mViewPager.setCurrentItem(2);
                }



                return true;
            }
        });

        h.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    h.setBackgroundColor(selected);
                    p.setBackgroundColor(an_selected);
                    i.setBackgroundColor(an_selected);
                    mViewPager.setCurrentItem(0);
                }

                return true;
            }
        });



        i.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    i.setBackgroundColor(selected);
                    h.setBackgroundColor(an_selected);
                    p.setBackgroundColor(an_selected);
                    mViewPager.setCurrentItem(1);
                }

                return true;
            }
        });
		
		while(is_computing)
            ;
		
	}

    public static MainActivity getInstance() {
        return activity;
    }

    public ArrayList<Result> getRet(){
        return ret;
    }
    public String getYu(){
        return YandMnumber;
    }

    public void updateOffers() {
        is_computing=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    last_time=Calendar.getInstance();

                    while(!MainService.ready) {
                        ;
                    }

                    try {
                        Pair<ArrayList<Result>,String> res=ComputeBestOffer.findBestOffer();
                        if(res!=null) {
                            ret = res.getFirst();
                            YandMnumber = res.getSecond();

                            if(ret.size()==1)
                                Stampa.err_msg="Data e ora sul telefono sbagliate, correggile per un funzionamento corretto";
                            else if(ret.size()==2)
                                Stampa.err_msg="Errore nell'analisi del file offerte. Probabilmente colpa del nostro operatore, ci scusiamo per il disagio";
                            else
                                Stampa.err_msg="";
                        }
                        else {
                            ret = null;
                            Stampa.err_msg="File delle offerte non disponibile in locale, riceverai una notifica a download avvenuto.";
                        }
                    } catch (IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "errore", Toast.LENGTH_SHORT).show();
                            }
                        });
                        e.printStackTrace();
                    }

                    //create the file that show that the first calculus is happened
                    //Utility.CreateEmptyFile(Constants.applicationFilesPath + Constants.isFirstCalculus);


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
                is_computing=false;

            }

        }).start();
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        int h=position;
        Intent i = new Intent(getApplicationContext(), Opzioni.class);
        i.putExtra("new_variable_name",h);
        startActivity(i);

        mDrawerList.setItemChecked(position, false);
        //setTitle(mItem[position]);
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    public void resetRet(){
        ret=new ArrayList<Result>();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }



        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            Fragment fragment = new Fragment();
            switch (position) {
                case 0:
                    return fragment = new Home();

                case 1:
                    return fragment = new Stampa();

                case 2:
                    return fragment = new Info();

                default:
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }


    }

    protected void setActionBarIcon(int iconRes) {
        toolbar.setNavigationIcon(iconRes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            // do something on back.
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
