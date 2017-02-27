package com.karrel.smssender;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

/**
 * 다중 sms 송신 액티비티
 * <p>
 * must have <uses-permission android:name="android.permission.SEND_SMS" /> in AndroidManifest.xml
 */
public class SmsActivity extends AppCompatActivity {
    public static final String SMS_MODELS = "SMS_MODELS";
    private static final int REQUEST_PERVISSION_SMS = 1000;
    private final String TAG = "SmsActivity";

    private List<SmsModel> smsModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        Log.d(TAG, ":: onCreate");

        // 전송 버튼
        findViewById(R.id.btn_send).setOnClickListener(v -> showSendDialog());
        // 취소 버튼
        findViewById(R.id.btn_cancel).setOnClickListener(v -> cancel());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Log.d(TAG, ":: onPostCreate");

        // 파셀레이블로된 sms model들을 가져온다.
        smsModels = getIntent().getParcelableArrayListExtra(SMS_MODELS);

        try {
            if (smsModels == null || smsModels.size() == 0)
                throw new NullPointerException(getString(R.string.error_empty_sms_model));
        } catch (Exception e) {
            e.printStackTrace();
            showModelsEmptyMessage();
        }
        setUpViewPager();
    }

    /**
     * 뷰페이져를 셋업한다.
     */
    private void setUpViewPager() {
        Log.d(TAG, ":: setUpViewPager");
        ViewPager viewPager = (ViewPager) findViewById(R.id.smsPager);
        SmsAdapter smsAdapter = new SmsAdapter(this, smsModels);
        viewPager.setAdapter(smsAdapter);
    }

    /**
     * 모델이 없는것을 노출하는 다이얼로그를 만든다.
     */
    private void showModelsEmptyMessage() {
        Log.d(TAG, ":: showModelsEmptyMessage");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.empty_message);
        builder.setPositiveButton(R.string.comfirm, (d, w) -> finish());
        builder.setCancelable(false);
        builder.create().show();
    }

    /**
     * 정말 메세지 보낼거냐고 묻는 다이얼로그
     */
    @SuppressLint("StringFormatMatches")
    private void showSendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format(getString(R.string.want_you_send_msg), smsModels.size()));
        builder.setPositiveButton(R.string.comfirm, (d, w) -> sendSms());
        builder.setNegativeButton(R.string.cancel, null);
        builder.setCancelable(false);
        builder.create().show();
    }

    /**
     * 메세지 송신
     */
    @SuppressLint("StringFormatMatches")
    private void sendSms() {
        Log.d(TAG, ":: sendSms");

        // 퍼미션이 없으면 퍼미션을 요청합니다.
        int checkPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        Log.d(TAG, ":: sendSms checkPermission -> " + checkPermission);
        if (checkPermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_PERVISSION_SMS);
            return;
        }

        // 메세지를 보냅니다.
        SmsManager smsManager = new SmsManager(this);
        smsManager.setOnSmsSendResponseListener((c, f, t) -> showCompleteMsg(String.format(getString(R.string.sms_completeMsg, t, c))));
        for (SmsModel s : smsModels) {
            smsManager.sendSms(s.phoneNumber, s.msg);
        }
    }

    private Dialog complteDialog;

    /**
     * 문자 발송 성공 팝업
     */
    private void showCompleteMsg(String str) {
        if (complteDialog != null && complteDialog.isShowing()) complteDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(str);
        builder.setPositiveButton(R.string.comfirm, (d, w) -> complete());
        builder.setCancelable(false);
        complteDialog = builder.create();
        complteDialog.show();
    }

    /**
     * 송신 완료
     */
    private void complete() {
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 송신 취소
     */
    private void cancel() {
        Log.d(TAG, ":: cancel");
        // 취소됨
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PERVISSION_SMS) {
            if (resultCode == RESULT_OK) {
                sendSms();
            }
        }
    }
}
