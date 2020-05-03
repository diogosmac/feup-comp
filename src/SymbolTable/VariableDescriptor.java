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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        VariableDescriptor that = (VariableDescriptor) o;

        return this.type.equals(that.type);
    }
}
