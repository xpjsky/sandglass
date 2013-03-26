package net.xpjsky.sandglass.common.page;

import javax.faces.model.DataModel;

/**
 * Description Here
 *
 * @author Paddy
 * @version 12-8-24 下午8:59
 */
public class JSFPage extends DataModel implements Page {
    @Override
    public boolean isRowAvailable() {
        return false;
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public Object getRowData() {
        return null;
    }

    @Override
    public int getRowIndex() {
        return 0;
    }

    @Override
    public void setRowIndex(int i) {

    }

    @Override
    public Object getWrappedData() {
        return null;
    }

    @Override
    public void setWrappedData(Object o) {

    }
}
