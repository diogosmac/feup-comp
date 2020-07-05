package SymbolTable;

import Exceptions.SemanticErrorException;

import java.util.*;

/**
 * <h1>Method Descriptor</h1>
 * <p>A method descriptor has more information regarding
 * a method statement and method body. This class uses
 * Maps to map a variable name to its descriptor</p>
 * <p>It also makes use of a LinkedHashMap in order
 * to maintain the order of parameters for comparison</p>
 * @see VariableDescriptor
 * @see LinkedHashMap
 * @see HashMap
 */
public class MethodDescriptor extends Descriptor {
    /**
     * Parameter list
     * LinkedHashMap preserves insertion order
     * identifier -> < data type >
     */
    private LinkedHashMap<String, VariableDescriptor> parameters;

    /**
     * Variable list
     * identifier -> < data type >
     */
    private HashMap<String, VariableDescriptor> variableDescriptors;

    /**
     * Constructor
     * @param type method's return type
     */
    public MethodDescriptor(String type) {
        this.type = type;
        this.parameters = new LinkedHashMap<>();
        this.variableDescriptors = new HashMap<>();
    }

    /**
     * Searches in its variable list for the VariableDescriptor
     * mapped to the identifier string passed as argument.
     * A SemanticErrorException is thrown if not found.
     * @param variableIdentifier variable name
     * @return variable's descriptor if found
     * @throws SemanticErrorException
     */
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

    /**
     * Adds a parameter to the parameter list. A SemanticErrorException
     * is thrown if the identifier is already being used.
     * @param identifier parameter name
     * @param dataType parameter type
     * @throws SemanticErrorException
     */
    public void addParameter(String identifier, String dataType) throws SemanticErrorException {
        if (!this.parameters.containsKey(identifier)) {
            VariableDescriptor parameter = new VariableDescriptor(dataType);
            parameter.setInitialised(true);
            this.parameters.put(identifier, parameter);
        }
        else
            throw new SemanticErrorException("Parameter name '" + identifier + "' already in use");
    }

    /**
     * Adds a variable to the variable list. A SemanticErrorException
     * is thrown if the identifier is already being used.
     * @param identifier variable name
     * @param dataType variable type
     * @throws SemanticErrorException
     */
    public void addVariable(String identifier, String dataType) throws SemanticErrorException {
        if (!this.variableDescriptors.containsKey(identifier))
            this.variableDescriptors.put(identifier, new VariableDescriptor(dataType));
        else
            throw new SemanticErrorException("Variable '" + identifier + "' already defined");
    }

    /**
     * This method receives a parameter list and a return type, and
     * compares these arguments to its own values. If the parameter
     * list is the same or the return type is different then a semantic
     * error exception is thrown.
     *
     * This method is called when two methods have the same identifier.
     * That is, these methods are valid if the parameter list is
     * different and the return type is the same. This is called
     * method overloading and is supported in J--.
     *
     * @param parameters parameter list
     * @param returnType return type
     * @throws SemanticErrorException
     */
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

    /**
     * Return string with information regarding this
     * method descriptor.
     *
     * @param prefix indentation
     * @return String with formatted information
     */
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

    /**
     * Returns the parameter list in List of String
     * format instead of VariableDescriptor format.
     * @return parameter list in String format
     * @see VariableDescriptor
     */
    public LinkedList<String> getStringParameters() {
        LinkedList<VariableDescriptor> parameterTypes = new LinkedList<VariableDescriptor>(this.parameters.values());
        LinkedList<String> returnTypes = new LinkedList<>();
        for (VariableDescriptor parameter : parameterTypes) {
            returnTypes.add(parameter.getType());
        }
        return returnTypes;
    }

    /**
     * Getter method for parameter list
     * @return parameter list
     */
    public HashMap<String, VariableDescriptor> getVariableDescriptors() {
        return variableDescriptors;
    }
}
