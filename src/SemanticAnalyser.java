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
        // start by root
        this.analyseNode(this.root);
    }

    private void analyseNode(SimpleNode node) {
        // get all children
        Node[] children = node.jjtGetChildren();
        // visit all nodes
        for (Node child : children) {
            // Operators
            if (child instanceof ASTand)
                this.analyseAnd((ASTand) child);
            else if (child instanceof ASTlt)
                this.analyseLessThan((ASTlt) child);
            else if (child instanceof ASTsum)
                return;
            else if (child instanceof ASTsub)
                return;
            else if (child instanceof ASTmult)
                return;
            else if (child instanceof ASTdiv)
                return;
            else if (child instanceof ASTnot)
                return;
            // control flow
            else if (child instanceof ASTIfElseBlock)
                return;
            else if (child instanceof ASTWhileBlock)
                return;
            // declarations
            else if (child instanceof ASTVarDeclaration)
                return;
            // repeat analysis for child nodes
            this.analyseNode((SimpleNode) child);
        }
    }

    private void analyseAnd(ASTand child) {

    }

    private void analyseLessThan(ASTlt child) {

    }


}
