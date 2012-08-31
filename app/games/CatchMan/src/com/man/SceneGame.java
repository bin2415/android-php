package com.man;

import com.man.controller.ControllerKey;
import com.man.view.GameView;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

/**
 * ��activity��
 */
public class SceneGame extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.newGame:
				controller.newGame();
				break;
			case R.id.resumeGame:
				if (!controller.isLive()){
					controller.startGame();
				}
				break;
			case R.id.quitGame:
				this.finish();
				break;
			default:
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_MENU:
				if (controller.isLive()){
					controller.pauseGame();
				}
				break;
			case KeyEvent.KEYCODE_BACK:
				break;
			default:
				break;
		}
		return false;
	}
	
	/**
	 * ������
	 */
	private ControllerKey controller;

	/**
	 * ��Ϸ���
	 */
	private GameView gameView;

	private PowerManager.WakeLock mWakeLock;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initActivity();//ϵͳ��ʼ��
		initGame(); //��Ϸ��ʼ��
		setContentView(gameView);
	}

	/**
	 * ϵͳ��ʼ��
	 */
	private void initActivity() {
		// �����ޱ���
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ����ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//��Ļ��������Ĵ���
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = pm.newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		
	}

	/**
	 * ��Ϸ��ʼ��
	 */
	private void initGame() {
		// �����������������󲢹���
		controller = new ControllerKey(this);
		
		gameView = new GameView(this);
		controller.setGameView(gameView);
		
		Resources res = getResources();
		Bitmap bmw1 = BitmapFactory.decodeResource(res, R.drawable.w1);
		Bitmap bmw2 = BitmapFactory.decodeResource(res, R.drawable.w2);
		controller.setWomanImage(bmw1);
		controller.setManImage(bmw2);
		
		gameView.setController(controller);
		
		// ע�����¼�
		gameView.setOnTouchListener(controller);
		
		// ����Ϸ
		controller.newGame();
	}
	
	@Override
	protected void onStop() {
		controller.endGame(false);
		super.onStop();
	}
	
	@Override
	protected void onPause() {
		mWakeLock.release();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mWakeLock.acquire();
		super.onResume();
	}
	
	public Context getContext () {
		return this;
	}
}