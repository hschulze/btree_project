package franz.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ConsoleMenu {

	private HashMap<Integer, String> menuItems;
	
	public ConsoleMenu() {
		menuItems = new HashMap<Integer, String>();
	}
	
	public void addMenuItem(String text, int number) {
		menuItems.put(new Integer(number), text);
	}
	
	public int showMenu() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("+++++++++++++++++++++++++++++++++++++++++++++++++\n");
		
		for(int i = 1; i < 10; i++) {
			if(menuItems.get(new Integer(i)) != null) {
				sb.append(String.format("%45s [%d]\n", menuItems.get(new Integer(i)), i));
			} else {
				sb.append("\n");
			}
		}
		
		if(menuItems.get(new Integer(0)) != null)
			sb.append(String.format("%45s [%d]\n", menuItems.get(new Integer(0)), 0));
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
	
}
