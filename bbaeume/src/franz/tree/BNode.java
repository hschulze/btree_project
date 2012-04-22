package franz.tree;

import java.util.LinkedList;
import java.util.List;

public class BNode {

	
	private List<NodeEntry> entrys;
	
	public BNode() {
		entrys = new LinkedList<NodeEntry>();
	}
	
	public BNode getChildNode(int i) {
		if(i > entrys.size()) return entrys.get(i-1).getHigherChild();
		return entrys.get(i).getLowerChild();
	}
	
	public int getNumberOfChildNodes() {
		int size = 0;
		for(NodeEntry entry : entrys) {
			size += entry.getNumberOfChilds();
		}
		return size;
	}
	
	public boolean isLeaf() {
		if(getNumberOfChildNodes() == 0) return true;
		return false;
	}
	
	public boolean isNode() {
		return !isLeaf();
	}
	
	public List<NodeEntry> getEntrys() {
		return entrys;
	}
	
	public int getNumberOfEntrys() {
		return entrys.size();
	}
	
	/**
	 * Gibt die Position des Keys in dem Knoten zur&uuml;ck. Falls der Key nicht vorhanden ist, wird eine -1 zur&uuml;ck gegeben
	 * @param key
	 * @return
	 */
	public int containsKey(int key) {
		int position = -1;
		for(int i = 0; i < entrys.size(); i++) {
			if(entrys.get(i).getKey() == key) {
				position = i;
				break;
			}
		}
		return position;
	}
}
