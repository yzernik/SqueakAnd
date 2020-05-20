package io.github.yzernik.squeakand;


import androidx.room.Embedded;

public class SqueakEntryWithProfile {

    @Embedded
    public SqueakEntry squeakEntry;

    @Embedded
    public SqueakProfile squeakProfile;

}
