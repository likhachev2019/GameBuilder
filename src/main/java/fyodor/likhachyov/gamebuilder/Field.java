package fyodor.likhachyov.gamebuilder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class Field extends View {

    private int width, height;

//    Масштаб
    private float scale = 1;

    private Cell[][] cells;

//    Координаты клетки, с которой начинается отрисовка
    int strRow = 0, strCol = 0;

    float deltX = 0, deltY = 0;

    public Field(Context context, AttributeSet attrs) {
        super(context, attrs);
        width = height = 1000;
        int cellDimension = Cell.getDimension();
        cells = new Cell[width/cellDimension][height/cellDimension];
        for (int r = 0; r < width/cellDimension; r++) {
            for (int c = 0; c < height/cellDimension; c++) {
                cells[r][c] = new Cell(c*cellDimension, r*cellDimension);
            }
        }

        CountDownTimer countDownTimer = new CountDownTimer(Long.MAX_VALUE, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                invalidate();
            }

            @Override
            public void onFinish() {

            }
        }.start();
        System.out.println(cells.length);
    }

    float strDeltX, strDeltY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            strDeltX = event.getX();
            strDeltY = event.getY();
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE){
            drag(event.getX(), event.getY());
        }
        else if (event.getAction() == MotionEvent.ACTION_UP){

        }
        return true;
    }

    private void drag(float x, float y) {
        deltX = (strDeltX - x) % Cell.getDimension();
        deltY = (strDeltY - y) % Cell.getDimension();
        if ((strDeltX - x) / Cell.getDimension() > 0){
            strCol+= (strDeltX - x) / Cell.getDimension();
            strCol = strCol >= 0 ? strCol : 0;
            strDeltX = x - x % Cell.getDimension(); // Когда клетку полностью перетащили, начинаем считать дельту относительно её конца
        }
        if ((strDeltY - y) / Cell.getDimension() > 0){
            strRow+= (strDeltX - x) / Cell.getDimension(); // Сдвигаем верхнюю строку, с которой начинается отрисовка
            strRow = strRow >= 0 ? strRow : 0;
            strDeltX = x - x % Cell.getDimension(); // Когда клетку полностью перетащили, начинаем считать дельту относительно её конца
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int cellDimension = Cell.getDimension();
        Paint paint = Cell.getPaint();
        int endRow = strRow + canvas.getHeight()/(int)(cellDimension/scale + 1), endCol = strCol + canvas.getWidth()/(int)(cellDimension/scale + 1);
        for (int r = strRow; r < endRow; r++) {
            for (int c = strCol; c < endCol; c++) {
                canvas.drawRect(cells[r][c].getX()/scale + deltX, cells[r][c].getY()/scale + deltY, (c+1)*(cellDimension/scale) + deltX, (r+1)*(cellDimension/scale) + deltY, paint);
            }
        }
    }
}
