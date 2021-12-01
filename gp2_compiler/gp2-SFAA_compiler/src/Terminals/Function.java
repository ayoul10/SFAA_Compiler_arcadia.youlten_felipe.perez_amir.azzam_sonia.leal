package Terminals;

import Terminals.Exception.AnalyzerExceptions;
import java.util.ArrayList;

/**
 * this class stores all the necessary metadata of a function symbol. Each
 * function in the input code will be translated into an object of this class.
 */
public class Function  extends Symbol{


    //---------------- attributes -----------------//
    private ArrayList<Variable> parameters;
    private int blockId;


    //---------------- methods -----------------//

    /**
     * the default constructor of a function object.
     * @param name the name of the function.
     * @param type the return type of the function.
     * @throws AnalyzerExceptions thrown if the function name starts with a
     * number
     */
    public Function(String name, String type) throws AnalyzerExceptions {
        super(name, type ,SymbolTable.id);
        if(name.toCharArray().length > 0 && Character.isDigit(name.charAt(0))) {
            throw new AnalyzerExceptions("Function names cannot start with a number");
        }
        SymbolTable.table.put(SymbolTable.id++, this);
        this.parameters = new ArrayList<>();
    }

    /**
     * this methods adds a parameter to the function's list of parameters
     * @param name the name of the parameter
     * @param type the type of the parameter
     * @param scope the scope of the parameter (should be the scope equal to
     *              the function)
     * @throws AnalyzerExceptions thrown if the name of the parameter doesn't
     * follow the language's specifications.
     */
    public void addParameter(String name, String type, Integer scope) throws AnalyzerExceptions {
        this.parameters.add(new Variable(name, type, scope));
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public int getBlockId() {
        return blockId;
    }

    public ArrayList<Variable> getParameters() {
        return parameters;
    }

    public int getNumParameters() {
        return parameters.size();
    }
}