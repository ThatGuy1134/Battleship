package battleship;

import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    public static int[][] getCoords(String coordinates, String rows) {
        coordinates = coordinates.toUpperCase();
        // Checking for start and end coordinates.
        // If it is just a shot, repeating the shot coordinates.
        if (coordinates.length() < 4) {
            coordinates = coordinates + " " + coordinates;
        }
        String[] splitCoords = coordinates.split(" ");
        int temp;

        int startRow = rows.indexOf(splitCoords[0].charAt(0)) + 1;
        int endRow = rows.indexOf(splitCoords[1].charAt(0)) + 1;
        int startColumn = Integer.parseInt(splitCoords[0].substring(1));
        int endColumn = Integer.parseInt(splitCoords[1].substring(1));
        if (endRow < startRow || endColumn < startColumn) {
            temp = startRow;
            startRow = endRow;
            endRow = temp;
            temp = startColumn;
            startColumn = endColumn;
            endColumn = temp;
        }
        return new int[][] { {startRow, startColumn}, {endRow, endColumn} };
    }

    public static void shipPlacer(Player thisPlayer) {
        Ships[] ships = Ships.values();
        String shipPosition;
        for (Ships ship : ships) {
            System.out.println();
            System.out.println("Enter the coordinates of the " +
                    ship.getName() + " (" + ship.getCells() + " cells):");
            shipPosition = scanner.nextLine();
            shipPlacement(shipPosition, ship.getCells(), ship.getName(), thisPlayer.gameBoard,
                    thisPlayer.shipNames);
            System.out.println();
            thisPlayer.gameBoard.displayBoard();
        }
    }

    public static boolean rangeChecker(int[][] position, int range) {
        // making sure that the entered coordinates
        // are within the range of the board
        for (int[] ints : position) {
            for (int anInt : ints) {
                if (anInt <= 0 || anInt > range) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void shipPlacement(String coordinates, int length, String name,
                                  Game_Board thisBoard, Game_Board shipNames) {
        int[][] startAndEnd = getCoords(coordinates, thisBoard.ROW_NAMES);

        // coordinates in range?
        while (!rangeChecker(startAndEnd, thisBoard.RANGE)) {
            System.out.println();
            System.out.println("Error! You entered the wrong coordinates! Try again:");
            coordinates = scanner.nextLine();
            startAndEnd = getCoords(coordinates, thisBoard.ROW_NAMES);
        }

        // not enough space?
        while (!hasGoodLength(startAndEnd, length)) {
            System.out.println();
            System.out.println("Error! Wrong length of the " + name + "! Try again:");
            coordinates = scanner.nextLine();
            startAndEnd = getCoords(coordinates, thisBoard.ROW_NAMES);
        }

        // placed diagonally?
        boolean notDiagonal = (startAndEnd[0][0] == startAndEnd[1][0] ||
                startAndEnd[0][1] == startAndEnd[1][1]);
        while (!notDiagonal) {
            System.out.println();
            System.out.println("Error! Wrong ship location! Try again:");
            coordinates = scanner.nextLine();
            startAndEnd = getCoords(coordinates, thisBoard.ROW_NAMES);
            notDiagonal = (startAndEnd[0][0] == startAndEnd[1][0] ||
                    startAndEnd[0][1] == startAndEnd[1][1]);
        }

        // too close to another ship?
        while (isTooClose(startAndEnd, thisBoard)) {
            System.out.println();
            System.out.println("Error! You placed it too close to another one. Try again:");
            coordinates = scanner.nextLine();
            startAndEnd = getCoords(coordinates, thisBoard.ROW_NAMES);
        }

        // checking horizontal or vertical and placing the ship
        if (direction(startAndEnd)) {
            for (int i = startAndEnd[0][1]; i <= startAndEnd[1][1]; ++i) {
                thisBoard.editCell("O", startAndEnd[0][0], i);
                shipNames.editCell(name, startAndEnd[0][0], i);
            }
        } else {
            for (int i = startAndEnd[0][0]; i <= startAndEnd[1][0]; ++i) {
                thisBoard.editCell("O", i, startAndEnd[0][1]);
                shipNames.editCell(name, i, startAndEnd[0][1]);
            }
        }
    }

    public static boolean isTooClose(int[][] position, Game_Board thisBoard) {
        int[] startCoords = new int[2];
        int length;
        int rowOrColumns = 3; // need to check before and after desired position

        if (direction(position)) {
            startCoords[0] = position[0][0] - 1;
            if (startCoords[0] < 0) {
                startCoords[0] = 0;
                rowOrColumns = 2; // at the top
            }

            startCoords[1] = position[0][1] - 1;
            length = position[1][1] - position[0][1] + 2;

            if (startCoords[1] < 0) {
                startCoords[1] = 0;
                length -= 1;
            }
            for (int i = startCoords[0]; i < startCoords[0] + rowOrColumns; ++i) {
                for (int j = startCoords[1]; j < startCoords[1] + length; ++j) {
                    if (thisBoard.getACell(i, j).equals("O")) {
                        return true;
                    }
                    if (j == thisBoard.RANGE) {
                        break;
                    }
                }
                if (i == thisBoard.RANGE) {
                    break;
                }
            }
        } else {
            startCoords[1] = position[0][1] - 1;
            if (startCoords[1] < 0) {
                startCoords[1] = 0;
                rowOrColumns = 2; // at left edge
            }

            startCoords[0] = position[0][0] - 1;
            length = position[1][0] - position[0][0] + 2;
            if (startCoords[0] < 0) {
                startCoords[0] = 0;
                length -= 1;
            }

            for (int i = startCoords[1]; i < startCoords[1] + rowOrColumns; ++i) {
                for (int j = startCoords[0]; j <= startCoords[0] + length; ++j) {
                    if (thisBoard.getACell(j, i).equals("O")) {
                        return true;
                    }
                    if (j == thisBoard.RANGE) {
                        break;
                    }
                }
                if (i == thisBoard.RANGE) {
                    break;
                }
            }
        }
        return false;
    }

    public static boolean hasGoodLength(int[][] position, int length) {
        if (direction(position)) {
            return (Math.abs(position[0][1] - position[1][1]) + 1) == length;
        } else {
            return (Math.abs(position[0][0] - position[1][0]) + 1) == length;
        }
    }


    public static boolean direction(int[][] position) {
        return position[0][0] == position[1][0];
    }


    public static void shotTaker(Player underFire, Player attacking) {
        String shot = scanner.nextLine();
        int[][] position = getCoords(shot, underFire.gameBoard.ROW_NAMES);
        // coordinates in range?
        while (!rangeChecker(position, underFire.gameBoard.RANGE)) {
            System.out.println();
            System.out.println("Error! You entered the wrong coordinates! Try again:");
            shot = scanner.nextLine();
            position = getCoords(shot, underFire.gameBoard.ROW_NAMES);
        }
        int row = position[0][0];
        int col = position[0][1];

        // is it a hit or a miss?
        String whichShip;
        if (underFire.gameBoard.getACell(row, col).equals("O")) {
            underFire.gameBoard.editCell("X", row, col);
            attacking.shotBoard.editCell("X", row, col);
            whichShip = underFire.shipNames.getACell(row, col);
            underFire.shipNames.editCell("X", row, col);
            if (!underFire.gameBoard.shipsLeft()) {
                return;
            }
            if (shipSank(whichShip, underFire.shipNames)) {
                System.out.println();
                System.out.println("You hit a ship!");
            } else {
                System.out.println();
                System.out.println("You sank a ship!");
            }
        } else if (underFire.gameBoard.getACell(row, col).equals("X")) {
            System.out.println();
            System.out.println("You hit a ship!");
        } else {
            underFire.gameBoard.editCell("M", row, col);
            attacking.shotBoard.editCell("M", row, col);
            System.out.println();
            System.out.println("You missed!");
        }
    }


    public static boolean shipSank(String shipName, Game_Board shipNames) {
        for (int i = 0; i <= shipNames.RANGE; ++i) {
            for (int j = 0; j <= shipNames.RANGE; ++j) {
                if (shipNames.getACell(i, j).equals(shipName)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void main(String[] args) {
        Player one = new Player();
        Player two = new Player();

        int whosTurn = 1;
        boolean shipsLeft = true;

        // Starting the game with player one
        System.out.println("Player 1, place your ships on the game field");
        System.out.println();
        // displaying the blank game board
        one.gameBoard.displayBoard();
        shipPlacer(one);

        System.out.println();
        System.out.println("Press Enter and pass the move to another player");
        scanner.nextLine();

        // Player two places ships
        System.out.println("Player 2, place your ships to the game field");
        System.out.println();
        two.gameBoard.displayBoard();
        shipPlacer(two);

        System.out.println();
        System.out.println("Press Enter and pass the move to another player");
        scanner.nextLine();

        // Playing the game
        while (shipsLeft) {
            if (whosTurn % 2 == 0) {
                two.shotBoard.displayBoard();
                System.out.println("-".repeat(21));
                two.gameBoard.displayBoard();
                System.out.println();
                System.out.println("Player 2, it's your turn");
                shotTaker(one, two);
                shipsLeft = one.gameBoard.shipsLeft();
                if (!shipsLeft) {
                    break;
                }
                System.out.println("Press Enter and pass the move to another player");
                scanner.nextLine();
            } else {
                one.shotBoard.displayBoard();
                System.out.println("-".repeat(21));
                one.gameBoard.displayBoard();
                System.out.println();
                System.out.println("Player 1, it's your turn:");
                shotTaker(two, one);
                shipsLeft = two.gameBoard.shipsLeft();
                if (!shipsLeft) {
                    break;
                }
                System.out.println("Press Enter and pass the move to another player");
                scanner.nextLine();
            }
            ++whosTurn;
        }
        System.out.println();
        System.out.println("You sank the last ship. You won. Congratulations!");
    }
}
