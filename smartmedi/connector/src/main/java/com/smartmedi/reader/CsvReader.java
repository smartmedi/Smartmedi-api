package com.smartmedi.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vignesh on 14/6/15.
 */
public class CsvReader {

    public static List<String>  readFile(String path) throws IOException{

        List<String> lines = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(path));
        br.readLine();
        String line = br.readLine();
        while (line != null) {
            lines.add(line);
            line = br.readLine();
        }
        return lines;
    }
}