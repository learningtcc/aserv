/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-7-9 下午3:36:15
 */
package com.absir.aserv.system.crud;

import com.absir.aserv.crud.*;
import com.absir.aserv.developer.Pag;
import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.menu.MenuContextUtils;
import com.absir.aserv.support.developer.JCrudField;
import com.absir.aserv.system.bean.JUpload;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.crud.value.IUploadProcessor;
import com.absir.aserv.system.crud.value.IUploadRule;
import com.absir.aserv.system.crud.value.UploadRule;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.domain.DSequence;
import com.absir.aserv.system.helper.HelperString;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.aserv.system.service.utils.SecurityServiceUtils;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.*;
import com.absir.bean.lang.LangCodeUtils;
import com.absir.client.helper.HelperClient;
import com.absir.context.core.ContextUtils;
import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperFileName;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAccessor.Accessor;
import com.absir.core.util.UtilContext;
import com.absir.core.util.UtilPipedStream;
import com.absir.orm.value.JoEntity;
import com.absir.property.PropertyErrors;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.Input;
import com.absir.servlet.IFilter;
import com.absir.servlet.InputRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 注意设置UploadPath变量被设置后,可能影响期望的上传|修改|删除逻辑
 */
@Base
@Bean
public class UploadCrudFactory implements ICrudFactory, ICrudProcessorInput<FileItem>, IFilter {

    public static final UploadCrudFactory ME = BeanFactoryUtils.get(UploadCrudFactory.class);

    public static final String MAX_FILE_SIZE = LangCodeUtils.get("文件太大", UploadCrudFactory.class);

    public static final String MIN_FILE_SIZE = LangCodeUtils.get("文件太小", UploadCrudFactory.class);

    public static final String ERROR_FILE_TYPE = LangCodeUtils.get("文件格式错误", UploadCrudFactory.class);

    public static final String RECORD = "UPLOAD@";

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    public static final String UPLOAD = "upload/";
    public static final int UPLOAD_LENGTH = UPLOAD.length();
    protected static final Logger LOGGER = LoggerFactory.getLogger(UploadCrudFactory.class);
    protected static final String[] UPLOAD_ROLE_REPLACES = new String[]{":name", ":id", ":ext", ":rand"};
    private static String uploadUrl;
    private static String uploadPath;
    private static String protectedUrl;
    private static String protectedPath;
    @Value(value = "upload.passTime")
    private static long uploadPassTime = 3600000;
    @Domain
    private DSequence nameSequence;
    @Value("upload.image.extension")
    private String imageExtension = "gif|jpg|jpeg|png|bmp";
    @Value("upload.manager.dir")
    private String managerDir = "";
    @Orders
    @Inject(type = InjectType.Selectable)
    private IUploadProcessor[] uploadProcessors;
    @Value("upload.cache.control")
    private String cacheControl = "max-age=3600";

    public static long getUploadPassTime() {
        return uploadPassTime;
    }

    public static MultipartUploader getMultipartUploader(Object[] parameters) {
        if (parameters.length > 0) {
            Object uploadVerify = parameters[0];
            if (!(uploadVerify instanceof MultipartUploader)) {
                synchronized (parameters) {
                    if (!(parameters[0] instanceof MultipartUploader)) {
                        parameters[0] = uploadVerify = new MultipartUploader(parameters);
                    }
                }
            }

            return (MultipartUploader) uploadVerify;
        }

        return null;
    }

    public static FileItem getUploadFile(InputRequest input, String name) {
        List<FileItem> fileItems = input.parseParameterMap().get(name);
        return fileItems == null || fileItems.isEmpty() ? null : fileItems.get(0);
    }

    public static void verifyMultipartFile(String field, FileItem file, Object[] parameters, PropertyErrors errors, Input input) {
        String extension = HelperFileName.getExtension(file.getName()).toLowerCase();
        if (KernelString.isEmpty(extension)) {
            errors.rejectValue(field, input.getLangMessage(ERROR_FILE_TYPE), null);
            return;
        }

        MultipartUploader uploader = getMultipartUploader(parameters);
        if (uploader == null) {
            if (!Pag.CONFIGURE.getUploadExtension().contains(extension)) {
                errors.rejectValue(field, input.getLangMessage(ERROR_FILE_TYPE), null);
                return;
            }

            if (Pag.CONFIGURE.getUploadSize() < file.getSize()) {
                errors.rejectValue(field, input.getLangMessage(MAX_FILE_SIZE), null);
                return;
            }

        } else {
            uploader.verify(extension, field, file, errors, input);
        }
    }

    public static String getConfigUrlPath(String expression, String route, String defaultUrlPath, boolean url) {
        String urlPath = BeanFactoryUtils.getBeanConfig().getExpressionValue(expression, null, String.class);
        if (KernelString.isEmpty(urlPath)) {
            urlPath = route + defaultUrlPath;

        } else {
            if (url) {
                if (urlPath.indexOf(':') <= 0) {
                    urlPath = HelperFileName.concat(route, urlPath);
                }

            } else {
                urlPath = HelperFileName.concat(route, urlPath);
            }
        }

        return urlPath;
    }

    public String getImageExtension() {
        return imageExtension;
    }

    public String getManagerDir() {
        return managerDir;
    }

    public boolean isUploadUrl(String url) {
        return url.startsWith(uploadUrl);
    }

    public String forUploadUrl() {
        return uploadUrl;
    }

    public String getUploadUrl(String filePath) {
        if (KernelString.isEmpty(filePath)) {
            return null;
        }

        if (filePath.charAt(0) == '@') {
            return getProtectedUrl(filePath);
        }

        return uploadUrl + filePath;
    }

    public String getProtectedUrl(String filePath) {
        if (filePath.charAt(0) == '@') {
            filePath = filePath.substring(1);
        }

        return protectedUrl + filePath;
    }

    public File getUploadFile(String filePath) {
        if (KernelString.isEmpty(filePath)) {
            return null;
        }

        if (filePath.charAt(0) == '@') {
            return getProtectedFile(filePath);
        }

        return new File(uploadPath + HelperFileName.normalize(filePath));
    }

    public File getProtectedFile(String filePath) {
        if (filePath.charAt(0) == '@') {
            filePath = filePath.substring(1);
        }

        return new File(protectedPath + HelperFileName.normalize(filePath));
    }

    public InputStream getUploadStream(String filePath) throws IOException {
        File file = getUploadFile(filePath);
        return file.exists() ? new FileInputStream(file) : null;
    }

    public InputStream getProtectedStream(String filePath) throws IOException {
        File file = getProtectedFile(filePath);
        return file.exists() ? new FileInputStream(file) : null;
    }

    @Started
    protected void started() {
        uploadUrl = getConfigUrlPath("resource.upload.url", MenuContextUtils.getSiteRoute(), "upload/", true);
        uploadPath = getConfigUrlPath("resource.upload.path", BeanFactoryUtils.getBeanConfig().getResourcePath(), "public/upload/", false);
        protectedUrl = getConfigUrlPath("resource.protected.url", MenuContextUtils.getSiteRoute(), "api/entity/resource/", true);
        protectedPath = getConfigUrlPath("resource.protected.path", BeanFactoryUtils.getBeanConfig().getResourcePath(), "protected/", false);
    }

    public String randUploadFile(int hashCode) {
        return randUploadFile(hashCode, true);
    }

    public String randUploadFile(int hashCode, boolean uploadDir) {
        String name = nameSequence.getNextHexId();
        return uploadDir ? (DATE_FORMAT.format(UtilContext.getCurrentDate()) + '/' + name) : name;
    }

    public void upload(String uploadFile, InputStream inputStream) throws IOException {
        if (HelperFile.write(getUploadFile(uploadFile), inputStream) <= 0) {
            throw new ServerException(ServerStatus.ON_ERROR, "Could not upload file = " + uploadFile);
        }
    }

    public void delete(String uploadFile) {
        if (uploadFile != null) {
            HelperFile.deleteQuietly(getUploadFile(uploadFile));
        }
    }

    /**
     * 远程下载
     */
    public String remoteDownload(String url, String defaultExtension, JiUserBase user) {
        String extension = HelperFileName.getExtension(url);
        if (KernelString.isEmpty(extension)) {
            extension = defaultExtension;
            if (KernelString.isEmpty(extension)) {
                return null;
            }
        }

        try {
            return uploadExtension(extension, HelperClient.openConnection((HttpURLConnection) (new URL(url)).openConnection()), user);

        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 上传扩展名内容
     */
    public String uploadExtension(String extension, InputStream inputStream, JiUserBase user) throws IOException {
        if (KernelString.isEmpty(extension)) {
            throw new ServerException(ServerStatus.ON_DENIED, "extension");
        }

        extension = extension.toLowerCase();
        if (!Pag.CONFIGURE.getUploadExtension().contains(extension)) {
            throw new ServerException(ServerStatus.ON_DENIED, "extension");
        }

        long fileSize = inputStream.available();
        if (Pag.CONFIGURE.getUploadSize() < fileSize) {
            throw new ServerException(ServerStatus.ON_DENIED, "size");
        }

        JUpload upload = new JUpload();
        upload.setFileType(extension);
        upload.setFileSize(fileSize);
        inputStream = uploadProcessor(extension, upload, inputStream);
        String uploadFile = randUploadFile(inputStream.hashCode()) + '.' + extension;
        upload(uploadFile, inputStream);
        long contextTime = ContextUtils.getContextTime();
        upload.setCreateTime(contextTime);
        upload.setPassTime(contextTime + uploadPassTime);
        if (user != null) {
            upload.setUserId(user.getUserId());
        }

        upload.setFilename(uploadFile);
        CrudServiceUtils.merge("JUpload", null, upload, true, user, null);
        return uploadFile;
    }

    /**
     * 上传文件处理(加水印,压缩,加密等)
     */
    protected InputStream uploadProcessor(String extension, JUpload upload, InputStream inputStream) {
        if (uploadProcessors != null) {
            for (IUploadProcessor uploadProcessor : uploadProcessors) {
                inputStream = uploadProcessor.process(extension, upload, inputStream);
                if (upload.getFileType() != null) {
                    break;
                }
            }

            if (inputStream == null) {
                throw new ServerException(ServerStatus.ON_DENIED, "process");
            }
        }

        return inputStream;
    }

    /**
     * 创建数据库目录
     */
    protected void createDirPath(String dirPath, long userId, long createTime) {
        dirPath = HelperFileName.getFullPathNoEndSeparator(dirPath);
        String dir = HelperFileName.getPath(dirPath);
        if (KernelString.isEmpty(dir)) {
            dir = "";

        } else {
            dirPath = dirPath.substring(dir.length());
        }

        if (BeanService.ME.selectQuerySingle("SELECT o.id FROM JUpload o WHERE o.dirPath = ? AND o.filename = ?", dir, dirPath) == null) {
            JUpload upload = new JUpload();
            upload.setDirPath(dir);
            upload.setFilename(dirPath);
            upload.setUserId(userId);
            upload.setCreateTime(createTime);
            BeanService.ME.persist(upload);
            if (dir != "") {
                createDirPath(dir, userId, createTime);
            }
        }
    }

    /**
     * JUpload实体处理
     */
    public void crud(JUpload upload, Crud crud, CrudHandler handler) {
        if (KernelString.isEmpty(upload.getFileType())) {
            return;
        }

        if (crud == Crud.CREATE) {
            upload.setImaged(imageExtension.contains(upload.getFileType()));
            String filename = upload.getFilename();
            String dirPath = HelperFileName.getPath(filename);
            if (KernelString.isEmpty(dirPath)) {
                upload.setDirPath("");

            } else {
                createDirPath(dirPath, SecurityServiceUtils.getUserId(), System.currentTimeMillis());
                upload.setDirPath(dirPath);
                upload.setFilename(filename.substring(dirPath.length()));
            }

        } else if (crud == Crud.DELETE) {
            Session session = BeanDao.getSession();
            Iterator iterate = QueryDaoUtils.createQueryArray(session, "SELECT o.id FROM JUploadCite o WHERE o.upload.id = ?", upload.getId()).iterate();
            if (iterate.hasNext()) {
                session.cancelQuery();
                upload.setPassTime(0);
                session.merge(upload);
            }
        }
    }

    /**
     * 上传文件
     */
    public List<String> uploads(JiUserBase user, HttpServletRequest request) throws IOException, FileUploadException {
        List<String> paths = new ArrayList<String>();
        FileItemIterator iterator = InputRequest.SERVLET_FILE_UPLOAD_DEFAULT.getItemIterator(request);
        while (iterator.hasNext()) {
            FileItemStream fileItem = iterator.next();
            if (!KernelString.isEmpty(fileItem.getName())) {
                paths.add(UploadCrudFactory.ME.uploadExtension(HelperFileName.getExtension(fileItem.getName()), fileItem.openStream(), user));
            }
        }

        return paths;
    }

    /**
     * 获取管理文件路径
     */
    public String getDirPath(String path) {
        if (KernelString.isEmpty(path) || path.equals("/")) {
            path = "";

        } else {
            if (path.charAt(0) == '/') {
                path = path.substring(1);
            }

            if (path.charAt(path.length() - 1) != '/') {
                path = path + '/';
            }
        }

        if (!KernelString.isEmpty(managerDir)) {
            path = managerDir + path;
        }

        return path;
    }

    /**
     * 文件管理
     */
    public List<JUpload> list(String path, String order) {
        path = getDirPath(path);
        String listHql = null;
        if (order != null) {
            order = order.toLowerCase();
            if (order.equals("name")) {
                listHql = "SELECT o FROM JUpload o WHERE o.dirPath = ? ORDER BY o.filename";

            } else if (order.equals("type")) {
                listHql = "SELECT o FROM JUpload o WHERE o.dirPath = ? ORDER BY o.fileType";

            } else if (order.equals("size")) {
                listHql = "SELECT o FROM JUpload o WHERE o.dirPath = ? ORDER BY o.fileSize";
            }
        }

        if (listHql == null) {
            listHql = "SELECT o FROM JUpload o WHERE o.dirPath = ?";
        }

        return (List<JUpload>) BeanService.ME.selectQuery(listHql, path);
    }

    /**
     * 检测文件夹是否为空
     */
    public boolean isEmpty(String path) {
        path = getDirPath(path);
        return BeanService.ME.selectQuerySingle("SELECT o.id FROM JUpload o WHERE o.dirPath = ?", path) == null;
    }

    @Override
    public boolean isMultipart() {
        return true;
    }

    @Override
    public FileItem crud(CrudProperty crudProperty, PropertyErrors errors, CrudHandler handler, JiUserBase user, Input input) {
        if (handler.getCrud() != Crud.DELETE) {
            String field = handler.getFilter().getPropertyPath();
            if (input instanceof InputRequest) {
                FileItem file = getUploadFile((InputRequest) input, field + "_file");
                if (file != null && !KernelString.isEmpty(file.getName())) {
                    verifyMultipartFile(field, file, crudProperty.getjCrud().getParameters(), errors, input);
                    return file;
                }
            }
        }

        return null;
    }

    @Override
    public void crud(CrudProperty crudProperty, Object entity, CrudHandler handler, JiUserBase user, FileItem requestBody) {
        if (requestBody == null) {
            if (handler.getCrudRecord() != null) {
                String uploadFile = (String) crudProperty.get(entity);
                if (!KernelString.isEmpty(uploadFile)) {
                    handler.getCrudRecord().put(RECORD + uploadFile, Boolean.TRUE);
                }
            }

        } else {
            Field field = crudProperty.getAccessor().getField();
            if (field != null && field.getType().isAssignableFrom(FileItem.class)) {
                crudProperty.set(entity, requestBody);
                return;
            }

            String uploadFile = (String) crudProperty.get(entity);
            if (!KernelString.isEmpty(uploadFile)) {
                if (handler.getCrudRecord() == null || !handler.getCrudRecord().containsKey(RECORD + uploadFile)) {
                    delete(uploadFile);
                }

                uploadFile = null;
            }

            InputStream uploadStream = null;
            String extensionName = HelperFileName.getExtension(requestBody.getName());
            try {
                Object[] parameters = crudProperty.getjCrud().getParameters();
                MultipartUploader multipartUploader = parameters.length == 0 ? null : (MultipartUploader) parameters[0];
                if (multipartUploader != null) {
                    if (multipartUploader.ruleName == null) {
                        String ruleName = null;
                        Accessor accessor = crudProperty.getAccessor();
                        if (accessor != null) {
                            UploadRule uploadRule = BeanConfigImpl.getAccessorAnnotation(accessor, UploadRule.class, false);
                            if (uploadRule != null) {
                                ruleName = uploadRule.value();
                                multipartUploader.ided = ruleName.contains(":id");
                                if (multipartUploader.ided && KernelString.isEmpty(HelperFileName.getPath(ruleName))) {
                                    ruleName = "entity/" + ruleName;
                                }

                                multipartUploader.rand = ruleName.contains(":rand");
                            }
                        }

                        multipartUploader.ruleName = ruleName == null ? "" : ruleName;
                    }

                    if ("".equals(multipartUploader.ruleName)) {
                        multipartUploader = null;

                    } else {
                        String identity = "";
                        if (multipartUploader.ided) {
                            Object id = CrudServiceUtils.identifier(handler.getCrudEntity().getJoEntity().getEntityName(), entity, handler.doCreate());
                            if (id != null) {
                                identity = DynaBinderUtils.getParamFromValue(id);
                            }
                        }

                        String rand = "";
                        if (multipartUploader.rand) {
                            rand = randUploadFile(requestBody.hashCode(), false);
                        }

                        uploadFile = HelperString.replaceEach(multipartUploader.ruleName, UPLOAD_ROLE_REPLACES, new String[]{crudProperty.getName(), identity, extensionName, rand});
                    }
                }

                if (multipartUploader == null && entity instanceof IUploadRule) {
                    IUploadRule uploadRule = (IUploadRule) entity;
                    uploadFile = uploadRule.getUploadRuleName(crudProperty.getName(), extensionName);
                    if (uploadFile != null) {
                        uploadStream = uploadRule.processInputStream(crudProperty.getName(), requestBody.getInputStream(), extensionName);
                    }
                }

                if (KernelString.isEmpty(uploadFile)) {
                    uploadFile = randUploadFile(requestBody.hashCode()) + '.' + extensionName;
                }

                if (uploadStream == null) {
                    uploadStream = requestBody.getInputStream();
                }

                uploadStream = uploadProcessor(extensionName, null, uploadStream);
                upload(uploadFile, uploadStream);

            } catch (IOException e) {
                LOGGER.error("upload error", e);
            }

            crudProperty.set(entity, uploadFile);
        }
    }

    @Override
    public void crud(CrudProperty crudProperty, Object entity, CrudHandler crudHandler, JiUserBase user, Input input) {
        if (crudHandler.getCrud() == Crud.DELETE) {
            String uploadFile = (String) crudProperty.get(entity);
            if (!KernelString.isEmpty(uploadFile)) {
                delete(uploadFile);
            }
        }
    }

    @Override
    public ICrudProcessor getProcessor(JoEntity joEntity, JCrudField crudField) {
        return ME;
    }

    /**
     * 上传文件静态文件服务(Cached)
     */
    @Override
    public boolean doFilter(String uri, HttpServletRequest req, HttpServletResponse res) throws Throwable {
        if (uri.length() > UPLOAD_LENGTH && uri.startsWith(UPLOAD)) {
            uri = uri.substring(UPLOAD.length());
            if (uri.charAt(0) != '@') {
                File file = getUploadFile(uri);
                if (file.exists()) {
                    String modified = String.valueOf(file.lastModified());
                    String ifModifiedSince = req.getHeader("If-Modified-Since");
                    if (ifModifiedSince != null && modified.equals(ifModifiedSince)) {
                        res.setStatus(304);
                        return true;
                    }

                    res.addHeader("cache-control", cacheControl);
                    res.addHeader("last-modified", modified);
                    InputStream inputStream = null;
                    try {
                        inputStream = new FileInputStream(file);
                        HelperIO.copy(inputStream, res.getOutputStream());

                    } finally {
                        if (inputStream != null) {
                            UtilPipedStream.closeCloseable(inputStream);
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public static class MultipartUploader {

        private long minSize;

        private long maxSize;

        private String[] extensions;

        private String ruleName;

        private boolean ided;

        private boolean rand;

        public MultipartUploader(Object[] parameters) {
            int last = parameters.length - 1;
            if (last > 2) {
                last = 2;
            }

            for (int i = 0; last >= 0; i++, last--) {
                switch (i) {
                    case 0:
                        Object extension = parameters[last];
                        if (extension instanceof String && !"".equals(extension)) {
                            extensions = ((String) extension).toLowerCase().split(",");
                        }

                        break;

                    case 1:
                        maxSize = (long) (KernelDyna.to(parameters[last], float.class) * 1024);
                        break;

                    case 3:
                        minSize = (long) (KernelDyna.to(parameters[last], float.class) * 1024);
                        break;

                    default:
                        break;
                }
            }
        }

        public long getMinSize() {
            return minSize;
        }

        public long getMaxSize() {
            return maxSize;
        }

        public String[] getExtensions() {
            return extensions;
        }

        public String getRuleName() {
            return ruleName;
        }

        public boolean isIded() {
            return ided;
        }

        public void verify(String extension, String field, FileItem file, PropertyErrors errors, Input input) {
            if (extensions != null) {
                if (!KernelArray.contain(extensions, extension)) {
                    errors.rejectValue(field, input.getLangMessage(ERROR_FILE_TYPE), null);
                    return;
                }

            } else {
                if (!Pag.CONFIGURE.getUploadExtension().contains(extension)) {
                    errors.rejectValue(field, input.getLangMessage(ERROR_FILE_TYPE), null);
                    return;
                }
            }

            if (maxSize > 0) {
                if (file.getSize() > maxSize) {
                    errors.rejectValue(field, input.getLangMessage(MAX_FILE_SIZE), null);
                    return;
                }

            } else if (maxSize == 0) {
                if (Pag.CONFIGURE.getUploadSize() < file.getSize()) {
                    errors.rejectValue(field, input.getLangMessage(MAX_FILE_SIZE), null);
                    return;
                }
            }

            if (minSize > 0 && file.getSize() < minSize) {
                errors.rejectValue(field, input.getLangMessage(MIN_FILE_SIZE), null);
                return;
            }
        }
    }
}
