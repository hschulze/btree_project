package franz.tree;

import java.util.List;

public class BTree {

	private int ordnung; 	// Knoten hat
							// mindestens ordnung/2 söhne
							// maximal ordnung söhne
							// maximal ordnung-1 schlüssel
							// mit k söhne speichert k-1 schlüssel
	private int numberOfTreeEntrys = 0;
	private BNode root = null;

	public BTree(int ordnung) {
		this.ordnung = ordnung;
	}

	public NodeEntry searchKey(int key) {
		return searchKey(root, key);
	}
	
	private NodeEntry searchKey(BNode node, int key) {
		if(node == null) 
			return null;
		for(int i = 0; i < node.getNumberOfEntrys(); i++) {
			if(key == node.getEntrys().get(i).getKey()) return node.getEntrys().get(i); 
			if(key < node.getEntrys().get(i).getKey()) return searchKey(node.getEntrys().get(i).getLowerChild(), key);
			if((i+1) == node.getNumberOfEntrys()) return searchKey(node.getEntrys().get(i).getHigherChild(), key);
		}
		return null;
	}

	/**
	 * 
	 * @param key
	 * @return true, if key is inserted successful
	 */
	public boolean insertEntry(NodeEntry entry) {
		if (root == null) {							// Noch kein Baum vorhanden
			root = new BNode();
		}
		if(searchKey(entry.getKey()) != null) {	// Schluessel schon vorhanden
			return false;
		}
		
		NodeEntry result = insertEntry(root, entry);
		
		if (result != null) {
			root = new BNode();
			insertEntry(root, result);
		}
		numberOfTreeEntrys++;
		return true;
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
			BNode higherSubTree = new BNode();										// neuer rechter Teilbaum
			while(middle < node.getNumberOfEntrys()) {
				insertEntry(higherSubTree, node.getEntrys().remove(middle));
			}
			
			overflowEntry.setLowerChild(node);												// alter Knoten wird linker Teilbaum
			overflowEntry.setHigherChild(higherSubTree);									// neuer Knoten wird rechter Teilbaum
			
		} else {
			overflowEntry = null;
		}
		
		return overflowEntry;
	}

	/**
	 * 
	 * @param key
	 * @return true, if key is removed successful
	 */
	public NodeEntry removeEntry(int key) {
		if (searchKey(key) == null) {
			return null;
		}
		NodeEntry result = removeKey(root, key);
		
		numberOfTreeEntrys--;
		return result;
	}
	/**
	 * Funktion geht davon aus, dass node oder seine Unterbaeume den Schluessel enthalten
	 * @param node
	 * @param key
	 * @return
	 */
	private NodeEntry removeKey(BNode node, int key) {
		NodeEntry returnValue = null;
		if(node.containsKey(key) > 0) {							// Knoten enthaelt Schluessel
			
			if(node.getNumberOfChildNodes() > 0) {				// Knoten mit Schluessel ist ein Blatt
				for(int i = 0; i < node.getNumberOfEntrys(); i++) {
					if(node.getEntrys().get(i).getKey() == key) {
						returnValue = node.getEntrys().remove(i);
						break;
					}
				}
			} else {											// Knoten mit Schluessel ist KEIN Blatt
				for(int i = 0; i < node.getNumberOfEntrys(); i++) {
					if(node.getEntrys().get(i).getKey() == key) {
						
						NodeEntry previousGreatestEntry = getGreatestPreviousEntry(node.getEntrys().get(i).getLowerChild());
						NodeEntry nextSmallestEntry = null;
						if(node.getEntrys().get(i).hasHigherChild()) {
							nextSmallestEntry = getSmallestNextEntry(node.getEntrys().get(i).getHigherChild());
						} else {
							nextSmallestEntry = getSmallestNextEntry(node.getEntrys().get(i + 1).getLowerChild());
						}
						int minEntrys = (int) Math.ceil(ordnung/2) - 1;
						
						if(previousGreatestEntry.getNode().getNumberOfEntrys() == minEntrys && 
								nextSmallestEntry.getNode().getNumberOfEntrys() == minEntrys) {
							vershmelzen der teilbaeume
						} else if(previousGreatestEntry.getNode().getNumberOfEntrys() > nextSmallestEntry.getNode().getNumberOfEntrys()) {
							returnValue = node.getEntrys().set(i, removeEntry(previousGreatestEntry.getKey()));
						} else {
							returnValue = node.getEntrys().set(i, removeEntry(nextSmallestEntry.getKey()));
						}
						break;
					}
				}
			}
			
		} else {												// Knoten enthaelt Schluessel NICHT
			// Naechsten Knoten herausfinden
			BNode nextNode = null;
			for(int i = 0; i < node.getNumberOfEntrys(); i++) {
				if(key < node.getEntrys().get(i).getKey()) {
					nextNode = node.getEntrys().get(i).getLowerChild();
					break;
				} else if(node.getEntrys().get(i).hasHigherChild()) {
					nextNode = node.getEntrys().get(i).getHigherChild();
					break;
				}
			}
			
			
		}
		
		return returnValue;
	}
	
	/**
	 * Methode zum Auffinden des vorherigen groesseren Schluessels im linken Teilbaum
	 * @param node
	 * @return
	 */
	private NodeEntry getGreatestPreviousEntry(BNode node) {
		if(node.getNumberOfChildNodes() > 0)
			return getGreatestPreviousEntry(node.getEntrys().get(node.getNumberOfEntrys()-1).getHigherChild());
		else
			return node.getEntrys().get(node.getNumberOfEntrys()-1);
	}
	
	/**
	 * Methode zum Auffinden des naechst kleineren Schluessels im rechten Teilbaum
	 * @param node
	 * @return
	 */
	private NodeEntry getSmallestNextEntry(BNode node) {
		if(node.getNumberOfChildNodes() > 0)
			return getSmallestNextEntry(node.getEntrys().get(0).getLowerChild());
		else
			return node.getEntrys().get(0);
	}

	public void showTree() {
		System.out.println("Ausgabe des Baums:");
		
		int maxHeight = getMaxHeight();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getWidth(root) + 24; i++) {
			sb.append('#');
		}
		String border = sb.toString();
		System.out.println(border);
		sb = new StringBuilder();
		for (int i = 1; i <= maxHeight; ++i) {
			sb.append(String.format("# Tiefe %2d |", i));
			printLine(sb, root, i);
			sb.append(String.format("| Tiefe %2d #", i));
			sb.append("\n");
		}
		System.out.print(sb.toString());
		System.out.println(border);
	}

	private void printLine(StringBuilder sb, BNode node, int depth) {
		if (node == null)
			return;
		for (int i = 0; i < node.getNumberOfEntrys(); i++) {
			if (depth == 1) {
				fillSpaces(sb, getWidth(node.getEntrys().get(i).getLowerChild()));
				if (i == 0 && node.getEntrys().size() == 1) { 									// nur ein Eintrag in Knoten
					sb.append(String.format("(%2s)", node.getEntrys().get(i).getKey()));
				} else if (i == 0) { 															// ist erster Eintrag in Knoten
					sb.append(String.format("(%2s ", node.getEntrys().get(i).getKey()));
				} else if (i != 0 && i + 1 == node.getNumberOfEntrys()) { 						// ist letzer Eintrag in Knoten
					sb.append(String.format(" %2s)", node.getEntrys().get(i).getKey()));
				} else {
					sb.append(String.format(" %2s ", node.getEntrys().get(i).getKey()));
				}
				fillSpaces(sb, getWidth(node.getEntrys().get(i).getHigherChild()));
			} else {
				printLine(sb, node.getEntrys().get(i).getLowerChild(), depth - 1);
				fillSpaces(sb, 4);
				printLine(sb, node.getEntrys().get(i).getHigherChild(), depth - 1);
			}
		}
	}

	private void fillSpaces(StringBuilder sb, int count) {
		for (int i = 0; i < count; i++)
			sb.append(' ');
	}

	private int getWidth(BNode node) {
		if (node == null)
			return 0;
		int leftWidth = 0;
		int rightWidth = 0;
		for (int i = 0; i < node.getNumberOfEntrys(); i++) {
			if (node.getEntrys().get(i).hasLowerChild())
				leftWidth += getWidth(node.getEntrys().get(i).getLowerChild());
			if (node.getEntrys().get(i).hasHigherChild())
				rightWidth += getWidth(node.getEntrys().get(i).getHigherChild());
		}
		return leftWidth + (4 * node.getNumberOfEntrys()) + rightWidth;
	}

	public void showStat() {
		System.out.println("Ausgabe der Statistik:");
		System.out.println("MinHeight: " + getMinHeight());
		System.out.println("MaxHeight: " + getMaxHeight());
	}

	public int getMinHeight() {
		double minHeight = Math.ceil(Math.log(numberOfTreeEntrys + 1) / Math.log(ordnung) - 1);
		int result = (int) minHeight;
		return result;
	}

	public int getMaxHeight() {
		double maxHeight = Math.floor(Math.log((double) ((numberOfTreeEntrys + 1) / 2)) / Math.log(Math.ceil((double) ordnung / 2))) + 1;
		int result = (int) maxHeight;
		return result;
	}
}