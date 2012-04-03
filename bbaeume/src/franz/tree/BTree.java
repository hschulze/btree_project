package franz.tree;

import java.util.List;

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
	
	public NodeEntry searchKeyR(int key) {
		if(root == null) return null;
		return root.searchEntry(key); 
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
		BNode result = null;
		try {
			result = root.insertEntry(entry);
		} catch(Exception e) {
			return false;
		}
		
		if(result != null)
			root = result;
		
		numberOfTreeEntrys++;
		return true;
	}
	/**
	 * 
	 * @param key
	 * @return true, if key is removed successful
	 */
	public boolean removeEntry(NodeEntry entry) {
		root.removeEntry(entry);
		numberOfTreeEntrys--;
		return true;
	}
	
	public void showTree() {
		System.out.println("Ausgabe des Baums:");
		//showNode(root, 0);
		
		// Richtige Baumstruktur
		/*
		List<int[]> rows = new LinkedList<int[]>();
		int maxWidth = (int) (Math.pow(ordnung, getMaxHeight()) - Math.pow(ordnung, getMaxHeight()-1));
		System.out.println("Debug: " + 
								"Entrys: " + numberOfTreeEntrys + 
								" Height: " + getMaxHeight() + 
								" Width: " + maxWidth);
		for(int i = 0; i < getMaxHeight(); i++) {
			rows.add(new int[maxWidth]);
		}
		showNode2(root, rows, 0, maxWidth/2);
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < getMaxHeight(); i++) {
			for(int j = 0; j < maxWidth; j++) {
				if(rows.get(i)[j] != 0) {
					sb.append(String.format("%2d ", rows.get(i)[j]));
				} else {
					sb.append("   ");
				}
			}
			sb.append("\n");
		}
		System.out.print(sb);
		for(int i = 0; i < maxWidth; i++)
			System.out.print(" 0 ");
		System.out.print("\n");
		System.out.println("#########################");
		*/
		showNode3(root);
	}
	
	private void showNode3(BNode node) {
		int maxHeight = getMaxHeight();
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < getWidth(root) + 24; i++) {
			sb.append('#');
		}
		String border = sb.toString();
		System.out.println(border);
		sb = new StringBuilder();
		for(int i = 1; i <= maxHeight; ++i) {
			sb.append(String.format("# Tiefe %2d |", i));
			printLine(sb, node, i);
			sb.append(String.format("| Tiefe %2d #", i));
			sb.append("\n");
		}
		System.out.print(sb.toString());
		System.out.println(border);
	}
	private void printLine(StringBuilder sb, BNode node, int depth) {
		if(node == null)
			return;
		for(int i = 0; i < node.getNumberOfEntrys(); i++) {
			if(depth == 1) {
				fillSpaces(sb, getWidth(node.getEntrys().get(i).getLowerChild()));
				if(i == 0 && node.getEntrys().size() == 1) {								// nur ein Eintrag in Knoten
					sb.append(String.format("(%2s)", node.getEntrys().get(i).getKey()));
				} else if(i == 0) {															// ist erster Eintrag in Knoten
					sb.append(String.format("(%2s ", node.getEntrys().get(i).getKey()));
				} else if(i != 0 && i+1 == node.getNumberOfEntrys()) {						// ist letzer Eintrag in Knoten
					sb.append(String.format(" %2s)", node.getEntrys().get(i).getKey()));
				} else {
					sb.append(String.format(" %2s ", node.getEntrys().get(i).getKey()));
				}
				fillSpaces(sb, getWidth(node.getEntrys().get(i).getHigherChild()));
			} else {
				printLine(sb, node.getEntrys().get(i).getLowerChild(), depth-1);
				fillSpaces(sb, 4);
				printLine(sb, node.getEntrys().get(i).getHigherChild(), depth-1);
			}
		}
	}
	private void fillSpaces(StringBuilder sb, int count) {
		for(int i = 0; i < count; i++)
			sb.append(' ');
	}
	private int getWidth(BNode node) {
		if(node == null) return 0;
		
		int leftWidth = 0;
		int rightWidth = 0;
		
		for(int i = 0; i < node.getNumberOfEntrys(); i++) {
			if(node.getEntrys().get(i).hasLowerChild())
				leftWidth += getWidth(node.getEntrys().get(i).getLowerChild());
			if(node.getEntrys().get(i).hasHigherChild())
				rightWidth += getWidth(node.getEntrys().get(i).getHigherChild());
		}
		return leftWidth + ( 4 * node.getNumberOfEntrys() ) + rightWidth;
	}
	
	private void showNode2(BNode node, List<int[]> rows, int depth, int pos) {
		if(node != null) {
			for(int i = 0; i < node.getNumberOfEntrys(); i++) {
				showNode2(node.getEntrys().get(i).getLowerChild(), rows, depth+1, pos-1);
				
				rows.get(depth)[pos-1+i] = node.getEntrys().get(i).getKey();
				
				if(node.getEntrys().get(i).hasHigherChild())
					showNode2(node.getEntrys().get(i).getHigherChild(), rows, depth+1, pos+1);
			}
		}
	}
	
	private void showNode(BNode node, int depth) {
		if(node != null) {
			for(int i = 0; i < node.getNumberOfEntrys(); i++) {
				showNode(node.getEntrys().get(i).getLowerChild(), depth + 1);
				
				StringBuilder sb = new StringBuilder();
				for(int j = 0; j < depth; j++) {
					sb.append('\t');
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
		System.out.println("MinHeight: " + getMinHeight());
		System.out.println("MaxHeight: " + getMaxHeight());
	}
	
	public int getMinHeight() {
		double minHeight = Math.ceil(Math.log(numberOfTreeEntrys + 1) / Math.log(ordnung) -1);
		int result = (int) minHeight;
		return result;
	}
	
	public int getMaxHeight() {
		double maxHeight = Math.floor( Math.log((double) ((numberOfTreeEntrys+1) / 2)) / Math.log(Math.ceil((double) ordnung / 2)) ) + 1;
		int result = (int) maxHeight;
		return result;
	}
}
