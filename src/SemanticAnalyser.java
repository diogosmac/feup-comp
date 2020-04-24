public class SemanticAnalyser implements ParserVisitor {

    @Override
    public Object visit(SimpleNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTProgram node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTImport node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTVarDeclaration node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTRegularMethod node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTReturn node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMethodParam node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMainMethod node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMainParams node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTType node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTReturnType node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTStatement node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTAssignStatement node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTIfElseBlock node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTWhileBlock node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTand node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTlt node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTsum node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTsub node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTmult node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTdiv node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTid node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTGetLength node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTCallMethod node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTinteger node, Object data) {
        return "int";
    }

    @Override
    public Object visit(ASTbool node, Object data) {
        return "boolean";
    }

    @Override
    public Object visit(AST_this node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTnot node, Object data) {
        // '!' (not) is a unary operator, therefore it only has one child node
        // that child must be of type boolean
        SimpleNode child = (SimpleNode) node.jjtGetChild(0);
        String childType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        // verify data type
        if (!childType.equals("boolean"))
            printError("Operand " + child.jjtGetValue() + " of '!' is not of 'boolean' type at line " + node.line + ", column " + node.column + ".");
        // '!' operator returns a boolean
        return "boolean";
    }

    @Override
    public Object visit(AST_new node, Object data) {
        return null;
    }

    private void printError(String message) {
        System.out.println("SEMANTIC ERROR: " + message);
    }
}
