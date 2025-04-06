package gamepkg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class GameWindow {
    public static void main(String[] args) {
        startWindow();
    }
    
    public static void startWindow(){
        // Original input panel for rows, columns, and seed
        JTextField rowsField = new JTextField("20");
        JTextField colsField = new JTextField("20");
        JTextField seedField = new JTextField("5124");

        JPanel inputPanel = new JPanel(new GridLayout(3, 3));
        inputPanel.add(new JLabel("Rows:"));
        inputPanel.add(rowsField);
        inputPanel.add(new JLabel("Columns:"));
        inputPanel.add(colsField);
        inputPanel.add(new JLabel("Seed:"));
        inputPanel.add(seedField);

        int result = JOptionPane.showConfirmDialog(
            null,
            inputPanel,
            "Enter Maze Size",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            System.exit(0); // user cancelled
        }

        int rows, cols, seed;
        try {
            rows = Integer.parseInt(rowsField.getText());
            cols = Integer.parseInt(colsField.getText());
            seed = Integer.parseInt(seedField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter integers.");
            return;
        }

        // MazeGame expects width then height, so pass (cols, rows)
        MazeGame game = new MazeGame(cols, rows, seed);

        JFrame frame = new JFrame("Maze Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel panel = new GamePanel(game);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.startGameLoop();  // optional, if you want periodic repainting
    }
}

class GamePanel extends JPanel implements KeyListener {
    final int TILE_SIZE = 16;
    MazeGame game;

    public GamePanel(MazeGame game) {
        this.game = game;
        int rows = game.maze.length;
        int cols = game.maze[0].length;
        // Use the original preferred size (no extra space, since health is drawn on the player's tile)
        this.setPreferredSize(new Dimension(cols * TILE_SIZE, rows * TILE_SIZE));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw maze tiles based on MazeGame state
        for (int y = 0; y < game.maze.length; y++) {
            for (int x = 0; x < game.maze[0].length; x++) {
                Color color = game.maze[y][x].color;
                g.setColor(color);
                g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        // Draw the player from MazeGame state
        Player p = game.p;
        g.setColor(p.color);
        g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE * p.width, TILE_SIZE * p.height);

        // Draw player's health (as in your original display)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, TILE_SIZE - 1));
        g.drawString(String.valueOf(p.health),
                     p.x * TILE_SIZE,
                     p.y * TILE_SIZE + TILE_SIZE - 2);

        // Draw direction indicator (small square just beyond the player's bounding box)
        g.setColor(Color.CYAN);
        int[] movementVector = p.getMovement();
        int noseX = p.x + movementVector[0];
        int noseY = p.y + movementVector[1];
        if (noseX >= 0 && noseX < game.maze[0].length && noseY >= 0 && noseY < game.maze.length) {
            int indicatorSize = TILE_SIZE / 4;
            int centerX = noseX * TILE_SIZE + TILE_SIZE / 2 - indicatorSize / 2;
            int centerY = noseY * TILE_SIZE + TILE_SIZE / 2 - indicatorSize / 2;
            g.fillRect(centerX, centerY, indicatorSize, indicatorSize);
        }
    }

    // Key handling: Q = turn left (0), W = move forward (1), E = turn right (2), S = move backward (3)
    @Override
    public void keyPressed(KeyEvent e) {
        int movementOption = -1;
        int code = e.getKeyCode();
        
        if (code == KeyEvent.VK_Q) { 
            movementOption = 0; 
        } else if (code == KeyEvent.VK_W) { 
            movementOption = 1; 
        } else if (code == KeyEvent.VK_E) { 
            movementOption = 2; 
        } else if (code == KeyEvent.VK_S) { 
            movementOption = 3; 
        }
        
        if (movementOption != -1) {
            game.move(movementOption);
            repaint();
            // Check for win/loss after the move
            if (game.goalReached()) {
                String message = "Congratulations, you've solved the puzzle!";
                String title = "Puzzle Solved";
                Object[] options = {"Start New Puzzle", "Close"};
                int choice = JOptionPane.showOptionDialog(
                        this,
                        message,
                        title,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]
                );
                if (choice == 0) {
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    topFrame.dispose();
                    GameWindow.startWindow();
                } else {
                    System.exit(0);
                }
            } else if (game.isDead()) {
                String message = "YOU DIED :(";
                String title = "Death";
                Object[] options = {"Start New Puzzle", "Close"};
                int choice = JOptionPane.showOptionDialog(
                        this,
                        message,
                        title,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]
                );
                if (choice == 0) {
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    topFrame.dispose();
                    GameWindow.startWindow();
                } else {
                    System.exit(0);
                }
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) { }
    @Override public void keyTyped(KeyEvent e) { }

    // Optional: Timer to periodically repaint if needed
    public void startGameLoop() {
        Timer timer = new Timer(100, e -> repaint());
        timer.start();
    }
}
