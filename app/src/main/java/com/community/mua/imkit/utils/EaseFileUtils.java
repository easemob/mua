package com.community.mua.imkit.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.community.mua.App;
import com.community.mua.imkit.manager.EasePreferenceManager;
import com.hyphenate.util.EMFileHelper;
import com.hyphenate.util.EMLog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EaseFileUtils {
    private static final String TAG = EaseFileUtils.class.getSimpleName();

    private static boolean isQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    public static boolean isFileExistByUri(Context context, Uri fileUri) {
        return EMFileHelper.getInstance().isFileExist(fileUri);
    }

    /**
     * 删除文件
     * @param context
     * @param uri
     */
    public static void deleteFile(Context context, Uri uri) {
        if(isFileExistByUri(context, uri)) {
            String filePath = getFilePath(context, uri);
            if(!TextUtils.isEmpty(filePath)) {
                File file = new File(filePath);
                if(file != null && file.exists() && file.isFile()) {
                    file.delete();
                }
            }else {
                try {
                    context.getContentResolver().delete(uri, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取文件名
     * @param context
     * @param fileUri
     * @return
     */
    public static String getFileNameByUri(Context context, Uri fileUri) {
        return EMFileHelper.getInstance().getFilename(fileUri);
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getFilePath(final Context context, final Uri uri) {
        return EMFileHelper.getInstance().getFilePath(uri);
    }

    /**
     * 判断uri是否以file开头
     * @param fileUri
     * @return
     */
    public static boolean uriStartWithFile(Uri fileUri) {
        return "file".equalsIgnoreCase(fileUri.getScheme()) && fileUri.toString().length() > 7;
    }

    /**
     * 判断是否以content开头的Uri
     * @param fileUri
     * @return
     */
    public static boolean uriStartWithContent(Uri fileUri) {
        return "content".equalsIgnoreCase(fileUri.getScheme());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 是否是本app的FileProvider
     * @param context
     * @param uri
     * @return
     */
    public static boolean isFileProvider(Context context, Uri uri) {
        return (context.getApplicationInfo().packageName + ".fileProvider").equalsIgnoreCase(uri.getAuthority());
    }

    /**
     * 其他app分享过来的FileProvider
     * @param context
     * @param uri
     * @return
     */
    public static boolean isOtherFileProvider(Context context, Uri uri) {
        String scheme = uri.getScheme();
        String authority = uri.getAuthority();
        if(TextUtils.isEmpty(scheme) || TextUtils.isEmpty(authority)) {
            return false;
        }
        return !(context.getApplicationInfo().packageName + ".fileProvider").equalsIgnoreCase(uri.getAuthority())
                && "content".equalsIgnoreCase(uri.getScheme())
                && authority.contains(".fileProvider".toLowerCase());
    }

    public static boolean saveUriPermission(Context context, Uri fileUri, Intent intent) {
        if(context == null || fileUri == null) {
            return false;
        }
        //目前只处理scheme为"content"的Uri
        if(!uriStartWithContent(fileUri)) {
            return false;
        }
        int intentFlags = 0;
        if(intent != null) {
            intentFlags = intent.getFlags();
        }
        int takeFlags = intentFlags & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        String last = null;
        try {
            context.getContentResolver().takePersistableUriPermission(fileUri, takeFlags);
            last = getLastSubFromUri(fileUri);
            EMLog.d(TAG, "saveUriPermission last part of Uri: "+last);
        } catch (SecurityException e) {
            EMLog.e("EaseFileUtils", "saveUriPermission failed e: "+e.getMessage());
        }
        if(!TextUtils.isEmpty(last)) {
            EasePreferenceManager.getInstance().putString(last, fileUri.toString());
            return true;
        }
        return false;
    }

    private static String getLastSubFromUri(Uri fileUri) {
        if(fileUri == null) {
            return "";
        }
        String uri = fileUri.toString();
        if(!uri.contains("/")) {
            return "";
        }
        int lastIndex = uri.lastIndexOf("/");
        return uri.substring(lastIndex + 1);
    }

    /**
     * 获取Uri的永久读权限
     * @param context
     * @param uri
     * @return
     */
    public static Uri takePersistableUriPermission(Context context, Uri uri) {
        if(context == null || uri == null) {
            return null;
        }
        //目前只处理scheme为"content"的Uri
        if(!uriStartWithContent(uri)) {
            return null;
        }
        //获取Uri的读权限
        String last = getLastSubFromUri(uri);
        if(!TextUtils.isEmpty(last)) {
            String fileUri = EasePreferenceManager.getInstance().getString(last);
            if(!TextUtils.isEmpty(fileUri)) {
                try {
                    context.getContentResolver().takePersistableUriPermission(Uri.parse(fileUri), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    return Uri.parse(fileUri);
                } catch (SecurityException e) {
                    EMLog.e("EaseFileUtils", "takePersistableUriPermission failed e: "+e.getMessage());
                    return null;
                }
            }
        }
        try {
            context.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException e) {
            EMLog.e("EaseFileUtils", "takePersistableUriPermission failed e: "+e.getMessage());
            return null;
        }
        return uri;
    }

    public static File getAppRootDirPath() {
        return App.getContext().getExternalFilesDir(null).getAbsoluteFile();
    }
    public static Uri uri;
    public static File createImageFile(Context context,boolean isCrop) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "";
            if (isCrop) {
                fileName = "IMG_"+timeStamp+"_CROP.jpg";
            } else {
                fileName = "IMG_"+timeStamp+".jpg";
            }
            File rootFile = new File(getAppRootDirPath() + File.separator + "capture");
            if (!rootFile.exists()) {
                rootFile.mkdirs();
            }
            File imgFile;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                imgFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + fileName);
                // 通过 MediaStore API 插入file 为了拿到系统裁剪要保存到的uri（因为App没有权限不能访问公共存储空间，需要通过 MediaStore API来操作）
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, imgFile.getAbsolutePath());
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }else {
                imgFile = new File(rootFile.getAbsolutePath() + File.separator + fileName);
            }
            return imgFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getCropFile(Context context, Uri uri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return new File(path);
        }


        return null;
    }

}

