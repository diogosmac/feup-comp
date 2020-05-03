import Exceptions.SemanticErrorException;
import SymbolTable.MethodDescriptor;
import SymbolTable.ImportDescriptor;
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

    private HashMap<String, ArrayList<String>> variableMap;
    private int currentVariableIndex;

    public CodeGenerator(SymbolTable table, SimpleNode root) {
        this.symbolTable = table;
        this.root = root;

        //Create the output directory if it does not exist
        new File("out/").mkdirs();
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

    private void writeInstruction(String instruction) {
        if (this.outFileWriter != null) {
            try {
                this.outFileWriter.write( instruction +"\n");
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    private String convertType(String type) {
        switch (type) {
            case "void":
                return "V";

            case "int":
                return "I";

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
            case "int":
                return "i";

            case "void":
                return "";

            default:
                return  "a";
        }
    }

    private LinkedList<String> fetchMethodArgs(SimpleNode node, MethodDescriptor methodDescriptor) {
        LinkedList<String> args = new LinkedList<>();

        //Fetch the arguments
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            SimpleNode child = (SimpleNode) node.jjtGetChild(i);

            if (child instanceof ASTinteger || child instanceof ASTsum || child instanceof ASTsub ||
                    child instanceof ASTmult || child instanceof ASTdiv)  {
                //The child is an int literal
                args.push("int");
            }
            else {
                try {
                    //look for the child in the method variables;
                    VariableDescriptor variable = methodDescriptor.lookupVariable((String) child.jjtGetValue());
                    args.push(variable.getType());
                }
                catch (SemanticErrorException e1) {
                    try {
                        //look for the child in the class attributes
                        VariableDescriptor variable = symbolTable.lookupAttribute((String) child.jjtGetValue());
                        args.push(variable.getType());
                    }
                    catch (SemanticErrorException e2) {
                        //test for object call instance
                        if (child instanceof ASTObjectCall) {
                            SimpleNode identifier = (SimpleNode) child.jjtGetChild(0);
                            SimpleNode childMethod = (SimpleNode) child.jjtGetChild(1);

                            try {
                                //look for the child method in the class methods
                                MethodDescriptor variable = symbolTable.lookupMethod((String) childMethod.jjtGetValue(),fetchMethodArgs(childMethod,methodDescriptor));
                                args.push(variable.getType());
                            }
                            catch (SemanticErrorException e3) {
                                try {
                                    //look for the child method in the imports
                                    ImportDescriptor variable = symbolTable.lookupImport((String) childMethod.jjtGetValue(),fetchMethodArgs(childMethod,methodDescriptor));
                                    args.push(variable.getType());
                                }
                                catch (SemanticErrorException e4) {
                                    e4.printStackTrace();
                                    System.out.println("Could not find " + child.jjtGetValue());
                                    e3.printStackTrace();
                                    //System.exit(1);
                                }
                            }
                        }
                        else {
                            //todo remove this
                            System.out.println("WTFFFFFF");
                        }
                    }
                }
            }
        }

        return args;
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
        //Create the .j file
        File outFile = new File("out/" + node.classId + ".j");
        try {
            if (outFile.createNewFile()) {
                this.outFileWriter = new FileWriter(outFile.getPath());
            }
            else {
                //clear the file
                PrintWriter pw = new PrintWriter(outFile.getPath());
                pw.close();

                //create the writer
                this.outFileWriter = new FileWriter(outFile.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
            //TODO maybe not exit immediately
        }

        //write the class name and super class
        writeInstruction(".class public " +  node.classId);


        //Write extending class
        String extendingClassName = symbolTable.getExtendedClassName();

        if (symbolTable.getExtendedClassName().isEmpty()) {
            extendingClassName = "java/lang/Object";
        }

        //write the superclass
        writeInstruction(".super " + extendingClassName);

        //Write the constructor
        writeInstruction(".method public <init>()V");
        writeInstruction("aload_0");
        writeInstruction("invokespecial " + extendingClassName + "/<init>()V");
        writeInstruction("return");
        writeInstruction(".end method");

        //Accept children
        return node.childrenAccept(this, null);
    }

    @Override
    public Object visit(ASTMainMethod node, Object data) {
        this.variableMap = new HashMap<>();
        this.currentVariableIndex = 1;

        //write the main method
        writeInstruction(".method static public main([Ljava/lang/String;)V");
        writeInstruction(".limit locals 100"); //Todo calculate actual locals
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

        writeInstruction("return");
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
                    args.push((String) paramType.jjtGetValue());

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
            writeInstruction(".limit locals 100"); //Todo calculate actual locals
            writeInstruction(".limit stack 100");

            node.childrenAccept(this,descriptor);

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
        MethodDescriptor belongingMethodDescriptor = (MethodDescriptor) data;

        if (node.jjtGetChild(0) instanceof AST_this) {
            //We have a mandatory method call
            //Get method name and arguments
            ASTCallMethod method = (ASTCallMethod) node.jjtGetChild(1);
            LinkedList<String> args = fetchMethodArgs(method,belongingMethodDescriptor);

            try {
                MethodDescriptor invokingMethodDescriptor = symbolTable.lookupMethod((String) method.jjtGetValue(),args);

                //todo write invoke instruction
            } catch (SemanticErrorException e) {
                //Error
                e.printStackTrace();
            }
        }
        else if (node.jjtGetChild(0) instanceof AST_new) {
            //We have a mandatory method call

            //todo get class name and compare
            //if its equal to the class name then we have a class method call
            //else we have an import
            //In the case the import lookup goes wrong -> error
        }
        else if (node.jjtGetChild(1) instanceof  ASTGetLength) {
            //todo deal with this
        }
        else {
            //Look for the identifier
            String identifier = (String) ((ASTIdentifier) node.jjtGetChild(0)).jjtGetValue();
            ASTCallMethod method = (ASTCallMethod) node.jjtGetChild(1);

            LinkedList<String> args = fetchMethodArgs(method,belongingMethodDescriptor);


            try {
                //Test if the Id is a method variable
                belongingMethodDescriptor.lookupVariable(identifier);

                //todo get invoking method descriptor
                MethodDescriptor descriptor = symbolTable.lookupMethod((String) method.jjtGetValue(),args);

                node.childrenAccept(this,data);

                writeInstruction("invokevirtual " + symbolTable.getClassName() + "/" + method.jjtGetValue() + "(" + convertParams(args) + ")" + convertType(descriptor.getType()));

                //todo invoke
            }
            catch (SemanticErrorException e1) {
                try {
                    //Test if the id is a class field
                    symbolTable.lookupAttribute(identifier);
                    //todo invoke
                }
                catch (SemanticErrorException e2) {
                    try {
                        ImportDescriptor descriptor = symbolTable.lookupImport(identifier + "." + method.jjtGetValue(),args);

                        //Accept children exept the identifier
                        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
                            node.jjtGetChild(i).jjtAccept(this,data);
                        }

                        //Assume the parameters are in the stack

                        if (descriptor.getStatic()) {
                            writeInstruction("invokestatic " + identifier + "/" + method.jjtGetValue() + "(" + convertParams(args) + ")" + convertType(descriptor.getType()));
                        }
                    }
                    catch (SemanticErrorException e3) {
                        System.err.println("Invalid identifier " + identifier);
                        e3.printStackTrace();
                    }
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
        //preform children operations (do not visit the identifier
        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            node.jjtGetChild(i).jjtAccept(this,data);
        }

        //Get the variable identifier
        String identifier = (String) ((SimpleNode) node.jjtGetChild(0)).jjtGetValue();

        //Get variable identifier info
        ArrayList<String> variableInfo = this.variableMap.get(identifier);

        if (variableInfo != null) {
            int index = Integer.parseInt(variableInfo.get(0)); //The index in the variable table
            String type = convertInstructionType(variableInfo.get(1)); //the type of the variable

            //assign the variable assuming the value to be assigned is on top of the stack
            if (index > 3) {
                writeInstruction(type + "store " + index);
            }
            else {
                writeInstruction(type +  "store_" + index);
            }
        }
        else {
            //todo check in the class fields
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

            if (index > 3) {
                writeInstruction(type + "load " + index);
            }
            else {
                writeInstruction(type + "load_" + index);
            }
        }
        else {
            //Check fields
            System.err.println("Unknown identifier " + id);
        }

        return null;
    }

    @Override
    public Object visit(ASTinteger node, Object data) {
        int number = Integer.parseInt((String) node.jjtGetValue());

        if (number > 200) {
            writeInstruction("ldc_w " + number);
        }
        else {
            writeInstruction("bipush " + number);
        }

        return null;
    }

    @Override
    public Object visit(AST_new node, Object data) {
        writeInstruction("new " +  node.jjtGetValue());
        writeInstruction("dup");
        writeInstruction("invokespecial " +node.jjtGetValue()+"/<init>()V");

        return null;
    }

    @Override
    public Object visit(ASTsum node, Object data) {
        //Accept children
        node.childrenAccept(this,data);

        writeInstruction("iadd");

        return null;
    }

    @Override
    public Object visit(ASTsub node, Object data) {
        //Accept children
        node.childrenAccept(this,data);

        writeInstruction("isub");

        return null;
    }

    @Override
    public Object visit(ASTmult node, Object data) {
        //Accept children
        node.childrenAccept(this,data);

        writeInstruction("imul");

        return null;
    }

    @Override
    public Object visit(ASTdiv node, Object data) {
        //Accept children
        node.childrenAccept(this,data);

        writeInstruction("idiv");

        return null;
    }

    @Override
    public Object visit(ASTReturn node, Object data) {
        //visit the child
        node.childrenAccept(this,data);

        //get the type of the return
        SimpleNode child = (SimpleNode) node.jjtGetChild(0);

        if (child instanceof ASTinteger) {
            writeInstruction("ireturn");
        }
        else {
            //only return variables for now
            ArrayList<String> varInfo = this.variableMap.get(child.jjtGetValue());

            if (varInfo != null) {
                String type = convertInstructionType(varInfo.get(1));

                writeInstruction(type + "return");
            }

            //TODO generate other types of returns
        }

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
    public Object visit(ASTGetLength node, Object data) {
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
}
