package Tree;

import java.util.ArrayList;

/**
 * a generic node structure to be used in the tree.
 *
 * this class implements the comparable interface so objects of this class
 * could be compared.
 *
 * @param <T> the datatype of the value stored in that node.
 */
public class Node<T extends Comparable<T>> implements Comparable<Node<T>>{

    //---------------- attributes -----------------//

    private ArrayList<Node<T>> next;
    private T value;
    private Node<T> parent;

    //---------------- methods -----------------//

    /**
     * the default constructor of a node object.
     * @param value the value to be stored inside that node.
     */
    public Node(T value) {
        this.next = new ArrayList<>();
        this.value = value;
        this.parent = null;
    }

    /**
     * adds a new node as a proceeding node to the current.
     * @param next the node to be added.
     */
    public void add(Node<T> next) {
        next.addParent(this);
        this.next.add(next);
    }

    /**
     * adds a list of nodes as proceeding nodes to the current.
     * @param next the list of nodes to be added.
     */
    public void addAll(ArrayList<Node<T>> next) {
        for( Node<T> n : next)
            n.addParent(this);
        this.next = next;
    }

    /**
     * sets a node as the parent of this node (sets a node as a preceding
     * node to the current)
     * @param parent the node to be set as the parent.
     */
    public void addParent(Node<T> parent) {
        this.parent = parent;
    }

    public ArrayList<Node<T>> getNext() {
        return next;
    }

    public Node<T> getNext(int position) {
        if (position < next.size())
            return next.get(position);
        return null;
    }

    public Node<T> getNext(Node<T> n) {
        for (Node<T> node : this.next){
            if (node.compareTo(n) == 0){
                return node;
            }
        }
        return null;
    }

    public Node<T> getParent() {
        return parent;
    }

    public T getValue() {
        return value;
    }


    @Override
    public int compareTo(Node<T> o) {
        if(this.value.compareTo(o.value) == 0)
            return 0;
        else
            return -1;
    }

    public int compareTo(T o) {
        if(this.value.compareTo(o) == 0)
            return 0;
        else
            return -1;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void deleteNext(int childIndex) {
        next.remove(childIndex);
    }

    public void deleteNext(Node<T> child) {
        next.remove(child);
    }

    public void addChildrenInPosition(ArrayList<Node<T>> children, int i) {
        next.addAll(i, children);
    }
}
