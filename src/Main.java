import Exceptions.SemanticErrorException;
import SymbolTable.*;

import java.util.LinkedList;

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
		// build symbol table
		SymbolTableBuilder tableBuilder = new SymbolTableBuilder(root);
		SymbolTable symbolTable = tableBuilder.buildSymbolTable();
		// dump table TODO: DELETE AFTER EVERYTHING IS OK :)
		System.out.println("\n==== Dumping SymbolTable ====\n");
		symbolTable.dump();
		// test lookups
		try {
			LinkedList<String> methPar = new LinkedList<>();
			methPar.add("int[]");
			// variable - class attribute
			VariableDescriptor var = symbolTable.lookupVariable("test_arr", "find_maximum", methPar);
			System.out.println(var.dump(""));
			// method
			MethodDescriptor meth = symbolTable.lookupMethod("find_maximum", methPar);
			System.out.println(meth.dump(""));
			// method variable
			VariableDescriptor methVar = symbolTable.lookupVariable("ed", "find_maximum", methPar);
			System.out.println(methVar.dump(""));
		} catch (SemanticErrorException e) {
			System.out.println(e.getMessage());
		}

		// analyse
		ParserVisitor semanticAnalyser = new SemanticAnalyser(symbolTable);
		semanticAnalyser.visit(root, null);
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