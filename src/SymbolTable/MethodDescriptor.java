package SymbolTable;

import java.util.HashMap;
import java.util.LinkedList;

public class MethodDescriptor {
    private String returnType;
    private HashMap<String, LinkedList<String>> parameters;
    private HashMap<String, LinkedList<VariableDescriptor>> variableDescriptors;

    public MethodDescriptor(String returnType) {
        this.returnType = returnType;
        this.parameters = new HashMap<>();
        this.variableDescriptors = new HashMap<>();
    }

    public void addParameter(String identifier, String dataType) {
        if (!this.parameters.containsKey(identifier))
            this.parameters.put(identifier, new LinkedList<>());

        this.parameters.get(identifier).add(dataType);
    }

    public void addVariable(String identifier, String dataType) {
        if (!this.variableDescriptors.containsKey(identifier))
            this.variableDescriptors.put(identifier, new LinkedList<>());

        this.variableDescriptors.get(identifier).add(new VariableDescriptor(dataType));
    }
}
