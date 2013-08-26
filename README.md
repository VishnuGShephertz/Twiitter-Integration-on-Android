Twiitter-Integration-on-Android
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
3. After Login on Twitter click on Create a new application button.
4. Fill the desired information and create your Twitter application.
5. You can find your Consumer key and Consumer secret here in OAuth settings option.
6. Download the project from [here] (https://github.com/VishnuGShephertz/Twiitter-Integration-on-Android/archive/master.zip) and import it in the eclipse.<br/>
7. Open Constants.java file and make following changes.

```
A. Change CONSUMER_KEY with your Consumer key from step 5 at line no 5.
B. Change CONSUMER_SECRET with your Consumer secret from step 5 at line no 6.
```
8.&nbsp; Build your android application and install on your android device.<br/>

# Design Details:

__Authorization With Face-Book:__ To use Twitter API in your android application you have to authorize application.
 as sample I have authorized my application in TwitterApp.java file. In this method you have to pass three parameters :
 1. Your host Activity on which you have to get callback from Facebook API.</br>
 4. Once your application is authorize , you can use face-Book API directly.
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
