package TAC;

import Grammar.GrammarNode;
import TAC.TAC;
import Terminals.*;
import Terminals.Exception.AnalyzerExceptions;
import Terminals.Exception.TokenDoesNotExistException;
import Tree.Tree;

import java.util.ArrayList;

/**
 * this class is responsible for generating the TAC from a grammar tree.
 */
public class TACGenerator {

    //---------------- attributes -----------------//

    private Tree<GrammarNode> parsingTree ;
    private ArrayList<TAC> tac;
    private Constant mainBlock;
    private static int blockIDGlobalIterator = 0;
    private Function currentFunction;
    private Variable returnVar = new Variable("0return", "int");


    //---------------- methods -----------------//

    /**
     * this is the default constructor of this class. This constructor
     * processes a grammar tree and creates an abstract tree out of it.
     * @param parsingTree the grammar tree to be used to generate the TAC.
     * @throws TokenDoesNotExistException if an invalid token is used while
     * generating the TAC.
     * @throws AnalyzerExceptions if a semantic error is detected while
     * analyzing the grammar tree.
     */
    public TACGenerator(Tree<GrammarNode> parsingTree) throws TokenDoesNotExistException, AnalyzerExceptions {
        this.tac = new ArrayList<>();
        mainBlock = new Constant((blockIDGlobalIterator++)+"",false);
        this.parsingTree = parsingTree;
        this.parsingTree.moveToRoot();
        TreePreprocessing();
        this.parsingTree.moveToRoot();
        GenerateAbstractSyntaxTree(0);
        this.parsingTree.moveToRoot();
        TreePostprocessing();
        this.parsingTree.moveToRoot();


        GenerateTAC();
        PrintTAC(tac);
    }

    /**
     * this method pre-processes the grammar tree by deleting the statements,
     * statement, or aux_statements non-terminals.
     */
    private void TreePreprocessing() {
        for (int i = 0; i < parsingTree.getNumberOfChildren(); i++) {
            if(!parsingTree.getChild(i).isTerminal()){
                parsingTree.moveToChild(i);
                if(parsingTree.getCurrentValue().getNonTerminalID().equals(
                        "statements")){
                    parsingTree.replaceCurrentWithChildren();
                }else if((parsingTree.getCurrentValue().getNonTerminalID().equals(
                        "statement"))){
                    parsingTree.replaceCurrentWithChildren();
                }else if((parsingTree.getCurrentValue().getNonTerminalID().equals(
                        "aux_statements"))) {
                    parsingTree.replaceCurrentWithChildren();
                }
                TreePreprocessing();
                parsingTree.moveToParent();
            }
        }
    }

    /**
     * this method does the post processing by deleting all the parentheses
     * from the math expressions and ensuring that the hierarchy is still
     * represented.
     * @throws TokenDoesNotExistException if an illegal token is used in the
     * process.
     */
    private void TreePostprocessing() throws TokenDoesNotExistException {
        for (int i = 0; i < parsingTree.getNumberOfChildren(); i++) {

            parsingTree.moveToChild(i);
            if(parsingTree.getCurrentValue().getTerminal().getId() == Token.OPENING_PAR
                    && parsingTree.getNumberOfChildren() != 0){
                parsingTree.deleteChild(new GrammarNode(new Token(")")));
                i = parsingTree.ReplaceCurrentWithChild(0);
            }
            TreePostprocessing();
            parsingTree.moveToParent();

        }
    }

    /**
     * this method recursively generates the abstract syntax tree form the
     * provided grammar tree. this is done by removing all the non-terminals
     * and only leaving the terminals in the tree.
     * @param pos the position of the current node.
     */
    private void GenerateAbstractSyntaxTree(int pos){

        for(int i = 0; i < parsingTree.getNumberOfChildren(); i++) {
            if(parsingTree.getCurrentValue().isTerminal()){
                // if the current node is a terminal
                parsingTree.moveToChild(i);

                if(!parsingTree.getCurrentValue().isTerminal()
                        && parsingTree.getNumberOfChildren() == 0){
                    parsingTree.deleteCurrent();
                }else {
                    GenerateAbstractSyntaxTree(i);
                    parsingTree.moveToParent();
                }

            }else{
                if(parsingTree.getChild(i).isTerminal()){
                    // if the current node is not a terminal
                    // but the child we are looking at is a terminal
                    i = parsingTree.ReplaceCurrentWithChild(i);
                }else{
                    // if the current node is not a terminal
                    // and the child we are looking at is not a terminal as
                    // well
                    parsingTree.moveToChild(i);
                    if(!parsingTree.getCurrentValue().isTerminal()
                            && parsingTree.getNumberOfChildren() == 0){
                        parsingTree.deleteCurrent();
                    }else {
                        GenerateAbstractSyntaxTree(i);
                        parsingTree.moveToParent();
                        if(parsingTree.getChild(i).isTerminal()){
                            i = parsingTree.ReplaceCurrentWithChild(i);
                        }
                    }
                }
            }
        }
    }


    /**
     * this method prints the TAC list in a readable format
     * @param tac the list of TACs
     */
    private void PrintTAC(ArrayList<TAC> tac){
        for (TAC t :
                tac) {
            System.out.print(t.valueToString(t.getResult()) + ": ");
            System.out.print(t.valueToString(t.getValue1()) );
            System.out.print(t.valueToString(t.getOperation()));
            System.out.print(t.valueToString(t.getValue2()) );

            System.out.println("");

        }

    }

    /**
     * this method gernates the TACs for all the grammar tree.
     * @throws TokenDoesNotExistException if an invalid token is used while
     * generating the TAC.
     * @throws AnalyzerExceptions if a semantic error is detected while
     * analyzing the grammar tree.
     */
    private void GenerateTAC() throws TokenDoesNotExistException, AnalyzerExceptions {
        // check for Main and Func
        Token current = parsingTree.getCurrentValue().getTerminal();
        if(current.compareTo(new Token(Token.START)) == 0){
            this.tac.addAll(GenerateMainTAC(parsingTree.getCurrentBranch()));
        }else if(current.compareTo(new Token(Token.FUNC)) == 0) {
            // Not implemented YET
            this.tac.addAll(GenerateFuncTAC(parsingTree.getCurrentBranch()));
        }
    }

    /**
     * this method genrates the TACs of the main.
     * @param mainBranch the starting node of the main.
     * @return a list of the main TACs
     * @throws TokenDoesNotExistException if an invalid token is used while
     * generating the TAC.
     * @throws AnalyzerExceptions if a semantic error is detected while
     * analyzing the grammar tree.
     */
    private ArrayList<TAC> GenerateMainTAC(Tree<GrammarNode> mainBranch) throws TokenDoesNotExistException, AnalyzerExceptions {
        // check statments

        return new ArrayList<>(SwitchTAC(mainBranch, 0, Token.END));
    }

    /**
     * this method generates the TACs for the functions, if there are any,
     * and then it will generate the main TACs.
     * @param funcBranch the starting node of the function.
     * @return a list of the generated TACs for the functions.
     * @throws TokenDoesNotExistException if an invalid token is used while
     * generating the TAC.
     * @throws AnalyzerExceptions if a semantic error is detected while
     * analyzing the grammar tree.
     */
    private ArrayList<TAC> GenerateFuncTAC(Tree<GrammarNode> funcBranch) throws TokenDoesNotExistException, AnalyzerExceptions {
        // check statements
        ArrayList<TAC> auxTAC = new ArrayList<TAC>();
        TAC aux = new TAC(new Token(Token.CONSTANTS, this.mainBlock.getId()), null, null, new Token(Token.GOTO));
        auxTAC.add(aux);
        currentFunction =
                (Function)SymbolTable.table.get(funcBranch.getChild(2).getTerminal().getExternalId());
        currentFunction.setBlockId(blockIDGlobalIterator++);
        Constant thisBlock = new Constant(currentFunction.getBlockId()+"",false);
        aux = new TAC(new Token(Token.CONSTANTS, thisBlock.getId()), null, null, new Token(Token.BLOCK_START));
        auxTAC.add(aux);//startingBlock

        funcBranch.moveToChild(3);
        int i = 0;
        for (; i < funcBranch.getNumberOfChildren(); i++) {
            if(funcBranch.getChild(i).getTerminal().getId() == Token.OPENING_CURLY){
                break;
            }
        }
        auxTAC.addAll(SwitchTAC(funcBranch, i, "}"));
        funcBranch.moveToParent();
        //main block
        aux = new TAC(new Token(Token.CONSTANTS, this.mainBlock.getId()), null, null, new Token(Token.BLOCK_START));
        auxTAC.add(aux);
        auxTAC.addAll(SwitchTAC(funcBranch, 5, Token.END));

        return auxTAC;
    }

    /**
     * this method generates the TAC to a function call
     * @param callBranch the call starting node.
     * @return the list of TACs to full fill this call
     * @throws TokenDoesNotExistException if an invalid token is used while
     * generating the TAC.
     * @throws AnalyzerExceptions if a semantic error is detected while
     * analyzing the grammar tree. This is also thrown if the return type and
     * the variable used to store it don't match.
     */
    private ArrayList<TAC> GenerateCallTAC(Tree<GrammarNode> callBranch)
            throws AnalyzerExceptions, TokenDoesNotExistException
    {
        ArrayList<TAC> tacs = new ArrayList<TAC>();

        Function callFunc =
                (Function)SymbolTable.table.get(callBranch.getChild(0).getTerminal().getExternalId());

        int i = 0;
        TAC aux;


        if(callBranch.getChild(2).getTerminal().getId() != Token.CLOSING_CURLY) {
            aux = new TAC(new Token(Token.SYMBOLS,
                    callFunc.getParameters().get(i).getId()),
                    callBranch.getChild(2).getTerminal());
            tacs.add(aux);
            callBranch.moveToChild(2);
            i++;
            while (callBranch.getNumberOfChildren() != 0 && i < callFunc.getNumParameters()) {
                if (callBranch.getChild(0).getTerminal().getId() == Token.SYMBOLS) {
                    aux = new TAC(new Token(Token.SYMBOLS,
                            callFunc.getParameters().get(i).getId()),
                            callBranch.getChild(0).getTerminal());
                    tacs.add(aux);
                    i++;
                }
                callBranch.moveToChild(0);
            }
        }

        Constant blockId = new Constant(callFunc.getBlockId() + "", false);
        aux = new TAC(new Token(Token.CONSTANTS, blockId.getId()),
                null,
                null,
                new Token(Token.CALL));
        tacs.add(aux);
        callBranch.moveToRoot();
        if(callBranch.getChild(4).getTerminal().getId() != Token.SEMICOLON){
            callBranch.moveToChild(4);
            if(SymbolTable.table
                    .get(callBranch.getChild(0)
                            .getTerminal().getExternalId()).getType()
                    .equals(returnVar.getType())){
                aux = new TAC(
                        callBranch.getChild(0).getTerminal(),
                        new Token(Token.SYMBOLS, returnVar.getId())
                );
                tacs.add(aux);
            }else throw new
                    AnalyzerExceptions(
                            "You can not store a return value of type "
                            + returnVar.getType()
                            + " in a variable of type "
                            + returnVar.getType()
                            + System.lineSeparator());

        }
        return tacs;
    }

    /**
     * this methods generates the TAC for a meanwhile header.
     * @param meanwhileBranch the starting node of the meanwhile
     * @return a list of TACs that represent the meanwhile header. the
     * condition and the inside of the loop.
     * @throws TokenDoesNotExistException if an invalid token is used while
     * generating the TAC.
     * @throws AnalyzerExceptions if a semantic error is detected while
     * analyzing the grammar tree.
     */
    private ArrayList<TAC> GenerateMeanwhileTAC(Tree<GrammarNode> meanwhileBranch)
            throws AnalyzerExceptions, TokenDoesNotExistException
    {
        ArrayList<TAC> tacs = new ArrayList<>();

        Constant thisBlock = new Constant((blockIDGlobalIterator++)+"",false);
        TAC aux = new TAC(new Token(Token.CONSTANTS, thisBlock.getId()), null, null, new Token(Token.BLOCK_START));
        tacs.add(aux);

        //first child is a parenthesis, so we instead return its first real
        ArrayList<TAC> boolTacs = GenerateBoolTAC(meanwhileBranch.getChildBranch(1));
        tacs.addAll(boolTacs);

        Constant nextBlock = new Constant((blockIDGlobalIterator++)+"",false);
        aux = new TAC(new Token(Token.CONSTANTS, nextBlock.getId()), boolTacs.get((boolTacs.size())-1).getResult(), null, new Token(Token.IF));
        tacs.add(aux);

        tacs.addAll(SwitchTAC(meanwhileBranch, 3, "done"));


        aux = new TAC(new Token(Token.CONSTANTS, thisBlock.getId()), null, null, new Token(Token.GOTO));
        tacs.add(aux);

        aux = new TAC(new Token(Token.CONSTANTS, nextBlock.getId()), null, null, new Token(Token.BLOCK_START));
        tacs.add(aux);


        return tacs;
    }

    /**
     * this method counts the number of elseif/ else statements are proceeding
     * the if main node.
     * @param ifBranch the starting node of this if.
     * @return a list of all the tags proceeding the if
     * @throws AnalyzerExceptions if a semantic error is detected while
     * analyzing the grammar tree.
     */
    private ArrayList<String> findIfSuccessors(Tree<GrammarNode> ifBranch) throws AnalyzerExceptions {
        GrammarNode lastNode =
                ifBranch.getChild(ifBranch.getNumberOfChildren()-1);
        ArrayList<String> ifSuccessors = new ArrayList<>();
        if(lastNode.isTerminal()){
            String terminal = lastNode.getTerminal().getName();

            if(terminal.equals("fi")){
                ifSuccessors.add("fi");
                return ifSuccessors;
            }
            else if(terminal.equals("elseif")){
                ifBranch.moveToChild(ifBranch.getNumberOfChildren() - 1);
                ifSuccessors.add("elseif");
                ifSuccessors.addAll(findIfSuccessors(ifBranch));
                ifBranch.moveToParent();
            }
            else if(terminal.equals("else")){
                ifBranch.moveToChild(ifBranch.getNumberOfChildren() - 1);
                ifSuccessors.add("else");
                ifSuccessors.addAll(findIfSuccessors(ifBranch));
                ifBranch.moveToParent();
            }else{
                throw new AnalyzerExceptions("if has weird children");
            }
        }
        return ifSuccessors;
    }

    /**
     * this method generates all the TACs and blocks needed for an if
     * statement and all of its proceeding elseif and else statements.
     * @param ifBranch the starting node of the if statement.
     * @return a list of TACs that represent the if statements.
     * @throws TokenDoesNotExistException if an invalid token is used while
     * generating the TAC.
     * @throws AnalyzerExceptions if a semantic error is detected while
     * analyzing the grammar tree.
     */
    private ArrayList<TAC> GenerateIfTAC(Tree<GrammarNode> ifBranch)
            throws AnalyzerExceptions, TokenDoesNotExistException
    {
        ArrayList<TAC> auxTac = new ArrayList<>();
        ArrayList<String> ifSuccessors = findIfSuccessors(ifBranch);
        ArrayList<Constant> blocks = new ArrayList<>();

        for (int i = 0; i < ifSuccessors.size(); i++) {
            blocks.add(new Constant((blockIDGlobalIterator++)+"",false));
        }

        for (int i = 0; i < ifSuccessors.size() ; i++) {
            if(!ifBranch.getCurrentValue().getTerminal().getName().equals(
                    "else")){
                auxTac.addAll(GenerateBoolTAC(ifBranch.getChildBranch(1)));
                auxTac.add(new TAC(new Token(Token.CONSTANTS,
                                blocks.get(i).getId()),
                                auxTac.get(auxTac.size() -1).getResult(),
                                null,
                                new Token(Token.IF)));
                auxTac.addAll(SwitchTAC(ifBranch,3, ifSuccessors.get(i)));
            }else{
                auxTac.addAll(SwitchTAC(ifBranch,0, ifSuccessors.get(i)));
            }


            if(!ifBranch.getCurrentValue().getTerminal().getName().equals(
                    "else")) {
                //goto the end block
                auxTac.add(new TAC(new Token(Token.CONSTANTS,
                        blocks.get(blocks.size()-1).getId()),
                        null,
                        null,
                               new Token(Token.GOTO)));
            }

            //create the SKIP block for this condition
            auxTac.add(new TAC(new Token(Token.CONSTANTS, blocks.get(i).getId()),
                            null, null, new Token(Token.BLOCK_START)));

            ifBranch.moveToChild(ifBranch.getNumberOfChildren() -1);
        }


        return auxTac;
    }

    /**
     * this method generates the TAC of a math expression, respecting the
     * hierarchy of the expression.
     * @param mathBranch the starting node of the expression.
     * @return a list of TACs that represent that math expression.
     * @throws TokenDoesNotExistException if an invalid token is used while
     * generating the TAC.
     * @throws AnalyzerExceptions if a semantic error is detected while
     * analyzing the grammar tree.
     */
    private ArrayList<TAC> GenerateMathTAC(Tree<GrammarNode> mathBranch)
            throws AnalyzerExceptions, TokenDoesNotExistException
    {
        ArrayList<TAC> auxTac = new ArrayList<>();
        if(mathBranch.getNumberOfChildren() == 0){
            TAC t = new TAC();
            t.add(mathBranch.getCurrentValue().getTerminal());
            auxTac.add(t);
            return auxTac;
        }
        else{
            for (int i = 0; i < mathBranch.getNumberOfChildren(); i++) {
                ArrayList<TAC> aux =
                        GenerateMathTAC(mathBranch.getChildBranch(i));
                //if each field of the TAC has been filled in
                // combine yourself with  just generated children
                if (aux.get(aux.size()-1).isFull()){
                    TAC t = new TAC();
                    t.add(mathBranch.getCurrentValue().getTerminal());
                    if(auxTac.isEmpty()){
                        t.add(aux.get(aux.size()-1).getResult());
                    }else{
                        //check if the aux tac is full
                        if(auxTac.get(aux.size()-1).isFull()){
                            t.add(auxTac.get(aux.size()-1).getResult());
                        }else{
                            throw new AnalyzerExceptions("two not full TACs " +
                                    "in hand.");
                        }
                    }
                    aux.add(t);
                }else{
                    if(auxTac.isEmpty())
                        aux.get(aux.size()-1).add(mathBranch.getCurrentValue().getTerminal());
                    else{
                        if(auxTac.get(auxTac.size() -1).isFull())
                            aux.get(aux.size()-1).add(auxTac.get(auxTac.size()-1).getResult());
                        else{
                            aux.get(aux.size()-1).add(auxTac.get(auxTac.size()-1).getValue2());

                            TAC t = new TAC();
                            t.add(auxTac.get(auxTac.size()-1).getOperation());
                            t.add(aux.get(aux.size()-1).getResult());

                            auxTac.remove(auxTac.size()-1);

                            aux.add(t);
                        }
                    }
                }

                auxTac.addAll(aux);

            }
        }


        return auxTac;
    }

    /**
     * this method generates the TACs for an assignation expression.
     * @param assBranch the starting of the assignation expression.
     * @return  a list of TACs that represent the assignation expression.
     * @throws TokenDoesNotExistException if an invalid token is used while
     * generating the TAC.
     * @throws AnalyzerExceptions if a semantic error is detected while
     * analyzing the grammar tree.
     */
    private ArrayList<TAC> GenerateAssignationTAC(Tree<GrammarNode> assBranch)
            throws AnalyzerExceptions, TokenDoesNotExistException
    {


        ArrayList<TAC> auxTac =
                new ArrayList<>(GenerateMathTAC(assBranch.getChildBranch(1)));

        if( auxTac.get(auxTac.size() -1).isFull())
            auxTac.add(
                new TAC(assBranch.getCurrentValue().getTerminal(),
                         auxTac.get(auxTac.size() -1).getResult() )
                );
        else{
            Token t = auxTac.get(auxTac.size() -1).getValue2();
            auxTac.remove(auxTac.size() -1);
            auxTac.add(
                    new TAC(assBranch.getCurrentValue().getTerminal(), t)
            );
        }

        return auxTac;
    }

    /**
     * this method generates the necessary TACs to the return statement, this
     * include assigning the return value to the global return auxiliary
     * placeholder variable.
     * @param returnBranch the starting of the return statement.
     * @return a list of TACs to carry out the return operation.
     * @throws TokenDoesNotExistException if an invalid token is used while
     * generating the TAC.
     * @throws AnalyzerExceptions if a semantic error is detected while
     * analyzing the grammar tree.
     */
    private ArrayList<TAC> GenerateReturnTAC(Tree<GrammarNode> returnBranch)
            throws AnalyzerExceptions, TokenDoesNotExistException {
        ArrayList<TAC> auxTac = new ArrayList<TAC>();
        TAC aux = new TAC(
                new Token(Token.SYMBOLS, returnVar.getId()),
                returnBranch.getChild(0).getTerminal()
        );
        auxTac.add(aux);

        aux =
                new TAC(null,
                        null ,
                        null,
                        new Token(Token.RETURN));
        auxTac.add(aux);

        return auxTac;
    }

    /**
     * generates the boolean expression TACs.
     * @param boolBranch the staring node of the boolean expression.
     * @return a list of TACs to represent the boolean expression.
     * @throws TokenDoesNotExistException if an invalid token is used while
     * generating the TAC.
     * @throws AnalyzerExceptions if a semantic error is detected while
     * analyzing the grammar tree.
     */
    private ArrayList<TAC> GenerateBoolTAC(Tree<GrammarNode> boolBranch)
            throws AnalyzerExceptions, TokenDoesNotExistException
    {
        ArrayList<TAC> auxTac = new ArrayList<>();


        if(boolBranch.getNumberOfChildren() == 0){
            TAC t = new TAC();
            t.add(boolBranch.getCurrentValue().getTerminal());
            auxTac.add(t);
            return auxTac;
        }
        else{
            for (int i = 0; i < boolBranch.getNumberOfChildren(); i++) {
                ArrayList<TAC> aux = new ArrayList<TAC>(GenerateBoolTAC(boolBranch.getChildBranch(i)));
                //if each field of the TAC has been filled in
                // combine yourself with  just generated children
                if (aux.get(aux.size()-1).isFull()){
                    TAC t = new TAC();
                    t.add(boolBranch.getCurrentValue().getTerminal());
                    if(auxTac.isEmpty()){
                        t.add(aux.get(aux.size()-1).getResult());
                    }else{
                        //check if the aux tac is full
                        if(auxTac.get(aux.size()-1).isFull()){
                            t.add(auxTac.get(aux.size()-1).getResult());
                        }else{
                            throw new AnalyzerExceptions("two not full TACs " +
                                    "in hand.");
                        }
                    }
                    aux.add(t);
                }else{
                    if(auxTac.isEmpty())
                        aux.get(aux.size()-1).add(boolBranch.getCurrentValue().getTerminal());
                    else{
                        if(auxTac.get(auxTac.size() -1).isFull())
                            aux.get(aux.size()-1).add(auxTac.get(auxTac.size()-1).getResult());
                        else{
                            aux.get(aux.size()-1).add(auxTac.get(auxTac.size()-1).getValue2());

                            TAC t = new TAC();
                            t.add(auxTac.get(auxTac.size()-1).getOperation());
                            t.add(aux.get(aux.size()-1).getResult());

                            auxTac.remove(auxTac.size()-1);

                            aux.add(t);
                        }
                    }
                }

                auxTac.addAll(aux);

            }
        }


        return auxTac;
    }

    /**
     * this method acts as a switch, it has a starting point and ending token.
     * For each node in the between those two tokens it will call the
     * appropriate methode to generate the TAC for that node.
     * @param parentTree the parent node of the current branch of nodes.
     * @param startingChild the starting child in the branch
     * @param endingToken the ending token
     * @return a list of all the TACs in between those two nodes.
     * @throws TokenDoesNotExistException if an invalid token is used while
     * generating the TAC.
     * @throws AnalyzerExceptions if a semantic error is detected while
     * analyzing the grammar tree.
     */
    private ArrayList<TAC> SwitchTAC(Tree<GrammarNode> parentTree,
                                     int startingChild, String endingToken)
            throws TokenDoesNotExistException, AnalyzerExceptions
    {
        ArrayList<TAC> auxTac = new ArrayList<>();

        for (int i = startingChild; i < parentTree.getNumberOfChildren(); i++) {
            if(parentTree.getChild(i).getTerminal().getName().equals(Token.MEANWHILE)){
                //run meanwhile
                auxTac.addAll(GenerateMeanwhileTAC(parentTree.getChildBranch(i)));
            }else if(parentTree.getChild(i).getTerminal().getName().equals(Token.IF)){
                //call the generate if
                auxTac.addAll(GenerateIfTAC(parentTree.getChildBranch(i)));
            }else if(parentTree.getChild(i).getTerminal().getId()== (Token.SYMBOLS)){
                //call the generate assignation
                auxTac.addAll(GenerateAssignationTAC(parentTree.getChildBranch(i)));
            }else if(parentTree.getChild(i).getTerminal().getName().equals(Token.RETURN)){
                auxTac.addAll(GenerateReturnTAC(parentTree.getChildBranch(i)));

            }else if(parentTree.getChild(i).getTerminal().getId()== (Token.CALL)){
                //call the generate call
                auxTac.addAll(GenerateCallTAC(parentTree.getChildBranch(i)));
            }
            else if(parentTree.getChild(i).getTerminal().getName().equals(endingToken)){
                    break;
            }
        }

        return auxTac;
    }

    public ArrayList<TAC> getTAC() {
        return tac;
    }
}