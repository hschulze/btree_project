package franz;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

import franz.tree.BTree;
import franz.tree.NodeEntry;

public class BTreeStarter {

	public static void main(String[] args) {
		System.out.printf("%20s%n", "Initiale Erzeugung des B-Baums");
		BTree tree = new BTree(readInt("Ordnung m des B-Baums [3]: ", 3));
		
		int choice = -1;
		
		while(choice != 0) {
			choice = showMenu();
			System.out.print("+++++++++++++++++++++++++++++++++++++++++\n");
			//System.out.println("Auswahl: " + choice);
			switch (choice) {
				case 1:
					break;
				case 2:
					tree.showTree();
					break;
				case 3:
					fillWithRandomData(tree);
					break;
				case 4:
					addOneNumber(tree);
					break;
				case 6:
					searchKey(tree);
					break;
				case 9:
					tree.showStat();
					break;
				default:
					break;
			}
		}
		
		System.out.println("Anwendung wird beendet...");
		try {
			Thread.sleep(500);
		} catch (Exception e) {}
		System.out.println("Beendet...");
	}
	
	private static int showMenu() {
		String menu = 	"+++++++++++++++++++++++++++++++++++++++++\n" +
						//"Erzeuge einen B-Baum                  [1]\n" +
						"Zeichne Baum                          [2]\n" +
						"Fuelle den Baum mit Zufallsdaten      [3]\n" +
						"Fuege eine Zahl hinzu                 [4]\n" +
						"\n" +
						"Suche Zahl                            [6]\n" +
						"Zeige Statistik                       [9]\n" +
						"\n" +
						"Beenden                               [0]\n" +
						"-----------------------------------------\n" +
						"Auswahl: ";
		return readInt(menu);
	}
	
	private static int readInt(String text) {
		return readInt(text, -1);
	}
	
	private static int readInt(String text, int defaultValue) {
		int result;
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
		} while (result == -1);
		return result;
	}
	
	public static void fillWithRandomData(BTree tree) {
		int numberOfValues = readInt("Anzahl der Schlüssel [20]: ", 20);
		int maxValue = readInt("Maximaler Wert [99]: ", 99);
		int seed = readInt("Seed [4711]: ", 4711);
		
		Random rand = new Random((long) seed);
		boolean successfulInsert;
		for(int i = 0; i < numberOfValues; i++) {
			successfulInsert = false;
			do {
				successfulInsert = tree.insertEntryR(new NodeEntry(rand.nextInt(maxValue)));
			} while(!successfulInsert);
		}
	}
	
	public static void addOneNumber(BTree tree) {
		if(tree.insertEntryR(new NodeEntry(readInt("Einzufuegende Zahl: ")))) {
			System.out.printf("%40s%n", "Zahl erfolgreich eingefuegt");
		} else {
			System.out.printf("%40s%n", "Zahl nicht eingefuegt");
		}
			
	}
	
	public static void searchKey(BTree tree) {
		if(tree.searchKeyR(readInt("Schluessel: "))) {
			System.out.printf("%40s%n", "Schluessel gefunden");
		} else {
			System.out.printf("%40s%n", "Schluessel nicht gefunden");
		}
	}
}
