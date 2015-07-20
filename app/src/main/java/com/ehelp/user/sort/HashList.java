package com.ehelp.user.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class HashList<K, V> {

	private List<K> keyArr = new ArrayList<K>();
	private HashMap<K, List<V>> map = new HashMap<K, List<V>>();
	private KeySort<K, V> keySort;

	public HashList(KeySort<K, V> keySort) {
		// TODO Auto-generated constructor stub
		this.keySort = keySort;
	}

	public K getKey(V v) {
		return keySort.getKey(v);
	}


	public void sortKeyComparator(Comparator<K> comparator) {

		Collections.sort(keyArr, comparator);
	}

	public K getKeyIndex(int key) {
		return keyArr.get(key);
	}


	public List<V> getValueListIndex(int key) {
		return map.get(getKeyIndex(key));
	}

	public V getValueIndex(int key, int value) {
		return getValueListIndex(key).get(value);

	}

	public int size() {
		return keyArr.size();
	}

	public void clear() {
		for (Iterator<K> it = map.keySet().iterator(); it.hasNext(); map.remove(it.next()));
	}

	public boolean contains(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public Object remove(int location) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean remove(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeAll(Collection arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean retainAll(Collection arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public Object set(int location, Object object) {
		// TODO Auto-generated method stub
		return keyArr.set(location, (K) object);
	}

	public List subList(int start, int end) {
		// TODO Auto-generated method stub
		return keyArr.subList(start, end);
	}

	public Object[] toArray() {
		// TODO Auto-generated method stub
		return keyArr.toArray();
	}

	public Object[] toArray(Object[] array) {
		return keyArr.toArray(array);
	}

	public boolean add(Object object) {
		V v = (V) object;
		K key = getKey(v);
		if (!map.containsKey(key)) {
			List<V> list = new ArrayList<V>();
			list.add(v);
			keyArr.add(key);
			map.put(key, list);
		} else {
			map.get(key).add(v);
		}
		return false;
	}
	public int indexOfKey(K k)
	{
		return keyArr.indexOf(k);
	}
}
