package com.juankpro.ane.localnotif;

import android.content.ContentProvider;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;

import com.juankpro.ane.localnotif.util.AssetDecompressor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.api.support.membermodification.MemberMatcher.methodsDeclaredIn;

@RunWith(PowerMockRunner.class)
@PrepareForTest({NotificationSoundProvider.class})
public class NotificationSoundProviderTest {
    @Mock
    private Uri uri;
    @Mock
    private Context context;
    @Mock
    private AssetDecompressor decompressor;
    @Mock
    private AssetFileDescriptor fileDescriptor;
    private ProviderInfo info;

    private NotificationSoundProvider subject;
    private NotificationSoundProvider getSubject() {
        if (subject == null) { subject = spy(new NotificationSoundProvider()); }
        return subject;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        info = new ProviderInfo();
        info.authority = "new.authority";

        doReturn(context).when(getSubject()).getContext();

        NotificationSoundProvider.AUTHORITY = "expected.authority";
        NotificationSoundProvider.CONTENT_URI = "content://expected.authority";

        try {
            whenNew(AssetDecompressor.class)
                .withArguments(context).thenReturn(decompressor);
        } catch (Throwable e) { e.printStackTrace(); }

        when(decompressor.decompress(anyString())).thenReturn(fileDescriptor);

        PowerMockito.suppress(methodsDeclaredIn(ContentProvider.class));
    }

    @Test
    public void provider_attachInfo_updatesAuthority() {
        assertEquals("expected.authority", NotificationSoundProvider.AUTHORITY);
        getSubject().attachInfo(null, info);
        assertEquals("new.authority", NotificationSoundProvider.AUTHORITY);
    }

    @Test
    public void provider_attachInfo_updatesUri() {
        assertEquals("content://expected.authority", NotificationSoundProvider.CONTENT_URI);
        getSubject().attachInfo(null, info);
        assertEquals("content://new.authority", NotificationSoundProvider.CONTENT_URI);
    }

    @Test
    public void provider_openAssetFile_returnsNullIfNotMp3OrWav() {
        when(uri.getLastPathSegment()).thenReturn("sound.mp3");

        try {
            assertSame(fileDescriptor, getSubject().openAssetFile(uri, ""));
        } catch (FileNotFoundException e) { e.printStackTrace(); }

        when(uri.getLastPathSegment()).thenReturn("sound.wav");

        try {
            assertSame(fileDescriptor, getSubject().openAssetFile(uri, ""));
        } catch (FileNotFoundException e) { e.printStackTrace(); }

        when(uri.getLastPathSegment()).thenReturn("sound.any");

        try {
            assertNull(getSubject().openAssetFile(uri, ""));
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }

    @Test
    public void provider_openAssetFile_decompressesUriLastSegment() {
        when(uri.getLastPathSegment()).thenReturn("sound.mp3");

        try {
            getSubject().openAssetFile(uri, "");
            verify(decompressor).decompress("sound.mp3");
        } catch (FileNotFoundException e) { e.printStackTrace(); }
    }
}
