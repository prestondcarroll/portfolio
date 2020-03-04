

//
//

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Timer;
import javax.swing.*;

public class Main extends JFrame{


    private JFrame Main;
    static private View view;
    public static Tetris tetris;


    public Main() {
        super("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setSize(500, 800);
        setResizable(false);

        view = new View();
        add(view);

        tetris = new Tetris();

    }

    public static View getView(){
        return view;
    }




    public static void main (String args[]){
        Main myMain = new Main();
    }






}
