package com.example.youtube.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.example.youtube.entities.Video;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GeneralUtils {

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static String getFileExtension(Context context, Uri uri) {
        String extension = null;
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                extension = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                if (extension != null) {
                    int dotIndex = extension.lastIndexOf('.');
                    if (dotIndex != -1) {
                        extension = extension.substring(dotIndex + 1);
                    } else {
                        extension = "";
                    }
                }
            }
        }
        return extension;
    }

    public static String timeAgo(String dateString) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date givenDate;

        if (dateString == null){
            return "just now";
        }

        try {
            givenDate = dateFormat1.parse(dateString);
        } catch (ParseException e) {
            try {
                givenDate = dateFormat2.parse(dateString);
            } catch (ParseException t){
                return "invalid date";
            }
        }

        Date currentDate = new Date();
        assert givenDate != null;
        long diffInMillis = currentDate.getTime() - givenDate.getTime();

        long years = TimeUnit.MILLISECONDS.toDays(diffInMillis) / 365;
        if (years > 0) {
            return years + " years ago";
        }

        long months = TimeUnit.MILLISECONDS.toDays(diffInMillis) / 30;
        if (months > 0) {
            return months + " months ago";
        }

        long weeks = TimeUnit.MILLISECONDS.toDays(diffInMillis) / 7;
        if (weeks > 0) {
            return weeks + " weeks ago";
        }

        long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        if (days > 0) {
            return days + " days ago";
        }

        return "today";
    }

    public static String getViews(String numberStr) {
        try {
            long number = Long.parseLong(numberStr);
            if (number >= 1_000_000_000) {
                return number / 1_000_000_000 + "B";
            } else if (number >= 1_000_000) {
                return number / 1_000_000 + "M";
            } else if (number >= 1_000) {
                return number / 1_000 + "K";
            } else {
                return String.valueOf(number);
            }
        } catch (NumberFormatException e) {
            return "Invalid number";
        }
    }

    public static List<Video> removeVideo(List<Video> videos, Video video){
        List<Video> temp = new ArrayList<>();
        if (video == null){
            return videos;
        }
        for (Video v : videos){
            if (!Objects.equals(v.get_id(), video.get_id())){
                temp.add(v);
            }
        }
        return temp;
    }

    public static String getStringAfterBackslash(String input) {
        int index = input.lastIndexOf('\\');
        if (index != -1 && index < input.length() - 1) {
            return input.substring(index + 1);
        } else {
            return "";
        }
    }

    public static String generateRandomString() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 2; i++) {
            sb.append(random.nextInt(6));
        }
        sb.append(":");
        for (int i = 0; i < 2; i++) {
            sb.append(random.nextInt(6));
        }
        return sb.toString();
    }

    public static String removeTrailingNewline(String str) {
        if (str != null && str.endsWith("\n")) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }
}
