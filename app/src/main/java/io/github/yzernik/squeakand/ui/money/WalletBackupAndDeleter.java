package io.github.yzernik.squeakand.ui.money;

public interface WalletBackupAndDeleter {

    String[] getWalletSeed();

    void deleteWallet();

}
