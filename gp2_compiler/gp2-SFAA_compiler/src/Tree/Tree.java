package Tree;

import LexicalAnalyzer.LexicalAnalyzer;
import Terminals.Exception.TokenDoesNotExistException;
import Terminals.Token;
import Terminals.TokenDatabase;

import java.util.ArrayList;

/**
 * a generic tree structure.
 *
 * this class implements the comparable interface so objects of this class
 * could be compared.
 *
 * @param <T> the datatype of the value stored in the nodes of this tree.
 */
public class Tree<T extends Comparable<T>> {

    //---------------- attributes -----------------//

    private final Node<T> root;
    private Node<T> current;

    //---------------- methods -----------------//

    /**
     * a tree constructor provided the value stored inside the root node
     * @param value the value stored in the root node
     * @throws TokenDoesNotExistException if the value is an invalid token
     */
    public Tree(T value) throws TokenDoesNotExistException {
        root = new Node<T>(value);
        root.addParent(root);
        this.current = root;
    }

    /**
     * a tree constructor provided an initial node (this node could be a
     * branch of a tree)
     * @param currentBranch the branch to be used as the base for the initial
     *                      node
     */
    public Tree(Node<T> currentBranch) {
        this.current = currentBranch;
        this.root = this.current;
    }

    /**
     * checks if the current node pointer is pointing
      * @return true if the current node is the root, and False otherwise.
     */
    public boolean isOnRoot(){
        return this.current.compareTo(root) == 0;
    }

    /**
     * append a node to the children of the current node.
     * @param n the child to be added
     */
    public void addNodeToCurrent(Node<T> n){
        this.current.add(n);
    }

    /**
     * adds a list of nodes to the children of the current node.
     * @param nodes the list of children to be added
     */
    public void addNodesToCurrent(ArrayList<T> nodes){
        for (T node :
                nodes) {
            this.current.add(new Node<T>(node));
        }
    }

    /**
     * moves the current pointer to the child given as a parameter
     * @param child the child to move the current pointer to.
     * @return true if the process was successful, false otherwise.
     */
    public boolean moveToChild(T child){
        for (Node<T> c :
                current.getNext()) {
            if (c.compareTo(child) == 0){
                this.current = c;
                return true;
            }
        }
        return false;
    }

    /**
     * moves to the current pointer to the child given the child position.
     * @param position the position of the child.
     * @return rue if the process was successful, false otherwise.
     */
    public boolean moveToChild(int position){
        Node<T> aux = this.current.getNext(position);
        if (aux != null) {
            this.current = aux;
            return true;
        }
        return false;
    }

    /**
     * move the current pointer to the parent of the current node.
     */
    public void moveToParent(){
        this.current = this.current.getParent();
    }

    /**
     * move the current node to the root of the tree.
     */
    public void moveToRoot() {
        current = root;
    }

    /**
     * delete the current node.
     */
    public void deleteCurrent(){
        Node<T> aux = current;
        this.moveToParent();

        current.deleteNext(aux);
    }

    /**
     * replaces the current node with its children (the children will be
     * placed in the position of the current node)
     */
    public void replaceCurrentWithChildren(){
        ArrayList<Node<T>> children;
        children = current.getNext();
        int i =  getCurrentPosition();
        deleteCurrent();
        for (int j = 0; j < children.size(); j++) {
            children.get(j).addParent(current);
        }
        current.addChildrenInPosition(children, i);
    }

    /**
     * replaces the current node with one of its children (the children of
     * the current will be concatenated to the children of the node to
     * be placed instead of the current node)
     * @param childIndex the node to be placed instead of the current
     * @return the index of the next node after the newly inserted children.
     */
    public int ReplaceCurrentWithChild(int childIndex){
        Node<T> auxChild = current.getNext(childIndex);
        int i = childIndex;
        auxChild.addParent(current.getParent());
        current.deleteNext(childIndex);
        current.getNext().addAll(childIndex, auxChild.getNext());
        if(auxChild.getNext().size() > 0)
            i += auxChild.getNext().size() - 1;
        else
            i--;
        current.setValue(auxChild.getValue());
        for (int j = 0; j < current.getNext().size(); j++) {
            current.getNext(j).addParent(current);
        }
        return i;
    }

    /**
     * deletes a child given it's value
     * @param child the child to be deleted
     */
    public void deleteChild(T child) {
        for (Node<T> c :
                current.getNext()) {
            if (c.getValue().compareTo(child) == 0){
                current.getNext().remove(c);
                break;
            }
        }
    }

    /**
     * gets the position current in its parent children list.
     * @return the position of the current node in its parent children list.
     */
    private int getCurrentPosition() {
        Node<T> aux = current;
        Node<T> parent = current.getParent();

        for (int i = 0; i < parent.getNext().size(); i++) {
            if(parent.getNext(i).compareTo(current) == 0){
                return i ;
            }

        }
        return -1;

    }

    public Tree<T> getCurrentBranch(){
        return new Tree<T>(this.current);
    }

    public int getNumberOfChildren(){
            return current.getNext().size();
    }

    public T getCurrentValue(){
        return current.getValue();
    }

    public T getChild(int index){
        return current.getNext(index).getValue();
    }

    public Tree<T> getChildBranch(int i) {
        return new Tree<T>(this.current.getNext(i));
    }
}



