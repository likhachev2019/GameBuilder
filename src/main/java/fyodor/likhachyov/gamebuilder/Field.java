package fyodor.likhachyov.gamebuilder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;


public class Field extends View {

    private int width, height;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    //    Масштаб поля
    private float scale = 1;

    private Cell[][] cells;

    int cellDimension;
    
    float relCellDimension; // Размер клетки с учётом масштаба (относительно поля)

    //    Координаты клетки, с которой начинается отрисовка
    int strRow = 0, strCol = 0;

    float deltX = 0, deltY = 0;

    public Field(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        width = height = 1000;
        cellDimension = Cell.getDimension();
        relCellDimension = cellDimension/scale;
        cells = new Cell[width / cellDimension][height / cellDimension];
        for (int r = 0; r < width / cellDimension; r++) {
            for (int c = 0; c < height / cellDimension; c++) {
                cells[r][c] = new Cell(c * cellDimension, r * cellDimension);
            }
        }
        new CountDownTimer(Long.MAX_VALUE, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                invalidate();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    float strDeltX, strDeltY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            strDeltX = event.getX();
            strDeltY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            drag(event.getX(), event.getY());
            strDeltX = event.getX();
            strDeltY = event.getY(); // эти координаты станут началом отсчёта увеличения смещения при следующем вызове drug-а
        }
        return true;
    }


    private void drag(float x, float y) {
        deltX -= (strDeltX - x) % relCellDimension;
        deltY -= (strDeltY - y) % relCellDimension;
        if ((int) Math.abs(deltX / relCellDimension) > 0) {
            strCol -= (int)(deltX / relCellDimension);// Когда палец ведут вправо, то strCol должен ++, но при этом дельта < 0
            if (strCol >= 0)
                deltX = (strDeltX - x) % relCellDimension; // Сбрасываем смещение
            else //Если пользователь листает за матрицу клеток влево, то позволим ему делать пустые отступы, не сбрасывая дельту
                strCol = 0;
        }
        if ((int) Math.abs(deltY / relCellDimension) > 0) {
            strRow -= (int) (deltY / relCellDimension); // Сдвигаем верхнюю строку, с которой начинается отрисовка
            if (strRow >= 0)
                deltY = (strDeltY - y) % relCellDimension;
            else
                strRow = 0;
        }
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save(); canvas.scale(mScaleFactor, mScaleFactor);
        Paint paint = Cell.getPaint();
        // Координаты относительно холста. Обычно он меньше абсолютного размера поля
        float relX, relY; // Координаты отрисовки квадрата клеток, рассчитываются каждыый раз в зависимости от их размера и размера холста
        int endRow = strRow + canvas.getHeight() / (int) (relCellDimension + 1), endCol = strCol + canvas.getWidth() / (int) (relCellDimension + 1);
        endRow = endRow < cells.length ? endRow : cells.length - 1;// str и end переменные н. для расчёта кол-ва, сколько на холсте помещается
        endCol = endCol < cells[0].length ? endCol : cells[0].length - 1;// и с какой клетки по какую брать из матрицы. Координаты же для их отрисовки не меняются при постоянном размере клетке
        for (int r = strRow > 0 ? strRow - 1 : 0; r <= endRow; r++) { // Коорд. рассчит. с пом. увелич. на ширину клетки
            // Если возможно, то будем рисовать слевого и верхнего края по одной неполной клетки (чтоб не было видно пустых полей, когдай клетки еще есть)
            relY = cells[r][0].getY()/scale - strRow * relCellDimension;
            for (int c = strCol > 0 ? strCol - 1 : strCol; c <= endCol; c++) {
                relX = cells[r][c].getX()/scale - strCol * relCellDimension;
                canvas.drawRect(relX + deltX, relY + deltY, relX + relCellDimension + deltX, relY + relCellDimension + deltY, paint);
            }
        }canvas.restore();
    }
}
