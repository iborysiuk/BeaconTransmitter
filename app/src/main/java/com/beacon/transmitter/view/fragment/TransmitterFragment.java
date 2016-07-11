package com.beacon.transmitter.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.beacon.transmitter.MyApp;
import com.beacon.transmitter.R;
import com.beacon.transmitter.view.activity.BaseActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.Collections;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Yuriy on 2016-07-09 Ibeacon.
 */

public class TransmitterFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.transmitter_radar)
    FloatingActionButton mRadarFab;

    @BindView(R.id.transmitter_uuid)
    EditText mUUIDView;

    @BindView(R.id.transmitter_major)
    EditText mMajorView;

    @BindView(R.id.transmitter_minor)
    EditText mMinorView;

    @BindView(R.id.transmitter_altbeacon_flag)
    CheckBox mAltbeaconFlag;

    @BindView(R.id.transmitter_ibeacon_flag)
    CheckBox mIbeaconFlag;

    private static final int REQUEST_ENABLE_BT = 1;
    private BeaconManager mBeaconManager = BeaconManager.getInstanceForApplication(getContext());
    private BeaconTransmitter mBeaconTransmitter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMenu();
        setListenerToRootView(view);
        mIbeaconFlag.setOnCheckedChangeListener(this);
        mAltbeaconFlag.setOnCheckedChangeListener(this);
    }

    private void setListenerToRootView(View view) {
        if (view != null)
            view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                int heightDiff = view.getRootView().getHeight() - view.getHeight();
                if (heightDiff > 250) {
                    mRadarFab.hide();
                } else {
                    mRadarFab.show();
                }
            });
    }

    @OnClick(R.id.transmitter_radar)
    public void simulateBeacon() {
        verifyBluetooth();
    }

    public void verifyBluetooth() {
        try {
            if (!mBeaconManager.checkAvailability()) {
                if (getActivity() != null) {
                    Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    getActivity().startActivityFromFragment(this, intentBtEnabled, REQUEST_ENABLE_BT);
                }
            } else if (isTransmissionSupported()) composeTransmitter();

        } catch (RuntimeException e) {
            showNotSupportAlertDialog();
        }

    }

    @OnClick(R.id.transmitter_btn_uuid)
    public void createUUID() {
        String newUUID = UUID.randomUUID().toString();
        mUUIDView.setText(newUUID);
    }

    private void composeTransmitter() {
        // Reset errors.
        mUUIDView.setError(null);
        mMajorView.setError(null);
        mMinorView.setError(null);

        // Store values at the time of the login attempt.
        String uuid = mUUIDView.getText().toString();
        String major = mMajorView.getText().toString();
        String minor = mMinorView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(uuid)) {
            mUUIDView.setError(getString(R.string.error_field_required));
            focusView = mUUIDView;
            cancel = true;

        } else if (TextUtils.isEmpty(major)) {
            mMajorView.setError(getString(R.string.error_field_required));
            focusView = mMajorView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(minor)) {
            mMinorView.setError(getString(R.string.error_field_required));
            focusView = mMinorView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            beaconTransmitter(uuid, major, minor);
        }
    }

    private void beaconTransmitter(String uuid, String major, String minor) throws RuntimeException {

        //Stop advertisement if it was starting early;
        if (mBeaconTransmitter != null && mBeaconTransmitter.isStarted()) {
            mBeaconTransmitter.stopAdvertising();
            mBeaconTransmitter = null;
            mRadarFab.setImageResource(R.drawable.ic_radar);
            showMessage("Advertisement stopped");
            return;
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String bluetoothName = bluetoothAdapter.getName();

        @SuppressLint("HardwareIds")
        Beacon.Builder builder = new Beacon.Builder()
                .setBluetoothName(!TextUtils.isEmpty(bluetoothName) ? bluetoothName : "Transmitter")
                .setBluetoothAddress(bluetoothAdapter.getAddress())
                .setId1(uuid)
                .setId2(major)
                .setId3(minor)
                .setTxPower(-59)
                .setRssi(-59);

        String beaconLayout = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"; // default Altbeacon;
        if (mAltbeaconFlag.isChecked()) {

            builder.setManufacturer(0x0118) //Radius Networks.
                    .setDataFields(Collections.singletonList(0L));

        } else if (mIbeaconFlag.isChecked()) {
            builder.setManufacturer(0x004C); //Apple Inc.
            beaconLayout = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
        }

        BeaconParser beaconParser = new BeaconParser().setBeaconLayout(beaconLayout);
        mBeaconTransmitter = new BeaconTransmitter(MyApp.appContext, beaconParser);
        mBeaconTransmitter.startAdvertising(builder.build(),
                new AdvertiseCallback() {
                    @Override
                    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                        mRadarFab.setImageResource(R.drawable.ic_stop);
                        showMessage("Advertisement start succeeded");
                    }

                    @Override
                    public void onStartFailure(int errorCode) {
                        super.onStartFailure(errorCode);
                        showMessage("Advertisement start failed with code:" + errorCode);
                    }
                });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBeaconTransmitter != null && mBeaconTransmitter.isStarted())
            mBeaconTransmitter.stopAdvertising();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_transmitter;
    }

    @Override
    public boolean hasCustomToolbar() {
        return true;
    }

    @Override
    protected void onReceiveBackPressed() {
        super.onReceiveBackPressed();
        getMainActivity().finish();
    }

    @Override
    protected int getTitle() {
        return R.string.app_name;
    }

    @Override
    protected int getMenu() {
        return R.menu.main_menu;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ENABLE_BT) {
                try {
                    if (isTransmissionSupported()) composeTransmitter();
                } catch (RuntimeException e) {
                    showNotSupportAlertDialog();
                }
            }
        }
    }

    private boolean isTransmissionSupported() {
        int code = BeaconTransmitter.checkTransmissionSupported(MyApp.appContext);
        if (code == BeaconTransmitter.NOT_SUPPORTED_CANNOT_GET_ADVERTISER_MULTIPLE_ADVERTISEMENTS
                || code == BeaconTransmitter.NOT_SUPPORTED_CANNOT_GET_ADVERTISER)
            throw new RuntimeException("Bluetooth LE not supported.");
        else return true;
    }

    private void showNotSupportAlertDialog() {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
            builder.setTitle(R.string.dialog_no_ble_broadcast_title);
            builder.setMessage(R.string.dialog_no_ble_broadcast_msg);
            builder.setPositiveButton(getString(R.string.ok), null);
            builder.show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (compoundButton != null) {
            if (compoundButton == mIbeaconFlag) {
                mIbeaconFlag.setChecked(checked);
                if (mIbeaconFlag.isChecked()) mAltbeaconFlag.setChecked(false);

            } else if (compoundButton == mAltbeaconFlag) {
                mAltbeaconFlag.setChecked(checked);
                if (mAltbeaconFlag.isChecked()) mIbeaconFlag.setChecked(false);
            }
        }
    }

    private void setupMenu() {
        if (getToolbar() != null)
            getToolbar().setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_menu_logout)
                    BaseActivity.mNavigator.setRootFragment(new LoginFragment(), true);
                return false;
            });
    }

}
