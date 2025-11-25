//this is defined as an outer class because that way we can save 8 bytes for each node.
//and we don't really need a pointer to the UAlgTree Object anyway

//W(n) = nº de ponteiros "null" existentes na sub-árvore que começa em "n"
//w(null) = 1
//w(n) = w(left(n)) + w(right(n))

class UAlgTreeNode<Key extends Comparable<Key>, Value> implements IUAlgTreeNode<Key, Value> {
    private static final int INITIAL_WEIGHT = 2;
    Key key;
    Value value;
    int size;
    int weight;
    UAlgTreeNode<Key, Value> left;
    UAlgTreeNode<Key, Value> right;

    public UAlgTreeNode(int size, Value value, Key key) {
        this.size = size;
        this.value = value;
        this.key = key;
        this.weight = INITIAL_WEIGHT;
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
        return weight;
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

    private int UAlgTreeNodeWeight(UAlgTreeNode<Key, Value> node) {
        return (node == null) ? 1 : node.getWeight();
    }

    private int UAlgTreeNodeSize(UAlgTreeNode<Key, Value> node) {
        return (node == null) ? 0 : node.getSize();
    }

    private int updateUAlgTreeNodeSize(UAlgTreeNode<Key, Value> node) {
        return UAlgTreeNodeSize(node.left) + UAlgTreeNodeSize(node.right) + 1;
    }

    private int updateUAlgTreeNodeWeight(UAlgTreeNode<Key, Value> node) {
        return UAlgTreeNodeWeight(node.left) + UAlgTreeNodeWeight(node.right);
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

    private UAlgTreeNode<Key, Value> fixWeights(UAlgTreeNode<Key, Value> node) {
        int weightLeft = UAlgTreeNodeWeight(node.left);
        int weightRight = UAlgTreeNodeWeight(node.right);

        if (weightLeft < 0.4 * weightRight) {
            assert node.right != null;

            int weightKidLeft = UAlgTreeNodeWeight(node.right.left);
            int weightKidRight = UAlgTreeNodeWeight(node.right.right);

            if (weightKidLeft > 1.5 * weightKidRight) {
                node.right = rotateRight(node.right);
                node.right.size = updateUAlgTreeNodeSize(node.right);
                node.right.weight = updateUAlgTreeNodeWeight(node.right);
            }

            node = rotateLeft(node);
        } else if (weightRight < 0.4 * weightLeft) {
            assert node.left != null;

            int weightKidLeft = UAlgTreeNodeWeight(node.left.left);
            int weightKidRight = UAlgTreeNodeWeight(node.left.right);

            if (weightKidRight > 1.5 * weightKidLeft) {
                node.left = rotateLeft(node.left);
                node.left.size = updateUAlgTreeNodeSize(node.left);
                node.left.weight = updateUAlgTreeNodeWeight(node.left);
            }

            node = rotateRight(node);
        }

        node.weight = updateUAlgTreeNodeWeight(node);
        node.size = updateUAlgTreeNodeSize(node);
        return node;
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

        node.size = updateUAlgTreeNodeSize(node);
        node.weight = updateUAlgTreeNodeWeight(node);

        return fixWeights(node);
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

    private UAlgTreeNode<Key, Value> getSucessor(UAlgTreeNode<Key, Value> node) {
        UAlgTreeNode<Key, Value> suc = node.right;
        while (suc != null && suc.left != null) {
            suc = suc.left;
        }
        return suc;
    }

    public void delete(Key k) {
        this.root = delete(this.root, k);
    }

    private UAlgTreeNode<Key, Value> delete(UAlgTreeNode<Key, Value> node, Key k) {
        if (node == null) return null;

        int cmp = k.compareTo(node.getKey());
        if (cmp > 0) {
            node.right = delete(node.right, k);
        } else if (cmp < 0) {
            node.left = delete(node.left, k);
        } else {
            //0/1 filho
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;

            //2 filhos
            UAlgTreeNode<Key, Value> suc = getSucessor(node);
            node.key = suc.key;
            suc.key = k;
            node.value = suc.value;

            node.right = delete(node.right, k);
        }

        node.size = updateUAlgTreeNodeSize(node);
        node.weight = updateUAlgTreeNodeWeight(node);
        return fixWeights(node);
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
