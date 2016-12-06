package com.grumoon.pulllistview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class PullListView extends ListView {

    public static final int GET_MORE_TYPE_AUTO = 0;

    public static final int GET_MORE_TYPE_CLICK = 1;

    public interface OnRefreshListener {
         void onRefresh();
    }

    public interface OnGetMoreListener {
         void onGetMore();
    }

    private static final String TAG = PullListView.class.getSimpleName();

    private final static int NONE = 0;

    private final static int PULL_TO_REFRESH = 1;

    private final static int RELEASE_TO_REFRESH = 2;

    private final static int REFRESHING = 3;

    private final static float RATIO = 1.7f;


    private ViewGroup headView;

    private TextView tvHeadTitle;

    private ImageView ivHeadArrow;

    private ProgressBar pbHeadRefreshing;

    private View footView;

    private TextView tvFootTitle;
    private ProgressBar pbFootRefreshing;

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;

    private int headViewHeight;


    private int state = NONE;

    private boolean isStartRecorded = false;

    private float startY;
    private boolean addPullHeaderByUser = false;
    private int getMoreType = GET_MORE_TYPE_AUTO;

    private boolean isFromReleaseToRefresh;


    private boolean addPullHeaderFlag = false;

    private boolean addGetMoreFooterFlag = false;

    private boolean hasMoreDataFlag = true;

    private int reachLastPositionCount = 0;

    private OnRefreshListener refreshListener;
    private OnGetMoreListener getMoreListener;

    private boolean canRefresh;
    private boolean isGetMoreing = false;

    public PullListView(Context context) {
        this(context, null, 0);
    }

    public PullListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context, AttributeSet attrs) {
        //获取属性
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.PullListView, 0, 0);
        if (arr != null) {
            addPullHeaderByUser = arr.getBoolean(R.styleable.PullListView_addPullHeaderByUser, addPullHeaderByUser);
            getMoreType = arr.getInt(R.styleable.PullListView_getMoreType, GET_MORE_TYPE_AUTO);
            arr.recycle();
        }
    //    initAnimation();
        LayoutInflater inflater = LayoutInflater.from(context);

        /**
         * 头部
         */
        headView = (LinearLayout) inflater.inflate(R.layout.pull_list_view_head, null);
       // ivHeadArrow = (ImageView) headView.findViewById(R.id.iv_head_arrow);
        pbHeadRefreshing = (ProgressBar) headView.findViewById(R.id.pb_head_refreshing);
       // tvHeadTitle = (TextView) headView.findViewById(R.id.tv_head_title);
        measureView(headView);
        headViewHeight = headView.getMeasuredHeight();
        headView.setPadding(0, -headViewHeight, 0, 0);
        headView.invalidate();

        if (!addPullHeaderByUser) {
            addHeaderView(headView, null, false);
            addPullHeaderFlag = true;
        }

        /**
         * 底部
         */
        footView = inflater.inflate(R.layout.pull_list_view_foot, this, false);
       // tvFootTitle = (TextView) footView.findViewById(R.id.tv_foot_title);
        pbFootRefreshing = (ProgressBar) footView.findViewById(R.id.pb_foot_refreshing);
        footView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCanClickGetMore()) {
                    getMore();
                }
            }
        });


        // 滑动监听
        setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                doOnScrollStateChanged(view, scrollState);

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                doOnScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);

            }
        });

        state = NONE;
        canRefresh = false;
    }

    /**
     * 初始化动画
     */
    /*private void initAnimation() {
        // 旋转
        animation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(300);
        animation.setFillAfter(true);

        //反向旋转
        reverseAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(300);
        reverseAnimation.setFillAfter(true);
    }*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // 不可下拉刷新
        if (!canRefresh) {
            return super.dispatchTouchEvent(event);
        }

        int action = event.getAction();
        float tempY = event.getRawY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (state == PULL_TO_REFRESH) {
                    state = NONE;
                    changeHeaderViewByState();
                } else if (state == RELEASE_TO_REFRESH) {
                    state = REFRESHING;
                    changeHeaderViewByState();
                    refresh();
                }
                isStartRecorded = false;
                isFromReleaseToRefresh = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (!checkCanPullDown() || state == REFRESHING) {
                    break;
                }
                if (!isStartRecorded) {
                    startY = tempY;
                    Log.v(TAG, "在开始滑动时记录初始位置");
                    isStartRecorded = true;
                }
                float deltaY = tempY - startY;
                float realDeltaY = deltaY / RATIO;

                // 初始状态下
                if (state == NONE) {
                    if (realDeltaY > 0) {
                        state = PULL_TO_REFRESH;
                        changeHeaderViewByState();
                        Log.v(TAG, "由初始状态转变为下拉刷新状态");
                    }
                }
                // 还没有到达显示松开刷新的时候PULL_TO_REFRESH状态
                else if (state == PULL_TO_REFRESH) {

                    headView.setPadding(0, -headViewHeight + (int) realDeltaY, 0, 0);

                    // 下拉到可以进入RELEASE_TO_REFRESH的状态
                    if (realDeltaY >= headViewHeight) {
                        state = RELEASE_TO_REFRESH;
                        isFromReleaseToRefresh = true;
                        changeHeaderViewByState();
                        Log.v(TAG, "下拉刷新转变到松开刷新状态");
                    }
                    // 上推到顶了
                    else if (realDeltaY <= 0) {
                        state = NONE;
                        changeHeaderViewByState();
                        Log.v(TAG, "下拉刷新转变到初始状态");
                    }

                }
                // 可以松手去刷新了
                else if (state == RELEASE_TO_REFRESH) {

                    headView.setPadding(0, -headViewHeight + (int) realDeltaY, 0, 0);

                    // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                    if (realDeltaY < headViewHeight && realDeltaY > 0) {
                        state = PULL_TO_REFRESH;
                        changeHeaderViewByState();
                        Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
                    }
                    // 一下子推到顶了
                    else if (realDeltaY <= 0) {
                        state = NONE;
                        changeHeaderViewByState();
                        Log.v(TAG, "由松开刷新状态转变到初始状态");
                    }
                }

                break;
        }

        return super.dispatchTouchEvent(event);
    }


    // 当状态改变时候，调用该方法，以更新界面
    private void changeHeaderViewByState() {
        switch (state) {
            case NONE:
                headView.setPadding(0, -1 * headViewHeight, 0, 0);
                pbHeadRefreshing.setVisibility(View.GONE);
             //   ivHeadArrow.clearAnimation();
             //   ivHeadArrow.setImageResource(R.drawable.pull_list_view_progressbar_bg);
    //            tvHeadTitle.setText("下拉刷新");
                break;

            case PULL_TO_REFRESH:
                pbHeadRefreshing.setVisibility(View.GONE);
    //            tvHeadTitle.setVisibility(View.VISIBLE);
           //     ivHeadArrow.clearAnimation();
            //    ivHeadArrow.setVisibility(View.VISIBLE);
      //          tvHeadTitle.setText("Pull To Refresh....");
                // 是由RELEASE_To_REFRESH状态转变来的
                if (isFromReleaseToRefresh) {
                    isFromReleaseToRefresh = false;
                   // ivHeadArrow.clearAnimation();
                   // ivHeadArrow.startAnimation(reverseAnimation);
                }
                break;

            case RELEASE_TO_REFRESH:
             //   ivHeadArrow.setVisibility(View.VISIBLE);
                pbHeadRefreshing.setVisibility(View.GONE);
      //          tvHeadTitle.setVisibility(View.VISIBLE);

           //     ivHeadArrow.clearAnimation();
           //     ivHeadArrow.startAnimation(animation);

     //           tvHeadTitle.setText("Release To Refresh...");

                break;

            case REFRESHING:

                headView.setPadding(0, 0, 0, 0);

                pbHeadRefreshing.setVisibility(View.VISIBLE);
             //   ivHeadArrow.clearAnimation();
            //    ivHeadArrow.setVisibility(View.GONE);
    //            tvHeadTitle.setText("Loading...");

                break;

        }
    }

    // 刷新
    private void refresh() {
        //刷新回调
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }

        //加载更多充值
        if (footView != null) {
            footView.setVisibility(View.VISIBLE);
            pbFootRefreshing.setVisibility(View.GONE);
          //  tvFootTitle.setText("Load More");
            isGetMoreing = false;
            hasMoreDataFlag = true;
        }
    }

    // 加载更多
    private void getMore() {
        //加载更多回调
        if (getMoreListener != null) {
            isGetMoreing = true;
            pbFootRefreshing.setVisibility(View.VISIBLE);
          //  tvFootTitle.setText("Loading...");
            getMoreListener.onGetMore();

        }
    }

    // 测量视图
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * 判断是否可以下拉<br/>
     * 也就是判断ListView是否滑动到了顶部
     *
     * @return
     */
    private boolean checkCanPullDown() {
        if (getFirstVisiblePosition() > 0) {
            return false;
        }
        return !canScroll(-1);
    }

    /**
     * 判断是否可以自动加载更多<br/>
     *
     * @return
     */
    private boolean checkCanAutoGetMore() {
        if (getMoreType != GET_MORE_TYPE_AUTO) {
            return false;
        }
        if (footView == null) {
            return false;
        }
        if (getMoreListener == null) {
            return false;
        }
        if (isGetMoreing) {
            return false;
        }
        if (!hasMoreDataFlag) {
            return false;
        }
        if (getAdapter() == null) {
            return false;
        }
        if (!canScroll(1) && !canScroll(-1)) {
            return false;
        }
        if (getLastVisiblePosition() != getAdapter().getCount() - 1) {
            return false;
        }
        if (reachLastPositionCount != 1) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否可以点击加载更多
     *
     * @return
     */
    private boolean checkCanClickGetMore() {
        if (getMoreType != GET_MORE_TYPE_CLICK) {
            return false;
        }
        if (footView == null) {
            return false;
        }
        if (getMoreListener == null) {
            return false;
        }
        if (getAdapter() == null) {
            return false;
        }
        if (isGetMoreing) {
            return false;
        }
        if (!hasMoreDataFlag) {
            return false;
        }
        return true;
    }

    /**
     * 判断ListView是否可以滑动
     *
     * @param direction
     * @return
     */
    private boolean canScroll(int direction) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return false;
        }

        final int firstPosition = getFirstVisiblePosition();
        final int listPaddingTop = getPaddingTop();
        final int listPaddingBottom = getPaddingTop();
        final int itemCount = getAdapter().getCount();

        if (direction > 0) {
            final int lastBottom = getChildAt(childCount - 1).getBottom();
            final int lastPosition = firstPosition + childCount;
            return lastPosition < itemCount || lastBottom > getHeight() - listPaddingBottom;
        } else {
            final int firstTop = getChildAt(0).getTop();
            return firstPosition > 0 || firstTop < listPaddingTop;
        }
    }

    public void setCanRefresh(boolean canRefresh) {
        this.canRefresh = canRefresh;
    }

    /**
     * 代码触发下拉刷新操作<br/>
     * 多用于首次进入页面的加载
     */
    public void performRefresh() {
        state = REFRESHING;
        changeHeaderViewByState();
        refresh();
    }

    /**
     * 设置下拉刷新监听器
     *
     * @param refreshListener
     */
    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        canRefresh = true;
    }

    /**
     * 设置加载更多监听器
     *
     * @param getMoreListener
     */
    public void setOnGetMoreListener(OnGetMoreListener getMoreListener) {
        this.getMoreListener = getMoreListener;
        if (!addGetMoreFooterFlag) {
            addGetMoreFooterFlag = true;
            this.addFooterView(footView);
        }
    }

    /**
     * 下拉刷新完成
     */
    public void refreshComplete() {
        state = NONE;
        changeHeaderViewByState();
    }

    /**
     * 加载更多完成
     */
    public void getMoreComplete() {
        pbFootRefreshing.setVisibility(View.GONE);
      //  tvFootTitle.setText("Load More");
        isGetMoreing = false;
    }

    /**
     * 设置没有更多的数据了<br/>
     * 不再显示加载更多按钮
     */
    public void setNoMore() {
        hasMoreDataFlag = false;
        if (footView != null) {
            footView.setVisibility(View.GONE);
        }
    }

    /**
     * 显示加载更多按钮
     */
    public void setHasMore() {
        hasMoreDataFlag = true;
        if (footView != null) {
            footView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 重新添加下拉刷新头部
     */
    public void reAddPullHeaderView() {
        if (headView != null) {
            removeHeaderView(headView);
            addHeaderView(headView, null, false);
            addPullHeaderFlag = true;
        }
    }

    /**
     * 添加下拉刷新头部
     */
    public void addPullHeaderView() {
        if (headView != null && !addPullHeaderFlag) {
            addHeaderView(headView, null, false);
            addPullHeaderFlag = true;
        }
    }


    /**
     * 如果项目中其他地方需要重新设置PullListView的OnScrollListener<br/>
     * 请在新的listener中onScrollStateChanged方法内调用此方法，保证PullListView正常运行。
     *
     * @param view
     * @param scrollState
     */
    public void doOnScrollStateChanged(AbsListView view, int scrollState) {
    }

    /**
     * 如果项目中其他地方需要重新设置PullListView的OnScrollListener<br/>
     * 请在新的listener中onScroll方法内调用此方法，保证PullListView正常运行。
     *
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    public void doOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (getAdapter() == null) {
            return;
        }

        if (getLastVisiblePosition() == getAdapter().getCount() - 1) {
            reachLastPositionCount++;
        } else {
            reachLastPositionCount = 0;
        }


        if (checkCanAutoGetMore()) {
            getMore();
        }


    }
}

