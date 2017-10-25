package android.content;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadows.ShadowApplication;

import static org.robolectric.Shadows.shadowOf;

@Implements(value = AsyncQueryHandler.class, inheritImplementationMethods = true)
public class ShadowAsyncQueryHandler {
    @RealObject AsyncQueryHandler realObject;

    @Implementation
    public void startQuery(int token, Object cookie, Uri uri,
                           String[] projection, String selection, String[] selectionArgs,
                           String orderBy) {
        Cursor cursor = shadowOf(ShadowApplication.getInstance().getContentResolver())
                .query(uri, projection, selection, selectionArgs, orderBy);
        realObject.onQueryComplete(token, cookie, cursor != null ?
                cursor : new MatrixCursor(new String[0]));
    }
}
