//this is defined as an outer class because that way we can save 8 bytes for each node.
//and we don't really need a pointer to the UAlgTree Object anyway

//W(n) = nº de ponteiros "null" existentes na sub-árvore que começa em "n"
//w(null) = 1
//w(n) = w(left(n)) + w(right(n))

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
        int weightLeft = (left == null) ? 1 : left.getWeight();
        int weightRight = (right == null) ? 1 : right.getWeight();

        return weightLeft + weightRight;
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
        this.root = null;
    }

    public UAlgTree(Key key, Value value) {
        this.root = new UAlgTreeNode<>(1, value, key);
    }

    public IUAlgTreeNode<Key, Value> getRoot() {
        return root;
    }

    public int size() {
        return (this.root == null) ? 0 : this.root.getSize();
    }

    private int UAlgTreeNodesize(UAlgTreeNode<Key, Value> node) {
        return (node == null) ? 0 : node.getSize();
    }

    private UAlgTreeNode<Key, Value> rank(Key k) {
        UAlgTreeNode<Key, Value> temp = root;
        if (temp == null) return null;
        while (true) {
            int cmp = k.compareTo(temp.getKey());
            if (cmp > 0) {
                if (temp.right == null) return temp;
                temp = temp.right;
            } else if (cmp < 0) {
                if (temp.left == null) return temp;
                temp = temp.left;
            } else {
                return temp;
            }
        }
    }

    public Value get(Key k) {
        UAlgTreeNode<Key, Value> node = rank(k);
        return (node == null || (k.compareTo(node.getKey()) != 0)) ? null : node.getValue();
    }

    public boolean contains(Key k) {
        UAlgTreeNode<Key, Value> node = rank(k);
        return node != null && (k.compareTo(node.getKey()) == 0);

    }

    private void fixWeights(UAlgTreeNode<Key, Value> node) {
        int weightLeft = (node.left == null) ? 1 : node.left.getWeight();
        int weightRight = (node.right == null) ? 1 : node.right.getWeight();

        if (weightLeft < 0.4 * weightRight) {

        } else if (weightRight < 0.4 * weightLeft) {

        }
    }


    public void put(Key k, Value v) {
        this.root = put(this.root, k, v);
    }

    private UAlgTreeNode<Key, Value> put(UAlgTreeNode<Key, Value> node, Key k, Value v) {
        if (node == null) return new UAlgTreeNode<Key, Value>(1, v, k);

        int cmp = k.compareTo(node.getKey());
        if (cmp > 0) node.right = put(node.right, k, v);
        else if (cmp < 0) node.left = put(node.left, k, v);
        else node.value = v;

        node.size = UAlgTreeNodesize(node.left) + UAlgTreeNodesize(node.right) + 1;

        fixWeights(node);

        return node;
    }

    private UAlgTreeNode<Key, Value> rotateLeft(UAlgTreeNode<Key, Value> node) {
        UAlgTreeNode<Key, Value> right = node.right;

        node.right = right.left;
        right.left = node;

        return right;
    }

    private UAlgTreeNode<Key, Value> rotateRight(UAlgTreeNode<Key, Value> node) {
        UAlgTreeNode<Key, Value> left = node.left;

        node.left = left.right;
        left.right = node;

        return left;
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
