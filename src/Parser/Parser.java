package Parser;

import ErrorWarnings.*;

import lexicalAnalyzer.LexicalAnalyser;
import lexicalAnalyzer.Tag;
import lexicalAnalyzer.Token;

import java.io.IOException;

public class Parser {
	private LexicalAnalyser lexicalAnalyser;
	private Token token;
	private First first = new First();

	public Parser() throws IOException, NoTarget {
		this.lexicalAnalyser = new LexicalAnalyser();
	}

	private void nextToken() throws OutOfRange, EmptyCharacter, LexicalError, ManyCharacters {
		this.token = lexicalAnalyser.nextToken();
	}

	public boolean tokenEquals(String mark) {
		return this.token.getMark().equals(mark);
	}

	public void programa() throws OutOfRange, EmptyCharacter, LexicalError, ManyCharacters, SyntaxError, SyntaxErrorMain {
		this.nextToken();
		if (!tokenEquals("INT"))
			throw new SyntaxErrorMain(this.token.getLine(), this.lexicalAnalyser.lineError());

		nextToken();
		if (!tokenEquals("MAIN"))
			throw new SyntaxErrorMain(this.token.getLine(), this.lexicalAnalyser.lineError() + "\nin token: "+ this.token.getValue());

		nextToken();
		if (!tokenEquals("SP_CHAR_OPEN_PARENTHESES"))
			throw new SyntaxErrorMain(this.token.getLine(), this.lexicalAnalyser.lineError());

		nextToken();
		if (!tokenEquals("SP_CHAR_CLOSE_PARENTHESES"))
			throw new SyntaxErrorMain(this.token.getLine(), this.lexicalAnalyser.lineError());

		nextToken();
		if (!tokenEquals("SP_CHAR_OPEN_BRACES"))
			throw new SyntaxErrorMain(this.token.getLine(), this.lexicalAnalyser.lineError());

		nextToken();
		if (!first.conteudo.contains(token.getMark()))
			throw new SyntaxError("unnexpected '" + token.getValue() + "'");
		conteudo();
	}

	void conteudo() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		do {
			if (tokenEquals("EOF"))
				throw new SyntaxError("Unnexpected End Of File");
			if (first.declaracao.contains(token.getMark())) {
				declaracao();
				nextToken();
			} else if (first.comando.contains(token.getMark())) {
				comando();
			}
		} while (!tokenEquals("SP_CHAR_CLOSE_BRACES"));
	}

	void comando() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		if (tokenEquals("IF")) {
			comandoIF();
		} else if (tokenEquals("ELSE")) {
			comandoELSE_IF();
		} else if (tokenEquals("WHILE")) {
			comandoWHILE();
		} else if (tokenEquals("DO")) {
			comandoDOWHILE();
		} else if (tokenEquals("FOR")) {
			comandoFOR();
		} else if (tokenEquals("ID")) {
			comandoID();
		}
	}

	private void comandoID() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		nextToken();
		if (!tokenEquals("ARI_OP_ATTRIBUTION"))
			throw new SyntaxError("Syntax error. '=' expected");

		nextToken();
		operacao();

		if (!tokenEquals("SP_CHAR_CLOSE_SEMICOLON"))
			throw new SyntaxError("Syntax error. ';' expected");

		nextToken();
	}

	private void comandoFOR() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		nextToken();
		if (!tokenEquals("SP_CHAR_OPEN_PARENTHESES"))
			throw new SyntaxError("Syntax error. '(' expected");

		nextToken();
		declaracao_for();

		condicao();

		if (!tokenEquals("SP_CHAR_CLOSE_SEMICOLON"))
			throw new SyntaxError("Syntax error. ';' expected");

		nextToken();
		operacao_for();

		if (!tokenEquals("SP_CHAR_CLOSE_PARENTHESES"))
			throw new SyntaxError("Syntax error. ')' expected");

		nextToken();
		if (!tokenEquals("SP_CHAR_OPEN_BRACES"))
			throw new SyntaxError("Syntax error. '{' expected");

		nextToken();
		conteudo();
		nextToken();
	}

	private void operacao_for() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		if (!first.operacao_for.contains(token.getMark()))
			throw new SyntaxError("Syntax error. ID expected");

		nextToken();
		if (!tokenEquals("ARI_OP_ATTRIBUTION"))
			throw new SyntaxError("Syntax error. ARI_OP_ATTRIBUTION expected");

		nextToken();
		operacao();
	}

	private void declaracao_for() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		if (!first.declaracao_for.contains(token.getMark()))
			throw new SyntaxError("Syntax error. data type expected");

		nextToken();
		if (!tokenEquals("ID"))
			throw new SyntaxError("Syntax error. 'ID' expected");

		fim_declaracao_for();
	}

	private void fim_declaracao_for() throws SyntaxError, LexicalError, ManyCharacters, EmptyCharacter, OutOfRange {
		nextToken();
		if (!first.fim_declaracao_for.contains(token.getMark()))
			throw new SyntaxError("Syntax error. '=' or ';' expected.");

		if (tokenEquals("ARI_OP_ATTRIBUTION")) {
			nextToken();
			operacao();
			if (!tokenEquals("SP_CHAR_CLOSE_SEMICOLON"))
				throw new SyntaxError("Syntax error. ';' expected.");
		}
	}

	private void comandoDOWHILE() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		nextToken();
		if (!tokenEquals("SP_CHAR_OPEN_BRACES"))
			throw new SyntaxError("Syntax error. '{' expected");

		nextToken();
		conteudo();

		nextToken();
		if (!tokenEquals("WHILE"))
			throw new SyntaxError("Syntax error. 'while' after 'do' expected");

		nextToken();
		if (!tokenEquals("SP_CHAR_OPEN_PARENTHESES"))
			throw new SyntaxError("Syntax error. '(' expected");

		condicao();

		if (!tokenEquals("SP_CHAR_CLOSE_PARENTHESES"))
			throw new SyntaxError("Syntax error. ')' expected");

		nextToken();
		if (!tokenEquals("SP_CHAR_CLOSE_SEMICOLON"))
			throw new SyntaxError("Syntax error. ';' expected");

		nextToken();
	}

	private void comandoWHILE() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		nextToken();
		if (!tokenEquals("SP_CHAR_OPEN_PARENTHESES"))
			throw new SyntaxError("Syntax error. '(' expected");

		condicao();

		if (!tokenEquals("SP_CHAR_CLOSE_PARENTHESES"))
			throw new SyntaxError("Syntax error. ')' expected. Given: " + token.getMark());

		nextToken();
		if (!tokenEquals("SP_CHAR_OPEN_BRACES"))
			throw new SyntaxError("Syntax error. '{' expected. Given: " + token.getMark());

		nextToken();
		conteudo();
		nextToken();
	}

	private void comandoELSE_IF() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		nextToken();
		if (tokenEquals("IF"))
			comandoIF();
		else if (tokenEquals("SP_CHAR_OPEN_BRACES")) {
			nextToken();
			conteudo();
			nextToken();
		}
	}

	private void comandoIF() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		nextToken();
		if (!tokenEquals("SP_CHAR_OPEN_PARENTHESES"))
			throw new SyntaxError("Syntax error. '(' expected");

		condicao();

		if (!tokenEquals("SP_CHAR_CLOSE_PARENTHESES"))
			throw new SyntaxError("Syntax error. ')' expected");

		nextToken();
		if (!tokenEquals("SP_CHAR_OPEN_BRACES"))
			throw new SyntaxError("Syntax error. '{' expected");

		nextToken();
		conteudo();
		nextToken();
	}

	private void condicao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		// Não aceita, por exemplo, if(id)
		nextToken();
		operacao();

		if (!first.operador_relacional.contains(token.getMark()))
			throw new SyntaxError("Syntax error. Relational operator expected");

		nextToken();
		operacao();
	}

	private void declaracao() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		// Aqui ja garante que começa com tipo_dado
		nextToken();
		if (!tokenEquals("ID"))
			throw new SyntaxError("Invalid declaration. identifier expected.");

		fim_declaracao();
	}

	private void fim_declaracao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		nextToken();
		if (!first.fim_declaracao.contains(token.getMark()))
			throw new SyntaxError("Invalid declaration. '=' or ';' expected.");

		if (tokenEquals("ARI_OP_ATTRIBUTION")) {
			valor_atribuicao();
			declaracao_inline();
		}

		// Se for SEMICOLON ta de boa, nem precisa fazer nada
	}

	private void valor_atribuicao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		nextToken();
		if (!tokenEquals("CHARACTER") && !first.operacao.contains(token.getMark()))
			throw new SyntaxError("Invalid attribuition declaration");

		if (tokenEquals("CHARACTER")) {
			nextToken();
			return;
		} else
			operacao();
	}

	private void declaracao_inline() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		if (!first.declaracao_inline.contains(token.getMark()))
			throw new SyntaxError("Syntax error. ',' or ';' expected.");

		if (tokenEquals("SP_CHAR_CLOSE_SEMICOLON"))
			return;

		nextToken();
		if (!tokenEquals("ID"))
			throw new SyntaxError("Syntax error. Identifier expected.");

		fim_declaracao();
	}

	private void operacao() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		if (!first.operacao.contains(token.getMark()))
			throw new SyntaxError("Invalid attribuition declaration");

		if (tokenEquals("SP_CHAR_OPEN_PARENTHESES")) {
			operacao_linha();
		} else {
			expressao_operacao();
			extensor_operacao();
		}
	}

	private void operacao_linha() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		nextToken();
		if (!first.operacao_linha.contains(token.getMark()))
			throw new SyntaxError("Invalid operation");

		operacao();

		if (!tokenEquals("SP_CHAR_CLOSE_PARENTHESES") && !first.operacao.contains(token.getMark()))
			throw new SyntaxError("Syntax error. Expected ')' oir ';'");

		nextToken();
		extensor_operacao();
	}

	private void expressao_operacao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		if (!first.expressao_operacao.contains(token.getMark()))
			throw new SyntaxError("Syntax erro. Operando expected.");

		expressao_operacao_linha();
	}

	private void expressao_operacao_linha()
			throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		nextToken();
		if (!first.expressao_operacao_linha.contains(token.getMark()))
			return;

		nextToken();
		operacao();
	}

	private void extensor_operacao() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		if (!first.extensor_operacao.contains(token.getMark()))
			return;

		nextToken();
		operacao();
	}
}
