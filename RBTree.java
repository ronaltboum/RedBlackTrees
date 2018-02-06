
/**
 *
 * RBTree
 *
 * An implementation of a Red Black Tree with
 * non-negative, distinct integer keys and values
 *
 */

public class RBTree {
	
	RBNode root;
	RBNode minNode;
	RBNode maxNode;
	
	public RBTree(){
		root = new RBNode(Integer.MAX_VALUE);
		root.setBlack();
		root.setnodeSize(1);
		maxNode = new RBNode(-2);//default value
		minNode = new RBNode(Integer.MAX_VALUE);	
	}                       //default value 
	 
  /**
   * public boolean empty()
   *
   * returns true if and only if the tree is empty
   *
   */
	public boolean empty() {
		if (root.nodeSize == 1)
			return true;
		else
			return false;
	}

 /**
   * public String search(int k)
   *
   * returns the value of an item with key k if it exists in the tree
   * otherwise, returns null
   */
  public String search(int k){
	  if (this.empty()==true)
		  return null;
	  if (root.getLeft().getKey()==k){
		  return root.getLeft().getValue();			  
	  }
	  RBNode x= root.getLeft();
	  while((x.isNILNode()==false) &&(x.getKey()!=k)){
		  if(k<x.getKey()){
			  x=x.left;
		  }
		  else if(k>x.getKey()){
			  x=x.right;
		  }
	  }
	  if ((x.getKey()>=0)) 
		  return x.getValue();  
	  else
		  return null;
  }

  /**
   * public int insert(int k, String v)
   *
   * inserts an item with key k and value v to the red black tree.
   * the tree must remain valid (keep its invariants).
   * returns the number of color flips, or 0 if no color flips were necessary.
   * returns -1 if an item with key k already exists in the tree.
   */
   public int insert(int k, String v) {
	   if (search(k)!=null) //checks if already exists 
		   return -1;
	   RBNode insertedNode = new RBNode(k, v);
	   insertedNode.setnodeSize(1);
	   //the case when the tree is empty
	   if (this.empty()){	
		   insertedNode.setBlack();
			root.addLeftChild(insertedNode);	
			updateMinNodeMaxNode(insertedNode);
			return 0;
		}
	   RBNode closeNode= findFatherForInsert(k);
	   if (closeNode.getKey() > k)
		   closeNode.addLeftChild(insertedNode);
	   else
		   closeNode.addRightChild(insertedNode);
	   
	   updateMinNodeMaxNode(insertedNode);
	   
	   return this.colorChangeForInsert(insertedNode,0);
   }

  /**
   * public int delete(int k)
   *
   * deletes an item with key k from the binary tree, if it is there;
   * the tree must remain valid (keep its invariants).
   * returns the number of color flips, or 0 if no color flips were needed.
   * returns -1 if an item with key k was not found in the tree.
   */
   public int delete(int k){
	   if (search(k)==null) //if k is not in the tree 
		   return -1;
	   RBNode deleteNode = findLocation(k);
	   //easy cases first: the case where the node has just one child
	   //or has no children
	   if ((deleteNode.right.isNILNode())||
			   (deleteNode==null)){
			deleteNode = findFatherForDelete(k); //TC O(logn)
			MinMax_NodesIfNodeWasDelete(deleteNode);//TC O(logn)
			deleteNode.Transplant(deleteNode.left);
			if (deleteNode.getColor() == 'R'){
				return 0;
			}
			if (this.empty()){
				return 0;
			}
			else{
				return ColorChange_delete(deleteNode.left, 0);
			}
		}
		else if ((deleteNode.left.isNILNode())||
			(deleteNode.left==null)){
			deleteNode = findFatherForDelete(k); 
			MinMax_NodesIfNodeWasDelete(deleteNode);
			deleteNode.Transplant(deleteNode.right);
			if (deleteNode.getColor() == 'R'){
				return 0;
			}
			return ColorChange_delete(deleteNode.right, 0);
		}
	   
	   //if the node has two children
		else{
			RBNode successor = deleteNode.findSuccessor();
			if ((successor.isNILNode())||((successor==null)))
				System.out.println("error");

			RBNode temporaryNode = successor;
			int numofColorChanges = this.delete(successor.getKey());
			deleteNode.replaceNodeInfo(temporaryNode);
			return numofColorChanges;
		}
   }

   /**
    * public String min()
    *
    * Returns the value of the item with the smallest key in the tree,
    * or null if the tree is empty
    */
   public String min()
   {
	   if(this.empty()==true)
		   return null;
	   /*RBNode realroot=root.getLeft();
	   if (realroot.getLeft().isNILNode())
		   return realroot.getValue();
	   RBNode x=realroot.getLeft();
	   while(x.isNILNode()==false){
		   x=x.getLeft();  
	   }
	   minNode=x.getParent();*/
	   return minNode.getValue();  
   }

   /**
    * public String max()
    *
    * Returns the value of the item with the largest key in the tree,
    * or null if the tree is empty
    */
   public String max()
   {
	   if(this.empty()==true)
		   return null;
	   /*RBNode realroot=root.getLeft();
	   if (realroot.getRight().isNILNode())
		   return realroot.getValue();
	   RBNode x=realroot.getRight();
	   while(x.isNILNode()==false){
		   x=x.getRight();  
	   }
	   maxNode=x.getParent();*/
	   return maxNode.getValue();   
   }

  /**
   * public int potential()
   *
   * Returns the tree's current potential, which is
   * the number of black nodes with two black children + 2 * the number of black nodes with two red children.
   */
    public int potential(){
    if(this.empty()==true)
    	return 0;
    //int TwoBlackChildren=0;
    //int TwoRedChildren=0;
    int[] array=new int[2];
    RBNode realroot=root.getLeft();
    int[] new_array=realroot.recPotential(array);
    
	return new_array[0]+2*new_array[1];	
    }

  /**
   * public int[] keysRange(int k1, int k2)
   *
   * Returns a sorted array which contains all keys in the tree between k1 and k2,
   * or an empty array if there are no such keys.
   */
  public int[] keysRange(int k1, int k2){
	  if ((k2<k1)||(this.empty()==true)||
	  (maxNode.getKey()<k1)||
	  (minNode.getKey()>k2))
		  return new int[0];
	  int[] arr = new int[k2-k1+1];
	  for (int i=0;i<arr.length;i++)
		  arr[i]=-1;//default value 0 may be a key
	  if (root.getLeft().getKey()<k1)
		  updateRangedArray(arr,root.getLeft().getRight(),k1,k2);
	  else if (root.getLeft().getKey()>k2)
		  updateRangedArray(arr,root.getLeft().getLeft(),k1,k2);
	  else
		  updateRangedArray(arr,root.getLeft(),k1,k2);
	  int countNodes=0;
	  for (int i=0;i<arr.length;i++){
		  if (arr[i]>=0)
			  countNodes++; 
	  }
	  int[] new_arr=new int[countNodes];
	  int new_count=0;
	  for (int i=0;i<arr.length;i++){
		  if (arr[i]>=0){
			  new_arr[new_count]=arr[i];
			  new_count++;
		  }
	  }
	  return new_arr;              
  }

  /**
   * public String[] valuesRange(int k1, int k2)
   *
   * Returns an array which contains all values in the tree which match keys
   * between k1 and k2, sorted by their respective keys,
   * or an empty array if there are no such values.
   */
  public String[] valuesRange(int k1, int k2){
	  if ((k2<k1)||(this.empty()==true)||
	  (maxNode.getKey()<k1)||
	  (minNode.getKey()>k2))
		  return new String[0];
	  int[] keys_array=keysRange(k1,k2);
	  String[] arr = new String[keys_array.length];
	  arr[0]=search(keys_array[0]);
	  RBNode startNode= searchNode(keys_array[0]);
	  for (int i = 1; i < arr.length; i++) {
		  RBNode Successor=startNode.findSuccessor();
		  arr[i]=Successor.getValue();
		  startNode=Successor;
	  }
      return arr;                    
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
	   return root.getNodeSize()-1; 
   }
   
   private void updateMinNodeMaxNode(RBNode new_Node) { 
	   //updates minNode and maxNode
	   if (new_Node.getKey() > maxNode.getKey()){
			maxNode = new_Node;
		}
	   if (new_Node.getKey() < minNode.getKey()){
			minNode = new_Node;
		}	
	}
   
   private int[] updateRangedArray(int[] arr, RBNode Node,
		   int k1,int k2){
	   if (Node.isNILNode()==true)
		   return arr; //we are done
	   else if (Node.getKey()<=k1){
		   if (Node.getKey()==k1)
			   arr[0]=k1;
		   updateRangedArray(arr,Node.getRight(),k1,k2);
	   }
	   else if (Node.getKey()>=k2){
		   if (Node.getKey()==k2)
			   arr[arr.length-1]=k2;
		   updateRangedArray(arr,Node.getLeft(),k1,k2);
	   }
	   else{
		   arr[Node.getKey()-k1]=Node.getKey();
		   updateRangedArray(arr,Node.getLeft(),k1,k2);
		   updateRangedArray(arr,Node.getRight(),k1,k2);
	   }
	   
	   return new int[0];//default value, program will never do that
   }
   
   public RBNode searchNode(int k){
		  if (this.empty()==true)
			  return null;
		  if (root.getLeft().getKey()==k){
			  return root.getLeft();			  
		  }
		  RBNode x= root.getLeft();
		  while((x.isNILNode()==false) &&(x.getKey()!=k)){
			  if(k<x.getKey()){
				  x=x.left;
			  }
			  else if(k>x.getKey()){
				  x=x.right;
			  }
		  }
		  if ((x.getKey()>=0)) 
			  return x;  
		  else
			  return null;
	  }
   
   private RBNode findFatherForInsert(int k) {
		RBNode closenode;
		for(closenode = root;(((!closenode.isNILNode()))&&
				(!(closenode==null)));){
			closenode.setnodeSize(closenode.getNodeSize()+1);
			if (k < closenode.key){
				if(closenode.left.isNILNode())
					return closenode;
				else
					closenode = closenode.left;
			}
			else{
				if(closenode.right.isNILNode())
					return closenode;
				else
					closenode = closenode.right;
			}
		}
		return closenode;
	}
   
   public RBNode findLocation(int k){ // returns the node 
	   //with key k, or if there's no node with key k we'll
	   //return the node with the closest key to k
		if (this.empty()){
			return root;
		}
		RBNode currentNode = root.left;
		RBNode tempNode = currentNode;
		while ((!(currentNode.isNILNode()))&&
				(!(currentNode==null))){
			tempNode = currentNode;
			if (k == currentNode.key){
				return currentNode;
			}
			else if (k > currentNode.key){
				currentNode = currentNode.right;
			}
			else{
				currentNode = currentNode.left;
			}
		}
		return tempNode;
	}
   
   private int colorChangeForInsert(RBNode insertedNode,
		                            int colorChanges) {
	   if (insertedNode.isRoot()){ //insertedNode is
		   insertedNode.setBlack();//Real Root
			return colorChanges+1;
		}
	   if (insertedNode.parent.getColor() == 'B'){ //easy case first:
		   // the case when the parent is black
			return colorChanges;
		}
	   if (insertedNode.parent.isRoot()){  //when the parent is 
		   //the real root
		   insertedNode.parent.setBlack();
			return colorChanges+1;
		}
	   //case 1
	   if (insertedNode.Uncle().getColor() == 'R'){
		   insertedNode.Uncle().setBlack();
		   insertedNode.parent.setBlack();
		   insertedNode.parent.parent.setRed();
			return colorChangeForInsert(insertedNode.parent.parent,
					                    colorChanges+3);
		}
	   //case 2
	   if(insertedNode.Uncle().getColor() == 'B' && 
			   ((insertedNode.isRightChild() && 
					   insertedNode.parent.isLeftChild()) || 
					   (insertedNode.isLeftChild()
					   && insertedNode.parent.isRightChild()))){
			if (insertedNode.parent.isLeftChild()){
				if(insertedNode.isRightChild()){
					insertedNode.parent.LeftRotation();

					return colorChangeForInsert(insertedNode.left,
							                    colorChanges);
				}
			}
			else{	//in case parent is a Right Child
				if(insertedNode.isLeftChild()){
					insertedNode.parent.RightRotation();
					return colorChangeForInsert(insertedNode.right, 
							                    colorChanges);
				}
			}
		}
	   //case 3 
	   if (insertedNode.Uncle().getColor() == 'B'){	
		   insertedNode.parent.setBlack();
		   insertedNode.parent.parent.setRed();	
			colorChanges = colorChanges+2;
			if (insertedNode.parent.isLeftChild()){
				if(insertedNode.isLeftChild())
					insertedNode.parent.parent.RightRotation();
			}
			else{
				if(insertedNode.isRightChild())
					insertedNode.parent.parent.LeftRotation();
			}
			return colorChanges;
		}
	   else{
			System.out.println("None of the insertion cases match");
			return -1; //dummy return value
		}
   }
   
   private RBNode findFatherForDelete(int k) {  //returns the 
	   //node we want to delete, or if there's no node with k
	   // as it's key, we'll return the node with the closest
	   // key to k. in addition the function subtracts one from
	   // every forefather of the node we want to delete.
		RBNode Node;
		for(Node = root;(!Node.isNILNode())&&(Node!=null);){
			Node.setnodeSize(Node.getNodeSize()-1);
			if (k < Node.key){
				if(Node.left.isNILNode())
					return Node;
				else
					Node = Node.left;
			}
			else{
				if(Node.right.isNILNode()||Node.key==k)
					return Node;
				else
					Node = Node.right;
			}
		}
		return Node;
	}
   
   private void MinMax_NodesIfNodeWasDelete(RBNode node){
		if (this.empty()){
			minNode = new RBNode(Integer.MAX_VALUE);
			maxNode = new RBNode(Integer.MIN_VALUE);
			return;
		}
		if (maxNode.getKey() == node.getKey())
			maxNode = node.FindPredecessor();
		if (minNode.getKey() == node.getKey())
			minNode = node.findSuccessor();
	}
   
   private int ColorChange_delete(RBNode node, int colorChanges) {
		if (node.isRoot())
			return colorChanges;
		
		if (node.getColor() == 'R'){
			node.setBlack();
			return (colorChanges + 1);
		}
		
		//cases 1 and 2 when all siblings kids are black
		RBNode sibling = node.getSibling();
		if (sibling.getColor() == 'B' && 
				sibling.left.getColor() == 'B' && 
				sibling.right.getColor() == 'B'){
			if (node.parent.getColor() == 'B'){
				sibling.setRed();
				return ColorChange_delete
						(node.parent, (colorChanges + 1));
			}
			else{	
				node.parent.setBlack();
				sibling.setRed();
				return (colorChanges + 2);
			}
		}
		
		if (node.isLeftChild() && sibling.getColor() == 'B'
				&& sibling.right.getColor() == 'R'){
			if (node.parent.getColor() == 'R'){
				sibling.setRed();
				colorChanges=colorChanges+1;
			}
			node.parent.setBlack();
			sibling.right.setBlack();
			colorChanges=colorChanges+2;
			node.parent.LeftRotation();
			return colorChanges;		
		}
		
		if (node.isRightChild() && 
				sibling.getColor() == 'B' &&
				sibling.left.getColor() == 'R'){
			if (node.parent.getColor() == 'R'){
				sibling.setRed();
				colorChanges=colorChanges+1;
			}
			node.parent.setBlack();
			sibling.left.setBlack();
			colorChanges=colorChanges+2;
			node.parent.RightRotation();
			return colorChanges;		
		}
		
		if (sibling.getColor() == 'B' && 
				sibling.left.getColor() == 'R'){
			sibling.left.setBlack();
			sibling.setRed();
			sibling.RightRotation();
			return ColorChange_delete
					(node, (colorChanges + 2));
		}
		
		if (sibling.getColor() == 'B' && 
				sibling.right.getColor() == 'R'){
			sibling.right.setBlack();
			sibling.setRed();
			sibling.LeftRotation();
			return ColorChange_delete
					(node, (colorChanges + 2));
		}
		
		
		if (sibling.getColor() == 'R'){
			node.parent.setRed();
			sibling.setBlack();
			if (node.isLeftChild())
				node.parent.LeftRotation();
			else
				node.parent.RightRotation();
			return ColorChange_delete
					(node, (colorChanges + 2));
		}
		
		else{
			System.out.println("None of the deletion cases match");
			return -1; //dummy return value
		}
	}


  /**
   * public class RBNode
   *
   * If you wish to implement classes other than RBTree
   * (for example RBNode), do it in this file, not in 
   * another file.
   * This is an example which can be deleted if no such classes are necessary.
   */
  public class RBNode{
	  
	  private String value;
	  private RBNode left, right, parent;
	  private int key;
	  private int nodeSize;
	  private char color; // B for black , R for red
	  private boolean NIL;
	  
	  // creates an NIL node
	  public RBNode() {
			this.NIL = true;
			this.left = this.right = null;
			this.setBlack();
			this.nodeSize=0;
			this.value = "NIL";
			this.key = -2;
		}
	  
	  public RBNode(int num, String name) {
			value = name;
			key = num;
			nodeSize=0;
			this.setRed();
			NIL = false;
			parent = null;
			this.addNILChildren();
		}
	  
	   
	  
	  public RBNode(int num) {
			key = num;
			value = "";
			nodeSize=0;
			parent = null;
			NIL = false;
			this.setRed();
			this.addNILChildren();
		}
	  public int getKey(){
		  return this.key;
	  }
	  public String getValue(){
		  return this.value;  
	  }
	  public RBNode getLeft(){
		  return this.left;
	  }
	  public RBNode getRight(){
		  return this.right;
	  }
	  public RBNode getParent(){
		  return this.parent;
	  }
	  public char getColor(){
		  return this.color;
	  }
	  public int getNodeSize(){
		  return this.nodeSize;
	  }
	  public void setBlack(){
			this.color = 'B';
		}
		
		public void setRed(){
			this.color = 'R';
		}
		public RBNode Uncle() {
			if ((this.isNILNode())||(this==null)){
				System.out.println(" no uncle for NIL or null node");
				return null;
			}
			if (this.parent.isRightChild())
				return this.parent.parent.left;
			
			if (this.parent.isLeftChild())
				return this.parent.parent.right;
			
			
			else{
				System.out.println("There's no uncle");
				return null;
			}
		}
		
		public void addNILChildren(){
			RBNode nilchild1 = new RBNode();	
			RBNode nilchild2 = new RBNode();	  
			this.addLeftChild(nilchild1);
			this.addRightChild(nilchild2);
		}
		
		public void addLeftChild(RBNode x) {
			this.left = x;
			if(!x.isNILNode())
				this.nodeSize=this.right.nodeSize+x.nodeSize+1;
			x.parent = this;
		}
		public void addRightChild(RBNode x) {
			this.right = x;
			if(!x.isNILNode())
				this.nodeSize=this.left.nodeSize+x.nodeSize+1;
			x.parent = this;
		}
		public boolean isNILNode(){
			if (this.NIL==true)
				return true;
			else
				return false;
		}
		public void setnodeSize(int sum){
			this.nodeSize=sum;
		}
		public boolean isRoot(){
			if(this.parent==root)
				return true;
			else
				return false;
		}
		public boolean isLeftChild() {
			if(this.parent.left==this)
				return true;
			else
				return false;
		}

		public boolean isRightChild() {
			if (this.parent.right == this)
				return true;
			else
				return false;
		}
		
		private boolean hasTwoBlackChildren(){
			if((this.isNILNode()==true)||
					(this==null)){
				return false;
			}
			else if((this.left.color=='B')&&
					(this.right.color=='B')){
				return true;
			}
			else
				return false;
		}
		
		private boolean hasTwoRedChildren(){
			if((this.isNILNode()==true)||
					(this==null)){
				return false;
			}
			else if((this.left.color=='R')&&
					(this.right.color=='R')){
				return true;
			}
			else
				return false;	
		}
		
		public int[] recPotential(int[] array){
			
			if(this.isNILNode()==true){
				return array;
				}
			else if (this.color=='R'){
				
				this.left.recPotential(array);
				this.right.recPotential(array);
			}
			else{
				if (this.hasTwoBlackChildren())
					array[0]++;
				else if (this.hasTwoRedChildren())
					array[1]++;
				if (this.left.isNILNode()){
					this.right.recPotential(array);
				}
				else if (this.right.isNILNode()){
					this.left.recPotential(array);
				}
				else if ((this.right.isNILNode())&&
						(this.left.isNILNode())){
					return array;
					}
				else
					{this.left.recPotential(array);
				this.right.recPotential(array);
					}
				
			}
			return array;
		}
		public RBNode findSuccessor(){
			RBNode node = this;
			if ((!node.right.isNILNode())&&
					(!(node.right==null))){
				node = node.right;
				while ((!(node.left==null))&&
						(!(node.left.isNILNode()))){
					node = node.left;
				}
				return node; 
			}
			else{
				if (this.isRoot())
					return null;
				else{
					while(node.isRightChild()){
						node = node.parent;
					}
					return node.parent;
				}
			}
		}
		//performs a left rotation
		public boolean LeftRotation(){
			if ((this.right.isNILNode())||
					(this.right==null)){
				return false;
			}
			else{
				RBNode x = this.right;
				RBNode par=this.parent;
				int par_Size=par.nodeSize;
				int New_Size=this.left.nodeSize+x.left.nodeSize+1;
				int x_NewSize=this.nodeSize;
				this.Transplant(x);
				this.addRightChild(x.left);
				x.addLeftChild(this);
				x.setnodeSize(x_NewSize);
				this.setnodeSize(New_Size);
				par.setnodeSize(par_Size);
				return true;
			}
		}
		
		public boolean RightRotation(){
			if ((this.left.isNILNode())||(this.left==null)){
				return false;
			}
			else{
				RBNode x = this.left;
				RBNode par=this.parent;
				int par_size=par.nodeSize;
				int New_Size=this.right.nodeSize+x.right.nodeSize+1;
				int  x_NewSize=this.nodeSize;
				this.Transplant(x);
				this.addLeftChild(x.right);
				x.addRightChild(this);
				x.setnodeSize( x_NewSize);
				this.setnodeSize(New_Size);
				par.setnodeSize(par_size);
				return true;
			}
		}
		
		public void Transplant(RBNode x) {
			int parent_size=this.parent.nodeSize;
			if ((this.isNILNode())||(this==null))
				return;
			if (this.isLeftChild()) 
				this.parent.addLeftChild(x);
			else 
				this.parent.addRightChild(x);
			
			this.parent.setnodeSize(parent_size);
		}
		
		public RBNode FindPredecessor(){
			RBNode tempNode = this;
			if ((!tempNode.left.isNILNode())&&
			(tempNode.left!=null)){
				tempNode = tempNode.left;
				while ((!tempNode.right.isNILNode())&&
				(tempNode.right!=null)){
					tempNode = tempNode.right;
				}
				return tempNode;
			}
			else{
				if (this.isRoot())
					return null;
				else{
					while(tempNode.isLeftChild()){
						tempNode = tempNode.parent;
					}
					return tempNode.parent;
				}
			}
		}
		
		public RBNode getSibling() {
			
			if (this.isLeftChild())
				return this.parent.right;
			if (this.isRightChild())
				return this.parent.left;
			else{
				System.out.println("sibling was not found");
				return null;
			}
		}
		
		public void replaceNodeInfo(RBNode other){
			this.key = other.getKey();
			this.value = other.getValue();
		}	
  }
  
}
 

