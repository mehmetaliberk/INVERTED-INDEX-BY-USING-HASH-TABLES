package hw1;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class HashTable<K, V> implements DictionaryInterface<K, V> {

	private int numberOfEntries;
	private static final int DEFAULT_CAPACITY = 5;
	private TableEntry<K, V>[] hashTable;
	private int tableSize;
	private boolean initialized = false;
	private static final double MAX_LOAD_FACTOR = 0.5;
	public String CollisionHandlingType="LP";// write LP or DH to determine collision type
	public String HashFunctionType = "SSF";// write SSF or PAF to determine hash function type
	//public int collcount=1;
	public HashTable() {
		this(DEFAULT_CAPACITY);
	}
	
	public HashTable(int initialCapacity) {

		numberOfEntries = 0;
		int tableSize = getNextPrime(initialCapacity);
		@SuppressWarnings("unchecked")
		TableEntry<K, V>[] temp = (TableEntry<K, V>[]) new TableEntry[tableSize];
		hashTable = temp;
		initialized = true;
	}

	public void displayHashTable() {
		checkInitialization();
		
		for (int index = 0; index < hashTable.length; index++) {
			if (hashTable[index] == null) {
				
			} else if (hashTable[index].isRemoved()) {
				System.out.println("removed state");
			} else {
				System.out.println(index +" "+ hashTable[index].getKey() + " " + hashTable[index].getValue());
			}

		}
		System.out.println();
	}

	public V add(K key, V value) {

		checkInitialization();
		if ((key == null) || (value == null))
			throw new IllegalArgumentException();
		else {
			V oldValue;
			int index=0;
			if (HashFunctionType.equals("SSF")) {
				String string=(String) key;
				string = string.toLowerCase();
	        	char[] ch = string.toCharArray();
	        	 index=0;
	        	for (int i = 0; i < ch.length; i++) {
	    			index+=ch[i]-96;
	    			index=index%hashTable.length;
	    		}
			}
			else {
				String string=(String) key;
	        	char[] ch = string.toCharArray();
	        	 index=0;
	        	int prime = 33;
	        	for (int i = 0; i < ch.length; i++) {
	        		int temp = 0;
	        		temp = ch[i]-96;
	        		temp = (int) (temp *  Math.pow(prime, ch.length-(i+1)));
	        		index+=temp%hashTable.length;	
	        		index=index%hashTable.length;
	        	}
			}
			int U=1;
			
			while (hashTable[index]!=null) {
				if (CollisionHandlingType.equals("LP")) {
					index+=1;
					index=index%hashTable.length;
					//collcount++;
				}
				else {
					int Prime = 33;
					index=index+U*Prime;
					index=index%hashTable.length;
					U+=1;
					//collcount++;
				}
			}
			assert (index >= 0) && (index < hashTable.length);
			if ((hashTable[index] == null) || hashTable[index].isRemoved()) {
				hashTable[index] = new TableEntry<>(key, value);
				numberOfEntries++;
				oldValue = null;
			} else {
				oldValue = hashTable[index].getValue();
				hashTable[index].setValue(value);
			}

			if (isHashTableTooFull())
				enlargeHashTable();
			return oldValue;
		}

	}

	public V remove(K key) {

		checkInitialization();
		V removedValue = null;
		int index=0;
		if (HashFunctionType.equals("SSF")) {
			String string=(String) key;
			string = string.toLowerCase();
        	char[] ch = string.toCharArray();
        	 index=0;
        	for (int i = 0; i < ch.length; i++) {
    			index+=ch[i]-96;
    			index=index%hashTable.length;
    		}
		}
		else {
			String string=(String) key;
        	char[] ch = string.toCharArray();
        	 index=0;
        	int prime = 33;
        	for (int i = 0; i < ch.length; i++) {
        		int temp = 0;
        		temp = ch[i]-96;
        		temp = (int) (temp *  Math.pow(prime, ch.length-(i+1)));
        		index+=temp%hashTable.length;	
        		index=index%hashTable.length;
        	}
		}
		index = locate(index, key);
		if (index != -1) {
			removedValue = hashTable[index].getValue();
			hashTable[index].setToRemoved();
			numberOfEntries--;
		}
		return removedValue;

	}

	public V getValue(K key) {
		
		checkInitialization();
		//long StartTime = System.nanoTime()
		V result = null;
		int index=0;
		if (HashFunctionType.equals("SSF")) {
			String string=(String) key;
			string = string.toLowerCase();
        	char[] ch = string.toCharArray();
        	 index=0;
        	for (int i = 0; i < ch.length; i++) {
    			index+=ch[i]-96;
    			index=index%hashTable.length;
    		}
		}
		else {
			String string=(String) key;
        	char[] ch = string.toCharArray();
        	 index=0;
        	int prime = 33;
        	for (int i = 0; i < ch.length; i++) {
        		int temp = 0;
        		temp = ch[i]-96;
        		temp = (int) (temp *  Math.pow(prime, ch.length-(i+1)));
        		index+=temp%hashTable.length;	
        		index=index%hashTable.length;
        	}
		}
		index = locate(index, key);
		if (index != -1)
			result = hashTable[index].getValue();
		//long endTime = System.nanoTime()
		//long Time=StartTime-endTime;
		//System.out.print(Time);
		return result;

	}

	public boolean contains(K key) {
		checkInitialization();

		int index=0;
		if (HashFunctionType.equals("SSF")) {
			String string=(String) key;
			string = string.toLowerCase();
        	char[] ch = string.toCharArray();
        	 index=0;
        	for (int i = 0; i < ch.length; i++) {
    			index+=ch[i]-96;
    			index=index%hashTable.length;
    		}
		}
		else {
			String string=(String) key;
        	char[] ch = string.toCharArray();
        	 index=0;
        	int prime = 33;
        	for (int i = 0; i < ch.length; i++) {
        		int temp = 0;
        		temp = ch[i]-96;
        		temp = (int) (temp *  Math.pow(prime, ch.length-(i+1)));
        		index+=temp%hashTable.length;	
        		index=index%hashTable.length;
        	}
		}
		if ((index < numberOfEntries) && key.equals(hashTable[index].getKey()))
			return true;
		return false;

	}

	public boolean isEmpty() {

		return numberOfEntries == 0;

	}

	public int getSize() {

		return numberOfEntries;

	}

	public void clear() {

		for (int i = 0; i < numberOfEntries; i++) {
			hashTable[i] = null;
		}
		numberOfEntries = 0;

	}

	public Iterator<K> getKeyIterator() {

		return new KeyIterator();

	}

	public Iterator<V> getValueIterator() {

		return new ValueIterator();

	}



	
	private int locate(int index, K key) {
		boolean found = false;

		while (!found && (hashTable[index] != null)) {

			if (hashTable[index].isIn() && key.equals(hashTable[index].getKey())) {

				found = true;
			} else {

				index = (index + 1) % hashTable.length;
			}

		}

		int result = -1;
		if (found) {
			result = index;
		}
		return result;

	}

	private void enlargeHashTable() {

		TableEntry<K, V>[] oldTable = hashTable;
		int oldSize = hashTable.length;
		int newSize = getNextPrime(oldSize + oldSize);
		@SuppressWarnings("unchecked")
		TableEntry<K, V>[] tempTable = (TableEntry<K, V>[]) new TableEntry[newSize];
		hashTable = tempTable;
		numberOfEntries = 0;
		for (int index = 0; index < oldSize; index++) {
			if ((oldTable[index] != null) && oldTable[index].isIn()) {
				add(oldTable[index].getKey(), oldTable[index].getValue());
			}

		}

	}

	private boolean isHashTableTooFull() {
		return numberOfEntries > MAX_LOAD_FACTOR * hashTable.length;

	}

	private int getNextPrime(int integer) {

		if (integer % 2 == 0) {
			integer++;
		}

		while (!isPrime(integer)) {
			integer = integer + 2;
		}
		return integer;
	}

	static boolean isPrime(int n) {
		// Corner cases
		if (n <= 1)
			return false;
		if (n <= 3)
			return true;

		if (n % 2 == 0 || n % 3 == 0)
			return false;

		for (int i = 5; i * i <= n; i = i + 6)
			if (n % i == 0 || n % (i + 2) == 0)
				return false;

		return true;
	}

	private void checkInitialization() {

		if (!initialized)
			throw new SecurityException("HashedDictionary object is not initialized properly.");
	}



	private class KeyIterator implements Iterator<K> {

		private int currentIndex;
		private int numberLeft;

		private KeyIterator() {

			currentIndex = 0;
			numberLeft = numberOfEntries;

		}

		public boolean hasNext() {

			return numberLeft > 0;

		}

		public K next() {
			K result = null;
			if (hasNext()) {

				while ((hashTable[currentIndex] == null) || hashTable[currentIndex].isRemoved()) {

					currentIndex++;
				}

				result = hashTable[currentIndex].getKey();
				numberLeft--;
				currentIndex++;
			} else {

				throw new NoSuchElementException();
			}
			return result;

		}

		public void remove() {

			throw new UnsupportedOperationException();
		}

	}

	private class ValueIterator implements Iterator<V> {

		private int currentIndex;
		private int numberLeft;

		private ValueIterator() {

			currentIndex = 0;
			numberLeft = numberOfEntries;

		}

		public boolean hasNext() {

			return numberLeft > 0;

		}

		public V next() {

			V result = null;
			if (hasNext()) {

				while ((hashTable[currentIndex] == null) || hashTable[currentIndex].isRemoved()) {

					currentIndex++;

				}

				result = hashTable[currentIndex].getValue();
				numberLeft--;
				currentIndex++;

			} else {

				throw new NoSuchElementException();
			}

			return result;

		}

		public void remove() {

			throw new UnsupportedOperationException();

		}

	}

	private static class TableEntry<S, T> {

		private S key;
		private T value;
		private States state;

		private enum States {

			CURRENT, REMOVED

		}

		private TableEntry(S searchKey, T dataValue) {

			key = searchKey;
			value = dataValue;
			state = States.CURRENT;

		}

		private S getKey() {

			return key;

		}

		private T getValue() {

			return value;

		}

		private void setValue(T newValue) {

			value = newValue;

		}

		private boolean isIn() {

			return state == States.CURRENT;

		}

		private boolean isRemoved() {

			return state == States.REMOVED;

		}

		private void setToRemoved() {

			key = null;
			value = null;
			state = States.REMOVED;

		}

		private void setToIn() {

			state = States.CURRENT;

		}
	
		
	}

}