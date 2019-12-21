package ru.sergeykozhukhov.tictactoe.FiguresViews;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

/*
* Класс, обеспечивающий отображение фигуры каждого участника
* */
public class DrawFigureView implements Parcelable {

    /*
    * id - id фигуры
    * name - наименование фигуры
    * color - цвет отображения фигуры
    * displayEffect - эффект отображения (анимация)
    * */
    private int id;
    private String name;
    private int color;
    private ValueAnimator displayEffect;
    private ProviderFiguresViews.DrawFigureFunction drawFigureFunction;

    public DrawFigureView(int id, String name, int color, ProviderFiguresViews.DrawFigureFunction drawFigureFunction) {
        this.id = id;
        this.color = color;
        this.name = name;
        this.drawFigureFunction = drawFigureFunction;
    }


    /*
    * Инициализация эффекта отображения фигуры
    * */
    public void initDisplayEffect(ValueAnimator displayEffect) {
        this.displayEffect = displayEffect;
    }

    /*
    * Запуск анимирования
    * */
    public void startAnimation() {
        if (displayEffect.isStarted())
            displayEffect.cancel();
        displayEffect.start();
    }

    /*
     * Отображение фигуры
     * left - левая граница отображения
     * top - верхняя граница отображения
     * right - правая граница отображения
     * bottom - нижнаяя граница отображения
     * */
    public void drawFigure(float left, float top, float right, float bottom, Canvas canvas, Paint paint) {
        paint.setColor(this.color);
        drawFigureFunction.drawFigure(left, top, right, bottom, canvas, paint);
    }

    /*
     * Отображение фигуры с применением эффекта
     * left - левая граница отображения
     * top - верхняя граница отображения
     * right - правая граница отображения
     * bottom - нижнаяя граница отображения
     * */
    public void drawAnimateFigure(float left, float top, float right, float bottom, Canvas canvas, Paint paint) {
        paint.setColor(this.color);
        drawFigureFunction.drawFigure(
                left-(float) displayEffect.getAnimatedValue(),
                top-(float) displayEffect.getAnimatedValue(),
                right+(float) displayEffect.getAnimatedValue(),
                bottom+(float) displayEffect.getAnimatedValue(),
                canvas, paint);
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected DrawFigureView(Parcel in) {
        id = in.readInt();
        name = in.readString();
        color = in.readInt();
    }

    public static final Creator<DrawFigureView> CREATOR = new Creator<DrawFigureView>() {
        @Override
        public DrawFigureView createFromParcel(Parcel in) {
            return new DrawFigureView(in);
        }

        @Override
        public DrawFigureView[] newArray(int size) {
            return new DrawFigureView[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(color);
    }

}
