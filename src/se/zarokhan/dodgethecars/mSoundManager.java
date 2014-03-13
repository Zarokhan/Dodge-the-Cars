package se.zarokhan.dodgethecars;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.ui.activity.LayoutGameActivity;

public class mSoundManager {
	private LayoutGameActivity activity;
	
	private Sound blop, slide, crash, start;
	private Music music;
	public boolean playSounds, playMusic;
	
	// TEXTURE
	
	public mSoundManager(LayoutGameActivity activity){
		this.activity = activity;
		
		playSounds = UserData.getInstance().getSoundEnabled();
		playMusic = UserData.getInstance().getMusicEnabled();
	}
	
	public void loadResources(){
		MusicFactory.setAssetBasePath("sfx/");
		SoundFactory.setAssetBasePath("sfx/");
		
		try {
			blop = SoundFactory.createSoundFromAsset(this.activity.getSoundManager(), activity, "blop.mp3");
			slide = SoundFactory.createSoundFromAsset(this.activity.getSoundManager(), activity, "carslide.mp3");
			crash = SoundFactory.createSoundFromAsset(this.activity.getSoundManager(), activity, "carcrash.mp3");
			start = SoundFactory.createSoundFromAsset(this.activity.getSoundManager(), activity, "carstart.mp3");
			music = MusicFactory.createMusicFromAsset(this.activity.getMusicManager(), activity, "Surprises In A Can.mp3");
		} catch (IOException e) {
			e.printStackTrace();
		}
		music.setVolume(0.5f);
	}
	
	public void setMusicEnable(boolean musicEnable) {
		playMusic = musicEnable;
		UserData.getInstance().setMusicEnable(musicEnable);
	}
	
	public void setSoundsEnable(boolean soundsEnable) {
		playSounds = soundsEnable;
		UserData.getInstance().setSoundEnable(soundsEnable);
	}
	
	public void playCrash() {
		if (playSounds)
			crash.play();
	}

	public void playSlide() {
		if (playSounds)
			slide.play();
	}

	public void playCarStart() {
		if (playSounds)
			start.play();
	}

	public void playBlop() {
		if (playSounds)
			blop.play();
	}

	public void playMusic() {
		if (playMusic)
		music.play();
	}
	
	public void pauseMusic() {
		music.pause();
	}
	
	public void pause() {
		music.pause();
		//playSounds = !playSounds;
	}
	
	public boolean isMusicNull(){
		return (music == null);
	}
	
	public boolean isMusicPlaying(){
		return music.isPlaying();
	}

}
