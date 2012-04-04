package franz.tree;

import java.util.LinkedList;
import java.util.List;

public class BNode {

	private int ordnung;
	
	private List<NodeEntry> entrys;
	
	private NodeEntry vatherNodeEntry;
	
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
	private NodeEntry searchEntry(BNode node, int key) {
		if(node == null) 
			return null;
		for(int i = 0; i < node.getNumberOfEntrys(); i++) {
			if(key == node.getEntrys().get(i).getKey()) return node.getEntrys().get(i); 
			if(key < node.getEntrys().get(i).getKey()) return searchEntry(node.getEntrys().get(i).getLowerChild(), key);
			if((i+1) == node.getNumberOfEntrys()) return searchEntry(node.getEntrys().get(i).getHigherChild(), key);
		}
		return null;
	}
	
	public BNode insertEntry(NodeEntry entry) throws Exception {
		if(searchEntry(entry.getKey()) != null) 												// Schluessel schon vorhanden
			throw new Exception("Schluessel bereits vorhanden");
		
		NodeEntry resultEntry = insertEntry(this, entry);
		if(resultEntry != null) {
			// Neuer Wurzelknoten
			BNode newRoot = new BNode(ordnung);
			newRoot.insertEntry(resultEntry);
			return newRoot;
		}
		return this;
	}
	
	private NodeEntry insertEntry(BNode node, NodeEntry entry) {
		NodeEntry overflowEntry = null;
		if(node.getNumberOfChildNodes() == 0) {
			// BNode ist ein Blatt
					
			for(int i = 0; i < ordnung; i++) {
				if(node.getEntrys().size() > i) {												// wenn der Eintrag i existiert
					if(entry.getKey() < node.getEntrys().get(i).getKey()) {
						entry.setNode(node);
						node.getEntrys().add(i, entry);
						break;
					}
				} else {
					entry.setNode(node);
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
					overflowEntry = insertEntry(node.getEntrys().get(i).getLowerChild(), entry);
					
					if(overflowEntry != null) {
						// das Einfuegen in den Kindknoten hat einen Eintrag zurueckgegeben, da der Kindknoten voll war
						node.getEntrys().get(i).setLowerChild(overflowEntry.getHigherChild());
						overflowEntry.setHigherChild(null);
						overflowEntry.setNode(node);
						node.getEntrys().add(i, overflowEntry);
						overflowEntry = null;
					}
					
					break;
				} else if(node.getEntrys().get(i).hasHigherChild()) {
					// Entry ist groeßer als alle Schluessel in dem Knoten
					overflowEntry = insertEntry(node.getEntrys().get(i).getHigherChild(), entry);
					
					if(overflowEntry != null) {
						// das Einfuegen in den Kindknoten hat einen Eintrag zurueckgegeben, da der Kindknoten voll war
						node.getEntrys().get(i).setHigherChild(null);
						overflowEntry.setNode(node);
						node.getEntrys().add(overflowEntry);
						overflowEntry = null;
					}
					break;
				}
			}
		}
		// Knoten voll?
		if(node.getNumberOfEntrys() == ordnung) {											// Knoten voll -> teilen
			int middle = node.getNumberOfEntrys() / 2;
			
			node.getEntrys().get(middle-1).setHigherChild(node.getEntrys().get(middle).getLowerChild());	// linken Teilbaum vervollstaendigen
			
			overflowEntry = node.getEntrys().remove(middle);								// mittlerer Knoten wird entfernt 
			overflowEntry.setNode(null);
			BNode higherSubTree = new BNode(ordnung);										// neuer rechter Teilbaum
			while(middle < node.getNumberOfEntrys()) {
				insertEntry(higherSubTree, node.getEntrys().remove(middle));
			}
			
			overflowEntry.setLowerChild(node);												// alter Knoten wird linker Teilbaum
			overflowEntry.setHigherChild(higherSubTree);										// neuer Knoten wird rechter Teilbaum
			
		} else {
			overflowEntry = null;
		}
		
		return overflowEntry;
	}
	
	public BNode removeEntry(int key) throws Exception {
		NodeEntry searchResult = searchEntry(key); 
		if(searchResult == null) 												// Schluessel nicht vorhanden
			throw new Exception("Schluessel nicht vorhanden");
		
		NodeEntry resultEntry = null;
		/*
		if(searchResult.getNode().getNumberOfChildNodes() == 0) {				// Schluessel ist in einem Blatt
			resultEntry = removeEntryFromLeaf(
		} else {																// Schluessel ist in einem Knoten
			
		}
		*/
		resultEntry = removeEntry(this, key);
		if(resultEntry != null) {
			// Neuer Wurzelknoten
			BNode newRoot = new BNode(ordnung);
			newRoot.insertEntry(resultEntry);
			return newRoot;
		}
		return this;
	}
	
	/**
	 * Die Funktion removeEntry setzt voraus, dass der Key in dem Baum vorhanden ist!
	 * @param node
	 * @param entry
	 * @return
	 */
	private NodeEntry removeEntry(BNode node, int key) {
		NodeEntry resultEntry = null;
		if(node.getNumberOfChildNodes() == 0) {										// Knoten ist ein Blatt, somit muss der Schluessel hier vorhanden sein
			for(int i = 0; i < node.getNumberOfEntrys(); i++) {
				if(key == node.getEntrys().get(i).getKey()) {
					resultEntry = node.getEntrys().remove(i);
					break;
				}
			}
			if(node.getNumberOfEntrys() >= Math.floor(ordnung/2)) {					// wenn noch genuegend Schluessel im Blatt sind
				resultEntry = null;
			} 
		} else {
			int pos = node.containsKey(key); 
			if(pos < 0) {								// Schluessel nicht im Knoten vorhanden
				for(int i = 0; i < node.getNumberOfEntrys(); i++) {
					if(key < node.getEntrys().get(i).getKey()) {					// Schluessel ist im kleineren Teilbaum
						resultEntry = removeEntry(node.getEntrys().get(i).getLowerChild(), key);
					} else if(i == node.getNumberOfEntrys() - 1) {					// Schluessel ist im groesserem Teilbaum
						resultEntry = removeEntry(node.getEntrys().get(i).getHigherChild(), key);
					}
				}
			} else {																// Schluessel ist im Knoten vorhanden
				// Suchen des kleinsten naechsten Knoden im rechten Teilbaum
				NodeEntry nextSmallestEntry = null;
				if(node.getEntrys().get(pos).hasHigherChild()) { 
					nextSmallestEntry = removeEntry(node, searchSmallestNextKey(node.getEntrys().get(pos).getHigherChild()));
				} else {
					nextSmallestEntry = removeEntry(node, searchSmallestNextKey(node.getEntrys().get(pos+1).getLowerChild()));
				}
				nextSmallestEntry.setHigherChild(node.getEntrys().get(pos).getLowerChild());
				nextSmallestEntry.setHigherChild(node.getEntrys().get(pos).getHigherChild());
				node.getEntrys().set(pos, nextSmallestEntry);
			}
		}
		return resultEntry;
	}
	
	/**
	 * Methode zum Auffinden des naechst kleineren Schluessels im rechten Teilbaum
	 * @param node
	 * @return
	 */
	private int searchSmallestNextKey(BNode node) {
		if(node.getNumberOfChildNodes() > 0)
			return searchSmallestNextKey(node.getEntrys().get(0).getLowerChild());
		else
			return node.getEntrys().get(0).getKey();
	}
	
	/**
	 * Gibt die Position des Keys in dem Knoten zur&uuml;ck. Falls der Key nicht vorhanden ist, wird eine -1 zur&uuml;ck gegeben
	 * @param key
	 * @return
	 */
	private int containsKey(int key) {
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
