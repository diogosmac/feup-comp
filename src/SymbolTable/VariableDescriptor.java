package SymbolTable;

public class VariableDescriptor {
    private String dataType;

    public VariableDescriptor(String dataType) {
        this.dataType = dataType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String dump(String prefix) {
        return prefix + "Variable Descriptor: " + this.dataType;
    }
}
