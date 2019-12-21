package ru.sergeykozhukhov.tictactoe.TicTacToeGame;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/*
 * Класс, отвечающий за логическое формирование игрового поля и проверку окончания игры
 * */

public class FieldGame implements Parcelable {

    public static final Creator<FieldGame> CREATOR = new Creator<FieldGame>() {
        @Override
        public FieldGame createFromParcel(Parcel in) {
            return new FieldGame(in);
        }

        @Override
        public FieldGame[] newArray(int size) {
            return new FieldGame[size];
        }
    };
    /*
     * field - игровое поле
     * sequence - минимальная последовательность фигур, считающаяся победной
     * size - размер поля
     * winningCombination - набор точек победной комбинации
     * historyPoints - история задействованных ячеек поля
     * */
    private int[][] field;
    private int sequence;
    private int size;
    private List<Point> winningCombination = new ArrayList<>();
    private List<Point> historyPoints = new ArrayList<>();

    public FieldGame(int size) {
        this.size = size;
        field = new int[size][size];
    }

    /*
     * Проверка наличия победной комбинации фигуры с обозначенным id в точки (x, y)
     * */
    public boolean isGameOver(int x, int y, int value) {

        if (isFullnessHistoryPoints())
            return true;

        Rect rect = getConfinesWinningCombination(x, y);

        int Dt1 = Math.min(rect.left, rect.top);
        int Dt2 = Math.min(rect.right, rect.bottom);
        int Db1 = Math.min(rect.left, rect.bottom);
        int Db2 = Math.min(rect.right, rect.top);

        if (checkHorizontal(x, y, rect.left, rect.right, value) ||
                checkVertical(x, y, rect.top, rect.bottom, value) ||
                checkDiagonal1(x, y, Dt1, Dt2, value) ||
                checkDiagonal2(x, y, Db1, Db2, value)) {
            // Добавляем центральную точку
            winningCombination.add(new Point(x, y));
            return true;
        }
        return false;
    }

    /*
    * Проверка на заполненность поля
    * */
    public boolean isFullnessHistoryPoints(){
        return historyPoints.size() == size*size;
    }

    /*
     * Получение точки в определенный момент истории step
     * */
    public Point getPointHistory(int step) {
        return historyPoints.get(step);
    }

    /*
     * Получение точки из победной комбинации
     * */
    public Point getPointWinningCombination(int i) {
        return winningCombination.get(i);
    }

    /*
     * Получение количетва точек в истории
     * */
    public int getFullnessHistoryPoints() {
        return historyPoints.size();
    }

    /*
     * Получение количества точек в победной комбинации
     * */
    public int getSizeWinCombination() {
        return winningCombination.size();
    }

    /*
     * Очистка поля
     * */
    public void clearField() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                field[i][j] = 0;
            }
        }
    }

    /*
     * Очистка истории
     * */
    public void clearHistoryPoints() {
        historyPoints.clear();
    }

    /*
     * Очистка победной комбинации
     * */
    public void clearWinningCombination() {
        winningCombination.clear();
    }

    /*
     * Установка в точке item id фигуры при условии, что точка свободна
     * */
    public boolean setIdFigure(Point item, int id) {
        if (field[item.x][item.y] == 0) {
            field[item.x][item.y] = id;
            historyPoints.add(item);
            return true;
        }
        return false;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getSequence() {
        return sequence;
    }

    public int getFieldSize() {
        return size;
    }

    /*
     * Проверка горизонтали точки (x,y) для фигуры с обозначенным id от left до right
     * */
    private boolean checkHorizontal(int x, int y, int left, int right, int id) {
        int sumH = 1;
        for (int i = 1; i <= left; i++) {
            if (field[x - i][y] == id) {
                sumH++;
            } else break;
        }
        for (int i = 1; i <= right; i++) {
            if (field[x + i][y] == id) {
                sumH++;
            } else break;
        }
        // если в даннам случае присутствует победная комбинация, проходимся еще раз и фиксируем победные точки
        if (sumH >= sequence) {
            for (int i = 1; i <= left; i++) {
                if (field[x - i][y] == id) {
                    winningCombination.add(new Point(x - i, y));
                } else break;
            }
            for (int i = 1; i <= right; i++) {
                if (field[x + i][y] == id) {
                    winningCombination.add(new Point(x + i, y));
                } else break;
            }
            return true;
        }
        return false;
    }

    /*
     * Проверка вертикали точки (x,y) для фигуры с обозначенным id от top до bottom
     * */
    private boolean checkVertical(int x, int y, int top, int bottom, int id) {
        int sumV = 1;
        for (int i = 1; i <= top; i++) {
            if (field[x][y - i] == id) {
                sumV++;
            } else break;
        }
        for (int i = 1; i <= bottom; i++) {
            if (field[x][y + i] == id) {
                sumV++;
            } else break;
        }
        // если в даннам случае присутствует победная комбинация, проходимся еще раз и фиксируем победные точки
        if (sumV >= sequence) {
            for (int i = 1; i <= top; i++) {
                if (field[x][y - i] == id) {
                    winningCombination.add(new Point(x, y - i));
                } else break;
            }
            for (int i = 1; i <= bottom; i++) {
                if (field[x][y + i] == id) {
                    winningCombination.add(new Point(x, y + i));
                } else break;
            }
            return true;
        }
        return false;
    }

    /*
     * Проверка первой диагонали (c левого верха до правого низа) точки (x,y) для фигуры с обозначенным id от dT1 до dT2
     * */
    private boolean checkDiagonal1(int x, int y, int dT1, int dT2, int id) {
        int sumDt = 1;
        for (int i = 1; i <= dT1; i++) {
            if (field[x - i][y - i] == id) {
                sumDt++;
            } else break;
        }
        for (int i = 1; i <= dT2; i++) {
            if (field[x + i][y + i] == id) {
                sumDt++;
            } else break;
        }
        // если в даннам случае присутствует победная комбинация, проходимся еще раз и фиксируем победные точки
        if (sumDt >= sequence) {

            for (int i = 1; i <= dT1; i++) {
                if (field[x - i][y - i] == id) {
                    winningCombination.add(new Point(x - i, y - i));
                } else break;
            }
            for (int i = 1; i <= dT2; i++) {
                if (field[x + i][y + i] == id) {
                    winningCombination.add(new Point(x + i, y + i));
                } else break;
            }
            return true;
        }
        return false;
    }

    /*
     * Проверка первой диагонали (c левого низа до правого верха) точки (x,y) для фигуры с обозначенным id от dB1 до dB2
     * */
    private boolean checkDiagonal2(int x, int y, int dB1, int dB2, int id) {
        int sumDb = 1;
        for (int i = 1; i <= dB1; i++) {
            if (field[x - i][y + i] == id) {
                sumDb++;
            } else break;
        }
        for (int i = 1; i <= dB2; i++) {
            if (field[x + i][y - i] == id) {
                sumDb++;
            } else break;
        }
        // если в даннам случае присутствует победная комбинация, проходимся еще раз и фиксируем победные точки
        if (sumDb >= sequence) {
            for (int i = 1; i <= dB1; i++) {
                if (field[x - i][y + i] == id) {
                    winningCombination.add(new Point(x - i, y + i));
                } else break;
            }
            for (int i = 1; i <= dB2; i++) {
                if (field[x + i][y - i] == id) {
                    winningCombination.add(new Point(x + i, y - i));
                } else break;
            }
            return true;
        }
        return false;
    }

    /*
     * Получение границ минимальных победных комбинаций вокруг точки с координатами (x,y)
     * */
    private Rect getConfinesWinningCombination(int x, int y) {
        Rect rect = new Rect();
        rect.left = x >= sequence - 1 ? sequence - 1 : x;
        rect.top = y >= sequence - 1 ? sequence - 1 : y;
        rect.right = (x + sequence - 1) < size ? sequence - 1 : (size - 1) - x;
        rect.bottom = (y + sequence - 1) < size ? sequence - 1 : (size - 1) - y;
        return rect;
    }


    protected FieldGame(Parcel in) {
        sequence = in.readInt();
        size = in.readInt();

        field = new int[size][size];
        for (int i = 0; i < size; i++) {
            field[i] = in.createIntArray();
        }

        winningCombination = in.createTypedArrayList(Point.CREATOR);
        historyPoints = in.createTypedArrayList(Point.CREATOR);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sequence);
        dest.writeInt(size);
        for (int i = 0; i < size; i++) {
            dest.writeIntArray(field[i]);
        }
        dest.writeTypedList(winningCombination);
        dest.writeTypedList(historyPoints);
    }

}
