/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月13日 下午4:08:45
 */
package com.absir.aserv.game.context;

import com.absir.aserv.configure.xls.XlsBase;
import com.absir.aserv.configure.xls.XlsDao;
import com.absir.aserv.configure.xls.XlsUtils;
import com.absir.aserv.configure.xls.value.XaWorkbook;
import com.absir.aserv.game.bean.JbPlayer;
import com.absir.aserv.game.bean.JbPlayerA;
import com.absir.aserv.system.context.value.CList;
import com.absir.aserv.system.context.value.CMapList;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.kernel.KernelString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AGameComponent<P extends JbPlayerContext, S extends JbServerContext> {

    // 功能组件
    public static final AGameComponent ME = BeanFactoryUtils.get(AGameComponent.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(AGameComponent.class);

    public final Class<P> PLAYER_CONTEXT_CLASS;

    public final Class<S> SERVER_CONTEXT_CLASS;

    public final Class<? extends JbPlayer> PLAYER_CLASS;

    public final Class<P> PLAYERA_CLASS;

    // 全部在线角色
    public final Map<Long, P> PLAYER_CONTEXT_MAP;

    // 全部在线服务
    public final Map<Long, S> SERVER_CONTEXT_MAP;

    private int Compo;

    /**
     * 初始化
     */
    public AGameComponent() {
        Class[] args = KernelClass.argumentClasses(getClass());
        PLAYER_CONTEXT_CLASS = args[0];
        SERVER_CONTEXT_CLASS = args[1];
        args = KernelClass.argumentClasses(PLAYER_CONTEXT_CLASS);
        PLAYER_CLASS = args[0];
        PLAYERA_CLASS = args[1];
        PLAYER_CONTEXT_MAP = (Map<Long, P>) (Object) ContextUtils.getContextFactory()
                .getContextMap(PLAYER_CONTEXT_CLASS);
        SERVER_CONTEXT_MAP = (Map<Long, S>) (Object) ContextUtils.getContextFactory()
                .getContextMap(SERVER_CONTEXT_CLASS);
    }

    protected List<XlsField> xlsFields;

    protected static class XlsField {

        protected Class<? extends XlsBase> xlsType;

        protected String workbook;

        protected Field field;

        protected int type;

        protected void setXlsDao(Object obj, XlsDao xlsDao) throws Throwable {
            if (type == 1) {
                field.set(obj, xlsDao);

            } else if (type == 2) {
                CList list = new CList();
                list.add(xlsDao.getAll());
                field.set(obj, list);

            } else if (type == 3) {
                CMapList mapList = new CMapList();
                mapList.addCollection(xlsDao.getAll());
                field.set(obj, mapList);
            }
        }

    }

    /**
     * 载入配置
     */
    @Inject
    public void reloadComponent() throws IOException {
        if (xlsFields == null) {
            xlsFields = new ArrayList<XlsField>();
            KernelReflect.doWithDeclaredFields(getClass(), new KernelLang.CallbackBreak<Field>() {
                @Override
                public void doWith(Field template) throws KernelLang.BreakException {
                    Class fieldType = template.getType();
                    int type = 0;
                    if (XlsDao.class.isAssignableFrom(fieldType)) {
                        type = 1;

                    } else if (fieldType == CList.class) {
                        type = 2;

                    } else if (fieldType == CMapList.class) {
                        type = 3;
                    }

                    if (type > 0) {
                        Class[] classes = KernelClass.argumentClasses(template.getGenericType(), true);
                        if (classes.length > 0) {
                            Class xlsType = classes[0];
                            if (xlsType != null && XlsBase.class.isAssignableFrom(xlsType)) {
                                XlsField xlsField = new XlsField();
                                xlsField.xlsType = xlsType;
                                XaWorkbook xaWorkbook = xlsField.xlsType.getAnnotation(XaWorkbook.class);
                                String workbook = xaWorkbook == null ? null : xaWorkbook.workbook();
                                xlsField.workbook = KernelString.isEmpty(workbook) ? xlsType.getSimpleName() : workbook;
                                template.setAccessible(true);
                                xlsField.field = template;
                                xlsField.type = type;
                                xlsFields.add(xlsField);
                            }
                        }
                    }
                }
            });
        }

        for (XlsField xlsField : xlsFields) {
            XlsDao xlsDao = XlsUtils.getReloadXlsDao(xlsField.xlsType);
            LOGGER.info("loaded " + xlsField.workbook + " => " + xlsField.xlsType + ", size = " + xlsDao.getAll().size());
            try {
                xlsField.setXlsDao(this, xlsDao);

            } catch (Throwable e) {
                LOGGER.error("reload set " + xlsField.field + " " + xlsDao + " error", e);
            }
        }
    }

    public P findPlayerContext(long playerId) {
        return PLAYER_CONTEXT_MAP.get(playerId);
    }

    public S findServerContext(long serverId) {
        return SERVER_CONTEXT_MAP.get(serverId);
    }

    public abstract JbPlayer createPlayer();

    public abstract JbPlayerA createPlayerA();

}
