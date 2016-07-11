package com.beacon.transmitter.controllers.util;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by Yuriy on 2016-07-09 Ibeacon.
 */

public class Navigator {

    @NonNull
    protected final FragmentManager mFragmentManager;

    @IdRes
    protected final int mDefaultContainer;


    public Navigator(@NonNull final FragmentManager fragmentManager, @IdRes final int defaultContainer) {
        mFragmentManager = fragmentManager;
        mDefaultContainer = defaultContainer;
    }


    public void goTo(final Fragment fragment) {
        mFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(mDefaultContainer, fragment, getName(fragment))
                .commit();
        mFragmentManager.executePendingTransactions();
    }

    protected String getName(final Fragment fragment) {
        return fragment.getClass().getSimpleName();
    }

    public void setRootFragment(final Fragment startFragment, boolean isAnimated) {
        if (getSize() > 0) {
            this.clearHistory();
        }
        if (!isAnimated) this.replaceWithOutAnimationFragment(startFragment);
        else this.replaceWithAnimationFragment(startFragment);
    }

    private void replaceWithOutAnimationFragment(final Fragment fragment) {
        mFragmentManager.beginTransaction()
                .replace(mDefaultContainer, fragment, getName(fragment))
                .commit();
        mFragmentManager.executePendingTransactions();
    }

    private void replaceWithAnimationFragment(final Fragment fragment) {
        mFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(mDefaultContainer, fragment, getName(fragment))
                .commit();
        mFragmentManager.executePendingTransactions();
    }

    public void removeFragment(final Fragment fragment) {
        mFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .remove(fragment)
                .commit();
        mFragmentManager.popBackStack();
    }

    public void goOneBack() {
        mFragmentManager.popBackStackImmediate();
    }

    public void goBack() {
        mFragmentManager.popBackStack();
    }


    public int getSize() {
        return mFragmentManager.getBackStackEntryCount();
    }

    public void clearHistory() {
        //noinspection StatementWithEmptyBody - it works as wanted
        while (mFragmentManager.popBackStackImmediate()) ;
    }

}
