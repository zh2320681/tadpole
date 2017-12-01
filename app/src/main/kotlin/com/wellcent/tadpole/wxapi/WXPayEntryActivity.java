package com.wellcent.tadpole.wxapi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

//import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        WXPayParas paras = (WXPayParas) getIntent().getSerializableExtra("data");
//        IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
//        msgApi.registerApp(paras.getAppid());
//        PayReq request = new PayReq();
//        request.appId = paras.getAppid();
//        request.partnerId = paras.getPartnerId();
//        request.prepayId= paras.getPrepay_id();
//        request.packageValue = paras.getPackageValue();
//        request.nonceStr= paras.getNonce_str();
//        request.timeStamp= paras.getTimestamp();
//        request.sign= paras.getSign();
//        msgApi.sendReq(request);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if(baseResp.getType() != ConstantsAPI.COMMAND_PAY_BY_WX){
            return;
        }
        String result = "";
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送微信支付成功!";
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
