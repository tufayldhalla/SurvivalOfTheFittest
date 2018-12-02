/** The "MazeGame" class.
 * @author
 * @version last updated December 2011
 */
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.image.ImageObserver;

public class SOTF extends JFrame
{
	// Program variables
	private final int TIME_INTERVAL = 100;
	private Timer timer;

	private int keysCollected = 0;
	private int play = 0;
	private int level = 1;
	private int lives = 0;
	private int mode = 1;

	private int cop1Row;
	private int cop1Column;
	private int cop2Row;
	private int cop2Column;
	private int cop3Row;
	private int cop3Column;

	private int copLevel = 0;
	private Timer copTimer;
	private MazeArea mazeArea;
	AudioClip song;

	public SOTF() throws IOException
	{

		// Set up the frame and the grid
		super("Survival of the Fittest");

		setLocation(15, 50);
		setIconImage(Toolkit.getDefaultToolkit().getImage("robber.png"));
		setResizable(false);

		// Set up for the maze area
		Container contentPane = getContentPane();
		mazeArea = new MazeArea();
		contentPane.add(mazeArea, BorderLayout.CENTER);
		// Tried to add audio but it does not work
		song = Applet.newAudioClip(getCompleteURL("loop.au"));
		song.play();
	}

	// Gets the URL needed for newAudioClip also audio
	public URL getCompleteURL(String fileName)
	{
		try
		{
			return new URL("file:" + System.getProperty("user.dir") + "/"
					+ fileName);
		}
		catch (MalformedURLException e)
		{
			System.err.println(e.getMessage());
		}
		return null;
	}

	/*
	 * Adds the menu bar for the game
	 */
	private void addMenus()
	{
		// Sets the bar
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setMnemonic('G');

		// Sets the sub-menu (Main Menu)
		final JMenuItem mainMenu = new JMenuItem("Main Menu");
		mainMenu.addActionListener(new ActionListener() {
			/**
			 * Responds to the Main menu selection
			 * 
			 * @param event The event that selected this menu option
			 */
			public void actionPerformed(ActionEvent event)
			{

				play = 2;
				lives = 3;
				level = 1;
				copTimer.stop();
				repaint();
			}

		});

		// Sets the sub-menu (New Game)
		final JMenuItem newOption = new JMenuItem("New Game");
		newOption.addActionListener(new ActionListener() {
			/**
			 * Responds to the New Game choice
			 * 
			 * @param event The event that selected this menu option
			 */
			public void actionPerformed(ActionEvent event)
			{

				song.play();
				play = 1;
				lives = 3;
				keysCollected = 0;
				level = 1;
				SOTF.this.mazeArea.newGame("maze1.txt");
				repaint();
			}

		});

		// Sets the sub-menu (Exit)
		JMenuItem exitOption = new JMenuItem("Exit");
		exitOption.setMnemonic('x');
		exitOption.addActionListener(new ActionListener() {
			/**
			 * Responds to the Exit Menu choice
			 * 
			 * @param event The event that selected this menu option
			 */
			public void actionPerformed(ActionEvent event)
			{
				hide();
				System.exit(0);

			}
		});
		// Draws on the Option bar with the submenu
		gameMenu.add(newOption);
		gameMenu.add(mainMenu);
		gameMenu.add(exitOption);
		menuBar.add(gameMenu);

		// New option for "Mode"
		JMenu modeMenu = new JMenu("Mode");
		modeMenu.setMnemonic('M');

		// Sub menu for 'easy'
		final JMenuItem mode1 = new JMenuItem("Easy");
		mode1.addActionListener(new ActionListener() {
			/**
			 * Responds to the Exit Menu choice
			 * 
			 * @param event The event that selected this menu option
			 */
			public void actionPerformed(ActionEvent event)
			{
				copLevel = 1;
				mode = 1;
				cop2Row = 0;
				cop2Column = 0;
				cop3Row = 0;
				cop3Column = 0;

			}
		});

		// Sub menu for 'medium'
		final JMenuItem mode2 = new JMenuItem("Medium");
		mode2.addActionListener(new ActionListener() {
			/**
			 * Responds to the Mode Menu choice
			 * 
			 * @param event The event that selected this menu option
			 */
			public void actionPerformed(ActionEvent event)
			{
				copLevel = 2;
				mode = 2;
				cop2Row = 12;
				cop2Column = 10;
				cop3Row = 0;
				cop3Column = 0;
			}
		});

		// Sub menu for 'hard'
		final JMenuItem mode3 = new JMenuItem("Hard");
		mode3.addActionListener(new ActionListener() {
			/**
			 * Responds to the mode Menu choice
			 * 
			 * @param event The event that selected this menu option
			 */
			public void actionPerformed(ActionEvent event)
			{
				copLevel = 3;
				mode = 3;
				cop2Row = 12;
				cop2Column = 10;
				cop3Row = 13;
				cop3Column = 14;
			}
		});

		menuBar.add(modeMenu);
		modeMenu.add(mode1);
		modeMenu.add(mode2);
		modeMenu.add(mode3);
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		JMenuItem rulesOption = new JMenuItem("Rules");
		rulesOption.setMnemonic('R');
		rulesOption.addActionListener(new ActionListener() {
			/**
			 * Responds to the Help Menu choice
			 * 
			 * @param event The event that selected this menu option
			 */
			public void actionPerformed(ActionEvent event)
			{
				JOptionPane
						.showMessageDialog(
								newOption,
								"The objective of the game is to run through the maze and \n"
										+ "try to find all keys and open the jail cell and brake free to \n"
										+ "get the gold coin to enter the next maze.\n",
								"Help", JOptionPane.INFORMATION_MESSAGE);
			}

		});
		JMenuItem aboutOption = new JMenuItem("About...");
		aboutOption.setMnemonic('A');
		aboutOption.addActionListener(new ActionListener() {
			/**
			 * Responds to the About Menu choice
			 * 
			 * @param event The event that selected this menu option
			 */
			public void actionPerformed(ActionEvent event)
			{
				JOptionPane.showMessageDialog(newOption,
						"\u00a9 2014 By Jacob Zachariah & Tufayl Dhalla",
						"About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		helpMenu.add(rulesOption);
		helpMenu.add(aboutOption);
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);

	}
	// Inner class for the maze area
	private class MazeArea extends JPanel
	{

		Image titlePage = new ImageIcon("TitlePage.png").getImage();
		Image playKey = new ImageIcon("P" + "lay Key.png").getImage();
		Image helpKey = new ImageIcon("Help Key.png").getImage();
		Image controlsKey = new ImageIcon("Controls Key.png").getImage();
		Image quitKey = new ImageIcon("Quit Key.png").getImage();
		Image backKey = new ImageIcon("Back Key.png").getImage();
		Image header = new ImageIcon("Caution Sign.jpg").getImage();
		Image controlsMenu = new ImageIcon("Controls Menu.jpg").getImage();
		Image helpMenu = new ImageIcon("Helps Menu.jpg").getImage();
		Image cop = new ImageIcon("Cop.png").getImage();
		Image robberCaught = new ImageIcon("Cop got Robber.png").getImage();
		Image panel = new ImageIcon("side panel.jpg").getImage();
		Image life = new ImageIcon("heart.png").getImage();
		Image loose = new ImageIcon("game over.jpg").getImage();
		Image win = new ImageIcon("game winner.jpg").getImage();

		private final int PATH = 0;
		private final int WALL = 1;
		private final int JAIL = 2;
		private final int COIN = 3;
		private final int KEY = 4;

		private final int IMAGE_WIDTH;
		private final int IMAGE_HEIGHT;

		private Image[] gridImages;
		private Image playerImage;

		// Variables to keep track of the grid and the player position
		private int[][] grid;
		private int currentRow;
		private int currentColumn;

		private int coinCollected = 0;

		private final int TIME_INTERVAL = 400;

		/**
		 * Constructs a new MazeArea object
		 */
		public MazeArea()
		{
			// Create an array for the gridImages and load them up
			// Also load up the player image
			gridImages = new Image[6];

			gridImages[0] = new ImageIcon("path.gif").getImage();
			gridImages[1] = new ImageIcon("brick.gif").getImage();
			gridImages[2] = new ImageIcon("Jail.png").getImage();
			gridImages[3] = new ImageIcon("Coin.png").getImage();
			gridImages[4] = new ImageIcon("Key.png").getImage();
			gridImages[5] = new ImageIcon("cop.png").getImage();
			playerImage = new ImageIcon("robber.png").getImage();
			cop = new ImageIcon("cop.png").getImage();

			setFont(new Font("Arial", Font.BOLD, 40));
			// Starts a new game and loads up the grid (sets size of grid array)
			newGame("maze1.txt");

			// Set the image height and width based on the path image size
			// Also sizes this panel based on the image and grid size
			IMAGE_WIDTH = gridImages[0].getWidth(this);
			IMAGE_HEIGHT = gridImages[0].getHeight(this);

			Dimension size = new Dimension(grid[0].length * IMAGE_WIDTH,
					grid.length * IMAGE_HEIGHT);
			this.setPreferredSize(size);

			// Sets up for keyboard input (arrow keys) on this panel
			this.setFocusable(true);
			this.addKeyListener(new KeyHandler());
			this.requestFocusInWindow();

			addMouseListener(new MouseHandler());
			copTimer = new Timer(TIME_INTERVAL, new TimerEventHandler());
			play = 2;
			lives = 3;

		}

		/**
		 * Repaint the drawing panel
		 * 
		 * @param g The Graphics context
		 */
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			if (play == 1)
			{
				// Redraw the grid with current images
				for (int row = 0; row < grid.length; row++)
					for (int column = 0; column < grid[0].length; column++)
					{
						// Put a path underneath everywhere
						g.drawImage(gridImages[0], column * IMAGE_WIDTH, row
								* IMAGE_HEIGHT, this);
						int imageNo = grid[row][column];
						g.drawImage(gridImages[imageNo], column * IMAGE_WIDTH,
								row * IMAGE_HEIGHT, this);
					}

				// Draw the moving player on top of the grid
				g.drawImage(playerImage, currentColumn * IMAGE_WIDTH,
						currentRow * IMAGE_HEIGHT, this);
				if (copLevel == 1 || copLevel == 0)
				{
					// Draw the cop on top of the grid
					g.drawImage(cop, cop1Column * IMAGE_WIDTH, cop1Row
							* IMAGE_HEIGHT, this);
				}
				else if (copLevel == 2)
				{
					// Draw the cop on top of the grid
					g.drawImage(cop, cop1Column * IMAGE_WIDTH, cop1Row
							* IMAGE_HEIGHT, this);
					// Draw the cop on top of the grid
					g.drawImage(cop, cop2Column * IMAGE_WIDTH + 10, cop2Row
							* IMAGE_HEIGHT, this);
				}

				else if (copLevel == 3)
				{
					// Draw the cop on top of the grid
					g.drawImage(cop, cop1Column * IMAGE_WIDTH, cop1Row
							* IMAGE_HEIGHT, this);
					// Draw the cop on top of the grid
					g.drawImage(cop, cop2Column * IMAGE_WIDTH, cop2Row
							* IMAGE_HEIGHT, this);
					g.drawImage(cop, cop3Column * IMAGE_WIDTH, cop3Row
							* IMAGE_HEIGHT, this);
				}
				// Draw the side panel
				g.drawImage(panel, 1000, 0, 280, 840, this);

				// Draw the lives
				if (lives >= 1)
				{
					g.drawImage(life, 1020, 410, 60, 60, this);
				}
				if (lives >= 2)
				{
					g.drawImage(life, 1085, 410, 60, 60, this);
				}
				if (lives >= 3)
				{
					g.drawImage(life, 1150, 410, 60, 60, this);
				}
				if (mode == 1 || mode == 0)
				{
					g.setColor(Color.BLACK);
					g.drawString("Easy", 1030, 710);
					repaint();
				}
				else if (mode == 2)
				{
					g.setColor(Color.BLACK);
					g.drawString("Medium", 1030, 710);
					repaint();
				}
				else if (mode == 3)
				{
					g.setColor(Color.BLACK);
					g.drawString("Hard", 1030, 710);
					repaint();
				}
				if (level == 1)
				{
					g.setColor(Color.BLACK);
					g.drawString("1", 1100, 320);
					repaint();
				}
				else if (level == 2)
				{
					g.setColor(Color.BLACK);
					g.drawString("2", 1100, 320);
					repaint();
				}
				else if (level == 3)
				{
					g.setColor(Color.BLACK);
					g.drawString("3", 1100, 320);
					repaint();
				}
				else if (level == 4)
				{
					g.setColor(Color.BLACK);
					g.drawString("4", 1100, 320);
					repaint();
				}
				else if (level == 5)
				{
					g.setColor(Color.BLACK);
					g.drawString("5", 1100, 320);
					repaint();
				}
				if (keysCollected == 0)
				{
					g.setColor(Color.BLACK);
					g.drawString("0", 1100, 580);
					repaint();
				}
				else if (keysCollected == 1)
				{
					g.setColor(Color.BLACK);
					g.drawString("1", 1100, 580);
					repaint();
				}
				else if (keysCollected == 2)
				{
					g.setColor(Color.BLACK);
					g.drawString("2", 1100, 580);
					repaint();
				}
				else if (keysCollected == 3)
				{
					g.setColor(Color.BLACK);
					g.drawString("3", 1100, 580);
					repaint();
				}
			}
			if (play == 2)
			{
				g.drawImage(titlePage, 0, 136, 1240, 581, this);
				g.drawImage(playKey, 0, 640, 240, 75, this);
				g.drawImage(helpKey, 333, 640, 240, 75, this);
				g.drawImage(controlsKey, 665, 640, 240, 75, this);
				g.drawImage(quitKey, 1000, 640, 240, 75, this);
				g.drawImage(header, 0, 0, 1240, 136, this);
				g.drawImage(header, 0, 717, 1240, 136, this);

			}
			if (play == 3)
			{
				g.drawImage(helpMenu, 0, 0, 1240, 850, this);
				g.drawImage(backKey, 1000, 0, 240, 75, this);
			}
			if (play == 4)
			{
				g.drawImage(controlsMenu, 0, 0, 1240, 850, this);
				g.drawImage(backKey, 1000, 0, 240, 75, this);
			}
			if (level == 6)
			{
				g.drawImage(win, 0, 0, 1240, 850, this);
				play = 1;
				lives = 3;

				keysCollected = 0;
				SOTF.this.mazeArea.newGame("maze1.txt");

			}
			if (lives == 0 && play == 1)
			{
				g.drawImage(loose, 0, 0, 1240, 850, this);
				level = 0;
				keysCollected = 0;
				SOTF.this.mazeArea.newGame("maze1.txt");
			}

		} // paint component method

		private void moveCop()
		{
			if (grid[cop1Row][cop1Column - 1] != WALL
					&& grid[cop1Row][cop1Column - 1] != JAIL
					&& cop1Column > currentColumn)
			{

				cop1Column--;
			}
			if (grid[cop1Row][cop1Column + 1] != WALL
					&& grid[cop1Row][cop1Column + 1] != JAIL
					&& cop1Column < currentColumn)
			{
				cop1Column++;
			}
			if (grid[cop1Row - 1][cop1Column] != WALL
					&& grid[cop1Row - 1][cop1Column] != JAIL
					&& cop1Row > currentRow)
			{
				cop1Row--;
			}
			if (grid[cop1Row + 1][cop1Column] != WALL
					&& grid[cop1Row + 1][cop1Column] != JAIL
					&& cop1Row < currentRow)
			{
				cop1Row++;
			}

			if (mode == 2 || mode == 3)
			{
				if (grid[cop2Row + 1][cop2Column] != WALL
						&& grid[cop2Row + 1][cop2Column] != JAIL
						&& cop2Row < currentRow)
				{
					cop2Row++;
				}
				if (grid[cop2Row][cop2Column - 1] != WALL
						&& grid[cop2Row][cop2Column - 1] != JAIL
						&& cop2Column > currentColumn)
				{
					cop2Column--;
				}

				if (grid[cop2Row][cop2Column + 1] != WALL
						&& grid[cop2Row][cop2Column + 1] != JAIL
						&& cop2Column < currentColumn)
				{
					cop2Column++;
				}
				if (grid[cop2Row - 1][cop2Column] != WALL
						&& grid[cop2Row - 1][cop2Column] != JAIL
						&& cop2Row > currentRow)
				{
					cop2Row--;
				}
			}

			if (mode == 3)
			{
				if (grid[cop3Row][cop3Column - 1] != WALL
						&& grid[cop3Row][cop3Column - 1] != JAIL
						&& cop3Column > currentColumn)
				{
					cop3Column--;
				}

				if (grid[cop3Row][cop3Column + 1] != WALL
						&& grid[cop3Row][cop3Column + 1] != JAIL
						&& cop3Column < currentColumn)

				{
					cop3Column++;
				}

				if (grid[cop3Row - 1][cop3Column] != WALL
						&& grid[cop3Row - 1][cop3Column] != JAIL
						&& cop3Row > currentRow)
				{
					cop3Row--;
				}

				if (grid[cop3Row + 1][cop3Column] != WALL
						&& grid[cop3Row + 1][cop3Column] != JAIL
						&& cop3Row < currentRow)
				{
					cop3Row++;
				}
			}
			if (cop1Row == currentRow && cop1Column == currentColumn
					|| cop2Row == currentRow && cop2Column == currentColumn
					|| cop3Row == currentRow && cop3Column == currentColumn)
			{
				lives--;
				currentRow = 1;
				currentColumn = 1;
				cop1Row = 10;
				cop1Column = 10;
				cop2Row = 12;
				cop2Column = 10;
				cop3Row = 15;
				cop3Column = 10;
				try
				{
					Thread.sleep(700);

				}
				catch (InterruptedException e)
				{
				}
			}

		}

		public void newGame(String mazeFileName)
		{

			// Initial position of the player
			// lives = 3;
			currentRow = 1;
			currentColumn = 1;

			// Since the game starts of with Easy the first cop position is
			// declared here
			if (mode == 1 || mode == 2 || mode == 3)
			{
				cop1Row = 10;
				cop1Column = 10;
			}
			if (mode == 2 || mode == 3)
			{
				cop2Row = 12;
				cop2Column = 10;

			}
			if (mode == 3)
			{
				cop3Row = 13;
				cop3Column = 14;
			}
			// Load up the file for the maze (try catch, is for file io errors)
			try
			{
				// Find the size of the file first to size the array
				// Standard Java file input (better than hsa.TextInputFile)
				BufferedReader mazeFile = new BufferedReader(new FileReader(
						mazeFileName));

				// Assume file has at least 1 line
				int noOfRows = 1;
				String rowStr = mazeFile.readLine();
				int noOfColumns = rowStr.length();

				// Read and count the rest of rows until the end of the file
				String line;
				while ((line = mazeFile.readLine()) != null)
				{
					noOfRows++;
				}
				mazeFile.close();

				// Set up the array
				grid = new int[noOfRows][noOfColumns];

				// Load in the file data into the grid (Need to re-open first)
				mazeFile = new BufferedReader(new FileReader(mazeFileName));
				for (int row = 0; row < grid.length; row++)
				{
					rowStr = mazeFile.readLine();
					for (int column = 0; column < grid[0].length; column++)
					{
						grid[row][column] = (int) (rowStr.charAt(column) - '0');
					}
				}
				mazeFile.close();
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(this, mazeFileName
						+ " not a valid maze file",
						"Message - Invalid Maze File",
						JOptionPane.WARNING_MESSAGE);
				System.exit(0);
			}
		}
		/**
		 * An inner class to deal with the timer events
		 */
		private class TimerEventHandler implements ActionListener
		{

			/**
			 * The following method is called each time a timer event is
			 * generated (every TIME_INTERVAL milliseconds in this example) Put
			 * your code here that handles this event
			 * 
			 * @param event the Timer event
			 */
			public void actionPerformed(ActionEvent event)
			{
				moveCop();

				// Repaint the screen after moving the cop
				repaint();
			}

		}

		private class MouseHandler extends MouseAdapter implements
				ImageObserver
		{

			public void mouseClicked(MouseEvent event)
			{
				Point mousePoint = event.getPoint();

				if (play != 1)
				{

					if (mousePoint.x >= 0 && mousePoint.x <= 240
							&& mousePoint.y >= 640 && mousePoint.y <= 715)
					{
						play = 1;
						// MazeGame.this.setVisible(false);
						// Add in the menus

						addMenus();
						SOTF.this.pack();
						SOTF.this.setVisible(true);
						copTimer.start();
						repaint();

					}

					if (mousePoint.x >= 332 && mousePoint.x <= 572
							&& mousePoint.y >= 640 && mousePoint.y <= 715)
					{

						play = 3;
						copTimer.stop();
					}

					if (mousePoint.x >= 665 && mousePoint.x <= 905
							&& mousePoint.y >= 640 && mousePoint.y <= 715)
					{
						play = 4;
						copTimer.stop();
					}

					if (mousePoint.x >= 1000 && mousePoint.x <= 1239
							&& mousePoint.y >= 640 && mousePoint.y <= 715)
					{
						SOTF.this.setVisible(false);
						SOTF.this.hide();
					}

					if (mousePoint.x >= 1000 && mousePoint.x <= 1239
							&& mousePoint.y >= 0 && mousePoint.y <= 75)
					{
						play = 2;
					}
					repaint();

				}
			}

			@Override
			public boolean imageUpdate(Image img, int infoflags, int x, int y,
					int width, int height)
			{
				return false;
			}

		}

		// Inner class to handle key events
		private class KeyHandler extends KeyAdapter
		{

			public void keyPressed(KeyEvent event)
			{
				if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					play = 2;
					return;
				}
				// Change the currentRow and currentColumn of the player
				// based on the key pressed
				if (event.getKeyCode() == KeyEvent.VK_LEFT
						|| event.getKeyCode() == KeyEvent.VK_A)
				{

					if (grid[currentRow][currentColumn - 1] != WALL
							&& grid[currentRow][currentColumn - 1] != JAIL)
					{

						currentColumn--;
					}
				}
				else if (event.getKeyCode() == KeyEvent.VK_RIGHT
						|| event.getKeyCode() == KeyEvent.VK_D)
				{
					if (grid[currentRow][currentColumn + 1] != WALL
							&& grid[currentRow][currentColumn + 1] != JAIL)

					{

						currentColumn++;
					}
				}
				else if (event.getKeyCode() == KeyEvent.VK_UP
						|| event.getKeyCode() == KeyEvent.VK_W)
				{
					if (grid[currentRow - 1][currentColumn] != WALL
							&& grid[currentRow - 1][currentColumn] != JAIL)
					{
						currentRow--;
					}
				}
				else if (event.getKeyCode() == KeyEvent.VK_DOWN
						|| event.getKeyCode() == KeyEvent.VK_S)
				{
					if (grid[currentRow + 1][currentColumn] != WALL
							&& grid[currentRow + 1][currentColumn] != JAIL)
					{
						currentRow++;
					}
				}
				// Get the keys
				if (grid[currentRow][currentColumn] == KEY)
				{
					keysCollected++;
					grid[currentRow][currentColumn] = PATH;
				}
				// Gets the coins
				if (grid[currentRow][currentColumn] == COIN)
				{
					coinCollected++;
					grid[currentRow][currentColumn] = PATH;
				}

				if (keysCollected == 3)
				{
					if (level == 1 || level == 2)
					{
						grid[1][17] = PATH;
						grid[1][18] = PATH;
					}
					if (level == 3)
					{
						grid[19][17] = PATH;
						grid[19][18] = PATH;
					}
					if (level == 4)
					{
						grid[9][21] = PATH;
						grid[9][22] = PATH;
						grid[9][23] = PATH;
					}
					if (level == 5)
					{
						grid[2][13] = PATH;
						grid[2][14] = PATH;
					}

				}
				if (coinCollected == 1)
				{
					currentRow = 1;
					currentColumn = 1;
					keysCollected = 0;
					coinCollected = 0;
					level++;
					if (level == 2)
					{
						newGame("maze2.txt");
					}
					else if (level == 3)
					{
						newGame("maze3.txt");
					}
					else if (level == 4)
					{
						newGame("maze4.txt");
					}
					else if (level == 5)
					{
						newGame("maze5.txt");
					}
				}

				// Repaint the screen after the change
				repaint();
			}
		}
	}

	// Sets up the main frame for the Game
	public static void main(String[] args) throws IOException
	{
		SOTF frame = new SOTF();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	} // main method
} // MazeGame class

