package com.beacon.transmitter.view.activity;

import android.os.Bundle;

import com.beacon.transmitter.R;
import com.beacon.transmitter.view.fragment.LoginFragment;


public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
            getFragment(new LoginFragment());
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_fragment_container;
    }

}
