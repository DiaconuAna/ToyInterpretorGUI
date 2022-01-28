package Model.ADTs;
import Exceptions.ADTsExceptions;

import java.util.List;


public interface ListInterface<T> {
    void add(T v);
    String toString();
    boolean isEmpty();
    void clear();
    public ListInterface<T> clone();
    List<T> getAll();

}
