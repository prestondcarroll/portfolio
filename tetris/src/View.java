


//
//
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

@SuppressWarnings( "deprecation" )
public class View extends JFrame{

    JLayeredPane bPane;


    public View() {
        super("Tetris");
        // Set the exit option for the JFrame
        bPane = getLayeredPane();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setSize(500,800);

        //addBlock();
    }

    public void addBlock(Point[] tetromino){

        String image = "blocks.png";
        for(int i = 0; i < 4; i++){
            JLabel block = new JLabel();
            ImageIcon blockImg =  new ImageIcon(View.class.getResource("res/"+ image));
            block.setIcon(blockImg);
            block.setOpaque(true);

            block.setBounds(tetromino[i].x * 25,tetromino[i].y * 25, 25,25);
            bPane.add(block, new Integer(1));
        }
    }


    public void clear(){
        //Container c = getContentPane();
        getContentPane().removeAll();
    }



}


