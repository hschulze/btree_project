package franz.tree;

public class BTree {

	private static final int ENTRY_WIDTH = 4;

	private static final int TEXT_WIDTH = 13;
	
	/**
	 * Knoten hat:<br />
	 * <li>mindestens ordnung/2 soehne</li>
	 * <li>maximal ordnung soehne</li>
	 * <li>maximal ordnung-1 schluessel</li>
	 * <li>mit k soehne speichert k-1 schluessel</li>
	 */
	private int order;
	private int numberOfTreeEntries = 0;
	private int minEntries;
	private int middle;
	private BNode root = null;

	public BTree(int order) {
		setOrder(order);
	}

	public NodeEntry searchKey(int key) {
		BNode node = searchKey(getRoot(), key);
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

	public boolean insertEntry(int key) {
		return insertEntry(new NodeEntry(key));
	}
	
	public boolean insertEntry(int key, String data) {
		return insertEntry(new NodeEntry(key, data));
	}
	
	/**
	 * 
	 * @param entry
	 * @return true, if key is inserted successful
	 */
	public boolean insertEntry(NodeEntry entry) {
		if (getRoot() == null) {							// Noch kein Baum vorhanden
			setRoot(new BNode(null));
		}
		if(searchKey(entry.getKey()) != null) {		// Schluessel schon vorhanden
			return false;
		}
		
		insertEntry(getRoot(), entry);
		
		if (getRoot().getNumberOfEntries() > getOrder()-1) {
			BNode newRoot = new BNode(null);
			splitTree(newRoot, 0, getRoot());
			setRoot(newRoot);
		}
		setNumberOfTreeEntries(getNumberOfTreeEntries() + 1);
		return true;
	}
	
	private void insertEntry(BNode node, NodeEntry entry) {
		if(node.getNumberOfChildren() == 0) {
			// BNode ist ein Blatt		
			for(int i = 0; i < order; i++) {
				if(node.getNumberOfEntries() > i) {												// wenn der Eintrag i existiert
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
			if(node.getChild(childPositionToInsert).getNumberOfEntries() > order-1) {
				splitTree(node, childPositionToInsert, node.getChild(childPositionToInsert));
			}
			
		}	
	}

	private void splitTree(BNode node, int childPositionToSplit, BNode subNodeToSplit) {
		BNode rightSubTree = null;
		
		node.addEntry(childPositionToSplit, subNodeToSplit.removeEntry(getMiddle()));
				
		rightSubTree = new BNode(node);
		while(getMiddle() < subNodeToSplit.getNumberOfEntries()) {
			rightSubTree.addChild(subNodeToSplit.removeChild(getMiddle()+1));
			rightSubTree.addEntry(subNodeToSplit.removeEntry(getMiddle()));
		}
		rightSubTree.addChild(subNodeToSplit.removeChild(subNodeToSplit.getNumberOfChildren() - 1));
		
		node.setChild(childPositionToSplit, subNodeToSplit);
		node.addChild(childPositionToSplit+1, rightSubTree);
	}
	
	/**
	 * 
	 * @param key
	 * @return true, if key is removed successful
	 */
	public NodeEntry removeEntry(int key) {
		NodeEntry result = null;
		
		if (searchKey(key) == null) {
			return null;
		}
		
		result = removeKey(getRoot(), key);
		
		setNumberOfTreeEntries(getNumberOfTreeEntries() - 1);
		return result;
	}
	
	private NodeEntry removeKey(BNode node, int key) {
		NodeEntry returnValue = null;
		BNode nodeWhereToDelete = searchKey(node, key);
		
		int keyEntryPosition = nodeWhereToDelete.containsKey(key);
		
		if(nodeWhereToDelete.getNumberOfChildren() == 0) {		// Knoten zum Loeschen ist ein Blatt
			returnValue = nodeWhereToDelete.removeEntry(keyEntryPosition);
			checkNode(nodeWhereToDelete);
		} else {												// Knoten zum Loeschen ist ein Knoten
			//Ersetzen durch Inorder-Nachfolger
			
//			returnValue = nodeWhereToDelete.setEntry(keyEntryPosition, removeKey(nodeWhereToDelete.getChild(keyEntryPosition+1), getSmallestNextNode(nodeWhereToDelete.getChild(keyEntryPosition+1)).getKey(0)));
			
			returnValue = nodeWhereToDelete.removeEntry(keyEntryPosition);
			nodeWhereToDelete.addEntry(keyEntryPosition, getSmallestNextNode(nodeWhereToDelete.getChild(keyEntryPosition+1)).getEntry(0));
			
			removeKey(getSmallestNextNode(nodeWhereToDelete.getChild(keyEntryPosition+1)), getSmallestNextNode(nodeWhereToDelete.getChild(keyEntryPosition+1)).getKey(0));
		}
		
		return returnValue;
	}
	
	private void checkNode(BNode node) {
		if(node.getNumberOfEntries() < minEntries && node.getParent() != null) {			// Knoten enthaelt nicht genug Eintraege und ist nicht die Wurzel
			BNode parent = node.getParent();
			int parentPosition = 0;
			while (parent.getChild(parentPosition) != node)
				parentPosition++;
			
			int numberOfLeftSiblingNodeEntries = parent.getChild(parentPosition-1) != null ? parent.getChild(parentPosition-1).getNumberOfEntries() : 0;
			int numberOfRightSiblingNodeEntries = parent.getChild(parentPosition+1) != null ? parent.getChild(parentPosition+1).getNumberOfEntries() : 0;
			
			if(numberOfLeftSiblingNodeEntries > getMinEntries() || numberOfRightSiblingNodeEntries > getMinEntries()) {
				// Fall 2: nextChild besitzt nach dem Loeschen nur m-1 Schluessel && ein Geschwisterknoten besitzt mind. m+1 Schluessel => rotate (168 f)
				if(numberOfLeftSiblingNodeEntries < numberOfRightSiblingNodeEntries) {
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
		if(nodeKeyPosition == node.getNumberOfChildren()-1) {
			// nextChild ist das rechteste Kind des Elternknotens
			leftNode = node.getChild(nodeKeyPosition-1);
			rightNode = node.getChild(nodeKeyPosition);
			
			//leftNode.addEntry(removeKey(node.getParent(), node.getParent().getKey(nodeKeyPosition)));			// Geht grundsaetzlich, außer dass das der Vater noch verschoben werden muss
			leftNode.addEntry(node.removeEntry(nodeKeyPosition-1));
		} else {
			leftNode = node.getChild(nodeKeyPosition);
			rightNode = node.getChild(nodeKeyPosition+1);
			
			leftNode.addEntry(node.removeEntry(nodeKeyPosition));
		}
		
		while(rightNode.getNumberOfEntries() > 0) {
			leftNode.addChild(rightNode.removeChild(0));
			leftNode.addEntry(rightNode.removeEntry(0));
		}
		leftNode.addChild(rightNode.removeChild(0));
		
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
		childNode.addChild(0, leftNode.removeChild(leftNode.getNumberOfChildren()-1));
		
		node.addEntry(nodeKeyPosition-1, leftNode.removeEntry(leftNode.getNumberOfEntries()-1));
	}
	
	/**
	 * Methode zum Auffinden des vorherigen groesseren Schluessels im linken Teilbaum
	 * @param node
	 * @return
	 */
	private BNode getGreatestPreviousNode(BNode node) {
		if(node.getNumberOfChildren() > 0)
			return getGreatestPreviousNode(node.getChild(node.getNumberOfEntries()));
		else
			return node;
	}
	
	/**
	 * Methode zum Auffinden des naechst kleineren Schluessels im rechten Teilbaum
	 * @param node
	 * @return
	 */
	private BNode getSmallestNextNode(BNode node) {
		if(node.getNumberOfChildren() > 0)
			return getSmallestNextNode(node.getChild(0));
		else
			return node;
	}

	public void printTree() {
		System.out.println("Ausgabe des Baums:");
		
		int maxHeight = getMaxHeight();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getWidth(getRoot()) + TEXT_WIDTH; i++) {
			sb.append('#');
		}
		String border = sb.toString();
		System.out.println(border);
		sb = new StringBuilder();
		for (int i = 1; i <= getMaxHeight(); ++i) {
			sb.append(String.format("# Tiefe %2d |", i));
			printLine(sb, getRoot(), i);
			//sb.append(String.format("| Tiefe %2d #", i));
			sb.append("#");
			sb.append("\n");
		}
		System.out.print(sb.toString());
		System.out.println(border);
	}

	public void printLine(StringBuilder sb, BNode node, int depth) {
		if (node == null)
			return;
		for (int i = 0; i < node.getNumberOfEntries(); i++) {
			if (depth == 1) {
				fillSpaces(sb, getWidth(node.getChild(i)));
				if (i == 0 && node.getNumberOfEntries() == 1) { 								// nur ein Eintrag in Knoten
					sb.append(String.format("(%"+(ENTRY_WIDTH-2)+"s)", node.getKey(i)));
				} else if (i == 0) { 															// ist erster Eintrag in Knoten
					sb.append(String.format("(%"+(ENTRY_WIDTH-2)+"s ", node.getKey(i)));
				} else if (i + 1 == node.getNumberOfEntries()) { 								// ist letzer Eintrag in Knoten
					sb.append(String.format(" %"+(ENTRY_WIDTH-2)+"s)", node.getKey(i)));
				} else {
					sb.append(String.format(" %"+(ENTRY_WIDTH-2)+"s ", node.getKey(i)));
				}
				if(i+1 == node.getNumberOfEntries())
					fillSpaces(sb, getWidth(node.getChild(i+1)));
			} else {
				printLine(sb, node.getChild(i), depth - 1);
				fillSpaces(sb, ENTRY_WIDTH);
				if(i+1 == node.getNumberOfEntries())
					printLine(sb, node.getChild(i+1), depth - 1);
			}
		}
	}

	private void fillSpaces(StringBuilder sb, int count) {
		for(int i = 0; i < count; i++)
			sb.append(' ');
	}

	private int getWidth(BNode node) {
		if (node == null)
			return 0;
		int leftWidth = 0;
		int rightWidth = 0;
		for (int i = 0; i < node.getNumberOfEntries(); i++) {
			if (node.getChild(i) != null)
				leftWidth += getWidth(node.getChild(i));
			//if (node.getChild(i+1) != null)
			if (i+1 == node.getNumberOfEntries())
				rightWidth += getWidth(node.getChild(i+1));
		}
		return leftWidth + (ENTRY_WIDTH * node.getNumberOfEntries()) + rightWidth;
	}

	public void printStats() {
		System.out.println("Ausgabe der Statistik:");
		System.out.printf("%25s: %2d\n", "Ordnung", getOrder());
		System.out.printf("%25s: %2d\n", "Eintraege", getNumberOfTreeEntries());
		System.out.printf("%25s: %2d\n", "MinEintraege/Knoten", getMinEntries());
		System.out.printf("%25s: %2d\n", "MaxEintraege/Knoten", getOrder()-1);
		System.out.printf("%25s: %2d\n", "MinHoehe", getMinHeight());
		System.out.printf("%25s: %2d\n", "MaxHoehe", getMaxHeight());		
	}

	public int getMinHeight() {
		return (int) (Math.ceil(Math.log(getNumberOfTreeEntries() + 1) / Math.log(order) - 1));
	}

	public int getMaxHeight() {
		return (int) (Math.floor(Math.log((double) ((getNumberOfTreeEntries() + 1) / 2)) / Math.log(Math.ceil((double) order / 2))) + 1);
	}

	private int getOrder() {
		return order;
	}

	private void setOrder(int order) {
		this.order = order;
		setMinEntries((int) Math.ceil(getOrder() / 2));	// -1
		setMiddle(getOrder() / 2);
	}

	private int getNumberOfTreeEntries() {
		return numberOfTreeEntries;
	}

	private void setNumberOfTreeEntries(int numberOfTreeEntries) {
		this.numberOfTreeEntries = numberOfTreeEntries;
	}

	private int getMinEntries() {
		return minEntries;
	}

	private void setMinEntries(int minEntries) {
		this.minEntries = minEntries;
	}

	private int getMiddle() {
		return middle;
	}

	private void setMiddle(int middle) {
		this.middle = middle;
	}

	public BNode getRoot() {
		return root;
	}

	private void setRoot(BNode root) {
		this.root = root;
	}
}