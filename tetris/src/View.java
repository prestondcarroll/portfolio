


//
//
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

@SuppressWarnings( "deprecation" )
public class View extends JFrame{

    static JLayeredPane bPane;


    public View() {
        super("Tetris");
        // Set the exit option for the JFrame
        bPane = getLayeredPane();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setSize(500,800);

        addBlock();


    }

    public void addBlock(){

        JLabel block = new JLabel();
        String image = "blocks.png";

        ImageIcon blockImg =  new ImageIcon(View.class.getResource("res/"+ image));
        //ImageIcon blockImg =  new ImageIcon(View.class.getResource("C:\\Users\\rubbe\\Desktop\\Preston\\github\\repo\\tetris\\res\\blocks.png"));
        block.setIcon(blockImg);
        block.setOpaque(true);

        //block.setBounds(21,69,blockImg.getIconWidth(),blockImg.getIconHeight());
        block.setBounds(0,0, 25,25);
        bPane.add(block, new Integer(1));




//        JLabel block2 = new JLabel();
//        ImageIcon blockImg =  new ImageIcon(View.class.getResource("res/"+ image));
//        //ImageIcon blockImg =  new ImageIcon(View.class.getResource("C:\\Users\\rubbe\\Desktop\\Preston\\github\\repo\\tetris\\res\\blocks.png"));
//        block.setIcon(blockImg);
//        block.setOpaque(true);
//
//        //block.setBounds(21,69,blockImg.getIconWidth(),blockImg.getIconHeight());
//        block.setBounds(0,0, 25,25);
//        bPane.add(block, new Integer(1));

    }



}


