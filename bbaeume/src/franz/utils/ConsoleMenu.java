package franz.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import franz.tree.BTree;


public class ConsoleMenu {

	private Map<Integer, String> menuItems;
	
	private BTree tree;
	
	public ConsoleMenu(BTree tree) {
		setTree(tree);
	}

	public void addMenuItem(String text, int number) {
		getMenuItems().put(number, text);
	}
	
	public int showMenu() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("+++++++++++++++++++++++++++++++++++++++++++++++++ ");
		for(int j = 0; j < getTree().getWidth(tree.getRoot()) + 14; j++) {
			sb.append("#");
		}
		sb.append('\n');
		
		for(int i = 1; i < 10 || i <= getTree().getMaxHeight()+1; i++) {
			if(getMenuItems().get(i) != null) {
				sb.append(String.format("%45s [%d] ", menuItems.get(i), i));
			} else {
				sb.append(String.format("%50s", ""));
			}
			if(i <= getTree().getMaxHeight()) {
				sb.append(String.format("# Tiefe %2d |", i));
				tree.printLine(sb, tree.getRoot(), i);
				sb.append('#');
			}
			if(i == getTree().getMaxHeight() + 1) {
				for(int j = 0; j < getTree().getWidth(tree.getRoot()) + 14; j++) {
					sb.append("#");
				}
			}
				
			sb.append('\n');
		}
		
		if(menuItems.get(0) != null)
			sb.append(String.format("%45s [%d]\n", menuItems.get(0), 0));
		sb.append("-------------------------------------------------\n");
		sb.append("Auswahl: ");
		return readInt(sb.toString());
	}
	
	public static int readInt(String text) {
		return readInt(text, -1);
	}
	
	public static int readInt(String text, int defaultValue) {
		return readInt(text, defaultValue, -1);
	}
	
	public static int readInt(String text, int defaultValue, int min) {
		int result = 0;
		
		do {
			System.out.printf("%40s", text);
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				result = Integer.parseInt(in.readLine());
			} catch (Exception e) {
				if(defaultValue == -1) {
					System.out.println("Fehler bei der Eingabe. Bitte erneut versuchen.");
					result = -1;
				} else {
					result = defaultValue;
				}
			}
		} while (result < min);
		
		return result;
	}

	private Map<Integer, String> getMenuItems() {
		if(menuItems == null) {
			menuItems = new HashMap<Integer, String>();
		}
		return menuItems;
	}
	
	private BTree getTree() {
		return tree;
	}

	public void setTree(BTree tree) {
		this.tree = tree;
	}
}
