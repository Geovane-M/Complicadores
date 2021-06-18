package IDE;

import ErrorWarnings.EmptyCharacter;
import ErrorWarnings.ManyCharacters;
import ErrorWarnings.SemanticError;
import ErrorWarnings.LexicalError;
import ErrorWarnings.SyntaxError;
import ErrorWarnings.OutOfRange;
import ErrorWarnings.NoTarget;
import java.io.IOException;
import Parser.Parser;

public class IDE {
	public static void main(String[] args) throws OutOfRange, ManyCharacters, LexicalError, EmptyCharacter{
		Parser parser;
		try {
			long initialTime = System.nanoTime();
			parser = new Parser();
			parser.programa();
			System.out.printf("Successfully compiled in %fs", (System.nanoTime() - initialTime)*Math.pow(10, -9));
		} catch (IOException | NoTarget | OutOfRange | EmptyCharacter | LexicalError | ManyCharacters | SyntaxError | SemanticError e) {
			e.printStackTrace();
		}
	}
}
