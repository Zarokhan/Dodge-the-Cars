package se.zarokhan.dodgethecars;

import android.content.Context;
import android.content.SharedPreferences;

public class UserData {
	
	private static UserData INSTANCE;
	
	private static final String PREFS_NAME = "GAME_USERDATA";
	
	private static final String HIGHSCORE_KEY = "highscoreKey";
	private static final String SOUND_KEY = "soundKey";
	private static final String MUSIC_KEY = "musicKey";
	
	private SharedPreferences mSettings;
	private SharedPreferences.Editor mEditor;
	
	private int highestScore;
	private boolean soundEnable, musicEnable;
	
	public static UserData getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new UserData();
		}
		return INSTANCE;
	}
	
	public synchronized void init(Context context) {
		if (mSettings == null) {
		mSettings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		mEditor = mSettings.edit();

		highestScore = mSettings.getInt(HIGHSCORE_KEY, 0);
		soundEnable = mSettings.getBoolean(SOUND_KEY, true);
		musicEnable = mSettings.getBoolean(MUSIC_KEY, true);
		}
	}
	
	public synchronized void setSoundEnable(boolean soundEnable) {
		this.soundEnable = soundEnable;
		mEditor.putBoolean(SOUND_KEY, this.soundEnable);
		mEditor.commit();
	}
	
	public synchronized void setMusicEnable(boolean musicEnable){
		this.musicEnable = musicEnable;
		mEditor.putBoolean(MUSIC_KEY, this.musicEnable);
		mEditor.commit();
	}
	
	public synchronized void setHighestScore(int score) {
		highestScore = score;
		mEditor.putInt(HIGHSCORE_KEY, highestScore);
		mEditor.commit();
	}
	
	public synchronized int getHighestScore() {
		return highestScore;
	}
	
	public synchronized boolean getMusicEnabled() {
		return musicEnable;
	}
	
	public synchronized boolean getSoundEnabled() {
		return soundEnable;
	}
}
