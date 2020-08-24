package de.comparus.opensource.longmap;

import de.comparus.opensource.longmap.exceptions.NoBucketWithSuchKeyException;
import de.comparus.opensource.longmap.utils.ContextValidation;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LongMapImpl<V> implements LongMap<V> {
    private ContextValidation context;

    private Node<Long, V>[] hashTable;
    private int size = 0;
    private float threshold;

    private static final int NO_VALUES_IN_TABLE = 0;
    private static final int DOUBLE_THE_TABLE_SIZE = 2;

    public LongMapImpl() {
        hashTable = new Node[16];
        threshold = hashTable.length * 0.75f;
    }

    private class Node<Long, V> {
        private List<Node<Long, V>> nodes;
        private int hash;
        private Long key;
        private V value;

        public Node(Long key, V value) {
            this.key = key;
            this.value = value;
            nodes = new LinkedList<>();
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public Long getKey() {
            return key;
        }

        public int hash() {
            return hashCode() % hashTable.length;
        }

        public List<Node<Long, V>> getNodes() {
            return nodes;
        }

        @Override
        public int hashCode() {
            hash = 31;
            hash = hash * 17 + key.hashCode();
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj instanceof Node) {
                Node<Long, V> node = (Node) obj;
                return (Objects.equals(key, node.getKey())
                        && Objects.equals(value, node.getValue())
                        || Objects.equals(hash, node.hashCode()));
            }
            return false;
        }
    }

    /**
     * Put the key and the value in map.
     * @param key   key of the value
     * @param value value of the map element
     * @return  value by the key or null
     * @throws  IllegalArgumentException If a key < 0
     */
    public V put(long key, V value) {
        context.validateKey(key);
        if (size + 1 >= threshold) {
            threshold *= 2;
            arrayDoubling();
        }
        Node<Long, V> newNode = new Node<>(key, value);
        int index = newNode.hash();

        if (hashTable[index] == null) {
            return simpleAdd(index, newNode);
        }

        List<Node<Long, V>> nodeList = hashTable[index].getNodes();

        for (Node<Long, V> node : nodeList) {
            if (keyExistButValueNew(node, newNode, value)
                    || processCollision(node, newNode, nodeList)) {
                return newNode.getValue();
            }
        }
        return null;
    }

    private V simpleAdd(int index, Node<Long, V> newNode) {
        hashTable[index] = new Node<>(null, null);
        hashTable[index].getNodes().add(newNode);
        size++;
        return newNode.getValue();
    }

    private boolean keyExistButValueNew(
            Node<Long, V> nodeFromList,
            Node<Long, V> newNode,
            V value) {

        if (newNode.getKey().equals(nodeFromList.getKey())
                && !newNode.getValue().equals(nodeFromList.getValue())) {
            nodeFromList.setValue(value);
            return true;
        }
        return false;
    }

    private boolean processCollision(
            Node<Long, V> nodeFromList,
            Node<Long, V> newNode,
            List<Node<Long, V>> nodes) {

        if (newNode.hashCode() == nodeFromList.hashCode()
                && !Objects.equals(newNode.key, nodeFromList.key)
                && !Objects.equals(newNode.value, nodeFromList.value)) {
            nodes.add(newNode);
            size++;
            return true;
        }
        return false;
    }

    private void arrayDoubling() {
        Node<Long, V>[] oldHashTable = hashTable;
        hashTable = new Node[oldHashTable.length * DOUBLE_THE_TABLE_SIZE];
        size = NO_VALUES_IN_TABLE;
        for (Node<Long, V> node : oldHashTable) {
            if (node != null) {
                for (Node<Long, V> n : node.getNodes()) {
                    put(n.key, n.value);
                }
            }
        }
    }

    /**
     * Returns value by the key
     * @param key the key of value
     * @return value by the key or null
     */
    public V get(long key) {
        context.validateKey(key);
        int index = hash(key);
        if (index < hashTable.length && hashTable[index] != null) {
            List<Node<Long, V>> list = hashTable[index].getNodes();
            for (Node<Long, V> node : list) {
                if (Long.valueOf(key).equals(node.getKey())) {
                    return node.getValue();
                }
            }
        }
        return null;
    }

    private int hash(Long key) {
        int hash = 31;
        hash = hash * 17 + key.hashCode();
        return hash % hashTable.length;
    }

    /**
     * Removes value from map by the key
     * @param key   key of the value which will be removed
     * @return  old value or null
     * @throws NoBucketWithSuchKeyException if there is no bucket by requested key
     */
    public V remove(long key) throws NoBucketWithSuchKeyException {
        context.validateKey(key);
        int index = hash(key);
        if (hashTable[index] == null) {
            throw new NoBucketWithSuchKeyException("There is no bucket requested by key!");
        }

        if (hashTable[index].getNodes().size() == 1) {
            Node<Long, V> oldValue = hashTable[index].getNodes().get(0);
            hashTable[index].getNodes().remove(0);
            size--;
            return oldValue.getValue();
        }

        List<Node<Long, V>> nodeList = hashTable[index].getNodes();
        for (Node<Long, V> node : nodeList) {
            if (Long.valueOf(key).equals(node.getKey())) {
                V oldValue = node.getValue();
                nodeList.remove(node);
                size--;
                return oldValue;
            }
        }
        return null;
    }

    /**
     * Checks if the map is empty
     * @return  {@code true} or {@code false}
     */
    public boolean isEmpty() {
        return this.size == NO_VALUES_IN_TABLE;
    }

    /**
     * Checks if the key is in map
     * @param key   key of value
     * @return {@code true} or {@code false}
     */
    public boolean containsKey(long key) {
        context.validateKey(key);
        int index = hash(key);
        if (hashTable[index] != null) {
            return hashTable[index].getNodes().stream().filter(Objects::nonNull)
                    .anyMatch((element) -> element.getKey().equals(key));
        }
        return false;
    }

    /**
     * Checks if the value is in map
     * @param value value which must be in map
     * @return {@code true} or {@code false}
     */
    public boolean containsValue(final V value) {
        return Arrays.stream(hashTable).filter(Objects::nonNull)
                .anyMatch((bucket) -> bucket.getNodes().stream()
                        .anyMatch((element) -> element.getValue().equals(value)));
    }

    /**
     * Returns array of keys from the map
     * @return array of keys
     */
    public long[] keys() {
        List<Long> keys = Arrays.stream(hashTable).filter(Objects::nonNull)
                .flatMap((l) -> l.getNodes().stream()
                        .map(Node::getKey))
                .collect(Collectors.toCollection(LinkedList::new));

        return keys.stream().mapToLong(key -> key).toArray();
    }

    /**
     * Returns array of values from the map
     * @return array of generalized values
     */
    public V[] values() {
        List<V> values = Arrays.stream(hashTable).filter(Objects::nonNull)
                .flatMap((bucket) -> bucket.getNodes().stream()
                        .map(Node::getValue))
                .collect(Collectors.toCollection(ArrayList::new));

        return (V[]) values.stream().toArray();
    }

    public long size() {
        return size;
    }

    /**
     * Clears MyMap
     */
    public void clear() {
        for (int bucket = 0; bucket < hashTable.length; bucket++) {
            if (ifBucketNullAndNodesEmpty(bucket)) {
                List<Node<Long, V>> currentBucket = hashTable[bucket].getNodes();
                for (Node<Long, V> element : currentBucket) {
                    currentBucket.remove(element);
                }
            }
            hashTable[bucket] = null;
        }
        size = NO_VALUES_IN_TABLE;
    }

    private boolean ifBucketNullAndNodesEmpty(int indexOfBucket) {
        return hashTable[indexOfBucket] != null
                && !hashTable[indexOfBucket].getNodes().isEmpty();
    }

    public void setContext(ContextValidation context) {
        this.context = context;
    }
}

