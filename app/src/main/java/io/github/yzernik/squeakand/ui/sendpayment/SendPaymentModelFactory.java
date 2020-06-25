package io.github.yzernik.squeakand.ui.sendpayment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class SendPaymentModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Application application;

    private final int offerID;

    public SendPaymentModelFactory(@NonNull Application application, int offerID) {
        this.application = application;
        this.offerID = offerID;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SendPaymentModel(application, offerID);
    }

}
