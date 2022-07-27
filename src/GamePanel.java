import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static final int SCREEN_WIDTH = 500;
	static final int SCREEN_HEIGHT = SCREEN_WIDTH;
	static final int UNIT_SIZE = 20;
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE);
	static int DELAY = (int) (60 + (Math.random() * 50));
	static int a = 3;

	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];

	int bodyParts = 4;
	int applesEaten;
	int appleX, appleY;
	char direction = 'R';
	
	static boolean gameOn = false;
	boolean running = false;
	
	Timer timer;
	Random random;
	
	Color colors[] = {Color.CYAN, Color.ORANGE, Color.MAGENTA, Color.YELLOW, Color.WHITE, Color.GREEN, Color.PINK, Color.RED};
	static int foodColor, foodColor1;
	
	GamePanel()
	{
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		this.setBackground(Color.BLACK);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	}
		
	public void startGame()
	{
		newApple();
		running = true;
		timer = new Timer(DELAY,this);
		timer.start();
	}
	
	public void restart()
	{
		bodyParts = 4;
		applesEaten = 0;
		a = 3;
		DELAY = (int) (90 + (Math.random() * 50));
		direction = 'R';
		gameOn = false;
		running = false;
		startGame();
	}

	public void pause()
	{
		gameOn = true;
		timer.stop();
	}
	
	public void resume()
	{
		gameOn = false;
		timer.start();
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g)
	{
		if(running)
		{
			//matrix or grid on panel
			for(int i=UNIT_SIZE/10; i<(SCREEN_HEIGHT/UNIT_SIZE)-1;i++)
				g.drawLine(i*UNIT_SIZE, UNIT_SIZE*2, i*UNIT_SIZE, SCREEN_HEIGHT-(UNIT_SIZE*2));
			
			for(int i=UNIT_SIZE/10; i<(SCREEN_WIDTH/UNIT_SIZE)-1;i++)
				g.drawLine(UNIT_SIZE*2, i*UNIT_SIZE, SCREEN_WIDTH-(UNIT_SIZE*2), i*UNIT_SIZE);				
			
			//generate food
			g.setColor(colors[foodColor]);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			
			//snakebody
			for(int i = 0; i < bodyParts; i++)
			{
				if(i == 0)
				{
					//head
					g.setColor(Color.GREEN);
					g.fillOval(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
				else
				{
					//body
					g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			
			//score while playing
			g.setColor(Color.WHITE);
			g.setFont(new Font("Cambria",Font.BOLD, 30));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score : " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score : " + applesEaten) - 2*UNIT_SIZE), g.getFont().getSize());

			//lives on the top during playing
			foodColor1 = ((int) (Math.random() * 10)) % 3;
			g.setColor(colors[foodColor1]);
			g.setFont(new Font("Cambria",Font.BOLD, 15));
			g.drawString("Lives : x" + a, 2*UNIT_SIZE, g.getFont().getSize());

			//information of the creator of a game
			g.setColor(Color.PINK);
			g.setFont(new Font("Cambria",Font.BOLD, 15));
			g.drawString("Made By: JAY, ROHAN & SMIT", 2*UNIT_SIZE, g.getFont().getSize()*2);

			//instruction of game at the bottom while playing
			g.setColor(Color.RED);
			g.setFont(new Font("Cambria",Font.BOLD, 12));
			FontMetrics metrics1 = getFontMetrics(g.getFont());
			g.drawString("Use ArrowKeys to Play Game!!", (SCREEN_WIDTH - metrics1.stringWidth("Use ArrowKeys to Play Game!!"))/2, SCREEN_HEIGHT-UNIT_SIZE-3);
			g.drawString("Click SpaceBar to Pause & Resume the Game!!", (SCREEN_WIDTH - metrics1.stringWidth("Click SpaceBar to Pause & Resume the Game!!"))/2, SCREEN_HEIGHT-UNIT_SIZE+13);
		}
		
		else
			gameOver(g);
	}
	
	public void newApple()
	{
		//generates coordinate of food
		appleX = (UNIT_SIZE*2 + (int) (Math.random() * ((SCREEN_WIDTH-(UNIT_SIZE*4))/UNIT_SIZE)) * UNIT_SIZE);
		appleY = (UNIT_SIZE*2 + (int) (Math.random() * ((SCREEN_HEIGHT-(UNIT_SIZE*4))/UNIT_SIZE)) * UNIT_SIZE);

		//selects any color from color array for food
		foodColor = ((int) (Math.random() * 10)) % 8;
	}

	public void move()
	{
		for(int i = bodyParts; i>0; i--)
		{
			//shift body of snake i.e. move
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		
		switch(direction)
		{
			//change direction
			case 'U': y[0] -= UNIT_SIZE;
							break;
			case 'D': y[0] += UNIT_SIZE;
							break;
			case 'L': x[0] -= UNIT_SIZE;
					  		break;
			case 'R': x[0] += UNIT_SIZE;
					  		break;
		}
	}

	public void checkApple()
	{
		if((x[0] == appleX) && (y[0] == appleY))
		{
			//increase length of snake
			bodyParts++;
			applesEaten++;
			newApple();
		}
	}
	
	public void checkCollisions()
	{
		//checks if head collides with body
		for(int i = bodyParts; i>0; i--)
		{
			if((x[0] == x[i]) && (y[0] == y[i]))
				running = false;
		}

		//check if head touches any border
		if(x[0] < UNIT_SIZE*2)
			x[0] = SCREEN_WIDTH - (UNIT_SIZE*3);

		if(x[0] > SCREEN_WIDTH - (UNIT_SIZE*2) - 1)
			x[0] = UNIT_SIZE*2;

		if(y[0] < UNIT_SIZE*2)
			y[0] = SCREEN_HEIGHT - (UNIT_SIZE*3);
			
		if(y[0] > SCREEN_HEIGHT - (UNIT_SIZE*2) - 1)
			y[0] = UNIT_SIZE*2;

		if(!running)
			timer.stop();
	}
		
	public void gameOver(Graphics g)
	{
		//final Score
		g.setColor(Color.CYAN);
		g.setFont(new Font("Cambria",Font.BOLD, 40));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("Score: "+ applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());

		//Game Over text
		g.setColor(Color.RED);
		g.setFont(new Font("Cambria",Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);

		//lives text on game over
		g.setColor(Color.ORANGE);
		g.setFont(new Font("Cambria",Font.BOLD, 20));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		if(a>1)
			g.drawString("You have " + (a-1) + " more Lives!!", (SCREEN_WIDTH - metrics3.stringWidth("You have " + (a-1) + " more Lives!!"))/2, SCREEN_HEIGHT - SCREEN_HEIGHT/2 - SCREEN_HEIGHT/4);
		else
			g.drawString("Sorry..!! No more Lives, Please Restart The Game.", (SCREEN_WIDTH - metrics3.stringWidth("Sorry..!! No more Lives, Please Restart The Game."))/2, SCREEN_HEIGHT - SCREEN_HEIGHT/2 - SCREEN_HEIGHT/4);

		//menu type bar after game over
		g.setColor(Color.GREEN);
		g.setFont(new Font("Cambria",Font.BOLD, 20));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("ENTER - Restart Game",  (SCREEN_WIDTH - metrics1.stringWidth("ENTER - RestartGame"))/2, ((SCREEN_HEIGHT/2) + (SCREEN_HEIGHT/4) - (2*UNIT_SIZE)));
		g.drawString("ESC - Exit",  (SCREEN_WIDTH - metrics1.stringWidth("ESC - Exit"))/2, ((SCREEN_HEIGHT/2) + (SCREEN_HEIGHT/4) + (2*UNIT_SIZE)));
		if(a>1)
		{
			g.setColor(Color.GREEN);
			g.setFont(new Font("Cambria",Font.BOLD, 20));
			g.drawString("CLTR - Continue Game",  (SCREEN_WIDTH - metrics1.stringWidth("CLTR - Continue Game"))/2, ((SCREEN_HEIGHT/2) + (SCREEN_HEIGHT/4)));
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(running)
		{
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
	}
	
	public class MyKeyAdapter extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			switch(e.getKeyCode())
			{
				//checks which key is pressed to change the direction of snake & perform specific action
				case KeyEvent.VK_LEFT: if(direction != 'R')
											direction = 'L';
											break;
				
				case KeyEvent.VK_RIGHT: if(direction != 'L')
											direction = 'R';
											break;
				
				case KeyEvent.VK_UP: if(direction != 'D')
											direction = 'U';
											break;
				
				case KeyEvent.VK_DOWN: if(direction != 'U')
											direction = 'D';
											break;

				case KeyEvent.VK_SPACE: if(gameOn)
											resume();
										else
											pause();
											break;

				case KeyEvent.VK_ENTER: if(!running)
											restart();
											break;

				case KeyEvent.VK_CONTROL: if(!running && a>1)
										  {
												a--;
												startGame();
										  }
										  break;

				case KeyEvent.VK_ESCAPE: if(!running)
											System.exit(0);
				
			}
		}
	}
}
