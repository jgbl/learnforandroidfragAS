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

/*
 Copyright 2010,2011 Kevin Glynn (kevin.glynn@twigletsoftware.com)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Author(s):

 Kevin Glynn (kevin.glynn@twigletsoftware.com)
 */

// Wraps a parameter so that it can be used as a ref or out param.
public class RefSupport<T> {

	public enum ReturnPreOrPostValue {
		PRE, POST
	}

	private T value;

	public RefSupport(T inValue) {
		value = inValue;
	}

	public RefSupport() {
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(T value) {
		this.value = value;
	}

	public T setValue(T value, ReturnPreOrPostValue preOrPost) {
		T preValue = this.value;
		this.value = value;
		return (preOrPost == ReturnPreOrPostValue.POST ? this.value : preValue);
	}

	/**
	 * @return the value
	 */
	public T getValue() {
		return this.value;
	}
}
