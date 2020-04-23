package SymbolTable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ImportDescriptor {

    private boolean isStatic;
    private boolean isMethod;
    private HashMap<String, LinkedList<String>> parameters;
    private String returnType;

    public ImportDescriptor(boolean isStatic, boolean isMethod) {
        this.isStatic = isStatic;
        this.isMethod = isMethod;
        if (isMethod) {
            this.parameters = new HashMap<>();
            this.returnType = "void";
        }
    }

    public void addParameter(String identifier, String dataType) {
        if (!this.isMethod)
            return;

        if (!this.parameters.containsKey(identifier))
            this.parameters.put(identifier, new LinkedList<>());

        this.parameters.get(identifier).add(dataType);
    }

    public void setReturnType(String dataType) {
        if (!this.isMethod)
            return;

        this.returnType = dataType;
    }

    public void addVariable(String identifier, String dataType) {
        if (!this.variableDescriptors.containsKey(identifier))
            this.variableDescriptors.put(identifier, new LinkedList<>());

        this.variableDescriptors.get(identifier).add(new VariableDescriptor(dataType));
    }

    public String dump(String prefix) {

        StringBuilder buf = new StringBuilder(prefix + "Descriptor " + "\n");
        if (this.isMethod) {
            // get return type
            buf.append(prefix).append("Return type ").append(this.returnType).append("\n");
            // get parameters
            buf.append(prefix).append("Parameters").append("\n");
            for (Map.Entry<String, LinkedList<String>> entry : this.parameters.entrySet()) {
                buf.append(prefix).append("\tParameter Name: ");
                // get parameter name
                buf.append(entry.getKey()).append(" : ");
                // get all parameters
                for (String dataType : entry.getValue())
                    buf.append(dataType).append(" ");
            }
        }

        return buf.toString();

    }
}
