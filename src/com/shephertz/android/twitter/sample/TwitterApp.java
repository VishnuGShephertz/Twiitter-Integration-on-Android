package com.shephertz.android.twitter.sample;



import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class TwitterApp extends Activity{
	private static final String TAG = "T4JSample";


	private static Twitter twitter;
	private static RequestToken requestToken;
	private static SharedPreferences mSharedPreferences;
	//private static TwitterStream twitterStream;
	private boolean running = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mSharedPreferences = getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE);
		handleCallback();
	
	}
	
	@Override
	public void onStart(){
		super.onStart();
		if(isConnected()){
			showTweets();
		}
		else{
			askOAuth();
		}
	}
	
	
	private void showTweets(){
		Intent intent=new Intent(this,TwitterActivity.class);
		intent.putExtra(Const.PREF_KEY_TOKEN,mSharedPreferences.getString(Const.PREF_KEY_TOKEN, null));
		intent.putExtra(Const.PREF_KEY_SECRET,mSharedPreferences.getString(Const.PREF_KEY_SECRET, null));
		intent.putExtra(Const.PREF_KEY_USER, mSharedPreferences.getString(Const.PREF_KEY_USER, null));
		startActivity(intent);
		finish();
	}


	protected void handleCallback() {
		Uri uri = getIntent().getData();
		if (uri != null && uri.toString().startsWith(Const.CALLBACK_URL)) {
			String verifier = uri.getQueryParameter(Const.IEXTRA_OAUTH_VERIFIER);
            try { 
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier); 
                Editor e = mSharedPreferences.edit();
                e.putString(Const.PREF_KEY_TOKEN, accessToken.getToken()); 
                e.putString(Const.PREF_KEY_SECRET, accessToken.getTokenSecret()); 
                e.putString(Const.PREF_KEY_USER, accessToken.getScreenName()); 
                e.commit();
                
	        } catch (Exception e) { 
	                Log.e(TAG, e.getMessage()); 
	                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show(); 
			}
        }		

	}

//	public void tweets() {
//	
//
//		if (isConnected()) {
//			String oauthAccessToken ="1111251072-naI1dQH6BwXUPlsG9rZFOaOoFz67TeCtCXqz5CM"; //mSharedPreferences.getString(Const.PREF_KEY_TOKEN, "");
//			String oAuthAccessTokenSecret ="hjnEHSoKCUhkJ7WO7mawGPh9DWUiuHx9lIxRugZLkU" ;//mSharedPreferences.getString(Const.PREF_KEY_SECRET, "");
//			ConfigurationBuilder confbuilder = new ConfigurationBuilder();
//			Configuration conf = confbuilder
//								.setOAuthConsumerKey(Const.CONSUMER_KEY)
//								.setOAuthConsumerSecret(Const.CONSUMER_SECRET)
//								.setOAuthAccessToken(oauthAccessToken)
//								.setOAuthAccessTokenSecret(oAuthAccessTokenSecret)
//								.build();
//			AccessToken accessToken = new AccessToken(oauthAccessToken, oAuthAccessTokenSecret);
//			//twitterStream = new TwitterStreamFactory(conf).getInstance();
//			twitter = new TwitterFactory(conf).getInstance(accessToken);
//			buttonLogin.setText(R.string.label_disconnect);
//			getTweetButton.setEnabled(true);
//			getTimeLine();
//			
//		} else {
//			buttonLogin.setText(R.string.label_connect);
//		}
//	}

	/**
	 * check if the account is authorized
	 * @return
	 */
	private boolean isConnected() {
		return mSharedPreferences.getString(Const.PREF_KEY_TOKEN, null) != null;
	}

	private void askOAuth() {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(Const.CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(Const.CONSUMER_SECRET);
		Configuration configuration = configurationBuilder.build();
		twitter = new TwitterFactory(configuration).getInstance();
		
		try {
			requestToken = twitter.getOAuthRequestToken(Const.CALLBACK_URL);
			Toast.makeText(this, "Please authorize this app!", Toast.LENGTH_LONG).show();
			Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
			this.startActivity(intent);
			finish();
		
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

//	/**
//	 * Remove Token, Secret from preferences
//	 */
//	private void disconnectTwitter() {
//		SharedPreferences.Editor editor = mSharedPreferences.edit();
//		editor.remove(Const.PREF_KEY_TOKEN);
//		editor.remove(Const.PREF_KEY_SECRET);
//		editor.commit();
//	}
	
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.twitterLogin:
//			if (isConnected()) {
//				disconnectTwitter();
//				buttonLogin.setText(R.string.label_connect);
//			} else {
//				askOAuth();
//			}
//			break;
//		case R.id.getTweet:
//			if (running) {
//				//getTimeLine();
//			//	stopStreamingTimeline();
//				running = false;
//				getTweetButton.setText("start streaming");
//			} else {
//				//startStreamingTimeline();
//				running = true;
//				getTweetButton.setText("stop streaming");
//			}
//			break;
//		}
//	}
//	
//	private void stopStreamingTimeline() {
//		twitterStream.shutdown();
//	}
	
//	public void getTimeLine(){
//		
//		final Handler callerThreadHandler = new Handler();
//		new Thread() {
//			@Override
//			public void run() {
//				try {
//					System.out.println("ddsffds");
//					ResponseList<Status> response=twitter.getUserTimeline();
//					for(int i=0;i<response.size();i++){
//						Status sta=response.get(i);
//						sta.getUser().getName();
//						String tewwtOn=sta.getCreatedAt().toString();
//						tewwtOn=sta.getText()	;
//						tewwtOn=""+sta.getCurrentUserRetweetId();
//						tewwtOn=""+sta.getId()	;
//						tewwtOn=""+sta.getRetweetCount();
//						tewwtOn=sta.getUser().getName();
//						
//						tewwtOn=sta.getUser().getScreenName();
//						tewwtOn=sta.getUser().getOriginalProfileImageURL();
//					
//						tewwtOn=sta.getUser().getMiniProfileImageURL();
//						tewwtOn=sta.getUser().getProfileBannerMobileURL();
//						tewwtOn=sta.getUser().getProfileImageURL();
//						System.out.println("vishnu");
//					
//					}
//					callerThreadHandler.post(new Runnable() {
//						@Override
//						public void run() {
//				
//						}
//					});
//				} catch (final Exception ex) {
//					callerThreadHandler.post(new Runnable() {
//						@Override
//						public void run() {
//						System.out.println(ex.toString());
//						}
//					});
//				}
//			}
//		}.start();
//	
//	}


}
