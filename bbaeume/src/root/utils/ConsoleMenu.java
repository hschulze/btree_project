package root.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import root.tree.BTree;



public class ConsoleMenu {

	private Map<Integer, String> menuItems;
	
	private BTree tree;
	private boolean showMenu;
	
	/**
	 * Konstruktor fuer ein Menue-Objekt
	 * @param tree - B-Baum, der in dem ConsoleMenu-Objekt verwaltet wird
	 * @param showMenu - gibt an, ob der B-Baum neben dem Menue angezeigt werden soll
	 */
	public ConsoleMenu(BTree tree, boolean showMenu) {
		setTree(tree);
		setShowMenu(showMenu);
	}
	
	/**
	 * Funktion zum Aufbau eines Auswahl-Menues
	 * @param text - Text des Menue-Punkts
	 * @param number - Nummer des Menue-Punkts
	 */
	public void addMenuItem(String text, int number) {
		getMenuItems().put(number, text);
	}
	
	/**
	 * Funktion die das Menue ausgibt und die darauffolgende Auswahl einliest
	 * @return getroffene Auswahl durch den Benutzer
	 */
	public int showMenu() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("------------------------------------------------- ");
		if(showMenu) {
			for(int j = 0; j < getTree().getWidth(tree.getRoot()) + 14; j++) {
				sb.append("#");
			}
		}
		sb.append('\n');
		
		for(int i = 1; i < 10 || i <= getTree().getMaxHeight()+1; i++) {
			if(getMenuItems().get(i) != null) {
				sb.append(String.format(" %d) %-45s ", i, menuItems.get(i)));
			} else {
				sb.append(String.format("%50s", ""));
			}
			if(isShowMenu() && i <= getTree().getMaxHeight()) {
				sb.append(String.format("# Tiefe %2d |", i));
				tree.printLine(sb, tree.getRoot(), i);
				sb.append('#');
			}
			if(isShowMenu() && i == getTree().getMaxHeight() + 1) {
				for(int j = 0; j < getTree().getWidth(tree.getRoot()) + 14; j++) {
					sb.append("#");
				}
			}
				
			sb.append('\n');
		}
		
		if(menuItems.get(0) != null)
			sb.append(String.format(" %d) %-45s \n", 0, menuItems.get(0)));
		sb.append("=================================================\n");
		sb.append("Auswahl: ");
		return readInt(sb.toString());
	}
	
	/**
	 * Funktion zum Einlesen eines int-Werts
	 * @param text - String der bei der Eingabe zur Erlaeuterung steht
	 * @return eingelesener int-Wert
	 */
	public static int readInt(String text) {
		return readInt(text, -1);
	}
	
	/**
	 * Funktion zum Einlesen eines int-Werts
	 * @param text - String der bei der Eingabe zur Erlaeuterung steht
	 * @param defaultValue - bei keiner Eingabe wird dieser Wert uebernommen und zurueckgegeben
	 * @return eingelesener int-Wert
	 */
	public static int readInt(String text, int defaultValue) {
		return readInt(text, defaultValue, -1);
	}
	
	/**
	 * Funktion zum Einlesen eines int-Werts
	 * @param text - String der bei der Eingabe zur Erlaeuterung steht
	 * @param defaultValue - bei keiner Eingabe wird dieser Wert uebernommen und zurueckgegeben
	 * @param min - kleinster beim einlesen zugelassener Wert
	 * @return eingelesener int-Wert
	 */
	public static int readInt(String text, int defaultValue, int min) {
		int result = 0;
		String tmp = "";
		
		do {
			System.out.printf("%40s", text);
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				tmp = in.readLine();
				result = Integer.parseInt(tmp);
			} catch (Exception e) {
				if(defaultValue == -1 || tmp.length() > 0) {
					System.out.print("Fehler bei der Eingabe. Bitte erneut einem gueltigen Integer-Wert eingeben");
					if(defaultValue != -1)
						System.out.print(" oder Eingabe fuer Default-Wert");
					System.out.print('\n');
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
	
	public void waitForEnter() {
		try {
			System.out.print("Weiter mit Enter...");
			new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (Exception e) {;}
	}
	
	/**
	 * Funktion zum uebergeben eines neuen B-Baums an das Menue
	 * @param tree
	 */
	public void setTree(BTree tree) {
		this.tree = tree;
	}

	private boolean isShowMenu() {
		return showMenu;
	}

	private void setShowMenu(boolean showMenu) {
		this.showMenu = showMenu;
	}
}
