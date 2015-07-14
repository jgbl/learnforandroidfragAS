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

import org.de.jmg.learn.libLearn;

public class clsVok {
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

	public String Wort;
	public short Zaehler;
	public Vokabel.Bewertung Bewertung;
	public int[] AnzTeilBed;

	private String[] mBedeutungen = new String[3];

	public String[] getBedeutungen() {
		return mBedeutungen;
	}

	public void setBedeutungen(String[] value) {
		// ERROR: Not supported in C#: OnErrorStatement

		libLearn.gStatus = "clsVok.Bedetungen Start";

		mBedeutungen[0] = value[0];

		mBedeutungen[1] = value[1];

		mBedeutungen[2] = value[2];

		return;
	}

	private void Class_Initialize() {
		// ERROR: Not supported in C#: OnErrorStatement

		libLearn.gStatus = "clsVok.Class_Initialize Start";

		AnzTeilBed = new int[] { 0, 0, 0 };

		return;
	}

	public clsVok() {
		Class_Initialize();
	}

}
