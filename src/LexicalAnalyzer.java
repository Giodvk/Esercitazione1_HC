import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class LexicalAnalyzer {
	
	private BufferedReader input;
	private static HashMap <String, Token> keywordTable;  // la struttura dati potrebbe essere una hash map
	private final HashMap<String,Token> symbolTable;// la symbol table in questo caso si riduce ad una semplice stringTable
	private int state;

	public LexicalAnalyzer(){
		// per la semplicit� di questo esercizio si potrebbe evitare di fare due tabelle
		keywordTable = new HashMap<>();
		symbolTable = new HashMap<>();
		state = 0;
		keywordTable.put("if", new Token("IF"));   // inserimento delle parole chiavi nella keywordTable per evitare di scrivere un diagramma di transizione per ciascuna di esse (le parole chiavi verranno "catturate" dal diagramma di transizione e gestite e di conseguenza). IF poteva anche essere associato ad una costante numerica
        keywordTable.put("else", new Token("ELSE"));
		keywordTable.put("then", new Token("THEN"));
		keywordTable.put("int", new Token("INT"));
		keywordTable.put("float", new Token("FLOAT"));
		keywordTable.put("while", new Token("WHILE"));
	}
	
	public boolean initialize(String filePath){
	
	   // prepara file input per lettura e controlla errori
		try {
			input = new BufferedReader(new FileReader(filePath));
			return true;
		}catch (IOException e){
			System.err.println("File con path: "+filePath+ " non trovato");
			return false;
		}
	    
	}
	private void retrack() {
		// fa il retract nel file di un carattere
		try {

			input.reset();
			//System.out.println((char) input.read());

		} catch (IOException e) {
			System.err.println("retrackt fallito");
			e.printStackTrace();
		}

	}
	public Token nextToken()throws Exception{
		
		//Ad ogni chiamata del lexer (nextToken())
		//si resettano tutte le variabili utilizzate
		state = 0;

		StringBuilder lessema = new StringBuilder(); //� il lessema riconosciuto
        char c;

		
		while(true){

			 // legge un carattere da input e lancia eccezione quando incontra EOF per restituire null
			 //  per indicare che non ci sono pi� token
			input.mark(2);
			if(input.read() == -1){
				throw new EOFException();
			}
			input.reset();
			input.mark(2);
            c = (char) input.read();

			//operator rel
			switch(state){
				case 0:
					if(Character.isLetter(c)){
						state = 9;
					}
					else if(c == '<'){
						state = 1;

					}else if(c == '='){
						state = 2;

					}else if(c == '>'){
						state = 3;

					}else if(c == '!'){
						state = 5;

					} else if(Character.isDigit(c)){
						state = 12;
					}else if(c == '('){
						state = 15;
					}else if(c == ')'){
						state = 16;
					}else if(c == '{'){
						state = 17;
					}else if(c == '}'){
						state = 18;
					}else if(c == ';'){
						state = 19;
					}else if(c == ','){
						state = 20;
					}else if(c == ' ' || c == '\t' || c == '\r' || c == '\n'){
						continue;
					}else{
						return new Token("ERROR");
					}
					break;


				case 1:

					if(c == '-'){
						state = 4;
						break;
					}else if (c == '='){
						return new Token("LE");
					}else {
						retrack();
						return new Token("LT");
					}


				case 2:
					if(c == '=') {
						return new Token("EQ");
					}else{
						return new Token ("ERROR");
					}
				case 3:
					if(c == '='){
						return new Token("GE");
					}else{
						retrack();
						return new Token("GT");
					}

				case 4:
					if(c == '-'){
						return new Token("ASSIGN");
					}else{
						return new Token("ERROR");
					}
				default:
					break;

				case 5:
					if(c == '='){
						return new Token("NOTEQ");
					}else{
						return new Token("ERROR");
					}
			}


			//id
			switch(state) {
				case 9:
						state = 10;
						lessema.append(c);
						break;

				case 10:
					if (Character.isLetterOrDigit(c)) {
						lessema.append(c);
					}else {
						retrack();
						return installID(lessema.toString());

					}
					break;

				default:

					break;
			}//end switch id

			//unsigned numbers
			switch(state){
				case 12:
						state = 13;
						lessema.append(c);
						break;
                           
                case 13:
					if(Character.isDigit(c)){
						lessema.append(c);
						break;
					}else{
						retrack();
						return new Token("NUMBER", lessema.toString());
					}
				default:
					break;
			}// end switch unsigned numbers

			//parentesi tonde e graffe
			switch(state){

				case 15:
						return new Token("LPAR");

				case 16:
						return new Token("RPAR");

				case 17:
						return new Token("LBRAC");

				case 18: return new Token("RBRAC");

				case 19 :
						return new Token("SEMI");

				case 20:
					return new Token("COMMA");

					default:
						break;
			}//end switch delimitatori


		}

	}



private Token installID(String lessema){
	Token token;
	
	//utilizzo come chiave della hashmap il lessema
	if(keywordTable.containsKey(lessema))
		return keywordTable.get(lessema);
	else{
		token =  new Token("ID", lessema);
		symbolTable.put(lessema, token);
		return token;
	}
}

}



