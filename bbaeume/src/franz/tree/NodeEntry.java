package franz.tree;

public class NodeEntry {

	private int key;
	private String data;
	
	/**
	 * Konstruktor fuer einen B-Baum-Eintrag
	 * @param key Schluessel mit dem der Eintrag erzeugt werden soll
	 */
	public NodeEntry(int key) {
		this(key, "");
	}
	/**
	 * Konstruktor fuer einen B-Baum-Eintrag
	 * @param key Schluessel mit dem der Eintrag erzeugt werden soll
	 * @param data Daten die dem Schluessel beigefuegt werden sollen
	 */
	public NodeEntry(int key, String data) {
		this.key = key;
		this.data = data;
	}
	
	/**
	 * Funktion die den Schluessel des Eintrags zurueckgibt
	 * @return Schluessel des Eintrags
	 */
	public int getKey() {
		return key;
	}
	
	/**
	 * Funktion die die Daten des Eintrags zurueckgibt
	 * @return Daten des Eintrags
	 */
	public String getData() {
		return data;
	}

}
