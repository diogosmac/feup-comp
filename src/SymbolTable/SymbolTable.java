package SymbolTable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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

    public SymbolTable() {
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

    public void addMethodParameter(String methodIdentifier, String parameterIdentifier, String dataType) {
        this.methodDescriptors.get(methodIdentifier).getLast().addParameter(parameterIdentifier, dataType);
    }

    public void addMethodVariable(String methodIdentifier, String variableIdentifier, String dataType) {
        this.methodDescriptors.get(methodIdentifier).getLast().addVariable(variableIdentifier, dataType);
    }

    public void dump() {
        // Variable Descriptors
        System.out.println("Variable Descriptors:");
        for (Map.Entry<String, LinkedList<VariableDescriptor>> entry : this.variableDescriptors.entrySet()) {
            StringBuilder buf = new StringBuilder("  Variable name: ");
            // get variable name
            buf.append(entry.getKey()).append("\n");
            // get all descriptor with the same name
            for (VariableDescriptor var : entry.getValue())
                buf.append(var.dump("    "));
            System.out.println(buf.toString());
        }

        // Method Descriptors
        System.out.println("\nMethod Descriptors:");
        for (Map.Entry<String, LinkedList<MethodDescriptor>> entry : this.methodDescriptors.entrySet()) {
            StringBuilder buf = new StringBuilder("  Method name: ");
            // get method name
            buf.append(entry.getKey()).append("\n");
            // get all descriptor with the same name
            for (MethodDescriptor method : entry.getValue())
                buf.append(method.dump("    ")).append("\n");
            System.out.println(buf.toString());
        }
    }
}
