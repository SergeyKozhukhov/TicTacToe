package ru.sergeykozhukhov.tictactoe.PlayersGame;

import android.os.Parcel;
import android.os.Parcelable;

import ru.sergeykozhukhov.tictactoe.FiguresViews.DrawFigureView;


/*
* Класс учатника игры
* */
public class PlayerGame implements Parcelable {

    /*
    * id - id участника
    * name - имя
    * numberOfWins - количество побед
    * figureView - фигура учатника на поле    *
    * */
    private int id;
    private String name;
    private int numberOfWins;
    private DrawFigureView figureView;

    public PlayerGame(int id, String name, int numberOfWins, DrawFigureView figureView) {
        this.id = id;
        this.name = name;
        this.numberOfWins = numberOfWins;
        this.figureView = figureView;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfWins() {
        return numberOfWins;
    }

    public void addWin() {
        this.numberOfWins += 1;
    }

    public DrawFigureView getFigureView() {
        return figureView;
    }

    public void setFigureView(DrawFigureView figureView) {
        this.figureView = figureView;
    }

    protected PlayerGame(Parcel in) {
        id = in.readInt();
        name = in.readString();
        numberOfWins = in.readInt();
    }

    public static final Creator<PlayerGame> CREATOR = new Creator<PlayerGame>() {
        @Override
        public PlayerGame createFromParcel(Parcel in) {
            return new PlayerGame(in);
        }

        @Override
        public PlayerGame[] newArray(int size) {
            return new PlayerGame[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(numberOfWins);
    }
}
