//Yiran Zheng
//zhengyr@brandeis.edu
//CS127
package edu.brandeis.cs127.pa3;

import java.util.Arrays;

/**
   LeafNodes of B+ trees
 */
public class LeafNode extends Node {

	/**
       Construct a LeafNode object and initialize it with the parameters.
       @param d the degree of the leafnode
       @param k the first key value of the node
       @param n the next node 
       @param p the previous node
	 */
	public LeafNode (int d, int k, Node n, Node p){
		super (d, n, p);
		keys [1] = k;
		lastindex = 1;
	}      


	public void outputForGraphviz() {

		// The name of a node will be its first key value
		// String name = "L" + String.valueOf(keys[1]);
		// name = BTree.nextNodeName();

		// Now, prepare the label string
		String label = "";
		for (int j = 0; j < lastindex; j++) {
			if (j > 0) label += "|";
			label += String.valueOf(keys[j+1]);
		}
		// Write out this node
		BTree.writeOut(myname + " [shape=record, label=\"" + label + "\"];\n");
	}

	/** 
	the minimum number of keys the leafnode should have.
	 */
	public int minkeys () {//if the node is not root
		if(getParent() != null) return (int) Math.ceil((degree-1)/2.0);
		return 1;
	}

	/**
       Check if this node can be combined with other into a new node without splitting.
       Return TRUE if this node and other can be combined. 
       @return true if this node can be combined with other; otherwise false.
	 */
	public boolean combinable (Node other){
		return getLast() + other.getLast() <= maxkeys();
	}

	/**
       Combines contents of this node and its next sibling (nextsib)
       into a single node
	 */
	public void combine (){
		int nextIndex = getNext().getParent().getIndex();
		for(int i = 1; i <= getNext().getLast(); i++){ //move everything from next node to this node
			lastindex++;
			keys[lastindex] = getNext().getKey(i);
		}
		//set next node to be the next of next
		setNext(getNext().getNext());
		if(getNext() != null) getNext().setPrev(this);
		getParent().getNode().delete(nextIndex);
	}

	/**
       Redistributes keys and pointers in this node and its
       next sibling so that they have the same number of keys
       and pointers, or so that this node has one more key and
       one more pointer,.  
       @return int Returns key that must be inserted
       into parent node.
	 */
	public int redistribute (){ 
		//the main idea is pretty simple here, we have two big lists that contain all the pointers
		//and keys in order, and then separate them into two nodes
		//the running time is proportional to the number of keys and pointers in the two nodes
		//it's linear
		int key = 0;
		int nextLastIndex = getNext().getLast();
		int[] combineKeys = new int[getLast()+nextLastIndex+1];
		for(int i = 1; i <= getLast(); i++){//copy from fist node
			combineKeys[i] = getKey(i);
			keys[i] = 0;
		}
		for(int i = 1; i <= nextLastIndex; i++){//copy from second node
			combineKeys[i+getLast()] = getNext().getKey(i);
			getNext().keys[i] = 0;
		}
		Arrays.sort(combineKeys);
		int indexOfKey = (int) Math.ceil((nextLastIndex+getLast())/2.0);
		lastindex = indexOfKey;
		getNext().lastindex = combineKeys.length - 1 - indexOfKey;
		for(int i = 1; i <= getLast(); i++){//copy back to first node
			keys[i] = combineKeys[i];
		}
		for(int i = 1; i <=getNext().getLast(); i++){//copy back to second node
			getNext().keys[i] = combineKeys[i + indexOfKey];
		}
		key = getNext().getKey(1);
		Reference parentOfNext = getNext().getParent();
		if(parentOfNext != null){//set parent key
			parentOfNext.getNode().keys[parentOfNext.getIndex()] = key;
		}
		return key;
	}

	/**
       Insert val into this node at keys [i].  (Ignores ptr) Called when this
       node is not full.
       @param val the value to insert to current node
       @param ptr not used now, use null when call this method 
       @param i the index where this value should be
	 */
	public void insertSimple (int val, Node ptr, int i){
		int count = getLast();
		while(count >= i){//shift everything to the right by one position
			keys[count+1] = keys[count];
			count--;
		}
		keys[i] = val;
		lastindex++;
	}


	/**
       Deletes keys [i] and ptrs [i] from this node,
       without performing any combination or redistribution afterwards.
       Does so by shifting all keys from index i+1 on
       one position to the left.  
	 */
	public void deleteSimple (int i){
		int oldKey = keys[i];
		for(int j = i; j < getLast(); j++){//shift everything after the deleted value by one
			keys[j] = keys[j+1];
		}
		keys[lastindex] = 0;
		lastindex--;
		if(i != 1 || getParent() == null || getPrev() == null) return; //if we did not delete key in internal node or we are root or we are the smallest then we are done
		if(getParent().getIndex() != 0){//if our node is not the left most one, just change the key in parent level
			getParent().getNode().keys[getParent().getIndex()] = keys[1];
		}else{//now we need to search up to find if our old key is used somewhere up the tree
			Node curr = getParent().getNode();
			while(curr.getParent() != null){//keep searching up to find if we need to replace internal node
				int indexOfCurr = curr.getParent().getIndex();
				if(oldKey == curr.getParent().getNode().getKey(indexOfCurr)){
					if(lastindex == 0){//if there is no key in current node, use sibling's node
						if(getNext() != null) curr.getParent().getNode().keys[indexOfCurr] = getNext().getKey(1);
					}else{
						curr.getParent().getNode().keys[indexOfCurr] = keys[1];
					}
					return;
				}
				curr = curr.getParent().getNode();
			}
		}
		
	} 

	/**
       Uses findKeyIndex, and if val is found, returns the reference with match set to true, otherwise returns
       the reference with match set to false.
       @return a Reference object referring to this node. 
	 */
	public Reference search (int val){//search if we find the value in leaf node
		int keyIndex = findKeyIndex(val);
		if(keys[keyIndex] == val){
			return new Reference(this, keyIndex, true);
		}else{
			return new Reference(this, keyIndex, false);
		}
	}

	/**
       Insert val into this, creating split
       and recursive insert into parent if necessary
       Note that ptr is ignored.
       @param val the value to insert
       @param ptr (not used now, use null when calling this method)
	 */
	public void insert (int val, Node ptr){
		if(!full()){//if we have room for this value
			int keyIndex = findKeyIndex(val);
			if(val > keys[keyIndex]) keyIndex = getLast() + 1; //if the value we insert is large than the value in the last index
			insertSimple(val, null, keyIndex);
		}else{
			LeafNode newNextNode = new LeafNode(degree, val, getNext(), this);
			int key = redistribute();
			if(getParent() != null){//if this node has a parent, then insert the key to with its leaf to the parent
				getParent().getNode().insert(key, newNextNode);
			}else{//if currently our node is root, then we need to create new internal node so that this leaf node and its sibling would have a parent
				new InternalNode(degree, this, key, newNextNode, null, null); 
			}

		}
	}


	/**
       Print to stdout the content of this node
	 */
	void printNode (){
		System.out.print ("[");
		for (int i = 1; i < lastindex; i++) 
			System.out.print (keys[i]+" ");
		System.out.print (keys[lastindex] + "]");
	}
}
