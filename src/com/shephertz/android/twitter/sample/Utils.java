package com.shephertz.android.twitter.sample;

import java.io.InputStream;
import java.io.OutputStream;

/*
 * @author Vishnu Garg
 */
public class Utils {
	
	 static boolean stop_Ad = false;
	 static long refreshTime = 0;
	 static boolean appClose = false;
	 static boolean frstAd = true;


	/*
	 * This function Copy InputStream to OutputStream
	 * @param InputStream is
	 * @param OutputStream os
	 */
	 static void CopyStream(InputStream is, OutputStream os) {
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