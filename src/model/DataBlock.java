package model;

public class DataBlock {
    //磁盘文件块,记录数据
    String Data; //数据区
    int NextDataId; //下一块文件id
    int used; //是否空闲

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public int getNextDataId() {
        return NextDataId;
    }

    public void setNextDataId(int nextDataId) {
        NextDataId = nextDataId;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }
}
