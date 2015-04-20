package com.road_sidekiq.android.roadsidekiq.widgets;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.road_sidekiq.android.roadsidekiq.R;


/**
 * Search field but with customizable hint
 */
public class SearchWidget extends FrameLayout {
    private EditText searchField;
    private TextView searchPseudoHint;
    private OnSearchFieldChangedListener listener;
    private ImageView clearSearchFieldButton;
    private InputMethodManager in;

    public SearchWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWidget(context);
    }

    public SearchWidget(Context context) {
        super(context);
        initWidget(context);
    }

    public interface OnSearchFieldChangedListener {
        public void onStartSearch(String searchKeyword);
        public void onSearchFieldCleared();
    }

    private void initWidget(Context context) {
        inflate(context, R.layout.widget_search, this);
        searchField         =   (EditText) findViewById(R.id.searchField);
        searchPseudoHint    =   (TextView) findViewById(R.id.searchPseudoHint);
        clearSearchFieldButton = (ImageView) findViewById(R.id.clearSearchFieldButton);
        in                  =   (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    searchPseudoHint.setVisibility(View.GONE);
                    clearSearchFieldButton.setVisibility(View.VISIBLE);
                } else {
                    searchPseudoHint.setVisibility(View.VISIBLE);
                    clearSearchFieldButton.setVisibility(View.GONE);
                    if (listener != null)
                        listener.onSearchFieldCleared();
                }
            }
        });
        clearSearchFieldButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearchString();
            }
        });
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (listener != null) {
                        in.hideSoftInputFromWindow(searchField.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        String searchString = v.getText().toString();
                        if (!TextUtils.isEmpty(searchString))
                            listener.onStartSearch(searchString);
                    }
                    handled = true;
                }
                return handled;
            }
        });
    }

    public void setOnStartSearchListener(OnSearchFieldChangedListener listener) {
        this.listener = listener;
    }

    public void setSearchString(String searchString) {
        searchField.setText(searchString);
        searchPseudoHint.setVisibility(View.GONE);
        clearSearchFieldButton.setVisibility(View.VISIBLE);
        // listener.onStartSearch(searchString);
    }

    public void clearSearchString() {
        searchField.setText("");
        searchPseudoHint.setVisibility(View.VISIBLE);
        clearSearchFieldButton.setVisibility(View.GONE);
        if (listener != null)
            listener.onSearchFieldCleared();
    }
}
