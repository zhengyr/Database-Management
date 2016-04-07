//Yiran Zheng
//zhengyr@brandeis.edu
//CS127
package edu.brandeis.cs127.pa3;

/**
 * Internal Nodes of B+-Trees.
 * 
 * @author cs127b
 */
public class InternalNode extends Node {

	/**
	 * Construct an InternalNode object and initialize it with the parameters.
	 * 
	 * @param d
	 *            degree
	 * @param p0
	 *            the pointer at the left of the key
	 * @param k1
	 *            the key value
	 * @param p1
	 *            the pointer at the right of the key
	 * @param n
	 *            the next node
	 * @param p
	 *            the previous node
	 */
	public InternalNode(int d, Node p0, int k1, Node p1, Node n, Node p) {

		super(d, n, p);
		ptrs[0] = p0;
		keys[1] = k1;
		ptrs[1] = p1;
		lastindex = 1;

		if (p0 != null)
			p0.setParent(new Reference(this, 0, false));
		if (p1 != null)
			p1.setParent(new Reference(this, 1, false));
	}

	/**
	 * The minimal number of keys this node should have.
	 * 
	 * @return the minimal number of keys a leaf node should have.
	 */
	public int minkeys() {
		if (getParent() != null) {// if the node is not root
			return (int) Math.ceil((degree) / 2.0) - 1;
		} else {
			return 1;
		}
	}

	/**
	 * Check if this node can be combined with other into a new node without
	 * splitting. Return TRUE if this node and other can be combined.
	 */
	public boolean combinable(Node other) {
		return getLast() + other.getLast() <= maxkeys() - 1; // the "-1" here is considering
																// the fact that we have to combine pointers of this node and the other node
	}

	/**
	 * Combines contents of this node and its next sibling (next) into a single
	 * node,
	 */
	public void combine() {
		lastindex++; //move pointer and parent key first
		int indexOfNext = getNext().getParent().getIndex();
		keys[lastindex] = getParent().getNode().getKey(indexOfNext);
		ptrs[lastindex] = getNext().getPtr(0);
		ptrs[lastindex].setParent(new Reference(this, lastindex, false));
		int i = 1;
		while (i <= getNext().getLast()) { //move everything in the next node to this node
			lastindex++;
			keys[lastindex] = getNext().getKey(i);
			ptrs[lastindex] = getNext().getPtr(i);
			ptrs[lastindex].setParent(new Reference(this, lastindex, false));
			i++;
		}
		setNext(getNext().getNext());
		if (getNext() != null)
			getNext().setPrev(this);
		getParent().getNode().delete(indexOfNext);// call the upper level
													// deletion

	}

	/**
	 * Redistributes keys and pointers in this node and its next sibling so that
	 * they have the same number of keys and pointers, or so that this node has
	 * one more key and one more pointer. Returns the key that must be inserted
	 * into parent node.
	 * 
	 * @return the value to be inserted to the parent node
	 */
	public int redistribute() {
		//the basic idea is that we have two big lists contain all the keys and pointers from the two node and then 
		//we separate them back to the two nodes
		//insert and delete are different cases since they have different number pointers
		//i could somehow merge the delete and insert together, but i wrote insert first in order to test insert first
		int key = 0;
		int nextLastIndex = getNext().getLast();
		if (getNext().parentref == null) {//insert
			int[] combineKeys = new int[getLast() + nextLastIndex + 1];
			Node[] combinePtrs = new Node[getLast() + nextLastIndex + 1];
			int indexOfVal = 1;
			while (indexOfVal <= getLast() && keys[indexOfVal] <= getNext().getKey(1)) {//where we should insert the new value
				indexOfVal++;
			}
			int indexOfMiddle = (int) Math.ceil(combineKeys.length / 2.0);
			if (indexOfVal > getLast()) {// if our value is the largest one, then we don't need the two big lists, 
											//we would simply shift things from this node to the next
				key = keys[indexOfMiddle];
				int val = getNext().getKey(1);
				Node valPtr = getNext().getPtr(1);
				keys[indexOfMiddle] = 0;
				getNext().ptrs[0] = ptrs[indexOfMiddle];
				getNext().ptrs[0].setParent(new Reference(getNext(), 0, false));
				ptrs[indexOfMiddle] = null;
				for (int i = indexOfMiddle + 1; i <= getLast(); i++) {//move things from this to next
					getNext().keys[nextLastIndex] = keys[i];
					getNext().ptrs[nextLastIndex] = ptrs[i];
					getNext().ptrs[nextLastIndex].setParent(new Reference(getNext(), nextLastIndex, false));
					keys[i] = 0;
					ptrs[i] = null;
					nextLastIndex++;
				}
				lastindex = indexOfMiddle - 1;
				getNext().keys[nextLastIndex] = val;
				getNext().ptrs[nextLastIndex] = valPtr;
				getNext().ptrs[nextLastIndex].setParent(new Reference(getNext(), nextLastIndex, false));
				getNext().lastindex = nextLastIndex;
				return key;
			} else {//if our value is not the largest, we would need to insert to the right position, the two big lists would help
				combinePtrs[0] = ptrs[0];
				for (int i = 1; i < indexOfVal; i++) {//copy to big lists
					combineKeys[i] = keys[i];
					keys[i] = 0;
					combinePtrs[i] = ptrs[i];
					ptrs[i] = null;
				}
				combineKeys[indexOfVal] = getNext().getKey(1);
				combinePtrs[indexOfVal] = getNext().getPtr(1);
				for (int i = indexOfVal; i <= getLast(); i++) {//copy to big lists
					combineKeys[i + 1] = keys[i];
					keys[i] = 0;
					combinePtrs[i + 1] = ptrs[i];
					ptrs[i] = null;
				}
				key = combineKeys[indexOfMiddle];
				ptrs[0] = combinePtrs[0];
				ptrs[0].setParent(new Reference(this, 0, false));
				for (int i = 1; i < indexOfMiddle; i++) {//copy back to this node
					keys[i] = combineKeys[i];
					ptrs[i] = combinePtrs[i];
					ptrs[i].setParent(new Reference(this, i, false));
				}
				lastindex = indexOfMiddle - 1;
				getNext().ptrs[0] = combinePtrs[indexOfMiddle];
				getNext().ptrs[0].setParent(new Reference(getNext(), 0, false));
				for (int i = indexOfMiddle + 1; i < combineKeys.length; i++) {//copy back to next node
					getNext().keys[nextLastIndex] = combineKeys[i];
					getNext().ptrs[nextLastIndex] = combinePtrs[i];
					getNext().ptrs[nextLastIndex].setParent(new Reference(getNext(), nextLastIndex, false));
					nextLastIndex++;
				}
				nextLastIndex--;
				getNext().lastindex = nextLastIndex;
				return key;
			}
		} else {//for delete, we have two node that's already sorted, so the copy and copy back part would be simpler
			int oldKey = getParent().getNode().getKey(getParent().getIndex() + 1);
			int[] combineKeys = new int[getLast() + getNext().getLast() + 2];
			Node[] combinePtrs = new Node[getLast() + getNext().getLast() + 2];
			combinePtrs[0] = getPtr(0);
			ptrs[0] = null;
			int last = getLast();
			for(int i = 1; i <= last; i++){//copy this node to list
				combineKeys[i] = keys[i];
				combinePtrs[i] = ptrs[i];
				keys[i] = 0;
				ptrs[i] = null;
				lastindex--;
			}
			combineKeys[last+1] = oldKey;
			combinePtrs[last+1] = getNext().ptrs[0];
			getNext().ptrs[0] = null;
			int offset = last + 1;
			last = getNext().getLast();
			for(int i = 1; i <= last; i++){//copy next node to list
				combineKeys[i + offset] = getNext().getKey(i);
				combinePtrs[i + offset] = getNext().getPtr(i);
				getNext().keys[i] = 0;
				getNext().ptrs[i] = null;
				getNext().lastindex--;
			}
			int indexOfMiddle = (int)Math.ceil(combineKeys.length/2.0);
			key = combineKeys[indexOfMiddle];
			getParent().getNode().keys[getParent().getIndex()+1] = combineKeys[indexOfMiddle];
			ptrs[0] = combinePtrs[0];
			ptrs[0].setParent(new Reference(this, 0, false));
			for(int i = 1; i < indexOfMiddle; i++){//copy back to this node
				keys[i] = combineKeys[i];
				ptrs[i] = combinePtrs[i];
				ptrs[i].setParent(new Reference(this, i, false));
				lastindex++;
			}
			getNext().ptrs[0] = combinePtrs[indexOfMiddle];
			getNext().ptrs[0].setParent(new Reference(getNext(), 0, false));
			int j = 1;
			for(int i = indexOfMiddle + 1; i < combineKeys.length; i++){//copy back to next node
				getNext().keys[j] = combineKeys[i];
				getNext().ptrs[j] = combinePtrs[i];
				getNext().ptrs[j].setParent(new Reference(getNext(), j, false));
				j++;
				getNext().lastindex++;
			}
			return key;
		}

	}
	

	/**
	 * Inserts (val, ptr) pair into this node at keys [i] and ptrs [i]. Called
	 * when this node is not full. Differs from {@link LeafNode} routine in that
	 * updates parent references of all ptrs from index i+1 on.
	 * 
	 * @param val
	 *            the value to insert
	 * @param ptr
	 *            the pointer to insert
	 * @param i
	 *            the position to insert the value and pointer
	 */
	public void insertSimple(int val, Node ptr, int i) {
		int count = getLast();
		while (count >= i) {//shift things by one position
			ptrs[count + 1] = ptrs[count];
			keys[count + 1] = keys[count];
			ptrs[count + 1].getParent().increaseIndex();
			count--;
		}
		ptrs[i] = ptr;//insert
		keys[i] = val;
		ptrs[i].setParent(new Reference(this, i, false));
		lastindex++;
	}

	/**
	 * Deletes keys [i] and ptrs [i] from this node, without performing any
	 * combination or redistribution afterwards. Does so by shifting all keys
	 * and pointers from index i+1 on one position to the left. Differs from
	 * {@link LeafNode} routine in that updates parent references of all ptrs
	 * from index i+1 on.
	 * 
	 * @param i
	 *            the index of the key to delete
	 */
	public void deleteSimple(int i) {
		while (i < getLast()) {//shift things left by one position
			keys[i] = getKey(i + 1);
			ptrs[i] = getPtr(i + 1);
			getPtr(i).getParent().decreaseIndex();
			i++;
		}
		keys[getLast()] = 0;
		ptrs[getLast()] = null;
		lastindex--;
	}

	/**
	 * Uses findPtrInex and calles itself recursively until find the value or
	 * pind the position where the value should be.
	 * 
	 * @return the referenene pointing to a leaf node.
	 */
	public Reference search(int val) {
		Reference ref = null;
		int ptrIndex = findPtrIndex(val);
		if (ptrs[ptrIndex] == null) {
			return ref;
		} else {//recursively call child's search
			ref = ptrs[ptrIndex].search(val);
		}
		return ref;
	}

	/**
	 * Insert (val, ptr) into this node. Uses insertSimple, redistribute etc.
	 * Insert into parent recursively if necessary
	 * 
	 * @param val
	 *            the value to insert
	 * @param ptr
	 *            the pointer to insert
	 */
	public void insert(int val, Node ptr) {
		if (!full()) {
			if (val <= keys[lastindex]) {// if the value we insert if less then
											// the value in the last index
				insertSimple(val, ptr, findKeyIndex(val));
			} else {
				insertSimple(val, ptr, getLast() + 1);
			}
		} else {
			InternalNode newNextNode = new InternalNode(degree, null, val, ptr, getNext(), this);
			int key = redistribute();
			if (getParent() != null) {// if this node has a parent, then insert
										// the key to with its leaf to the
										// parent
				// newNextNode.setParent(new Reference(getParent().getNode(),
				// -1, false));
				getParent().getNode().insert(key, newNextNode);
			} else {// if currently our node is root, then we need to create new
					// internal node so that this leaf node and its sibling
					// would have a parent
				new InternalNode(degree, this, key, newNextNode, null, null);
			}
		}
	}

	public void outputForGraphviz() {

		// The name of a node will be its first key value
		// String name = "I" + String.valueOf(keys[1]);
		// name = BTree.nextNodeName();

		// Now, prepare the label string
		String label = "";
		for (int j = 0; j <= lastindex; j++) {
			if (j > 0)
				label += "|";
			label += "<p" + ptrs[j].myname + ">";
			if (j != lastindex)
				label += "|" + String.valueOf(keys[j + 1]);
			// Write out any link now
			BTree.writeOut(myname + ":p" + ptrs[j].myname + " -> " + ptrs[j].myname + "\n");
			// Tell your child to output itself
			ptrs[j].outputForGraphviz();
		}
		// Write out this node
		BTree.writeOut(myname + " [shape=record, label=\"" + label + "\"];\n");
	}

	/**
	 * Print out the content of this node
	 */
	void printNode() {

		int j;
		System.out.print("[");
		for (j = 0; j <= lastindex; j++) {

			if (j == 0)
				System.out.print(" * ");
			else
				System.out.print(keys[j] + " * ");

			if (j == lastindex)
				System.out.print("]");
		}
	}
}
