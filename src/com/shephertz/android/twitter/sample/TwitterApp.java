package com.shephertz.android.twitter.sample;



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
import android.util.Log;
import android.widget.Toast;
/*
 * @author Vishnu Garg
 * This class shows How can we authorize with Twitter in our application
 */
public class TwitterApp extends Activity{
	private static final String TAG = "TwiiterSample";
	private static Twitter twitter;
	private static RequestToken requestToken;
	private static SharedPreferences mSharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mSharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
		handleCallback();
	}
	
	@Override
	public void onStart(){
		super.onStart();
		if(isConnected()){
			showTweets();
		}
		else{
			authorizeApp();
		}
	}
	
	/*
	 * Show TweetList if authorization is done 
	 */
	private void showTweets(){
		Intent intent=new Intent(this,TwitterActivity.class);
		intent.putExtra(Constants.PREF_KEY_TOKEN,mSharedPreferences.getString(Constants.PREF_KEY_TOKEN, null));
		intent.putExtra(Constants.PREF_KEY_SECRET,mSharedPreferences.getString(Constants.PREF_KEY_SECRET, null));
		intent.putExtra(Constants.PREF_KEY_USER, mSharedPreferences.getString(Constants.PREF_KEY_USER, null));
		startActivity(intent);
		finish();
	}


	/*
	 * This function handle callback while authorizing with Twitter
	 */
	protected void handleCallback() {
		Uri uri = getIntent().getData();
		if (uri != null && uri.toString().startsWith(Constants.CALLBACK_URL)) {
			String verifier = uri.getQueryParameter(Constants.IEXTRA_OAUTH_VERIFIER);
            try { 
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier); 
                Editor e = mSharedPreferences.edit();
                e.putString(Constants.PREF_KEY_TOKEN, accessToken.getToken()); 
                e.putString(Constants.PREF_KEY_SECRET, accessToken.getTokenSecret()); 
                e.putString(Constants.PREF_KEY_USER, accessToken.getScreenName()); 
                e.commit();
                
	        } catch (Exception e) { 
	                Log.e(TAG, e.getMessage()); 
	                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show(); 
			}
        }		

	}

	/**
	 * check if the account is authorized
	 * @return
	 */
	private boolean isConnected() {
		return mSharedPreferences.getString(Constants.PREF_KEY_TOKEN, null) != null;
	}

	/*
	 * This function helps in authorization
	 */
	private void authorizeApp() {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(Constants.CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(Constants.CONSUMER_SECRET);
		Configuration configuration = configurationBuilder.build();
		twitter = new TwitterFactory(configuration).getInstance();
		try {
			requestToken = twitter.getOAuthRequestToken(Constants.CALLBACK_URL);
			Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
			this.startActivity(intent);
			finish();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}


}
