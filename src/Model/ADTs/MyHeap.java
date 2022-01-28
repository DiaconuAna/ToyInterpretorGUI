package Model.ADTs;

import Exceptions.ADTsExceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MyHeap<V> implements HeapInterface<V>{

    int freeLocation;
    Map<Integer, V> heap;

    public MyHeap(){
        this.heap = new HashMap<Integer, V>();
        freeLocation = 1;
    }

    public MyHeap(HashMap<Integer, V> h){
        this.heap = h;
        freeLocation = 1;
    }

    public int getFreeLocation(){
        return this.freeLocation;
    }

    @Override
    public int allocate(V value) {
        synchronized (this) {
            int address = this.freeLocation;
            this.freeLocation++;
            this.heap.put(address, value);

            return address;
        }
    }

    @Override
    public V deallocate(int address) throws ADTsExceptions {
        synchronized (this) {
            if (isAddress(address))
                this.heap.remove(address);
            throw new ADTsExceptions("Cannot deallocate a non-existent address!");
        }
    }

    @Override
    public V getValue(int address) {
        return this.heap.get(address);
    }

    @Override
    public void put(int address, V value) {
        synchronized (this) {
            this.heap.put(address, value);
        }
    }

    @Override
    public boolean isAddress(int address) {
        return heap.containsKey(address);
    }

    @Override
    public Map<Integer, V> getContent() {
        return this.heap;
    }

    @Override
    public void setContent(Map<Integer, V> newContent) {
        synchronized (this) {
            this.heap = newContent;
        }
    }

    @Override
    public void update(int address, V value) {
        synchronized (this) {
            V val = heap.get(address);
            heap.put(address, value);
        }
    }

    @Override
    public void remove(int address) {
        synchronized (this) {
            if (isAddress(address))
                heap.remove(address);
        }

    }

    @Override
    public String toString(){

        String final_string = "{";
        for(Integer key: heap.keySet()){
            if(key != null){
                final_string += key.toString() + "->" + heap.get(key).toString() + " ; ";
            }
        }
        final_string += "}";
        return final_string;
    }
}
