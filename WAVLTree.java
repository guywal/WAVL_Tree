// גיא וולדמן (guywaldman1) - 305218877
// יובל שטראוסברג (straussberg) - 2044594212

/**
 *
 * WAVLTree
 *
 * An implementation of a WAVL Tree with
 * distinct integer keys and info
 *
 */

public class WAVLTree_guywaldman1_straussberg {
	
	private WAVLNode root;
	private WAVLNode EXT = new WAVLNode(-1, null);
	
	public WAVLTree_guywaldman1_straussberg() { //builds an empty tree
		root = null;
		EXT.rank = -1;
		EXT.size = 0;
	}
	
  /**
   * public boolean empty()
   *
   * returns true if and only if the tree is empty
   *
   */
  public boolean empty() {
    return this.root == null; 
  }

 /**
   * public String search(int k)
   *
   * returns the info of an item with key k if it exists in the tree
   * otherwise, returns null
   */
  public String search(int k)
  {
	  IWAVLNode node = this.getRoot(); //start from root of tree
	  int key;
	  while (node != null) { //iterate over tree nodes until found or external leaf
		  key = node.getKey();
		  if (k == key) { //found the node
			  return node.getValue();
		  }
		  else if (k < key) { //search the left subtree
			  node = node.getLeft();
		  }
		  else { //search the right subtree
			  node = node.getRight();
		  }
	  } 
	  return null; //went over the relevant subtree and didn't find the node. current node is external leaf
		  	  
  }

/**
 * public int insert(int k, String i)
 *
 * inserts an item with key k and info i to the WAVL tree.
 * the tree must remain valid (keep its invariants).
 * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
 * returns -1 if an item with key k already exists in the tree.
 */
 public int insert(int k, String i) {
	   WAVLNode node = new WAVLNode(k, i); //build relevant node
	   WAVLNode parent = this.treePosition(k); //find node's parent
	   if (parent == null) { //if the tree is empty make node it's root
		   this.root = node;
		   return 0;
	   }
	   if (parent.getKey() == k) { //node already exists in tree
		   return -1;
	   }
	   this.connectNode(parent, node); //make node the son of parent
	   this.updateSizes(parent, 1); //+1 to sizes from parent to root
	   return this.rebalanceInsert(parent); //rebalance according to case   
 }
 
 
WAVLNode treePosition(int k) { //return node with key K if exist or his insert position
	WAVLNode node = (WAVLNode)this.getRoot(); //start from root of tree
	if (node == null) { //if tree is empty return null
		return null;
	}
	int key;
	WAVLNode parent = node;
	while (node != null) { //iterate over tree nodes until found or external leaf
		parent = node;
		key = node.getKey();
		if (k == key) { //node with key k exists
			return node;
		}
		else if (k < key) { //search the left subtree
			node = (WAVLNode)node.getLeft();
		}
		else { //search the right subtree
			node = (WAVLNode)node.getRight();
			}
		} 
	return parent; //went over the relevant subtree and didn't find the node. 
		  	 
}
private void connectNode(WAVLNode parent, WAVLNode node) { //connect node to the correct side of parent by key size
	if (parent == null) {
		this.root = node;
	}
	else {
		if (node.getKey() < parent.getKey()) {
			parent.left = node;
		}
		else {
			parent.right = node;
		}
	}
	node.parent = parent;
}

private void connectNode(WAVLNode parent, WAVLNode node, WAVLNode childToReplace) { //connect node instead of ChildToReplace
	if (parent == null) {
		this.root = node;
	}
	else {
		if (parent.left.key == childToReplace.key) {
			parent.left = node;
		}
		else {
			parent.right = node;
		}
	}
	node.parent = parent;
}

private int rebalanceInsert(WAVLNode parent) { // rebalance the tree after insertion
	int count = 0,rightRank,leftRank;
	WAVLNode child;
	while(parent != null) { // stops at root
		rightRank = parent.rank - parent.right.rank; // right child rank difference
		leftRank = parent.rank - parent.left.rank; //left child rank difference
		if ((rightRank + leftRank == 3) || (rightRank == 1 && leftRank == 1)) { //parent is node 2,1 or 1,2 or 1,1 - rebalanced
			break;
		}
		else if (rightRank + leftRank == 1) { //parent is node 0,1 or 1,0 - promote
			parent.rank += 1;
			count += 1;                   // 1 operation
			parent = parent.parent;
			continue;
		}
		else if(rightRank == 0 ) {  // rightRank == 0 && leftRank == 2 - parent is node 2,0
			child = parent.right;
			if (child.rank - child.right.rank == 1) { //child is node 2,1 - rotate left
				this.singleRotation(parent, child, "left");
				count += 1; // 1 operation
				break;
			}
			else {								//child is node 1,2 - double rotate
				this.doubleRotation(parent, child, "left");
				count += 2; // 2 operations
				break;
			}
		}
		else {                  	// rightRank == 2 && leftRank == 0 - parent is node 0,2
			child = parent.left;
			if (child.rank - child.left.rank == 1) { // child is node 1,2 - rotate right
				this.singleRotation(parent, child, "right");
				count += 1; // 1 operation
				break;
			}
			else {								//child is node 2,1 - double rotate
				this.doubleRotation(parent, child, "right");
				count += 2; //2 operations
				break;
			}
		}
		
	}
	return count; // return # of operations
}

private void singleRotation(WAVLNode parent, WAVLNode child ,String dirction) { //rotate according to direction given
	WAVLNode gParent = parent.parent;
	this.connectNode(gParent,child); //connect child to gParent
	if (dirction == "right") { //check which of child's children needs to connect to parent
		this.connectNode(parent, child.right, parent.left); 
	}
	if (dirction == "left") { //if other direction
		this.connectNode(parent, child.left, parent.right);
	}
	this.connectNode(child, parent); //make parent a son of child
	parent.rank -= 1; //demote
	this.updateNodeSize(parent);
	this.updateNodeSize(child);
	if (child.parent == null) { //check if we change the root
		this.root = child;
	}
}

private void doubleRotation(WAVLNode parent, WAVLNode child ,String dirction) {
	String otherDir = "left"; //the first rotate's direction
	WAVLNode gChild = child.right;
	if (dirction.equals(otherDir)) {
		otherDir = "right";
		gChild = child.left;
	}
	singleRotation(child, gChild, otherDir); //first rotation
	singleRotation(parent, gChild, dirction); //second rotation
	gChild.rank += 1; //promote
}

private void updateNodeSize(WAVLNode node) {
	node.size = node.right.size + node.left.size + 1;
}




/**
   * public int delete(int k)
   *
   * deletes an item with key k from the binary tree, if it is there;
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
   * returns -1 if an item with key k was not found in the tree.
   */
   public int delete(int k) {
	   if (this.empty()) { //if the tree is empty
		   return -1;
	   }
	   WAVLNode node = this.treePosition(k); 
	   if (node.getKey() != k) { //tree doesn't have node with key k
		   return -1;
	   }
	   if (node.right.isRealNode() && node.left.isRealNode()) { //node has 2 real children
		   this.replaceWithSuccessor(node); //switch between node and his successor
	   }
	   this.connectNode(node.parent, this.onlyChild(node), node); //delete node by connecting his child to parent
	   this.updateSizes(node.parent, -1); //-1 to sizes from parent to root
	   int count = this.rebalanceDelete(node.parent); //rebalance the tree after deletion and get # of operations
	   if (!this.root.isRealNode()) { //if tree is empty, make sure EXT isn't the root
		   this.root = null;
	   }
	   return count; //return # of operations
   }

   private int rebalanceDelete(WAVLNode parent) { //rebalance the tree after deletion
	   int count = 0, rightRank, leftRank;
	   WAVLNode child;
	   if (parent != null) {
		   if (parent.rank == 1 && !(this.onlyChild(parent).isRealNode())) { //parent is a rank 1 leaf - demote
			   parent.rank -= 1;
			   count += 1; //1 operation
			   parent= parent.parent;
		   }
	   }
	   while(parent != null) { //stops at root
		   rightRank = parent.rank - parent.right.rank; //rank difference between parent and right child
		   leftRank = parent.rank - parent.left.rank; //rank difference between parent and left child
		   if ((rightRank + leftRank == 3) || (rightRank == 2 && leftRank == 2)) { //parent is node 2,1 or 1,2 or 2,2 - balanced
			   break;
		   }
		   else if (rightRank + leftRank == 5) { // parent is node 3,2 or 2,3 - demote
			   parent.rank -= 1;
			   count += 1; // 1 operation
			   parent = parent.parent; //move up the tree
			   continue;
		   }
		   else {
			   if (rightRank == 1) { //parent is node 3,1 - check right child
				   child = parent.right;
				   rightRank = child.rank - child.right.rank; //rank difference between child and child's right child
				   leftRank = child.rank - child.left.rank; //rank difference between child and child's left child
				   if (leftRank == 2 && rightRank == 2) { //child is node 2,2 - double demote 
					   child.rank -= 1;
					   parent.rank -= 1;
					   count += 2; //2 operations
					   parent = parent.parent;
					   continue;
				   }
				   else if (rightRank == 1) {			 //child is node 2,1 or 1,1 - rotate left
					   this.singleRotation(parent, child, "left");
					   child.rank += 1;
					   count += 1; //1 operation
					   if ((parent.rank == 1 && !(this.onlyChild(parent).isRealNode()))) { //parent is a rank 1 leaf - demote
						   parent.rank -= 1;
						   //count += 1; // do we need it?
					   }
					   break;
				   }
				   else {								//child is node 1,2 - double rotate
					   this.doubleRotation(parent, child, "left");
					   parent.parent.rank += 1;
					   parent.rank -= 1;
					   count += 2;  // 2 operations
					   break;
				   }
			   }
			   else {			// parent is node 1,3 - check left child
				   child = parent.left;
				   rightRank = child.rank - child.right.rank;
				   leftRank = child.rank - child.left.rank;
				   if (leftRank == 2 && rightRank == 2) { //child is node 2,2 - double demote
					   child.rank -= 1;
					   parent.rank -= 1;
					   count += 2; //2 operations
					   parent = parent.parent;
					   continue;
				   }
				   else if (leftRank == 1) { //child is node node 1,2 or 1,1 - rotate right
					   this.singleRotation(parent, child, "right");
					   child.rank += 1;
					   count += 1; //1 operation
					   if ((parent.rank == 1 && !(this.onlyChild(parent).isRealNode()))) { //parent is a rank 1 leaf - demote
						   parent.rank -= 1; 
						   //count += 1; // do we need it?
					   }
					   break;
				   }
				   else {				//child is node 2,1 - double rotate
					   this.doubleRotation(parent, child, "right");
					   parent.parent.rank += 1;
					   parent.rank -= 1;
					   count += 2; //2 operations
					   break;
				   }
			   }
		   }
	   }
	return count; //return # of operations
}

private void replaceWithSuccessor(WAVLNode node) { //switch between node and his successor - used when deleting node with 2 real children
	WAVLNode succ = this.successor(node);
	WAVLNode tempChild = this.onlyChild(succ);
	WAVLNode tempParent = succ.parent;
	if (node.right == succ) { //if successor is node's right child
		this.connectNode(node.parent, succ);
		succ.right = node;
		node.parent = succ;
		this.connectNode(succ, node.left);
		this.connectNode(node, tempChild);
	}
	else {
		this.connectNode(node.parent, succ);
		this.connectNode(tempParent, node);
		this.connectNode(succ, node.right);
		this.connectNode(succ, node.left);
		this.connectNode(node, tempChild);
	}
	if (!tempChild.isRealNode()) { //successor was a leaf
		node.right = EXT;
		node.left = EXT;
	}
	succ.size = node.size;
	succ.rank = node.rank;
}

   private WAVLNode onlyChild(WAVLNode succ) { //gets unary node or leaf and returns only child or EXT accordingly
	if (succ.right.isRealNode()) {
		return succ.right;
	}
	if (succ.left.isRealNode()) {
		return succ.left;
	}
	return EXT;
}

/**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty
    */
   public String min() //return smallest key node's value
   {
	   if (this.empty()) {
		   return null;
	   }
	   WAVLNode node = (WAVLNode) this.minNode(this.root);
	   return node.getValue(); 
   }
   
   
   private WAVLNode minNode(WAVLNode node) { //returns node with smallest key in tree with root node
	   while (node.left.isRealNode()) { //keep going left until leaf
		   node = node.left;
	   }
	   return node;
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty
    */
   public String max() //return biggest key node's value
   {
	   WAVLNode node = (WAVLNode) this.maxNode(this.root);
	   return node.getValue(); 
   }
   
   
   private WAVLNode maxNode(WAVLNode node) { //returns node with biggest key in tree with root node
	   while (node.right.isRealNode()) { //keep going right until leaf
		   node = node.right;
	   }
	   return node;
   }
   

  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */
  public int[] keysToArray()
  {
	  if (this.empty()) { //if tree is empty
		  int[] res = {};
		  return res;
	  }
	  int[] keyArr = new int[this.size()]; //assign an array the size of the number of nodes in tree
	  int index = 0;
	  WAVLNode node = this.minNode(this.root); //start at node with smallest key
	  while (index < this.size()) {
		  keyArr[index] = node.getKey();
		  index++;
		  node = this.successor(node);
	  }
	  return keyArr;              
  }
  
  private WAVLNode successor(WAVLNode node) { //returns the node with the next biggest key after current node
	  if (node.right.isRealNode()) {
		  node = node.right;
		  return this.minNode(node);
	  }
	  WAVLNode parent = node.parent;
	  while (parent != null && node == parent.right) {
		  node = parent;
		  parent = node.parent;
	  }
	  return parent;
  }
  

  /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */
  public String[] infoToArray()
  {
	  if (this.empty()) {
		  String[] res = {};
		  return res;
	  }
	  String[] infoArr = new String[this.size()];
	  int index = 0;
	  WAVLNode node = this.minNode(this.root); //start at node with smallest key
	  while (index < this.size()) {
		  infoArr[index] = node.getValue();
		  index++;
		  node = this.successor(node);
	  }
	  return infoArr;                     
  }

   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    *
    * precondition: none
    * postcondition: none
    */
   public int size()
   {
	   if (this.empty()) {
		   return 0;
	   } 
	   return this.root.getSubtreeSize(); //return size of root
   }
   
   
   /**
    * @param node
    * @param a
    * @pre a == -1 or a == 1
    */
   private void updateSizes(WAVLNode node, int a) { //increases or decreases all nodes' sizes from node to root
	  while (node != null) {
		  if (a == 1) {
			  node.size += 1;
		  }
		  else {
			  node.size -= 1;
		  }
		  node = node.parent;
	  }
   }
   
     /**
    * public int getRoot()
    *
    * Returns the root WAVL node, or null if the tree is empty
    *
    * precondition: none
    * postcondition: none
    */
   public IWAVLNode getRoot()
   {
		   return this.root;
   }
   
     /**
    * public int select(int i)
    *
    * Returns the value of the i'th smallest key (return -1 if tree is empty)
    * Example 1: select(1) returns the value of the node with minimal key 
	* Example 2: select(size()) returns the value of the node with maximal key 
	* Example 3: select(2) returns the value 2nd smallest minimal node, i.e the value of the node minimal node's successor 	
    *
	* precondition: size() >= i > 0
    * postcondition: none
    */   
   public String select(int i)
   {
	   if (this.empty()) {
			return "-1";
		   }
	   if (this.size() < i) { 
		   	return "-1";
	   }
	   return subSelect(this.root, i);
   }
   
   private String subSelect(WAVLNode x, int i) {//finds the (i)th node in tree with root x
	   int r = x.left.getSubtreeSize();
	   if (i == r) {
		   return x.getValue();
	   }
	   else if (i < r) {
		   return subSelect(x.left, i);
	   }
	   return subSelect(x.right, i - r - 1);
   }

	/**
	   * public interface IWAVLNode
	   * ! Do not delete or modify this - otherwise all tests will fail !
	   */
   
   
	public interface IWAVLNode{	
		public int getKey(); //returns node's key (for virtual node return -1)
		public String getValue(); //returns node's value [info] (for virtual node return null)
		public IWAVLNode getLeft(); //returns left child (if there is no left child return null)
		public IWAVLNode getRight(); //returns right child (if there is no right child return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual WAVL node (i.e not a virtual leaf or a sentinal)
		public int getSubtreeSize(); // Returns the number of real nodes in this node's subtree (Should be implemented in O(1))
	}

   /**
   * public class WAVLNode
   *
   * If you wish to implement classes other than WAVLTree
   * (for example WAVLNode), do it in this file, not in 
   * another file.
   * This class can and must be modified.
   * (It must implement IWAVLNode)
   */
  public class WAVLNode implements IWAVLNode{
	  private String info;
	  private int key;
	  public int rank;
	  private WAVLNode parent = null;
	  private WAVLNode left = EXT;
	  private WAVLNode right = EXT;
	  private int size;
	  
	  
	  	public WAVLNode(int key, String info) {
	  		this.key = key;
	  		this.info = info;
	  		this.rank = 0;
	  		this.size = 1;
	  	}
	  	
	  	
		public int getKey()
		{
			return this.key; 
		}
		public String getValue()
		{
			return this.info; 
		}
		
		public IWAVLNode getLeft()
		{
			if (this.rank == -1) { //this is EXT
				return null;
			}
			if (!this.left.isRealNode()) { //this.left is EXT
				return null;
			}
			return this.left; 
		}
		
		public IWAVLNode getRight()
		{
			if (this.rank == -1) { //this is EXT
				return null;
			}
			if (!this.right.isRealNode()) { //this.right is EXT
				return null;
			}
			return this.right; 
		}
		
		// Returns True if this is a non-virtual WAVL node (i.e not a virtual leaf or a sentinal)
		public boolean isRealNode()
		{
			return (this.rank != -1 && this != EXT);
		}

		public int getSubtreeSize()
		{
			return this.size;
		}
  }

}
  
