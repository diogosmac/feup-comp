import Exceptions.SemanticErrorException;
import SymbolTable.SymbolTable;

/**
 * <h1>J-- Compiler</h1>
 *
 * <p>The J-- Compiler is a compiler implemented in java using JavaCC
 * for a mini-java type language called J--. This class is responsible
 * for calling all other class methods which take care of the key
 * parts of compiler execution: </p>
 * <ol>
 *     <li>Parsing and Syntax Analysis</li>
 *     <li>Semantic Analysis and Symbol Table Building</li>
 *     <li>Code Generation</li>
 * </ol>
 *
 * @see Parser
 * @see SemanticAnalyser
 * @see SymbolTableBuilder
 * @see CodeGenerator
 */
public class Main {

	/**
	 * J-- Compiler main method
	 * @param args command line arguments
	 * @throws Exception
	 */
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
		if (debug) {
			System.out.println("\n==== Dumping Syntax Tree ====\n");
			root.dump("");
		}

		// ***** Semantic Analysis
		// build symbol table
		SymbolTableBuilder tableBuilder = new SymbolTableBuilder(root);
		SymbolTable symbolTable = tableBuilder.buildSymbolTable();
		// dump table in debug mode
		if (debug) {
			System.out.println("\n==== Dumping SymbolTable ====\n");
			symbolTable.dump();
		}
		// analyse
		SemanticAnalyser semanticAnalyser = new SemanticAnalyser(symbolTable, tableBuilder.getNumErrors());
		boolean noErrors = semanticAnalyser.analise(root);
		if (!noErrors)
			throw new SemanticErrorException("Semantic Errors found");

		// ***** Code Generation
		//generate code
		CodeGenerator codeGenerator = new CodeGenerator(symbolTable,root);
		codeGenerator.generateCode();
	}

	/**
	 * Parses the file whose name is passed as argument
	 * @param filename name of the .jmm file
	 * @return Root node of the built Abstract Syntax Tree (AST)
	 * @throws ParseException
	 */
	private static SimpleNode parse(String filename) throws ParseException {
		Parser parser;
		// open file as input stream
		try {
			parser = new Parser(new java.io.FileInputStream(filename));
		}
		catch (java.io.FileNotFoundException e) {
			System.out.println("ERROR: file " + filename + " not found.");
			return null;
		}
		// parse and return root node
		return parser.parse();
	}
}