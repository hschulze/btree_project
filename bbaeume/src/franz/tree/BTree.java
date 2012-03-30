package franz.tree;

public class BTree {

	private int ordnung;	// Knoten hat 
							// 		mindestens ordnung/2 söhne
							// 		maximal ordnung söhne
							//		maximal ordnung-1 schlüssel
							//		mit k söhne speichert k-1 schlüssel
	
	private int numberOfTreeEntrys = 0;
	private int numberOfTreeNodes = 0;
	private BNode root = null;
	
	public BTree(int ordnung) {
		this.ordnung = ordnung;
	}
	
	public boolean searchKeyR(int key) {
		if(root == null) return false;
		NodeEntry result = root.searchEntry(key); 
		if(result == null) {
			return false;
		} else {
			
			return true;
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return true, if key is inserted successful
	 */
	public boolean insertEntryR(NodeEntry entry) {
		if(root == null) {
			root = new BNode(ordnung);
		}
		root.insertEntryR(entry);
		
		numberOfTreeEntrys++;
		return true;
	}
	/**
	 * 
	 * @param key
	 * @return true, if key is removed successful
	 */
	public boolean removeEntry(NodeEntry entry) {
		root.removeEntry(root, entry);
		numberOfTreeEntrys--;
		return true;
	}
	/**
	 * Werte bis 99 werden korrekt angezeigt
	 */
	public void showTree() {
		System.out.println("Ausgabe des Baums:");
		int maxWidth = getMaxHeight() * (ordnung-1) * 3;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < getMaxHeight(); i++) {
			for(int j = 0; j < ((i+1)*(ordnung-1)); j++) { 
				sb.append(String.format("", root.getEntrys()));
			}
		}
		
	}
	
	public void showStat() {
		System.out.println("Ausgabe der Statistik:");
		System.out.println("MaxHeight: " + getMaxHeight());
	}
	
	public int getMaxHeight() {
		int result = (int) (Math.log((numberOfTreeEntrys + 1) / 2) / Math.log(ordnung)) + 1;
		return result;
	}
}
