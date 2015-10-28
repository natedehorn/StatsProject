/**
 * Created by nathandehorn on 10/13/15.
 * Project:
 */
import constant.Region;
import dto.Champion.Champion;
import dto.League.League;
import dto.Stats.*;
import dto.Summoner.Summoner;
import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;

import java.io.*;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class main {

    public static void main(String[] args) throws RiotApiException, IOException {

        RiotApi api = new RiotApi("e5688ad1-402b-4e51-94b1-d4eaef98930a", Region.NA);

        Scanner scanner = new Scanner(new File("/Users/nathandehorn/IdeaProjects/StatProject/src/summoners.txt"));
        List<String> lines = new ArrayList<String>();
        while (scanner.hasNextLine())
            lines.add(scanner.nextLine());
        String[] summonerList = lines.toArray(new String[0]); //Array of summoners names from txt file
        System.out.println(Arrays.toString(summonerList));

        Workbook wb = new HSSFWorkbook();
        //CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("Summoner Names");

        //Set headers
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Summoner Name");
        row.createCell(1).setCellValue("Summoner ID");
        row.createCell(2).setCellValue("Champion ID");
        row.createCell(3).setCellValue("Total games");
        row.createCell(4).setCellValue("Average kills");
        row.createCell(5).setCellValue("Average deaths");
        row.createCell(6).setCellValue("Average assists");
        int rowCount = 1;

        for(int i = 0; i < summonerList.length; i++)
        {
            String summonerName = summonerList[i].toLowerCase().replaceAll("\\s+", "");
            Summoner summoner = api.getSummonersByName(Region.NA, summonerName).get(summonerName); //Set summoner
            apiSleep(1);

            long summonerID = summoner.getId();
            RankedStats summonerRankedStats = api.getRankedStats(summonerID);
            apiSleep(1);
            System.out.println(summonerName);

            List<ChampionStats> summonerChampions = summonerRankedStats.getChampions();

            for(int j = 0; j < summonerChampions.size(); j++)
            {
                ChampionStats currentChampionStats = summonerChampions.get(j);
                int championID = currentChampionStats.getId();
                AggregatedStats currentChampionAggStats = currentChampionStats.getStats();
                float totalGames = currentChampionAggStats.getTotalSessionsPlayed();
                float averageKills = currentChampionAggStats.getTotalChampionKills()/totalGames;
                float averageDeaths = currentChampionAggStats.getTotalDeathsPerSession()/totalGames;
                float averageAssists = currentChampionAggStats.getTotalAssists()/totalGames;

                if(championID == 0)
                {
                    row = sheet.createRow(rowCount);
                    row.createCell(0).setCellValue(summonerName); //Set Summoner Name cell
                    row.createCell(1).setCellValue(summonerID); //Set Summoner ID cell
                    row.createCell(2).setCellValue(championID); //Set Champion ID cell
                    row.createCell(3).setCellValue(totalGames); //Set Total games cell
                    row.createCell(4).setCellValue(averageKills); //Set Average Kills cell
                    row.createCell(5).setCellValue(averageDeaths); //Set Average deaths cell
                    row.createCell(6).setCellValue(averageAssists); //Set Average assists cell
                    rowCount++;
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

    public static void apiSleep(float seconds)
    {
        try {
            float timeout = seconds*1000;
            System.out.print("* ");
            Thread.sleep(((long) timeout));                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

// Find how variables are related
// Scatterplot -> correlation coefficients (Shows how each variable individually correlates to another)