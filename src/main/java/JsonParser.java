import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.StringBuilder;

public class JsonParser {
	
	public static final int NUM_SESSIONS = 6;
	public static final int NUM_ROUNDS = 8;
	private static final int NUM_TEAMS = 8;
	private static final int NUM_ATTACKS_FIELDS = 6;
	private static final int NUM_ATTACKS = 2;
	
	GameStateJsonParser[][] gameState;
	
	String[] inputFolderNames = {
			"C:\\Users\\Rens\\surfdrive\\rens-developer\\Parsifal app\\Session1_Fri_May_29_13-59-12_CEST_2015\\",
			"C:\\Users\\Rens\\surfdrive\\rens-developer\\Parsifal app\\Session2_Fri_May_29_18-51-53_CEST_2015\\",
			"C:\\Users\\Rens\\surfdrive\\rens-developer\\Parsifal app\\Session3_Fri_May_29_21-37-14_CEST_2015\\",
			"C:\\Users\\Rens\\surfdrive\\rens-developer\\Parsifal app\\Session4_Sat_May_30_14-13-32_CEST_2015\\",
			"C:\\Users\\Rens\\surfdrive\\rens-developer\\Parsifal app\\Session5_Sat_May_30_19-09-36_CEST_2015\\",
			"C:\\Users\\Rens\\surfdrive\\rens-developer\\Parsifal app\\Session6_Sat_May_30_21-43-52_CEST_2015\\"
	};
	
	String outputFileName = 
			"C:\\Users\\Rens\\surfdrive\\rens-developer\\Parsifal app\\all-sessions-2015.xls";
	
	// workbook
	FileOutputStream outFile;
	Workbook wb = new HSSFWorkbook();	
	Sheet battleResultsSheet = wb.createSheet("battle results");
	Sheet attacksSheet = wb.createSheet("attacks");
	Sheet resourcesSheet = wb.createSheet("resources");
	Sheet towersSheet = wb.createSheet("towers");
	Sheet pricesSheet = wb.createSheet("prices");
	Sheet weaponsSheet = wb.createSheet("weapons");
	Sheet buildingBlocksSheet = wb.createSheet("building blocks");
	
	// data rows
	Row resourcesAtStartOfLastRoundRow;
	Row resourcesAvailableInCurrentRoundRow;
	Row resourcesDiscoveredInLastRoundRow;
	Row resourcesFromEcoScienceInLastRoundRow;
	Row resourcesNaturalGrowthInLastRoundRow;
	Row resourcesToCountryInvestmentTrustInLastRoundRow;
	Row resourcesUsedInLastRoundRow;
	Row[] attacksRows;
	Row[] battleResultsFromLastRoundRows;
	Row towerHeightBeforeLastRoundRow;
	Row towerGrowthDuringLastRoundRow;
	Row towerHeightAfterLastRoundRow;
	Row priceFactorRow;
	Row relativePriceChangeRow;
	Row pricesRow;
	Row[] weaponsRows;
	Row[] buildingBlocksRows;

	
	// header rows
	Row[] resourcesSheetSessionHeaders;
	Row[] attacksHeaders;
	Row[] battleResultsHeaders;
	Row[] weaponsHeaders;
	Row[] buildingBlocksHeaders;

	public JsonParser() {
		 try {
			outFile = new FileOutputStream(outputFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private GameStateJsonParser[][] readJsonFile() {
		
		GameStateJsonParser[][] gameState = new GameStateJsonParser[NUM_SESSIONS][];
		for(int i = 0; i < NUM_SESSIONS; i++)
			gameState[i] = new GameStateJsonParser[NUM_ROUNDS];

		for(int sessionID = 0; sessionID < NUM_SESSIONS; sessionID++) {
			for(int roundID = 0; roundID < NUM_ROUNDS; roundID++) {

				String fileName = inputFolderNames[sessionID] + "state" + roundID + ".json";

				try {
					FileReader fileReader = new FileReader(fileName);
					BufferedReader reader = new BufferedReader(fileReader);
					String         line = null;
					StringBuilder data = new StringBuilder();
					Json jsonInstance = new Json();

					try {
						while( ( line = reader.readLine() ) != null )
							data.append(line + '\n');
					} finally {
						reader.close();
					}
					
					gameState[sessionID][roundID] = jsonInstance.fromJson(GameStateJsonParser.class, data.toString());					
				} catch(Exception e) {
					
				}
			}
		}
		
		return(gameState);
	}

	private void generateGenericHeaders() {
		// headers in resources sheet
		resourcesSheet.createRow(0).createCell(0).setCellValue("Resources at start of last round");
		resourcesSheet.createRow(1*(NUM_ROUNDS + 3)).createCell(0).setCellValue("Resources available in current round");
		resourcesSheet.createRow(2*(NUM_ROUNDS + 3)).createCell(0).setCellValue("Resources discovered in last round");
		resourcesSheet.createRow(3*(NUM_ROUNDS + 3)).createCell(0).setCellValue("Resources from eco science in last round");
		resourcesSheet.createRow(4*(NUM_ROUNDS + 3)).createCell(0).setCellValue("Resources natural growth in last round");
		resourcesSheet.createRow(5*(NUM_ROUNDS + 3)).createCell(0).setCellValue("Resources to country investment trust in last round");
		resourcesSheet.createRow(6*(NUM_ROUNDS + 3)).createCell(0).setCellValue("Resources used in last round");
		
		resourcesSheetSessionHeaders = new Row[7];
		for(int resourcesDataID = 0; resourcesDataID < 7; resourcesDataID++)
			resourcesSheetSessionHeaders[resourcesDataID] = resourcesSheet.createRow(1 + resourcesDataID * (NUM_ROUNDS + 3));
		
		// towers sheet
		towersSheet.createRow(0).createCell(0).setCellValue("Tower height before last round");
		towersSheet.createRow(NUM_ROUNDS + 4).createCell(0).setCellValue("Tower growth during last round");
		towersSheet.createRow(2 * (NUM_ROUNDS + 4)).createCell(0).setCellValue("Tower height after last round");
		
		//prices
		pricesSheet.createRow(0).createCell(0).setCellValue("Price factor");
		pricesSheet.createRow(NUM_ROUNDS + 3).createCell(0).setCellValue("Relative price change");
		pricesSheet.createRow(2 * (NUM_ROUNDS + 3)).createCell(0).setCellValue("Price table");
	}
	
	private void newDataRows(int roundID) {
		// create new data rows for this round
		resourcesAtStartOfLastRoundRow = resourcesSheet.createRow(roundID+2);
		resourcesAvailableInCurrentRoundRow = resourcesSheet.createRow(roundID+2+1*(NUM_ROUNDS+3));
		resourcesDiscoveredInLastRoundRow = resourcesSheet.createRow(roundID+2+2*(NUM_ROUNDS+3));
		resourcesFromEcoScienceInLastRoundRow = resourcesSheet.createRow(roundID+2+3*(NUM_ROUNDS+3));
		resourcesNaturalGrowthInLastRoundRow = resourcesSheet.createRow(roundID+2+4*(NUM_ROUNDS+3));
		resourcesToCountryInvestmentTrustInLastRoundRow = resourcesSheet.createRow(roundID+2+5*(NUM_ROUNDS+3));
		resourcesUsedInLastRoundRow = resourcesSheet.createRow(roundID+2+6*(NUM_ROUNDS+3));
		
		attacksRows = new Row[NUM_TEAMS];
		for(int teamID = 0; teamID < NUM_TEAMS; teamID++){
			int rowNumber = 2 + (NUM_TEAMS + 3) * roundID + teamID;
			attacksRows[teamID] = attacksSheet.createRow(rowNumber);
		}

		battleResultsFromLastRoundRows = new Row[NUM_TEAMS];
		for(int teamID = 0; teamID < NUM_TEAMS; teamID++){
			int rowNumber = 3 + (NUM_TEAMS + 4) * roundID + teamID;
			battleResultsFromLastRoundRows[teamID] = battleResultsSheet.createRow(rowNumber);
		}
		
		towerHeightBeforeLastRoundRow = towersSheet.createRow(roundID + 3);
		towerGrowthDuringLastRoundRow = towersSheet.createRow(roundID + 3 + (NUM_ROUNDS + 4));
		towerHeightAfterLastRoundRow = towersSheet.createRow(roundID + 3 + 2 * (NUM_ROUNDS + 4));
		
		priceFactorRow = pricesSheet.createRow(roundID + 2);
		relativePriceChangeRow = pricesSheet.createRow(roundID + 2 + (NUM_ROUNDS + 3));
		pricesRow = pricesSheet.createRow(roundID + 2 + 2 * (NUM_ROUNDS + 3) + 1);

		weaponsRows = new Row[NUM_TEAMS];
		for(int teamID = 0; teamID < NUM_TEAMS; teamID++){
			int rowNumber = 2 + (NUM_TEAMS + 3) * roundID + teamID;
			weaponsRows[teamID] = weaponsSheet.createRow(rowNumber);
		}

		buildingBlocksRows = new Row[NUM_TEAMS];
		for(int teamID = 0; teamID < NUM_TEAMS; teamID++){
			int rowNumber = 2 + (NUM_TEAMS + 3) * roundID + teamID;
			buildingBlocksRows[teamID] = buildingBlocksSheet.createRow(rowNumber);
		}
}
	
	private void generateColumnHeaders(int roundID) {
		// create headers
		attacksHeaders = new Row[2];
		attacksHeaders[0] = attacksSheet.createRow((NUM_TEAMS + 3) * roundID);
		attacksHeaders[1] = attacksSheet.createRow((NUM_TEAMS + 3) * roundID + 1);
		
		for(int sessionID = 0; sessionID < NUM_SESSIONS; sessionID++) {
			attacksHeaders[0].createCell(3 + (2*NUM_ATTACKS_FIELDS + 4) * sessionID).setCellValue("Session " + (sessionID + 1));
			attacksHeaders[1].createCell(3 + (2*NUM_ATTACKS_FIELDS + 4) * sessionID + 0).setCellValue("AttackerID");
			attacksHeaders[1].createCell(3 + (2*NUM_ATTACKS_FIELDS + 4) * sessionID + 1).setCellValue("DefenderID1");
			attacksHeaders[1].createCell(3 + (2*NUM_ATTACKS_FIELDS + 4) * sessionID + 2).setCellValue("Number of swords");
			attacksHeaders[1].createCell(3 + (2*NUM_ATTACKS_FIELDS + 4) * sessionID + 3).setCellValue("Number of Rockets");
			attacksHeaders[1].createCell(3 + (2*NUM_ATTACKS_FIELDS + 4) * sessionID + 4).setCellValue("Number of Viruses");
			attacksHeaders[1].createCell(3 + (2*NUM_ATTACKS_FIELDS + 4) * sessionID + 5).setCellValue("Result");
			attacksHeaders[1].createCell(3 + (2*NUM_ATTACKS_FIELDS + 4) * sessionID + 6).setCellValue("AttackerID");
			attacksHeaders[1].createCell(3 + (2*NUM_ATTACKS_FIELDS + 4) * sessionID + 7).setCellValue("DefenderID2");
			attacksHeaders[1].createCell(3 + (2*NUM_ATTACKS_FIELDS + 4) * sessionID + 8).setCellValue("Number of swords");
			attacksHeaders[1].createCell(3 + (2*NUM_ATTACKS_FIELDS + 4) * sessionID + 9).setCellValue("Number of Rockets");
			attacksHeaders[1].createCell(3 + (2*NUM_ATTACKS_FIELDS + 4) * sessionID + 10).setCellValue("Number of Viruses");
			attacksHeaders[1].createCell(3 + (2*NUM_ATTACKS_FIELDS + 4) * sessionID + 11).setCellValue("Result");
		}
		
		battleResultsHeaders = new Row[3];
		battleResultsHeaders[0] = battleResultsSheet.createRow((NUM_TEAMS + 4) * roundID);
		battleResultsHeaders[1] = battleResultsSheet.createRow((NUM_TEAMS + 4) * roundID + 1);
		battleResultsHeaders[2] = battleResultsSheet.createRow((NUM_TEAMS + 4) * roundID + 2);
		
		for(int sessionID = 0; sessionID < NUM_SESSIONS; sessionID++) {
			battleResultsHeaders[0].createCell(3 + (NUM_TEAMS + 4) * sessionID).setCellValue("Session " + (sessionID + 1));
			battleResultsHeaders[1].createCell(3 + (NUM_TEAMS + 4) * sessionID).setCellValue("Defenders");
			for(int teamNumber = 0; teamNumber < NUM_TEAMS; teamNumber++)
				battleResultsHeaders[2].createCell(3 + (NUM_TEAMS + 4) * sessionID + teamNumber).setCellValue("Team " + (teamNumber + 1));
		}		

		// resources sheet
		if(roundID == 0)
			for(int resourcesDataID = 0; resourcesDataID < 7; resourcesDataID++)
				for(int sessionID = 0; sessionID < NUM_SESSIONS; sessionID++)
					resourcesSheetSessionHeaders[resourcesDataID].createCell(1 + sessionID).setCellValue("Session " + (sessionID + 1));

		// towers sheet
		if(roundID == 0) {
			for(int towerData = 0; towerData < 3; towerData++) {
				Row towersSheetHeader1 = towersSheet.createRow(1 + towerData * (NUM_ROUNDS + 4)); 
				Row towersSheetHeader2 = towersSheet.createRow(2 + towerData * (NUM_ROUNDS + 4)); 
				for(int sessionID = 0; sessionID < NUM_SESSIONS; sessionID++) {
					towersSheetHeader1.createCell(1 + (NUM_TEAMS + 2) * sessionID).setCellValue("Session " + (sessionID + 1));
					for(int teamID = 0; teamID < NUM_TEAMS; teamID++)
						towersSheetHeader2.createCell(1 + (NUM_TEAMS + 2) * sessionID + teamID).setCellValue("Team " + (teamID + 1));
				}
			}
		}

		// prices sheet
		if(roundID == 0) {
			Row priceFactorHeaders = pricesSheet.createRow(1);
			Row relativePriceChangeHeaders = pricesSheet.createRow(1 + (NUM_ROUNDS + 3));
			Row pricesTableHeaders1 = pricesSheet.createRow(1 + 2 * (NUM_ROUNDS + 3)); 
			Row pricesTableHeaders2 = pricesSheet.createRow(2 + 2 * (NUM_ROUNDS + 3)); 
			for(int sessionID = 0; sessionID < NUM_SESSIONS; sessionID++) {
				priceFactorHeaders.createCell(1 + sessionID).setCellValue("Session " + (sessionID + 1));
				relativePriceChangeHeaders.createCell(1 + sessionID).setCellValue("Session " + (sessionID + 1));
				pricesTableHeaders1.createCell(1 + (11 + 2) * sessionID).setCellValue("Session " + (sessionID + 1));
				pricesTableHeaders2.createCell(1 + (11 + 2) * sessionID + 0).setCellValue("Sword");
				pricesTableHeaders2.createCell(1 + (11 + 2) * sessionID + 1).setCellValue("Rocket");
				pricesTableHeaders2.createCell(1 + (11 + 2) * sessionID + 2).setCellValue("Virus");
				pricesTableHeaders2.createCell(1 + (11 + 2) * sessionID + 3).setCellValue("Shield");
				pricesTableHeaders2.createCell(1 + (11 + 2) * sessionID + 4).setCellValue("Rocket shield");
				pricesTableHeaders2.createCell(1 + (11 + 2) * sessionID + 5).setCellValue("Anti virus");
				pricesTableHeaders2.createCell(1 + (11 + 2) * sessionID + 6).setCellValue("Patent rocket");
				pricesTableHeaders2.createCell(1 + (11 + 2) * sessionID + 7).setCellValue("Patent rocket shield");
				pricesTableHeaders2.createCell(1 + (11 + 2) * sessionID + 8).setCellValue("Patent virus");
				pricesTableHeaders2.createCell(1 + (11 + 2) * sessionID + 9).setCellValue("Patent anti virus");
				pricesTableHeaders2.createCell(1 + (11 + 2) * sessionID + 10).setCellValue("Building block");
			}
		}

		// weapons sheet
		weaponsHeaders = new Row[2];
		weaponsHeaders[0] = weaponsSheet.createRow((NUM_TEAMS + 3) * roundID);
		weaponsHeaders[1] = weaponsSheet.createRow((NUM_TEAMS + 3) * roundID + 1);
		
		for(int sessionID = 0; sessionID < NUM_SESSIONS; sessionID++) {
			weaponsHeaders[0].createCell(2 + (9 + 3) * sessionID).setCellValue("Session " + (sessionID + 1));
			weaponsHeaders[1].createCell(2 + (9 + 3) * sessionID + 0).setCellValue("Number of shields before attack");
			weaponsHeaders[1].createCell(2 + (9 + 3) * sessionID + 1).setCellValue("Number of rocket shields before attack");
			weaponsHeaders[1].createCell(2 + (9 + 3) * sessionID + 2).setCellValue("Number of anti viruses before attack");
			weaponsHeaders[1].createCell(2 + (9 + 3) * sessionID + 3).setCellValue("Number of shields after attack");
			weaponsHeaders[1].createCell(2 + (9 + 3) * sessionID + 4).setCellValue("Number of rocket shields after attack");
			weaponsHeaders[1].createCell(2 + (9 + 3) * sessionID + 5).setCellValue("Number of anti viruses after attack");
			weaponsHeaders[1].createCell(2 + (9 + 3) * sessionID + 6).setCellValue("Number of swords attacked by");
			weaponsHeaders[1].createCell(2 + (9 + 3) * sessionID + 7).setCellValue("Number of rockets attacked by");
			weaponsHeaders[1].createCell(2 + (9 + 3) * sessionID + 8).setCellValue("Number of viruses attacked by");
		}

		// building blocks sheet
		buildingBlocksHeaders = new Row[2];
		buildingBlocksHeaders[0] = buildingBlocksSheet.createRow((NUM_TEAMS + 3) * roundID);
		buildingBlocksHeaders[1] = buildingBlocksSheet.createRow((NUM_TEAMS + 3) * roundID + 1);
		
		for(int sessionID = 0; sessionID < NUM_SESSIONS; sessionID++) {
			buildingBlocksHeaders[0].createCell(2 + (3 + 3) * sessionID).setCellValue("Session " + (sessionID + 1));
			buildingBlocksHeaders[1].createCell(2 + (3 + 3) * sessionID + 0).setCellValue("Building blocks captured");
			buildingBlocksHeaders[1].createCell(2 + (3 + 3) * sessionID + 1).setCellValue("Building blocks lost");
			buildingBlocksHeaders[1].createCell(2 + (3 + 3) * sessionID + 2).setCellValue("Building blocks purchased");
		}
	}
	
	private void generateRowHeaders(int roundID, int sessionID) {
		// battle results sheet
		for(int attackerID = 0; attackerID < NUM_TEAMS; attackerID++) {
			if(attackerID == 0) {
				Cell header0 =  battleResultsFromLastRoundRows[attackerID].createCell((NUM_TEAMS + 4) * sessionID);
				header0.setCellValue("Round " + roundID);
				Cell header1 =  battleResultsFromLastRoundRows[attackerID].createCell((NUM_TEAMS + 4) * sessionID + 1);
				header1.setCellValue("Attackers");
			}
			Cell header2 =  battleResultsFromLastRoundRows[attackerID].createCell((NUM_TEAMS + 4) * sessionID + 2);
			header2.setCellValue("Team " + (attackerID + 1));
		}
		
		// attacks sheet
		for(int attackerID = 0; attackerID < NUM_TEAMS; attackerID++) {
			if(attackerID == 0) {
				Cell header0 =  attacksRows[attackerID].createCell((2*NUM_ATTACKS_FIELDS + 4) * sessionID + 1);
				header0.setCellValue("Round " + roundID);
			}
			Cell header1 =  attacksRows[attackerID].createCell((2*NUM_ATTACKS_FIELDS + 4) * sessionID + 2);
			header1.setCellValue("Team " + (attackerID + 1));
		}
		
		// resources sheet				
		if(sessionID == 0)
			for(int resourcesDataID = 0; resourcesDataID < 7; resourcesDataID++)
				resourcesSheet.createRow(2 + resourcesDataID * (NUM_ROUNDS + 3) + roundID).createCell(0).setCellValue("Round " + roundID);

		// towers sheet
		towerHeightBeforeLastRoundRow.createCell(sessionID * (NUM_TEAMS + 2)).setCellValue("Round " + roundID);
		towerGrowthDuringLastRoundRow.createCell(sessionID * (NUM_TEAMS + 2)).setCellValue("Round " + roundID);
		towerHeightAfterLastRoundRow.createCell(sessionID * (NUM_TEAMS + 2)).setCellValue("Round " + roundID);
		
		// prices sheet
		if(sessionID == 0)
			for(int pricesDataID = 0; pricesDataID < 2; pricesDataID++)
				pricesSheet.createRow(2 + pricesDataID * (NUM_ROUNDS + 3) + roundID).createCell(0).setCellValue("Round " + roundID);
		pricesRow.createCell(sessionID * (11 + 2)).setCellValue("Round " + roundID);
		
		// weapons sheet
		for(int teamID = 0; teamID < NUM_TEAMS; teamID++) {
			if(teamID == 0) {
				Cell header0 =  weaponsRows[teamID].createCell((9 + 3) * sessionID);
				header0.setCellValue("Round " + roundID);
			}
			Cell header1 =  weaponsRows[teamID].createCell((9 + 3) * sessionID + 1);
			header1.setCellValue("Team " + (teamID + 1));
		}

		// building blocks sheet
		for(int teamID = 0; teamID < NUM_TEAMS; teamID++) {
			if(teamID == 0) {
				Cell header0 =  buildingBlocksRows[teamID].createCell((3 + 3) * sessionID);
				header0.setCellValue("Round " + roundID);
			}
			Cell header1 =  buildingBlocksRows[teamID].createCell((3 + 3) * sessionID + 1);
			header1.setCellValue("Team " + (teamID + 1));
		}
	}
	
	private void writeData(int roundID, int sessionID) {
		// battle results from last round sheet
		for(int attackerID = 0; attackerID < NUM_TEAMS; attackerID++) {
			if(gameState[sessionID][roundID].battleResultsFromLastRound != null) {
				Array<String> results = gameState[sessionID][roundID].battleResultsFromLastRound.get(attackerID);
				for(int defenderID = 0; defenderID < NUM_TEAMS; defenderID++) {
					int columnNumber = 3 + (NUM_TEAMS + 4) * sessionID + defenderID;
					Cell battleResultsFromLastRoundCell = battleResultsFromLastRoundRows[attackerID].createCell(columnNumber);
					battleResultsFromLastRoundCell.setCellValue(results.get(defenderID));
				}
			}
		}

		// attacks sheet
		for(int attackerID = 0; attackerID < NUM_TEAMS; attackerID++) {
			if(gameState[sessionID][roundID].attacks != null) {
				IntIntMap[] attackerAttacks = gameState[sessionID][roundID].attacks[attackerID];
				for(int attack = 0; attack < NUM_ATTACKS; attack++) {
					for(int attackField = 0; attackField < NUM_ATTACKS_FIELDS; attackField++) {
						int columnNumber = 3 + (2*NUM_ATTACKS_FIELDS + 4) * sessionID + attack * NUM_ATTACKS_FIELDS + attackField;
						Cell attacksCell = attacksRows[attackerID].createCell(columnNumber);
						if((attackField == 0) || (attackField == 1))
							attacksCell.setCellValue(attackerAttacks[attack].get(attackField, -100) + 1);
						else
							attacksCell.setCellValue(attackerAttacks[attack].get(attackField, -100));
					}
				}
			}
		}
		
		// resources sheet
		Cell resourcesAtStartOfLastRoundCell = resourcesAtStartOfLastRoundRow.createCell(sessionID+1);
		Cell resourcesAvailableInCurrentRoundCell = resourcesAvailableInCurrentRoundRow.createCell(sessionID+1);
		Cell resourcesDiscoveredInLastRoundCell = resourcesDiscoveredInLastRoundRow.createCell(sessionID + 1);
		Cell resourcesFromEcoScienceInLastRoundCell = resourcesFromEcoScienceInLastRoundRow.createCell(sessionID + 1);
		Cell resourcesNaturalGrowthInLastRoundCell = resourcesNaturalGrowthInLastRoundRow.createCell(sessionID + 1);
		Cell resourcesToCountryInvestmentTrustInLastRoundCell = resourcesToCountryInvestmentTrustInLastRoundRow.createCell(sessionID + 1);
		Cell resourcesUsedInLastRoundCell = resourcesUsedInLastRoundRow.createCell(sessionID + 1);
		
		resourcesAtStartOfLastRoundCell.setCellValue(gameState[sessionID][roundID].resourcesAtStartOfLastRound);
		resourcesAvailableInCurrentRoundCell.setCellValue(gameState[sessionID][roundID].resourcesAvailableInCurrentRound);
		resourcesDiscoveredInLastRoundCell.setCellValue(gameState[sessionID][roundID].resourcesDiscoveredInLastRound);
		resourcesFromEcoScienceInLastRoundCell.setCellValue(gameState[sessionID][roundID].resourcesFromEcoScienceInLastRound);
		resourcesNaturalGrowthInLastRoundCell.setCellValue(gameState[sessionID][roundID].resourcesNaturalGrowthInLastRound);
		resourcesToCountryInvestmentTrustInLastRoundCell.setCellValue(gameState[sessionID][roundID].resourcesToCountryInvestmentTrustInLastRound);
		resourcesUsedInLastRoundCell.setCellValue(gameState[sessionID][roundID].resourcesUsedInLastRound);

		//towers sheet
		for (int teamID = 0; teamID < NUM_TEAMS; teamID++) {
			towerHeightBeforeLastRoundRow.createCell(1 + (NUM_TEAMS + 2) * sessionID + teamID).setCellValue(gameState[sessionID][roundID].towerHeightBeforeLastRound.get(teamID, -100));
			towerGrowthDuringLastRoundRow.createCell(1 + (NUM_TEAMS + 2) * sessionID + teamID).setCellValue(gameState[sessionID][roundID].towerGrowthDuringLastRound.get(teamID, -100));
			towerHeightAfterLastRoundRow.createCell(1 + (NUM_TEAMS + 2) * sessionID + teamID).setCellValue(gameState[sessionID][roundID].towerHeightAfterLastRound.get(teamID, -100));
		}
		
		// prices sheet
		priceFactorRow.createCell(1 + sessionID).setCellValue(gameState[sessionID][roundID].priceFactor);
		relativePriceChangeRow.createCell(1 + sessionID).setCellValue(gameState[sessionID][roundID].relativePriceChange);
		pricesRow.createCell(1 + sessionID * (11 + 2) + 0).setCellValue(gameState[sessionID][roundID].prices.get(GameStateJsonParser.ZWAARD, -100));
		pricesRow.createCell(1 + sessionID * (11 + 2) + 1).setCellValue(gameState[sessionID][roundID].prices.get(GameStateJsonParser.RAKET, -100));
		pricesRow.createCell(1 + sessionID * (11 + 2) + 2).setCellValue(gameState[sessionID][roundID].prices.get(GameStateJsonParser.VIRUS, -100));
		pricesRow.createCell(1 + sessionID * (11 + 2) + 3).setCellValue(gameState[sessionID][roundID].prices.get(GameStateJsonParser.SCHILD, -100));
		pricesRow.createCell(1 + sessionID * (11 + 2) + 4).setCellValue(gameState[sessionID][roundID].prices.get(GameStateJsonParser.RAKETSCHILD, -100));
		pricesRow.createCell(1 + sessionID * (11 + 2) + 5).setCellValue(gameState[sessionID][roundID].prices.get(GameStateJsonParser.ANTIVIRUS, -100));
		pricesRow.createCell(1 + sessionID * (11 + 2) + 6).setCellValue(gameState[sessionID][roundID].prices.get(GameStateJsonParser.PATENT_RAKET, -100));
		pricesRow.createCell(1 + sessionID * (11 + 2) + 7).setCellValue(gameState[sessionID][roundID].prices.get(GameStateJsonParser.PATENT_RAKETSCHILD, -100));
		pricesRow.createCell(1 + sessionID * (11 + 2) + 8).setCellValue(gameState[sessionID][roundID].prices.get(GameStateJsonParser.PATENT_VIRUS, -100));
		pricesRow.createCell(1 + sessionID * (11 + 2) + 9).setCellValue(gameState[sessionID][roundID].prices.get(GameStateJsonParser.PATENT_ANTIVIRUS, -100));
		pricesRow.createCell(1 + sessionID * (11 + 2) + 10).setCellValue(gameState[sessionID][roundID].prices.get(GameStateJsonParser.BOUWSTEEN, -100));

		// weapons sheet
		for(int teamID = 0; teamID < NUM_TEAMS; teamID++) {
			if(gameState[sessionID][roundID].numShieldsBeforeAttack != null)
				weaponsRows[teamID].createCell(2 + (9 + 3) * sessionID + 0).setCellValue(gameState[sessionID][roundID].numShieldsBeforeAttack.get(teamID, -100));
			if(gameState[sessionID][roundID].numRocketShieldsBeforeAttack != null)
				weaponsRows[teamID].createCell(2 + (9 + 3) * sessionID + 1).setCellValue(gameState[sessionID][roundID].numRocketShieldsBeforeAttack.get(teamID, -100));
			if(gameState[sessionID][roundID].numAntiVirusesBeforeAttack != null)
				weaponsRows[teamID].createCell(2 + (9 + 3) * sessionID + 2).setCellValue(gameState[sessionID][roundID].numAntiVirusesBeforeAttack.get(teamID, -100));
			if(gameState[sessionID][roundID].numShieldsAfterAttack != null)
				weaponsRows[teamID].createCell(2 + (9 + 3) * sessionID + 3).setCellValue(gameState[sessionID][roundID].numShieldsAfterAttack.get(teamID, -100));
			if(gameState[sessionID][roundID].numRocketShieldsAfterAttack != null)
				weaponsRows[teamID].createCell(2 + (9 + 3) * sessionID + 4).setCellValue(gameState[sessionID][roundID].numRocketShieldsAfterAttack.get(teamID, -100));
			if(gameState[sessionID][roundID].numAntiVirusesAfterAttack != null)
				weaponsRows[teamID].createCell(2 + (9 + 3) * sessionID + 5).setCellValue(gameState[sessionID][roundID].numAntiVirusesAfterAttack.get(teamID, -100));
			if(gameState[sessionID][roundID].numSwordsAttackedBy != null)
				weaponsRows[teamID].createCell(2 + (9 + 3) * sessionID + 6).setCellValue(gameState[sessionID][roundID].numSwordsAttackedBy.get(teamID, -100));
			if(gameState[sessionID][roundID].numRocketsAttackedBy != null)
				weaponsRows[teamID].createCell(2 + (9 + 3) * sessionID + 7).setCellValue(gameState[sessionID][roundID].numRocketsAttackedBy.get(teamID, -100));
			if(gameState[sessionID][roundID].numVirusesAttackedBy != null)
				weaponsRows[teamID].createCell(2 + (9 + 3) * sessionID + 8).setCellValue(gameState[sessionID][roundID].numVirusesAttackedBy.get(teamID, -100));
		}

		// building blocks sheet
		for(int teamID = 0; teamID < NUM_TEAMS; teamID++) {
			if(gameState[sessionID][roundID].conqueredBouwstenen != null)
				buildingBlocksRows[teamID].createCell(2 + (3 + 3) * sessionID + 0).setCellValue(gameState[sessionID][roundID].conqueredBouwstenen.get(teamID, -100));
			if(gameState[sessionID][roundID].lostBouwstenen != null)
				buildingBlocksRows[teamID].createCell(2 + (3 + 3) * sessionID + 1).setCellValue(gameState[sessionID][roundID].lostBouwstenen.get(teamID, -100));
			if(gameState[sessionID][roundID].purchasedBouwstenen != null)
				buildingBlocksRows[teamID].createCell(2 + (3 + 3) * sessionID + 2).setCellValue(gameState[sessionID][roundID].purchasedBouwstenen.get(teamID, -100));
		}
	}
	
	public void parse() throws Exception
	{
		gameState = readJsonFile();		
		
		generateGenericHeaders();
		
		// iterate over all rounds
		for(int roundID = 0; roundID < NUM_ROUNDS; roundID++) {
			
			newDataRows(roundID);
			
			generateColumnHeaders(roundID);

			// iterate over all sessions
			for(int sessionID = 0; sessionID < NUM_SESSIONS; sessionID++) {

				generateRowHeaders(roundID, sessionID);
				
				// write data, if there are any
				if(gameState[sessionID][roundID] != null) {
					writeData(roundID, sessionID);
				}
			}
		}
		
		wb.write(outFile);
		outFile.close();
		wb.close();
	}
	
	public static void main(String[] args) {

		JsonParser jsonParser = new JsonParser();
		
		try {
			jsonParser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
