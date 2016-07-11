package com.beacon.transmitter.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.beacon.transmitter.R;
import com.beacon.transmitter.controllers.util.Navigator;
import com.beacon.transmitter.view.fragment.BaseFragment;

import butterknife.ButterKnife;

/**
 * Created by Yuriy on 2016-07-09 Ibeacon.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public static Navigator mNavigator;
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);

        setupToolbar();
        initNavigator();

    }

    private void initNavigator() {
        mNavigator = new Navigator(getSupportFragmentManager(), R.id.container);
    }

    private void setupToolbar() {
        mToolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    public void showActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.show();
    }

    public void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.hide();
    }

    public void getFragment(BaseFragment fragment) {
        if (fragment.hasCustomToolbar()) {
            hideActionBar();
        } else {
            showActionBar();
        }
        mNavigator.setRootFragment(fragment, false);
    }

    @Override
    public void finish() {
        mNavigator = null;
        super.finish();
    }

    protected abstract
    @LayoutRes
    int getLayout();


    @Override
    public void onBackPressed() {
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(this);
        mgr.sendBroadcast(new Intent(BaseFragment.ACTION_BACK_PRESSED));
    }
}
