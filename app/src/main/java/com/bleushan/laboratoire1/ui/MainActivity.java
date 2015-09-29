/*
 * MainActivity.java
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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;

import com.bleushan.laboratoire1.R;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.activity_main);
    Toolbar toolbar = ((Toolbar) this.findViewById(R.id.main_toolbar));
    if (toolbar != null) {
      this.setSupportActionBar(toolbar);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    this.getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    Toolbar toolbar = ((Toolbar) this.findViewById(R.id.main_toolbar));
    Intent intent = new Intent();
    // This sets the intent so that the document picker shows only *.txt files.
    intent.setTypeAndNormalize(MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt"));
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    switch (item.getItemId()) {
      case R.id.action_newFile:
        intent.setAction(Intent.ACTION_CREATE_DOCUMENT);
        this.getFragmentManager()
            .beginTransaction()
            .replace(R.id.main_placeholder,
                     DocumentCardFragment.newInstance(intent, DocumentCardFragment.CREATE_CODE))
            .commit();
        return true;
      case R.id.action_openFile:
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        this.getFragmentManager()
            .beginTransaction()
            .replace(R.id.main_placeholder,
                     DocumentCardFragment.newInstance(intent, DocumentCardFragment.READ_CODE))
            .commit();
        return true;
      case R.id.action_settings:
        if (toolbar != null) {
          Snackbar.make(toolbar, R.string.app_error_notimplemented, Snackbar.LENGTH_SHORT).show();
        }
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }

  }
}
