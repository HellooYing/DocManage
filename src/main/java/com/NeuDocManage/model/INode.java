package com.NeuDocManage.model;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class INode extends IndexNode {
    List<INode> dirSon=new ArrayList<>();
    List<INode> fileSon=new ArrayList<>();
    INode father;

    public void addDirSon(INode iNode){
        dirSon.add(iNode);
    }

    public void addFileSon(INode iNode){
        fileSon.add(iNode);
    }

    public List<INode> getDirSon() {
        return dirSon;
    }

    public void setDirSon(List<INode> dirSon) {
        this.dirSon = dirSon;
    }

    public List<INode> getFileSon() {
        return fileSon;
    }

    public void setFileSon(List<INode> fileSon) {
        this.fileSon = fileSon;
    }

    public INode getFather() {
        return father;
    }

    public void setFather(INode father) {
        this.father = father;
    }

    @Override
    public void print(){
        System.out.println(JSON.toJSONString(this));
    }

    public String toString(){
        return getFileName()+" "+getId();
    }
}
