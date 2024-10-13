import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class lexer {
	
	private BufferedReader input;
	private static HashMap <String, Token> keywordTable;  // la struttura dati potrebbe essere una hash map
	private HashMap<String,Token> symbolTable;// la symbol table in questo caso si riduce ad una semplice stringTable
	private int state;

	public lexer(){
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
	
	public Boolean initialize(String filePath){
	
	   // prepara file input per lettura e controlla errori
		try {
			input = new BufferedReader(new FileReader(filePath));
			return true;
		}catch (IOException e){
			System.err.println("File con path: "+filePath+ " non trovato");
			return false;
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
			
            c = (char) input.read();
			
			//operator rel
			switch(state){
				case 0:
					if(c == '<'){
						state = 1;
						input.mark(1);
						if(input.read() == -1){
							return new Token("LT");
						}
					}
					break;

				case 1:
					if(c == '-'){
						input.mark(1);
						if(input.read() == -1){
							return new Token("ASSIGN");
						}
						return new Token("ASSIGN");
					}
					state = 2;
					break;

				case 2:
					if(c == '='){
						input.mark(1);
						if(input.read() == -1){
							return new Token("LE");
						}
						return new Token("LE");
					}else{
						retrack();
						return new Token("LT");
					}

				case 3:
					if(c == '='){
						input.mark(1);
						if(input.read() == -1){
							return new Token("EQ");
						}
						return new Token("EQ");
					}
					break;

				case 4:
					if(c == '>'){
						state = 5;
						input.mark(1);
						if(input.read() == -1){
							return new Token("GT");
						}
						break;
					}

				case 5:
					if(c == '='){
						input.mark(1);
						if(input.read() == -1){
							return new Token("GE");
						}
						return new Token("GE");
					}else{
						retrack();
						return new Token("GT");
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
						input.mark(1);
						// Nel caso in cui il file � terminato ma ho letto qualcosa di valido
						// devo lanciare il token (altrimenti perderei l'ultimo token, troncato per l'EOF)
						if (input.read() == -1) {
							return installID(lessema);
						}
						break;
					}
					state = 12;
					break;

				case 10:
					if (Character.isLetterOrDigit(c)) {
						lessema += c;
						input.mark(1);
						if (input.read() == -1) {
							return installID(lessema);
						}
					}else {
						state = 11;
						retrack();
						return installID(lessema);

					}
					break;

				default:break;
			}//end switch id

			//unsigned numbers
			switch(state){
				case 12:
					if(Character.isDigit(c)){
						state = 13;
						lessema += c;
						input.mark(1);
						if(input.read() == -1) {
							return new Token("NUMBER", lessema);
						}
						break;
					}
					state = 22;
					break;
                           
                case 13:
					if(Character.isDigit(c)){
						lessema += c;
						input.mark(1);
						if(input.read() == -1) {

						}
					}else{
						retrack();
						return new Token("NUMBER", lessema);

					}
			}
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
	

private void retrack() {
	// fa il retract nel file di un carattere
}
}



