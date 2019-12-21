package ru.sergeykozhukhov.tictactoe.PlayersGame;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import ru.sergeykozhukhov.tictactoe.FiguresViews.DrawFigureView;


/*
* Класс, обеспечивающий подготовку данных участников игры
* */

public class ProviderPlayersGame implements Parcelable {

    /*
    * playerGames - участники игры
    * */
    private List<PlayerGame> playerGames;

    public ProviderPlayersGame(){
        playerGames = new ArrayList<>();
    }

    /*
    * Добавление участника
    * */
    public void addPlayer(int id, String name, int numberOfWins, DrawFigureView figureView)
    {
        playerGames.add(new PlayerGame(id, name, numberOfWins, figureView));
    }

    /*
    * обращение к участнику по его очередности хода в игре
    * */
    public PlayerGame getPlayer(int place){
        return playerGames.get(place);
    }

    /*
    * Получение списка всех участников
    * */
    public List<PlayerGame> getPlayerGames(){
        return new ArrayList<>(playerGames);
    }

    public int getNumberOfPlayers(){
        return playerGames.size();
    }


    protected ProviderPlayersGame(Parcel in) {
        playerGames = in.createTypedArrayList(PlayerGame.CREATOR);
    }

    public static final Creator<ProviderPlayersGame> CREATOR = new Creator<ProviderPlayersGame>() {
        @Override
        public ProviderPlayersGame createFromParcel(Parcel in) {
            return new ProviderPlayersGame(in);
        }

        @Override
        public ProviderPlayersGame[] newArray(int size) {
            return new ProviderPlayersGame[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(playerGames);
    }
}
