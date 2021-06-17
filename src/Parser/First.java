package Parser;

import lexicalAnalyzer.Tag;

import java.util.Hashtable;

public class First {
    public final Hashtable<String, Tag> conteudo = new Hashtable<>()
    {
        {
            put("SP_CHAR_CLOSE_BRACES", Tag.SP_CHAR_CLOSE_BRACES);
            put("IF", Tag.IF);
            put("ELSE", Tag.ELSE);
            put("WHILE", Tag.WHILE);
            put("DO", Tag.DO);
            put("FOR", Tag.FOR);
            put("ID", Tag.ID);
            put("INT", Tag.INT);
            put("CHAR", Tag.CHAR);
            put("FLOAT", Tag.FLOAT);
        }
    };

    public final Hashtable<String, Tag> comando = new Hashtable<>()
    {
        {
            put("IF", Tag.IF);
            put("ELSE", Tag.ELSE);
            put("WHILE", Tag.WHILE);
            put("DO", Tag.DO);
            put("FOR", Tag.FOR);
            put("ID", Tag.ID);
        }
    };

    public final Hashtable<String, Tag> operador_relacional = new Hashtable<>()
    {
        {
            put("REL_OP_LESS_THAN", Tag.REL_OP_LESS_THAN);
            put("REL_OP_GREATER_THAN", Tag.REL_OP_GREATER_THAN);
            put("REL_OP_LESS_THAN_OR_EQUALS_TO", Tag.REL_OP_LESS_THAN_OR_EQUALS_TO);
            put("REL_OP_GREATER_THAN_OR_EQUALS_TO", Tag.REL_OP_GREATER_THAN_OR_EQUALS_TO);
            put("REL_OP_EQUALS", Tag.REL_OP_EQUALS);
            put("REL_OP_NOT_EQUAL_TO", Tag.REL_OP_NOT_EQUAL_TO);
        }
    };

    public final Hashtable<String, Tag> operador_aritmetico = new Hashtable<>()
    {
        {
            put("ARI_OP_ADDITION", Tag.ARI_OP_ADDITION);
            put("ARI_OP_ATTRIBUTION", Tag.ARI_OP_ATTRIBUTION);
            put("ARI_OP_DIVISION", Tag.ARI_OP_DIVISION);
            put("ARI_OP_MULTIPLICATION", Tag.ARI_OP_MULTIPLICATION);
            put("ARI_OP_SUBTRACTION", Tag.ARI_OP_SUBTRACTION);
        }
    };

    public final Hashtable<String, Tag> atribuicao_ou_chamada = new Hashtable<>()
    {
        {
            put("SP_CHAR_OPEN_PARENTHESES", Tag.SP_CHAR_OPEN_PARENTHESES);
        }
    };

    public final Hashtable<String, Tag> declaracao = new Hashtable<>()
    {
        {
            put("INT", Tag.INT);
            put("CHAR", Tag.CHAR);
            put("FLOAT", Tag.FLOAT);
        }
    };

    public final Hashtable<String, Tag> fim_declaracao = new Hashtable<>()
    {
        {
            put("SP_CHAR_OPEN_PARENTHESES", Tag.SP_CHAR_OPEN_PARENTHESES);
            put("ARI_OP_ATTRIBUTION", Tag.ARI_OP_ATTRIBUTION);
            put("SP_CHAR_SEMICOLON", Tag.SP_CHAR_SEMICOLON);
            put("SP_CHAR_COMMA", Tag.SP_CHAR_COMMA);
        }
    };

    public final Hashtable<String, Tag> declaracao_inline = new Hashtable<>()
    {
        {
            put("SP_CHAR_OPEN_COMMA", Tag.SP_CHAR_COMMA);
            put("SP_CHAR_CLOSE_SEMICOLON", Tag.SP_CHAR_SEMICOLON);
        }
    };

//    public final Hashtable<String, Tag> operando = new Hashtable<>(){
//        {
//            put("ID", Tag.ID);
//            put("INTEGER",Tag.INTEGER);
//            put("CHARACTER", Tag.CHARACTER);
//            put("FLOATING_POINT", Tag.FLOATING_POINT);
//        }
//    };

    public final Hashtable<String, Tag> operacao = new Hashtable<>()
    {
        {
            put("SP_CHAR_OPEN_PARENTHESES", Tag.SP_CHAR_OPEN_PARENTHESES);
            put("ID", Tag.ID);
            put("INTEGER",Tag.INTEGER);
            put("CHARACTER", Tag.CHARACTER);
            put("FLOATING_POINT", Tag.FLOATING_POINT);
        }
    };

    public final Hashtable<String, Tag> operacao_linha = new Hashtable<>()
    {
        {
            put("SP_CHAR_OPEN_PARENTHESES", Tag.SP_CHAR_OPEN_PARENTHESES);
            put("ID", Tag.ID);
            put("INTEGER",Tag.INTEGER);
            put("CHARACTER", Tag.CHARACTER);
            put("FLOATING_POINT", Tag.FLOATING_POINT);
        }
    };

    public final Hashtable<String, Tag> expressao_operacao = new Hashtable<>()
    {
        {
            put("ID", Tag.ID);
            put("INTEGER",Tag.INTEGER);
            put("CHARACTER", Tag.CHARACTER);
            put("FLOATING_POINT", Tag.FLOATING_POINT);
        }
    };

    public final Hashtable<String, Tag> expressao_operacao_linha = new Hashtable<>()
    {
        {
            put("ARI_OP_ADDITION", Tag.ARI_OP_ADDITION);
            put("ARI_OP_ATTRIBUTION", Tag.ARI_OP_ATTRIBUTION);
            put("ARI_OP_DIVISION", Tag.ARI_OP_DIVISION);
            put("ARI_OP_MULTIPLICATION", Tag.ARI_OP_MULTIPLICATION);
            put("ARI_OP_SUBTRACTION", Tag.ARI_OP_SUBTRACTION);
        }
    };

    public final Hashtable<String, Tag> extensor_operacao = new Hashtable<>()
    {
        {
            put("ARI_OP_ADDITION", Tag.ARI_OP_ADDITION);
            put("ARI_OP_ATTRIBUTION", Tag.ARI_OP_ATTRIBUTION);
            put("ARI_OP_DIVISION", Tag.ARI_OP_DIVISION);
            put("ARI_OP_MULTIPLICATION", Tag.ARI_OP_MULTIPLICATION);
            put("ARI_OP_SUBTRACTION", Tag.ARI_OP_SUBTRACTION);
        }
    };

    public final Hashtable<String, Tag> expressao_aritmetica = new Hashtable<>() {
        {
            put("SP_CHAR_OPEN_PARENTHESES", Tag.SP_CHAR_OPEN_PARENTHESES);
            put("ID", Tag.ID);
            put("INTEGER", Tag.INTEGER);
            put("CHARACTER", Tag.CHARACTER);
            put("FLOATING_POINT", Tag.FLOATING_POINT);
        }
    };

    public final Hashtable<String, Tag> expressao_aritmetica_linha = new Hashtable<>() {
        {
            put("ARI_OP_SUBTRACTION", Tag.ARI_OP_SUBTRACTION);
            put("ARI_OP_ADDITION", Tag.ARI_OP_ADDITION);
        }
    };

    public final Hashtable<String, Tag> termo = new Hashtable<>()
    {
        {
            put("SP_CHAR_OPEN_PARENTHESES", Tag.SP_CHAR_OPEN_PARENTHESES);
            put("ID", Tag.ID);
            put("INTEGER", Tag.INTEGER);
            put("CHARACTER", Tag.CHARACTER);
            put("FLOATING_POINT", Tag.FLOATING_POINT);
        }
    };

    public final Hashtable<String, Tag> termo_linha = new Hashtable<>()
    {
        {
            put("ARI_OP_MULTIPLICATION", Tag.ARI_OP_MULTIPLICATION);
            put("ARI_OP_DIVISION", Tag.ARI_OP_DIVISION);
        }
    };

    public final Hashtable<String, Tag> fator = new Hashtable<>()
    {
        {
            put("SP_CHAR_OPEN_PARENTHESES", Tag.SP_CHAR_OPEN_PARENTHESES);
            put("ID", Tag.ID);
            put("INTEGER", Tag.INTEGER);
            put("CHARACTER", Tag.CHARACTER);
            put("FLOATING_POINT", Tag.FLOATING_POINT);
        }
    };

    public final Hashtable<String, Tag> operacao_for = new Hashtable<>()
    {
        {
            put("ID", Tag.ID);
        }
    };

    public final Hashtable<String, Tag> fim_parametros = new Hashtable<>()
    {
        {
            put("SP_CHAR_OPEN_COMMA", Tag.SP_CHAR_COMMA);
        }
    };

    public final Hashtable<String, Tag> passada_de_parametros  = new Hashtable<>()
    {
        {
            put("ID", Tag.ID);
            put("INTEGER",Tag.INTEGER);
            put("CHARACTER", Tag.CHARACTER);
            put("FLOATING_POINT", Tag.FLOATING_POINT);
        }
    };
    
    public final Hashtable<String, Tag> fim_passada_de_parametros  = new Hashtable<>()
    {
        {
            put("SP_CHAR_OPEN_COMMA", Tag.SP_CHAR_COMMA);
        }
    };

    public final Hashtable<String, Tag> tipo_dado = new Hashtable<>()
    {
        {
            put("INT", Tag.INT);
            put("CHAR", Tag.CHAR);
            put("FLOAT", Tag.FLOAT);
        }
    };

    public final Hashtable<String, Tag> fim_declaracao_for = new Hashtable<>()
    {
        {
            put("ARI_OP_ATTRIBUTION", Tag.ARI_OP_ATTRIBUTION);
            put("SP_CHAR_SEMICOLON", Tag.SP_CHAR_SEMICOLON);
        }
    };

    public final Hashtable<String, Tag> declaracao_for_inline = new Hashtable<>()
    {
        {
            put("SP_CHAR_COMMA", Tag.SP_CHAR_COMMA);
            put("SP_CHAR_SEMICOLON", Tag.SP_CHAR_SEMICOLON);
        }
    };

    public final Hashtable<String, Tag> condicao = new Hashtable<>()
    {
        {
            put("ARI_OP_ATTRIBUTION", Tag.ARI_OP_ATTRIBUTION);
            put("SP_CHAR_SEMICOLON", Tag.SP_CHAR_SEMICOLON);
        }
    };
}