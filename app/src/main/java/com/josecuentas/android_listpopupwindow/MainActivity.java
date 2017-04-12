package com.josecuentas.android_listpopupwindow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.view.WindowManager;
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
                listPopupWindow.setInputMethodMode(listPopupWindow.isShowing() ? ListPopupWindow.INPUT_METHOD_NEEDED : ListPopupWindow.INPUT_METHOD_NOT_NEEDED);

                listPopupWindow.show();
                listPopupWindow.getListView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DemoActivity.class));
            }
        });
    }

    private void popupWindow() {
        /*listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(new ArrayAdapter(this, R.layout.list_item, products));
        listPopupWindow.setAnchorView(mEteProductName);
        listPopupWindow.setWidth(mEteProductName.getMeasuredWidth());
        listPopupWindow.setHeight(400);

        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener(this);*/
        //
        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(new ArrayAdapter(this, R.layout.list_item, products));
        listPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        listPopupWindow.setAnchorView(mEteProductName);
        listPopupWindow.setOnItemClickListener(new DropDownItemClickListener());
        listPopupWindow.setModal(true);

        // For dropdown width, the developer can specify a specific width, or
        // MATCH_PARENT (for full screen width), or WRAP_CONTENT (to match the
        // width of the anchored view).

        listPopupWindow.setWidth(mEteProductName.getMeasuredWidth());
        listPopupWindow.setHeight(400);
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mEteProductName.setText(products[position]);
        listPopupWindow.dismiss();
    }

    private class DropDownItemClickListener implements AdapterView.OnItemClickListener {

        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mEteProductName.setText(products[position]);
            listPopupWindow.dismiss();
        }
    }
}
