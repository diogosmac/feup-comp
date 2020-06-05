**PROJECT TITLE: Compiler of the Java-- language to Java Bytecodes

**GROUP: 3F
(Names, numbers, self assessment, and contribution of the members of the group to the project according to:)
Diogo José de Sousa Machado, 201706832, GRADE: <0 to 20 value>, CONTRIBUTION: <0 to 100 %>
Gonçalo José Marantes Pimenta da Costa Monteiro, 201706917, GRADE: <0 to 20 value>, CONTRIBUTION: <0 to 100 %>
Leonardo Fernandes Moura, 201706907, GRADE: <0 to 20 value>, CONTRIBUTION: <0 to 100 %>
Maria João Sera Viana, 201604751, GRADE: <0 to 20 value>, CONTRIBUTION: <0 to 100 %>

GLOBAL Grade of the project: <0 to 20>

** SUMMARY: 

This project was developed for the Compilers course unit. It aims to implement compiler (jmm) that translates programs in Java-- into java bytecodes. It generates files files of the classes with JVM instructions accepted by jasmin (the tool that translates those classes into java bytecodes. 
The compiler includes syntactical analysis, semantical analysis and code generation.

** EXECUTE: 

To compile the program: gradle build
To execute the program: java -jar <jar filename> text/fixtures/public/<file.jmm>

**DEALING WITH SYNTACTIC ERRORS: (Describe how the syntactic error recovery of your tool does work. Does it exit after the first error?)

The compiler does not abort execution immediately after the first error. It displays the first 10 errors found, before aborting the execution, so that the developer proceeds with their correction.

**SEMANTIC ANALYSIS: 

The compiler implements the following semantic rules:

* Unary/Binary Operation: it verifies if operations are done with the same type;
* Array Operations: it is not possible to use arrays directly to arithmetic operations;
* Array Index: it verifies if an array access is done in an actual array;
* Array Index: it verifies if the index of the acmes array is an integer;
* Assignments Types: it verifies if the assignee value is equal to the assigned value;
* Conditional Expression: it verifies if conditional expressions return a boolean value;
* Variable Initialization: it verifies if variables are initialized, giving a warning instead of an error;
* Target Verification: it verifies if the target of a method exists, and if it contains the method. If it is a declared class, it verifies if it is a extends method, considering the imports;
* Imported Methods: if the method is not from the declared class, it verifies if the method was imported;
* Parameter List: it verifies if the number of the invoked arguments is equal to the number of parameters of the declaration;
* Parameter Type List: it verifies if the type of the parameters matches the type of arguments.

**INTERMEDIATE REPRESENTATIONS (IRs): (for example, when applicable, briefly describe the HLIR (high-level IR) and the LLIR (low-level IR) used, if your tool includes an LLIR with structure different from the HLIR)

**CODE GENERATION: (describe how the code generation of your tool works and identify the possible problems your tool has regarding code generation.)

**OVERVIEW: (refer the approach used in your tool, the main algorithms, the third-party tools and/or packages, etc.)

**TASK DISTRIBUTION: (Identify the set of tasks done by each member of the project. You can divide this by checkpoint it if helps)

**PROS: (Identify the most positive aspects of your tool)
**CONS: (Identify the most negative aspects of your tool)