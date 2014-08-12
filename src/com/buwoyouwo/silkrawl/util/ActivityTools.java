package com.buwoyouwo.silkrawl.util;

import android.content.Context;
import android.content.Intent;

public class ActivityTools {
	public static void jumpDirect(Context context, Class activityClass){
		Intent intent = new Intent(context, activityClass);
		context.startActivity(intent);
	}
}
