/*
• letra ::= [a-z]
• digito ::= [0-9]
• inteiro ::= dígito+
• float ::= dígito+.dígito+
• char ::= 'letra' | 'dígito'
• identificador ::= (letra | "_")(letra | "_" | dígito)*
• operador_relacional ::= <  |  >  |  <=  |  >=  |  ==  |  !=
• operador_aritmético ::= "+"  |  "-"  |  "*"  |  "/"  |  "="
• caracter_especial ::= ")"  |  "("  |  "{"  |  "}"  |  ","  |  ";"
• palavra_reservada ::= main  |  if  |  else  |  while  |  do  |  for  |  int  |  float  |  char
*/

import java.io.*;

class TokenValidation {
    private int state = 0;
    private String current_string = "";
    private int curr_id = 0;
    private String data;

    private int curr_line = 1;
    private int curr_column = 1;

    void init(){
        try {
            File file = new File("D:/Java_projects/analisador_lexico/src/teste.txt");
            FileInputStream fis = new FileInputStream(file);
            byte[] _data = new byte[(int) file.length()];
            fis.read(_data);
            fis.close();

            data = new String(_data, "UTF-8");
        } catch(FileNotFoundException | UnsupportedEncodingException ex){
            System.out.println("Error :(");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isEOF(){
        return this.curr_id >= this.data.length();
    }

    private boolean isSpace(char cr_char){
        return cr_char == ' ' || cr_char == '\t' || cr_char == '\r';
    }

    private void incrementLine(){
        this.curr_line += 1;
        this.curr_column = 1;
    }

    private void incrementId(){
        this.curr_id += 1;
        this.curr_column += 1;
    }

    Token validateToken(){
        this.current_string = "";
        this.state = 0;
        while(!this.isEOF()) {
            switch (state) {
                case 0:
                    char cr_char = data.charAt(this.curr_id);
                    if(cr_char == '\n'){
                        this.incrementLine();
                        this.curr_id += 1;
                    }
                    else if (this.isSpace(cr_char)) {
                        incrementId();
                    } else if (CRules.is_digit(cr_char)) {
                        this.current_string += cr_char;
                        this.state = 1;
                        return validateDigit();
                    } else if(cr_char == '\''){
                        this.current_string += cr_char;
                        this.state = 4;
                        return validateChar();
                    } else if(cr_char == '"'){
                        this.current_string += cr_char;
                        this.state = 6;
                        return validateString();
                    }
                    else if((cr_char == '=' && this.next_char() != '=') || CRules.is_arithmetic_operator(cr_char)){
                        return this.validateAriOp();
                    }
                    else if(cr_char == '=' || CRules.is_relational_operator(cr_char)){
                        if (cr_char == '=') {
                            this.current_string += cr_char;
                            this.state = 8;
                        }
                        return validateRelOp();
                    }
                    else if(cr_char== '_' || CRules.is_letter(cr_char)){
                        return this.validateId();
                    }
                    else {
                        incrementId();
                    }
                    break;
            }
        }
        return null;
    }

    char c_char(){
        return this.data.charAt(this.curr_id);
    }

    Character next_char() {
        if (this.curr_id < this.data.length() - 1)
            return this.data.charAt(this.curr_id + 1);
        return null;
    }

    private Token validateId() {
        if (this.state != 0)
            this.incrementId();
        char c = this.c_char();

        if (this.state == 13 && !CRules.is_digit(c) && !CRules.is_letter(c) && c != '_')
            return new Token("<id/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        else if (this.state == 14 && !CRules.is_letter(c) && !CRules.is_digit(c) && !CRules.is_letter(c) && c != '_') {
            if (CRules.is_reserved_word(this.current_string))
                return new Token("<" + this.current_string + "/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
            return new Token("<id/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        }

        this.current_string += c;

        if (this.state == 0 && c == '_') {
            this.state = 13;
            return this.validateId();
        } else if (this.state == 13 && (CRules.is_digit(c) || CRules.is_letter(c) || c == '_'))
            return this.validateId();
        else if (this.state == 0 && CRules.is_letter(c)) {
            this.state = 14;
            return this.validateId();
        } else if (this.state == 14 && CRules.is_letter(c))
            return this.validateId();
        else {
            this.state = 13;
            return this.validateId();
        }
    }

    private Token validateRelOp(){
        if (this.state != 0)
            this.incrementId();
        char c = this.c_char();
        if (this.state == 8 && c == '=') {
            this.current_string += c;
            this.incrementId();
            return new Token("<rel_op_comparison/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        }
        else if(this.state == 0 && c == '!'){
            this.current_string += c;
            this.state = 9;
            return validateRelOp();
        }
        else if(this.state == 9 && c == '='){
            this.current_string += c;
            this.state = 9;
            return new Token("<rel_op_difference/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        }
        else if(this.state == 9 && !CRules.is_relational_operator(c)){
            this.current_string += c;
            this.state = 9;
            return new Token("<rel_op_not/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        }
        else if(this.state == 0 && c == '<'){
            this.current_string += c;
            this.state = 10;
            return validateRelOp();
        }
        else if(this.state == 10 && c == '='){
            this.current_string += c;
            this.state = 10;
            return new Token("<rel_op_minor_or_equals/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        }
        else if(this.state == 10 && !CRules.is_relational_operator(c)){
            this.current_string += c;
            this.state = 10;
            return new Token("<rel_op_minor_than/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        }
        else if(this.state == 0 && c == '>'){
            this.current_string += c;
            this.state = 11;
            return validateRelOp();
        }
        else if(this.state == 11 && c == '='){
            this.current_string += c;
            return new Token("<rel_op_greater_or_equals/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        }
        else if(this.state == 11 && !CRules.is_relational_operator(c)){
            this.current_string += c;
            return new Token("<rel_op_greater_than/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        }
        else
            throw new RuntimeException("Invalid relational operator formation at " + this.curr_line + ":" + (this.curr_column));
    }

    private Token validateAriOp(){
        char c = this.c_char();

        this.incrementId();
        this.current_string += c;
        if(c == '+')
            return new Token("<ar_op_add/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        else if(c == '-')
            return new Token("<ar_op_sub/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        else if(c == '*')
            return new Token("<ar_op_mul/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        else if(c == '=')
            return new Token("<ar_op_ass/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        else
            return new Token("<ar_op_div/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
    }

    private Token validateString(){
        this.incrementId();

        if(this.state == 6 && (this.isEOF() || this.c_char() == '\n')){
            throw new RuntimeException("Invalid string formation. '\"' expected at " + this.curr_line + ":" + (this.curr_column));
        }
        else if(this.state == 6 && this.c_char() != '"'){
            this.current_string += this.c_char();
            return validateString();
        }
        else{
            this.current_string += this.c_char();
            this.incrementId();
            return new Token("<string/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        }
    }

    private Token validateChar(){
        this.incrementId();

        if(this.state == 4 && this.c_char() != '\''){
            this.current_string += this.c_char();
            this.state = 5;
            return validateChar();
        }
        else if(this.state == 5 && this.c_char() == '\''){
            this.current_string += this.c_char();
            this.state = 5;
            this.incrementId();
            return new Token("<char/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        }
        else{
            throw new RuntimeException("Invalid character formation at " + this.curr_line + ":" + (this.curr_column - this.current_string.length()));
        }
    }

    private Token validateDigit(){
        this.incrementId();

        if((this.state == 2 || this.state == 3) && this.c_char() == '.' ||
                (this.state == 2 && !CRules.is_digit(this.c_char())))
            throw new RuntimeException("Invalid float number formation at " + this.curr_line + ":" + (this.curr_column - this.current_string.length()));
        else if ((this.state == 1 || this.state == 3) && this.isEOF() || !CRules.is_digit(this.c_char())){
            if (this.state == 3)
                return new Token("<float/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
            return new Token("<int/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        }
        else if((this.state == 1 && data.charAt(this.curr_id) == '.')){
            this.state = 2;
            this.current_string += this.c_char();
            return validateDigit();
        }
        else if (this.state == 2 && CRules.is_digit(this.c_char())){
            this.state = 3;
            this.current_string += this.c_char();
            return validateDigit();
        }
        else if ((this.state == 1 || this.state == 3) && CRules.is_digit(this.c_char())){
            this.current_string += this.c_char();
            return validateDigit();
        }
        else if ((this.state == 1 || this.state == 3) && CRules.is_letter(this.c_char()))
            throw new RuntimeException("Invalid identifier formation at " + this.curr_line + ":" + (this.curr_column - this.current_string.length()));
        else
            throw new RuntimeException("Invalid number formation at " + this.curr_line + ":" + (this.curr_column - this.current_string.length()));
    }
}
