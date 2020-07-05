package SymbolTable;

import Exceptions.SemanticErrorException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * <h1>Symbol Table</h1>
 * <p>This class represents a symbol table. It contains
 * information about the imported resources, class attributes
 * and methods.</p>
 * <p>This is a vital part of semantic analysis and lookups
 * during code generation</p>
 */
public class SymbolTable {

    /**
     * Name of the compiled class
     */
    private String className;
    /**
     * Name of the extended class (if any)
     */
    private String extendedClassName;
    /**
     * Attribute list
     * identifier -> < data type >
     */
    private HashMap<String, VariableDescriptor> variableDescriptors;
    /**
     * Declared Methods
     * identifier -> < return data type, list of params, list of local variables >
     */
    private HashMap<String, LinkedList<MethodDescriptor>> methodDescriptors;
    /**
     * Imported resources
     * identifier -> < isMethod, isStatic, return data type, list of params >
     */
    private HashMap<String, LinkedList<ImportDescriptor>> importDescriptors;

    /**
     * Constructor
     */
    public SymbolTable() {
        this.className = "";
        this.extendedClassName = "";
        this.methodDescriptors = new HashMap<>();
        this.variableDescriptors = new HashMap<>();
        this.importDescriptors = new HashMap<>();
    }

    /**
     * Lookup class attribute with the name passed as
     * argument. A SemanticErrorException is thrown if
     * not found.
     * @param variableIdentifier identifier
     * @return Variable Descriptor if found
     * @throws SemanticErrorException if not found
     */
    public VariableDescriptor lookupAttribute(String variableIdentifier) throws SemanticErrorException {
        if (!this.variableDescriptors.containsKey(variableIdentifier))
            throw new SemanticErrorException("Variable '" + variableIdentifier + "' not defined");
        else
            return this.variableDescriptors.get(variableIdentifier);
    }

    /**
     * Lookup class method with the identifier and parameters
     * type list passed as argument. A SemanticErrorException
     * is thrown if not found.
     * @param methodIdentifier identifier
     * @param parameterTypes List of parameter types
     * @return Method Descriptor if found
     * @throws SemanticErrorException if not found
     */
    public MethodDescriptor lookupMethod(String methodIdentifier, LinkedList<String> parameterTypes) throws SemanticErrorException {
        // build error message
        StringBuilder message = new StringBuilder(methodIdentifier + "(");
        message.append(String.join(", ", parameterTypes));
        message.append(")");
        // if method name does not exist throw error
        if (!this.methodDescriptors.containsKey(methodIdentifier))
            throw new SemanticErrorException("Method '" + message + "' not defined");
        // methodIdentifier exists, now we must check for parameter types
        // find method in possible method descriptors
        LinkedList<MethodDescriptor> possibleMethods = this.methodDescriptors.get(methodIdentifier);
        for (MethodDescriptor possibleMethod : possibleMethods) {
            // get List of parameter types
            LinkedList<String> possibleParameterTypes = possibleMethod.getStringParameters();
            if (possibleParameterTypes.equals(parameterTypes))
                return possibleMethod;
        }
        // if method parameter types do not match any of the declared
        // then the method is not defined
        throw new SemanticErrorException("Method '" + message + "' not defined");
    }

    /**
     * Lookup class method with the identifier and parameters
     * type list passed as argument. A SemanticErrorException
     * is thrown if not found.
     * @param importIdentifier import identifier
     * @param parameterTypes List of parameter types
     * @return Import Descriptor if found
     * @throws SemanticErrorException if not found
     */
    public ImportDescriptor lookupImport(String importIdentifier, LinkedList<String> parameterTypes) throws SemanticErrorException {
        // build error message
        StringBuilder message = new StringBuilder(importIdentifier + "(");
        message.append(String.join(", ", parameterTypes));
        message.append(")");
        // if method name does not exist throw error
        if (!this.importDescriptors.containsKey(importIdentifier))
            throw new SemanticErrorException("Method '" + message + "' not defined");
        // methodIdentifier exists, now we must check for parameter types
        // find method in possible method descriptors
        LinkedList<ImportDescriptor> possibleImports = this.importDescriptors.get(importIdentifier);
        for (ImportDescriptor possibleImport : possibleImports) {
            // get List of parameter types
            LinkedList<String> possibleParameterTypes = possibleImport.getStringParameters();
            if (possibleParameterTypes.equals(parameterTypes))
                return possibleImport;
        }
        // if method parameter types do not match any of the declared
        // then the method is not defined
        throw new SemanticErrorException("Method '" + message + "' not defined");
    }

    /**
     * Adds an attribute to the attribute list. A SemanticErrorException
     * is thrown if the identifier is already being used.
     * @param identifier attribute identifier
     * @param dataType attribute data type
     * @throws SemanticErrorException if the identifier is already in use
     */
    public void addVariable(String identifier, String dataType) throws SemanticErrorException {
        if (!this.variableDescriptors.containsKey(identifier))
            this.variableDescriptors.put(identifier, new VariableDescriptor(dataType));
        else
            throw new SemanticErrorException("Variable '" + identifier + "' already defined");
    }

    /**
     * Adds a method to the method list.
     * @param identifier method identifier
     * @param dataType method return data type
     */
    public void addMethod(String identifier, String dataType) {
        // no methods with the name 'identifier'
        if (!this.methodDescriptors.containsKey(identifier))
            this.methodDescriptors.put(identifier, new LinkedList<>());
        // methods with the name 'identifier' are already present
        this.methodDescriptors.get(identifier).add(new MethodDescriptor(dataType));
    }

    /**
     * Adds an method parameter to the method's parameter list.
     * A SemanticErrorException is thrown if the parameter
     * identifier is already being used.
     * @param methodIdentifier method identifier
     * @param parameterIdentifier parameter identifier
     * @param dataType parameter data type
     * @throws SemanticErrorException if the parameter identifier is already in use
     */
    public void addMethodParameter(String methodIdentifier, String parameterIdentifier, String dataType) throws SemanticErrorException {
        try {
            this.methodDescriptors.get(methodIdentifier).getLast().addParameter(parameterIdentifier, dataType);
        } catch (SemanticErrorException e) {
            throw new SemanticErrorException(e.getMessage() + " in method '" + methodIdentifier + "'");
        }
    }

    /**
     * Adds an method variable to the method's variable list.
     * A SemanticErrorException is thrown if the variable
     * identifier is already being used
     * @param methodIdentifier method identifier
     * @param variableIdentifier variable identifier
     * @param dataType variable data type
     * @throws SemanticErrorException if the identifier is already in use
     */
    public void addMethodVariable(String methodIdentifier, String variableIdentifier, String dataType) throws SemanticErrorException {
        try {
            this.methodDescriptors.get(methodIdentifier).getLast().addVariable(variableIdentifier, dataType);
        } catch (SemanticErrorException e) {
            throw new SemanticErrorException(e.getMessage() + " in method '" + methodIdentifier + "'");
        }
    }

    /**
     * This method receives a method identifier and compares all methods
     * with the same identifier to make sure method overloading is
     * supported.
     *
     * @param identifier method identifier
     * @throws SemanticErrorException if any error occurs
     */
    public void checkEqualMethods(String identifier) throws SemanticErrorException {
        // get method descriptor list
        LinkedList<MethodDescriptor> methods = this.methodDescriptors.get(identifier);
        // cross check parameter list and return type
        for (int firstIndex = 0; firstIndex < methods.size(); firstIndex++) {
            for (int secondIndex = firstIndex + 1; secondIndex < methods.size(); secondIndex++) {
                MethodDescriptor first = methods.get(firstIndex);
                MethodDescriptor second = methods.get(secondIndex);
                try {
                    first.checkEqualMethod(second.getParameters(), second.getType());
                } catch (SemanticErrorException e) {
                    throw new SemanticErrorException(e.getMessage() + " in method '" + identifier + "'");
                }
            }
        }
    }

    /**
     * Adds an import to the import list.
     * @param importIdentifier import identifier
     * @param isStatic true if import is static
     * @param isMethod true if import is a method
     */
    public void addImport(String importIdentifier, boolean isStatic, boolean isMethod) {
        // no imports with the name 'identifier'
        if (!this.importDescriptors.containsKey(importIdentifier))
            this.importDescriptors.put(importIdentifier, new LinkedList<>());
        // imports with the name 'identifier' are already present
        this.importDescriptors.get(importIdentifier).add(new ImportDescriptor(isStatic, isMethod));
    }

    /**
     * Adds an import parameter to the import's parameter list.
     * @param importIdentifier method identifier
     * @param dataType parameter data type
     */
    public void addImportParameter(String importIdentifier, String dataType) {
        this.importDescriptors.get(importIdentifier).getLast().addParameter(dataType);
    }

    /**
     * Sets the import's return type
     * @param importIdentifier method identifier
     * @param dataType import's return type
     */
    public void setImportReturnType(String importIdentifier, String dataType) {
        this.importDescriptors.get(importIdentifier).getLast().setReturnType(dataType);
    }

    /**
     * This method receives an import identifier and compares all imports
     * with the same identifier to make sure method overloading is
     * supported.
     *
     * @param identifier import identifier
     * @throws SemanticErrorException if any error occurs
     */
    public void checkEqualImports(String identifier) throws SemanticErrorException {
        // get import descriptor list
        LinkedList<ImportDescriptor> imports = this.importDescriptors.get(identifier);
        // verification only valid for method imports
        LinkedList<ImportDescriptor> methodImports = new LinkedList<>(imports);
        methodImports.removeIf(ImportDescriptor -> !ImportDescriptor.isMethod());
        // cross check parameter list and return type
        for (int firstIndex = 0; firstIndex < methodImports.size(); firstIndex++) {
            for (int secondIndex = firstIndex + 1; secondIndex < methodImports.size(); secondIndex++) {
                ImportDescriptor first = methodImports.get(firstIndex);
                ImportDescriptor second = methodImports.get(secondIndex);
                try {
                    first.checkEqualImport(second.getParameters(), second.getType());
                } catch (SemanticErrorException e) {
                    throw new SemanticErrorException(e.getMessage() + " in import '" + identifier + "'");
                }
            }
        }
    }

    /**
     * Getter method for import descriptors
     * @return import descriptor list
     */
    public HashMap<String, LinkedList<ImportDescriptor>> getImportDescriptors() {
        return importDescriptors;
    }

    /**
     * Getter method for method descriptors
     * @return method descriptor list
     */
    public HashMap<String, LinkedList<MethodDescriptor>> getMethodDescriptors() {
        return methodDescriptors;
    }

    /**
     * Getter method for parameter descriptors
     * @return parameter descriptor list
     */
    public HashMap<String, VariableDescriptor> getVariableDescriptors() {
        return variableDescriptors;
    }

    /**
     * Getter method for class name
     * @return class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Getter method for extended class name
     * @return extended class name
     */
    public String getExtendedClassName() {
        return extendedClassName;
    }

    /**
     * Setter method for class name
     * @param className class name to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Setter method for extended class name
     * @param extendedClassName extended class name to set
     */
    public void setExtendedClassName(String extendedClassName) {
        this.extendedClassName = extendedClassName;
    }

    /**
     * Prints to console the information regarding the
     * symbol table.
     */
    public void dump() {
        // Class name
        StringBuilder buffer = new StringBuilder("Class Name: " + this.className + "\n");
        if (!this.extendedClassName.equals(""))
            buffer.append("Extended Class Name: ").append(this.extendedClassName).append("\n");
        System.out.println(buffer);

        // Import Descriptors
        System.out.println("Import Descriptors");
        for (Map.Entry<String, LinkedList<ImportDescriptor>> entry : this.importDescriptors.entrySet()) {
            StringBuilder buf = new StringBuilder(" Import name: ");
            // get import name
            buf.append(entry.getKey()).append("\n");
            // get all descriptors with the same name
            for (ImportDescriptor var : entry.getValue())
                buf.append(var.dump("    "));
            System.out.println(buf.toString());
        }

        // Variable Descriptors
        System.out.println("Variable Descriptors:");
        for (Map.Entry<String, VariableDescriptor> entry : this.variableDescriptors.entrySet()) {
            StringBuilder buf = new StringBuilder("  Variable name: ");
            // get variable name
            buf.append(entry.getKey()).append("\n");
            // get all descriptors with the same name
            buf.append(entry.getValue().dump("    "));
            System.out.println(buf.toString());
        }

        // Method Descriptors
        System.out.println("\nMethod Descriptors:");
        for (Map.Entry<String, LinkedList<MethodDescriptor>> entry : this.methodDescriptors.entrySet()) {
            StringBuilder buf = new StringBuilder("  Method name: ");
            // get method name
            buf.append(entry.getKey()).append("\n");
            // get all descriptors with the same name
            for (MethodDescriptor method : entry.getValue())
                buf.append(method.dump("    ")).append("\n");
            System.out.println(buf.toString());
        }
    }
}
