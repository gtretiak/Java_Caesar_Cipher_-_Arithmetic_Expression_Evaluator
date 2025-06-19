import java.util.InputMismatchException; // for handling mismatched data type with user input
import java.util.Scanner; // for scanner usage and reading from user
import java.nio.file.Files; // for reading from a file
import java.nio.file.Paths; // for creating a path from a given string
import java.nio.file.Path; // the the path representing
import java.io.IOException; // for handling input/output exceptions
import java.util.List; // for list usage
import java.util.Deque; // for using Deque
import java.util.ArrayDeque; // for using resizable deque
import java.util.ArrayList; // for using resizable lists

class ExpressionEvaluator // class for calculations
{
	private enum	TokenType { number, operator, parenthesis }

	private static class	Token
	{
		TokenType	type; // either a number, an operator or parenthesis
		String	value; 
		Token(TokenType type, String value)
		{
			this.value = value;
			this.type = type;
		}
		@Override
		public String toString()
		{
			return (type + ": " + value);
		}
	}
	
	public double evaluate(String expr)
	{
		List<Token> tokens = get_tokens(expr); // we create tokens from the user input
		List<Token> postfix = convert_to_postfix(tokens); // we convert it into postfix notation
		return evaluate_postfix(postfix); // we evaluate it
	}
		
	private List<Token> get_tokens(String expr)
	{
		int	i = 0;
		char	c;
		List<Token>	tokens = new ArrayList<>(); // A resizable list creation

		while (i < expr.length())
		{
			c = expr.charAt(i); // Char-by-char iteration
			if (Character.isWhitespace(c)) // Skip if the current char is whitespace
			{
				i++;
				continue ;
			}
			if (Character.isDigit(c) || c == '.' || c == '-') // Digits, dots and signes
									  // represent numbers
				i = processNumber(expr, i, tokens);
			else if (c == '(' || c == ')')
			{
				tokens.add(new Token(TokenType.parenthesis, Character.toString(c)));
				i++;
				continue ; // Similarly we append '(' or ')' char to Token.value
			}
			else if (c == '-' || c == '+' || c == '/' || c == '*')
			{
				tokens.add(new Token(TokenType.operator, Character.toString(c)));
				i++;
				continue ; // This time we append an operator and go to the next iter
			}
			else
				throw new RuntimeException("Invalid character: " + c);// some unexpected char
		}
		return (tokens);
	}
	
	private int processNumber(String expr, int i, List<Token> tokens)
	{
		char	c = expr.charAt(i);
		boolean	is_negative;
		if (c == '-')
			is_negative = true;
		else
			is_negative = false;
		if (is_negative) {
			if (tokens.isEmpty()
				|| tokens.get(tokens.size() - 1).type == TokenType.operator
				|| tokens.get(tokens.size() - 1).value.equals("("))
			{} // we do nothing here, since it's a number
			else {// otherwise we append '-' which is most likely an operator
				tokens.add(new Token(TokenType.operator, "-"));
				i++;
				return (i); // we append the token and go to the next one
			}
		}
		StringBuilder	num = new StringBuilder();
		if (is_negative) {// We start with '-' if it's a negative number
			num.append('-');
			i++;
		}
		int	dots = 0;
		while (i < expr.length()
			&& (Character.isDigit(expr.charAt(i))
			|| expr.charAt(i) == '.')) {
			if (expr.charAt(i) == '.') {
				dots++;
				if (dots > 1) // A number might have at max one dot
					throw new RuntimeException("Too many dots");
			}
			num.append(expr.charAt(i));
			i++;
		}
		tokens.add(new Token(TokenType.number, num.toString()));
		// we append the built string-token and go to the next one
		return (i);
	}

	private List<Token> convert_to_postfix(List<Token> tokens)
	{
		List<Token>	postfix = new ArrayList<>();
		Deque<Token>	stack = new ArrayDeque<>(); //A queue to move to the postfix sequence
		
		for (Token current : tokens) // For every token in tokens sequence...
		{
			switch (current.type){ // Depending on the type...
			case number:
				postfix.add(current); // Just add if it's a number
				break ; // Go to the next token
			case operator:
				handleOperator(current, postfix, stack);
				break ;
			case parenthesis:
				handleParenthesis(current, postfix, stack);
				break ;
			}
		}
		// Tokens are over. Now it's time to finish with the rest in the stack
		while (!stack.isEmpty())
		{
			if (stack.peek().value.equals("(")) // which means we found unexpected '('
				throw new IllegalArgumentException("Mismatched parentheses");
			postfix.add(stack.pop()); // we add one-by-one from the stack to the sequence
		}
		return (postfix);
	}

	private void handleOperator(Token curr, List<Token> postfix, Deque<Token> stack)
	{
		while (!stack.isEmpty() // we work with the stack until it's empty
					// and while what's on the stack top 
					// has the priority on the current token
			&& is_higher_presedence(stack.peek().value, curr.value))
			postfix.add(stack.pop()); // if so, we add it to the sequence
		stack.push(curr); // and then push the current token into the stack
	}

	private void handleParenthesis(Token curr, List<Token> postfix, Deque<Token> stack)
	{
		if (curr.value.equals("("))
			stack.push(curr); // push opening bracket to the stack
		else // current.value.equals(")")
		{
			while (!stack.isEmpty() // Searching for the opening bracket
				&& !stack.peek().value.equals("("))
				postfix.add(stack.pop());
			if (stack.isEmpty()) // which means we didn't find '('
				throw new IllegalArgumentException("Mismatched parentheses");
			stack.pop(); // if all right, we remove '(' once we handle it
		}
	}

	private boolean is_higher_presedence(String stack_op, String token_op)
	{
		int	stack_presedence = get_presedence(stack_op);
		int	token_presedence = get_presedence(token_op);
		return (stack_presedence >= token_presedence); // if stack has a privilege we push it
	}

	private int get_presedence(String s) // for both top stack's value and current token's value
					     // we determine the presedence depending on the operator
	{
		switch (s) {
			case "+", "-":
				return 1;
			case "*", "/":
				return 2;
			default:
				return 0;
		}
	}

	private double evaluate_postfix(List<Token> postfix)
	{
		Deque<Double>	stack = new ArrayDeque<>(); // Double for precision
		for (Token current : postfix) // for every token in the postfix sequence
		{ // now we have only numbers and operators (no parentheses)
			if (current.type == TokenType.number) // if it's a number, we push it in stack
				stack.push(Double.parseDouble(current.value));// casting to double
			else {// which means it's an operator, so we assign numbers to compute:
				double right = stack.pop(); // we pick up them from the stack
				double left = stack.pop();
				switch (current.value) {
					// Here we replace two previous numbers with computed one:
				case "+":
					stack.push(left + right);
					break ;
				case "-":
					stack.push(left - right);
					break ;
				case "*":
					stack.push(left * right);
					break ;
				case "/":
					if (right == 0)
						throw new ArithmeticException("Division by zero!");
					stack.push(left / right);
					break ;
				}
			}
		}
		if (stack.size() != 1) // we still have something to compute in the stack,
				       // but something is missing (either an operator or a number)
			throw new IllegalArgumentException("Invalid expression.");
		return stack.pop(); // we return the only number that rest in the stack
	}
}

class CaesarCipher // class for encryption/decryption
{
	public String getInput(Scanner scanner, String operation) // First we take the text, all the output is self-explanatory
	{
		System.out.print("Where the text? (console/file): ");
		String type = scanner.nextLine();
		String	input;
		if (type.equals("console"))
		{
			System.out.print("Enter text to " + operation + ": ");
			input = scanner.nextLine();
		}
		else if (type.equals("file"))
		{
			System.out.print("Enter file path: ");
			String path = scanner.nextLine();
			try
			{
				input = Files.readString(Paths.get(path));// we try to read from the file
			}
			catch (IOException e)
			{
				System.out.println("Error reading file: " + e.getMessage());
				return "";
			}
		}
		else
		{
			System.out.println("Error: invalid input source.");
			return "";
		}
		if (input == null || input.isBlank()) // if there is nothing to encrypt/decrypt
			throw new IllegalArgumentException("Error: input text is empty.");
		return (input);
	}
	
	public int getShift(Scanner scanner) // Here we take a shift value (the workflow is also self-explanatory)
	{
		int	shift;
		while (true)
		{
			System.out.print("Enter shift value: ");
			try
			{
				shift = scanner.nextInt();
				break ;
			}
			catch (InputMismatchException e)
			{
				System.out.println("Invalid input. Enter an integer for the shift.");
				scanner.nextLine();//consuming the newline
			}
		}
		return (shift);
	}

	public String process(String text, int shift, Boolean is_russian) // the actual meat
	{
		final String upper = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"; // we need both strings to handle Russian, because Russian letters are not consecutive
		final String lower = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
		int	idx;
		char	c;
		StringBuilder	res = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			c = text.charAt(i);
			idx = upper.indexOf(c); // we get the index of the matching char OR -1
			if (idx != -1) { // The char matches "upper" russian string
				if (is_russian == null || is_russian)
					res.append(upper.charAt((idx + shift + 33) % 33));
				else
					res.append('-'); // we omit english characters
				continue ;
			}		
			idx = lower.indexOf(c);
			if (idx != -1) { // The car matches "lower" russian string
				if (is_russian == null || is_russian)
					res.append(lower.charAt((idx + shift + 33) % 33));
				else
					res.append('-'); // we omit english characters
				continue ;
			}
			if (Character.isUpperCase(c) && c >= 'A' && c <= 'Z') {
				if (is_russian == null || !is_russian)
					res.append((char) ('A' + (c - 'A' + shift + 26) % 26));
				else
					res.append('-'); // we omit russian characters
			}
			else if (Character.isLowerCase(c) && c >= 'a' && c <= 'z') {
				if (is_russian == null || !is_russian)
					res.append((char) ('a' + (c - 'a' + shift + 26) % 26));
				else
					res.append('-'); // we omit russian characters
			}
			else // we append any other character as is
				res.append(c);
		}
		return (res.toString());
	}
	
	public String listAll(String text)
	{
		text = text.strip();
		boolean	has_russian = text.matches(".*[А-Яа-яЁё].*"); // we know that the text has russian characters
		boolean has_english = text.matches(".*[A-Za-z].*"); // we know that the text has english characters
		StringBuilder	res = new StringBuilder();

		if (has_russian)
		{
			res.append("\n=== Possible Russian Decryptions (32 shifts) ===\n"); 
			for (int shift = 1; shift < 33; shift++) // Russian alphabet has 33 letters
			{
				String decrypted = process(text, -shift, true); // call "process" with "true"
				res.append("Shift ").append(shift).append(": ").append(decrypted).append("\n");
			}
		}
		if (has_english)
		{
			res.append("\n=== Possible English Decryptions (25 shifts) ===\n");
			for (int shift = 1; shift < 26; shift++) // English alphabet has 26 letters
			{
				String decrypted = process(text, -shift, false); // call "process" with "false"
				res.append("Shift ").append(shift).append(": ").append(decrypted).append("\n");
			}
		}
		if (!has_russian && !has_english) // Nothing to decrypt
			res.append(text).append("\n(No Russian or English letters detected)\n");
		return (res.toString());
	}
}

public class MyProgram
{
	public static void main(String[] args)
	{
		Scanner scanner = new Scanner(System.in); // The tool to scan an user input
		ExpressionEvaluator calculator = new ExpressionEvaluator();// building the object of the class ExprEval
		CaesarCipher crypto = new CaesarCipher(); // building the object of the class CaesarCipher
		char loop; 
		int choice;

		while (true)
		{
			DisplayMenu();
			choice = scanner.nextInt();
			scanner.nextLine(); // consuming newline after presssing "Enter"
			switch (choice) {
				case 1:
					EncryptMe(crypto, scanner);
					break ;
				case 2:
					DecryptMe(crypto, scanner);
					break ;
				case 3:
					CalculateMe(calculator, scanner);
					break ;
				case 4:
					System.out.println("Exiting...");
					return ;
				default:
					System.out.println("Invalid choice."); // undefined option
					break ;
			}
			System.out.print("Continue? (y/n): ");
			loop = scanner.next().charAt(0);
			if (loop == 'y' || loop == 'Y')
				continue ;
			else if (loop == 'n' || loop == 'N')
				System.exit(0);
			else
				System.out.println("Error: unexpected character.");
				System.exit(2);
		}
	}

	private static void DisplayMenu()
	{
		System.out.println(
			"Please choose an option:\n" +
			"1. Caesar Cipher Encryption\n" +
			"2. Caesar Cipher Decryption\n" +
			"3. Arithmetic Expression Evaluation\n" +
			"4. Exit\n");
		System.out.print("Enter your choice: ");
	}

	private static void EncryptMe(CaesarCipher crypto, Scanner scanner)
	{
		String input = crypto.getInput(scanner, "encrypt"); // we run the method from the class with "code" string
		int shift = crypto.getShift(scanner); // we run the method from the class
		if (!input.endsWith("\n")) // for handling "console" option
			input += "\n";
		System.out.println("Result: " + crypto.process(input, shift, null)); // we run the method from the class
	}

	private static void DecryptMe(CaesarCipher crypto, Scanner scanner) // Similar logic, except the shift value might be unknown
	{
		String input = crypto.getInput(scanner, "decrypt");
		System.out.print("Is shift value known? (y/n): ");
		char is_shift_known = scanner.next().charAt(0);
		if (is_shift_known == 'y' || is_shift_known == 'Y')
		{
			int shift = -crypto.getShift(scanner); //Negative for decryption
			System.out.println("Result: " + crypto.process(input, shift, null));
		}
		else
			System.out.println("Result:\n" + crypto.listAll(input)); // shift value is unknown, so let's print all the possible options
	}
	
	private static void CalculateMe(ExpressionEvaluator calculator, Scanner scanner)
	{
		System.out.println("Enter the expression: ");
		String expr = scanner.nextLine();
		try
		{
			System.out.println("Result: " + calculator.evaluate(expr));
		}
		catch (Exception e) // exception handling if something is wrong
		{
			System.out.println("Error: " + e.getMessage());
		}
	}
}
