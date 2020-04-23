import SymbolTable.SymbolTable;

public class SymbolTableBuilder {

    public SymbolTable buildSymbolTable(SimpleNode root) {
        SymbolTable table = new SymbolTable();
        // get root's children
        Node[] children = root.jjtGetChildren();
        // find ASTClassDeclaration Node
        for (Node child : children)
            if (child instanceof ASTClassDeclaration)
                this.processClassDeclaration((ASTClassDeclaration) child);

        return table;
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

    }

    private void processRegularMethodDeclaration(ASTRegularMethod node) {

    }

    private void processMainMethodDeclaration(ASTMainMethod node) {

    }
}
