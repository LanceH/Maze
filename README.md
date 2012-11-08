Maze
====

Creates mazes for printing.

Installing and Running
----------------------

I wrote this and ran it in Eclipse.  No consideration was ever given to making it a standalone program.

Just tell eclipse that this is a java project with the src directory and run Amazing.java as an application and it should spit out some mazes as .png and maybe some latex for printing a lot of them at once.

Configuration
-------------

There are several configurable options available within the program itself:

	int cols;                   // the number of cells wide
	int rows;					// the number of cells tall
	int cells;
	int maze[];					// array structure keeping track of which walls are open
	int smaze[];				// another array to keep track of the path followed in finding the solution
	
	int north = 1;				// the four directions are the first four bits of a number
	int east = 2;               // so that I can just do an && (logical AND) against a direction
	int south = 4;				// and a cell in the maze to find out if a wall has been traversed
	int west = 8;				// and is thus already traveled.
	
	int scale = 4;				// inner width of a cell
	int twisty = 0;				// the propensity for hallways in a maze to go straight
	
	int header = 0;
	int footer = 0;

	String output = "output/";  // the base directory for output
	
	Color BACKGROUND = Color.WHITE;
	Color WALLS 	 = Color.BLACK;
	Color SOLUTION   = Color.RED;

	boolean solve = true;       // produce the solution as well
	boolean ascii = false;      // draw an ascii version to console
	boolean sf = false;         // draw the start and finish
	boolean torque = false;     // Used with TileMaps in Garagegames TGB