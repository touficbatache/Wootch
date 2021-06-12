package com.batache.wootch.app.home.stream;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.batache.wootch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadingBeamingDialogFragment extends DialogFragment {

  private DialogInterface.OnCancelListener mOnCancelListener;

  @BindView(R.id.progress_textview)
  TextView mTextView;

  public static LoadingBeamingDialogFragment newInstance() {
    return new LoadingBeamingDialogFragment();
  }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   * on create view
   * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = LayoutInflater.from(new ContextThemeWrapper(getActivity(), R.style.Theme_Wootch)).inflate(R.layout
        .fragment_loading_detail, container, false);
    ButterKnife.bind(this, v);
    mTextView.setText(R.string.starting_beam);
    return v;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_FRAME, R.style.Theme_Dialog_Transparent);
  }

  @Override
  public void onCancel(DialogInterface dialog) {
    if (mOnCancelListener != null)
      mOnCancelListener.onCancel(dialog);
  }

  public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
    mOnCancelListener = onCancelListener;
  }

}
