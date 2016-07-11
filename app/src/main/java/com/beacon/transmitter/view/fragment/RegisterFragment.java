package com.beacon.transmitter.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.beacon.transmitter.R;
import com.beacon.transmitter.controllers.retrofit.ApiService;
import com.beacon.transmitter.controllers.retrofit.RestAPIClient;
import com.beacon.transmitter.model.Users;
import com.beacon.transmitter.view.activity.BaseActivity;

import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.beacon.transmitter.controllers.util.Utils.hideKeyboard;
import static com.beacon.transmitter.controllers.util.Validation.isEmailValid;
import static com.beacon.transmitter.controllers.util.Validation.isPasswordValid;


/**
 * Created by Yuriy on 2016-07-09 Ibeacon.
 */

public class RegisterFragment extends BaseFragment {

    @BindView(R.id.register_username_box)
    EditText mUsernameView;

    @BindView(R.id.register_email_box)
    EditText mEmailView;

    @BindView(R.id.register_password_box)
    EditText mPasswordView;

    private Subscription mRegisterSubscription;

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
    }

    @OnClick(R.id.register_btn_action)
    public void attemptSingUp() {

        mUsernameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

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
            this.mEmailView.setError(getString(R.string.error_field_required));
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
            mRegisterSubscription = RestAPIClient.createVPService(ApiService.class, new Users(username, email, password))
                    .register()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((Response<ResponseBody> usersResponse) -> {
                        hideDialog();
                        if (usersResponse.isSuccessful())
                            onReceiveBackPressed();
                        else {
                            showMessage(getString(R.string.error_unexpected));
                        }
                    }, throwable -> {
                        hideDialog();
                        if (throwable instanceof UnknownHostException || throwable instanceof HttpException)
                            showMessage(getString(R.string.error_no_internet_connection));
                        else showMessage(throwable.getMessage());
                    });
        }

    }

    @Override
    public void onDestroy() {
        if (mRegisterSubscription != null && !mRegisterSubscription.isUnsubscribed())
            mRegisterSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_register;
    }

    @Override
    protected void onReceiveBackPressed() {
        super.onReceiveBackPressed();
        if (isFragmentNavigatorNotNull())
            BaseActivity.mNavigator.removeFragment(this);
    }

    @Override
    public boolean hasCustomToolbar() {
        return true;
    }

    @Override
    public boolean hasCustomBackArrow() {
        return true;
    }

    @Override
    protected int getTitle() {
        return R.string.title_register_fragment;
    }
}
