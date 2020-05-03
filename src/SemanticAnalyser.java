import Exceptions.SemanticErrorException;
import SymbolTable.MethodDescriptor;
import SymbolTable.VariableDescriptor;
import SymbolTable.ImportDescriptor;
import SymbolTable.SymbolTable;

import java.util.LinkedList;

public class SemanticAnalyser implements ParserVisitor {

    /**
     * Maximum number of semantic errors accepted before terminating
     * analysing due to too many errors
     */
    private static final int MAX_ERRORS = 10;

    /**
     * Current number os semantic errors while analysing
     */
    private int numErrors = 0;

    private final SymbolTable table;

    public SemanticAnalyser(SymbolTable table, int numErrors) {
        this.table = table;
        this.numErrors = numErrors;
    }

    public boolean analise(SimpleNode root) {
        // visit root node
        this.visit(root, null);
        // return false in case of errors
        return numErrors == 0;
    }

    private void printError(String message, int line, int column) {
        this.numErrors++;
        System.out.println("SEMANTIC ERROR: " + message + " at line: " + line + ", column: " + column + ".");
        if (this.numErrors >= MAX_ERRORS) {
            System.out.println("TOO MANY SEMANTIC ERRORS: Stopping Analysis");
            System.exit(0);
        }
    }
    private void printWarning(String message, int line, int column) {
        System.out.println("SEMANTIC WARNING: " + message + " at line: " + line + ", column: " + column + ".");
    }

    private void setAsInitialised(ASTIdentifier node, Object data) {
        // get method descriptor
        MethodDescriptor method = (MethodDescriptor) data;
        // get variable id
        String variableIdentifier = (String) node.jjtGetValue();
        // lookup variable in method
        VariableDescriptor descriptor = null;
        try {
            descriptor = method.lookupVariable(variableIdentifier);
        } catch (SemanticErrorException ignored) {
            // lookup variable in class
            try {
                descriptor = table.lookupAttribute(variableIdentifier);
            } catch (SemanticErrorException e) {
                this.printError(e.getMessage(), node.line, node.column);
                return;
            }
        }
        // if identifier node has a child then its an array access
        if (node.jjtGetNumChildren() == 1) {
            // check if the whole array was initialised
            if (!descriptor.isInitialised()) {
                this.printError("Accessing element of uninitialised array '" + variableIdentifier + "'" ,node.line, node.column);
                return;
            }
        }
        // set variable as initialised
        descriptor.setInitialised(true);
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
        // get method id node children
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
        // for each child node (ASTMethodParam) get type
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
        // special case of MainMethod: main(String[] <parameter_name>);
        // get method id node children
        String methodIdentifier = "main";
        // get method parameters
        LinkedList<String> parameterList = new LinkedList<>();
        parameterList.add("String[]");
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
    public Object visit(ASTMainParams node, Object data) {
        // not necessary since argument is always of type 'String[]'
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
        // An Assignment has two child nodes
        // 1. visit the second child (right hand side) to check for uninitialised variables
        //    and get the type
        // 2. set the first child (left hand side) as initialised
        // 3. visit the first child to get the type

        // second child is the assigner (right side)
        String assignerType = (String) node.jjtGetChild(1).jjtAccept(this, data);
        // first child is the assignee (left side)
        SimpleNode assigneeNode = (SimpleNode) node.jjtGetChild(0);
        // left side must be a variable
        if (!(assigneeNode instanceof ASTIdentifier)) {
            this.printError("Left side of '=' is not a variable", node.line, node.column);
            return null;
        } else {
            this.setAsInitialised((ASTIdentifier) assigneeNode, data);
        }
        String assigneeType = (String) assigneeNode.jjtAccept(this, data);

        // check for past semantic errors
        if (assigneeType == null || assignerType == null)
            return null;
        // check if types are the same
        if (!assigneeType.equals(assignerType))
            this.printError("Assignment of different types '" + assigneeType + "' and '" + assignerType + "'", node.line, node.column);
        return null;
    }

    @Override
    public Object visit(ASTObjectCall node, Object data) {
        // an object call has two child nodes
        // first child is the object (caller)
        SimpleNode identifier = (SimpleNode) node.jjtGetChild(0);
        String objectType = (String) identifier.jjtGetValue();
        // second child is the method (callee)
        SimpleNode callMethod = (SimpleNode) node.jjtGetChild(1);
        String methodIdentifier = (String) callMethod.jjtGetValue();
        LinkedList<String> parameterList = (LinkedList<String>) callMethod.jjtAccept(this, data);

        // special import case
        // lookup 'objectType.methodName' static import
        try {
            String importIdentifier = objectType + "." + methodIdentifier;
            ImportDescriptor descriptor = this.table.lookupImport(importIdentifier, parameterList);
            if (descriptor.isStatic())
                return descriptor.getType();
        } catch (SemanticErrorException ignored) { }

        // normal case
        objectType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        // int[] case
        if (objectType.equals("int[]")) {
            if (methodIdentifier.equals("getLen"))
                return "int";
            else
                this.printError("'int[]' has no method " + methodIdentifier, identifier.line, identifier.column);
            return null;
        }
        // lookup 'objectType.methodName'
        // 1. objectType = class name
        if (objectType.contains(this.table.getClassName())) {
            // extended class method call
            if (!this.table.getExtendedClassName().equals(""))
                try {
                    String importIdentifier = this.table.getExtendedClassName() + "." + methodIdentifier;
                    ImportDescriptor descriptor = this.table.lookupImport(importIdentifier, parameterList);
                    return descriptor.getType();
                } catch (SemanticErrorException ignored) { }
            // this class method call
            try {
                MethodDescriptor descriptor = this.table.lookupMethod(methodIdentifier, parameterList);
                return descriptor.getType();
            } catch (SemanticErrorException e) {
                printError(e.getMessage(), callMethod.line, callMethod.column);
            }
        }
        // 2. objectType = imported class name
        else {
            try {
                ImportDescriptor descriptor = this.table.lookupImport(objectType + "." + methodIdentifier, parameterList);
                return descriptor.getType();
            } catch (SemanticErrorException e) {
                printError(e.getMessage(), callMethod.line, callMethod.column);
            }
        }
        return null;
    }

    @Override
    public Object visit(ASTIfElseBlock node, Object data) {

        String expressionType = (String) node.jjtGetChild(0).jjtAccept(this, data);

        // check for past semantic errors
        if (expressionType == null)
            return null;
        //verify data type
        if(!expressionType.equals("boolean"))
            printError("Conditional expression is not of 'boolean' type", node.line, node.column);
        // visit children statements
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTWhileBlock node, Object data) {

        String expressionType = (String) node.jjtGetChild(0).jjtAccept(this, data);

        //verify data type
        if(!expressionType.equals("boolean"))
            printError("Conditional expression is not of 'boolean' type", node.line, node.column);
        // visit children statements
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTand node, Object data) {
        SimpleNode leftChild = (SimpleNode) node.jjtGetChild(0);
        SimpleNode rightChild = (SimpleNode) node.jjtGetChild(1);

        String leftChildType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String rightChildType = (String) node.jjtGetChild(1).jjtAccept(this, data);

        // check for past semantic errors
        if (leftChildType == null || rightChildType == null)
            return null;
        // verify data type
        if (!leftChildType.equals("boolean"))
            printError("Operand " + leftChild.jjtGetValue() + " of '&&' is not of 'boolean' type", leftChild.line, leftChild.column);
        else if(!rightChildType.equals("boolean"))
            printError("Operand " + rightChild.jjtGetValue() + " of '&&' is not of 'boolean' type", rightChild.line, rightChild.column);

        // '&&' operator returns an int
        return "boolean";
    }

    @Override
    public Object visit(ASTlt node, Object data) {
        SimpleNode leftChild = (SimpleNode) node.jjtGetChild(0);
        SimpleNode rightChild = (SimpleNode) node.jjtGetChild(1);

        String leftChildType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String rightChildType = (String) node.jjtGetChild(1).jjtAccept(this, data);

        // check for past semantic errors
        if (leftChildType == null || rightChildType == null)
            return null;
        // verify data type
        if (!leftChildType.equals("int"))
            printError("Operand " + leftChild.jjtGetValue() + " of '<' is not of 'integer' type", leftChild.line, leftChild.column);
        else if(!rightChildType.equals("int"))
            printError("Operand " + rightChild.jjtGetValue() + " of '<' is not of 'integer' type", rightChild.line, rightChild.column);

        // '<' operator returns a boolean
        return "boolean";
    }

    @Override
    public Object visit(ASTsum node, Object data) {
        SimpleNode leftChild = (SimpleNode) node.jjtGetChild(0);
        SimpleNode rightChild = (SimpleNode) node.jjtGetChild(1);

        String leftChildType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String rightChildType = (String) node.jjtGetChild(1).jjtAccept(this, data);

        // check for past semantic errors
        if (leftChildType == null || rightChildType == null)
            return null;
        // verify data type
        if (!leftChildType.equals("int"))
            printError("Operand " + leftChild.jjtGetValue() + " of '+' is not of 'integer' type", leftChild.line,leftChild.column);
        else if(!rightChildType.equals("int"))
            printError("Operand " + rightChild.jjtGetValue() + " of '+' is not of 'integer' type", rightChild.line,rightChild.column);

        // '+' operator returns an int
        return "int";
    }

    @Override
    public Object visit(ASTsub node, Object data) {
        SimpleNode leftChild = (SimpleNode) node.jjtGetChild(0);
        SimpleNode rightChild = (SimpleNode) node.jjtGetChild(1);

        String leftChildType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String rightChildType = (String) node.jjtGetChild(1).jjtAccept(this, data);

        // check for past semantic errors
        if (leftChildType == null || rightChildType == null)
            return null;
        // verify data type
        if (!leftChildType.equals("int"))
            printError("Operand " + leftChild.jjtGetValue() + " of '-' is not of 'integer' type", leftChild.line, leftChild.column);
        else if(!rightChildType.equals("int"))
            printError("Operand " + rightChild.jjtGetValue() + " of '-' is not of 'integer' type", rightChild.line, rightChild.column);

        // '-' operator returns an int
        return "int";
    }

    @Override
    public Object visit(ASTmult node, Object data) {
        SimpleNode leftChild = (SimpleNode) node.jjtGetChild(0);
        SimpleNode rightChild = (SimpleNode) node.jjtGetChild(1);

        String leftChildType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String rightChildType = (String) node.jjtGetChild(1).jjtAccept(this, data);

        // check for past semantic errors
        if (leftChildType == null || rightChildType == null)
            return null;
        // verify data type
        if (!leftChildType.equals("int"))
            printError("Operand " + leftChild.jjtGetValue() + " of '*' is not of 'integer' type", leftChild.line, leftChild.column);
        else if(!rightChildType.equals("int"))
            printError("Operand " + rightChild.jjtGetValue() + " of '*' is not of 'integer' type", rightChild.line, rightChild.column);

        // '*' operator returns an int
        return "int";
    }

    @Override
    public Object visit(ASTdiv node, Object data) {
        SimpleNode leftChild = (SimpleNode) node.jjtGetChild(0);
        SimpleNode rightChild = (SimpleNode) node.jjtGetChild(1);

        String leftChildType = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String rightChildType = (String) node.jjtGetChild(1).jjtAccept(this, data);

        // check for past semantic errors
        if (leftChildType == null || rightChildType == null)
            return null;
        // verify data type
        if (!leftChildType.equals("int"))
            printError("Operand " + leftChild.jjtGetValue() + " of '/' is not of 'integer' type", leftChild.line,leftChild.column);
        else if(!rightChildType.equals("int"))
            printError("Operand " + rightChild.jjtGetValue() + " of '/' is not of 'integer' type", rightChild.line, rightChild.column);

        // '/' operator returns an int
        return "int";
    }

    @Override
    public Object visit(ASTIdentifier node, Object data) {
        // get method descriptor
        MethodDescriptor method = (MethodDescriptor) data;
        // get variable id
        String variableIdentifier = (String) node.jjtGetValue();
        // lookup variable in method
        VariableDescriptor descriptor = null;
        String variableType = null;
        try {
            descriptor = method.lookupVariable(variableIdentifier);
            variableType = descriptor.getType();
        } catch (SemanticErrorException ignored) {
            // lookup variable in class
            try {
                descriptor = table.lookupAttribute(variableIdentifier);
                variableType = descriptor.getType();
            } catch (SemanticErrorException e) {
                this.printError(e.getMessage(), node.line, node.column);
                return null;
            }
        }
        // if identifier node has a child then its an array access
        if (node.jjtGetNumChildren() == 1) {
            // check if identifier is of 'int[]' type
            if (!variableType.equals("int[]")) {
                this.printError("Variable '" + variableIdentifier + "' is not of array type", node.line, node.column);
                return null;
            }
            String accessType = (String) node.jjtGetChild(0).jjtAccept(this, data);
            if (!accessType.equals("int")) {
                this.printError("Invalid array access of type '" + accessType + "' must be of 'integer' value", node.line, node.column);
                return null;
            }
            // change variable type top 'int'
            variableType = "int";
        }
        // check if variable was initialized
        if (!descriptor.isInitialised())
            this.printWarning("Variable '" + variableIdentifier + "' may not have been initialized", node.line, node.column);
        // return identifier type
        return variableType;
    }

    @Override
    public Object visit(ASTGetLength node, Object data) {
        return new LinkedList<String>();
    }

    @Override
    public Object visit(ASTCallMethod node, Object data) {
        // get argument node children
        Node[] children = node.jjtGetChildren();
        // get method call arguments list
        // for each child node get type
        LinkedList<String> parameterList = new LinkedList<>();
        if (children != null)
            for (Node child : children) {
                String parameterType = (String) child.jjtAccept(this, data);
                parameterList.add(parameterType);
            }
        return parameterList;
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
        return this.table.getClassName();
    }

    @Override
    public Object visit(ASTnot node, Object data) {
        // '!' (not) is a unary operator, therefore it only has one child node
        // that child must be of type boolean
        SimpleNode child = (SimpleNode) node.jjtGetChild(0);
        String childType = (String) node.jjtGetChild(0).jjtAccept(this, data);

        // check for past semantic errors
        if (childType == null)
            return null;
        // verify data type
        if (!childType.equals("boolean"))
            printError("Operand " + child.jjtGetValue() + " of '!' is not of 'boolean' type", child.line, child.column );
        // '!' operator returns a boolean
        return "boolean";
    }

    @Override
    public Object visit(AST_new node, Object data) {
        // we can create a new Class Instance or array
        // get new creation name
        String identifier = (String) node.jjtGetValue();
        // in case of array we must see if child is of 'integer' type
        if (identifier.equals("int[]")) {
            // get child
            String childType = (String) node.jjtGetChild(0).jjtAccept(this, data);
            if (!childType.equals("int"))
                this.printError("Array creation size of type '" + childType + "', must be of type 'integer'", node.line, node.column);
        }

        return identifier;
    }
}
