public class Main {

	public static void main(String[] args) {
		System.out.println("Parser for programs in the Java-- language");

		Parser parser;
		// open file as input stream
		try {
			parser = new Parser(new java.io.FileInputStream(args[0]));
		}
		catch (java.io.FileNotFoundException e) {
			System.out.println("ERROR: file " + args[0] + " not found.");
			return;
		}

		// create Syntax Tree (returns reference to root node)
		SimpleNode root = parser.Tree();

		// Print tree to console
		root.dump("");
	}
}