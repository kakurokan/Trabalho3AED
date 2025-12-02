package aed.tables;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

class UAlshBucket<Key, Value> implements IUAlshBucket<Key, Value> {

    Key key;
    Value value;
    int maxSharedTable;
    int hc1;
    int hc2;
    boolean is_deleted;

    UAlshBucket(Value value, Key key, Function<Key, Integer> hc2, int maxSharedTable) {
        this.value = value;
        this.key = key;
        this.maxSharedTable = maxSharedTable;
        is_deleted = false;
        this.hc1 = key.hashCode();
        this.hc2 = hc2.apply(key);
    }

    public int getMaxSharedTable() {
        return maxSharedTable;
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public boolean isEmpty() {
        return (key == null || is_deleted);
    }

    @Override
    public boolean isDeleted() {
        return is_deleted;
    }

    void delete() {
        is_deleted = true;
    }
}

public class UAlshTable<Key, Value> {

    //mudei de ideais relativamente aos primos iniciais, iremos usar
    //37, 17, 11, 7, e 5. Esta mudança não tem qualquer impacto significativo
    private static final int[] primes = {5, 7, 11, 17, 37, 79, 163, 331, 673, 1361, 2729, 5471, 10949, 21911, 43853, 87719, 175447, 350899, 701819, 1403641, 2807303, 5614657, 11229331, 22458671, 44917381, 89834777, 179669557};
    private final int DEFAULT_PRIME_INDEX = 4;
    private final Function<Key, Integer> hc2;
    private int min;
    private int size;
    private int primeIndex;
    private int deletedKeys;
    //Tabelas
    private UAlshBucket<Key, Value>[] t1;
    private UAlshBucket<Key, Value>[] t2;
    private UAlshBucket<Key, Value>[] t3;
    private UAlshBucket<Key, Value>[] t4;
    private UAlshBucket<Key, Value>[] t5;

    @SuppressWarnings("unchecked")
    public UAlshTable(Function<Key, Integer> hc2) {
        this.hc2 = hc2;
        this.size = 0;
        this.primeIndex = 4;
        this.deletedKeys = 0;

        this.t1 = (UAlshBucket<Key, Value>[]) new UAlshBucket[primes[DEFAULT_PRIME_INDEX]];
        this.t2 = (UAlshBucket<Key, Value>[]) new UAlshBucket[primes[DEFAULT_PRIME_INDEX - 1]];
        this.t3 = (UAlshBucket<Key, Value>[]) new UAlshBucket[primes[DEFAULT_PRIME_INDEX - 2]];
        this.t4 = (UAlshBucket<Key, Value>[]) new UAlshBucket[primes[DEFAULT_PRIME_INDEX - 3]];
        this.t5 = (UAlshBucket<Key, Value>[]) new UAlshBucket[primes[DEFAULT_PRIME_INDEX - 4]];
    }

    @SuppressWarnings("unchecked")
    private UAlshTable(Function<Key, Integer> hc2, int primeIndex) {
        this.hc2 = hc2;
        this.size = 0;
        this.primeIndex = primeIndex;
        this.deletedKeys = 0;

        this.t1 = (UAlshBucket<Key, Value>[]) new UAlshBucket[primes[primeIndex]];
        this.t2 = (UAlshBucket<Key, Value>[]) new UAlshBucket[primes[primeIndex - 1]];
        this.t3 = (UAlshBucket<Key, Value>[]) new UAlshBucket[primes[primeIndex - 2]];
        this.t4 = (UAlshBucket<Key, Value>[]) new UAlshBucket[primes[primeIndex - 3]];
        this.t5 = (UAlshBucket<Key, Value>[]) new UAlshBucket[primes[primeIndex - 4]];
    }

    public static void main(String[] args) {
        System.out.println("=== TESTE DE LOAD FACTOR E RESIZING ===");

        // 1. Inicialização
        // Capacidade inicial esperada: 37 + 17 + 11 + 7 + 5 = 77
        UAlshTable<Integer, String> table = new UAlshTable<>(Object::hashCode);

        printStats(table, "Estado Inicial (Vazia)");

        // 2. Preencher até perto do limite de resize
        // Limite de resize é 0.85 * T1 (37) = 31.45.
        // Vamos inserir 31 elementos. Não deve fazer resize ainda.
        System.out.println(">>> A inserir 31 elementos...");
        for (int i = 0; i < 31; i++) {
            table.put(i, "Val-" + i);
        }

        // O Load Factor deve ser aprox 31 / 77 = 0.4025
        printStats(table, "Após 31 inserções (Pré-Resize)");

        // 3. Forçar o Resize
        // Ao inserir o 32.º elemento, deve passar o threshold (31.45) e duplicar.
        // Nova T1 será 79. Capacidade total estimada: 79+37+17+11+7 = 151.
        System.out.println(">>> A inserir mais 5 elementos (Total 36) - DEVE OCORRER RESIZE...");
        for (int i = 31; i < 36; i++) {
            table.put(i, "Val-" + i);
        }

        // O Load Factor deve CAIR drasticamente porque a capacidade aumentou.
        // Esperado: Size 36 / Cap 151 = ~0.238
        printStats(table, "Após 36 inserções (Pós-Resize)");

        // 4. Testar consistência dos dados
        System.out.println("Verificação rápida: get(10) = " + table.get(10));
        System.out.println("Verificação rápida: get(35) = " + table.get(35));
        System.out.println();

        // 5. Testar Downsizing (Encolher)
        // Ocorre quando size < 0.25 * T1.
        // Atual T1 = 79. Threshold = 19.75.
        // Temos 36 elementos. Vamos apagar 20, ficando com 16.
        System.out.println(">>> A apagar 20 elementos para forçar Downsizing...");
        for (int i = 0; i < 20; i++) {
            table.delete(i);
        }

        // Agora devemos ter voltado à capacidade original (77) ou perto disso,
        // pois o tamanho (16) é menor que 19.75.
        printStats(table, "Após remoção massiva (Downsizing)");
    }

    // Método auxiliar para imprimir bonito
    private static void printStats(UAlshTable<?, ?> table, String fase) {
        System.out.println("--- " + fase + " ---");
        System.out.println("Size (elementos): " + table.size());
        System.out.println("Main Capacity (T1): " + table.getMainCapacity());
        System.out.println("Total Capacity: " + table.getTotalCapacity());
        System.out.printf("Load Factor: %.4f (%.2f%%)\n", table.getLoadFactor(), table.getLoadFactor() * 100);
        System.out.println("------------------------------------------------\n");
    }

    private void resetMin() {
        min = Integer.MAX_VALUE;
    }

    public int size() {
        return this.size - this.deletedKeys;
    }

    private int UAsh(Key k, int i) {
        return ((k.hashCode() + (i * hc2.apply(k))) & 0x7fffffff) % getSubTable(i).length;
    }

    public int getMainCapacity() {
        return primes[this.primeIndex];
    }

    public int getTotalCapacity() {
        int sum = 0;
        for (int i = 0; i < 5; i++)
            sum += primes[primeIndex - i];
        return sum;
    }

    public float getLoadFactor() {
        return this.size / (float) getTotalCapacity();
    }

    public int getDeletedNotRemoved() {
        return this.deletedKeys;
    }

    private void resize(int new_primeIndex) {
        if (new_primeIndex >= primes.length) return;

        UAlshTable<Key, Value> new_table = new UAlshTable<>(this.hc2, new_primeIndex);

        for (int i = 1; i <= 5; i++) {
            UAlshBucket<Key, Value>[] sub_table = (UAlshBucket<Key, Value>[]) getSubTable(i);
            for (UAlshBucket<Key, Value> bucket : sub_table) {
                if (bucket != null && !bucket.isDeleted()) {
                    new_table.fastPut(bucket.getKey(), bucket.getValue());
                }
            }
        }

        this.t1 = new_table.t1;
        this.t2 = new_table.t2;
        this.t3 = new_table.t3;
        this.t4 = new_table.t4;
        this.t5 = new_table.t5;
        this.deletedKeys = 0;
        this.size = new_table.size;
        this.primeIndex = new_primeIndex;
    }

    public IUAlshBucket<Key, Value>[] getSubTable(int i) {
        if (i == 1) return t1;
        else if (i == 2) return t2;
        else if (i == 3) return t3;
        else if (i == 4) return t4;
        else if (i == 5) return t5;

        return null;
    }

    public boolean containsKey(Key k) {
        return get(k) != null;
    }

    @SuppressWarnings("unchecked")
    private UAlshBucket<Key, Value>[] possibleBuckets(Key k) {
        resetMin();
        UAlshBucket<Key, Value>[] buckets = (UAlshBucket<Key, Value>[]) new UAlshBucket[5];

        for (int i = 1; i <= 5; i++) {
            UAlshBucket<Key, Value> bucket = (UAlshBucket<Key, Value>) getSubTable(i)[UAsh(k, i)];
            if (bucket == null) {
                break;
            }
            buckets[i - 1] = bucket;
            min = Math.min(min, bucket.getMaxSharedTable());
        }

        return buckets;
    }

    public Value get(Key k) {
        int khc1 = k.hashCode();
        int khc2 = hc2.apply(k);

        UAlshBucket<Key, Value>[] buckets = possibleBuckets(k);

        if (min == Integer.MAX_VALUE)
            return null;

        for (int i = min - 1; i >= 0; i--) {
            UAlshBucket<Key, Value> bucket = buckets[i];
            if (bucket != null && !bucket.isDeleted() && bucket.hc1 == khc1 && bucket.hc2 == khc2) {
                if (bucket.getKey().equals(k)) return bucket.getValue();
            }
        }

        return null;
    }

    public void put(Key k, Value v) {
        if (v == null) {
            delete(k);
            return;
        }

        int khc1 = k.hashCode();
        int khc2 = hc2.apply(k);

        UAlshBucket<Key, Value>[] buckets = possibleBuckets(k);

        if (min == Integer.MAX_VALUE) {
            fastPut(k, v);
            return;
        }

        for (int i = min - 1; i >= 0; i--) {
            if ((buckets[i] != null && buckets[i].hc1 == khc1 && buckets[i].hc2 == khc2)) {
                if (buckets[i].getKey().equals(k)) {
                    if (buckets[i].isDeleted()) {
                        this.deletedKeys--;
                        buckets[i].is_deleted = false;
                    }
                    buckets[i].value = v;
                    return;
                }
            }
        }

        fastPut(k, v);
    }

    @SuppressWarnings("unchecked")
    public void fastPut(Key k, Value v) {
        if (this.size >= 0.85 * t1.length) {
            resize(primeIndex + 1);
        }

        UAlshBucket<Key, Value>[] buckets = (UAlshBucket<Key, Value>[]) new UAlshBucket[5];
        int sharedTable = 0;

        for (int i = 1; i <= 5; i++) {
            int UAsh = UAsh(k, i);
            UAlshBucket<Key, Value>[] table = (UAlshBucket<Key, Value>[]) getSubTable(i);

            if (table[UAsh] == null) {
                getSubTable(i)[UAsh] = new UAlshBucket<>(v, k, hc2, 0);
                buckets[i - 1] = (UAlshBucket<Key, Value>) getSubTable(i)[UAsh];
                sharedTable = i;

                buckets[i - 1] = table[UAsh];
                this.size++;
                break;
            } else if (table[UAsh].isDeleted()) {
                getSubTable(i)[UAsh] = new UAlshBucket<>(v, k, hc2, table[UAsh].getMaxSharedTable());

                buckets[i - 1] = (UAlshBucket<Key, Value>) getSubTable(i)[UAsh];

                sharedTable = i;
                this.deletedKeys--;
                break;
            }
            buckets[i - 1] = table[UAsh];
        }
        for (UAlshBucket<Key, Value> bucket : buckets) {
            if (bucket != null)
                bucket.maxSharedTable = Math.max(bucket.maxSharedTable, sharedTable);
        }
    }

    public void delete(Key k) {
        int khc1 = k.hashCode();
        int khc2 = hc2.apply(k);

        UAlshBucket<Key, Value>[] buckets = possibleBuckets(k);

        if (min == Integer.MAX_VALUE) {
            return;
        }

        for (int i = min - 1; i >= 0; i--) {
            if (buckets[i] != null && !buckets[i].isDeleted() && buckets[i].hc1 == khc1 && buckets[i].hc2 == khc2) {
                if (buckets[i].getKey().equals(k)) {
                    buckets[i].delete();
                    this.deletedKeys++;
                    break;
                }
            }
        }

        if (primeIndex > DEFAULT_PRIME_INDEX && (size() < 0.25 * primes[primeIndex]))
            resize(this.primeIndex - 1);
    }

    public Iterable<Key> keys() {
        return UalshIterator::new;
    }

    public class UalshIterator implements Iterator<Key> {
        private int currentTableIndex;
        private int currentBucketIndex;
        private UAlshBucket<Key, Value> currentBucket;

        public UalshIterator() {
            this.currentTableIndex = 1;
            this.currentBucketIndex = 0;
            findNext();
        }

        private void findNext() {
            currentBucket = null;
            while (currentBucket == null && currentTableIndex <= 5) {
                UAlshBucket<Key, Value>[] table = (UAlshBucket<Key, Value>[]) getSubTable(currentTableIndex);
                while (currentBucketIndex < table.length) {
                    UAlshBucket<Key, Value> tempBucket = table[currentBucketIndex];
                    if (tempBucket != null && !tempBucket.isDeleted()) {
                        currentBucket = tempBucket;
                        currentBucketIndex++;
                        return;
                    }
                    currentBucketIndex++;
                }
                currentTableIndex++;
                currentBucketIndex = 0;
            }
        }

        @Override
        public boolean hasNext() {
            return currentBucket != null;
        }

        @Override
        public Key next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Key k = currentBucket.getKey();
            findNext();
            return k;
        }
    }
}
