package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import hw1.Field;
import hw1.IntField;
import hw1.RelationalOperator;
import hw3.BPlusTree;
import hw3.Entry;
import hw3.InnerNode;
import hw3.LeafNode;
import hw3.Node;

public class YourHW3Tests {

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testDeleteNonexistentNode() {
		BPlusTree bt = new BPlusTree(3, 2);
		bt.insert(new Entry(new IntField(9), 0));
		
		bt.delete(new Entry(new IntField(7), 0));
		
		//verify that existing node wasn't deleted
		Node root = bt.getRoot();

		assertTrue(root.isLeafNode());

		LeafNode ln = (LeafNode)root;
		ArrayList<Entry> e = ln.getEntries();

		assertTrue(e.get(0).getField().compare(RelationalOperator.EQ, new IntField(9)));
	}

    @Test
    public void testSearchBTree() {
		BPlusTree bt = new BPlusTree(3, 2);
		bt.insert(new Entry(new IntField(530), 0));
		bt.insert(new Entry(new IntField(350), 0));
		bt.insert(new Entry(new IntField(53), 0));
		bt.insert(new Entry(new IntField(35), 0));
		bt.insert(new Entry(new IntField(1000), 0));

		assertTrue(bt.search(new IntField(1)) == null);
		assertTrue(bt.search(new IntField(50)) == null);
		assertTrue(bt.search(new IntField(500)) == null);
		assertTrue(bt.search(new IntField(5000)) == null);
		
		assertTrue(bt.search(new IntField(530)) != null);
		assertTrue(bt.search(new IntField(350)) != null);
		assertTrue(bt.search(new IntField(53)) != null);
		assertTrue(bt.search(new IntField(35)) != null);
		assertTrue(bt.search(new IntField(1000)) != null);
    }
}
