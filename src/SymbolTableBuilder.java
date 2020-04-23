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
        for (Node child : children) {
            if (child instanceof ASTClassDeclaration)
                this.processClassDeclaration((ASTClassDeclaration) child);
            else if (child instanceof ASTImportDeclaration)
                this.processImportDeclaration((ASTImportDeclaration) child);
        }

        return this.table;
    }

    private void processClassDeclaration(ASTClassDeclaration node) {
        // get class declaration node children
        Node[] children = node.jjtGetChildren();
        // check if Class has children
        if (children == null) {
            return;
        }
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
        // get variable id node children
        String variableIdentifier = node.id;
        // get variable type
        ASTType typeNode = (ASTType) node.jjtGetChild(0);
        String variableType = typeNode.getType();
        // put variable entry in symbol table
        this.table.addVariable(variableIdentifier, variableType);
    }

    private void processRegularMethodDeclaration(ASTRegularMethod node) {
        // get method declaration node children
        Node[] children = node.jjtGetChildren();
    }

    private void processMainMethodDeclaration(ASTMainMethod node) {
        // get method declaration node children
        Node[] children = node.jjtGetChildren();
    }

    private void processImportDeclaration(ASTImportDeclaration node) {
        // get import declaration node children
        Node[] children = node.jjtGetChildren();
        // check if there are imports
        if (children == null) {
            return;
        }
        // find class and method declarations
        for (Node child : children) {
            if (child instanceof ASTImport)
                this.processImport((ASTImport) child);
        }
    }

    private void processImport(ASTImport node) {
        // get import id node children
        String importIdentifier = node.id;
        // put import entry in symbol table
        this.table.addImport(variableIdentifier, node.isStatic);
        // get import node children
        Node[] children = node.jjtGetChildren();
        // check if there are parameters or return value
        if (children == null) {
            return;
        }
        // add parameters and return value
        for (Node child : children) {
            if (child instanceof ASTType) {
                ASTType typeNode = (ASTType) child;
                this.table.addImportParameter(importIdentifier, typeNode.id, typeNode.getType());
            }
            else if (child instanceof ASTReturnType) {
                ASTReturnType returnTypeNode = (ASTReturnType) child;
                this.table.setImportReturnType(importIdentifier, returnTypeNode.getType());
            }
        }
    }

}
