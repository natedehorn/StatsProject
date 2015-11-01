/**
 * Created by nathandehorn on 10/13/15.
 * Project:
 */

import constant.Region;
import dto.Stats.AggregatedStats;
import dto.Stats.ChampionStats;
import dto.Stats.RankedStats;
import dto.Summoner.Summoner;
import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class main {

    public static void main(String[] args) throws RiotApiException, IOException {

        // Build file of summoner names
        File file = new File("/Users/nathandehorn/IdeaProjects/StatProject/src/summonerNames.txt");
//      PrintWriter summonernames = new PrintWriter("/Users/nathandehorn/IdeaProjects/StatProject/src/summonerNames.txt");
//      String rooturl = "http://www.lolsummoners.com/ladders/na/";
//        for(int pageCount = 1; pageCount < 1000; pageCount++) { //Get the top 25000 (25*1000) summoners
//            Document document = Jsoup.connect(rooturl + pageCount).timeout(10*1000).get(); //10 second timeout
//            System.out.println(". . . working on page number " + pageCount + " . . .");
//            for (int count = 0; count < 25; count++) {
//                Element tableHeader = document.select("td.name").get(count);
//                String name = tableHeader.text();
//                summonernames.println(name);
//            }
//        }

        RiotApi api = new RiotApi("e5688ad1-402b-4e51-94b1-d4eaef98930a", Region.NA); //Set api key

        Scanner scanner = new Scanner(file);
        List<String> lines = new ArrayList<>();
        while (scanner.hasNextLine())
            lines.add(scanner.nextLine());
        String[] summonerList = lines.toArray(new String[lines.size()]); //Array of summoners names from txt file
        System.out.println(summonerList.length + " entries - " + Arrays.toString(summonerList));

        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("Summoner Names");

        //Set headers
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Summoner Name");
        row.createCell(1).setCellValue("Summoner ID");
        row.createCell(2).setCellValue("Champion ID");
        row.createCell(3).setCellValue("Total games");
        row.createCell(4).setCellValue("Total wins");
        row.createCell(5).setCellValue("Win percentage");
        row.createCell(6).setCellValue("Average kills");
        row.createCell(7).setCellValue("Average deaths");
        row.createCell(8).setCellValue("Average assists");
        row.createCell(9).setCellValue("Average gold");
        row.createCell(10).setCellValue("Killing spree");
        row.createCell(11).setCellValue("Max largest critical strike");
        row.createCell(12).setCellValue("Max time played");
        row.createCell(13).setCellValue("Max time spent living");
        row.createCell(14).setCellValue("Ranked solo games played");
        row.createCell(15).setCellValue("Average damage dealt");
        row.createCell(16).setCellValue("Average damage taken");
        row.createCell(17).setCellValue("Average first blood");
        row.createCell(18).setCellValue("Average heal");
        row.createCell(19).setCellValue("Average magic damage done");
        row.createCell(20).setCellValue("Average minions killed");
        row.createCell(21).setCellValue("Average neutral minions killed");
        row.createCell(22).setCellValue("Average turrets killed");
        int rowCount = 1;

        for (int i = 0; i < summonerList.length; i++) {
            try {
                System.out.print(i + " ");
                String summonerName = summonerList[i].toLowerCase().replaceAll("\\s+", "");
                apiSleep(1);
                Summoner summoner = api.getSummonersByName(Region.NA, summonerName).get(summonerName); //Set summoner

                long summonerID = summoner.getId();
                apiSleep(1);
                RankedStats summonerRankedStats = api.getRankedStats(summonerID);
                System.out.println(summonerName);

                List<ChampionStats> summonerChampions = summonerRankedStats.getChampions();
                write(summonerChampions, rowCount, sheet, summonerName, summonerID);


            }
            catch (RiotApiException | NullPointerException ex) {
                String msg = ex.toString();
                System.out.println(msg);
                if (msg.equals("main.java.riotapi.RiotApiException: Rate limit exceeded")) {
                    System.out.println(". . . reseting . . .");
                    apiSleep(1);apiSleep(1);apiSleep(1);apiSleep(1);apiSleep(1);apiSleep(1);apiSleep(1);apiSleep(1);
                    System.out.println();

                    //Do it again!
                    System.out.print(i + " ");
                    String summonerName = summonerList[i].toLowerCase().replaceAll("\\s+", "");
                    apiSleep(1);
                    Summoner summoner = api.getSummonersByName(Region.NA, summonerName).get(summonerName); //Set summoner

                    long summonerID = summoner.getId();
                    apiSleep(1);
                    RankedStats summonerRankedStats = api.getRankedStats(summonerID);
                    System.out.println(summonerName);

                    List<ChampionStats> summonerChampions = summonerRankedStats.getChampions();
                    write(summonerChampions, rowCount, sheet, summonerName, summonerID);
                }
            }
        }

        FileOutputStream fileOut = new FileOutputStream("/Users/nathandehorn/IdeaProjects/StatProject/src/summoners.xls");
        System.out.println();
        System.out.println(". . . writing excel file . . .");
        wb.write(fileOut);
        System.out.println("Excel file written successfully!");
        fileOut.close();
    }

    private static void write(List<ChampionStats> summChamps, int rowCount, Sheet sheet, String summonerName, long summonerID) {
        for (ChampionStats currentChampionStats : summChamps) {
            int championID = currentChampionStats.getId();
            AggregatedStats currentChampionAggStats = currentChampionStats.getStats();
            float totalGames = currentChampionAggStats.getTotalSessionsPlayed();
            float averageKills = currentChampionAggStats.getTotalChampionKills() / totalGames;
            float averageDeaths = currentChampionAggStats.getTotalDeathsPerSession() / totalGames;
            float averageAssists = currentChampionAggStats.getTotalAssists() / totalGames;
            float totalWins = currentChampionAggStats.getTotalSessionsWon();
            float winPercentage = (totalWins / totalGames) * 100;
            float averageGold = currentChampionAggStats.getTotalGoldEarned() / totalGames;
            float killingSpree = currentChampionAggStats.getKillingSpree() / totalGames;
            float maxLargestCriticalStrike = currentChampionAggStats.getMaxLargestCriticalStrike();
            float maxTimePlayed = currentChampionAggStats.getMaxTimePlayed();
            float maxTimeSpentLiving = currentChampionAggStats.getMaxTimeSpentLiving();
            float rankedSoloGamesPlayed = currentChampionAggStats.getRankedSoloGamesPlayed();
            float averageDamageDealt = currentChampionAggStats.getTotalDamageDealt() / totalGames;
            float averageDamageTaken = currentChampionAggStats.getTotalDamageTaken() / totalGames;
            float averageFirstBlood = currentChampionAggStats.getTotalFirstBlood() / totalGames;
            float averageHeal = currentChampionAggStats.getTotalHeal() / totalGames;
            float averageMagicDamageDone = currentChampionAggStats.getTotalMagicDamageDealt() / totalGames;
            float averageMinionKills = currentChampionAggStats.getTotalMinionKills() / totalGames;
            float averageNeutralMinionsKilled = currentChampionAggStats.getTotalNeutralMinionsKilled() / totalGames;
            float averageTurretsKilled = currentChampionAggStats.getTotalTurretsKilled() / totalGames;

            if (championID == 0) //This returns the overall ranked stats for a summoner
            {
                Row row = sheet.createRow(rowCount);
                row.createCell(0).setCellValue(summonerName); //Set Summoner Name cell
                row.createCell(1).setCellValue(summonerID); //Set Summoner ID cell
                row.createCell(2).setCellValue(championID); //Set Champion ID cell
                row.createCell(3).setCellValue(totalGames); //Set Total games cell
                row.createCell(4).setCellValue(totalWins); //Set Total wins cell
                row.createCell(5).setCellValue(winPercentage); //Set Win percentage cell
                row.createCell(6).setCellValue(averageKills); //Set Average Kills cell
                row.createCell(7).setCellValue(averageDeaths); //Set Average deaths cell
                row.createCell(8).setCellValue(averageAssists); //Set Average assists cell
                row.createCell(9).setCellValue(averageGold); //Set Average gold cell
                row.createCell(10).setCellValue(killingSpree); //Set Killing spree cell
                row.createCell(11).setCellValue(maxLargestCriticalStrike); //Set Max largest critical strike cell
                row.createCell(12).setCellValue(maxTimePlayed); //Set Max time played cell
                row.createCell(13).setCellValue(maxTimeSpentLiving); //Set Max time spent living
                row.createCell(14).setCellValue(rankedSoloGamesPlayed); //Set Ranked solo games played cell
                row.createCell(15).setCellValue(averageDamageDealt); //Set Average damage dealt cell
                row.createCell(16).setCellValue(averageDamageTaken); //Set Average damage taken cell
                row.createCell(17).setCellValue(averageFirstBlood); //Set Average first blood cell
                row.createCell(18).setCellValue(averageHeal); //Set Average heal cell
                row.createCell(19).setCellValue(averageMagicDamageDone); //Set Average magic damage done cell
                row.createCell(20).setCellValue(averageMinionKills); //Set Average minion kills cell
                row.createCell(21).setCellValue(averageNeutralMinionsKilled); //Set Average neutral minion kills cell
                row.createCell(22).setCellValue(averageTurretsKilled); //Set Average turrets killed cell
                rowCount++;
            }
        }
    }

    public static void apiSleep(float seconds) {
        try {
            float timeout = seconds * 1000;
            System.out.print("* ");
            Thread.sleep(((long) timeout));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

// Find how variables are related
// Scatterplot -> correlation coefficients (Shows how each variable individually correlates to another)