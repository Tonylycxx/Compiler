package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class ThreeTuple<K, P, V> {

    private HashMap<K, TwoTuple<P, V>> dataMap;

    public ThreeTuple() {
        this.dataMap = new HashMap<>();
    }

    public TwoTuple<P, V> put(K key, P pos, V val) {
        TwoTuple<P, V> valueInfo = new TwoTuple<>(pos, val);
        dataMap.put(key, valueInfo);
        return valueInfo;
    }

    public TwoTuple<P, V> get(K key) {
        return dataMap.get(key);
    }

    public boolean containsKey(K key) {
        return dataMap.containsKey(key);
    }

    public int getKeySetLength() {
        return dataMap.keySet().size();
    }
    
    public Collection<TwoTuple<P,V>> getValueSet() {
        return dataMap.values();
    }

    public int size() {
        return dataMap.size();
    }

}
