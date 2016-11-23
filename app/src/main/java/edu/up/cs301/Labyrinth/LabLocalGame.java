package edu.up.cs301.Labyrinth;

import android.util.Log;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.IllegalMoveInfo;


/**
 * The LabyrinthLocalGame class for a Labyrinth game. Defines and enforces
 * the game rules; handles interactions with players.
 * 
 * @author Chloe Kuhar
 * @author Liz Frick
 * @author Nicole Kister
 * @author Mikayla Whiteaker
 * @version Nov 2016, preAlpha
 */

public class LabLocalGame extends LocalGame
{
	/**
	 * PSA:
	 * Player 0 is Red
	 * Player 1 is Green
	 * Player 2 is Blue
	 * Player 3 is Yellow
	 */

	private LabGameState masterGameState;
	private boolean[][] booleanMazeMap = new boolean[9][9];

	/*
	 * Welcome to the lovely local game constructor
	 */
	public LabLocalGame()
	{
		masterGameState = new LabGameState();
	}

	/*
	 * canMove(playerIndex:int):boolean - this method takes a player index, which is an int,
	 * and returns a boolean that reflects whether or not it is the
	 * player’s turn whose index was passed to the method
	 */
	protected boolean canMove(int playerIdx)
	{
		return playerIdx == masterGameState.getTurnID();
	}


	/*
	 * checkGameOver():String - this method checks to see if any player has
	 * won by checking to see if any of the player’s cardsToCollect
	 * hand is empty and if they have returned to their home maze tile.
	 */
	protected String checkIfGameOver()
	{
		//top left is green = 0 player Index
		//top right is red = 1 player Index
		//bottom left is blue = 2 player Index
		//bottom right is yellow = 3 player Index
		MazeTile[][] masterMaze = masterGameState.getMaze();
		if (masterGameState.getTurnID() == 0)
		{
			if (masterMaze[1][1].occupiedBy.contains(0) && masterGameState.getPlayerHand(0).size() == 0)
			{
				return "The Red Player Has Won";
			}
		} else if (masterGameState.getTurnID() == 1)
		{
			if (masterMaze[7][1].occupiedBy.contains(1) && masterGameState.getPlayerHand(1).size() == 0)
			{
				return "The Green Player Has Won";
			}
		} else if (masterGameState.getTurnID() == 2)
		{
			if (masterMaze[1][7].occupiedBy.contains(2) && masterGameState.getPlayerHand(2).size() == 0)
			{
				return "The Blue Player Has Won";
			}
		} else if (masterGameState.getTurnID() == 3)
		{
			if (masterMaze[7][7].occupiedBy.contains(3) && masterGameState.getPlayerHand(3).size() == 0)
			{
				return "The Yellow Player Has Won";
			}
		}
		return null;
	}

	/*
	 * makeMove(action:LabGameAction):boolean - this takes a game action
	 * and is based on whether the action is a maze tile insertion or
	 * piece move. It first checks to see if the action is from the player whose turn
	 * it is and then calls either of the helper makeMove methods listed below:
	 */
	protected boolean makeMove(GameAction action)
	{
		if (canMove(this.getPlayerIdx(action.getPlayer())))
		{
			if (action instanceof LabMoveMazeAction)
			{
				return makeMazeMove(action);
			}
			else if(action instanceof LabMoveExtraTile){
				masterGameState.moveExtraTile(((LabMoveExtraTile) action).getCoords()[0], ((LabMoveExtraTile) action).getCoords()[1]);
				return true;
			}
			else if (action instanceof LabMovePieceAction && masterGameState.hasMovedMaze())
			{
				return makePlayerPieceMove(action);
			}
		}
		return false;
	}

	/*
	 * makeMazeMove(action:LabMoveMazeAction):boolean - this is one of the
	 * helper methods for makeMove(). This will shift the maze row or column so that the extra maze tile is
	 * in the maze and the tile on the opposing side is pushed out of the maze. Assumes that the extra tile
	 * has been moved to the proper space for the movement
	 */
	private boolean makeMazeMove(GameAction action)
	{

		if (!(action instanceof LabMoveMazeAction) || ( ! checkExtraTile() ) )
		{
			return false;
		}

		//finding the coordinates for where the extra tile is
		int[] coordinates = masterGameState.findExtraTile();

		//extra tile is in the top row
		if (coordinates[0] == 0)
		{
			masterGameState.moveCol(coordinates[1], true);
			masterGameState.setHasMovedMaze(true);
			return true;
		}
		//extra tile is on the left side
		else if (coordinates[1] == 0)
		{
			masterGameState.moveRow(coordinates[0], true);
			masterGameState.setHasMovedMaze(true);
			return true;
		}
		//extra tile is on the bottom
		else if (coordinates[0] == masterGameState.getMaze().length - 1)
		{
			masterGameState.moveCol(coordinates[1], false);
			masterGameState.setHasMovedMaze(true);
			return true;
		}
		//extra tile is on the right side
		else if (coordinates[1] == masterGameState.getMaze().length - 1)
		{
			masterGameState.moveRow(coordinates[0], false);
			masterGameState.setHasMovedMaze(true);
			return true;
		}

		return false;
	}


	/*
	 * makePlayerPieceMove(action:LabMovePieceAction):boolean - this is the other helper method for makeMove() this method
	  * will check there is a connected path via a helper method If there is,
	 * it will remove the player from the occupiedBy arrayList of the current
	 * tile and add the player to the occupiedBy ArrayList of the selected tile
	 */
	private boolean makePlayerPieceMove(GameAction action)
	{
		if(!masterGameState.hasMovedMaze()){
			return false;
		}
		MazeTile[][] newMaze = masterGameState.getMaze();
		if(newMaze[((LabMovePieceAction) action).getCoords()[0]][((LabMovePieceAction) action).getCoords()[1]].getOccupiedBy().contains(((LabMovePieceAction) action).getPlayerNum())){
			return true;
		}
		else if(checkPath(((LabMovePieceAction) action).getCoords()[0], ((LabMovePieceAction) action).getCoords()[1])){
			newMaze[((LabMovePieceAction) action).getCoords()[0]][((LabMovePieceAction) action).getCoords()[1]].addPlayer(((LabMovePieceAction) action).getPlayerNum());
			masterGameState.setMaze(newMaze);
			masterGameState.setHasMovedMaze(false);
			return true;
		}

		return false;
	}

	/*
	 * this is the helper method for makePlayerPieceMove()
	 */
	private boolean checkPath(int xDest, int yDest)
	{
		return true;
//		MazeTile[][] maze = masterGameState.getMaze();
//		for (int i = 0; i < maze.length; i++)
//		{
//			for (int j = 0; j < maze[i].length; j++)
//			{
//				booleanMazeMap[i][j] = false;
//				if(maze[i][j] == null){continue;}
//				if (maze[i][j].getOccupiedBy().contains((Integer) masterGameState.getTurnID()))
//				{
//					booleanMazeMap[i][j] = true;
//				}
//			}
//		}
//		boolean changeFlag = true;
//		while (changeFlag)
//		{
//			for (int i = 1; i < maze.length-1; i++)
//			{
//				for (int j = 1; j < maze[i].length-1; j++)
//				{
//
//					if (booleanMazeMap[i][j])
//					{
//						//top
//						if (maze[i][j].getPathMap()[0] && maze[i - 1][j].getPathMap()[2])
//						{
//							booleanMazeMap[i - 1][i] = true;
//							changeFlag = true;
//						}
//						//right
//						else if (maze[i][j].getPathMap()[1] && maze[i][j + 1].getPathMap()[3])
//						{
//							booleanMazeMap[i][j + 1] = true;
//							changeFlag = true;
//						}
//						//bottom
//						else if (maze[i][j].getPathMap()[2] && maze[i + 1][j].getPathMap()[0])
//						{
//							booleanMazeMap[i + 1][j] = true;
//							changeFlag = true;
//						}
//						//left
//						else if (maze[i][j].getPathMap()[3] && maze[i][j - 1].getPathMap()[1])
//						{
//							booleanMazeMap[i][j - 1] = true;
//							changeFlag = true;
//						} else
//						{
//							changeFlag = false;
//						}
//					}
//				}
//			}
//
//		}
//		return booleanMazeMap[xDest][yDest];
	}

	/*
	 * checkTCollect(topCard:LabTCard, currTile:MazeTile):boolean - this takes the
	 * current maze Tile and the top Card to collect of the current player.
	 * It will be called as a helper method at the end of makeMove(). It will
	 * check to see if the current maze tile has the same treasure symbol.
	 */
	private boolean checkTCollect(TCard topCard, MazeTile currTile)
	{
		if (currTile.getTreasureSymbol().getName().equals(topCard.getTreasure().getName()))
		{
			return true;
		}
		return false;
	}

	/*
	 * sendUpdatedStateTo(p:GamePlayer):void - this takes the current player whose turn it is and makes deep copy of the maze, the
	 * number of cards to collect for each player, the current player’s top
	 * card to collect treasure. Then it sends all this info to the player.
	 */
	protected void sendUpdatedStateTo(GamePlayer p)
	{
		Log.i("LabLocalGame", p.toString());
		LabGameState copy = new LabGameState(masterGameState);
		p.sendInfo(copy);
	}

	/**
	 * checks that extra tile is in valid location
	 * @return true is extra tile is in valid location
     */
	private boolean checkExtraTile()
	{
		//find the extra tile
		int[] extraTileCoor = masterGameState.findExtraTile();

		if (extraTileCoor[0] % 2 != 0 && extraTileCoor[1] % 2 != 0)
		{
			return false;
		}
		else if (extraTileCoor[0] == 0 && extraTileCoor[1] == 0)
		{
			return false;
		}
		else if (extraTileCoor[0] == 0 && extraTileCoor[1] == 8)
		{
			return false;
		}
		else if (extraTileCoor[0] == 8 && extraTileCoor[1] == 0)
		{
			return false;
		}
		else if (extraTileCoor[0] == 8 && extraTileCoor[1] == 8)
		{
			return false;
		}

		return true;
	}
}
