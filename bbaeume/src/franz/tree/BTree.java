package franz.tree;

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
		BNode node = searchKey(root, key);
		if(node == null) {
			return null;
		} else {
			return node.getEntry(node.containsKey(key));
		}
	}
	
	private BNode searchKey(BNode node, int key) {
		if(node == null) 
			return null;
		
		int entryPosition = node.containsKey(key);
		if(entryPosition < 0) {
			return searchKey(node.getChild(node.getNextChildPositionForKey(key)), key);
		} else {
			//return node.getEntry(entryPosition); 
			return node;
		}
	}

	/**
	 * 
	 * @param key
	 * @return true, if key is inserted successful
	 */
	public boolean insertEntry(NodeEntry entry) {
		if (root == null) {							// Noch kein Baum vorhanden
			root = new BNode(null);
		}
		if(searchKey(entry.getKey()) != null) {		// Schluessel schon vorhanden
			return false;
		}
		
		insertEntry(root, entry);
		
		if (root.getNumberOfEntrys() > ordnung-1) {
			BNode newRoot = new BNode(null);
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
			int childPositionToInsert = node.getNextChildPositionForKey(entry.getKey());
			
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
		
		BNode rightSubTree = new BNode(node);
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
	
	private NodeEntry removeKey(BNode node, int key) {
		NodeEntry returnValue = null;
		BNode nodeWhereToDelete = searchKey(node, key);
		
		int keyEntryPosition = nodeWhereToDelete.containsKey(key);
		
		if(nodeWhereToDelete.getNumberOfChilds() == 0) {		// Knoten zum Loeschen ist ein Blatt
			returnValue = nodeWhereToDelete.removeEntry(keyEntryPosition);
			checkNode(nodeWhereToDelete);
		} else {												// Knoten zum Loeschen ist ein Knoten
			//Ersetzen durch Inorder-Nachfolger
			
			//returnValue = nodeWhereToDelete.setEntry(keyEntryPosition, removeKey(nodeWhereToDelete.getChild(keyEntryPosition+1), getSmallestNextNode(nodeWhereToDelete.getChild(keyEntryPosition+1)).getKey(0)));
			
			returnValue = nodeWhereToDelete.removeEntry(keyEntryPosition);
			nodeWhereToDelete.addEntry(keyEntryPosition, getSmallestNextNode(nodeWhereToDelete.getChild(keyEntryPosition+1)).getEntry(0));
			
			removeKey(getSmallestNextNode(nodeWhereToDelete.getChild(keyEntryPosition+1)), getSmallestNextNode(nodeWhereToDelete.getChild(keyEntryPosition+1)).getKey(0));
		}
		
		return returnValue;
	}
	
	private void checkNode(BNode node) {
		if(node.getNumberOfEntrys() < minEntrys && node.getParent() != null) {			// Knoten enthaelt nicht genug Eintraege und ist nicht die Wurzel
			BNode parent = node.getParent();
			int parentPosition = 0;
			while (parent.getChild(parentPosition) != node)
				parentPosition++;
			
			int numberOfLeftSilbingNodeEntrys = parent.getChild(parentPosition-1) != null ? parent.getChild(parentPosition-1).getNumberOfEntrys() : 0;
			int numberOfRightSilbingNodeEntrys = parent.getChild(parentPosition+1) != null ? parent.getChild(parentPosition+1).getNumberOfEntrys() : 0;
			
			if(numberOfLeftSilbingNodeEntrys > minEntrys || numberOfRightSilbingNodeEntrys > minEntrys) {
				// Fall 2: nextChild besitzt nach dem Loeschen nur m-1 Schluessel && ein Geschwisterknoten besitzt mind. m+1 Schluessel => rotate (168 f)
				if(numberOfLeftSilbingNodeEntrys < numberOfRightSilbingNodeEntrys) {
					// Rotation mit dem rechten Geschwisterknoten
					rotateRight(parent, parentPosition);
				} else {
					// Rotation mit dem linken Geschwisterknoten
					rotateLeft(parent, parentPosition);
				}
			} else {
				// Fall 3: nextChild besitzt nach dem Loeschen nur m-1 Schluessel && die Geschwisterknoten besitzen ebenfalls m Schluessel => merge (170 f)  !!! Beachte, wenn Wurzel !!!
				merge(parent, parentPosition);
				checkNode(parent);
			} 
		}
	}
	
	private void merge(BNode node, int nodeKeyPosition) {
		BNode leftNode = null;
		BNode rightNode = null;
		if(nodeKeyPosition == node.getNumberOfChilds()-1) {
			// nextChild ist das rechteste Kind des Elternknotens
			leftNode = node.getChild(node.getNumberOfChilds()-2);
			rightNode = node.getChild(node.getNumberOfChilds()-1);
		} else {
			leftNode = node.getChild(nodeKeyPosition);
			rightNode = node.getChild(nodeKeyPosition+1);
		}
		
		//leftNode.addEntry(removeKey3(node.getParent(), node.getParent().getKey(nodeKeyPosition)));
		leftNode.addEntry(node.removeEntry(nodeKeyPosition));
		
		while(rightNode.getNumberOfEntrys() > 0) {
			leftNode.addChild(rightNode.removeChild(0));
			leftNode.addEntry(rightNode.removeEntry(0));
		}
		
		node.removeChild(nodeKeyPosition+1);
	}
	
	private void rotateRight(BNode node, int nodeKeyPosition) {
		BNode childNode = node.getChild(nodeKeyPosition);
		BNode rightNode = node.getChild(nodeKeyPosition+1);
		
		childNode.addEntry(node.removeEntry(nodeKeyPosition));
		childNode.addChild(rightNode.removeChild(0));
		
		node.addEntry(nodeKeyPosition, rightNode.removeEntry(0));
	}
	
	private void rotateLeft(BNode node, int nodeKeyPosition) {
		BNode childNode = node.getChild(nodeKeyPosition);
		BNode leftNode = node.getChild(nodeKeyPosition-1);
		
		childNode.addEntry(0, node.removeEntry(nodeKeyPosition-1));
		childNode.addChild(0, leftNode.removeChild(leftNode.getNumberOfChilds()-1));
		
		node.addEntry(nodeKeyPosition-1, leftNode.removeEntry(leftNode.getNumberOfEntrys()-1));
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