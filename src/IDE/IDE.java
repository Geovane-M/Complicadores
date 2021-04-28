package IDE;

import java.io.IOException;

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

import Parser.Parser;

public class IDE {
	public static void main(String[] args) throws IOException {
		// CRIAR EOF
//		try {
//			Scanner scanner = new Scanner();
//			Token token;
//			while (true) {
//				token = scanner.nextToken();
//				if (token == null) {
//					break;
//				} else {
//					System.out.println(token);
//				}
//			}
		Parser parser = new Parser();
		parser.programa();
//
//		} catch ()
	}
}
