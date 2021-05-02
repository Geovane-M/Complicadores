package IDE;

import java.io.IOException;

import ErrorWarnings.EmptyCharacter;
import ErrorWarnings.LexicalError;
import ErrorWarnings.ManyCharacters;
import ErrorWarnings.NoTarget;
import ErrorWarnings.OutOfRange;
import ErrorWarnings.SyntaxError;
import Parser.Parser;
import lexicalAnalyzer.LexicalAnalyzer;
import lexicalAnalyzer.Tag;
import lexicalAnalyzer.Token;

public class IDE {
	public static void main(String[] args){
		Parser parser;
//		int i = 0;
//		Token t;
//		try {
//			LexicalAnalyzer l  = new LexicalAnalyzer();
//		while(i <30) {
//			t = l.nextToken();
//			System.out.println(t);
//			if(t.getMark().equals(Tag.EOF)) {
//				break;
//			}
//			i++;
//		}
//		} catch (OutOfRange e) {
//			e.printStackTrace();
//		} catch (ManyCharacters e) {
//			e.printStackTrace();
//		} catch (LexicalError e) {
//			e.printStackTrace();
//		} catch (EmptyCharacter e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (NoTarget e) {
//			e.printStackTrace();
//		}

		try {
			long initialTime = System.nanoTime();
			parser = new Parser();
			parser.programa();
			System.out.printf("Successfully compiled in %fs", (System.nanoTime() - initialTime)*Math.pow(10, -9));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoTarget e) {
			e.printStackTrace();
		}catch (OutOfRange e) {
			e.printStackTrace();
		} catch (EmptyCharacter e) {
			e.printStackTrace();
		} catch (LexicalError e) {
			e.printStackTrace();
		} catch (ManyCharacters e) {
			e.printStackTrace();
		} catch (SyntaxError e) {
			e.printStackTrace();
		}
	}
	
}
