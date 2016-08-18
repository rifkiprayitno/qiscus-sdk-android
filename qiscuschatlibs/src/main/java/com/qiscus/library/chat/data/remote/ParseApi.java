package com.qiscus.library.chat.data.remote;

import android.util.Log;

import com.parse.ParseInstallation;
import com.parse.ParsePush;

import rx.Observable;
import rx.Subscriber;

public enum ParseApi {
    INSTANCE;
    private static final String TAG = ParseApi.class.getSimpleName();

    ParseApi() {
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static ParseApi getInstance() {
        return INSTANCE;
    }

    public Observable<Void> subscribePush(final String channel) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                Log.d(TAG, "Parse try to subscribe " + channel);
                ParsePush.subscribeInBackground(channel, e -> {
                    if (e != null) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed subscribe to " + channel);
                        subscriber.onError(e);
                    } else {
                        Log.d(TAG, "Succeeded subscribe to " + channel);
                        subscriber.onNext(null);
                        Log.d(TAG, "Currently we subscribe to " +
                                ParseInstallation.getCurrentInstallation().getList("channels"));
                    }
                    subscriber.onCompleted();
                });
            }
        });
    }

    public Observable<Void> unSubscribePush(final String channel) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                Log.d(TAG, "Parse try to unSubscribe " + channel);
                ParsePush.unsubscribeInBackground(channel, e -> {
                    if (e != null) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed unSubscribe to " + channel);
                        subscriber.onError(new Exception("Unable to connect to server!"));
                    } else {
                        Log.d(TAG, "Succeeded unSubscribe to " + channel);
                        subscriber.onNext(null);
                        Log.d(TAG, "Currently we subscribe to " +
                                ParseInstallation.getCurrentInstallation().getList("channels"));
                    }
                    subscriber.onCompleted();
                });
            }
        });
    }

    public void clearParseInstallation() {
        final ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.remove("channels");
        installation.saveEventually(e -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                Log.d(TAG, "Clear parse installation");
                Log.d(TAG, "Currently we subscribe to " +
                        ParseInstallation.getCurrentInstallation().getList("channels"));
            }
        });
    }
}
