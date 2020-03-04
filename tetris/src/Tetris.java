



//
//
//
import java.awt.Point;

public class Tetris {

    //static Point[] tetromino = new Point[4];
    static int dx = 0;
    static boolean rotate = false;
    int colorNum = 1;

    static Point[] tetromino = {
            new Point(0,0),
            new Point(0, 0),
            new Point(0, 0),
            new Point(0, 0)
    };

    static View view = Main.getView();

    static int [][]  shapes = {
            {1,3,5,7},  //I -> 0
            {2,4,5,7},  //Z -> 1
            {3,5,4,6},  //S -> 2
            {3,5,4,7},  //T -> 3
            {2,3,5,7},  //L -> 4
            {3,5,7,6},  //J -> 5
            {2,3,4,5},  //O -> 6
    };


    public static Point[] make_tetro(int shape_num){
        for(int i = 0; i < 4; i++){
            tetromino[i].x = shapes[shape_num][i] % 2;
            tetromino[i].y = shapes[shape_num][i] / 2;
        }

        return tetromino;

        //view.clear();
        //view.addBlock(tetromino);
    }

    public static void setMove(int num){
        dx = num;
    }

}
