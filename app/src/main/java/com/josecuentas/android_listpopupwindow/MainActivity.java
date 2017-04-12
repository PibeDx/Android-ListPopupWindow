package com.josecuentas.android_listpopupwindow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, Filter.FilterListener {

    private ListPopupWindow listPopupWindow;
    private EditText mEteProductName;
    String[] products={"Camera", "Laptop", "Watch","Smartphone", "Television"};
    private Filter mFilter;
    private ListAdapter mAdapter;
    private int mThreshold = 1;
    private boolean mPopupCanBeUpdated = true;
    private boolean mBlockCompletion;
    private boolean mOpenBefore;
    private int mLastKeyCode = KeyEvent.KEYCODE_UNKNOWN;

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
                if (listPopupWindow.isShowing()) {
                    listPopupWindow.setInputMethodMode(true ? ListPopupWindow.INPUT_METHOD_NEEDED : ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
                }

                if (listPopupWindow.isDropDownAlwaysVisible() || (mFilter != null && enoughToFilter())) {
                    showDropDown();
                }

            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DemoActivity.class));
            }
        });

        mEteProductName.addTextChangedListener(new MyWatcher());
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

        mAdapter = new ArrayAdapter(this, R.layout.list_item, products);
        mFilter = ((Filterable) mAdapter).getFilter();
        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(mAdapter);
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

    private class MyWatcher implements TextWatcher {
        public void afterTextChanged(Editable s) {
            doAfterTextChanged();
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            doBeforeTextChanged();
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    void doBeforeTextChanged() {
        if (mBlockCompletion) return;

        // when text is changed, inserted or deleted, we attempt to show
        // the drop down
        mOpenBefore = listPopupWindow.isShowing();
    }

    void doAfterTextChanged() {
        if (mBlockCompletion) return;

        // if the list was open before the keystroke, but closed afterwards,
        // then something in the keystroke processing (an input filter perhaps)
        // called performCompletion() and we shouldn't do any more processing.
        if (mOpenBefore && !listPopupWindow.isShowing()) {
            return;
        }

        // the drop down is shown only when a minimum number of characters
        // was typed in the text view
        if (enoughToFilter()) {
            if (mFilter != null) {
                mPopupCanBeUpdated = true;
                performFiltering(mEteProductName.getText(), mLastKeyCode);
            }
        } else {
            // drop down is automatically dismissed when enough characters
            // are deleted from the text view
            if (!listPopupWindow.isDropDownAlwaysVisible()) {
                dismissDropDown();
            }
            if (mFilter != null) {
                mFilter.filter(null);
            }
        }
    }

    protected void performFiltering(CharSequence text, int keyCode) {
        mFilter.filter(text, this);
    }

    @Override public void onFilterComplete(int count) {
        updateDropDownForFilter(count);
    }

    private void updateDropDownForFilter(int count) {

        /*
         * This checks enoughToFilter() again because filtering requests
         * are asynchronous, so the result may come back after enough text
         * has since been deleted to make it no longer appropriate
         * to filter.
         */

        final boolean dropDownAlwaysVisible = listPopupWindow.isDropDownAlwaysVisible();
        final boolean enoughToFilter = enoughToFilter();
        if ((count > 0 || dropDownAlwaysVisible) && enoughToFilter) {
            if (mEteProductName.hasFocus() && mEteProductName.hasWindowFocus() && mPopupCanBeUpdated) {
                showDropDown();
            }
        } else if (!dropDownAlwaysVisible && listPopupWindow.isShowing()) {
            dismissDropDown();
            // When the filter text is changed, the first update from the adapter may show an empty
            // count (when the query is being performed on the network). Future updates when some
            // content has been retrieved should still be able to update the list.
            mPopupCanBeUpdated = true;
        }
    }

    public void showDropDown() {
        buildImeCompletions();

        /*if (listPopupWindow.getAnchorView() == null) {
            if (mDropDownAnchorId != View.NO_ID) {
                listPopupWindow.setAnchorView(getRootView().findViewById(mDropDownAnchorId));
            } else {
                listPopupWindow.setAnchorView(this);
            }
        }*/
        if (!listPopupWindow.isShowing()) {
            // Make sure the list does not obscure the IME when shown for the first time.
            listPopupWindow.setInputMethodMode(android.widget.ListPopupWindow.INPUT_METHOD_NEEDED);
            //listPopupWindow.setListItemExpandMax(EXPAND_MAX);
        }
        listPopupWindow.show();
        listPopupWindow.getListView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
    }

    public void dismissDropDown() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.displayCompletions(mEteProductName, null);
        }
        listPopupWindow.dismiss();
        mPopupCanBeUpdated = false;
    }

    private void buildImeCompletions() {
        final ListAdapter adapter = mAdapter;
        if (adapter != null) {
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                final int count = Math.min(adapter.getCount(), 20);
                CompletionInfo[] completions = new CompletionInfo[count];
                int realCount = 0;

                for (int i = 0; i < count; i++) {
                    if (adapter.isEnabled(i)) {
                        Object item = adapter.getItem(i);
                        long id = adapter.getItemId(i);
                        completions[realCount] = new CompletionInfo(id, realCount, convertSelectionToString(item));
                        realCount++;
                    }
                }

                if (realCount != count) {
                    CompletionInfo[] tmp = new CompletionInfo[realCount];
                    System.arraycopy(completions, 0, tmp, 0, realCount);
                    completions = tmp;
                }

                imm.displayCompletions(mEteProductName, completions);
            }
        }
    }

    public boolean enoughToFilter() {
        return mEteProductName.getText().length() >= mThreshold;
    }

    protected CharSequence convertSelectionToString(Object selectedItem) {
        return mFilter.convertResultToString(selectedItem);
    }
}
