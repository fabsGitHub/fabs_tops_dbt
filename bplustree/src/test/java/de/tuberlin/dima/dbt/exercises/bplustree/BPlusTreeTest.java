package de.tuberlin.dima.dbt.exercises.bplustree;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static de.tuberlin.dima.dbt.grading.bplustree.BPlusTreeMatcher.isTree;
import static de.tuberlin.dima.dbt.exercises.bplustree.BPlusTreeUtilities.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class BPlusTreeTest {

    // fail each test after 1 second
    @Rule
    public Timeout globalTimeout = new Timeout(1000);

    private BPlusTree tree;

    ///// Lookup tests

    @Test
    public void findKeyInLeaf() {
        // given
        tree = newTree(newLeaf(keys(1, 2, 3), values("a", "b", "c")));
        // when
        String value = tree.lookup(2);
        // then
        assertThat(value, is("b"));
    }


    @Test
    public void findNoKeyInLeaf() {
        // given
        tree = newTree(newLeaf(keys(1, 3), values("a", "c")));
        // when
        String value = tree.lookup(2);
        // then
        assertThat(value, is(nullValue()));
    }

    @Test
    public void findNoKeyInLeaf2() {
        // given
        tree = newTree(newLeaf(keys(43, 47), values("uJm", "bVR")));
        // when
        String value = tree.lookup(60);
        // then
        assertThat(value, is(nullValue()));
    }

    @Test
    public void findKeyInChild() {
        // given
        tree = newTree(newNode(keys(3),
                nodes(newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(3, 4), values("c", "d")))));
        // when
        String value = tree.lookup(1);
        // then
        assertThat(value, is("a"));
    }

    @Test
    public void findNoKeyInChild() {
        // given
        tree = newTree(newNode(keys(3),
                nodes(newLeaf(keys(1, 3), values("a", "c")),
                        newLeaf(keys(5, 7), values("e", "g")))));
        // when
        String value = tree.lookup(6);
        // then
        assertThat(value, is(nullValue()));
    }

    @Test
    public void bigfindKeyInChild() {
        // given
        tree = newTree(
                newNode(
                        keys(9),
                        nodes(
                                newNode(
                                        keys(4, 6),
                                        nodes(
                                                newLeaf(keys(1, 2, 3), values("a", "b", "c")),
                                                newLeaf(keys(4, 5), values("d", "e")),
                                                newLeaf(keys(6, 7, 8), values("f", "g", "h"))
                                        )
                                ),
                                newNode(
                                        keys(12, 16),
                                        nodes(
                                                newLeaf(keys(9, 10, 11), values("i", "j", "k")),
                                                newLeaf(keys(12, 13, 14, 15), values("l", "m", "n", "o")),
                                                newLeaf(keys(16, 17), values("p", "q"))
                                        )
                                )
                        )
                )
        );
        // when
        String value = tree.lookup(8);
        // then
        assertThat(value, is("h"));
    }
    ///// Insertion tests

    @Test
    public void insertIntoLeaf() {
        // given
        tree = newTree(newLeaf(keys(1, 3), values("a", "c")));
        // when
        tree.insert(2, "b");
        // then
        assertThat(tree, isTree(
                newTree(newLeaf(keys(1, 2, 3), values("a", "b", "c")))));
    }

    @Test
    public void insertIntoLeaf2() {
        // given
        tree = newTree(newLeaf(keys(84, 107), values("YvI", "nJV")));
        // when
        tree.insert(90, "TwM");
        // then
        assertThat(tree, isTree(
                newTree(newLeaf(keys(84, 90, 107), values("YvI", "TwM", "nJV")))));
    }

    @Test
    public void insertIntoLeaf3() {
        // given
        tree = newTree(newNode(keys(107),
                nodes(newLeaf(keys(77, 82), values("Hyi", "waM")),
                        newLeaf(keys(107, 114), values("rsq", "XVH")))));
        // when
        tree.insert(94, "Ptm");
        // then
        assertThat(tree, isTree(newTree(newNode(
                keys(107),
                nodes(newLeaf(keys(77, 82, 94), values("Hyi", "waM", "Ptm")),
                        newLeaf(keys(107, 114), values("rsq", "XVH")))))));
    }

    @Test
    public void insertIntoLeaf4() {
        // given
        tree = newTree(newNode(keys(114),
                nodes(newLeaf(keys(92, 105), values("gNV", "ALs")),
                        newLeaf(keys(114, 128, 136, 149), values("cXs", "EUL", "uPy", "IzR"))
                )));
        // when
        tree.insert(159, "GJD");
        // then
        assertThat(tree, isTree(newTree(
                newNode(keys(114, 136),
                        nodes(newLeaf(keys(92, 105), values("gNV", "ALs")),
                                newLeaf(keys(114, 128), values("cXs", "EUL")),
                                newLeaf(keys(136, 149, 159), values("uPy", "IzR", "GJD"))
                        )))));
    }

    @Test
    public void insertIntoFullLeaf() {
        // given
        tree = newTree(newLeaf(keys(2, 3, 4, 5), values("b", "c", "d", "e")));
        // when
        tree.insert(1, "a");
        // then
        assertThat(tree, isTree(newTree(newNode(
                keys(3),
                nodes(newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(3, 4, 5), values("c", "d", "e"))
                )))));
    }


    @Test
    public void insertIntoEmptyTree() {
        // given
        tree = newTree(newLeaf(keys(), values()));
        // when
        tree.insert(1, "a");
        tree.insert(2, "b");
        tree.insert(3, "c");
        tree.insert(4, "d");
        tree.insert(5, "e");
        // then
        assertThat(tree, isTree(newTree(
                newNode(
                        keys(3),
                        nodes(
                                newNode(
                                        keys(2),
                                        nodes(
                                                newLeaf(keys(1), values("a")),
                                                newLeaf(keys(2), values("b"))
                                        )
                                ),
                                newNode(
                                        keys(4),
                                        nodes(
                                                newLeaf(keys(3), values("c")),
                                                newLeaf(keys(4, 5), values("d", "e"))
                                        )
                                )
                        )
                )
        )));
    }


    @Test
    public void splitLeafs() {
        // given
        tree = newTree(newNode(keys(3),
                nodes(newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(3, 4, 5, 6), values("c", "d", "e", "f")))));
        // when
        tree.insert(7, "g");
        // then
        assertThat(tree, isTree(newTree(newNode(
                keys(3, 5),
                nodes(newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(3, 4), values("c", "d")),
                        newLeaf(keys(5, 6, 7), values("e", "f", "g")))))));
    }

    @Test
    public void bigSplitLeafs() {
        // given
        tree = newTree(
                newNode(
                        keys(9),
                        nodes(
                                newNode(
                                        keys(4, 6),
                                        nodes(
                                                newLeaf(keys(1, 2, 3), values("a", "b", "c")),
                                                newLeaf(keys(4, 5), values("d", "e")),
                                                newLeaf(keys(6, 7, 8), values("f", "g", "h"))
                                        )
                                ),
                                newNode(
                                        keys(12, 17),
                                        nodes(
                                                newLeaf(keys(9, 10, 11), values("i", "j", "k")),
                                                newLeaf(keys(12, 13, 14, 15), values("l", "m", "n", "o")),
                                                newLeaf(keys(17, 18), values("q", "r"))
                                        )
                                )
                        )
                )
        );
        // when
        tree.insert(16, "p");
        // then
        assertThat(tree, isTree(newTree(
                newNode(
                        keys(9),
                        nodes(
                                newNode(
                                        keys(4, 6),
                                        nodes(
                                                newLeaf(keys(1, 2, 3), values("a", "b", "c")),
                                                newLeaf(keys(4, 5), values("d", "e")),
                                                newLeaf(keys(6, 7, 8), values("f", "g", "h"))
                                        )
                                ),
                                newNode(
                                        keys(12, 14, 17),
                                        nodes(
                                                newLeaf(keys(9, 10, 11), values("i", "j", "k")),
                                                newLeaf(keys(12, 13), values("l", "m")),
                                                newLeaf(keys(14, 15, 16), values("n", "o", "p")),
                                                newLeaf(keys(17, 18), values("q", "r"))
                                        )
                                )
                        )
                )
        )));
    }

    @Test
    public void bigSplitLeafs2() {
        // given
        tree = newTree(newNode(keys(76, 97, 112, 139),
                nodes(
                        newLeaf(keys(55, 69), values("a", "b")),
                        newLeaf(keys(76, 81), values("c", "d")),
                        newLeaf(keys(97, 101), values("e", "f")),
                        newLeaf(keys(112, 126), values("g", "h")),
                        newLeaf(keys(139, 149, 158, 166), values("i", "j", "k", "l"))

                )));
        // when
        tree.insert(173, "m");
        // then
        assertThat(tree, isTree(newTree(newNode(
                keys(139),
                nodes(
                        newNode(
                                keys(76, 97),
                                nodes(
                                        newLeaf(keys(55, 69), values("a", "b")),
                                        newLeaf(keys(76, 81), values("c", "d")),
                                        newLeaf(keys(97, 101), values("e", "f"))
                                )
                        ),
                        newNode(
                                keys(139, 158),
                                nodes(
                                        newLeaf(keys(112, 126), values("g", "h")),
                                        newLeaf(keys(139, 149), values("i", "j")),
                                        newLeaf(keys(158, 166, 173), values("k", "l", "m"))
                                )
                        )
                )
        ))));
    }

    @Test
    public void bigbigSplitLeafs() {
        // given
        tree = newTree(
                newNode(
                        keys(18),
                        nodes(
                                newNode(
                                        keys(4, 8, 12),
                                        nodes(
                                                newLeaf(keys(1, 2, 3), values("a", "b", "c")),
                                                newLeaf(keys(4, 5), values("d", "e")),
                                                newLeaf(keys(8, 9), values("f", "g")),
                                                newLeaf(keys(12, 13), values("h", "i"))
                                        )
                                ),
                                newNode(
                                        keys(20, 25, 32, 37),
                                        nodes(
                                                newLeaf(keys(18, 19), values("j", "k")),
                                                newLeaf(keys(20, 21), values("l", "m")),
                                                newLeaf(keys(25, 26), values("n", "o")),
                                                newLeaf(keys(32, 33, 34, 35), values("p", "q", "r", "s")),
                                                newLeaf(keys(37, 38), values("u", "v"))
                                        )
                                )
                        )
                )
        );
        // when
        tree.insert(36, "t");
        // then
        assertThat(tree, isTree(newTree(
                newNode(
                        keys(18, 34),
                        nodes(
                                newNode(
                                        keys(4, 8, 12),
                                        nodes(
                                                newLeaf(keys(1, 2, 3), values("a", "b", "c")),
                                                newLeaf(keys(4, 5), values("d", "e")),
                                                newLeaf(keys(8, 9), values("f", "g")),
                                                newLeaf(keys(12, 13), values("h", "i"))
                                        )
                                ),
                                newNode(
                                        keys(20, 25),
                                        nodes(
                                                newLeaf(keys(18, 19), values("j", "k")),
                                                newLeaf(keys(20, 21), values("l", "m")),
                                                newLeaf(keys(25, 26), values("n", "o"))
                                        )
                                ),
                                newNode(
                                        keys(34, 37),
                                        nodes(
                                                newLeaf(keys(32, 33), values("p", "q")),
                                                newLeaf(keys(34, 35, 36), values("r", "s", "t")),
                                                newLeaf(keys(37, 38), values("u", "v"))
                                        )
                                )
                        )
                )
        )));
    }

    //Fehler im Baum, keine Auswirkung auf DIESEN Test
    @Test
    public void bigBigbigSplitLeafs() {
        // given
        tree = newTree(
                newNode(
                        keys(21),
                        nodes(
                                newNode(
                                        keys(9, 15),
                                        nodes(
                                                newNode(
                                                        keys(3, 5),
                                                        nodes(
                                                                newLeaf(keys(1, 2), values("a", "b")),
                                                                newLeaf(keys(3, 4), values("c", "d")),
                                                                newLeaf(keys(5, 6), values("e", "f"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(9, 11),
                                                        nodes(
                                                                newLeaf(keys(7, 8), values("g", "h")),
                                                                newLeaf(keys(9, 10), values("i", "j")),
                                                                newLeaf(keys(11, 12), values("k", "l"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(15, 17),
                                                        nodes(
                                                                newLeaf(keys(13, 14), values("m", "n")),
                                                                newLeaf(keys(15, 16), values("o", "p")),
                                                                newLeaf(keys(17, 18), values("q", "r"))
                                                        )
                                                )
                                        )
                                ),
                                newNode(
                                        keys(29, 35),
                                        nodes(
                                                newNode(
                                                        keys(23, 25),
                                                        nodes(
                                                                newLeaf(keys(21, 22), values("s", "t")),
                                                                newLeaf(keys(23, 24), values("u", "v")),
                                                                newLeaf(keys(25, 26), values("w", "x"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(29, 31),
                                                        nodes(
                                                                newLeaf(keys(27, 28), values("y", "z")),
                                                                newLeaf(keys(29, 30), values("aa", "ab")),
                                                                newLeaf(keys(31, 32), values("ac", "ad"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(35, 37, 42, 44),
                                                        nodes(
                                                                newLeaf(keys(33, 34), values("ae", "af")),
                                                                newLeaf(keys(35, 36), values("ag", "ah")),
                                                                newLeaf(keys(37, 38, 39, 40), values("ai", "aj", "ak", "al")),
                                                                newLeaf(keys(42, 43), values("am", "an")),
                                                                newLeaf(keys(44, 45), values("ao", "ap"))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
        // when
        tree.insert(41, "xxx");
        // then
        assertThat(tree, isTree(newTree(
                newNode(
                        keys(21),
                        nodes(
                                newNode(
                                        keys(9, 15),
                                        nodes(
                                                newNode(
                                                        keys(3, 5),
                                                        nodes(
                                                                newLeaf(keys(1, 2), values("a", "b")),
                                                                newLeaf(keys(3, 4), values("c", "d")),
                                                                newLeaf(keys(5, 6), values("e", "f"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(9, 11),
                                                        nodes(
                                                                newLeaf(keys(7, 8), values("g", "h")),
                                                                newLeaf(keys(9, 10), values("i", "j")),
                                                                newLeaf(keys(11, 12), values("k", "l"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(15, 17),
                                                        nodes(
                                                                newLeaf(keys(13, 14), values("m", "n")),
                                                                newLeaf(keys(15, 16), values("o", "p")),
                                                                newLeaf(keys(17, 18), values("q", "r"))
                                                        )
                                                )
                                        )
                                ),
                                newNode(
                                        keys(29, 35, 42),
                                        nodes(
                                                newNode(
                                                        keys(23, 25),
                                                        nodes(
                                                                newLeaf(keys(21, 22), values("s", "t")),
                                                                newLeaf(keys(23, 24), values("u", "v")),
                                                                newLeaf(keys(25, 26), values("w", "x"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(29, 31),
                                                        nodes(
                                                                newLeaf(keys(27, 28), values("y", "z")),
                                                                newLeaf(keys(29, 30), values("aa", "ab")),
                                                                newLeaf(keys(31, 32), values("ac", "ad"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(35, 37),
                                                        nodes(
                                                                newLeaf(keys(33, 34), values("ae", "af")),
                                                                newLeaf(keys(35, 36), values("ag", "ah")),
                                                                newLeaf(keys(37, 38), values("ai", "aj"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(42, 44),
                                                        nodes(
                                                                newLeaf(keys(39, 40, 41), values("ak", "al", "xxx")),
                                                                newLeaf(keys(42, 43), values("am", "an")),
                                                                newLeaf(keys(44, 45), values("ao", "ap"))
                                                        )
                                                )
                                        )
                                )
                        )
                )

        )));
    }

    ///// Deletion tests

    @Test
    public void deleteFromLeaf() {
        // given
        tree = newTree(newLeaf(keys(1, 2, 3), values("a", "b", "c")));
        // when
        String value = tree.delete(2);
        // then
        assertThat(value, is("b"));
        assertThat(tree, isTree(
                newTree(newLeaf(keys(1, 3), values("a", "c")))));
    }

    @Test
    public void deleteFromLeafResultEmtpyTree() {
        // given
        tree = newTree(newLeaf(keys(1), values("a")));
        // when
        String value = tree.delete(1);
        // then
        assertThat(value, is("a"));
        assertThat(tree, isTree(
                newTree(newLeaf(keys(), values()))));
    }

    @Test
    public void deleteInEmptyTree() {
        // given
        tree = newTree(newLeaf(keys(), values()));
        // when
        String value = tree.delete(2);
        // then
        assertThat(value, is(nullValue()));
        assertThat(tree, isTree(
                newTree(newLeaf(keys(), values()))));
    }

    @Test
    public void deleteFromChild() {
        // given
        tree = newTree(newNode(
                keys(4), nodes(newLeaf(keys(1, 2, 3), values("a", "b", "c")),
                        newLeaf(keys(4, 5), values("d", "e")))));
        // when
        String value = tree.delete(1);
        // then
        assertThat(value, is("a"));
        assertThat(tree, isTree(newTree(newNode(
                keys(4), nodes(newLeaf(keys(2, 3), values("b", "c")),
                        newLeaf(keys(4, 5), values("d", "e")))))));
    }

    @Test
    public void deleteFromChildOnlyRoot() {
        // given
        tree = newTree(newNode(
                keys(4), nodes(newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(4, 5), values("d", "e")))));
        // when
        String value = tree.delete(5);
        // then
        assertThat(value, is("e"));
        assertThat(tree, isTree(newTree(newLeaf(
                keys(1, 2, 4), values("a", "b", "d")))));
    }

    @Test
    public void deleteFromChildOnlyRoot2() {
        // given
        tree = newTree(newNode(
                keys(4), nodes(newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(4, 5), values("d", "e")))));
        // when
        String value = tree.delete(2);
        // then
        assertThat(value, is("b"));
        assertThat(tree, isTree(newTree(newLeaf(
                keys(1, 4, 5), values("a", "d", "e")))));
    }

    @Test
    public void deleteFromChildChangeRoot() {
        // given
        tree = newTree(newNode(
                keys(4), nodes(newLeaf(keys(1, 2, 3), values("a", "b", "c")),
                        newLeaf(keys(4, 5), values("d", "e")))));
        // when
        String value = tree.delete(1);
        // then
        assertThat(value, is("a"));
        assertThat(tree, isTree(newTree(newNode(
                keys(4), nodes(newLeaf(keys(2, 3), values("b", "c")),
                        newLeaf(keys(4, 5), values("d", "e")))))));
    }

    @Test
    public void deleteFromChildKeepRoot() {
        // given
        tree = newTree(newNode(
                keys(3), nodes(newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(3, 4, 5), values("c", "d", "e")))));
        // when
        String value = tree.delete(3);
        // then
        assertThat(value, is("c"));
        assertThat(tree, isTree(newTree(newNode(
                keys(3), nodes(newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(4, 5), values("d", "e")))))));
    }

    @Test
    public void deleteFromChildKeepRoot2() {
        // given
        tree = newTree(newNode(
                keys(56, 77), nodes(
                        newLeaf(keys(44, 54), values("a", "b")),
                        newLeaf(keys(56, 70), values("c", "d")),
                        newLeaf(keys(77, 93), values("e", "f"))
                )));
        // when
        String value = tree.delete(56);
        // then
        assertThat(value, is("c"));
        assertThat(tree, isTree(newTree(newNode(
                keys(56), nodes(newLeaf(keys(44, 54), values("a", "b")),
                        newLeaf(keys(70, 77, 93), values("d", "e", "f")))))));
    }

    @Test
    public void deleteFromChildStealFromSibling2() {
        // given
        tree = newTree(newNode(
                keys(4), nodes(newLeaf(keys(1, 2, 3), values("a", "b", "c")),
                        newLeaf(keys(4, 5), values("d", "e")))));
        // when
        String value = tree.delete(5);
        // then
        assertThat(value, is("e"));
        assertThat(tree, isTree(newTree(newNode(
                keys(3), nodes(newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(3, 4), values("c", "d")))))));

    }

    @Test
    public void deleteFromChildStealFromSibling3() {
        // given
        tree = newTree(newNode(
                keys(4, 8), nodes(
                        newLeaf(keys(1, 2, 3), values("a", "b", "c")),
                        newLeaf(keys(4, 5), values("d", "e")),
                        newLeaf(keys(8, 9, 10), values("h", "i", "j")))));
        // when
        String value = tree.delete(5);
        // then
        assertThat(value, is("e"));
        assertThat(tree, isTree(newTree(newNode(
                keys(3, 8), nodes(
                        newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(3, 4), values("c", "d")),
                        newLeaf(keys(8, 9, 10), values("h", "i", "j"))))
        )));

    }

    @Test
    public void deleteFromChildStealFromSibling() {
        // given
        tree = newTree(newNode(
                keys(3), nodes(newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(3, 4, 5), values("c", "d", "e")))));
        // when
        String value = tree.delete(1);
        // then
        assertThat(value, is("a"));
        assertThat(tree, isTree(newTree(newNode(
                keys(4), nodes(newLeaf(keys(2, 3), values("b", "c")),
                        newLeaf(keys(4, 5), values("d", "e")))))));

    }

    @Test
    public void deleteFromChildStealFromLeftSibbling() {
        // given
        tree = newTree(
                newNode(
                        keys(29),
                        nodes(
                                newNode(
                                        keys(9, 15),
                                        nodes(
                                                newNode(
                                                        keys(3, 5),
                                                        nodes(
                                                                newLeaf(keys(1, 2), values("a", "b")),
                                                                newLeaf(keys(3, 4), values("c", "d")),
                                                                newLeaf(keys(5, 6), values("e", "f"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(9, 11),
                                                        nodes(
                                                                newLeaf(keys(7, 8), values("g", "h")),
                                                                newLeaf(keys(9, 10), values("i", "j")),
                                                                newLeaf(keys(11, 12), values("k", "l"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(15, 17),
                                                        nodes(
                                                                newLeaf(keys(13, 14), values("m", "n")),
                                                                newLeaf(keys(15, 16), values("o", "p")),
                                                                newLeaf(keys(17, 18, 19, 20), values("q", "r", "s", "t"))
                                                        )
                                                )
                                        )
                                ),
                                newNode(
                                        keys(37, 50),
                                        nodes(
                                                newNode(
                                                        keys(31, 33),
                                                        nodes(
                                                                newLeaf(keys(29, 30), values("aa", "ab")),
                                                                newLeaf(keys(31, 32), values("ac", "ad")),
                                                                newLeaf(keys(33, 34), values("ae", "af"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(37, 42, 44, 46),
                                                        nodes(
                                                                newLeaf(keys(35, 36), values("ag", "ah")),
                                                                newLeaf(keys(37, 38, 39, 40), values("ai", "aj", "ak", "al")),
                                                                newLeaf(keys(42, 43), values("am", "an")),
                                                                newLeaf(keys(44, 45), values("ao", "ap")),
                                                                newLeaf(keys(46, 47), values("aq", "ar"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(50, 52),
                                                        nodes(
                                                                newLeaf(keys(48, 49), values("as", "at")),
                                                                newLeaf(keys(50, 51), values("au", "av")),
                                                                newLeaf(keys(52, 53), values("aw", "ax"))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
        // when
        tree.delete(42);
        // then
        assertThat(tree, isTree(newTree(
                newNode(
                        keys(29),
                        nodes(
                                newNode(
                                        keys(9, 15),
                                        nodes(
                                                newNode(
                                                        keys(3, 5),
                                                        nodes(
                                                                newLeaf(keys(1, 2), values("a", "b")),
                                                                newLeaf(keys(3, 4), values("c", "d")),
                                                                newLeaf(keys(5, 6), values("e", "f"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(9, 11),
                                                        nodes(
                                                                newLeaf(keys(7, 8), values("g", "h")),
                                                                newLeaf(keys(9, 10), values("i", "j")),
                                                                newLeaf(keys(11, 12), values("k", "l"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(15, 17),
                                                        nodes(
                                                                newLeaf(keys(13, 14), values("m", "n")),
                                                                newLeaf(keys(15, 16), values("o", "p")),
                                                                newLeaf(keys(17, 18, 19, 20), values("q", "r", "s", "t"))
                                                        )
                                                )
                                        )
                                ),
                                newNode(
                                        keys(37, 50),
                                        nodes(
                                                newNode(
                                                        keys(31, 33),
                                                        nodes(
                                                                newLeaf(keys(29, 30), values("aa", "ab")),
                                                                newLeaf(keys(31, 32), values("ac", "ad")),
                                                                newLeaf(keys(33, 34), values("ae", "af"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(37, 40, 44, 46),
                                                        nodes(
                                                                newLeaf(keys(35, 36), values("ag", "ah")),
                                                                newLeaf(keys(37, 38, 39), values("ai", "aj", "ak")),
                                                                newLeaf(keys(40, 43), values("al", "an")),
                                                                newLeaf(keys(44, 45), values("ao", "ap")),
                                                                newLeaf(keys(46, 47), values("aq", "ar"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(50, 52),
                                                        nodes(
                                                                newLeaf(keys(48, 49), values("as", "at")),
                                                                newLeaf(keys(50, 51), values("au", "av")),
                                                                newLeaf(keys(52, 53), values("aw", "ax"))
                                                        )
                                                )
                                        )
                                )
                        )
                )

        )));
    }

    @Test
    public void deleteFromChildStealFromLeftSibblingOnlyChangeLLeaf() {
        // given
        tree = newTree(
                newNode(
                        keys(29),
                        nodes(
                                newNode(
                                        keys(9, 15),
                                        nodes(
                                                newNode(
                                                        keys(3, 5),
                                                        nodes(
                                                                newLeaf(keys(1, 2), values("a", "b")),
                                                                newLeaf(keys(3, 4), values("c", "d")),
                                                                newLeaf(keys(5, 6), values("e", "f"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(9, 11),
                                                        nodes(
                                                                newLeaf(keys(7, 8), values("g", "h")),
                                                                newLeaf(keys(9, 10), values("i", "j")),
                                                                newLeaf(keys(11, 12), values("k", "l"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(15, 17),
                                                        nodes(
                                                                newLeaf(keys(13, 14), values("m", "n")),
                                                                newLeaf(keys(15, 16), values("o", "p")),
                                                                newLeaf(keys(17, 18, 19, 20), values("q", "r", "s", "t"))
                                                        )
                                                )
                                        )
                                ),
                                newNode(
                                        keys(42, 50),
                                        nodes(
                                                newNode(
                                                        keys(31, 33, 35),
                                                        nodes(
                                                                newLeaf(keys(29, 30), values("aa", "ab")),
                                                                newLeaf(keys(31, 32), values("ac", "ad")),
                                                                newLeaf(keys(33, 34), values("ae", "af")),
                                                                newLeaf(keys(35, 36), values("ag", "ah"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(42, 44, 46),
                                                        nodes(
                                                                newLeaf(keys(37, 38, 39, 40), values("ai", "aj", "ak", "al")),
                                                                newLeaf(keys(42, 43), values("am", "an")),
                                                                newLeaf(keys(44, 45), values("ao", "ap")),
                                                                newLeaf(keys(46, 47), values("aq", "ar"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(50, 52),
                                                        nodes(
                                                                newLeaf(keys(48, 49), values("as", "at")),
                                                                newLeaf(keys(50, 51), values("au", "av")),
                                                                newLeaf(keys(52, 53), values("aw", "ax"))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
        // when
        tree.delete(29);
        // then
        assertThat(tree, isTree(newTree(
                newNode(
                        keys(29),
                        nodes(
                                newNode(
                                        keys(9, 15),
                                        nodes(
                                                newNode(
                                                        keys(3, 5),
                                                        nodes(
                                                                newLeaf(keys(1, 2), values("a", "b")),
                                                                newLeaf(keys(3, 4), values("c", "d")),
                                                                newLeaf(keys(5, 6), values("e", "f"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(9, 11),
                                                        nodes(
                                                                newLeaf(keys(7, 8), values("g", "h")),
                                                                newLeaf(keys(9, 10), values("i", "j")),
                                                                newLeaf(keys(11, 12), values("k", "l"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(15, 17),
                                                        nodes(
                                                                newLeaf(keys(13, 14), values("m", "n")),
                                                                newLeaf(keys(15, 16), values("o", "p")),
                                                                newLeaf(keys(17, 18, 19, 20), values("q", "r", "s", "t"))
                                                        )
                                                )
                                        )
                                ),
                                newNode(
                                        keys(42, 50),
                                        nodes(
                                                newNode(
                                                        keys(33, 35),
                                                        nodes(
                                                                newLeaf(keys(30, 31, 32), values("ab", "ac", "ad")),
                                                                newLeaf(keys(33, 34), values("ae", "af")),
                                                                newLeaf(keys(35, 36), values("ag", "ah"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(42, 44, 46),
                                                        nodes(
                                                                newLeaf(keys(37, 38, 39, 40), values("ai", "aj", "ak", "al")),
                                                                newLeaf(keys(42, 43), values("am", "an")),
                                                                newLeaf(keys(44, 45), values("ao", "ap")),
                                                                newLeaf(keys(46, 47), values("aq", "ar"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(50, 52),
                                                        nodes(
                                                                newLeaf(keys(48, 49), values("as", "at")),
                                                                newLeaf(keys(50, 51), values("au", "av")),
                                                                newLeaf(keys(52, 53), values("aw", "ax"))
                                                        )
                                                )
                                        )
                                )
                        )
                )

        )));
    }

    @Test
    public void deleteFromChildStealFromRightSibbling() {
        // given
        tree = newTree(
                newNode(
                        keys(27),
                        nodes(
                                newNode(
                                        keys(9, 21),
                                        nodes(
                                                newNode(
                                                        keys(3, 5),
                                                        nodes(
                                                                newLeaf(keys(1, 2), values("a", "b")),
                                                                newLeaf(keys(3, 4), values("c", "d")),
                                                                newLeaf(keys(5, 6), values("e", "f"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(9, 11, 17, 19),
                                                        nodes(
                                                                newLeaf(keys(7, 8), values("g", "h")),
                                                                newLeaf(keys(9, 10), values("i", "j")),
                                                                newLeaf(keys(11, 12, 13, 14), values("k", "l", "m", "n")),
                                                                newLeaf(keys(17, 18), values("q", "r")),
                                                                newLeaf(keys(19, 20), values("s", "t"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(21, 23),
                                                        nodes(
                                                                newLeaf(keys(21, 22), values("u", "v")),
                                                                newLeaf(keys(23, 24), values("w", "x")),
                                                                newLeaf(keys(25, 26), values("y", "z"))
                                                        )
                                                )
                                        )
                                ),
                                newNode(
                                        keys(37, 45),
                                        nodes(
                                                newNode(
                                                        keys(31, 33, 35),
                                                        nodes(
                                                                newLeaf(keys(27, 28, 29, 30), values("aa", "ab", "ba", "bb")),
                                                                newLeaf(keys(31, 32), values("ac", "ad")),
                                                                newLeaf(keys(33, 34), values("ae", "af")),
                                                                newLeaf(keys(35, 36), values("ag", "ah"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(39, 41),
                                                        nodes(
                                                                newLeaf(keys(37, 38), values("ai", "aj")),
                                                                newLeaf(keys(39, 40), values("ak", "al")),
                                                                newLeaf(keys(41, 42), values("am", "an"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(45, 47),
                                                        nodes(
                                                                newLeaf(keys(43, 44), values("ao", "ap")),
                                                                newLeaf(keys(45, 46), values("aq", "ar")),
                                                                newLeaf(keys(47, 48), values("as", "at"))

                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
        // when
        tree.delete(10);
        // then
        assertThat(tree, isTree(newTree(
                newNode(
                        keys(27),
                        nodes(
                                newNode(
                                        keys(9, 21),
                                        nodes(
                                                newNode(
                                                        keys(3, 5),
                                                        nodes(
                                                                newLeaf(keys(1, 2), values("a", "b")),
                                                                newLeaf(keys(3, 4), values("c", "d")),
                                                                newLeaf(keys(5, 6), values("e", "f"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(9, 12, 17, 19),
                                                        nodes(
                                                                newLeaf(keys(7, 8), values("g", "h")),
                                                                newLeaf(keys(9, 11), values("i", "k")),
                                                                newLeaf(keys(12, 13, 14), values("l", "m", "n")),
                                                                newLeaf(keys(17, 18), values("q", "r")),
                                                                newLeaf(keys(19, 20), values("s", "t"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(21, 23),
                                                        nodes(
                                                                newLeaf(keys(21, 22), values("u", "v")),
                                                                newLeaf(keys(23, 24), values("w", "x")),
                                                                newLeaf(keys(25, 26), values("y", "z"))
                                                        )
                                                )
                                        )
                                ),
                                newNode(
                                        keys(37, 45),
                                        nodes(
                                                newNode(
                                                        keys(31, 33, 35),
                                                        nodes(
                                                                newLeaf(keys(27, 28, 29, 30), values("aa", "ab", "ba", "bb")),
                                                                newLeaf(keys(31, 32), values("ac", "ad")),
                                                                newLeaf(keys(33, 34), values("ae", "af")),
                                                                newLeaf(keys(35, 36), values("ag", "ah"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(39, 41),
                                                        nodes(
                                                                newLeaf(keys(37, 38), values("ai", "aj")),
                                                                newLeaf(keys(39, 40), values("ak", "al")),
                                                                newLeaf(keys(41, 42), values("am", "an"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(45, 47),
                                                        nodes(
                                                                newLeaf(keys(43, 44), values("ao", "ap")),
                                                                newLeaf(keys(45, 46), values("aq", "ar")),
                                                                newLeaf(keys(47, 48), values("as", "at"))

                                                        )
                                                )
                                        )
                                )
                        )
                )

        )));
    }

    /*
    @Test
    public void deleteFromChildMergeWithRightSibblingAndStealFromRightUpperSibbling() {
        // given
        tree = newTree(
                newNode(
                        keys(27),
                        nodes(
                                newNode(
                                        keys(9, 21),
                                        nodes(
                                                newNode(
                                                        keys(3, 5),
                                                        nodes(
                                                                newLeaf(keys(1, 2), values("a", "b")),
                                                                newLeaf(keys(3, 4), values("c", "d")),
                                                                newLeaf(keys(5, 6), values("e", "f"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(9, 11, 17, 19),
                                                        nodes(
                                                                newLeaf(keys(7, 8), values("g", "h")),
                                                                newLeaf(keys(9, 10), values("i", "j")),
                                                                newLeaf(keys(11, 12, 13, 14), values("k", "l", "m", "n")),
                                                                newLeaf(keys(17, 18), values("q", "r")),
                                                                newLeaf(keys(19, 20), values("s", "t"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(21, 23),
                                                        nodes(
                                                                newLeaf(keys(21, 22), values("u", "v")),
                                                                newLeaf(keys(23, 24), values("w", "x")),
                                                                newLeaf(keys(25, 26), values("y", "z"))
                                                        )
                                                )
                                        )
                                ),
                                newNode(
                                        keys(37, 45),
                                        nodes(
                                                newNode(
                                                        keys(31, 33, 35),
                                                        nodes(
                                                                newLeaf(keys(27, 28, 29, 30), values("aa", "ab", "ba", "bb")),
                                                                newLeaf(keys(31, 32), values("ac", "ad")),
                                                                newLeaf(keys(33, 34), values("ae", "af")),
                                                                newLeaf(keys(35, 36), values("ag", "ah"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(39, 41),
                                                        nodes(
                                                                newLeaf(keys(37, 38), values("ai", "aj")),
                                                                newLeaf(keys(39, 40), values("ak", "al")),
                                                                newLeaf(keys(41, 42), values("am", "an"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(45, 47),
                                                        nodes(
                                                                newLeaf(keys(43, 44), values("ao", "ap")),
                                                                newLeaf(keys(45, 46), values("aq", "ar")),
                                                                newLeaf(keys(47, 48), values("as", "at"))

                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
        // when
        tree.delete(1);
        // then
        assertThat(tree, isTree(newTree(
                newNode(
                        keys(27),
                        nodes(
                                newNode(
                                        keys(9, 21),
                                        nodes(
                                                newNode(
                                                        keys(5, 7),
                                                        nodes(
                                                                newLeaf(keys(2, 3, 4), values("b", "c", "d")),
                                                                newLeaf(keys(5, 6), values("e", "f")),
                                                                newLeaf(keys(7, 8), values("g", "h"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(11, 17, 19),
                                                        nodes(
                                                                newLeaf(keys(9, 10), values("i", "j")),
                                                                newLeaf(keys(11, 12, 13, 14), values("k", "l", "m", "n")),
                                                                newLeaf(keys(17, 18), values("q", "r")),
                                                                newLeaf(keys(19, 20), values("s", "t"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(21, 23),
                                                        nodes(
                                                                newLeaf(keys(21, 22), values("u", "v")),
                                                                newLeaf(keys(23, 24), values("w", "x")),
                                                                newLeaf(keys(25, 26), values("y", "z"))
                                                        )
                                                )
                                        )
                                ),
                                newNode(
                                        keys(37, 45),
                                        nodes(
                                                newNode(
                                                        keys(31, 33, 35),
                                                        nodes(
                                                                newLeaf(keys(27, 28, 29, 30), values("aa", "ab", "ba", "bb")),
                                                                newLeaf(keys(31, 32), values("ac", "ad")),
                                                                newLeaf(keys(33, 34), values("ae", "af")),
                                                                newLeaf(keys(35, 36), values("ag", "ah"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(39, 41),
                                                        nodes(
                                                                newLeaf(keys(37, 38), values("ai", "aj")),
                                                                newLeaf(keys(39, 40), values("ak", "al")),
                                                                newLeaf(keys(41, 42), values("am", "an"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(45, 47),
                                                        nodes(
                                                                newLeaf(keys(43, 44), values("ao", "ap")),
                                                                newLeaf(keys(45, 46), values("aq", "ar")),
                                                                newLeaf(keys(47, 48), values("as", "at"))

                                                        )
                                                )
                                        )
                                )
                        )
                )

        )));
    }


     */
    @Test
    public void fabsdeleteFromChildStealFromSibling() {
        // given
        tree = newTree(newNode(
                keys(3), nodes(newLeaf(keys(1, 2, 3), values("a", "b", "c")),
                        newLeaf(keys(4, 5), values("d", "e")))));
        // when
        String value = tree.delete(5);
        // then
        assertThat(value, is("e"));
        assertThat(tree, isTree(newTree(newNode(
                keys(3), nodes(newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(3, 4), values("c", "d")))))));

    }

    @Test
    public void deleteFromChildMergeWithSibling() {
        // given
        tree = newTree(newNode(keys(3, 5),
                nodes(newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(3, 4), values("c", "d")),
                        newLeaf(keys(5, 6), values("e", "f")))));
        // when
        String value = tree.delete(2);
        // then
        assertThat(value, is("b"));
        assertThat(tree, isTree(newTree(newNode(
                keys(5), nodes(newLeaf(keys(1, 3, 4), values("a", "c", "d")),
                        newLeaf(keys(5, 6), values("e", "f")))))));
    }

    @Test
    public void deleteFromChildMergeWithSibblingOnlyRoot() {
        // given
        tree = newTree(newNode(
                keys(4), nodes(newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(4, 5), values("d", "e")))));
        // when
        String value = tree.delete(2);
        // then
        assertThat(value, is("b"));
        assertThat(tree, isTree(newTree(newLeaf(
                keys(1, 4, 5), values("a", "d", "e")))));
    }

    @Test
    public void fabsdeleteFromChildMergeWithSibling() {
        // given
        tree = newTree(newNode(keys(3, 5),
                nodes(newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(3, 4), values("c", "d")),
                        newLeaf(keys(5, 6), values("e", "f")))));
        // when
        String value = tree.delete(6);
        // then
        assertThat(value, is("f"));
        assertThat(tree, isTree(newTree(newNode(
                keys(3), nodes(newLeaf(keys(1, 2), values("a", "b")),
                        newLeaf(keys(3, 4, 5), values("c", "d", "e")))))));
    }

    @Test
    public void bigdeleteFromChildMergeWithRightSibling() {
        // given
        tree = newTree(
                newNode(
                        keys(12),
                        nodes(
                                newNode(
                                        keys(4, 6, 8),
                                        nodes(
                                                newLeaf(keys(1, 2), values("a", "b")),
                                                newLeaf(keys(4, 5), values("d", "e")),
                                                newLeaf(keys(6, 7), values("f", "g")),
                                                newLeaf(keys(8, 9), values("h", "i"))
                                        )
                                ),
                                newNode(
                                        keys(12, 16),
                                        nodes(
                                                newLeaf(keys(10, 11), values("j", "k")),
                                                newLeaf(keys(12, 13, 14, 15), values("l", "m", "n", "o")),
                                                newLeaf(keys(16, 17), values("p", "q"))
                                        )
                                )
                        )
                )
        );
        // when
        String value = tree.delete(5);
        // then
        assertThat(value, is("e"));
        assertThat(tree, isTree(
                        newTree(
                                newNode(
                                        keys(12),
                                        nodes(
                                                newNode(
                                                        keys(4, 8),
                                                        nodes(
                                                                newLeaf(keys(1, 2), values("a", "b")),
                                                                newLeaf(keys(4, 6, 7), values("d", "f", "g")),
                                                                newLeaf(keys(8, 9), values("h", "i"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(12, 16),
                                                        nodes(
                                                                newLeaf(keys(10, 11), values("j", "k")),
                                                                newLeaf(keys(12, 13, 14, 15), values("l", "m", "n", "o")),
                                                                newLeaf(keys(16, 17), values("p", "q"))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

    }

    @Test
    public void bigdeleteNonExisitngValueFromChildMergeWithRightSibling() {
        // given
        tree = newTree(
                newNode(
                        keys(12),
                        nodes(
                                newNode(
                                        keys(4, 6, 8),
                                        nodes(
                                                newLeaf(keys(1, 2), values("a", "b")),
                                                newLeaf(keys(4, 5), values("d", "e")),
                                                newLeaf(keys(6, 7), values("f", "g")),
                                                newLeaf(keys(8, 9), values("h", "i"))
                                        )
                                ),
                                newNode(
                                        keys(12, 16),
                                        nodes(
                                                newLeaf(keys(10, 11), values("j", "k")),
                                                newLeaf(keys(12, 13, 14, 15), values("l", "m", "n", "o")),
                                                newLeaf(keys(16, 17), values("p", "q"))
                                        )
                                )
                        )
                )
        );
        // when
        String value = tree.delete(18);
        // then
        assertThat(value, is(nullValue()));
        assertThat(tree, isTree(
                        newTree(
                                newNode(
                                        keys(12),
                                        nodes(
                                                newNode(
                                                        keys(4, 6, 8),
                                                        nodes(
                                                                newLeaf(keys(1, 2), values("a", "b")),
                                                                newLeaf(keys(4, 5), values("d", "e")),
                                                                newLeaf(keys(6, 7), values("f", "g")),
                                                                newLeaf(keys(8, 9), values("h", "i"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(12, 16),
                                                        nodes(
                                                                newLeaf(keys(10, 11), values("j", "k")),
                                                                newLeaf(keys(12, 13, 14, 15), values("l", "m", "n", "o")),
                                                                newLeaf(keys(16, 17), values("p", "q"))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

    }

    @Test
    public void bigdeleteFromChildMergeWithLeftSibling() {
        // given
        tree = newTree(
                newNode(
                        keys(12),
                        nodes(
                                newNode(
                                        keys(4, 6, 8),
                                        nodes(
                                                newLeaf(keys(1, 2), values("a", "b")),
                                                newLeaf(keys(4, 5), values("d", "e")),
                                                newLeaf(keys(6, 7), values("f", "g")),
                                                newLeaf(keys(8, 9), values("h", "i"))
                                        )
                                ),
                                newNode(
                                        keys(12, 16),
                                        nodes(
                                                newLeaf(keys(10, 11), values("j", "k")),
                                                newLeaf(keys(12, 13, 14, 15), values("l", "m", "n", "o")),
                                                newLeaf(keys(16, 17), values("p", "q"))
                                        )
                                )
                        )
                )
        );
        // when
        String value = tree.delete(9);
        // then
        assertThat(value, is("i"));
        assertThat(tree, isTree(
                        newTree(
                                newNode(
                                        keys(12),
                                        nodes(
                                                newNode(
                                                        keys(4, 6),
                                                        nodes(
                                                                newLeaf(keys(1, 2), values("a", "b")),
                                                                newLeaf(keys(4, 5), values("d", "e")),
                                                                newLeaf(keys(6, 7, 8), values("f", "g", "h"))
                                                        )
                                                ),
                                                newNode(
                                                        keys(12, 16),
                                                        nodes(
                                                                newLeaf(keys(10, 11), values("j", "k")),
                                                                newLeaf(keys(12, 13, 14, 15), values("l", "m", "n", "o")),
                                                                newLeaf(keys(16, 17), values("p", "q"))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

    }

}
