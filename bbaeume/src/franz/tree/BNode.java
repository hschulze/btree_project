package franz.tree;

import java.util.ArrayList;
import java.util.List;



public class BNode {

	private List<NodeEntry> entrys;
	
	private List<BNode> childs;
	
	public BNode() {
		entrys = new ArrayList<NodeEntry>();
		childs = new ArrayList<BNode>();
	}
	
	public void addEntry(NodeEntry element) {
		if(element != null)
			entrys.add(element);
	}
	
	public void addEntry(int index, NodeEntry element) {
		if(element != null)
			entrys.add(index, element);
	}
	
	public NodeEntry setEntry(int index, NodeEntry element) {
		return entrys.set(index, element);
	}
	
	public NodeEntry removeEntry(int index) {
		if(index > getNumberOfEntrys() - 1) return null;
		return entrys.remove(index);
	}
	/**
	 * 
	 * @param index
	 * @return Key-Value an der Stelle des Index <i>index</i>
	 */
	public int getKey(int index) {
		if(index > getNumberOfEntrys()-1 || index == -1) return -1;
		return entrys.get(index).getKey();
	}
	
	public NodeEntry getEntry(int index) {
		if(index > getNumberOfEntrys()-1 || index == -1) return null;
		return entrys.get(index);
	}
	
	public BNode setChild(int index, BNode element) {
		if(index >= getNumberOfChilds()) {
			childs.add(element);
			return null;
		}
		return childs.set(index, element);
	}
	
	public void addChild(BNode element) {
		if(element != null)
			childs.add(element);
	}
	
	public void addChild(int index, BNode element) {
		if(element != null)
			childs.add(index, element);
	}
	
	public BNode removeChild(int index) {
		if(index > getNumberOfChilds() - 1 || index == -1) return null;
		return childs.remove(index);
	}
	
	public BNode getChild(int index) {
		if(index > getNumberOfChilds() - 1 || index == -1) return null;
		return childs.get(index);
	}
	
	public int getNextChildPositionForKey(int key) {
		
		if(containsKey(key) >= 0) 
			return -1;
		
		int nextChildPosition = -1;
		for(int i = 0; i < getNumberOfEntrys(); i++) {
			if(key < getKey(i)) {
				nextChildPosition = i;
				break;
			} else if(i == getNumberOfEntrys() - 1) {
				nextChildPosition = i+1;
			}
		}
		return nextChildPosition;
	}
	
	public int getNumberOfChilds() {
		int size = 0;
		for(int i = 0; i < childs.size(); i++) {
			if(childs.get(i) != null) size++;
		}
		return size;
	}
	
	public boolean isLeaf() {
		if(getNumberOfChilds() == 0) return true;
		return false;
	}
	
	public boolean isNode() {
		return !isLeaf();
	}
	
//	public List<NodeEntry> getEntrys() {
//		return entrys;
//	}
	
	public int getNumberOfEntrys() {
		for(int i = 0; i < entrys.size(); i++) {
			if(entrys.get(i) == null) entrys.remove(i);
		}
		return entrys.size();
	}
	
	/**
	 * Gibt die Position des Keys in dem Knoten zur&uuml;ck. Falls der Key nicht vorhanden ist, wird eine -1 zur&uuml;ck gegeben
	 * @param key
	 * @return
	 */
	public int containsKey(int key) {
		int position = -1;
		for(int i = 0; i < getNumberOfEntrys(); i++) {
			if(entrys.get(i).getKey() == key) {
				position = i;
				break;
			}
		}
		return position;
	}
}
