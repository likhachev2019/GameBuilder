package fyodor.likhachyov.gamebuilder;

import android.graphics.Paint;

public class Cell {

    private static int dimension = 100;

    private static Paint paint = new Paint() {
        {
            setStyle(Style.STROKE);
        }
    };

    public static Paint getPaint() {
        return paint;
    }

    //    Left-top corner
    private float x, y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public static int getDimension() {
        return dimension;
    }
}
