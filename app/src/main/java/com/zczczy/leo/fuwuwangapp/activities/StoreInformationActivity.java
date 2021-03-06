package com.zczczy.leo.fuwuwangapp.activities;

import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.marshalchen.ultimaterecyclerview.CustomUltimateRecyclerview;
import com.marshalchen.ultimaterecyclerview.divideritemdecoration.FlexibleDividerDecoration;
import com.marshalchen.ultimaterecyclerview.divideritemdecoration.HorizontalDividerItemDecoration;
import com.zczczy.leo.fuwuwangapp.MyApplication;
import com.zczczy.leo.fuwuwangapp.R;
import com.zczczy.leo.fuwuwangapp.adapters.BaseRecyclerViewAdapter;
import com.zczczy.leo.fuwuwangapp.adapters.BaseUltimateRecyclerViewAdapter;
import com.zczczy.leo.fuwuwangapp.adapters.GoodsAdapters;
import com.zczczy.leo.fuwuwangapp.items.StoreInformationHeaderItemView;
import com.zczczy.leo.fuwuwangapp.items.StoreInformationHeaderItemView_;
import com.zczczy.leo.fuwuwangapp.model.BaseModelJson;
import com.zczczy.leo.fuwuwangapp.model.Goods;
import com.zczczy.leo.fuwuwangapp.model.StoreDetailModel;
import com.zczczy.leo.fuwuwangapp.rest.MyDotNetRestClient;
import com.zczczy.leo.fuwuwangapp.rest.MyErrorHandler;
import com.zczczy.leo.fuwuwangapp.tools.AndroidTool;
import com.zczczy.leo.fuwuwangapp.tools.Constants;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;

/**
 * Created by Leo on 2016/5/5.
 */
@EActivity(R.layout.activity_store_information)
public class StoreInformationActivity extends BaseActivity {

    @ViewById
    CustomUltimateRecyclerview ultimateRecyclerView;

    @Bean(GoodsAdapters.class)
    BaseUltimateRecyclerViewAdapter myAdapter;

    @RestService
    MyDotNetRestClient myRestClient;

    @Bean
    MyErrorHandler myErrorHandler;

    @Extra
    String storeId;

    StoreInformationHeaderItemView storeInformationHeaderItemView;

    LinearLayoutManager linearLayoutManager;

    int pageIndex = 1;

    @AfterInject
    void afterInject() {
        myRestClient.setRestErrorHandler(myErrorHandler);
    }


    @AfterViews
    void afterView() {
        AndroidTool.showLoadDialog(this);
        ultimateRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        ultimateRecyclerView.setAdapter(myAdapter);
        ultimateRecyclerView.setLayoutManager(linearLayoutManager);
        storeInformationHeaderItemView = StoreInformationHeaderItemView_.build(this);
        ultimateRecyclerView.setNormalHeader(storeInformationHeaderItemView);
        Paint paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(line_color);
        paint.setAntiAlias(true);
        ultimateRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).margin(35).visibilityProvider(new FlexibleDividerDecoration.VisibilityProvider() {
            @Override
            public boolean shouldHideDivider(int position, RecyclerView parent) {
                return position == 0;
            }
        }).paint(paint).build());
        getStoreInfo();

        myAdapter.setOnItemClickListener(new BaseUltimateRecyclerViewAdapter.OnItemClickListener<Goods>() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, Goods obj, int position) {
                GoodsDetailActivity_.intent(StoreInformationActivity.this).goodsId(obj.GoodsInfoId).start();
            }

            @Override
            public void onHeaderClick(RecyclerView.ViewHolder viewHolder, int position) {
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        storeInformationHeaderItemView.stopAutoCycle();
    }

    @Override
    public void finish() {
        storeInformationHeaderItemView.removeAllSliders();
        storeInformationHeaderItemView.stopAutoCycle();
        super.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        storeInformationHeaderItemView.startAutoCycle();
    }

    @Background
    void getStoreInfo() {
        afterGetStoreInfo(myRestClient.getStoreDetailById(storeId));
    }

    @UiThread
    void afterGetStoreInfo(BaseModelJson<StoreDetailModel> bmj) {
        if (bmj == null) {
            AndroidTool.showToast(this, no_net);
        } else if (!bmj.Successful) {
            AndroidTool.showToast(this, bmj.Error);
        } else {
            if (Constants.STORE_STATE_ACTIVITY.equals(bmj.Data.StoreStatus)) {
                myAdapter.getMoreData(pageIndex, Constants.PAGE_COUNT, false, 0, bmj.Data.GoodsList);
                storeInformationHeaderItemView.init(bmj.Data);
            } else {
                AndroidTool.showToast(this, "该店铺已锁定");
                finish();
            }
        }
    }

}
