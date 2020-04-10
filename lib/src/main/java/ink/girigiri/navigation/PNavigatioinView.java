package ink.girigiri.navigation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuItemImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Note:底部导航栏
 * @Author:Pulis
 * @DATE:2020/4/9
 */
public class PNavigatioinView extends ViewGroup {

    /**
     * 上下文
     */
    private Context context;

    /**
     * 菜单文件
     */
    private Menu menu;
    /**
     * 图标大小
     */
    private int iconSize;
    /**
     * 文本颜色
     */
    private int labelColor;
    /**
     * 文本大小
     */
    private int labelSize;
    /**
     * item个数
     */
    private int itemSize;
    /**
     * 存放子View
     */
    private ArrayList<PNavigationItemView> itemList;
    /**
     * 存放 menu item
     */
    private Map<Integer, Map<String, Drawable>> itemMap;
    /**
     * 默认背景
     */
    private Drawable defaultBackground;
    /**
     * 默认前景
     */
    private int defaultColor;
    /**
     * 是否显示文本
     */
    private boolean isShowLabel;
    /**
     * minibar颜色
     */
    private int miniBarColor;
    /**
     * 是否显示MiniBar
     */
    private boolean isShowMiniBar;
    /**
     * 默认导航栏高度
     */
    private int defaultHeight;
    /**
     * 子view高度、宽度
     */
    private int childHeight;
    private int childWidth;
    /**
     * 导航栏实际宽高
     */
    private int width;
    private int height;
    /**
     * item点击监听
     */
    private OnPNavigationItemCheckedListener listener;
    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 当前选中item
     */
    private int checkedItem;

    public PNavigatioinView(Context context) {
        this(context, null);
    }

    public PNavigatioinView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PNavigatioinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defaultColor = context.getColor(R.color.colorAccent);
        defaultBackground = new ColorDrawable(context.getColor(R.color.colorPrimary));

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PNavigatioinView);


        //获取Menu
        int menuId = typedArray.getResourceId(R.styleable.PNavigatioinView_menu, 0);
        MenuInflater menuInflater = new SupportMenuInflater(context);
        menu = new MenuBuilder(context);
        menuInflater.inflate(menuId, menu);

        labelColor = typedArray.getColor(R.styleable.PNavigatioinView_labelColor, defaultColor);
        labelSize = typedArray.getDimensionPixelSize(R.styleable.PNavigatioinView_labelSize, DP2PXUtils.dip2px(context, 15));
        iconSize = typedArray.getDimensionPixelSize(R.styleable.PNavigatioinView_iconSize, DP2PXUtils.dip2px(context, 36));
        isShowLabel = typedArray.getBoolean(R.styleable.PNavigatioinView_showLabel, false);
        isShowMiniBar = typedArray.getBoolean(R.styleable.PNavigatioinView_showMiniBar, false);
        miniBarColor = typedArray.getColor(R.styleable.PNavigatioinView_miniBarColor, defaultColor);
        miniBarHeight = typedArray.getDimensionPixelSize(R.styleable.PNavigatioinView_miniBarHeight, DP2PXUtils.dip2px(context, 8));

        //释放 TypeArrary
        typedArray.recycle();
        this.context = context;


        init();
    }

    private float downY;

    public void setScorllingAction(ScrollView scorllingView) {
        scorllingView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float moveY = event.getY();
                        int distanceY = (int) (moveY - downY);
                        if (distanceY > 100) {
                            show();
                        } else if (distanceY < -100) {
                            hide();
                        }
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return false;
            }
        });

    }

    public void setScorllingAction(ListView listView) {
        listView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float moveY = event.getY();
                        int distanceY = (int) (moveY - downY);
                        if (distanceY > 100) {
                            show();
                        } else if (distanceY < -100) {
                            hide();
                        }
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return false;
            }
        });

    }

    private void hide() {
        this.animate().translationY(height - miniBarHeight);
    }

    private void show() {
        this.animate().translationY(0);
    }

    /**
     * 初始化
     */
    private void init() {
        itemSize = menu.size();
        if (itemSize == 0) {
            return;
        }
        //默认选中第一个
        checkedItem = 0;
        //初始化画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(miniBarColor);
        //minbar高度
        miniBarHeight = isShowMiniBar ? miniBarHeight : 0;


        // 初始化默认高度
        defaultHeight = DP2PXUtils.dip2px(context, 70);

        itemList = new ArrayList<>();
        itemMap = new ArrayMap<>();
        //根据 menu 初始化子View
        for (int i = 0; i < itemSize; i++) {

            MenuItemImpl menuItem = (MenuItemImpl) menu.getItem(i);
            Drawable icon = menuItem.getIcon();
            String title = menuItem.getTitle().toString();
            int id = menuItem.getItemId();
            Map<String, Drawable> map = new HashMap<>();
            map.put(title, icon);
            itemMap.put(id, map);

            final PNavigationItemView item = new PNavigationItemView(context);
            item.setLabel(title);
            item.setIcon(icon);
            item.setLabelColor(labelColor);
            item.setIsDrawLabel(isShowLabel);
            item.setIconSize(iconSize);
            item.setLabelSize(labelSize);
            addView(item);
            itemList.add(item);
            //设置点击事件
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = itemList.indexOf(v);
                    onChecked(index);
                }
            });
        }


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //拿到宽高
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        //拿到模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //定义子View 宽高
        childHeight = 0;
        childWidth = MeasureSpec.makeMeasureSpec(width / itemSize, widthMode);
        //如果是具体数值则采用具体数值高度 如果是 自适应 默认 70 dp
        if (heightMode == MeasureSpec.EXACTLY) {

            childHeight = height;
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = defaultHeight;
            childHeight = MeasureSpec.makeMeasureSpec(height, heightMode);
            setMeasuredDimension(widthMeasureSpec, childHeight);
        }


        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.measure(childWidth, childHeight);
        }
        //minbar位置
        miniBarLeft = 0;
        miniBarRight = width / itemSize;
        resetItem();
    }

    private void resetItem() {
        for (int i = 0; i < itemList.size(); i++) {
            PNavigationItemView itemView = itemList.get(i);
            itemView.setLabelSize(labelSize);
            itemView.setIconSize(iconSize);
            itemView.setIsDrawLabel(isShowLabel);
            itemView.setLabelColor(labelColor);
        }
    }


    /**
     * minibar 位置
     */
    private int miniBarLeft;
    private int miniBarRight;
    private int miniBarHeight;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //是否绘制minibar
        if (isShowMiniBar && itemSize > 0) {
            //绘制minibar背景
            paint.setColor(Color.argb(50, 0, 0, 0));
            Rect r = new Rect(0, 0, width, miniBarHeight);
            canvas.drawRect(r, paint);
            //绘制minibar
            paint.setColor(miniBarColor);
            r.set(miniBarLeft, 0, miniBarRight, miniBarHeight);
            canvas.drawRect(r, paint);
        }


    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.layout(i * view.getMeasuredWidth(), 0, (i + 1) * view.getMeasuredWidth(), view.getMeasuredHeight());

        }
    }

    /**
     * 选中item时
     *
     * @param position 选中的item
     */
    public void onChecked(int position) {
        checkedItem = position;
        //minibar 当前 left 坐标
        int currl = miniBarLeft;
        //需要移动到的 left 坐标
        int movel = position * (width / itemSize);
        // 属性动画
        ValueAnimator valueAnimator = ValueAnimator.ofInt(currl, movel);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                miniBarLeft = (int) animation.getAnimatedValue();
                //移动后的 Right 坐标=移动后的 Left 左边+minibar的宽度
                miniBarRight = miniBarLeft + (width / itemSize);
                //重绘
                invalidate();
            }
        });
        //移动所需时间
        valueAnimator.setDuration(400);
        valueAnimator.start();
        //调用监听器
        if (listener != null) {
            listener.onChecked(position, itemList.get(position));
        }
    }

    public int getIconSize() {
        return iconSize;

    }

    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
    }

    public int getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
    }

    public int getLabelSize() {
        return labelSize;
    }

    public void setLabelSize(int labelSize) {
        this.labelSize = labelSize;
    }

    public boolean isShowLabel() {
        return isShowLabel;
    }

    public void setShowLabel(boolean showLabel) {
        isShowLabel = showLabel;
    }

    public int getMiniBarColor() {
        return miniBarColor;
    }

    public void setMiniBarColor(int miniBarColor) {
        this.miniBarColor = miniBarColor;
    }

    public boolean isShowMiniBar() {
        return isShowMiniBar;
    }

    public void setShowMiniBar(boolean showMiniBar) {
        isShowMiniBar = showMiniBar;
    }


    public void setOnItemClickListener(OnPNavigationItemCheckedListener listener) {
        this.listener = listener;
    }

    public int getMiniBarHeight() {
        return miniBarHeight;
    }

    public void setMiniBarHeight(int miniBarHeight) {
        this.miniBarHeight = miniBarHeight;
    }


    public PNavigationItemView getItem(int position) {
        return itemList.get(position);
    }


    /**
     * item View
     */
    public class PNavigationItemView extends View {
        //显示文本
        private String label;
        //显示图标
        private Drawable icon;
        //高宽
        private int height;
        private int width;

        //画笔
        private Paint paint;
        //前景色
        private int color;
        //是否绘制文本
        private boolean isDrawLabel;


        private int iconSize;
        private int labelSize;

        public PNavigationItemView(Context context) {
            super(context);

        }

        /**
         * 初始化
         */
        private void init() {
            paint = new Paint();
            paint.setAntiAlias(true);
            //默认使用水波纹背景
            this.setBackground(context.getDrawable(R.drawable.selectable_item_background));
            //开启点击
            this.setClickable(true);

        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
            init();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            //获取 icon bitmap
            Bitmap bitmap = drawableToBitmap(icon);
            //如果不存在icon 不进行绘制图片
            if (bitmap == null) {
                iconSize = 0;
            }
            //一半的宽高
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            //绘制的位置
            int t, l, r, b;
            if (isDrawLabel) {
                //文本跟图片的间距
                int p = DP2PXUtils.dip2px(getContext(), 4);

                l = halfWidth - (iconSize / 2);
                t = halfHeight - (labelSize / 2) - (iconSize / 2) - (p / 2) + miniBarHeight;
                r = halfWidth + (iconSize / 2);
                b = halfHeight - (labelSize / 2) + (iconSize / 2) - (p / 2) + miniBarHeight;
                //图片不为空则绘制图片
                if (bitmap != null) {
                    Rect dst = new Rect(l, t, r, b);
                    Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    canvas.drawBitmap(bitmap, src, dst, paint);
                }
                //文字居中绘制
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(color);
                paint.setTextSize(labelSize);

                canvas.drawText(label, halfWidth, halfHeight + (labelSize / 2) + (iconSize / 2) + (p / 2), paint);
            } else {
                if (bitmap == null) {
                    return;
                }
                l = halfWidth - (iconSize / 2);
                t = halfHeight - (iconSize / 2) + (miniBarHeight / 2);
                r = halfWidth + (iconSize / 2);
                b = halfHeight + (iconSize / 2) + (miniBarHeight / 2);
                Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                Rect dst = new Rect(l, t, r, b);

                canvas.drawBitmap(bitmap, src, dst, paint);
            }


        }


        /**
         * drawable to bitmap
         *
         * @param drawable 需要转化的drawable
         * @return bitmap
         */
        private Bitmap drawableToBitmap(Drawable drawable) {
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bd = (BitmapDrawable) drawable;
                return bd.getBitmap();
            }
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            drawable.draw(canvas);
            return bitmap;
        }

        public void setIsDrawLabel(boolean isShowLabel) {
            isDrawLabel = isShowLabel;
        }

        public void setLabel(String label) {
            this.label = label;

        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
            if (icon == null) {
                return;
            }

        }

        public void setLabelColor(int color) {
            this.color = color;

        }

        public int getIconSize() {
            return iconSize;
        }

        public void setIconSize(int iconSize) {
            this.iconSize = iconSize;
        }

        public int getLabelSize() {
            return labelSize;
        }

        public void setLabelSize(int labelSize) {
            this.labelSize = labelSize;
        }

        public String getLabel() {
            return label;
        }

        public Drawable getIcon() {
            return icon;
        }

        public int getColor() {
            return color;
        }
    }

    public interface OnPNavigationItemCheckedListener {
        void onChecked(int position, PNavigationItemView itemView);
    }
}
