package Terminals;

import Terminals.Exception.AnalyzerExceptions;

import java.lang.reflect.Type;

/**
 * this class is used to represent the Constants in our language. This class
 * stores the metadata needed for every constant.
 */
public class Constant {

    //---------------- attributes -----------------//

    /*
     * there are 4 different values that each constant object has, however,
     * only one of those attributes will have a value (depending on the type
     * of the constant).
     *
     */
    private int value1;
    private float value2;
    private char value3;
    private boolean value4;

    private String type;
    private int id;


    //---------------- methods -----------------//

    /**
     * this the main constructor of the class. As the class description
     * requires the constant to have a value, the constructor of the class
     * requires this value to be passed.
     *
     * @param value a string containing the value of the constant
     * @param isChar a boolean to determine if the constant is a character as
     *              the characters could also be numeric digits.
     *
     * @throws AnalyzerExceptions this exception is thrown in case an invalid
     * value is passed as a parameter
     */
    public Constant(String value, boolean isChar) throws AnalyzerExceptions {
        value1 = Integer.MIN_VALUE;
        value2 = Float.MIN_VALUE;
        value3 = Character.MIN_VALUE;
        value4 = false; // if it is none of them then it's a boolean

        if (value != null){
            // if the input value is a char
            if (isChar){
                if (value.length() == 1) {
                    this.value3 = value.charAt(0);
                    this.type = Token.CHAR;
                }
                else{
                    throw new AnalyzerExceptions("Invalid Constant: A char can not be longer than one character");
                }

            // if the input string is a boolean
            }else if (value.compareTo( "FALSE") == 0 || value.compareTo("TRUE") == 0){
                this.value4 = Boolean.parseBoolean(value);
                this.type = Token.BOOLEAN;

            // if the value contains a "." it means that the value is a float
            }else if(value.contains(".")){
                try{
                    this.value2 = Float.parseFloat(value);
                    this.type = Token.FLOAT;
                }catch(NumberFormatException e){
                    throw new AnalyzerExceptions("Invalid float");
                }

            // if it's none of the previous options then the value is an int
            }else{
                try {
                    this.value1 = Integer.parseInt(value);
                    this.type = Token.INT;
                } catch (NumberFormatException e) {
                    throw new AnalyzerExceptions("Invalid constant");
                }
            }
        }else{
            throw new AnalyzerExceptions("Null Constant Exception");
        }

        ConstantTable.table.put(ConstantTable.id, this);
        this.id = ConstantTable.id++;
    }

    public int getId(){
        return id;
    }
    
    public String getType(){
        return type;
    }


    public Object getValue(){
        switch (type){
            case "int":
                return value1;

            case "float":
                return value2;

            case "char":
                return value3;

            case "boolean":
                return value4;

            default:
                return null;
        }
    }
}
