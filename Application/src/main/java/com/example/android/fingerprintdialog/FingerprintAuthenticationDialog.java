/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.example.android.fingerprintdialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class FingerprintAuthenticationDialog extends Dialog
        implements TextView.OnEditorActionListener, FingerprintUiHelper.Callback {

    private TextView mCancelButton;
    private CheckBox mUseFingerprintFutureCheckBox;
    private TextView mNewFingerprintEnrolledTextView;
    private Stage mStage = Stage.FINGERPRINT;
    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintUiHelper mFingerprintUiHelper;
    private Activity mActivity;


    public FingerprintAuthenticationDialog(@NonNull Context context) {
        super(context, R.style.Theme_Transparent);
        mActivity = (Activity) context;
        getWindow().setGravity(Gravity.CENTER);
        getWindow().getDecorView().setPadding(0, 0, 0, 0); //消除边距

        WindowManager.LayoutParams lp = getWindow().getAttributes();

        WindowManager m = mActivity.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        lp.width = (int) (d.getWidth() * 0.8);   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
        initView(context);
    }


    public void initView(Context context) {
        View v = View.inflate(context, R.layout.fingerprint_dialog_container, null);
        mCancelButton = (TextView) v.findViewById(R.id.cancel_button);
        mCancelButton.setText(R.string.cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mUseFingerprintFutureCheckBox = (CheckBox)
                v.findViewById(R.id.use_fingerprint_in_future_check);
        mNewFingerprintEnrolledTextView = (TextView)
                v.findViewById(R.id.new_fingerprint_enrolled_description);
        mFingerprintUiHelper = new FingerprintUiHelper(
                mActivity.getSystemService(FingerprintManager.class),
                (ImageView) v.findViewById(R.id.fingerprint_icon),
                (TextView) v.findViewById(R.id.fingerprint_status), this);
        updateStage();

        if (!mFingerprintUiHelper.isFingerprintAuthAvailable()) {
            goToBackup();
        }
        mFingerprintUiHelper.startListening(mCryptoObject);
        this.setContentView(v);

    }


    public void setStage(Stage stage) {
        mStage = stage;
    }


    /**
     * Sets the crypto object to be passed in when authenticating with fingerprint.
     */
    public void setCryptoObject(FingerprintManager.CryptoObject cryptoObject) {
        mCryptoObject = cryptoObject;
    }

    /**
     * Switches to backup (password) screen. This either can happen when fingerprint is not
     * available or the user chooses to use the password authentication method by pressing the
     * button. This can also happen when the user had too many fingerprint attempts.
     */
    private void goToBackup() {
        mStage = Stage.DISABLE;
        updateStage();
//        mPassword.requestFocus();

        // Show the keyboard.
//        mPassword.postDelayed(mShowKeyboardRunnable, 500);

        // Fingerprint is not used anymore. Stop listening for it.
        mFingerprintUiHelper.stopListening();
    }
    /**
     * @return true if {@code password} is correct, false otherwise
     */
    private boolean checkPassword(String password) {
        // Assume the password is always correct.
        // In the real world situation, the password needs to be verified in the server side.
        return password.length() > 0;
    }

    private final Runnable mShowKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
//            mInputMethodManager.showSoftInput(mPassword, 0);
        }
    };

    private void updateStage() {
        switch (mStage) {
            case FINGERPRINT:
                mCancelButton.setText(R.string.cancel);
                break;
            case NEW_FINGERPRINT_ENROLLED:
                // Intentional fall through
            case DISABLE:
                mCancelButton.setText(R.string.cancel);
                if (mStage == Stage.NEW_FINGERPRINT_ENROLLED) {
                    mNewFingerprintEnrolledTextView.setVisibility(View.VISIBLE);
                    mUseFingerprintFutureCheckBox.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            return true;
        }
        return false;
    }

    @Override
    public void onAuthenticated() {
//        mActivity.onPurchased(true /* withFingerprint */, mCryptoObject);
        dismiss();
    }

    @Override
    public void onError() {
        goToBackup();
    }

    /**
     * Enumeration to indicate which authentication method the user is trying to authenticate with.
     */
    public enum Stage {
        FINGERPRINT,
        NEW_FINGERPRINT_ENROLLED,
        DISABLE
    }
}
