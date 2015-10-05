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
import org.de.jmg.learn.*;
import org.de.jmg.learn.R;
import org.de.jmg.lib.lib;

import br.com.thinkti.android.filechooser.FileArrayAdapter;
import br.com.thinkti.android.filechooser.Option;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class fragFileChooser extends ListFragment 
{
	public final static int fragID = 1;
	private File currentDir;
	private FileArrayAdapter adapter;
	private FileFilter fileFilter;
	private File fileSelected;
	private boolean unicode;
	private String DefaultDir;
	private ArrayList<String> extensions;
	public MainActivity _main;
	private View _chooserView;
	private Intent _Intent;
	private boolean _blnInitialized;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		_chooserView = super.onCreateView(inflater, container, savedInstanceState);
		if (_main==null)_main = (MainActivity)getActivity();
		if (_Intent==null&&_main!=null)_Intent=_main.getFileChooserIntent(true);
		init();
		return _chooserView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
	 	super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
	}




	public void init(Intent intent, MainActivity main)
	{
			_main = main;
			_Intent = intent;
	}
	
	public void init()
	{
		if (_main==null||_Intent==null||_chooserView==null||_blnInitialized)
		{
			return;
		}
		try
		{
			Bundle extras = _Intent.getExtras();
			if (extras != null) 
			{
				unicode = extras.getBoolean("blnUniCode", true);
				DefaultDir = extras.getString("DefaultDir");
				if (extras.getStringArrayList("filterFileExtension") != null) {
					extensions = extras.getStringArrayList("filterFileExtension");				
					fileFilter = new FileFilter() {
						@Override
						public boolean accept(File pathname) 
						{						
							return ((pathname.isDirectory()) 
									|| ExtensionsMatch(pathname));
						}
					};
				}
			}
			
			setCurrentDir((DefaultDir));
			_blnInitialized=true;
		}
		catch(Exception ex)
		{
			Toast.makeText(_main, _main.getString(R.string.Error) + ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void setCurrentDir (String dir)
	{
		DefaultDir = dir;
		if (DefaultDir == null || DefaultDir.length()==0) DefaultDir=Environment.getExternalStorageDirectory().getPath();
		currentDir = new File(DefaultDir);
		Toast.makeText(_main, _main.getString(R.string.txtLoading) + currentDir.getPath(), Toast.LENGTH_LONG).show();
		fill(currentDir);;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_main = (org.de.jmg.learn.MainActivity)getActivity();
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
		ImageView im = (ImageView) v.findViewById(br.com.thinkti.android.filechooser.R.id.img1);
		TextView t1 = (TextView) v.findViewById(br.com.thinkti.android.filechooser.R.id.TextView01);
		TextView t2 = (TextView) v.findViewById(br.com.thinkti.android.filechooser.R.id.TextView02);
		Option o = adapter.getItem((int)info.id);
		switch (item.getItemId()) {
			case R.id.mnuDelete:
				lib.ShowToast(_main,"delete " + t1.getText().toString() + " " + t2.getText().toString() + " " + o.getData() + " "  + o.getPath() + " " + o.getName());
				//editNote(info.id);
				return true;
			case R.id.mnuRename:
				lib.ShowToast(_main, "rename " + t1.getText().toString() + " " + t2.getText().toString() + " " + o.getData() + " "  + o.getPath() + " " + o.getName());
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
	
	private boolean ExtensionsMatch(File pathname)
	{
		String ext;
		if(pathname.getName().contains("."))
		{
			ext = pathname.getName().substring(pathname.getName().lastIndexOf("."));
		}
		else
		{
			return false;
		}
		
		if (extensions.contains(ext)) return true;
		
		for(String itext: extensions)
		{
			itext = itext.replace(".", "\\.");
			itext = itext.toLowerCase();
			ext = ext.toLowerCase();
			if (ext.matches(itext.replace("?", ".{1}").replace("*", ".*")))
					{
						return true;
					}
		}
		
		return false;
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if ((!currentDir.getName().equals("sdcard")) && (currentDir.getParentFile() != null)) {
        		AlertDialog.Builder A = new AlertDialog.Builder(getActivity());
				A.setPositiveButton(getString(R.string.yes),
						new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								_main.mPager.setCurrentItem(_MainActivity.fragID);
							}
						});
				A.setNegativeButton(getString(R.string.no),
						new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) 
							{
								currentDir = currentDir.getParentFile();
					        	fill(currentDir);
							}
						});
				A.setMessage(getString(R.string.close));
				A.setTitle(_main.getString((R.string.question)));
				A.show();

        		
        	} else {
        		_main.mPager.setCurrentItem(_MainActivity.fragID);
        	}
            return false;
        }
        return true;
	}

	private void fill(File f) {
		File[] dirs = null;
		if (fileFilter != null)
			dirs = f.listFiles(fileFilter);
		else 
			dirs = f.listFiles();
			
		_main.setTitle(f.getName());
		List<Option> dir = new ArrayList<Option>();
		List<Option> fls = new ArrayList<Option>();
		try {
			for (File ff : dirs) {
				if (ff.isDirectory() && !ff.isHidden())
					dir.add(new Option(ff.getName(), getString(R.string.folder), ff
							.getAbsolutePath(), true, false, false));
				else {
					if (!ff.isHidden())
						fls.add(new Option(ff.getName(), getString(R.string.fileSize) + ": "
								+ ff.length(), ff.getAbsolutePath(), false, false, false));
				}
			}
		} catch (Exception e) {

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!f.getName().equalsIgnoreCase("sdcard")) {
			if (f.getParentFile() != null) dir.add(0, new Option("..", getString(R.string.parentDirectory), f.getParent(), false, true, false));
		}
		adapter = new FileArrayAdapter(getActivity(), R.layout.file_view,
				dir);
		this.setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) 
	{
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Option o = adapter.getItem(position);
		if (o.isFolder() || o.isParent()) {			
			currentDir = new File(o.getPath());
			fill(currentDir);
		} else {
			//onFileClick(o);
			fileSelected = new File(o.getPath());
			Intent intent = new Intent();
			intent.putExtra("fileSelected", fileSelected.getAbsolutePath());
			intent.putExtra("blnUniCode", this.unicode);
			_main.onActivityResult(MainActivity.FILE_CHOOSER, Activity.RESULT_OK, intent);
			_main.mPager.setCurrentItem(_MainActivity.fragID);
		}		
	}
//
//	private void onFileClick(Option o) {
//		Toast.makeText(this, "File Clicked: " + o.getName(), Toast.LENGTH_SHORT)
//				.show();
//	}		
}