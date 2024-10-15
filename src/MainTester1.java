
public class MainTester1 {

	public static void main(String[] args) {

		LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
		String filePath = args[0];

		if (lexicalAnalyzer.initialize(filePath)) {

			Token token;
			try {
				while ((token = lexicalAnalyzer.nextToken()) != null) {
					System.out.println("<" + token + ">");
				}
			} catch (Exception e) {
				System.out.println("EOF raggiunto");
			}

		} else
			System.out.println("File not found!!");
	}

}
