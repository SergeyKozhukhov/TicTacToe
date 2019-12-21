package ru.sergeykozhukhov.tictactoe.TicTacToeGame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import ru.sergeykozhukhov.tictactoe.FiguresViews.DrawFigureView;
import ru.sergeykozhukhov.tictactoe.PlayersGame.PlayerGame;
import ru.sergeykozhukhov.tictactoe.PlayersGame.ProviderPlayersGame;

import static android.view.MotionEvent.ACTION_DOWN;
import static java.lang.Thread.sleep;

/*
* CustomView для отображения игры "Крестики-нолики"
* */
public class DrawTicTacToeGame extends View {

    /*
    * stageGame - текущий этап игры
    * onGameOverListener - слушатель для определения стадии окончания игры
    * */
    private StageGame stageGame = StageGame.WAITING;
    private OnGameOverListener onGameOverListener;

    /*
    * fieldGame - игровое поле
    * cellSize - размер одной ячейки
    * displayEffectField - аниматор появления поля
    * */
    private FieldGame fieldGame;
    private float cellSize;
    private ValueAnimator displayEffectField;

    /*
    * playersGame - участники игры
    * playerCurrent - текущий игрок
    * */
    private ProviderPlayersGame playersGame;
    private int playerCurrent = 0;

    /*
    * figurePaint - общие параметры отображения фигур игроков
    * fieldPaint - параметры отображения поля
    * */
    private Paint figurePaint;
    private Paint fieldPaint;

    /*
    * figureWinner - фигуры для выделения ячеек победной комбинации
    * */
    private DrawFigureView figureWinner;

    /*
    * position - верхний левый угол поля
    * modeScroll - режим скроллинга поля
    * */

    private PointF position = new PointF();
    private boolean modeScroll = false;


    /*
    * Слушатель для скроллинга (скроллинг не реализован)
    * */
    private GestureDetector.OnGestureListener mGestureListener;
    private GestureDetector mDetector;

    public DrawTicTacToeGame(Context context) {
        super(context);
    }

    public DrawTicTacToeGame(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setSaveEnabled(true);
        initPaints();
        initGestures();
    }

    /*
     * Инициализация поля
     * */
    public void initField(FieldGame fieldGame) {
        this.fieldGame = fieldGame;
        stageGame = StageGame.DRAW_FIELD;
    }

    /*
     * Инициализация игроков
     * */
    public void initPlayers(ProviderPlayersGame players) {
        playersGame = players;
    }

    /*
    * Инициализация фигуры, выделяющей ячейки победной комбинации
    * */
    public void initFigureWinner(DrawFigureView drawFigureView) {
        figureWinner = drawFigureView;
    }

    /*
    * Установка слушателя на окончание игры
    * */
    public void setOnGameOverListener(OnGameOverListener listener) {
        onGameOverListener = listener;
    }

    /*
     * Начало новой игры
     * */
    public void restartGame(){
        fieldGame.clearWinningCombination();
        fieldGame.clearField();
        fieldGame.clearHistoryPoints();
        stageGame = StageGame.DRAW_FIELD;
        displayEffectField.start();
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Point sizeDisp = new Point();
        getDisplay().getSize(sizeDisp);

        int width = sizeDisp.x;
        int height = sizeDisp.y;

        float desiredWidth = Math.max(width, getSuggestedMinimumWidth()) + getPaddingLeft() + getPaddingRight();
        float desiredHeight = Math.max(height, getSuggestedMinimumHeight()) + getPaddingTop() + getPaddingBottom();
        int desiredSize = (int) (Math.min(desiredHeight, desiredWidth));
        final int resolvedWidth = resolveSize(desiredSize, widthMeasureSpec);
        final int resolvedHeight = resolveSize(desiredSize, heightMeasureSpec);
        setMeasuredDimension(resolvedWidth, resolvedHeight);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int sizeField = fieldGame.getFieldSize();
        int minSizeScreen = Math.min(w, h);
        cellSize = (float) minSizeScreen / (float) sizeField;
        initDisplayEffectField();
        if (stageGame == StageGame.DRAW_FIELD)
            displayEffectField.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (stageGame == StageGame.GAME) {
            drawField(canvas);
            if (fieldGame.getFullnessHistoryPoints() > 0) {
                drawFiguresHistory(canvas);
            }
        } else if (stageGame == StageGame.DRAW_FIELD) {
            drawAnimatedField(canvas);
        } else if (stageGame == StageGame.GAME_OVER) {
            drawField(canvas);
            drawFiguresHistory(canvas);
            drawWinningCombination(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Point pointField = getPointFieldBy(event.getX(), event.getY());
        if (stageGame == StageGame.GAME) {
            if (pointField.x <= fieldGame.getFieldSize() || pointField.y <= fieldGame.getFieldSize()) {
                switch (event.getAction()) {
                    case ACTION_DOWN:
                        int idFigure = playersGame.getPlayer(playerCurrent).getFigureView().getId();
                        if (fieldGame.setIdFigure(pointField, idFigure)) {
                            playersGame.getPlayer(playerCurrent).getFigureView().startAnimation();
                            if (!fieldGame.isGameOver(pointField.x, pointField.y, idFigure)) {
                                invalidate();
                                toNextFigure();
                                return true;
                            }
                            stageGame = StageGame.GAME_OVER;
                            if (!fieldGame.isFullnessHistoryPoints()) {
                                playersGame.getPlayer(playerCurrent).addWin();
                                figureWinner.startAnimation();
                                onGameOverListener.onGameOver(playersGame.getPlayer(playerCurrent));
                            } else
                                onGameOverListener.onGameOver(null);
                            break;
                        }
                }
            }
            if (modeScroll)
                mDetector.onTouchEvent(event);
        }
        return true;
    }

    /*
    * Обработка прокрутки поля
    * */
    private void initGestures() {
        mGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                position.x -= distanceX;
                position.y -= distanceY;
                invalidate();
                return true;
            }
        };
        mDetector = new GestureDetector(getContext(), mGestureListener);

    }

    /*
    * Инициализация анимирования поля
    * */
    private void initDisplayEffectField() {

        displayEffectField = ValueAnimator.ofFloat(0f, cellSize * (fieldGame.getFieldSize()));
        displayEffectField.setDuration(4000);
        displayEffectField.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });

        displayEffectField.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                stageGame = StageGame.GAME;
            }
        });
    }

    /*
    * Инициализация начальных параметров отрисовки поля и фигур игроков
    * */
    private void initPaints() {
        figurePaint = new Paint();
        figurePaint.setAntiAlias(true);
        figurePaint.setStrokeWidth(10);
        figurePaint.setStyle(Paint.Style.STROKE);

        fieldPaint = new Paint();
        fieldPaint.setAntiAlias(true);
        fieldPaint.setColor(Color.BLACK);
        fieldPaint.setStrokeWidth(10);
        fieldPaint.setStyle(Paint.Style.STROKE);
    }

    /*
    * Отрисовка поля
    * */
    private void drawField(Canvas canvas) {
        for (int i = 0; i <= fieldGame.getFieldSize(); i++) {
            canvas.drawLine(0, i * cellSize, cellSize * fieldGame.getFieldSize(), i * cellSize, fieldPaint);
            canvas.drawLine(i * cellSize, 0, i * cellSize, cellSize * fieldGame.getFieldSize(), fieldPaint);
        }
    }

    /*
    * Отрисовка анимированного поля
    * */
    private void drawAnimatedField(Canvas canvas) {
        for (int i = 0; i <= fieldGame.getFieldSize(); i++) {
            canvas.drawLine(0, i * cellSize, (float) displayEffectField.getAnimatedValue(), i * cellSize, fieldPaint);
            canvas.drawLine(i * cellSize, 0, i * cellSize, (float) displayEffectField.getAnimatedValue(), fieldPaint);
        }

    }

    /*
     * Отрисовка фигур игроков
     * */
    private void drawFiguresHistory(Canvas canvas) {
        int currentFigureView = 0;
        for (int i = 0; i < fieldGame.getFullnessHistoryPoints(); i++) {
            if (currentFigureView == playersGame.getNumberOfPlayers())
                currentFigureView = 0;
            if (i == fieldGame.getFullnessHistoryPoints() - 1) {
                drawAnimateFigureViewBy(playersGame.getPlayer(currentFigureView).getFigureView(),
                        fieldGame.getPointHistory(i), canvas);
            } else
                drawFigureViewBy(playersGame.getPlayer(currentFigureView).getFigureView(),
                        fieldGame.getPointHistory(i), canvas);
            currentFigureView++;
        }
    }

    /*
    * Получение координат матрицы поля по точке на экране (x, y)
    * */
    private Point getPointFieldBy(float x, float y) {
        Point point = new Point();
        point.x = (int) (x / cellSize);
        point.y = (int) (y / cellSize);
        return point;
    }

    /*
    * Отрисовка обозначения победной комбинации
    * */
    private void drawWinningCombination(Canvas canvas) {
        for (int i = 0; i < fieldGame.getSizeWinCombination(); i++) {
            drawAnimateFigureViewBy(figureWinner, fieldGame.getPointWinningCombination(i), canvas);
        }
    }

    /*
    * Отрисовка фигуры в точке матрницы поля
    * */
    private void drawFigureViewBy(DrawFigureView drawFigureView, Point point, Canvas canvas) {
        drawFigureView.drawFigure(
                point.x * cellSize + fieldPaint.getStrokeWidth() / 2.0f,
                point.y * cellSize + fieldPaint.getStrokeWidth() / 2.0f,
                point.x * cellSize + cellSize - fieldPaint.getStrokeWidth() / 2.0f,
                point.y * cellSize + cellSize - fieldPaint.getStrokeWidth() / 2.0f,
                canvas, figurePaint);
    }

    /*
     * Отрисовка анимированной фигуры в точке матрницы поля
     * */
    private void drawAnimateFigureViewBy(DrawFigureView drawFigureView, Point point, Canvas canvas) {
        drawFigureView.drawAnimateFigure(
                point.x * cellSize + fieldPaint.getStrokeWidth() / 2.0f,
                point.y * cellSize + fieldPaint.getStrokeWidth() / 2.0f,
                point.x * cellSize + cellSize - fieldPaint.getStrokeWidth() / 2.0f,
                point.y * cellSize + cellSize - fieldPaint.getStrokeWidth() / 2.0f,
                canvas, figurePaint);
    }

    /*
     * Переход к следующей фигуре
     * */
    private void toNextFigure() {
        if (playerCurrent < playersGame.getNumberOfPlayers() - 1)
            playerCurrent++;
        else
            playerCurrent = 0;
    }

    public interface OnGameOverListener {
        void onGameOver(@Nullable PlayerGame playerGame);
    }

    /*
    * Стадии игры
    * */
    private enum StageGame{
        WAITING,
        DRAW_FIELD,
        GAME,
        GAME_OVER
    }


    @Override
    protected Parcelable onSaveInstanceState() {

        final Parcelable superState = super.onSaveInstanceState();
        final SavedState customViewSavedState = new SavedState(superState);
        customViewSavedState.mField = this.fieldGame;
        customViewSavedState.playerCurrent = this.playerCurrent;
        return customViewSavedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        final SavedState customViewSavedState = (SavedState) state;
        super.onRestoreInstanceState(customViewSavedState.getSuperState());
        initPaints();
        initGestures();
        initField(customViewSavedState.mField);
        this.playerCurrent = (customViewSavedState.playerCurrent);
        if (fieldGame.getSizeWinCombination() == 0)
            this.stageGame = StageGame.GAME;
        else {
            this.stageGame = StageGame.GAME_OVER;
            if (!fieldGame.isFullnessHistoryPoints()) {
                figureWinner.startAnimation();
                onGameOverListener.onGameOver(playersGame.getPlayer(playerCurrent));
            } else
                onGameOverListener.onGameOver(null);
        }
    }



    private static class SavedState extends BaseSavedState {
        private FieldGame mField;
        private int playerCurrent;

        public SavedState(Parcel source) {
            super(source);
            mField = source.readParcelable(getClass().getClassLoader());
            playerCurrent = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(mField, 0);
            out.writeInt(playerCurrent);
        }
    }


}



