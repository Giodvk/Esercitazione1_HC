
public class Tester {

	public static void main(String[] args) {

		LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
		String filePath = "C:\\Users\\giova\\IdeaProjects\\Esercitazione1_HC\\src\\test1.txt";

		if (lexicalAnalyzer.initialize(filePath)) {

			Token token;
			try {
				while ((token = lexicalAnalyzer.nextToken()) != null) {
					System.out.println("<" + token + ">");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else
			System.out.println("File not found!!");
	}

}
