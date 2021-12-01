package Terminals;

import Terminals.Exception.TokenDoesNotExistException;


/**
 * this class stores the metadata needed to create a token. Moreover, it
 * stores general information about all tokens.
 *
 * this class implements the comparable interface so it would be possible to
 * compare two different objects of this class.
 */
public class Token implements Comparable<Token>{

    //---------------- attributes -----------------//

    private int tokenId;
    private String tokenName;
    private int externalId;

    //defines for Token ranges and specific values we want
    //numeric defines :
    public static final int CONSTANTS = 81;
    public static final int SYMBOLS = 80;
    public static int VAR = 30;
    public static int DATATYPE_LOW_RANGE = 36;
    public static int DATATYPE_HIGH_RANGE = 41;
    public static int HASHTAG = 3;
    public static int APOSTROPHE = 13;
    public static int TRUE = 71;
    public static int FALSE = 70;
    public static int EPSILON = 14;
    public static int FUNC = 28;
    public static int OPENING_CURLY = 4;
    public static int CLOSING_CURLY = 5;
    public static int START = 20;
    public static int CALL = 31;
    public static int EQUALS = 12;
    public static int SEMICOLON = 0;
    public static int OPENING_PAR = 1;
    public static int CLOSING_PAR = 2;

    //string defines :
    public static final String END = "end";
    public static final String CHAR = "char";
    public static final String BOOLEAN = "boolean";
    public static final String INT = "int";
    public static final String FLOAT = "float";
    public static final String MEANWHILE = "meanwhile";
    public static final String IF = "if";
    public static final String GOTO = "GOTO";
    public static final String BLOCK_START = "BLOCK_START";
    public static final String GT = ">";
    public static final String LT = "<";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String RETURN = "return";



    //---------------- methods -----------------//

    /**
     * this is the default constructor of this class using a token name.
     * @param tokenName the name of the token to be created.
     * @throws TokenDoesNotExistException if the token name provided doesn't
     * exist.
     */
    public Token(String tokenName) throws TokenDoesNotExistException{
        if(TokenDatabase.tokenList.containsValue(tokenName)){
            for(Integer  key : TokenDatabase.tokenList.keySet()){
                if(TokenDatabase.tokenList.get(key).compareTo(tokenName) == 0)
                {
                    this.tokenName = tokenName;
                    this.tokenId = key;
                    this.externalId = -1;
                }
            }
        }
        else{
            throw new TokenDoesNotExistException();
        }
    }

    /**
     * this is the default constructor of this class using a token id.
     * @param tokenId the id of the token to be created.
     * @throws TokenDoesNotExistException if the token id provided doesn't
     * exist.
     */
    public Token(int tokenId) throws TokenDoesNotExistException{
        if(TokenDatabase.tokenList.containsKey(tokenId)){
            this.tokenName = TokenDatabase.tokenList.get(tokenId);
            this.tokenId = tokenId;
            this.externalId = -1;
        }
        else{
            throw new TokenDoesNotExistException();
        }
    }

    /**
     * this constructor is used to link the current token (providing the
     * token name) to an external Id.
     * @param tokenName the name of the token
     * @param externalId the external id of that token
     * @throws TokenDoesNotExistException if the token name provided doesn't
     * exist.
     */
    public Token(String tokenName, int externalId) throws TokenDoesNotExistException {
        if (TokenDatabase.tokenList.containsValue(tokenName)) {
            for (Integer key : TokenDatabase.tokenList.keySet()) {
                if (TokenDatabase.tokenList.get(key).compareTo(tokenName)==0) {
                    this.tokenName = tokenName;
                    this.tokenId = key;
                    this.externalId = externalId;
                }
            }
        } else {
            throw new TokenDoesNotExistException();
        }
    }
    /**
     * this constructor is used to link the current token (providing the
     * token id) to an external Id.
     * @param tokenId the id of the token
     * @param externalId the external id of that token
     * @throws TokenDoesNotExistException if the token id provided doesn't
     * exist.
     */
    public Token(int tokenId, int externalId) throws TokenDoesNotExistException {
        if (TokenDatabase.tokenList.containsKey(tokenId)) {
            this.tokenName = TokenDatabase.tokenList.get(tokenId);
            this.tokenId = tokenId;
            this.externalId = externalId;
        } else {
            throw new TokenDoesNotExistException();
        }
        if (externalId == -1){
            throw new TokenDoesNotExistException();
        }
    }

    public int getId(){
        return this.tokenId;
    }

    public String getName(){
        return this.tokenName;
    }

    public int getExternalId(){
        return this.externalId;
    }

    public void addExternalId(int externalId){
            this.externalId = externalId;
    }


    @Override
    public int compareTo(Token o) {
        return (this.getId() == o.getId()) ? 0 : 1;
    }
}

