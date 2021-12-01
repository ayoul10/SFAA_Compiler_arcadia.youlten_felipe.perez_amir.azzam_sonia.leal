package LexicalAnalyzer;

import Terminals.*;
import Terminals.Exception.AnalyzerExceptions;
import Terminals.Exception.TokenDoesNotExistException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * an implementation of a lexical analyzer that given a set of Tokens is able
 * to identify and parse those tokens from an input file.
 */
public class LexicalAnalyzer {

    //---------------- attributes -----------------//

    private Node trie;
    private boolean varFlag = false;
    private boolean initFlag = false;
    private String typeAuxToken;
    private boolean funcJustDeclared = false;
    private String returnValue;
    private Function func;
    private ArrayList<String> auxParameters = new ArrayList<>();
    private String funcName;
    private FuncLevel funcLevel = FuncLevel.NOT_FUNC;
    private CallLevel callLevel = CallLevel.NOT_CALL;
    private Integer scope;
    private boolean equalsFlag = false;
    private StringBuilder sb = new StringBuilder();
    private Variable auxVar;

    private enum FuncLevel{
        NOT_FUNC,
        RETURN,
        NAME,
        PARAM
    }
    private enum CallLevel{
        NOT_CALL,
        NAME,
        OPENING_CURLY,
        PARAM
    }


    //---------------- methods -----------------//

    /**
     * this is the default constructor of the lexical analyzer and it
     * initializes the trie with the tokens needed to parse the input file.
     * @throws TokenDoesNotExistException if a provided token name doesn't
     * exist in the token database.
     */
    public LexicalAnalyzer() throws TokenDoesNotExistException {
        trie = new Node(new HashMap<>(), null);
        for (String s : TokenDatabase.tokenList.values()) {
            initAdd(s, new Token(s));
        }

        //Node that will have all of the numbers which also points to itself
        //memory isn't wasted cuz we're accessing the same node
        //as long as numbers are being entered, we can continue to add
        // numbers in an infinite loop until we find another symbol
        Node number = new Node(null, null);

        HashMap<Character, Node> n = new HashMap<>(){{
            put('0', number);
            put('1', number);
            put('2', number);
            put('3', number);
            put('4', number);
            put('5', number);
            put('6', number);
            put('7', number);
            put('8', number);
            put('9', number);
            put('.', number);
        }};

        number.setNext(n);
        // add an empty constructor to constant
        number.getNext().get('0').setValue(new Token(81));
        number.getNext().get('1').setValue(new Token(81));
        number.getNext().get('2').setValue(new Token(81));
        number.getNext().get('3').setValue(new Token(81));
        number.getNext().get('4').setValue(new Token(81));
        number.getNext().get('5').setValue(new Token(81));
        number.getNext().get('6').setValue(new Token(81));
        number.getNext().get('7').setValue(new Token(81));
        number.getNext().get('8').setValue(new Token(81));
        number.getNext().get('9').setValue(new Token(81));
        number.getNext().get('.').setValue(new Token(81));

        trie.getNext().put('0', number);
        trie.getNext().put('1', number);
        trie.getNext().put('2', number);
        trie.getNext().put('3', number);
        trie.getNext().put('4', number);
        trie.getNext().put('5', number);
        trie.getNext().put('6', number);
        trie.getNext().put('7', number);
        trie.getNext().put('8', number);
        trie.getNext().put('9', number);
        trie.getNext().put('.', number);

    }

    /**
     * this function will break the token string into character and insert
     * them into the trie. moreover, this function will store the value of
     * the token in the leaf of the name.
     *
     * @param s the name of the token.
     * @param value the value of the token.
     */
    private void initAdd(String s, Token value) {
        Node node = trie;
        char[] arr = s.toCharArray();
        char c;
        for (int i = 0; i < arr.length; i++) {
            c = arr[i];

            if (node.getNext().containsKey(c)) {
                node = node.getNext().get(c);
                if (i == arr.length - 1) {
                    node.setValue(value);
                }
            } else {
                if (i == arr.length - 1) {
                    node.getNext().put(c, new Node(new HashMap<>(), value));
                } else {
                    node.getNext().put(c, new Node(new HashMap<>(), null));
                }
                node = node.getNext().get(c);

            }
        }

    }


    /**
     * this method parses the input files into an array of tokens.
     * @param file the input file
     * @return an array of tokens generated from tokenizing the input file.
     * @throws AnalyzerExceptions thrown if a violation to the language
     * lexical format is detected.
     */
    public ArrayList<Token> getTokens(File file) throws AnalyzerExceptions {
        Scanner s = null;
        ArrayList<Token> tokens = new ArrayList<>();
        int line_num =0;
        try {
            s = new Scanner(file);
            // get string
            while (s.hasNextLine()) {
                line_num++;
                String line = s.nextLine();
                // for each word in the line
                // split string by white spaces
                for (String word : line.split(" ")) {
                    getTokensInWord(word, tokens, line_num, line);
                }
            }
            s.close();
        } catch (FileNotFoundException | TokenDoesNotExistException e) {
            e.printStackTrace();
        }
        //printTokens(tokens);
        return tokens;
    }

    /**
     * this function parses the tokens in a given word. Moreover, if more
     * than one token were found in the word then they all will be parsed.
     * @param word the word to parse tokens from.
     * @param tokens the list that the tokens will be added to.
     * @param line_num the number of the line being parsed in the file.
     * @param line the line that is being parsed in the file.
     * @throws AnalyzerExceptions if any of language's lexical
     * structure is violated.
     * @throws TokenDoesNotExistException if the no tokens were found in a
     * word.
     */
    private void getTokensInWord(String word, ArrayList<Token> tokens, int line_num, String line) throws AnalyzerExceptions, TokenDoesNotExistException {

        Node currentNode = trie;
        ArrayList<Token> aux = new ArrayList<>();
        char[] arr = word.toCharArray();
        char c;
        StringBuilder number = new StringBuilder();

        /* for every character in the current word */
        for (int i = 0; i < arr.length; i++) {
            c = arr[i];

            /* if it's possible to move from the current node in the trie to
            this character */
            if (currentNode.getNext().containsKey(c)) {
                // move there
                currentNode = currentNode.getNext().get(c);
                // if we have a constant, then we add it to a string builder,
                // to build that constant
                if (currentNode.getValue() != null && currentNode.getValue().getId() == Token.CONSTANTS) {
                    number.append(c);
                }

                /* if the current node has a value (the current node is a
                leaf to one of the tokens) */
                if (currentNode.getValue() != null) {
                    // check the value stored in that node:
                    if (currentNode.getValue().getId() == Token.EQUALS) {
                        equalsFlag = true;
                    }
                    if (currentNode.getValue().getId() == Token.SEMICOLON) {
                        if (equalsFlag && initFlag) {
                            if (sb.toString().matches("[0-9.]+"))
                                try{
                                    this.auxVar.setValue(this.sb.toString());
                                }catch (AnalyzerExceptions e){
                                    throw new AnalyzerExceptions(e.getMessage()
                                            + System.lineSeparator()
                                            + " at line (" + (line_num) + "): "
                                            + line);
                                }
                            equalsFlag = false;
                            this.sb = new StringBuilder();
                        }
                        initFlag = false;
                    }
                }


                // check if we're in the last character of the array to see if
                // we should add the token
                if (i == arr.length - 1) {

                    /* make sure that the last character of the node ended
                    on a leaf node to a token. Otherwise throw an exception*/
                    if (currentNode.getValue() != null) {

                        // check what the value of that node is:
                        if (currentNode.getValue().getId() == Token.CONSTANTS) {
                            Constant auxConstant = new Constant(number.toString(), false);
                            aux.add(new Token(Token.CONSTANTS, auxConstant.getId()));
                            sb = number;
                            number = new StringBuilder();
                        } else if (currentNode.getValue().getId() == Token.TRUE){
                            Constant auxConstant = new Constant("TRUE", false);
                            aux.add(new Token(Token.CONSTANTS, auxConstant.getId()));

                        } else if( currentNode.getValue().getId() == Token.FALSE){
                            Constant auxConstant = new Constant("FALSE", false);
                            aux.add(new Token(Token.CONSTANTS, auxConstant.getId()));
                        }
                        else {
                            if (currentNode.getValue().getId() == Token.VAR) {
                                varFlag = true;
                                initFlag = true;
                            }else if (currentNode.getValue().getId() == Token.FUNC){
                                funcLevel = FuncLevel.RETURN;
                            }else if (currentNode.getValue().getId() == Token.OPENING_CURLY){
                                funcLevel = FuncLevel.NOT_FUNC;
                                if(callLevel == CallLevel.OPENING_CURLY){
                                    callLevel = CallLevel.PARAM;
                                }
                            }else if (currentNode.getValue().getId() == Token.START){
                                scope = 0;
                            }else if(currentNode.getValue().getId() == Token.CALL){
                                callLevel = CallLevel.NAME;
                            }else if(currentNode.getValue().getId() == Token.CLOSING_CURLY &&
                                    callLevel == CallLevel.PARAM){
                                if(SymbolTable.notValidParameters(funcName, auxParameters)){
                                    if(SymbolTable.problem == SymbolTable.FunctionInfo.WRONG_PARAM_NUM){
                                        throw new AnalyzerExceptions("Function " + funcName + " passed the wrong number of parameters - "+auxParameters+"\nLine  " + line_num + ": " + line);
                                    }
                                    else if(SymbolTable.problem == SymbolTable.FunctionInfo.WRONG_PARAM_TYPE){
                                        throw new AnalyzerExceptions(
                                                "Function " + funcName + " passed the wrong type of parameters \n Line  " + line_num + ": " + line);

                                    }
                                    else if(SymbolTable.problem == SymbolTable.FunctionInfo.FUNCTION_AS_PARAM){
                                        throw new AnalyzerExceptions(
                                                "Function " + funcName + " passed a function as a parameter \n Line  " + line_num + ": " + line);
                                    }
                                }
                                callLevel = CallLevel.NOT_CALL;
                            }

                            if (currentNode.getValue().getId() >= Token.DATATYPE_LOW_RANGE
                                        && currentNode.getValue().getId() <= Token.DATATYPE_HIGH_RANGE ) {
                                if(varFlag) typeAuxToken = currentNode.getValue().getName();
                                else if(funcLevel == FuncLevel.RETURN || funcLevel == FuncLevel.PARAM) {
                                    returnValue = currentNode.getValue().getName();
                                    if (funcLevel == FuncLevel.RETURN) funcLevel = FuncLevel.NAME;
                                }
                            }

                            aux.add(currentNode.getValue());
                        }
                        currentNode = trie;
                    }
                    else {
                        throw new AnalyzerExceptions("Unknown Token - Expected valid token, but found "+ word +" \nLine " + line_num + ": " + line);
                    }
                }
            }
            // if we are on a leaf node to one token
            else {

                if (currentNode.getValue() != null) {
                    // check the token :

                    if (currentNode.getValue().getId() == Token.HASHTAG) {
                        // get symbol name
                        StringBuilder varName = new StringBuilder();
                        for (; i < arr.length; i++) {
                            c = arr[i];
                            varName.append(c);
                        }

                        /*if it's a variable declaration*/
                        if (varFlag && typeAuxToken != null) {
                            // add the variable token
                            if(-1!=SymbolTable.checkSymbolExists(varName.toString(), true, scope)){
                                throw new AnalyzerExceptions("Variable " + varName.toString() + " already declared - \nLine " + line_num+ ": " + line);
                            }
                            // adding scompe name to variable
                            varName.append(scope);
                            auxVar = new Variable(varName.toString(), typeAuxToken, scope);
                            aux.add(new Token(Token.SYMBOLS , auxVar.getId()));
                            varFlag = false;
                            typeAuxToken = null;
                        } else {
                            if(funcLevel != FuncLevel.NOT_FUNC){ // it's a function
                                if(funcLevel == FuncLevel.NAME){
                                    String nameFunc = varName.toString();

                                    func = new  Function(nameFunc, returnValue);
                                    scope = func.getId();
                                }else if(funcLevel == FuncLevel.PARAM){
                                    func.addParameter(varName.toString() + scope, returnValue, scope);
                                }
                            }
                            int varId = -1;

                            if(funcLevel==FuncLevel.NOT_FUNC
                                    || funcLevel==FuncLevel.PARAM){ //Var
                                varId = SymbolTable.checkSymbolExists(varName.toString(), true, scope);
                            }else{
                                varId = SymbolTable.checkSymbolExists(varName.toString(), false, scope);
                                funcLevel = FuncLevel.PARAM;
                            }

                            if(callLevel != CallLevel.NOT_CALL){
                                if(callLevel == CallLevel.NAME){
                                    funcName = varName.toString();
                                    if(!SymbolTable.checkIfFunctionExists(varName.toString())){
                                        throw new AnalyzerExceptions("Function " + varName.toString() + " is not declared - \nLine " + line_num+ ": " + line);
                                    }else{
                                        varId = SymbolTable.checkSymbolExists(varName.toString(), false, scope);
                                    }
                                    callLevel = CallLevel.OPENING_CURLY;
                                }else if(callLevel == CallLevel.PARAM){
                                    auxParameters.add(varName.toString() + scope);
                                }
                            }



                            if(varId != -1){
                                aux.add(new Token(Token.SYMBOLS, SymbolTable.table.get(varId).getId()));
                            }else{
                                //throw variable not declared exception
                                throw new AnalyzerExceptions("Variable " + varName.toString() + " is not declared - \nLine " + line_num+ ": " + line);
                            }

                        }
                        break;

                    // if it is a constant
                    } else if (currentNode.getValue().getId() == Token.CONSTANTS) {
                        // add constant to the constant table

                        Constant auxConstant = new Constant(number.toString(), false);
                        aux.add(new Token(Token.CONSTANTS, auxConstant.getId()));
                        sb = number;
                        number = new StringBuilder();
                        i--;
                        currentNode = trie;
                    }
                    // if it is a ' then store what comes after as a char
                    else if (currentNode.getValue().getId() == Token.APOSTROPHE) {

                        StringBuilder char_name = new StringBuilder();

                        while (i < arr.length && c != '\'') {
                            c = arr[i++];
                            char_name.append(c);
                            if (i < arr.length)
                                c = arr[i];
                        }
                        Constant auxConstant = new Constant(char_name.toString(), true);
                        aux.add(new Token(Token.CONSTANTS, auxConstant.getId()));

                        sb = new StringBuilder();
                        this.sb.append("'" + char_name);

                        currentNode = trie;
                    } else if (currentNode.getValue().getId() == Token.TRUE) {
                        Constant auxConstant = new Constant("TRUE", false);
                        aux.add(new Token(Token.CONSTANTS, auxConstant.getId()));
                        i--;
                        currentNode = trie;
                    } else if (currentNode.getValue().getId() == Token.FALSE) {
                        Constant auxConstant = new Constant("FALSE", false);
                        aux.add(new Token(Token.CONSTANTS, auxConstant.getId()));
                        i--;
                        currentNode = trie;
                    } else { // if it's any other token then add it
                        if (currentNode.getValue().getId() == Token.VAR) {
                            varFlag = true;
                            initFlag = true;
                        } else if (currentNode.getValue().getId() == Token.FUNC){
                            funcLevel = FuncLevel.RETURN;
                        }else if (currentNode.getValue().getId() == Token.OPENING_CURLY){
                            funcLevel = FuncLevel.NOT_FUNC;
                            if(callLevel == CallLevel.OPENING_CURLY){
                                callLevel = CallLevel.PARAM;
                            }
                        }else if(currentNode.getValue().getId() == Token.CLOSING_CURLY &&
                                callLevel == CallLevel.PARAM){
                            if(SymbolTable.notValidParameters(funcName, auxParameters)){
                                if(SymbolTable.problem == SymbolTable.FunctionInfo.WRONG_PARAM_NUM){
                                    throw new AnalyzerExceptions("Function " + funcName + " passed the wrong number of parameters - "+auxParameters+"\nLine  " + line_num + ": " + line);
                                }
                                else if(SymbolTable.problem == SymbolTable.FunctionInfo.WRONG_PARAM_TYPE){
                                    throw new AnalyzerExceptions(
                                            "Function " + funcName + " passed the wrong type of parameters \n Line  " + line_num + ": " + line);

                                }
                                else if(SymbolTable.problem == SymbolTable.FunctionInfo.FUNCTION_AS_PARAM){
                                    throw new AnalyzerExceptions(
                                            "Function " + funcName + " passed a function as a parameter \n Line  " + line_num + ": " + line);
                                }
                            }
                            callLevel = CallLevel.NOT_CALL;
                        }
                        if (currentNode.getValue().getId() >= Token.DATATYPE_LOW_RANGE
                                && currentNode.getValue().getId() <= Token.DATATYPE_HIGH_RANGE) {

                            if(varFlag) typeAuxToken = currentNode.getValue().getName();

                            else if(funcLevel == FuncLevel.RETURN || funcLevel == FuncLevel.PARAM){
                                returnValue = currentNode.getValue().getName();
                                if(funcLevel == FuncLevel.RETURN) funcLevel = FuncLevel.NAME;
                            }
                        }
                        aux.add(currentNode.getValue());
                        currentNode = trie;
                        i--;
                    }
                } else {
                    // if we can't move on, but we're not at a leaf, throw an
                    // exception
                    throw new AnalyzerExceptions("Unknown Token - Expected valid token, but found "+ word +" \nLine " + line_num + ": " + line);

                }
            }
        }
        tokens.addAll(aux);
    }

    /**
     * this function prints the tokens array in a readable format
     * @param tokens the tokens array to be printed
     */
    public void printTokens(ArrayList<Token> tokens) {
        for (Token t : tokens) {
            System.out.print(t.getId() + " : " + t.getName());
            if (t.getExternalId() != -1){
                System.out.println(" ("+t.getExternalId()+")");
            }else{
                System.out.println("");
            }
            System.out.println(System.lineSeparator());
        }
    }
}
