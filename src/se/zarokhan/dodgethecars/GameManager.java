package se.zarokhan.dodgethecars;

public class GameManager {

	private static GameManager INSTANCE;
	
	public static final String GAME_VERSION = "ALPHA";
	public static final int lengthOfTile = 135;
	
	public static final int INITIAL_PHASE = 0;
	public static final int INITIAL_SCORE = 0;
	public static final int INITIAL_HEALTH = 3;
	public static final int INITIAL_ENEMIES = 5;
	public static final int INITIAL_ENEMYPASSED = 0;
	public static final int INITIAL_ENEMYSPAWNED = 0;
	
	public static final int POINTS_FOR_STANDARDENEMY = 10;
	
	private int phase = INITIAL_PHASE;
	private int health = INITIAL_HEALTH;
	private int score = INITIAL_SCORE;
	private int enemyPassed = INITIAL_ENEMYPASSED;
	private int enemySpawned = INITIAL_ENEMYSPAWNED;
	
	private boolean updateScoreText = false;
	
	public static GameManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GameManager();
		}
		return INSTANCE;
	}
	
	public int getScore(){
		return score;
	}
	
	public int getHealth(){
		return health;
	}
	
	public int getPhase(){
		return phase;
	}
	
	public int getEnemyPassed(){
		return enemyPassed;
	}
	
	public int getEnemySpawned(){
		return enemySpawned;
	}
	
	public void removeHealth(){
		health--;
	}
	
	public void addScore(int value){
		score += value;
	}
	
	public void enemyPassed(){
		enemyPassed++;
	}
	
	public void enemySpawned(){
		enemySpawned++;
	}
	
	public void resetGame(){
		phase = INITIAL_PHASE;
		health = INITIAL_HEALTH;
		score = INITIAL_SCORE;
		enemyPassed = INITIAL_ENEMYPASSED;
		enemySpawned = INITIAL_ENEMYSPAWNED;
	}

	public void setUpdateScoreText(boolean b) {
		updateScoreText = b;
	}
	
	public boolean getUpdateScoreText(){
		return updateScoreText;
	}
}
