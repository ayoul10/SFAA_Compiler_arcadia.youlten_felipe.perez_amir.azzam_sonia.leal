package Terminals;

import com.opencsv.CSVReader;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * this is a single-tone class that stores all the tokens and their ids.
 */
public class TokenDatabase {

    //---------------- attributes -----------------//

    /* the file where the tokens are located */
    private  final String TOKEN_FILE = "Resources/Tokens.csv";
    public  static HashMap<Integer, String> tokenList = new HashMap<>();

    //---------------- methods -----------------//

    /**
     * the default constructor. This constructor fills in the the token table
     * using the token file provided by the TOKEN_FILE attribute.
     */
    public TokenDatabase(){
        String line = "";
        String splitBy = ",";

        try (CSVReader csvReader = new CSVReader(new FileReader(TOKEN_FILE));) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                tokenList.put(Integer.parseInt(Arrays.asList(values).get(0)),
                        Arrays.asList(values).get(1) );
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
