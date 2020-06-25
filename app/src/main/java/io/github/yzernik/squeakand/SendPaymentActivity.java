package io.github.yzernik.squeakand;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import io.github.yzernik.squeakand.ui.sendpayment.SendPaymentFragment;

public class SendPaymentActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_payment);

        // Get the transferred data from source activity.
        int offerId = getIntent().getIntExtra("offer_id", -1);
        Log.i(getCallingPackage(), "offerId in onCreate: " + offerId);

        Bundle bundle = new Bundle();
        bundle.putInt("offer_id", offerId);
        Fragment newFragment = new SendPaymentFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        newFragment.setArguments(bundle);
        transaction.replace(R.id.send_payment_fragment_frame, newFragment);
        // transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

}
