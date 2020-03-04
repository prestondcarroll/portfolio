


//
//
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

@SuppressWarnings( "deprecation" )
public class View extends JPanel{

    JLayeredPane bPane;
    private Timer timer;
    private final int FPS = 1;
    private final int delay = 1000/FPS;
    private boolean gameOver = false;
    int shape_num = 0;


    public View() {
        timer = new Timer(delay, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                update();
                repaint();
            }
        });

        timer.start();
        addKeyListener(new TAdapter());

    }

    public void addBlock(Point[] tetromino){

        String image = "blocks.png";
        for(int i = 0; i < 4; i++){
            JLabel block = new JLabel();
            ImageIcon blockImg =  new ImageIcon(View.class.getResource("res/"+ image));
            block.setIcon(blockImg);
            block.setOpaque(true);

            block.setBounds(tetromino[i].x * 25,tetromino[i].y * 25, 25,25);
            this.add(block, new Integer(1));
        }
    }


    public void clear(){
        //Container c = getContentPane();
        //getContentPane().removeAll();
    }

    public void update(){
        addBlock(Main.tetris.make_tetro(shape_num));
        shape_num++;
        //Tetris.draw_tetro(shape_num);
    }


//    static class keyListener implements ActionListener {
//
//        timer = new Timer(delay, new ActionListener();
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//
//            Tetris.update();
//            Tetris.repaint();
//        }
//    }


    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

//            if (curPiece.getShape() == Tetrominoe.NoShape) {
//
//                return;
//            }

            int keycode = e.getKeyCode();

            // Java 12 switch expressions
            switch (keycode) {
                //case KeyEvent.VK_P -> pause();
                case KeyEvent.VK_LEFT ->  Tetris.setMove(1);//tryMove(curPiece, curX - 1, curY);
                case KeyEvent.VK_RIGHT -> Tetris.setMove(-1);//tryMove(curPiece, curX + 1, curY);
                //case KeyEvent.VK_DOWN -> tryMove(curPiece.rotateRight(), curX, curY);
                //case KeyEvent.VK_UP -> tryMove(curPiece.rotateLeft(), curX, curY);
                //case KeyEvent.VK_SPACE -> dropDown();
                //case KeyEvent.VK_D -> oneLineDown();
            }
        }
    }



}


