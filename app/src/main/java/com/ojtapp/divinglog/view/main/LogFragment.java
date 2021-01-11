package com.ojtapp.divinglog.view.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ojtapp.divinglog.R;
import com.ojtapp.divinglog.appif.DivingLog;
import com.ojtapp.divinglog.controller.ReadAsyncTask;
import com.ojtapp.divinglog.view.detail.LogActivity;

import java.util.List;

/**
 * リスト表示フラグメント
 */
public class LogFragment extends Fragment {
    private static final String TAG = LogFragment.class.getSimpleName();
    private ListView listView;
    private LogAdapter logAdapter;

    /**
     * フラグメントのインスタンスを作成
     * {@inheritDoc}
     */
    public static LogFragment newInstance() {
        android.util.Log.d(TAG, "newInstance()");

        LogFragment fragment = new LogFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Viewを作成
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceStat) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceStat) {
        super.onViewCreated(view, savedInstanceStat);
        // リスト表示のViewを結び付ける
        listView = view.findViewById(R.id.list_view_log);
        Log.d(TAG, "listView = " + listView);

        // リスト押下時
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick");
                DivingLog divingLog = (DivingLog) parent.getItemAtPosition(position);

                Intent intent = new Intent(getContext(), LogActivity.class);
                intent.putExtra(LogActivity.MODE_KEY, LogActivity.Mode.DETAIL_MOOD.value);
                intent.putExtra(LogActivity.TABLE_KEY, divingLog);
                startActivity(intent);
            }
        });

        // データを取得し、画面を更新処理
        refreshView();
    }

    private void refreshView() {
        Log.d(TAG, "refreshView()");
        final Context context = requireContext();
        //-------【DB】データ取得処理-------------
        ReadAsyncTask displayAsyncTask = new ReadAsyncTask(context);

        // コールバック処理
        displayAsyncTask.setOnCallBack(new ReadAsyncTask.DisplayCallback() {
            @Override
            public void onDisplay(List<DivingLog> logList) {
                Log.d(TAG, "onDisplay");
                // Adapterの設定
                LogFragment.this.logAdapter = new LogAdapter(context, R.layout.list_log_item, logList);
                listView.setAdapter(logAdapter);
            }
        });

        // 非同期処理のメソッドに移動
        displayAsyncTask.execute(0);
    }
}
