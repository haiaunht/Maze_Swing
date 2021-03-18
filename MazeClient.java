/**
 * Hai-Au Bui, Final
 * Professor Abbott
 * I was testing with new Maze(6,4), new Maze(40, 25), new Maze(80,50) a numberic of times
 * and its working nicely
 */

public class MazeClient {

  public static void main(String[] args) {
    Maze maze = new Maze(40,25);

    maze.drawWalls();
    System.out.println("...fini...");
  }
}