import Grammar.ParsingTable;
import LexicalAnalyzer.LexicalAnalyzer;
import MIPS.AssemblyGenerator;
import TAC.TAC;
import Terminals.Exception.AnalyzerExceptions;
import Terminals.Exception.IncorrectGrammarException;
import Terminals.Exception.TokenDoesNotExistException;
import Terminals.SymbolTable;
import Terminals.Token;
import Terminals.TokenDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws TokenDoesNotExistException, AnalyzerExceptions, IncorrectGrammarException, IOException {
        TokenDatabase td = new TokenDatabase();
        LexicalAnalyzer la = new LexicalAnalyzer();
        File file = new File("Resources/fibFunc.sfaa");
        ArrayList<Token> t = la.getTokens(file);
        ParsingTable pt = new ParsingTable();
        ArrayList<TAC> grammar = pt.checkGrammar(t);
        AssemblyGenerator ag = new AssemblyGenerator();
        ag.convertTacToMIPS(grammar);
        SymbolTable.printSymbolTable();
    }
}
