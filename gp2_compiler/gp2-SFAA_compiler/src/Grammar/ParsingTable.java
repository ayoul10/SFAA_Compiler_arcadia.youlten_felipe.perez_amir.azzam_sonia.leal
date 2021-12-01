package Grammar;

import TAC.TAC;
import TAC.TACGenerator;
import Terminals.Exception.AnalyzerExceptions;
import Terminals.Exception.IncorrectGrammarException;
import Terminals.Exception.TokenDoesNotExistException;
import Terminals.SymbolTable;
import Terminals.Token;
import Terminals.TokenDatabase;
import Tree.Tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * this class uses the grammar tree to create a parsing table. Then, provides
 * the ability to use this table in order to check the provided list of tokens.
 */
public class ParsingTable {

    //---------------- attributes -----------------//

    private HashMap<String, ArrayList<Token>> first;
    private HashMap<String , HashMap<Integer, ArrayList<GrammarNode>>> firstProduction;
    private HashMap<String, ArrayList<Token>> follow;
    private HashMap<String, ArrayList<ArrayList<GrammarNode>>> auxGrammarTree =
            new HashMap();
    private Tree<GrammarNode> parsingTree =
            new Tree<GrammarNode>(new GrammarNode(GrammarTree.START, false));
    //each row = non-terminal
    //each column = terminal
    //related by producitons
    private HashMap<String, HashMap<Integer, ArrayList<GrammarNode>>> parsingTable;
    private GrammarTree grammar;


    //---------------- methods -----------------//

    /**
     * this is the default constructor of the class. This constructor
     * generates the first and follow sets, and creates the parsing table.
     * @throws TokenDoesNotExistException if the grammar has a token that is
     * not defined in the token database.
     */
    public ParsingTable() throws TokenDoesNotExistException {
        this.grammar = new GrammarTree();
        this.first = new HashMap<>();
        this.follow = new HashMap<>();
        this.firstProduction = new HashMap<>();
        this.parsingTable = new HashMap<>();
        First();
        Follow();
        InitilizeTable();
        ParsingTableCreator();
    }

    /**
     * this function generates the first set for the full grammar
     */
    private void First(){

        for(Object k : grammar.getTable().keySet()) {
            String key = (String)k;
            if (!first.containsKey(key)){
                firstProduction.put(key, new HashMap<>());
                first.put(key, new ArrayList<>());
                getFirstOfProduction(key);
            }
        }
    }

    /**
     * this function recursively generates the first set of a non-terminal
     * productions
     * @param nonTerminalId the non-terminal to generate the first set to.
     * @return the first set of the specified non-terminal.
     */
    private ArrayList<Token> getFirstOfProduction(String nonTerminalId){
        ArrayList<Token> firstSet= new ArrayList<>();
        HashMap<Integer, ArrayList<GrammarNode>> auxFirstProduction =
                new HashMap<>();
        ArrayList<ArrayList<GrammarNode>> productionRules =
                (ArrayList<ArrayList<GrammarNode>>)this.grammar.getTable().get(nonTerminalId);
        boolean done = true;


        for (ArrayList<GrammarNode> rule :
                productionRules){
            int i = 0;
            done = true;
            do{
                if(rule.get(i).isTerminal()){
                    firstSet.add(rule.get(i).getTerminal());
                    if (rule.get(i).getTerminal()!=null)
                        auxFirstProduction.put(rule.get(i).getTerminal().getId(), rule);
                    done = true;
                }else{
                    String auxNonTerminal = rule.get(i).getNonTerminalID();
                    if(!first.containsKey(auxNonTerminal)){
                        first.put(auxNonTerminal, new ArrayList<>());
                        firstProduction.put(auxNonTerminal, new HashMap<>());

                        //if the aux set contains an epsilon, we need to investigate further
                        ArrayList<Token> firstSetAux = new ArrayList<>(getFirstOfProduction(auxNonTerminal));
                        if (firstSetAux.contains(null)){
                            i++;
                            done = false;
                        }else{
                            done = true;
                        }

                        for (Token t :firstSetAux){
                            auxFirstProduction.put(t.getId(), rule);
                        }

                        firstSet.addAll(firstSetAux);
                    }else{
                        for (Token t :this.first.get(auxNonTerminal)){
                            //if (t != null)
                                auxFirstProduction.put(t.getId(), rule);
                        }
                        firstSet.addAll(this.first.get(auxNonTerminal));
                        done = true;
                    }
                }
            }while(!done && i < rule.size());
        }

        firstProduction.get(nonTerminalId).putAll(auxFirstProduction);
        first.get(nonTerminalId).addAll(firstSet);
        return firstSet;
    }

    /**
     * this function generates the follow set for the full grammar
     */
    private void Follow() {

        for (Object k : grammar.getTable().keySet()) {
            String key = (String) k;
            if (!this.follow.containsKey(key)) {
                this.follow.put(key, new ArrayList<>());
                getFollowOfProduction(key);
            }
        }
    }

    /**
     * this function recursively generates the follow set of a non-terminal
     * productions
     * @param nonTerminalId the id of the non-terminal to generate the follow
     *                     set to
     * @return the follow set of the given non-terminal
     */
    private ArrayList<Token> getFollowOfProduction(String nonTerminalId){
        ArrayList<Token> followSet= new ArrayList<>();
        ArrayList<ArrayList<GrammarNode>> productionRules;
        boolean done = true;

        for (Object k : grammar.getTable().keySet()) { // vertical movement
            String key = (String) k;
            productionRules = (ArrayList<ArrayList<GrammarNode>>) this.grammar
                    .getTable().get(key);
                    //for each grammar rule
            for (ArrayList<GrammarNode> rule : productionRules){
                for (int i = 0; i < rule.size(); i++) {
                    done = true;
                    //if we're at a non-terminal
                    if (!rule.get(i).isTerminal() && rule.get(i).getNonTerminalID().equals(nonTerminalId)){
                        int j = 1;
                        do{
                            done = true;
                            //if we haven't gone over the size of the array, recurse
                            if(i+j < rule.size()){
                                if(rule.get(i+1).isTerminal()){
                                    if (!containsToken(followSet,rule.get(i + 1).getTerminal()))
                                        followSet.add(rule.get(i+1).getTerminal());
                                }else{
                                    String nextNonTerminal =
                                            rule.get(i+1).getNonTerminalID();
                                    //repeat all previous steps with the next position
                                    if (this.first.get(nextNonTerminal).contains(null)){
                                        done = false;
                                        j++;
                                    }
                                    //check for already exisisting
                                    //followSet.addAll(this.first.get(nextNonTerminal));
                                    for (Token t : this.first.get(nextNonTerminal)) {
                                        if(!containsToken(followSet, t))
                                            followSet.add(t);
                                    }
                                }
                            }else{
                                //add the terminals of 9 non terminal id (key)that has the production
                                //add the follow of key
                                if (nonTerminalId != key){
                                    if (!this.follow.containsKey(key)) {
                                    follow.put(key, new ArrayList<>());
                                    //followSet.addAll(getFollowOfProduction(key));
                                    for (Token t : getFollowOfProduction(key)) {
                                        if (!containsToken(followSet, t))
                                            followSet.add(t);
                                    }
                                }else{
                                    //followSet.addAll(this.follow.get(key));
                                    for (Token t : this.follow.get(key)) {
                                        if (!containsToken(followSet,t))
                                            followSet.add(t);
                                    }

                                }
                                    done = true;
                                }

                            }
                        }while (!done);
                    }
                }
            }
        }

        this.follow.get(nonTerminalId).addAll(followSet);
        return followSet;

    }

    /**
     * this function checks if a token is in a list of tokens
     * @param f the list of token
     * @param t the token to look for
     * @return true if the token is in the list of tokens, false otherwise.
     */
    private boolean containsToken(ArrayList<Token> f, Token t){
        if (t == null)
            return true;
        for (Token token : f) {
            if (token.getId() == t.getId()){
                return true;
            }
        }
        return false;
    }

    /**
     * this function initializes the parsing table.
     */
    private void InitilizeTable(){
        for (Object k : grammar.getTable().keySet()) {
            String key = (String) k;
            for (Integer terminal : TokenDatabase.tokenList.keySet()) {
                parsingTable.put(key, new HashMap<>());
                parsingTable.get(key).put(terminal,
                        new ArrayList<>());
            }

        }
    }

    /**
     * checks if any of the productions of one non-terminal contain an epsilon.
     * @param key the key of non-terminal in the parsing table.
     * @return true if any of the productions contain an epsilon, false
     * otherwise.
     */
    private boolean containsEpsilon(String key) {
        ArrayList<ArrayList<GrammarNode>> productionRules = (ArrayList<ArrayList<GrammarNode>>) this.grammar.getTable()
                .get(key);
        for (ArrayList<GrammarNode> rule : productionRules) {
            for (GrammarNode g : rule) {
                if (g.isTerminal() && g.getTerminal() == null) {
                    return true;
                }
            }
            if (rule.contains(null)) {
                return true;
            }
        }
        return false;
    }


    /**
     * this function goes throw all the productions and first/follow sets and
     * creates the parsing table.
     *
     * the parsing table needs to be initialized using the method (InitilizeTable) before
     * calling this method.
     * @throws TokenDoesNotExistException if any token that was found in any
     * production didn't exist in the token database.
     */
    private void ParsingTableCreator() throws TokenDoesNotExistException {

        for (Object k : grammar.getTable().keySet()) {
            String key = (String) k;
            if (containsEpsilon(key)){
                // add epsilon to all the slots with the follow set of this non terminal

                for (Token followElement : follow.get(key)) {
                    if(parsingTable.get(key).get(followElement.getId()) == null)
                        parsingTable.get(key).put(followElement.getId(),
                                new ArrayList<GrammarNode>());
                    parsingTable.get(key).get(followElement.getId()).add(new GrammarNode("epsilon",true));
                }
            }
            // add the production that produces the first under all of the first set of this non terminal
            //i.e. fill in each square of the parse table
            for (Token firstElement : first.get(key)) {
                if (firstElement != null){
                    if(parsingTable.get(key).get(firstElement.getId()) == null)
                        parsingTable.get(key).put(firstElement.getId(),
                                new ArrayList<GrammarNode>());
                    parsingTable.get(key).get(firstElement.getId()).addAll(
                            firstProduction.get(key).get(firstElement.getId())
                    );
                }
            }
        }
    }


    /**
     * this function goes throw a list of tokens and uses the LL1 parsing
     * techniques to parse and check that this list of tokens matches the
     * grammar. Moreover, this method will generate a grammar tree out of
     * this token array. Finally, it will create the TACs for this grammar
     * and return it.
     * @param input the token array
     * @return the TAC of the input token array that corresponds to the
     * language specified grammar.
     * @throws TokenDoesNotExistException if during the parsing process it
     * encounters a token that isn't in the token database
     * @throws IncorrectGrammarException if the grammar in the input token
     * list doesn't match the grammar defind by the language specification.
     * @throws AnalyzerExceptions if while generating the TAC there was a
     * problem.
     */
    public ArrayList<TAC> checkGrammar(ArrayList<Token> input) throws TokenDoesNotExistException, IncorrectGrammarException, AnalyzerExceptions {

        LinkedList<GrammarNode> stack = new LinkedList<>();
        stack.push(new GrammarNode(GrammarTree.START, false));
        //push the
        // starting
        // symbol

        int i = 0;
        ArrayList<GrammarNode> aux = new ArrayList<>();
        // start matching tokens from the parsing table to the input
        while (i < input.size() && !stack.isEmpty()) {
            if (stack.peek() != null) {
                if (stack.peek().isTerminal()) {
                    assert stack.peek() != null;
                    if (stack.peek().getTerminal().getId() == input.get(i).getId()) { // Correct grammar
                        i++;
                        stack.pop();
                        if (stack.peek() != null){
                            parsingTree.moveToParent();
                            assert stack.peek() != null;
                            while(!parsingTree.moveToChild(stack.peek())){
                                parsingTree.moveToParent();
                                if(parsingTree.isOnRoot()){
                                    break;
                                }
                            }
                        }
                    } else
                        throw new IncorrectGrammarException("Expected " + stack.peek().getTerminal().getName()  + " Got "+ input.get(i).getName());
                } else {

                    assert stack.peek() != null;
                    if (parsingTable.get(stack.peek().getNonTerminalID()).get(input.get(i).getId()) != null){
                        if(parsingTable.get(stack.peek().getNonTerminalID())
                                .get(input.get(i).getId()).size() > 0 &&
                                parsingTable.get(stack.peek().getNonTerminalID())
                                .get(input.get(i).getId())
                                .get(0).isTerminal() &&
                                parsingTable.get(stack.peek().getNonTerminalID())
                                        .get(input.get(i).getId())
                                        .get(0).getTerminal().getId() == Token.EPSILON){
                            stack.pop(); //null element
                            if (stack.peek() != null){
                                parsingTree.moveToParent();
                                assert stack.peek() != null;
                                while(!parsingTree.moveToChild(stack.peek())){
                                    parsingTree.moveToParent();
                                    if(parsingTree.isOnRoot()){
                                        break;
                                    }
                                }
                            }
                        }else{
                            //add prodcution to the stack
                            aux = parsingTable
                                    .get(stack.peek().getNonTerminalID())
                                    .get(input.get(i).getId());
                            stack.pop();
                            assert stack.peek() != null;

                            ArrayList<GrammarNode> a = new ArrayList<>();
                            for (GrammarNode n :
                                    aux) {
                                if (n.isTerminal() &&
                                    ((n.getTerminal().getId() == Token.SYMBOLS)
                                    || (n.getTerminal().getId() == Token.CONSTANTS))){
                                    for (int j = i; j < input.size(); j++) {
                                        if(input.get(j).getId() == n.getTerminal().getId()
                                            && input.get(j).getExternalId() != -1){
                                            a.add(new GrammarNode
                                                    (new Token(n.getTerminal().getId(),
                                                            input.get(j).getExternalId())));
                                            input.get(j).addExternalId(-1);
                                            break;
                                        }
                                    }

                                }else{
                                    a.add(n);
                                }
                            }
                            parsingTree.addNodesToCurrent(a);
                            stack.addAll(0,aux);
                            while(!parsingTree.moveToChild(stack.peek())){
                                parsingTree.moveToParent();
                                if(parsingTree.isOnRoot()){
                                    break;
                                }
                            }
                        }
                    }else{
                        throw new IncorrectGrammarException("Expected "+ stack.peek().getNonTerminalID() +" but found " + input.get(i).getName());
                    }
                }


            }
        }
        if (i == input.size() && stack.isEmpty()){
            TACGenerator tac = new TACGenerator(parsingTree);
            return tac.getTAC();
        }
        else{
            throw new IncorrectGrammarException("Stack not empty, but we're at the end of the input");

        }
    }   
}
