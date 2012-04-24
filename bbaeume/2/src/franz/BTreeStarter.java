package franz;

import java.util.Random;

import franz.tree.BTree;
import franz.tree.NodeEntry;
import franz.utils.ConsoleMenu;

public class BTreeStarter {

	public static void main(String[] args) { 
		ConsoleMenu console = new ConsoleMenu();

		
		console.addMenuItem("Erzeuge einen neuen B-Baum", 1);
		console.addMenuItem("Zeichne Baum", 2);
		console.addMenuItem("Fuelle den Baum mit Zufallszahlen", 3);
		console.addMenuItem("Zahl hinzufuegen", 4);
		console.addMenuItem("Zahl suchen", 6);
		console.addMenuItem("Zahl loeschen", 7);
		console.addMenuItem("Zeige B-Baum-Statistik", 9);
		console.addMenuItem("Beenden", 0);
		
		System.out.printf("%20s%n", "Initiale Erzeugung des B-Baums");
		BTree tree = new BTree(ConsoleMenu.readInt("Ordnung m des B-Baums [3]: ", 3, 3));
		
		int choice = -1;
		
		while(choice != 0) {
			choice = console.showMenu();
			System.out.print("+++++++++++++++++++++++++++++++++++++++++++++++++\n");
			//System.out.println("Auswahl: " + choice);
			switch (choice) {
				case 1:
					tree = new BTree(ConsoleMenu.readInt("Ordnung m des B-Baums [3]: ", 3, 2));
					break;
				case 2:
					tree.showTree();
					break;
				case 3:
					fillWithRandomData(tree);
					tree.showTree();
					break;
				case 4:
					addOneNumber(tree);
					tree.showTree();
					break;
				case 6:
					searchKey(tree);
					break;
				case 7:
					deleteKey(tree);
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

	public static void fillWithRandomData(BTree tree) {
		int numberOfValues = ConsoleMenu.readInt("Anzahl der Schlüssel [20]: ", 20);
		int minValue = numberOfValues;
		int defaultValue = minValue * 2;
		int maxValue = ConsoleMenu.readInt("Maximaler Wert [" + defaultValue + "]: ", defaultValue, minValue);
		int seed = ConsoleMenu.readInt("Seed [4711]: ", 4711);
		
		Random rand = new Random((long) seed);
		int randNumber = 0;
		boolean successfulInsert;
		int failedInserts = 0;
		for(int i = 0; i < numberOfValues; i++) {
			successfulInsert = false;
			do {
				randNumber = rand.nextInt(maxValue) + 1;
				successfulInsert = tree.insertEntry(new NodeEntry(randNumber));
				failedInserts++;
			} while(!successfulInsert && failedInserts < 100);
			//System.out.println(randNumber);
			if(failedInserts == 100) {
				System.out.println("Fehler: Zu viele (100) Einfuegeversuche fehlgeschlagen!\n"+
								   "        Es wurden " + (i == 0 ? "keine": "nur " + i) + " Zahlen eingefuegt.");
				break;
			}
			failedInserts = 0;
		}
	}
	
	public static void addOneNumber(BTree tree) {
		if(tree.insertEntry(new NodeEntry(ConsoleMenu.readInt("Einzufuegender Schluessel: ")))) {
			System.out.printf("%40s%n", "Schluessel erfolgreich eingefuegt");
		} else {
			System.out.printf("%40s%n", "Schluessel nicht eingefuegt");
		}
			
	}
	
	public static void searchKey(BTree tree) {
		NodeEntry searchResult = tree.searchKey(ConsoleMenu.readInt("Zu suchender Schluessel: "));
		if(searchResult != null) {
			System.out.printf("Schluessel mit dem Key %d gefunden\n und er enhaelt folgende Daten:\n %s", searchResult.getKey(), searchResult.getData());
		} else {
			System.out.printf("%40s%n", "Schluessel nicht gefunden");
		}
	}
	
	private static void deleteKey(BTree tree) {
		int keyToDelete = ConsoleMenu.readInt("Zu loeschender Schluessel: ", -1, 0);
		System.out.println("Baum vorher");
		tree.showTree();
		if(tree.removeEntry(keyToDelete) != null) {
			System.out.printf("%40s%n", "Schluessel erfolgreich geloescht");
			System.out.println("Baum nachher");
			tree.showTree();
		} else {
			System.out.printf("%40s%n", "Schluessel nicht geloescht");
		}
	}
}
