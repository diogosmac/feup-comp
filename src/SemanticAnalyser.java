import Exceptions.SemanticErrorException;
import SymbolTable.MethodDescriptor;
import SymbolTable.SymbolTable;

import java.util.LinkedList;

public class SemanticAnalyser implements ParserVisitor {

    private SymbolTable table;

    public SemanticAnalyser(SymbolTable table) {
        this.table = table;
    }

    private void printError(String message, int line, int column) {
        System.out.println("SEMANTIC ERROR: " + message + " at line: " + line + ", column: " + column + ".");
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTProgram node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTImport node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTVarDeclaration node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTRegularMethod node, Object data) {
        // get method  id node children
        String methodIdentifier = (String) node.jjtGetValue();
        // get method parameters
        // method parameters is always second child
        // NOTE: may not exist
        LinkedList<String> parameterList = new LinkedList<>();
        if (node.jjtGetChild(1) instanceof ASTMethodParams) {
            parameterList = (LinkedList<String>) node.jjtGetChild(1).jjtAccept(this, data);
        }

        // lookup method with methodIdentifier and parameterList
        // visit RegularMethod children with MethodDescriptor
        // for method specific symbol table lookups
        try {
            MethodDescriptor method = this.table.lookupMethod(methodIdentifier, parameterList);
            return node.childrenAccept(this, method);
        } catch (SemanticErrorException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object visit(ASTReturn node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMethodParams node, Object data) {
        // get parameters node children
        Node[] children = node.jjtGetChildren();
        // create parameter type list
        LinkedList<String> parameterList = new LinkedList<>();
        for (Node child : children) {
            String parameterType = (String) child.jjtAccept(this, data);
            parameterList.add(parameterType);
        }
        return parameterList;
    }

    @Override
    public Object visit(ASTMethodParam node, Object data) {
        // return parameter type
        return node.jjtGetChild(0).jjtAccept(this, data);
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
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTReturnType node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTAssignment node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTObjectCall node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTIdentifier node, Object data) {
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
        SimpleNode leftChild = (SimpleNode) node.jjtGetChild(0);
        SimpleNode rightChild = (SimpleNode) node.jjtGetChild(1);

        String leftChildType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String rightChildType = (String) node.jjtGetChild(1).jjtAccept(this, data);

        // verify data type
        if (!leftChildType.equals("boolean"))
            printError("Operand " + leftChild.jjtGetValue() + " of '&&' is not of 'boolean' type", node.line, node.column);
        else if(!rightChildType.equals("boolean"))
            printError("Operand " + rightChild.jjtGetValue() + " of '&&' is not of 'boolean' type", node.line, node.column);

        // '&&' operator returns an int
        return "boolean";
    }

    @Override
    public Object visit(ASTlt node, Object data) {
        SimpleNode leftChild = (SimpleNode) node.jjtGetChild(0);
        SimpleNode rightChild = (SimpleNode) node.jjtGetChild(1);

        String leftChildType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String rightChildType = (String) node.jjtGetChild(1).jjtAccept(this, data);

        // verify data type
        if (!leftChildType.equals("boolean"))
            printError("Operand " + leftChild.jjtGetValue() + " of '<' is not of 'boolean' type", node.line, node.column);
        else if(!rightChildType.equals("boolean"))
            printError("Operand " + leftChild.jjtGetValue() + " of '<' is not of 'boolean' type", node.line, node.column);

        // '<' operator returns a boolean
        return "boolean";
    }

    @Override
    public Object visit(ASTsum node, Object data) {
        SimpleNode leftChild = (SimpleNode) node.jjtGetChild(0);
        SimpleNode rightChild = (SimpleNode) node.jjtGetChild(1);

        String leftChildType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String rightChildType = (String) node.jjtGetChild(1).jjtAccept(this, data);

        // verify data type
        if (!leftChildType.equals("integer"))
            printError("Operand " + leftChild.jjtGetValue() + " of '+' is not of 'integer' type", node.line,node.column);
        else if(!rightChildType.equals("integer"))
            printError("Operand " + rightChild.jjtGetValue() + " of '+' is not of 'integer' type", node.line,node.column);

        // '+' operator returns an int
        return "integer";
    }

    @Override
    public Object visit(ASTsub node, Object data) {
        SimpleNode leftChild = (SimpleNode) node.jjtGetChild(0);
        SimpleNode rightChild = (SimpleNode) node.jjtGetChild(1);

        String leftChildType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String rightChildType = (String) node.jjtGetChild(1).jjtAccept(this, data);

        // verify data type
        if (!leftChildType.equals("integer"))
            printError("Operand " + leftChild.jjtGetValue() + " of '-' is not of 'integer' type", node.line,node.column);
        else if(!rightChildType.equals("integer"))
            printError("Operand " + rightChild.jjtGetValue() + " of '-' is not of 'integer' type", node.line,node.column);

        // '-' operator returns an int
        return "integer";
    }

    @Override
    public Object visit(ASTmult node, Object data) {
        SimpleNode leftChild = (SimpleNode) node.jjtGetChild(0);
        SimpleNode rightChild = (SimpleNode) node.jjtGetChild(1);

        String leftChildType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String rightChildType = (String) node.jjtGetChild(1).jjtAccept(this, data);

        // verify data type
        if (!leftChildType.equals("integer"))
            printError("Operand " + leftChild.jjtGetValue() + " of '*' is not of 'integer' type", node.line,node.column);
        else if(!rightChildType.equals("integer"))
            printError("Operand " + rightChild.jjtGetValue() + " of '*' is not of 'integer' type", node.line,node.column);

        // '*' operator returns an int
        return "integer";
    }

    @Override
    public Object visit(ASTdiv node, Object data) {
        SimpleNode leftChild = (SimpleNode) node.jjtGetChild(0);
        SimpleNode rightChild = (SimpleNode) node.jjtGetChild(1);

        String leftChildType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String rightChildType = (String) node.jjtGetChild(1).jjtAccept(this, data);

        // verify data type
        if (!leftChildType.equals("integer"))
            printError("Operand " + leftChild.jjtGetValue() + " of '/' is not of 'integer' type", node.line,node.column);
        else if(!rightChildType.equals("integer"))
            printError("Operand " + rightChild.jjtGetValue() + " of '/' is not of 'integer' type", node.line, node.column);

        // '/' operator returns an int
        return "integer";
    }

    @Override
    public Object visit(ASTid node, Object data) {
        // get method descriptor
        MethodDescriptor method = (MethodDescriptor) data;
        // get variable id
        String variableIdentifier = (String) node.jjtGetValue();
        // lookup variable in method
        try {
            return method.lookupVariable(variableIdentifier);
        } catch (SemanticErrorException ignored) {
        }
        // lookup variable in class
        try {
            return table.lookupAttribute(variableIdentifier);
        } catch (SemanticErrorException e) {
            this.printError(e.getMessage(), node.line, node.column);
        }
        // Semantic error: return null
        return null;
    }

    @Override
    public Object visit(ASTGetLength node, Object data) {
        return "int";
    }

    @Override
    public Object visit(ASTCallMethod node, Object data) {
        // get method id
        String methodIdentifier = (String) node.jjtGetValue();
        // get method call arguments list

        // lookup identifier in the symbol table
        // this.table.lookupMethod(methodIdentifier, );
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
            printError("Operand " + child.jjtGetValue() + " of '!' is not of 'boolean' type", node.line, node.column );
        // '!' operator returns a boolean
        return "boolean";
    }

    @Override
    public Object visit(AST_new node, Object data) {
        return null;
    }
}
