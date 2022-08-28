import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Sudoku_Solver7 {

    public static int size; //columns & rows

    public static int root; //total boxes

    public static List<List<Integer>> possibilities = new ArrayList<>(); //every possible digit for each box (will be the main object modified throughout the code)

    public static List<Integer> changes = new ArrayList<>(); //holds indices of boxes whose possibilities were changed, but not checked

    public static long count = 0; //total number of valid solutions

    public static void main (String [] args) {
        Scanner console = new Scanner(System.in);
        System.out.println("Enter the size of the sudoku (number of rows/columns): ");
        size = console.nextInt();
        root = (int)Math.sqrt(size); //sets up sudoku size

        int [][] sudoku = new int [size][size]; //all the guaranteed/correct numbers in the solved sudoku
        List<Integer> start = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            start.add(i);
        } //adds 1 to "size" into a list which represents all the possible digits for a box

        System.out.println("Enter the sudoku (place a \"0\" for empty boxes). Start from the top left and progress to the right:");
        for (int i = 0; i < Math.pow(size, 2); i++) {
            int row = i / size;
            int column = i % size;
            int digit = console.nextInt(); //intakes a number from the user
            if (digit == 0) {
                possibilities.add(start); //"0" represents an unknown value for a box; results in that box having every digit as a possibility
            } else if (digit > size) {
                System.out.println("Invalid number. Input a different integer."); //number outside of the size
                i--;
            }
            else {
                List<Integer> thing = new ArrayList<>();
                thing.add(digit);
                possibilities.add(thing); //sets the given digit as the ONLY possibility
                sudoku[row][column] = digit; //sets this box as the given value in the final sudoku
            }
        }

        if (!isValid(sudoku)) {
            System.out.println("Invalid sudoku.");
            System.exit(0);
        } //shuts down program if the given sudoku is invalid

        for (int i = 0; i < Math.pow(size, 2); i++) {
            if (possibilities.get(i).size() == 1) {
                removeSingles(i, sudoku, possibilities);
            } else if (possibilities.get(i).size() == 2) {
                nakedDoublesRC(i, possibilities);
                nakedDoubleBox(i, possibilities);
            }
        }

        while (changes.size() != 0) {
            if (possibilities.get(changes.get(0)).size() == 1) {
                removeSingles(changes.get(0), sudoku, possibilities);
            } else if (possibilities.get(changes.get(0)).size() == 2) {
                nakedDoublesRC(changes.get(0), possibilities);
                nakedDoubleBox(changes.get(0), possibilities);
            }
            changes.remove(0);
        }
        //System.out.println(possibilities);
        finalResort(sudoku, possibilities, 0);
        if (count == 1) {
            System.out.println("There is 1 solution. This sudoku is valid!");
        } else if (count == 0){
            System.out.println("This sudoku has no solutions and is invalid.");
        } else {
            System.out.println("This sudoku has " + count + " solutions.");
        }
    }

    public static boolean isValid (int [][] sudoku) {
        /*
        The isValid method is used to identify if the sudoku given is ever invalid.
        The sudoku is considered invalid if any number is repeated in a row,
        column, or sub-square.

        @param sudoku This is the sudoku containing the finalized digits
        @return boolean Returns false if the sudoku is invalid and true otherwise
         */

        //keeps count of how many times each digit ranging from 0 to size appears
        int [] values = new int [size + 1];

        //checks digits within the row
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                //increments the digit in the values array
                values[sudoku[i][j]]++;

                //returns false whenever a digit aside from '0' appears multiple times
                if (values[sudoku[i][j]] > 1 && sudoku[i][j] != 0) {
                    return false;
                }
            }
            //resets array
            Arrays.fill(values, 0);
        }

        //similar to the previous for loop, but checks digits in each column
        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku.length;j++) {
                values[sudoku[j][i]]++;
                if (values[sudoku[j][i]] > 1 && sudoku[j][i] != 0) {
                    return false;
                }
            }
            Arrays.fill(values, 0);
        }

        //similar to previous 2 loops, but checks digits within each sub-square
        for (int i = 0; i < root; i++) {
            for (int j = 0; j < root; j++) {
                for (int k = i * root; k < (i + 1) * root; k++) {
                    for (int l = j * root; l < (j + 1) * root; l++) {
                        values[sudoku[k][l]]++;
                        if (values[sudoku[k][l]] > 1 && sudoku[k][l] != 0) {
                            return false;
                        }
                    }
                }
                Arrays.fill(values, 0);
            }
        }

        //returns true when no digit has been repeated
        return true;
    }

    public static boolean isUnfinished(int [][] sudoku) {
        /*
        This method checks to see if the sudoku is completed by searching for a '0' in the array,
        indicating an unknown value

        @param sudoku This is the sudoku containing the finalized digits
        @return boolean Returns true if the sudoku is unfinished
         */

        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku[0].length; j++) {

                //returns true when the sudoku is missing a digit
                if (sudoku[i][j] == 0) {
                    return true;
                }
            }
        }

        //returns false when box contains a digit ranging from 1 to size
        return false;
    }

    public static void removeColumn(int column, int value, List<List<Integer>> possibilities) {
        /*
        The removeColumn method removes a certain value as a possibility from an entire column

        @param column This is the column number from which a digit should be removed as a possible option
        @param value This is the digit which should be removed as a possibility
        @param possibilities This is the list of lists containing all the possible digits for each box
         */

        //loop travels from one row to another within the same column
        for (int k = 0; k < size; k++) {
            if (possibilities.get(size * k + column).contains(value)) {

                //creates a new list identical to the possibilities list for that box and removes the specified value
                ArrayList<Integer> extra = new ArrayList<>(possibilities.get(size * k + column));
                extra.remove((Integer) value);
                possibilities.set(size * k + column, extra);

                //any box which has value removed as a possibility is added to the changes list
                if (!changes.contains(size * k + column)) {
                    changes.add(size * k + column);
                }
            }
        }
    }

    public static void removeRow (int row, int value, List<List<Integer>> possibilities) {
        /*
        The removeRow method removes a certain value as a possibility from an entire row.
        This functions very similarly to the removeColumn method

        @param row This is the row number from which a digit should be removed as a possible option
        @param value This is the digit which should be removed as a possibility
        @param possibilities This is the list of lists containing all the possible digits for each box
         */

        //loop travels across the row; rest of the function matches previous one
        for (int k = 0; k < size; k++) {
            if (possibilities.get(size * row + k).contains(value)) {
                ArrayList<Integer> extra = new ArrayList<>(possibilities.get(size * row + k));
                extra.remove((Integer) value);
                possibilities.set(size * row + k, extra);
                if (!changes.contains(size * row + k)) {
                    changes.add(size * row + k);
                }
            }
        }
    }

    public static void removeSubSquare(int row, int column, int value, List<List<Integer>> possibilities) {
        /*
        The removeSubSquare method removes a certain value as a possibility from the sub-square.
        This functions very similarly to the removeColumn and removeRow methods

        @param row This is the row number of the box which is causing the changes
        @param column This is the column number of the box which is causing the changes
        @param value This is the digit which should be removed as a possibility
        @param possibilities This is the list of lists containing all the possible digits for each box
         */

        //loop goes through each box in a sub-square
        for (int k = row / root * root; k < row / root * root + root; k++) {
            for (int l = column / root * root; l < column / root * root + root; l++) {
                if (possibilities.get(size * k + l).contains(value)) {
                    ArrayList<Integer> extra = new ArrayList<>(possibilities.get(size * k + l));
                    extra.remove((Integer) value);
                    possibilities.set(size * k + l, extra);
                    if (!changes.contains(size * k + l)) {
                        changes.add(size * k + l);
                    }
                }
            }
        }
    }

    public static void removeSingles(int index, int [][] sudoku, List<List<Integer>> options) {
        /*
        Whenever a box has only one possible option, it is guaranteed that it is the correct digit.
        When this occurs, the removeSingles method will remove this digit as a possible option in the
        appropriate row, column, and sub-square.

        @param index This is the box number
        @param sudoku This is the 2-D array which contains the finalized digits
        @param options This is essentially just the possibilities list
         */

        //identify row and column given the box number
        int row = index / size;
        int column = index % size;

        //calls 3 functions to remove this digit as a possibility from each the corresponding row, column, and sub-square
        if (options.get(index).size() == 1) {
            int digit = options.get(index).get(0);
            sudoku[row][column] = digit;
            removeRow(row, digit, options);
            removeColumn(column, digit, options);
            removeSubSquare(row, column, digit, options);
        }
    }

    public static void nakedDoublesRC (int index, List<List<Integer>> possibilities) {
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

        int row = index / size;
        int col = index % size;

        //traverses through each box in the same row as the parameter
        for (int other = row * size; other < (row + 1) * size; other++) {

            //checks to see if the two boxes have the same 2 possible digits
            if (possibilities.get(index).equals(possibilities.get(other)) && possibilities.get(index).size() == 2 && index != other) {

                //checks to see if the changes list already contains "other"
                boolean isPresent = false;
                if (changes.contains(other)) {
                    isPresent = true;
                }

                //obtain the two digits
                int digit1 = possibilities.get(index).get(0);
                int digit2 = possibilities.get(index).get(1);

                //remove the two digits as possibilities from the rest of the row
                removeRow(row, digit1, possibilities);
                removeRow(row, digit2, possibilities);
                possibilities.get(index).add(digit1);
                possibilities.get(index).add(digit2);
                possibilities.get(other).add(digit1);
                possibilities.get(other).add(digit2);

                /*The removeRow function will place "other" into the changes list despite already showing up.
                Thus, we must remove it if it was not already in there before this function was performed.
                 */
                if (!isPresent) {
                    changes.remove((Integer) other);
                }
            }
        }

        //same as previous loop, but checks for naked doubles in the column
        for (int other = col; other < Math.pow(size, 2); other += size) {
            if (possibilities.get(index).equals(possibilities.get(other)) && possibilities.get(index).size() == 2 && other != index) {
                boolean isPresent = false;
                if (changes.contains(other)) {
                    isPresent = true;
                }

                int digit1 = possibilities.get(index).get(0);
                int digit2 = possibilities.get(index).get(1);

                removeColumn(col, digit1, possibilities);
                removeColumn(col, digit2, possibilities);

                possibilities.get(index).add(digit1);
                possibilities.get(index).add(digit2);

                possibilities.get(other).add(digit1);
                possibilities.get(other).add(digit2);

                if (!isPresent) {
                    changes.remove((Integer) other);
                }
            }
        }
    }

    public static void nakedDoubleBox (int box, List<List<Integer>> possibilities) {
        /*
        This method works very similarly to the previous one. However, this method addresses naked doubles
        within a sub-square as opposed to rows or columns.
         */

        int row = box / size;
        int col = box % size;

        //loop works similarly to those in the other naked doubles function, but for sub-squares
        for (int comparison_row = row / root * root; comparison_row < row / root * root + root; comparison_row++) {
            for (int comparison_col = col / root * root; comparison_col < col / root * root + root; comparison_col++) {
                boolean isPresent = false;
                int comparison_index = comparison_row * size + comparison_col;
                if (changes.contains(comparison_index)) {
                    isPresent = true;
                }

                if (possibilities.get(box).equals(possibilities.get(comparison_index)) && box != comparison_index && possibilities.get(comparison_index).size() == 2) {
                    int digit1 = possibilities.get(box).get(0);
                    int digit2 = possibilities.get(box).get(1);

                    removeSubSquare(row, col, digit1, possibilities);
                    removeSubSquare(row, col, digit2, possibilities);

                    possibilities.get(box).add(digit1);
                    possibilities.get(box).add(digit2);

                    possibilities.get(comparison_index).add(digit1);
                    possibilities.get(comparison_index).add(digit2);

                    if (!isPresent) {
                        changes.remove((Integer) comparison_index);
                    }
                }
            }
        }
    }

    public static void finalResort(int [][]sudoku, List<List<Integer>> available, int current) {
        /*
        The finalResort method's name accurately describes its function and purpose. It highlights the last
        strategy to solve more difficult Sudokus: backtracking. Essentially, this method tries every
        combination of possible digits for each box. If the resultant Sudoku is valid, the program will
        print it. However, recursion and backtracking are incredibly inefficient codes and can seriously increase
        run time for Sudokus with multiple solutions. Therefore, in order to combat this problem, the method simply
        guesses a single digit. Afterwards, it functions exactly like earlier - it uses the aforementioned techniques
        to obtain more information. When the code can no longer identify more digits, it reverts back to the brute
        force method and guesses another digit. This cycle continues until the sudoku is completed.

        @param sudoku Only for this method, the sudoku parameter contains guessed or possibly incorrect values
        @param available This is simply the possibilities object, but the code can tamper with this list, preserving
        the original possibilities
        @param current This is the box which the code is checking. If this box already contains a digit, the program
        will continue. Otherwise, it will guess a digit.
         */

        if (current < Math.pow(size, 2)) {
            int [][] sudoku_copy = new int [size][size];
            for (int j = 0; j < size; j++) {
                System.arraycopy(sudoku[j], 0, sudoku_copy[j], 0, size);
            }
            List<List<Integer>> available_copy = new ArrayList<>();
            available_copy.addAll(available);

            int current_copy = current;

            int row = current / size;
            int col = current % size;

            if (sudoku[row][col] == 0 && available.get(current).size() > 0) {
                for (int i = 0; i < available.get(current).size(); i++) {
                    for (int j = 0; j < size; j++) {
                        System.arraycopy(sudoku[j], 0, sudoku_copy[j], 0, size);
                    }
                    available_copy.clear();
                    available_copy.addAll(available);
                    current_copy = current;

                    int digit = available.get(current).get(i);
                    sudoku_copy[row][col] = digit;
                    List<Integer> extra = List.of(digit);
                    available_copy.set(row * size + col, extra);

                    for (int j = 0; j < Math.pow(size, 2); j++) {
                        if (available_copy.get(j).size() == 1) {
                            removeSingles(j, sudoku_copy, available_copy);
                        } else if (available_copy.get(j).size() == 2) {
                            nakedDoublesRC(j, available_copy);
                            nakedDoubleBox(j, available_copy);
                        }
                    }

                    while (changes.size() != 0) {
                        if (available_copy.get(changes.get(0)).size() == 1) {
                            removeSingles(changes.get(0), sudoku_copy, available_copy);
                        } else if (available_copy.get(changes.get(0)).size() == 2) {
                            nakedDoublesRC(changes.get(0), available_copy);
                            nakedDoubleBox(changes.get(0), available_copy);
                        }
                        changes.remove(0);
                    }

                    current_copy++;
                    finalResort(sudoku_copy, available_copy, current_copy);
                }
            } else {
                current_copy++;
                finalResort(sudoku_copy, available_copy, current_copy);
            }
        } else {
            if (!isUnfinished(sudoku)) {
                count++;
                printSudoku(sudoku);
                System.out.println();
            }
        }
    }

    public static void printSudoku(int [][] sudoku) {
        /*
        This method prints out a fully completed sudoku with appropriate spacing to make
        reading the final solution clean and straightforward.

        @param sudoku: a correct solution
         */

        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku[0].length; j++) {
                System.out.print(sudoku[i][j] + " ");
            }
            System.out.println();
        }
    }
}
