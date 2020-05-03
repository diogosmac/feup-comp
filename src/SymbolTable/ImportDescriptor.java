package SymbolTable;

import Exceptions.SemanticErrorException;

import java.util.LinkedList;

public class ImportDescriptor extends Descriptor{

    private final boolean isStatic;
    private final boolean isMethod;
    private LinkedList<VariableDescriptor> parameters;

    public ImportDescriptor(boolean isStatic, boolean isMethod) {
        this.isStatic = isStatic;
        this.isMethod = isMethod;
        if (isMethod) {
            this.parameters = new LinkedList<>();
            this.type = "void";
        }
    }

    public void addParameter(String dataType) {
        if (!this.isMethod)
            return;

        this.parameters.add(new VariableDescriptor(dataType));
    }

    public void setReturnType(String dataType) {
        if (!this.isMethod)
            return;

        this.type = dataType;
    }

    public boolean isMethod() {
        return isMethod;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void checkEqualImport(LinkedList<VariableDescriptor> parameters, String returnType) throws SemanticErrorException {
        // check if both parameter lists are the same
        if (this.parameters.equals(parameters))
            throw new SemanticErrorException("Parameter type list already defined");
        // check return type
        if (!this.getType().equals(returnType))
            throw new SemanticErrorException("Return type '" + returnType + "' different from other imports with the same identifier");
    }

    public String dump(String prefix) {

        StringBuilder buf = new StringBuilder(prefix + "Descriptor " + (this.isStatic ? "static" : "") + "\n");
        if (this.isMethod) {
            // get return type
            buf.append(prefix).append("  Return type ").append(this.type).append("\n");
            // get parameters
            buf.append(prefix).append("  Parameters").append("\n");

            for (VariableDescriptor param : this.parameters) {
                //get parameter type
                buf.append(param.dump(prefix + "    ")).append("\n");
            }
        }
        return buf.toString();
    }

    public LinkedList<VariableDescriptor> getParameters() {
        return parameters;
    }

    public LinkedList<String> getStringParameters() {
        LinkedList<String> returnTypes = new LinkedList<>();
        for (VariableDescriptor parameter : this.parameters) {
            returnTypes.add(parameter.getType());
        }
        return returnTypes;
    }

    public boolean getStatic(){
        return this.isStatic;
    }
}
