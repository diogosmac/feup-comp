public class Symbol {
    public int order;
    public String id;
    public String type;
    public int line;
    public int column;

    public Symbol(int order, String id, String type, int line, int column) {
        this.order = order;
        this.id = id;
        this.type = type;
        this.line = line;
        this.column = column;
    }
}
