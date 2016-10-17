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

import org.de.jmg.lib.lib.libString;

public class typVok {
	// Aufbau einer Vokabel
	public String Wort;
	public String Bed1;
	public String Bed2;
	public String Bed3;

	public String[] getBedeutungen() {
		return new String[] { Bed1, Bed2, Bed3 };
	}

	public int getAnzBed() {
		int functionReturnValue = 0;
		for (String Bed : getBedeutungen()) {
			if (!libString.IsNullOrEmpty(Bed))
				functionReturnValue += 1;
		}
		return functionReturnValue;
	}

	// Kommentar
	public String Kom;
	// Zähler wie oft gewußt
	public short z;

	public typVok(String Wort, String Bed1, String Bed2, String Bed3,
			String Kom, short z) {
		this.Wort = Wort;
		this.Bed1 = Bed1;
		this.Bed2 = Bed2;
		this.Bed3 = Bed3;
		this.Kom = Kom;
		this.z = z;
	}

	public typVok() {

	}
}
