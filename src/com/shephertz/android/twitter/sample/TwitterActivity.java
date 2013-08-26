package com.shephertz.android.twitter.sample;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



public class TwitterActivity extends Activity {

	private ArrayList<TwitterInfo> tweetInfoList;
	private ListView list;
    private ProgressDialog progressDialog;
    private String myTwittername;
    private ImageLoader imageLoader;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_layout);
		imageLoader=new ImageLoader(this, 0);
		buildListView();
		progressDialog=new ProgressDialog(this);
		loadMyTweets();
	}
	private void buildHeader() {
		
		TextView header = (TextView)findViewById(R.id.page_header);
		header.setText( myTwittername);
	}
	public void onBackClicked(View view){
		finish();
		
	}
	private void showLaoding(String msg){
		progressDialog.setMessage(msg);
		progressDialog.show();
	}
	
	private void loadMyTweets(){
		try{
			Intent intent=getIntent();
		myTwittername=intent.getStringExtra(Const.PREF_KEY_USER);
		buildHeader();
		String myToken=intent.getStringExtra(Const.PREF_KEY_TOKEN);
		String myTokenSecret=intent.getStringExtra(Const.PREF_KEY_SECRET);
		showLaoding("Loading tweets.....");
		new TwitterService().loadTweets(myToken,myTokenSecret,this);
		}
		catch(Exception e){
		}
	}
	
	private void buildListView() {
		list = (ListView) findViewById(R.id.list);
		
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long duration) {
				showTweetInfo(tweetInfoList.get(position));
			}
		});
	}

	private void showTweetInfo(TwitterInfo tweet){
		Intent intent = new Intent(TwitterActivity.this, TweetDetails.class);
		intent.putExtra("count", tweet.getCount());
		intent.putExtra("name", tweet.getName());
		intent.putExtra("picUrl", tweet.getPicurl());
		intent.putExtra("tweet", tweet.getText());
		intent.putExtra("screenName", myTwittername);
		intent.putExtra("date", tweet.getCreatedAt().toString());
		startActivityForResult(intent, 0);
	}

	

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
	
	 void onTweetList(ArrayList<TwitterInfo> allTweets){
		progressDialog.dismiss();
		this.tweetInfoList=allTweets;
		list.setAdapter(new ActionListAdapter(this, tweetInfoList));
	}
	
	 void onError(String msg){
		progressDialog.dismiss();
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	
	
	/*
	 * Used to show friend list
	 */
	private class ActionListAdapter extends BaseAdapter {
		private Activity activity;
		private ArrayList<TwitterInfo> tweetList;
		private LayoutInflater inflater = null;

		public ActionListAdapter(Activity a,
				ArrayList<TwitterInfo> tweetInfoList) {
			activity = a;
			this.tweetList = tweetInfoList;
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {

				view = inflater.inflate(R.layout.twitter_item, null);
			}
			TwitterInfo tweet = tweetList.get(position);
			ImageView itemIcon = (ImageView) view.findViewById(R.id.image_icon);
			TextView itemName = (TextView) view.findViewById(R.id.username);
			TextView text = (TextView) view.findViewById(R.id.tweet_decs);
			if (itemIcon != null) {
				try {
					String url = tweet.getPicurl();
					if (url != null || !url.equals("")) {
					//	Utils.loadImageFromUrl(itemIcon, url);
						imageLoader.DisplayImage(url,  itemIcon);
					}
				} catch (Exception e) {

				}
			}
			itemName.setText(tweet.getName().trim());
			//text.setTextColor(AppContext.textColor);
			text.setText(tweet.getText().toString().trim());
					return view;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tweetInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

	}

}
