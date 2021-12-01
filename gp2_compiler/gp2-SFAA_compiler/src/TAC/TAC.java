package TAC;

import Terminals.ConstantTable;
import Terminals.Exception.AnalyzerExceptions;
import Terminals.Exception.TokenDoesNotExistException;
import Terminals.SymbolTable;
import Terminals.Token;
import Terminals.Variable;

/**
 * this class represents the basic structure of one TAC elements.
 * Each TAC is made up of three main sections :
 *
 *      [result] -> [value1] [operation] [value2]
 *
 * this class provide tools to manage those TAC.
 *
 */
public class TAC {

    //---------------- attributes -----------------//

    private Token result;
    private Token value1;
    private Token value2;
    private Token operation;

    private static Integer globalTCounter = 0;

    //---------------- methods -----------------//

    /**
     * this is the default constructor and it generates an empty TAC
     */
    public TAC() {
        this.result = null;
        this.operation = null;
        this.value2 = null;
        this.value1 = null;
    }

    /**
     * this constructor generates a TAC given all the attributes needed.
     * @param result the result of the TAC
     * @param value1 the first value in the TAC
     * @param value2 the second value in the TAC
     * @param operation the operation carried out on those two values.
     * @throws AnalyzerExceptions this is thrown in case an invalid value 1
     * or 2 is provided. Moreover, it will be thrown if an illegal operating
     * is carried out between those two values.
     */
    public TAC(Token result, Token value1, Token value2, Token operation)
            throws AnalyzerExceptions
    {
        this.result = result;
        if(value1 != null){
            if (value1.getExternalId() != -1) {
                this.value1 = value1;
            } else {
                {
                    throw new AnalyzerExceptions("Error compiling your code" +
                            ".ErrorCode = 1");
                }
            }
        }

        if(value2 != null){
            if (value2.getExternalId() != -1) {
                this.value2 = value2;
            } else {
                {
                    throw new AnalyzerExceptions("Error compiling your code" +
                            ".ErrorCode = 1");
                }
            }
        }

        if (operation.getId() >= 46 && operation.getId() <= 76
                || operation.getId() == 22 //if
                || operation.getId() == 29 // return
                || operation.getId() == 31  //call
        )
            this.operation = operation;
        else
            throw new AnalyzerExceptions("Error compiling your code. " +
                    "ErrorCode = 2");
    }


    /**
     * this constructor creates a TAC with only the result and the first value.
     * @param result the result of the TAC
     * @param value1 the first value of the TAC
     * @throws AnalyzerExceptions if an invalid value is provided.
     */
    public TAC(Token result, Token value1) throws AnalyzerExceptions {
        this.result = result;
        if (value1.getExternalId() != -1)
            this.value1 = value1;
        else
            throw new AnalyzerExceptions("Error compiling your code" +
                    ".ErrorCode = 3");

        this.operation = null;
        this.value2 = null;
    }

    /**
     * this function creates a string with the information of one token
     * @param value the token to be processed
     * @return a string containing the token's data.
     */
    public String valueToString(Token value){
        StringBuilder string = new StringBuilder();

        if(value != null)
            switch (value.getId()){
                case Token.CONSTANTS:
                    string.append(value.getName()
                            +value.getExternalId() + " "
                            + "( "
                            + ConstantTable.table.get(value.getExternalId()).getValue()
                            +" ) ");
                    break;
                case Token.SYMBOLS:
                    string.append(value.getName()
                            +value.getExternalId() + " "
                            + "( "
                            + SymbolTable.table.get(value.getExternalId()).getName()
                            +" ) ");
                    break;
                default:
                    string.append(value.getName() + " ");

            }


            return string.toString();
    }

    /**
     * checks if the TAC is completely filled (this happens when the result
     * of the TAC is filled in)
     * @return true if the TAC is full, false otherwise.
     */
    public boolean isFull(){
        return this.result != null;
    }

    /**
     * checks if the TAC is half full. This means that the TAC has both of
     * it's values and the operation fields filled but not necessarily the
     * result.
     * @return true if the TAC is half full, false otherwise.
     */
    public boolean isHalfFull(){
        if(this.value1==null || this.value2==null || this.operation==null) {
            return false;
        }
        return true;
    }

    /**
     * this method adds a token to its correct position. For instance, if the
     * provided token is an operation it will be added to the operation field.
     * Moreover, if the token is a value it will be added to value 1 or 2
     * depending on which one is free. Finally, if adding this token makes
     * the TAC half full, then a result token is created and added to the
     * result field.
     * @param t the token to be added
     * @return the number of the auxiliary variable placed in the results if
     * any. Otherwise it will return -1-
     * @throws AnalyzerExceptions if provided a token to TAC that is already
     * full or if the position where that token fits is already occupied.
     * @throws TokenDoesNotExistException if an invalid token was used while
     * creating the result.
     */
    public int add(Token t) throws AnalyzerExceptions, TokenDoesNotExistException {
        if (this.operation == null
                && t.getId() >= 46
                && t.getId() <= 76)
        // the token is an operator
        {
            this.operation = t;
        }else if((value1 == null ||value2 == null) && t.getExternalId() != -1){ //if not -1 it's not a var nor constant
            if(value2 == null){
                value2 = t;
            }else {
                value1 = t;
            }
        }else{
            throw new AnalyzerExceptions("Cannot add element to a full TAC. " +
                    "Error code: 4");
        }
        if (isHalfFull()){
            value1 = t;
            String type2 = "";
            String type1 = "";
            if (value1.getId() == Token.CONSTANTS){
                type1 =
                        ConstantTable.table.get(value1.getExternalId()).getType();
            }else if(value1.getId() == Token.SYMBOLS){
                type1 =
                        SymbolTable.table.get(value1.getExternalId()).getType();
            }

            if (value2.getId() == Token.CONSTANTS){
                type2 =
                        ConstantTable.table.get(value2.getExternalId()).getType();
            }else if(value2.getId() == Token.SYMBOLS){
                type2 =
                        SymbolTable.table.get(value2.getExternalId()).getType();
            }

            if (type1.compareTo(type2) == 0){
                Variable auxVar = new Variable(TAC.globalTCounter.toString(),type1);
                result = new Token(Token.SYMBOLS , auxVar.getId());
            }
            else
                throw new AnalyzerExceptions("In this language, operating on " +
                        "two entities with different types is prohibited."
                        + System.lineSeparator()
                        + type1 + " " + operation.getName() + " " + type2);

            return TAC.globalTCounter++;
        }
        return -1;
    }
    public Token getValue1() {
        return value1;
    }

    public Token getValue2() {
        return value2;
    }

    public Token getOperation() {
        return operation;
    }

    public Token getResult(){
        return this.result;
    }

}
