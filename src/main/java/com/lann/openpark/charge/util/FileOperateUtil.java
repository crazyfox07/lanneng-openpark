package com.lann.openpark.charge.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class FileOperateUtil {
    private static final Logger log = LoggerFactory.getLogger(FileOperateUtil.class);

    private static final String REALNAME = "realName";

    private static final String STORENAME = "storeName";

    private static final String SIZE = "size";

    private static final String SUFFIX = "suffix";

    private static final String CONTENTTYPE = "contentType";
    private static final String CREATETIME = "createTime";
    private static final String UPLOADDIR = "uploadDir/";
    public static final String THIRD_PARK_DIR = "thirdParkImgs";

    private static String rename(String name) {
        Long now = Long.valueOf(Long.parseLong((new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date())));
        Long random = Long.valueOf((long) (Math.random() * now.longValue()));
        String fileName = now + "" + random;
        if (name.indexOf(".") != -1) {
            fileName = fileName + name.substring(name.lastIndexOf("."));
        }
        return fileName;
    }


    private static String zipName(String name) {
        String prefix = "";
        if (name.indexOf(".") != -1) {
            prefix = name.substring(0, name.lastIndexOf("."));
        } else {
            prefix = name;
        }
        return prefix + ".zip";
    }


    public static List<Map<String, Object>> upload(HttpServletRequest request, String[] params, Map<String, Object[]> values) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = mRequest.getFileMap();
        String uploadDir = request.getSession().getServletContext().getRealPath("/") + "uploadDir/";
        File file = new File(uploadDir);
        if (!file.exists()) {
            file.mkdir();
        }
        String fileName = null;
        int i = 0;
        for (Iterator<Map.Entry<String, MultipartFile>> it = fileMap.entrySet().iterator(); it.hasNext(); i++) {
            Map.Entry<String, MultipartFile> entry = it.next();
            MultipartFile mFile = entry.getValue();
            fileName = mFile.getOriginalFilename();
            String storeName = rename(fileName);
            String noZipName = uploadDir + storeName;
            String zipName = zipName(noZipName);

            ZipOutputStream outputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipName)));

            outputStream.putNextEntry(new ZipEntry(fileName));
            FileCopyUtils.copy(mFile.getInputStream(), outputStream);
            Map<String, Object> map = new HashMap<>();

            map.put("realName", zipName(fileName));
            map.put("storeName", zipName(storeName));
            map.put("size", Long.valueOf((new File(zipName)).length()));
            map.put("suffix", "zip");
            map.put("contentType", "application/octet-stream");
            map.put("createTime", new Date());

            for (String param : params) {
                map.put(param, ((Object[]) values.get(param))[i]);
            }
            result.add(map);
        }
        return result;
    }


    public static void download(HttpServletRequest request, HttpServletResponse response, String contentType, String filePath, String fileName) throws IOException {
        request.setCharacterEncoding("UTF-8");
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        long fileLength = (new File(filePath)).length();
        response.setContentType(contentType);
        response.setHeader("Content-disposition", "attachment;" + UserAgentUtil.encodeFileName(request, fileName));
        response.setHeader("Content-Length", String.valueOf(fileLength));
        bis = new BufferedInputStream(new FileInputStream(filePath));
        bos = new BufferedOutputStream((OutputStream) response.getOutputStream());
        byte[] buff = new byte[2048];
        int bytesRead;
        while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
            bos.write(buff, 0, bytesRead);
        }
        bis.close();
        bos.close();
    }


    public static List<String> saveThirdParkBase64Images(String baseRoot, String[] images, String suffix) {
        List<String> names = new ArrayList<>();
        Long now = Long.valueOf(Long.parseLong((new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date())));
        Long day = Long.valueOf(Long.parseLong((new SimpleDateFormat("yyyyMMdd")).format(new Date())));

        String path = baseRoot + "parkImgs" + File.separator + day + File.separator;
        for (int i = 0; i < images.length; i++) {
            String fileName = now.toString() + i + LanNengStringUtils.getRandomChar(6) + suffix.trim();
            String baseFile = images[i];
            if (baseFile.length() >= 500) {
                saveBase64File(baseFile, path, fileName);
                names.add("parkImgs" + File.separator + day + File.separator + fileName);
            }
        }
        return names;
    }

    public static List<String> saveThirdParkBase64Images(String baseRoot, String[] images, String suffix, String floderName) {
        List<String> names = new ArrayList<>();
        Long now = Long.valueOf(Long.parseLong((new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date())));
        Long day = Long.valueOf(Long.parseLong((new SimpleDateFormat("yyyyMMdd")).format(new Date())));

        String path = baseRoot + "parkImgs" + File.separator + floderName + File.separator;
        for (int i = 0; i < images.length; i++) {
            String fileName = now.toString() + i + LanNengStringUtils.getRandomChar(6) + suffix.trim();
            String baseFile = images[i];
            if (baseFile.length() >= 500) {
                saveBase64File(baseFile, path, fileName);
                names.add("parkImgs" + File.separator + floderName + File.separator + fileName);
            }
        }
        return names;
    }


    private static void saveBase64File(String baseStr, String path, String fileName) {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream write = new FileOutputStream(new File(path + fileName));
            byte[] decoderBytes = decoder.decodeBuffer(baseStr);
            write.write(decoderBytes);
            write.flush();
            write.close();
        } catch (IOException e) {
            log.error("抛出异常", e);
        }
    }

    public static File inputStream2File(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (FileNotFoundException e) {
            log.error("抛出异常", e);
        } catch (IOException e) {
            log.error("抛出异常", e);
        }
        return file;
    }
}

