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

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cheaphone.core.R;

/**
 * Created by giacomo on 19/02/2015.
 */
public class Opzioni extends ActionBarActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opzioni);
        toolbar = (Toolbar) findViewById(R.id.tr);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        int value=0;
        if (extras != null) {
           value = extras.getInt("new_variable_name");
        }

        if(value==0){
            // Create new fragment and transaction
            Fragment newFragment = new Op();
            // consider using Java coding conventions (upper first char class names!!!)
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.cont, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
        else if(value==1){
            // Create new fragment and transaction
            Fragment newFragment = new Inf();
            // consider using Java coding conventions (upper first char class names!!!)
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.cont, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();

        }
        else if(value==2){
            // Create new fragment and transaction
            Fragment newFragment = new Menu();
            // consider using Java coding conventions (upper first char class names!!!)
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.cont, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }
    public class Op extends Fragment {

        public Op() {

        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup contiene,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.op, contiene, false);
            return rootView;
        }

    }

    public class Inf extends Fragment {

        public Inf() {

        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup contiene,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.inf, contiene, false);
            return rootView;
        }
    }

    public class Menu extends Fragment {

        public Menu() {

        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup contiene,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.menu, contiene, false);
            return rootView;
        }
    }
}
