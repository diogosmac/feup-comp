package SymbolTable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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

    public String dump(String prefix) {
        StringBuilder buf = new StringBuilder();
        // get return type
        buf.append(prefix).append("Return type: ").append(this.returnType).append("\n");
        // get parameters
        buf.append(prefix).append("Parameters:").append("\n");
        for (Map.Entry<String, LinkedList<String>> entry : this.parameters.entrySet()) {
            buf.append(prefix).append("  Parameter Name: ");
            // get parameter name
            buf.append(entry.getKey()).append(" : ");
            // get all parameters
            for (String dataType : entry.getValue())
                buf.append(dataType).append(" ");
        }

        // get local variable descriptors
        buf.append("\n").append(prefix).append("Local Variables:").append("\n");
        for (Map.Entry<String, LinkedList<VariableDescriptor>> entry : this.variableDescriptors.entrySet()) {
            buf.append(prefix).append("  Variable Name: ");
            // get variable name
            buf.append(entry.getKey()).append("\n");
            // get all descriptor with the same name
            for (VariableDescriptor var : entry.getValue())
                buf.append(var.dump(prefix + "    ")).append("\n");
        }
        return buf.toString();
    }
}
