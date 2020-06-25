package io.github.yzernik.squeakand.lnd;

import android.util.Log;

import org.bitcoinj.core.Sha256Hash;

import java.util.concurrent.ExecutorService;

import io.github.yzernik.squeakand.server.SqueakNetworkAsyncClient;
import io.github.yzernik.squeakand.server.SqueakNetworkController;
import lnrpc.Rpc;

public class LndAsyncClient {

    private final LndController lndController;

    public LndAsyncClient(LndController lndController) {
        this.lndController = lndController;
    }

    public void sendPayment(String paymentRequest, PaymentResponseHandler responseHandler) {
        LndClient lndClient = lndController.getLndClient();

        lndClient.sendPayment(paymentRequest, new LndClient.SendPaymentCallBack() {
            @Override
            public void onError(Exception e) {
                responseHandler.onFailure(e);
            }

            @Override
            public void onResponse(Rpc.SendResponse response) {
                responseHandler.onSuccess(response);
            }
        });
    }

    public interface PaymentResponseHandler {
        public void onSuccess(Rpc.SendResponse response);
        public void onFailure(Throwable e);
    }

}
