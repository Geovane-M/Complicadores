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
		return this.token.getMark().equals(mark);
	}

	public void programa() throws OutOfRange, EmptyCharacter, LexicalError, ManyCharacters, SyntaxError {
		this.conteudo_antes_main();
		this.nextToken();
		this.conteudo();
		if (!this.tokenEquals(Tag.EOF))
			throw new SyntaxError("Unexpected " + this.token.getValue() + " at " + this.token.getLine() + ":" + this.token.getColumn());
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
							this.token.getColumn());
				this.fim_declaracao();
			}
			else if(this.tokenEquals(Tag.EOF))
				throw new SyntaxError("Syntax error! 'main' is not defined");
		} while (!sair);
	}

	private boolean conteudo_antes_main_linha()
			throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		this.nextToken();
		if (!this.tokenEquals(Tag.MAIN) && !this.tokenEquals(Tag.ID))
			throw new SyntaxError(Tag.MAIN.getDescription() + " or Identifier", this.token.getValue(),
					this.token.getLine(), this.token.getColumn());

		if (this.tokenEquals(Tag.MAIN)) {
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
			throw new SyntaxError("'('", this.token.getValue(), this.token.getLine(), this.token.getColumn());
		this._parametros();
		if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
			throw new SyntaxError("')'", this.token.getValue(), this.token.getLine(), this.token.getColumn());
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
			throw new SyntaxError("'{'", this.token.getValue(), this.token.getLine(), this.token.getColumn());
		this.nextToken();
		this.conteudo();
		this.retorno();
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_BRACES))
			throw new SyntaxError("'}'", this.token.getValue(), this.token.getLine(), this.token.getColumn());
	}

	private void _parametros() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		nextToken();
		if (!this.first.tipo_dado.contains(token.getMark()))
			return;

		nextToken();
		if (!tokenEquals(Tag.ID))
            throw new SyntaxError("'Identifier'", this.token.getValue(), this.token.getLine(), this.token.getColumn());

//		nextToken();
		fim_parametros();
	}

	private void passada_de_parametros() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		if (!this.first.passada_de_parametros.contains(token.getMark()))
			return;

		fim_passada_de_parametros();
	}

	private void fim_passada_de_parametros() throws SyntaxError, LexicalError, ManyCharacters, EmptyCharacter, OutOfRange {
		nextToken();
		if (this.first.fim_passada_de_parametros.contains(token.getMark())){
			nextToken();
			if (!tokenEquals(Tag.ID))
				throw new SyntaxError("'Identifier'", this.token.getValue(), this.token.getLine(), this.token.getColumn());

			fim_passada_de_parametros();
		}
	}

	private void atribuicao_ou_chamada() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		if(!this.first.atribuicao_ou_chamada.contains(this.token.getMark())
			&& !this.first.operador_aritmetico.contains(this.token.getMark()))
            throw new SyntaxError("function call or variable atribuition after Identifier", this.token.getValue(), this.token.getLine(), this.token.getColumn());

		if (this.first.atribuicao_ou_chamada.contains(this.token.getMark())){
			nextToken();
			passada_de_parametros();

			if (!tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
                throw new SyntaxError("')'", this.token.getValue(), this.token.getLine(), this.token.getColumn());

			nextToken();
			if (!tokenEquals(Tag.SP_CHAR_SEMICOLON))
                throw new SyntaxError("';'", this.token.getValue(), this.token.getLine(), this.token.getColumn());
			nextToken();
		} else{
			operador_nested();
			if (!tokenEquals(Tag.ARI_OP_ATTRIBUTION))
                throw new SyntaxError("'='", this.token.getValue(), this.token.getLine(), this.token.getColumn());
			nextToken();
			operador_nested();
			operacao();
			if (!tokenEquals(Tag.SP_CHAR_SEMICOLON))
                throw new SyntaxError("';'", this.token.getValue(), this.token.getLine(), this.token.getColumn());
			nextToken();
		}
	}

	private void operador_nested() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange {
		if (this.tokenEquals(Tag.ARI_OP_ADDITION) ||
				this.tokenEquals(Tag.ARI_OP_DIVISION) ||
				this.tokenEquals(Tag.ARI_OP_MULTIPLICATION) ||
				this.tokenEquals(Tag.ARI_OP_SUBTRACTION))
			nextToken();
	}

	private void fim_parametros() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		nextToken();
		if (!this.first.fim_parametros.contains(token.getMark()))
			return;

		nextToken();
		if (!this.first.tipo_dado.contains(token.getMark()))
            throw new SyntaxError("'Data type'", this.token.getValue(), this.token.getLine(), this.token.getColumn());

		nextToken();
		if (!tokenEquals(Tag.ID))
            throw new SyntaxError("';'", this.token.getValue(), this.token.getLine(), this.token.getColumn());

		fim_parametros();
	}

	private void retorno() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		if (!this.tokenEquals(Tag.RETURN))
			throw new SyntaxError(Tag.RETURN.getDescription(), this.token.getValue(), this.token.getLine(),
					this.token.getColumn());
		this.nextToken();
		if (!this.first.operando.contains(this.token.getMark()))
			throw new SyntaxError("'Id' or 'data value'", this.token.getValue(), this.token.getLine(),
					this.token.getColumn());
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
			throw new SyntaxError("';'", this.token.getValue(), this.token.getLine(), this.token.getColumn());
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
			this.nextToken();
			this.atribuicao_ou_chamada();
		}
	}

	private void comandoFOR() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
			throw new SyntaxError("'('", this.token.getValue(), this.token.getLine(), this.token.getColumn());
		this.nextToken();
		this.declaracao_for();
		this.condicao();
		if (!this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
			throw new SyntaxError("';'", this.token.getValue(), this.token.getLine(), this.token.getColumn());
		;
		this.nextToken();
		this.operacao_for();
		if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
			throw new SyntaxError("')'", this.token.getValue(), this.token.getLine(), this.token.getColumn());
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
			throw new SyntaxError("'{'", this.token.getValue(), this.token.getLine(), this.token.getColumn());
		this.nextToken();
		this.conteudo();
		this.nextToken();
	}

	private void operacao_for() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		if (!this.first.operacao_for.contains(this.token.getMark()))
			throw new SyntaxError("'Identifier'", this.token.getValue(), this.token.getLine(),
					this.token.getColumn());

		this.nextToken();
		operador_nested();
		if (!this.tokenEquals(Tag.ARI_OP_ATTRIBUTION))
			throw new SyntaxError("'='", this.token.getValue(), this.token.getLine(), this.token.getColumn());
		this.nextToken();
		operador_nested();
		this.operacao();
	}

	private void declaracao_for() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		if (!this.first.tipo_dado.contains(token.getMark()))
			throw new SyntaxError("'Data type'", this.token.getValue(), this.token.getLine(),
					this.token.getColumn());

		this.nextToken();
		if (!this.tokenEquals(Tag.ID))
			throw new SyntaxError("'Identifier'", this.token.getValue(), this.token.getLine(),
					this.token.getColumn());
		this.fim_declaracao_for();
	}

	private void fim_declaracao_for() throws SyntaxError, LexicalError, ManyCharacters, EmptyCharacter, OutOfRange {
		this.nextToken();
		if (!first.fim_declaracao_for.contains(token.getMark()))
			throw new SyntaxError("'=' or ';'", this.token.getValue(), this.token.getLine(),
					this.token.getColumn());

		if (this.tokenEquals(Tag.ARI_OP_ATTRIBUTION)) {
//			this.nextToken();
			this.valor_atribuicao();

			this.declaracao_for_inline();
		}
	}

	private void declaracao_for_inline() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		if (!this.first.declaracao_for_inline.contains(this.token.getMark()))
			throw new SyntaxError("';'", this.token.getValue(), this.token.getLine(), this.token.getColumn());

		if (this.tokenEquals(Tag.SP_CHAR_COMMA)){
			nextToken();
			if (!this.tokenEquals(Tag.ID))
				throw new SyntaxError("'Identifier'", this.token.getValue(), this.token.getLine(), this.token.getColumn());

			fim_declaracao_for();
		}
	}

	private void comandoDOWHILE() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
			throw new SyntaxError("'{'", this.token.getValue(), this.token.getLine(), this.token.getColumn());

		this.nextToken();
		this.conteudo();

		this.nextToken();
		if (!this.tokenEquals(Tag.WHILE))
			throw new SyntaxError(Tag.WHILE.getDescription() + " after " + Tag.DO.getDescription(),
					this.token.getValue(), this.token.getLine(), this.token.getColumn());
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
			throw new SyntaxError("'('", this.token.getValue(), this.token.getLine(), this.token.getColumn());

		this.condicao();

		if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
			throw new SyntaxError("')'", this.token.getValue(), this.token.getLine(), this.token.getColumn());

		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
			throw new SyntaxError("';'", this.token.getValue(), this.token.getLine(), this.token.getColumn());
		this.nextToken();
	}

	private void comandoWHILE() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
			throw new SyntaxError("'('", this.token.getValue(), this.token.getLine(), this.token.getColumn());

		this.condicao();

		if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
			throw new SyntaxError("')'", this.token.getValue(), this.token.getLine(), this.token.getColumn());

		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
			throw new SyntaxError("'{'", this.token.getValue(), this.token.getLine(), this.token.getColumn());

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
			throw new SyntaxError("'('", this.token.getValue(), this.token.getLine(), this.token.getColumn());

		this.condicao();

		if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
			throw new SyntaxError("')'", this.token.getValue(), this.token.getLine(), this.token.getColumn());

		this.nextToken();
		if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
			throw new SyntaxError("'{'", this.token.getValue(), this.token.getLine(), this.token.getColumn());
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
					this.token.getColumn());
		this.nextToken();
		this.operacao();
	}

	private void declaracao() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		// Aqui ja garante que começa com tipo_dado
		this.nextToken();
		if (!this.tokenEquals(Tag.ID))
			throw new SyntaxError("'Identifier'", this.token.getValue(), this.token.getLine(),
					this.token.getColumn());

		this.fim_declaracao();
	}

	private void fim_declaracao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		this.nextToken();
		if (!this.first.fim_declaracao.contains(this.token.getMark()))
			throw new SyntaxError("'=' or ';'", this.token.getValue(), this.token.getLine(),
					this.token.getColumn());

		if (this.tokenEquals(Tag.ARI_OP_ATTRIBUTION)) {
			this.valor_atribuicao();
			this.declaracao_inline();
		} else if (this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES)) {
			this._parametros();
			if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
				throw new SyntaxError("')'", this.token.getValue(), this.token.getLine(),
						this.token.getColumn());

			this.nextToken();
			if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
				throw new SyntaxError("'{'", this.token.getValue(), this.token.getLine(),
						this.token.getColumn());
			this.nextToken();
			this.conteudo();
			this.retorno();
			this.nextToken();
			if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_BRACES))
				throw new SyntaxError("'}'", this.token.getValue(), this.token.getLine(),
						this.token.getColumn());
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
			throw new SyntaxError("Syntax error. ',' or ';' expected. Found '" + this.token.getValue() + "'");

		if (this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
			return;

		this.nextToken();
		if (!this.tokenEquals(Tag.ID))
			throw new SyntaxError("'Identifier'", this.token.getValue(), this.token.getLine(),
					this.token.getColumn());
		this.fim_declaracao();
	}

	private void operacao() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
		if (!this.first.operacao.contains(this.token.getMark()))
			throw new SyntaxError("Invalid attribuition declaration at " + this.token.getLine() + ":" + this.token.getColumn());

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
			throw new SyntaxError("'(' or 'expression'", this.token.getValue(), this.token.getLine(),
					this.token.getColumn());
		this.operacao();

		if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES) && !this.first.operacao.contains(this.token.getMark()))
			throw new SyntaxError("')' or ';'", this.token.getValue(), this.token.getLine(),
					this.token.getColumn());
		this.nextToken();
		this.extensor_operacao();
	}

	private void expressao_operacao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
		if (!this.first.expressao_operacao.contains(this.token.getMark()))
			throw new SyntaxError("expression", this.token.getValue(), this.token.getLine(),
					this.token.getColumn());
		this.expressao_operacao_linha();
	}

	private void expressao_operacao_linha() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError {
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
