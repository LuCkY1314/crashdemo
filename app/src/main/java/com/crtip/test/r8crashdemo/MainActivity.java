package com.crtip.test.r8crashdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hotfix.patchdispatcher.HotfixPatchProxy;
import com.hotfix.patchdispatcher.IPatchHandler;
import com.hotfix.patchdispatcher.Patch;
import com.hotfix.patchdispatcher.util.ToastUtil;

import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private Handler handler = new Handler(Looper.getMainLooper());

    private Button button;

    private Calculator calculator = new Calculator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.btn);
        button.setText("It's an obvious bug:" + "\n" + getButtonDesc());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hotfix();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void hotfix() {
        HotfixPatchProxy.getInstance().patch(this, new IPatchHandler.OnPatchFixListener() {
            @Override
            public void onPatchesAcquired(List<Patch> patches) {
                Log.i(TAG, "All patches have been acquired");
            }

            @Override
            public void onPatchFixSuccess(Patch patch) {
                Log.i(TAG, "patch:" + patch.getName() + "has been applied");
            }

            @Override
            public void onPatchFixFailed(Patch patch) {
                Log.i(TAG, "patch:" + patch.getName() + "fix failed");
            }

            @Override
            public void onAllPatchesFixCompleted(final List<Patch> patches) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showTextForShort(MainActivity.this, String.format(Locale.CHINA, "All %d patches have been applied", patches.size()));
                        button.setText("Bug fixed:" + "\n" + getButtonDesc());
                    }
                });
            }

            @Override
            public void onPatchFixException(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showTextForShort(MainActivity.this, msg);
                    }
                });
            }
        });
    }

    private String getButtonDesc() {
        return "4 * 5 = " + doMultiplication(4, 5);
    }

    private int doAddition(Calculator calculator, int num1, int num2, String operation) {
        if (calculator == null) {
            return -1;
        }

        return calculator.calculate(num1, num2, operation) + 1;
    }

    private int doMultiplication(int num1, int num2) {
        if (calculator == null) {
            return -1;
        }

        return calculator.calculate(num1, num2, Calculator.MULTIPLICATION) + 1;
    }

    private void printPackageName() {
        printSomething(getPackageName());
    }

    private void printSomething(String printContent) {
        Log.i(getPackageName(), printContent);
    }
}