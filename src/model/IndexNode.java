package model;

import java.util.Date;

public class IndexNode {
    //i节点(索引)
    int Id; //索引号

    //文件说明信息
    int type; //文件类型 1:目录 2:文件 3:硬链接 4:软链接
    int Mode; //权限
    int Used; //是否空闲
    int Size; //对应文件大小

    String name; //文件名称/目录名称
    String creator; //创建者

    Date createTime; //创建日期
    Date changeTime; //最后一次修改时间

    //索引地址
    int offset; //存放磁盘块的直接地址（磁盘偏移量）
    int indirectData; //存放一个间接索引地址（指向i节点)

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMode() {
        return Mode;
    }

    public void setMode(int mode) {
        Mode = mode;
    }

    public int getUsed() {
        return Used;
    }

    public void setUsed(int used) {
        Used = used;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int size) {
        Size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Date changeTime) {
        this.changeTime = changeTime;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getIndirectData() {
        return indirectData;
    }

    public void setIndirectData(int indirectData) {
        this.indirectData = indirectData;
    }
}