/*
 * TextDocumentsProvider.java
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

package com.bleushan.laboratoire1.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract.Document;
import android.provider.DocumentsContract.Root;
import android.provider.DocumentsProvider;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;

import com.bleushan.laboratoire1.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A subclass of {@link DocumentsProvider} that provides access to text document represented as
 * files located in the device public external storage document folder. Been a document provider,
 * it manages those document and exposes them to the Android system for sharing.
 * <p/>
 * This implementation is heavily inspired, at times copied, from the
 * <a href="http://bit.ly/1KTocwr">StorageProvider</a> sample code. It, therefore, inherits some of
 * its quirks. For example: we don't, yet, index our documents or store document metadata in a
 * database. The reason being that our documents are file based the querying the filesystem
 * directly fits most of our needs. The main improvement a database could provide is an easier
 * file indexing mechanism and a "cleaner" way to query the documents metadata at the cost of a
 * more complicated codebase.
 *
 * @see Environment#getExternalStoragePublicDirectory(String)
 * @see Environment#DIRECTORY_DOCUMENTS
 */
public class TextDocumentsProvider extends DocumentsProvider {

  private static final String TAG = TextDocumentsProvider.class.getSimpleName();
  /**
   * Use these as the default columns to return information about a root if no specific
   * columns are requested in a query.
   *
   * @see android.provider.DocumentsContract.Root
   */
  private static final String[] DEFAULT_ROOT_PROJECTION = new String[]{
    Root.COLUMN_ROOT_ID,
    Root.COLUMN_MIME_TYPES,
    Root.COLUMN_FLAGS,
    Root.COLUMN_ICON,
    Root.COLUMN_TITLE,
    Root.COLUMN_SUMMARY,
    Root.COLUMN_DOCUMENT_ID,
    Root.COLUMN_AVAILABLE_BYTES
  };
  /**
   * Use these as the default columns to return information about a document if no specific
   * columns are requested in a query.
   *
   * @see android.provider.DocumentsContract.Document
   */
  private static final String[] DEFAULT_DOCUMENT_PROJECTION = new String[]{
    Document.COLUMN_DOCUMENT_ID,
    Document.COLUMN_MIME_TYPE,
    Document.COLUMN_DISPLAY_NAME,
    Document.COLUMN_LAST_MODIFIED,
    Document.COLUMN_FLAGS,
    Document.COLUMN_SIZE
  };
  /**
   * The default value we use for the root projection.
   *
   * @see android.provider.DocumentsContract.Root#COLUMN_ROOT_ID
   */
  private static final String ROOT_ID = "textdocument";

  /**
   * The operation log buffered file writer. The file is located in the cache directory of the
   * application.
   *
   * @see Context#getCacheDir()
   */
  private BufferedWriter logFileWriter;
  /**
   * The document provider root directory.
   */
  private File rootDir;

  /**
   * Helper method to resolve the root projection.
   *
   * @param projection
   *   the requested root column projection
   *
   * @return either the requested root column projection, or the default projection if the
   * requested projection is null
   *
   * @see <a href="http://bit.ly/1KTocwr">StorageProvider sample</a>
   */
  private static String[] resolveRootProjection(String[] projection) {
    return projection != null ? projection : DEFAULT_ROOT_PROJECTION;
  }

  /**
   * Helper method to resolve the document projection.
   *
   * @param projection
   *   the requested document column projection
   *
   * @return either the requested document column projection, or the default projection if the
   * requested projection is null
   *
   * @see <a href="http://bit.ly/1KTocwr">StorageProvider sample</a>
   */

  private static String[] resolveDocumentProjection(String[] projection) {
    return projection != null ? projection : DEFAULT_DOCUMENT_PROJECTION;
  }

  /**
   * Helper method that returns the supported value to set a cursor {@link Document#COLUMN_FLAGS}
   * for a given file
   *
   * @param file
   *   The file which we need the flags of
   *
   * @return the flags for the file
   */
  private static int getFlagsForFile(File file) {
    int flags = 0;
    if ((file != null) && file.canWrite()) {
      if (file.isDirectory()) {
        flags |= Document.FLAG_DIR_SUPPORTS_CREATE;
      } else {
        flags |= Document.FLAG_SUPPORTS_WRITE;
        flags |= Document.FLAG_SUPPORTS_DELETE;
      }
    }
    return flags;
  }

  @Override
  public boolean onCreate() {
    this.rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    File logFile = new File(this.getContext().getCacheDir().getPath() + "/log.txt");
    try {
      this.logFileWriter = new BufferedWriter(new FileWriter(logFile, true));
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    // We ensure that the root directory exist with this return statement.
    return (this.rootDir.mkdirs() || this.rootDir.isDirectory());
  }

  @Override
  public Cursor queryRoots(String[] projection) throws FileNotFoundException {
    // Create a cursor and define its columns.
    MatrixCursor result = new MatrixCursor(resolveRootProjection(projection));
    // Build the single root cursor row supported by the app and add all the values for the  column
    // given by the document contract. Also, we don't need to use conditionals since columns not
    // defined by the cursor are ignored.
    result.newRow()
          .add(Root.COLUMN_ROOT_ID, ROOT_ID)
          .add(Root.COLUMN_MIME_TYPES, MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt"))
          .add(Root.COLUMN_FLAGS, Root.FLAG_SUPPORTS_CREATE)
          .add(Root.COLUMN_ICON, R.mipmap.ic_launcher)
          .add(Root.COLUMN_TITLE, this.getContext().getString(R.string.app_name))
          .add(Root.COLUMN_SUMMARY,
               this.getContext().getString(R.string.app_provider_textdocument_rootsummary))
          .add(Root.COLUMN_DOCUMENT_ID, this.getDocID(this.rootDir))
          .add(Root.COLUMN_AVAILABLE_BYTES, this.rootDir.getUsableSpace());
    return result;
  }

  @Override
  public Cursor queryDocument(String documentId, String[] projection) throws FileNotFoundException {
    return this.getCursorForQuery(documentId, projection);
  }

  @Override
  public Cursor queryChildDocuments(String parentDocumentId, String[] projection, String sortOrder)
    throws FileNotFoundException {
    return this.getCursorForQuery(parentDocumentId, projection);
  }

  @Override
  public ParcelFileDescriptor openDocument(String documentId,
                                           String mode,
                                           CancellationSignal signal) throws FileNotFoundException {
    return null;
  }

  @Override
  public String createDocument(String parentDocumentId, String mimeType, String displayName)
    throws FileNotFoundException {
    return super.createDocument(parentDocumentId, mimeType, displayName);
  }

  @Override
  public void deleteDocument(String documentId) throws FileNotFoundException {
    super.deleteDocument(documentId);
  }

  /**
   * Gets the document ID for a given File object.
   * <p/>
   * This implementation is copied/inspired from the
   * <a href="http://bit.ly/1KTocwr">StorageProvider</a> sample code. Therefore it has the same
   * flaws. Ideally, you would store the filepath and the document ID in key-value pair fashion a
   * database. This would help ensure that the document ID is unique and consistent and probably be
   * easier to query.
   *
   * @param file
   *   The File whose document ID you want
   *
   * @return the corresponding document ID
   *
   * @see TextDocumentsProvider#queryDocument(String, String[])
   */
  private String getDocID(@NonNull File file) {
    String path = file.getAbsolutePath();
    String rootPath = this.rootDir.getPath();
    if (this.rootDir.getPath().equals(path)) {
      path = "";
    } else if (rootPath.endsWith("/")) {
      path = path.substring(rootPath.length());
    } else {
      path = path.substring(rootPath.length() + 1);
    }
    return ROOT_ID + ':' + path;
  }

  /**
   * Gets the File object for a given document ID.
   * <p/>
   * This implementation is copied/inspired from the
   * <a href="http://bit.ly/1KTocwr">StorageProvider</a> sample code. Therefore it has the same
   * flaws. Ideally, you would store the filepath and the document ID in key-value pair fashion a
   * database. This would help ensure that the document ID is unique and consistent and probably be
   * easier to query.
   *
   * @param documentId
   *   the File whose document ID you want
   *
   * @return the corresponding document ID
   *
   * @see TextDocumentsProvider#queryDocument(String, String[])
   */
  private File getFile(@NonNull String documentId) throws FileNotFoundException {
    File target = this.rootDir;
    if (documentId.equals(ROOT_ID)) {
      return target;
    }
    int splitIndex = documentId.indexOf(':', 1);
    if (splitIndex < 0) {
      throw new FileNotFoundException("Missing root for " + documentId);
    } else {
      String path = documentId.substring(splitIndex + 1);
      target = new File(target, path);
      if (!target.exists()) {
        throw new FileNotFoundException("Missing file for " + documentId + " at " + target);
      }
      return target;
    }
  }

  /**
   * A helper method for creating a cursor that contains filesystem metadata.
   * <p/>
   * This is a kind of cleanup version of includeFile from the
   * <a href="http://bit.ly/1KTocwr">StorageProvider</a> sample code. The original was using a
   * side effecting function parameter and it wasn't quite handling file directories.
   *
   * @param documentId
   *   The document ID that is going to be used as a basis for the document cursor
   * @param projection
   *   The projection for the cursor
   *
   * @return a cursor that contains filesystem metadata of the document.
   *
   * @throws FileNotFoundException
   */
  private Cursor getCursorForQuery(String documentId, String[] projection)
    throws FileNotFoundException {
    MatrixCursor result = new MatrixCursor(resolveDocumentProjection(projection));
    int flags = 0;
    File document = this.getFile(documentId);
    if ((document != null) && document.canWrite()) {
      if (document.isDirectory()) {
        flags |= Document.FLAG_DIR_SUPPORTS_CREATE;
      } else {
        flags |= Document.FLAG_SUPPORTS_WRITE;
        flags |= Document.FLAG_SUPPORTS_DELETE;
      }
    }
    return result;

  }
}
