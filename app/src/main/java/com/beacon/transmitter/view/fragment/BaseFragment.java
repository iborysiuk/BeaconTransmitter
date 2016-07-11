package com.beacon.transmitter.view.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beacon.transmitter.R;
import com.beacon.transmitter.view.activity.BaseActivity;

import butterknife.ButterKnife;

/**
 * Created by Yuriy on 2016-07-09 Ibeacon.
 */

public abstract class BaseFragment extends Fragment {

    private CoordinatorLayout mRootLayout;
    private ProgressDialog mProgressDialog;
    private Toolbar mToolbar;

    public static final String ACTION_BACK_PRESSED = "ACTION_BACK_PRESSED";
    public static final String ACTION_NETWORK_STATUS = "ACTION_NETWORK_STATUS";

    protected LocalBroadcastManager mManager;
    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (!TextUtils.isEmpty(intentAction))
                if (intent.getAction().equals(ACTION_BACK_PRESSED)) {
                    onReceiveBackPressed();
                } else if (intentAction.equals(ACTION_NETWORK_STATUS)) {
                    boolean status = intent.getExtras().getBoolean("status");
                    if (!status) showMessage(getString(R.string.error_no_internet_connection));
                }
        }
    };

    protected abstract
    @LayoutRes
    int getLayout();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager = LocalBroadcastManager.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        if (view != null) {
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            setToolbar(view);
            setProgressDialog();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_BACK_PRESSED);
        filter.addAction(ACTION_NETWORK_STATUS);
        mManager.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        mManager.unregisterReceiver(mReceiver);
        super.onPause();
    }

    protected void setToolbar(View view) {
        if (!hasCustomToolbar()) return;
        mRootLayout = ButterKnife.findById(view, getRootViewId());
        mToolbar = ButterKnife.findById(view, getToolbarId());
        mToolbar.setTitle(getTitle());
        mToolbar.inflateMenu(getMenu());
        if (hasCustomBackArrow()) {
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            mToolbar.setNavigationOnClickListener(view1 -> BaseFragment.this.onReceiveBackPressed());
        }
    }

    protected void onReceiveBackPressed() {
    }

    public boolean isFragmentNavigatorNotNull() {
        return BaseActivity.mNavigator != null;
    }

    public boolean hasCustomToolbar() {
        return false;
    }

    protected
    @IdRes
    int getToolbarId() {
        return R.id.toolbar;
    }

    protected
    @IdRes
    int getRootViewId() {
        return R.id.rootLayout;
    }

    protected
    @StringRes
    int getTitle() {
        return R.string.not_title_set;
    }

    protected
    @MenuRes
    int getMenu() {
        return R.menu.empty_menu;
    }

    protected boolean hasCustomBackArrow() {
        return false;
    }

    public Activity getMainActivity() {
        return (getActivity() != null) ? (BaseActivity) getActivity() : null;
    }

    public void showMessage(String message) {
        //noinspection ResourceType
        if (mRootLayout != null) {
            Snackbar snackbar = Snackbar.make(mRootLayout, message, Snackbar.LENGTH_SHORT)
                    .setDuration(1000);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    private ProgressDialog setProgressDialog() {
        mProgressDialog = new ProgressDialog(getMainActivity());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_msg));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        return mProgressDialog;
    }

    public void showDialog() {
        if (mProgressDialog != null) mProgressDialog.show();
    }

    public void hideDialog() {
        if (mProgressDialog != null) mProgressDialog.hide();
    }

}
