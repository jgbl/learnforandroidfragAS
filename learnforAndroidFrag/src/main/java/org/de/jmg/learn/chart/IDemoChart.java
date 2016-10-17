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
package org.de.jmg.learn.chart;

import android.content.Context;
import android.content.Intent;

/**
 * Defines the demo charts.
 */
public interface IDemoChart {
	/** A constant for the name field in a list activity. */
	String NAME = "name";
	/** A constant for the description field in a list activity. */
	String DESC = "desc";

	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	String getName();

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	String getDesc();

	/**
	 * Executes the chart demo.
	 * 
	 * @param context
	 *            the context
	 * @return the built intent
	 */
	Intent execute(Context context);

}
