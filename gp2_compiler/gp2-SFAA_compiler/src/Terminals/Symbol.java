package Terminals;

import LexicalAnalyzer.LexicalAnalyzer;
import Terminals.Exception.AnalyzerExceptions;

/**
 * an abstract class containing the general information all symbols must
 * contain.
 */
public abstract class Symbol {

    //---------------- attributes -----------------//

    private String name;
    private String type;
    private int id;

    //---------------- methods -----------------//

    /**
     * the default constructor of a Symbol.
     * @param name the name of the symbol to be created.
     * @param type the type of the symbol.
     * @param id the id of the symbol.
     */
    public Symbol( String name, String type, int id){
        this.name = name;
        this.type = type;
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public String getName(){
        return this.name;
    }

    public int getId(){
        return this.id;
    }

}
