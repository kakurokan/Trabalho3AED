//this is defined as an outer class because that way we can save 8 bytes for each node.
//and we don't really need a pointer to the UAlgTree Object anyway
class UAlgTreeNode<Key extends Comparable<Key>,Value> implements IUAlgTreeNode<Key, Value>
{
    //implement your node logic in this class
    //you can add any fields to the class, if you think they are necessary

    //you need to implement all the interface methods or else your project won't pass
    //most of the Mooshak tests
    //Although you can, you don't need to use these methods on your code, these are for testing purposes
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

public class UAlgTree<Key extends Comparable<Key>,Value> {

    //TODO: implement

    public UAlgTree()
    {
        //TODO: implement
    }

    //This method is used only because of automatic tests using Mooshak
    //so that I can be sure you are implementing the UAlgTree correctly
    //Normally, I would not make an internal data structure public, because we're breaking
    //data abstraction barriers, which is often a bad idea
    //Anyway, at least I'm using an interface which creates some separation between
    //your code and the interface I will use. This means that you are free to implement
    //the node as you best see fit. Also, I'm only using getters for the interface, which
    // implies I cannot change your nodes.
    public IUAlgTreeNode<Key,Value> getRoot()
    {
        //TODO: implement
        return null;
    }

    public int size()
    {
        //TODO: implement
        return 0;
    }

    public Value get(Key k)
    {
        //TODO: implement
        return null;
    }
    
    public boolean contains(Key k)
    {
        //TODO: implement
        return false;
    }

    public void put(Key k, Value v)
    {
        //TODO: implement
    }

    public void delete(Key k)
    {
        //TODO: implement
    }

    public Iterable<Key> keys()
    {
        //TODO: implement
    	return null;
    }

    public Iterable<Value> values()
    {
        //TODO: implement
    	return null;
    }

    public UAlgTree<Key,Value> shallowCopy()
    {
        //TODO: implement
        return null;
    }
}
