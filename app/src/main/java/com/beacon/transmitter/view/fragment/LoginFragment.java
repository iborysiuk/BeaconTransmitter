package com.beacon.transmitter.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;

import com.beacon.transmitter.R;
import com.beacon.transmitter.controllers.retrofit.ApiService;
import com.beacon.transmitter.controllers.retrofit.RestAPIClient;
import com.beacon.transmitter.controllers.retrofit.RestAPIError;
import com.beacon.transmitter.model.Users;
import com.beacon.transmitter.view.activity.BaseActivity;

import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.beacon.transmitter.AppPreferences.getEmail;
import static com.beacon.transmitter.AppPreferences.isChecked;
import static com.beacon.transmitter.AppPreferences.setChecked;
import static com.beacon.transmitter.AppPreferences.setEmail;
import static com.beacon.transmitter.controllers.util.Utils.hideKeyboard;
import static com.beacon.transmitter.controllers.util.Validation.isEmailValid;
import static com.beacon.transmitter.controllers.util.Validation.isPasswordValid;


/**
 * Created by Yuriy on 2016-07-09 Ibeacon.
 */
public class LoginFragment extends BaseFragment {

    @BindView(R.id.login_email_box)
    EditText mEmailView;

    @BindView(R.id.login_password_box)
    EditText mPasswordView;

    @BindView(R.id.login_remember)
    CheckBox mRememberCheck;

    private Subscription mLoginSubscription;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(getActivity(), mPasswordView);
                return true;
            }
            return false;
        });

        mRememberCheck.setOnCheckedChangeListener((compoundButton, flag) -> setChecked(flag));
    }

    @Override
    public void onStart() {
        super.onStart();

        this.mRememberCheck.setChecked(isChecked());

        if (mRememberCheck.isChecked()) {
            mEmailView.setText(getEmail());
            mPasswordView.requestFocus();
        } else {
            mEmailView.setText(null);
            mPasswordView.setText(null);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_login;
    }

    @OnClick(R.id.login_btn_signin)
    public void attemptSingIn() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;

        } else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;

        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showDialog();
            mLoginSubscription = RestAPIClient.createVPService(ApiService.class, new Users(email, password))
                    .login()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(usersResponse -> {
                        hideDialog();
                        if (!usersResponse.isSuccessful()) {
                            showMessage(RestAPIError.getErrorMessage(usersResponse.code()));

                        } else {
                            if (mRememberCheck.isChecked()) {
                                setEmail(email);
                            }

                            if (isFragmentNavigatorNotNull())
                                BaseActivity.mNavigator.setRootFragment(new TransmitterFragment(), true);
                        }

                    }, throwable -> {
                        hideDialog();
                        if (throwable instanceof UnknownHostException || throwable instanceof HttpException)
                            showMessage(getString(R.string.error_no_internet_connection));
                        else showMessage(throwable.getMessage());
                    });
        }

    }


    @OnClick(R.id.login_btn_register)
    public void attemptSingUp() {
        if (isFragmentNavigatorNotNull()) {
            BaseActivity.mNavigator.goTo(new RegisterFragment());
        }
    }

    @Override
    public void onDestroy() {
        if (mLoginSubscription != null && !mLoginSubscription.isUnsubscribed())
            mLoginSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public boolean hasCustomToolbar() {
        return true;
    }

    @Override
    protected int getTitle() {
        return R.string.app_name;
    }

    @Override
    protected void onReceiveBackPressed() {
        getActivity().finish();
    }


}
