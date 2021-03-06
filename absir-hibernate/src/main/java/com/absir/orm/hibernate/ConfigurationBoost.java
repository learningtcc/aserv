/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-10-14 下午7:13:59
 */
package com.absir.orm.hibernate;

import com.absir.context.lang.LangBundle;
import com.absir.core.kernel.KernelCharset;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.BasicType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

public class ConfigurationBoost extends Configuration {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationBoost.class);

    protected static SessionFactoryBoost sessionFactoryBoost;

    private boolean boostLocale;

    public ConfigurationBoost() {
        super();
    }

    public ConfigurationBoost(BootstrapServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    public ConfigurationBoost(MetadataSources metadataSources) {
        super(metadataSources);
    }

    @Override
    protected void reset() {
        super.reset();
        if (sessionFactoryBoost.getBasicTypes() != null && sessionFactoryBoost.getBasicTypes().length > 0) {
            try {
                for (BasicType basicType : sessionFactoryBoost.getBasicTypes()) {
                    LOGGER.info("register basic type => " + basicType.getClass());
                    registerTypeOverride(basicType);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加本地化基础实体
     */
    public void boostLocale() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<?xml version=\"1.0\"?>\r\n");
        stringBuilder.append("<!DOCTYPE hibernate-mapping PUBLIC\r\n");
        stringBuilder.append("\"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\r\n");
        stringBuilder.append("\"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">\r\n");
        stringBuilder.append("<hibernate-mapping>\r\n");
        stringBuilder.append("<class entity-name=\"JLocale\">\r\n");
        stringBuilder.append("<composite-id mapped=\"true\">\r\n");
        stringBuilder
                .append("<key-property name=\"entity\" type=\"java.lang.String\" length=\"63\"></key-property>\r\n");
        stringBuilder.append("<key-property name=\"id\" type=\"java.lang.String\" length=\"255\"></key-property>\r\n");
        stringBuilder.append("<key-property name=\"name\" type=\"java.lang.String\" length=\"63\"></key-property>\r\n");
        stringBuilder.append("<generator class=\"assigned\"></generator>\r\n");
        stringBuilder.append("</composite-id>\r\n");
        stringBuilder.append("<property name=\"relateId\" type=\"java.lang.String\" length=\"255\"/>\r\n");
        Map<Integer, Locale> codeMaplocale = LangBundle.ME.getCodeMaplocale();
        if (codeMaplocale != null) {
            for (Integer code : codeMaplocale.keySet()) {
                stringBuilder
                        .append("<property name=\"_" + code + "\" type=\"java.lang.String\" length=\"65536\" />\r\n");
            }
        }

        stringBuilder.append("</class>\r\n");
        stringBuilder.append("</hibernate-mapping>");
        addInputStream(new ByteArrayInputStream(stringBuilder.toString().getBytes(KernelCharset.UTF8)));
        boostLocale = true;
    }

    @Override
    public SessionFactory buildSessionFactory() throws HibernateException {
        sessionFactoryBoost.beforeBuildConfiguration(this, boostLocale);
        return super.buildSessionFactory();
    }

    @Override
    public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
        sessionFactoryBoost.beforeBuildConfiguration(this, boostLocale);
        SessionFactory sessionFactory = super.buildSessionFactory(serviceRegistry);
        sessionFactoryBoost.afterBuildConfiguration(this, (SessionFactoryImpl) sessionFactory);
        return sessionFactory;
    }

    /**
     * 数据库升级
     *
     * @param connectionProvider
     */
    public void buildUpdateConnectionSql(ConnectionProvider connectionProvider) {
        try {
            Connection connection = connectionProvider.getConnection();
            try {

            } finally {
                connectionProvider.closeConnection(connection);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("connection error ", e);
        }
    }
}
