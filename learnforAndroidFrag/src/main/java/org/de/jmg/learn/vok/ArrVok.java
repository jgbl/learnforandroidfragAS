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

import java.util.ArrayList;

import org.de.jmg.learn.vok.typVok;

public class ArrVok extends ArrayList<typVok> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5138813153169214705L;

	public void AddVokabel(String Wort, String Bed1, String Bed2, String Bed3,
			String Kom, short z) {
		typVok vok = new typVok(Wort, Bed1, Bed2, Bed3, Kom, z);
		this.add(vok);
	}

}
