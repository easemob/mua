package com.community.mua.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.community.mua.imkit.utils.EaseCompat;
import com.community.mua.imkit.utils.EaseFileUtils;

import java.io.File;

public class TakeImageUtils {
    private static final TakeImageUtils ourInstance = new TakeImageUtils();
    public static final int REQUEST_CODE_CAMERA = 1010;
    public static final int REQUEST_CODE_GALLERY = 1011;
    public static final int REQUEST_CODE_CROP = 1012;

    public static TakeImageUtils getInstance() {
        return ourInstance;
    }

    private TakeImageUtils() {
    }


    public Uri doTakePhoto(Activity context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String path = context.getFilesDir().getAbsolutePath() + File.separator + "test.jpg"; //这里只是当前目录，如果是多级文件夹 得通过 mkdirs 创建
        File file = new File(path);

        //由于 Android 文件安全机制 向第三方应用提供路径的时候得使用 FileProvider，注意需要在清单文件中注册
        Uri mPicUri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            mPicUri = FileProvider.getUriForFile(context, "com.community.mua.fileProvider", file);
        } else {
            mPicUri = Uri.fromFile(file);
        }

        //这里设置了 uri 那么后面就不能使用 data.getParcelableExtra("data") 获取图片了
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPicUri);

        context.startActivityForResult(intent, REQUEST_CODE_CAMERA);

        return mPicUri;
    }

    public File cropPhoto(Activity context, Uri sourceUri, int sizeX, int sizeY) {
        File imageCropFile = EaseFileUtils.createImageFile(context, true);
        if (imageCropFile != null) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.putExtra("crop", "true");

            intent.putExtra("aspectX", sizeX);
            intent.putExtra("aspectY", sizeY);
            intent.putExtra("outputX", sizeX);
            intent.putExtra("outputY", sizeY);
            intent.putExtra("scale ", true);  //是否保留比例
            intent.putExtra("return-data", false);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.setDataAndType(sourceUri, "image/*"); //设置数据源
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, EaseFileUtils.uri);
            } else {
                Uri imgCropUri = Uri.fromFile(imageCropFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgCropUri);
            }
            context.startActivityForResult(intent, REQUEST_CODE_CROP);
        }
        return imageCropFile;
    }

    public void doOpenGallery(Activity context) {
        EaseCompat.openImage(context, REQUEST_CODE_GALLERY);
    }
}
