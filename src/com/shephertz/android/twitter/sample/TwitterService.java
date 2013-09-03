package com.shephertz.android.twitter.sample;

import java.util.ArrayList;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.os.Handler;

/*
 * @author Vishnu Garg
 */
public class TwitterService {


	/*
	 * This function Load all Tweets of User
	 * @param twitterToken
	 * @param twitterSecret
	 */
	 void loadTweets(final String twitterToken,final String twitterSecret,final MyTwitterListener callBack) {
		final Handler callerThreadHandler = new Handler();
		new Thread() {
			@Override
			public void run() {
				try {
				
					final ArrayList<TwitterInfo> myTweets=getTwitterList(twitterToken,twitterSecret);
					callerThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onTweetList(myTweets);
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
	

	 /*
	  * This Function Makes a ArrayList of user Tweets with desired information
	  * @param Twitter twitter
	  * @return ArrayList<TwitterInfo> tweetsList
	  */
	private ArrayList<TwitterInfo> getTweets(Twitter twitter) {
		ArrayList<TwitterInfo> tweetsList=new ArrayList<TwitterInfo>();
		try{
			ResponseList<Status> response=twitter.getUserTimeline();
			for(int i=0;i<response.size();i++){
				TwitterInfo tweetInfo=new TwitterInfo();
				Status status=response.get(i);
				tweetInfo.setText(status.getText());
				tweetInfo.setCreatedAt(status.getCreatedAt());
				tweetInfo.setCount(status.getRetweetCount());
				tweetInfo.setName(status.getUser().getName());
				tweetInfo.setPicurl(status.getUser().getProfileImageURL());
				tweetsList.add(tweetInfo);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return tweetsList;

	}
	
	
	/*
	 * This function Return User TweetList
	 * @param String twitterToken
	 * @param String twitterSecret
	 * @return ArrayList<TwitterInfo>
	 */
	private ArrayList<TwitterInfo> getTwitterList(String twitterToken,String twitterSecret){
		ConfigurationBuilder confbuilder = new ConfigurationBuilder();
		Configuration conf = confbuilder
							.setOAuthConsumerKey(Constants.CONSUMER_KEY)
							.setOAuthConsumerSecret(Constants.CONSUMER_SECRET)
							.setOAuthAccessToken(twitterToken)
							.setOAuthAccessTokenSecret(twitterSecret)
							.build();
		AccessToken accessToken =	new AccessToken(twitterToken, twitterSecret);
		Twitter twitter = new TwitterFactory(conf).getInstance(accessToken);
		return getTweets(twitter);
	} 
	
	/*
	 * interface to hadle callback when Tweets are loaded from Web
	 */
	public interface MyTwitterListener{
		public void onTweetList(ArrayList<TwitterInfo> myTweets);
		public void onError(String error);
		public void onStatusUpdate();
		
	}
}
