package io.github.yzernik.squeakand;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        return String.format(Locale.ENGLISH, "%s (Block #%d)", getDateString(blockTime), blockNumber);
    }

    public static String getBlockTextTimeAgo(SqueakEntryWithProfile entryWithProfile) {
        long blockNumber = entryWithProfile.squeakEntry.blockHeight;
        String timeAgo = TimeUtil.timeAgo(entryWithProfile.squeakEntry.block.getTime());
        return String.format(Locale.ENGLISH, "%s (Block #%d)", timeAgo, blockNumber);
    }

    public static String getAddressText(SqueakEntryWithProfile entryWithProfile) {
        return entryWithProfile.squeakEntry.authorAddress;
    }

    private static String getDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm aa - dd MMM yyyy", Locale.ENGLISH);
        System.out.println("Given date and time in AM/PM: "+ sdf.format(date));
        return sdf.format(date);
    }

}
