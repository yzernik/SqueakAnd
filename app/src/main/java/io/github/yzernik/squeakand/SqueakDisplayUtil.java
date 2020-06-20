package io.github.yzernik.squeakand;

import java.text.ParseException;
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
        return String.format("Block #%d (%s)", blockNumber, getDateString(blockTime));
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
