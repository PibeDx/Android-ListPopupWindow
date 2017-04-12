package com.josecuentas.android_listpopupwindow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListPopupWindow listPopupWindow;
    private EditText mEteProductName;
    String[] products={"Camera", "Laptop", "Watch","Smartphone",
            "Television"};

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEteProductName = (EditText) findViewById(R.id.product_name);
        mEteProductName.measure(0, 0);       //must call measure!
        //mEteProductName.getMeasuredHeight(); //get height
        //mEteProductName.getMeasuredWidth();  //get width
        popupWindow();
        mEteProductName.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listPopupWindow.show();
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DemoActivity.class));
            }
        });
    }

    private void popupWindow() {
        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(new ArrayAdapter(this, R.layout.list_item, products));
        listPopupWindow.setAnchorView(mEteProductName);
        listPopupWindow.setWidth(mEteProductName.getMeasuredWidth());
        listPopupWindow.setHeight(400);

        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(this);

    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mEteProductName.setText(products[position]);
        listPopupWindow.dismiss();
    }
}
