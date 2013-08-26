package com.shephertz.android.twitter.sample;

import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
	
	public static boolean stop_Ad = false;
	public static long refreshTime = 0;
	public static boolean appClose = false;
	public static boolean frstAd = true;


	
	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

}