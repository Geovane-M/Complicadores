package IDE;

import java.io.IOException;

import ErrorWarnings.EmptyCharacter;
import ErrorWarnings.LexicalError;
import ErrorWarnings.ManyCharacters;
import ErrorWarnings.NoTarget;
import ErrorWarnings.OutOfRange;
import ErrorWarnings.SyntaxError;
import Parser.Parser;
public class IDE {
	public static void main(String[] args){
		Parser parser;
		
		try {
			long initialTime = System.nanoTime();
			parser = new Parser();
			parser.programa();
			System.out.printf("Successfully compiled in %fs", (System.nanoTime() - initialTime)*Math.pow(10, -9));
		} catch (IOException | NoTarget | OutOfRange | EmptyCharacter | LexicalError | ManyCharacters | SyntaxError e) {
			e.printStackTrace();
		}
	}
	
}
