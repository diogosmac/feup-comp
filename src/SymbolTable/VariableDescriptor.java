package SymbolTable;

public class VariableDescriptor extends Descriptor {

    private boolean initialised;

    public VariableDescriptor(String dataType) {
        this.initialised = false;
        this.type = dataType;
    }

    public String dump(String prefix) {
        return prefix + "Variable Descriptor: " + this.type;
    }

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }
}
