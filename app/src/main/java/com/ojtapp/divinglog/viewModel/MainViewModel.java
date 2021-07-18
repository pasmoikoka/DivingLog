package com.ojtapp.divinglog.viewModel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;

import com.ojtapp.divinglog.BindingAdapter.ImageViewBindingAdapter;
import com.ojtapp.divinglog.LogConstant;
import com.ojtapp.divinglog.appif.DivingLog;
import com.ojtapp.divinglog.controller.DeleteAsyncTask;
import com.ojtapp.divinglog.controller.RegisterAsyncTask;
import com.ojtapp.divinglog.controller.UpdateAsyncTask;
import com.ojtapp.divinglog.util.ControlDBUtil;
import com.ojtapp.divinglog.util.ConversionUtil;
import com.ojtapp.divinglog.view.dialog.DialogFragment;
import com.ojtapp.divinglog.view.main.MainActivity;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class MainViewModel extends ViewModel implements ClickHandlers {
    private static final String TAG = MainViewModel.class.getSimpleName();
    public String diveNumber;
    public String place;
    public String point;
    public String depthMax;
    public String depthAve;
    public String airStart;
    public String airEnd;
    public String weather;
    public String temp;
    public String tempWater;
    public String visibility;
    public String memberNavigate;
    public String member;
    public String memo;
    public Uri uri;
    public int year;
    public int month;
    public int day;
    public int hourStart;
    public int minuteStart;
    public int hourEnd;
    public int minuteEnd;
    private DivingLog divingLog = new DivingLog();

    /**
     * Context受け取り用
     */
    private static WeakReference<Context> weakReference = null;

    /**
     * コンストラクタ
     * @param context
     * @param divingLog
     */
    public MainViewModel(@NonNull Context context, @Nullable DivingLog divingLog) {
        weakReference = new WeakReference<>(context);
        if (null != divingLog) {
            this.divingLog = divingLog;
            setDataToLayout(divingLog);
        }
    }

    /**
     * {@inheritDoc}
     * @param view ボタン
     */
    @Override
    public void onMakeClick(View view) {
        Log.d("VM", "onMakeClick");
        // -----【DB】保存処理--------------
        RegisterAsyncTask registerAsyncTask = new RegisterAsyncTask(weakReference.get());
        registerAsyncTask.setOnCallBack(new RegisterAsyncTask.RegisterCallback() {
            @Override
            public void onSuccess() {
                // --------最初の画面へ戻る処理------
                Intent intent = new Intent(weakReference.get(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                weakReference.get().startActivity(intent);
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "正常にデータを保存できませんでした");
            }
        });

        setDateToDivingLog(divingLog);
        registerAsyncTask.execute(divingLog);
    }

    /**
     * {@inheritDoc}
     * @param view 　ボタン
     */
    @Override
    public void onDeleteClick(View view) {
        final FragmentActivity activity = (FragmentActivity) weakReference.get();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        DialogFragment deleteDialogFragment = DialogFragment.newInstance(
                LogConstant.TITLE_DELETE_DIALOG,
                LogConstant.MESSAGE_DELETE_DIALOG);

        deleteDialogFragment.setOnClickButtonListener(new DialogFragment.OnClickButtonListener() {
            @Override
            public void onClickPositiveButton() {
                DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask(weakReference.get(), activity);
                deleteAsyncTask.setDeleteCallback(new DeleteAsyncTask.DeleteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "削除に成功しました");
                        Intent intent = new Intent(weakReference.get(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        weakReference.get().startActivity(intent);
                    }

                    @Override
                    public void onFailure() {
                        Log.e(TAG, "削除に失敗しました");
                    }
                });
                deleteAsyncTask.execute(divingLog);
            }

            @Override
            public void onClickNegativeButton() {
            }
        });
        deleteDialogFragment.show(fragmentManager, null);
    }

    /**
     * {@inheritDoc}
     * @param view 　ボタン
     */
    @Override
    public void onEditClick(View view) {
        // -----【DB】更新処理--------------
        UpdateAsyncTask updateAsyncTask = new UpdateAsyncTask(weakReference.get());
        updateAsyncTask.setUpdateCallback(new UpdateAsyncTask.UpdateCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "上書きに成功しました");
                // 情報をintentに詰める
                Intent intent = new Intent(weakReference.get(), MainActivity.class);
                // 指定したアクティビティより上のViewを削除
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                weakReference.get().startActivity(intent);
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "上書きに失敗しました");
            }
        });

        setDateToDivingLog(divingLog);
        updateAsyncTask.execute(divingLog);
    }

    /**
     * DivingLogクラスにあるデータをレイアウトに格納する
     *
     * @param divingLog データを持っているDivingLogクラス
     */
    private void setDataToLayout(DivingLog divingLog) {
        diveNumber = String.valueOf(divingLog.getDivingNumber());
        place = divingLog.getPlace();
        point = divingLog.getPoint();
        depthMax = ConversionUtil.getStrFromInt(divingLog.getDepthMax());
        depthAve = ConversionUtil.getStrFromInt(divingLog.getDepthAve());
        airStart = ConversionUtil.getStrFromInt(divingLog.getAirStart());
        airEnd = ConversionUtil.getStrFromInt(divingLog.getAirEnd());
        weather = divingLog.getWeather();
        temp = ConversionUtil.getStrFromInt(divingLog.getTemp());
        tempWater = ConversionUtil.getStrFromInt(divingLog.getTempWater());
        visibility = ConversionUtil.getStrFromInt(divingLog.getVisibility());
        member = divingLog.getMember();
        memberNavigate = divingLog.getMemberNavigate();
        memo = divingLog.getMemo();

        Calendar cal = Calendar.getInstance();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(LogConstant.FORMAT_DATE, Locale.JAPAN);
            Optional<Date> defaultDateOpt = Optional.ofNullable(dateFormat.parse(divingLog.getDate()));
            defaultDateOpt.ifPresent(cal::setTime);  //TODO ifPresentOrElse
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            Log.e(TAG, "Date's Error : " + e);
        }

        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat(LogConstant.FORMAT_TIME, Locale.JAPAN);
            Optional<Date> defaultStartTimeOpt = Optional.ofNullable(timeFormat.parse(divingLog.getTimeStart()));
            defaultStartTimeOpt.ifPresent(cal::setTime);  //TODO ifPresentOrElse
            hourStart = cal.get(Calendar.HOUR_OF_DAY);
            minuteStart = cal.get(Calendar.MINUTE);

            Optional<Date> defaultEndTimeOpt = Optional.ofNullable(timeFormat.parse(divingLog.getTimeEnd()));
            defaultEndTimeOpt.ifPresent(cal::setTime);  //TODO ifPresentOrElse
            hourEnd = cal.get(Calendar.HOUR_OF_DAY);
            minuteEnd = cal.get(Calendar.MINUTE);
        } catch (ParseException e) {
            Log.e(TAG, "Time's Error : " + e);
        }

// TODO 模索中
//        try {
//            String pictureUri = divingLog.getPictureUri();
//            if (null != pictureUri) {
//                uri = Uri.parse(pictureUri);
//                Bitmap bitmap = ConversionUtil.getBitmapFromUri(uri, requireContext());
//                picture.setImageBitmap(bitmap);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * ユーザーに入力されたデータをDivingLogクラスに格納する
     *
     * @param divingLog 　格納先のDivingLogクラス
     */
    public void setDateToDivingLog(DivingLog divingLog) {
        divingLog.setDiveNumber(Integer.parseInt(diveNumber));
        divingLog.setPlace(place);
        divingLog.setPoint(point);
        divingLog.setDepthMax(ConversionUtil.getIntFromStr(depthMax));
        divingLog.setDepthAve(ConversionUtil.getIntFromStr(depthAve));
        divingLog.setAirStart(ConversionUtil.getIntFromStr(airStart));
        divingLog.setAirEnd(ConversionUtil.getIntFromStr(airEnd));
        divingLog.setAirDive(getDiveAir());
        divingLog.setWeather(weather);
        divingLog.setTemp(ConversionUtil.getIntFromStr(temp));
        divingLog.setTempWater(ConversionUtil.getIntFromStr(tempWater));
        divingLog.setVisibility(ConversionUtil.getIntFromStr(visibility));
        divingLog.setMember(member);
        divingLog.setMemberNavigate(memberNavigate);
        divingLog.setMemo(memo);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(LogConstant.FORMAT_DATE, Locale.JAPAN);
        SimpleDateFormat timeFormat = new SimpleDateFormat(LogConstant.FORMAT_TIME, Locale.JAPAN);

        divingLog.setDate(getStrDate(calendar, dateFormat));
        divingLog.setTimeStart(getStrTimeStart(calendar, timeFormat));
        divingLog.setTimeEnd(getStrTimeEnd(calendar, timeFormat));
        divingLog.setTimeDive(getStrTimeDive(calendar, timeFormat));

        Uri uri = ImageViewBindingAdapter.uri;
        if (null != uri) {
            divingLog.setPictureUri(uri.toString());
        }
    }

    /**
     * ダイビングで使用した空気の総量を計算し、値を返す
     *
     * @return ダイビングで使用した空気の総量
     */
    private int getDiveAir() {
        int airStartInt = ConversionUtil.getIntFromStr(airStart);
        int airEndInt = ConversionUtil.getIntFromStr(airEnd);

        if ((ConversionUtil.NO_DATA == airStartInt) || (ConversionUtil.NO_DATA == airEndInt)) {
            return ConversionUtil.NO_DATA;
        } else {
            return airStartInt - airEndInt;
        }
    }

    /**
     * 日付をString型のフォーマットに変換して返す
     *
     * @param calendar   カレンダークラス
     * @param dateFormat 日付用のフォーマット
     * @return フォーマットを適用した日付
     */
    private String getStrDate(@NonNull Calendar calendar, @NonNull SimpleDateFormat dateFormat) {
        Log.d("MV", "y=" + year + " ,m=" + month + " , d=" + day);
        calendar.set(year, month, day); // 日付をカレンダークラスにセット
        return dateFormat.format(calendar.getTime());   // フォーマットを指定してDivingLogクラスにセット
    }

    /**
     * 開始時間をString型のフォーマットに変換して返す
     *
     * @param calendar   カレンダークラス
     * @param timeFormat 時間用のフォーマット
     * @return フォーマットを適用した開始時間
     */
    private String getStrTimeStart(@NonNull Calendar calendar, @NonNull SimpleDateFormat timeFormat) {
        calendar.set(Calendar.HOUR_OF_DAY, hourStart);
        calendar.set(Calendar.MINUTE, minuteStart);
        return timeFormat.format(calendar.getTime());
    }

    /**
     * 終了時間をString型のフォーマットに変換して返す
     *
     * @param calendar   カレンダークラス
     * @param timeFormat 時間用のフォーマット
     * @return フォーマットを適用した終了時間
     */
    private String getStrTimeEnd(@NonNull Calendar calendar, @NonNull SimpleDateFormat timeFormat) {
        calendar.set(Calendar.HOUR_OF_DAY, hourEnd);
        calendar.set(Calendar.MINUTE, minuteEnd);
        return timeFormat.format(calendar.getTime());
    }

    /**
     * ダイビング総時間をString型のフォーマットに変換して返す
     *
     * @param calendar   カレンダークラス
     * @param timeFormat 時間用のフォーマット
     * @return フォーマットを適用した総時間
     */
    private String getStrTimeDive(@NonNull Calendar calendar, @NonNull SimpleDateFormat timeFormat) {
        int hour = hourEnd - hourStart;
        int minute;
        if (minuteEnd < minuteStart) {
            minute = minuteEnd + 60 - minuteStart;
            hour = hour - 1;
        } else {
            minute = minuteEnd - minuteStart;
        }
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        Log.d(TAG, "time = " + timeFormat.format(calendar.getTimeInMillis()));
        return timeFormat.format(calendar.getTimeInMillis());
    }
}
