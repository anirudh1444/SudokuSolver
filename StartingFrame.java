package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class StartingFrame extends JFrame implements ActionListener {

    JButton enter = new JButton(); // Enter button
    JTextField response = new JTextField(); // User input (grid size)
    JLabel invalid = new JLabel("", JLabel.CENTER); // Statement in case user input is not a positive integer

    StartingFrame() {

        // Setup the title and opening screen
        this.setTitle("Sudoku Solver");
        this.setSize(700, 700);
        this.setResizable(false);
        this.setLayout(null);
        this.getContentPane().setBackground(new Color(164, 188, 245));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setup title box
        JPanel title_box = new JPanel();
        title_box.setBackground(new Color(164, 188, 245));
        title_box.setBounds(0, 80, 700, 100);
        JLabel title = new JLabel("Sudoku Solver");
        title.setFont(new Font("Times New Roman", Font.BOLD, 50));
        title_box.add(title);

        // Setup Sudoku size text box
        JPanel question_box = new JPanel();
        question_box.setBackground(new Color(164, 188, 245));
        question_box.setBounds(0, 350, 700, 100);
        JLabel question = new JLabel("Enter Sudoku Size");
        question.setFont(new Font ("Times New Roman", Font.ITALIC, 39));
        question_box.add(question);

        // Setup box for user to input a sudoku size
        response.setSize(80, 80);
        response.setBounds(290, 220, 120, 120);
        response.setHorizontalAlignment(JTextField.CENTER);
        response.setFont(new Font("ChelthmITC Bk BT", Font.PLAIN, 40));

        // Setup button to continue the process and generate a blank sudoku of the given size
        enter.setText("Continue");
        enter.setFocusable(false);
        enter.setBounds(250, 450, 200, 60);
        enter.setFont(new Font("Trebuchet", Font.BOLD, 24));
        enter.addActionListener(this);

        // Setup invalid input message text box
        invalid.setBounds(0, 575, 700, 20);

        // Set all these boxes to visible on the screen
        this.add(invalid);
        this.add(enter);
        this.add(question_box);
        this.add(title_box);
        this.add(response);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Validate inupt given is a square integer
        if (e.getSource() == enter && !Objects.equals(response.getText(), "")) {

            // Detects the input contains only digits
            if (!validInput(response.getText())) {
                invalid.setText("Input must be a positive integer!");
                return;
            }

            // Asserts that the input is a square number
            int input = Integer.parseInt(response.getText());
            if (Math.sqrt(input) % 1 != 0 || input == 0) {
                invalid.setText("Invalid Size");
                return;
            }

            GameFrame frame = new GameFrame(input);
            this.dispose();
            frame.setVisible(true);
        }
    }

    public static boolean validInput(String input) {
        return input.matches("[0-9]+");
    }
}
