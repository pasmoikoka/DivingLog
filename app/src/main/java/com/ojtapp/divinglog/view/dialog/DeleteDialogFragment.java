package com.ojtapp.divinglog.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ojtapp.divinglog.appif.DivingLog;
import com.ojtapp.divinglog.controller.DeleteAsyncTask;
import com.ojtapp.divinglog.view.main.MainActivity;

;

public class DeleteDialogFragment extends DialogFragment {
    /**
     * クラス名
     */
    private static final String TAG = DeleteDialogFragment.class.getSimpleName();
    /**
     * ダイアログのタイトル
     */
    private static final String TITLE_DELETE_DIALOG = "削除確認";
    /**
     * ダイアログのメッセージ
     */
    private static final String MESSAGE_DELETE_DIALOG = "削除してもよろしいですか？";
    /**
     * ポジティブボタンの名称
     */
    private static final String BUTTON_POSITIVE = "はい";
    /**
     * ネガティブボタンの名称
     */
    private static final String BUTTON_NEGATIVE = "いいえ";
    /**
     * DivingLogオブジェクト受け取り用キー
     */
    private static final String LOG_KEY = "DIVE_LOG";
    /**
     * コンテクスト
     */
    private Context context;
    /**
     * 「はい」押下時のコールバック
     */
    public OnClickButtonListener onClickButtonListener;

    /**
     * デフォルトコンストラクタ
     */
    DeleteDialogFragment() {
    }

    /**
     * インスタンスの作成
     *
     * @param divingLog 削除処理をするログ
     * @return フラグメント
     */
    public static DeleteDialogFragment newInstance(@NonNull DivingLog divingLog) {
        Log.d(TAG, "newInstance()");

        DeleteDialogFragment fragment = new DeleteDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(LOG_KEY, divingLog);
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.context = getContext();
        assert this.context != null;
        return new AlertDialog.Builder(this.context)
                .setTitle(TITLE_DELETE_DIALOG)
                .setMessage(MESSAGE_DELETE_DIALOG)
                .setPositiveButton(BUTTON_POSITIVE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClickButtonListener.onClickPositiveButton();
                    }
                })
                .setNegativeButton(BUTTON_NEGATIVE, null)
                .show();
    }

//    /**
//     * 削除ダイアログにて「はい」を押下時に、そのログを削除する
//     */
//    private void onClickPositiveButtonListener() {
//        DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask(this.context);
//
//        deleteAsyncTask.setDeleteCallback(new DeleteAsyncTask.DeleteCallback() {
//            @Override
//            public void onSuccess() {
//                Intent intent = new Intent(DeleteDialogFragment.this.context, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onFailure() {
//                Log.e(TAG, "正常に削除されませんでした");
//            }
//        });
//
//        Bundle args = getArguments();
//        if (null == args) {
//            Log.e(TAG, "args = null");
//            return;
//        }
//
//        // シリアライズしたDivingLogクラスを格納
//        final DivingLog divingLog = (DivingLog) args.getSerializable(LOG_KEY);
//
//        // 非同期処理を実行
//        deleteAsyncTask.execute(divingLog);
//    }

    /**
     * コールバック処理を設定
     *
     * @param onClickButtonListener 　コールバックする内容
     */
    public void setOnClickButtonListener(@Nullable OnClickButtonListener onClickButtonListener) {
        this.onClickButtonListener = onClickButtonListener;
    }

    /**
     * コールバック用インターフェイス
     */
    public interface OnClickButtonListener {
        void onClickPositiveButton();
    }
}