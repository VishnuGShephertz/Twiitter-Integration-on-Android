package com.shephertz.android.twitter.sample;

import java.util.ArrayList;

import android.os.Handler;
import android.provider.CallLog;

import com.shephertz.android.twitter.sample.TwitterService.MyTwitterListener;
import com.shephertz.app42.paas.sdk.android.ServiceAPI;
import com.shephertz.app42.paas.sdk.android.push.PushNotificationService;
import com.shephertz.app42.paas.sdk.android.social.Social;
import com.shephertz.app42.paas.sdk.android.social.SocialService;

public class App42Service {
	
	private SocialService socialService;
	private static App42Service mInstance = null;
	private App42Service() {
		ServiceAPI sp = new ServiceAPI(Constants.API_KEY, Constants.SECRET_KEY);
      this.socialService=sp.buildSocialService();
	}

	/*
	 * instance of class
	 */
	public static App42Service instance() {

		if (mInstance == null) {
			mInstance = new App42Service();
		}

		return mInstance;
	}
	
	void upDateStatus(final String userName,final String twitterToken,final String twitterSecret,final String status,final MyTwitterListener callBack) {
		final Handler callerThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
				
					Social socialObj = socialService.linkUserTwitterAccount(userName,
							twitterToken, twitterSecret,
							Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
					socialService.updateTwitterStatus(userName, status);
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onStatusUpdate();
						}
					});
				} catch (final Exception ex) {
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							if(callBack!=null){
								callBack.onError(ex.toString());
							}
					
						}
					});
				}
			}
		}.start();
	}
}
