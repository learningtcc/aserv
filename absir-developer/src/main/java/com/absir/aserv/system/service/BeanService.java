/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-28 下午5:24:59
 */
package com.absir.aserv.system.service;

import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.system.bean.value.JiOrdinal;
import com.absir.aserv.system.service.impl.BeanServiceBase;
import com.absir.aserv.system.service.impl.BeanServiceImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.core.kernel.KernelLang.CallbackTemplate;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.TransactionAttribute;
import com.absir.orm.transaction.TransactionContext;
import com.absir.orm.transaction.TransactionUtils;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@SuppressWarnings("rawtypes")
@Inject
public interface BeanService {

    public static final BeanServiceBase ME = BeanFactoryUtils.get(BeanServiceImpl.class);

    public static final TransactionAttribute TRANSACTION_READ_ONLY = new TransactionAttribute(true, null, false, true,
            0);

    public static final TransactionAttribute TRANSACTION_READ_WRITE = new TransactionAttribute(false,
            new Class[]{Throwable.class}, false, true, 0);

    public static final Comparator<JiOrdinal> COMPARATOR = new Comparator<JiOrdinal>() {

        @Override
        public int compare(JiOrdinal lhs, JiOrdinal rhs) {
            return lhs.getOrdinal() - rhs.getOrdinal();
        }
    };

    @Transaction(readOnly = true)
    public <T> T get(Class<T> entityClass, Serializable id);

    @Transaction(readOnly = true)
    public Object get(String entityName, Serializable id);

    @Transaction(readOnly = true)
    public <T> T get(String entityName, Class<T> entityClass, Serializable id);

    @Transaction(readOnly = true)
    public <T> T find(Class<T> entityClass, Object id);

    @Transaction(readOnly = true)
    public Object find(String entityName, Object id);

    @Transaction(readOnly = true)
    public <T> T find(String entityName, Class<T> entityClass, Object id);

    @Transaction(readOnly = true)
    public <T> T find(Class<T> entityClass, Object... conditions);

    @Transaction(readOnly = true)
    public <T> T find(String entityName, Class<T> entityClass, Object... conditions);

    @Transaction
    public void persist(Object entity);

    @Transaction
    public void persist(String entityName, Object entity);

    @Transaction
    public void update(Object entity);

    @Transaction
    public void update(String entityName, Object entity);

    @Transaction
    public <T> T merge(T entity);

    @Transaction
    public <T> T merge(String entityName, T entity);

    @Transaction
    public void delete(Object entity);

    @Transaction
    public void delete(String entityName, Object entity);

    @Transaction(readOnly = true)
    public <T> List<T> findAll(Class<T> entityClass);

    @Transaction(readOnly = true)
    public List findAll(String entityName);

    @Transaction(readOnly = true)
    public <T> List<T> findAll(String entityName, Class<T> entityClass);

    @Transaction(readOnly = true)
    public Set<Object> getSearch(String entityName, Object... ids);

    @Transaction(readOnly = true)
    public Set<Serializable> getSearchIds(String entityName, Object... ids);

    @Transaction(readOnly = true)
    public List list(String entityName, String queue, int firstResult, int maxResults, Object... conditions);

    @Transaction(readOnly = true)
    public List list(String entityName, String queue, JdbcPage jdbcPage, Object... conditions);

    @Transaction
    public void persists(Collection<?> entities);

    @Transaction
    public void persists(String entityName, Collection<?> entities);

    @Transaction
    public void mergers(Collection<?> entities);

    @Transaction
    public void mergers(String entityName, Collection<?> entities);

    @Transaction(readOnly = true)
    public List selectQuery(String queryString, Object... parameters);

    @Transaction(readOnly = true)
    public Object selectQuerySingle(String queryString, Object... parameters);

    @Transaction
    public int executeUpdate(String queryString, Object... parameters);

    public static class MERGE {

        public static Object merge(Object entity, Serializable id, CallbackTemplate<Object> merge) {
            if (entity instanceof IMergeService) {
                return ((IMergeService) entity).merge(merge, id);

            } else {
                SessionFactory sessionFactory = SessionFactoryUtils.getSessionFactory(entity.getClass());
                if (sessionFactory != null) {
                    TransactionContext transactionContext = TransactionUtils.getTransactionContext(sessionFactory);
                    transactionContext.add(BeanService.TRANSACTION_READ_WRITE);
                    Throwable ex = null;
                    try {
                        Session session = sessionFactory.getCurrentSession();
                        session.load(entity, id);
                        merge.doWith(entity);
                        session.flush();

                    } catch (Exception e) {
                        ex = e;

                    } finally {
                        transactionContext.closeCurrent(ex, null);
                    }
                }
            }

            return entity;
        }
    }

}
