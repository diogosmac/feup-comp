package SymbolTable;

import Exceptions.SemanticErrorException;

import java.util.LinkedList;

/**
 * <h1>Import Descriptor</h1>
 * <p>An import descriptor has more information regarding
 * an import statement. It can be a method (static or not)
 * or an object constructor</p>
 */
public class ImportDescriptor extends Descriptor{

    /**
     * May or may not be a static import
     */
    private final boolean isStatic;

    /**
     * May or may not be a method, it can also
     * be a constructor
     */
    private final boolean isMethod;
    /**
     * Parameter list storing parameter types
     * such as [int, int, int] if our import
     * statement has 3 parameters of type int
     */
    private LinkedList<VariableDescriptor> parameters;

    /**
     * Constructor
     * @param isStatic static flag
     * @param isMethod method flag
     */
    public ImportDescriptor(boolean isStatic, boolean isMethod) {
        this.isStatic = isStatic;
        this.isMethod = isMethod;
        if (isMethod) {
            this.parameters = new LinkedList<>();
            this.type = "void";
        }
    }

    /**
     * Adds a parameter to the parameter list
     * @param dataType new parameter
     */
    public void addParameter(String dataType) {
        if (!this.isMethod)
            return;

        this.parameters.add(new VariableDescriptor(dataType));
    }

    /**
     * Set import return type.
     * Default is void.
     * @param dataType import return type
     */
    public void setReturnType(String dataType) {
        if (!this.isMethod)
            return;

        this.type = dataType;
    }

    /**
     * Getter method for isMethod Flag
     * Returns true if import is a method
     * @return isMethod flag
     */
    public boolean isMethod() {
        return isMethod;
    }

    /**
     * Getter method for isStatic Flag
     * Returns true if import is static
     * @return isMethod flag
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * This method receives a parameter list and a return type, and
     * compares these arguments to its own values. If the parameter
     * list is the same or the return type is different then a semantic
     * error exception is thrown.
     *
     * This method is called when two imports have the same identifier.
     * That is, these imports are valid if the parameter list is
     * different and the return type is the same. This is called
     * method overloading and is supported in J--.
     *
     * @param parameters parameter list
     * @param returnType return type
     * @throws SemanticErrorException
     */
    public void checkEqualImport(LinkedList<VariableDescriptor> parameters, String returnType) throws SemanticErrorException {
        // check if both parameter lists are the same
        if (this.parameters.equals(parameters))
            throw new SemanticErrorException("Parameter type list already defined");
        // check return type
        if (!this.getType().equals(returnType))
            throw new SemanticErrorException("Return type '" + returnType + "' different from other imports with the same identifier");
    }

    /**
     * Return string with information regarding this
     * import descriptor.
     *
     * @param prefix identation
     * @return String with formatted information
     */
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

    /**
     * Getter method for parameter list
     * @return parameter list
     */
    public LinkedList<VariableDescriptor> getParameters() {
        return parameters;
    }

    /**
     * Returns the parameter list in List of String
     * format instead of VariableDescriptor format.
     * @return parameter list in String format
     * @see VariableDescriptor
     */
    public LinkedList<String> getStringParameters() {
        LinkedList<String> returnTypes = new LinkedList<>();
        for (VariableDescriptor parameter : this.parameters) {
            returnTypes.add(parameter.getType());
        }
        return returnTypes;
    }
}
