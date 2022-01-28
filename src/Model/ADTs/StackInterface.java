package Model.ADTs;
import Exceptions.ADTsExceptions;

import java.util.List;

public interface StackInterface<T> {
    T pop() throws ADTsExceptions;
    public void push(T v);
    boolean isEmpty();
    String toString();
    List<T> getAll();
}
