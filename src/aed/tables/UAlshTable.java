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

    UAlshBucket(Value value, Key key, int khc1, int khc2, int maxSharedTable) {
        this.value = value;
        this.key = key;
        this.maxSharedTable = maxSharedTable;
        this.hc1 = khc1;
        this.hc2 = khc2;
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
        return (key == null || isDeleted());
    }

    @Override
    public boolean isDeleted() {
        return this.value == null;
    }

    void delete() {
        this.value = null;
    }
}

public class UAlshTable<Key, Value> {

    //mudei de ideais relativamente aos primos iniciais, iremos usar
    //37, 17, 11, 7, e 5. Esta mudança não tem qualquer impacto significativo
    private static final int[] primes = {5, 7, 11, 17, 37, 79, 163, 331, 673, 1361, 2729, 5471, 10949, 21911, 43853, 87719, 175447, 350899, 701819, 1403641, 2807303, 5614657, 11229331, 22458671, 44917381, 89834777, 179669557};
    private final int DEFAULT_PRIME_INDEX = 4;
    private final Function<Key, Integer> hc2;
    int primeIndex;
    private int min;
    private int size;
    private int deletedKeys;
    //Tabelas
    private UAlshBucket<Key, Value>[] t1;
    private UAlshBucket<Key, Value>[] t2;
    private UAlshBucket<Key, Value>[] t3;
    private UAlshBucket<Key, Value>[] t4;
    private UAlshBucket<Key, Value>[] t5;

    public UAlshTable(Function<Key, Integer> hc2) {
        this.hc2 = hc2;
        this.size = 0;
        this.primeIndex = 4;
        this.deletedKeys = 0;

        initTables(DEFAULT_PRIME_INDEX);
    }

    private UAlshTable(Function<Key, Integer> hc2, int primeIndex) {
        this.hc2 = hc2;
        this.size = 0;
        this.primeIndex = primeIndex;
        this.deletedKeys = 0;

        initTables(primeIndex);
    }

    @SuppressWarnings("unchecked")
    private void initTables(int primeIndex) {

        this.t1 = (UAlshBucket<Key, Value>[]) new UAlshBucket[primes[primeIndex]];
        this.t2 = (UAlshBucket<Key, Value>[]) new UAlshBucket[primes[primeIndex - 1]];
        this.t3 = (UAlshBucket<Key, Value>[]) new UAlshBucket[primes[primeIndex - 2]];
        this.t4 = (UAlshBucket<Key, Value>[]) new UAlshBucket[primes[primeIndex - 3]];
        this.t5 = (UAlshBucket<Key, Value>[]) new UAlshBucket[primes[primeIndex - 4]];
    }

    private void resetMin() {
        min = Integer.MAX_VALUE;
    }

    public int size() {
        return this.size - this.deletedKeys;
    }

    private int UAsh(int i, int kch1, int kch2) {
        int h = ((kch1 + (i * kch2)) & 0x7fffffff);
        return h % getSubTable(i).length;
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
        if (new_primeIndex > primeIndex)
            new_primeIndex = Math.min(new_primeIndex, primes.length - 1);
        else
            new_primeIndex = Math.max(new_primeIndex, DEFAULT_PRIME_INDEX);

        UAlshTable<Key, Value> new_table = new UAlshTable<>(this.hc2, new_primeIndex);

        for (int i = 1; i <= 5; i++) {
            UAlshBucket<Key, Value>[] sub_table = (UAlshBucket<Key, Value>[]) getSubTable(i);
            for (UAlshBucket<Key, Value> bucket : sub_table) {
                if (bucket != null && !bucket.isDeleted()) {
                    new_table.fastPut(bucket.getKey(), bucket.getValue(), bucket.hc1, bucket.hc2);
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
        this.primeIndex = new_table.primeIndex;
    }

    public IUAlshBucket<Key, Value>[] getSubTable(int i) {
        return switch (i) {
            case 1 -> t1;
            case 2 -> t2;
            case 3 -> t3;
            case 4 -> t4;
            case 5 -> t5;
            default -> null;
        };
    }

    public boolean containsKey(Key k) {
        return get(k) != null;
    }

    @SuppressWarnings("unchecked")
    private UAlshBucket<Key, Value>[] possibleBuckets(int khc1, int khc2) {
        resetMin();
        UAlshBucket<Key, Value>[] buckets = (UAlshBucket<Key, Value>[]) new UAlshBucket[5];

        for (int i = 1; i <= 5; i++) {
            UAlshBucket<Key, Value> bucket = (UAlshBucket<Key, Value>) getSubTable(i)[UAsh(i, khc1, khc2)];
            if (bucket == null) {
                break;
            }
            buckets[i - 1] = bucket;
            min = Math.min(min, bucket.getMaxSharedTable());
        }

        if (min == Integer.MAX_VALUE)
            min = -1;
        return buckets;
    }

    public Value get(Key k) {
        int khc1 = k.hashCode();
        int khc2 = hc2.apply(k);

        UAlshBucket<Key, Value>[] buckets = possibleBuckets(khc1, khc2);

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

        UAlshBucket<Key, Value>[] buckets = possibleBuckets(khc1, khc2);

        for (int i = min - 1; i >= 0; i--) {
            if ((buckets[i] != null && buckets[i].hc1 == khc1 && buckets[i].hc2 == khc2)) {
                if (buckets[i].getKey().equals(k)) {
                    if (buckets[i].isDeleted()) {
                        this.deletedKeys--;
                    }
                    buckets[i].value = v;
                    return;
                }
            }
        }

        fastPut(k, v, khc1, khc2);
    }

    public void fastPut(Key k, Value v) {
        int khc1 = k.hashCode();
        int khc2 = hc2.apply(k);

        fastPut(k, v, khc1, khc2);
    }

    private void fastPut(Key k, Value v, int khc1, int khc2) {
        if (this.size >= 0.85 * t1.length) {
            resize(primeIndex + 1);
        }

        boolean wasAdded = fastPutHelper(k, v, khc1, khc2);
        while (!wasAdded && primeIndex < primes.length - 1) {
            resize(primeIndex + 1);
            wasAdded = fastPutHelper(k, v, khc1, khc2);
        }

    }

    @SuppressWarnings("unchecked")
    private boolean fastPutHelper(Key k, Value v, int khc1, int khc2) {
        UAlshBucket<Key, Value>[] buckets = (UAlshBucket<Key, Value>[]) new UAlshBucket[5];
        int sharedTable = 0;
        for (int i = 1; i <= 5; i++) {
            int UAsh = UAsh(i, khc1, khc2);
            UAlshBucket<Key, Value>[] table = (UAlshBucket<Key, Value>[]) getSubTable(i);

            if (table[UAsh] == null) {
                table[UAsh] = new UAlshBucket<>(v, k, khc1, khc2, 0);
                buckets[i - 1] = table[UAsh];
                sharedTable = i;
                this.size++;
                break;
            }
            buckets[i - 1] = table[UAsh];
        }
        for (UAlshBucket<Key, Value> bucket : buckets) {
            if (bucket != null)
                bucket.maxSharedTable = Math.max(bucket.maxSharedTable, sharedTable);
        }

        return sharedTable != 0;
    }

    public void delete(Key k) {
        int khc1 = k.hashCode();
        int khc2 = hc2.apply(k);

        UAlshBucket<Key, Value>[] buckets = possibleBuckets(khc1, khc2);

        for (int i = min - 1; i >= 0; i--) {
            if (buckets[i] != null && !buckets[i].isDeleted() && buckets[i].hc1 == khc1 && buckets[i].hc2 == khc2) {
                if (buckets[i].getKey().equals(k)) {
                    buckets[i].delete();
                    this.deletedKeys++;
                    break;
                }
            }
        }

        if (primeIndex > DEFAULT_PRIME_INDEX && size() < 0.25 * primes[primeIndex])
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

            while (currentTableIndex <= 5) {
                UAlshBucket<Key, Value>[] table = (UAlshBucket<Key, Value>[]) getSubTable(currentTableIndex);

                while (currentBucketIndex < table.length) {
                    UAlshBucket<Key, Value> tempBucket = table[currentBucketIndex++];
                    if (tempBucket != null && !tempBucket.isDeleted()) {
                        currentBucket = tempBucket;
                        return;
                    }
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
