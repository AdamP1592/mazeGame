package gamepkg;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Main extends JFrame {
    private static final long serialVersionUID = 1L;

    public Main(){
        setTitle("Maze Game");
        String inputStr = JOptionPane.showInputDialog("Enter maze generator key");
        setSize(800, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Add a dedicated game panel instead of a new Main instance.
        add(new GamePanel());
        setVisible(true);
    }
    
    public static void main(String[] args) {
        // Ensure the GUI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                new Main();
            }
        });
    }
}

class GamePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // Place your game drawing logic here.
    }
}
