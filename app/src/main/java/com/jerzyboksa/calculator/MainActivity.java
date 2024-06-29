package com.jerzyboksa.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText resultAndInputTextView;
    private EditText operandHolderTextView;

    private Double firstOperand;
    private Double secondOperand;
    private Operation operation;

    private final Button[] numButtons = new Button[11];

    private Button addButton;
    private Button substractButton;
    private Button equalButton;
    private Button multiplyButton;
    private Button divideButton;
    private Button clearButton;
    private Button removeButton;
    private Button invertButton;
    private Button changeSignButton;

    private static final String OPERATION = "operation";
    private static final String STATE_FIRST_OPERAND = "firstOperand";
    private static final String STATE_SECOND_OPERAND = "secondOperand";


    private StringBuilder userInput = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultAndInputTextView = findViewById(R.id.resultAndInputTextView);
        operandHolderTextView = findViewById(R.id.pendingNumberTextView);

        initFunctionButtons();
        setOperationsButtonsListener();
        setFunctionButtonsListener();
        initNumButtons();
        setNumButtonsListener();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(OPERATION, operation.toString());
        outState.putString(STATE_FIRST_OPERAND, String.valueOf(firstOperand));
        outState.putString(STATE_SECOND_OPERAND, String.valueOf(secondOperand));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        operation = Operation.valueOf(savedInstanceState.getString(OPERATION));
        firstOperand = Double.parseDouble(savedInstanceState.getString(STATE_FIRST_OPERAND));
        secondOperand = Double.parseDouble(savedInstanceState.getString(STATE_SECOND_OPERAND));

    }

    private void initFunctionButtons() {
        addButton = findViewById(R.id.addButton);
        substractButton = findViewById(R.id.substractButton);
        equalButton = findViewById(R.id.equalButton);
        multiplyButton = findViewById(R.id.multiplyButton);
        divideButton = findViewById(R.id.divideButton);
        clearButton = findViewById(R.id.clearButton);
        removeButton = findViewById(R.id.removeButton);
        invertButton = findViewById(R.id.invertButton);
        changeSignButton = findViewById(R.id.changeSignButton);
    }

    private void initNumButtons() {
        numButtons[0] = findViewById(R.id.button0);
        numButtons[1] = findViewById(R.id.button1);
        numButtons[2] = findViewById(R.id.button2);
        numButtons[3] = findViewById(R.id.button3);
        numButtons[4] = findViewById(R.id.button4);
        numButtons[5] = findViewById(R.id.button5);
        numButtons[6] = findViewById(R.id.button6);
        numButtons[7] = findViewById(R.id.button7);
        numButtons[8] = findViewById(R.id.button8);
        numButtons[9] = findViewById(R.id.button9);
        numButtons[10] = findViewById(R.id.dotButton);
    }

    private void setNumButtonsListener() {
        for (int i = 0; i < numButtons.length - 1; i++) {
            int finalI = i;
            numButtons[i].setOnClickListener(l -> onNumButtonListener(String.valueOf(finalI)));
        }
        numButtons[10].setOnClickListener(l -> onDotButtonListener());
    }

    private void onNumButtonListener(String digitToAppend) {
        if (userInput.toString().equals("0")) {
            userInput = new StringBuilder();
        }
        userInput.append(digitToAppend);
        resultAndInputTextView.setText(userInput.toString());
    }

    private void onDotButtonListener() {
        if (!userInput.toString().contains(".")) {
            onNumButtonListener(".");
        }
    }

    private void setOperationsButtonsListener() {
        addButton.setOnClickListener(l -> {
            operation = Operation.ADD;
            setFirstOperandAndDisplay();
        });

        substractButton.setOnClickListener(l -> {
            operation = Operation.SUBSTRACT;
            setFirstOperandAndDisplay();
        });

        multiplyButton.setOnClickListener(l -> {
            operation = Operation.MULTIPLY;
            setFirstOperandAndDisplay();
        });

        divideButton.setOnClickListener(l -> {
            operation = Operation.DIVIDE;
            setFirstOperandAndDisplay();
        });
    }

    private void setFirstOperandAndDisplay() {
        String operandHolder = userInput.toString();
        if (operandHolder.isBlank()) {
            String currentOperandHolderText = operandHolderTextView.getText().toString();
            if (!currentOperandHolderText.isBlank()) {
                operandHolder = currentOperandHolderText.substring(0, currentOperandHolderText.length() - 1);
            } else {
                return;
            }
        }
        firstOperand = Double.parseDouble(operandHolder);

        switch (operation) {
            case ADD -> operandHolder += "+";
            case SUBSTRACT -> operandHolder += "-";
            case MULTIPLY -> operandHolder += "x";
            case DIVIDE -> operandHolder += "/";
        }
        operandHolderTextView.setText(operandHolder);
        userInput = new StringBuilder();
        updateResultAndInputTextView();
    }

    private void setFunctionButtonsListener() {
        clearButton.setOnClickListener(l -> clearAll());
        removeButton.setOnClickListener(l -> deleteLastDigit());
        changeSignButton.setOnClickListener(l -> changeSign());
        invertButton.setOnClickListener(l -> invertNumber());
        equalButton.setOnClickListener(l -> calculate());
    }

    private void clearAll() {
        userInput = new StringBuilder();
        operandHolderTextView.setText("");
        resultAndInputTextView.setText("");
    }

    private void deleteLastDigit() {
        if (userInput.length() == 0) {
            return;
        }
        if (userInput.length() == 2 && userInput.charAt(0) == '-') {
            userInput.deleteCharAt(0);
        }
        userInput.deleteCharAt(userInput.length() - 1);
        updateResultAndInputTextView();
    }


    private void changeSign() {
        if (userInput.length() == 0) {
            return;
        }

        char firstChar = userInput.charAt(0);
        if (firstChar == '-') {
            userInput.deleteCharAt(0);
        } else {
            userInput.insert(0, '-');
        }
        updateResultAndInputTextView();
    }


    private void invertNumber() {
        if (userInput.length() == 0) {
            return;
        }
        double toInvert = Double.parseDouble(userInput.toString());
        if (toInvert != 0) {
            toInvert = roundNumber(1 / toInvert);
            userInput = new StringBuilder(Double.toString(toInvert));
        }
        deleteLastZeros();
        updateResultAndInputTextView();
    }

    private Double roundNumber(Double number) {
        BigDecimal bd = BigDecimal.valueOf(number);
        bd = bd.setScale(5, RoundingMode.HALF_UP);
        double roundedNumber = bd.doubleValue();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat scientificFormat = new DecimalFormat("0.#####E0", symbols);

        if (Math.abs(roundedNumber) >= 1e6 || Math.abs(roundedNumber) <= 1e-6) {
            return Double.parseDouble(scientificFormat.format(roundedNumber));
        } else {
            return roundedNumber;
        }
    }

    private void updateResultAndInputTextView() {
        String resultText = userInput.toString();
        resultAndInputTextView.setText(resultText);
    }

    private void calculate() {
        String firstOperandText = operandHolderTextView.getText().toString();
        if (userInput.toString().isBlank() || firstOperandText.isBlank()) {
            return;
        }
        firstOperand = Double.valueOf(firstOperandText.substring(0, firstOperandText.length() - 1));
        secondOperand = Double.parseDouble(userInput.toString());
        Double result = roundNumber(proceedOperation());
        operandHolderTextView.setText("");
        userInput = new StringBuilder(result.toString());
        deleteLastZeros();
        updateResultAndInputTextView();
    }

    private void deleteLastZeros() {
        String resultText = userInput.toString();

        double numb = Double.parseDouble(resultText);
        if (numb == Math.round(numb)) {
            resultText = userInput.substring(0, userInput.length() - 2);
            userInput = new StringBuilder(resultText);
        }
    }

    private Double proceedOperation() {
        switch (operation) {
            case ADD -> {
                return firstOperand + secondOperand;
            }
            case SUBSTRACT -> {
                return firstOperand - secondOperand;
            }
            case MULTIPLY -> {
                return firstOperand * secondOperand;
            }
            case DIVIDE -> {
                if (secondOperand != 0) {
                    return firstOperand / secondOperand;
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "NO DIVISION BY 0", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        }
        return 0.0;

    }


}
