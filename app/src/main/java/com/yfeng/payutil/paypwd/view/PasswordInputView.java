package com.yfeng.payutil.paypwd.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomSheetDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.yfeng.payutil.paypwd.Constants;
import com.yfeng.payutil.paypwd.R;
import com.yfeng.payutil.paypwd.adapter.KeyAdapter;
import com.yfeng.payutil.paypwd.listener.OnPwdInputListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PasswordInputView implements View.OnClickListener {

    private Context context;
    private LinearLayout llyPwdInputView;
    private BottomSheetDialog payPwdDialog;

    private PasswordEditText etPwd; // 密码输入框
    private TextWatcher textWatcher;
    private GridView gvKeyboard; // 密码键盘
    private ArrayList<Map<String, String>> numList; // 数字按键序列
    private String password = ""; // 输入的密码
    private OnPwdInputListener onPwdInputListener;


    Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case Constants.KEYBOARD_INPUT:
                    int position = (int) msg.obj;
                    if (position < 11 && position != 9) {
                        // 点击0-9按键
                        password = etPwd.getText().append(numList.get(position).get("num")).toString();
                        etPwd.setText(password);
                    } else {
                        if (position == 11) {
                            // 点击退格键
                            if (!TextUtils.isEmpty(password) && !password.equals("")) {
                                password = etPwd.getText().delete(password.length() - 1, password.length()).toString();
                                etPwd.setText(password);
                            }
                        }
                    }
                    break;
            }
        }
    };

    public PasswordInputView(Context context) {
        this.context = context;
        payPwdDialog = new BottomSheetDialog(context);
        payPwdDialog.setCancelable(false);
        payPwdDialog.setCanceledOnTouchOutside(false);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_pay_pwd, null, false);
        initStep1(view);
        llyPwdInputView = (LinearLayout) view.findViewById(R.id.lly_pwd_input_view);
        showStep1();
    }

    /**
     * 初始化第一页
     * @param view
     */
    private void initStep1(View view) {
        view.findViewById(R.id.iv_close_dialog).setOnClickListener(this);
        etPwd = (PasswordEditText) view.findViewById(R.id.et_password_InputView);
        etPwd.setEnabled(false); // 设置输入框不可编辑，防止系统键盘弹出
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etPwd.getText().length() == 6) {
                    onPwdInputListener.onPwdInput(etPwd.getText().toString());
                }
            }
        };
        etPwd.addTextChangedListener(textWatcher);

        gvKeyboard = (GridView) view.findViewById(R.id.gv_keyboard);
        initKeyboard();

        payPwdDialog.setContentView(view);

    }


    /**
     * 初始化密码键盘
     */
    private void initKeyboard() {
        final int number = 10;
        int[] keys = new int[number];
        for (int i = 0; i < 10; i++) {
            keys[i] = i;
        }
        // 随机生成键盘数字
        Random random = new Random();
        for (int i = 0; i < number; i++) {
            int p = random.nextInt(number);
            int tmp = keys[i];
            keys[i] = keys[p];
            keys[p] = tmp;
        }

        numList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Map<String, String> map = new HashMap<>();
            if (i < 9) {
                map.put("num", String.valueOf(keys[i]));
            } else if (i == 9) {
                map.put("num", "");
            } else if (i == 10) {
                map.put("num", String.valueOf(keys[9]));
            } else if (i == 11) {
                map.put("num", "");
            }
            numList.add(map);
        }
        KeyAdapter keyAdapter = new KeyAdapter(context, numList, handler);
        gvKeyboard.setAdapter(keyAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close_dialog:
                if (payPwdDialog.isShowing()) {
                    payPwdDialog.dismiss(); // 关闭对话框
                }
                break;

        }
    }
    public void show() {
        payPwdDialog.show();
    }
    public void showStep1() {
        llyPwdInputView.setVisibility(View.VISIBLE);
    }

    public void showStep2() {
        llyPwdInputView.setVisibility(View.GONE);

    }

    public void setOnPwdInputListener(OnPwdInputListener listener) {
        this.onPwdInputListener = listener;
    }
}
