using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Collections.Generic;
using Microsoft.Xna.Framework.Graphics;
using System.Security.Cryptography.X509Certificates;

namespace Tetris
{
    class Tetrominoe
    {
        //private int[,] coords;
        int[,] tetro;
        private static List<int[,]> pieces;

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

        Vector2 tetroLocation; 



        public Tetrominoe()
        {
            // coords = new int[4, 2];

            pieces = new List<int[,]>();
            /* I Piece */
            pieces.Add(new int[4, 4] { { 0, 0, 0, 0 }, { 1, 1, 1, 1 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } });
            /* L Piece */
            pieces.Add(new int[3, 3] { { 0, 0, 1 }, { 1, 1, 1 }, { 0, 0, 0 } });
            /* O Piece */
            pieces.Add(new int[2, 2] { { 1, 1 }, { 1, 1 } });
            /* S Piece */
            pieces.Add(new int[3, 3] { { 0, 1, 1 }, { 1, 1, 0 }, { 0, 0, 0 } });
            /* T Piece */
            pieces.Add(new int[3, 3] { { 0, 1, 0 }, { 1, 1, 1 }, { 0, 0, 0 } });
            /* Z Piece */
            pieces.Add(new int[3, 3] { { 1, 1, 0 }, { 0, 1, 1 }, { 0, 0, 0 } });
            /* J Piece */
            pieces.Add(new int[3, 3] { { 1, 0, 0 }, { 1, 1, 1 }, { 0, 0, 0 } });

            spawnTetro();

        }



        public void spawnTetro()
        {

            Random r = new Random();
            int color = r.Next(0, pieces.Count);


            tetro = (int[,])pieces[color].Clone();
            int dim = tetro.GetLength(0);


            for (int x = 0; x < dim; x++) 
            {
                for (int y = 0; y < dim; y++)
                {
                        tetro[x, y] *= (color + 1);
                }
            }
            tetroLocation.X = 3;
            tetroLocation.Y = 0;
        }

        public int[,] rotate(int[,] tetroPiece, bool left)
        {

            int dim = tetroPiece.GetLength(0);
            int[,] npiece = new int[dim, dim];

            for (int i = 0; i < dim; i++)
            {
                for (int j = 0; j < dim; j++)
                {
                    if (left)
                    {
                        npiece[j, i] = tetro[i, dim - 1 - j];
                    }
                    else
                    {
                        npiece[j, i] = tetro[dim - 1 - i, j];
                    }                   
                }
            }
            return npiece;
        }

/*
        public void changeX(int x)
        {
            tetroLocation.X += x;
        }

        public void changeY(int y)
        {
            tetroLocation.Y += y;
        }*/


        public int getX()
        {
            return (int)tetroLocation.X;
        }

        public int getY()
        {
            return (int)tetroLocation.Y;
        }

        public int[,] getTetro()
        {
            return tetro;
        }

        public int valueAt(int x, int y)
        {
            return tetro[x, y];
        }

        public int getDim()
        {
            return tetro.GetLength(0);
        }

        public Vector2 getLoc()
        {
            return tetroLocation;
        }

        public void setLoc(Vector2 loc)
        {
            tetroLocation = loc;
        }

        public void setTetro(int[,] newTetro)
        {
            tetro = newTetro;
        }


    }
}
