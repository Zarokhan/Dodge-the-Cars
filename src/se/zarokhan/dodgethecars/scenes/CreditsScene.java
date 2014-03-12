package se.zarokhan.dodgethecars.scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
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

import android.graphics.Typeface;

import se.zarokhan.dodgethecars.GameManager;
import se.zarokhan.dodgethecars.SceneManager;
import se.zarokhan.dodgethecars.SceneManager.AllScenes;
import se.zarokhan.dodgethecars.mSoundManager;
import se.zarokhan.dodgethecars.scenes.stuff.WorldMap;

public class CreditsScene {
	
	private final static int HOME_BTN_ID = 0;
	
	private LayoutGameActivity activity;
	private Camera camera;
	private SceneManager sceneManager;
	private mSoundManager sounds;
	private WorldMap map;
	private org.andengine.entity.scene.menu.MenuScene scene;
	
	private BuildableBitmapTextureAtlas TA, TA2;
	private TextureRegion creditsTR, homeTR, textTR;
	private Font font;
	
	public CreditsScene(LayoutGameActivity activity, Camera camera, SceneManager sceneManager, mSoundManager sounds) {
		this.activity = activity;
		this.camera = camera;
		this.sceneManager = sceneManager;
		this.sounds = sounds;
		
		map = new WorldMap(activity, camera, 0);
	}
	
	public void loadResources(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		TA = new BuildableBitmapTextureAtlas(this.activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		TA2 = new BuildableBitmapTextureAtlas(this.activity.getTextureManager(), 1024, 1024, BitmapTextureFormat.RGBA_4444, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		map.loadResources(TA);
		
		creditsTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(TA, activity, "menu/credits.png");
		homeTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(TA, activity, "home.png");
		textTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(TA2, this.activity, "credits.png");
		
		font = FontFactory.create(this.activity.getFontManager(), this.activity.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), GameManager.lengthOfTile);
		font.load();
		
		try {
			TA.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(1, 1, 0));
			TA.load();
			
			TA2.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			TA2.load();
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
	}
	
	public org.andengine.entity.scene.menu.MenuScene createScene(){
		scene = new org.andengine.entity.scene.menu.MenuScene(camera);
		map.loadMap(scene);
		
		final Sprite credits = new Sprite((camera.getWidth()-creditsTR.getWidth())/2, GameManager.lengthOfTile * 2, creditsTR, this.activity.getVertexBufferObjectManager());
		final Sprite text = new Sprite((camera.getWidth()-textTR.getWidth())/2, GameManager.lengthOfTile * 4, textTR, this.activity.getVertexBufferObjectManager());
		
		final IMenuItem buttonHome = new ScaleMenuItemDecorator(new SpriteMenuItem(HOME_BTN_ID, homeTR, this.activity.getVertexBufferObjectManager()), 1, 1);
		buttonHome.setPosition(camera.getWidth()/2 - homeTR.getWidth()/2, camera.getHeight() - homeTR.getHeight()*2);
		scene.addMenuItem(buttonHome);
		
		scene.setOnMenuItemClickListener(new IOnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClicked(org.andengine.entity.scene.menu.MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
				
				switch(pMenuItem.getID()){
				case HOME_BTN_ID:
					sounds.playBlop();
					sceneManager.createMenuScene();
					sceneManager.setCurrentSence(AllScenes.MENU);
					break;
				}
				
				return false;
			}
		});
		
		scene.attachChild(credits);
		scene.attachChild(text);
		return scene;
	}

	public Scene getScene() {
		return scene;
	}
}
