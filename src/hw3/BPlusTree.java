//Howard Lan, Amy Kim

package hw3;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hw1.Field;
import hw1.RelationalOperator;

public class BPlusTree {
	int pInner;	// BPlusTree(3, 2): inner node must have 2~3 children
	int pLeaf;	// BPlusTree(3, 2): leaf node must have 1~2 entries
	private Node root;
    
    public BPlusTree(int pInner, int pLeaf) {
    	//your code here
    	this.pInner = pInner;
    	this.pLeaf = pLeaf;
    }
    
    public LeafNode search(Field f) {
    	Node result = searchHelper(f, root);
    	if(result.isLeafNode()) 
    	{
    		 for(Entry e: ((LeafNode)result).getEntries()) {
    			  if(e.getField().compare(RelationalOperator.EQ, f)) 
    			 {
    				  return (LeafNode)result;
    			}
    		}
    		
    	}
    	return null;
    }
    
    // k is field we are searching for
    // n is node we will traverse to
    public LeafNode searchHelper(Field k, Node n) {
    	if(n.isLeafNode()) {
    		return (LeafNode)n;
    	} else {
    		InnerNode in = (InnerNode)n;
    		ArrayList<Node> children = in.getChildren();
    		boolean searchPerformed = false;
    		for(int i = 0; i < in.getKeys().size(); i++) {
    			if(k.compare(RelationalOperator.LTE, in.getKeys().get(i))) {
    				searchPerformed = true;
    				return searchHelper(k, children.get(i));
    			}
    		}
    		if(searchPerformed == false) {
    			return searchHelper(k, children.get(children.size()-1));
    		}
    	}
    	return null;
    }
    
   
    public void insert(Entry e)
    {
    	if(root == null) 
    	{
    		ArrayList<Entry> rootEntries = new ArrayList<Entry>();
    		rootEntries.add(e);
    		root = new LeafNode(pLeaf);
    		((LeafNode)root).setEntries(rootEntries);
    	}
    	else 	// root is not null
    	{
    		LeafNode s = searchHelper(e.getField(), root);
    		ArrayList<Entry> newEntries = ((LeafNode)s).getEntries();
    		newEntries = addSorted(newEntries, e);
    		((LeafNode)s).setEntries(newEntries);
    			
    		if(isOverloaded(s)) 
    		{
    			// LEAF SPLIT
    	    	InnerNode pnode  = (s.parent == null) ?  new InnerNode(pInner) : s.parent;
    	    
    	    	LeafNode left = new LeafNode(pLeaf);
    	    	left.setParent(pnode);
    	    	LeafNode right = new LeafNode(pLeaf);
    	    	right.setParent(pnode);
    	    	
    	    	int mid = (s.EntriesSize() - 1 ) / 2 + 1 ;
    	    	right.setEntries(new ArrayList<Entry> (s.getEntries().subList(mid, s.EntriesSize())));
    	    	left.setEntries(new ArrayList<Entry> (s.getEntries().subList(0, mid) ));
    	    	
    	    	if(pnode.getChildren().size() > 0) 
    	    	{	    		
    	    		int idx = findNodePosAndSet(pnode.getChildren(), s, left, right);
    	    		ArrayList<Field> newRootsKey =  pnode.getKeys();
    	    		Entry newEntry = left.getEntries().get(left.EntriesSize()-1);
    	    		newRootsKey.add(idx, newEntry.getField());
    	            pnode.setKeys(newRootsKey);
    	            
    	        	if(isOverloaded(pnode))
    	        	{
    	        		// PUSH UP
    	        		while(pnode.getChildren().size() > pInner)
    	        		{
    	        	    	InnerNode p = pnode;
    	        	        pnode = pnode.getParent();
    	        	         
    	        	         pnode  = (pnode == null) ?  new InnerNode(pInner) : pnode;

    	        	    	 InnerNode leftinner = new InnerNode(pInner);
    	        	    	 leftinner.setParent(pnode);
    	        	    	 InnerNode rightinner = new InnerNode(pInner);
    	        	    	 rightinner.setParent(pnode);
    	        	    	
    	        	     	 leftinner.setChildren(new ArrayList<Node> (p.getChildren().subList(0, (p.KeysSize()-1)/2 + 1)));
    	        	     	 rightinner.setChildren(new ArrayList<Node> (p.getChildren().subList((p.KeysSize() - 1 ) / 2 + 1, p.getChildren().size())));

       	        	    	for(int i = 0; i < p.getChildren().size(); i++)
    	        	    	{
    	        	    		if(i < (p.KeysSize()-1)/2 + 1){
    	        	    			leftinner.getChildren().get(i).setParent(leftinner);
    	        	    		}
    	        	    		else {
    	        	    			rightinner.getChildren().get(i -((p.KeysSize()-1)/2+1)).setParent(rightinner);
    	        	    		}
    	        	    	}
       	        	    	
    	        	    	leftinner.setKeys(new ArrayList<Field> (p.getKeys().subList(0, (p.KeysSize()-1)/2)));
    	        	    	rightinner.setKeys(new ArrayList<Field> (p.getKeys().subList((p.KeysSize()-1)/2+1, p.KeysSize()))); 	        	    	

    	        	    	this.root = pnode;
    	        	    	pnode.getKeys().add(p.getKeys().get((p.KeysSize()-1)/2));
    	        	    	pnode.setLRchildren(leftinner, rightinner);
  	
    	        		}	
    	        	}
    	        	
    	    	}
    	    	else 
    	    	{
    	            root = pnode;
    	    		ArrayList<Node> newChildren = ((InnerNode) root).getChildren();
    	    		newChildren.add(left);
    	            newChildren.add(right);
    	            ((InnerNode) root).setChildren(newChildren);
    	            
    	    		ArrayList<Field> newRootsKey =  ((InnerNode) root).getKeys();
    	    		newRootsKey.add(left.getEntries().get(left.EntriesSize()-1).getField());
    	            ((InnerNode) root).setKeys(newRootsKey);
    	    	}

    		}
    	}
    }
    
    
    public boolean checkContains(ArrayList<Entry> arr, Entry e)
    {
    	boolean b = false;
    	for(int i = 0; i < arr.size(); i++)
    	{
    		if(e.getField().compare(RelationalOperator.EQ, arr.get(i).getField())) {
    			b = true;
    		}
    	}
    	return b;
    }
    public void delete(Entry e) {
    	//your code here
    	if(root == null) {
    		return;
    	}
    	LeafNode s = searchHelper(e.getField(), root);

    	if(checkContains(s.getEntries(), e))
    	{
        	if(s.equals(root) && s.getEntries().size() == 1) {
        		root = null;
        		return;
        	}
        	
        	RemoveEntryFromNode(s, e);
    		if(s.getEntries().size() < Math.ceil(pLeaf/2.0))	// s underloaded
    		{
    			boolean merged = false;
    			int sIndex = s.parent.getChildren().indexOf(s);
    			if(sIndex > 0)	// if s has left sibling
    			{	
    				LeafNode leftSibling = (LeafNode)(s.parent.getChildren().get(sIndex-1));
    				if(leftSibling.EntriesSize() > Math.ceil(pLeaf/2.0))	// borrow from sibling
    				{
    					addSorted(s.getEntries(), leftSibling.getEntries().get(leftSibling.EntriesSize()-1));	// s borrows from leftSibling
    					leftSibling.getEntries().remove(leftSibling.EntriesSize()-1);	// remove biggest entry in leftSibling
    					Entry leftNewGreatest = leftSibling.getEntries().get(leftSibling.EntriesSize()-1);
    					
    					// update parent's keys and children
    					InnerNode p = s.parent;
    					int leftSiblingIndex = p.getChildren().indexOf(leftSibling);
    					p.getKeys().set(leftSiblingIndex, leftNewGreatest.getField());
    					p.getChildren().set(leftSiblingIndex, leftSibling);
    					
    					// iterate through s.parent's children and setParent to the modified parent
   	        	    	for(int i = 0; i < p.getChildren().size(); i++)
	        	    	{
   	        	    		p.getChildren().get(i).setParent(p);
	        	    	}
    				
    				
    				} else {	// merge with left sibling
    					//!!
    					ArrayList<Entry> leftSibEntries = leftSibling.getEntries();
    					for(Entry ent: s.getEntries())
    					{
    						addSorted(leftSibEntries, ent);
    					}
    					
    					// delete key in parent node
    					if(s.parent.getKeys().size() > 1) {
    						s.parent.getKeys().remove(s.parent.getChildren().indexOf(s));
    					}
    					
    					// delete child s from parent
    					s.parent.getChildren().remove(s);
    					
    					merged = true;
    				}
    				
    			} else if(sIndex < s.parent.getChildren().size()-1) {	// s has a right sibling
    				LeafNode rightSibling = (LeafNode)(s.parent.getChildren().get(sIndex+1));
    				if(rightSibling.EntriesSize() > Math.ceil(pLeaf/2.0)) // borrow from sibling
    				{	
    					addSorted(s.getEntries(), rightSibling.getEntries().get(0));	// s borrows from rightSibling
    					rightSibling.getEntries().remove(0);
    					Entry sNewGreatest = s.getEntries().get(s.getEntries().size()-1);
    					
    					// update parent's keys and children
    					InnerNode p = s.parent;
    					int rightSiblingIndex = p.getChildren().indexOf(rightSibling);
    					p.getKeys().set(p.getChildren().indexOf(s), sNewGreatest.getField());
    					p.getChildren().set(rightSiblingIndex, rightSibling);
    					
    					// iterate through s.parent's children and setParent to the modified parent
   	        	    	for(int i = 0; i < p.getChildren().size(); i++)
	        	    	{
   	        	    		p.getChildren().get(i).setParent(p);
	        	    	}
    				}
    				else {		// merge with right sibling
    					ArrayList<Entry> rightSibEntries = rightSibling.getEntries();
    					for(Entry ent: s.getEntries())
    					{
    						addSorted(rightSibEntries, ent);
    					}
    					
    					// delete key in parent node
    					if(s.parent.getKeys().size() > 1) {
    						s.parent.getKeys().remove(s.parent.getChildren().indexOf(s));
    					}
    					
    					// delete child s from parent
    					s.parent.getChildren().remove(s);
    					
    					merged = true;
    				}
    			}
    			if(merged && s.parent.getChildren().size() < Math.ceil(pInner/2.0)) {
    				if(s.parent.equals(root)) {
    					// COLLAPSE ROOT LEVEL
    					root = s.parent.getChildren().get(s.parent.getChildren().size()-1);
    					return;
    				}
    				// left uncle
    				InnerNode uncle = (InnerNode)s.parent.parent.getChildren().get(s.parent.parent.getChildren().indexOf(s.parent)-1);
    				if(uncle.getChildren().size() > Math.ceil(pInner/2.0)) {// if uncle has a child to give
    					// PUSH THROUGH
    					// save values we need later
    					LeafNode cousin = (LeafNode)uncle.getChildren().get(uncle.getChildren().size()-1); // 6,7
    					Field keyToMoveUp = uncle.getKeys().get(uncle.getKeys().size()-1); //4
    					// save key in root which will become new parent
    					Field stepParentField = ((InnerNode)root).getKeys().get(((InnerNode)root).getKeys().size()-1); //7
    					
    					// delete cousin and keyToMoveUp from uncle
    					uncle.getChildren().remove(cousin);
    					uncle.getKeys().remove(uncle.getKeys().size()-1);
    					
    					// set stepParent's field as stepParentField
    					InnerNode stepParent = s.parent;
    					ArrayList<Field> temp = new ArrayList<Field>();
    					temp.add(stepParentField);
    					stepParent.setKeys(temp);
    					stepParent.getChildren().add(0, cousin);
    					
    					// set root as keyToMoveUp
    					ArrayList<Field> temp2 = new ArrayList<Field>();
    					temp2.add(keyToMoveUp);
    					((InnerNode)root).setKeys(temp2);
    					
    					// set root's children as uncle and stepParent
    					ArrayList<Node> temp3 = new ArrayList<Node>();
    					temp3.add(uncle);
    					temp3.add(stepParent);
    					((InnerNode)root).setChildren(temp3);
    					
    					// go through uncle's children and reset parent as modified uncle
    					for(int i = 0; i < uncle.getChildren().size(); i++) {
    						uncle.getChildren().get(i).setParent(uncle);
    					}
    					
    					// reset merged leafnode as stepParent
    					for(int i = 0; i < stepParent.getChildren().size(); i++) {
    						stepParent.getChildren().get(i).setParent(stepParent);
    					}
    					
    					
    					
    				}
    				
    				else {	// else, uncle does not have a child to give
    					// COLLAPSE UNCLE'S LEVEL
    					// save values we need later
    					Field uncleFieldToPushUp = uncle.getKeys().get(uncle.getKeys().size()-1);
    					InnerNode grandparent = s.parent.parent;
    					
    					// make arraylist of children of uncle + children of s.parent
    					ArrayList<Node> newChildrens = uncle.getChildren();
    					for(int i = 0; i < s.parent.getChildren().size(); i++)
    					{
    						newChildrens.add(s.parent.getChildren().get(i));
    					}
    					
    					// set grandparent's children as the arraylist above
    					grandparent.setChildren(newChildrens);
    					
    					// set grandparent's keys
    					addSortedKeys(grandparent.getKeys(), uncleFieldToPushUp);
    					
    					// go through grandparent's children and reset parent as grandparent
    					for(int i = 0; i < grandparent.getChildren().size(); i++) {
    						grandparent.getChildren().get(i).setParent(grandparent);
    					}
    				}
    			}
    			// ---------end of if(s is underloaded)-----------
    		}

    	}
    	
    	
    }
    
    public void RemoveEntryFromNode(LeafNode s, Entry e)
    {
		ArrayList<Entry> entries = s.getEntries();
		for(int i = 0; i < entries.size(); i++) {
			if(entries.get(i).getField().compare(RelationalOperator.EQ, e.getField())) {
				entries.remove(i);
			}
		}
    }
    
    public Node getRoot() {
    	//your code here
    	return root;
    }
    
    public int findNodePosAndSet(ArrayList<Node> arr, Object obj, Object left, Object right)
    {
    	int i = 0;
    	boolean found = false;
    	while(i < arr.size()){
    		 if (obj.equals(arr.get(i))){
    			 found = true;
    			 break;    		
    		 }
    		 i++;
    	}
    	if(!found){
    		i = -1;
    	}
    	arr.set(i, (Node) left);
		arr.add(i+1, (Node) right);
    	return i;
    }
    
    public boolean isOverloaded(Node n) {
    	if(n.isLeafNode()) {
    		if(((LeafNode)n).getEntries().size() > pLeaf) {
    			return true;
    		}
    	} else {
    		// ?? should also I check that # keys = # children - 1?
    		if(((InnerNode)n).getKeys().size() > pInner-1) {	// !! changed from > to >=
    			return true;
    		}
    	}
    	return false;
    }
    
    
    public ArrayList<Entry> addSorted(ArrayList<Entry> entries, Entry e) {
    	boolean addPerformed = false;
		for(int i = 0; i < entries.size(); i++) {
			// insert so that targetEntries is in ascending order
			if(e.getField().compare(RelationalOperator.EQ, entries.get(i).getField())) {
				// entry already exists, don't insert
				return entries;
			}
			else if(e.getField().compare(RelationalOperator.LT, entries.get(i).getField())) {
				entries.add(i, e);
				addPerformed = true;
				break;
			}
		}
		// if e is greater than all entries
		if(addPerformed == false) {
			entries.add(e);
		}
		return entries;
    }
    
    
    
    public ArrayList<Field> addSortedKeys(ArrayList<Field> keys, Field f) {
    	boolean addPerformed = false;
		for(int i = 0; i < keys.size(); i++) {
			// insert so that targetEntries is in ascending order
			if(f.compare(RelationalOperator.EQ, keys.get(i))) {
				// entry already exists, don't insert
				return keys;
			}
			else if(f.compare(RelationalOperator.LT, keys.get(i))) {
				keys.add(i, f);
				addPerformed = true;
				break;
			}
		}
		// if e is greater than all entries
		if(addPerformed == false) {
			keys.add(f);
		}
		return keys;
    }
    
}
