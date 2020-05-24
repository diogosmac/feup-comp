package SymbolTable;

import Exceptions.SemanticErrorException;

import java.util.*;

public class MethodDescriptor extends Descriptor {
    /**
     * LinkedHashMap preserves insertion order
     * identifier -> < data type >
     */
    private LinkedHashMap<String, VariableDescriptor> parameters;

    /**
     * identifier -> < data type >
     */
    private HashMap<String, VariableDescriptor> variableDescriptors;

    public MethodDescriptor(String type) {
        this.type = type;
        this.parameters = new LinkedHashMap<>();
        this.variableDescriptors = new HashMap<>();
    }

    public VariableDescriptor lookupVariable(String variableIdentifier) throws SemanticErrorException {
        // find variable descriptor in defined variables
        if (this.variableDescriptors.containsKey(variableIdentifier))
            return this.variableDescriptors.get(variableIdentifier);
            // find variable descriptor in parameter list
        else if (this.parameters.containsKey(variableIdentifier))
            return this.parameters.get(variableIdentifier);
        else
            throw new SemanticErrorException("Variable '" + variableIdentifier + "' not defined");
    }

    public void addParameter(String identifier, String dataType) throws SemanticErrorException {
        if (!this.parameters.containsKey(identifier)) {
            VariableDescriptor parameter = new VariableDescriptor(dataType);
            parameter.setInitialised(true);
            this.parameters.put(identifier, parameter);
        }
        else
            throw new SemanticErrorException("Parameter name '" + identifier + "' already in use");
    }

    public void addVariable(String identifier, String dataType) throws SemanticErrorException {
        if (!this.variableDescriptors.containsKey(identifier))
            this.variableDescriptors.put(identifier, new VariableDescriptor(dataType));
        else
            throw new SemanticErrorException("Variable '" + identifier + "' already defined");
    }

    public void checkEqualMethod(HashMap<String, VariableDescriptor> parameters, String returnType) throws SemanticErrorException {
        // get parameters types list
        List<VariableDescriptor> thisParametersTypes = new ArrayList<VariableDescriptor>(this.parameters.values());
        List<VariableDescriptor> parametersTypes = new ArrayList<VariableDescriptor>(parameters.values());
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
        for (Map.Entry<String, VariableDescriptor> entry : this.parameters.entrySet()) {
            buf.append(prefix).append("  Parameter Name: ");
            // get parameter name
            buf.append(entry.getKey()).append("\n");
            // get all descriptor with the same name
            buf.append(entry.getValue().dump(prefix + "    ")).append("\n");
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

    public HashMap<String, VariableDescriptor> getParameters() {
        return this.parameters;
    }

    public LinkedList<String> getStringParameters() {
        LinkedList<VariableDescriptor> parameterTypes = new LinkedList<VariableDescriptor>(this.parameters.values());
        LinkedList<String> returnTypes = new LinkedList<>();
        for (VariableDescriptor parameter : parameterTypes) {
            returnTypes.add(parameter.getType());
        }
        return returnTypes;
    }

    public HashMap<String, VariableDescriptor> getVariableDescriptors() {
        return variableDescriptors;
    }
}
