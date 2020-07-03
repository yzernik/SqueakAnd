package io.github.yzernik.squeakand.ui.money;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

public class MoneyMenuActions {

    private WalletBackupAndDeleter walletBackupAndDeleter;

    public MoneyMenuActions(WalletBackupAndDeleter walletBackupAndDeleter) {
        this.walletBackupAndDeleter = walletBackupAndDeleter;
    }


    public void showWalletBackupAlert(Context context) {
        String[] seedWords = getSeedWords();
        if (seedWords == null) {
            showNoWalletBackupAlert(context);
        } else {
            showHasWalletBackupAlert(context, seedWords);
        }
    }

    private void showNoWalletBackupAlert(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("No Wallet backup seed");
        alertDialog.setMessage("You do not have any saved wallet seed words.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showHasWalletBackupAlert(Context context, String[] seedWords) {
        String seedWordsString = String.join(", ", seedWords);
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Wallet backup seed");
        alertDialog.setMessage("Seed words: " + seedWordsString + ".");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    private String[] getSeedWords() {
        return walletBackupAndDeleter.getWalletSeed();
    }


    private void deleteWallet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                walletBackupAndDeleter.deleteWallet();
            }
        }).start();
    }

    public void showDeleteWalletAlertDialog(Context context) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();
        alertDialog.setTitle("Delete wallet?");
        alertDialog.setMessage("Are you sure you want to delete this wallet? Make sure to backup the seed words first.");

        alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(getClass().getName(), "Deleting wallet.");
                        deleteWallet();
                        dialog.dismiss();
                    }
                });


        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

}
