package com.karrel.smssender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // SMS 송신에 대한 결과
    private static final int REQUEST_SEND_SMS = 1000;
    private final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mms);

        findViewById(R.id.send_message).setOnClickListener(v -> startSmsActivity());
    }

    /**
     * sms를 송신하는 액티비티를 시작하자
     */
    private void startSmsActivity() {
        Log.d(TAG, ":: startSmsActivity");
        Intent intent = new Intent(this, SmsActivity.class);

        ArrayList<SmsModel> smsModels = new ArrayList<>();
        smsModels.add(new SmsModel("hello, i'm test messege!!", "010-7495-0523"));
        smsModels.add(new SmsModel("hello, i'm test messege!!", "010-7495-0523"));
//        smsModels.add(new SmsModel("hello, i'm test messege!!hello, i'm test messege!!hello, i'm test messege!!", "010-5032-4678"));

        intent.putParcelableArrayListExtra(SmsActivity.SMS_MODELS, smsModels);
        startActivityForResult(intent, REQUEST_SEND_SMS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SEND_SMS) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, ":: onActivityResult -> RESULT_OK");
            } else {
                Log.d(TAG, ":: onActivityResult -> RESULT_CANCEL");
            }
        }
    }
}
