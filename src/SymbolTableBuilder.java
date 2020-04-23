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
        //get method  id node children
        String methodName = node.methodId;
        // get method type
        ASTType typeNode = (ASTType) node.jjtGetChild(0);
        String methodType = typeNode.getType();
        // put method to method descriptors
        this.table.addMethod(methodName, methodType);

        for(Node child : children){
            if (child instanceof ASTMethodParam){
                // parse parameter declarations
                String parameterName = ((ASTMethodParam) child).paramId;
                // get parameter type
                ASTType typeParam = (ASTType) child.jjtGetChild(0);
                String paramType = typeParam.getType();
                // put parameter entry in symbol table
                this.table.addMethodParameter(methodName, parameterName, paramType);

            }else if (child instanceof ASTVarDeclaration) {
                // parse variable declarations
                String variableName = ((ASTVarDeclaration) child).id;
                // get variable type
                ASTType typeVar = (ASTType) child.jjtGetChild(0);
                String varType = typeVar.getType();
                // put variable entry in symbol table
                this.table.addMethodVariable(methodName, variableName, varType);
            }
        }
    }

    private void processMainMethodDeclaration(ASTMainMethod node) {
        // get method declaration node children
        Node[] children = node.jjtGetChildren();
        // add method "main" to the method descriptors
        this.table.addMethod("main", "void");
        // find variable and method declarations
        for (Node child : children) {
            if (child instanceof ASTMainParams) {
                // parse main parameters
                String parameterName = ((ASTMainParams) child).paramId;
                this.table.addMethodParameter("main", parameterName, "String[]");
            } else if (child instanceof ASTVarDeclaration) {
                // parse variable declarations
                String variableIdentifier = ((ASTVarDeclaration) child).id;
                // get variable type
                ASTType typeNode = (ASTType) child.jjtGetChild(0);
                String variableType = typeNode.getType();
                // put variable entry in symbol table
                this.table.addMethodVariable("main", variableIdentifier, variableType);
            }
        }
    }
}
