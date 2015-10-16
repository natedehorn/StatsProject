/**
 * Created by nathandehorn on 10/13/15.
 * Project:
 */
import constant.Region;
import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;

import java.io.*;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;

public class main {

    public static void main(String[] args) throws RiotApiException, IOException {

        RiotApi api = new RiotApi("e5688ad1-402b-4e51-94b1-d4eaef98930a", Region.NA);

        Scanner scanner = new Scanner(new File("/Users/nathandehorn/IdeaProjects/StatProject/src/summoners.txt"));
        List<String> lines = new ArrayList<String>();
        while (scanner.hasNextLine())
            lines.add(scanner.nextLine());

        String[] summonerList = lines.toArray(new String[0]);

        System.out.println(Arrays.toString(summonerList));



        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Sample sheet");
        String summonerNum;
        String summonerNumSTD;

        Map<String, Object[]> data = new HashMap<String, Object[]>();
        data.put("1", new Object[] {"Summoner No.", "Summmoner Name"});
        for(int i = 0; i < lines.size(); i++)
        {
            summonerNum = String.valueOf(i + 2);
            summonerNumSTD = summonerNum + "d";
            data.put(summonerNum, new Object[] {summonerNumSTD, lines.get(i)});
        }

        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset) {
            Row row = sheet.createRow(rownum++);
            Object [] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if(obj instanceof Date)
                    cell.setCellValue((Date)obj);
                else if(obj instanceof Boolean)
                    cell.setCellValue((Boolean)obj);
                else if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Double)
                    cell.setCellValue((Double)obj);
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(new File("/Users/nathandehorn/IdeaProjects/StatProject/src/summoners.xls"));
            workbook.write(out);
            out.close();
            System.out.println("Excel written successfully..");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}

//TODO: Add on to excel sheet after passing summoner name