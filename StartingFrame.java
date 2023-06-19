package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class StartingFrame extends JFrame implements ActionListener {

    JButton enter = new JButton();
    JTextField response = new JTextField();
    JLabel invalid = new JLabel("", JLabel.CENTER);

    StartingFrame() {
        this.setTitle("Sudoku Solver");
        this.setSize(700, 700);
        this.setResizable(false);
        this.setLayout(null);
        this.getContentPane().setBackground(new Color(164, 188, 245));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel title_box = new JPanel();
        title_box.setBackground(new Color(164, 188, 245));
        title_box.setBounds(0, 80, 700, 100);
        JLabel title = new JLabel("Sudoku Solver");
        title.setFont(new Font("Times New Roman", Font.BOLD, 50));
        title_box.add(title);

        JPanel question_box = new JPanel();
        question_box.setBackground(new Color(164, 188, 245));
        question_box.setBounds(0, 350, 700, 100);
        JLabel question = new JLabel("Enter Sudoku Size");
        question.setFont(new Font ("Times New Roman", Font.ITALIC, 39));
        question_box.add(question);

        response.setSize(80, 80);
        response.setBounds(290, 220, 120, 120);
        response.setHorizontalAlignment(JTextField.CENTER);
        response.setFont(new Font("ChelthmITC Bk BT", Font.PLAIN, 40));

        enter.setText("Continue");
        enter.setFocusable(false);
        enter.setBounds(250, 450, 200, 60);
        enter.setFont(new Font("Trebuchet", Font.BOLD, 24));
        enter.addActionListener(this);

        invalid.setBounds(0, 575, 700, 20);
        invalid.setVisible(true);

        this.add(invalid);
        this.add(enter);
        this.add(question_box);
        this.add(title_box);
        this.add(response);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == enter && !Objects.equals(response.getText(), "")) {
            if (!validInput(response.getText())) {
                invalid.setText("Input must be an integer!");
                return;
            }
            if (Math.sqrt(Integer.parseInt(response.getText())) % 1 != 0) {
                invalid.setText("Invalid Size");
                return;
            }

            MyUpdatedFrame frame = new MyUpdatedFrame(Integer.parseInt(response.getText()));
            this.dispose();
            frame.setVisible(true);
        }
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
