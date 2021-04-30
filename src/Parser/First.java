package Parser;

import java.util.Hashtable;

public class First {
    public final Hashtable<String, String> conteudo = new Hashtable<>()
    {
        {
            put("SP_CHAR_CLOSE_BRACES", "SP_CHAR_CLOSE_BRACES");
            put("IF", "IF");
            put("ELSE", "ELSE");
            put("WHILE", "WHILE");
            put("DO", "DO");
            put("FOR", "FOR");
            put("ID", "ID");
            put("INT", "INT");
            put("CHAR", "CHAR");
            put("FLOAT", "FLOAT");
        }
    };

    public final Hashtable<String, String> comando = new Hashtable<>()
    {
        {
            put("IF", "IF");
            put("ELSE", "ELSE");
            put("WHILE", "WHILE");
            put("DO", "DO");
            put("FOR", "FOR");
            put("ID", "ID");
        }
    };

    public final Hashtable<String, String> operador_relacional = new Hashtable<>()
    {
        {
            put("REL_OP_LESS_THAN", "REL_OP_LESS_THAN");
            put("REL_OP_GREATER_THAN", "REL_OP_GREATER_THAN");
            put("REL_OP_LESS_THAN_OR_EQUALS_TO", "REL_OP_LESS_THAN_OR_EQUALS_TO");
            put("REL_OP_GREATER_THAN_OR_EQUALS_TO", "REL_OP_GREATER_THAN_OR_EQUALS_TO");
            put("REL_OP_EQUALS", "REL_OP_EQUALS");
            put("REL_OP_NOT_EQUAL_TO", "REL_OP_NOT_EQUAL_TO");
        }
    };

    public final Hashtable<String, String> declaracao = new Hashtable<>()
    {
        {
            put("INT", "INT");
            put("CHAR", "CHAR");
            put("FLOAT", "FLOAT");
        }
    };

    public final Hashtable<String, String> tipo_dado = new Hashtable<>()
    {
        {
            put("INT", "INT");
            put("CHAR", "CHAR");
            put("FLOAT", "FLOAT");
        }
    };

    public final Hashtable<String, String> fim_declaracao = new Hashtable<>()
    {
        {
            put("SP_CHAR_OPEN_PARENTHESES", "SP_CHAR_OPEN_PARENTHESES");
            put("ARI_OP_ATTRIBUTION", "ARI_OP_ATTRIBUTION");
            put("SP_CHAR_SEMICOLON", "SP_CHAR_SEMICOLON");
        }
    };

    public final Hashtable<String, String> declaracao_inline = new Hashtable<>()
    {
        {
            put("SP_CHAR_OPEN_COMMA", "SP_CHAR_OPEN_COMMA");
            put("SP_CHAR_CLOSE_SEMICOLON", "SP_CHAR_CLOSE_SEMICOLON");
        }
    };

    public final Hashtable<String, String> operacao = new Hashtable<>()
    {
        {
            put("SP_CHAR_OPEN_PARENTHESES", "SP_CHAR_OPEN_PARENTHESES");
            put("ID", "ID");
            put("INTEGER","INTEGER");
            put("CHARACTER", "CHARACTER");
            put("FLOATING_POINT", "FLOATING_POINT");
        }
    };

    public final Hashtable<String, String> operacao_linha = new Hashtable<>()
    {
        {
            put("SP_CHAR_OPEN_PARENTHESES", "SP_CHAR_OPEN_PARENTHESES");
            put("ID", "ID");
            put("INTEGER","INTEGER");
            put("CHARACTER", "CHARACTER");
            put("FLOATING_POINT", "FLOATING_POINT");
        }
    };

    public final Hashtable<String, String> expressao_operacao = new Hashtable<>()
    {
        {
            put("ID", "ID");
            put("INTEGER","INTEGER");
            put("CHARACTER", "CHARACTER");
            put("FLOATING_POINT", "FLOATING_POINT");
        }
    };

    public final Hashtable<String, String> expressao_operacao_linha = new Hashtable<>()
    {
        {
            put("ARI_OP_ADDITION", "ARI_OP_ADDITION");
            put("ARI_OP_ATTRIBUTION", "ARI_OP_ATTRIBUTION");
            put("ARI_OP_DIVISION", "ARI_OP_DIVISION");
            put("ARI_OP_MULTIPLICATION", "ARI_OP_MULTIPLICATION");
            put("ARI_OP_SUBTRACTION", "ARI_OP_SUBTRACTION");
        }
    };

    public final Hashtable<String, String> extensor_operacao = new Hashtable<>()
    {
        {
            put("ARI_OP_ADDITION", "ARI_OP_ADDITION");
            put("ARI_OP_ATTRIBUTION", "ARI_OP_ATTRIBUTION");
            put("ARI_OP_DIVISION", "ARI_OP_DIVISION");
            put("ARI_OP_MULTIPLICATION", "ARI_OP_MULTIPLICATION");
            put("ARI_OP_SUBTRACTION", "ARI_OP_SUBTRACTION");
        }
    };

    public final Hashtable<String, String> operacao_for = new Hashtable<>()
    {
        {
            put("ID", "ID");
        }
    };

    public final Hashtable<String, String> declaracao_for = new Hashtable<>()
    {
        {
            put("INT", "INT");
            put("CHAR", "CHAR");
            put("FLOAT", "FLOAT");
        }
    };

    public final Hashtable<String, String> fim_declaracao_for = new Hashtable<>()
    {
        {
            put("ARI_OP_ATTRIBUTION", "ARI_OP_ATTRIBUTION");
            put("SP_CHAR_SEMICOLON", "SP_CHAR_SEMICOLON");
        }
    };
}