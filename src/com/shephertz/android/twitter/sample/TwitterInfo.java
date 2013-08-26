package com.shephertz.android.twitter.sample;

import java.util.Date;

public class TwitterInfo {

	
	private String name;
	private long count;
	private Date createdAt;
	private String text;
	private String picurl;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public String getPicurl() {
		return picurl;
	}
	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}
	
	
	
	
}
