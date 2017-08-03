//Title: Project 2
//Files: Code.java, Project2.java
//Semester: (CS302) Summmer 2017

//Author: Collin Patteson, Joong Ho Kim - Partner Pair


import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

//import Project2.Color;


enum Color{EMPTY,RED,ORANGE,YELLOW,GREEN,BLUE,VIOLET}

/**
 * Contains the main for Mastermind.
 * @author Collin Patteson, Joong Ho Kim
 * 
 */
public class Project2 {


	/**
	 * Seed for random.
	 */
	public final static int SEED = 321;
	/**
	 * Number of trials for the AI to undergo.
	 */
	public final static int TRIALS = 10000;
	public final static double TOLERANCE = 0.00001;

	public final static int TURNSTOPLAY = 10;

	static ArrayList<Color> curPossibleColors;
	static ArrayList<Color> notPossibleColors;

	public static Scanner scnr = new Scanner(System.in);
	public static Scanner entryScnr;
	public static Random ranGen = new Random(SEED);

	/**
	 * Person who must break the Code: Player0
	 */
	private static String codeBreaker; 	//Player0
	/**
	 * Person who must make the Code: Player1
	 */
	private static String codeMaker;		//Player1
	private static String winner;

	private static ArrayList<Code> possibleCodes;

	private static boolean[] isAI = new boolean[2];
	private static boolean isAIGame;
	@SuppressWarnings("unused")
	private static boolean isHumanGame;
	private static boolean isDone;
	private static boolean isWinner;

	private static boolean isLog;
	//private static boolean isDebug;
	private static int numBlack;
	private static int numWhite;
	private static long totTurns;
	private static int numTurn;
	private static int numGame;

	private static Code guessCode;
	private static Code actualCode;

	//public int

	public static void main(String[] args) {
		isDone = false;
		numGame = 1;

		/*			//All the dumb tests.
		Color[] s1 = {Color.BLUE,Color.RED, Color.YELLOW, Color.VIOLET, };
		Color[] s2 = {Color.RED, Color.BLUE, Color.VIOLET, Color.YELLOW};
		Code bob = new Code(s1);//Project2.randColors();
		Code lob = new Code(s2);//Project2.randColors();
		System.out.println(bob.toString() + lob.toString());
		System.out.println(bob.pegs(lob));
				Project2.allOpts();							//Code gen tests.
		//		System.out.println(Project2.randColors());
		//		System.out.println(Project2.allSame());
		//		System.out.println(Project2.allDiff());
		//		System.out.println(Project2.twoDubs());
		 */
		//System.out.println(allOpts());
		//allOpts();
		initPlayers();
		while(!isDone){
			initGame();
			while(!isWinner){
				turn();
			}
			numTurn--;
			totTurns +=numTurn;
			numGame++;

			if(checkContinue()){
				isDone = false;
			}
			else{
				isDone = true;
			}
		}
		double avgTurns = (double) totTurns/(numGame-1);
		System.out.println("\nThe game turn length was: " + avgTurns);

	}
	/**
	 * Initializes players for the game by requesting input via console. Will keep requesting an input until valid input is given.
	 */
	private static void initPlayers(){

		for(int i = 0; i<2; i++){
			for(boolean isValidInput=false;!isValidInput;){
				try{

					if (i == 0){
						System.out.println("Enter a name for the CodeBreaker.");	
					}
					else{
						System.out.println("Enter a name for the CodeMaker.");
					}

					{
						String input = scnr.nextLine();
						if(input.length()<1||input==null){
							//throw new InvalidInputException("You must input a name.");
							throw new Exception("You must input a name.");
						}
						if(input.contains("AI ")){
							isAI[i] = true;
						}
						else{
							isAI[i] = false;
						}
						if(i == 0){
							codeBreaker = input;
						}
						else{
							codeMaker = input;
							if(codeMaker.equals(codeBreaker)){
								System.out.println("Choose different name please.");
								i--;
							}
						}

						isValidInput = true;
					}

				}

				catch(NullPointerException e){
					//System.out.println("You must input a name.");
				}
				//				catch(InvalidInputException e){
				//					System.out.println(e.getMessage());
				//				}
				catch(Exception e){
					//System.out.println("Something went wrong in initPlayers().");
					//System.out.println(e.getMessage());
				}
			}
		}

	}

	private static void initGame(){
		possibleCodes = allOpts();
		if(isAI[0]&&isAI[1]){
			isAIGame = true;
			isLog = false;
			isHumanGame = false;
		}
		else if(!(isAI[0]&&isAI[1])){
			isHumanGame = true;
			isLog = true;
			isAIGame = false;
		}
		else{
			isHumanGame = false;
			isLog = true;
			isAIGame = false;
		}
		curPossibleColors = new ArrayList<Color>();
		notPossibleColors = new ArrayList<Color>();
		for(int i = 0; i<Color.values().length;i++){
			curPossibleColors.add(Color.values()[i]);
		}
		numTurn = 1;
		isWinner = false;
		winner = null;

		if(isAIGame && (numGame == TRIALS)){
			isLog = true;
		}
		//if(isLog){
		System.out.println("Playing Game#" + numGame);
		System.out.println();
		//}
		if(isAI[1]){
			if(isLog){
				System.out.println(codeMaker + " has made a code for " + codeBreaker + " to break.");
			}
			actualCode = genDoubleRandCode();
		}
		if(!isAI[1]){
			if(isLog){
				System.out.println(codeMaker + " should mentally think of a code for " + codeBreaker + " to break.");
			}
			actualCode = null;
		}

	}

	/**
	 * Generates a code for a human or AI to guess.
	 * @return Code
	 */
	private static Code genDoubleRandCode() {
		int ranGenType = ranGen.nextInt(4);
		switch (ranGenType){
		case 0: return randColors();
		case 1: return allDiff();
		case 2: return allSame();
		case 3: return twoDubs();
		default: return randColors();
		}
	}
	/**
	 * Does the logic for game turns.
	 */
	private static void turn(){
		if(!checkWin()){
			int blackPeg = 0;
			int whitePeg = 0;
			guessCode = null;
			if(isLog){
				System.out.println("\nThis is Game#"+numGame+"; Turn#"+numTurn + "\n");
				
				if(isAIGame){
					System.out.println("The actualCode is: " + actualCode);
				}
				if(!isAI[0])
					System.out.println("CodeBreaker, enter exactly "+Code.SLOTLENGTH+" colors.");
			}
			{//Actual guess logic
				if(!isAI[0]){
					guessCode = inputColors(); //inputColors();
					//					if(!isAI[1]){
					//						System.out.println(codeBreaker+ " has guessed " +guessCode);
					//					}

					//					if(isAI[1]){
					//						if(isLog){
					//							System.out.println("Black Pegs: " +actualCode.pegs(guessCode)/10 + " White Pegs: " +actualCode.pegs(guessCode)%10);
					//						}
					//					}
					if(!isAI[1]){
						//System.out.println(codeMaker + " will now tell how many black pegs and white pegs there are.");
					}
				}
				else{
					guessCode = pickMove();
					//					if(isLog){
					//						System.out.println(codeBreaker + " entered " + guessCode);
					//					}
				}
				if(guessCode == null){
					isWinner = true;
				}
				else{
					if(isAI[1]&&!isAI[0]){
						blackPeg = actualCode.pegs(guessCode)/10;
						whitePeg = actualCode.pegs(guessCode)%10;
					}
					else if(!isAI[1]){
						blackPeg = numBlack;
						whitePeg = numWhite;
					}
					else{
						blackPeg = actualCode.pegs(guessCode)/10;
						whitePeg = actualCode.pegs(guessCode)%10;
					}


					if(isLog){
						System.out.println(codeBreaker + " has guessed " + guessCode +", and " + codeMaker + " declares: Black = " +
								blackPeg + " White = " + whitePeg);

					}
					if(blackPeg == Code.SLOTLENGTH){
						isWinner = true;
						winner = codeBreaker;
						if(isLog){
							System.out.println(winner + " has broken the code of " + codeMaker + " on Turn#" + numTurn);
						}
					}
				}
			}
			numTurn++;
		}








	}
	/**
	 * 
	 * @return Random code from list of available codes.
	 */
	private static Code pickMove() {

//		System.out.println(possibleCodes.size());
		int inPegs = 0;
		if(possibleCodes.size()<1){
			if(isLog){
				System.out.println(codeBreaker + " cannot make guess. Inconsistant peg input. Terminating game.");
			}
			return null;
		}
		else if(possibleCodes.size() == 1){
			guessCode = possibleCodes.get(0);
			if(isLog){
				System.out.println(codeBreaker + " entered " + guessCode);
			}
			return guessCode;
		}


		if(guessCode!=null){
			if(isLog){
				System.out.println(codeBreaker + " entered " + guessCode);
			}
		}


		if(isAI[1]){
			if(Color.values().length<=Code.SLOTLENGTH){
				int randColorIndex = ranGen.nextInt(curPossibleColors.size());
				Color[] randSameColor = {Color.values()[randColorIndex],Color.values()[randColorIndex],Color.values()[randColorIndex],Color.values()[randColorIndex]};
				guessCode = new Code(randSameColor);
			}
			else{
				guessCode = possibleCodes.get(ranGen.nextInt(possibleCodes.size()));
			}
			if(isLog){
				System.out.println(codeBreaker + " entered " + guessCode);
			}
			inPegs = actualCode.pegs(guessCode);
		}
		else{
			guessCode = possibleCodes.get(ranGen.nextInt(possibleCodes.size()));
			System.out.println(codeBreaker + " entered " + guessCode);
			inPegs = inputPegs();
		}
		numBlack = inPegs/10;
		numWhite = inPegs%10;
//		if(isLog){
//			System.out.println("Cur: " + curPossibleColors);
//		}

		if(numWhite==0&&numBlack==0){

			for(int u = 0; u<Code.SLOTLENGTH;u++){
				Color tempColor = guessCode.getSlots()[u];

//				System.out.println(tempColor);
				if(curPossibleColors.contains(tempColor)){
					curPossibleColors.remove(tempColor);
					notPossibleColors.add(tempColor);
					u--;
				}
			}
			if(possibleCodes.size()==1){
				return possibleCodes.get(0);
			}
			for(int num = 0; num < 50; num++){
				for(int c = 0; c<possibleCodes.size();c++){
					for(int i = 0; i<Code.SLOTLENGTH;i++){
						if(notPossibleColors.contains((possibleCodes.get(c).getSlots()[i]))){
							possibleCodes.remove(c);
							if(c == possibleCodes.size()&&possibleCodes.size()!=0){
								c--;
							}
						}
					}
				}
			}
		}

//		else if(numWhite == 0){
//			for(int num = 0; num < 50; num++){
//				for(int c = 0; c<possibleCodes.size();c++){
//					for(int i = 0; i<Code.SLOTLENGTH;i++){
//						if(!curPossibleColors.contains(possibleCodes.get(c).getSlots()[i])){
//							if(possibleCodes.size()==1){
//								return possibleCodes.get(0);
//							}
//
//							possibleCodes.remove(c);
//							if(c == possibleCodes.size()&&possibleCodes.size()!=0){
//								c--;
//							}
//						}
//					}
//				}
//			}
//		}
		else{
			for(int c = 0; c<possibleCodes.size();c++){
				if(possibleCodes.get(c).pegs(guessCode)/10!=numBlack&&possibleCodes.get(c).pegs(guessCode)%10!=numWhite){
					if(possibleCodes.size()==1){
						return possibleCodes.get(0);
					}
					possibleCodes.remove(c);
					if(c == possibleCodes.size()&&possibleCodes.size()!=0){
						c--;
					}
				}
			}
		}
		possibleCodes.remove(guessCode);
		//System.out.println(possCodes.size());
		return guessCode;
	}
	private static int inputPegs() {
		int inBlack = 0;
		int inWhite = 0;
		if(isLog){
			System.out.println("CodeMaker, enter the number of black (exact matches) pegs: ");
		}
		for(boolean isValidInput=false; !isValidInput; ){
			try{
				if(scnr.hasNextLine()){
					inBlack = Integer.parseInt(scnr.nextLine());
					isValidInput = true;
				}
				else{
					throw new Exception("That is not a valid number of pegs, try again.");
				}
			}
			catch(NumberFormatException e){
				System.out.println("That is not a valid number of pegs, try again.");
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
		if(isLog){
			System.out.println("CodeMaker, enter the number of white (wrong position) pegs: ");
		}
		for(boolean isValidInput=false; !isValidInput; ){
			try{
				if(scnr.hasNextLine()){
					inWhite = Integer.parseInt(scnr.nextLine());
					isValidInput = true;
				}
				else{
					throw new Exception("That is not a valid number of pegs, try again.");
				}
			}
			catch(NumberFormatException e){
				System.out.println("That is not a valid number of pegs, try again.");
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
		int outPegs = Integer.parseInt(Integer.toString(inBlack)+Integer.toString(inWhite));
		return outPegs;
	}
	private static Code inputColors() {

		for(boolean isValidInput = false;!isValidInput;){
			try{
				String inString = scnr.nextLine();
				if(inString.length()<1||inString ==null){
					throw new Exception("You must enter exactly " + Code.SLOTLENGTH + " colors, try again.");
				}
				entryScnr = new Scanner (inString);
				try{
					String compString = "";
					while(entryScnr.hasNext()){
						String addString = entryScnr.next();
						if(!checkIsColor(addString)){
							throw new Exception("You must enter exactly " + Code.SLOTLENGTH + " colors, try again.");
						}
						compString+=Character.toLowerCase(addString.toCharArray()[0]);

					}
					if(compString.length()<Code.SLOTLENGTH){
						throw new Exception("You must enter exactly " + Code.SLOTLENGTH + " colors, try again.");
					}
					else if(compString.length()>Code.SLOTLENGTH){
						throw new Exception("You must enter exactly " + Code.SLOTLENGTH + " colors, try again.");
					}
					Color[] outColors = new Color[Code.SLOTLENGTH];
					for(int i = 0; i<compString.length();i++){
						Color test = Project2.toColorValueFromChar(compString.toCharArray()[i]);
						//						System.out.println(test);
						//						System.out.println(compString);
						outColors[i]= test;

					}
					isValidInput = true;
					return new Code(outColors);

				}
				catch(NoSuchElementException e){
					System.out.println("You must enter exactly " + Code.SLOTLENGTH + " colors, try again.");;
				}
				catch(Exception e){
					System.out.println(e.getMessage());
				}

			}
			catch(NoSuchElementException e){
				if(isLog){
					e.printStackTrace();
				}
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
		}

		return null;
	}
	private static boolean checkWin(){
		if(winner==null){
			if(guessCode!=null){
				if(isAI[1]&&guessCode.equals(actualCode)){
					winner = codeBreaker;
					isWinner = true;
					if(isLog){
						System.out.println(codeBreaker + " has broken the code on turn #" + Integer.toString(numTurn-1));
					}
					return isWinner;
				}
				else if(numTurn == TURNSTOPLAY+1){
					winner = codeMaker;
					isWinner = true;
					if(isLog){
						System.out.println(codeBreaker + " has failed to guess the code within the " + TURNSTOPLAY + " turn limit.");
						if(isAI[1]){
							System.out.println("The solution was " + actualCode);
						}
						else if(!isAI[0]){
							System.out.println(codeMaker+" will now verbally tell the solution to " + codeBreaker);
						}
					}
					return isWinner;
				}
				return false;
			}
		}
		return false;
	}
	/**
	 * Prompts user for yes or no input and returns true if they enter yes, and false if they enter anything but yes.
	 * @return boolean
	 */
	private static boolean checkContinue(){
		if(isAIGame&&(numGame<=TRIALS)){
			return true;
		}
		else if(isAIGame){
			return false;
		}
		else{
			for(boolean isValidInput = false;!isValidInput;){
				try{
					String input;
					System.out.println("Would you like to play again? (Y/N)");
					input = scnr.nextLine();
					if(input.length()<1||input == null){
						throw new Exception("Input 'Y' or 'y' to continue or anything else to stop.");
					}
					else if(Character.toLowerCase(input.charAt(0))=='y'){
						isValidInput = true;//Unnecessary but prevents stupidity later perhaps.
						return true;
					}
					else{
						isValidInput = true;
						return false;
					}
				}
				catch(Exception e){
					System.out.println(e.getMessage());
				}
				//				catch(Exception e){
				//					System.out.println("Something went wrong in checkContinue().");
				//				}
			}
		}

		return false;
	}


	/**
	 * Generates an entirely random Code of colours up to the length of SLOTLENGTH.
	 * @return Code
	 * 
	 */
	private static Code randColors(){
		Color[] outColours = new Color[Code.SLOTLENGTH];
		for(int i = 0; i < Code.SLOTLENGTH; i++){
			outColours[i] = Color.values()[ranGen.nextInt(Color.values().length)];
		}
		Code outCode = new Code(outColours);
		return outCode;
	}
	/**
	 * Generates a random Code of non-repeating colours up to the length of SLOTLENGTH.
	 * @return Code
	 */
	private static Code allDiff(){
		ArrayList <Color> usedColors = new ArrayList <Color>();
		Color[] outColours = new Color[Code.SLOTLENGTH];
		for(int i = 0; i < Code.SLOTLENGTH; i++){
			for(boolean isValidColorAdded = false; !isValidColorAdded;){
				Color testColor = Color.values()[ranGen.nextInt(Color.values().length)];
				if(!usedColors.contains(testColor)){
					usedColors.add(testColor);
					outColours[i] = testColor;	
					isValidColorAdded = true;
				}
			}

		}
		Code outCode = new Code(outColours);
		return outCode;
	}
	/**
	 * Generates a Code containing all the same colour with a number slots up to SLOTLENGTH.
	 * @return Code
	 */
	private static Code allSame(){
		Color theChosenLad = Color.values()[ranGen.nextInt(Color.values().length)];
		Color[] outColours = new Color[Code.SLOTLENGTH];
		for(int i = 0; i < Code.SLOTLENGTH; i++){
			outColours[i] = theChosenLad;
		}
		Code outCode = new Code(outColours);
		return outCode;
	}
	/**
	 * Generates a Code containing SLOTLENGTH/2 Color doubles which are then randomized to generate the final Code.
	 * 
	 * @return Code, (Often of the form { a a b b }, { a b b a}, or { a b a b })
	 */
	private static Code twoDubs(){

		//Color[] outColours = new Color[Code.SLOTLENGTH];
		ArrayList <Color> colourSets = new ArrayList <Color>(Code.SLOTLENGTH/2);
		if(colourSets.size()>Color.values().length){
			//You dun goofed mate.
			//Should have error if using more than 2 times the number of colours, but doesn't matter because SLOTLENGTH should always be equal to 4 in this project.
		}
		for(int i = 0; i<Code.SLOTLENGTH/2;i++){
			for(boolean isValidColorAdded = false; !isValidColorAdded;){
				Color testColor = Color.values()[ranGen.nextInt(Color.values().length)];
				if(!colourSets.contains(testColor)){
					colourSets.add(testColor);
					isValidColorAdded = true;
				}
			}
		}
		Color[] randColours = new Color[Code.SLOTLENGTH];//randColours is a list of doubles of colours of the form { a a b b c c d d }
		for(int i = 0; i < Code.SLOTLENGTH; i ++){
			randColours[i] = colourSets.get(i/2);
		}
		for (int i = randColours.length - 1; i > 0; i--){//Randomizes randColours' order.
			int index = ranGen.nextInt(i + 1);
			Color a = randColours[index];
			randColours[index] = randColours[i];
			randColours[i] = a;
		}
		Code outCode = new Code(randColours);
		return outCode; 

	}
	/**
	 * 
	 * @return ArrayList of every 
	 */
	private static ArrayList<Code> allOpts(){
		ArrayList<Code> outList = new ArrayList<Code>();
		for(int i = 0; i<Color.values().length;i++){
			for(int j = 0; j<Color.values().length;j++){
				for(int f = 0; f<Color.values().length;f++){
					for(int k = 0; k<Color.values().length;k++){
						Color[] inColorA = {Color.values()[i]
								,Color.values()[j]
										,Color.values()[f]
												,Color.values()[k]};

						Code test = new Code(inColorA);
						if(!outList.contains(test)){
							outList.add(test);
							//System.out.println(test);
						}
					}
				}
			}
		}
		//System.out.println(outList.size());
		return outList;
	}

	private static boolean checkIsColor(String inString){
		//System.out.println(Character.toLowerCase(inString.toCharArray()[0]));
		switch(Character.toLowerCase(inString.toCharArray()[0])){
		case 'e':return true;
		case 'r':return true;
		case 'o':return true;
		case 'y':return true;
		case 'g':return true;
		case 'b':return true;
		case 'v':return true;
		default:return false;
		}

		//return null;
	}

	private static Color toColorValueFromChar(char inChar){
		switch(inChar){
		case 'e':return Color.EMPTY;
		case 'r':return Color.RED;
		case 'o':return Color.ORANGE;
		case 'y':return Color.YELLOW;
		case 'g':return Color.GREEN;
		case 'b':return Color.BLUE;
		case 'v':return Color.VIOLET;
		default:/*System.out.println("You screwed up somewhere.");*/ return null;

		}
	}

}

