package service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static config.MainConfig.*;

public class DiskService {
    public static MappedByteBuffer disk;

    /**
     * 初始化磁盘，如果没有disk.txt就创建，有则打开
     * @throws IOException
     */
    public static void initDisk() throws IOException {
        disk=new RandomAccessFile("disk.txt","rw").
                getChannel().map(FileChannel.MapMode.READ_WRITE,0,DISKSIZE);
    }
}
