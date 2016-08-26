/*Cubed.java
 *
 *Annie Zhang
 *02/16/2016
 *ICS4U
 *Mr. McKenzie
 *
 *Simple game project:
 *Objective of the game is to pass all levels by moving
 *the player's cube on top of a layout of cubes, to the final red cube.
 *Gray cubes have 1 life
 *Purple cubes have 2 lives
 *Green cubes add cubes to the layout
 *Blue cubes teleport the player to a different spot
 *Failure to clear all cubes before reaching the red cube or not
 *reaching the red cube at all means the level was not cleared.
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.applet.*;
import javax.sound.sampled.AudioSystem;

//Cubed - contains main()
public class Cubed extends JFrame implements ActionListener{
	Timer myTimer = new Timer(10, this);   // trigger every 10 ms
	GamePanel game;
	GameMenu gm;
	AudioClip back;
    public Cubed() {
		super("Cubed");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,570);
		game = new GamePanel(this);
		add(game);
		setResizable(false);
		gm = new GameMenu(this);
		back = Applet.newAudioClip(getClass().getResource("dreamy.au"));
		back.loop();
		
    }
	
	public void start(){	//Makes sure to only start once everything is loaded
		myTimer.start();
	}
	
	public void actionPerformed(ActionEvent evt){
		game.move();
		game.repaint();
	}

    public static void main(String[] arguments) {
		Cubed frame = new Cubed();		
    }
}
//Separate JFrame for Menu
class GameMenu extends JFrame implements ActionListener{
	Cubed main;
	JButton play, instr;	//Buttons to start game and to display instructions
	Image bg,cubed;			//Images of background and title
	public GameMenu(Cubed m){
		main = m;
		setSize(800,570);
		setLayout(null);
		bg = new ImageIcon("background.png").getImage();
		cubed = new ImageIcon("cubed.png").getImage();
		
		play = new JButton("Play");
		instr = new JButton("Instructions");
		
		play.addActionListener(this);
		instr.addActionListener(this);
		
		play.setBounds(330,365,140,40);
		instr.setBounds(330,415,140,40);
		
		add(play);
		add(instr);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent evt){
    	if (evt.getSource()==play){
    		setVisible(false);
    		main.setVisible(true);
    	}
    	if (evt.getSource()==instr){
    		JOptionPane.showMessageDialog(null, "Use arrow keys to move.\nRed cube is your final desination.\nGrey-you can visit only once.\nPurple-you can visit twice.\nGreen-help build\nBlue-teleportation");
    	}
    }
    public void paint(Graphics g){		
    	if (bg!=null && cubed!=null){
    		g.drawImage(bg,0,0,this);
    		g.drawImage(cubed,70,150,this);
    	}
    }
}

class GamePanel extends JPanel implements KeyListener,ActionListener{
	private int boxx,boxy,endx,endy,r,c;	//boxx,boxy is pos of player //endx,endy is destination //r,c is pos of player in grid
	private boolean end = false;
	private boolean paused = false;
	private boolean []keys;
	private Image win, gamePaused;	//Image to display when the player wins or when paused
	private Cubed mainFrame;
	JButton pause;
	Timer timer = new Timer(10, this);	//Game timer
	Timer countTimer = new Timer(1000, this); //Display timer
	private int totMin = 0;
	private int totSec = 0;
	private int min = 0;
	private int sec = 0;
	
	//10 Total Stored Levels
	//Game is on a 5x5 grid, where the 0,0 position in the grid is at 200,200 on the screen
	private int[][] level1 = {{0,0,0,0,0},
							  {0,0,0,0,0},
							  {11,1,1,1,5},	//11 is start, 5 is finish
							  {0,0,0,0,0},	//1 is normal block with one life
							  {0,0,0,0,0}};
									 
	private int[][] level2 = {{0,0,0,1,1},
							  {1,11,0,5,1},
							  {1,1,1,1,1},
							  {0,1,1,0,0},
							  {0,0,0,0,0}};
									 
	private int[][] level3 = {{0,0,1,1,1},
							  {0,0,1,0,1},
							  {11,5,2,1,1},	//2 is block with 2 lives
							  {1,0,1,0,0},
							  {1,1,1,0,0}};
							  
	private int[][] level4 = {{11,1,303,0,1},	//>=300 is a builder
							  {0,1,1,0,1},		//It adds to the layout at the digit coordinates after 3
							  {0,1,1,0,1},		//Eg. 304 adds a cube at position (0,4) of currentLvl
							  {0,1,1,0,5},
							  {0,0,0,0,0}};
							  
	private int[][] level5 = {{1,1,1,1,1},
							  {1,1,1,1,1},
							  {1,1,0,11,1},
							  {1,1,2,1,0},
							  {1,1,1,1,5}};
							  
	private int[][] level6 = {{0,0,0,5,1},
							  {1,1,0,1,1},
							  {1,424,0,1,1},	//>=400 is a teleporter
							  {1,1,0,0,0},		//It teleports the player cube to the x,y, digit coords
							  {11,1,0,0,0}};	//Eg. 420 teleports player to (2,0) of currentLvl
							  
	private int[][] level7 = {{11,1,1,1,1},
							  {0,1,1,1,1},
							  {0,1,402,0,1},
							  {1,1,1,0,1},
							  {1,1,1,0,5}};
							  
	private int[][] level8 = {{0,0,0,0,0},
							  {1,1,0,312,1},
							  {1,1,1,1,1},
							  {1,1,5,11,431},
							  {1,1,1,1,1}};
							  
	private int[][] level9 = {{1,1,1,1,1},
							  {1,1,1,1,1},
							  {1,2,1,1,1},
							  {1,11,1,1,5},
							  {1,1,1,1,1}};
							  
	private int[][] level10 = {{1,1,1,1,1},
							  {1,0,1,1,1},
							  {1,1,411,1,1},
							  {5,1,1,1,311},
							  {11,1,1,1,1}};
	
	//Stored list of all levels
	private int[][][] lvls = {level1,level2,level3,level4,level5,level6,level7,level8,level9,level10};
	//Preset info to start
	private int lvlNum = 1;
	private int[][] currentLvl =  copy(level1);
	private String lvlStr = "Level "+lvlNum;
	
	//Contructs game panel, where game happens
	public GamePanel(Cubed m){
		keys = new boolean[KeyEvent.KEY_LAST+1];
		mainFrame = m;
	    findStart();
	    findEnd();
	    setLayout(null);
		setSize(800,570);
        addKeyListener(this);
        win = new ImageIcon("youwin.png").getImage();
        gamePaused = new ImageIcon("gamePaused.png").getImage();
		pause = new JButton("Pause");
		pause.setBounds(360,10,80,25);
		add(pause);
		pause.addActionListener(this);
	}
	//Restarts the current level
	private void restart(){
		currentLvl = copy(lvls[lvlNum-1]);
		this.repaint();
		min = 0;
		sec = 0;
		findStart();
	    findEnd();
	}
	//Returns copy of the original array
	private int[][] copy(int[][] input){
		int[][] c = new int[5][5];
		for (int i=0; i<5; i++){
			for (int j=0; j<5; j++){
				c[i][j] = input[i][j];
			}
		}
		return c;
	}
	
	//ACTION
	public void actionPerformed(ActionEvent evt){
		if(evt.getSource()==countTimer){
			sec+=1;
			if (sec>=60){
				sec = sec%60;
				min+=1;
			}
		}
		if (evt.getSource()==pause){
			if (paused){
				paused = false;
				countTimer.start();
			}
			else{
				paused = true;
				countTimer.stop();
			}
		}
		if (evt.getSource()==timer){
			if (currentLvl[r][c]>=400){	//If the block is a teleporter
				String s = Integer.toString(currentLvl[r][c]);
				int nr = Integer.valueOf(s.substring(1,2));
				int nc = Integer.valueOf(s.substring(2,3));
				currentLvl[r][c] = 0;
				r = nr;
				c = nc;
				if (checkSpot(r,c)){
					boxx = 200+c*50+r*20;
					boxy = 200+r*20-40;
				}
				else{	//Can't happen, restart
					restart();
				}
			}
			else if (currentLvl[r][c]>=300){	//If the block is a builder
				String s = Integer.toString(currentLvl[r][c]);
				int nr = Integer.valueOf(s.substring(1,2));
				int nc = Integer.valueOf(s.substring(2,3));
				currentLvl[nr][nc] = 1;
			}
			if (boxx == endx && boxy == endy){
				//Update time
				totSec += sec;
				totMin += min+totSec/60;
				totSec = totSec%60;
				if (checkWin()){
					if (lvlNum == 10){	//Finished entire game of 10 levels	
						end = true;
						boxx = 0;
						boxy = 0;
					}
					else{	//Move on to next level
						lvlNum += 1;
						countTimer.stop();
						String tt = totMin+" minutes and "+totSec+" seconds";
						String t = min+" minutes and "+sec+" seconds";
						JOptionPane.showMessageDialog(null, "You pass!\nYou took "+tt+" in total\nYou took "+t+" this level\nNext: Level "+lvlNum);
						currentLvl = copy(lvls[lvlNum-1]);
						lvlStr = "Level "+lvlNum;
						findStart();
						findEnd();
						min = 0;
						sec = 0;
						countTimer.start();
					}
				}
				else{	//Restarts the level
					restart();
				}
			}
		}
	}
	
    public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
        countTimer.start();
        timer.start();
    }
    
    //Finds the starting boxx,boxy of the player cube
	public void findStart(){
		for (int i=0; i<5; i++){
			for (int j=0; j<5; j++){
				if (currentLvl[i][j] == 11){
					r = i;
					c = j;
					boxx = 200+c*50+r*20;
					boxy = 200+r*20-40;
					currentLvl[i][j] -= 10;	//Make it a normal cube with 1 life
				}
			}
		}
	}
	
	//Finds endx,endy
	private void findEnd(){
		for (int i=0; i<5; i++){
			for (int j=0; j<5; j++){
				if (currentLvl[i][j] == 5){
					int tempr = i;
					int tempc = j;
					//Sets the endx and endy to be compared to boxx and boxy
					endx = 200+tempc*50+tempr*20;
					endy = 200+tempr*20-40;
				}
			}
		}
	}
	
	private boolean checkWin(){	//Checks for leftover cubes that weren't eaten (with >0 lives left)
		boolean flag = true;
		for (int i=0; i<5; i++){
			for (int j=0; j<5; j++){
				if (currentLvl[i][j]!=0){
					if (currentLvl[i][j]!=5){	//5 doesn't count as leftover, it's the finishline
						flag = false;
					}
				}
			}
		}
		return flag;
	}
	
	//MOVING
	public void move(){
		if(keys[KeyEvent.VK_RIGHT]){
			
			//First check if spot is available
			if (checkSpot(r,c+1)){
				//Check if the cube is a builder, so that only after the player leaves the
				//builder cube, the layout changes
				if (currentLvl[r][c]>=300){
					currentLvl[r][c] = 1;
				}
				//Else take 1 life away from the cube
				else{
					currentLvl[r][c] -= 1;
					c += 1;
					boxx += 50;
					keys[KeyEvent.VK_RIGHT] = false;
				}
			}
			//If player makes invalid move, restart the level
			else{
				restart();
			}
		}
		if(keys[KeyEvent.VK_LEFT]){
			if (checkSpot(r,c-1)){
				if (currentLvl[r][c]>=300){
					currentLvl[r][c] = 1;
				}
				else{
					currentLvl[r][c] -= 1;
					c -= 1;
					boxx -= 50;
					keys[KeyEvent.VK_LEFT] = false;
				}
			}
			else{
				restart();
			}
		}
		if(keys[KeyEvent.VK_UP]){
			if (checkSpot(r-1,c)){
				if (currentLvl[r][c]>=300){
					currentLvl[r][c] = 1;
				}
				else{
					currentLvl[r][c] -= 1;
					r -= 1;
					boxx -= 20;	//When the cube moves up, the x value moves to the left
					boxy -= 20;
					keys[KeyEvent.VK_UP] = false;
				}
			}
			else{
				restart();
			}
		}
		if(keys[KeyEvent.VK_DOWN]){
			if (checkSpot(r+1,c)){
				if (currentLvl[r][c]>=300){
					currentLvl[r][c] = 1;
				}
				else{
					currentLvl[r][c] -= 1;
					r += 1;
					boxx += 20;	//Cube goes down, the x value goes to right
					boxy += 20;
					keys[KeyEvent.VK_DOWN] = false;
				}
			}
			else{
				restart();
			}
		}
	}
	
	//Checks if the move to currentLvl[r][c] is valid
	private boolean checkSpot(int r, int c){
		if (r>=0 && r<5 && c>=0 && c<5){
			if (currentLvl[r][c] != 0){
				return true;
			}
			return false;
		}
		return false;
	}
	
    public void keyTyped(KeyEvent e) {}
	
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    
    //GRAPHICS
    public void paintComponent(Graphics g){
    	g.setColor(Color.black);
		g.fillRect(0,0,800,570);
		//Build the layout depending on level
		buildCube(g,currentLvl);
		//Build the player's cube wherever it may be
		playerCube(g);
		//Displaying level # and time taken
		g.setColor(Color.white);
		g.drawString(lvlStr,10,20);
		g.drawString(min+":"+sec,390,60);
		//If the game has ended
		if (end){
			remove(pause);
			g.setColor(Color.orange);
			g.fillRect(0,0,800,570);
			g.drawImage(win,0,100,this);
			g.setColor(Color.red);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 30)); 
			g.drawString("You have won the game in "+totMin+" minutes and "+totSec+" seconds!",20,400);
		}
		if (paused){
			g.setColor(Color.orange);
			g.fillRect(0,0,800,570);
			g.drawImage(gamePaused,20,100,this);
			
		}
    }
    //Builds the player's cube - yellow
    public void playerCube(Graphics g){
    	Color yellow1 = new Color(200,200,0);
    	Color yellow2 = new Color(150,150,0);
    	Color yellow3 = new Color(100,100,0);
    	
    	//Side Rect
    	g.setColor(yellow3);
		int xpoints[] = {boxx, boxx, boxx+20, boxx+20};
	    int ypoints[] = {boxy, boxy+40, boxy+60, boxy+20};
	    int npoints = 4;
    	g.fillPolygon(xpoints, ypoints, npoints);
    	
    	//Top Rect
    	g.setColor(yellow1);
    	int x3points[] = {boxx, boxx+20, boxx+70, boxx+50};
	    int y3points[] = {boxy, boxy+20, boxy+20, boxy};
	    int n3points = 4;
    	g.fillPolygon(x3points, y3points, n3points);
    	
    	//Front Rect
    	g.setColor(yellow2);
    	int x2points[] = {boxx+20, boxx+20, boxx+70, boxx+70};
	    int y2points[] = {boxy+20, boxy+60, boxy+60, boxy+20};
	    int n2points = 4;
    	g.fillPolygon(x2points, y2points, n2points);
    }
    //Builds the layout of the current level
    public void buildCube(Graphics g, int[][] p){
    	Color gray1 = new Color(50,50,50);
    	Color gray2 = new Color(100,100,100);
    	Color gray3 = new Color(200,200,200);
    	Color purple1 = new Color(75,0,75);
    	Color purple2 = new Color(100,0,100);
    	Color purple3 = new Color(150,0,150);
    	Graphics2D g2 = (Graphics2D) g;	//For drawing outlines
        //Goes through 2D array of nums and depending on numbers uses diff colours
        for (int i=0; i<5; i++){
        	for (int j=4; j>=0; j--){	//Go backwards in the columns to draw the farthest cubes first
        		int x = 200+j*50+i*20;	//Determined by the row, because of 3D-ness
        		int y = 200+i*20;		//200,200 is the pixel pos of the 0,0 grid pos
        		if (p[i][j]==1){	//Normal cube with 1 life = gray
        			//Side Rect
		    		g.setColor(gray1);
					int xpoints[] = {x, x, x+20, x+20};	//x = 10
				    int ypoints[] = {y, y+40, y+60, y+20}; //boxy = 20
				    int npoints = 4;
			    	g.fillPolygon(xpoints, ypoints, npoints);
			    	
			    	//Top Rect
			    	g.setColor(gray3);
			    	int x3points[] = {x, x+20, x+70, x+50};
				    int y3points[] = {y, y+20, y+20, y};
				    int n3points = 4;
			    	g.fillPolygon(x3points, y3points, n3points);
			    	
			    	//Front Rect
			    	g.setColor(gray2);
			    	int x2points[] = {x+20, x+20, x+70, x+70};
				    int y2points[] = {y+20, y+60, y+60, y+20};
				    int n2points = 4;
			    	g.fillPolygon(x2points, y2points, n2points);
			    	
			    	//Outlines
			    	g2.setColor(Color.black);
			    	Line2D lin = new Line2D.Float(x,y,x,y+40);
			    	Line2D lin2 = new Line2D.Float(x,y+40,x+20,y+60);
			    	Line2D lin3 = new Line2D.Float(x+20,y+20,x+20,y+60);
			    	Line2D lin4 = new Line2D.Float(x+20,y+20,x,y);
			    	Line2D lin5 = new Line2D.Float(x,y,x+50,y);
			    	Line2D lin6 = new Line2D.Float(x+50,y,x+70,y+20);
			    	Line2D lin7 = new Line2D.Float(x+20,y+20,x+70,y+20);
			    	Line2D lin8 = new Line2D.Float(x+70,y+20,x+70,y+60);   	
			    	Line2D lin9 = new Line2D.Float(x+20,y+60,x+70,y+60);
			    	g2.draw(lin);
			    	g2.draw(lin2);
		        	g2.draw(lin3);
		        	g2.draw(lin4);
		        	g2.draw(lin5);
		        	g2.draw(lin6);
		        	g2.draw(lin7);
		        	g2.draw(lin8);
		        	g2.draw(lin9);

        		}
        		if (p[i][j]==2){	//Normal cube with 2 lives = purple
        			//Side Rect
		    		g.setColor(purple1);
					int xpoints[] = {x, x, x+20, x+20};	//x = 10
				    int ypoints[] = {y, y+40, y+60, y+20}; //boxy = 20
				    int npoints = 4;
			    	g.fillPolygon(xpoints, ypoints, npoints);
			    	
			    	//Top Rect
			    	g.setColor(purple3);
			    	int x3points[] = {x, x+20, x+70, x+50};
				    int y3points[] = {y, y+20, y+20, y};
				    int n3points = 4;
			    	g.fillPolygon(x3points, y3points, n3points);
			    	
			    	//Front Rect
			    	g.setColor(purple2);
			    	int x2points[] = {x+20, x+20, x+70, x+70};
				    int y2points[] = {y+20, y+60, y+60, y+20};
				    int n2points = 4;
			    	g.fillPolygon(x2points, y2points, n2points);
			    	
			    	//Outlines
			    	g2.setColor(Color.black);
			    	Line2D lin = new Line2D.Float(x,y,x,y+40);
			    	Line2D lin2 = new Line2D.Float(x,y+40,x+20,y+60);
			    	Line2D lin3 = new Line2D.Float(x+20,y+20,x+20,y+60);
			    	Line2D lin4 = new Line2D.Float(x+20,y+20,x,y);
			    	Line2D lin5 = new Line2D.Float(x,y,x+50,y);
			    	Line2D lin6 = new Line2D.Float(x+50,y,x+70,y+20);
			    	Line2D lin7 = new Line2D.Float(x+20,y+20,x+70,y+20);
			    	Line2D lin8 = new Line2D.Float(x+70,y+20,x+70,y+60);   	
			    	Line2D lin9 = new Line2D.Float(x+20,y+60,x+70,y+60);
			    	g2.draw(lin);
			    	g2.draw(lin2);
		        	g2.draw(lin3);
		        	g2.draw(lin4);
		        	g2.draw(lin5);
		        	g2.draw(lin6);
		        	g2.draw(lin7);
		        	g2.draw(lin8);
		        	g2.draw(lin9);
        		}
        		if (p[i][j]>=400){	//Teleporter cube = blue
        			g.setColor(Color.blue);
        			//Side
	    			int xpoints[] = {x, x, x+20, x+20};	//x = 10
				    int ypoints[] = {y, y+40, y+60, y+20}; //boxy = 20
				    int npoints = 4;
			    	g.fillPolygon(xpoints, ypoints, npoints);
			    	//Top
			    	int x3points[] = {x, x+20, x+70, x+50};
				    int y3points[] = {y, y+20, y+20, y};
				    int n3points = 4;
			    	g.fillPolygon(x3points, y3points, n3points);
			    	//Front
			    	int x2points[] = {x+20, x+20, x+70, x+70};
				    int y2points[] = {y+20, y+60, y+60, y+20};
				    int n2points = 4;
			    	g.fillPolygon(x2points, y2points, n2points);
			    	//Outlines
			    	g2.setColor(Color.black);
			    	Line2D lin = new Line2D.Float(x,y,x,y+40);
			    	Line2D lin2 = new Line2D.Float(x,y+40,x+20,y+60);
			    	Line2D lin3 = new Line2D.Float(x+20,y+20,x+20,y+60);
			    	Line2D lin4 = new Line2D.Float(x+20,y+20,x,y);
			    	Line2D lin5 = new Line2D.Float(x,y,x+50,y);
			    	Line2D lin6 = new Line2D.Float(x+50,y,x+70,y+20);
			    	Line2D lin7 = new Line2D.Float(x+20,y+20,x+70,y+20);
			    	Line2D lin8 = new Line2D.Float(x+70,y+20,x+70,y+60);   	
			    	Line2D lin9 = new Line2D.Float(x+20,y+60,x+70,y+60);
			    	g2.draw(lin);
			    	g2.draw(lin2);
		        	g2.draw(lin3);
		        	g2.draw(lin4);
		        	g2.draw(lin5);
		        	g2.draw(lin6);
		        	g2.draw(lin7);
		        	g2.draw(lin8);
		        	g2.draw(lin9);
        		}
        		else if (p[i][j]>=300){	//Builder cube = green
        			g.setColor(Color.green);
        			//Side
	    			int xpoints[] = {x, x, x+20, x+20};	//x = 10
				    int ypoints[] = {y, y+40, y+60, y+20}; //boxy = 20
				    int npoints = 4;
			    	g.fillPolygon(xpoints, ypoints, npoints);
			    	//Top
			    	int x3points[] = {x, x+20, x+70, x+50};
				    int y3points[] = {y, y+20, y+20, y};
				    int n3points = 4;
			    	g.fillPolygon(x3points, y3points, n3points);
			    	//Front
			    	int x2points[] = {x+20, x+20, x+70, x+70};
				    int y2points[] = {y+20, y+60, y+60, y+20};
				    int n2points = 4;
			    	g.fillPolygon(x2points, y2points, n2points);
			    	//Outlines
			    	g2.setColor(Color.black);
			    	Line2D lin = new Line2D.Float(x,y,x,y+40);
			    	Line2D lin2 = new Line2D.Float(x,y+40,x+20,y+60);
			    	Line2D lin3 = new Line2D.Float(x+20,y+20,x+20,y+60);
			    	Line2D lin4 = new Line2D.Float(x+20,y+20,x,y);
			    	Line2D lin5 = new Line2D.Float(x,y,x+50,y);
			    	Line2D lin6 = new Line2D.Float(x+50,y,x+70,y+20);
			    	Line2D lin7 = new Line2D.Float(x+20,y+20,x+70,y+20);
			    	Line2D lin8 = new Line2D.Float(x+70,y+20,x+70,y+60);   	
			    	Line2D lin9 = new Line2D.Float(x+20,y+60,x+70,y+60);
			    	g2.draw(lin);
			    	g2.draw(lin2);
		        	g2.draw(lin3);
		        	g2.draw(lin4);
		        	g2.draw(lin5);
		        	g2.draw(lin6);
		        	g2.draw(lin7);
		        	g2.draw(lin8);
		        	g2.draw(lin9);
        		}
        		if (p[i][j]==5){	//Destination cube = red
        			g.setColor(Color.red);
	    			//Side
	    			int xpoints[] = {x, x, x+20, x+20};	//x = 10
				    int ypoints[] = {y, y+40, y+60, y+20}; //boxy = 20
				    int npoints = 4;
			    	g.fillPolygon(xpoints, ypoints, npoints);
			    	//Top
			    	int x3points[] = {x, x+20, x+70, x+50};
				    int y3points[] = {y, y+20, y+20, y};
				    int n3points = 4;
			    	g.fillPolygon(x3points, y3points, n3points);
			    	//Front
			    	int x2points[] = {x+20, x+20, x+70, x+70};
				    int y2points[] = {y+20, y+60, y+60, y+20};
				    int n2points = 4;
			    	g.fillPolygon(x2points, y2points, n2points);
			    	//Outlines
			    	g2.setColor(Color.black);
			    	Line2D lin = new Line2D.Float(x,y,x,y+40);
			    	Line2D lin2 = new Line2D.Float(x,y+40,x+20,y+60);
			    	Line2D lin3 = new Line2D.Float(x+20,y+20,x+20,y+60);
			    	Line2D lin4 = new Line2D.Float(x+20,y+20,x,y);
			    	Line2D lin5 = new Line2D.Float(x,y,x+50,y);
			    	Line2D lin6 = new Line2D.Float(x+50,y,x+70,y+20);
			    	Line2D lin7 = new Line2D.Float(x+20,y+20,x+70,y+20);
			    	Line2D lin8 = new Line2D.Float(x+70,y+20,x+70,y+60);   	
			    	Line2D lin9 = new Line2D.Float(x+20,y+60,x+70,y+60);
			    	g2.draw(lin);
			    	g2.draw(lin2);
		        	g2.draw(lin3);
		        	g2.draw(lin4);
		        	g2.draw(lin5);
		        	g2.draw(lin6);
		        	g2.draw(lin7);
		        	g2.draw(lin8);
		        	g2.draw(lin9);
        		}
        	}
        }
    }
}