package org.de.jmg.lib;


public interface IBorderedView {
    final int BORDER_TOP = 0x00000001;
    final int BORDER_RIGHT = 0x00000002;
    final int BORDER_BOTTOM = 0x00000004;
    final int BORDER_LEFT = 0x00000008;

    enum BottomOrTop
    {
        undefined, between, bottom, top, both
    }

    void setShowBorders(boolean showBorders, int BackColor);
    void init();

    BottomOrTop getScrollBottomOrTopReached() throws Exception;

}
