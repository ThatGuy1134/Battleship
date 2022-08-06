package battleship;

public class Game_Board {
    // add more letters for a bigger board
    final String ROW_NAMES = "ABCDEFGHIJ"; // KLMNOPQRSTUVWXYZ
    // this should be one larger than the rows/columns in play
    final private int SIZE = 11;
    final int RANGE = SIZE - 1; // range for cell coordinates
    private String[][] theBoard = new String[SIZE][SIZE];

    public Game_Board() {
        // Constructing the game board with numbers
        // for the columns, capital letters for the
        // rows, and the "fog of war" in each cell
        char FOG = '~';
        theBoard[0][0] = " ";
        for (int i = 1; i < SIZE; i++) {
            theBoard[0][i] = Integer.toString(i);
        }
        for (int i = 1; i < SIZE; i++) {
            theBoard[i][0] = "" + ROW_NAMES.charAt(i - 1);
            for (int j = 1; j < SIZE; j++) {
                theBoard[i][j] = "" + FOG;
            }
        }
    }

    // displaying the game board
    public void displayBoard() {
        for (String[] strings : theBoard) {
            System.out.println(String.join(" ", strings));
        }
    }

    public String getACell(int row, int col) {
        return theBoard[row][col];
    }

    // changing a cell
    public void editCell(String replacement, int row, int col) {
        theBoard[row][col] = replacement;
    }

    // any ships left?
    public boolean shipsLeft() {
        for (String[] rows : theBoard) {
            for (String cell : rows) {
                if (cell.equals("O")) {
                    return true;
                }
            }
        }
        return false;
    }
}
