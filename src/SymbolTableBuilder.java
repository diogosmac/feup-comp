import Exceptions.SemanticErrorException;
import SymbolTable.SymbolTable;

public class SymbolTableBuilder implements ParserVisitor {

    private SimpleNode root;

    private SymbolTable table;

    public SymbolTableBuilder(SimpleNode root) {
        this.root = root;
        this.table = new SymbolTable();
    }

    public SymbolTable buildSymbolTable() {
        // start visiting nodes from the root
        this.visit(root, null);
        // return complete symbol table
        return this.table;
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
        // get import id node children
        String importIdentifier = (String) node.jjtGetValue();
        // put import entry in symbol table
        this.table.addImport(importIdentifier, node.isStatic, node.isMethod);
        // get import node children
        Node[] children = node.jjtGetChildren();
        // check if there are parameters or return value
        if (children == null) {
            return null;
        }
        // add parameters and return value
        for (Node child : children) {
            if (child instanceof ASTType) {
                ASTType typeNode = (ASTType) child;
                this.table.addImportParameter(importIdentifier, (String) typeNode.jjtGetValue());
            }
            else if (child instanceof ASTReturnType) {
                ASTReturnType returnTypeNode = (ASTReturnType) child;
                this.table.setImportReturnType(importIdentifier, (String) returnTypeNode.jjtGetValue());
            }
        }
        return null;
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTVarDeclaration node, Object data) {
        // Class attribute declaration
        if (data == null) {
            // get variable id node children
            String variableIdentifier = (String) node.jjtGetValue();
            // get variable type
            String variableType = (String) node.jjtGetChild(0).jjtAccept(this, data);
            // put variable entry in symbol table
            try {
                this.table.addVariable(variableIdentifier, variableType);
            } catch (SemanticErrorException e) {
                this.printError(e.getMessage(), node.line, node.column);
            }
        } else { // Method variable declaration
            // get method name from data
            String methodName = (String) data;
            // parse variable declarations
            String variableName = (String) node.jjtGetValue();
            // get variable type
            String varType = (String) node.jjtGetChild(0).jjtAccept(this, data);
            // put variable entry in symbol table
            try {
                this.table.addMethodVariable(methodName, variableName, varType);
            } catch (SemanticErrorException e) {
                this.printError(e.getMessage(), node.line, node.column);
            }
        }
        return null;
    }

    @Override
    public Object visit(ASTRegularMethod node, Object data) {
        // get method  id node children
        String methodName = (String) node.jjtGetValue();
        // get method type
        String methodType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        // put method to method descriptors
        this.table.addMethod(methodName, methodType);
        // get Method Parameters and Variable Declarations
        // send method name for editing MethodDescriptor
        node.childrenAccept(this, methodName);
        // check if this method is in sync with others
        // same identifier -> same return type, but different parameter list
        try {
            this.table.checkEqualMethods(methodName);
        } catch (SemanticErrorException e) {
            this.printError(e.getMessage(), node.line, node.column);
        }
        return null;
    }

    @Override
    public Object visit(ASTReturn node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMethodParam node, Object data) {
        // get method name from data
        String methodName = (String) data;
        // parse parameter declarations
        String parameterName = (String) node.jjtGetValue();
        // get parameter type
        String paramType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        // put parameter entry in symbol table
        try {
            this.table.addMethodParameter(methodName, parameterName, paramType);
        } catch (SemanticErrorException e) {
            this.printError(e.getMessage(), node.line, node.column);
        }
        return null;
    }

    @Override
    public Object visit(ASTMainMethod node, Object data) {
        // add method "main" to the method descriptors
        this.table.addMethod("main", "void");
        // get main Method Parameters and Variable Declarations
        // send method name for editing MethodDescriptor
        node.childrenAccept(this, "main");
        return null;
    }

    @Override
    public Object visit(ASTMainParams node, Object data) {
        node.childrenAccept(this, data);
        // parse main parameters
        String parameterName = (String) node.jjtGetValue();
        // add parameter name to main method
        try {
            this.table.addMethodParameter("main", parameterName, "String[]");
        } catch (SemanticErrorException e) {
            this.printError(e.getMessage(), node.line, node.column);
        }
        return null;
    }

    @Override
    public Object visit(ASTType node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTReturnType node, Object data) {
        return node.jjtGetValue();
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
        return null;
    }

    @Override
    public Object visit(ASTbool node, Object data) {
        return null;
    }

    @Override
    public Object visit(AST_this node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTnot node, Object data) {
        return null;
    }

    @Override
    public Object visit(AST_new node, Object data) {
        return null;
    }
}
