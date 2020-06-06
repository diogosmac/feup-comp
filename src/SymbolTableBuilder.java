import Exceptions.SemanticErrorException;
import SymbolTable.SymbolTable;
import SymbolTable.ImportDescriptor;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * <h1>Symbol Table Builder</h1>
 * <p>The main goal of this class is to generate a symbol
 * table with the information present in the parsed AST.</p>
 * <p>This class makes use of the ParserVisitor and uses
 * the visitor pattern supported by JavaCC to visit all
 * nodes of the AST.</p>
 * <p>This makes it easy to analyse each node type in a
 * specific way, analysing a node based on which type
 * of node we are currently visiting.</p>
 * @see ParserVisitor
 */
public class SymbolTableBuilder implements ParserVisitor {

    /**
     * Maximum number of semantic errors accepted before terminating
     * analysing due to too many errors
     */
    private static final int MAX_ERRORS = 10;

    /**
     * Current number os semantic errors while analysing
     */
    private int numErrors = 0;

    /**
     * AST root node
     */
    private final SimpleNode root;

    /**
     * Symbol Table
     */
    private final SymbolTable table;

    /**
     * Constructor
     * @param root AST root node
     */
    public SymbolTableBuilder(SimpleNode root) {
        this.root = root;
        this.table = new SymbolTable();
        this.numErrors = 0;
    }

    /**
     * Getter method for numErrors
     * @return numErrors
     */
    public int getNumErrors() {
        return numErrors;
    }

    /**
     * Initiator method for symbol table building
     * @return built symbol table
     */
    public SymbolTable buildSymbolTable() {
        // start visiting nodes from the root
        this.visit(root, null);
        // return complete symbol table
        return this.table;
    }

    /**
     * Print to terminal an error message
     * @param message error message
     * @param line line the error occurred
     * @param column column the error occurred
     */
    private void printError(String message, int line, int column) {
        numErrors++;
        System.out.println("SEMANTIC ERROR: " + message + " at line: " + line + ", column: " + column + ".");
        if (numErrors >= MAX_ERRORS) {
            System.out.println("TOO MANY SEMANTIC ERRORS: Stopping Analysis");
            System.exit(0);
        }
    }

    /**
     * Print to terminal a warning message
     * @param message error message
     * @param line line the error occurred
     * @param column column the error occurred
     */
    private void printWarning(String message, int line, int column) {
        System.out.println("SEMANTIC WARNING: " + message + " at line: " + line + ", column: " + column + ".");
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
        // check if this import is in sync with others
        // same identifier -> same return type, but different parameter type list
        try {
            this.table.checkEqualImports(importIdentifier);
        } catch (SemanticErrorException e) {
            this.printWarning(e.getMessage(), node.line, node.column);
        }
        return null;
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        // get class name
        String className = (String) node.jjtGetValue();
        this.table.setClassName(className);
        // get extended class name
        if (node.extId != null)
            this.table.setExtendedClassName(node.extId);
        // visit children (method declarations)
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTVarDeclaration node, Object data) {
        // parse variable declarations
        String variableIdentifier = (String) node.jjtGetValue();
        // get variable type
        String variableType = (String) node.jjtGetChild(0).jjtAccept(this, data);

        // Class attribute declaration
        if (data == null) {
            // put variable entry in symbol table
            try {
                this.table.addVariable(variableIdentifier, variableType);
            } catch (SemanticErrorException e) {
                this.printError(e.getMessage(), node.line, node.column);
            }
        } else { // Method variable declaration
            // get method name from data
            String methodIdentifier = (String) data;
            // put variable entry in symbol table
            try {
                this.table.addMethodVariable(methodIdentifier, variableIdentifier, variableType);
            } catch (SemanticErrorException e) {
                this.printError(e.getMessage(), node.line, node.column);
            }
        }

        // check for imported types
        if (!variableType.equals("int") && !variableType.equals("int[]") && !variableType.equals("boolean") && !variableType.equals(this.table.getClassName())) {
            // check import table
            HashMap<String, LinkedList<ImportDescriptor>> importTable = this.table.getImportDescriptors();
            if (!importTable.containsKey(variableType)) {
                this.printError("Type '" + variableType + "' not defined", node.line, node.column);
            }
        }

        return null;
    }

    @Override
    public Object visit(ASTRegularMethod node, Object data) {
        // get method  id node children
        String methodIdentifier = (String) node.jjtGetValue();
        // get method type
        String methodType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        // put method in method descriptors
        this.table.addMethod(methodIdentifier, methodType);
        // get Method Parameters and Variable Declarations
        // send method name for editing MethodDescriptor
        node.childrenAccept(this, methodIdentifier);
        // check if this method is in sync with others
        // same identifier -> same return type, but different parameter type list
        try {
            this.table.checkEqualMethods(methodIdentifier);
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
    public Object visit(ASTMethodParams node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTMethodParam node, Object data) {
        // get method name from data
        String methodIdentifier = (String) data;
        // parse parameter declarations
        String parameterName = (String) node.jjtGetValue();
        // get parameter type
        String paramType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        // put parameter entry in symbol table
        try {
            this.table.addMethodParameter(methodIdentifier, parameterName, paramType);
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
        String parameterIdentifier = (String) node.jjtGetValue();
        // add parameter name to main method
        try {
            this.table.addMethodParameter("main", parameterIdentifier, "String[]");
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
    public Object visit(ASTIfBlock node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTElseBlock node, Object data) {
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
