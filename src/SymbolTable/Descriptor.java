package SymbolTable;

/**
 * <h1>Descriptor</h1>
 * <p>General purpose descriptor which may be used
 * as a variable, method or import. It consists
 * of a type which might be a method return
 * type or a variable type.</p>
 * <p>It mainly can be an 'int', 'String',
 * 'boolean' or Object type</p>
 */
public class Descriptor {

    /**
     * return type used in operations
     */
    protected String type;

    /**
     * Getter method for type
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Setter method for type
     * @param type new type
     */
    public void setType(String type) {
        this.type = type;
    }
}
