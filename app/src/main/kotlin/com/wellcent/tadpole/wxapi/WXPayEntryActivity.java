package com.wellcent.tadpole.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.wellcent.tadpole.bo.GoodsPayResult;
import com.wellcent.tadpole.ui.OrderResultActivity;

//import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    //传参数写的不好 管他呢
    public static GoodsPayResult goodsPayResult;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReq(BaseReq baseReq) {}

    @Override
    public void onResp(BaseResp baseResp) {
        if(baseResp.getType() != ConstantsAPI.COMMAND_PAY_BY_WX){
            return;
        }
        String result = "";
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送微信支付成功!";
                Intent i = new Intent(this,OrderResultActivity.class);
                i.putExtra("RESULT",goodsPayResult);
                startActivity(i);
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送微信支付取消!";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送微信支付被拒绝!";
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = "发送微信支付位置错误!";
                break;
            default:
                result = "发送微信支付返回!";
                break;
        }

        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }
}
