import java.util.InputMismatchException;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

enum	TokenType {
	num,
	operator,
	parenthesis
}

class	Token {
	TokenType	type;
	String	value;
	Token(TokenType type, String value) {
		this.value = value;
		this.type = type;
	}
	public String toString()
	{
		return (type + ": " + value);
	}
}

public class MyProgram
{
	public static void main(String[] args)
	{
		Scanner scanner = new Scanner(System.in);
		CaesarCipher crypto = new CaesarCripher();//TODO
		ExpressionEvaluator calculator = new ExpressionEvaluator();//TODO
		char loop; 
		int choice;

		while (true)
		{
			DisplayMenu();
			choice = scanner_choice.nextInt();
			scanner.nextLine();
			switch (choice) {
				case 1:
					EncryptMe(crypto, scanner);
				case 2:
					DecryptMe();
				case 3:
					CalculateMe();
				case 4:
					System.out.println("Exiting...");
					return ;
				default:
					System.out.println("Invalid choice.");
					System.out.print("Continue? (y/n): ");
					loop = scanner_loop.next().charAt(0);
					if (loop == 'y' || loop == 'Y')
						break ;
					else if (loop == 'n' || loop == 'N')
						System.exit(0);
					else
						System.out.println("Error: unexpected character.");
						System.exit(2);
			}
			System.out.print("Continue? (y/n): ");
			loop = scanner_loop.next().charAt(0);
			if (loop == 'y' || loop == 'Y')
				break ;
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

	private static void EncryptMe()
	{
		Scanner	scanner_type = new Scanner(System.in);
		System.out.print("Where the text? (console/file): ");
		String type = scanner_type.nextLine();
		Scanner scanner_input = new Scanner(System.in);
		String	input = "";
		int	shift;
		if (type.equals("console"))
		{
			System.out.print("Enter text to encrypt: ");
			input = scanner_input.nextLine();
		}
		else if (type.equals("file"))
		{
			System.out.print("Enter the file path: ");
			String path = scanner_input.nextLine();
			try
			{
				input = Files.readString(Paths.get(path));
			}
			catch (IOException e)
			{
				System.out.println("Error reading file: " + e.getMessage());
				return ;
			}
		}
		else
		{
			System.out.println("Error: unexpected direction to the text.");
			System.exit(2);
		}
		Scanner scanner_shift = new Scanner(System.in);
		if (input == null || input.isBlank())
		{
			System.out.println("Error: input text is empty.");
			return ;
		}
		while (true)
		{
			System.out.print("Enter shift value: ");
			try
			{
				shift = scanner_shift.nextInt();
				break ;
			}
			catch (InputMismatchException e)
			{
				System.out.println("Invalid input. Enter an integer for the shift.");
				scanner_shift.nextLine();
			}
		}
		if (!input.endsWith("\n"))
			input += "\n";
		String res = PerformCaesarCipher(input, shift);
		System.out.print("Result: " + res + "\n");
	}

	private static void DecryptMe()
	{
		Scanner	scanner_type = new Scanner(System.in);
		System.out.print("Where the text? (console/file): ");
		String type = scanner_type.nextLine();
		int	shift;
		Scanner scanner_input = new Scanner(System.in);
		String	input = "";
		if (type.equals("console"))
		{
			System.out.print("Enter text to decrypt: ");
			input = scanner_input.nextLine();
		}
		else if (type.equals("file"))
		{
			System.out.print("Enter the file path: ");
			String path = scanner_input.nextLine();
			try
			{
				input = Files.readString(Paths.get(path));
			}
			catch (IOException e)
			{
				System.out.println("Error reading file: " + e.getMessage());
				return ;
			}

		}
		else
		{
			System.out.println("Error: unexpected direction to the text.");
			return ;
		}
		if (input == null || input.isBlank())
		{
			System.out.println("Error: input text is empty.");
			System.exit(2);
		}
		Scanner scanner_is_shift_known = new Scanner(System.in);
		System.out.print("Is shift value known? (y/n): ");
		char is_shift_known = scanner_is_shift_known.next().charAt(0);
		if (is_shift_known == 'y' || is_shift_known == 'Y')
		{
			Scanner scanner_shift = new Scanner(System.in);
			while (true)
			{
				System.out.print("Enter shift value: ");
				try
				{
					shift = scanner_shift.nextInt();
					break ;
				}
				catch (InputMismatchException e)
				{
					System.out.println("Invalid input. Enter an integer for the shift.");
					scanner_shift.nextLine();
				}
			}
			shift *= -1;
		}
		else
			shift = 666;
		String res = PerformCaesarCipher(input, shift);
		System.out.println("Result:\n" + res);
	}

	private static String PerformCaesarCipher(String text, int shift)
	{
		if (shift == 666)
			return (ListAll(text));
		else
			return ProcessStr(text, shift, null);
	}

	private static String ListAll(String text)
	{
		text = text.strip();
		boolean	has_russian = text.matches(".*[А-Яа-яЁё].*");
		boolean has_english = text.matches(".*[A-Za-z].*");
		StringBuilder	res = new StringBuilder();

		if (has_russian)
		{
			res.append("\n=== Possible Russian Decryptions (32 shifts) ===\n"); 
			for (int shift = 1; shift < 33; shift++)
			{
				String decrypted = ProcessStr(text, -shift, true);
				res.append("Shift ").append(shift).append(": ").append(decrypted).append("\n");
			}
		}
		if (has_english)
		{
			res.append("\n=== Possible English Decryptions (25 shifts) ===\n");
			for (int shift = 1; shift < 26; shift++)
			{
				String decrypted = ProcessStr(text, -shift, false);
				res.append("Shift ").append(shift).append(": ").append(decrypted).append("\n");
			}
		}
		if (!has_russian && !has_english)
			res.append(text).append("\n(No Russian or English letters detected)\n");
		return (res.toString());
	}

	private static String ProcessStr(String text, int shift, Boolean is_russian)
	{
		final String upper = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
		final String lower = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
		int	idx;
		char	c;

		StringBuilder	res = new StringBuilder();

		for (int i = 0; i < text.length(); i++)
		{
			c = text.charAt(i);
			idx = upper.indexOf(c);
			if (idx != -1)
			{
				if (is_russian == null || is_russian)
					res.append(upper.charAt((idx + shift + 33) % 33));
				else
					res.append('-');
				continue ;
			}		
			idx = lower.indexOf(c);
			if (idx != -1)
			{
				if (is_russian == null || is_russian)
					res.append(lower.charAt((idx + shift + 33) % 33));
				else
					res.append('-');
				continue ;
			}
			if (Character.isUpperCase(c) && c >= 'A' && c <= 'Z')
			{
				if (is_russian == null || !is_russian)
					res.append((char) ('A' + (c - 'A' + shift + 26) % 26));
				else
					res.append('-');
			}
			else if (Character.isLowerCase(c) && c >= 'a' && c <= 'z')
			{
				if (is_russian == null || !is_russian)
					res.append((char) ('a' + (c - 'a' + shift + 26) % 26));
				else
					res.append('-');
			}
			else
				res.append(c);
		}
		return (res.toString());
	}

	private static void CalculateMe()
	{
		int	res;

		Scanner	scanner_expr = new Scanner(System.in);
		System.out.println("Enter the expression: ");
		String expr = scanner_expr.nextLine();
		List<Token> tokens = get_tokens(expr);
		List<Token> postfix = convertToPostFix(tokens);
		res = processPostfix(postfix);
		System.out.println(res);
	}

	private static List<Token> convertToPostFix(List<Token> tokens)
	{
		List<Token> stack = new ArrayList<>();
		for (Token node : tokens)
		{
			if (token.type == TokenType.num)
				stack.push(
		}
	}

	private static List<Token> get_tokens(String expr)
	{
		int	i = 0;
		char	c;
		List<Token>	tokens = new ArrayList<>();

		while (i < expr.length())
		{
			c = expr.charAt(i);
			if (Character.isWhitespace(c))
			{
				i++;
				continue ;
			}
			if (Character.isDigit(c) || c == '.')
			{
				StringBuilder	num = new StringBuilder();
				int	dots = 0;
				while (i < expr.length()
					&& (Character.isDigit(expr.charAt(i))
					|| expr.charAt(i) == '.'))
				{
					if (expr.charAt(i) == '.')
					{
						dots++;
						if (dots > 1)
							throw new RuntimeException("Too many dots");
					}
					num.append(expr.charAt(i));
					i++;
				}
				tokens.add(new Token(TokenType.num, num.toString()));
				continue ;
			}
			if (c == '(' || c == ')')
			{
				tokens.add(new Token(TokenType.parenthesis, Character.toString(c)));
				i++;
				continue ;
			}
			if (c == '-' || c == '+' || c == '/' || c == '*' || c == '^')
			{
				tokens.add(new Token(TokenType.operator, Character.toString(c)));
				i++;
				continue ;
			}
			throw new RuntimeException("Invalid character: " + c);
		}
		return (tokens);
	}
}
