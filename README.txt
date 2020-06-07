**PROJECT TITLE: Compiler of the Java-- language to Java Bytecodes


**GROUP: 3F

Diogo José de Sousa Machado, 201706832, GRADE: 17.5, CONTRIBUTION: 25%
Gonçalo José Marantes Pimenta da Costa Monteiro, 201706917, GRADE: 17.5, CONTRIBUTION: 25%
Leonardo Fernandes Moura, 201706907, GRADE: 17.5, CONTRIBUTION: 25%
Maria João Sera Viana, 201604751, GRADE: 17.5, CONTRIBUTION: 25%

GLOBAL Grade of the project: 17.5


** SUMMARY:

This project was developed for the Compilers course unit. It aims to implement compiler (jmm) that translates programs in Java-- into java bytecodes. It generates files files of the classes with JVM instructions accepted by jasmin (the tool that translates those classes into java bytecodes. 
The compiler includes syntactic analysis (including Syntax Tree building), semantic analysis (including symbol table building) and code generation (with best instruction selection).


** EXECUTE:

To compile the program: gradle build
To execute the program: java -jar <jar filename> text/fixtures/public/<file.jmm> [ <DEBUG_MODE> ]
    * <DEBUG_MODE> - 'true' or 'false'
        * true - dumps generated Syntax Tree and Symbol Table to the terminal
        * false (default) - does nothing


**DEALING WITH SYNTACTIC ERRORS:

The compiler does not abort execution immediately after the first error. It displays the first 10 errors found, before aborting the execution, so that the developer proceeds with their correction.


**SEMANTIC ANALYSIS:

The compiler implements the following semantic rules:

***Type Verification
* Unary/Binary Operation: it verifies if operations are done with the same type;
* Array Operations: it is not possible to use arrays directly in arithmetic operations;
* Array Index: it verifies if an array access is done in an actual array;
* Array Index: it verifies if the index of the acmes array is an integer;
* Assignments Types: it verifies if the assignee type is equal to the assigner type;
* Conditional Expression: it verifies if conditional expressions return a boolean value;
* Variable Initialization: it verifies if variables are initialized, giving a warning instead of an error;

***Function Verification
* Target Verification: it verifies if the target of a method exists, and if it contains the method. If it is a declared class, it verifies if it is an extended class method, considering the imports;
* Imported Methods: if the method is not from the declared class, it verifies if the method was imported;
* Parameter List: it verifies if the number of the invoked arguments is equal to the number of parameters of the declaration;
* Parameter Type List: it verifies if the type of the parameters matches the type of arguments.

Besides the mentioned semantic rules, in this stage our compiler also builds a symbol table with the following features:
* Global Information: information regarding our class and import statements;
* Class Specific Information: information about possible extended class, fields and methods;
* Method Specific Information: information about parameters and local variables;
* Method Overloading: Our compiler supports method overloading, i.e. method with the same name and return type, but different parameter signature;
* Table Lookups: The symbol tables allows to lookup any import, method, variable and attribute at any time during the semantic analysis and code generation steps;
* Debug Mode: (Off by default) If active it prints the symbol table to the terminal

**INTERMEDIATE REPRESENTATIONS (IRs):

The intermediate representation is being delivered by the Syntax Tree (Abstract Syntax Tree) that has in mind the conservation of the operations order based on their priorities. This helps in the overall project but most importantly in the code generation phase.


**CODE GENERATION: 

If there are no errors during the semantic and syntactic analysis, the code for the specified file is generated, generating for each AST node the corresponding jvm code.


**OVERVIEW:

We were able to finish almost all the suggestions for this compiler, being:

1.  Develop a parser for Java-- using JavaCC and taking as starting point the Java-- grammar furnished;
2.  Include error treatment and recovery mechanisms;
3.  Proceed with the specification of the AST;
4.  Include the necessary symbol tables;
5.  Semantic Analysis;
6.  Generate JVM code accepted by jasmin corresponding to the invocation of functions in
    Java--;
7.  Generate JVM code accepted by jasmin for arithmetic expressions;
8.  Generate JVM code accepted by jasmin for conditional instructions (if and if-else);
9.  Generate JVM code accepted by jasmin for loops;
10. Generate JVM code accepted by jasmin to deal with arrays;
11. Complete the compiler and test it using a set of Java-- classes;
12. Proceed with the optimizations related to the code generation, related to the optimizations related to the “-o” option.


**TASK DISTRIBUTION:

Every member contributed equally to every stage of the development.


**PROS: 

* The usage of some built in javacc/jjtree features (parser visitor), which makes our project scalable easily.
* Functional and easy to understand AST with the help of javacc/jjtree feature option (multi).
* Runs all test files.


**CONS:

* The optimization related to the register allocation (“-r”) was not implemented, which is something that can be improved in the future.

**CONCLUSIONS

Having completed this project our group has concluded that building a good Syntax Tree is one of the most important steps when building a compiler.
Also having spent half the project's time in correcting and perfecting our parser in order to build a consistent and easy to understand Syntax Tree made us aware of that. In return the other phases (Semantic Analysis and Code Generation) were much simpler with a good AST.
In fact in all 3 checkpoints of this project we found ourselves looking back at our parser in order to fix some inconsistencies.
