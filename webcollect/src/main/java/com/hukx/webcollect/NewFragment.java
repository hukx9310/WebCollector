package com.hukx.webcollect;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.hukx.webcollect.utils.UrlUtil;

/**
 * Created by hkx on 17-3-18.
 */

public class NewFragment extends HandleBackFragment {

    String lastActionBatTitle;

    EditText urlText;
    EditText noteText;

    HomeActivity homeActivity;

    MenuItem actionOK;

    public NewFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.detail_page,container,false);
        noteText = (EditText)v.findViewById(R.id.note_et);
        urlText = (EditText)v.findViewById(R.id.url_et);

        homeActivity = (HomeActivity) getActivity();

        TextWatcher urlTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actionOK.setVisible(!s.toString().equals(""));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        urlText.addTextChangedListener(urlTextWatcher);


        return v;
    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if(enter){
            return AnimationUtils.loadAnimation(homeActivity, R.anim.frag_in);
        } else {
            return AnimationUtils.loadAnimation(homeActivity, R.anim.frag_out);
        }
    }

    @Override
    public boolean onBackPressed() {

        if(urlText.getText().toString().equals("") && noteText.getText().toString().equals("")){
            return false;
        }
        if(ifInterceptBack()){
            showTextChangeDialog();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homeActivity.showFloatingButton();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add,menu);
        lastActionBatTitle = homeActivity.getSupportActionBar().getTitle().toString();
        homeActivity.getSupportActionBar().setTitle(R.string.new_record);
        actionOK = menu.findItem(R.id.action_ok);
        actionOK.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_ok:
                ((InputMethodManager) homeActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(homeActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if(UrlUtil.isValidUrlPattern(urlText.getText().toString())) {
                    homeActivity.mPresenter.add(urlText.getText().toString(), noteText.getText().toString(), System.currentTimeMillis());
                    getFragmentManager().popBackStack();
                } else {
                    showInvalidUrlDialog();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        homeActivity.getSupportActionBar().setTitle(lastActionBatTitle);
    }

    private void showTextChangeDialog(){
        AlertDialog textChangeDialog;
        textChangeDialog = new AlertDialog.Builder(homeActivity).create();

        textChangeDialog.setMessage(this.getResources().getString(R.string.if_save));
        textChangeDialog.setButton(AlertDialog.BUTTON_POSITIVE, this.getResources().getString(R.string.yes) ,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((InputMethodManager) homeActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(homeActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                homeActivity.mPresenter.add(urlText.getText().toString(), noteText.getText().toString(), System.currentTimeMillis());
                getFragmentManager().popBackStack();
            }
        });
        textChangeDialog.setButton(AlertDialog.BUTTON_NEGATIVE, this.getResources().getString(R.string.no) ,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getFragmentManager().popBackStack();
            }
        });
        textChangeDialog.show();
    }

    private void showInvalidUrlDialog(){
        AlertDialog invalidUrlDialog;
        invalidUrlDialog = new AlertDialog.Builder(homeActivity).create();

        invalidUrlDialog.setMessage(this.getResources().getString(R.string.invalid_url));
        invalidUrlDialog.setButton(AlertDialog.BUTTON_NEUTRAL, this.getResources().getString(R.string.back) ,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ;
            }
        });
        invalidUrlDialog.show();
        invalidUrlDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(this.getResources().getColor(R.color.colorPrimary));
    }
}
