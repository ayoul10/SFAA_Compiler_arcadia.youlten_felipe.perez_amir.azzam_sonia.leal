package Terminals;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * this is a singleton class that stores all the symbols in the input code.
 */
public class SymbolTable {

    //---------------- attributes -----------------//

    public static HashMap<Integer, Symbol> table = new HashMap<>();
    public static int id = 1;

    public enum FunctionInfo{
        OK,
        WRONG_PARAM_NUM,
        WRONG_PARAM_TYPE,
        FUNCTION_AS_PARAM
    }

    public static FunctionInfo problem = FunctionInfo.OK;


    //---------------- methods -----------------//

    //pass a variable name, to see if it already has been declared
    //if we get a redeclaration of the symbol, that 's bad

    /**
     * this method checks for duplicate symbol names in the scope
     * @param name the name to be checked
     * @param isVariable a boolean specifying if the name corresponds to a
     *                   variable or a function
     * @param scope the scope of that symbol if the symbol is a variable
     * @return an integer containing the id of the symbol if exists or a -1
     * if the symbol is not duplicated (the symbol doesn't exist in this scope)
     */
    public static int checkSymbolExists(String name, boolean isVariable, Integer scope){
        if(isVariable) name = name.concat(scope +"");
        for (Symbol var : table.values()) {
            if(var.getName().compareTo(name) == 0){
                if(isVariable && var instanceof Variable){
                    if(!((Variable) var).getScope().equals(scope)) return -1;
                    else return var.getId(); // can't redeclare vars in the same scope
                } else return var.getId(); //can't have two functions with the same name
            }
        }
        return -1;
    }

    /**
     * checks if the function is defined.
     * @param name the name of the function
     * @return true if the function exists, and false if it doesn't.
     */
    public static boolean checkIfFunctionExists(String name){
        for (Symbol var : table.values()) {
            if((var.getName().compareTo(name) == 0) && var instanceof Function ){
                return true;
            }
        }
        return false;
    }

    /**
     * this method prints out the symbol table to the terminal.
     */
    public static void printSymbolTable(){
        for (Integer name: table.keySet()) {
            String key = name.toString();
            Symbol value = table.get(name);

            if(value instanceof Variable){
                System.out.print("VAR=> ");
                System.out.println(key + " [name: " + value.getName() + "] [type:" +
                        " " + value.getType()+"] [value:" + ((Variable)value).getValue());
            }
            else{
                System.out.print("FUNC=> ");
                System.out.println(key + " [name: " + value.getName() + "] [type:" +
                        " " + value.getType()+"]");
            }



        }
    }

    /**
     * checks the number and types of the parameters passed to a function
     * call match the parameters in the function definition.
     * @param name the name of the function.
     * @param auxParameters a list of all the parameters passed to the
     *                      function call.
     * @return true if the parameters are not valid, and false in case the
     * parameters match.
     */
    public static boolean notValidParameters(String name, ArrayList<String> auxParameters) {
        int funcId = -1;
        for (Symbol var : table.values()) {
            if((var.getName().compareTo(name) == 0) && var instanceof Function ){
                funcId = var.getId();
            }
        }
        Symbol func = table.get(funcId);
        if(func instanceof Function){
            if(((Function) func).getNumParameters() != auxParameters.size()){
                problem = FunctionInfo.WRONG_PARAM_NUM;
                return true;
            }
            ArrayList<Variable> parameters = ((Function) func).getParameters();
            for (int i = 0; i < auxParameters.size(); i++) {
                Symbol var = table.get(getIdByName(auxParameters.get(i)  ));
                if(var instanceof Variable){
                    if(!var.getType().equals(parameters.get(i).getType())){ //checking if types are different
                        problem = FunctionInfo.WRONG_PARAM_TYPE;
                        return true;
                    }
                }else{
                    if(!table.get(var.getName()).getType().equals(parameters.get(i).getType())){ //checking if types are different
                        problem = FunctionInfo.WRONG_PARAM_TYPE;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * gets the id of a symbol by the name of that symbol
     * @param name the name of the symbol
     * @return and integer containing the id of the symbol
     */
    public static int getIdByName(String name){
        int funcId = -1;
        for (Symbol var : table.values()) {
            if((var.getName().compareTo(name) == 0)){
                funcId = var.getId();
                return funcId;
            }
        }
        return funcId;
    }
}
