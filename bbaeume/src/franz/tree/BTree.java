package franz.tree;

public class BTree {

	private int ordnung;	// Knoten hat 
							// 		mindestens ordnung/2 söhne
							// 		maximal ordnung söhne
							//		maximal ordnung-1 schlüssel
							//		mit k söhne speichert k-1 schlüssel
	
	private int numberOfTreeEntrys = 0;
	
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
	
	public void showTree() {
		System.out.println("Ausgabe des Baums:");
		System.out.println("#########################################");
		showNode(root, 0);
		System.out.println("#########################################");
	}
	
	private void showNode(BNode node, int depth) {
		if(node != null) {
			for(int i = 0; i < node.getNumberOfEntrys(); i++) {
				showNode(node.getEntrys().get(i).getLowerChild(), depth + 1);
				
				StringBuilder sb = new StringBuilder();
				for(int j = 0; j < depth; j++) {
					sb.append('-');
				}
				sb.append(node.getEntrys().get(i).getKey());
				System.out.println(sb.toString());
				
				if(node.getEntrys().get(i).hasHigherChild()) {
					showNode(node.getEntrys().get(i).getHigherChild(), depth + 1);
				}
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
