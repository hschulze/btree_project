package franz.tree;

public class BNode {

	private int ordnung;
	
	private NodeEntry[] entrys;
	private int numberOfEntrys = 0;
	private BNode[] childNodes;
	private int numberOfChildNodes = 0;
	
	public BNode(int ordnung) {
		this.ordnung = ordnung;
		entrys = new NodeEntry[ordnung - 1];
		childNodes = new BNode[ordnung];
	}
	
	public BNode[] getChildNodes() {
		return childNodes;
	}
	
	public int getNumberOfChildNodes() {
		return numberOfChildNodes;
	}
	
	public NodeEntry[] getEntrys() {
		return entrys;
	}
	
	public int getNumberOfEntrys() {
		return numberOfEntrys;
	}
	
	public void setNumberOfEntrys(int numberOfEntrys) {
		this.numberOfEntrys = numberOfEntrys;
	}
	public NodeEntry searchEntry(int key) {
		return searchEntry(this, key);
	}
	public NodeEntry searchEntry(BNode node, int key) {
		if(node == null) 
			return null;
		for(int i = 0; i < node.getNumberOfEntrys(); i++) {
			if(key == node.entrys[i].getKey()) return node.entrys[i]; 
			if(key < node.entrys[i].getKey()) return searchEntry(node.getChildNodes()[i], key);
			if((i+1) == node.getNumberOfEntrys()) return searchEntry(node.getChildNodes()[i+1], key);
		}
		return null;
	}
	
	public NodeEntry insertEntryR(NodeEntry entry) {
		NodeEntry searchResult = searchEntry(entry.getKey()); 
		if(searchResult != null) return searchResult;											// Schluessel schon vorhanden
		return insertEntryR(this, entry);
	}
	
	private NodeEntry insertEntryR(BNode node, NodeEntry entry) {
		if(node.getNumberOfChildNodes() == 0) {													// wenn es ein Blatt ist
			
			if(node)
			
			for(int i = 0; i < ordnung - 1; i++) {
				if(node.getEntrys()[i] != null) {												// wenn der Eintrag i existiert
					if(entry.getKey() < node.getEntrys()[i].getKey()) {
						for(int j = node.getNumberOfEntrys() - 1; j > i; j--) {
							node.getEntrys()[j] = node.getEntrys()[j-1];
						}
						node.getEntrys()[i] = entry;
						break;
					}
				} else {
					node.getEntrys()[i] = entry;
					break;
				}
			}
			node.setNumberOfEntrys(node.getNumberOfEntrys()+1);									// Zaehler um eins erhoehen
			// Eingefuegt
			if(node.getNumberOfEntrys() == ordnung) {											// Knoten voll
				
			} else {
				return null;
			}
		} else {																				// wenn es ein Knoten ist (kein Blatt)
			
		}
		
		return node;
	}
	
	public BNode removeEntry(BNode node, NodeEntry entry) {
		return null;
	}
}
