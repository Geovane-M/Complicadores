package lexicalAnalyzer;

import java.util.Hashtable;

public class ReservedWords {
	private Hashtable<String, Tag> table;

	public ReservedWords() {
		this.table = new Hashtable<String, Tag>();
		this.table.put("main", Tag.MAIN);
		this.table.put("if", Tag.IF);
		this.table.put("else", Tag.ELSE);
		this.table.put("while", Tag.WHILE);
		this.table.put("do", Tag.DO);
		this.table.put("for", Tag.FOR);
		this.table.put("int", Tag.INT);
		this.table.put("float", Tag.FLOAT);
		this.table.put("char", Tag.CHAR);
		this.table.put("return" ,Tag.RETURN);
//		this.table.put("" ,Tag);
	}
	
	public boolean containsKey(String tag) {
		return this.table.containsKey(tag);
	}
	
	public Tag getTag(String tag) {
		return this.table.get(tag);
	}

}
