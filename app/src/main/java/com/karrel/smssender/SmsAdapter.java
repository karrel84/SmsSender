package com.karrel.smssender;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by 이주영 on 2017-02-27.
 */
public class SmsAdapter extends PagerAdapter {
    private final String TAG = SmsAdapter.class.getSimpleName();

    private LayoutInflater mInflater;

    private List<SmsModel> smsModels;

    public SmsAdapter(Context c, List<SmsModel> smsModels) {
        mInflater = LayoutInflater.from(c);
        this.smsModels = smsModels;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d(TAG, " :: instantiateItem");

        View v = mInflater.inflate(R.layout.sms_page, null);

        SmsHolder smsHolder = new SmsHolder(v);
        smsHolder.setupData(smsModels.get(position));

        container.addView(v);

        return v;
    }

    @Override
    public int getCount() {
        Log.d(TAG, " :: getCount -> " + smsModels.size());
        return smsModels.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
