package com.shu.hbase.service.impl.downLoad;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class DownloadInOffset {
    private FSDataInputStream fsInputStream = null;

    public DownloadInOffset(FileSystem fs, String srcPath) throws Exception {
        fsInputStream = fs.open(new Path(srcPath));
    }

    public int download(byte[] ioBuffer, long offset) {
        try {
            fsInputStream.seek(offset);
            return fsInputStream.read(ioBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int download(byte[] ioBuffer) throws IOException {
        if (ioBuffer == null) {
            IOException e = new IOException("ioBuffer is null");
            throw e;
        }
        return fsInputStream.read(ioBuffer);
    }

    public void close() throws IOException {
        if (fsInputStream != null) {
            try {
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fsInputStream.close();
            }
        }
    }

    public long getFileSize(FileSystem fs, String srcPath) throws Exception {
        FileStatus fileStatus = fs.getFileStatus(new Path(srcPath));
        long size = fileStatus.getLen();
        return size;
    }
}
