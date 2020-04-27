import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Depth-First Search (DFS)
 * 
 * You should fill the search() method of this class.
 */
public class DepthFirstSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public DepthFirstSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main depth first search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		// FILL THIS METHOD

		// explored list is a 2D Boolean array that indicates if a state associated with a given position in the maze has already been explored.
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

		// Stack implementing the Frontier list
		LinkedList<State> stack = new LinkedList<State>();
		
		State checkState = new State( null, null, 0, 0 );
		ArrayList<State> solPath = new ArrayList<State>();
		int solLength = 0;
		
		Square playerSquare = maze.getPlayerSquare();
		State playerState = new State( playerSquare, null, 0, 0 );
		stack.push( playerState );

		int nodesExpanded = 0;
		int maxDepth = 0;
		int maxFrontier = 1; 
		
		while (!stack.isEmpty()) {
			// TODO return true if find a solution
			// TODO maintain the cost, noOfNodesExpanded (a.k.a. noOfNodesExplored),
			// maxDepthSearched, maxSizeOfFrontier during
			// the search
			// TODO update the maze if a solution found

			// use stack.pop() to pop the stack.
			// use stack.push(...) to elements to stack
			
			State currState = stack.pop();
			
			if ( currState.getDepth() > maxDepth ) {
				maxDepth = currState.getDepth();
			}
			
			if ( !explored[currState.getX()][currState.getY()] ) {	
				if ( currState.isGoal(maze) ) {
					nodesExpanded++;
					solPath.add( currState ); 
					checkState = currState; 
					Square newSquare = new Square( checkState.getX(), checkState.getY() );
					while ( checkState.getParent() != null ) {
						if ( checkState.isGoal(maze) ) {
							solPath.add( checkState.getParent() );
							checkState = checkState.getParent();
							solLength++;
						} else {
							solPath.add( checkState.getParent() );
							newSquare = new Square( checkState.getX(), checkState.getY() );
							maze.setOneSquare(newSquare, '.');
							checkState = checkState.getParent();
							solLength++;
						}
					}
					explored[currState.getX()][currState.getY()] = true;
					this.cost = solLength;
					this.maxDepthSearched = maxDepth;
					this.maxSizeOfFrontier = maxFrontier;
					this.noOfNodesExpanded = nodesExpanded;
					return true;
				} else {
					explored[currState.getX()][currState.getY()] = true;
					ArrayList<State> children = currState.getSuccessors(explored, maze);
					boolean dup = false;
					while ( !children.isEmpty() ) {
						State child = children.get( 0 );
						if ( !explored[child.getX()][child.getY()] ) {
							for ( int i = 0; i < stack.size(); i++ ) {
								if ( child.getX() == stack.get( i ).getX() && child.getY() == stack.get( i ).getY() ) {
									dup = true;
								}
							}
							if ( !dup )
								stack.push( child );
						}
						children.remove( 0 );
					}
					if ( stack.size() > maxFrontier ) {
						maxFrontier = stack.size();
						}
					}
				nodesExpanded++;
			}
		}
		// TODO return false if no solution
		return false;
	}
}
