package com.app.NAMESPACE.app;

import java.util.HashMap;

import com.app.NAMESPACE.R;
import com.app.NAMESPACE.auth.AuthApp;
import com.app.NAMESPACE.base.BaseMessage;
import com.app.NAMESPACE.base.C;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class AppWrite extends AuthApp {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.app_write);
		// show keyboard
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		findViewById(R.id.app_write_submit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText mWriteText = (EditText) findViewById(R.id.app_write_text);
				HashMap<String, String> urlParams = new HashMap<String, String>();
				urlParams.put("content", mWriteText.getText().toString());
				doTaskAsync(C.task.blogCreate, C.api.blogCreate, urlParams);
			}
		});
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// async task callback methods
	
	@Override
	public void onTaskComplete(int taskId, BaseMessage message) {
		super.onTaskComplete(taskId, message);
		doFinish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			doFinish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
}