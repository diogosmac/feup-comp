import SymbolTable.SymbolTable;

public class Main {

	public static void main(String[] args) throws ParseException {
		// validate arguments
		if (args.length != 1) {
			System.err.println("Wrong number of arguments, expected: <file.jmm>");
		}

		// ***** Syntactical Analysis
		// get root of Syntax Tree
		SimpleNode root = parse(args[0]);
		// Print tree to console
		root.dump("");


		// ***** Semantic Analysis
		SymbolTableBuilder tableBuilder = new SymbolTableBuilder(root);
		SymbolTable symbolTable = tableBuilder.buildSymbolTable();

		symbolTable.dump();
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