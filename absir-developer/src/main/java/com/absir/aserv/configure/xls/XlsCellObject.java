/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-25 下午3:42:17
 */
package com.absir.aserv.configure.xls;

import com.absir.core.base.IBase;
import org.apache.poi.hssf.usermodel.HSSFCell;

public class XlsCellObject extends XlsCellBase {

    private Object obj;

    private XlsBase xlsBase;

    public XlsCellObject(Object obj, XlsBase xlsBase) {
        this.obj = obj;
        this.xlsBase = xlsBase;
    }

    @Override
    public void wirteHssfCell(HSSFCell hssfCell) {
        if (obj != null) {
            if (obj instanceof IBase) {
                obj = ((IBase<?>) obj).getId();
            }

            if (obj != null) {
                if (obj.getClass() == Boolean.class) {
                    obj = (Boolean) obj ? "1" : "0";
                }

                xlsBase.write(hssfCell, obj.toString());
            }
        }
    }
}
