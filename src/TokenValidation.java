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
    private int curr_column = 0;

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
        this.curr_column = 0;
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
                    } else {
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

    Token validateDigit(){
        this.incrementId();

        if((this.state == 2 || this.state == 3) && this.c_char() == '.' || (this.state == 2 && !CRules.is_digit(this.c_char())))
            throw new RuntimeException("Invalid float number");
        else if ((this.state == 1 || this.state == 3) && this.isEOF()){
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
        else if ((this.state == 1 || this.state == 3) && (!CRules.is_digit(this.c_char()))){
            if (this.state == 3)
                return new Token("<float/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
            return new Token("<int/>", this.curr_line, this.curr_column - this.current_string.length(), this.current_string);
        }
        else
            throw new RuntimeException("Invalid number");
    }
}
