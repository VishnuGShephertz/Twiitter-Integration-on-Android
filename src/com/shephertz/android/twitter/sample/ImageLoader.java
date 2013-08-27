package com.shephertz.android.twitter.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/*
 * @author Vishnu Garg
 */
public class ImageLoader {
	private int requiredSize=0;
    private boolean loadNormal=true;

	private HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();

	private File cacheDir;

	/*
	 * Constructor of Class
	 * @param context of the Activity which is calling
	 * @param imageSize required for image sampling if not pass -1
	 * 
	 */
	public ImageLoader(Context context,int imageSize) {
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
		requiredSize=imageSize;
		if(imageSize>0){
			loadNormal=false;
		}
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),Constants.APP_NAME);
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	final int stub_id = R.drawable.icon;

	/*
	 * This function loads image on ImageView in background Thread
	 * @param url URL of image 
	 * @param imageView on which image is going to be displayed
	 * 
	 */
	public void DisplayImage(String url,ImageView imageView) {
		if (cache.containsKey(url)){
			imageView.setImageBitmap(cache.get(url));
		}
		else {
			imageView.setTag(url);
			queuePhoto(url, imageView);
		}
	}

	/*
	 *  This function makes queue of images when the are requested to load
	 * @param url URL of image 
	 * @param imageView on which image is going to be displayed
	 */
	private void queuePhoto(String url, ImageView imageView) {
		// This ImageView may be used for other images before. So there may be
		// some old tasks in the queue. We need to discard them.
		photosQueue.Clean(imageView);
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		synchronized (photosQueue.photosToLoad) {
			photosQueue.photosToLoad.push(p);
			photosQueue.photosToLoad.notifyAll();
		}

		// start thread if it's not started yet
		if (photoLoaderThread.getState() == Thread.State.NEW)
			photoLoaderThread.start();
	}

	/*
	 * This function used to get Bitmap of image.
	 * Also used to do sampling of image if required else load without sampling
	 * @param url of Image
	 */
	public  Bitmap getBitmap(String url) {
		// I identify images by hashcode. Not a perfect solution, good for the
		// demo.
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);

		// from SD cache
		Bitmap b;
		if(loadNormal){
			b = decodeWithoutSampling(f);
		}
		else{
			b = decodewithSampleing(f);
		}
	
		if (b != null)
			return b;

		// from web
		try {
			Bitmap bitmap = null;
			InputStream is = new URL(url).openStream();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			if(loadNormal){
				bitmap = decodeWithoutSampling(f);
			}
			else{
				bitmap = decodewithSampleing(f);
			}
			return bitmap;
		} catch (Throwable ex) {
			 ex.printStackTrace();
	           if(ex instanceof OutOfMemoryError)
	              clearCache();
	           return null;
		}
	}

	/*
	 * This function decode File in Bitmap without sampling
	 * @param file the is going to be decoded.
	 */
	private Bitmap decodeWithoutSampling(File f){
		try {
			return BitmapFactory.decodeStream(new FileInputStream(f));
			
		} catch (Exception e) {
		}
		return null;
	}
	
	/*
	 * This function decode File in Bitmap with sampling
	 * This is uses to reduce memory consumption of our Application
	 * @param file the is going to be decoded.
	 */
	private Bitmap decodewithSampleing(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < requiredSize
						|| height_tmp / 2 < requiredSize)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	/*
	 * Class Task for the Queue
	 */
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	PhotosQueue photosQueue = new PhotosQueue();

	/*
	 * Stops the Thread
	 */
	public void stopThread() {
		photoLoaderThread.interrupt();
	}

	/*
	 * This class stores the images that are downloaded.
	 */
	class PhotosQueue {
		private Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();

		// removes all instances of this ImageView
		public void Clean(ImageView image) {
			for (int j = 0; j < photosToLoad.size();) {
				if (photosToLoad.get(j).imageView == image)
					photosToLoad.remove(j);
				else
					++j;
			}
		}
	}

	
/*
 * This class load image in background Thread
 * 
 */
	class PhotosLoader extends Thread {
		public void run() {
			try {
				while (true) {
					// thread waits until there are any images to load in the
					// queue
					if (photosQueue.photosToLoad.size() == 0)
						synchronized (photosQueue.photosToLoad) {
							photosQueue.photosToLoad.wait();
						}
					if (photosQueue.photosToLoad.size() != 0) {
						PhotoToLoad photoToLoad;
						synchronized (photosQueue.photosToLoad) {
							photoToLoad = photosQueue.photosToLoad.pop();
						}
						Bitmap bmp = getBitmap(photoToLoad.url);
						cache.put(photoToLoad.url, bmp);
						Object tag = photoToLoad.imageView.getTag();
						if (tag != null
								&& ((String) tag).equals(photoToLoad.url)) {
							BitmapDisplayer bd = new BitmapDisplayer(bmp,
									photoToLoad.imageView);
							Activity a = (Activity) photoToLoad.imageView
									.getContext();
							a.runOnUiThread(bd);
						}
					}
					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				// allow thread to exit
			}
		}
	}

	PhotosLoader photoLoaderThread = new PhotosLoader();

	/*
	 * This class display Image or Bitmap in UI Thread
	 */
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageView imageView;

		public BitmapDisplayer(Bitmap b, ImageView i) {
			bitmap = b;
			imageView = i;
		}

		public void run() {
			if (bitmap != null)
				imageView.setImageBitmap(bitmap);
			else
				imageView.setImageResource(stub_id);
		}
	}

	/*
	 * This function clear the memory cache if memory is full
	 */
	public void clearCache() {
		// clear memory cache
		cache.clear();
		// clear SD cache
		File[] files = cacheDir.listFiles();
		for (File f : files)
			f.delete();
	}

}
