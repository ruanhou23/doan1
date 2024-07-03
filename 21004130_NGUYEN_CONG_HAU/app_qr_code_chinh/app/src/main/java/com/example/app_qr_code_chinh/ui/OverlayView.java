/*
 * Máy quét mã. Một ứng dụng Android để quét và tạo mã (mã vạch, mã QR, v.v.)
 * Bản quyền (C) 2022 czlucius
 *
 * Chương trình này là phần mềm miễn phí: bạn có thể phân phối lại và/hoặc sửa đổi nó
 * nó tuân theo các điều khoản của Giấy phép Công cộng GNU Affero như đã được xuất bản
 * bởi Tổ chức Phần mềm Tự do, phiên bản 3 của Giấy phép hoặc
 * (theo lựa chọn của bạn) bất kỳ phiên bản nào sau này.
 *
 * Chương trình này được phân phối với hy vọng nó sẽ hữu ích,
 * nhưng KHÔNG CÓ BẤT KỲ ĐẢM BẢO NÀO; thậm chí không có sự bảo đảm ngụ ý của
 * Khả năng bán được hoặc SỰ PHÙ HỢP CHO MỘT MỤC ĐÍCH CỤ THỂ. Xem
 * Giấy phép Công cộng GNU Affero để biết thêm chi tiết.
 *
 * Bạn hẳn đã nhận được một bản sao Giấy phép Công cộng GNU Affero
 * cùng với chương trình này. Nếu không, hãy xem <https://www.gnu.org/licenses/>.
 */
package com.example.app_qr_code_chinh.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class OverlayView extends View {
    private final Paint paint = new Paint();
    private int alpha = 85;
    private final RectArea rectArea = new RectArea();

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.BLACK);
        paint.setAlpha(alpha);
    }


    public void setAlpha(int alpha) {
        this.alpha = alpha;
        paint.setAlpha(alpha);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Vẽ 4 hình chữ nhật
        float width = getWidth();
        float height = getHeight();


        if (getWidth() < getHeight()) {
            // Chế độ dọc
            rectArea.width = (getRight() - getLeft()) / 3;
            rectArea.height = (getBottom() - getTop()) / 5;
        } else {
            // Chế độ ngang
            rectArea.width = (getRight() - getLeft()) / 5;
            rectArea.height = (getBottom() - getTop()) / 3;

        }
        Log.i("OverlayView", "onDraw: " + rectArea.width + "?" + rectArea.height);


        // Vẽ xung quanh hình chữ nhật.
        canvas.drawRect(0, 0, width, height / 2 - rectArea.height, paint);
        canvas.drawRect(0, rectArea.height + (height / 2), width, height, paint);
        canvas.drawRect(0, height / 2 - rectArea.height, width / 2 - rectArea.width, height / 2 + rectArea.height, paint);
        canvas.drawRect(width / 2 + rectArea.width, height / 2 - rectArea.height, width, height / 2 + rectArea.height, paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public static class RectArea {
        private int width;
        private int height;

        public RectArea(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public RectArea() {}
    }
}