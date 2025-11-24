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
        int weightleft = (left == null) ? 1 : left.getWeight();

        int weightright = (right == null) ? 1 : right.getWeight();

        return weightleft + weightright;
    }

    public boolean isBalanced() {
        int weightleft = (left == null) ? 1 : left.getWeight();
        int weightright = (right == null) ? 1 : right.getWeight();

        if (weightleft < 0.4 * weightright || weightright < 0.4 * weightleft) {
            return false;
        }
        boolean leftbalanced = (left == null) || left.isBalanced();
        boolean rightbalanced = (right == null) || right.isBalanced();
        return leftbalanced && rightbalanced;
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

    public int UalgTreeNodesize(UAlgTreeNode node) {
        return (node == null) ? 0 : node.getSize();
    }

    private UAlgTreeNode rank(Key k) {

        UAlgTreeNode temp = root;
        if (temp == null) return null;
        while (true) {
            int cmp = temp.key.compareTo(k);
            if (cmp < 0) {
                if (temp.right == null) return temp;
                temp = temp.right;
            } else if (cmp > 0) {
                if (temp.left == null) return temp;
                temp = temp.left;
            } else {
                return temp;
            }
        }
    }

    public Value get(Key k) {
        UAlgTreeNode temp = rank(k);
        if (temp == null) return null;
        return (temp.key.compareTo(k) == 0) ? (Value) temp.getValue() : null;
    }

    public boolean contains(Key k) {
        UAlgTreeNode temp = rank(k);
        if (temp == null) return false;
        return (temp.key.compareTo(k) == 0) ? true : false;
    }

    public void put(Key k, Value v) {
        this.root = put(this.root, k, v);
    }

    public UAlgTreeNode put(UAlgTreeNode node, Key k, Value v) {
        if (node == null) return new UAlgTreeNode(1, v, k);

        int cmp = node.getKey().compareTo(k);
        if (cmp < 0) node.right = put(node.right, k, v);
        else if (cmp > 0) node.left = put(node.left, k, v);
        else node.value = v;
        node.size = UalgTreeNodesize(node.left) + 1 + UalgTreeNodesize(node.right);
        if (!node.isBalanced()){
            //TODO//
        }
        return node;
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
