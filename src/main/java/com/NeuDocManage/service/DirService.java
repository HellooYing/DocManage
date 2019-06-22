package com.NeuDocManage.service;

import com.NeuDocManage.model.DirBlock;
import com.NeuDocManage.model.HostHolder;
import com.NeuDocManage.model.IndexNode;
import com.alibaba.fastjson.JSON;

import java.util.Date;

import static com.NeuDocManage.config.MainConfig.BLOCKNUM;
import static com.NeuDocManage.service.BlockService.writeBlock;
import static com.NeuDocManage.service.DataService.getDataBlock;

public class DirService {
    //目录块有关操作
    int mkdir(String dirName){
        //创建一个目录,返回的是该目录的i节点号(创建失败返回BLOCKNUM - 1)
        int inodeNum = getInodeBlock(); //申请一个空闲i节点
        int dirBlockNum = getDataBlock(); //申请一个空闲数据块

        //写入i节点信息
        IndexNode inode = new IndexNode();
        inode.setId(inodeNum);
        inode.setType(1);
        //mode是空的，表示只有root和自己能有全部权限
        inode.setUsed(true);
        inode.setSize(0); //空目录，若新建文件要在这里标记增加

        inode.setName(dirName);
        inode.setCreator(HostHolder.getUser().getUserName());

        inode.setCreateTime(new Date());
        inode.setChangeTime(new Date());

        inode.setOffset(dirBlockNum);
        inode.setIndirectData(inodeNum); //指向自己，如果一个目录存不下，可能会扩增，indirect指向下一个

        //写入dirBlock信息
        DirBlock dirBlock = new DirBlock();
        dirBlock.setDirName(dirName);
        dirBlock.setBfcb(inodeNum);

        dirBlock.setFaDirId(getCurDir()); //父目录设置为当前目录，如果在初始化root目录时需要设置为空
        dirBlock.setSonDirId(BLOCKNUM - 1); //表示没有
        dirBlock.setNextDirId(BLOCKNUM - 1);   //表示没有

        dirBlock.setUsed(true);

        //写入磁盘
        writeBlock(inodeNum, JSON.toJSONString(inode));
        writeBlock(dirBlockNum, JSON.toJSONString(dirBlock));

        return inodeNum;
    }
}
