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
    JButton reloadButton = new JButton("Reload 🔄");

    MineTile[][] board = new MineTile[numRows][numCols];
    ArrayList<MineTile> mineList;

    int tilesClicked = 0;
    boolean gameOver = false;
    boolean minesSet = false; // <--- ilk klikdən sonra minalar yerləşdiriləcək

    Minesweeper() {
        frame.setSize(boardWidth, boardHeight + 100);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Üst panel (text + reload button)
        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + mineCount);
        textLabel.setOpaque(true);

        reloadButton.setFocusable(false);
        reloadButton.setFont(new Font("Arial", Font.PLAIN, 18));
        reloadButton.addActionListener(e -> resetGame()); // <--- Restart funksiyası

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.CENTER);
        textPanel.add(reloadButton, BorderLayout.EAST);
        frame.add(textPanel, BorderLayout.NORTH);

        // Oyunun sahəsi
        boardPanel.setLayout(new GridLayout(numRows, numCols));
        frame.add(boardPanel, BorderLayout.CENTER);

        // Butonların yaradılması
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) return;

                        MineTile clickedTile = (MineTile) e.getSource();

                        // İlk klikdən sonra minalar yerləşdirilir
                        if (!minesSet && e.getButton() == MouseEvent.BUTTON1) {
                            setMines(clickedTile);
                            minesSet = true;
                        }

                        // Sol klik
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (clickedTile.getText().equals("")) {
                                if (mineList.contains(clickedTile)) {
                                    revealMines();
                                } else {
                                    checkMine(clickedTile.r, clickedTile.c);
                                }
                            }
                        }
                        // Sağ klik (bayraq)
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (clickedTile.getText().equals("") && clickedTile.isEnabled()) {
                                clickedTile.setText("🚩");
                            } else if (clickedTile.getText().equals("🚩")) {
                                clickedTile.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }

        frame.setVisible(true);
    }

    // Oyunu sıfırlayan metod
    void resetGame() {
        gameOver = false;
        tilesClicked = 0;
        minesSet = false;
        textLabel.setText("Minesweeper: " + mineCount);
        boardPanel.removeAll();
        mineList = new ArrayList<>();

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) return;
                        MineTile clickedTile = (MineTile) e.getSource();

                        if (!minesSet && e.getButton() == MouseEvent.BUTTON1) {
                            setMines(clickedTile);
                            minesSet = true;
                        }

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (clickedTile.getText().equals("")) {
                                if (mineList.contains(clickedTile)) {
                                    revealMines();
                                } else {
                                    checkMine(clickedTile.r, clickedTile.c);
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (clickedTile.getText().equals("") && clickedTile.isEnabled()) {
                                clickedTile.setText("🚩");
                            } else if (clickedTile.getText().equals("🚩")) {
                                clickedTile.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    // Minaların yerləşdirilməsi (ilk klikdən sonra)
    void setMines(MineTile firstClicked) {
        mineList = new ArrayList<>();
        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
            MineTile tile = board[r][c];

            // İlk kliklənən xanada mina olmasın
            if (tile == firstClicked || mineList.contains(tile)) continue;

            mineList.add(tile);
            mineLeft--;
        }
    }

    void revealMines() {
        for (MineTile tile : mineList) {
            tile.setText("💣");
        }
        gameOver = true;
        textLabel.setText("Game Over!");
    }

    void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return;

        MineTile tile = board[r][c];
        if (!tile.isEnabled()) return;

        tile.setEnabled(false);
        tilesClicked++;

        int minesFound = 0;
        // Yuxarı
        minesFound += countMine(r - 1, c - 1);
        minesFound += countMine(r - 1, c);
        minesFound += countMine(r - 1, c + 1);
        // Ortalar
        minesFound += countMine(r, c - 1);
        minesFound += countMine(r, c + 1);
        // Aşağı
        minesFound += countMine(r + 1, c - 1);
        minesFound += countMine(r + 1, c);
        minesFound += countMine(r + 1, c + 1);

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");
            // Ətraf xanaları da yoxla
            checkMine(r - 1, c - 1);
            checkMine(r - 1, c);
            checkMine(r - 1, c + 1);
            checkMine(r, c - 1);
            checkMine(r, c + 1);
            checkMine(r + 1, c - 1);
            checkMine(r + 1, c);
            checkMine(r + 1, c + 1);
        }

        if (tilesClicked == numCols * numRows - mineList.size()) {
            gameOver = true;
            textLabel.setText("Mines Cleared 🎉");
        }
    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) return 0;
        if (mineList.contains(board[r][c])) return 1;
        return 0;
    }

    public static void main(String[] args) {
        new Minesweeper();
    }
}
