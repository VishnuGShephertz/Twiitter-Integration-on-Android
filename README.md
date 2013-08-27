Twitter-Integration-on-Android
===============================

This sample application shows how easily we can we link our application through Twitter.

# About Sample application

1. This sample shows how can we create application on twitter and generate Consumer-Key and Secret-Key .
2. How can we authenticate Twitter User with our android application.
2. How can we show Tweets of User with all details.
3. How can a user re-tweet those tweets again.


# Running Sample

1. First you have to create a Twitter application to get required Twitter keys.<br/>
2. Click [here] (https://dev.twitter.com/apps) to create a Twitter application.
3. After successful login on Twitter, click on Create a new application button.
4. Fill the desired information and create your Twitter application.
5. You can find your Consumer key and Consumer secret here in OAuth settings option.
6. Download the project from [here] (https://github.com/VishnuGShephertz/Twiitter-Integration-on-Android/archive/master.zip) and import it in the eclipse.<br/>
7. Open Constants.java file and make following changes.

```
A. Change CONSUMER_KEY with your Consumer key from step 5 at line no 5.
B. Change CONSUMER_SECRET with your Consumer secret from step 5 at line no 6.
```
8.&nbsp; Here we are using twitter4j-core-3.0.3.jar for Twitter  integration.<br/>
9.&nbsp; Build your android application and install on your android device.<br/>

# Design Details:

__Authorization With Face-Book:__ To use Twitter API in your android application you have to authorize application.
 as sample I have authorized my application in TwitterApp.java file.
 1. We require CONSUMER_KEY and CONSUMER_SECRET here.</br>
 2. Once your application is authorize , you can use face-Book API directly.
 
``` 
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
  
```

__Handle Authorization callback :__ After authorization step you have to handle authorization callback and have to save authorization details such that next time no authorization needed.
This is done in TwitterApp.java file.</br>
 1. Here we get Accesstoken that shows that authorization is done.
 2. Using this AccessToken we get our authToken , authTokenSecert and screenName.We save these details 
    so that next time no need to authorize user.
```
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
```

__Load Tweet-list :__ Following method is use used load all user Tweets if user is authorize.It takes
  authToken, authTokenSecert as an argument that are save in Shared Preferences of android device. This is written in TwitterService.java file. 

```
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
```

__Build Tweet-list :__ Following function makes Tweet-list with desired Tweet information.
This function is written in TwitterService.java file.

```
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
