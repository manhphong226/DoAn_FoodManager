package vn.edu.stu.nguyenmanhphong_dh52301513.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import vn.edu.stu.nguyenmanhphong_dh52301513.R;

public class ImageHelper {

    private static final String IMAGE_FOLDER = "food_images";

    public static String copyImageToInternalStorage(Context context, Uri uri) {
        try {
            File folder = new File(context.getFilesDir(), IMAGE_FOLDER);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File destination = new File(folder, "food_" + System.currentTimeMillis() + ".jpg");

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(destination);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return destination.getAbsolutePath();
        } catch (Exception e) {
            Log.e("LOI_ANH", e.toString());
            return "";
        }
    }

    public static void loadImage(ImageView imageView, String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                BitmapFactory.Options bounds = new BitmapFactory.Options();
                bounds.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imagePath, bounds);

                int sampleSize = 1;
                int targetSize = 300;
                while (bounds.outWidth / sampleSize > targetSize * 2
                        && bounds.outHeight / sampleSize > targetSize * 2) {
                    sampleSize *= 2;
                }

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = sampleSize;
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    return;
                }
            }
        }
        imageView.setImageResource(R.drawable.ic_food_placeholder);
    }

    public static void deleteImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
