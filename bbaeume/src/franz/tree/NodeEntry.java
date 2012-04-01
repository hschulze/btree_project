package franz.tree;

public class NodeEntry {

	private int key;
	private String data;
	
	private BNode node = null;
	
	private BNode lowerChild = null;
	
	private BNode higherChild = null;
	
	public NodeEntry(int key) {
		this(key, "");
	}
	
	public NodeEntry(int key, String data) {
		this.key = key;
		this.data = data;
	}
	
	public int getNumberOfChilds() {
		int childs = 0;
		if(lowerChild != null) childs++;
		if(higherChild != null) childs++;
		return childs;
			
	}
	
	public int getKey() {
		return key;
	}
	
	public String getData() {
		return data;
	}

	public BNode getLowerChild() {
		return lowerChild;
	}

	public void setLowerChild(BNode lowerChild) {
		this.lowerChild = lowerChild;
	}

	/**
	 * HigherChild ist nur gesetzt, wenn es das letzte/groesste Element in einem Knoten ist
	 * @param higherChild
	 */
	public BNode getHigherChild() {
		return higherChild;
	}
	/**
	 * HigherChild wird nur gesetzt, wenn es das letzte/groesste Element in einem Knoten ist
	 * @param higherChild
	 */
	public void setHigherChild(BNode higherChild) {
		this.higherChild = higherChild;
	}
	/**
	 * HigherChild ist nur gesetzt, wenn es das letzte/groesste Element in einem Knoten ist
	 * @param higherChild
	 */
	public boolean hasHigherChild() {
		if(higherChild != null) return true;
		return false;
	}
	
	public BNode getNode() {
		return node;
	}

	public void setNode(BNode node) {
		this.node = node;
	}
	
}
