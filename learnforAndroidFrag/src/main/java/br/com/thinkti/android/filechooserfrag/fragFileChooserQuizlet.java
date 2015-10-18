/*
 * Original at https://code.google.com/p/android-file-chooser/
 *
 * Modified by J.M.Goebel
 *
 * License:
 * http://www.gnu.org/licenses/gpl.html
 *
 * Copyright (c) 2015 GPL by J.M.Goebel. Distributed under the GNU GPL v3.
 * 
 * 08.06.2015
 * 
 * This file is part of learnforandroid.
 *
 * learnforandroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  learnforandroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.thinkti.android.filechooserfrag;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.de.jmg.learn.MainActivity;
import org.de.jmg.learn.R;
import org.de.jmg.learn._MainActivity;
import org.de.jmg.learn.vok.Vokabel;
import org.de.jmg.learn.vok.typVok;
import org.de.jmg.lib.lib;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import android.content.Context;

@TargetApi(11)
public class fragFileChooserQuizlet extends ListFragment
{

	public final static int fragID = 4;
	private File currentDir;
	private QuizletArrayAdapter adapter;
	private FileFilter fileFilter;
	private File fileSelected;
	private boolean unicode;
	private String DefaultDir;
	private ArrayList<String> extensions;
	public MainActivity _main;
	private View _chooserView;
	private Intent _Intent;
	private boolean _blnInitialized;




	// edit the line below with your quizlet client id
	private static String QUIZLET_CLIENT_ID;
	private static String QUIZLET_SECRET_KEY;
	private static String browseApiUrl; //= "https://api.quizlet.com/2.0/search/sets?client_id=" + QUIZLET_CLIENT_ID + "&time_format=fuzzy_date" ;
	private static String getSetApiUrl; //= "https://api.quizlet.com/2.0/sets?client_id=" + QUIZLET_CLIENT_ID + "&set_ids=" ;

	private String username;
	private String searchPhrase;
	private int page = 1;   // 1 based page number
	private int totalPages;
	private int totalResults;
	private String errorDescription ;
	private String errorTitle ;
	private boolean blnAdapterInvalid;

	public fragFileChooserQuizlet()
	{

	}
	public void  initfragFileChooserQuizlet(final MainActivity main, final String username, final String searchPhrase) {
		this.username = username;
		this.searchPhrase = searchPhrase ;
		this._main = main;
		//String passwd = lib.InputBox(_main,"password","password","",false).input;
		QUIZLET_CLIENT_ID = new String(Base64.decode(Data.QuizletClientID, Base64.DEFAULT));
		QUIZLET_SECRET_KEY = new String(Base64.decode(Data.SecretKey, Base64.DEFAULT));
		browseApiUrl = "https://api.quizlet.com/2.0/search/sets?client_id=" + QUIZLET_CLIENT_ID + "&time_format=fuzzy_date" ;
		getSetApiUrl = "https://api.quizlet.com/2.0/sets?client_id=" + QUIZLET_CLIENT_ID + "&set_ids=" ;
		blnAdapterInvalid = true;
	}


	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}


	public String getSearchPhrase() {
		return searchPhrase ;
	}
	public void setSearchPhrase( String searchPhrase )
	{
		this.searchPhrase = searchPhrase ;
		if (adapter!=null)
		{
			blnAdapterInvalid = true;
		}
	}


	public void firstPage()
	{
		page=1;
		new TaskopenPage().execute();
	}


	public void nextPage()
	{
		page++;
		new TaskopenPage().execute();
	}


	class TaskopenPage extends AsyncTask<Void,Void,List<RowData>>
	{
		ProgressDialog p;
		@Override
		protected List<RowData> doInBackground(Void... params) {
			List<RowData> list = null;

			try {
				list = openPage();
			} catch (Exception e) {
				e.printStackTrace();
				list = new ArrayList<RowData>();
				RowData r = new RowData();
				r.ex = e;
				list.add (r);
			}

			return list;
		}

		@Override
		protected void onPostExecute(List<RowData> list) {
			if(list!=null)
			{
				if (list.size()==1 && list.get(0).ex != null)
				{
					Log.d("OpenPage", list.get(0).ex.getMessage(),list.get(0).ex);
					lib.ShowMessage(_main,list.get(0).ex.getMessage(),_main.getString(R.string.Error));
				}
				else
				{
					if (adapter == null || blnAdapterInvalid)
					{
						blnAdapterInvalid = false;
						adapter = new QuizletArrayAdapter(getActivity(), R.layout.file_view,
								list,fragFileChooserQuizlet.this);
						fragFileChooserQuizlet.this.setListAdapter(adapter);
					}
					else
					{
						adapter.addAll(list);
					}
				}


			}
			p.dismiss();

		}

		@Override
		protected void onPreExecute() {
			p = new ProgressDialog(fragFileChooserQuizlet.this._main);
			p.setMessage(_main.getString(R.string.loading));
			p.show();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}



	}


	class TaskopenSet extends AsyncTask<String,String,List<typVok>>
	{
		ProgressDialog p;
		@Override
		protected List<typVok> doInBackground(String... params) {
			List<typVok> list = null;

			try {
				list = openSet(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return list;
		}

		@Override
		protected void onPostExecute(List<typVok> list) {
			try
			{
				if(list!=null)
				{
					_main.vok.NewFile();
					_main.vok.getVokabeln().addAll(list);

					if (_main.vok.getGesamtzahl()>1)
					{
						//_main.vok.setIndex(1);

						_main.vok.aend = true;

						_main.vok.InitAbfrage();

						_main.mPager.setCurrentItem(_MainActivity.fragID);

						lib.yesnoundefined res = lib.ShowMessageYesNo(_main, getString(R.string.txtFlashCardFile),"");
						if (res == lib.yesnoundefined.yes)
						{
							_main.vok.setCardMode(true);
							_main.fPA.fragMain.SetViewsToCardmode();
						}
						else
						{
							_main.vok.setCardMode(false);
							_main.fPA.fragMain.SetViewsToVokMode();
						}
						try
						{
							if (!lib.libString.IsNullOrEmpty(_main.vok.title))
							{
								File file = new File(_main.vok.getvok_Path(), _main.vok.title + (_main.vok.getCardMode()?".kar":".vok"));
								_main.vok.setFileName(file.getPath());
							}

						}
						catch (Exception ex)
						{

						}
						_main.fPA.fragMain.SetActionBarTitle();
						//_main.fPA.fragMain.getVokabelDelayed(1000);
						//_main.fPA.fragMain.getVokabel(false, false, false, true);
						//_main.fPA.fragMain._scrollView.fullScroll(View.FOCUS_UP);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			p.dismiss();

		}

		@Override
		protected void onPreExecute() {
			p = new ProgressDialog(fragFileChooserQuizlet.this._main);
			p.setMessage(_main.getString(R.string.loading));
			p.show();

		}

		@Override
		protected void onProgressUpdate(String... values) {
			p.setMessage(values[0]);
		}



	}

	private List<RowData> openPage() throws Exception {
		this.errorDescription = null ;
		this.errorTitle = null ;

		List<RowData> list = new ArrayList<RowData>();
		InputStream inputStream = null ;
		try {
			URL url = new URL( getCatalogUrl() );
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			if ( connection.getResponseCode() >= 400 ) {
				inputStream = connection.getErrorStream();
			}
			else {
				inputStream = connection.getInputStream();
			}
			JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
			reader.beginObject();
			while ( reader.hasNext() ) {
				String name = reader.nextName();
				if ( "total_pages".equals( name )) {
					this.totalPages = reader.nextInt();
					if ( page > totalPages ) {

					}
				}
				else if ( "total_results".equals( name ))
				{
					this.totalResults = reader.nextInt();
				}
				else if ( "page".equals( name )) {
					this.page = reader.nextInt();
				}
				else if ( "error_title".equals( name )) {
					errorTitle = reader.nextString();
				}
				else if ( "error_description".equals( name )) {
					errorDescription = reader.nextString();
				}
				else if ( "sets".equals( name ) ) {
					reader.beginArray();
					while ( reader.hasNext() ) {
						list.add( parseSetJson( reader ) );
					}
					reader.endArray();
				}
				else {
					reader.skipValue();
				}
			}
			reader.endObject();
		}
		finally {
			if ( inputStream != null ) {
				inputStream.close();
			}
		}
		return list ;
	}


	private List<typVok> openSet(String id) throws Exception {
		this.errorDescription = null ;
		this.errorTitle = null ;
		InputStream inputStream = null ;
		List<typVok>list = new ArrayList<typVok>();
		String Kom = "";
		_main.vok.title = "";
		try {
			URL url = new URL( getDeckUrl(id) );
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			if ( connection.getResponseCode() >= 400 ) {
				inputStream = connection.getErrorStream();
			}
			else {
				inputStream = connection.getInputStream();
			}
			JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
			reader.beginArray();
			while ( reader.hasNext() )
			{
				reader.beginObject();
				while ( reader.hasNext()) {
					String name = reader.nextName();
					if ("id".equals(name)) {
						long intId = reader.nextLong();
						/*if (page > totalPages) {

						}*/
					} else if ("url".equals(name)) {
						String strUrl = reader.nextString();
					} else if ("title".equals(name)) {
						String title = reader.nextString();
						_main.vok.title = title;
					} else if ("created_by".equals(name)) {
						String created_by = reader.nextString();
						Kom = _main.getString(R.string.created_by) + " " + created_by
								+ " " + _main.getString((R.string.at))
								+ " <link://https://quizlet.com/ Quizlet/>";
					} else if ("term_count".equals(name)) {
						int term_count = reader.nextInt();
					} else if ("terms".equals(name)) {
						reader.beginArray();
						while (reader.hasNext()) {
							typVok v = parseSetDataJson(reader);
							String kom = v.Kom;
							v.Kom = Kom;
							if (! lib.libString.IsNullOrEmpty(kom))
							{
								v.Kom += " " + kom;
							}
							list.add(v);
						}
						reader.endArray();
					} else {
						reader.skipValue();
					}
				}
				reader.endObject();
			}
			reader.endArray();
		}
		finally {
			if ( inputStream != null ) {
				inputStream.close();
			}
		}
		return list ;
	}


	RowData parseSetJson( JsonReader reader ) throws IOException {
		reader.beginObject();
		RowData rowData = new RowData();

		while ( reader.hasNext() ) {
			String name = reader.nextName();
			if ( name.equals( "title" )) {
				rowData.name = reader.nextString();
			}
			else if ( name.equals( "description" )) {
				rowData.description = reader.nextString();
				if (rowData.description.length()>200) rowData.description = rowData.description.substring(0,100);
			}
			else if ( name.equals( "id" )) {
				rowData.id = reader.nextInt();
			}
			else if ( name.equals( "term_count" )) {
				rowData.numCards = reader.nextInt();
			}
			else if ( name.equals( "modified_date" )) {
				long value = reader.nextLong();
				rowData.lastModified = Data.SHORT_DATE_FORMAT.format( new Date( value * 1000 ) );
				Log.d( Data.APP_ID, " modified_date   value=" + value + " formatted=" + rowData.lastModified + " now=" + (new Date().getTime())  );
			}
			else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return rowData ;
	}

	typVok parseSetDataJson( JsonReader reader ) throws IOException {
		reader.beginObject();
		typVok rowData = new typVok();

		while ( reader.hasNext() ) {
			String name = reader.nextName();
			if ( name.equals( "term" )) {
				rowData.Wort = reader.nextString();
			}
			else if ( name.equals( "id" )) {
				long id = reader.nextLong();
				rowData.z = 0;
			}
			else if ( name.equals( "definition" )) {
				rowData.Bed1 = reader.nextString();
				rowData.Bed2 = "";
				rowData.Bed3 = "";
			}
			else if ( name.equals( "image" )) {
				try
				{
					reader.beginObject();
					while ( reader.hasNext() ) {
						String strName = reader.nextName();
						if ( strName.equals( "url" )) {
							String value = "<link://" + reader.nextString() + " " + _main.getString(R.string.picture) +  "/>";
							rowData.Kom = value;
						}
						else
						{
							reader.skipValue();
						}
					}
					reader.endObject();
				}
				catch (Exception exception)
				{
					reader.skipValue();
					//String value = "<link://" + reader.nextString() + "/>";
					//rowData.Kom = value;
				}

			}
			else {
				reader.skipValue();
			}
		}
		reader.endObject();
		if (lib.libString.IsNullOrEmpty(rowData.Bed1)) {
			rowData.Bed1 = rowData.Kom;
			rowData.Bed2 = "";
			rowData.Bed3 = "";
		}
		return rowData ;
	}



	public void prevPage() throws Exception {
		if ( page > 1 ) {
			page--;
		}
		new TaskopenPage().execute();
	}


	public String getErrorDescription() {
		return this.errorDescription ;
	}

	public String getErrorTitle() {
		return this.errorTitle ;
	}

	public static String getDeckUrl(String id) {
		return getSetApiUrl + id;
	}

	public String getCatalogUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append( fragFileChooserQuizlet.browseApiUrl );
		sb.append( "&q=" ) ;
		if ( this.username != null && this.username.length() > 0 ) {
			sb.append( "creator:" + username + " " );
		}
		sb.append( this.searchPhrase );
		sb.append( "&page=" );
		sb.append( page );
		Log.d(Data.APP_ID, sb.toString());
		return sb.toString() ;
	}

	public int getPage() {
		return this.page ;
	}

	public int getTotalPages() {
		return this.totalPages ;
	}

	public void Load()
	{
		new TaskopenPage().execute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		_chooserView = super.onCreateView(inflater, container, savedInstanceState);
		if (_main==null)_main = (MainActivity)getActivity();
		//if (_Intent==null&&_main!=null)_Intent=_main.getFileChooserIntent(true);
		//init()



		return _chooserView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
	 	super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_main = (MainActivity)getActivity();
		_blnInitialized = false;
		_chooserView = null;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		if (info.position>0)
		{
			MenuInflater inflater = _main.getMenuInflater();
			inflater.inflate(R.menu.context, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		View v = info.targetView;
		RowData o = adapter.getItem((int)info.id);
		switch (item.getItemId()) {
			case R.id.mnuDelete:
				String msg = String.format(getString(R.string.txtReallyDelete),o.name);
				if (lib.ShowMessageYesNo(_main, msg,_main.getString(R.string.question))== lib.yesnoundefined.yes)
				{
					try
					{
						/*
						File F = new File(o.getPath());
						if (F.exists()) {
							if (F.isDirectory()) {
								String [] deleteCmd = {"rm", "-r", F.getPath()};
								Runtime runtime = Runtime.getRuntime();
								runtime.exec(deleteCmd);
							} else {
								F.delete();
							}
						}
						*/
						adapter.remove(o);

					}
					catch(Exception ex)
					{
						lib.ShowMessage(_main,ex.getMessage(),getString((R.string.Error)));
					}
				}
				//lib.ShowToast(_main,"delete " + t1.getText().toString() + " " + t2.getText().toString() + " " + o.getData() + " "  + o.getPath() + " " + o.getName());
				//editNote(info.id);
				return true;
			case R.id.mnuRename:
				String msg2 = String.format(getString(R.string.txtRenameFile),o.name);
				lib.OkCancelStringResult res = lib.InputBox(_main,getString(R.string.rename),msg2,o.name,false);
				if (res.res == lib.okcancelundefined.ok.ok && !lib.libString.IsNullOrEmpty(res.input) && res.input != o.name)
				{
					try
					{
						/*
						File F = new File(o.getPath());
						File F2 = new File(F.getParent(),res.input);
						F.renameTo(F2);
						*/
						o.name=(res.input);
						//o.setPath(F2.getPath());
					}
					catch(Exception ex)
					{
						lib.ShowMessage(_main,ex.getMessage(),getString((R.string.Error)));
					}
				}
				//lib.ShowToast(_main, "rename " + t1.getText().toString() + " " + t2.getText().toString() + " " + o.getData() + " "  + o.getPath() + " " + o.getName());
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
	



	@Override
	public void onListItemClick(ListView l, View v, int position, long id) 
	{

		super.onListItemClick(l, v, position, id);
		RowData o = adapter.getItem(position);
			//onFileClick(o);
		String[]params = new String[]{"" + o.id};
		try {
			if (_main.saveVok(false,true))
			{
				new TaskopenSet().execute(params);
			}
		} catch (Exception e) {
			lib.ShowException(_main,e);
		}


	}

//
//	private void onFileClick(Option o) {
//		Toast.makeText(this, "File Clicked: " + o.getName(), Toast.LENGTH_SHORT)
//				.show();
//	}		
}