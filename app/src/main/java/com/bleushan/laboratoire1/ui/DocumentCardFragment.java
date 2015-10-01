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
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.bleushan.laboratoire1.BuildConfig;
import com.bleushan.laboratoire1.R;
import com.bleushan.laboratoire1.utils.MemoryUnit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Calendar;

/**
 * A {@link Fragment} subclass that represent a card view of a text document.
 * Use the {@link DocumentCardFragment#newInstance} factory method to create an instance of this
 * fragment.
 */
public class DocumentCardFragment extends Fragment implements OnClickListener, TextWatcher,
                                                              OnEditorActionListener {

  /**
   * Request code constant that's passed for reading a document
   */
  public static final int READ_CODE = 42;
  /**
   * Request code constant that's passed for creating a document
   */
  public static final int CREATE_CODE = 69;
  private static final String ARG_REQUEST_CODE = "ARG_REQUEST_CODE";
  private static final String TAG = DocumentCardFragment.class.getSimpleName();
  private final File documentsDir =
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
  /**
   * The charset used to read or write files
   */
  private final Charset charset = Charset.defaultCharset();
  private EditText titleEditText;
  private EditText contentEditText;
  private String initialFileContent = "";
  private Button saveButton;
  private Uri fileUri;
  private boolean hasDocumentDir = false;
  private boolean isLoadingDocument = false;

  public DocumentCardFragment() {
    // Required empty public constructor
  }

  /**
   * Instantiate a document card fragment.
   * <p/>
   * This method has a slightly peculiar parameters when compared to the usual fragment factory
   * method. It can be passed an {@link Integer} representing a request code for an document action
   * and the fragment will manage sending the {@link Intent} to the {@link ContentResolver}
   * accordingly.
   *
   * @param requestCode
   *   The request code to be passed along the intent
   *
   * @return A shiny new {@link DocumentCardFragment}
   *
   * @see DocumentCardFragment#READ_CODE
   * @see DocumentCardFragment#CREATE_CODE
   */
  public static DocumentCardFragment newInstance(int requestCode) {
    Bundle bundle = new Bundle();
    DocumentCardFragment fragment = new DocumentCardFragment();
    bundle.putInt(ARG_REQUEST_CODE, requestCode);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.hasDocumentDir = (this.documentsDir.mkdirs() || this.documentsDir.isDirectory());
    Bundle args = this.getArguments();
    if (args != null) {
      int requestCode = args.getInt(ARG_REQUEST_CODE, 0);
      if (requestCode == READ_CODE) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // This sets the intent so that the document picker shows only *.txt files.
        intent.setTypeAndNormalize(MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt"));
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
    if (this.titleEditText != null) {
      this.titleEditText.setOnEditorActionListener(this);
    }
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
        if ((this.contentEditText != null) &&
            (this.titleEditText != null)) {
          String fileName = this.titleEditText.getText().toString();
          Activity activity = this.getActivity();
          if (activity != null) {
            try (OutputStream out = activity.getContentResolver().openOutputStream(this.fileUri)) {
              if (out != null) {
                //
                if (this.fileUri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
                  this.writeLog(fileName, "File created");
                }
                // Write the text box content to the file with the current fragment charset.
                out.write(this.contentEditText.getText()
                                              .toString()
                                              .getBytes(this.charset));
                this.writeLog(fileName, "File saved");
              }
            } catch (IOException e) {
              e.printStackTrace();
            } finally {
              this.writeLog(fileName, "File closed");
            }
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
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Activity activity = this.getActivity();
    if ((resultCode == Activity.RESULT_OK) && (activity != null)) {
      this.fileUri = data.getData();
      if (this.fileUri != null) {
        if (this.titleEditText != null) {
          String fileName = this.fileUri.getLastPathSegment();
          fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
          this.titleEditText.setText(fileName);
          String message = null;
          if (requestCode == READ_CODE) {
            message = "File opened";
          } else if (requestCode == CREATE_CODE) {
            // This is stub/dead code. If we ever decide to use ACTION_CREATE_DOCUMENT intent.
            message = "File created";
          }
          if (message != null) {
            this.writeLog(fileName, message);
          }
        }
        // Open a file descriptor to the Uri.
        // Because we target KitKat or higher and we compile with Java 7, we can use
        // try-with-resource statements. This will close the resource automatically.
        this.isLoadingDocument = true;
        try (ParcelFileDescriptor pfd =
               activity.getContentResolver().openFileDescriptor(this.fileUri, "r")) {
          if ((pfd != null) && (this.contentEditText != null)) {
            // Compute the file size.
            long size = pfd.getStatSize();
            // Check if the file descriptor is actually a file. if it's not, value is -1
            if (size != -1) {
              // Bind the file descriptor to a MappedByteBuffer. Because we open the file in
              // read-only mode we cannot quite the take full advantage of the MappedByteBuffer as a
              // memory mapped I/O mechanism.
              MappedByteBuffer buffer =
                new FileInputStream(pfd.getFileDescriptor()).getChannel()
                                                            .map(MapMode.READ_ONLY, 0, size);
              // Load the entire file into memory.
              // It will crash and burn if the file is bigger that the available memory.
              buffer.load();
              if (buffer.isLoaded()) {
                // Try to decode the buffer with the current fragment charset encoding if it fail
                // remove the fragment and show a snackbar.
                // TODO: Support more charset.
                try {
                  this.initialFileContent = this.charset.newDecoder().decode(buffer).toString();
                  this.contentEditText.setText(this.initialFileContent);
                  this.contentEditText.requestFocus();
                  this.contentEditText.setSelection(this.initialFileContent.length());
                } catch (CharacterCodingException e) {
                  View view = this.getView();
                  if (view != null) {
                    Snackbar.make(view, R.string.app_error_fileread, Snackbar.LENGTH_SHORT)
                            .show();
                  }
                  this.getFragmentManager().beginTransaction().remove(this).commit();
                }
              }
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
          View view = this.getView();
          if (view != null) {
            Snackbar.make(view, R.string.app_error_fileread, Snackbar.LENGTH_SHORT)
                    .show();
          }
          this.getFragmentManager().beginTransaction().remove(this).commit();
        }
        this.isLoadingDocument = false;
      }
    } else if (resultCode == Activity.RESULT_CANCELED) {
      // Remove the fragment.
      // Things got canceled so there's nothing to show.
      this.getFragmentManager()
          .beginTransaction()
          .remove(this)
          .commit();
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
    if (this.initialFileContent != null) {
      if (s.toString().compareToIgnoreCase(this.initialFileContent) != 0) {
        if (this.saveButton != null) {
          this.saveButton.setEnabled(!s.toString().isEmpty());
        }
        // Check if we have both a textbox to pull data from and if we're not loading from a file
        // before writing to the log.
        if ((this.titleEditText != null) && (!this.isLoadingDocument)) {
          this.writeLog(this.titleEditText.getText().toString(), "File modified");
        }
      }
    }
  }

  /**
   * Helper method to log file operation to a log file.
   *
   * @param filename
   *   The name of the file that's been operated on
   * @param message
   *   The message to add to the log.
   */
  private void writeLog(@NonNull String filename, @NonNull String message) {
    Context cxt = this.getActivity();
    if (cxt != null) {
      File logFile = new File(cxt.getCacheDir().getPath(), "log.txt");
      if (logFile.canWrite()) {
        // This is to ensure that the log file is never bigger than 1 MB
        boolean writeNew = (logFile.length() <= MemoryUnit.MEGABYTE.toByte(1));
        try (FileOutputStream logOutputStream =
               new FileOutputStream(logFile, writeNew)) {
          String logMessage = Calendar.getInstance().getTime().toString() + " " + filename + ": " +
                              message + "\n";
          if (BuildConfig.DEBUG) {
            Log.d(TAG, logMessage);
          }
          logOutputStream.write(logMessage.getBytes());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    if ((v.getId() == R.id.document_title_edit_text) &&
        (((actionId == EditorInfo.IME_ACTION_NEXT) ||
          (actionId == EditorInfo.IME_ACTION_DONE)) ||
         ((event != null) &&
          (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)))) {
      StringBuilder textBuilder = new StringBuilder(v.getText());
      if (textBuilder.lastIndexOf(".txt") == -1) {
        textBuilder.append(".txt");
      }
      if ((this.hasDocumentDir) && (this.fileUri == null)) {
        this.fileUri = Uri.fromFile(new File(this.documentsDir, textBuilder.toString()));
      }
      v.setText(textBuilder.toString());
    }
    // always return false so the default action works out.
    return false;
  }

}
