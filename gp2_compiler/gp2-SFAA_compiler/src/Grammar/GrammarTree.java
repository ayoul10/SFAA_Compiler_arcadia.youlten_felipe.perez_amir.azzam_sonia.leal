package Grammar;

import Terminals.Exception.TokenDoesNotExistException;
import Terminals.Token;

import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * this class builds a grammar tree structure.
 */
public class GrammarTree {

    //---------------- attributes -----------------//

    private HashMap<String, ArrayList<ArrayList<GrammarNode>>> tree = new HashMap();
    public static final String START = "SFAA";

    //---------------- methods -----------------//

    /**
     * this is the default constructor that will parse the data from the
     * grammar.json file.
     */
    public GrammarTree(){
        JSONParser parser = new JSONParser();
        try {
            JSONArray grammar = (JSONArray) parser.parse(new FileReader("Resources/grammar.json"));
            String id;
            ArrayList<ArrayList<GrammarNode>> node;
            ArrayList<GrammarNode> rule;

            for (Object pro : grammar) {
                JSONObject production = (JSONObject) pro;
                id = (String) production.get("Id");
                 node = new ArrayList<>(); //array of rules
                for (JSONArray rules :
                        (ArrayList<JSONArray>) production.get("production")) {
                    rule = new ArrayList<>();
                    for (Object o : rules) {
                        JSONObject obj = (JSONObject) o;
                        rule.add(new GrammarNode((String)obj.get("Id"),
                                (boolean)obj.get("isTerminal")));
                    }
                    node.add(rule);
                }
                tree.put(id, node);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap getTable(){
        return this.tree;
    }
}
