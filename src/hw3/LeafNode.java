//Howard Lan, Amy Kim

package hw3;

import java.util.ArrayList;

import hw1.Field;

public class LeafNode implements Node {
	int degree;
	ArrayList<Entry> entries = new ArrayList<Entry>();
	InnerNode parent;
	
	public LeafNode(int degree) {
		//your code here
		this.degree = degree;
	}
	
	public ArrayList<Entry> getEntries() {
		//your code here
		return this.entries;
	}
	
	public int EntriesSize() {
		//your code here
		return this.entries.size();
	}

	public int getDegree() {
		//your code here
		return this.degree;
	}
	
	public void setParent(InnerNode p) {
		parent = p;
	}
	
	public void setEntries(ArrayList<Entry> newEntries) {
		this.entries = newEntries;
	}
		
	public boolean isLeafNode() {
		return true;
	}
	
	public String toString() {
		String s = "";
		for(int i = 0; i < entries.size(); i++) {
			s = s + " " + entries.get(i).toString();
		}
		return s;
	}

}