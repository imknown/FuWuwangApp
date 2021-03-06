package com.zczczy.leo.fuwuwangapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.zczczy.leo.fuwuwangapp.items.BaseUltimateViewHolder;
import com.zczczy.leo.fuwuwangapp.items.CouponManageInfoItemView_;
import com.zczczy.leo.fuwuwangapp.model.BaseModelJson;
import com.zczczy.leo.fuwuwangapp.model.QueueCompanyDetail;
import com.zczczy.leo.fuwuwangapp.prefs.MyPrefs_;
import com.zczczy.leo.fuwuwangapp.rest.MyDotNetRestClient;
import com.zczczy.leo.fuwuwangapp.rest.MyErrorHandler;
import com.zczczy.leo.fuwuwangapp.tools.AndroidTool;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.rest.spring.annotations.RestService;

import java.util.List;

/**
 * Created by Leo on 2016/4/29.
 */
@EBean
public class CouponManageInfoAdapter extends BaseUltimateRecyclerViewAdapter<QueueCompanyDetail> {

    @StringRes
    String no_net;

    @Bean
    MyErrorHandler myErrorHandler;

    @RestService
    MyDotNetRestClient myRestClient;

    @Pref
    MyPrefs_ pre;

    boolean isRefresh = false;

    @AfterInject
    void afterInject() {
        myRestClient.setRestErrorHandler(myErrorHandler);
    }


    @Override
    @Background
    public void getMoreData(int pageIndex, int pageSize, boolean isRefresh, Object... objects) {
        this.isRefresh = isRefresh;
        String token = pre.token().get();
        myRestClient.setHeader("Token", token);
        BaseModelJson<List<QueueCompanyDetail>> bmj = myRestClient.GetCompanyQueueDetail(objects[0].toString());
        afterGetData(bmj);
    }

    @UiThread
    void afterGetData(BaseModelJson<List<QueueCompanyDetail>> bmj) {
        AndroidTool.dismissLoadDialog();
        if (bmj == null) {
            bmj = new BaseModelJson<>();
//            AndroidTool.showToast(context, no_net);
        } else if (bmj.Successful) {
            if (isRefresh) {
                clear();
            }
            if (bmj.Data.size() > 0) {
                insertAll(bmj.Data, getItems().size());
            }
        } else {
            AndroidTool.showToast(context, bmj.Error);
        }
    }

    @Override
    void onBindHeaderViewHolder(BaseUltimateViewHolder viewHolder) {

    }

    @Override
    protected View onCreateItemView(ViewGroup parent) {
        return CouponManageInfoItemView_.build(parent.getContext());
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }
}
