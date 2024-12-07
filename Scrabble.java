/*
 * RUNI version of the Scrabble game.
 */
public class Scrabble {

	// Note 1: "Class variables", like the five class-level variables declared below,
	// are global variables that can be accessed by any function in the class. It is
	// customary to name class variables using capital letters and underline characters.
	// Note 2: If a variable is declared "final", it is treated as a constant value
	// which is initialized once and cannot be changed later.

	// Dictionary file for this Scrabble game
	static final String WORDS_FILE = "dictionary.txt";

	// The "Scrabble value" of each letter in the English alphabet.
	// 'a' is worth 1 point, 'b' is worth 3 points, ..., z is worth 10 points.
	static final int[] SCRABBLE_LETTER_VALUES = { 1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3,
												  1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10 };

	// Number of random letters dealt at each round of this Scrabble game
	static int HAND_SIZE = 10;

	// Maximum number of possible words in this Scrabble game
	static int MAX_NUMBER_OF_WORDS = 100000;

    // The dictionary array (will contain the words from the dictionary file)
	static String[] DICTIONARY = new String[MAX_NUMBER_OF_WORDS];

	// Actual number of words in the dictionary (set by the init function, below)
	static int NUM_OF_WORDS;

	// Populates the DICTIONARY array with the lowercase version of all the words read
	// from the WORDS_FILE, and sets NUM_OF_WORDS to the number of words read from the file.
	public static void init() {
		// Declares the variable in to refer to an object of type In, and initializes it to represent
		// the stream of characters coming from the given file. Used for reading words from the file.  
		In in = new In(WORDS_FILE);
        System.out.println("Loading word list from file...");
        NUM_OF_WORDS = 0;
		while (!in.isEmpty()) {
			// Reads the next "token" from the file. A token is defined as a string of 
			// non-whitespace characters. Whitespace is either space characters, or  
			// end-of-line characters.
			DICTIONARY[NUM_OF_WORDS++] = in.readString().toLowerCase();
		}
        System.out.println(NUM_OF_WORDS + " words loaded.");
	}

	// Checks if the given word is in the dictionary.
	public static boolean isWordInDictionary(String word) {
		// Using the init() function to get the actual number of words.
		if (NUM_OF_WORDS == 0)
		init();

		word = word.toLowerCase(); // No Instructions given regarding upper case words.
		// Checking if the inputed word is in the dictionary
		for (int i = 0; i < NUM_OF_WORDS; i++) {
			if (DICTIONARY[i].equals(word) && DICTIONARY[i] != null)
			return true;
		}
		return false;
	}
	
	// Returns the Scrabble score of the given word.
	// If the length of the word equals the length of the hand, adds 50 points to the score.
	// If the word includes the sequence "runi", adds 1000 points to the game.
	public static int wordScore(String word) {
		int score = 0;
		// The Following Check assumes, as per the WRITTEN INSTRUCTIONS IN THE HW FILE, that we're not checking for a SEQUENCE,
		// But for the existance of the letters. But god forbid we get actual consistent instructions right?
		boolean [] runiCheck = new boolean[4];
		for (int i = 0; i < word.length(); i++) {
			if (word.charAt(i)  >= 'a' && word.charAt(i) <= 'z')
			score+= SCRABBLE_LETTER_VALUES[word.charAt(i) - 'a'];

			switch (word.charAt(i)) {
				case 'r' :
				runiCheck[0] = true;
				break;
				case 'u' :
				runiCheck[1] = true;
				break;
				case 'n' :
				runiCheck[2] = true;
				break;
				case 'i' :
				runiCheck[3] = true;
				break;
			} 
		}
		score *= word.length();

		if (word.length() == HAND_SIZE)
		score += 50;
		for (int i = 0; i < 4; i++)
		if (runiCheck[i] != true)
		return score;

		return score+1000;
	}

	// Creates a random hand of length (HAND_SIZE - 2) and then inserts
	// into it, at random indexes, the letters 'a' and 'e'
	// (these two vowels make it easier for the user to construct words)
	public static String createHand() {
		// Generates a random string with HAND_SIZE-2 length
		String randomString = MyString.randomStringOfLetters(HAND_SIZE-2);
         // Insert the character at the random index
		 randomString = MyString.insertRandomly('a', randomString);
		 randomString = MyString.insertRandomly('e', randomString);
		return randomString;
	}
	
    // Runs a single hand in a Scrabble game. Each time the user enters a valid word:
    // 1. The letters in the word are removed from the hand, which becomes smaller.
    // 2. The user gets the Scrabble points of the entered word.
    // 3. The user is prompted to enter another word, or '.' to end the hand. 
	public static void playHand(String hand) {
		int n = hand.length(); // I don't know what's the point of this, but this was given to us.
		int score = 0;
		// Declares the variable in to refer to an object of type In, and initializes it to represent
		// the stream of characters coming from the keyboard. Used for reading the user's inputs.   
		In in = new In();
		int printScore;
		while (hand.length() > 0) {
			System.out.println("Current Hand: " + MyString.spacedString(hand));
			System.out.println("Enter a word, or '.' to finish playing this hand:");
			// Reads the next "token" from the keyboard. A token is defined as a string of 
			// non-whitespace characters. Whitespace is either space characters, or  
			// end-of-line characters.
			String input = in.readString();
			if (input.charAt(0) == '.')
			break;
			if (!MyString.subsetOf(input, hand)) {
				System.out.println("Invalid word. Try again.");
				continue;
			}
			if (isWordInDictionary(input)) {
				if (MyString.subsetOf(input, hand)) {
					printScore = wordScore(input);
					score += printScore;
					System.out.println(input+" earned "+printScore+" points. Score: "+score+" points\n");
					hand = MyString.remove(hand, input);
				}
			}
			else
			System.out.println("No such word in the dictionary. Try again.");
		}
		if (hand.length() == 0) {
	        System.out.println("Ran out of letters. Total score: " + score + " points");
		} else {
			System.out.println("End of hand. Total score: " + score + " points");
		}
	}

	// Plays a Scrabble game. Prompts the user to enter 'n' for playing a new hand, or 'e'
	// to end the game. If the user enters any other input, writes an error message.
	public static void playGame() {
		// Initializes the dictionary
    	init();
		// The variable in is set to represent the stream of characters 
		// coming from the keyboard. Used for getting the user's inputs.  
		In in = new In();
		boolean flagE = false;
		while(true) {
			System.out.println("Enter n to deal a new hand, or e to end the game:");
			// Gets the user's input, which is all the characters entered by 
			// the user until the user enter the ENTER character.
			String input = in.readString();
			switch (input) {
				case "n" :
				playHand(createHand());
				break;
				case "e" :
				flagE = true;
				break;
				default :
				System.out.println("INVALID INPUT");
				break;
			}
			if (flagE)
			break;
		}
	}

	public static void main(String[] args) {
		//// Uncomment the test you want to run
		////testBuildingTheDictionary();  
		////testScrabbleScore();    
		////testCreateHands();  
		////testPlayHands();
		playGame();
	}

	public static void testBuildingTheDictionary() {
		init();
		// Prints a few words
		for (int i = 0; i < 5; i++) {
			System.out.println(DICTIONARY[i]);
		}
		System.out.println(isWordInDictionary("mango"));
	}
	
	public static void testScrabbleScore() {
		System.out.println(wordScore("bee"));	
		System.out.println(wordScore("babe"));
		System.out.println(wordScore("friendship"));
		System.out.println(wordScore("running"));
	}
	
	public static void testCreateHands() {
		System.out.println(createHand());
		System.out.println(createHand());
		System.out.println(createHand());
	}
	public static void testPlayHands() {
		init();
		//playHand("ocostrza");
		//playHand("arbffip");
		//playHand("aretiin");
	}
}
