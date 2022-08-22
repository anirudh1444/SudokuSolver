import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Sudoku_Solver7 {

    public static int size;

    public static int root;

    public static List<List<Integer>> possibilities = new ArrayList<>();

    public static List<Integer> changes = new ArrayList<>();

    public static long count = 0;

    public static void main (String [] args) {
        Scanner console = new Scanner(System.in);
        System.out.println("Enter the size of the sudoku (number of rows/columns): ");
        size = console.nextInt();
        root = (int)Math.sqrt(size);

        int [][] sudoku = new int [size][size];
        List<Integer> start = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            start.add(i);
        }
        System.out.println("Enter the sudoku (place a \"0\" for empty boxes). Start from the top left and progress to the right:");
        for (int i = 0; i < Math.pow(size, 2); i++) {
            int row = i / size;
            int column = i % size;
            int digit = console.nextInt();
            if (digit == 0) {
                possibilities.add(start);
            } else if (digit > size) {
                System.out.println("Invalid number. Input a different integer.");
                i--;
            }
            else {
                List<Integer> thing = new ArrayList<>();
                thing.add(digit);
                possibilities.add(thing);
                sudoku[row][column] = digit;
            }
        }
        //System.out.println("info received");

        if (!isValid(sudoku)) {
            System.out.println("Invalid sudoku.");
            System.exit(0);
        }

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
        int [] values = new int [size + 1];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size;j++) {
                values[sudoku[i][j]]++;
                if (values[sudoku[i][j]] > 1 && sudoku[i][j] != 0) {
                    return false;
                }
            }
            Arrays.fill(values, 0);
        }
        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku.length;j++) {
                values[sudoku[j][i]]++;
                if (values[sudoku[j][i]] > 1 && sudoku[j][i] != 0) {
                    return false;
                }
            }
            Arrays.fill(values, 0);
        }
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
        return true;
    }

    public static boolean isUnfinished(int [][] sudoku) { // checks if sudoku is completed
        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku[0].length; j++) {
                if (sudoku[i][j] == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void removeColumn(int column, int value, List<List<Integer>> possibilities) {
        for (int k = 0; k < size; k++) {
            if (possibilities.get(size * k + column).contains(value)) {
                ArrayList<Integer> extra = new ArrayList<>(possibilities.get(size * k + column));
                extra.remove((Integer) value);
                possibilities.set(size * k + column, extra);
                if (!changes.contains(size * k + column)) {
                    changes.add(size * k + column);
                }
            }
        }
    }

    public static void removeRow (int row, int value, List<List<Integer>> possibilities) {
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

    public static void removeBox (int row, int column, int value, List<List<Integer>> possibilities) {
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
        int row = index / size;
        int column = index % size;
        if (options.get(index).size() == 1) {
            int digit = options.get(index).get(0);
            sudoku[row][column] = digit;
            removeRow(row, digit, options);
            removeColumn(column, digit, options);
            removeBox(row, column, digit, options);
        }
    }

    public static void nakedDoublesRC (int index, List<List<Integer>> possibilities) {
        int row = index / size;
        int col = index % size;
        for (int other = row * size; other < (row + 1 ) * size; other++) {
            if (possibilities.get(index).equals(possibilities.get(other)) && possibilities.get(index).size() == 2 && index != other) {
                boolean isPresent = false;
                if (changes.contains(other)) {
                    isPresent = true;
                }
                int digit1 = possibilities.get(index).get(0);
                int digit2 = possibilities.get(index).get(1);

                removeRow(row, digit1, possibilities);
                removeRow(row, digit2, possibilities);

                possibilities.get(index).add(digit1);
                possibilities.get(index).add(digit2);

                possibilities.get(other).add(digit1);
                possibilities.get(other).add(digit2);

                if (!isPresent) {
                    changes.remove((Integer) other);
                }
            }
        }
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
        int row = box / size;
        int col = box % size;
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

                    removeBox(row, col, digit1, possibilities);
                    removeBox(row, col, digit2, possibilities);

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
        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku[0].length; j++) {
                System.out.print(sudoku[i][j] + " ");
            }
            System.out.println();
        }
    }
}
