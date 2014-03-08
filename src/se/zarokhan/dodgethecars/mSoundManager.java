package se.zarokhan.dodgethecars;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.LayoutGameActivity;

public class mSoundManager {
	
	private LayoutGameActivity activity;
	private Camera camera;
	
	private Sound blop, slide, crash, start;
	private Music music;
	
	private Scene scene;
	private Sprite musicOn, musicOff, soundOn, soundOff;
	
	// TEXTURE
	private BuildableBitmapTextureAtlas TA;
	private TextureRegion soundsOnTR, soundsOffTR, musicOnTR, musicOffTR;
	
	private Boolean playSounds = true;
	private Boolean playMusic = true;
	
	public mSoundManager(LayoutGameActivity activity, Camera camera){
		this.activity = activity;
		this.camera = camera;
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
		music.setVolume(0.7f);
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		TA = new BuildableBitmapTextureAtlas(this.activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		soundsOnTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(TA, this.activity, "sound on.png");
		soundsOffTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(TA, this.activity, "sound off.png");
		musicOnTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(TA, this.activity, "music on.png");
		musicOffTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(TA, this.activity, "music off.png");
		
	}
	
	public Scene createScene(){
		float screenWidth = camera.getHeight();
		float screenHeight = camera.getWidth();
		scene = new Scene();
		// SCENE SETUP
		scene.setRotation(270);
		scene.setPosition(0, camera.getHeight());
		
		
		
		return scene;
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
