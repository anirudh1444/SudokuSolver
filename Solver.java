package com.company;

import java.util.*;

public class Solver {
    int[][] grid; // Sudoku board
    int size; // # of rows and columns
    int root; // length of sub-square
    ArrayList<Integer>[][] possibilities; // every possible digit for each box (will be the main object modified throughout the code)
    Deque<Cell> changes = new ArrayDeque<>(); // holds the indices of boxes which have been updated and must be rechecked
    long count = 0; // total number of valid solutions
    List<int[][]> answers = new ArrayList<>(); // All valid solution grids

    @SuppressWarnings("Cast")

    public Solver (int[][] grid) {
        this.grid = grid;
        this.size = grid.length;
        this.root = (int)Math.sqrt(size);
        possibilities = (ArrayList<Integer>[][]) new ArrayList[this.size][this.size];

        // Creates list ranging from 1 to size (all possible numbers)
        List<Integer> start = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            start.add(i);
        }

        // Assigns each cell the values it can take on (the user-inputted number, or 1-size if unknown)
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col] != 0) {
                    possibilities[row][col] = new ArrayList<>(List.of(grid[row][col]));
                    changes.addLast(new Cell(row, col));
                } else {
                    possibilities[row][col] = new ArrayList<>(start);
                }
            }
        }
    }

    public void solve() {

        while (!changes.isEmpty()) {

            Cell box = changes.pollFirst();  // Front element of the changes stack

            if (possibilities[box.row][box.col].size() == 1) {
                removeSingles(box.row, box.col, grid, possibilities);
            } else if (possibilities[box.row][box.col].size() == 2) {
                nakedDoublesRC(box.row, box.col, possibilities);
                nakedDoubleBox(box.row, box.col, possibilities);
            }
        }

        finalResort(grid, possibilities, 0);
    }

    public void RemoveAndUpdate(int row, int col, int value, List<Integer>[][] possibilities) {
        // Removes value as an option
        possibilities[row][col].remove((Integer) value);

        //any box which has value removed as a possibility is added to the changes list
        Cell item = new Cell(row, col);
        if (!changes.contains(item)) {
            changes.addLast(item);
        }
    }

    public void removeColumn(int column, int value, List<Integer>[][] possibilities, List<Integer> exceptions) {
        /*
        The removeColumn method removes a certain value as a possibility from an entire column

        @param column This is the column number from which a digit should be removed as a possible option
        @param value This is the digit which should be removed as a possibility.
        @param possibilities This is the list of lists containing all the possible digits for each box
        @param exceptions The list of positions to not remove digits from (usually the source box)
         */

        //loop travels from one row to another within the same column
        for (int row = 0; row < size; row++) {
            if (possibilities[row][column].contains(value) && !exceptions.contains(row)) {
                RemoveAndUpdate(row, column, value, possibilities);
            }
        }
    }

    public void removeRow (int row, int value, List<Integer>[][] possibilities, List<Integer> exceptions) {
        /*
        The removeRow method removes a certain value as a possibility from an entire row.
        This functions very similarly to the removeColumn method

        @param row This is the row number from which a digit should be removed as a possible option
        @param value This is the digit which should be removed as a possibility
        @param possibilities This is the list of lists containing all the possible digits for each box
         */

        //loop travels across the row; rest of the function matches previous one
        for (int col = 0; col < size; col++) {
            if (possibilities[row][col].contains(value) && !exceptions.contains(col)) {
                RemoveAndUpdate(row, col, value, possibilities);
            }
        }
    }

    public void removeSubSquare(int row, int column, int value, List<Integer>[][] possibilities, List<List<Integer>> exceptions) {
        /*
        The removeSubSquare method removes a certain value as a possibility from the sub-square.
        This functions very similarly to the removeColumn and removeRow methods

        @param row This is the row number of the box which is causing the changes
        @param column This is the column number of the box which is causing the changes
        @param value This is the digit which should be removed as a possibility
        @param possibilities This is the list of lists containing all the possible digits for each box
         */

        //loop goes through each box in a sub-square
        for (int otherrow = row / root * root; otherrow < row / root * root + root; otherrow++) {
            for (int othercol = column / root * root; othercol < column / root * root + root; othercol++) {
                if (possibilities[otherrow][othercol].contains(value) && !exceptions.contains(new ArrayList<>(Arrays.asList(otherrow, othercol)))) {
                    RemoveAndUpdate(otherrow, othercol, value, possibilities);
                }
            }
        }
    }

    public void removeSingles(int row, int column, int [][] sudoku, List<Integer>[][] options) {
        /*
        Whenever a box has only one possible option, it is guaranteed that it is the correct digit.
        When this occurs, the removeSingles method will remove this digit as a possible option in the
        appropriate row, column, and sub-square.

        @param index This is the box number
        @param sudoku This is the 2-D array which contains the finalized digits
        @param options This is essentially just the possibilities list
         */

        //calls 3 functions to remove this digit as a possibility from each the corresponding row, column, and sub-square
        if (options[row][column].size() == 1) {
            int digit = options[row][column].get(0);
            sudoku[row][column] = digit;
            removeRow(row, digit, options, new ArrayList<>());
            removeColumn(column, digit, options, new ArrayList<>());
            removeSubSquare(row, column, digit, options, new ArrayList<>());
        }
    }

    public void nakedDoublesRC (int row, int col, List<Integer>[][] possibilities) {
        /*
        This method compares the possibilities of the box of the given index with those in the same row and column.
        In short, a naked double is when two boxes in the same row, column, or sub-square
        have exactly 2 possible digits - both of which are the same for both boxes. When this occurs,
        no other box in the grouping may contain those 2 digits. For example,
        if two boxes in the same row both have possible solutions of 4 and 7, then no other box in the row
        may contain 4 or 7. Therefore, we can remove them as possibilities for the rest of the row.

        This method performs the aforementioned activities ONLY for rows and columns.

        @param index This is the box number
        @param possibilities This is the compilation of possible digits
         */

        //traverses through each box in the same row
        for (int other = 0; other < size; other++) {

            //checks to see if the two boxes have the same 2 possible digits and removes from rest of the row
            if (possibilities[row][other].equals(possibilities[row][col]) && possibilities[row][col].size() == 2 && other != col) {
                for (int digit : possibilities[row][other]) {
                    removeRow(row, digit, possibilities, new ArrayList<>(Arrays.asList(other, col)));
                }
            }
        }

        //same as previous loop, but checks for naked doubles in the column
        for (int other = 0; other < size; other ++) {
            if (possibilities[row][col].equals(possibilities[other][col]) && possibilities[row][col].size() == 2 && other != row) {
                for (int digit : possibilities[row][col]) {
                    removeColumn(col, digit, possibilities, new ArrayList<>(Arrays.asList(row, other)));
                }
            }
        }
    }

    public void nakedDoubleBox (int row, int col, List<Integer>[][] possibilities) {
        /*
        This method works very similarly to the previous one. However, this method addresses naked doubles
        within a sub-square instead.
         */

        //loop works similarly to those in the other naked doubles function, but for sub-squares
        for (int otherRow = row / root * root; otherRow < row / root * root + root; otherRow++) {
            for (int otherCol = col / root * root; otherCol < col / root * root + root; otherCol++) {
                if (possibilities[row][col].equals(possibilities[otherRow][otherCol]) && possibilities[row][col].size() == 2 && (row != otherRow || col != otherCol)) {
                    for (Integer digit : possibilities[row][col]) {
                        removeSubSquare(row, col, digit, possibilities, new ArrayList<>(Arrays.asList(Arrays.asList(row, col), Arrays.asList(otherRow, otherCol))));
                    }
                }
            }
        }
    }

    @SuppressWarnings("Cast")

    public void finalResort(int[][] sudoku, List<Integer>[][] available, int current) {
        /*
        The finalResort method's name accurately describes its function and purpose. It highlights the last
        strategy to solve more difficult Sudokus: backtracking. Essentially, this method tries every
        combination of possible digits for each box. If the resultant Sudoku is valid, the program will
        print it. However, recursion and backtracking are incredibly inefficient codes and can seriously increase
        run time for Sudokus with multiple solutions. Therefore, in order to combat this problem, the method simply
        guesses a single digit. Afterward, it functions exactly like earlier - it uses the aforementioned techniques
        to obtain more information. When the code can no longer identify more digits, it reverts to the brute
        force method and guesses another digit. This cycle continues until the sudoku is completed.

        @param sudoku Only for this method, the sudoku parameter contains guessed or possibly incorrect values
        @param available This is simply the possibilities object, but the code can tamper with this list, preserving
        the original possibilities
        @param current This is the box which the code is checking. If this box already contains a digit, the program
        will continue. Otherwise, it will guess a digit.
         */

        if (current < Math.pow(size, 2)) {

            int row = current / size;
            int col = current % size;

            // Initialize alternate copy of the grid and possibilities to permit guesses
            int [][] sudoku_copy = new int[size][size];
            List<Integer>[][] available_copy = (List<Integer>[][]) new ArrayList[size][size];

            // Proceed to the next box if already filled
            if (sudoku[row][col] != 0) {
                finalResort(sudoku, available, current + 1);

            } else if (!available[row][col].isEmpty()) {

                // Run the brute force method for each possibility
                for (Integer digit : available[row][col]) {

                     // Recopy all the information into our copies
                    for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                            sudoku_copy[i][j] = sudoku[i][j];
                            available_copy[i][j] = new ArrayList<>(available[i][j]);
                        }
                    }

                    // Select the desired digit and make appropriate changes
                    available_copy[row][col] = new ArrayList<>(List.of(digit));
                    sudoku_copy[row][col] = digit;
                    changes.addLast(new Cell(row, col));

                    // Convert back to proper solving strategies
                    while (!changes.isEmpty()) {
                        Cell item = changes.pollFirst();

                        int innerRow = item.row;
                        int innerCol = item.col;

                        if (available_copy[innerRow][innerCol].size() == 1) {
                            removeSingles(innerRow, innerCol, sudoku_copy, available_copy);
                        } else if (available_copy[innerRow][innerCol].size() == 2) {
                            nakedDoublesRC(innerRow, innerCol, available_copy);
                            nakedDoubleBox(innerRow, innerCol, available_copy);
                        }
                    }

                    // Trigger brute force method once again
                    finalResort(sudoku_copy, available_copy, current + 1);
                }
            }

            // Validate a filled sudoku and save it
        } else {

            if (GameFrame.isValid(size, sudoku)) {

                count++;

                int[][] copy = new int[size][size];

                for (int row = 0; row < size; row++) {
                    System.arraycopy(sudoku[row], 0, copy[row], 0, size);
                }
                answers.add(copy);
            }
        }
    }
}
