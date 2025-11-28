package aed.trees;
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

    public static void main(String[] args) {
        System.out.println("=== TESTE RIGOROSO DE SIZE (UAlgTree) ===");
        UAlgTree<Integer, String> tree = new UAlgTree<>();

        // 1. Inserção
        System.out.println("\n[1] Inserindo 6 elementos...");
        tree.put(50, "Raiz");
        tree.put(20, "Esq");
        tree.put(70, "Dir");
        tree.put(10, "NetoEsq");
        tree.put(30, "NetoDir");
        tree.put(5, "Bisneto");

        // CHECK 1: Tamanho inicial
        int esperado = 6;
        if (tree.size() == esperado) {
            System.out.println("   [OK] Size inicial correto: " + tree.size());
        } else {
            System.out.println("   [ERRO] Size incorreto! Esperado: " + esperado + ", Obtido: " + tree.size());
        }

        // 2. Teste de Get (Rotação Simples)
        System.out.println("\n[2] Get(30) - Rotação simples...");
        tree.get(30);

        // CHECK 2
        if (tree.size() == esperado) {
            System.out.println("   [OK] Size manteve-se após get(30).");
        } else {
            System.out.println("   [ERRO CRÍTICO] Size mudou para " + tree.size() + " após get(30)!");
        }

        // 3. Teste de Get Profundo (Rotação Múltipla)
        // O nó 5 está no fundo. Trazê-lo para cima mexe em muitos ponteiros.
        System.out.println("\n[3] Get(5) - Rotação profunda (Stress Test)...");
        String val = tree.get(5);
        System.out.println("   Valor retornado: " + val);

        // CHECK 3
        if (tree.size() == esperado) {
            System.out.println("   [OK] Size manteve-se após get(5).");
        } else {
            System.out.println("   [ERRO CRÍTICO] Size mudou para " + tree.size() + " após get(5). Um ramo foi perdido!");
        }

        // 4. Teste de Update (Não deve alterar size)
        System.out.println("\n[4] Put(50, ...) - Atualizar valor existente...");
        tree.put(50, "Raiz Atualizada");

        // CHECK 4
        if (tree.size() == esperado) {
            System.out.println("   [OK] Size manteve-se após update.");
        } else {
            System.out.println("   [ERRO] Size mudou após update! (Não devia inserir novo nó)");
        }

        // 5. Teste de Delete (Deve alterar size)
        System.out.println("\n[5] Delete(20) - Removendo um nó...");
        tree.delete(20);
        esperado = 5; // Agora esperamos 5

        // CHECK 5
        if (tree.size() == esperado) {
            System.out.println("   [OK] Size atualizou corretamente para " + tree.size());
        } else {
            System.out.println("   [ERRO] Delete falhou no size. Esperado: 5, Obtido: " + tree.size());
        }

        // Resumo final
        System.out.println("\n=== FIM DOS TESTES ===");
        if (tree.size() == 5 && tree.contains(5) && tree.contains(50) && !tree.contains(20)) {
            System.out.println(">>> SUCESSO: A árvore está estável! <<<");
        } else {
            System.out.println(">>> FALHA: Verifique os erros acima. <<<");
        }
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

        long weight = node.getWeight();
        long weightLeftKid = UAlgTreeNodeWeight(node.right.left);
        long weightRightKid = UAlgTreeNodeWeight(node.right.right);

        return (2 * weight <= 7 * weightRightKid) && (2 * weight <= 7 * weightLeftKid + 2 * weightRightKid);
    }

    private boolean isSafeRightRotation(UAlgTreeNode<Key, Value> node) {
        if (node.left == null) return false;

        long weight = node.getWeight();
        long weightLeftKid = UAlgTreeNodeWeight(node.left.left);
        long weightRightKid = UAlgTreeNodeWeight(node.left.right);

        return (2 * weight <= 7 * weightLeftKid) && (2 * weight <= 7 * weightRightKid + 2 * weightLeftKid);
    }

    public Value get(Key k) {
        UAlgTreeNode<Key, Value> node = this.root;
        if (node == null) return null;

        Value value;
        Stack<UAlgTreeNode<Key, Value>> nodes = new Stack<>();
        nodes.push(node);
        int cmp;

        while (true) {
            cmp = k.compareTo(node.getKey());
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
        UAlgTreeNode<Key, Value> parent;
        UAlgTreeNode<Key, Value> oldParent;

        while (!nodes.isEmpty()) {
            parent = nodes.pop();
            oldParent = parent;
            boolean isRoot = (parent == this.root);

            cmp = node.getKey().compareTo(parent.getKey());

            if (cmp > 0) {
                if (isRoot || isSafeLeftRotation(parent)) {
                    parent = rotateLeft(parent);
                    wasRotated = true;
                }
            } else if (cmp < 0) {
                if (isRoot || isSafeRightRotation(parent)) {
                    parent = rotateRight(parent);
                    wasRotated = true;
                }
            }

            if (wasRotated) {
                if (!isRoot) {
                    if (!nodes.isEmpty()) {
                        UAlgTreeNode<Key, Value> grandParent = nodes.peek();

                        if (grandParent.left == oldParent)
                            grandParent.left = parent;
                        else
                            grandParent.right = parent;
                    }
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
        return get(k) != null;
    }

    private UAlgTreeNode<Key, Value> fixWeights(UAlgTreeNode<Key, Value> node) {
        int weightLeft = UAlgTreeNodeWeight(node.left);
        int weightRight = UAlgTreeNodeWeight(node.right);

        if (10 * weightLeft < 4 * weightRight) {
            assert node.right != null;

            int weightKidLeft = UAlgTreeNodeWeight(node.right.left);
            int weightKidRight = UAlgTreeNodeWeight(node.right.right);

            if (2 * weightKidLeft > 3 * weightKidRight) {
                node.right = rotateRight(node.right);
            }

            node = rotateLeft(node);
        } else if (5 * weightRight < 2 * weightLeft) {
            assert node.left != null;

            int weightKidLeft = UAlgTreeNodeWeight(node.left.left);
            int weightKidRight = UAlgTreeNodeWeight(node.left.right);

            if (2 * weightKidRight > 3 * weightKidLeft) {
                node.left = rotateLeft(node.left);
            }

            node = rotateRight(node);
        }

        return node;
    }

    public void put(Key k, Value v) {
        if (v == null) {
            delete(k);
            return;
        }
        this.root = put(this.root, k, v);
    }

    private UAlgTreeNode<Key, Value> put(UAlgTreeNode<Key, Value> node, Key k, Value v) {
        if (node == null) return new UAlgTreeNode<>(1, v, k);

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
            node.value = suc.value;

            node.right = deleteSuc(node.right);
        }

        node.size = updateUAlgTreeNodeSize(node);
        node.weight = updateUAlgTreeNodeWeight(node);
        return fixWeights(node);
    }

    private UAlgTreeNode<Key, Value> deleteSuc(UAlgTreeNode<Key, Value> node) {
        if (node.left == null) return node.right;

        node.left = deleteSuc(node.left);

        node.size = updateUAlgTreeNodeSize(node);
        node.weight = updateUAlgTreeNodeWeight(node);
        return fixWeights(node);
    }

    public Iterable<Key> keys() {
        return KeyIterator::new;
    }

    public Iterable<Value> values() {
        return ValueIterator::new;
    }

    public UAlgTree<Key, Value> shallowCopy() {
        UAlgTree<Key, Value> copy = new UAlgTree<>();
        copy.root = shallowCopyNode(this.root);
        return copy;
    }

    private UAlgTreeNode<Key, Value> shallowCopyNode(UAlgTreeNode<Key, Value> node) {
        if (node == null) return null;

        UAlgTreeNode<Key, Value> copy = new UAlgTreeNode<>(node.getSize(), node.getValue(), node.getKey());
        copy.weight = node.weight;

        copy.left = shallowCopyNode(node.left);
        copy.right = shallowCopyNode(node.right);

        return copy;
    }

    public class ValueIterator implements Iterator<Value> {
        private final Stack<UAlgTreeNode<Key, Value>> stack = new Stack<>();

        public ValueIterator() {
            pushLeft((UAlgTreeNode<Key, Value>) getRoot());
        }

        private void pushLeft(UAlgTreeNode<Key, Value> node) {
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
                pushLeft(node.right);
            }
            return node.getValue();

        }
    }

    public class KeyIterator implements Iterator<Key> {
        private final Stack<UAlgTreeNode<Key, Value>> stack = new Stack<>();

        public KeyIterator() {
            pushLeft((UAlgTreeNode<Key, Value>) getRoot());
        }

        public boolean hasNext() {
            return (!stack.isEmpty());
        }

        private void pushLeft(UAlgTreeNode<Key, Value> node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }

        @Override
        public Key next() {
            UAlgTreeNode<Key, Value> node = stack.pop();
            if (node.right != null) {
                pushLeft(node.right);
            }
            return node.getKey();
        }
    }
}
