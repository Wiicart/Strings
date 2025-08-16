package com.pedestriamc.strings.api.collections;

import org.apache.commons.collections4.iterators.ArrayIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * A Buffer that only goes up to a certain size.
 * If the maximum size is reached, the oldest element is dropped.
 * @param <T> The type for this Collection.
 */
@SuppressWarnings("unchecked")
public class BoundedLinkedBuffer<T> implements Collection<T> {

    private int size = 0;

    private final int capacity;

    private Node<T> head;
    private Node<T> tail;

    public BoundedLinkedBuffer(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean add(T t) {
        if (head == null) {
            head = new Node<>(t);
            tail = head;
            size++;
            return true;
        }

        head.previous = new Node<>(t, head);
        head = head.previous;

        if (size == capacity) {
            if (tail != null) {
                Node<T> previous = tail.previous();
                if (previous != null) {
                    previous.next = null;
                }
                tail = previous;
            }
        } else {
            size++;
        }

        return true;
    }


    @Override
    public boolean remove(Object o) {
        Node<T> current = head;
        while (current != null) {
            if (!Objects.equals(current.get(), o)) {
                current = current.next();
                continue;
            }

            Node<T> next = current.next();
            Node<T> previous = current.previous();

            if (previous == null) {
                head = next;
            } else {
                previous.next = next;
            }

            if (next == null) {
                tail = previous;
            } else {
                next.previous = previous;
            }

            size--;
            return true;
        }

        return false;
    }


    @Nullable
    public Node<T> getHead() {
        return head;
    }

    @Nullable
    public Node<T> getTail() {
        return tail;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        Node<T> current = head;
        while (current != null) {
            T val = current.get();
            if (Objects.equals(val, o)) {
                return true;
            }

            current = current.next();
        }

        return false;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new ArrayIterator<>(toArray());
    }

    @Override
    public Object @NotNull [] toArray() {
        Object[] array = new Object[size];
        Node<T> current = head;
        int pos = 0;
        while (current != null) {
            array[pos++] = current.get();
            current = current.next();
        }

        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V @NotNull [] toArray(V @NotNull [] a) {
        return (V[]) new Object[0];
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        boolean changed = false;
        for (T t : c) {
            if (add(t)) {
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            if (remove(o)) {
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        boolean changed = false;
        T[] values = (T[]) toArray();
        for (T t : values) {
            if (!c.contains(t)) {
                remove(t);
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public void clear() {
        size = 0;
        head = null;
        tail = null;
    }

    public static final class Node<T> {

        private final T t;

        private Node<T> next;
        private Node<T> previous;

        Node(T t) {
            this(t, null, null);
        }

        Node(T t, Node<T> next) {
            this(t, next, null);
        }

        Node(T t, Node<T> next, Node<T> prev) {
            this.t = t;
            this.next = next;
            this.previous = prev;
        }

        /**
         * Gets the next Node, moving from head towards the tail.
         * @return The next Node
         */
        @Nullable
        public Node<T> next() {
            return next;
        }

        /**
         * Gets the previous node, moving from the tail towards the head.
         * @return The previous Node
         */
        @Nullable
        public Node<T> previous() {
            return previous;
        }

        /**
         * Provides the value of this Node
         * @return The Node's value
         */
        @Nullable
        public T get() {
            return t;
        }
    }
}
