/* Generated By:JJTree: Do not edit this line. ASTid.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTid extends SimpleNode {

  public String id;

  public ASTid(int id) {
    super(id);
  }

  public ASTid(Parser p, int id) {
    super(p, id);
  }

  public String toString() {
    return "id: " + id;
  }

}
/* JavaCC - OriginalChecksum=03b15ae806035d9afe731d9e34eea5d6 (do not edit this line) */
