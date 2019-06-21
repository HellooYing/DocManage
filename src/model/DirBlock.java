package model;

public class DirBlock {
    //目录文件
    String DirName; //目录名
    int Bfcb; //目录所指向的i节点号

    int FaDirId; //父亲目录id
    int SonDirId; //儿子目录id
    int NextDirId; //下一个目录id(兄弟节点)

    int Used; //是否空闲

    public String getDirName() {
        return DirName;
    }

    public void setDirName(String dirName) {
        DirName = dirName;
    }

    public int getBfcb() {
        return Bfcb;
    }

    public void setBfcb(int bfcb) {
        Bfcb = bfcb;
    }

    public int getFaDirId() {
        return FaDirId;
    }

    public void setFaDirId(int faDirId) {
        FaDirId = faDirId;
    }

    public int getSonDirId() {
        return SonDirId;
    }

    public void setSonDirId(int sonDirId) {
        SonDirId = sonDirId;
    }

    public int getNextDirId() {
        return NextDirId;
    }

    public void setNextDirId(int nextDirId) {
        NextDirId = nextDirId;
    }

    public int getUsed() {
        return Used;
    }

    public void setUsed(int used) {
        Used = used;
    }
}
