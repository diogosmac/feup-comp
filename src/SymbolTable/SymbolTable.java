package SymbolTable;

import Exceptions.SemanticErrorException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Symbol Table in JMM
 */
public class SymbolTable {

    private String className;
    /**
     * identifier -> < data type >
     */
    private HashMap<String, VariableDescriptor> variableDescriptors;
    /**
     * identifier -> < return data type, list of params, list of local variables >
     */
    private HashMap<String, LinkedList<MethodDescriptor>> methodDescriptors;
    /**
     * identifier -> < isMethod, isStatic, return data type, list of params >
     */
    private HashMap<String, LinkedList<ImportDescriptor>> importDescriptors;

    public SymbolTable() {
        this.className = "";
        this.methodDescriptors = new HashMap<>();
        this.variableDescriptors = new HashMap<>();
        this.importDescriptors = new HashMap<>();
    }

    public VariableDescriptor lookupAttribute(String variableIdentifier) throws SemanticErrorException {
        if (!this.variableDescriptors.containsKey(variableIdentifier))
            throw new SemanticErrorException("Variable '" + variableIdentifier + "' not defined");
        else
            return this.variableDescriptors.get(variableIdentifier);
    }

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

    public void addVariable(String identifier, String dataType) throws SemanticErrorException {
        if (!this.variableDescriptors.containsKey(identifier))
            this.variableDescriptors.put(identifier, new VariableDescriptor(dataType));
        else
            throw new SemanticErrorException("Variable '" + identifier + "' already defined");
    }

    public void addMethod(String identifier, String dataType) {
        // no methods with the name 'identifier'
        if (!this.methodDescriptors.containsKey(identifier))
            this.methodDescriptors.put(identifier, new LinkedList<>());
        // methods with the name 'identifier' are already present
        this.methodDescriptors.get(identifier).add(new MethodDescriptor(dataType));
    }

    public void addMethodParameter(String methodIdentifier, String parameterIdentifier, String dataType) throws SemanticErrorException {
        try {
            this.methodDescriptors.get(methodIdentifier).getLast().addParameter(parameterIdentifier, dataType);
        } catch (SemanticErrorException e) {
            throw new SemanticErrorException(e.getMessage() + " in method '" + methodIdentifier + "'");
        }
    }

    public void addMethodVariable(String methodIdentifier, String variableIdentifier, String dataType) throws SemanticErrorException {
        try {
            this.methodDescriptors.get(methodIdentifier).getLast().addVariable(variableIdentifier, dataType);
        } catch (SemanticErrorException e) {
            throw new SemanticErrorException(e.getMessage() + " in method '" + methodIdentifier + "'");
        }
    }

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

    public void addImport(String importIdentifier, boolean isStatic, boolean isMethod) {
        // no imports with the name 'identifier'
        if (!this.importDescriptors.containsKey(importIdentifier))
            this.importDescriptors.put(importIdentifier, new LinkedList<>());
        // imports with the name 'identifier' are already present
        this.importDescriptors.get(importIdentifier).add(new ImportDescriptor(isStatic, isMethod));
    }

    public void addImportParameter(String importIdentifier, String dataType) {
        this.importDescriptors.get(importIdentifier).getLast().addParameter(dataType);
    }

    public void setImportReturnType(String importIdentifier, String dataType) {
        this.importDescriptors.get(importIdentifier).getLast().setReturnType(dataType);
    }

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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void dump() {
        // Class name
        System.out.println("Class Name: " + this.className + "\n");

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
