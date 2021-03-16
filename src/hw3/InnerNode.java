//Howard Lan, Amy Kim

package hw3;

import java.util.ArrayList;

import hw1.Field;

public class InnerNode implements Node {
	int degree;
	ArrayList<Field> keys = new ArrayList<Field>();
	ArrayList<Node> children = new ArrayList<Node>();
	InnerNode parent;	// Amy's field
	
	public InnerNode(int degree) {
		//your code here
		this.degree = degree;
	}
	 
	public ArrayList<Field> getKeys() {
		//your code here
		return this.keys;
	}
	
	public int KeysSize() {
		//your code here
		return this.keys.size();
	}
	
	public ArrayList<Node> getChildren() {
		//your code here
		return this.children;
	}
	
	public void setParent(InnerNode p) {
		parent = p;
	}
	
	public void setKeys(ArrayList<Field> newKeys) {
		this.keys = newKeys;
	}

	public void setChildren(ArrayList<Node> newChildren) {
		this.children = newChildren;
	}

	public int getDegree() {
		//your code here
		return this.degree;
	}
	
	public boolean hasChildren() {
		if(children.size() == 0)
			return false;
 		else
 			return true;
	}
	
	public boolean isLeafNode() {
		return false;
	}
	
	public String toString() {
		String s = "";
		for(int i = 0; i < keys.size(); i++) {
			s = s + " " + keys.get(i).toString();
		}
		return s;
	}
	
	public InnerNode getParent()
	{
		return this.parent;
	}
	
	public void setLRchildren(InnerNode l, InnerNode r)
	{
		this.children.add(l);
		this.children.add(r);
	}

}