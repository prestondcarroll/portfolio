using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Input;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Tetris
{
    class Board
    {
        private int BOARD_WIDTH = 10;
        private int BOARD_HEIGHT = 22;

        public Color[] TetronimoColors = { 
         /* 0 */   Color.Transparent, 
         /* 1 */ Color.LightSkyBlue, 
         /* 2 */ Color.Orange, 
         /* 3 */ Color.Yellow, 
         /* 4 */ Color.LimeGreen, 
         /* 5 */ Color.BlueViolet, 
         /* 6 */ Color.Red, 
         /* 7 */ Color.Blue
        };

        public enum PlaceStates { CAN_PLACE, BLOCKED, OFFSCREEN };

        public int[,] boardArr;
        Vector2 boardLocation = Vector2.Zero;


        public Board()
        {
            boardArr = new int[BOARD_WIDTH, BOARD_HEIGHT];

            for (int x = 0; x < BOARD_WIDTH; x++)
            {
                for (int y = 0; y < BOARD_HEIGHT; y++)
                {
                    boardArr[x, y] = 0;
                }
            }

            boardLocation = Vector2.Zero;
        }

        // Check to see if there are any already completed lines on the board, if yes remove them 
        public void removeCompleteLines()
        {
            // Start at the bottom of the board and work our way up 
            for (int y = BOARD_HEIGHT - 1; y >= 0; y--)
            {
                // Check to see if the line on row y is complete (non-zero) 
                bool isComplete = true;
                for (int x = 0; x < BOARD_WIDTH; x++)
                {
                    if (boardArr[x, y] == 0)
                    {
                        isComplete = false;
                    }
                }
                if (isComplete)
                {
                    // Row y needs to go bye bye 
                    // Copy row y-1 to row y 
                    for (int yc = y; yc > 0; yc--)
                    {
                        for (int x = 0; x < 10; x++)
                        {
                            boardArr[x, yc] = boardArr[x, yc - 1];
                        }
                    }
                    // Recheck this row 
                    y++;
                    // Score += 100; 
                }
            }
        }

        public PlaceStates canPlace(int[,] piece, int x, int y)
        {
            int dim = piece.GetLength(0);

            for (int px = 0; px < dim; px++)
            {
                for (int py = 0; py < dim; py++)
                {
                    // Calculate where on the game board this segment should be placed 
                    int coordx = x + px;
                    int coordy = y + py;

                    if (piece[px, py] != 0)
                    {
                        // If the board location would be too far to the left or right then 
                        // we are hitting a wall 
                        if (coordx < 0 || coordx >= BOARD_WIDTH)
                        {
                            return PlaceStates.OFFSCREEN;
                        }
                        if (coordy >= BOARD_HEIGHT || boardArr[coordx, coordy] != 0)
                        {
                            return PlaceStates.BLOCKED;
                        }
                    }

                }
            }
            return PlaceStates.CAN_PLACE;
        }


        // Permanently write piece to the game board 
        // Note that this method assumes that the piece can actually be placed already and does not recheck 
        // to make sure the piece can be placed 
        public void place(int[,] piece, int x, int y)
        {
            int dim = piece.GetLength(0);
            for (int px = 0; px < dim; px++)
            {
                for (int py = 0; py < dim; py++)
                {
                    int coordx = x + px;
                    int coordy = y + py;
                    if (piece[px, py] != 0)
                    {
                        boardArr[coordx, coordy] = piece[px, py];
                    }
                }
            }
            removeCompleteLines();
        }

        public bool updateNewStep(Tetrominoe tetro)
        {
            Vector2 newSpawnedPieceLocation = tetro.getLoc() + new Vector2(0, 1);
            int[,] spawnedPiece = tetro.getTetro();

            PlaceStates ps = canPlace(spawnedPiece, (int)newSpawnedPieceLocation.X, (int)newSpawnedPieceLocation.Y);
            if (ps != PlaceStates.CAN_PLACE)
            {
                // We can't move down any further, so place the piece where it is currently
                place(spawnedPiece, tetro.getX(), tetro.getY());
                tetro.spawnTetro();
                spawnedPiece = tetro.getTetro();
                // This is just a check to see if the newly spawned piece is already blocked, in which case the 
                // game is over 
                ps = canPlace(spawnedPiece, tetro.getX(), tetro.getY());
            if (ps == PlaceStates.BLOCKED)
            {
                    //Game Over
                    return true;
                }
            }
            else
            {
                // We can move our piece into the new location, so update the existing piece location 
                tetro.setLoc(newSpawnedPieceLocation);
            }

            return false;
        }

        public void updateSideways(Tetrominoe tetro, KeyboardState ks)
        {
            // Create a new location that contains where we WANT to move the piece
            Vector2 NewSpawnedPieceLocation = tetro.getLoc() + new Vector2(ks.IsKeyDown(Keys.Left) ? -1 : 1, 0);
            // Next, check to see if we can actually place the piece there 
            int[,] spawnedPiece = tetro.getTetro();

            PlaceStates ps = canPlace(spawnedPiece, (int)NewSpawnedPieceLocation.X, (int)NewSpawnedPieceLocation.Y);
            if (ps == PlaceStates.CAN_PLACE)
            {
                tetro.setLoc(NewSpawnedPieceLocation);
            }
        }

        public void updateRotate(Tetrominoe tetro, bool direction)
        {
            int[,] spawnedPiece = tetro.getTetro();

            int[,] newSpawnedPiece = tetro.rotate(spawnedPiece, direction);
            PlaceStates ps = canPlace(newSpawnedPiece, tetro.getX(), tetro.getY());
            if (ps == PlaceStates.CAN_PLACE)
            {
                tetro.setTetro(newSpawnedPiece);
            }
        }


        public int getBoardHeight()
        {
            return BOARD_HEIGHT;
        }

        public int getBoardWidth()
        {
            return BOARD_WIDTH;
        }


        public int getX()
        {
            return (int)boardLocation.X;
        }

        public int getY()
        {
            return (int)boardLocation.Y;
        }

        public int valueAt(int x, int y)
        {
            return boardArr[x, y];
        }

    }
}



