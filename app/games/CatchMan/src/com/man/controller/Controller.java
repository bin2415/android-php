package com.man.controller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.os.Handler;

import com.man.SceneMenu;
import com.man.cfg.CFG;
import com.man.entity.Man;
import com.man.entity.PeopleMoveException;
import com.man.entity.UglyWoman;
import com.man.entity.Woman;
import com.man.entity.WomanFactory;
import com.man.plug.AlertGame;
import com.man.plug.MsgScore;
import com.man.util.GameUtil;
import com.man.view.GameView;

/**
 * ��Ϸ������
 */
public class Controller implements Runnable {

	/**
	 * ��Ϸ�Ƿ�����
	 */
	protected boolean live = false;
	
	/**
	 * ����
	 */
	protected Man man;

	/**
	 * Ů����
	 */
	private List<Woman> women;

	/**
	 * ����ͼƬ
	 */
	private Bitmap manImage = null;
	
	/**
	 * Ů��ͼƬ
	 */
	private Bitmap womanImage = null;
	
	/**
	 * ��Ϸ����
	 */
	private GameView gameView;

	/**
	 * Handler
	 */
	private Handler handler;

	/**
	 * Context
	 */
	private Context context;
	
	/**
	 * Handler
	 */
	private Activity activity;
	
	/**
	 * ʱ����ʾ��
	 */
	private MsgScore msgScore;

	/**
	 * ����һ��������
	 */
	public Controller(Activity activity) {
		this.handler = new Handler();
		this.activity = activity;
		this.context = activity;
	}

	/**
	 * ��Ϸʱ��<br/>
	 * ��λ����
	 */
	private float gameTime = 0;

	/**
	 * ����������
	 * 
	 * @param canvas
	 *            ����
	 */
	public void drawAll(Canvas canvas) {
		// �Ż����
		PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
		canvas.setDrawFilter(pfd);
		
		// ��������
		man.drawMe(canvas);

		// ����Ů����
		for (Woman woman : women) {
			woman.drawMe(canvas);
		}

		// ����ʱ����ʾ��
		msgScore.drawMe(canvas);
	}

	public void setMan(Man man) {
		this.man = man;
	}

	public void setWomen(List<Woman> women) {
		this.women = women;
	}

	public void setManImage(Bitmap bm) {
		this.manImage = bm;
	}
	
	public void setWomanImage(Bitmap bm) {
		this.womanImage = bm;
	}
	
	public void setGameView(GameView gameView) {
		this.gameView = gameView;
	}

	/**
	 * �������Ů���ƶ��ķ���
	 */
	@Override
	public void run() {
		handler.postDelayed(this, CFG.DELAY_TIME);

		// ��ʱ
		gameTime += CFG.DELAY_TIME / 1000.0;
		msgScore.setTime((int) gameTime);

		// �ƶ�����
		man.move();
		
		// ��Ҫ�Ƴ���
		List<Woman> removeWomen = null;
		
		// �ƶ�Ů����
		for (Woman woman : women) {
			try {
				woman.move();
			} catch (PeopleMoveException e) {
				// ������Ů�˳�ȥ��
				if (removeWomen == null) {
					removeWomen = new ArrayList<Woman>();
				}
				removeWomen.add(woman);
			}
		}

		// �Ƴ������
		if (removeWomen != null) {
			
			women.removeAll(removeWomen);
		}

		// ����Ӽ���
		if (women.size() < CFG.GAME_LEVEL[((int) (gameTime / 10))
				% CFG.GAME_LEVEL.length]) {
			int tmpCount = CFG.GAME_LEVEL[((int) (gameTime / 10))
					% CFG.GAME_LEVEL.length]
					- women.size();
			for (int i = 0; i < tmpCount; i++) {
				Woman woman = WomanFactory.getWoman();
				woman.setImage(womanImage);
				woman.lookPeople(man);
				women.add(woman);
			}
		}

		// �����ػ�
		gameView.redraw();

		// �����ײ
		checkCollide();
	}

	/**
	 * �����е���������ײ
	 */
	private void checkCollide() {
		// �����Ů����ײ����������Ϸ����
		for (Woman woman : women) {
			if (woman.collide(man)) {
				if (woman instanceof UglyWoman) {
//					pauseGame();
					endGame(true);
				}
			}
		}
	}

	/**
	 * ����Ϸ
	 */
	public void newGame() {
		// ��������
		man = new Man();
		man.setImage(manImage); // ����ͼƬ
		man.setLocation(new PointF(120, 150));

		// ����һ��Ů��
		women = new ArrayList<Woman>();
		Woman woman = WomanFactory.getWoman();
		woman.setImage(womanImage); // ����ͼƬ
		woman.lookPeople(man); // ��Ů�˿�������
		women.add(woman);

		// ��ʱ
		gameTime = 0;

		// ����һ��ʱ����ʾ��
		msgScore = new MsgScore();
		msgScore.setTime((int) gameTime);
		msgScore.setScore(GameUtil.getHighScore(activity));

		// ��ʼ
		startGame();
	}

	/**
	 * ��ʼ��Ϸ
	 */
	public void startGame() {
		handler.removeCallbacks(this);
		handler.post(this);
		live = true;
	}

	/**
	 * ��ͣ��Ϸ
	 */
	public void pauseGame() {
		handler.removeCallbacks(this);
		live = false;
	}
	
	/**
	 * ������Ϸ
	 * @param showDialog
	 */
	public void endGame(boolean showDialog) {
		// ��¼����
		int thisScore = (int) gameTime;
		int highScore = GameUtil.getHighScore(activity);
		if (thisScore > highScore) {
			highScore = thisScore;
			GameUtil.setHighScore(activity, thisScore);
		}
		
		handler.removeCallbacks(this);
		live = false;

		if (showDialog) {
			AlertGame alertGame = new AlertGame(context);
			alertGame.setScore(thisScore);
			alertGame.setHighScore(highScore);
			alertGame.setBtnCallback(new AlertGame.BtnCallback(){
				@Override
				public void onRestart() {
					newGame();
				}
				@Override
				public void onBack() {
					GameUtil.forward(activity, SceneMenu.class);
				}
			});
			alertGame.show();
		}
	}
	
	/**
	 * �Ƿ���������
	 * @return
	 */
	public boolean isLive() {
		return live;
	}
}
