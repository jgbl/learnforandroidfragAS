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
package org.de.jmg.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class WindowsBufferedReader extends BufferedReader {

	public WindowsBufferedReader(Reader in) {
		super(in);
		// TODO Auto-generated constructor stub
	}

	public WindowsBufferedReader(Reader in, int size) {
		super(in, size);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String readLine() throws IOException {

		String s = super.readLine();
		if (s != null) {
			int length = s.length();
			if (length > 1) {
				char c = s.charAt(0);
				int ic = c;
				if (ic == 65279) {
					s = s.substring(1);
				}
			}
		}
		return s;
	}

}
