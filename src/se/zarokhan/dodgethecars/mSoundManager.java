package se.zarokhan.dodgethecars;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;

import se.zarokhan.dodgethecars.SceneManager.AllScenes;

import android.content.SharedPreferences;

public class mSoundManager {
	
	private static final int MUSIC_ON_BTN_ID = 0, SOUND_ON_BTN_ID = 1, MUSIC_OFF_BTN_ID = 2, SOUND_OFF_BTN_ID = 3;
	
	private static final String PREFS_NAME = "GAME_SOUNDS_DATA";
	
	private static final String SOUND_KEY = "soundKey";
	private static final String MUSIC_KEY = "musicKey";
	
	private SharedPreferences mSettings;
	private SharedPreferences.Editor mEditor;
	
	private LayoutGameActivity activity;
	private Camera camera;
	
	private Sound blop, slide, crash, start;
	private Music music;
	
	private MenuScene scene;
	private Sprite musicOn, musicOff, soundOn, soundOff;
	
	private boolean mSoundEnable, mMusicEnable;
	
	// TEXTURE
	private TextureRegion soundsOnTR, soundsOffTR, musicOnTR, musicOffTR;
	
	public Boolean playSounds = true;
	public Boolean playMusic = true;
	
	public mSoundManager(LayoutGameActivity activity, Camera camera){
		this.activity = activity;
		this.camera = camera;
	}
	
	public void loadResources(BuildableBitmapTextureAtlas TA){
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
