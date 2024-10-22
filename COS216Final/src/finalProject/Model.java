package finalProject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.Queue;

/**
 * COS216 Final Project back-end
 * 
 * This is the back-end for the game quoridor! It's three most notable functions
 * are constructing a grid within an adj matrix of arbitrary size, determining
 * if a player is performing a legal movement move, and determining whether or
 * not a barrier is a legal placement.
 * 
 * @author mjg29296
 * @version Spring 2023
 */

public class Model {

	/** Main back-end storage */
	private int[][] adjMat;

	/** Alternates between true and false, letting back-end know whose turn it is */
	private boolean playerTurn = true;

	/** Keeps track of where player 1 is within the grid */
	private int playerOnePos;

	/** Keeps track of where player 2 is within the grid */
	private int playerTwoPos;

	/** Stores all possible winning locations for player 2 */
	private int start[];

	/** Stores all possible winning locations for player 1 */
	private int end[];

	/** Stores the length of the side of the board */
	private int size;

	/** Marker to inform back-end whether the game has ended or not */
	private boolean winner = false;

	/**
	 * Constructor -
	 * 
	 * @param board the size of the length of the board
	 */
	public Model(int board) {
		size = board;
		adjMat = new int[size * size][size * size];
		buildGrid();
	}

	/**
	 * setSize - creates a new grid of arbitrary size
	 * 
	 * @param board the size of the length of the board
	 */
	public void setSize(int board) {
		size = board;
		adjMat = new int[size * size][size * size];
		buildGrid();
		pcs.firePropertyChange("newSize", null, null);
	}

	/**
	 * buildGrid - fills the entire adj matrix without edges, then places an edge
	 * between nodes to form a grid of size board length X board length. Also
	 * initializes player 1 and player 2 positions, as well as ensuring it's player
	 * 1's turn and that the game is not over
	 */
	private void buildGrid() {
		for (int k = 0; k < size * size; k++) {
			for (int z = 0; z < size * size; z++) {
				adjMat[k][z] = 0;
			}
		}

		for (int i = 0; i < size * size; i++) {
			int left = i - 1;
			int right = i + 1;

			if (right % size != 0) {
				adjMat[i][right] = 1;
				adjMat[right][i] = 1;
			}

			if (i % size != 0) {
				adjMat[i][left] = 1;
				adjMat[left][i] = 1;
			}

			if (i + size < size * size) {
				adjMat[i][i + size] = 1;
				adjMat[i + size][i] = 1;
			}

			if (i - size > 0) {
				adjMat[i][i - size] = 1;
				adjMat[i - size][i] = 1;
			}
		}

		start = new int[size];
		for (int i = 0; i < size; i++) {
			start[i] = i;
		}

		int x = 0;
		end = new int[size];
		for (int i = size * size - 1; i >= size * size - size; i--) {
			end[x] = i;
			x++;
		}

		playerOnePos = size / 2;

		playerTwoPos = (size * size) - (size / 2 + 1);

		playerTurn = true;

		winner = false;
	}

	/**
	 * getTitle -
	 * 
	 * @return A title for this application
	 */
	public String getTitle() {
		return "Quoridor";
	}

	/**
	 * getSize -
	 * 
	 * @return the length of the side of the board
	 */
	public int getSize() {
		return size;
	}

	/**
	 * getFeedback -
	 * 
	 * @return an appropriate description of the state of the system
	 */
	public String getFeedback() {
		for (int i = 0; i < size; i++) {
			if (playerOnePos == end[i]) {
				winner = true;
				return "	Blue has won the game!";
			}
			if (playerTwoPos == start[i]) {
				winner = true;
				return "	Orange has won the game!";
			}
		}
		if (playerTurn) {
			return "	Blue's turn";
		} else {
			return "	Orange's turn";
		}
	}

	/**
	 * player - calculates where the player clicked in accordance to the adj matrix,
	 * while also determining what information to send to playerLogic
	 * 
	 * @param col
	 * @param row
	 */
	public void player(int col, int row) {
		int click = (row / 2) * size + (col / 2);

		if (playerTurn) {
			playerLogic(playerOnePos, playerTwoPos, click, col, row);
		} else {
			playerLogic(playerTwoPos, playerOnePos, click, col, row);
		}
	}

	/**
	 * playerLogic - calculates whether or not the player's move is a legal request
	 * 
	 * @param player
	 * @param opponent
	 * @param click
	 * @param col
	 * @param row
	 */
	private void playerLogic(int player, int opponent, int click, int col, int row) {
		if (!winner) {
			if (adjMat[player][click] == 0) {
				if (adjMat[player][opponent] == 1) {
					if (adjMat[opponent][click] == 1) {
						if (playerTurn) {
							playerOnePos = click;
						} else {
							playerTwoPos = click;
						}
						if (playerTurn) {
							playerTurn = false;
						} else {
							playerTurn = true;
						}
						if (col == row) {
							row++;
							pcs.firePropertyChange("newMove++", col, row);
						} else {
							pcs.firePropertyChange("newMove", col, row);
						}
						return;
					}
				}
			}
			if (adjMat[player][click] == 1 && click != opponent) {
				if (playerTurn) {
					playerOnePos = click;
				} else {
					playerTwoPos = click;
				}
				if (playerTurn) {
					playerTurn = false;
				} else {
					playerTurn = true;
				}
				if (col == row) {
					row++;
					pcs.firePropertyChange("newMove++", col, row);
				} else {
					pcs.firePropertyChange("newMove", col, row);
				}
				return;
			}
			pcs.firePropertyChange("rejected", null, null);
		}
	}

	/**
	 * barrier - calculates whether or not the player's barrier placement is a legal
	 * request
	 * 
	 * @param col1
	 * @param row1
	 * @param col2
	 * @param row2
	 */
	public void barrier(int col1, int row1, int col2, int row2) {
		if (!winner) {
			int index1 = (row1 / 2) * size + (col1 / 2);
			int index2 = (row2 / 2) * size + (col2 / 2);

			if (adjMat[index1][index2] == 1) {
				adjMat[index1][index2] = 0;
				adjMat[index2][index1] = 0;
				if (!path(end, playerOnePos) || !path(start, playerTwoPos)) {
					adjMat[index1][index2] = 1;
					adjMat[index2][index1] = 1;
					pcs.firePropertyChange("rejected", null, null);
				} else {
					if (playerTurn) {
						playerTurn = false;
					} else {
						playerTurn = true;
					}
					pcs.firePropertyChange("newBarrier", (col1 + col2) / 2, (row1 + row2) / 2);
				}
			} else {
				pcs.firePropertyChange("rejected", null, null);
			}
		}
	}

	/**
	 * path - utilizing queues within a queue, this method determines whether or not
	 * a path still exists for either player to win the game
	 * 
	 * @param dest
	 * @param player
	 * @return true if path exists, otherwise false
	 */
	private boolean path(int dest[], int player) {
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.add(player);
		boolean visited[] = new boolean[size * size];
		visited[player] = true;
		while (!queue.isEmpty()) {
			int temp = queue.remove();
			Queue<Integer> tempQueue = new LinkedList<Integer>();
			if (temp + 1 < size * size && adjMat[temp][temp + 1] == 1) {
				tempQueue.add(temp + 1);
			}
			if (temp - 1 >= 0 && adjMat[temp][temp - 1] == 1) {
				tempQueue.add(temp - 1);
			}
			if (temp + size < size * size && adjMat[temp][temp + size] == 1) {
				tempQueue.add(temp + size);
			}
			if (temp - size >= 0 && adjMat[temp][temp - size] == 1) {
				tempQueue.add(temp - size);
			}
			while (!tempQueue.isEmpty()) {
				for (int i = 0; i < dest.length; i++) {
					if (tempQueue.peek() == dest[i]) {
						return true;
					}
				}
				if (!visited[tempQueue.peek()]) {
					queue.add(tempQueue.peek());
				}
				visited[tempQueue.remove()] = true;
			}
		}
		return false;
	}

	/** Handles observer pattern behavior */
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * Observers use this to subscribe
	 * 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Observers use this to unsubscribe
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}
}
