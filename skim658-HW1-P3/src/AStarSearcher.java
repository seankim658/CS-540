import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public AStarSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main a-star search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {

		// FILL THIS METHOD
		
		// explored list is a Boolean array that indicates if a state associated with a given position in the maze has already been explored. 
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

		PriorityQueue<StateFValuePair> frontier = new PriorityQueue<StateFValuePair>();
		
		int solLength = 0;
		ArrayList<State> solPath = new ArrayList<State>();

		// TODO initialize the root state and add
		// to frontier list
		Square playerSquare = maze.getPlayerSquare(); 
		State playerState = new State( playerSquare, null, 0, 0 );
		StateFValuePair playerFState = new StateFValuePair( playerState, playerState.getGValue() + Math.sqrt( Math.pow( playerState.getX() - maze.getGoalSquare().X, 2) + Math.pow( playerState.getY() - maze.getGoalSquare().Y, 2) ) );
		frontier.add( playerFState ); 

		int nodesExpanded = 0;
		int maxDepth = 0;
		int maxFrontier = 1;
		
		while (!frontier.isEmpty()) {
			// TODO return true if a solution has been found
			// TODO maintain the cost, noOfNodesExpanded (a.k.a. noOfNodesExplored),
			// maxDepthSearched, maxSizeOfFrontier during
			// the search
			// TODO update the maze if a solution found
			StateFValuePair currFState = frontier.poll();
			
			if ( currFState.getState().getDepth() > maxDepth ) {
				maxDepth = currFState.getState().getDepth();
			}
			
			if ( !explored[currFState.getState().getX()][currFState.getState().getY()] ) {
				if ( currFState.getState().isGoal(maze) ) {
					State checkState = currFState.getState();
					nodesExpanded++;
					solPath.add( checkState );
					Square newSquare = new Square( checkState.getX(), checkState.getY() );
					while ( checkState.getParent() != null ) {
						if ( checkState.isGoal(maze) ) {
							solPath.add( checkState.getParent() );
							checkState = checkState.getParent(); 
							solLength++;
						} else {
							solPath.add( checkState.getParent() );
							newSquare = new Square( checkState.getX(), checkState.getY() );
							maze.setOneSquare( newSquare, '.' );
							checkState = checkState.getParent();
							solLength++;
						}
					}
					explored[currFState.getState().getX()][currFState.getState().getY()] = true;
					this.cost = solLength;
					this.maxDepthSearched = maxDepth;
					this.maxSizeOfFrontier = maxFrontier;
					this.noOfNodesExpanded = nodesExpanded;
					return true;
				} else {
					explored[currFState.getState().getX()][currFState.getState().getY()] = true;
					ArrayList<State> children = currFState.getState().getSuccessors(explored, maze);
					boolean dup = false;
					while ( !children.isEmpty() ) {
						State child = children.get( 0 );
						StateFValuePair currChild = new StateFValuePair( child, child.getGValue() + Math.sqrt( Math.pow( child.getX() - maze.getGoalSquare().X , 2) + Math.pow( child.getY() - maze.getGoalSquare().Y, 2) ) );
						double childValue = currChild.getState().getGValue();
						double compareCheckFValue = 0.0;
						StateFValuePair dupCheckF = new StateFValuePair( null, 0.0 );
						StateFValuePair dupChildF = new StateFValuePair( null, 0.0 );
						if ( !explored[child.getX()][child.getY()] ) {
							Object[] frontierCheck = frontier.toArray();
							for ( int i = 0; i < frontierCheck.length; i++ ) {
								double checkFValue = ((StateFValuePair) frontierCheck[i]).getState().getGValue();
								if ( ((StateFValuePair) frontierCheck[i]).getState().getX() == currChild.getState().getX() && ((StateFValuePair) frontierCheck[i]).getState().getY() == currChild.getState().getY() ) {
									dup = true;
									dupCheckF = (StateFValuePair) frontierCheck[i];
									dupChildF = currChild;
									compareCheckFValue = checkFValue;
								}
							}
							if ( dup && compareCheckFValue > childValue ) {
								frontier.remove( dupCheckF );
								frontier.add( currChild );
							}
							if ( !dup ) {
								frontier.add( currChild );
							}
						} 
					children.remove( 0 );
					}
					if ( frontier.size() > maxFrontier ) {
						maxFrontier = frontier.size();
					}
				}
				nodesExpanded++;
			}
			// use frontier.poll() to extract the minimum stateFValuePair.
			// use frontier.add(...) to add stateFValue pairs
		}

		// TODO return false if no solution
		return false;
	}

}
