package com.company;

import javax.swing.*;
import java.awt.*;

public class CellBox extends JTextField {

    public CellBox(int row, int col, int size) {

        this.setPreferredSize(new Dimension(40, 40));

        if ((row / size + col / size) % 2 == 0) {
            this.setBackground(new Color(164, 188, 245));
        } else {
            this.setBackground(Color.white);
        }

        this.setFont(new Font("Times New Roman", Font.PLAIN, 250 / (int)Math.pow(size, 2)));
        this.setHorizontalAlignment(JTextField.CENTER);
    }
}
