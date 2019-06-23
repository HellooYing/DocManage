package com.NeuDocManage.model;

import java.util.ArrayList;
import java.util.List;

public class DirBlock extends Block {
    //目录文件
    private String dirName; //目录名
    private int bfcb; //目录所指向的i节点号

    private int faDirId; //父亲目录id
    private List<Integer> sonDirId; //儿子目录id
    private int nextDirId; //存不下了，下一个盘区

    private List<Integer> sonDataId; //当前目录下存放的文件

    private boolean used; //是否空闲

    public DirBlock() {
        super("DirBlock");
        sonDirId = new ArrayList<Integer>();
        sonDataId = new ArrayList<Integer>();
    }

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

    public List<Integer> getSonDirId() {
        return sonDirId;
    }

    public void setSonDirId(List<Integer> sonDirId) {
        this.sonDirId = sonDirId;
    }

    public List<Integer> getSonDataId() {
        return sonDataId;
    }

    public void setSonDataId(List<Integer> sonDataId) {
        this.sonDataId = sonDataId;
    }

    public int getNextDirId() {
        return nextDirId;
    }

    public void setNextDirId(int nextDirId) {
        this.nextDirId = nextDirId;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
