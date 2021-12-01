package Grammar;

import Terminals.Exception.TokenDoesNotExistException;
import Terminals.Token;

/**
 * this class represents the structure of each node in the grammar tree.
 *
 * this class implements the comparable interface so it would be possible to
 * compare two different objects of this class.
 */
public class GrammarNode implements Comparable<GrammarNode>{

    //---------------- attributes -----------------//

    private String nonTerminalId;
    private boolean isTerminal;
    private Token terminal;

    //---------------- methods -----------------//

    /**
     * a constructor given an id (a terminal or a non-terminal) and a boolean
     * determining if the id is a terminal or not.
     * @param id the id of the terminal/non-terminal
     * @param isTerminal boolean to determine if the id corresponds to a
     *                   terminal or a non-terminal.
     * @throws TokenDoesNotExistException if the terminal id doesn't
     * correspond to a valid token.
     */
    public GrammarNode(String id, boolean isTerminal) throws TokenDoesNotExistException {
        this.isTerminal = isTerminal;
        if (isTerminal) {
            if (id.compareTo("e") == 0) {
                this.terminal = null;
            } else {
                this.terminal = new Token(id);
            }
        } else {
            this.nonTerminalId = id;
        }

    }

    /**
     * construct a grammar node given a token
     * @param t the token to construct a grammar node out of.
     */
    public GrammarNode(Token t) {
        this.isTerminal = true;
        this.terminal = t;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public Token getTerminal() {
        return terminal;
    }

    public String getNonTerminalID() {
        return nonTerminalId;
    }

    @Override
    public int compareTo(GrammarNode o) {
        if (o.isTerminal &&  this.isTerminal){
            return o.getTerminal().compareTo(this.getTerminal());
        }else if (!o.isTerminal &&  !this.isTerminal){
            return o.getNonTerminalID().equals(this.getNonTerminalID()) ? 0 : 1;
        }
        return 1;
    }


}
