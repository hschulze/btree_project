package franz.tree;

public class NodeEntry {

	private int key;
	private String data;
	
	public NodeEntry(int key) {
		this(key, "");
	}
	
	public NodeEntry(int key, String data) {
		this.key = key;
		this.data = data;
	}
	
	public int getKey() {
		return key;
	}
	
	public String getData() {
		return data;
	}

}
