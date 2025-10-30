package com.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class Minesweeper {
    private class MineTile extends JButton {
        int r;
        int c;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }


    int tileSize = 70;
    int numRows = 8;
    int mineCount = 10;
    int numCols = numRows;
    int boardWidth = numCols * tileSize;
    int boardHeight = numRows * tileSize;

    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    Random random = new Random();

    MineTile[][] board = new MineTile[numRows][numCols];
    ArrayList<MineTile> mineList;

    int tilesClicked = 0;
    boolean gameOver = false;


    Minesweeper() {
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        // frame.setVisible(true); // Pəncərəni göstər

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + Integer.toString(mineCount));
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));//8*8
//        boardPanel.setBackground(Color.GREEN);
        frame.add(boardPanel);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
//                tile.setText("💣");

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();
                        //left click
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText() == "") {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMine(tile.r, tile.c);
                                }

                            }

                        }//rightclick
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText() == "" && tile.isEnabled()) {
                                tile.setText("🚩");
                            } else if (tile.getText() == "🚩") {
                                tile.setText("");
                            }


                        }
                    }
                });
                boardPanel.add(tile);

            }
        }
        frame.setVisible(true);//burda yazma sebebimiz budurki her sey yuklenib hazir olduqdan sonra ekran gorsensin demekdir

        setMines();

    }

    void setMines() {
        mineList = new ArrayList<>();

//        mineList.add(board[2][2]);
//        mineList.add(board[2][3]);
//        mineList.add(board[5][6]);
//        mineList.add(board[3][4]);
//        mineList.add(board[1][1]);
        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft -= 1;
            }
        }

    }

    void revealMines() {
        for (MineTile tile : mineList) {
            tile.setText("💣");
        }
        gameOver = true;
        textLabel.setText("Game Over");
    }

    void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return;
        }
        MineTile tile = board[r][c];
        if (!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        tilesClicked += 1;
        int minesFound = 0;

        //top3
        minesFound += countMine(r - 1, c - 1);//topleft
        minesFound += countMine(r - 1, c);//top
        minesFound += countMine(r - 1, c + 1);//topright
        //left and right

        minesFound += countMine(r, c - 1);//left
        minesFound += countMine(r, c + 1);//right

        minesFound += countMine(r + 1, c - 1);//bottomleft
        minesFound += countMine(r + 1, c);//bottom
        minesFound += countMine(r + 1, c + 1);//bottomright

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");
            //top3
            checkMine(r - 1, c - 1);
            checkMine(r - 1, c);
            checkMine(r - 1, c + 1);

            //left and right
            checkMine(r, c + 1);
            checkMine(r, c - 1);

            //bottom3
            checkMine(r + 1, c - 1);
            checkMine(r + 1, c);
            checkMine(r + 1, c + 1);

        }
        if (tilesClicked == numCols * numRows - mineList.size()) {
            gameOver = true;
            textLabel.setText("MinesCleared");
        }

    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return 0;
        }
        if (mineList.contains(board[r][c])) {
            return 1;
        }
        return 0;
    }
}
