package com.zczczy.leo.fuwuwangapp.fragments;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zczczy.leo.fuwuwangapp.MyApplication;
import com.zczczy.leo.fuwuwangapp.R;
import com.zczczy.leo.fuwuwangapp.activities.LoginActivity_;
import com.zczczy.leo.fuwuwangapp.activities.MemberInfoActivity_;
import com.zczczy.leo.fuwuwangapp.activities.MemberOrderActivity_;
import com.zczczy.leo.fuwuwangapp.activities.ReviewActivity_;
import com.zczczy.leo.fuwuwangapp.activities.VipActivity_;
import com.zczczy.leo.fuwuwangapp.prefs.MyPrefs_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.springframework.util.StringUtils;

/**
 * Created by Leo on 2016/4/27.
 */
@EFragment(R.layout.fragment_mine)
public class MineFragment extends BaseFragment {

    @ViewById
    ImageView img_vip_icon;

    @ViewById
    TextView txt_name, txt_address;

    @ViewById
    LinearLayout ll_color_control;

    @ViewById
    RelativeLayout rl_vip, rl_setting;

    @ViewById
    ImageView img_avatar;

    @Pref
    MyPrefs_ pre;

    @AfterViews
    void afterView() {
        setData();
    }

    @Click
    void txt_review() {
        if (isLogin()) {
            ReviewActivity_.intent(this).start();
        } else {
            LoginActivity_.intent(this).startForResult(1000);
        }
    }

    @Click
    void txt_already_order() {
        if (isLogin()) {
            MemberOrderActivity_.intent(this).orderState(MyApplication.SEND).start();
        } else {
            LoginActivity_.intent(this).startForResult(1000);
        }
    }

    @Click
    void txt_waiting_order() {
        if (isLogin()) {
            MemberOrderActivity_.intent(this).orderState(MyApplication.DUEPAYMENT).start();
        } else {
            LoginActivity_.intent(this).startForResult(1000);
        }
    }

    @Click
    void txt_paid_order() {
        if (isLogin()) {
            MemberOrderActivity_.intent(this).orderState(MyApplication.PAID).start();
        } else {
            LoginActivity_.intent(this).startForResult(1000);
        }
    }

    @Click
    void rl_whole() {
        if (isLogin()) {
            MemberOrderActivity_.intent(this).orderState(MyApplication.ALL_ORDER).start();
        } else {
            LoginActivity_.intent(this).startForResult(1000);
        }
    }

    @Click
    void ll_login() {
        if (isLogin()) {
            MemberInfoActivity_.intent(this).start();
        } else {
            LoginActivity_.intent(this).startForResult(1000);
        }
    }

    @OnActivityResult(value = 1000)
    void afterLogin(int resultCode) {
        if (resultCode == FragmentActivity.RESULT_OK) {
            setData();
        }
    }

    @Click
    void card_logout() {
        if (isLogin()) {
            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setTitle("提示").setMessage("确定要注销吗？").setPositiveButton("注销", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pre.clear();
                    setData();
                }
            }).setNegativeButton("取消", null).setIcon(R.mipmap.logo).create().show();
        }
    }

    void setData() {
        if (isLogin()) {
            txt_name.setText(pre.username().get());
            if (!StringUtils.isEmpty(pre.avatar().get())) {
                Picasso.with(getActivity()).load(pre.avatar().get()).placeholder(R.drawable
                        .default_header).error(R.drawable.default_header).into(img_avatar);
            }
            if (MyApplication.VIP.equals(pre.userType().get())) {
                rl_vip.setVisibility(View.VISIBLE);
                img_vip_icon.setVisibility(View.VISIBLE);
                rl_setting.setVisibility(View.INVISIBLE);
            } else {
                rl_vip.setVisibility(View.INVISIBLE);
                img_vip_icon.setVisibility(View.INVISIBLE);
                rl_setting.setVisibility(View.VISIBLE);
            }
        } else {
            img_vip_icon.setVisibility(View.GONE);
            rl_vip.setVisibility(View.INVISIBLE);
            img_avatar.setImageResource(R.drawable.default_header);
            rl_setting.setVisibility(View.VISIBLE);
            txt_name.setText("点击登录");
            txt_address.setText("地址");
        }
    }

    @Click
    void rl_vip() {
        if (isLogin()) {
            VipActivity_.intent(this).start();
        }
    }

    //判断是否登录
    boolean isLogin() {
        return !StringUtils.isEmpty(pre.token().get()) && !StringUtils.isEmpty(pre.shopToken().get());
    }
}
