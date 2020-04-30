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

    private void writeInstruction(String instruction) {
        if (this.outFileWriter != null) {
            try {
                this.outFileWriter.write( instruction +"\n");
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
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

            default:
                return  "a";
        }
    }

    private LinkedList<String> fetchMethodArgs(SimpleNode node, MethodDescriptor methodDescriptor) {
        LinkedList<String> args = new LinkedList<>();

        //Fetch the arguments
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            SimpleNode child = (SimpleNode) node.jjtGetChild(i);

            if (child instanceof ASTinteger)  {
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
                        try {
                            //look for the child in the class methods
                            MethodDescriptor variable = symbolTable.lookupMethod((String) child.jjtGetValue(),fetchMethodArgs(child,methodDescriptor));
                            args.push(variable.getType());
                        }
                        catch (SemanticErrorException e3) {
                            //Error
                            System.out.println("wtffff");
                            e3.printStackTrace();
                            System.exit(1);
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
            System.exit(1);
            //TODO maybe not exit immediately
        }

        //write the class name and super class
        writeInstruction(".class public " +  node.classId);

        //Write extending class
        if (node.extId != null) {
            //Write extendig class
        }
        else {
            writeInstruction(".super java/lang/Object");
        }

        return node.childrenAccept(this, null);
    }

    @Override
    public Object visit(ASTMainMethod node, Object data) {
        this.variableMap = new HashMap<>();
        this.currentVariableIndex = 1;

        //write the main method
        writeInstruction(".method static public main([Ljava/lang/String;)V");

        //get main method descriptor
        LinkedList<String> args = new LinkedList<>();
        args.push("String[]");
        try {
            MethodDescriptor descriptor = symbolTable.lookupMethod("main",args);

            node.childrenAccept(this, descriptor);
        }
        catch (SemanticErrorException e) {
            e.printStackTrace();
            System.exit(1);
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

            if (child instanceof ASTMethodParam) {
                ASTType param = (ASTType) child.jjtGetChild(0);
                args.push((String) param.jjtGetValue());
                continue;
            }

            break;
        }

        try {
            //Get method descriptor
            MethodDescriptor descriptor = symbolTable.lookupMethod((String) node.jjtGetValue(),args);

            writeInstruction(".method " + node.jjtGetValue() + "(" + convertParams(args) + ")" + convertType(descriptor.getType()));

            node.childrenAccept(this,descriptor);

            writeInstruction(".end method");
        }
        catch (SemanticErrorException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    @Override
    public Object visit(ASTVarDeclaration node, Object data) {
        if (node.jjtGetParent() instanceof ASTRegularMethod || node.jjtGetParent() instanceof ASTMainMethod){
            ///Alocate the variable in the variable map
            /*ArrayList<String> info = new ArrayList<>();
            info.add(String.valueOf(this.currentVariableIndex));
            SimpleNode child = (SimpleNode) node.jjtGetChild(0);
            info.add((String) child.jjtGetValue());

            this.variableMap.put((String) node.jjtGetValue(),info);
            this.currentVariableIndex++;*/
        }
        else {
            SimpleNode child = (SimpleNode) node.jjtGetChild(0);
            writeInstruction(".field private " + node.jjtGetValue() +  " " + convertType((String) child.jjtGetValue()));
        }

        return null;
    }

    /* TODO: SORRY LEO
    @Override
    public Object visit(ASTStatement node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTCallMethod node, Object data) {
        MethodDescriptor methodDescriptor = (MethodDescriptor) data;

        LinkedList<String> args = fetchMethodArgs(node,methodDescriptor);

        //try a class method
        try {
            MethodDescriptor descriptor = symbolTable.lookupMethod((String) node.jjtGetValue(), args);

            //TODO write the instruction
        }
        catch (SemanticErrorException semanticErrorException) {
            //Invalid call, might be an import method
            ASTStatement parent = (ASTStatement) node.jjtGetParent();

            try {
                ImportDescriptor descriptor = symbolTable.lookupImport(parent.id + "." + node.jjtGetValue(),args);

                if (descriptor.getStatic()) {
                    writeInstruction("invokestatic " + data + "/" + node.jjtGetValue() + "()" + convertType(descriptor.getType()));
                }

            }
            catch (SemanticErrorException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        return null;
    }
    */

    /* TODO: SORRY LEO
    @Override
    public Object visit(ASTAssignStatement node, Object data) {
        //Preform children operations
        /*node.childrenAccept(this,data);

        ArrayList<String> variableInfo = this.variableMap.get(data);


        if (variableInfo != null) {
            int index = Integer.parseInt(variableInfo.get(0));
            String type = convertInstructionType(variableInfo.get(1));

            //Assume the value to be assigned is on top of the stack
            writeInstruction(type + "store_" + index);
        }
        else { //Probably a field
            //TODO deal with a field or invalid variable
        }

        return null;
    }*/

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
    public Object visit(ASTReturn node, Object data) {
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
    public Object visit(ASTReturnType node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTAssignment node, Object data) {
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
    public Object visit(ASTIdentifier node, Object data) {
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
    public Object visit(ASTObjectCall node, Object data) {
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
