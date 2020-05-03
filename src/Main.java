import Exceptions.SemanticErrorException;
import SymbolTable.SymbolTable;

public class Main {

	public static void main(String[] args) throws Exception {
		// validate arguments
		if (args.length > 2) {
			System.err.println("Wrong number of arguments, expected: <file.jmm> [ <debug> ]");
		}
		boolean debug = false;
		if (args.length == 2)
			debug = Boolean.parseBoolean(args[1]);

		// ***** Syntactical Analysis
		// get root of Syntax Tree
		SimpleNode root = parse(args[0]);
		// Print tree to console
		root.dump("");

		// ***** Semantic Analysis
		// build symbol table
		SymbolTableBuilder tableBuilder = new SymbolTableBuilder(root);
		SymbolTable symbolTable = tableBuilder.buildSymbolTable();
		// dump table TODO: DELETE AFTER EVERYTHING IS OK :)
		if (debug) {
			System.out.println("\n==== Dumping SymbolTable ====\n");
			symbolTable.dump();
		}
		// analyse
		SemanticAnalyser semanticAnalyser = new SemanticAnalyser(symbolTable);
		boolean noErrors = semanticAnalyser.analise(root);
		if (!noErrors)
			throw new SemanticErrorException("Semantic Errors found");
	}

	public static void test(SimpleNode node) {
		System.out.println(node.jjtGetValue());

		if (node.jjtGetValue() == null) {
			System.out.println(node.toString());
		}

		Node[] children = node.jjtGetChildren();

		if (children == null) return;

		for (Node c : children)
			test((SimpleNode) c);

	}

	public static SimpleNode parse(String filename) throws ParseException {
		Parser parser;
		// open file as input stream
		try {
			parser = new Parser(new java.io.FileInputStream(filename));
		}
		catch (java.io.FileNotFoundException e) {
			System.out.println("ERROR: file " + filename + " not found.");
			return null;
		}

		return parser.parse();
	}
}