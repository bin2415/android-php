package com.man.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public final class GameUtil {
	
	public static void setHighScore(Activity activity, int score) {
		SharedPreferences settings = activity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("highScore", "" + score);
		editor.commit();
	}
	
	public static int getHighScore(Activity activity) {
		SharedPreferences settings = activity.getPreferences(Context.MODE_PRIVATE);
		String highScore = settings.getString("highScore", "0");
		return Integer.parseInt(highScore);
	}
	
	public static void forward(Activity activity, Class<?> target) {
		Intent intent = new Intent();
		intent.setClass(activity, target);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
		// �ر��л�����Ч��
		activity.overridePendingTransition(0, 0);
		activity.finish();
	}
}