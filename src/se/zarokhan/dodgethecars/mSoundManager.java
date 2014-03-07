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
	
	private Boolean playSounds = true;
	
	public mSoundManager(LayoutGameActivity activity){
		this.activity = activity;
	}
	
	public void loadSoundResources(){
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
		
		music.setVolume(0.7f);
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
		music.play();
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
