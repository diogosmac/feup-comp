package SymbolTable;

import Exceptions.SemanticErrorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodDescriptor extends Descriptor {
    /**
     * identifier -> < data type >
     */
    private HashMap<String, String> parameters;

    /**
     * identifier -> < data type >
     */
    private HashMap<String, VariableDescriptor> variableDescriptors;

    public MethodDescriptor(String type) {
        this.type = type;
        this.parameters = new HashMap<>();
        this.variableDescriptors = new HashMap<>();
    }

    public VariableDescriptor lookupVariable(String variableIdentifier) throws SemanticErrorException {
        if (!this.variableDescriptors.containsKey(variableIdentifier))
            throw new SemanticErrorException("Variable '" + variableIdentifier + "' not defined");
        else
            return this.variableDescriptors.get(variableIdentifier);
    }

    public void addParameter(String identifier, String dataType) throws SemanticErrorException {
        if (!this.parameters.containsKey(identifier))
            this.parameters.put(identifier, dataType);
        else
            throw new SemanticErrorException("Parameter name" + identifier + " already in use");
    }

    public void addVariable(String identifier, String dataType) throws SemanticErrorException {
        if (!this.variableDescriptors.containsKey(identifier))
            this.variableDescriptors.put(identifier, new VariableDescriptor(dataType));
        else
            throw new SemanticErrorException("Variable " + identifier + " already defined");
    }

    public void checkEqualMethod(HashMap<String, String> parameters, String returnType) throws SemanticErrorException {
        // get parameters types list
        List<String> thisParametersTypes = new ArrayList<String>(this.parameters.values());
        List<String> parametersTypes = new ArrayList<String>(parameters.values());
        // check if both parameter lists are the same
        if (thisParametersTypes.equals(parametersTypes))
            throw new SemanticErrorException("Parameter type list already defined");
        // check return type
        if (!this.getType().equals(returnType))
            throw new SemanticErrorException("Return type '" + returnType + "' different from other methods with the same identifier");
    }

    public String dump(String prefix) {
        StringBuilder buf = new StringBuilder();
        // get return type
        buf.append(prefix).append("Return type: ").append(this.type).append("\n");
        // get parameters
        buf.append(prefix).append("Parameters:").append("\n");
        for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
            buf.append(prefix).append("  Parameter Name: ");
            // get parameter name
            buf.append(entry.getKey()).append(" : ");
            // get all parameters
            buf.append(entry.getValue());
            buf.append("\n");
        }

        // get local variable descriptors
        buf.append(prefix).append("Local Variables:").append("\n");
        for (Map.Entry<String, VariableDescriptor> entry : this.variableDescriptors.entrySet()) {
            buf.append(prefix).append("  Variable Name: ");
            // get variable name
            buf.append(entry.getKey()).append("\n");
            // get all descriptor with the same name
            buf.append(entry.getValue().dump(prefix + "    ")).append("\n");
        }
        return buf.toString();
    }

    public HashMap<String, String> getParameters() {
        return this.parameters;
    }
}
