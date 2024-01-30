package de.tuberlin.dima.dbt.exercises.bplustree;

import com.sun.source.tree.IfTree;

import java.util.*;

/**
 * Implementation of a B+ tree.
 * <p>
 * The capacity of the tree is given by the capacity argument to the
 * constructor. Each node has at least {capacity/2} and at most {capacity} many
 * keys. The values are strings and are stored at the leaves of the tree.
 * <p>
 * For each inner node, the following conditions hold:
 * <p>
 * {pre}
 * Integer[] keys = innerNode.getKeys();
 * Node[] children = innerNode.getChildren();
 * {pre}
 * <p>
 * - All keys in {children[i].getKeys()} are smaller than {keys[i]}.
 * - All keys in {children[j].getKeys()} are greater or equal than {keys[i]}
 * if j > i.
 */
public class BPlusTree {

    ///// Implement these methods


    private Node lookForChildNode(Integer key, Node node, Deque<InnerNode> parents) {
        InnerNode tempInnerNode = (InnerNode) node;
        if (parents != null) {
            parents.addLast(tempInnerNode);
        }
        Integer[] keyArray = cleanKeys(node.getKeys());
        for (int i = 0; i < keyArray.length; i++) {

            if (key < keyArray[i]) {
                node = getNode(tempInnerNode.getChildren(), i);
                break;

            } else if (i + 1 == keyArray.length) {
                node = getNode(tempInnerNode.getChildren(), i + 1);
                break;
            }

        }
        return node;
    }

    private LeafNode findLeafNode(Integer key, Node node, Deque<InnerNode> parents) {
        if (node instanceof LeafNode) {
            return (LeafNode) node;
        } else {
            boolean hasMoreDepth = true;
            while (hasMoreDepth) {
                node = lookForChildNode(key, node, parents);

                if (node != null && node.getKeys().length == node.getPayload().length) {
                    hasMoreDepth = false;
                }
            }

            return (LeafNode) node;
        }
    }

    private Node getNode(Node[] array, int index) {
        return array[index];
    }


    private String lookupInLeafNode(Integer key, LeafNode node) {
        // TODO: lookup value in leaf node
        if (node != null) {
            int index = Arrays.binarySearch(node.getKeys(), key);
            String[] values = node.getValues();
            if (index < 0) {
                return null;
            } else {
                return values[index];
            }
        }
        return null;
    }

    private void insertIntoLeafNode(Integer key, String value, LeafNode node, Deque<InnerNode> parents) {
        // TODO: insert value into leaf node (and propagate changes up)
        if (node != null) {
            Integer[] keyArray = cleanKeys(node.getKeys());
            String[] valueArray = cleanValues(node.getValues());

            ArrayList<Integer> tempKeyArraylist = new ArrayList<>(List.of(keyArray));
            tempKeyArraylist.add(key);
            Collections.sort(tempKeyArraylist);

            ArrayList<String> tempValueArraylist = new ArrayList<>(List.of(valueArray));
            tempValueArraylist.add(value);
            Collections.sort(tempValueArraylist);

            int size = tempValueArraylist.size();
            int halfSize = size / 2;
            Integer[] keys1 = new Integer[halfSize];
            String[] values1 = new String[halfSize];
            Integer[] keys2 = new Integer[halfSize + 1];
            String[] values2 = new String[halfSize + 1];
            Node node1;
            Node node2;

            if (size > BPlusTreeUtilities.CAPACITY) {
                for (int i = 0; i < size; i++) {
                    if (i < halfSize) {
                        keys1[i] = tempKeyArraylist.get(i);
                        values1[i] = tempValueArraylist.get(i);
                    } else {
                        keys2[i - 2] = tempKeyArraylist.get(i);
                        values2[i - 2] = tempValueArraylist.get(i);
                    }
                }
                node1 = new LeafNode(keys1, values1, BPlusTreeUtilities.CAPACITY);
                node2 = new LeafNode(keys2, values2, BPlusTreeUtilities.CAPACITY);

                Node[] newInnerNodeArray = new Node[BPlusTreeUtilities.CAPACITY + 1];
                Node[] oldInnerNodeArray = cleanChildren(parents.getLast().getChildren());

                int j = 0;
                for (int i = 0; i < oldInnerNodeArray.length; i++) {
                    if (oldInnerNodeArray[i] != node) {
                        newInnerNodeArray[j] = oldInnerNodeArray[i];
                    } else {
                        newInnerNodeArray[j] = node1;
                        j++;
                        newInnerNodeArray[j] = node2;

                    }
                    j++;
                }

                Integer[] newKeys = generateNewKeys(newInnerNodeArray.length - 1, newInnerNodeArray);

                // NUR FÜR max HEIGHT=3 nicht beliebig groß
                Node newNode = new InnerNode(newKeys, newInnerNodeArray, BPlusTreeUtilities.CAPACITY);
                Node[] payLoadLastLast = newInnerNodeArray;

                if (parents.size() > 1) {
                    boolean hasMoreElements = true;
                    while (hasMoreElements) {
                        Node last = parents.pollLast();
                        InnerNode lastLast = parents.getLast();
                        payLoadLastLast = cleanChildren((Node[]) lastLast.getPayload());

                        // final
                        for (int i = 0; i < payLoadLastLast.length; i++) {
                            if (last == payLoadLastLast[i]) {
                                payLoadLastLast[i] = newNode;
                            }
                        }

                        if (parents.size() == 1){
                            hasMoreElements = false;
                        }
                    }
                }

                root.setPayload(payLoadLastLast);
                root.setKeys(generateNewKeys(payLoadLastLast.length-1,payLoadLastLast));
            }

            node.setValues(tempValueArraylist.toArray(new String[0]));
            node.setKeys(tempKeyArraylist.toArray(new Integer[0]));
        }
    }

    public static Node[] cleanChildren(final Node[] v) {
        List<Node> list = new ArrayList<Node>(Arrays.asList(v));
        list.removeAll(Collections.singleton(null));
        return list.toArray(new Node[0]);
    }


    public static Integer[] cleanKeys(final Integer[] v) {
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(v));
        list.removeAll(Collections.singleton(null));
        return list.toArray(new Integer[0]);
    }

    public static String[] cleanValues(final String[] v) {
        List<String> list = new ArrayList<String>(Arrays.asList(v));
        list.removeAll(Collections.singleton(null));
        return list.toArray(new String[list.size()]);
    }

    private String deleteFromLeafNode(Integer key, LeafNode node,
                                      Deque<InnerNode> parents) {
        // TODO: delete value from leaf node (and propagate changes up)
        Integer[] keyArray = cleanKeys(node.getKeys());
        String[] valueArray = cleanValues(node.getValues());
        List<Integer> keyList = Arrays.asList(keyArray);
        int index = keyList.indexOf(key);
        String removeValue = valueArray[index];
        keyArray[index] = null;
        valueArray[index] = null;
        node.setValues(cleanValues(valueArray));
        node.setKeys(cleanKeys(keyArray));

        if (!parents.isEmpty() && parents.getLast() != null) {
            Node[] allChildren = parents.pollLast().getChildren();
            Node[] cleanedChildren = cleanChildren(allChildren);

            int indexOfCorrespodingChild = 0;
            for (int i = 0; i < cleanedChildren.length; i++) {
                if (node == cleanedChildren[i]) {
                    indexOfCorrespodingChild = i;
                    break;
                }
            }

            boolean stealingOrMergingSucced = false;

            // left sibling
            stealingOrMergingSucced = stealingFromLeftSibling(node, parents, cleanedChildren, indexOfCorrespodingChild, allChildren);


            // right sibling
            if (!stealingOrMergingSucced) {
                stealingOrMergingSucced = stealingFromRightSibling(node, parents, cleanedChildren, indexOfCorrespodingChild, allChildren);
            }

            if (!stealingOrMergingSucced) {
                // In höheren parents gucken
            }

            // Merge with sibling
            if (!stealingOrMergingSucced && cleanKeys(node.getKeys()).length < BPlusTreeUtilities.CAPACITY / 2) {
                if (cleanedChildren.length - 1 > indexOfCorrespodingChild) {
                    mergeWithRightChild(node, cleanedChildren, indexOfCorrespodingChild, allChildren);
                } else {
                    mergeWithLeftChild(node, cleanedChildren, indexOfCorrespodingChild, allChildren);
                }
            }
        }
        return removeValue;
    }

    private void mergeWithLeftChild(LeafNode node, Node[] cleanedChildren, int indexOfCorrespodingChild, Node[] allChildren) {
        if (cleanedChildren[indexOfCorrespodingChild - 1] != null) {
            Integer[] keysOfSibling = cleanKeys(cleanedChildren[indexOfCorrespodingChild - 1].getKeys());
            String[] valuesOfSibling = cleanValues((String[]) cleanedChildren[indexOfCorrespodingChild - 1].getPayload());

            Integer[] newKeys = new Integer[BPlusTreeUtilities.CAPACITY];
            String[] newValues = new String[BPlusTreeUtilities.CAPACITY];

            for (int i = 0; i < keysOfSibling.length; i++) {
                newKeys[i] = keysOfSibling[i];
                newValues[i] = valuesOfSibling[i];
            }
            Integer[] nodeKeys = node.getKeys();
            String[] nodeValues = node.getValues();

            newKeys[keysOfSibling.length] = nodeKeys[0];
            newValues[keysOfSibling.length] = nodeValues[0];

            node.setKeys(newKeys);
            node.setValues(newValues);

            allChildren[indexOfCorrespodingChild] = null;
            allChildren[indexOfCorrespodingChild - 1] = node;

            Integer[] newParentKeys = generateNewKeys(cleanedChildren.length, allChildren);
            root.setKeys(cleanKeys(newParentKeys));
            root.setPayload(allChildren);
        }
    }

    private void mergeWithRightChild(LeafNode node, Node[] cleanedChildren, int indexOfCorrespodingChild, Node[] allChildren) {
        int orignalIndex = indexOfCorrespodingChild;
        boolean isInnerNode = false;
        if (cleanedChildren[indexOfCorrespodingChild + 1] != null) {
            Node[] leafNods = new Node[BPlusTreeUtilities.CAPACITY];
            Node child = cleanedChildren[indexOfCorrespodingChild];
            System.arraycopy(cleanedChildren, 0, leafNods, 0, cleanedChildren.length);

            if (child.getPayload().length - 1 == child.getKeys().length) {
                isInnerNode = true;
                leafNods = cleanChildren((Node[]) child.getPayload());
                for (int i = 0; i < leafNods.length; i++) {
                    Node tempNode = leafNods[i];
                    if (cleanKeys(tempNode.getKeys()).length == 1) {
                        indexOfCorrespodingChild = i;
                        break;
                    }

                }

            }

            Integer[] keysOfSibling = cleanKeys(leafNods[indexOfCorrespodingChild + 1].getKeys());
            String[] valuesOfSibling = cleanValues((String[]) leafNods[indexOfCorrespodingChild + 1].getPayload());

            Integer[] nodeKeys = node.getKeys();
            String[] nodeValues = node.getValues();

            for (int i = 0; i < keysOfSibling.length; i++) {
                nodeKeys[i + 1] = keysOfSibling[i];
                nodeValues[i + 1] = valuesOfSibling[i];
            }

            if (isInnerNode) {
                Node[] newNodes = new Node[BPlusTreeUtilities.CAPACITY + 1];
                for (int i = 0; i < leafNods.length; i++) {
                    newNodes[i] = leafNods[i];
                }
                newNodes[indexOfCorrespodingChild] = node;
                newNodes[indexOfCorrespodingChild + 1] = null;
                Integer[] newInnerKeys = generateNewKeys(cleanChildren(newNodes).length - 1, cleanChildren(newNodes));
                Node newNode = new InnerNode(newInnerKeys, newNodes, BPlusTreeUtilities.CAPACITY);
                allChildren[orignalIndex] = newNode;

            } else {
                allChildren[indexOfCorrespodingChild] = node;
                allChildren[indexOfCorrespodingChild + 1] = null;
            }


            Integer[] newParentKeys = generateNewKeys(cleanChildren(allChildren).length - 1, cleanChildren(allChildren));
            root.setKeys(cleanKeys(newParentKeys));
            root.setPayload(cleanChildren(allChildren));
        }
    }

    private boolean stealingFromRightSibling(LeafNode node, Deque<InnerNode> parents, Node[] cleanedChildren, int indexOfCorrespodingChild, Node[] allChildren) {
        String stealingValue;
        Node[] leafNods = new Node[BPlusTreeUtilities.CAPACITY];
        Integer stealingKey = null;
        Node child = cleanedChildren[indexOfCorrespodingChild];

        System.arraycopy(cleanedChildren, 0, leafNods, 0, cleanedChildren.length);

        if (child.getPayload().length - 1 == child.getKeys().length) {
            leafNods = cleanChildren((Node[]) child.getPayload());
            for (int i = 0; i < leafNods.length; i++) {
                Node tempNode = leafNods[i];
                if (cleanKeys(tempNode.getKeys()).length == 1) {
                    indexOfCorrespodingChild = i;
                    break;
                }

            }

        }

        if (leafNods[indexOfCorrespodingChild + 1] != null) {
            if (cleanedChildren.length - 1 > indexOfCorrespodingChild) {
                LeafNode siblingChild = (LeafNode) leafNods[indexOfCorrespodingChild + 1];
                Integer[] siblingChildKeys = cleanKeys(siblingChild.getKeys());
                if (siblingChildKeys.length > BPlusTreeUtilities.CAPACITY / 2) {
                    stealingKey = siblingChildKeys[0];
                    siblingChildKeys[0] = null;
                    String[] siblingChildValues = cleanValues((String[]) siblingChild.getPayload());
                    stealingValue = siblingChildValues[0];
                    siblingChildValues[0] = null;
                    Integer[] nodeKeys = node.getKeys();
                    String[] nodeValues = node.getValues();

                    for (int i = 0; i < nodeKeys.length; i++) {
                        if (nodeKeys[i] == null) {
                            nodeKeys[i] = stealingKey;
                            nodeValues[i] = stealingValue;
                            break;
                        }
                    }

                    LeafNode newRightChild = new LeafNode(cleanKeys(siblingChildKeys), cleanValues(siblingChildValues), BPlusTreeUtilities.CAPACITY);
                    allChildren[indexOfCorrespodingChild] = node;
                    allChildren[indexOfCorrespodingChild + 1] = newRightChild;
                    Integer[] newParentKeys = generateNewKeys(cleanedChildren.length, allChildren);
                    root.setKeys(newParentKeys);
                    root.setPayload(allChildren);

                }
            }
        }
        return stealingKey != null;
    }


    private boolean stealingFromLeftSibling(LeafNode node, Deque<InnerNode> parents, Node[] cleanedChildren, int indexOfCorrespodingChild, Node[] allChildren) {
        String stealingValue;
        Node[] leafNods = new Node[BPlusTreeUtilities.CAPACITY];
        Integer stealingKey = null;
        Node child = cleanedChildren[indexOfCorrespodingChild];
        System.arraycopy(cleanedChildren, 0, leafNods, 0, cleanedChildren.length);

        if (child.getPayload().length - 1 == child.getKeys().length) {
            leafNods = cleanChildren((Node[]) child.getPayload());
            for (int i = 0; i < leafNods.length; i++) {
                Node tempNode = leafNods[i];
                if (cleanKeys(tempNode.getKeys()).length == 1) {
                    indexOfCorrespodingChild = i;
                    break;
                }

            }

        }

        if (indexOfCorrespodingChild > 0 && leafNods[indexOfCorrespodingChild - 1] != null) {
            Node siblingChild = leafNods[indexOfCorrespodingChild - 1];
            Integer[] siblingChildKeys = cleanKeys(siblingChild.getKeys());
            if (siblingChildKeys.length > BPlusTreeUtilities.CAPACITY / 2) {
                stealingKey = siblingChildKeys[siblingChildKeys.length - 1];
                siblingChildKeys[siblingChildKeys.length - 1] = null;
                String[] siblingChildValues = cleanValues((String[]) siblingChild.getPayload());
                stealingValue = siblingChildValues[siblingChildKeys.length - 1];
                siblingChildValues[siblingChildKeys.length - 1] = null;

                Integer[] nodeKeys = cleanKeys(node.getKeys());
                String[] nodeValues = cleanValues(node.getValues());

                Integer[] newKeys = new Integer[BPlusTreeUtilities.CAPACITY];
                String[] newValues = new String[BPlusTreeUtilities.CAPACITY];

                boolean go = true;
                int i = 0;
                int j = 0;
                while (go) {
                    if (nodeKeys[j] < stealingKey) {
                        newKeys[i] = nodeKeys[j];
                        newValues[i] = nodeValues[j];
                        i++;
                        j++;
                    } else {
                        newKeys[i] = stealingKey;
                        newValues[i] = stealingValue;
                        i++;
                        stealingKey = Integer.MAX_VALUE;
                    }
                    if (cleanKeys(newKeys).length == nodeKeys.length + 1) {
                        go = false;
                    }
                }

                node.setKeys(newKeys);
                node.setValues(newValues);

                LeafNode newLeftChild = new LeafNode(cleanKeys(siblingChildKeys), cleanValues(siblingChildValues), BPlusTreeUtilities.CAPACITY);
                allChildren[indexOfCorrespodingChild] = node;
                allChildren[indexOfCorrespodingChild - 1] = newLeftChild;
                Integer[] newParentKeys = generateNewKeys(cleanedChildren.length, allChildren);
                root.setKeys(newParentKeys);
                root.setPayload(allChildren);
            }
        }
        return stealingKey != null;
    }


    private static Integer[] generateNewKeys(int valueSize, Node[] allChildren) {
        Integer[] newParentKeys = new Integer[BPlusTreeUtilities.CAPACITY];
        for (int i = 1; i <= valueSize; i++) {
            if (allChildren[i] != null) {
                Integer[] tempKeys = allChildren[i].getKeys();
                newParentKeys[i - 1] = tempKeys[0];
            }
        }
        return newParentKeys;
    }

    ///// Public API
    ///// These can be left unchanged

    /**
     * Lookup the value stored under the given key.
     *
     * @return The stored value, or {null} if the key does not exist.
     */
    public String lookup(Integer key) {
        LeafNode leafNode = findLeafNode(key, root);
        return lookupInLeafNode(key, leafNode);
    }

    /**
     * Insert the key/value pair into the B+ tree.
     */
    public void insert(int key, String value) {
        Deque<InnerNode> parents = new LinkedList<>();
        LeafNode leafNode = findLeafNode(key, root, parents);
        insertIntoLeafNode(key, value, leafNode, parents);
    }

    /**
     * Delete the key/value pair from the B+ tree.
     *
     * @return The original value, or {null} if the key does not exist.
     */
    public String delete(Integer key) {
        Deque<InnerNode> parents = new LinkedList<>();
        LeafNode leafNode = findLeafNode(key, root, parents);
        return deleteFromLeafNode(key, leafNode, parents);
    }

    ///// Leave these methods unchanged

    private int capacity = 0;

    private Node root;

    public BPlusTree(int capacity) {
        this(new LeafNode(capacity), capacity);
    }

    public BPlusTree(Node root, int capacity) {
        assert capacity % 2 == 0;
        this.capacity = capacity;
        this.root = root;
    }

    public Node rootNode() {
        return root;
    }

    public String toString() {
        return new BPlusTreePrinter(this).toString();
    }

    private LeafNode findLeafNode(Integer key, Node node) {
        return findLeafNode(key, node, null);
    }

}
