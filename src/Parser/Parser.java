package Parser;

import ErrorWarnings.*;
import lexicalAnalyzer.LexicalAnalyzer;
import lexicalAnalyzer.Tag;
import lexicalAnalyzer.Token;
import semanticAnalyzer.SemanticFunctionToken;
import semanticAnalyzer.SemanticToken;
import semanticAnalyzer.SemanticType;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.LinkedList;

public class Parser {
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    private SemanticType tipo;
    private Token token;

    private final LinkedList<String> allScopes = new LinkedList<>();
    private final LinkedList<String> nestedScope = new LinkedList<>();

    private final First first = new First();
    private LexicalAnalyzer lexicalAnalyzer;
    private final Hashtable<String, SemanticToken> symbolTable = new Hashtable<>();
    private final Hashtable<String, SemanticFunctionToken> functionsTable = new Hashtable<>();

    private SemanticType currentFunctionReturnType = null;
    private SemanticFunctionToken currentFunctionToken = null;

    public Parser() throws IOException, NoTarget {
        this.generateScope();
        this.lexicalAnalyzer = new LexicalAnalyzer();
    }

    private void generateScope() {
        String newScope = this.randomString(8);
        while (allScopes.contains(newScope)) {
            newScope = this.randomString(8);
        }
        allScopes.add(newScope);
        nestedScope.add(newScope);
    }

    private String currentScope() {
        return this.nestedScope.get(this.nestedScope.size() - 1);
    }

    private void returnToPreviousScope() {
        this.nestedScope.remove(this.currentScope());
    }

    private boolean identifierAlreadyDeclared() {
        for (String scope : this.nestedScope)
            if (this.symbolTable.containsKey(scope + "_" + this.token.getValue()))
                return true;

        return false;
    }

    private void registraIdentificadorAtual() throws SemanticError {
//		if (this.identifierAlreadyDeclared()){
        if (this.symbolTable.containsKey(this.currentScope() + "_" + this.token.getValue())) {
            throw new SemanticError("Identifier '" + this.currentScope() + "_" + this.token.getValue() + "' already declared in this scope.");
        }
        this.token.setScope(this.currentScope());
        symbolTable.put(this.currentScope() + "_" + this.token.getValue(), new SemanticToken(this.token, this.tipo));
    }

    private void nextToken() throws OutOfRange, EmptyCharacter, LexicalError, ManyCharacters {
        this.token = lexicalAnalyzer.nextToken();
        this.token.setScope(this.currentScope());
    }

    public boolean tokenEquals(Tag mark) {
        return this.token.getMark().equals(mark);
    }

    private void reconheceFuncoes() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange {
        SemanticType function_type;
        Token function_token;
        LinkedList<Tag> function_params;

        this.nextToken();
        while (!this.tokenEquals(Tag.EOF)) {
            function_params = new LinkedList<>();
            if (this.first.tipo_dado.contains(this.token.getMark())) {
                function_type = SemanticType.valueOf(this.token.getMark().name());
                this.nextToken();
                if (this.token.getMark().getDescription().equals("ID") || this.token.getMark().getDescription().equals("MAIN")) {
                    function_token = this.token;
                    this.nextToken();
                    if (this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES)) {
                        this.nextToken();
                        while (this.first.tipo_dado.contains(this.token.getMark()) && !this.tokenEquals(Tag.EOF)) {
                            // Salva todos os tipos de parâmetros da funçao
                            function_params.add(this.token.getMark());
                            this.nextToken();
                            this.nextToken();
                            this.nextToken();
                        }
                        this.functionsTable.put(this.currentScope() + "_" +
                                        function_token.getValue(),
                                new SemanticFunctionToken(function_token, function_type, function_params));
                    }
                }
            }
            this.nextToken();
        }

//        System.out.println(this.functionsTable);
//        System.out.println("\n");
    }

    public void programa() throws OutOfRange, EmptyCharacter, LexicalError, ManyCharacters, SyntaxError, SemanticError, IOException, NoTarget {
        this.reconheceFuncoes();
        this.lexicalAnalyzer = new LexicalAnalyzer();

        this.conteudo_antes_main();
        this.nextToken();
        this.conteudo();

//        System.out.println(symbolTable);

        if (!this.tokenEquals(Tag.EOF))
            throw new SyntaxError("Unexpected " + this.token.getValue() + " at " + this.token.getLine()
                    + this.lexicalAnalyzer.lineError());
    }

    private void conteudo_antes_main() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        boolean sair = false;
        do {
            this.nextToken();
            if (this.first.comando.contains(this.token.getMark()))
                this.comando();
            else if (this.tokenEquals(Tag.INT))
                sair = this.conteudo_antes_main_linha();
            else if (this.tokenEquals(Tag.FLOAT) || this.tokenEquals(Tag.CHAR)) {
                this.tipo = SemanticType.valueOf(this.token.getMark().name());
                this.nextToken();
                if (!this.tokenEquals(Tag.ID))
                    throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
                            this.lexicalAnalyzer.lineError());
                this.registraIdentificadorAtual();
                this.fim_declaracao();
            } else if (this.tokenEquals(Tag.EOF))
                throw new SyntaxError("Syntax error! main is not defined");
        } while (!sair);
    }

    private boolean conteudo_antes_main_linha()
            throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        this.tipo = SemanticType.valueOf(this.token.getMark().name());
        this.currentFunctionReturnType = SemanticType.valueOf(this.token.getMark().name());
        this.nextToken();
        if (!this.tokenEquals(Tag.MAIN) && !this.tokenEquals(Tag.ID))
            throw new SyntaxError(Tag.MAIN.getDescription() + " or Identifier", this.token.getValue(),
                    this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.registraIdentificadorAtual();
        if (this.tokenEquals(Tag.MAIN)) {
            this.main();
            return true;
        } else {
            this.fim_declaracao();
        }
        return false;
    }

    private void main() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
        this.nextToken();
        if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
            throw new SyntaxError("(", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        this._parametros();
        if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
            throw new SyntaxError(")", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        this.nextToken();
        if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
            throw new SyntaxError("{", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        this.generateScope();
        this.nextToken();
        this.conteudo();
        this.retorno();
        this.nextToken();
        this.returnToPreviousScope();
        if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_BRACES))
            throw new SyntaxError("}", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
    }

    private void _parametros() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
        nextToken();
        if (!this.first.tipo_dado.contains(token.getMark()))
            return;

        nextToken();
        if (!tokenEquals(Tag.ID))
            throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());

        this.registraIdentificadorAtual();
        fim_parametros();
    }

    private void passada_de_parametros() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        if (!this.first.passada_de_parametros.contains(token.getMark()) && !this.first.valor_dado.contains(this.token.getMark()))
            return;
        int i_passada_de_parametros = 0;
        Tag currentParamType = this.currentFunctionToken.getFunctionParams().get(i_passada_de_parametros);
        SemanticToken identifier = this.getIdentifier(this.token.getValue());
        if (identifier == null){
            if (!this.token.getMark().getDescription().equals(SemanticType.valueOf(currentParamType.getDescription()).getDescription())) {
                throw new SemanticError("Unnexpected param type '" +
                        SemanticType.valueOf(this.token.getMark().getDescription()).getDescription() + "' expected '" +
                        currentParamType.getDescription() + "'");
            }
//            throw new SemanticError("Identifier '" + this.token.getValue() + "' is not defined.");
        }
        else if (!currentParamType.getDescription().equals(identifier.getType().name())) {
            throw new SemanticError("Unnexpected param type '" +
                    identifier.getType().name() + "' expected '" +
                    currentParamType.getDescription() + "'");
        }
        i_passada_de_parametros += 1;
        fim_passada_de_parametros(i_passada_de_parametros);
    }

    private void fim_passada_de_parametros(int i_passada_de_parametros)
            throws SyntaxError, LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SemanticError {
        nextToken();
        int params_quantity = this.currentFunctionToken.getFunctionParams().size();
        if (this.first.fim_passada_de_parametros.contains(token.getMark())) {
            nextToken();
            if (!tokenEquals(Tag.ID) && !this.first.valor_dado.contains(this.token.getMark()))
                throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
                        this.lexicalAnalyzer.lineError());


            if (i_passada_de_parametros + 1 > params_quantity)
                throw new SemanticError("Semantic Error! Function '" +
                        this.currentFunctionToken.getValue() +
                        "' has only " + params_quantity + " parameters, found " + (i_passada_de_parametros + 1));

            Tag currentParamType = this.currentFunctionToken.getFunctionParams().get(i_passada_de_parametros);
            SemanticToken identifier = this.getIdentifier(this.token.getValue());
            if (identifier == null){
                if (!this.token.getMark().getDescription().equals(SemanticType.valueOf(currentParamType.getDescription()).getDescription())) {
                    throw new SemanticError("Unnexpected param type '" +
                            SemanticType.valueOf(this.token.getMark().getDescription()).getDescription() + "' expected '" +
                            currentParamType.getDescription() + "'");
                }
//            throw new SemanticError("Identifier '" + this.token.getValue() + "' is not defined.");
            }
//        this.tipo = SemanticType.valueOf(this.token.getMark().name());
//        currentParamType = SemanticType.valueOf(currentParamType.name());
            else if (!currentParamType.getDescription().equals(identifier.getType().name())) {
                throw new SemanticError("Unnexpected param type '" +
                        identifier.getType().name() + "' expected '" +
                        currentParamType.getDescription() + "'");
            }

            i_passada_de_parametros += 1;
            fim_passada_de_parametros(i_passada_de_parametros);
        }
        else if (i_passada_de_parametros < params_quantity)
            throw new SemanticError("Semantic Error! Function '" +
                    this.currentFunctionToken.getValue() +
                    "' needs " + params_quantity +
                    " parameters, but found " + i_passada_de_parametros + ".");
    }

    private void atribuicao_ou_chamada() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        if (!this.first.atribuicao_ou_chamada.contains(this.token.getMark())
                && !this.first.operador_aritmetico.contains(this.token.getMark()))
            throw new SyntaxError("function call or variable atribuition after Identifier", this.token.getValue(),
                    this.token.getLine(), this.lexicalAnalyzer.lineError());

        if (this.first.atribuicao_ou_chamada.contains(this.token.getMark())) {
            nextToken();
            passada_de_parametros();

            if (!tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
                throw new SyntaxError(")", this.token.getValue(), this.token.getLine(),
                        this.lexicalAnalyzer.lineError());

            nextToken();
            if (!tokenEquals(Tag.SP_CHAR_SEMICOLON))
                throw new SyntaxError(";", this.token.getValue(), this.token.getLine(),
                        this.lexicalAnalyzer.lineError());
            nextToken();
        } else {
            operador_nested();
            if (!tokenEquals(Tag.ARI_OP_ATTRIBUTION))
                throw new SyntaxError("=", this.token.getValue(), this.token.getLine(),
                        this.lexicalAnalyzer.lineError());
            nextToken();
//			operador_nested();
            operacao();
            if (!tokenEquals(Tag.SP_CHAR_SEMICOLON))
                throw new SyntaxError(";", this.token.getValue(), this.token.getLine(),
                        this.lexicalAnalyzer.lineError());
            nextToken();
        }
    }

    private void operador_nested() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange {
        if (this.tokenEquals(Tag.ARI_OP_ADDITION) || this.tokenEquals(Tag.ARI_OP_DIVISION)
                || this.tokenEquals(Tag.ARI_OP_MULTIPLICATION) || this.tokenEquals(Tag.ARI_OP_SUBTRACTION))
            nextToken();
    }

    private void fim_parametros() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
        nextToken();
        if (!this.first.fim_parametros.contains(token.getMark()))
            return;

        nextToken();
        if (!this.first.tipo_dado.contains(token.getMark()))
            throw new SyntaxError("Data type", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());

        nextToken();
        if (!tokenEquals(Tag.ID))
            throw new SyntaxError(";", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.registraIdentificadorAtual();
        fim_parametros();
    }

    private void retorno() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
        if (!this.tokenEquals(Tag.RETURN))
            throw new SyntaxError(Tag.RETURN.getDescription(), this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());
        this.nextToken();
        if (!this.first.operando.contains(this.token.getMark()))
            throw new SyntaxError("Id or data value", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());
        if (!this.token.getMark().name().equals(this.currentFunctionReturnType.getDescription())) {
            throw new SemanticError("Expected type '" + this.currentFunctionReturnType.getDescription() + "' on return found '" + this.token.getMark().name() + "'");
        }
        this.nextToken();
        if (!this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
            throw new SyntaxError(";", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
    }

    void conteudo() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
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

    void comando() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
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
            SemanticToken identifier = this.getIdentifier(this.token.getValue());
            this.currentFunctionToken = this.getFunctionIdentifier(this.token.getValue());
            if (identifier == null && this.currentFunctionToken == null) {
                throw new SemanticError("Identifier '" + this.token.getValue() + "' is not defined.");
            }
            if (identifier == null)
                this.tipo = this.currentFunctionToken.getType();
            else
                this.tipo = identifier.getType();
            this.nextToken();
            this.atribuicao_ou_chamada();
        }
    }

    private void comandoFOR() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        this.generateScope();
        this.nextToken();
        if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
            throw new SyntaxError("(", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        this.nextToken();
        this.declaracao_for();
        this.condicao();
        if (!this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
            throw new SyntaxError(";", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.nextToken();
        this.operacao_for();
        if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
            throw new SyntaxError(")", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        this.nextToken();
        if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
            throw new SyntaxError("{", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        this.generateScope();
        this.nextToken();
        this.conteudo();
        this.nextToken();
        this.returnToPreviousScope();
    }

    private void operacao_for() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        if (!this.first.operacao_for.contains(this.token.getMark()))
            throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());

        this.nextToken();
        operador_nested();
        if (!this.tokenEquals(Tag.ARI_OP_ATTRIBUTION))
            throw new SyntaxError("=", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        this.nextToken();
        operador_nested();
        this.operacao();
    }

    private void declaracao_for() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
        if (!this.token.getMark().name().equals("ID") && !this.first.tipo_dado.contains(token.getMark()))
            throw new SyntaxError("Data type", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());

        if (this.token.getMark().name().equals("ID")) {
            SemanticToken identifier = this.getIdentifier(this.token.getValue());
            if (identifier == null)
                throw new SemanticError("Identifier '" + this.token.getValue() + "' is not defined");
            this.tipo = identifier.getType();
        } else {
            this.tipo = SemanticType.valueOf(this.token.getMark().name());
            this.nextToken();
            if (!this.tokenEquals(Tag.ID))
                throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
                        this.lexicalAnalyzer.lineError());
            this.registraIdentificadorAtual();
        }
        this.fim_declaracao_for();
    }

    private void fim_declaracao_for() throws SyntaxError, LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SemanticError {
        this.nextToken();
        if (!first.fim_declaracao_for.contains(token.getMark()))
            throw new SyntaxError("= or ;", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());

        if (this.tokenEquals(Tag.ARI_OP_ATTRIBUTION)) {
//			this.nextToken();
            this.valor_atribuicao();

            this.declaracao_for_inline();
        }
    }

    private void declaracao_for_inline() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        if (!this.first.declaracao_for_inline.contains(this.token.getMark()))
            throw new SyntaxError(";", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        if (this.tokenEquals(Tag.SP_CHAR_COMMA)) {
            nextToken();
            if (!this.tokenEquals(Tag.ID))
                throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
                        this.lexicalAnalyzer.lineError());

            fim_declaracao_for();
        }
    }

    private void comandoDOWHILE() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        this.nextToken();
        if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
            throw new SyntaxError("{", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.generateScope();
        this.nextToken();
        this.conteudo();
        this.returnToPreviousScope();

        this.nextToken();
        if (!this.tokenEquals(Tag.WHILE))
            throw new SyntaxError(Tag.WHILE.getDescription() + " after " + Tag.DO.getDescription(),
                    this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        this.nextToken();
        if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
            throw new SyntaxError("(", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.condicao();

        if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
            throw new SyntaxError(")", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.nextToken();
        if (!this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
            throw new SyntaxError(";", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        this.nextToken();
    }

    private void comandoWHILE() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        this.nextToken();
        if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
            throw new SyntaxError("(", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.condicao();

        if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
            throw new SyntaxError(")", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.nextToken();
        if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
            throw new SyntaxError("{", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.generateScope();
        this.nextToken();
        this.conteudo();
        this.nextToken();
        this.returnToPreviousScope();
    }

    private void comandoELSE_IF() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        this.nextToken();
        if (this.tokenEquals(Tag.IF))
            this.comandoIF();
        else if (this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES)) {
            this.generateScope();
            this.nextToken();
            this.conteudo();
            this.nextToken();
            this.returnToPreviousScope();
        }
    }

    private void comandoIF() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        this.nextToken();
        if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
            throw new SyntaxError("(", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.condicao();

        if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
            throw new SyntaxError(")", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.nextToken();
        if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
            throw new SyntaxError("{", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        this.generateScope();
        this.nextToken();
        this.conteudo();
        this.nextToken();
        this.returnToPreviousScope();
    }

    private void condicao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        // Não aceita, por exemplo, if(id)
        this.nextToken();
        if (this.token.getMark().name().equals("ID")) {
            SemanticToken identifier = this.getIdentifier(this.token.getValue());
            if (identifier == null)
                throw new SemanticError("Identifier " + this.token.getValue() + " is not defined");

            this.tipo = identifier.getType();
        } else
            this.tipo = SemanticType.valueOf(this.token.getMark().name());
        this.operacao();
        if (!this.first.operador_relacional.contains(this.token.getMark()))
            throw new SyntaxError("Relational operator", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());
        this.nextToken();
        this.operacao();
    }

    private void declaracao() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
        // Aqui ja garante que começa com tipo_dado
        this.tipo = SemanticType.valueOf(this.token.getMark().name());
        this.nextToken();
        if (!this.tokenEquals(Tag.ID))
            throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());

        this.registraIdentificadorAtual();
//		SemanticToken token = new SemanticToken(this.token.getMark(), this.token.getValue());
//		token.setType(SemanticType.valueOf(this.tipo.name()));
//		this.pilha.push(token);
        this.fim_declaracao();
    }

    private void fim_declaracao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        this.nextToken();
        if (!this.first.fim_declaracao.contains(this.token.getMark()))
            throw new SyntaxError("= or ;", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());

        if (this.tokenEquals(Tag.ARI_OP_ATTRIBUTION)) {
            this.valor_atribuicao();
            this.declaracao_inline();
        } else if (this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES)) {
            this.currentFunctionReturnType = this.tipo;
            this.generateScope();
            this._parametros();
            if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
                throw new SyntaxError(")", this.token.getValue(), this.token.getLine(),
                        this.lexicalAnalyzer.lineError());

            this.nextToken();

            if (this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
                return;

            if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
                throw new SyntaxError("{", this.token.getValue(), this.token.getLine(),
                        this.lexicalAnalyzer.lineError());
            this.nextToken();
            this.conteudo();
            this.retorno();
            this.nextToken();
            if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_BRACES))
                throw new SyntaxError("}", this.token.getValue(), this.token.getLine(),
                        this.lexicalAnalyzer.lineError());
            this.returnToPreviousScope();
        }

        // Se for SEMICOLON ta de boa, nem precisa fazer nada
    }

    private void valor_atribuicao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        this.nextToken();
        if (!this.first.operacao.contains(this.token.getMark()))
            throw new SyntaxError("Invalid attribuition declaration");
        if (this.tokenEquals(Tag.CHARACTER)) {
            this.verificaTipo();
            this.nextToken();
        } else
            this.operacao();
    }

    private void declaracao_inline() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
        if (!this.first.declaracao_inline.contains(this.token.getMark()))
            throw new SyntaxError("Operation", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());

        if (this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
            return;
        this.nextToken();
        if (!this.tokenEquals(Tag.ID))
            throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());
        this.fim_declaracao();
    }

    private void operacao() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
        if (!this.first.operacao.contains(this.token.getMark()))
            throw new SyntaxError("Invalid attribuition declaration at " + this.token.getLine() + ":\n"
                    + this.lexicalAnalyzer.lineError());

        if (this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES)) {
            this.operacao_linha();
        } else {
            this.expressao_operacao();
            this.extensor_operacao();
        }
    }

    private void operacao_linha() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
        this.nextToken();
        if (!this.first.operacao_linha.contains(this.token.getMark()))
            throw new SyntaxError("( or expression", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());
        if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
            this.verificaTipo();
        this.operacao();
        if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES) && !this.first.operacao.contains(this.token.getMark()))
            throw new SyntaxError(")", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        this.nextToken();
        this.extensor_operacao();
    }

    private void expressao_operacao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        if (!this.first.expressao_operacao.contains(this.token.getMark()))
            throw new SyntaxError("expression", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());
        this.expressao_operacao_linha();
    }

    private void expressao_operacao_linha()
            throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
        this.verificaTipo();
        this.nextToken();
        if (!this.first.expressao_operacao_linha.contains(this.token.getMark()))
            return;
        this.nextToken();
        this.operacao();
    }

    private void extensor_operacao() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
        if (!this.first.extensor_operacao.contains(this.token.getMark()))
            return;
        this.nextToken();
        this.operacao();
    }

    private void verificaTipo() throws SemanticError, LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange {
        if (this.token.getMark().getDescription().equals("ID")) {
            SemanticToken identifier = getIdentifier(this.token.getValue());


//            SemanticFunctionToken functionToken = this.getFunctionIdentifier(this.token.getValue());
//            if(functionToken != null){
//                if (!this.tipo.getDescription().equals(functionToken.getType().getDescription()))
//                    throw new SemanticError("Functino return type '" + this.tipo.getDescription() + "' is not the same type as the type of identifier " + identifier.getValue() + " (" + identifier.getType().getDescription() + ")");
//
//                this.currentFunctionToken = functionToken;
//                this.nextToken();
//                this.atribuicao_ou_chamada();
//            }
            if (identifier == null)
                throw new SemanticError("Identifier " + this.token.getValue() + " is not defined");

            else if (!this.tipo.getDescription().equals(identifier.getType().getDescription()))
                    throw new SemanticError("Type '" + this.tipo.getDescription() + "' is not the same type as the type of identifier " + identifier.getValue() + " (" + identifier.getType().getDescription() + ")");

        }
        else if (!this.token.getMark().getDescription().equals(this.tipo.getDescription()))
            throw new SemanticError(this.tipo.getDescription(), this.token.getMark().getDescription(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());
    }

    private SemanticToken getIdentifier(String value) {
        for (String scope : this.nestedScope) {
            if (this.symbolTable.containsKey(scope + "_" + value))
                return this.symbolTable.get(scope + "_" + value);
        }
        return null;
    }

    private SemanticFunctionToken getFunctionIdentifier(String value) {
        for (String scope : this.nestedScope) {
            if (this.functionsTable.containsKey(scope + "_" + value))
                return this.functionsTable.get(scope + "_" + value);
        }
        return null;
    }
}
