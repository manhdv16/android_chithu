package com.example.quanlichitieu.Utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MoneyTextWatcher implements TextWatcher {
    private EditText editText;

    public MoneyTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        editText.removeTextChangedListener(this);
        try {
            String originalString = s.toString();
            // Xóa tất cả các dấu phân cách hiện có
            String cleanString = originalString.replaceAll("[,.]", "");
            // Định dạng lại chuỗi số tiền
            double parsed = Double.parseDouble(cleanString);
            String formatted = String.format("%,.0f", parsed);

            editText.setText(formatted);
            editText.setSelection(formatted.length());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        editText.addTextChangedListener(this);
    }
}

