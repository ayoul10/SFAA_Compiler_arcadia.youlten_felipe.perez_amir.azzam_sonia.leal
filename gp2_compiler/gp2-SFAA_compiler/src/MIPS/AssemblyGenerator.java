package MIPS;
import Grammar.*;
import TAC.*;
import Terminals.*;
import Tree.*;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static Terminals.Token.*;

public class AssemblyGenerator {

    final private String data = "    .data";
    final private String text = "    .text";

    final private String int_type_asm = ".word";
    final private String float_type_asm = ".word";
    final private String char_type_asm = ".byte";
    final private String bool_type_asm = ".byte";

    final private String load_int = "lw";
    final private String load_float = "lw";
    final private String load_char = "lb";
    final private String load_bool = "lb";

    final private String load_address = "la";
    final private String jump = "j";

    private boolean function_var_flag = false;
    private int function_var_counter = 0;
    private StringBuilder sb = new StringBuilder();

    private File file;
    private FileWriter fw;

    /**
     * Constructor for the class. creates the necessary objects for generating the MIPS assembly.
     * @throws IOException If the file could not be opened.
     */
    public AssemblyGenerator() throws IOException {
        this.file = new File("../Resources/compiled_file.asm");
        this.fw = new FileWriter(file.getName(), false);
    }

    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    /**
     * Public method for generating the MIPS assembly
     * @param given_tac The TAC provided to generate the MIPS assembly from
     */
    public void convertTacToMIPS(ArrayList<TAC> given_tac){
        generateAssemblyFile(given_tac);
    }

    /**
     * Private method for generating the MIPS assembly. Writes the output to file.
     * @param given_tac The TAC provided to generate the MIPS assembly from
     */
    private void generateAssemblyFile(ArrayList<TAC> given_tac){
        try {

            writeVariables();
            initVariables();
            writeCode(given_tac);

            fw.write("exit:");
            fw.close();

        } catch (IOException e) {
            System.out.println("An error occurred when writing to the file.");
            e.printStackTrace();
        }

    }

    /**
     * Method that writes the MIPS assembly code. This doesnt include the variable declarations.
     * @param given_tac The TAC provided to generate the MIPS assembly from
     * @throws IOException If the file could not be written to.
     */
    private void writeCode(ArrayList<TAC> given_tac) throws IOException {

        for (TAC tac: given_tac) {
            if (tac.getOperation() != null){
                String op = tac.getOperation().getName();

                switch (op){
                    case BLOCK_START:
                        sb.append("Label");
                        sb.append(ConstantTable.table.get(tac.getResult().getExternalId()).getValue()).append(":\n");

                        break;

                    case GOTO:
                        sb.append("\t").append(jump).append(" Label").append(ConstantTable.table.get(tac.getResult().getExternalId()).getValue()).append("\n");
                        break;

                    case RETURN:
                        sb.append("\tjr $ra\n");
                        break;

                    case "call":
                        sb.append("\tjal Label");
                        sb.append(ConstantTable.table.get(tac.getResult().getExternalId()).getValue()).append("\n");
                        break;

                    case GT:
                        sb.append('\t').append("sgt ");

                        sb.append("$t").append((SymbolTable.table.get(tac.getResult().getExternalId()).getId() - 1)).append(" , ");
                        writeValue1ComparisonCondition(tac);
                        writeValue2ComparisonCondition(tac);
                        break;

                    case LT:
                        sb.append('\t').append("slt ");

                        sb.append("$t").append((SymbolTable.table.get(tac.getResult().getExternalId()).getId() - 1)).append(" , ");
                        writeValue1ComparisonCondition(tac);
                        writeValue2ComparisonCondition(tac);

                        break;

                    case IF:
                        sb.append('\t').append("blt ");
                        sb.append("$t").append((SymbolTable.table.get(tac.getValue1().getExternalId()).getId() - 1)).append(" , 1 , ");
                        sb.append("Label").append(ConstantTable.table.get(tac.getResult().getExternalId()).getValue()).append(" \n");

                        break;

                    case PLUS:
                        sb.append("\tadd ");
                        sb.append("$t").append((SymbolTable.table.get(tac.getResult().getExternalId()).getId() - 1)).append(" , ");

                        //check if value1 is either a constant or a symbol
                        if ("symbol".equals(tac.getValue1().getName())){
                            sb.append("$t").append((SymbolTable.table.get(tac.getValue1().getExternalId()).getId() - 1)).append(" , ");
                        }else{
                            sb.append(ConstantTable.table.get(tac.getValue1().getExternalId()).getValue()).append(" , ");
                        }

                        //check if value2 is either a constant or a symbol
                        if ("symbol".equals(tac.getValue2().getName())){
                            sb.append("$t").append((SymbolTable.table.get(tac.getValue2().getExternalId()).getId() - 1)).append(" \n");
                        }else{
                            sb.append(ConstantTable.table.get(tac.getValue2().getExternalId()).getValue()).append(" \n");
                        }

                        break;

                    case MINUS:

                        sb.append("\tsub ");
                        sb.append("$t").append((SymbolTable.table.get(tac.getResult().getExternalId()).getId() - 1)).append(" , ");

                        //check if value1 is either a constant or a symbol
                        if ("symbol".equals(tac.getValue1().getName())){
                            sb.append("$t").append((SymbolTable.table.get(tac.getValue1().getExternalId()).getId() - 1)).append(" , ");
                        }else{
                            sb.append(ConstantTable.table.get(tac.getValue1().getExternalId()).getValue()).append(" , ");
                        }

                        //check if value2 is either a constant or a symbol
                        if ("symbol".equals(tac.getValue2().getName())){
                            sb.append("$t").append((SymbolTable.table.get(tac.getValue2().getExternalId()).getId() - 1)).append(" \n");
                        }else{
                            sb.append(ConstantTable.table.get(tac.getValue2().getExternalId()).getValue()).append(" \n");
                        }

                        break;
                }
            }else{
                //else, is an assignation
                sb.append('\t').append(load_address);

                //check if the destination variable needs to be a 'v' register.
                if(function_var_counter != 0 && SymbolTable.table.get(tac.getResult().getExternalId()).getName().contains("return") && pattern.matcher(SymbolTable.table.get(tac.getResult().getExternalId()).getName().substring(0,1)).matches()){
                    sb.append(" $v").append(SymbolTable.table.get(tac.getResult().getExternalId()).getName().charAt(0)).append(" , ");
                }else {
                    sb.append(" $t").append((SymbolTable.table.get(tac.getResult().getExternalId()).getId() - 1)).append(" , ");
                }

                if ("symbol".equals(tac.getValue1().getName())){
                    //check if the variable we are assigning needs to be a 'v' register.
                    if(function_var_counter != 0 && SymbolTable.table.get(tac.getValue1().getExternalId()).getName().contains("return") && pattern.matcher(SymbolTable.table.get(tac.getValue1().getExternalId()).getName().substring(0,1)).matches()){
                        sb.append(" ($v").append(SymbolTable.table.get(tac.getValue1().getExternalId()).getName().charAt(0)).append(")\n");
                    }else {
                        sb.append(" ($t").append((SymbolTable.table.get(tac.getValue1().getExternalId()).getId() - 1)).append(")\n");
                    }
                }else{
                    sb.append(ConstantTable.table.get(tac.getValue1().getExternalId()).getValue()).append("\n");
                }

            }
            fw.write(sb.toString());
            sb.setLength(0);
        }
    }

    /**
     * Writes comparison code for value2 based on if value2 is a symbol or a constant
     * @param tac The TAC element to write to code
     */
    private void writeValue2ComparisonCondition(TAC tac) {
        //check if value2 is either a constant or a symbol
        if ("symbol".equals(tac.getValue2().getName())){
            sb.append("$t").append((SymbolTable.table.get(tac.getValue2().getExternalId()).getId() - 1)).append(" \n");
        }else{
            sb.append(ConstantTable.table.get(tac.getValue2().getExternalId()).getValue()).append(" \n");
        }
    }

    /**
     * Writes comparison code for value1 based on if value1 is a symbol or a constant
     * @param tac The TAC element to write to code
     */
    private void writeValue1ComparisonCondition(TAC tac) {
        //check if value1 is either a constant or a symbol
        if ("symbol".equals(tac.getValue1().getName())){
            sb.append("$t").append((SymbolTable.table.get(tac.getValue1().getExternalId()).getId() - 1)).append(" , ");
        }else{
            sb.append(ConstantTable.table.get(tac.getValue1().getExternalId()).getValue()).append(" , ");
        }
    }

    /**
     * Writes the variables as tags in MIPS. They are set to the values they equal in the TAC, but are later converted
     * into MIPS registers in the {@link #initVariables()} method
     * @throws IOException If the file could not be written to.
     */
    private void writeVariables() throws IOException {
        fw.write(data+"\n");

        for (Integer key: SymbolTable.table.keySet()) {

            Symbol value = SymbolTable.table.get(key);

            if(value instanceof Variable ) {
                //check if the first char of the name is numeric, this means it is a tac 't'
                if(pattern.matcher(value.getName().substring(0,1)).matches()){
                    sb.append('z').append(value.getName()).append(": ");
                }else{
                    sb.append(value.getName()).append(": ");
                }

                //Check what type the variable is, and initialize correspondingly
                //If the variable is not initialized in the symbol table, we default it to 0
                switch(value.getType()){
                    case CHAR:
                        sb.append(char_type_asm);
                        if ((((Variable) value).getValue() == null)){
                            sb.append(" ").append("0").append("\n");
                        }
                        else{
                            char symbol_table_id = (char)((Variable) value).getValue().getValue();
                            sb.append(" ").append("'").append(symbol_table_id).append("'").append("\n");
                        }
                        break;
                    case INT:
                        sb.append(int_type_asm);
                        if ((((Variable) value).getValue() == null)){
                            sb.append(" ").append("0").append("\n");
                        }
                        else{
                            int symbol_table_id = (int)((Variable) value).getValue().getValue();

                            sb.append(" ").append(symbol_table_id).append("\n");
                        }
                        break;
                    case FLOAT:
                        sb.append(int_type_asm);
                        if ((((Variable) value).getValue() == null)){
                            sb.append(" ").append("0").append("\n");
                        }
                        else{
                            int symbol_table_id = Math.round((float)((Variable) value).getValue().getValue());
                            sb.append(" ").append(symbol_table_id).append("\n");
                        }
                        break;
                    case BOOLEAN:
                        sb.append(bool_type_asm);
                        if ((((Variable) value).getValue() == null)){
                            sb.append(" ").append("0").append("\n");
                        }
                        else{
                            boolean symbol_table_id = (boolean)((Variable) value).getValue().getValue();

                            sb.append(" ").append(symbol_table_id).append("\n");
                        }
                        break;
                }
                fw.write(sb.toString());
                sb.setLength(0);

            }
            // Ignore Function types in the symbol table as they dont matter to this part of the code
            else if(value instanceof Function) {
                int junk =0;
            }

        }
    }

    /**
     * Writes the initialization code for the variables, takes the defined variables and places them in MIPS registers
     * @throws IOException If the file could not be written to.
     */
    private void initVariables() throws IOException {
        fw.write(text + "\n");

        for (Integer key : SymbolTable.table.keySet()) {
            Symbol value = SymbolTable.table.get(key);

            if (value instanceof Variable) {
                String name;

                //Check if the start of the name is numerical. this signifies the variable is a return variable
                //Else is a regular variable
                if(pattern.matcher(value.getName().substring(0,1)).matches()){
                    name = sb.append('z').append(value.getName()).toString();
                    if(value.getName().contains("return")){
                        //this is a function return value so we will assign it a 'v' register
                        function_var_flag = true;
                    }
                }else{
                    name = sb.append(value.getName()).toString();
                }
                sb.setLength(0);

                //Get the type of variable so we know what MIPS load instruction we need to use
                //Then write the destination register
                switch (value.getType()) {
                    case CHAR:
                        sb.append(load_char);
                        writeInitVariable(key, name);
                        break;
                    case INT:
                        sb.append(load_int);
                        writeInitVariable(key, name);
                        break;
                    case FLOAT:
                        sb.append(load_float);
                        writeInitVariable(key, name);
                        break;
                    case BOOLEAN:
                        sb.append(load_bool);
                        writeInitVariable(key, name);
                        break;
                }
                fw.write(sb.toString());
                sb.setLength(0);
                function_var_flag = false;

            // Ignore Function types in the symbol table as they dont matter to this part of the code
            } else if (value instanceof Function) {
                int junk = 0;
            }
        }
    }

    /**
     * Writes the destination registers for the initialization of variables
     * @param key The id/key of the variable in the symbol table
     * @param name The name of the variable in the symbol table
     */
    private void writeInitVariable(int key, String name) {
        if (!function_var_flag)
            sb.append(" $t").append(key - 1 - function_var_counter).append(", ").append(name).append('\n');
        else {
            sb.append(" $v").append(function_var_counter).append(", ").append(name).append('\n');
            function_var_counter++;
        }
    }
}
