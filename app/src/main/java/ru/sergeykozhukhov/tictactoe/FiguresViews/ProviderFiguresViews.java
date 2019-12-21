package ru.sergeykozhukhov.tictactoe.FiguresViews;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.BounceInterpolator;

import ru.sergeykozhukhov.tictactoe.R;

/*
 * Класс, обеспечивающий подготовку возможных фигур для игры
 * */

public class ProviderFiguresViews {

    /*
    * ID_WINNER - id фигуры, отмечающий клетки победной комбинации
    * ID_CROSS - id фигуры "Крестик"
    * ID_CIRCLE - id фигуры "Нолик"
    * ID_SQUARE_GREEN - id фигуры "Зеленый квадрат"
    * ID_SQUARE_YELLOW - id фигуры "Желтый квадрат"
    * */
    public static final int ID_WINNER = -1;
    public static final int ID_CROSS = 1;
    public static final int ID_CIRCLE = 2;
    public static final int ID_SQUARE_GREEN = 3;
    public static final int ID_SQUARE_YELLOW = 4;

    /*
     * Набор возможных фигур для игры
     * */
    private SparseArray<DrawFigureView> figuresViews;

    /*
    * Функции, обеспечивающие отображение фигур
    * */
    private DrawFigureFunction drawCross;
    private DrawFigureFunction drawCircle;
    private DrawFigureFunction drawSquare;
    private DrawFigureFunction drawWinner;

    /*
    * displayEffectFigureView - эффект отображения фигуры
    * displayEffectFigureView - эффект отображения победной комбинации
    * */
    private ValueAnimator displayEffectFigureView;
    private ValueAnimator displayEffectFigureWinner;

    /*
     * view - место наложения эффекта отображения фигур (анимирования)
     * */
    private View view;

    /*
    * Внутренний отступ от границы клетки для отрисовки фигуры
    * */
    private float inner_indent = 15.0f;

    public ProviderFiguresViews(Context context) {
        initDrawFiguresFunction();
        initFigures(context);
    }

    /*
     * Инициализация эффектов отображения у фигур
     * */
    public void initDisplayEffect(View view){
        this.view = view;
        initDisplayEffectFigureView();
        initDisplayEffectFigureWinner();
        figuresViews.get(ID_WINNER).initDisplayEffect(displayEffectFigureWinner);
        figuresViews.get(ID_CROSS).initDisplayEffect(displayEffectFigureView);
        figuresViews.get(ID_CIRCLE).initDisplayEffect(displayEffectFigureView);
        figuresViews.get(ID_SQUARE_GREEN).initDisplayEffect(displayEffectFigureView);
        figuresViews.get(ID_SQUARE_YELLOW).initDisplayEffect(displayEffectFigureView);

    }

    /*
    * Инициализация базового набора фигур
    * */
    private void initFigures(Context context) {
        figuresViews = new SparseArray<>();
        figuresViews.put(ID_WINNER, new DrawFigureView(ID_WINNER, context.getString(R.string.figureView_winner_name), Color.MAGENTA, drawWinner));
        figuresViews.put(ID_CROSS, new DrawFigureView(ID_CROSS, context.getString(R.string.figureView_red_cross_name), Color.RED, drawCross));
        figuresViews.put(ID_CIRCLE, new DrawFigureView(ID_CIRCLE, context.getString(R.string.figureView_blue_circle_name), Color.BLUE, drawCircle));
        figuresViews.put(ID_SQUARE_GREEN, new DrawFigureView(ID_SQUARE_GREEN,context.getString(R.string.figureView_green_square_name), Color.GREEN, drawSquare));
        figuresViews.put(ID_SQUARE_YELLOW, new DrawFigureView(ID_SQUARE_YELLOW,context.getString(R.string.figureView_yellow_square_name), Color.YELLOW, drawSquare));
    }

    /*
     * Получение фигуры по ее id
     * */
    public DrawFigureView getFigureView(int id){
        return figuresViews.get(id);
    }

    /*
    * Эффект отображения фигуры (анимация)
    * */
    private void initDisplayEffectFigureView(){

        displayEffectFigureView = ValueAnimator.ofFloat(0f, inner_indent -3.0f);
        displayEffectFigureView.setDuration(200);
        displayEffectFigureView.setRepeatMode(ValueAnimator.REVERSE);
        displayEffectFigureView.setInterpolator(new BounceInterpolator());
        displayEffectFigureView.setRepeatCount(3);
        displayEffectFigureView.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.invalidate();
            }
        });
    }

    /*
    * Эффект отображения победной комбинации (анимация)
    * */
    private void initDisplayEffectFigureWinner(){

        displayEffectFigureWinner = ValueAnimator.ofFloat(0f, inner_indent -3.0f);
        displayEffectFigureWinner.setDuration(2000);
        displayEffectFigureWinner.setRepeatMode(ValueAnimator.REVERSE);
        displayEffectFigureWinner.setRepeatCount(ValueAnimator.INFINITE);
        displayEffectFigureWinner.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.invalidate();
            }
        });
    }


    /*
    * Инициализия функций отображения фигур
    * */
    private void initDrawFiguresFunction() {
        drawCross = new DrawFigureFunction() {
            @Override
            public void drawFigure(float left, float top, float right, float bottom, Canvas canvas, Paint paint) {
                canvas.drawLine(left + inner_indent, top + inner_indent, right - inner_indent, bottom - inner_indent, paint);
                canvas.drawLine(right - inner_indent, top + inner_indent, left + inner_indent, bottom - inner_indent, paint);
            }

        };
        drawCircle = new DrawFigureFunction() {
            @Override
            public void drawFigure(float left, float top, float right, float bottom, Canvas canvas, Paint paint) {
                canvas.drawOval(left + inner_indent, top + inner_indent, right - inner_indent, bottom - inner_indent, paint);
            }

        };
        drawSquare = new DrawFigureFunction() {
            @Override
            public void drawFigure(float left, float top, float right, float bottom, Canvas canvas, Paint paint) {
                canvas.drawRect(left + inner_indent, top + inner_indent, right - inner_indent, bottom - inner_indent, paint);
            }
        };
        drawWinner = new DrawFigureFunction() {
            @Override
            public void drawFigure(float left, float top, float right, float bottom, Canvas canvas, Paint paint) {
                canvas.drawRect(left, top, right, bottom, paint);
            }
        };
    }

    /*
    * Интерфейс, обеспечивающий отображение фигуры
    * */
    public interface DrawFigureFunction {
        /*
        * Отображение фигуры
        * left - левая граница отображения
        * top - верхняя граница отображения
        * right - правая граница отображения
        * bottom - нижнаяя граница отображения
        * */
        void drawFigure(float left, float top, float right, float bottom, Canvas canvas, Paint paint);
    }
}
