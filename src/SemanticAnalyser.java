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

    private static int MAX_ERRORS = 5;

    private static int currentErrors = 0;

    public SemanticAnalyser(SimpleNode root, SymbolTable table) {
        this.root = root;
        this.table = table;
    }

    public void analyse() {
        // start by root
        this.analyseNode(this.root);
    }

    private void analyseNode(SimpleNode node) {
        // analyse single node
        try {
            node.analyse(this);
        } catch (SemanticException e) {
            currentErrors++;
            System.err.println("SEMANTIC ERROR: " + e.getMessage());
        }

        // get all children
        Node[] children = node.jjtGetChildren();
        // check if current node has children
        if (children == null)
            return;
        // visit all nodes
        for (Node child : children) {
            this.analyseNode((SimpleNode) child);
        }
    }

    public void analyseAnd(ASTand node) throws SemanticException {
        // get children
        SimpleNode left = (SimpleNode) node.jjtGetChild(0);
        SimpleNode right = (SimpleNode) node.jjtGetChild(1);
        // check if children:
        // - boolean values
        // - boolean operations
        // - boolean identifiers
    }
}
