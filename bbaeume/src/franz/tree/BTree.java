package franz.tree;

import java.util.List;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

public class BTree {

	private int ordnung; 	// Knoten hat
							// mindestens ordnung/2 s�hne
							// maximal ordnung s�hne
							// maximal ordnung-1 schl�ssel
							// mit k s�hne speichert k-1 schl�ssel
	private int numberOfTreeEntrys = 0;
	private int minEntrys;
	private BNode root = null;

	public BTree(int ordnung) {
		this.ordnung = ordnung;
		this.minEntrys = (int) Math.ceil(ordnung/2) - 1;
	}

	public NodeEntry searchKey(int key) {
		return searchKey(root, key);
	}
	
	private NodeEntry searchKey(BNode node, int key) {
		if(node == null) 
			return null;
		for(int i = 0; i < node.getNumberOfEntrys(); i++) {
			if(key == node.getEntry(i).getKey()) return node.getEntry(i); 
			if(key < node.getEntry(i).getKey()) return searchKey(node.getChild(i), key);
			if((i+1) == node.getNumberOfEntrys()) return searchKey(node.getChild(i+1), key);
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
		
		insertEntry(root, entry);
		
		if (root.getNumberOfEntrys() > ordnung-1) {
			BNode newRoot = new BNode();
			splitTree(newRoot, 0, root);
			root = newRoot;
		}
		numberOfTreeEntrys++;
		return true;
	}
	
	private void insertEntry(BNode node, NodeEntry entry) {
		if(node.getNumberOfChilds() == 0) {
			// BNode ist ein Blatt		
			for(int i = 0; i < ordnung; i++) {
				if(node.getNumberOfEntrys() > i) {												// wenn der Eintrag i existiert
					if(entry.getKey() < node.getEntry(i).getKey()) {
						node.addEntry(i, entry);
						break;
					}
				} else {
					node.addEntry(i, entry);
					break;
				}
			}
			// Fertig mit Eingefuegen im Blatt
		} else {
			// BNode ist ein Knoten
			int childPositionToInsert = -1;
			
			for (int i = 0; i < node.getNumberOfEntrys(); i++) {
				if(entry.getKey() < node.getEntry(i).getKey()) {
					// Entry ist kleiner als der Schluessel an Position i
					childPositionToInsert = i;					
					break;
				} else if(i == node.getNumberOfEntrys() - 1) {
					// Entry ist groe�er als alle Schluessel in dem Knoten
					childPositionToInsert = i+1;
				}
			}
			// Einfuegen in UnterKnoten
			insertEntry(node.getChild(childPositionToInsert), entry);
		
			// Unterknoten voll?
			if(node.getChild(childPositionToInsert).getNumberOfEntrys() > ordnung-1) {
				splitTree(node, childPositionToInsert, node.getChild(childPositionToInsert));
			}
			
		}	
	}

	private void splitTree(BNode node, int childPositionToSplit, BNode subNodeToSplit) {
		int middle = subNodeToSplit.getNumberOfEntrys() / 2;
		
		node.addEntry(childPositionToSplit, subNodeToSplit.removeEntry(middle));
		
		BNode rightSubTree = new BNode();
		while(middle < subNodeToSplit.getNumberOfEntrys()) {
			rightSubTree.addChild(subNodeToSplit.removeChild(middle+1));
			rightSubTree.addEntry(subNodeToSplit.removeEntry(middle));
		}
		rightSubTree.addChild(subNodeToSplit.removeChild(subNodeToSplit.getNumberOfChilds() - 1));
		
		node.setChild(childPositionToSplit, subNodeToSplit);
		node.addChild(childPositionToSplit+1, rightSubTree);
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
	 * @return Entfernter Schluessel aus dem Knoten/Blatt
	 */
	private NodeEntry removeKey(BNode node, int key) {
		NodeEntry returnValue = null;
		int keyPosition = node.containsKey(key); 
		System.out.println("keyPosition: " + keyPosition);
		if(keyPosition >= 0) {							// Knoten enthaelt Schluessel
			
			if(node.getNumberOfChilds() == 0) {				// Knoten mit Schluessel ist ein Blatt
					returnValue = node.removeEntry(keyPosition);
			} else {											// Knoten mit Schluessel ist KEIN Blatt
				NodeEntry nextSmallestEntry = null;
				if(node.getEntry(keyPosition).hasHigherChild()) {
					nextSmallestEntry = getSmallestNextEntry(node.getEntry(keyPosition).getHigherChild());
				} else {
					nextSmallestEntry = getSmallestNextEntry(node.getEntry(keyPosition + 1).getLowerChild());
				}
				returnValue = node.setEntry(keyPosition, removeEntry(nextSmallestEntry.getKey()));
			}
			
		} else {												// Knoten enthaelt Schluessel NICHT
			// Naechsten Knoten herausfinden
			BNode nextNode = null;
			int nextNodePosition = 0;
			for(int i = 0; i < node.getNumberOfEntrys(); i++) {
				if(key < node.getEntry(i).getKey()) {
					nextNode = node.getChild(i);
					nextNodePosition = i;
					break;
				} else if(node.getChild(i+1) != null) {
					nextNode = node.getChild(i+1);
					nextNodePosition = i + 1;
					break;
				}
			}
			// naechster Knoten wurde ermittelt
			returnValue = removeKey(nextNode, key);
			
			if(nextNode.getNumberOfEntrys() < minEntrys) {
				if(nextNodePosition > 0 &&
						nextNode.getNumberOfEntrys() + node.getChild(nextNodePosition-1).getNumberOfEntrys() >= minEntrys * 2) {
					node.setEntry(nextNodePosition, mergeNodes(node.getChild(nextNodePosition-1), 
																		node.getChild(nextNodePosition), 
																		node.getEntry(nextNodePosition)));
					
				} else if(nextNodePosition < node.getNumberOfChilds() &&
						nextNode.getNumberOfEntrys() + node.getChild(nextNodePosition+1+1).getNumberOfChilds() >= minEntrys * 2) {
					node.setEntry(nextNodePosition, mergeNodes(node.getChild(nextNodePosition), 
																		node.getChild(nextNodePosition+1), 
																		node.getEntry(nextNodePosition)));
				}
			}
		}
		
		return returnValue;
	}
	
	private NodeEntry mergeNodes(BNode leftTree, BNode rightTree, NodeEntry node) {
		return null;
	}
	
	/**
	 * Methode zum Auffinden des vorherigen groesseren Schluessels im linken Teilbaum
	 * @param node
	 * @return
	 */
	private NodeEntry getGreatestPreviousEntry(BNode node) {
		if(node.getNumberOfChilds() > 0)
			return getGreatestPreviousEntry(node.getChild(node.getNumberOfEntrys()-1+1));
		else
			return node.getEntry(node.getNumberOfEntrys()-1);
	}
	
	/**
	 * Methode zum Auffinden des naechst kleineren Schluessels im rechten Teilbaum
	 * @param node
	 * @return
	 */
	private NodeEntry getSmallestNextEntry(BNode node) {
		if(node.getNumberOfChilds() > 0)
			return getSmallestNextEntry(node.getChild(0));
		else
			return node.getEntry(0);
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
				fillSpaces(sb, getWidth(node.getChild(i)));
				if (i == 0 && node.getNumberOfEntrys() == 1) { 									// nur ein Eintrag in Knoten
					sb.append(String.format("(%2s)", node.getEntry(i).getKey()));
				} else if (i == 0) { 															// ist erster Eintrag in Knoten
					sb.append(String.format("(%2s ", node.getEntry(i).getKey()));
				} else if (i != 0 && i + 1 == node.getNumberOfEntrys()) { 						// ist letzer Eintrag in Knoten
					sb.append(String.format(" %2s)", node.getEntry(i).getKey()));
				} else {
					sb.append(String.format(" %2s ", node.getEntry(i).getKey()));
				}
				if(i+1 == node.getNumberOfEntrys())
					fillSpaces(sb, getWidth(node.getChild(i+1)));
			} else {
				printLine(sb, node.getChild(i), depth - 1);
				fillSpaces(sb, 4);
				if(i+1 == node.getNumberOfEntrys())
					printLine(sb, node.getChild(i+1), depth - 1);
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
			if (node.getChild(i) != null)
				leftWidth += getWidth(node.getChild(i));
			//if (node.getChild(i+1) != null)
			if (i+1 == node.getNumberOfEntrys())
				rightWidth += getWidth(node.getChild(i+1));
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