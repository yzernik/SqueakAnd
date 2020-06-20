package io.github.yzernik.squeakand;

import java.util.Date;

public class SqueakDisplayUtil {

    public static String getAuthorText(SqueakEntryWithProfile entryWithProfile) {
        String authorAddress = entryWithProfile.squeakEntry.authorAddress;
        SqueakProfile authorProfile = entryWithProfile.squeakProfile;
        String authorDisplay = authorAddress;
        if (authorProfile != null) {
            String authorName = entryWithProfile.squeakProfile.getName();
            authorDisplay = authorName;
        }
        return authorDisplay;
    }

    public static String getSqueakText(SqueakEntryWithProfile entryWithProfile) {
        return entryWithProfile.squeakEntry.getDecryptedContentStr();
    }

    public static String getBlockText(SqueakEntryWithProfile entryWithProfile) {
        long blockNumber = entryWithProfile.squeakEntry.blockHeight;
        Date blockTime = entryWithProfile.squeakEntry.block.getTime();
        return String.format("Block #%d (%s)", blockNumber, blockTime.toString());
    }

    public static String getAddressText(SqueakEntryWithProfile entryWithProfile) {
        return entryWithProfile.squeakEntry.authorAddress;
    }

}
