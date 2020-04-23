import SymbolTable.SymbolTable;

public class SemanticAnalyser {
    /**
     * AST
     */
    private SimpleNode root;
    /**
     * Class symbol table
     */
    private SymbolTable table;

    public SemanticAnalyser(SimpleNode root, SymbolTable table) {
        this.root = root;
        this.table = table;
    }

    public void analyse() {
        // get children
        Node[] children = this.root.jjtGetChildren();
    }
}
