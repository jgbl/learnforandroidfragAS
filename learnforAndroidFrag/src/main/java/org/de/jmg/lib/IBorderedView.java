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


public interface IBorderedView {
    int BORDER_TOP = 0x00000001;
    int BORDER_RIGHT = 0x00000002;
    int BORDER_BOTTOM = 0x00000004;
    int BORDER_LEFT = 0x00000008;

    enum BottomOrTop
    {
        undefined, between, bottom, top, both
    }

    void setShowBorders(boolean showBorders, int BackColor);
    void init();

    BottomOrTop getScrollBottomOrTopReached() throws Exception;

}
