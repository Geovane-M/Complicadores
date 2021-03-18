package ErrorWarnings;

import lexicalAnalyzer.Tag;

public class OutOfRange extends Exception{

	private static final long serialVersionUID = 1L;

	public OutOfRange(String num, String type, int line, String column) {
		System.err.println("LexicalError: The literal "+num+" of type "+((type.compareTo(Tag.INTEGER.getDescription()) == 0)? "int": "float")+" is out of range at line " +line+":\n"+ column
				+ ((type.compareTo(Tag.INTEGER.getDescription()) == 0)? "\nThe valid range is -2,147,483,648 to 2,147,483,647. \nSee (https://en.wikipedia.org/wiki/C_data_types#Main_types) for more details": 
					"\nThe valid range is 1.2E-38 to 3.4E+38.  \nSee (https://en.wikipedia.org/wiki/C_data_types#Main_types) for more details"));
	}

}
