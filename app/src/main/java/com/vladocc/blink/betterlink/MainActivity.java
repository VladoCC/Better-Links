package com.vladocc.blink.betterlink;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;

import com.vladocc.blink.betterlink.SQLite.LinkDbHelper;
import com.vladocc.blink.betterlink.fragments.AddFragment;
import com.vladocc.blink.betterlink.fragments.ScanFragment;
import com.vladocc.blink.betterlink.fragments.ShowFragment;

public class MainActivity extends AppCompatActivity {

    ShowFragment fragment1;
    AddFragment fragment2;
    ScanFragment fragment3;

    public static final int showFragment = R.id.navigation_show;
    public static final int addFragment = R.id.navigation_add;
    public static final int scanFragment = R.id.navigation_scan;

    int itemId = R.id.navigation_add;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case showFragment:
                    setBrightness(1);
                    itemId = showFragment;
                    fragmentTransaction.replace(R.id.content, fragment1).commit();
                    return true;
                case addFragment:
                    setBrightness(-1);
                    itemId = addFragment;
                    fragmentTransaction.replace(R.id.content, fragment2).commit();
                    return true;
                case scanFragment:
                    setBrightness(-1);
                    itemId = scanFragment;
                    fragmentTransaction.replace(R.id.content, fragment3).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinkDbHelper.createInstance(this);

        fragment1 = ShowFragment.newInstance();
        fragment2 = AddFragment.newInstance();
        fragment3 = ScanFragment.newInstance();

        if (savedInstanceState != null) {
            itemId = savedInstanceState.getInt("itemId");
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (savedInstanceState == null) {
            navigation.setSelectedItemId(itemId);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("itemId", itemId);
    }

    public void setBrightness(float brightness){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightness;
        getWindow().setAttributes(lp);
    }
}
