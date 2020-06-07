package SymbolTable;

/**
 * <h1>Variable Descriptor</h1>
 * <p>A Variable Descriptor contains information regarding
 * a variable or a method parameter.</p>
 */
public class VariableDescriptor extends Descriptor {

    /**
     * True if the variable
     */
    private boolean initialised;
    private boolean constant;

    public VariableDescriptor(String dataType) {
        this.initialised = false;
        this.type = dataType;
        this.constant = true;
    }

    /**
     * Return string with information regarding this
     * variable descriptor.
     *
     * @param prefix indentation
     * @return String with formatted information
     */
    public String dump(String prefix) {
        return prefix + "Variable Descriptor: " + this.type;
    }

    /**
     * Getter for initialised attribute
     * @return initialised
     */
    public boolean isInitialised() {
        return initialised;
    }

    /**
     * Setter for initialised attribure
     * @param initialised value to set
     */
    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    public void setNotConstant() {
        this.constant = false;
    }

    public boolean isConstant() {
        return this.constant;
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
