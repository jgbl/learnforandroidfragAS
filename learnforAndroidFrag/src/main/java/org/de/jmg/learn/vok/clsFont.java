/*
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
package org.de.jmg.learn.vok;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.widget.TextView;

import org.de.jmg.learn.libLearn;
import org.de.jmg.lib.RefSupport;
import org.de.jmg.lib.lib.libString;

public class clsFont {
	// Learn For All New Version
	// By J.M.Goebel (jhmgbl2@t-online.dee)
	//
	// This program is free software; you can redistribute it and/or
	// modify it under the terms of the GNU General Public License
	// as published by the Free Software Foundation; either version 2
	// of the License, or (at your option) any later version.
	//
	// This program is distributed in the hope that it will be useful,
	// but WITHOUT ANY WARRANTY; without even the implied warranty of
	// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	// GNU General Public License for more details.
	//
	// You should have received a copy of the GNU General Public License
	// along with this program; if not, write to the Free Software
	// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307,
	// USA.
	//

	// lokale Variable(n) zum Zuweisen der Eigenschaft(en)
	// lokale Kopie
	private String mvarName;
	// lokale Kopie
	private int mvarSize;
	// lokale Kopie
	private boolean mvarBold;
	// lokale Kopie
	private boolean mvarunderline;
	// lokale Kopie
	private boolean mvarstrikethrough;
	// lokale Kopie
	private short mvarWeight;
	// lokale Kopie
	private int mvarStyle;
	// lokale Variable(n) zum Zuweisen der Eigenschaft(en)
	// lokale Kopie
	private boolean mvarItalic;
	// lokale Variable(n) zum Zuweisen der Eigenschaft(en)
	// lokale Kopie
	private Typeface mvarFontset;
	private static String mFontName;
	public Activity Container;

	public static boolean FontNameExists(String vData) {
		// UPGRADE_ISSUE: Screen Eigenschaft Screen.FontCount wurde nicht
		// aktualisiert. Klicken Sie hier für weitere Informationen:
		// 'ms-help://MS.VSExpressCC.v80/dv_commoner/local/redirect.htm?keyword="CC4C7EC0-C903-48FC-ACCC-81861D12DA4A"'
		if (Typeface.create(vData, Typeface.NORMAL) != null) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean Exists(Typeface f) {
		if (mFontName.contains(f.toString())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean getItalic() {
		boolean functionReturnValue = false;
		// ERROR: Not supported in C#: OnErrorStatement

		libLearn.gStatus = "clsFont.Italic Start";
		// wird beim Ermitteln einer Eignschaft auf der rechten Seite der
		// Gleichung verwendet.
		// Syntax: Debug.Print X.Italic
		functionReturnValue = mvarItalic;
		return functionReturnValue;

	}

	public void setItalic(boolean value) {
		// ERROR: Not supported in C#: OnErrorStatement

		libLearn.gStatus = "clsFont.Italic Start";
		// wird beim Zuweisen eines Werts in eine Eigenschaft auf der linken
		// Seite der Gleichung, verwendet.
		// Syntax: X.Italic = 5
		mvarItalic = value;
		return;

	}

	public int getStyle() {
		libLearn.gStatus = "clsFont.Charset Start";
		return mvarStyle;
	}

	public void setCharset(int value) {
		libLearn.gStatus = "clsFont.Charset Start";
		mvarStyle = value;
		return;
	}

	public short getWeight() {
		short functionReturnValue = 0;
		libLearn.gStatus = "clsFont.Weight Start";
		functionReturnValue = mvarWeight;
		return functionReturnValue;
	}

	public void setWeight(short value) {
		libLearn.gStatus = "clsFont.Weight Start";
		// wird beim Zuweisen eines Werts in eine Eigenschaft auf der linken
		// Seite der Gleichung, verwendet.
		// Syntax: X.Weight = 5
		mvarWeight = value;
		return;
	}

	public boolean getstrikethrough() {
		libLearn.gStatus = "clsFont.strikethrough Start";
		return mvarstrikethrough;
	}

	public void setstrikethrough(boolean value) {
		libLearn.gStatus = "clsFont.strikethrough Start";
		mvarstrikethrough = value;
		return;
	}

	public boolean getunderline() {
		boolean functionReturnValue = false;
		libLearn.gStatus = "clsFont.underline Start";
		functionReturnValue = mvarunderline;
		return functionReturnValue;
	}

	public void setunderline(boolean value) {
		libLearn.gStatus = "clsFont.underline Start";
		mvarunderline = value;
		return;
	}

	public boolean getBold() {
		boolean functionReturnValue = false;
		libLearn.gStatus = "clsFont.Bold Start";
		functionReturnValue = mvarBold;
		return functionReturnValue;
	}

	public void setBold(boolean value) {
		libLearn.gStatus = "clsFont.Bold Start";
		mvarBold = value;
		return;
	}

	public int getSize() {
		int functionReturnValue = 0;
		libLearn.gStatus = "clsFont.Size Start";
		if (mvarSize < 8)
			mvarSize = 8;
		functionReturnValue = mvarSize;
		return functionReturnValue;
	}

	public void setSize(int value) {
		libLearn.gStatus = "clsFont.Size Start";
		if (value > 5) {
			mvarSize = value;
		} else {
			mvarSize = 5;
		}
		return;
	}

	public String getName() throws Exception {
		String functionReturnValue = null;
		functionReturnValue = mvarName;
		return functionReturnValue;
	}

	public void setName(String value) throws Exception {
		libLearn.gStatus = "clsFont.Name Start";
		value = (value.trim());
		mvarName = value;
		if (FontNameExists(value)) {
			return;
		}

		mvarName = "SANS_SERIF";
		return;
	}

	public void Fontset(Typeface vData, int Size, boolean Underline) {
		// ERROR: Not supported in C#: OnErrorStatement

		libLearn.gStatus = "clsFont.Fontset Start";
		// wird beim Zuweisen eines Objekts in eine Eigenschaft auf der linken
		// Seite der Gleichung, verwendet.
		// Syntax: Set x.Fontset = Form1
		mvarFontset = vData;
		if (mvarFontset.getClass() == Typeface.class) {
			if (Typeface.SANS_SERIF.equals(Typeface.create(mvarFontset,
					Typeface.NORMAL))) {
				mvarName = "SANS_SERIF";
			} else if (Typeface.MONOSPACE.equals(Typeface.create(mvarFontset,
					Typeface.NORMAL))) {
				mvarName = "MONOSPACE";
			} else if (Typeface.SERIF.equals(Typeface.create(mvarFontset,
					Typeface.NORMAL))) {
				mvarName = "SERIF";
			} else {
				mvarName = "";
			}
			mvarSize = Size;
			mvarStyle = mvarFontset.getStyle();
			mvarBold = mvarFontset.isBold();
			mvarunderline = Underline;
			mvarItalic = mvarFontset.isItalic();
		}

		return;
	}

	public void FontNameGet(TextView vData) throws Exception {
		int Size = -1;
		fontNameGet(vData, Size);
	}

	public void fontNameGet(TextView vData, int Size) throws Exception {
		// ERROR: Not supported in C#: OnErrorStatement
		libLearn.gStatus = "clsFont.FontNameGet Start";
		// wird beim Zuweisen eines Objekts in eine Eigenschaft auf der linken
		// Seite der Gleichung, verwendet.
		// Syntax: Set x.Fontset = Form1
		if (libString.IsNullOrEmpty(mvarName))
			mvarName = "SANS_SERIF";

		if (FontNameExists(mvarName) == true) {
			Typeface tf = Typeface.create(mvarName, vData.getTypeface()
					.getStyle());
			vData.setTypeface(tf);
			vData.setTextSize(TypedValue.COMPLEX_UNIT_PX, (Size > 0 ? Size
					: this.getSize()));
		} else {
			Typeface tf = Typeface.create(vData.getTypeface(), vData
					.getTypeface().getStyle());
			vData.setTypeface(tf);
			vData.setTextSize(TypedValue.COMPLEX_UNIT_PX, (Size > 0 ? Size
					: this.getSize()));
		}
		return;
	}

	public void setFont(RefSupport<Typeface> vdata) throws Exception {
		// ERROR: Not supported in C#: OnErrorStatement
		libLearn.gStatus = "clsFont.SetFont Start";
		// wird beim Zuweisen eines Objekts in eine Eigenschaft auf der linken
		// Seite der Gleichung, verwendet.
		// Syntax: Set x.Fontset = Form1
		if (mvarSize < 5) {
			libLearn.gStatus = "clsFont.SetFont Line 72";
			// Inserted by CodeCompleter
			mvarSize = 5;
		}

		if (FontNameExists(mvarName) == true) {
			vdata.setValue(Typeface.create(
					mvarName,
					(this.getBold() ? Typeface.BOLD : Typeface.NORMAL)
							| (this.getItalic() ? Typeface.ITALIC
									: Typeface.NORMAL)
							| (this.getunderline() ? Typeface.BOLD_ITALIC
									: Typeface.NORMAL)
							| (this.getstrikethrough() ? Typeface.BOLD_ITALIC
									: Typeface.NORMAL)));
		}

		return;
	}

	public clsFont(Activity container2) {
		this.Container = container2;
	}

	public clsFont() {
		// TODO Auto-generated constructor stub
	}

}
