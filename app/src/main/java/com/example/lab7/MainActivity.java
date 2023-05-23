package com.example.lab7;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.lab7.SocketManager.SocketManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SocketManager.SocketListener {

    private ImageView[][] imageViews = new ImageView[3][3];
    private int[][] board = new int[3][3];
    private int currentPlayer;
    private boolean gameEnded;

    private Button btnReset;
    private SocketManager socketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TableLayout tableLayout = findViewById(R.id.tableLayout);
        btnReset = findViewById(R.id.btnEmpezar);

        socketManager = new SocketManager(this);

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
                            sendMoveToOpponent(rowIdx, colIdx);
                        }
                    }
                });
            }
        }

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
                sendResetToOpponent();
            }
        });

        showStartGameDialog();
    }

    private void showStartGameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Start New Game or Join Existing Game?");
        builder.setPositiveButton("Start New Game", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                socketManager.startServer();
            }
        });
        builder.setNegativeButton("Join Existing Game", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showEnterIpDialog();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEnterIpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Server IP Address");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String serverIp = input.getText().toString();
                socketManager.connectToServer(serverIp);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void makeMove(int row, int col) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    private void checkGameStatus() {
        if (checkWin(1)) {
            String message = "Player X wins!";
            showToast(message);
            showWinMessage(message);
            gameEnded = true;
        } else if (checkWin(2)) {
            String message = "Player O wins!";
            showToast(message);
            showWinMessage(message);
            gameEnded = true;
        } else if (isBoardFull()) {
            String message = "It's a draw!";
            showToast(message);
            showWinMessage(message);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        imageViews[i][j].setImageResource(R.drawable.empty);
                        board[i][j] = 0;
                    }
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showWinMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                resetGame();
                sendResetToOpponent();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendMoveToOpponent(int row, int col) {
        // Construct the move message in the format "MOVE:row:col"
        String moveMessage = "MOVE:" + row + ":" + col;
        socketManager.sendMessage(moveMessage);
    }

    private void sendResetToOpponent() {
        socketManager.sendMessage("RESET");
    }

    // SocketManager.SocketListener methods

    @Override
    public void onMessageReceived(String message) {
        if (message.startsWith("MOVE:")) {
            // Parse the move message and make the corresponding move
            String[] parts = message.split(":");
            if (parts.length == 3) {
                int row = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);
                makeMove(row, col);
                checkGameStatus();
            }
        } else if (message.equals("RESET")) {
            resetGame();
        }
    }

    @Override
    public void onClientConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast("Opponent connected");
            }
        });
    }

    @Override
    public void onClientDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast("Opponent disconnected");
            }
        });
    }
}
