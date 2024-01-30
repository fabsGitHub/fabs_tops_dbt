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


    ///// own tests


    @Test
    public void bigfindKeyInLeaf() {
        // given
        tree = newTree(
                newNode(
                        keys(12),
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
                        keys(12),
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
                        keys(12),
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
    public void bigBigSplitLeafs() {
        // given
        tree = newTree(
                newNode(
                        keys(12),
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
                        keys(12),
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
    public void bigdeleteFromChildMergeWithSibling() {
        // given
        tree = newTree(
                newNode(
                        keys(12),
                        nodes(
                                newNode(
                                        keys(4, 6),
                                        nodes(
                                                newLeaf(keys(1, 2), values("a", "b")),
                                                newLeaf(keys(4, 5), values("d", "e")),
                                                newLeaf(keys(6, 7), values("f", "g"))
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
        String value = tree.delete(5);
        // then
        assertThat(value, is("e"));
        assertThat(tree, isTree(
                        newTree(
                                newNode(
                                        keys(12),
                                        nodes(
                                                newNode(
                                                        keys(4),
                                                        nodes(
                                                                newLeaf(keys(1, 2), values("a", "b")),
                                                                newLeaf(keys(4, 6, 7), values("d", "f", "g"))
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
                        )
                )
        );

    }

}
