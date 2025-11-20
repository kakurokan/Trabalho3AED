//this is defined as an outer class because that way we can save 8 bytes for each node.
//and we don't really need a pointer to the UAlgTree Object anyway
class UAlgTreeNode<Key extends Comparable<Key>, Value> implements IUAlgTreeNode<Key, Value> {
    Key key;
    Value value;
    int size;
    UAlgTreeNode<Key, Value> left;
    UAlgTreeNode<Key, Value> right;

    public UAlgTreeNode(int size, Value value, Key key) {
        this.size = size;
        this.value = value;
        this.key = key;
    }

    @Override
    public IUAlgTreeNode<Key, Value> getLeft() {
        return left;
    }

    @Override
    public IUAlgTreeNode<Key, Value> getRight() {
        return right;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getWeight() {
        //TODO: implement
        return 0;
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public Value getValue() {
        return value;
    }
}

public class UAlgTree<Key extends Comparable<Key>, Value> {

    private UAlgTreeNode<Key, Value> root;

    public UAlgTree() {
        //TODO: implement
    }

    public IUAlgTreeNode<Key, Value> getRoot() {
        return root;
    }

    public int size() {
        return getRoot().getSize();
    }

    public Value get(Key k) {
        //TODO: implement
        return null;
    }

    public boolean contains(Key k) {
        //TODO: implement
        return false;
    }

    public void put(Key k, Value v) {
        //TODO: implement
    }

    public void delete(Key k) {
        //TODO: implement
    }

    public Iterable<Key> keys() {
        //TODO: implement
        return null;
    }

    public Iterable<Value> values() {
        //TODO: implement
        return null;
    }

    public UAlgTree<Key, Value> shallowCopy() {
        //TODO: implement
        return null;
    }

    private void printInorder(IUAlgTreeNode<Key, Value> n) {
        if (n == null)
            return;

        printInorder(n.getLeft());

        System.out.println(n.getValue());

        printInorder(n.getRight());
    }
}
