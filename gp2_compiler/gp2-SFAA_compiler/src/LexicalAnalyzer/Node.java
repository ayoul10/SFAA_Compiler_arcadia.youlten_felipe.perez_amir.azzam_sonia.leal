package LexicalAnalyzer;
import Terminals.Token;

import java.security.PublicKey;
import java.util.HashMap;

/**
 * a node structure that is used in building a trie for the lexical analyzer
 */
public class Node {

    //---------------- attributes -----------------//

    private HashMap<Character, Node> next;
    private Token value;

    //---------------- methods -----------------//

    /**
     * the default constructor of this node structure.
     * @param next a list of nodes that proceed this node
     * @param value the value of the current node
     */
    public Node(HashMap<Character, Node> next, Token value){
        this.next = next;
        this.value = value;
    }

    public HashMap<Character, Node> getNext() {
        return next;
    }

    public void setNext(HashMap<Character, Node> next) {
        this.next = next;
    }

    public Token getValue() {
        return value;
    }

    public void setValue(Token value) {
        this.value = value;
    }
}
