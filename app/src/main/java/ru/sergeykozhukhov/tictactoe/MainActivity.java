package ru.sergeykozhukhov.tictactoe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import ru.sergeykozhukhov.tictactoe.FiguresViews.ProviderFiguresViews;
import ru.sergeykozhukhov.tictactoe.PlayersGame.PlayerGame;
import ru.sergeykozhukhov.tictactoe.PlayersGame.ProviderPlayersGame;
import ru.sergeykozhukhov.tictactoe.TicTacToeGame.DrawTicTacToeGame;
import ru.sergeykozhukhov.tictactoe.TicTacToeGame.FieldGame;

public class MainActivity extends AppCompatActivity {

    /*
     * drawTicTacToeGame - CustomView для отрисовки игры
     * fieldGame - игровое поле
     * players - участники игры
     * figuresView - набор возможных фигур для игроков
    *
    * */
    private DrawTicTacToeGame drawTicTacToeGame;
    private FieldGame fieldGame;
    private ProviderPlayersGame players;
    private ProviderFiguresViews figuresViews;

    private TextView playersResults_tv;
    private AlertDialog.Builder isNewGame_ad;

    private int fieldSize; // размер поля
    private int gameWinnerSequence; // минимальная последовательность фигур для победы

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawTicTacToeGame = findViewById(R.id.draw_field_view);
        playersResults_tv = findViewById(R.id.tv_results);

        fieldSize = 10;
        gameWinnerSequence = 4;

        initFiguresViews();
        initPlayers(savedInstanceState);
        initFieldGame(savedInstanceState);
        initDrawTicTacToeGame(savedInstanceState);
        initDialogForNewGame();

        playersResults_tv.setText(getPlayersResults(players));
    }

    /*
    * Инициализация CustomView для отрисовки игры
    * */
    private void initDrawTicTacToeGame(Bundle savedInstanceState){
        drawTicTacToeGame.initPlayers(players);
        drawTicTacToeGame.initFigureWinner(figuresViews.getFigureView(ProviderFiguresViews.ID_WINNER));
        if (savedInstanceState == null) {
            drawTicTacToeGame.initField(fieldGame);
        }
        drawTicTacToeGame.setOnGameOverListener(new DrawTicTacToeGame.OnGameOverListener() {
            @Override
            public void onGameOver(PlayerGame playerGame) {
                setMessageForNewGame(playerGame);
                playersResults_tv.setText(getPlayersResults(players));

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        isNewGame_ad.show();
                    }
                }, 3500);

            }
        });

    }

    /*
    * Инициализация игрового поля
    * */
    private void initFieldGame(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            fieldGame = new FieldGame(fieldSize);
            fieldGame.setSequence(gameWinnerSequence);
        }
    }

    /*
    * Инициализация участников игры
    * */
    private void initPlayers(Bundle savedInstanceState){
        players = new ProviderPlayersGame();
        if (savedInstanceState == null){
            players.addPlayer(1, getString(R.string.player_1_name),0, figuresViews.getFigureView(ProviderFiguresViews.ID_CROSS));
            players.addPlayer(2, getString(R.string.player_2_name), 0, figuresViews.getFigureView(ProviderFiguresViews.ID_CIRCLE));
            players.addPlayer(3, getString(R.string.player_3_name), 0 , figuresViews.getFigureView(ProviderFiguresViews.ID_SQUARE_GREEN));
        }
        else {
            players = savedInstanceState.getParcelable("players");
            players.getPlayer(0).setFigureView(figuresViews.getFigureView(ProviderFiguresViews.ID_CROSS));
            players.getPlayer(1).setFigureView(figuresViews.getFigureView(ProviderFiguresViews.ID_CIRCLE));
            players.getPlayer(2).setFigureView(figuresViews.getFigureView(ProviderFiguresViews.ID_SQUARE_GREEN));
        }
    }

    /*
    * Инициализация набора возможных фигур для игроков
    * */
    private void initFiguresViews(){
        figuresViews = new ProviderFiguresViews(this);
        figuresViews.initDisplayEffect(drawTicTacToeGame);
    }

    /*
    * Инициализация диалога для начала новой игры
    * */
    private void initDialogForNewGame(){
        String title = getString(R.string.isNewGame_ad_title);
        String button1String = getString(R.string.isNewGame_ad_positive);
        String button2String = getString(R.string.isNewGame_ad_negative);

        isNewGame_ad = new AlertDialog.Builder(MainActivity.this);
        isNewGame_ad.setTitle(title);  // заголовок

        isNewGame_ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                drawTicTacToeGame.restartGame();
            }
        });
        isNewGame_ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(MainActivity.this, getString(R.string.toast_gameOver_text), Toast.LENGTH_LONG)
                        .show();
            }
        });
        isNewGame_ad.setCancelable(true);
        isNewGame_ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(MainActivity.this, getString(R.string.toast_gameOver_text),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    /*
    * Установка текста сообщения диалога
    * */
    private void setMessageForNewGame(PlayerGame playerGame){
        StringBuilder message = new StringBuilder();
        if (playerGame == null)
            message.append(getString(R.string.isNewGame_ad_message_1));
        else {
            message.append(getString(R.string.isNewGame_ad_message_2));
            message.append(playerGame.getName());
        }
        message.append(getString(R.string.isNewGame_ad_message_global));
        isNewGame_ad.setMessage(message); // сообщение
    }

    /*
    * Получение списка результатов участников
    * */
    private String getPlayersResults(ProviderPlayersGame players){
        StringBuilder stringBuilder = new StringBuilder();
        for (PlayerGame pl: players.getPlayerGames()) {
            stringBuilder.append(getString(R.string.playersResults_text_1));
            stringBuilder.append(pl.getName());
            stringBuilder.append(getString(R.string.playersResults_text_2));
            stringBuilder.append(pl.getNumberOfWins());
            stringBuilder.append(getString(R.string.playersResults_text_3));
            stringBuilder.append(pl.getFigureView().getName());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("players", players);
    }


    @Override
    protected void onDestroy() {
        if (handler != null)
            handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
