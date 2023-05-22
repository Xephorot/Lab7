package com.example.lab7;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView[][] imageViews = new ImageView[3][3];
    private int[][] board = new int[3][3];
    private int currentPlayer;
    private boolean gameEnded;

    private Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TableLayout tableLayout = findViewById(R.id.tableLayout);
        btnReset = findViewById(R.id.btnEmpezar);

        // Initialize the imageViews array and set click listeners
        for (int i = 0; i < 3; i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            for (int j = 0; j < 3; j++) {
                final int rowIdx = i;
                final int colIdx = j;
                ImageView imageView = (ImageView) row.getChildAt(j);
                imageViews[i][j] = imageView;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!gameEnded && board[rowIdx][colIdx] == 0) {
                            makeMove(rowIdx, colIdx);
                            checkGameStatus();
                        }
                    }
                });
            }
        }

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });

        resetGame();
    }

    private void makeMove(int row, int col) {
        if (currentPlayer == 1) {
            imageViews[row][col].setImageResource(R.drawable.x);
            board[row][col] = 1;
            currentPlayer = 2;
        } else {
            imageViews[row][col].setImageResource(R.drawable.o);
            board[row][col] = 2;
            currentPlayer = 1;
        }
    }

    private void checkGameStatus() {
        if (checkWin(1)) {
            showToast("Player X wins!");
            gameEnded = true;
        } else if (checkWin(2)) {
            showToast("Player O wins!");
            gameEnded = true;
        } else if (isBoardFull()) {
            showToast("It's a draw!");
            gameEnded = true;
        }
    }

    private boolean checkWin(int player) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player)
                return true;
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player)
                return true;
        }

        // Check diagonals
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player)
            return true;

        if (board[0][2] == player && board[1][1] == player && board[2][0] == player)
            return true;

        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0)
                    return false;
            }
        }
        return true;
    }

    private void resetGame() {
        currentPlayer = new Random().nextInt(2) + 1; // Randomly choose player 1 or 2
        gameEnded = false;
        clearBoard();
    }

    private void clearBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                imageViews[i][j].setImageResource(R.drawable.empty);
                board[i][j] = 0;
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}