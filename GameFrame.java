package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Objects;

public class GameFrame extends JFrame implements ActionListener {

    int size; // sudoku size (size x size)
    CellBox[][] values; // user-inputted values & later displays the answer
    int[][] original; // grid from the most recent solve
    JPanel table; // contains the size x size grid
    JButton solve = new JButton(); // button to solve the sudoku
    JButton reset = new JButton(); // button to reset to the previously solved sudoku
    JButton clear = new JButton(); // button to completely clear the board
    int current = -1; // current displayed solution
    Solver solver; // the solver itself
    JButton next = new JButton(); // button to move to the next solution
    JButton prev = new JButton(); // button to move back a solution
    JLabel counts = new JLabel("", JLabel.CENTER); // label displaying total solutions
    JLabel current_count = new JLabel("", JLabel.CENTER); // label displaying the solution currently being viewed
    JPanel buttons = new JPanel(); // panel to hold the buttons

    public GameFrame(int size) {

        // Set up frame specifications
        this.size = size;
        original = new int[size][size];
        this.setTitle("Sudoku Solver");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.pack();
        this.setSize(700, 700);
        this.setResizable(false);

        //Creates the grid holding the sudoku values
        createTable();
        table.setBounds(50, 100, 450, 450);

        // Formatting the grid containing the buttons
        buttons.setLayout(new GridLayout(3, 1));
        buttons.setBounds(550, 300, 100, 100);
        this.add(buttons);

        // Setting up each button
        standardSettings(solve, "Solve!", true);
        standardSettings(reset, "Reset", false);
        standardSettings(clear, "Clear", true);
        standardSettings(next, "Next", false);
        standardSettings(prev, "Previous", false);
        buttons.add(solve);
        buttons.add(reset);
        buttons.add(clear);
        next.setBounds(400, 590, 100, 20);
        prev.setBounds(50, 590, 100, 20);
        this.add(next);
        this.add(prev);


        // Adding the two labels
        counts.setBounds(50, 550, 450, 50);
        this.add(counts);

        current_count.setLayout(new FlowLayout());
        current_count.setBounds(50, 600, 450, 50);
        this.add(current_count);

        // Setting up title specifications
        JLabel title = new JLabel("Sudoku Solver", JLabel.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 50));
        title.setBounds(50, 0, 450, 100);
        this.add(title);

        this.setVisible(true);
    }

    public void createTable() {
        /*
        Creates the table which holds all the boxes for users to input values of the appropriate size
         */
        values = new CellBox[size][size];
        table = new JPanel();
        table.setLayout(new GridLayout(size, size));

        // Initializes JTextFields for each box, allowing users to input values
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                CellBox box = new CellBox(row, col, (int) Math.sqrt(size));
                values[row][col] = box;
                table.add(box);
            }
        }

        this.add(table);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /*
        Triggers different responses based on the button which the user has clicked. Some responses can be triggered
        by a variety of buttons, whereas some are button-specific. Each response performs various functions, and
        correctly disables or enables other buttons.
         */

        // Solves the sudoku given by the user
        if (e.getSource() == solve) {

            int[][] grid = new int[size][size];

            // Checks to make sure boxes have valid inputs
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    if (!Objects.equals(values[row][col].getText(), "")) {

                        // Reject non-integers
                        if (!StartingFrame.validInput(values[row][col].getText())) {
                            counts.setText("Entries must be positive integers!");
                            return;
                        }

                        // Reject an input of 0
                        int number = Integer.parseInt(values[row][col].getText());
                        if (number == 0) {
                            counts.setText("Invalid Sudoku.");
                            return;
                        }
                        grid[row][col] = number;
                    }
                }
            }

            // Future resets will now be to this grid
            for (int i = 0; i < size; i++) {
                System.arraycopy(grid[i], 0, original[i], 0, size);
            }

            // Create an instance of the solver
            solver = new Solver(grid);

            //Checks to make sure the given digits form a valid sudoku
            if (isValid(size, grid)) {

                // Modify all the buttons and solve the sudoku
                solve.setEnabled(false);
                solver.solve();
                current = -1;
                this.changeAnswer(1);
                next.setEnabled(true);
                prev.setEnabled(true);

                // Updates the text for the label
                if (solver.count == 1) {
                    counts.setText("There is 1 solution. This sudoku is valid!");
                } else if (solver.count == 0){
                    counts.setText("This sudoku has no solutions and is invalid.");
                } else {
                    counts.setText("This sudoku has " + solver.count + " solutions.");
                }

                counts.setVisible(true);
                setBoxesEditable(false);

            } else {

                // Incorrect numbers will result in an invalid sudoku
                counts.setText("Invalid sudoku.");
            }

            reset.setEnabled(true);

            // Clearing the grid will erase all numbers on the grid
        } else if (e.getSource() == clear) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    values[row][col].setText("");
                }
            }

            reset.setEnabled(false);
            solve.setEnabled(true);

            // Resetting will restore the grid to the previously asked grid to solve
        } else if (e.getSource() == reset) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    if (original[row][col] == 0) {
                        values[row][col].setText("");
                    } else {
                        values[row][col].setText(String.valueOf(original[row][col]));
                    }
                }
            }

            // Moves to the next or the previous solution found if possible
        } else if (e.getSource() == next) {
            changeAnswer(1);
        } else if (e.getSource() == prev) {
            changeAnswer(-1);
        }

        if (e.getSource() == reset || e.getSource() == clear) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    values[row][col].setForeground(Color.BLACK);
                }
            }

            setBoxesEditable(true);
            solve.setEnabled(true);
            next.setEnabled(false);
            prev.setEnabled(false);

            current_count.setText("");
            counts.setText("");
        }
    }

    // Fills in the empty boxes with the correct number in red
    private void setNumbers (int[][] grid) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (original[row][col] == 0) {
                    values[row][col].setForeground(Color.RED);
                    values[row][col].setText(String.valueOf(grid[row][col]));
                }
            }
        }
    }

    // Performs necessary modifications to display a different grid
    private void changeAnswer (int modifier) {
        // Changes the solution which appears on the board
        current += modifier;
        if (current < solver.answers.size() && current >= 0) {
            setNumbers(solver.answers.get(current));
        } else {
            current -= modifier;
        }

        // Changes the corresponding label
        if (solver.count != 0) {
            current_count.setText("You are on solution " + (current + 1) + " out of " + solver.count + ".");
        }
    }

    // Assigns all buttons a certain set of standaradized settings
    private void standardSettings (JButton button, String name, boolean enabled) {
        // Gives each button the correct size and aesthetics
        button.setText(name);
        button.setSize(100, 20);
        button.addActionListener(this);
        button.setFocusable(false);
        button.setEnabled(enabled);
    }

    // Turns on and off the ability to modify the boxes (cannot modify boxes after solving; must reset or clear)
    private void setBoxesEditable (boolean permission) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                values[i][j].setEditable(permission);
            }
        }
    }

    public static boolean isValid(int size, int[][] grid) {
        /*
        The isValid method is used to identify if the sudoku given is ever invalid.
        The sudoku is considered invalid if any number is repeated in a row,
        column, or sub-square.

        @return boolean Returns false if the sudoku is invalid
         */

        int root = (int)Math.sqrt(size);

        // Assures that all inputted numbers are valid for this sized sudoku
        for (int[] row : grid) {
            for (int value : row) {
                if (value < 0 || value > size) {
                    return false;
                }
            }
        }

        // Digit frequency counter
        int [] values = new int[size + 1];

        // Checks frequency of each number for each row
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                // increments the digit in the values array
                values[grid[i][j]]++;

                // returns false whenever a digit aside from '0' appears multiple times
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

}
