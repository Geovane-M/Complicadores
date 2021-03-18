package IDE;

import java.io.IOException;

import ErrorWarnings.EmptyCharacter;

// -----------------------------------------
/*
• Ler arquivo texto com código na linguagem C
• Implementar método que retorne próximo token do texto, indicando seu tipo (“getNextToken(): Token”).
• Implementar método que imprima todos os tokens encontrados e respectivos tipos.
• O programa deverá imprimir mensagens de erro adequada ao problema encontrado:
o Mensagem deve ser específica: que tipo de erro? (ex. nome de variável inválida, operado lógico inválido, etc.)
o Indicar último token lido.
o Outras possibilidades: (1) linha do erro, (2) coluna do erro, etc.
*/

import ErrorWarnings.LexicalError;
import ErrorWarnings.ManyCharacters;
import ErrorWarnings.NoTarget;
import ErrorWarnings.OutOfRange;
import lexicalAnalyzer.LexicalAnalyzer;
import lexicalAnalyzer.Token;

public class IDE {
	public static void main(String[] args) throws IOException {

		try {
			LexicalAnalyzer la = new LexicalAnalyzer();
			Token token;
			while (true) {
				token = la.nextToken();
				if (token == null) {
					break;
				} else {
					System.out.println(token);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfRange e) {
			e.printStackTrace();
		} catch (ManyCharacters e) {
			e.printStackTrace();
		} catch (LexicalError e) {
			e.printStackTrace();
		} catch (EmptyCharacter e) {
			e.printStackTrace();
		} catch (NoTarget e) {
			e.printStackTrace();
		}
	}
}
