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

public class skim658Player extends Player {


	/*Use IDS search to find the best move. The step starts from 1 and keeps incrementing by step 1 until
	 * interrupted by the time limit. The best move found in each step should be stored in the
	 * protected variable move of class Player.
	 */
	//Boolean for repeat moves and possible steals
	boolean repeatMove = false;
	boolean steal = false;
	int stealAmount = 0;
	//Checkers for first and second
	boolean second = false;
	boolean firstIteration = true;
	

	public void move(GameState state)
	{
		
		//Loop to check if first or second
		//Check board for zeros
		if(firstIteration){
			for(int i = 0; i < 13; i ++){
				//Don't check mancalas
				if(i == 6 || i == 13){
					i++;
				}
				//if one of the bins has zero already ai is going second
				if(state.stoneCount(i) == 0){
					second = true;
				}
				firstIteration = false;
			}
		}
		//tmp move
		int tmpMove;

		//Max Depth
		int maxDepth = 1;
		//While loop to go forever
		boolean forever = true;
		while(forever){
			tmpMove = maxAction(state, maxDepth);
			
			//Double check that move is legal
			while(state.illegalMove(tmpMove) == true){
				tmpMove++;
			}
			
			move = tmpMove;
			maxDepth++;
	//		System.out.println("Move : " + move);
		}

	}

	// Return best move for max player. Note that this is a wrapper function created for ease to use.
	// In this function, you may do one step of search. Thus you can decide the best move by comparing the 
	// sbe values returned by maxSBE. This function should call minAction with 5 parameters.
	public int maxAction(GameState state, int maxDepth)
	{
		//Best move varible
		int bestMove = 0;
		//current SBE
		int currSBE = 0;
		//starting SBE
		int startSBE = -1000;
		//int secondStartSBE = 1000;
		//Alpha and beta
		int alpha = -1000;
		int beta =  1000;

		//Loop and create the iniial children of the passed in state
		GameState tmp = new GameState(state);
		for(int i = 0; i < 6; i++){
			//Check if the child is legal
			if(state.illegalMove(i) == false){

				//Call min Action with a tmp state
				//Do this with state???
				GameState copy = new GameState (tmp);

				//apply the valid move
				repeatMove = copy.applyMove(i);

				if(repeatMove){
					currSBE = maxAction(copy, 1 , maxDepth, alpha, beta);
				}
				else{
					currSBE = minAction(copy, 1, maxDepth, alpha, beta);
				}
			}
			//Boolean to reverse if  playing second
			//get the best move for min
			if(currSBE > startSBE){			
				//May have to move this up
				bestMove = i;
				startSBE = currSBE;
	//			System.out.println("CurrSBE : " + currSBE);
	//			System.out.println("best Move : " + bestMove);
			}
			//Get best move for max
//			if(currSBE < secondStartSBE && (second == false)){
//				bestMove = i;
//				secondStartSBE = currSBE;
//				System.out.println("CurrSBE : " + currSBE);
//				System.out.println("best Move : " + bestMove);
//				
//	         }
		}
		return bestMove;
	}

	//return sbe value related to the best move for max player
	public int maxAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
	{
		//make a copy of the GameState
		GameState curr = new GameState(state);

		int v = -10000;

		//curr.rotate();
		//check if the gamee is over
		if(state.gameOver()){
			return sbe(state);
		}
		//if the currentdepth = max depth return that state sbe
		if(currentDepth >= maxDepth){
			return sbe(curr);
		}
		//For each move on their side of the array generate successors
		for(int i = 0; i < 6; i++){
			//Copy the array of the current state and create a child
			//GameState currCopy = new GameState(curr);
			if(curr.illegalMove(i) == false){

				//	System.out.println("Before Max Move");
				//  System.out.println(currCopy.toString());
				//create a new state
				GameState maxChild = new GameState ( curr);

				//apply move
				repeatMove = maxChild.applyMove(i);
				//Test code to print out state
				//System.out.println("After Max Move");
				//System.out.println(currCopy.toString());

				if(repeatMove){
					v = Math.max(v, maxAction(maxChild, ++currentDepth, maxDepth, alpha, beta));

				}
				else{
					v = Math.max(v, minAction(maxChild, ++currentDepth, maxDepth, alpha, beta));
				}
				//Update/prune??
				if(v >= beta){
					return v;
				}
				//TEST CODE
				//System.out.println(" beta and  min " + beta "   " +  b)
				alpha = Math.max(alpha, v );
			}
		}
		return v;

	}
	//return sbe value related to the bset move for min player
	public int minAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
	{
		//check if terminal state
		if(state.gameOver()){
			return sbe(state);
		}
		if(currentDepth >= maxDepth ){
			return sbe(state);
		}
		//Copy the GameState
		GameState currCopy = new GameState(state.toArray());

		//int v = value of currstate
		int v = 10000;

		//curr.rotate();

		//For each move our OTHER SIDE OF THE ARRAY
		for(int i = 7; i < 13 ; i++){
			//GameState currCopy = new GameState(curr.state);
			if(currCopy.illegalMove(i) == false){
				//				System.out.println("Before Min Move");
				//				System.out.println(currCopy.toString());
				//				currCopy.applyMove(i);
				//				System.out.println("After Min Move");
				//				System.out.println(currCopy.toString());
				GameState minChild = new GameState(currCopy);
				repeatMove = minChild.applyMove(i);

				if(repeatMove){
					v = Math.min(v, minAction(minChild, ++currentDepth, maxDepth, alpha, beta)); 
				}
				else{
					v = Math.min( v , maxAction(minChild, ++currentDepth, maxDepth, alpha, beta));
				}

				if(alpha >= v){
					return v;
				}
				beta = Math.min(beta, v);
			}
		}
		return v;

	}

	//the sbe function for game state. Note that in the game state, the bins for current player are always in the bottom row.
	private int sbe(GameState state)
	{
		int SBE;

		//Total the number of stones on opposite side
		//May need to switch 13 and 6
		int oppositeStones = 0;
		for ( int j = 7; j < 13; j++ ) {
			oppositeStones = oppositeStones + state.stoneCount( j );
		}

		int myStones = 0;
		for(int i = 0; i < 6; i++){
			myStones = myStones + state.stoneCount( i );
		}

		/*	for(int k = 0; k < 6; k++){
			if ( state.stoneCount( k ) + k < 6 ) {
				if ( state.stoneCount( state.stoneCount( k ) + k )  == 0 ) {
					steal = true;
					stealAmount = state.stoneCount( ( 13 - k ) - 1 );
				}
			}
		}   */

		int bin = 0;
		if ( repeatMove ) {
			bin = 7;
		}

		int oppositeBin = 0;
		if ( steal ) {
			oppositeBin = stealAmount + 4;
		}

		//	if ( first ) {
		SBE = ( state.stoneCount(6) - state.stoneCount(13) ) + ( myStones - oppositeStones ) + bin + oppositeBin;
	//	SBE = ( state.stoneCount(6) - state.stoneCount(13) + (oppositeStones - myStones ) + bin + oppositeBin);
		//	}
		//		else { 
		//			SBE = ( state.stoneCount(6) - state.stoneCount(13) ) + ( myStones - oppositeStones ) - bin - oppositeBin;
		//		}
		//		
		//		goAgain = false;
		//		steal = false;
		//		stealAmount = 0;
		int simpleSBE;
		//if(second == false){
			//simpleSBE = state.stoneCount( 1, 6 ) - state.stoneCount(0 , 6);
		//}
		//else{
			simpleSBE = state.stoneCount(0, 6 ) - state.stoneCount(1,6);
		//}
		
		return simpleSBE;

	}
}


