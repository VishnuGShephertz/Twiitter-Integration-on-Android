package com.shephertz.android.twitter.sample;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetDetails extends Activity {

	private String myTwitterName;
	private ImageLoader imageloader;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tweet_detail);

		imageloader = new ImageLoader(this, 0);

		upDateView();
		buildHeader();

	}

	public void onBackClicked(View view) {
		finish();

	}

	private void buildHeader() {

		TextView header = (TextView) findViewById(R.id.page_header);
		header.setText(myTwitterName);
	}

	private void upDateView() {
		Intent intent = getIntent();
		myTwitterName = intent.getStringExtra("screenName");

		TextView txtName = (TextView) findViewById(R.id.my_name);

		txtName.setText(intent.getStringExtra("name"));

		TextView txtDate = (TextView) findViewById(R.id.tweet_date);

		txtDate.setText("Created  : " + intent.getStringExtra("date"));

		TextView txtCount = (TextView) findViewById(R.id.tweet_count);

		txtCount.setText("Retweets : " + intent.getLongExtra("count", 0));

		TextView txtDetail = (TextView) findViewById(R.id.tweet_details);

		txtDetail.setText("Tweet    : " + intent.getStringExtra("tweet"));

		ImageView myImage = (ImageView) findViewById(R.id.my_pic);

		imageloader.DisplayImage(intent.getStringExtra("picUrl"), myImage);

	}

	public void onRetweetClicked(View view) {
		Intent viewIntent = new Intent("android.intent.action.VIEW",
				Uri.parse("https://mobile.twitter.com/" + myTwitterName));
		startActivity(viewIntent);
	}

}
