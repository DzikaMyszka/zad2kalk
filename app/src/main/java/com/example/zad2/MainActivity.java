package com.example.zad2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private Double firstOperand = null;
    private String pendingOperation = null;
    private boolean isNewInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDisplay = findViewById(R.id.tvDisplay);

        setupDigitButtons();
        setupOperatorButtons();
        setupActionButtons();
    }

    private void setupDigitButtons() {
        int[] digitButtonIds = new int[]{
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        View.OnClickListener listener = v -> {
            Button button = (Button) v;
            String digit = button.getText().toString();
            onDigitClicked(digit);
        };

        for (int id : digitButtonIds) {
            Button btn = findViewById(id);
            if (btn != null) {
                btn.setOnClickListener(listener);
            }
        }
    }

    private void onDigitClicked(String digit) {
        String currentText = tvDisplay.getText().toString();
        if (isNewInput || currentText.equals("0") || currentText.equals("Błąd")) {
            tvDisplay.setText(digit);
            isNewInput = false;
        } else {
            tvDisplay.setText(currentText + digit);
        }
    }

    private void setupOperatorButtons() {
        findViewById(R.id.btnDodaj).setOnClickListener(v -> onOperatorClicked("+"));
        findViewById(R.id.btnOdejmij).setOnClickListener(v -> onOperatorClicked("-"));
        findViewById(R.id.btnMnozenie).setOnClickListener(v -> onOperatorClicked("*"));
        findViewById(R.id.btnDzielenie).setOnClickListener(v -> onOperatorClicked("/"));
    }

    private void onOperatorClicked(String operator) {
        String currentText = tvDisplay.getText().toString();
        if (currentText.equals("Błąd")) {
            return;
        }

        double currentValue = parseDisplayValue(currentText);

        if (firstOperand != null && !isNewInput && pendingOperation != null) {
            double result = calculate(firstOperand, currentValue, pendingOperation);
            if (Double.isInfinite(result) || Double.isNaN(result)) {
                tvDisplay.setText("Błąd");
                firstOperand = null;
                pendingOperation = null;
                isNewInput = true;
                return;
            }
            firstOperand = result;
            tvDisplay.setText(formatResult(result));
        } else {
            firstOperand = currentValue;
        }

        pendingOperation = operator;
        isNewInput = true;
    }

    private void setupActionButtons() {
        findViewById(R.id.btncomma).setOnClickListener(v -> onCommaClicked());
        findViewById(R.id.btnequal).setOnClickListener(v -> onEqualClicked());
        findViewById(R.id.btnclear).setOnClickListener(v -> onClearClicked());
    }

    private void onCommaClicked() {
        String currentText = tvDisplay.getText().toString();
        if (isNewInput || currentText.equals("Błąd")) {
            tvDisplay.setText("0,");
            isNewInput = false;
        } else if (!currentText.contains(",") && !currentText.contains(".")) {
            tvDisplay.setText(currentText + ",");
        }
    }

    private void onEqualClicked() {
        String currentText = tvDisplay.getText().toString();
        if (firstOperand == null || pendingOperation == null || currentText.equals("Błąd")) {
            return;
        }

        double secondOperand = parseDisplayValue(currentText);

        if (pendingOperation.equals("/") && secondOperand == 0) {
            tvDisplay.setText("Błąd");
            firstOperand = null;
            pendingOperation = null;
            isNewInput = true;
            return;
        }

        double result = calculate(firstOperand, secondOperand, pendingOperation);
        if (Double.isInfinite(result) || Double.isNaN(result)) {
            tvDisplay.setText("Błąd");
        } else {
            tvDisplay.setText(formatResult(result));
        }

        firstOperand = null;
        pendingOperation = null;
        isNewInput = true;
    }

    private void onClearClicked() {
        tvDisplay.setText("0");
        firstOperand = null;
        pendingOperation = null;
        isNewInput = true;
    }

    private double calculate(double operand1, double operand2, String op) {
        switch (op) {
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                if (operand2 == 0) {
                    return Double.NaN;
                }
                return operand1 / operand2;
            default:
                return operand2;
        }
    }

    private double parseDisplayValue(String text) {
        try {
            String normalized = text.replace(',', '.');
            return Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String formatResult(double value) {
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            return "Błąd";
        }
        long longVal = (long) value;
        if (value == longVal) {
            return String.valueOf(longVal);
        } else {
            String str = String.valueOf(value);
            if (str.contains(".")) {
                str = str.replaceAll("0+$", "").replaceAll("\\.$", "");
            }
            return str.replace('.', ',');
        }
    }
}