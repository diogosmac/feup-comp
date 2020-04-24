package SymbolTable;

public class VariableDescriptor extends Descriptor {

    public VariableDescriptor(String dataType) {
        this.type = dataType;
    }


    public String dump(String prefix) {
        return prefix + "Variable Descriptor: " + this.type;
    }
}
