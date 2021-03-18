/**
 * Hai-Au Bui, Final
 * Professor Abbott
 * I am define several private classes in this maze class
 * I kept your MazeClient original and test with Maze (6,4), Maze(40,25),...Maze(80, 50)
 * I have added one EXTRA method allConnected() in YOUR DisjoinSet.java,
 * So please use my version of DisjointSet java file. Thanks Tom!
 * Everything working nicely :).
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Maze {

  private int width;
  private int height;
  private int totalCell ;
  public static final int CELL = 20;
  public static final int MARGIN = 50;
  public static final int DOT = 7;
  public static final int DOT_MARGIN = 5;
  private Cell[] cells;
  private boolean[] solution;

  final int NORTH = 0;
  final int SOUTH = 1;
  final int EAST = 2;
  final int WEST = 3;

  public Maze(int width, int height) {
    this.height = height;
    this.width = width;
    this.totalCell = height * width;
    cells = new Cell[totalCell];

    for (int i = 0; i < totalCell; i++) {
      cells[i] = new Cell();
    }

    if (totalCell > 0) {
      makeWalls();
      decimateWalls( totalCell / 2);
      solution = new boolean[totalCell];
      solutionPath();
    }
  }

  /**
   * this public interface will draw the entire a maze and its solution
   */
  public void drawWalls() {
    JFrame window = new JFrame("Final - Maze - HaiAu");
    MazePanel panel = new MazePanel(this);
    JScrollPane scrollPane = new JScrollPane(panel);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setSize(1000, 800);
    window.add(scrollPane, BorderLayout.CENTER);
    window.setVisible(true);
  }

  /**
   * this makeWalls() method draw the entire a grid of wished Maze(width * height)
   */
  public void makeWalls() {
    // set NORTH, EAST, SOUTH, WEST walls
    for (int i = 0; i < width * height; i++) {
      cells[i].walls[NORTH] = i - width;
      cells[i].walls[SOUTH] = i + width;
      cells[i].walls[EAST] = i + 1;
      cells[i].walls[WEST] = i - 1;
    }

    // set all the cell that lies on NORTH, EAST, SOUTH AND WEST BORDER TO -1
    for (int i = 0; i < width; i++) {
      cells[i].walls[NORTH] = -1;
      cells[width * height - i - 1].walls[SOUTH] = -1;
    }

    for (int i = 0; i < totalCell; i += width) {
      cells[totalCell - i - 1].walls[EAST] = -1;
      cells[i].walls[WEST] = -1;
    }
  }

  /**
   * this method will randomly break a wall between two random cell by using disjointset algo
   * @param beyond: I have to declare this beyond due to your requirement but I don't have to
   *              use it since I have defined extra method in DisjoinSet to check whether I am
   *              done joining entire cell of (width * height)
   */
  public void decimateWalls( int beyond) {
    DisjointSet disjointSet = getMaze();

    // go through each cell and call DisjoinSet.find()
    for (int k = 0; k < totalCell; k++) {
      disjointSet.find(k);
    }

//    // TODO: Tom, if you just want a maze without its solution, comment this back, then comment out solutionPath() on line 51
//    // open the first cell's west wall and last cell's east wall
//    cells[0].walls[WEST] = totalCell;
//    cells[totalCell-1].walls[EAST] = totalCell;

    Random generator = new Random();
    //check if all the elements in the set are connected, if not continue join by choosing randomly
    while ( !disjointSet.allConnected() ) {
      int firstCell = generator.nextInt(totalCell);
      int wall = generator.nextInt(4);

      // pick a second cell as next to first cell
      int secondCell = cells[firstCell].walls[wall];

      if (secondCell != -1 && secondCell != totalCell) {
        if (disjointSet.find(firstCell) != disjointSet.find(secondCell)) {
          cells[firstCell].walls[wall] = totalCell;

          if (wall == NORTH || wall == EAST) {
            cells[secondCell].walls[wall + 1] = totalCell;
          }
          if (wall == SOUTH || wall == WEST) {
            cells[secondCell].walls[wall - 1] = totalCell;
          }

          disjointSet.union(disjointSet.find(firstCell), disjointSet.find(secondCell));
        }
      }
    }
  }

  /**
   * solutionPath() use DFS algorithm to find the possible solution for the maze
   * if the maze != 1, start searching from cell 0
   */
  public void solutionPath() {
    if (width != 1) {
      depthFirstSearch(0);

      solution[0] = true;
      solution[totalCell - 1] = true;

      int current = cells[totalCell - 1].visited;
      while (current != 0)
      {
        solution[current] = true;
        current = cells[current].visited;
      }
    } else // if maze is of size 1
    {
      solution[0] = true;
    }

    cells[0].walls[WEST] = totalCell;
    cells[totalCell - 1].walls[EAST] = totalCell;
  }

  public void depthFirstSearch(int cell) {
    Cell startCell = cells[cell];

    for (int i = 0; i < 4; i++) {
      int adjacent = -1;

      if (startCell.walls[i] == totalCell) {
        if (i == NORTH) {
          adjacent = cell - width;
        }
        if (i == SOUTH) {
          adjacent = cell + width;
        }
        if (i == EAST) {
          adjacent = cell + 1;
        }
        if (i == WEST) {
          adjacent = cell - 1;
        }

        if (cells[adjacent].visited == -1) {
          cells[adjacent].visited = cell;
          depthFirstSearch(adjacent);
        }
      }
    }
  }

  /**
   * draws a maze in black color and its solution in red color.
   */
  public void draw(Graphics g) {

    /** DRAWING THE MAZE GRID HERE */
    g.setColor(Color.BLACK);
    for (int i = 0; i < width; i++) {
      int count = i;
      for (int j = 0; j < height; j++) {
        if (j != 0) {
          count += width;
        }

        // if there exists a wall to the north
        if (cells[count].walls[NORTH] != totalCell) {
          g.drawLine((i * CELL + MARGIN), (j * CELL + MARGIN), ((i + 1) * CELL + MARGIN), (j * CELL + MARGIN));
        }

        // if there exists a wall to the south
        if (cells[count].walls[SOUTH] != totalCell) {
          g.drawLine(i * CELL + MARGIN, (j + 1) * CELL + MARGIN, (i + 1) * CELL + MARGIN, (j + 1) * CELL + MARGIN);
        }

        // if there exists a wall to the east
        if (cells[count].walls[EAST] != totalCell) {
          g.drawLine((i + 1) * CELL + MARGIN, (j) * CELL + MARGIN, (i + 1) * CELL + MARGIN, (j + 1) * CELL + MARGIN);
        }

        // if there exists a wall to the west
        if (cells[count].walls[WEST] != totalCell) {
          g.drawLine(i * CELL + MARGIN, j * CELL + MARGIN, i * CELL + MARGIN, (j + 1) * CELL + MARGIN);
        }
      }
    }

    /** DRAWING THE MAZE SOLUTION HERE */
    g.setColor(Color.RED);
    for (int i = 0; i < width; i++) {
      int count = i;
      for (int j = 0; j < height; j++) {
        if (j != 0) {
          count += width;
        }

        if (solution[count] == true) {
          g.fillOval(i * CELL + MARGIN + DOT_MARGIN, j * CELL + MARGIN + DOT_MARGIN, DOT, DOT);
        }
      }
    }
  }

  /***
   * @return reference to the current DisjointSet
   */
  public DisjointSet getMaze() {
    DisjointSet disjointSet = new DisjointSet(totalCell);
    return disjointSet;
  }

  /**
   * define a private class Cell, each cell has 4 walls
   */
  private class Cell {
    // each cell contain 4 walls NORTH, EAST, SOUTH, WEST
    int[] walls;
    int visited;

    public Cell() {
      walls = new int[4];
      visited = -1;
    }
  }

  /**
   * set the window size
   * @return Dimension of the window
   */
  public Dimension windowSize() {
    return new Dimension(width * CELL + MARGIN * 2, height * CELL + MARGIN * 2);
  }

  /**
   * define MazePanel here
   */
  class MazePanel extends JPanel {
    private Maze maze;

    public MazePanel(Maze theMaze) {
      maze = theMaze;
    }

    public void paintComponent(Graphics page) {
      super.paintComponent(page);
      setBackground(Color.white);
      this.setPreferredSize(maze.windowSize());
      maze.draw(page);
    }
  }

}

