package nl.han.ica.datastructures;

import java.util.EmptyStackException;
import java.util.LinkedList;

public class HANStack<T> implements IHANStack<T>{
    LinkedList<T> list = new LinkedList<>();
    @Override
    public void push(T value) {
        list.add(value);
    }

    @Override
    public T pop() {
        if (list.isEmpty()) {
            throw new EmptyStackException();
        }
        return list.remove(list.size() - 1);

    }

    @Override
    public T peek() {
        if (list.isEmpty()) {
            throw new EmptyStackException();
        }
        return list.get(list.size() - 1);

    }
}
