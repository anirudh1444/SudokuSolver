package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Solver {
    int[][] grid; // Sudoku board
    int size; // # of rows and columns
    int root; // length of sub-square
    ArrayList<Integer>[][] possibilities;   //every possible digit for each box (will be the main object modified throughout the code)
    List<List<Integer>> changes = new ArrayList<>(); // holds the indices of boxes which have been updated and must be rechecked
    long count = 0; //total number of valid solutions
    List<int[][]> answers = new ArrayList<>();

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

        // States that any number is a possibility for any box
        for (Object[] row : possibilities) {
            for (int j = 0; j < size; j++) {
                row[j] = new ArrayList<>(start);
            }
        }

        // Changes the possible options for any box to the one given, if any
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col] != 0) {
                    possibilities[row][col] = new ArrayList<>(List.of(grid[row][col]));
                    changes.add(new ArrayList<>(Arrays.asList(row, col)));
                }
            }
        }
    }

    public void solve () {

        while (changes.size() != 0) {
            List<Integer> box = changes.remove(0);
            if (possibilities[box.get(0)][box.get(1)].size() == 1) {
                removeSingles(box.get(0), box.get(1), grid, possibilities);
            } else if (possibilities[box.get(0)][box.get(1)].size() == 2) {
                nakedDoublesRC(box.get(0), box.get(1), possibilities);
                nakedDoubleBox(box.get(0), box.get(1), possibilities);
            }
        }

        finalResort(grid, possibilities, 0);
    }

    public boolean isValid() {
        /*
        The isValid method is used to identify if the sudoku given is ever invalid.
        The sudoku is considered invalid if any number is repeated in a row,
        column, or sub-square.

        @param sudoku This is the sudoku containing the finalized digits
        @return boolean Returns false if the sudoku is invalid and true otherwise
         */

        // Assures that all inputted numbers are valid for this sized sudoku
        for (int[] row : grid) {
            for (int value : row) {
                if (value < 0 || value > size) {
                    return false;
                }
            }
        }

        // Digit frequency counter
        int [] values = new int [size + 1];

        // Checks frequency of each number for each row
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                //increments the digit in the values array
                values[grid[i][j]]++;

                //returns false whenever a digit aside from '0' appears multiple times
                if (values[grid[i][j]] > 1 && grid[i][j] != 0) {
                    return false;
                }
            }
            //resets array
            Arrays.fill(values, 0);
        }

        // Checks frequency of each number for each column
        for (int i = 0; i < grid.length; i++) {
            for (int[] ints : grid) {
                values[ints[i]]++;
                if (values[ints[i]] > 1 && ints[i] != 0) {
                    return false;
                }
            }
            Arrays.fill(values, 0);
        }

        // Checks frequency of each number for each sub-square
        for (int i = 0; i < root; i++) {
            for (int j = 0; j < root; j++) {
                for (int k = i * root; k < (i + 1) * root; k++) {
                    for (int l = j * root; l < (j + 1) * root; l++) {
                        values[grid[k][l]]++;
                        if (values[grid[k][l]] > 1 && grid[k][l] != 0) {
                            return false;
                        }
                    }
                }
                Arrays.fill(values, 0);
            }
        }

        // Returns true if sudoku is valid
        return true;
    }

    public boolean isUnfinished(int [][] sudoku) {
        /*
        This method checks to see if the sudoku is completed by searching for a '0' in the array,
        indicating an unknown value

        @param sudoku This is the sudoku containing the finalized digits
        @return boolean Returns true if the sudoku is unfinished
         */

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (sudoku[i][j] == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void RemoveAndUpdate(int row, int col, int value, List<Integer>[][] possibilities) {
        // Removes value as an option
        possibilities[row][col].remove((Integer) value);

        //any box which has value removed as a possibility is added to the changes list
        List<Integer> item = new ArrayList<>(Arrays.asList(row, col));
        if (!changes.contains(item)) {
            changes.add(item);
        }
    }

    public void removeColumn(int column, int value, List<Integer>[][] possibilities, List<Integer> exceptions) {
        /*
        The removeColumn method removes a certain value as a possibility from an entire column

        @param column This is the column number from which a digit should be removed as a possible option
        @param value This is the digit which should be removed as a possibility
        @param possibilities This is the list of lists containing all the possible digits for each box
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
        no other box in the row, column, or sub-square may contain those 2 digits. For example,
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
        within a sub-square as opposed to rows or columns.
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
        guesses a single digit. Afterwards, it functions exactly like earlier - it uses the aforementioned techniques
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

            if (sudoku[row][col] == 0  && available[row][col].size() > 0) {

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
                    changes.add(new ArrayList<>(Arrays.asList(row, col)));

                    // Convert back to proper solving strategies
                    while (changes.size() != 0) {
                        List<Integer> item = changes.remove(0);
                        if (available_copy[item.get(0)][item.get(1)].size() == 1) {
                            removeSingles(item.get(0), item.get(1), sudoku_copy, available_copy);
                        } else if (available_copy[row][col].size() == 2) {
                            nakedDoublesRC(row, col, available_copy);
                            nakedDoubleBox(row, col, available_copy);
                        }
                    }

                    // Trigger brute force method once again
                    finalResort(sudoku_copy, available_copy, current + 1);
                }

            } else {
                finalResort(sudoku, available, current + 1);
            }

        } else {
            if (!isUnfinished(sudoku)) {
                count++;

                int[][] copy = new int[size][size];

                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        copy[row][col] = sudoku[row][col];
                    }
                }
                answers.add(copy);
            }
        }
    }

    public void printSudoku(int [][] sudoku) {
        /*
        This method prints out a fully completed sudoku with appropriate spacing to make
        reading the final solution clean and straightforward.

        @param sudoku: a correct solution
         */

        for (int[] ints : sudoku) {
            for (int j = 0; j < sudoku[0].length; j++) {
                System.out.print(ints[j] + " ");
            }
            System.out.println();
        }
    }
}
