package SymbolTable;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Symbol Table in JMM
 */
public class SymbolTable {

    /**
     * identifier -> <data type>
     */
    private HashMap<String, LinkedList<VariableDescriptor>> variableDescriptors;
    /**
     * identifier -> < return data type, list of params, list of local variables >
     */
    private HashMap<String, LinkedList<MethodDescriptor>> methodDescriptors;

    private SymbolTable() {
        this.methodDescriptors = new HashMap<>();
        this.variableDescriptors = new HashMap<>();
    }

    public void addVariable(String identifier, String dataType) {
        if (!this.variableDescriptors.containsKey(identifier))
            this.variableDescriptors.put(identifier, new LinkedList<>());

        this.variableDescriptors.get(identifier).add(new VariableDescriptor(dataType));
    }

    public void addMethod(String identifier, String dataType) {
        if (!this.methodDescriptors.containsKey(identifier))
            this.methodDescriptors.put(identifier, new LinkedList<>());

        this.methodDescriptors.get(identifier).add(new MethodDescriptor(dataType));
    }
}
