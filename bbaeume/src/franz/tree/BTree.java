package franz.tree;

import java.util.List;

import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

public class BTree {

	private int ordnung; 	// Knoten hat
							// mindestens ordnung/2 söhne
							// maximal ordnung söhne
							// maximal ordnung-1 schlüssel
							// mit k söhne speichert k-1 schlüssel
	private int numberOfTreeEntrys = 0;
	private int minEntrys;
	private int middle;
	private BNode root = null;

	public BTree(int ordnung) {
		this.ordnung = ordnung;
		this.minEntrys = (int) Math.ceil(ordnung/2);	// -1
		this.middle = ordnung / 2;
	}

	public NodeEntry searchKey(int key) {
		return searchKey(root, key);
	}
	
	private NodeEntry searchKey(BNode node, int key) {
		if(node == null) 
			return null;
		for(int i = 0; i < node.getNumberOfEntrys(); i++) {
			if(key == node.getKey(i)) return node.getEntry(i); 
			if(key < node.getKey(i)) return searchKey(node.getChild(i), key);
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
					if(entry.getKey() < node.getKey(i)) {
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
				if(entry.getKey() < node.getKey(i)) {
					// Entry ist kleiner als der Schluessel an Position i
					childPositionToInsert = i;					
					break;
				} else if(i == node.getNumberOfEntrys() - 1) {
					// Entry ist groeßer als alle Schluessel in dem Knoten
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
		//System.out.println("NODE: " + node.getEntry(0).getKey());
		int keyEntryPosition = node.containsKey(key);
		//System.out.println("keyEntryPosition: " + keyEntryPosition);
		if(keyEntryPosition < 0) {										// Knoten enthaelt Key NICHT
			int nextChildPosition = -1;
			for(int i = 0; i < node.getNumberOfEntrys(); i++) {
				if(key < node.getKey(i)) {
					nextChildPosition = i;
					break;
				} else if(i+1 == node.getNumberOfEntrys()) {
					nextChildPosition = i+1;
				}
				//System.out.println(i+":"+node.getNumberOfEntrys()+":"+nextChildPosition+":"+node.getEntry(i).getKey()+":"+key);
			}
			//System.out.println(nextChildPosition);
			if(node.getChild(nextChildPosition).containsKey(key) >= 0 && node.getChild(nextChildPosition).getNumberOfEntrys() <= minEntrys) {		// naechster Knoten ist zu leer
				if(nextChildPosition > 0) {
					mergeNodes(node.getChild(nextChildPosition-1), node.getChild(nextChildPosition), node, nextChildPosition);	// merge mit linkem Teilbaum
				} else {
					mergeNodes(node.getChild(nextChildPosition), node.getChild(nextChildPosition+1), node, nextChildPosition);	// merge mit rechtem Teilbaum
				}
			}
			// !!! Aufpassen, dass der gesuchte Key nicht nach oben kommt oder jetzt im anderen Knoten ist !!!
			int keyPosition = node.containsKey(key);
			if(keyPosition >= 0) {
				if( getGreatestPreviousNode(node.getChild(keyPosition)).getNumberOfEntrys() > getSmallestNextNode(node.getChild(keyPosition+1)).getNumberOfEntrys() ) {
					returnValue = node.setEntry(keyPosition, removeKey(node.getChild(keyPosition), getGreatestPreviousNode(node.getChild(keyPosition)).getKey(getGreatestPreviousNode(node.getChild(keyPosition)).getNumberOfEntrys()-1))); 		// Eintrag wird durch naechst kleineren ersetzt
				} else {
					returnValue = node.setEntry(keyPosition, removeKey(node.getChild(keyPosition+1), getSmallestNextNode(node.getChild(keyPosition+1)).getKey(0))); 		// Eintrag wird durch naechst kleineren ersetzt
				}
				
			} else if(node.getChild(nextChildPosition) == null) {
				returnValue = removeKey(node.getChild(nextChildPosition-1), key);
			} else {
				returnValue = removeKey(node.getChild(nextChildPosition), key);
			}
		} else {														// Knoten enthaelt Key
			
			if(node.getNumberOfChilds() == 0) {							// Knoten ist ein Blatt
				returnValue = node.removeEntry(node.containsKey(key));
			} else {													// Knoten ist kein Blatt
				// Eintrag wird durch nachfolgenden Knoten ersetzt
				// das passiert aber schon oben!!!
			}
			
		}
		
		return returnValue;
	}
	
	private void mergeNodes(BNode leftTree, BNode rightTree, BNode node, int childPositionToMerge) {
		if(leftTree.getNumberOfEntrys() + rightTree.getNumberOfEntrys() >= ordnung) {			// Rotation
			
			rightTree.addEntry(0, node.setEntry(childPositionToMerge, leftTree.removeEntry(middle)));
			rightTree.addChild(0, leftTree.removeChild(leftTree.getNumberOfChilds()+1));
			
			while(middle < leftTree.getNumberOfEntrys()) {
				rightTree.addEntry(0, leftTree.removeEntry(leftTree.getNumberOfEntrys()-1));
				rightTree.addChild(0, leftTree.removeChild(leftTree.getNumberOfChilds()+1));
			}
		} else {																				// Zusammenlegen
			leftTree.addEntry(node.removeEntry(childPositionToMerge));
			leftTree.addChild(rightTree.removeChild(0));
			
			while(rightTree.getNumberOfEntrys() > 0) {
				leftTree.addEntry(rightTree.removeEntry(0));
				leftTree.addChild(rightTree.removeChild(0));
			}
			
			node.removeChild(childPositionToMerge);
		}
	}
	
	/**
	 * Methode zum Auffinden des vorherigen groesseren Schluessels im linken Teilbaum
	 * @param node
	 * @return
	 */
	private BNode getGreatestPreviousNode(BNode node) {
		if(node.getNumberOfChilds() > 0)
			return getGreatestPreviousNode(node.getChild(node.getNumberOfEntrys()-1+1));
		else
			return node;
	}
	
	/**
	 * Methode zum Auffinden des naechst kleineren Schluessels im rechten Teilbaum
	 * @param node
	 * @return
	 */
	private BNode getSmallestNextNode(BNode node) {
		if(node.getNumberOfChilds() > 0)
			return getSmallestNextNode(node.getChild(0));
		else
			return node;
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
					sb.append(String.format("(%2s)", node.getKey(i)));
				} else if (i == 0) { 															// ist erster Eintrag in Knoten
					sb.append(String.format("(%2s ", node.getKey(i)));
				} else if (i != 0 && i + 1 == node.getNumberOfEntrys()) { 						// ist letzer Eintrag in Knoten
					sb.append(String.format(" %2s)", node.getKey(i)));
				} else {
					sb.append(String.format(" %2s ", node.getKey(i)));
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
		System.out.println("Ordung:    " + ordnung);
		System.out.println("MinEntrys: " + minEntrys);
		System.out.println("MaxEntrys: " + (ordnung-1));
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