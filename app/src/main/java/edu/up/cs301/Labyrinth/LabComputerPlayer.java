package edu.up.cs301.Labyrinth;

import java.util.Random;

import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.infoMsg.NotYourTurnInfo;

/**
 * This is a really dumb computer player that always just makes a random move
 * it's so stupid that it sometimes tries to make moves on non-blank spots.
 * 
 * @author Chloe Kuhar
 * @author Liz Frick
 * @author Nicole Kister
 * @author Mikayla Whiteaker
 * @version Nov 2016, preAlpha
 */
public class LabComputerPlayer extends GameComputerPlayer
{
    public LabComputerPlayer()
    {
        super("hi");
    }

    protected void receiveInfo(GameInfo info) {
        //Coordinates for the player to send to the local game
        int xCordMaze;
        int yCordMaze;
        int xCordPiece;
        int yCordPiece;

        //If it isn't the computer's turn, ignore
        if(info instanceof NotYourTurnInfo){
            return;
        }

        //Pick a random spot to place the extra tile
        Random rand = new Random();
        xCordMaze = rand.nextInt(8);
        yCordMaze = rand.nextInt(8);
        //If the random spot is not even, pick again
        while(xCordMaze % 2 != 0)
        {
            xCordMaze = rand.nextInt(8);
        }
        while(yCordMaze % 2 != 0)
        {
            yCordMaze = rand.nextInt(8);
        }
        //Delay to make it look like the player is thinking
        sleep(3000);
        //Then send the move maze action to the local game
        game.sendAction(new LabMoveMazeAction(this, xCordMaze, yCordMaze));

        //Pick a random spot to move the player
        xCordPiece = rand.nextInt(6) + 1;
        yCordPiece = rand.nextInt(6) + 1;
        //Delay to make it look like the player is thinking
        sleep(3000);
        //Then send the move piece action to the local game
        game.sendAction(new LabMovePieceAction(this, xCordPiece, yCordPiece));
    }
}
