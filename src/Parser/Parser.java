package Parser;

import ErrorWarnings.*;
import IntermediateCodeGenerator.ExpressionToken;
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
import java.util.Objects;

public class Parser {
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    String randomScopeID() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    private SemanticType currentType;
    private Token token;

    private final LinkedList<String> allScopes = new LinkedList<>();
    private final LinkedList<String> nestedScope = new LinkedList<>();

    private final First first = new First();
    private final LexicalAnalyzer lexicalAnalyzer;

    private final Hashtable<String, SemanticToken> symbolTable = new Hashtable<>();
    private final Hashtable<String, SemanticFunctionToken> functionsTable = new Hashtable<>();

    private SemanticType currentFunctionReturnType = null;
    private SemanticFunctionToken currentFunctionToken = null;

    private Token attribuitionCurrentToken;
    private LinkedList<Token> expressaoDaAtribuicaoAtual = new LinkedList<>();

    private final LinkedList<String> labels = new LinkedList<>();
    private final LinkedList<String> expressionFunctionsCalls = new LinkedList<>();

    private boolean printLastToken = false;

    public Parser() throws IOException, NoTarget {
        this.generateScope();
        this.lexicalAnalyzer = new LexicalAnalyzer();
    }

    private void nextToken() throws OutOfRange, EmptyCharacter, LexicalError, ManyCharacters {
        if (this.printLastToken) {
            System.out.print(this.token.getValue() + " ");
        }
        this.token = lexicalAnalyzer.nextToken();
        this.token.setScope(this.currentScope());
    }

    private boolean tokenEquals(Tag mark) {
        return this.token.getMark().equals(mark);
    }

    private String generateLabel() {
        this.labels.add("L" + (this.labels.size() + 1));
        return this.labels.get(this.labels.size() - 1);
    }

    private void generateScope() {
        String newScope = this.randomScopeID();
        while (allScopes.contains(newScope)) {
            newScope = this.randomScopeID();
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

    private void registraIdentificadorAtual() throws SemanticError {
        if (this.symbolTable.containsKey(this.currentScope() + "_" + this.token.getValue())) {
            throw new SemanticError("Identifier '" + this.currentScope() + "_" + this.token.getValue() + "' already declared in this scope.",
                    this.token.getLine(), this.lexicalAnalyzer.lineError());
        }
        this.token.setScope(this.currentScope());
        symbolTable.put(this.currentScope() + "_" + this.token.getValue(), new SemanticToken(this.token, this.currentType));
    }

    private SemanticToken getIdentifier(String value) {
        for (String scope : this.nestedScope) {
            if (this.symbolTable.containsKey(scope + "_" + value))
                return this.symbolTable.get(scope + "_" + value);
        }
        return null;
    }

    private SemanticToken getFunction(String value) {
        for (String scope : this.nestedScope) {
            if (this.functionsTable.containsKey(scope + "_" + value))
                return this.functionsTable.get(scope + "_" + value);
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
                        this.functionsTable.put(this.currentScope() + "_" + function_token.getValue(),
                                new SemanticFunctionToken(function_token, function_type, function_params, this.currentType));
                    }
                }
            }
            this.nextToken();
        }
    }

    public void programa() throws OutOfRange, EmptyCharacter, LexicalError, ManyCharacters, SyntaxError, SemanticError, IOException {
        this.reconheceFuncoes();
        this.lexicalAnalyzer.restartPointer();

        this.conteudo_antes_main();
        this.nextToken();
        this.conteudo();


        if (!this.tokenEquals(Tag.EOF))
            throw new SyntaxError("Unexpected " + this.token.getValue() + " at " + this.token.getLine()
                    + this.lexicalAnalyzer.lineError(), this.token.getLine(), this.lexicalAnalyzer.lineError());
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
                this.currentType = SemanticType.valueOf(this.token.getMark().name());
                this.nextToken();
                if (!this.tokenEquals(Tag.ID))
                    throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
                            this.lexicalAnalyzer.lineError());
                this.attribuitionCurrentToken = this.token;
                this.registraIdentificadorAtual();
                this.fim_declaracao();
            } else if (this.tokenEquals(Tag.EOF))
                throw new SyntaxError("Syntax error! main is not defined");
        } while (!sair);
    }

    private boolean conteudo_antes_main_linha()
            throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        this.currentType = SemanticType.valueOf(this.token.getMark().name());
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
            this.attribuitionCurrentToken = this.token;
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
        String currentFunctionCallParams = "(";
        if (!this.first.passada_de_parametros.contains(token.getMark())) {
            if (this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES) && this.currentFunctionToken.getFunctionParams().size() > 0) {
                throw new SemanticError("Semantic Error! Function '" +
                        this.currentFunctionToken.getValue() +
                        "' needs " + this.currentFunctionToken.getFunctionParams().size() +
                        " parameters, but found 0.", this.token.getLine(), this.lexicalAnalyzer.lineError());
            }
            this.expressionFunctionsCalls.add(currentFunctionCallParams + ")");
            return;
        }
        if (this.currentFunctionToken.getFunctionParams().size() == 0) {
            throw new SemanticError("Semantic Error! Function '" +
                    this.currentFunctionToken.getValue() +
                    "' needs " + this.currentFunctionToken.getFunctionParams().size() +
                    " parameters, but parameter(s) is(are) being passed.", this.token.getLine(), this.lexicalAnalyzer.lineError());
        }
        int i_passada_de_parametros = 0;
        Tag currentParamType = this.currentFunctionToken.getFunctionParams().get(i_passada_de_parametros);
        SemanticToken identifier = this.getIdentifier(this.token.getValue());
        if (identifier == null && this.tokenEquals(Tag.ID)) {
            throw new SemanticError("Identifier '" + this.token.getValue() + "' is not defined.", this.token.getLine(), this.lexicalAnalyzer.lineError());
        } else if (!this.token.getMark().getDescription().equals(SemanticType.valueOf(currentParamType.name()).getDescription())) {
            throw new SemanticError(currentParamType.getDescription(), SemanticType.valueOf(this.token.getMark().name()).getDescription(),
                    this.token.getLine(), this.lexicalAnalyzer.lineError());
        }
        currentFunctionCallParams += this.token.getValue();
        i_passada_de_parametros += 1;
        currentFunctionCallParams = fim_passada_de_parametros(i_passada_de_parametros, currentFunctionCallParams);
        currentFunctionCallParams += ")";
        this.expressionFunctionsCalls.add(currentFunctionCallParams);
    }

    private String fim_passada_de_parametros(int i_passada_de_parametros, String currentFunctionCallParams) throws SyntaxError, LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SemanticError {
        nextToken();
        int params_quantity = this.currentFunctionToken.getFunctionParams().size();
        if (this.first.fim_passada_de_parametros.contains(token.getMark())) {
            nextToken();
            if (!tokenEquals(Tag.ID) && this.first.tipo_dado.contains(this.token.getMark()))
                throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
                        this.lexicalAnalyzer.lineError());

            if (i_passada_de_parametros + 1 > params_quantity)
                throw new SemanticError("Semantic Error! Function '" +
                        this.currentFunctionToken.getValue() +
                        "' has only " + params_quantity + " parameters.", this.token.getLine(), this.lexicalAnalyzer.lineError());

            Tag currentParamType = this.currentFunctionToken.getFunctionParams().get(i_passada_de_parametros);
            SemanticToken identifier = this.getIdentifier(this.token.getValue());
            if (identifier == null && this.tokenEquals(Tag.ID)) {
                throw new SemanticError("Identifier '" + this.token.getValue() + "' is not defined.", this.token.getLine(), this.lexicalAnalyzer.lineError());
            } else if (!this.token.getMark().getDescription().equals(SemanticType.valueOf(currentParamType.name()).getDescription())) {
                throw new SemanticError(currentParamType.getDescription(), SemanticType.valueOf(this.token.getMark().name()).getDescription(),
                        this.token.getLine(), this.lexicalAnalyzer.lineError());
            }

            currentFunctionCallParams += this.token.getValue();
            i_passada_de_parametros += 1;
            fim_passada_de_parametros(i_passada_de_parametros, currentFunctionCallParams);
        } else if (i_passada_de_parametros < params_quantity)
            throw new SemanticError("Semantic Error! Function '" +
                    this.currentFunctionToken.getValue() +
                    "' needs " + params_quantity +
                    " parameters, but found " + i_passada_de_parametros + ".", this.token.getLine(), this.lexicalAnalyzer.lineError());
        return currentFunctionCallParams;
    }

    private void atribuicao_ou_chamada() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        if (!this.first.atribuicao_ou_chamada.contains(this.token.getMark())
                && !this.first.operador_aritmetico.contains(this.token.getMark()))
            throw new SyntaxError("function call or variable atribuition after Identifier", this.token.getValue(),
                    this.token.getLine(), this.lexicalAnalyzer.lineError());

        if (this.first.atribuicao_ou_chamada.contains(this.token.getMark())) {
            nextToken();
            this.passada_de_parametros();

            if (!tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
                throw new SyntaxError(")", this.token.getValue(), this.token.getLine(),
                        this.lexicalAnalyzer.lineError());

            nextToken();
        } else {
            Token nested = operador_nested();
            if (nested != null)
                this.nextToken();
            if (!tokenEquals(Tag.ARI_OP_ATTRIBUTION))
                throw new SyntaxError("=", this.token.getValue(), this.token.getLine(),
                        this.lexicalAnalyzer.lineError());
            if (nested != null) {
                this.expressaoDaAtribuicaoAtual.add(this.attribuitionCurrentToken);
                this.expressaoDaAtribuicaoAtual.add(nested);
            }
            nextToken();

            this.expressaoAritmetica(true);
            this.gerarTokensTemporariosDeExpressoesAritmeticas(false);
            this.expressaoDaAtribuicaoAtual = new LinkedList<>();
        }
        if (!tokenEquals(Tag.SP_CHAR_SEMICOLON))
            throw new SyntaxError(";", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());
        nextToken();
    }

    private Token operador_nested() {
        if (this.tokenEquals(Tag.ARI_OP_ADDITION) || this.tokenEquals(Tag.ARI_OP_DIVISION)
                || this.tokenEquals(Tag.ARI_OP_MULTIPLICATION) || this.tokenEquals(Tag.ARI_OP_SUBTRACTION)) {
            return this.token;
        }
        return null;
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

        if (!this.currentFunctionReturnType.equals(SemanticType.CHAR)) {
            this.currentType = this.currentFunctionReturnType;
            this.expressaoAritmetica(false);
        }
        else {
            if (this.token.getMark().equals(Tag.ID)) {
                SemanticToken aux = this.getIdentifier(this.token.getValue());
                if (aux == null)
                    throw new SemanticError("Identifier '" + this.token.getValue() + "' is not defined.",
                            this.token.getLine(), this.lexicalAnalyzer.lineError());
                else if (!aux.getType().equals(SemanticType.CHAR))
                    throw new SemanticError("Identifier '" + this.token.getValue() + "'" +
                            " is not of the function return type " + SemanticType.CHAR.getDescription().toLowerCase()
                            , this.token.getLine(), this.lexicalAnalyzer.lineError());
            } else if (!this.token.getMark().equals(Tag.CHARACTER))
                throw new SemanticError("Identifier '" + this.token.getValue() + "'" +
                        " is not of the function return type " + Tag.CHARACTER.getDescription().toLowerCase()
                        , this.token.getLine(), this.lexicalAnalyzer.lineError());
            this.nextToken();
        }

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
                throw new SemanticError("Identifier '" + this.token.getValue() + "' is not defined.", this.token.getLine(), this.lexicalAnalyzer.lineError());
            }
            this.currentType = Objects.requireNonNullElseGet(identifier, () -> this.currentFunctionToken).getType();
            this.attribuitionCurrentToken = this.token;
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

        this.condicao(false);
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
        Token nested = operador_nested();
        if (nested != null)
            this.nextToken();
        if (!this.tokenEquals(Tag.ARI_OP_ATTRIBUTION))
            throw new SyntaxError("=", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        if (nested != null) {
            this.expressaoDaAtribuicaoAtual.add(this.attribuitionCurrentToken);
            this.expressaoDaAtribuicaoAtual.add(nested);
        }
        nextToken();

        this.expressaoAritmetica(true);
        this.gerarTokensTemporariosDeExpressoesAritmeticas(false);
        this.expressaoDaAtribuicaoAtual = new LinkedList<>();
    }

    private void declaracao_for() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
        if (!this.token.getMark().equals(Tag.ID) && !this.first.tipo_dado.contains(token.getMark()))
            throw new SyntaxError("Data type or identifier", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());
        if (this.token.getMark().equals(Tag.ID)) {
            this.attribuitionCurrentToken = this.token;
            SemanticToken identifier = this.getIdentifier(this.token.getValue());
            if (identifier == null)
                throw new SemanticError("Identifier '" + this.token.getValue() + "' is not defined", this.token.getLine(), this.lexicalAnalyzer.lineError());
            this.currentType = identifier.getType();
        } else {
            this.currentType = SemanticType.valueOf(this.token.getMark().name());
            this.nextToken();
            if (!this.tokenEquals(Tag.ID))
                throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
                        this.lexicalAnalyzer.lineError());
            this.attribuitionCurrentToken = this.token;
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
        String labelCurrentDoWhile = this.generateLabel();
        System.out.print("\n" + labelCurrentDoWhile + " ");

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
        System.out.print("if ");
        this.printLastToken = true;
        this.condicao(false);

        if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
            throw new SyntaxError(")", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.nextToken();
        this.printLastToken = false;
        System.out.println("goto " + labelCurrentDoWhile);
        if (!this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
            throw new SyntaxError(";", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        this.nextToken();
    }

    private void comandoWHILE() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        String labelCurrentWhile = this.generateLabel();
        System.out.print("\n" + labelCurrentWhile + " ");
        this.nextToken();
        if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
            throw new SyntaxError("(", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        System.out.print("If ");
        this.printLastToken = true;

        this.condicao(true);

        if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
            throw new SyntaxError(")", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.nextToken();
        this.printLastToken = false;
        System.out.println("goto " + this.generateLabel());
        if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
            throw new SyntaxError("{", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.generateScope();
        this.nextToken();
        this.conteudo();
        this.nextToken();
        System.out.println("goto " + labelCurrentWhile);
        System.out.print("\n" + this.labels.getLast() + " ");
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
        this.printLastToken = true;
        this.nextToken();
        if (!this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES))
            throw new SyntaxError("(", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.condicao(true);

        if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
            throw new SyntaxError(")", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.nextToken();
        if (!this.tokenEquals(Tag.SP_CHAR_OPEN_BRACES))
            throw new SyntaxError("{", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        this.generateScope();
        this.printLastToken = false;
        System.out.println("goto " + this.generateLabel());
        this.nextToken();

        this.conteudo();
        this.nextToken();
        System.out.print("\n" + this.labels.get(this.labels.size() - 1) + " ");
        this.returnToPreviousScope();
    }

    private void condicao(boolean invertRelOperator) throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        this.nextToken();
        if (this.token.getMark().name().equals("ID")) {
            SemanticToken identifier = this.getIdentifier(this.token.getValue());
            if (identifier == null)
                throw new SemanticError("Identifier " + this.token.getValue() + " is not defined", this.token.getLine(), this.lexicalAnalyzer.lineError());

            this.currentType = identifier.getType();
        } else {
            switch (this.token.getMark().name()) {
                case "INT" -> this.currentType = SemanticType.INT;
                case "FLOAT" -> this.currentType = SemanticType.FLOAT;
                case "CHAR" -> this.currentType = SemanticType.CHAR;
            }
        }
        this.expressaoAritmetica(false);

        if (!this.first.operador_relacional.contains(this.token.getMark()))
            throw new SyntaxError("Relational operator", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());
        if (invertRelOperator)
            this.token = this.invertRelOperator(this.token);
        this.nextToken();
        this.expressaoAritmetica(false);
    }

    private Token invertRelOperator(Token token) {
        switch (token.getMark().getDescription()) {
            case "REL_OP_EQUALS" -> token = new Token(Tag.REL_OP_NOT_EQUAL_TO, "!=", token.getScope());
            case "REL_OP_NOT_EQUAL_TO" -> token = new Token(Tag.REL_OP_EQUALS, "==", token.getScope());
            case "REL_OP_GREATER_THAN" -> token = new Token(Tag.REL_OP_LESS_THAN, "<", token.getScope());
            case "REL_OP_LESS_THAN" -> token = new Token(Tag.REL_OP_GREATER_THAN, ">", token.getScope());
            case "REL_OP_GREATER_THAN_OR_EQUALS_TO" -> token = new Token(Tag.REL_OP_LESS_THAN_OR_EQUALS_TO, "<=", token.getScope());
            case "REL_OP_LESS_THAN_OR_EQUALS_TO" -> token = new Token(Tag.REL_OP_GREATER_THAN_OR_EQUALS_TO, ">=", token.getScope());
        }
        return token;
    }

    private void declaracao() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
        this.currentType = SemanticType.valueOf(this.token.getMark().name());

        this.nextToken();
        if (!this.tokenEquals(Tag.ID))
            throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());

        this.attribuitionCurrentToken = this.token;

        this.registraIdentificadorAtual();

        this.fim_declaracao();
    }

    private void fim_declaracao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        this.nextToken();
        if (!this.first.fim_declaracao.contains(this.token.getMark()))
            throw new SyntaxError("'=', ';' or inline declaration", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());

        if (this.tokenEquals(Tag.ARI_OP_ATTRIBUTION)) {
            this.valor_atribuicao();
            // Quando uma variável receber uma função os tokens que chegam aqui são os "(" ... ")"
            if (this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES)) {
                this.nextToken();
                this.passada_de_parametros();
                this.nextToken();
            }
            this.declaracao_inline();
        } else if (this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES)) {
            this.declaracaoDeFuncao();
        } else {
            this.geraDeclaracaoDeVariavelSemValor(this.attribuitionCurrentToken.getValue());
            if (this.tokenEquals(Tag.SP_CHAR_COMMA)) {
                this.declaracao_inline();
            }
        }
    }

    private void valor_atribuicao() throws LexicalError, ManyCharacters, EmptyCharacter, SyntaxError, OutOfRange, SemanticError {
        this.nextToken();
        if (!this.first.operacao.contains(this.token.getMark()))
            throw new SyntaxError("Invalid attribuition declaration");
        if (this.tokenEquals(Tag.CHARACTER)) {
            this.verificaTipo();
            this.registraIdentificadorAtual();

            this.geraDefinicaoDeVariavel(true, this.attribuitionCurrentToken.getValue(), this.token.getValue());

            this.nextToken();
        } else {
            this.expressaoAritmetica(true);
            this.gerarTokensTemporariosDeExpressoesAritmeticas(true);
            this.expressaoDaAtribuicaoAtual = new LinkedList<>();
        }
    }

    private void declaracao_inline() throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
        if (!this.first.declaracao_inline.contains(this.token.getMark()))
            throw new SyntaxError("Declaration", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());

        if (this.tokenEquals(Tag.SP_CHAR_SEMICOLON))
            return;
        this.nextToken();
        if (!this.tokenEquals(Tag.ID))
            throw new SyntaxError("Identifier", this.token.getValue(), this.token.getLine(),
                    this.lexicalAnalyzer.lineError());

        this.attribuitionCurrentToken = this.token;

        this.registraIdentificadorAtual();
        this.fim_declaracao();
    }

    private void declaracaoDeFuncao() throws LexicalError, SyntaxError, ManyCharacters, EmptyCharacter, OutOfRange, SemanticError {
        this.currentFunctionReturnType = this.currentType;
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

    private void expressaoAritmetica(boolean atribuicao) throws SyntaxError, LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SemanticError {
        if (atribuicao)
            this.expressaoDaAtribuicaoAtual.add(this.token);
        if (!this.first.expressao_aritmetica.contains(this.token.getMark()))
            throw new SyntaxError("operator", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        this.termo(atribuicao);
        this.expressaoAritmeticaLinha(atribuicao);
    }

    private void expressaoAritmeticaLinha(boolean atribuicao) throws SyntaxError, LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SemanticError {
        if (!this.first.expressao_aritmetica_linha.contains(this.token.getMark()))
            return;

        this.nextToken();
        if (atribuicao)
            this.expressaoDaAtribuicaoAtual.add(this.token);
        this.termo(atribuicao);
        this.expressaoAritmeticaLinha(atribuicao);
    }

    private void termo(boolean atribuicao) throws SyntaxError, LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SemanticError {
        if (!this.first.termo.contains(this.token.getMark()))
            throw new SyntaxError("operator", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        this.fator(atribuicao);
        this.nextToken();
        if (atribuicao)
            this.expressaoDaAtribuicaoAtual.add(this.token);
        this.termoLinha(atribuicao);
    }

    private void termoLinha(boolean atribuicao) throws LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SyntaxError, SemanticError {
        if (this.first.termo_linha.contains(this.token.getMark())) {
            this.nextToken();
            if (atribuicao)
                this.expressaoDaAtribuicaoAtual.add(this.token);
            this.termo(atribuicao);
        }
    }

    private void fator(boolean atribuicao) throws SyntaxError, LexicalError, ManyCharacters, EmptyCharacter, OutOfRange, SemanticError {
        if (!this.first.fator.contains(this.token.getMark()))
            throw new SyntaxError("operator", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        if (this.tokenEquals(Tag.SP_CHAR_OPEN_PARENTHESES)) {
            this.nextToken();
            this.expressaoAritmetica(atribuicao);
            if (!this.tokenEquals(Tag.SP_CHAR_CLOSE_PARENTHESES))
                throw new SyntaxError(")", this.token.getValue(), this.token.getLine(), this.lexicalAnalyzer.lineError());
        } else {
            this.verificaTipo();
            // Verifica se o identificador seja uma função, tratar os parâmetros
            if (this.getFunction(this.token.getValue()) != null) {
                this.currentFunctionToken = this.getFunctionIdentifier(this.token.getValue());
                this.nextToken();
                this.nextToken();
                this.passada_de_parametros();
            }
        }
    }

    private void verificaTipo() throws SemanticError {
        if (this.token.getMark().getDescription().equals("ID")) {
            SemanticToken identifier = getIdentifier(this.token.getValue());

            if (identifier == null) {
                SemanticFunctionToken functionIdentifier = this.getFunctionIdentifier(this.token.getValue());
                if (functionIdentifier == null)
                    throw new SemanticError("Function '" + this.token.getValue() + "' is not defined", this.token.getLine(), this.lexicalAnalyzer.lineError());
                else if (!functionIdentifier.getReturnType().equals(this.currentType))
                    throw new SemanticError(this.currentType.getDescription().toLowerCase(),
                            functionIdentifier.getReturnType().getDescription().toLowerCase(),
                            this.token.getLine(), this.lexicalAnalyzer.lineError());
            } else if (!this.currentType.getDescription().equals(identifier.getType().getDescription()))
                throw new SemanticError(this.currentType.getDescription(),
                        identifier.getType().getDescription(), this.token.getLine(), this.lexicalAnalyzer.lineError());

        } else if (!this.token.getMark().getDescription().equals(this.currentType.getDescription()))
            throw new SemanticError(this.currentType.getDescription(), this.token.getMark().getDescription(),
                    this.token.getLine(), this.lexicalAnalyzer.lineError());
    }

    /**
     * Gera codigo para definição de variável inicialmente sem valor
     **/
    private void geraDeclaracaoDeVariavelSemValor(String varName) {
        String varDefinition = "";
        switch (this.currentType.getDescription()) {
            case "INTEGER" -> varDefinition += "int ";
            case "FLOATING_POINT" -> varDefinition += "float ";
            case "CHARACTER" -> varDefinition += "char ";
        }
        varDefinition += varName + ";";
        System.out.println(varDefinition);
    }

    /**
     * Gera codigo para definição de variável inicialmente com valor
     **/
    private void geraDefinicaoDeVariavel(boolean declaracao, String varName, String varValue) {
        String varDefinition = "";
        if (declaracao)
            switch (this.currentType.getDescription()) {
                case "INTEGER" -> varDefinition += "int ";
                case "FLOATING_POINT" -> varDefinition += "float ";
                case "CHARACTER" -> varDefinition += "char ";
            }
        varDefinition += varName + " = " + varValue + ";";
        System.out.println(varDefinition);
    }

    private void gerarTokensExpressoesAritmeticas(
            int totalScopes,
            LinkedList<ExpressionToken> expressionTokens,
            LinkedList<Integer> idsTaken,
            Tag operator) {
        int forCurrentScope = 0;
        ExpressionToken newExpression;
        int i = 0;
        for (Token t : this.expressaoDaAtribuicaoAtual) {
            if (i == this.expressaoDaAtribuicaoAtual.size() - 1) break;
            if (t.getMark().equals(Tag.SP_CHAR_OPEN_PARENTHESES)) forCurrentScope += 1;
            else if (t.getMark().equals(Tag.SP_CHAR_CLOSE_PARENTHESES)) forCurrentScope -= 1;
            else if (t.getMark().equals(operator) && forCurrentScope == totalScopes) {
                newExpression = new ExpressionToken();
                if (!idsTaken.contains(i - 1)) {
                    Token leftToken = this.expressaoDaAtribuicaoAtual.get(i - 1);
                    newExpression.setLeftOperator(leftToken);
                    idsTaken.add(i - 1);
                }
                if (!idsTaken.contains(i + 1)) {
                    Token rightToken = this.expressaoDaAtribuicaoAtual.get(i + 1);
                    newExpression.setRightOperator(rightToken);
                    idsTaken.add(i + 1);
                }
                newExpression.setAriOperator(this.expressaoDaAtribuicaoAtual.get(i));
                idsTaken.add(i);
                newExpression.setExpressionScope(forCurrentScope);
                expressionTokens.add(newExpression);
            }
            i += 1;
        }
    }

    /* Gera codigo para atribuicao de expressoes aritmeticas em variaveis*/
    private void gerarTokensTemporariosDeExpressoesAritmeticas(boolean declaracao) {
        LinkedList<ExpressionToken> expressionTokens = new LinkedList<>();
        LinkedList<Integer> idsTaken = new LinkedList<>();
        LinkedList<ExpressionToken> temporaryTokens = new LinkedList<>();
        int i = 1;

        String tag = (this.currentType.getDescription().equals("INTEGER")) ? "int" : "float";

        int totalScopes = 0;
        for (Token t : this.expressaoDaAtribuicaoAtual) {
            if (t.getMark().equals(Tag.SP_CHAR_OPEN_PARENTHESES)) totalScopes += 1;

            SemanticFunctionToken aux = this.getFunctionIdentifier(t.getValue());
            if (aux != null) {
                t.setValue(aux.getValue() + this.expressionFunctionsCalls.getFirst());
                this.expressionFunctionsCalls.removeFirst();
            }
        }

        // Gera os tokens de acordo com a prioridade da operação em cada escopo
        while (totalScopes >= 0) {
            this.gerarTokensExpressoesAritmeticas(totalScopes, expressionTokens, idsTaken, Tag.ARI_OP_MULTIPLICATION);
            this.gerarTokensExpressoesAritmeticas(totalScopes, expressionTokens, idsTaken, Tag.ARI_OP_DIVISION);
            this.gerarTokensExpressoesAritmeticas(totalScopes, expressionTokens, idsTaken, Tag.ARI_OP_ADDITION);
            this.gerarTokensExpressoesAritmeticas(totalScopes, expressionTokens, idsTaken, Tag.ARI_OP_SUBTRACTION);
            totalScopes -= 1;
        }

        // Cria os tokens temporários
        ExpressionToken newToken;
        for (ExpressionToken t : expressionTokens) {
            newToken = new ExpressionToken("temp_" + tag + i, t.getLeftOperator(), t.getAriOperator(), t.getRightOperator(), t.getExpressionScope());
            if ((t.getLeftOperator() == null || t.getLeftOperator().getMark().equals(Tag.SP_CHAR_CLOSE_PARENTHESES))
                    && (t.getRightOperator() == null || t.getRightOperator().getMark().equals(Tag.SP_CHAR_OPEN_PARENTHESES))) {
                if (t.getAriOperator().getMark().equals(Tag.ARI_OP_ADDITION) || t.getAriOperator().getMark().equals(Tag.ARI_OP_SUBTRACTION)) {
                    temporaryTokens.add(newToken);
                } else {
                    try {
                        newToken.setLeftOperator(temporaryTokens.get(i - 3));
                        newToken.setRightOperator(temporaryTokens.get(i - 2));
                    } catch (Exception ex) {

                    }
                    temporaryTokens.add(newToken);
                }
            } else if (t.getLeftOperator() == null) {
                if (t.getAriOperator().getMark().equals(Tag.ARI_OP_ADDITION) || t.getAriOperator().getMark().equals(Tag.ARI_OP_SUBTRACTION)) {
                    temporaryTokens.add(newToken);
                } else {
                    try {
                        newToken.setLeftOperator(temporaryTokens.get(i - 2));
                    } catch (Exception ex) {

                    }
                    temporaryTokens.add(newToken);
                }
            } else if (t.getRightOperator() == null) {
                if (t.getAriOperator().getMark().equals(Tag.ARI_OP_ADDITION) || t.getAriOperator().getMark().equals(Tag.ARI_OP_SUBTRACTION)) {
                    temporaryTokens.add(newToken);
                } else {
                    try {
                        newToken.setRightOperator(temporaryTokens.get(i - 2));
                    } catch (Exception ex) {

                    }
                    temporaryTokens.add(newToken);
                }
            } else {
                temporaryTokens.add(newToken);
            }
            i += 1;
        }

        if (temporaryTokens.size() == 0)
            this.geraDefinicaoDeVariavel(declaracao, this.attribuitionCurrentToken.getValue(), this.expressaoDaAtribuicaoAtual.get(this.expressaoDaAtribuicaoAtual.size() - 2).getValue());

        else if (temporaryTokens.size() == 1) {
            ExpressionToken aux = temporaryTokens.getLast();
            String varValue = aux.getLeftOperator().getValue();
            varValue += aux.getAriOperator().getValue();
            varValue += aux.getRightOperator().getValue();
            this.geraDefinicaoDeVariavel(
                    declaracao,
                    this.attribuitionCurrentToken.getValue(),
                    varValue);
        } else
            this.imprimirTokensTemporariosDeExpressoesAritmeticas(declaracao, temporaryTokens);
    }

    private void imprimirTokensTemporariosDeExpressoesAritmeticas(boolean declaracao, LinkedList<ExpressionToken> temporaryTokens) {
        LinkedList<Integer> idsTaken = new LinkedList<>();
        int i = 0;
        int aux = 0;
        for (ExpressionToken t : temporaryTokens) {
            if (t.getRightOperator() == null) {
                try {
                    t.setRightOperator(temporaryTokens.get(aux - 1));
                    idsTaken.add(aux - 2);
                } catch (Exception ex) {
                }
            }
            if (t.getLeftOperator() == null)
                try {
                    t.setLeftOperator(temporaryTokens.get(aux - 2));
                    idsTaken.add(aux - 1);
                } catch (Exception ex) {
                    t.setLeftOperator(temporaryTokens.get(aux - 1));
                }

            if (t.getLeftOperator() != null &&
                    (t.getLeftOperator().getMark() != null &&
                            t.getLeftOperator().getMark().equals(Tag.SP_CHAR_CLOSE_PARENTHESES))) {
                try {
                    if (idsTaken.contains(i - 1))
                        t.setLeftOperator(temporaryTokens.get(aux - 2));
                    else
                        t.setLeftOperator(temporaryTokens.get(aux - 1));
                    idsTaken.add(aux - 1);
                } catch (Exception ex) {
                }
            }
            if (t.getRightOperator() != null &&
                    (t.getRightOperator().getMark() != null &&
                            t.getRightOperator().getMark().equals(Tag.SP_CHAR_OPEN_PARENTHESES))) {
                for (i = aux; i >= 0; i -= 1) {
                    if (temporaryTokens.get(i).getExpressionScope() > t.getExpressionScope()) {
                        t.setRightOperator(temporaryTokens.get(i));
                        idsTaken.add(i);
                        break;
                    }
                }
            }
            if (!this.registradorTemporarioDeclarado(t.getName())) {
                this.registraIdentificadorTemporario(t);
                System.out.println(this.currentType.toString().toLowerCase() + " " + t);
            } else {
                System.out.println(t);
            }

            aux += 1;
        }
        this.geraDefinicaoDeVariavel(declaracao, this.attribuitionCurrentToken.getValue(), temporaryTokens.get(temporaryTokens.size() - 1).getName());
        System.out.println();
    }

    private final Hashtable<String, ExpressionToken> temporaryIntegers = new Hashtable<>();
    private final Hashtable<String, ExpressionToken> temporaryFloats = new Hashtable<>();

    private boolean registradorTemporarioDeclarado(String name) {
        if (this.currentType.getDescription().equals("INTEGER") && temporaryIntegers.containsKey(this.currentScope() + "_" + name)) return true;
        else return this.currentType.getDescription().equals("FLOATING_POINT") && temporaryFloats.containsKey(this.currentScope() + "_" + name);
    }

    private void registraIdentificadorTemporario(ExpressionToken token) {
        token.setScope(this.currentScope());
        if (this.currentType.getDescription().equals("INTEGER")) {
            temporaryIntegers.put(this.currentScope() + "_" + token.getName(), token);
        } else {
            temporaryFloats.put(this.currentScope() + "_" + token.getName(), token);
        }
    }
}
