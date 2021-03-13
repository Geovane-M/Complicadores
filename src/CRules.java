import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CRules {
    private static final List<Character> relational_operators =  Arrays.asList('<', '>', '!');
    private static final List<Character> arithmetic_operators = Arrays.asList('+', '-', '*', '/');
    private static final List<Character> special_chars =  Arrays.asList(')', '(', '{','}', ',', ';');
    private static final List<String> reserved_words =  Arrays.asList("main", "if", "else", "while", "do", "for", "int", "float", "char");

    static boolean is_relational_operator(char c){
        return relational_operators.contains(c);
    }

    static boolean is_arithmetic_operator(char c){
        return arithmetic_operators.contains(c);
    }

    static boolean is_digit(char c){
        return c >= '0' && c <= '9';
    }

    static boolean is_letter(char c){
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    static boolean is_special_char(char c){
        return special_chars.contains(c);
    }

    static boolean is_reserved_word(String word){
        return reserved_words.contains(word);
    }
}