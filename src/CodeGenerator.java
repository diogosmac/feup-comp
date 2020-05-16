import Exceptions.SemanticErrorException;
import SymbolTable.ImportDescriptor;
import SymbolTable.MethodDescriptor;
import SymbolTable.SymbolTable;
import SymbolTable.VariableDescriptor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class CodeGenerator implements ParserVisitor{
    private final SymbolTable symbolTable;
    private FileWriter outFileWriter;
    private final SimpleNode root;
    private StringBuilder instructionBuffer;

    private HashMap<String, ArrayList<String>> variableMap;
    private int currentVariableIndex;
    private int if_counter = 0;
    private int while_counter = 0;
    private int logic_operation_counter = 0;

    public CodeGenerator(SymbolTable table, SimpleNode root) {
        this.symbolTable = table;
        this.root = root;

        //Create the output directory if it does not exist
        new File("out/").mkdirs();

        //Create the .j file
        File outFile = new File("out/" + symbolTable.getClassName()+ ".j");
        try {
            if (!outFile.createNewFile()) {
                //clear the file
                PrintWriter pw = new PrintWriter(outFile.getPath());
                pw.close();
            }

            //create the writer
            this.outFileWriter = new FileWriter(outFile.getPath());

            //create the instruction Buffer
            this.instructionBuffer = new StringBuilder();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateCode() {
        this.visit(this.root, null);
    }

    private boolean addVariable(String variableId, String variableType) {
        if (!this.variableMap.containsKey(variableId)) {
            ArrayList<String> paramInfo = new ArrayList<>();
            paramInfo.add(String.valueOf(this.currentVariableIndex));
            paramInfo.add(variableType);
            this.variableMap.put(variableId,paramInfo);
            this.currentVariableIndex++;

            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Adds an instruction to the buffer so it can be dumped later
     * @param instruction
     */
    private void bufferInstruction(String instruction) {
        this.instructionBuffer.append("   ").append(instruction).append("\n");
    }

    /**
     * Writes an instruction directly in the output file
     * @param instruction
     */
    private void writeInstruction(String instruction) {
        try {
            this.outFileWriter.write(instruction + "\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dumps buffered instructions to the output file and clears the buffer (by creating a new one)
     */
    private void dumpInstructions() {
        try {
            this.outFileWriter.write(this.instructionBuffer.toString());
            this.instructionBuffer = new StringBuilder();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertType(String type) {
        switch (type) {
            case "void":
                return "V";

            case "int":
            case "boolean":
                return "I";

            case "int[]":
                return "[I";

            default:
                return null;
        }
    }

    private String convertParams(LinkedList<String> args) {
        String result = "";

        for (String arg : args) {
            result += convertType(arg);
        }

        return result;
    }

    private String convertInstructionType(String type) {
        switch (type) {
            case "boolean":
            case "int":
                return "i";

            case "void":
                return "";

            default:
                return  "a";
        }
    }

    private LinkedList<String> fetchMethodArgs(ASTCallMethod node, MethodDescriptor methodDescriptor) {
        LinkedList<String> args = new LinkedList<>();
        // create semantic analyzer
        SemanticAnalyser analyser = new SemanticAnalyser(this.symbolTable, 0);
        // use semantic analyser logic to fetch method call arguments
        args = (LinkedList<String>) analyser.visit(node, methodDescriptor);

        return args;
    }

    private int getLimitLocals(ASTMainMethod node) {
        // get method name and parameter types
        String methodName = "main";
        LinkedList<String> parameterTypes = new LinkedList<>();
        parameterTypes.add("String[]");
        int localLimit = 0;
        try {
            // lookup method descriptor
            MethodDescriptor methodDescriptor = this.symbolTable.lookupMethod(methodName, parameterTypes);
            // calculate local limit (parameters + local variables)
            localLimit = methodDescriptor.getParameters().size() + methodDescriptor.getVariableDescriptors().size();
        } catch (SemanticErrorException e) {
            e.printStackTrace();
        }
        return localLimit;
    }

    private int getLimitLocals(ASTRegularMethod node) {
        // get method name
        String methodName = (String) node.jjtGetValue();
        // get parameter types
        LinkedList<String> parameterTypes = new LinkedList<>();
        Node params = node.jjtGetChild(1);
        // check if method has parameters
        if (params instanceof ASTMethodParams) {
            // get parameters
            ASTMethodParams methodParams = (ASTMethodParams) params;
            Node[] children = methodParams.jjtGetChildren();
            // get parameter types
            for (Node child : children)  {
                ASTType typeNode = (ASTType) child.jjtGetChild(0);
                parameterTypes.add((String) typeNode.jjtGetValue());
            }
        }

        int localLimit = 0;
        try {
            // lookup method descriptor
            MethodDescriptor methodDescriptor = this.symbolTable.lookupMethod(methodName, parameterTypes);
            // calculate local limit (parameters + local variables + object (this))
            localLimit = methodDescriptor.getParameters().size() + methodDescriptor.getVariableDescriptors().size() + 1;
        } catch (SemanticErrorException e) {
            e.printStackTrace();
        }
        return localLimit;
    }

    private int getLimitStack(String methodName) {

        return 0;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        //The root node
        node.childrenAccept(this,null);

        try {
            this.outFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        //write the class name and super class
        writeInstruction(".class public " +  node.classId);


        //Write extending class
        String extendingClassName = symbolTable.getExtendedClassName();

        if (symbolTable.getExtendedClassName().isEmpty()) {
            extendingClassName = "java/lang/Object";
        }

        //write the superclass
        writeInstruction(".super " + extendingClassName);

        //Accept children
        node.childrenAccept(this, null);

        //Write the constructor
        writeInstruction(".method public <init>()V");
        bufferInstruction("aload_0");
        bufferInstruction("invokespecial " + extendingClassName + "/<init>()V");
        bufferInstruction("return");
        dumpInstructions();
        writeInstruction(".end method");

        return null;
    }

    @Override
    public Object visit(ASTMainMethod node, Object data) {
        this.variableMap = new HashMap<>();
        this.currentVariableIndex = 1;

        //write the main method
        writeInstruction(".method static public main([Ljava/lang/String;)V");
        writeInstruction(".limit locals " + this.getLimitLocals(node));
        writeInstruction(".limit stack 100");

        //get main method descriptor
        LinkedList<String> args = new LinkedList<>();
        args.push("String[]");
        try {
            MethodDescriptor descriptor = symbolTable.lookupMethod("main",args);

            node.childrenAccept(this, descriptor);
        }
        catch (SemanticErrorException e) {
            e.printStackTrace();
            System.exit(0);
        }

        bufferInstruction("return");
        dumpInstructions(); //Write all method instructions to the file
        writeInstruction(".end method");

        return null;
    }

    @Override
    public Object visit(ASTRegularMethod node, Object data) {
        this.variableMap = new HashMap<>();
        this.currentVariableIndex = 1;

        LinkedList<String> args = new LinkedList<>();

        //Get  arguments
        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            SimpleNode child = (SimpleNode) node.jjtGetChild(i);

            if (child instanceof ASTMethodParams) {
                for (int j = 0; j < child.jjtGetNumChildren(); j++) {
                    ASTMethodParam param = (ASTMethodParam) child.jjtGetChild(j);
                    ASTType paramType = (ASTType) param.jjtGetChild(0);

                    //push the param type to the args array
                    args.add((String) paramType.jjtGetValue());

                    //Add the parameter to the variable index table
                    this.addVariable((String) param.jjtGetValue(),(String) paramType.jjtGetValue());
                }

                continue;
            }

            break;
        }

        try {
            //Get method descriptor
            MethodDescriptor descriptor = symbolTable.lookupMethod((String) node.jjtGetValue(),args);

            writeInstruction(".method public " + node.jjtGetValue() + "(" + convertParams(args) + ")" + convertType(descriptor.getType()));
            writeInstruction(".limit locals " + this.getLimitLocals(node));
            writeInstruction(".limit stack 100");

            node.childrenAccept(this,descriptor);

            dumpInstructions(); //Write all children instructions to the file
            writeInstruction(".end method");
        }
        catch (SemanticErrorException e) {
            e.printStackTrace();
            System.exit(0);
        }

        return null;
    }

    @Override
    public Object visit(ASTObjectCall node, Object data) {
        // get belonging method descriptor
        MethodDescriptor belongingMethodDescriptor = (MethodDescriptor) data;
        // an object call has two child nodes
        // first child is the object (caller)
        SimpleNode identifier = (SimpleNode) node.jjtGetChild(0);
        String objectType = (String) identifier.jjtGetValue();
        // second child is the method (callee)
        // this can be a method or array.length
        SimpleNode method = (SimpleNode) node.jjtGetChild(1);
        String methodIdentifier = (String) method.jjtGetValue();
        LinkedList<String> args = new LinkedList<>();
        if (method instanceof ASTCallMethod)
            args = fetchMethodArgs((ASTCallMethod) method, belongingMethodDescriptor);

        // 1 - this.method()
        if (identifier instanceof AST_this) {
            // extended class method call
            if (!this.symbolTable.getExtendedClassName().equals("")) {
                try {
                    // fetch invoking method descriptor
                    String importIdentifier = this.symbolTable.getExtendedClassName() + "." + methodIdentifier;
                    ImportDescriptor invokingMethodDescriptor = this.symbolTable.lookupImport(importIdentifier, args);
                    // Load the this pointer into the stack
                    bufferInstruction("aload_0");
                    // load any arguments into the stack
                    node.childrenAccept(this, data);
                    // invoke method
                    bufferInstruction("invokevirtual " + symbolTable.getClassName() + "/" + methodIdentifier + "(" + convertParams(args) + ")" + convertType(invokingMethodDescriptor.getType()));
                } catch (SemanticErrorException ignored) { }
            }
            // this class method call
            else {
                try {
                    // fetch invoking method descriptor
                    MethodDescriptor invokingMethodDescriptor = symbolTable.lookupMethod(methodIdentifier, args);
                    // Load the this pointer into the stack
                    bufferInstruction("aload_0");
                    // load any arguments into the stack
                    node.childrenAccept(this, data);
                    // invoke method
                    bufferInstruction("invokevirtual " + symbolTable.getClassName() + "/" + methodIdentifier + "(" + convertParams(args) + ")" + convertType(invokingMethodDescriptor.getType()));
                } catch (SemanticErrorException e) {
                    //Error
                    e.printStackTrace();
                }
            }
        }
        // 2 - new Object().method();
        else if (identifier instanceof AST_new) {
            // We have a mandatory method call
            // if its equal to the class name then we have a class method call
            if (objectType.equals(this.symbolTable.getClassName())) {
                try {
                    // fetch invoking method descriptor
                    MethodDescriptor invokingMethodDescriptor = symbolTable.lookupMethod(objectType, args);
                    // load new and any arguments into the stack
                    node.childrenAccept(this,data);
                    // invoke method
                    bufferInstruction("invokevirtual " + objectType + "/" + methodIdentifier + "(" + convertParams(args) + ")" + convertType(invokingMethodDescriptor.getType()));
                } catch (SemanticErrorException e) {
                    e.printStackTrace();
                }
            }
            // if its equal to int[] then we have something like: new int[N].length
            else  if (objectType.equals("int[]")) {
                // load new and any arguments into the stack
                node.childrenAccept(this,data);
            }
            // else we have an import
            else {
                try {
                    // fetch invoking method descriptor
                    String importIdentifier = objectType + "." + methodIdentifier;
                    ImportDescriptor invokingMethodDescriptor = symbolTable.lookupImport(importIdentifier, args);
                    // load new and any arguments into the stack
                    node.childrenAccept(this,data);
                    // invoke method
                    bufferInstruction("invokevirtual " + objectType + "/" + methodIdentifier + "(" + convertParams(args) + ")" + convertType(invokingMethodDescriptor.getType()));
                } catch (SemanticErrorException e) {
                    e.printStackTrace();
                }
            }
        }
        // 3 - array.length
        else if (method instanceof ASTGetLength) {
            // push array to stack
            node.jjtGetChild(0).jjtAccept(this, data);
            // visit ASTGetLength for its instruction
            node.jjtGetChild(1).jjtAccept(this, data);
        }
        // 4 - object.method()
        else {
            // 4.1 - identifier is static import: static Object.method()
            try {
                String importIdentifier = objectType + "." + methodIdentifier;
                ImportDescriptor descriptor = symbolTable.lookupImport(importIdentifier, args);
                //Accept children except the identifier
                for (int i = 1; i < node.jjtGetNumChildren(); i++) {
                    node.jjtGetChild(i).jjtAccept(this,data);
                }
                //Assume the parameters are in the stack
                bufferInstruction("invokestatic " + identifier.jjtGetValue() + "/" + method.jjtGetValue() + "(" + convertParams(args) + ")" + convertType(descriptor.getType()));
                return null;
            }
            catch (SemanticErrorException ignored) {
            }
            // 4.2 - identifier is a local variable
            // get class name
            SemanticAnalyser analyser = new SemanticAnalyser(this.symbolTable, 0);
            objectType = (String) node.jjtGetChild(0).jjtAccept(analyser, data);
            // 4.2.1 - variable is a local class instance
            if (objectType.equals(symbolTable.getClassName())) {
                try {
                    // lookup method
                    MethodDescriptor descriptor = symbolTable.lookupMethod(methodIdentifier, args);
                    //load the arguments and identifier
                    node.childrenAccept(this, data);
                    // write instructions
                    bufferInstruction("invokevirtual " + objectType + "/" + method.jjtGetValue() + "(" + convertParams(args) + ")" + convertType(descriptor.getType()));
                } catch (SemanticErrorException ignored) {
                }
            }
            // 4.2.2 - variable is an imported class instance
            else {
                try {
                    // lookup imported class method
                    String importIdentifier = objectType + "." + methodIdentifier;
                    ImportDescriptor descriptor = symbolTable.lookupImport(importIdentifier, args);
                    //load the arguments and identifier
                    node.childrenAccept(this, data);
                    // write instructions
                    bufferInstruction("invokevirtual " + objectType + "/" + method.jjtGetValue() + "(" + convertParams(args) + ")" + convertType(descriptor.getType()));
                } catch (SemanticErrorException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    public Object visit(ASTCallMethod node, Object data) {
        node.childrenAccept(this,data);

        return null;
    }

    @Override
    public Object visit(ASTVarDeclaration node, Object data) {
        if (node.jjtGetParent() instanceof ASTRegularMethod || node.jjtGetParent() instanceof ASTMainMethod){
            //Alocate the variable in the variable map
            SimpleNode child = (SimpleNode) node.jjtGetChild(0);
            this.addVariable((String) node.jjtGetValue(),(String) child.jjtGetValue());
        }
        else {
            SimpleNode child = (SimpleNode) node.jjtGetChild(0);
            writeInstruction(".field private " + node.jjtGetValue() +  " " + convertType((String) child.jjtGetValue()));
        }

        return null;
    }

    @Override
    public Object visit(ASTAssignment node, Object data) {
        SimpleNode assignee = (SimpleNode) node.jjtGetChild(0);

        //Get the variable identifier
        String identifier = (String) assignee.jjtGetValue();

        //Get variable identifier info
        ArrayList<String> variableInfo = this.variableMap.get(identifier);

        if (variableInfo != null) {
            //Local variable assignment
            if (assignee.jjtGetNumChildren() == 1) { //array
                node.jjtGetChild(0).jjtAccept(this, data); //get the array reference and position
                for (int i = 1; i < node.jjtGetNumChildren(); i++) { //Get the value to store in the array
                    node.jjtGetChild(i).jjtAccept(this,data);
                }
                // update type to integer address
                bufferInstruction("iastore");
            }
            else {
                //visit children but not the identifier
                for (int i = 1; i < node.jjtGetNumChildren(); i++) { //Get the value to store in the array
                    node.jjtGetChild(i).jjtAccept(this,data);
                }

                int index = Integer.parseInt(variableInfo.get(0)); //The index in the variable table
                String type = convertInstructionType(variableInfo.get(1)); //the type of the variable

                //assign the variable assuming the value to be assigned is on top of the stack
                if (index > 3) {
                    bufferInstruction(type + "store " + index);
                }
                else {
                    bufferInstruction(type +  "store_" + index);
                }
            }
        }
        else {
            try {
                VariableDescriptor fieldDescriptor = symbolTable.lookupAttribute(identifier); //the identifier is a field

                //load the this pointer
                bufferInstruction("aload_0");

                if (assignee.jjtGetNumChildren() == 1) { //array
                    node.jjtGetChild(0).jjtAccept(this, data); //get the array reference and position
                    for (int i = 1; i < node.jjtGetNumChildren(); i++) { //Get the value to store in the array
                        node.jjtGetChild(i).jjtAccept(this,data);
                    }

                    //store in the array
                    bufferInstruction("iastore");
                }
                else {
                    for (int i = 1; i < node.jjtGetNumChildren(); i++) { //Get the value to store in field
                        node.jjtGetChild(i).jjtAccept(this,data);
                    }

                    //store in the field
                    bufferInstruction("putfield "+ symbolTable.getClassName() + "/" + identifier + " " + convertType(fieldDescriptor.getType()));
                }
            }
            catch (SemanticErrorException e) {
                System.err.println("Unknown identifier " + identifier);
                e.printStackTrace();
            }
        }


        return null;
    }

    @Override
    public Object visit(ASTIdentifier node, Object data) {
        //Check if the identifier exists
        String id = (String) node.jjtGetValue();
        if (this.variableMap.containsKey(id)) {
            //get the identifier information
            ArrayList<String> variableInfo = this.variableMap.get(id);
            int index = Integer.parseInt(variableInfo.get(0)); //The index in the variable table
            String type = convertInstructionType(variableInfo.get(1)); //the type of the variable
            // load variable
            if (index > 3) {
                bufferInstruction(type + "load " + index);
            }
            else {
                bufferInstruction(type + "load_" + index);
            }
        }
        else {
            //Check fields
            try {
                VariableDescriptor fieldDescriptor = symbolTable.lookupAttribute(id);

                bufferInstruction("aload_0"); //load the this pointer
                bufferInstruction("getfield " + symbolTable.getClassName() + "/" + id + " " + convertType(fieldDescriptor.getType()));
            }
            catch (SemanticErrorException e) {
                System.err.println("Unknown identifier " + id);
                e.printStackTrace();
            }
        }

        if (node.jjtGetNumChildren() == 1) {
            // visit child
            node.jjtGetChild(0).jjtAccept(this, data);
            // Load int from array only if it is not being assigned (lhs)
            // get node parent, if parent is of type ASTAssignment check if
            // node is the first child
            SimpleNode parent = (SimpleNode) node.jjtGetParent();
            SimpleNode firstChild = (SimpleNode) parent.jjtGetChild(0);
            if (!(parent instanceof ASTAssignment && firstChild.equals(node))) {
                bufferInstruction("iaload");
            }
        }

        return null;
    }

    @Override
    public Object visit(ASTinteger node, Object data) {
        int number = Integer.parseInt((String) node.jjtGetValue());

        if (number > 200) {
            bufferInstruction("ldc_w " + number);
        }
        else {
            bufferInstruction("bipush " + number);
        }

        return null;
    }

    @Override
    public Object visit(AST_new node, Object data) {
        // when using the 'new' keyword, we can be creating an
        // object instance or an array
        String type = (String) node.jjtGetValue();
        // new int[n]
        if (type.equals("int[]")) {
            // visit child for size of array
            node.childrenAccept(this, data);
            // create array instance
            bufferInstruction("newarray int");
        }
        // new Object()
        else {
            bufferInstruction("new " + type);
            bufferInstruction("dup");
            bufferInstruction("invokespecial " + type + "/<init>()V");
        }

        return null;
    }

    @Override
    public Object visit(ASTsum node, Object data) {
        //Accept children
        node.childrenAccept(this,data);

        bufferInstruction("iadd");

        return null;
    }

    @Override
    public Object visit(ASTsub node, Object data) {
        //Accept children
        node.childrenAccept(this,data);

        bufferInstruction("isub");

        return null;
    }

    @Override
    public Object visit(ASTmult node, Object data) {
        //Accept children
        node.childrenAccept(this,data);

        bufferInstruction("imul");

        return null;
    }

    @Override
    public Object visit(ASTdiv node, Object data) {
        //Accept children
        node.childrenAccept(this,data);

        bufferInstruction("idiv");

        return null;
    }

    @Override
    public Object visit(ASTReturn node, Object data) {
        //visit the child
        node.childrenAccept(this,data);

        String returnType = ((MethodDescriptor) data).getType();

        bufferInstruction(convertInstructionType(returnType) + "return");
        return null;
    }


    @Override
    public Object visit(ASTReturnType node, Object data) {
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
    public Object visit(ASTMethodParams node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMethodParam node, Object data) {
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
    public Object visit(ASTIfElseBlock node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTIfBlock node, Object data) {

        bufferInstruction("ifeq else_" + if_counter);

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            node.jjtGetChild(i).jjtAccept(this, data);
        }

       bufferInstruction("goto endif_" + if_counter);

        return null;

    }

    @Override
    public Object visit(ASTElseBlock node, Object data) {

        bufferInstruction("else_" + if_counter + ":");

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            node.jjtGetChild(i).jjtAccept(this, data);
        }

        bufferInstruction("endif_" + if_counter + ":");

        if_counter++;
        return null;

    }

    @Override
    public Object visit(ASTWhileBlock node, Object data) {

        bufferInstruction("while_" + while_counter + ":");
        node.jjtGetChild(0).jjtAccept(this, data);      // accept condition
        bufferInstruction("ifeq end_while_" + while_counter);
        for (int i = 1; i < node.jjtGetNumChildren(); i++) {    // accept statements
            node.jjtGetChild(i).jjtAccept(this, data);
        }
        bufferInstruction("goto while_" + while_counter);
        bufferInstruction("end_while_" + while_counter + ":");

        while_counter++;
        return null;

    }

    @Override
    public Object visit(ASTand node, Object data) {

        String trueLabel = "true_and_" + logic_operation_counter;
        String falseLabel = "false_and_" + logic_operation_counter;
        logic_operation_counter++;

        // visit first child children
        node.jjtGetChild(0).jjtAccept(this, data);
        bufferInstruction("ifeq " + falseLabel);
        // visit second child children
        node.jjtGetChild(1).jjtAccept(this, data);
        bufferInstruction("ifeq " + falseLabel);
        bufferInstruction("iconst_1");
        bufferInstruction("goto " + trueLabel);
        // compare
        bufferInstruction(falseLabel + ":");
        bufferInstruction("iconst_0");
        bufferInstruction(trueLabel + ":");
        return null;
    }

    @Override
    public Object visit(ASTlt node, Object data) {

        String trueLabel = "true_lt_" + logic_operation_counter;
        String falseLabel = "false_lt_" + logic_operation_counter;
        logic_operation_counter++;

        // visit 2 children
        node.childrenAccept(this, data);

        // compare
        bufferInstruction("if_icmplt " + trueLabel);
        bufferInstruction("iconst_0");
        bufferInstruction("goto " + falseLabel);
        bufferInstruction(trueLabel + ":");
        bufferInstruction("iconst_1");
        bufferInstruction(falseLabel + ":");

        return null;
    }

    @Override
    public Object visit(ASTGetLength node, Object data) {
        bufferInstruction("arraylength");
        return null;
    }

    @Override
    public Object visit(ASTbool node, Object data) {
        // get boolean value
        String booleanValue = node.jjtGetValue().toString();
        if (booleanValue.equals("true"))
            bufferInstruction("iconst_1");
        else
            bufferInstruction("iconst_0");
        return null;
    }

    @Override
    public Object visit(AST_this node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTnot node, Object data) {

        String trueLabel = "not_eq_" + logic_operation_counter;
        String falseLabel = "eq_" + logic_operation_counter;
        logic_operation_counter++;

        // visit child
        node.childrenAccept(this, data);
        // compare
        bufferInstruction("ifne " + trueLabel);
        bufferInstruction("iconst_1");
        bufferInstruction("goto " + falseLabel);
        bufferInstruction(trueLabel + ":");
        bufferInstruction("iconst_0");
        bufferInstruction(falseLabel + ":");

        return null;

    }

}
