package com.karrel.smssender;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by 이주영 on 2017-02-27.
 */
public class SmsHolder {
    private final String TAG = SmsHolder.class.getSimpleName();
    private SmsModel smsModel;

    private View contentView;

    private TextView phoneNum;
    private TextView message;

    public SmsHolder(View contentView) {
        this.contentView = contentView;

        phoneNum = (TextView) contentView.findViewById(R.id.phone_num);
        message = (TextView) contentView.findViewById(R.id.message);
    }

    public void setupData(SmsModel smsData) {
        Log.d(TAG, ":: setupData smsData -> " + smsData.toString());

        this.smsModel = smsData;

        phoneNum.setText(smsData.phoneNumber);
        message.setText(smsData.msg);
    }
}
