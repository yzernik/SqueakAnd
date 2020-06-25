package io.github.yzernik.squeakand.ui.sendpayment;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

public class SendPaymentModel extends AndroidViewModel {

    private int offerId;

    public SendPaymentModel(Application application, int offerId) {
        super(application);
        this.offerId = offerId;
    }

}