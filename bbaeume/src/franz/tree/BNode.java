package franz.tree;

import java.util.LinkedList;
import java.util.List;

public class BNode {

	private int ordnung;
	
	private List<NodeEntry> entrys;
	private List<BNode> childNodes;
	
	public BNode(int ordnung) {
		this.ordnung = ordnung;
		entrys = new LinkedList<NodeEntry>();
		childNodes = new LinkedList<BNode>();
	}
	
	public List<BNode> getChildNodes() {
		return childNodes;
	}
	
	public int getNumberOfChildNodes() {
		return childNodes.size();
	}
	
	public List<NodeEntry> getEntrys() {
		return entrys;
	}
	
	public int getNumberOfEntrys() {
		return entrys.size();
	}
	
	public NodeEntry searchEntry(int key) {
		return searchEntry(this, key);
	}
	public NodeEntry searchEntry(BNode node, int key) {
		if(node == null) 
			return null;
		for(int i = 0; i < node.getNumberOfEntrys(); i++) {
			if(key == node.getEntrys().get(i).getKey()) return node.getEntrys().get(i); 
			if(key < node.getEntrys().get(i).getKey()) return searchEntry(node.getChildNodes().get(i), key);
			if((i+1) == node.getNumberOfEntrys()) return searchEntry(node.getChildNodes().get(i+1), key);
		}
		return null;
	}
	
	public NodeEntry insertEntryR(NodeEntry entry) {
		NodeEntry searchResult = searchEntry(entry.getKey()); 
		if(searchResult != null) return searchResult;											// Schluessel schon vorhanden
		return insertEntryR(this, entry);
	}
	
	private NodeEntry insertEntryR(BNode node, NodeEntry entry) {
		NodeEntry resultValue = null;
		if(node.getNumberOfChildNodes() == 0) {
			// BNode ist ein Blatt
					
			for(int i = 0; i < ordnung - 1; i++) {
				if(node.getEntrys().get(i) != null) {												// wenn der Eintrag i existiert
					if(entry.getKey() < node.getEntrys().get(i).getKey()) {
						node.getEntrys().add(i, entry);
						break;
					}
				} else {
					node.getEntrys().add(i, entry);
					break;
				}
			}
			// Eingefuegt
			if(node.getNumberOfEntrys() == ordnung) {											// Knoten voll
				node.getEntrys()
			} else {
				return null;
			}
		} else {
			// BNode ist ein Knoten
			
		}
		
		return node;
	}
	
	public BNode removeEntry(BNode node, NodeEntry entry) {
		return null;
	}
}
