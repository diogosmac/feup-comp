/* Generated By:JJTree: Do not edit this line. ASTImportDeclaration.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTImportDeclaration extends SimpleNode {

  public String importId;
  public boolean isStatic = false;

  public ASTImportDeclaration(int id) {
    super(id);
  }

  public ASTImportDeclaration(Parser p, int id) {
    super(p, id);
  }

  public String toString() {
    return "ImportDeclaration: " + (isStatic ? "static " : "") + importId;
  }

}
/* JavaCC - OriginalChecksum=58cf9268d6c581cdfa9ac5a77cd0cce3 (do not edit this line) */
