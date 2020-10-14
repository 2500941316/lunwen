package com.shu.hbase.tools.filetype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

//文件类型判断类
public final class FileTypeJudge {
    private static Logger logger = LoggerFactory.getLogger(FileTypeJudge.class);

    /**
     * Constructor
     */
    private FileTypeJudge() {
    }

    /**
     * 将文件头转换成16进制字符串
     *
     * @return 16进制字符串
     */
    private static String bytesToHexString(byte[] src) {

        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 得到文件头
     *
     * @return 文件头
     * @throws IOException
     */
    private static String getFileContent(InputStream is) throws IOException {

        byte[] b = new byte[28];
        try {
            is.read(b, 0, 28);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }
        return bytesToHexString(b);
    }

    //获得文件的类型
    public static FileType getType(InputStream is) throws IOException {
        String fileHead = getFileContent(is);
        if (fileHead == null || fileHead.length() == 0) {
            return null;
        }
        fileHead = fileHead.toUpperCase();
        FileType[] fileTypes = FileType.values();
        for (FileType type : fileTypes) {
            if (fileHead.startsWith(type.getValue())) {
                return type;
            }
        }
        return null;
    }

    /**
     * @param value 表示文件类型
     * @return
     */
    public static String isFileType(FileType value) {
        String type = "other";// 其他
        // 图片
        FileType[] pics = {FileType.JPEG, FileType.PNG, FileType.GIF, FileType.TIFF, FileType.BMP, FileType.DWG, FileType.PSD};
        FileType[] docs = {FileType.RTF, FileType.XML, FileType.HTML, FileType.CSS, FileType.JS, FileType.EML, FileType.DBX, FileType.PST, FileType.XLS_DOC, FileType.XLSX_DOCX, FileType.VSD,
                FileType.MDB, FileType.WPS, FileType.WPD, FileType.EPS, FileType.PDF, FileType.QDF, FileType.PWL};
        FileType[] videos = {FileType.AVI, FileType.RAM, FileType.RM, FileType.MPG, FileType.MOV, FileType.ASF, FileType.MP4, FileType.FLV, FileType.MID};
        FileType[] audios = {FileType.WAV, FileType.MP3};
        FileType[] others = {FileType.JAVA, FileType.CLASS, FileType.ZIP, FileType.RAR, FileType.JSP, FileType.JAR, FileType.MF, FileType.EXE, FileType.CHM,};

        // 图片
        for (FileType fileType : pics) {
            if (fileType.equals(value)) {
                type = "pic";
            }
        }
        // 文档
        for (FileType fileType : docs) {
            if (fileType.equals(value)) {
                type = "doc";
            }
        }
        // 视频
        for (FileType fileType : videos) {
            if (fileType.equals(value)) {
                type = "video";
            }
        }

        // 音乐
        for (FileType fileType : audios) {
            if (fileType.equals(value)) {
                type = "music";
            }
        }

        // 其他
        for (FileType fileType : others) {
            if (fileType.equals(value)) {
                type = "other";
            }
        }
        return type;
    }
}
