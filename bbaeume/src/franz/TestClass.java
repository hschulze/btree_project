package franz;

import franz.tree.BNode;
import franz.tree.NodeEntry;

public class TestClass {

	
	public static void main(String[] args) {
		BNode node = new BNode();
		
		node.addEntry(new NodeEntry(1));
		node.addEntry(new NodeEntry(2));
		node.addEntry(new NodeEntry(3));
		node.addEntry(1, new NodeEntry(9));
		
		BNode test = node;
		
		if(test == node)
			System.out.println("JA");
			
		for(int i = 0; i < node.getNumberOfEntrys(); i++) {
			System.out.println(i + " " + node.getEntry(i).getKey());
		}

	}

}
