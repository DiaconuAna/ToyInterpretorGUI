package Model.ADTs;
import Exceptions.ADTsExceptions;

import java.util.LinkedList;
import java.util.List;

public class MyList<T> implements ListInterface<T> {

    LinkedList<T> list;

    public MyList(){
        this.list = new LinkedList<T>();
    }

    @Override
    public void add(T v) {
        synchronized (this) {
            this.list.add(v);
        }
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public void clear() {
        synchronized (this) {
            this.list.clear();
        }
    }

    @Override
    public ListInterface<T> clone() {
        ListInterface<T> newlist = new MyList<>();
        for(T el: this.list) {
            newlist.add(el);
        }
        return newlist;
    }

    @Override
    public List<T> getAll() {
        return list;
    }

    public T getFirst() throws ADTsExceptions{
        synchronized (this) {
            if (this.list.size() == 0)
                throw new ADTsExceptions("List is empty.");
            return this.list.getFirst();
        }
    }

    public T getByIndex(int index) throws ADTsExceptions {
        if(index >= this.list.size())
            throw new ADTsExceptions("Invalid index.");
        return this.list.get(index);
    }

    @Override
    public String toString(){
        return this.list.toString();
    }
}
