import java.util.function.Function;

class UAlshBucket<Key, Value> implements IUAlshBucket<Key, Value> {
    //devem guardar no balde tudo o que for necessário para implementar
    //as funções públicas da interface. Mas podem também adicionar outros
    //campos e métodos (incluindo o construtor), caso o entendam.

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

    @Override
    public boolean isEmpty() {
        //TODO: implement
        return false;
    }

    @Override
    public boolean isDeleted() {
        //TODO: implement
        return false;
    }
}

public class UAlshTable<Key, Value> {

    //mudei de ideais relativamente aos primos iniciais, iremos usar
    //37, 17, 11, 7, e 5. Esta mudança não tem qualquer impacto significativo
    private static final int[] primes = {
            5, 7, 11, 17, 37, 79, 163, 331,
            673, 1361, 2729, 5471, 10949,
            21911, 43853, 87719, 175447, 350899,
            701819, 1403641, 2807303, 5614657,
            11229331, 22458671, 44917381, 89834777, 179669557
    };


    public UAlshTable(Function<Key, Integer> hc2) {
        //TODO: implement
    }

    public int size() {
        //TODO: implement
        return 0;
    }

    public int getMainCapacity() {
        //TODO: implement
        return 0;
    }

    public int getTotalCapacity() {
        //TODO: implement
        return 0;
    }

    public float getLoadFactor() {
        //TODO: implement
        return 0;
    }

    public int getDeletedNotRemoved() {
        //TODO: implement
        return 0;
    }

    IUAlshBucket<Key, Value> getSubTable(int i) {
        //dica: nesta função podem e devem retornar um array de UAlshBuckets.
        //O Java vai verificar que isto é correcto, pois se um UAlshBucket é um
        //IUAlshBucket, então um array de UAlshBuckets é um array de IUAlshBuckets.
        //TODO: implement
        return null;
    }

    public boolean containsKey(Key k) {
        //TODO: implement
        return false;
    }

    public Value get(Key k) {
        //TODO: implement
        return null;
    }

    public void put(Key k, Value v) {
        //TODO: implement
    }

    public void fastPut(Key k, Value v) {
        //TODO: implement
    }

    public void delete() {
        //TODO: implement
    }

    public Iterable<Key> keys() {
        //TODO: implement
        return null;
    }
}
