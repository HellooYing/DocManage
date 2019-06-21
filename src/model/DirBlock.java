package model;

public class DirBlock {
    //目录文件
    private String dirName; //目录名
    private int bfcb; //目录所指向的i节点号

    private int faDirId; //父亲目录id
    private int sonDirId; //儿子目录id
    private int nextDirId; //下一个目录id(兄弟节点)

    private int used; //是否空闲

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public int getBfcb() {
        return bfcb;
    }

    public void setBfcb(int bfcb) {
        this.bfcb = bfcb;
    }

    public int getFaDirId() {
        return faDirId;
    }

    public void setFaDirId(int faDirId) {
        this.faDirId = faDirId;
    }

    public int getSonDirId() {
        return sonDirId;
    }

    public void setSonDirId(int sonDirId) {
        this.sonDirId = sonDirId;
    }

    public int getNextDirId() {
        return nextDirId;
    }

    public void setNextDirId(int nextDirId) {
        this.nextDirId = nextDirId;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }
}
