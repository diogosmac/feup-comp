import SymbolTable.SymbolTable;

public class SymbolTableBuilder {

    private SimpleNode root;

    private SymbolTable table;

    public SymbolTableBuilder(SimpleNode root) {
        this.root = root;
        this.table = new SymbolTable();
    }

    public SymbolTable buildSymbolTable() {
        // get root's children
        Node[] children = this.root.jjtGetChildren();
        // find ASTClassDeclaration Node
        for (Node child : children)
            if (child instanceof ASTClassDeclaration)
                this.processClassDeclaration((ASTClassDeclaration) child);

        return this.table;
    }

    private void processClassDeclaration(ASTClassDeclaration node) {
        // get class declaration node children
        Node[] children = node.jjtGetChildren();
        // find variable and method declarations
        for (Node child : children) {
            if (child instanceof ASTVarDeclaration)
                this.processVariableDeclaration((ASTVarDeclaration) child);
            else if (child instanceof ASTRegularMethod)
                this.processRegularMethodDeclaration((ASTRegularMethod) child);
            else if (child instanceof ASTMainMethod)
                this.processMainMethodDeclaration((ASTMainMethod) child);
        }
    }

    private void processVariableDeclaration(ASTVarDeclaration node) {
        // get variable declaration node children
        Node[] children = node.jjtGetChildren();
    }

    private void processRegularMethodDeclaration(ASTRegularMethod node) {
        // get method declaration node children
        Node[] children = node.jjtGetChildren();
    }

    private void processMainMethodDeclaration(ASTMainMethod node) {
        // get method declaration node children
        Node[] children = node.jjtGetChildren();
    }
}
