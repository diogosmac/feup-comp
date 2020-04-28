import Exceptions.SemanticErrorException;
import SymbolTable.SymbolTable;
import SymbolTable.ImportDescriptor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

public class CodeGenerator implements ParserVisitor{
    private final SymbolTable symbolTable;
    private FileWriter outFileWriter;
    private final SimpleNode root;

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
            case ("void"):
                return "V";

            default:
                return null;
        }
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
    public Object visit(ASTCallMethod node, Object data) {
        System.out.println(data);
        System.out.println(node.jjtGetNumChildren());

        //no parameters
        if (node.jjtGetNumChildren() == 0) {
            if (data != null) {
                try {
                    ImportDescriptor descriptor = symbolTable.lookupImport(data + "." + node.value,new LinkedList<>());

                    if (descriptor.getStatic()) {
                        writeInstruction("invokestatic " + data + "/" + node.value + "()" + convertType(descriptor.getType()));
                    }

                } catch (SemanticErrorException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    public Object visit(ASTMainMethod node, Object data) {
        //write the main method
        writeInstruction(".method static public main([Ljava/lang/String;)V");

        node.childrenAccept(this, null);

        writeInstruction("return");
        writeInstruction(".end method");

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
       return node.childrenAccept(this, node.id);
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
