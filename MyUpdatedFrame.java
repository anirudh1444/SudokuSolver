package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class MyUpdatedFrame extends JFrame implements ActionListener {

    int size; // sudoku size
    JTextField[][] values; // user-inputted values, later displays the answer
    boolean[][] original; // contains which boxes had user-inputted values
    JPanel table; // contains the size x size grid
    JButton solve = new JButton();
    JButton reset = new JButton();
    JButton clear = new JButton();
    int current = -1; // current displayed solution
    Solver solver; // actual solver
    JButton next = new JButton();
    JButton prev = new JButton();
    JLabel counts = new JLabel("", JLabel.CENTER); // label displaying total solutions
    JLabel current_count = new JLabel("", JLabel.CENTER); // label displaying current solution
    JPanel buttons = new JPanel(); // panel for some buttons

    public MyUpdatedFrame(int size) {

        // Set up frame specifications
        this.size = size;
        original = new boolean[size][size];
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
        buttons.setLayout(new GridLayout(5, 1));
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

        // Setting up title spcifications
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
        values = new JTextField[size][size];
        table = new JPanel();
        table.setLayout(new GridLayout(size, size));

        // Initializes JTextFields for each box, allowing users to input values
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Value_Box box = new Value_Box(row, col, (int) Math.sqrt(size));
                values[row][col] = box;
                table.add(box);
            }
        }


        values[0][0].setText("7");
        values[0][5].setText("2");
        values[0][7].setText("1");
        values[1][1].setText("1");
        values[1][2].setText("5");
        values[1][3].setText("6");
        values[1][6].setText("9");
        values[2][0].setText("3");
        values[2][4].setText("5");
        values[3][0].setText("1");
        values[4][1].setText("2");
        values[4][2].setText("4");
        values[4][4].setText("7");
        values[4][7].setText("9");
        values[5][5].setText("8");
        values[5][6].setText("6");
        values[6][1].setText("5");
        values[6][2].setText("9");
        values[6][4].setText("4");
        values[6][7].setText("2");
        values[7][2].setText("3");
        values[8][3].setText("7");
        values[8][8].setText("4");

        this.add(table);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /*
        Triggers different responses based on the button which the user has clicked. Some responses can be triggered
        by a variety of buttons, whereas some are button-specific. Each response performs various functions, and
        correctly disables or enables other buttons.
         */
        if (e.getSource() == solve) {

            // Solves the sudoku given by the user
            solve.setEnabled(false);
            int[][] grid = new int[size][size];

            // Checks which boxes have valid user inputs, and updates "original"
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    if (!Objects.equals(values[row][col].getText(), "")) {
                        if (!validInput(values[row][col].getText())) {
                            counts.setText("Entries must be integers!");
                            solve.setEnabled(true);
                            return;
                        }
                        int number = Integer.parseInt(values[row][col].getText());
                        if (number == 0) {
                            counts.setText("Invalid Sudoku.");
                            solve.setEnabled(true);
                            return;
                        }
                        grid[row][col] = number;
                        original[row][col] = true;
                    } else {
                        original[row][col] = false;
                    }
                }
            }

            solver = new Solver(grid);

            //Checks to make sure the given digits form a valid sudoku
            if (solver.isValid()) {
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


            } else {
                counts.setText("Invalid sudoku.");
            }

            reset.setEnabled(true);
            solve.setEnabled(false);

        } else if (e.getSource() == clear) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    values[row][col].setText("");
                }
            }

            reset.setEnabled(false);

        } else if (e.getSource() == reset) {
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    if (!original[row][col]) {
                        values[row][col].setText("");
                    }
                }
            }

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

            solve.setEnabled(true);
            next.setEnabled(false);
            prev.setEnabled(false);

            current_count.setText("");
            counts.setText("");
        }
    }

    private void setNumbers (int[][] grid) {
        // Fills in the empty boxes with the correct number in red
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (!original[row][col]) {
                    values[row][col].setForeground(Color.RED);
                    values[row][col].setText(String.valueOf(grid[row][col]));
                }
            }
        }
    }

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

    private void standardSettings (JButton button, String name, boolean enabled) {
        // Gives each button the correct size and aesthetics
        button.setText(name);
        button.setSize(100, 20);
        button.addActionListener(this);
        button.setFocusable(false);
        button.setEnabled(enabled);
    }

    private boolean validInput(String input) {
        for (int i = 0; i < input.length(); i++) {
            int ascii = input.charAt(i);
            if (ascii < '0' || ascii > '9') {
                return false;
            }
        }

        return true;
    }
}
