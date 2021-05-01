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

	public boolean tokenEquals(Tag mark) {
		return this.token.getMark().equals(mark.getDescription());
	}

	public void programa() throws OutOfRange, EmptyCharacter, LexicalError, ManyCharacters, SyntaxError {
		this.conteudo_antes_main();
		this.nextToken();
		this.conteudo();
		this.nextToken();
		if (!this.tokenEquals(Tag.EOF))
			throw new SyntaxError("Unnexpected EOF expected");
	}

	private void conteudo_antes_main() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		boolean sair = false;
		do {
			this.nextToken();
			if (this.first.comando.contains(this.token.getMark()))
				this.comando();
			else if (this.tokenEquals(Tag.INT))
				sair = this.conteudo_antes_main_linha();
			else if (this.tokenEquals(Tag.FLOAT) || this.tokenEquals(Tag.CHAR)) {
				this.nextToken();
				if (!this.tokenEquals(Tag.ID))
					throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
							this.lexicalAnalyser.lineError());
				this.fim_declaracao();
			}
		} while (!sair);
	}

	private boolean conteudo_antes_main_linha()
			throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		this.nextToken();
		if (!this.tokenEquals(Tag.ID))
			throw new SyntaxError(Tag.MAIN.getDescription() + " or Identifier", this.token.getValue(),
					this.token.getLine(), this.lexicalAnalyser.lineError());

		if (this.tokenEquals(Tag.ID)) {
			this.main();
			return true;
		} else {
			this.fim_declaracao();
		}
		return false;
	}

	private void main() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
			throw new SyntaxError("(", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
			throw new SyntaxError(")", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
			throw new SyntaxError("{", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());
		this.nextToken();
		this.conteudo();
		this.retorno();
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_BRACES))
			throw new SyntaxError("}", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());
	}

	private void retorno() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		if (!this.tokenEquals(Tag.RETURN))
			throw new SyntaxError(Tag.RETURN.getDescription(), this.token.getValue(), this.token.getLine(),
					this.lexicalAnalyser.lineError());
		this.nextToken();
		if (!this.first.valor_dado.contains(this.token.getMark()))
			throw new SyntaxError("Data type", this.token.getValue(), this.token.getLine(),
					this.lexicalAnalyser.lineError());
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
			throw new SyntaxError(";", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());
	}

	void conteudo() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		do {
			if (this.first.declaracao.contains(this.token.getMark())) {
				this.declaracao();
				this.nextToken();
			} else if (this.first.comando.contains(this.token.getMark())) {
				this.comando();
			} else
				break;
		} while (true);
	}

	void comando() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		if (this.tokenEquals(Tag.IF)) {
			this.comandoIF();
		} else if (this.tokenEquals(Tag.ELSE)) {
			this.comandoELSE_IF();
		} else if (this.tokenEquals(Tag.WHILE)) {
			this.comandoWHILE();
		} else if (this.tokenEquals(Tag.DO)) {
			this.comandoDOWHILE();
		} else if (this.tokenEquals(Tag.FOR)) {
			this.comandoFOR();
		} else if (this.tokenEquals(Tag.ID)) {
			this.comandoID();
		}
	}

	private void comandoID() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		this.nextToken();
		if (!this.tokenEquals(Tag.ARI_OP_ATTRIBUTION))
			throw new SyntaxError("=", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());
		this.nextToken();
		this.operacao();
		if (!this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
			throw new SyntaxError(";", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());
		this.nextToken();
	}

	private void comandoFOR() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
			throw new SyntaxError("(", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());
		this.nextToken();
		this.declaracao_for();
		this.condicao();
		if (!this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
			throw new SyntaxError(";", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());
		;
		this.nextToken();
		this.operacao_for();
		if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
			throw new SyntaxError(")", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
			throw new SyntaxError("{", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());
		this.nextToken();
		this.conteudo();
		this.nextToken();
	}

	private void operacao_for() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		if (!this.first.operacao_for.contains(this.token.getMark()))
			throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
					this.lexicalAnalyser.lineError());

		this.nextToken();
		if (!this.tokenEquals(Tag.ARI_OP_ATTRIBUTION))
			throw new SyntaxError("=", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());
		this.nextToken();
		this.operacao();
	}

	private void declaracao_for() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		if (!this.first.declaracao_for.contains(token.getMark()))
			throw new SyntaxError("Data type", this.token.getValue(), this.token.getLine(),
					this.lexicalAnalyser.lineError());

		this.nextToken();
		if (!this.tokenEquals(Tag.ID))
			throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
					this.lexicalAnalyser.lineError());
		this.fim_declaracao_for();
	}

	private void fim_declaracao_for() throws SyntaxError, LexicalError, ManyCharacters, EmptyCharacter, OutOfRange {
		this.nextToken();
		if (!first.fim_declaracao_for.contains(token.getMark()))
			throw new SyntaxError("= or ;", this.token.getValue(), this.token.getLine(),
					this.lexicalAnalyser.lineError());

		if (this.tokenEquals(Tag.ARI_OP_ATTRIBUTION)) {
			this.nextToken();
			this.operacao();
			if (!this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
				throw new SyntaxError(";", this.token.getValue(), this.token.getLine(),
						this.lexicalAnalyser.lineError());
		}
	}

	private void comandoDOWHILE() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
			throw new SyntaxError("{", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());

		this.nextToken();
		this.conteudo();

		this.nextToken();
		if (!this.tokenEquals(Tag.WHILE))
			throw new SyntaxError(Tag.WHILE.getDescription() + " after " + Tag.DO.getDescription(),
					this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
			throw new SyntaxError("(", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());

		this.condicao();

		if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
			throw new SyntaxError(")", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());

		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
			throw new SyntaxError(";", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());
		this.nextToken();
	}

	private void comandoWHILE() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
			throw new SyntaxError("(", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());

		this.condicao();

		if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
			throw new SyntaxError(")", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());

		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
			throw new SyntaxError("{", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());

		this.nextToken();
		this.conteudo();
		this.nextToken();
	}

	private void comandoELSE_IF() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		this.nextToken();
		if (this.tokenEquals(Tag.IF))
			this.comandoIF();
		else if (this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES)) {
			this.nextToken();
			this.conteudo();
			this.nextToken();
		}
	}

	private void comandoIF() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
			throw new SyntaxError("(", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());

		this.condicao();

		if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
			throw new SyntaxError(")", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());

		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
			throw new SyntaxError("{", this.token.getValue(), this.token.getLine(), this.lexicalAnalyser.lineError());
		this.nextToken();
		this.conteudo();
		this.nextToken();
	}

	private void condicao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		// Não aceita, por exemplo, if(id)
		this.nextToken();
		this.operacao();
		if (!this.first.operador_relacional.contains(this.token.getMark()))
			throw new SyntaxError("Relational operator", this.token.getValue(), this.token.getLine(),
					this.lexicalAnalyser.lineError());
		this.nextToken();
		this.operacao();
	}

	private void declaracao() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		// Aqui ja garante que começa com tipo_dado
		this.nextToken();
		if (!this.tokenEquals(Tag.ID))
			throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
					this.lexicalAnalyser.lineError());

		this.fim_declaracao();
	}

	private void fim_declaracao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		this.nextToken();
		if (!this.first.fim_declaracao.contains(this.token.getMark()))
			throw new SyntaxError("= or ;", this.token.getValue(), this.token.getLine(),
					this.lexicalAnalyser.lineError());

		if (this.tokenEquals(Tag.ARI_OP_ATTRIBUTION)) {
			this.valor_atribuicao();
			this.declaracao_inline();
		} else if (this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES)) {
			this.nextToken();
			if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
				throw new SyntaxError(")", this.token.getValue(), this.token.getLine(),
						this.lexicalAnalyser.lineError());

			this.nextToken();
			if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
				throw new SyntaxError("{", this.token.getValue(), this.token.getLine(),
						this.lexicalAnalyser.lineError());
			this.nextToken();
			this.conteudo();
			this.retorno();
			this.nextToken();
			if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_BRACES))
				throw new SyntaxError("}", this.token.getValue(), this.token.getLine(),
						this.lexicalAnalyser.lineError());
		}

		// Se for SEMICOLON ta de boa, nem precisa fazer nada
	}

	private void valor_atribuicao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		this.nextToken();
		if (!this.tokenEquals(Tag.CHARACTER) && !this.first.operacao.contains(this.token.getMark()))
			throw new SyntaxError("Invalid attribuition declaration");

		if (this.tokenEquals(Tag.CHARACTER)) {
			this.nextToken();
			return;
		} else
			this.operacao();
	}

	private void declaracao_inline() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		if (!this.first.declaracao_inline.contains(this.token.getMark()))
			throw new SyntaxError("Syntax error. ',' or ';' expected.");

		if (this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
			return;

		this.nextToken();
		if (!this.tokenEquals(Tag.ID))
			throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
					this.lexicalAnalyser.lineError());
		this.fim_declaracao();
	}

	private void operacao() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		if (!this.first.operacao.contains(this.token.getMark()))
			throw new SyntaxError("Invalid attribuition declaration at line" + this.lexicalAnalyser.lineError());

		if (this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES)) {
			this.operacao_linha();
		} else {
			this.expressao_operacao();
			this.extensor_operacao();
		}
	}

	private void operacao_linha() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		this.nextToken();
		if (!this.first.operacao_linha.contains(this.token.getMark()))
			throw new SyntaxError("operating", this.token.getValue(), this.token.getLine(),
					this.lexicalAnalyser.lineError());
		this.operacao();

		if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES) && !this.first.operacao.contains(this.token.getMark()))
			throw new SyntaxError(") or ;", this.token.getValue(), this.token.getLine(),
					this.lexicalAnalyser.lineError());
		this.nextToken();
		this.extensor_operacao();
	}

	private void expressao_operacao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		if (!this.first.expressao_operacao.contains(this.token.getMark()))
			throw new SyntaxError("operating", this.token.getValue(), this.token.getLine(),
					this.lexicalAnalyser.lineError());
		this.expressao_operacao_linha();
	}

	private void expressao_operacao_linha()
			throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		this.nextToken();
		if (!this.first.expressao_operacao_linha.contains(this.token.getMark()))
			return;
		this.nextToken();
		this.operacao();
	}

	private void extensor_operacao() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		if (!this.first.extensor_operacao.contains(this.token.getMark()))
			return;
		this.nextToken();
		this.operacao();
	}
}
