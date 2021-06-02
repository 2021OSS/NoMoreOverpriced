package com.example.nomoreoverpriced;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReadCsv {
    public static void readDataFromCsv(String filePath) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(filePath));
        String [] nextLine;
        while ((nextLine  = reader.readNext()) != null) {
            for (int i = 0; i < nextLine.length; i++) {
                System.out.println(i + " "  + nextLine[i]);
            }
            System.out.println();
        }
    }
}