/*
 * DocumentCardFragment.java
 * Laboratoire1
 *
 * Copyright (c) 2015. Philippe Lafontaine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.bleushan.laboratoire1.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bleushan.laboratoire1.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A {@link Fragment} subclass that represent a card view of a text document.
 * Use the {@link DocumentCardFragment#newInstance} factory method to create an instance of this
 * fragment.
 */
public class DocumentCardFragment extends Fragment implements OnClickListener, TextWatcher {

  /**
   * Request code constant that's passed for reading a document
   */
  public static final int READ_CODE = 42;
  /**
   * Request code constant that's passed for creating a document
   */
  public static final int CREATE_CODE = 69;
  private static final String ARG_INTENT = "ARG_INTENT";
  private static final String ARG_REQUEST_CODE = "ARG_REQUEST_CODE";
  private static final String TAG = DocumentCardFragment.class.getSimpleName();
  private EditText titleEditText;
  private EditText contentEditText;
  private Button saveButton;
  private InputStream documentIn;
  private OutputStream documentOut;

  public DocumentCardFragment() {
    // Required empty public constructor
  }

  /**
   * Instantiate a document card fragment.
   * <p/>
   * This method has a slightly peculiar parameters when compared to the usual fragment factory
   * method. It can be passed an {@link Intent} and an {@link Integer} representing a request code
   * and the {@link DocumentCardFragment#onCreate(Bundle)} method will send it along.
   * <p/>
   * Example usage:
   * <p/>
   * {@code
   * Intent intent = new Intent(Intent.CREATE_DOCUMENT);
   * intent.setTypeAndNormalize(MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt"));
   * intent.addCategory(Intent.CATEGORY_OPENABLE);
   * <p/>
   * DocumentCardFragment fragment =
   * DocumentCardFragment.newInstance(intent, DocumentCardFragment.CREATE_CODE);
   * getFragmentManager().beginTransaction().replace(R.id.main_placeholder,fragment).commit();
   * }
   *
   * @param intent
   *   The intent to be sent by the fragment. Can be null
   * @param requestCode
   *   The request code to be passed along the intent
   *
   * @return A shiny new {@link DocumentCardFragment}
   *
   * @see DocumentCardFragment#READ_CODE
   * @see DocumentCardFragment#CREATE_CODE
   */
  public static DocumentCardFragment newInstance(@Nullable Intent intent, int requestCode) {
    Bundle bundle = new Bundle();
    DocumentCardFragment fragment = new DocumentCardFragment();
    if (intent != null) {
      bundle.putParcelable(ARG_INTENT, intent);
    }
    bundle.putInt(ARG_REQUEST_CODE, requestCode);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle args = this.getArguments();
    if (args != null) {
      Intent intent = args.getParcelable(ARG_INTENT);
      int requestCode = args.getInt(ARG_REQUEST_CODE, 0);
      if (intent != null) {
        this.startActivityForResult(intent, requestCode);
      }
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_document_card, container, false);
    this.titleEditText = ((EditText) view.findViewById(R.id.document_title_edit_text));
    this.contentEditText = ((EditText) view.findViewById(R.id.document_content_edit_text));
    if (this.contentEditText != null) {
      this.contentEditText.addTextChangedListener(this);
    }
    this.saveButton = ((Button) view.findViewById(R.id.document_save));
    if (this.saveButton != null) {
      this.saveButton.setOnClickListener(this);
    }
    return view;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.document_save:
        if (this.contentEditText != null) {
          try {
            this.documentOut.write(this.contentEditText.getText().toString().getBytes());
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        // Because, it's presented as a card, we remove the fragment from view.
        this.getFragmentManager()
            .beginTransaction()
            .remove(this)
            .commit();
        break;
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    try {
      if (this.documentOut != null) {
        this.documentOut.flush();
        this.documentOut.close();
      }
      if (this.documentIn != null) {
        this.documentIn.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Activity activity = this.getActivity();
    if ((resultCode == Activity.RESULT_OK) && (activity != null)) {
      Uri uri = data.getData();
      if (uri != null) {
        if (this.titleEditText != null) {
          String title = uri.getLastPathSegment();
          title = title.substring(title.lastIndexOf("/") + 1);
          this.titleEditText.setText(title);
        }
        String mode = (requestCode == CREATE_CODE) ? "rwt" : "rw";
        try {
          if (requestCode == READ_CODE) {
            this.documentIn = activity.getContentResolver().openInputStream(uri);
          }
          this.documentOut = activity.getContentResolver().openOutputStream(uri, mode);
          if ((this.documentIn != null) && (this.contentEditText != null)) {
            byte buffer[] = new byte[this.documentIn.available()];
            while (this.documentIn.read(buffer) != -1) {
              for (byte b : buffer) {
                this.contentEditText.append(String.valueOf((char) b));
              }
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
  }

  @Override
  public void afterTextChanged(Editable s) {
    if (this.saveButton != null) {
      this.saveButton.setEnabled(!s.toString().isEmpty());
    }
  }
}
