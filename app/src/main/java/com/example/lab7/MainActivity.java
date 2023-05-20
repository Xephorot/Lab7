package com.example.lab7;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, imageView7, imageView8, imageView9;
    private Button btnEmpezar;
    private char currentPlayer;
    private int moves;
    private boolean gameOver;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);
        imageView6 = findViewById(R.id.imageView6);
        imageView7 = findViewById(R.id.imageView7);
        imageView8 = findViewById(R.id.imageView8);
        imageView9 = findViewById(R.id.imageView9);
        btnEmpezar = findViewById(R.id.btnEmpezar);

        currentPlayer = ' ';
        moves = 0;
        gameOver = false;

        showChooseSymbolAlert();
    }

    private void showChooseSymbolAlert() {
        final CharSequence[] symbols = {"X", "O"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elegir símbolo");
        builder.setItems(symbols, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentPlayer = symbols[which].charAt(0);
                startGame();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void startGame() {
        btnEmpezar.setEnabled(false);
        resetBoard();

        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerUI(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (!gameOver) {
                    Toast.makeText(MainActivity.this, "¡Tiempo agotado! No hay ganador.", Toast.LENGTH_SHORT).show();
                    resetBoard();
                }
            }
        };
        timer.start();
    }

    private void resetBoard() {
        imageView1.setImageResource(R.drawable.empty);
        imageView2.setImageResource(R.drawable.empty);
        imageView3.setImageResource(R.drawable.empty);
        imageView4.setImageResource(R.drawable.empty);
        imageView5.setImageResource(R.drawable.empty);
        imageView6.setImageResource(R.drawable.empty);
        imageView7.setImageResource(R.drawable.empty);
        imageView8.setImageResource(R.drawable.empty);
        imageView9.setImageResource(R.drawable.empty);

        imageView1.setTag(null);
        imageView2.setTag(null);
        imageView3.setTag(null);
        imageView4.setTag(null);
        imageView5.setTag(null);
        imageView6.setTag(null);
        imageView7.setTag(null);
        imageView8.setTag(null);
        imageView9.setTag(null);

        currentPlayer = ' ';
        moves = 0;
        gameOver = false;
    }

    private void updateTimerUI(long milliseconds) {
        long seconds = milliseconds / 1000;
        String timerText = "Temporizador: " + seconds + " segundos";
        TextView temporizador = findViewById(R.id.temporizador);
        temporizador.setText(timerText);
    }

    public void onCellClick(View view) {
        if (!gameOver) {
            ImageView cell = (ImageView) view;

            if (cell.getTag() == null) {
                cell.setImageResource(getSymbolImage(currentPlayer));
                cell.setTag(currentPlayer);
                moves++;

                if (checkWin()) {
                    gameOver = true;
                    showWinnerAlert();
                } else if (moves == 9) {
                    gameOver = true;
                    showDrawAlert();
                } else {
                    currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                }
            }
        }
    }
    private int getSymbolImage(char symbol) {
        if (symbol == 'X') {
            return R.drawable.x;
        } else if (symbol == 'O') {
            return R.drawable.o;
        }
        return R.drawable.empty;
    }

    private void showWinnerAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¡Tenemos un ganador!");
        builder.setMessage("El jugador " + currentPlayer + " ha ganado el juego.");
        builder.setPositiveButton("Reiniciar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetBoard();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void showDrawAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¡Empate!");
        builder.setMessage("El juego ha terminado en empate.");
        builder.setPositiveButton("Reiniciar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetBoard();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private boolean checkWin() {
        int[][] positions = {
                {0, 1, 2},
                {3, 4, 5},
                {6, 7, 8},
                {0, 3, 6},
                {1, 4, 7},
                {2, 5, 8},
                {0, 4, 8},
                {2, 4, 6}
        };

        for (int[] pos : positions) {
            if (checkLine(pos[0], pos[1], pos[2]))
                return true;
        }
        return false;
    }

    private boolean checkLine(int a, int b, int c) {
        return (getSymbolFromCell(a) == currentPlayer && getSymbolFromCell(b) == currentPlayer && getSymbolFromCell(c) == currentPlayer);
    }

    private char getSymbolFromCell(int cell) {
        ImageView imageView;
        switch (cell) {
            case 0:
                imageView = imageView1;
                break;
            case 1:
                imageView = imageView2;
                break;
            case 2:
                imageView = imageView3;
                break;
            case 3:
                imageView = imageView4;
                break;
            case 4:
                imageView = imageView5;
                break;
            case 5:
                imageView = imageView6;
                break;
            case 6:
                imageView = imageView7;
                break;
            case 7:
                imageView = imageView8;
                break;
            case 8:
                imageView = imageView9;
                break;
            default:
                imageView = null;
        }

        if (imageView != null) {
            if (imageView.getTag() != null) {
                return (char) imageView.getTag();
            }
        }
        return ' ';
    }
}