package franz;

import java.util.Random;

import franz.tree.BTree;
import franz.tree.NodeEntry;
import franz.utils.ConsoleMenu;

public class BTreeStarter {

	public static void main(String[] args) {
		System.out.printf("%20s%n", "Initiale Erzeugung des B-Baums");
		BTree tree = new BTree(ConsoleMenu.readInt("Ordnung m des B-Baums [3]: ", 3, 3));
		
		ConsoleMenu console = new ConsoleMenu(tree);

		console.addMenuItem("Erzeuge einen neuen B-Baum", 1);
		console.addMenuItem("Zeichne Baum", 2);
		console.addMenuItem("Fuelle den Baum mit Zufallszahlen", 3);
		console.addMenuItem("Zahl hinzufuegen", 4);
		console.addMenuItem("Zahl suchen", 6);
		console.addMenuItem("Zahl loeschen", 7);
		console.addMenuItem("Zeige B-Baum-Statistik", 9);
		console.addMenuItem("Beenden", 0);
		
		//schneller für Tests
		fillWithRandomData(tree);
		tree.printTree();
		
		int choice = -1;
		
		while(choice != 0) {
			choice = console.showMenu();
			System.out.print("+++++++++++++++++++++++++++++++++++++++++++++++++\n");
			//System.out.println("Auswahl: " + choice);
			switch (choice) {
				case 1:
					tree = new BTree(ConsoleMenu.readInt("Ordnung m des B-Baums [3]: ", 3, 2));
					console.setTree(tree);
					break;
				case 2:
					tree.printTree();
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
				case 7:
					deleteKey(tree);
					break;
				case 9:
					tree.printStats();
					break;
				default:
					break;
			}
		}
		
		System.out.println("Anwendung wird beendet...");
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Beendet...");
	}

	public static void fillWithRandomData(BTree tree) {
		int minNumberOfValues = ConsoleMenu.readInt("Anzahl der Schluessel [20]: ", 20);
		int maxValue = ConsoleMenu.readInt("Maximaler Wert [" + minNumberOfValues*2 + "]: ", minNumberOfValues*2, minNumberOfValues);
		int seed = ConsoleMenu.readInt("Seed [4711]: ", 4711);
		
		Random rand = new Random((long) seed);
		int randNumber = 0;
		boolean successfulInsert = false;
		int failedInserts = 0;
		for(int i = 0; i < minNumberOfValues; i++) {
			do {
				randNumber = rand.nextInt(maxValue) + 1;
				successfulInsert = tree.insertEntry(randNumber);
				failedInserts++;
			} while(!successfulInsert && failedInserts < 100);
			System.out.println(randNumber);
			if(failedInserts == 100) {
				System.out.println("Fehler: Zu viele (100) Einfuegeversuche fehlgeschlagen!\n"+
								   "        Es wurden " + (i == 0 ? "keine": "nur " + i) + " Zahlen eingefuegt.");
				break;
			}
			failedInserts = 0;
		}
		tree.printTree();
	}
	
	public static void addOneNumber(BTree tree) {
		if(tree.insertEntry(new NodeEntry(ConsoleMenu.readInt("Einzufuegender Schluessel: ")))) {
			System.out.printf("%40s%n", "Schluessel erfolgreich eingefuegt");
		} else {
			System.out.printf("%40s%n", "Schluessel nicht eingefuegt");
		}
		tree.printTree();			
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
		tree.printTree();
		NodeEntry removedEntry = tree.removeEntry(keyToDelete);
		if(removedEntry != null) {
			System.out.printf("%40s%n", "Schluessel erfolgreich geloescht");
			System.out.println("Baum nachher");
			tree.printTree();
			System.out.println("Geloeschter Schluessel:");
			System.out.println("      key: " + removedEntry.getKey());
		} else {
			System.out.printf("%40s%n", "Schluessel nicht geloescht");
		}
	}
}
