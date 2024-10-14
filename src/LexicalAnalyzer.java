import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class LexicalAnalyzer {
	
	private BufferedReader input;
	private static HashMap <String, Token> keywordTable;  // la struttura dati potrebbe essere una hash map
	private HashMap<String,Token> symbolTable;// la symbol table in questo caso si riduce ad una semplice stringTable
	private int state;

	public LexicalAnalyzer(){
		// per la semplicit� di questo esercizio si potrebbe evitare di fare due tabelle
		keywordTable = new HashMap<String,Token>();
		symbolTable = new HashMap<String,Token>();
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
			System.out.println("ciao");
			input.reset();

		} catch (IOException e) {
			System.err.println("retrackt fallito");
			e.printStackTrace();
		}

	}
	public Token nextToken()throws Exception{
		
		//Ad ogni chiamata del lexer (nextToken())
		//si resettano tutte le variabili utilizzate
		state = 0;

		String lessema = ""; //� il lessema riconosciuto
        char c;

		
		while(true){

			 // legge un carattere da input e lancia eccezione quando incontra EOF per restituire null
			 //  per indicare che non ci sono pi� token
			input.mark(2);
			if(input.read() == -1){
				return null;
			}
			input.reset();
			input.mark(2);
            c = (char) input.read();
			System.out.print(c);
			//operator rel
			switch(state){
				case 0:
					if(c == '<'){

						state = 1;
						/*input.mark(1);
						if(input.read() == -1){
							return new Token("LT");
						}
						input.reset();
						*/
					}else{
						state = 3;
					}
					break;

				case 1:
					if(c == '-'){
						/*input.mark(1);
						if(input.read() == -1){
							return new Token("ASSIGN");
						}
						input.reset();*/
						state = 6;
					}else{
						state = 2;
					}
					break;

				case 2:
					if(c == '='){
						/*
						input.mark(1);
						if(input.read() == -1){
							return new Token("LE");
						}
						input.reset();
						*/
						return new Token("LE");
					}else{
						retrack();
						return new Token("LT");
					}

				case 3:
					if(c == '='){
						/*
						input.mark(1);
						if(input.read() == -1){
							return new Token("EQ");
						}
						input.reset();*/
						return new Token("EQ");
					}else {
						state = 4;
					}
					break;

				case 4:
					if(c == '>'){
						state = 5;
						/*
						input.mark(1);
						if(input.read() == -1){
							return new Token("GT");
						}
						input.reset();
						*/
					}
					break;

				case 5:
					if(c == '='){
						/*
						input.mark(1);
						if(input.read() == -1){
							return new Token("GE");
						}
						input.reset();
						*/
						return new Token("GE");
					}else{
						retrack();
						return new Token("GT");
					}

				case 6:
					if(c == '-'){
						return new Token("ASSIGN");
					}else{
						return new Token("ERROR");
					}
				default:
					state = 9;
					break;
			}


			//id
			switch(state) {
				case 9:
					if (Character.isLetter(c)) {
						state = 10;
						lessema += c;
						//input.mark(1);
						// Nel caso in cui il file � terminato ma ho letto qualcosa di valido
						// devo lanciare il token (altrimenti perderei l'ultimo token, troncato per l'EOF)
						//if (input.read() == -1) {
						//	return installID(lessema);
						//}
						//input.reset();
						break;
					}else {
						state = 12;
					}
					break;

				case 10:

					if (Character.isLetterOrDigit(c)) {
						lessema += c;

						/*input.mark(1);
						if (input.read() == -1) {
							return installID(lessema);
						}
						input.reset();*/
					}else {
						state = 11;
						retrack();
						return installID(lessema);

					}
					break;

				default:
					state = 12;
					break;
			}//end switch id

			//unsigned numbers
			switch(state){
				case 12:
					if(Character.isDigit(c)){
						state = 13;
						lessema += c;
						/*input.mark(1);
						if(input.read() == -1) {
							return new Token("NUMBER", lessema);
						}
						input.reset();*/
						break;
					}
					break;
                           
                case 13:
					if(Character.isDigit(c)){
						lessema += c;
						/*
						input.mark(1);
						if(input.read() == -1) {
							return new Token("NUMBER", lessema);
						}
						input.reset();
						*/
					}else{
						retrack();
						return new Token("NUMBER", lessema);

					}
				default:
					state = 14;
					break;
			}

			//parentesi tonde e graffe
			switch(state){
				case 14:
					if(c == '('){
						return new Token("LPAR");
					}else{
						state = 15;
					}
					break;

				case 15:
					if(c == ')'){
						return new Token("RPAR");
					}else{
						state = 16;
					}
					break;

				case 16:
					if(c == '{'){
						return new Token("LBRAC");
					} else if (c == '}') {
						return new Token("RBRAC");
					}else{
						state = 17;
					}
					break;

				case 17 :
					if(c == ';'){
						return new Token("SEMI");
					} else if (c == ',') {
						return new Token("COMMA");
					}
					break;
				default:
					state = 18;
					break;

			}
			//mangia gli spazi e ritorno a capo
			switch(state){
				case 18:
					if(c == ' '){
						return new Token("SPACE");
					} else if (c == '\n' || c == '\t') {
						return new Token("SPACE");
					}
					break;
				default:
				break;

			}
			//System.out.println(state);
           }

		}//end while
	//end method

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



