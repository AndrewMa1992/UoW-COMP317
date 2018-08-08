// Jason Hayman 1293913
// Yunhao Fu 1255469

import java.util.ArrayList;
import java.util.HashMap;

class ByteTrie{
	private TrieNode root;
	private TrieNode blankNode;
	private ArrayList<TrieNode> nodeIndex;
	
	//Initializes a ByteTrie
	public ByteTrie(){
		root = new TrieNode();
		nodeIndex = new ArrayList<TrieNode>();
		
		// Create the initial 256 byte option nodes
		byte b = -128;
		for(int i = -128; i<=127 ; i++){
			add(b,root);
			b++;
		}
	}
	
	//Add the Byte as a child to the given node
	public void add(Byte word, TrieNode parent) {
		TrieNode newNode = new TrieNode(word, parent);
		parent.children.put(word, newNode);
		nodeIndex.add(newNode);
	}
	
	//Adds a blank node for deccoding
	public void addBlank(TrieNode newParent){
		// Checks if there is a blank node that hasnt been set yet
		if(blankNode != null && blankNode.getByte() == null){
			//If so get the content for the node from the headNode of the new node to be created
			TrieNode lastBlankParent = blankNode.getParent();
			Byte content = getHeadNode(newParent).getByte();
			blankNode.setByte(content);
			lastBlankParent.children.put(content, blankNode);
		}
		// Create the new blankNode and add to the nodeIndex
		blankNode = new TrieNode(newParent);
		nodeIndex.add(blankNode);
	}
	
	// Returns the child node that contains this key or null if doesn't exist
	public TrieNode getChild(Byte word, TrieNode parent){
		return parent.getChildren().get(word);
	}
	
	// Returns the node of index i
	public TrieNode findIndex(int i){
		TrieNode foundNode = nodeIndex.get(i);
		
		if(blankNode != null && blankNode.getByte() == null){
			TrieNode lastBlankParent = blankNode.getParent();
			Byte content = getHeadNode(foundNode).getByte();
			blankNode.setByte(content);
			lastBlankParent.children.put(content, blankNode);
		}
		return foundNode;
	}
	
	// Returns the index of the node
	public int findNode(TrieNode node){
		return nodeIndex.indexOf(node);
	}
	
	//Returns an array of the bytes down to this node
	public byte[] getContent(TrieNode node){
		//Create a Byte arraylist from headnode down to this node
		ArrayList<Byte> wordList = new ArrayList<Byte>();
		while(node != root){
			wordList.add(node.getByte());
			node = node.getParent();
		}
		//Convert the Byte arraylist into a Byte array and then into a byte array
		Byte[] wordsByte = wordList.toArray(new Byte[wordList.size()]);
		byte[] words = new byte[wordsByte.length];
		for(int i =0; i <wordsByte.length; i++){
			words[i] = wordsByte[i].byteValue();
		}
		return words;
	}
	
	// Returns the root
	public TrieNode getRoot(){
		return root;
	}
	
	// Returns the size of the dictionary
	public int getSize(){
		return nodeIndex.size();
	}
	
	//Priavte method to get the node highest up the tree from this node that isn't the root
	private TrieNode getHeadNode(TrieNode node){
		TrieNode parent = node.getParent();
		//Loop while the parent is not the root node
		while(parent != root){
			node = parent;
			parent = node.getParent();
		}
		return node;
	}
	
	//Node subclass
	public class TrieNode {
		private HashMap<Byte, TrieNode> children;
		private TrieNode parent;
		private Byte content;
		
		public TrieNode(){
			parent = null;
			content = null;
			children = new HashMap(256);
		}
		
		public TrieNode(TrieNode parent){
			content = null;
			this.parent = parent;
			children = new HashMap(0);
		}
		
		public TrieNode(Byte content, TrieNode parent){
			this.content = content;
			this.parent = parent;
			children = new HashMap(0);
		}
		
		public Byte getByte(){
			return content;
		}
		
		public TrieNode getParent(){
			return parent;
		}
		
		public HashMap<Byte, TrieNode> getChildren(){
			return children;
		}
		
		public void setByte(Byte content){
			this.content = content;
		}
	}
}