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

    private Node getNode(Node[] array, int index) {
        return array[index];
    }

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

    public boolean contains(final Integer[] array, final Integer key) {
        return Arrays.asList(array).contains(key);
    }

    private LeafNode findLeafNode(Integer key, Node node, Deque<InnerNode> parents) {
        if (node instanceof LeafNode) {
            return (LeafNode) node;
        } else {
            boolean hasMoreDepth = true;
            while (hasMoreDepth) {
                node = lookForChildNode(key, node, parents);

                // Wenn true, dann ist node = leafnode und Suche kann beendet werden
                if (node != null && node.getKeys().length == node.getPayload().length) {
                    hasMoreDepth = false;
                }
            }

            return (LeafNode) node;
        }
    }

    private String lookupInLeafNode(Integer key, LeafNode node) {
        if (node != null && contains(node.getKeys(), key)) {
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
        if (node != null) {
            Integer[] keyArray = cleanKeys(node.getKeys());
            String[] valueArray = cleanValues(node.getValues());

            Integer[] newKeyArray = new Integer[BPlusTreeUtilities.CAPACITY + 1];
            String[] newValueArray = new String[BPlusTreeUtilities.CAPACITY + 1];
            int z = 0;
            boolean isKeyAdded = false;
            for (int i = 0; i < keyArray.length; i++) {
                if (keyArray[i] < key) {
                    newKeyArray[z] = keyArray[i];
                    newValueArray[z] = valueArray[i];
                } else {
                    if (!isKeyAdded) {
                        newKeyArray[z] = key;
                        newValueArray[z] = value;
                        isKeyAdded = true;
                        i--;
                    } else {
                        newKeyArray[z] = keyArray[i];
                        newValueArray[z] = valueArray[i];
                    }
                }
                z++;
            }

            if (!isKeyAdded) {
                newKeyArray[z] = key;
                newValueArray[z] = value;
            }


            // Falls zu groß, splite werte in zwei nodes
            int size = cleanKeys(newKeyArray).length;

            if (size > BPlusTreeUtilities.CAPACITY) {
                int halfSize = size / 2;
                Integer[] keys1 = new Integer[halfSize];
                String[] values1 = new String[halfSize];
                Integer[] keys2 = new Integer[halfSize + 1];
                String[] values2 = new String[halfSize + 1];
                Node node1;
                Node node2;

                for (int i = 0; i < size; i++) {
                    if (i < halfSize) {
                        keys1[i] = newKeyArray[i];
                        values1[i] = newValueArray[i];
                    } else {
                        keys2[i - BPlusTreeUtilities.CAPACITY/2] = newKeyArray[i];
                        values2[i - BPlusTreeUtilities.CAPACITY/2] = newValueArray[i];
                    }
                }
                node1 = new LeafNode(keys1, values1, BPlusTreeUtilities.CAPACITY);
                node2 = new LeafNode(keys2, values2, BPlusTreeUtilities.CAPACITY);


                ArrayList<Node> tempLeafNodeArray = new ArrayList<>();

                if (!parents.isEmpty()) {
                    Node[] oldLeafNodeArray = cleanChildren(parents.getLast().getChildren());

                    for (int i = 0; i < oldLeafNodeArray.length; i++) {
                        if (oldLeafNodeArray[i] != node) {
                            tempLeafNodeArray.add(oldLeafNodeArray[i]);
                        } else {
                            tempLeafNodeArray.add(node1);
                            tempLeafNodeArray.add(node2);
                        }
                    }

                } else {
                    tempLeafNodeArray.add(node1);
                    tempLeafNodeArray.add(node2);
                }
                Node[] newLeafNodeArray1 = new Node[BPlusTreeUtilities.CAPACITY + 1];
                Node[] newLeafNodeArray2 = new Node[BPlusTreeUtilities.CAPACITY + 1];

                if (tempLeafNodeArray.size() <= BPlusTreeUtilities.CAPACITY + 1) {
                    for (int i = 0; i < tempLeafNodeArray.size(); i++) {
                        newLeafNodeArray1[i] = tempLeafNodeArray.get(i);
                    }
                } else {
                    int j = 0;
                    int k = 0;
                    for (int i = 0; i < tempLeafNodeArray.size(); i++) {
                        if (i < tempLeafNodeArray.size() / 2) {
                            newLeafNodeArray1[j] = tempLeafNodeArray.get(i);
                            j++;
                        } else {
                            newLeafNodeArray2[k] = tempLeafNodeArray.get(i);
                            k++;
                        }
                    }
                }


                Node newInnerNode2 = null;

                Integer[] newKeys1 = generateNewKeys(newLeafNodeArray1.length - 1, newLeafNodeArray1);
                Node newInnerNode1 = new InnerNode(newKeys1, newLeafNodeArray1, BPlusTreeUtilities.CAPACITY);
                if (cleanChildren(newLeafNodeArray2).length != 0) {
                    Integer[] newKeys2 = generateNewKeys(newLeafNodeArray2.length - 1, newLeafNodeArray2);
                    newInnerNode2 = new InnerNode(newKeys2, newLeafNodeArray2, BPlusTreeUtilities.CAPACITY);
                }


                // Integer[] newKeys = generateNewKeys(newLeafNodeArray1.length - 1, newLeafNodeArray1);

                // NUR FÜR max HEIGHT=3 nicht beliebig groß, wird für Test-Verifizierung von Splits verwendet
                // Node newInnerNode = new InnerNode(newKeys, newLeafNodeArray1, BPlusTreeUtilities.CAPACITY);
                Node[] payLoadLastLast = newLeafNodeArray1;

                // Versuch für beliebige Größen
                boolean isRootAlreadyUpdated = false;
                if (parents.size() > 1) {

                    boolean hasMoreElements = true;
                    InnerNode upperInnerNode = null;
                    while (hasMoreElements) {
                        Node parentsLastNode = parents.pollLast();
                        Node parentsLastLastNode = parents.getLast();

                        // Identifizierung von unmittelbar höherer Node
                        payLoadLastLast = (Node[]) parentsLastLastNode.getPayload();
                        for (int i = 0; i < payLoadLastLast.length; i++) {
                            if (parentsLastNode == payLoadLastLast[i]) {
                                if (newInnerNode1 != null) {
                                    payLoadLastLast[i] = newInnerNode1;
                                    newInnerNode1 = null;
                                } else {
                                    payLoadLastLast[i] = upperInnerNode;
                                }
                                // Wenn es gesplittet wurde
                                if (newInnerNode2 != null) {
                                    i++;
                                    payLoadLastLast[i] = newInnerNode2;
                                    newInnerNode2 = null;
                                }

                            }
                        }

                        if (parents.size() == 1) {
                            hasMoreElements = false;
                        } else {
                            upperInnerNode = new InnerNode(generateNewKeys(payLoadLastLast.length - 1, payLoadLastLast), payLoadLastLast, BPlusTreeUtilities.CAPACITY);
                        }
                    }
                } else {
                    if (tempLeafNodeArray.size() > BPlusTreeUtilities.CAPACITY+1) {
                        Node[] newLeftNodeValues = new Node[BPlusTreeUtilities.CAPACITY + 1];
                        Node[] newRightNodeValues = new Node[BPlusTreeUtilities.CAPACITY + 1];

                        for (int i = 0; i < tempLeafNodeArray.size() / 2; i++) {
                            newLeftNodeValues[i] = tempLeafNodeArray.get(i);
                        }
                        int k = 0;
                        for (int j = tempLeafNodeArray.size() / 2; j < tempLeafNodeArray.size(); j++) {
                            newRightNodeValues[k] = tempLeafNodeArray.get(j);
                            k++;
                        }

                        Node newLeftNode = new InnerNode(generateNewKeys(cleanChildren(newLeftNodeValues).length, newLeftNodeValues), newLeftNodeValues, BPlusTreeUtilities.CAPACITY);
                        Node newRightNode = new InnerNode(generateNewKeys(cleanChildren(newRightNodeValues).length, newRightNodeValues), newRightNodeValues, BPlusTreeUtilities.CAPACITY);


                        Node[] workingNodeArray = new Node[BPlusTreeUtilities.CAPACITY + 1];
                        workingNodeArray[0] = newLeftNode;
                        workingNodeArray[1] = newRightNode;

                        Integer[] rootKeys = generateNewKeys(cleanChildren(workingNodeArray).length - 1, cleanChildren(workingNodeArray));

                        if (BPlusTreeUtilities.CAPACITY == 2){
                            Node[] tempNode = (Node[]) newRightNode.getPayload();

                            rootKeys[0] = tempNode[0].getKeys()[0];
                        }

                        root = new InnerNode(rootKeys, workingNodeArray, BPlusTreeUtilities.CAPACITY);
                        isRootAlreadyUpdated = true;
                    }
                }
                if (!isRootAlreadyUpdated) {
                    if (root instanceof LeafNode) {
                        root = new InnerNode(generateNewKeys(cleanChildren(payLoadLastLast).length, payLoadLastLast), payLoadLastLast, BPlusTreeUtilities.CAPACITY);

                    } else {
                        root.setPayload(payLoadLastLast);
                    }

                    Integer[] rootKeys = root.getKeys();
                    if (payLoadLastLast[cleanKeys(rootKeys).length + 1] != null) {
                        Node tempNode = payLoadLastLast[cleanKeys(rootKeys).length + 1];
                        Integer[] tempNodeKeys = tempNode.getKeys();
                        rootKeys[cleanKeys(rootKeys).length] = tempNodeKeys[0];
                    }
                    root.setKeys(rootKeys);
                }

            }

            // muss drin sein, wenn node ein leafnode ist
            node.setValues(newValueArray);
            node.setKeys(newKeyArray);

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

    private String deleteFromLeafNode(Integer key, LeafNode node, Deque<InnerNode> parents) {
        if (contains(node.getKeys(), key)) {
            Integer[] keyArray = cleanKeys(node.getKeys());
            String[] valueArray = cleanValues(node.getValues());
            List<Integer> keyList = Arrays.asList(keyArray);
            int index = keyList.indexOf(key);
            String removeValue = valueArray[index];
            keyArray[index] = null;
            valueArray[index] = null;
            node.setValues(cleanValues(valueArray));
            node.setKeys(cleanKeys(keyArray));

            if (cleanKeys(keyArray).length < 2 && !parents.isEmpty()) {
                Node[] allChildren = parents.getLast().getChildren();
                Node[] cleanedChildren = cleanChildren(allChildren);

                int indexOfCorrespodingChild = getIndexOfCorrespodingChild(node, cleanedChildren);

                chooseDeletionMethod(key, node, parents, cleanedChildren, indexOfCorrespodingChild, allChildren);

            }
            return removeValue;
        }
        return null;
    }

    private static int getIndexOfCorrespodingChild(LeafNode node, Node[] cleanedChildren) {
        int indexOfCorrespodingChild = 0;
        for (int i = 0; i < cleanedChildren.length; i++) {
            if (node == cleanedChildren[i]) {
                indexOfCorrespodingChild = i;
                break;
            }
        }
        return indexOfCorrespodingChild;
    }

    private void chooseDeletionMethod(Integer key, LeafNode node, Deque<InnerNode> parents, Node[] cleanedChildren,
                                      int indexOfCorrespodingChild, Node[] allChildren) {
        boolean stealingOrMergingSucced;

        stealingOrMergingSucced = stealingFromLeftSibling(key, node, parents, cleanedChildren, indexOfCorrespodingChild, allChildren);

        if (!stealingOrMergingSucced) {
            stealingOrMergingSucced = stealingFromRightSibling(key, node, parents, cleanedChildren, indexOfCorrespodingChild, allChildren);
        }

        if (!stealingOrMergingSucced) {
            if (cleanedChildren.length - 1 > indexOfCorrespodingChild) {
                mergeWithRightChild(key, node, parents, cleanedChildren, indexOfCorrespodingChild, allChildren);
            } else {
                mergeWithLeftChild(key, node, parents, cleanedChildren, indexOfCorrespodingChild, allChildren);
            }
        }
    }

    private void mergeWithLeftChild(Integer key, LeafNode node, Deque<InnerNode> parents, Node[] cleanedChildren,
                                    int indexOfCorrespodingChild, Node[] allChildren) {
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


            Integer[] newParentKeys;
            Integer[] oldParentKeys = parents.getLast().getKeys();
            if (!contains(oldParentKeys, key)) {
                newParentKeys = generateNewKeys(cleanChildren(allChildren).length - 1, cleanChildren(allChildren));
            } else {
                for (int i = 0; i < oldParentKeys.length; i++) {
                    if (!Objects.equals(oldParentKeys[i], key)) {
                        oldParentKeys[i] = null;
                    }
                }
                newParentKeys = oldParentKeys;
            }
            if (cleanKeys(newParentKeys).length == 0) {
                root = node;
            } else {

                Node[] tempWorkingChildren = cleanChildren(allChildren);

                Node[] tempTempWorkingChildren = new Node[BPlusTreeUtilities.CAPACITY + 1];
                for (int i = 0; i < tempWorkingChildren.length; i++) {
                    if (tempWorkingChildren[i] != null) {
                        tempTempWorkingChildren[i] = tempWorkingChildren[i];
                    } else {
                        tempTempWorkingChildren[i] = null;
                    }
                }

                Integer[] newNodeKeys = generateNewKeys(tempTempWorkingChildren.length - 1, tempTempWorkingChildren);
                Node newInnerNode = new InnerNode(newNodeKeys, tempTempWorkingChildren, BPlusTreeUtilities.CAPACITY);

                Node[] payLoadLastLast = allChildren;
                if (parents.size() > 1) {
                    boolean hasMoreHeight = true;
                    InnerNode upperInnerNode = null;
                    while (hasMoreHeight) {
                        Node parentsLastNode = parents.pollLast();
                        Node parentsLastLastNode = parents.getLast();

                        // Identifizierung von unmittelbar höherer Node
                        payLoadLastLast = (Node[]) parentsLastLastNode.getPayload();
                        for (int a = 0; a < payLoadLastLast.length; a++) {
                            if (parentsLastNode == payLoadLastLast[a]) {
                                if (newInnerNode != null) {
                                    payLoadLastLast[a] = newInnerNode;
                                    newInnerNode = null;
                                } else {
                                    payLoadLastLast[a] = upperInnerNode;
                                }
                            }
                        }

                        if (parents.size() == 1) {
                            hasMoreHeight = false;
                        } else {
                            upperInnerNode = new InnerNode(generateNewKeys(payLoadLastLast.length - 1, payLoadLastLast), payLoadLastLast, BPlusTreeUtilities.CAPACITY);
                        }
                    }

                    root.setPayload(payLoadLastLast);
                    Integer[] rootKeys = root.getKeys();
                    if (payLoadLastLast[cleanKeys(rootKeys).length + 1] != null) {
                        Node tempNode = payLoadLastLast[cleanKeys(rootKeys).length + 1];
                        Integer[] tempNodeKeys = tempNode.getKeys();
                        rootKeys[cleanKeys(rootKeys).length] = tempNodeKeys[0];
                    }
                    root.setKeys(rootKeys);
                } else {
                    root.setPayload(cleanChildren(allChildren));
                    root.setKeys(cleanKeys(newParentKeys));
                }

            }


        }
    }

    private void mergeWithRightChild(Integer key, LeafNode node, Deque<InnerNode> parents, Node[] leafNods,
                                     int indexOfCorrespodingChild, Node[] workingChildren) {
        boolean isInnerNode = false;

        if (leafNods[indexOfCorrespodingChild + 1] != null) {
            Integer[] keysOfSibling = cleanKeys(leafNods[indexOfCorrespodingChild + 1].getKeys());
            String[] valuesOfSibling = cleanValues((String[]) leafNods[indexOfCorrespodingChild + 1].getPayload());
            Integer[] nodeKeys = node.getKeys();
            String[] nodeValues = node.getValues();

            // building of new Node
            for (int i = 0; i < keysOfSibling.length; i++) {
                nodeKeys[i + 1] = keysOfSibling[i];
                nodeValues[i + 1] = valuesOfSibling[i];
            }

            workingChildren[indexOfCorrespodingChild] = node;
            workingChildren[indexOfCorrespodingChild + 1] = null;

            Integer[] newParentKeys;
            Integer[] oldParentKeys = parents.getLast().getKeys();
            if (!contains(oldParentKeys, key)) {
                newParentKeys = generateNewKeys(cleanChildren(workingChildren).length - 1, cleanChildren(workingChildren));
            } else {
                for (int i = 0; i < oldParentKeys.length; i++) {
                    if (!Objects.equals(oldParentKeys[i], key)) {
                        oldParentKeys[i] = null;
                    }
                }
                newParentKeys = oldParentKeys;
            }
            if (cleanKeys(newParentKeys).length == 0) {
                root = node;
            } else {

                Node[] tempWorkingChildren = cleanChildren(workingChildren);

                Node[] tempTempWorkingChildren = new Node[BPlusTreeUtilities.CAPACITY + 1];
                for (int i = 0; i < tempWorkingChildren.length; i++) {
                    if (tempWorkingChildren[i] != null) {
                        tempTempWorkingChildren[i] = tempWorkingChildren[i];
                    } else {
                        tempTempWorkingChildren[i] = null;
                    }
                }

                Integer[] newNodeKeys = generateNewKeys(tempTempWorkingChildren.length - 1, tempTempWorkingChildren);
                Node newInnerNode = new InnerNode(newNodeKeys, tempTempWorkingChildren, BPlusTreeUtilities.CAPACITY);

                Node[] payLoadLastLast = workingChildren;
                if (parents.size() > 1) {
                    boolean hasMoreHeight = true;
                    InnerNode upperInnerNode = null;
                    while (hasMoreHeight) {
                        Node parentsLastNode = parents.pollLast();
                        Node parentsLastLastNode = parents.getLast();

                        // Identifizierung von unmittelbar höherer Node
                        payLoadLastLast = (Node[]) parentsLastLastNode.getPayload();
                        for (int a = 0; a < payLoadLastLast.length; a++) {
                            if (parentsLastNode == payLoadLastLast[a]) {
                                if (newInnerNode != null) {
                                    payLoadLastLast[a] = newInnerNode;
                                    newInnerNode = null;
                                } else {
                                    payLoadLastLast[a] = upperInnerNode;
                                }
                            }
                        }

                        if (parents.size() == 1) {
                            hasMoreHeight = false;
                        } else {
                            upperInnerNode = new InnerNode(generateNewKeys(payLoadLastLast.length - 1, payLoadLastLast), payLoadLastLast, BPlusTreeUtilities.CAPACITY);
                        }
                    }

                    root.setPayload(payLoadLastLast);
                    Integer[] rootKeys = root.getKeys();
                    if (payLoadLastLast[cleanKeys(rootKeys).length + 1] != null) {
                        Node tempNode = payLoadLastLast[cleanKeys(rootKeys).length + 1];
                        Integer[] tempNodeKeys = tempNode.getKeys();
                        rootKeys[cleanKeys(rootKeys).length] = tempNodeKeys[0];
                    }
                    root.setKeys(rootKeys);
                } else {
                    root.setPayload(cleanChildren(workingChildren));
                    root.setKeys(cleanKeys(newParentKeys));
                }

            }
        }
    }


    private boolean stealingFromRightSibling(Integer key, LeafNode node, Deque<InnerNode> parents, Node[] leafNods,
                                             int indexOfCorrespodingChild, Node[] workingNodeArray) {
        String stealingValue;
        Integer stealingKey = null;

        if (indexOfCorrespodingChild + 1 < leafNods.length && leafNods[indexOfCorrespodingChild + 1] != null) {
            if (leafNods.length - 1 > indexOfCorrespodingChild) {
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
                    workingNodeArray[indexOfCorrespodingChild] = node;
                    workingNodeArray[indexOfCorrespodingChild + 1] = newRightChild;

                    Integer[] newParentKeys;
                    Integer[] oldParentKeys = parents.getLast().getKeys();
                    if (!contains(oldParentKeys, key)) {
                        newParentKeys = generateNewKeys(cleanChildren(workingNodeArray).length - 1, cleanChildren(workingNodeArray));
                    } else {
                        for (int i = 0; i < oldParentKeys.length; i++) {
                            if (!Objects.equals(oldParentKeys[i], key)) {
                                oldParentKeys[i] = null;
                            }
                        }
                        newParentKeys = oldParentKeys;
                    }
                    if (cleanKeys(newParentKeys).length == 0) {
                        root = node;
                    } else {

                        Node[] tempWorkingChildren = cleanChildren(workingNodeArray);

                        Node[] tempTempWorkingChildren = new Node[BPlusTreeUtilities.CAPACITY + 1];
                        for (int i = 0; i < tempWorkingChildren.length; i++) {
                            if (tempWorkingChildren[i] != null) {
                                tempTempWorkingChildren[i] = tempWorkingChildren[i];
                            } else {
                                tempTempWorkingChildren[i] = null;
                            }
                        }

                        Integer[] newNodeKeys = generateNewKeys(tempTempWorkingChildren.length - 1, tempTempWorkingChildren);
                        Node newInnerNode = new InnerNode(newNodeKeys, tempTempWorkingChildren, BPlusTreeUtilities.CAPACITY);

                        Node[] payLoadLastLast = workingNodeArray;
                        if (parents.size() > 1) {
                            boolean hasMoreHeight = true;
                            InnerNode upperInnerNode = null;
                            while (hasMoreHeight) {
                                Node parentsLastNode = parents.pollLast();
                                Node parentsLastLastNode = parents.getLast();

                                // Identifizierung von unmittelbar höherer Node
                                payLoadLastLast = (Node[]) parentsLastLastNode.getPayload();
                                for (int a = 0; a < payLoadLastLast.length; a++) {
                                    if (parentsLastNode == payLoadLastLast[a]) {
                                        if (newInnerNode != null) {
                                            payLoadLastLast[a] = newInnerNode;
                                            newInnerNode = null;
                                        } else {
                                            payLoadLastLast[a] = upperInnerNode;
                                        }
                                    }
                                }

                                if (parents.size() == 1) {
                                    hasMoreHeight = false;
                                } else {
                                    upperInnerNode = new InnerNode(generateNewKeys(payLoadLastLast.length - 1, payLoadLastLast), payLoadLastLast, BPlusTreeUtilities.CAPACITY);
                                }
                            }

                            root.setPayload(payLoadLastLast);
                            Integer[] rootKeys = root.getKeys();
                            if (payLoadLastLast[cleanKeys(rootKeys).length + 1] != null) {
                                Node tempNode = payLoadLastLast[cleanKeys(rootKeys).length + 1];
                                Integer[] tempNodeKeys = tempNode.getKeys();
                                rootKeys[cleanKeys(rootKeys).length] = tempNodeKeys[0];
                            }
                            root.setKeys(rootKeys);
                        } else {
                            root.setPayload(cleanChildren(workingNodeArray));
                            root.setKeys(cleanKeys(newParentKeys));
                        }

                    }

                }
            }
        }
        return stealingKey != null;
    }


    private boolean stealingFromLeftSibling(Integer key, LeafNode node, Deque<InnerNode> parents, Node[] leafNods,
                                            int indexOfChild, Node[] workingNodeArray) {
        String stealingValue;
        Integer stealingKey = null;

        // Abfrage ob sibling links existiert
        if (indexOfChild > 0 && leafNods[indexOfChild - 1] != null) {
            Node siblingChild = leafNods[indexOfChild - 1];
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
                workingNodeArray[indexOfChild] = node;
                workingNodeArray[indexOfChild - 1] = newLeftChild;


                Integer[] newNodeKeys = generateNewKeys(workingNodeArray.length - 1, workingNodeArray);
                Node newInnerNode = new InnerNode(newNodeKeys, workingNodeArray, BPlusTreeUtilities.CAPACITY);

                Node[] payLoadLastLast = workingNodeArray;
                if (parents.size() > 1) {
                    boolean hasMoreHeight = true;
                    InnerNode upperInnerNode = null;
                    while (hasMoreHeight) {
                        Node parentsLastNode = parents.pollLast();
                        Node parentsLastLastNode = parents.getLast();

                        // Identifizierung von unmittelbar höherer Node
                        payLoadLastLast = (Node[]) parentsLastLastNode.getPayload();
                        for (int a = 0; a < payLoadLastLast.length; a++) {
                            if (parentsLastNode == payLoadLastLast[a]) {
                                if (newInnerNode != null) {
                                    payLoadLastLast[a] = newInnerNode;
                                    newInnerNode = null;
                                } else {
                                    payLoadLastLast[a] = upperInnerNode;
                                }
                            }
                        }

                        if (parents.size() == 1) {
                            hasMoreHeight = false;
                        } else {
                            upperInnerNode = new InnerNode(generateNewKeys(payLoadLastLast.length - 1, payLoadLastLast), payLoadLastLast, BPlusTreeUtilities.CAPACITY);
                        }
                    }
                    root.setPayload(payLoadLastLast);
                } else {
                    root.setKeys(newNodeKeys);
                    root.setPayload(workingNodeArray);
                }

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
