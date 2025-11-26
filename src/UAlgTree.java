//this is defined as an outer class because that way we can save 8 bytes for each node.
//and we don't really need a pointer to the UAlgTree Object anyway

//W(n) = nº de ponteiros "null" existentes na sub-árvore que começa em "n"
//w(null) = 1
//w(n) = w(left(n)) + w(right(n))

import java.util.Iterator;
import java.util.Stack;

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

    private boolean isSafeLeftRotation(UAlgTreeNode<Key, Value> node) {
        if (node.right == null) return false;

        int weight = UAlgTreeNodeWeight(node);
        int weightLeftKid = UAlgTreeNodeWeight(node.right.left);
        int weightRightKid = UAlgTreeNodeWeight(node.right.right);

        return (weight <= 3.5 * weightRightKid) && (weight <= 3.5 * weightLeftKid + weightRightKid);
    }

    private boolean isSafeRightRotation(UAlgTreeNode<Key, Value> node) {
        if (node.left == null) return false;

        int weight = UAlgTreeNodeWeight(node);
        int weightLeftKid = UAlgTreeNodeWeight(node.right.left);
        int weightRightKid = UAlgTreeNodeWeight(node.right.right);

        return (weight <= 3.5 * weightLeftKid) && (weight <= 3.5 * weightRightKid + weightLeftKid);
    }

    public Value get(Key k) {
        UAlgTreeNode<Key, Value> node = this.root;
        if (node == null) return null;

        Value value;
        Stack<UAlgTreeNode<Key, Value>> nodes = new Stack<>();
        nodes.push(node);

        while (true) {
            int cmp = k.compareTo(node.getKey());
            if (cmp > 0) {
                node = node.right;
            } else if (cmp < 0) {
                node = node.left;
            } else {
                break;
            }
            if (node == null) return null;
            nodes.push(node);
        }

        nodes.pop(); // retira o nó encontrado
        value = node.getValue();

        boolean wasRotated = false;
        UAlgTreeNode<Key, Value> parent = null;
        while (!nodes.isEmpty()) {
            parent = nodes.pop();
            boolean isRoot = parent == this.root;

            if (parent.left == node && isSafeRightRotation(parent)) {
                parent = rotateRight(parent);
                wasRotated = true;
            } else if (parent.right == node && isSafeLeftRotation((parent))) {
                parent = rotateLeft(parent);
                wasRotated = true;
            } else if (isRoot && !wasRotated) {
                if (this.root.right == node)
                    this.root = rotateLeft(this.root);
                else
                    this.root = rotateRight(this.root);
            } else if (wasRotated) {
                if (!isRoot) {
                    UAlgTreeNode<Key, Value> grandParent = nodes.pop();

                    int cmp = parent.getKey().compareTo(grandParent.getKey());
                    if (cmp > 0)
                        grandParent.right = parent;
                    else
                        grandParent.left = parent;
                } else {
                    this.root = parent;
                }

                break;
            }

            node = parent;
        }
        return value;
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
            }

            node = rotateLeft(node);
        } else if (weightRight < 0.4 * weightLeft) {
            assert node.left != null;

            int weightKidLeft = UAlgTreeNodeWeight(node.left.left);
            int weightKidRight = UAlgTreeNodeWeight(node.left.right);

            if (weightKidRight > 1.5 * weightKidLeft) {
                node.left = rotateLeft(node.left);
            }

            node = rotateRight(node);
        }

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

        node.weight = updateUAlgTreeNodeWeight(node);
        node.size = updateUAlgTreeNodeSize(node);

        right.weight = updateUAlgTreeNodeWeight(right);
        right.size = updateUAlgTreeNodeSize(right);

        return right;
    }

    private UAlgTreeNode<Key, Value> rotateRight(UAlgTreeNode<Key, Value> node) {
        UAlgTreeNode<Key, Value> left = node.left;

        node.left = left.right;
        left.right = node;

        node.weight = updateUAlgTreeNodeWeight(node);
        node.size = updateUAlgTreeNodeSize(node);

        left.weight = updateUAlgTreeNodeWeight(left);
        left.size = updateUAlgTreeNodeSize(left);

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
        return new Iterable<Key>() {
            @Override
            public Iterator<Key> iterator() {
                return new KeyIterator();
            }
        };
    }

    public Iterable<Value> values() {
        return new Iterable<Value>() {
            @Override
            public Iterator<Value> iterator() {
                return new ValueIterator();
            }
        };
    }

    public UAlgTree<Key, Value> shallowCopy() {
        //TODO: implement
        return null;
    }

    public class ValueIterator implements Iterator<Value> {
        private final Stack<UAlgTreeNode<Key, Value>> stack = new Stack<>();

        public ValueIterator() {
            PushLeft((UAlgTreeNode<Key, Value>) getRoot());
        }

        private void PushLeft(UAlgTreeNode<Key, Value> node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }

        public boolean hasNext() {
            return !stack.isEmpty();
        }

        public Value next() {
            UAlgTreeNode<Key, Value> node = stack.pop();
            if (node.right != null) {
                PushLeft(node.right);
            }
            return node.getValue();

        }
    }

    public class KeyIterator implements Iterator<Key> {
        private final Stack<UAlgTreeNode<Key, Value>> stack = new Stack<>();

        public KeyIterator() {
            PushLeft((UAlgTreeNode<Key, Value>) getRoot());
        }

        public boolean hasNext() {
            return (!stack.isEmpty());
        }

        private void PushLeft(UAlgTreeNode<Key, Value> node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }

        @Override
        public Key next() {
            UAlgTreeNode<Key, Value> node = stack.pop();
            if (node.right != null) {
                PushLeft(node.right);
            }
            return node.getKey();
        }
    }
}
