package model;

public class DataBlock {
    //磁盘文件块,记录数据
    private String data; //数据区
    private int nextDataId; //下一块文件id
    private boolean used; //是否空闲

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getNextDataId() {
        return nextDataId;
    }

    public void setNextDataId(int nextDataId) {
        this.nextDataId = nextDataId;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
