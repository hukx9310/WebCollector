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

import com.hukx.webcollect.presenter.WebRecord;
import com.hukx.webcollect.utils.UrlUtil;

/**
 * Created by hkx on 17-6-15.
 */

public class DetailFragment extends HandleBackFragment {

    String lastActionBatTitle;

    EditText urlText;
    EditText noteText;

    WebRecord showItem;

    HomeActivity homeActivity;

    MenuItem actionOK;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.detail_page, null);

        showItem = (WebRecord) getArguments().getSerializable("record");

        urlText = (EditText)v.findViewById(R.id.url_et);
        showItem.setCurrEditText(urlText);
        urlText.setEnabled(showItem.isUrlComplete());
        noteText = (EditText)v.findViewById(R.id.note_et);

        noteText.setHint("");

        urlText.setText(showItem.getURL());
        noteText.setText(showItem.getNote());

        TextWatcher urlTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actionOK.setVisible(!s.toString().equals(showItem.getURL()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        TextWatcher noteTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actionOK.setVisible(!s.toString().equals(showItem.getNote()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        urlText.addTextChangedListener(urlTextWatcher);
        noteText.addTextChangedListener(noteTextWatcher);

        homeActivity = (HomeActivity)getActivity();

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
        if(urlText.getText().toString().equals(showItem.getURL()) && noteText.getText().toString().equals(showItem.getNote())){
            return false;
        }
        if(ifInterceptBack()){
            showTextChangeDialog();
            return true;
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeActivity.showFloatingButton();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        actionOK = menu.findItem(R.id.action_changge_ok);
        actionOK.setVisible(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_detail, menu);
        lastActionBatTitle = homeActivity.getSupportActionBar().getTitle().toString();
        homeActivity.getSupportActionBar().setTitle(R.string.detail);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_changge_ok:
                ((InputMethodManager)homeActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(homeActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if(!urlText.getText().toString().equals(showItem.getURL())){
                    if(UrlUtil.isValidUrlPattern(urlText.getText().toString())) {
                        homeActivity.mPresenter.add(urlText.getText().toString(), noteText.getText().toString(), System.currentTimeMillis());
                        homeActivity.mPresenter.delete(showItem);
                        getFragmentManager().popBackStack();
                    } else {
                        showInvalidUrlDialog();
                    }
                } else {
                    showItem.setNote(noteText.getText().toString());
                    homeActivity.mPresenter.updateOnlyNote(showItem);
                    getFragmentManager().popBackStack();
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
                ((InputMethodManager)homeActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(homeActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if(!urlText.getText().toString().equals(showItem.getURL())){
                    if(UrlUtil.isValidUrlPattern(urlText.getText().toString())) {
                        showItem.setURL(urlText.getText().toString());
                        showItem.setNote(noteText.getText().toString());
                        homeActivity.mPresenter.update(showItem);
                        getFragmentManager().popBackStack();
                    } else {
                        showInvalidUrlDialog();
                    }
                } else {
                    showItem.setNote(noteText.getText().toString());
                    homeActivity.mPresenter.updateOnlyNote(showItem);
                    getFragmentManager().popBackStack();
                }
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
        invalidUrlDialog.setButton(AlertDialog.BUTTON_POSITIVE, this.getResources().getString(R.string.back) ,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ;
            }
        });
        invalidUrlDialog.show();
        invalidUrlDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this.getResources().getColor(R.color.colorPrimary));
    }

}
