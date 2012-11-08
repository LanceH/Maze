import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;

import javax.imageio.ImageIO;

public class Amazing {
/* I started this with only 2 configurable variables
 * cols and rows.  I was originally writing this just 
 * because I had that Aha! moment figuring out how to
 * generate a maze.
 * 
 * With the addition of so many other configurables
 * they really should go into a properties file.
 */		
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
	boolean torque = false;

	Stack<Integer> solution = new Stack<Integer>();

	File tFile;

	BufferedWriter tOut;

	Random r;

	public static void main(String args[]){
		int rows = 1000;
		int cols = 1000;
		int reps = 1;
		if (args.length == 2) {
			rows = (new Integer(args[0])).intValue();
			cols = (new Integer(args[1])).intValue();
		}
		Amazing A = new Amazing();
		A.initialize();
		try {
		A.texHeader();
		int counter = 0;
		for (int i = 1; i <= reps; i++) {
			A.create(rows, cols);
			String m = "maze" + cols + "by" + rows + "-" + i;
			A.bi(m);
			A.ptex("\\includegraphics[scale=0.75]{"+ m +".png}");
			if (counter % 2 == 1) {
				A.ptex("\\newpage");
			} else {
				A.ptex("\\\\[12mm]");
			}
			counter++;
		}
		A.texFooter();
		A.tOut.close();
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			System.out.println(e.getMessage());
		}
	}

	public void create(int rr, int cc) {
		this.rows = rr;
		this.cols = cc;
		this.cells = rows * cols;
		this.maze = new int[cells];
		this.smaze = new int[cells];
		Stack<Integer> stack = new Stack<Integer>();
		int start = r.nextInt(cells);
		int count = 1;

		// initialize
		stack.push(new Integer(start));
		int current = start;
		// p(current + "\n");
		double n = 0.0;
		double e = 0.0;
		double w = 0.0;
		double s = 0.0;
		double g = 0.0;
		int path = 0;
		int lastDir = 0;
		while (count < cells) {
			g = 0.0;
			path = 0;
			n = e = w = s = 0.0;
			if ((maze[current] & north) == 0 && current >= cols
					&& maze[current - cols] == 0) {
				n = r.nextDouble();
				if (lastDir == north) {
					n = (n + twisty) / (twisty + 1);
				}
				g = n;
				path = north;
			}
			if ((maze[current] & east) == 0 && current % cols != cols - 1
					&& maze[current + 1] == 0) {
				e = r.nextDouble();
				if (lastDir == east) {
					e = (e + twisty) / (twisty + 1);
				}
				if (e > g) {
					path = east;
					g = e;
				}
			}
			if ((maze[current] & west) == 0 && current % cols != 0
					&& maze[current - 1] == 0) {
				w = r.nextDouble();
				if (lastDir == west) {
					w = (w + twisty) / (twisty + 1);
				}
				if (w > g) {
					path = west;
					g = w;
				}
			}
			if ((maze[current] & south) == 0 && current < cells - cols - 1
					&& maze[current + cols] == 0) {
				s = r.nextDouble();
				if (lastDir == south) {
					s = (s + twisty) / (twisty + 1);
				}
				if (s > g) {
					path = south;
				}
			}
			lastDir = path;
			// decide to move forward or traceback
			if (path == 0) {
				// backtrack here.
				current = stack.pop();
				// p(current + "\n");
			} else {
				maze[current] = maze[current] | path; // open the wall on
				// the path side

				// find the new current cell by moving
				// in the direction of the path
				// then open the wall in the direction you came from
				if (path == north) {
					current -= cols;
					maze[current] |= south;
				}
				if (path == east) {
					current += 1;
					maze[current] |= west;
				}
				if (path == south) {
					current += cols;
					maze[current] |= north;
				}
				if (path == west) {
					current -= 1;
					maze[current] |= east;
				}
				stack.push(new Integer(current));
				// p(current + "\n");
				count++;
			}
		}

		if (ascii) {
			ascii(maze);
		}

		if (solve) {
			getSolution();
		}

	}

	void p(String s) {
		System.out.print(s);
	}

	/*
	 * Create a .png for the maze 
	 * and optionally another one for the maze with solution
	 */
	void bi(String filename) {
		String mazeFile = output + filename + ".png";
		System.out.println(filename);
		String solutionFile = output + filename + "solution.png";
		int width = cols * scale + 1;
		int height = rows * scale + 1 + header + footer;
		BufferedImage b = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) b.createGraphics();
		g2.setBackground(BACKGROUND);
		g2.clearRect(0, 0, width, height);
		g2.setColor(WALLS);
		g2.drawRect(0, 0 + header, width - 1, height - 1 - footer - header);
		g2.drawChars("start".toCharArray(), 0, 5, 1, header - 1);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (!((maze[i * cols + j] & east) == east)) {
					g2.drawLine((j + 1) * scale, i * scale + header, (j + 1) * scale,
							(i + 1) * scale + header);
				}
			}
			for (int j = 0; j < cols; j++) {
				if (!((maze[i * cols + j] & south) == south)) {
					g2.drawLine(j * scale, (i + 1) * scale + header, (j + 1) * scale,
							(i + 1) * scale + header);
				}
				// Draw start and end
				if (sf && i == 0 && j == 0) {
					g2.setColor(BACKGROUND);
					g2.drawLine(0, header, 0, header + scale);
					g2.drawLine(0, header, scale, header);
					g2.setColor(WALLS);
					//g2.drawOval(0, 0, scale, scale);
				} else if (sf && i == rows - 1 && j == cols - 1) {
					g2.setColor(BACKGROUND);
					g2.drawLine(width - 1, height - footer, width - 1, height - footer - scale);
					g2.drawLine(0, header, scale, header);
					g2.setColor(WALLS);
					//g2.drawOval(width - scale - 1, height - scale - 1, scale,	scale);
				}
			}
		}
		if (scale < 8) {
			g2.clearRect(0, 0 + header, scale, scale + header);
			g2.clearRect(width - scale, height - scale + header, width, height + header);
		}
		File f = new File(mazeFile);
		try {
			ImageIO.write(b, "png", f);
		} catch (Exception e) {

		}
		if (solve) {
			Iterator<Integer> i = solution.iterator();
			g2.setColor(SOLUTION);
			int x1, y1, x2, y2;
			int current = i.next();
			int j;
			while (i.hasNext()) {
				j = (Integer) i.next();
				// System.out.println("current " + current + " j " + j);
				x1 = scale * (current % cols) + scale / 2;
				y1 = scale * (current / cols) + scale / 2;
				x2 = scale * (j % cols) + scale / 2;
				y2 = scale * (j / cols) + scale / 2;
				g2.drawLine(x1, y1 + header, x2, y2 + header);
				current = j;
				// System.out.println("x1 " + x1 + " y1 " + y1 + " x2 " + x2 + "
				// y2 " + y2);
			}
			f = new File(solutionFile);
			try {
				ImageIO.write(b, "png", f);
			} catch (Exception e) {

			}
		}
		System.out.println(f);
	}

	public Stack<Integer> getSolution() {
		int current = 0;
		double g;
		double n, e, w, s;
		int path;
		solution = new Stack<Integer>();
		solution.push(0);
		while (current != (rows * cols - 1)) {
			// System.out.println(solution);
			g = 0.0;
			path = 0;
			n = e = w = s = 0.0;
			if ((maze[current] & north) == north
					&& (smaze[current] & north) != north) {
				n = .1;
				g = n;
				path = north;
			}
			if ((maze[current] & east) == east
					&& (smaze[current] & east) != east) {
				e = .2;
				if (e > g) {
					path = east;
					g = e;
				}
			}
			if ((maze[current] & west) == west
					&& (smaze[current] & west) != west) {
				w = .3;
				if (w > g) {
					path = west;
					g = w;
				}
			}
			if ((maze[current] & south) == south
					&& (smaze[current] & south) != south) {
				s = .4;
				if (s > g) {
					path = south;
				}
			}
			// decide to move forward or traceback
			if (path == 0) {
				// backtrack here.
				current = ((Integer) solution.pop()).intValue();
				// p(current + "\n");
			} else {
				smaze[current] = smaze[current] | path;
				solution.push(current);
				// p(current + "\n");
				if (path == north) {
					current -= cols;
					smaze[current] |= south;
				}
				if (path == east) {
					current += 1;
					smaze[current] |= west;
				}
				if (path == south) {
					current += cols;
					smaze[current] |= north;
				}
				if (path == west) {
					current -= 1;
					smaze[current] |= east;
				}
			}

		}
		solution.push(cells - 1);
		return solution;
	}

	void initialize() {
		long l = 1l;
		output = "output/maze/" + l + "/";
		File f = new File(output);
		while (f.isDirectory() == true) {
			l++;
			output = "output/maze/" + l + "/";
			f = new File(output);
		}
		f.mkdirs();
		this.r = new Random(l);
		tFile = new File(output + "Book" + l + ".tex");
		try {
			tOut = new BufferedWriter(new FileWriter(tFile));
			System.out.println(tFile);
		} catch (Exception e) {
			System.out.println("Can't write to" + tFile);
		}
	}

	void ascii(int[] maze) {
		p("+");
		for (int i = 0; i < cols; i++) {
			p("---+");
		}
		p("\n");
		for (int i = 0; i < rows; i++) {
			p("|");
			for (int j = 0; j < cols; j++) {
				if (i == 0 && j == 0) {
					p(" O ");
				} else if (i == rows - 1 && j == cols - 1) {
					p(" X ");
				} else {
					p("   ");
				}
				if ((maze[i * cols + j] & east) == east && (j != cols - 1)) {
					p(" ");
				} else {
					p("|");
				}
			}
			p("\n");
			p("+");
			for (int j = 0; j < cols; j++) {
				if ((maze[i * cols + j] & south) == south && (i != rows - 1)) {
					p("   +");
				} else {
					p("---+");
				}
			}
			p("\n");
		}
		if (torque) {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					p("    maze.setStaticTile(" + i + "," + j
							+ ", \"gridlines16ImageMap\"," + maze[i * cols + j]
							+ ");\n");
				}
				p("\n");
			}
		}
	}

	void texHeader() throws IOException {
		ptex("\\documentclass{book}");

		ptex("\\usepackage[]{graphicx}");

		ptex("\\usepackage[left=1cm,top=1cm,right=1cm,nofoot]{geometry}");
		ptex("\\headheight=1cm");
		ptex("\\headsep=0cm");
		ptex("\\usepackage{fancyhdr}");

		ptex("\\begin{document}");

		ptex("\\raggedbottom");

		ptex("\\begin{center}");
		ptex("\\pagestyle{fancy}");
		ptex("\\renewcommand{\\headrulewidth}{0pt}");
		ptex("\\chead{Maze \\thepage}");
		ptex("\\cfoot{littlebadwolf.com}");
	}
	
	void texFooter() throws IOException {
		ptex("\\end{center}");

		ptex("\\end{document}");
	}

void ptex(String s) throws IOException {
		tOut.write(s + "\n");
	}
}
