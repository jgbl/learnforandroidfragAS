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

/**
 * Changed by J.M.Goebel Copyright (C) 2015
 * GPL 3
 *  This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.de.jmg.learn.chart;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import org.de.jmg.learn.MainActivity;
import org.de.jmg.learn.R;
import org.de.jmg.learn.vok.Vokabel;
import org.de.jmg.lib.lib;
import org.de.jmg.lib.lib.libString;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;

/**
 * Sales demo bar chart.
 */
public class LearnBarChart extends AbstractDemoChart {

	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	public String getName() {
		return "Sales horizontal bar chart";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	public String getDesc() {
		return "The monthly sales for the last 2 years (horizontal bar chart)";
	}

	/**
	 * Executes the chart demo.
	 * 
	 * @param context
	 *            the context
	 * @return the built intent
	 */
	public Intent execute(Context context) {
		MainActivity Main = (MainActivity) context;
		BuildChart(Main);
		
		return ChartFactory.getBarChartIntent(context,
				buildBarDataset(titles, values), renderer, Type.DEFAULT);
	}

	public View getView(MainActivity Main) {
		BuildChart(Main);
		
		return ChartFactory.getBarChartView(Main,
				buildBarDataset(titles, values), renderer, Type.DEFAULT);
	}
	
	String[] titles;
	List<double[]> values;
	XYMultipleSeriesRenderer renderer;
	
	public void BuildChart(MainActivity Main)
	{
		Vokabel vok = Main.vok;
		File F;
		Uri uri;
		String name = "";
		if (!libString.IsNullOrEmpty(vok.getFileName()))
		{
			F = new File(vok.getFileName());
			name = F.getName();
		}
		else if (vok.getURI()!=null)
		{
			uri = vok.getURI();
			name = lib.dumpUriMetaData(Main, uri);
			if (name.contains(":")) name = name.split(":")[0];
		}
		titles = new String[] { name };
		values = new ArrayList<double[]>();
		double v[] = new double[14];
		for (int i = -6; i <= 6; i++) {
			v[i + 6] = vok.getLearned(i);
		}

		values.add(v);

		int[] colors = new int[] { Color.CYAN }; // Color.CYAN,Color.GREEN,Color.BLUE,Color.MAGENTA,Color.RED,Color.YELLOW};
		renderer = buildBarRenderer(colors);
		renderer.setOrientation(Orientation.HORIZONTAL);

		setChartSettings(renderer, Main.getString(R.string.LearnedVocabulary) + name,
				Main.getString(R.string.LearnIndex), Main.getString((R.string.Words)), 1, 14, 0, vok.getGesamtzahl(),
				Color.GREEN, Color.YELLOW);
		renderer.setBackgroundColor(Color.BLACK);
		renderer.setApplyBackgroundColor(true);
		renderer.setXLabels(0);
		renderer.setYLabels(10);

		for (int i = -6; i <= 6; i++) {
			renderer.addXTextLabel(i + 7, "" + i);

		}

		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer seriesRenderer = renderer
					.getSeriesRendererAt(i);
			seriesRenderer.setDisplayChartValues(true);
			seriesRenderer.setGradientEnabled(true);
			seriesRenderer.setGradientStart(0, Color.CYAN);
			seriesRenderer.setGradientStop(vok.getGesamtzahl(), Color.RED);
		}

	}

}
