package Model.ADTs;
import Exceptions.ADTsExceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Dictionary<K, V> implements DictInterface<K, V> {
    Map<K,V> dictionary;

    public Dictionary() {
        this.dictionary = new HashMap<K, V>();
    }

    @Override
    public void add(K key, V value) throws ADTsExceptions {
        synchronized (this) {
            if (this.dictionary.containsKey(key))
                throw new ADTsExceptions("Cannot add a value with an already existent key!");
            this.dictionary.put(key, value);
        }

    }

    @Override
    public void update(K key, V value) throws ADTsExceptions{
        synchronized (this) {
            if (!this.dictionary.containsKey(key))
                throw new ADTsExceptions("Cannot update a nonexistent element");
            this.dictionary.replace(key, value);
        }
    }

    @Override
    public V lookup(K info) {
        return this.dictionary.get(info);
    }

    @Override
    public boolean isDefined(K id) {
        return this.dictionary.containsKey(id);
    }

    @Override
    public void remove(K id) throws ADTsExceptions {
        synchronized (this) {
            if (this.lookup(id) == null)
                throw new ADTsExceptions("There is no value  to be removed");
            else
                this.dictionary.remove(id);
        }
    }

    @Override
    public String toString(){
        return this.dictionary.toString();
    }

    @Override
    public Map<K, V> getContent() {
        return this.dictionary;
    }

    @Override
    public DictInterface<K, V> clone() {
        DictInterface<K,V> new_dictionary = new Dictionary<>();
        for(K key: this.dictionary.keySet()) {
            try {
                new_dictionary.add(key, dictionary.get(key));
            } catch (ADTsExceptions e) {
                e.printStackTrace();
            }
        }
        return new_dictionary;
    }
}
