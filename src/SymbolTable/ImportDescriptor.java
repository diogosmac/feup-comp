package SymbolTable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ImportDescriptor {

    private final boolean isStatic;
    private final boolean isMethod;
    private LinkedList<String> parameters;
    private String returnType;

    public ImportDescriptor(boolean isStatic, boolean isMethod) {
        this.isStatic = isStatic;
        this.isMethod = isMethod;
        if (isMethod) {
            this.parameters = new LinkedList<>();
            this.returnType = "void";
        }
    }

    public void addParameter(String dataType) {
        if (!this.isMethod)
            return;

        this.parameters.add(dataType);
    }

    public void setReturnType(String dataType) {
        if (!this.isMethod)
            return;

        this.returnType = dataType;
    }

    public String dump(String prefix) {

        StringBuilder buf = new StringBuilder(prefix + "Descriptor " + "\n");
        if (this.isMethod) {
            // get return type
            buf.append(prefix).append("  Return type ").append(this.returnType).append("\n");
            // get parameters
            buf.append(prefix).append("  Parameters").append("\n");

            for (String param : this.parameters) {
                buf.append(prefix).append("    Parameter Name: ");
                //get parameter type
                buf.append(param).append("\n");
            }
        }

        return buf.toString();

    }
}
