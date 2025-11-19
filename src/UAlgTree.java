//this is defined as an outer class because that way we can save 8 bytes for each node.
//and we don't really need a pointer to the UAlgTree Object anyway
class UAlgTreeNode<Key extends Comparable<Key>, Value> implements IUAlgTreeNode<Key, Value> {
    @Override
    public IUAlgTreeNode<Key, Value> getLeft() {
        //TODO: implement
        return null;
    }

    @Override
    public IUAlgTreeNode<Key, Value> getRight() {
        //TODO: implement
        return null;
    }

    @Override
    public int getSize() {
        //TODO: implement
        return 0;
    }

    @Override
    public int getWeight() {
        //TODO: implement
        return 0;
    }

    @Override
    public Key getKey() {
        //TODO: implement
        return null;
    }

    @Override
    public Value getValue() {
        //TODO: implement
        return null;
    }
}

public class UAlgTree<Key extends Comparable<Key>, Value> {

    //TODO: implement

    public UAlgTree() {
        //TODO: implement
    }

    public IUAlgTreeNode<Key, Value> getRoot() {
        //TODO: implement
        return null;
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
}
