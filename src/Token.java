class Token{
    private final String[] token_types = {
            "ident",
            "op_rela",
            "op_arit",
            "separ",
            "resev",
    };

    private String name;
    private long line;
    private long column;
    private String actual_value;

    Token(String name, long line, long column, String actual_value){
        this.name = name;
        this.line = line;
        this.column = column;
        this.actual_value = actual_value;
    }

    public Token() {

    }

    long getLine(){
        return this.line;
    }

    long getColumn(){
        return this.column;
    }

    boolean isNull(){
        return this.actual_value.equals("none");
    }

    @Override
    public String toString(){
        return "Token name: " + this.name +
                " --- Actual value: " + this.actual_value + " --- Position (line:column) " + this.line + ":" + this.column;
    }
}