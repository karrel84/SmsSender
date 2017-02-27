package com.karrel.smssender;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

/**
 * Created by 이주영 on 2016-09-01.
 */
public class SmsManager {

    private int complete;
    private int fail;
    private int total;
    private Context mContext;

    public interface SmsSendResponseListener {
        void onResponse(int complete, int fail, int total);
    }

    private SmsSendResponseListener smsSendResponseListener;

    public SmsManager(Context context) {
        mContext = context;
        complete = 0;
        fail = 0;
        total = 0;

        setupRegister(context);
    }

    private void setupRegister(Context context) {
        context.registerReceiver(smsReceiver, new IntentFilter("SMS_SENT_ACTION"));
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        // 도착 완료
                        Toast.makeText(context, context.getString(R.string.sms_receive_complete), Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // 도착 안됨
                        Toast.makeText(context, context.getString(R.string.sms_receive_fail), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_DELIVERED_ACTION"));
    }

    public void setOnSmsSendResponseListener(SmsSendResponseListener listener) {
        smsSendResponseListener = listener;

    }


    public final void sendSmsForApp(String phoneNumber, String massege) {
        if (Build.VERSION.SDK_INT > 18) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + Uri.encode(phoneNumber)));
            intent.putExtra("sms_body", massege);
            mContext.startActivity(intent);
        } else {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("address", phoneNumber); // 받는사람 번호
            sendIntent.putExtra("sms_body", massege); // 보낼 문자
            sendIntent.setType("vnd.android-dir/mms-sms");
            mContext.startActivity(sendIntent);
        }
    }

    public final void sendSmsForApp(String phoneNumber, String massege, int flag) {
        if (Build.VERSION.SDK_INT > 18) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setFlags(flag);
            intent.setData(Uri.parse("smsto:" + Uri.encode(phoneNumber)));
            intent.putExtra("sms_body", massege);
            mContext.startActivity(intent);
        } else {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setFlags(flag);
            sendIntent.putExtra("address", phoneNumber); // 받는사람 번호
            sendIntent.putExtra("sms_body", massege); // 보낼 문자
            sendIntent.setType("vnd.android-dir/mms-sms");
            mContext.startActivity(sendIntent);
        }
    }

    public void sendSms(String smsNumber, String smsText) {
        String strs[] = smsText.split("\t\n");

        for (String str : strs) {
            sendTextMessage(smsNumber, str);
        }
    }

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ++total;
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    // 전송 성공
//                        Toast.makeText(context, context.getString(R.string.send_complete), Toast.LENGTH_SHORT).show();
                    complete++;
                    break;
                case android.telephony.SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    // 전송 실패
//                        Toast.makeText(context, context.getString(R.string.send_fail), Toast.LENGTH_SHORT).show();
                    fail++;
                    break;
                case android.telephony.SmsManager.RESULT_ERROR_NO_SERVICE:
                    // 서비스 지역 아님
//                        Toast.makeText(context, context.getString(R.string.not_service_area), Toast.LENGTH_SHORT).show();
                    fail++;
                    break;
                case android.telephony.SmsManager.RESULT_ERROR_RADIO_OFF:
                    // 무선 꺼짐
//                        Toast.makeText(context, context.getString(R.string.off_the_radio), Toast.LENGTH_SHORT).show();
                    fail++;
                    break;
                case android.telephony.SmsManager.RESULT_ERROR_NULL_PDU:
                    // PDU 실패
//                        Toast.makeText(context, "PDU Null", Toast.LENGTH_SHORT).show();
                    fail++;
                    break;
            }

            smsSendResponseListener.onResponse(complete, fail, total);
        }
    };

    /**
     * 메세지를 보낸다.
     */
    private void sendTextMessage(String smsNumber, String smsText) {
        if (smsText.length() > 70) {
            sendTextMessage(smsNumber, smsText.substring(0, 70));
            smsText = smsText.substring(70);
        }

        PendingIntent sentIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("SMS_SENT_ACTION"), 0);
        PendingIntent deliveredIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("SMS_DELIVERED_ACTION"), 0);

        android.telephony.SmsManager mSmsManager = android.telephony.SmsManager.getDefault();
        mSmsManager.sendTextMessage(smsNumber, null, smsText, sentIntent, deliveredIntent);
    }

}
