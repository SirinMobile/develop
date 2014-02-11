package com.uiu.camera.orangeil.friendly.util;

import com.uiu.util.CursorAnalyzer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MessageThreadObserver
{
	
	private ContentResolver contentResolver = null;
	private static Handler threadhandler = null;
	private ContentObserver threadObserver = null;
	public Uri uriToObserve;
	public boolean monitorStatus;
	int threadCount = 0;
	String code;
	Context mContext;
	

	public MessageThreadObserver(final ContentResolver contentResolver, final Context mainContext, final Uri uri, Handler handler)
	{
		uriToObserve = uri;
		mContext = mainContext;
		this.contentResolver = contentResolver;
		threadhandler = handler;
		threadObserver = new ThreadObserver(threadhandler);
		
		monitorStatus = false;
		System.out.println("ThreadMonitor :: ***** Start Thread Monitor *****");
	}

	public void startThreadMonitoring()
	{
		try
		{
			if (!monitorStatus)
			{
				mContext.getContentResolver().registerContentObserver(uriToObserve, true, threadObserver);
				String[] projection = { "path", "time", "source_created" };
				String[] selectionArgs = { "device" };
				Cursor threadCur = contentResolver.query(uriToObserve, projection, "source_created = ?", selectionArgs, null);
				
				if (threadCur != null && threadCur.getCount() > 0)
				{
					// Number of SMS
					threadCount = threadCur.getCount();
					System.out.println("ThreadMonitor :: Init ThreadCount ==" + threadCount);
				}
				monitorStatus = true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("ThreadMonitor :: startThreadMonitoring Exception== " + e.getMessage());
		}
	}

	public void stopThreadMonitoring()
	{
		try
		{
			if (!monitorStatus)
			{
				monitorStatus = false;
				contentResolver.unregisterContentObserver(threadObserver);
			}
		}
		catch (Exception e)
		{
			System.out.println("ThreadMonitor :: stopThreadMonitoring Exception == " + e.getMessage());
		}
	}

	// A Handler allows you to send and process Message and Runnable
	// objects associated with a thread's MessageQueue.
	class ThreadObserver extends ContentObserver
	{
		private Handler thread_handle = null;

		public ThreadObserver(final Handler threadhandle)
		{
			super(threadhandle);
			thread_handle = threadhandle;
		}

		public void onChange(final boolean bSelfChange)
		{
			super.onChange(bSelfChange);
			Cursor threadCur = null;
			try
			{
				monitorStatus = true;

				// Send message to Activity
				Message msg = new Message();
				
				// Getting the Thread count
				String[] projection = { "path" };
				String[] selectionArgs = { "device" };
				threadCur = contentResolver.query(uriToObserve, projection, "source_created = ?", selectionArgs, "time DESC");
				//threadCur = contentResolver.query(uriToObserve, new String[]{"_id","type", "body"}, null, null,"normalized_date DESC");
				
				int currThreadCount = 0;
				if (threadCur != null && threadCur.getCount() > 0)
				{
					currThreadCount = threadCur.getCount();
				}
				
				if (currThreadCount > threadCount)
				{
					threadhandler.sendMessage(msg);
					threadCount = currThreadCount;
				}
				threadCur.close();

			}
			catch (Exception e)
			{
				System.out.println("ThreadMonitor Exception:: " + e.getMessage());
			}
			finally
			{
				if(threadCur != null && !threadCur.isClosed())
					threadCur.close();
			}

		}
	}

}
