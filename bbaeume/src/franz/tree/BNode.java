package franz.tree;

import java.util.LinkedList;
import java.util.List;

public class BNode {

	private int ordnung;
	
	private List<NodeEntry> entrys;
	
	public BNode(int ordnung) {
		this.ordnung = ordnung;
		entrys = new LinkedList<NodeEntry>();
	}
	
	public int getNumberOfChildNodes() {
		int size = 0;
		for(NodeEntry entry : entrys) {
			size += entry.getNumberOfChilds();
		}
		return size;
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
			if(key < node.getEntrys().get(i).getKey()) return searchEntry(node.getEntrys().get(i).getLowerChild(), key);
			if((i+1) == node.getNumberOfEntrys()) return searchEntry(node.getEntrys().get(i).getHigherChild(), key);
		}
		return null;
	}
	
	public NodeEntry insertEntryR(NodeEntry entry) {
		NodeEntry resultEntry = searchEntry(entry.getKey()); 
		if(resultEntry != null) return resultEntry;											// Schluessel schon vorhanden
		
		resultEntry = insertEntryR(this, entry);
		return resultEntry;
	}
	
	private NodeEntry insertEntryR(BNode node, NodeEntry entry) {
		NodeEntry overflowNode = null;
		if(node.getNumberOfChildNodes() == 0) {
			// BNode ist ein Blatt
					
			for(int i = 0; i < ordnung; i++) {
				if(node.getEntrys().size() > i) {												// wenn der Eintrag i existiert
					if(entry.getKey() < node.getEntrys().get(i).getKey()) {
						node.getEntrys().add(i, entry);
						break;
					}
				} else {
					node.getEntrys().add(i, entry);
					break;
				}
			}
			// Fertig mit eingefuegen
			
		} else {
			// BNode ist ein Knoten
			for (int i = 0; i < node.getNumberOfEntrys(); i++) {
				if(entry.getKey() < node.getEntrys().get(i).getKey()) {
					// Entry ist kleiner als der Schluessel an Position i
					overflowNode = insertEntryR(node.getEntrys().get(i).getLowerChild(), entry);
					
					if(overflowNode != null) {
						// das Einfuegen in den Kindknoten hat einen Eintrag zurueckgegeben, da der Kindknoten voll war
						node.getEntrys().get(i).setLowerChild(overflowNode.getHigherChild());
						overflowNode.setHigherChild(null);
						node.getEntrys().add(i, overflowNode);
						overflowNode = null;
					}
					
					break;
				} else if(node.getEntrys().get(i).hasHigherChild()) {
					// Entry ist groeßer als alle Schluessel in dem Knoten
					overflowNode = insertEntryR(node.getEntrys().get(i).getHigherChild(), entry);
					
					if(overflowNode != null) {
						// das Einfuegen in den Kindknoten hat einen Eintrag zurueckgegeben, da der Kindknoten voll war
						node.getEntrys().get(i).setHigherChild(null);
						node.getEntrys().add(overflowNode);
						overflowNode = null;
					}
				}
			}
		}
		// Knoten voll?
		if(node.getNumberOfEntrys() == ordnung) {											// Knoten voll -> teilen
			int middle = node.getEntrys().size() / 2;
			
			node.getEntrys().get(middle-1).setHigherChild(node.getEntrys().get(middle).getLowerChild());	// linken Teilbaum vervollstaendigen
			
			overflowNode = node.getEntrys().remove(middle);									// mittlerer Knoten wird entfernt 
						
			BNode higherSubTree = new BNode(ordnung);										// neuer rechter Teilbaum
			while(middle < node.getNumberOfEntrys()) {
				insertEntryR(higherSubTree, node.getEntrys().remove(middle));
			}
			
			overflowNode.setLowerChild(node);												// alter Knoten wird linker Teilbaum
			overflowNode.setHigherChild(higherSubTree);										// neuer Knoten wird rechter Teilbaum
			
		} else {
			overflowNode = null;
		}
		
		return overflowNode;
	}
	
	public BNode removeEntry(BNode node, NodeEntry entry) {
		return null;
	}
}
