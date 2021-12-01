package Terminals;

import Terminals.Exception.AnalyzerExceptions;

/**
 * this class stores all the necessary metadata of a variable symbol. Each
 * variable in the input code will be translated into an object of this class.
 */
public class Variable extends Symbol{

    //---------------- attributes -----------------//
    private Constant value;
    private Integer scope;


    //---------------- methods -----------------//

    /**
     * the default constructor of a variable object.
     * @param name the name of the variable.
     * @param type the type of the variable.
     * @param scope the scope of the variable.
     * @throws AnalyzerExceptions thrown if the variable name starts with a
     * number
     */
    public Variable(String name, String type, Integer scope) throws AnalyzerExceptions{
        super(name, type ,SymbolTable.id);
        if(name.toCharArray().length > 0 && Character.isDigit(name.charAt(0))) {
            throw new AnalyzerExceptions("Variable names cannot start with a number");
        }
        SymbolTable.table.put(SymbolTable.id++, this);
        this.scope = scope;
    }

    /**
     * a variable object constructor with the default scope and without name
     * restrictions.
     * @param name the name of the variable.
     * @param type the type of the variable.
     */
    public Variable(String name, String type){

        super(name, type ,SymbolTable.id);
        SymbolTable.table.put(SymbolTable.id++, this);
        this.scope = -1;
    }

    public Integer getScope(){ return this.scope;}

    /**
     * sets the vaule of a variable
     * @param value the new value.
     * @throws AnalyzerExceptions thrown if the value's type doesn't match
     * the type of the variable.
     */
    public void setValue(String value) throws AnalyzerExceptions{
        boolean isChar = this.getType().equals("char");
        if(isChar && value.charAt(0) == '\'')
            value = value.substring(1, value.length());
        else isChar = false;
        Constant c = new Constant(value, isChar);
        if (c.getType().equals(this.getType()))
            this.value = c;
        else
            throw new AnalyzerExceptions("incompatible types variable "
                    + this.getNameWithoutScope()
                    + " is from type "
                    + this.getType()
                    + " but assigned a value of type "
                    + c.getType());

    }

    public Constant getValue(){
        return value;
    }

    public String getNameWithoutScope() {
        return super.getName().substring(0,
                super.getName().length() - scope.toString().length());
    }
}
