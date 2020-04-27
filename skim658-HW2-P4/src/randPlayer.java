import java.util.Random;

/****************************************************************
 * studPlayer.java
 * Implements MiniMax search with A-B pruning and iterative deepening search (IDS). The static board
 * evaluator (SBE) function is simple: the # of stones in studPlayer's
 * mancala minue the # in opponent's mancala.
 * -----------------------------------------------------------------------------------------------------------------
 * Licensing Information: You are free to use or extend these projects for educational purposes provided that
 * (1) you do not distribute or publish solutions, (2) you retain the notice, and (3) you provide clear attribution to UW-Madison
 *
 * Attribute Information: The Mancala Game was developed at UW-Madison.
 *
 * The initial project was developed by Chuck Dyer(dyer@cs.wisc.edu) and his TAs.
 *
 * Current Version with GUI was developed by Fengan Li(fengan@cs.wisc.edu).
 * Some GUI componets are from Mancala Project in Google code.
 */




//################################################################
// studPlayer class
//################################################################

public class randPlayer extends Player {


	/*Use IDS search to find the best move. The step starts from 1 and keeps incrementing by step 1 until
	 * interrupted by the time limit. The best move found in each step should be stored in the
	 * protected variable move of class Player.
	 */
	public void move(GameState state)
	{
		//7 will probably have to changed later
		maxAction(state, 2);
	}

	// Return best move for max player. Note that this is a wrapper function created for ease to use.
	// In this function, you may do one step of search. Thus you can decide the best move by comparing the 
	// sbe values returned by maxSBE. This function should call minAction with 5 parameters.
	public int maxAction(GameState state, int maxDepth)
	{
			Random rand = new Random();
		 move = (int)rand.nextInt(6) + 0;
		 while(state.illegalMove(move) == true){
			 move = (int)rand.nextInt(6) + 0;
		 }
		 return move;
		 
	}

	//return sbe value related to the best move for max player
	public int maxAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
	{
		//check if the gamee is over
		if(state.gameOver()){
			return sbe(state);
		}
		//intialize v to beta
		int v = beta;
		//make a copy of the GameState
		GameState curr = new GameState(state.toArray());
		//For each move on our side of the array generate successors
		for(int i = 0; i < 6; i++){
			//If player gets another move call max action again
			if(curr.illegalMove(i) == false){
				while(curr.applyMove(i)){
					maxAction(state, ++currentDepth, maxDepth, alpha, beta);
				}
				curr.applyMove(i);
				System.out.println(curr.toString());
				//get the current value for the state
				//For debugin purposes stop executing if current is deeper than max depth
				if(currentDepth == maxDepth){
					v = Math.max(v, minAction(state, ++currentDepth, maxDepth, alpha, beta));
				}
				// update beta value
				if( v >= beta){
					//Print out current sbe 
					System.out.println("Current SBE : " + sbe(curr));
					return sbe(curr);
				}
				//update alpha
				alpha = Math.max(alpha, v);
			}
		}
		//Print out current sbe 
		System.out.println(sbe(curr));
		return sbe(curr);
	}

	//return sbe value related to the bset move for min player
	public int minAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
	{
		//check if terminal state
		if(state.gameOver()){
			return sbe(state);
		}
		//intialize the value of the min state to alpha
		int v  = alpha;
		//Copy the GameState
		GameState curr = new GameState(state.toArray());
		curr.rotate();
		//For each move ON OTHER SIDE OF THE ARRAY
		for(int i = 0; 0 < 6; i++){
			//If the play gets another turn call minAction again
			if(curr.illegalMove(i) == false){
				while(curr.applyMove(i)){
					minAction(curr, ++currentDepth, maxDepth, alpha, beta);
				}
				//apply the move
				curr.applyMove(i);
				System.out.println(curr.toString());
				//get the current value for the state
				//For debugin purposes stop executing if current is deeper than max depth
				if(currentDepth == maxDepth){
					//if they are equal evaluate the board function
					v = Math.min( v, maxAction(state, ++currentDepth, maxDepth, alpha, beta));
					System.out.println("Current SBE : " + sbe(curr));
				}
				//Check for pruning
				if( v <= alpha){
					//Printing out current SBE
					System.out.println("Current SBE : " + sbe(curr));
					return sbe(curr);
				}
			}
			//else set beta to min value
			beta = Math.min(beta, v);
		}
		//return move;
	}

	//the sbe function for game state. Note that in the game state, the bins for current player are always in the bottom row.
	private int sbe(GameState state)
	{
		//Total the number of stones on opposite side
		//May need to swithc 13 and 6
		int oppositeStones = 0;
		for(int i = 7; i < 13; i++){
			oppositeStones += state.mancalaOf(i);
		}
		//Total the amount of stones on our side
		int myStones = 0;
		for(int i = 0; i < 6; i++){
			myStones += state.mancalaOf(i);
		}
		int SBE = ((state.stoneCount(6) - state.stoneCount(13) + (myStones - oppositeStones)));

		return SBE;


	}
}

