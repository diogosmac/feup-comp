package SymbolTable;

import java.util.HashMap;
import java.util.LinkedList;

public class MethodDescriptor {
    private String returnType;
    private HashMap<String, String> parameters;
    private HashMap<String, LinkedList<VariableDescriptor>> variableDescriptors;

    public MethodDescriptor(String returnType) {
        this.returnType = returnType;
        this.parameters = new HashMap<>();
        this.variableDescriptors = new HashMap<>();
    }
}
