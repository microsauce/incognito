package org.microsauce.incognito;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ArrayAdaptor extends List {

    protected Runtime runtime;
    protected Type type;
    protected Object target;
    // 25 methods
    @Override
    public int size() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isEmpty() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean contains(Object o) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterator iterator() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override // no
    public Object[] toArray() {
        return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean add(Object o) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean remove(Object o) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override  // no
    public boolean containsAll(Collection c) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean addAll(Collection c) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean addAll(int index, Collection c) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override   // no
    public boolean removeAll(Collection<?> c) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override   // no
    public boolean retainAll(Collection<?> c) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override    // ???
    public boolean equals(Object o) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override   // ???
    public int hashCode() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object get(int index) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object set(int index, Object element) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void add(int index, Object element) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object remove(int index) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int indexOf(Object o) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ListIterator listIterator() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ListIterator listIterator(int index) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override   // no
    public Object[] toArray(Object[] a) {
        return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
