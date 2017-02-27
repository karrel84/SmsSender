package com.karrel.smssender;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 이주영 on 2017-02-27.
 */
public class SmsModel implements Parcelable {
    private final String TAG = SmsModel.class.getSimpleName();

    public String msg;
    public String phoneNumber;

    public SmsModel(String msg, String phoneNumber) {
        this.msg = msg;
        this.phoneNumber = phoneNumber;
    }

    protected SmsModel(Parcel in) {
        msg = in.readString();
        phoneNumber = in.readString();
    }

    public static final Creator<SmsModel> CREATOR = new Creator<SmsModel>() {
        @Override
        public SmsModel createFromParcel(Parcel in) {
            return new SmsModel(in);
        }

        @Override
        public SmsModel[] newArray(int size) {
            return new SmsModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msg);
        dest.writeString(phoneNumber);
    }

    @Override
    public String toString() {
        return String.format("phoneNumber : %s, msg : %s", phoneNumber, msg);
    }
}
