

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;

public class GameStateJsonParser {

	public static final Integer ZWAARD = 0;
	public static final Integer RAKET = 1;
	public static final Integer VIRUS = 2;
	public static final Integer SCHILD = 3;
	public static final Integer RAKETSCHILD = 4;
	public static final Integer ANTIVIRUS = 5;
	public static final Integer PATENT_RAKET = 6;
	public static final Integer PATENT_RAKETSCHILD = 7;
	public static final Integer PATENT_VIRUS = 8;
	public static final Integer PATENT_ANTIVIRUS = 9;
	public static final Integer BOUWSTEEN = 10;

	public static final Integer INIT_PRICE_ZWAARD = 1;
	public static final Integer INIT_PRICE_RAKET = 2;
	public static final Integer INIT_PRICE_VIRUS = 3;
	public static final Integer INIT_PRICE_SCHILD = 2;
	public static final Integer INIT_PRICE_RAKETSCHILD = 4;
	public static final Integer INIT_PRICE_ANTIVIRUS = 6;
	public static final Integer INIT_PRICE_PATENT_RAKET = 5;
	public static final Integer INIT_PRICE_PATENT_RAKETSCHILD = 5;
	public static final Integer INIT_PRICE_PATENT_VIRUS = 10;
	public static final Integer INIT_PRICE_PATENT_ANTIVIRUS = 10;
	public static final Integer INIT_PRICE_BOUWSTEEN = 5;
	
	public static final int MAX_NUM_WEAPONS = 19;
	public static final int MAX_NUM_DEFENSE = 19;
	public static final int MAX_NUM_BOUWSTENEN = 10;

	// automatically changed
	public boolean gameIsFinished = false;
	public int currentRound = 0;
	public int numTeams = 8;
	

	public long roundTimeLimitMS = (5 * 60 + 30) * 1000;

	public Array<Array<String>> battleResultsFromLastRound; // first array is
															// attackers, second
															// array is
															// defenders
	public double priceFactor;
	public double relativePriceChange;
	
	public int resourcesAtStartOfLastRound;
	public int resourcesUsedInLastRound;
	public int resourcesNaturalGrowthInLastRound;
	public int resourcesDiscoveredInLastRound;
	public int resourcesFromEcoScienceInLastRound;
	public int resourcesAvailableInCurrentRound;
	public int resourcesToCountryInvestmentTrustInLastRound;

	public int totalHighTechAsiaInvestment;

	public Integer basePriceZwaard = INIT_PRICE_ZWAARD;
	public Integer basePriceRaket = INIT_PRICE_RAKET;
	public Integer basePriceVirus = INIT_PRICE_VIRUS;
	public Integer basePriceSchild = INIT_PRICE_SCHILD;
	public Integer basePriceRaketSchild = INIT_PRICE_RAKETSCHILD;
	public Integer basePriceAntivirus = INIT_PRICE_ANTIVIRUS;

	public IntIntMap towerHeightBeforeLastRound = new IntIntMap();
	public IntIntMap towerGrowthDuringLastRound = new IntIntMap();
	public IntIntMap towerHeightAfterLastRound = new IntIntMap();
	public IntIntMap prices = new IntIntMap();

	public IntIntMap numShieldsBeforeAttack = new IntIntMap();
	public IntIntMap numRocketShieldsBeforeAttack = new IntIntMap();
	public IntIntMap numAntiVirusesBeforeAttack = new IntIntMap();
	public IntIntMap numShieldsAfterAttack = new IntIntMap();
	public IntIntMap numRocketShieldsAfterAttack = new IntIntMap();
	public IntIntMap numAntiVirusesAfterAttack = new IntIntMap();
	public IntIntMap numSwordsAttackedBy = new IntIntMap();
	public IntIntMap numRocketsAttackedBy = new IntIntMap();
	public IntIntMap numVirusesAttackedBy = new IntIntMap();

	public IntIntMap conqueredBouwstenen = new IntIntMap();
	public IntIntMap lostBouwstenen = new IntIntMap();
	public IntIntMap purchasedBouwstenen = new IntIntMap();
	
	// attacks: 1st dimension: numTeams; 2nd dimension: numAttacks;
	public IntIntMap[][] attacks;
	public static final int ATTACKER_ID_FIELD = 0;
	public static final int DEFENDER_ID_FIELD = 1;
	public static final int NUM_SWORDS_FIELD = 2;
	public static final int NUM_ROCKETS_FIELD = 3;
	public static final int NUM_VIRUSES_FIELD = 4;
	public static final int RESULT_FIELD = 5;

	public GameStateJsonParser() {
	}

}
