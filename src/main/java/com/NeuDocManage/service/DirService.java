package com.NeuDocManage.service;

import com.NeuDocManage.model.DataBlock;
import com.NeuDocManage.model.DirBlock;
import com.NeuDocManage.model.HostHolder;
import com.NeuDocManage.model.IndexNode;
import com.alibaba.fastjson.JSON;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.NeuDocManage.config.MainConfig.*;
import static com.NeuDocManage.model.HostHolder.getCurDir;
import static com.NeuDocManage.service.BlockService.readBlock;
import static com.NeuDocManage.service.BlockService.writeBlock;
import static com.NeuDocManage.service.DataService.getDataBlock;
import static com.NeuDocManage.service.INodeServie.getIndexBlock;

public class DirService {
    //目录块有关操作
    //目录有关操作
    //下面三个都是假的!

//    private static int CurDir = 1;
//
//    public static void setCurDir(int dirId){
//        CurDir = dirId;
//    }
//
//    private static int getCurDir(){
//        //假的
//        return CurDir;
//    }

    private static int findSubDir(int dirId,String dirName){
        //找一个目录的子目录
        System.out.println(dirId+" "+readBlock(dirId));
        IndexNode nowInode = JSON.parseObject(readBlock(dirId), IndexNode.class);
        DirBlock nowDir = JSON.parseObject(readBlock(nowInode.getOffset()), DirBlock.class);
        if(nowDir.getSonDirId() == BLOCKNUM - 1){
            return -1; //没找到
        }else{
            nowInode = JSON.parseObject(readBlock(nowDir.getSonDirId()), IndexNode.class);
            nowDir = JSON.parseObject(readBlock(nowInode.getOffset()), DirBlock.class);
            if(nowDir.getDirName().equals(dirName)){
                return nowInode.getId();
            }else{
                while(nowDir.getNextDirId() != BLOCKNUM - 1){
                    nowInode = JSON.parseObject(readBlock(nowDir.getNextDirId()), IndexNode.class);
                    nowDir = JSON.parseObject(readBlock(nowInode.getOffset()), DirBlock.class);
                    if(nowDir.getDirName().equals(dirName)){
                        return nowInode.getId();
                    }
                }
                return -1; //没找到
            }
        }
    }

    public static int mkdir(String dirName) {
        //创建一个目录,返回的是该目录的i节点号(创建失败返回BLOCKNUM - 1)
        int inodeNum = getIndexBlock(); //申请一个空闲i节点
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

        dirBlock.setFaDirId(getCurDir().getId()); //父目录设置为当前目录，如果在初始化root目录时需要设置为空
        dirBlock.setSonDirId(BLOCKNUM - 1); //表示没有
        dirBlock.setNextDirId(BLOCKNUM - 1);   //表示没有

        //寻找父亲目录的子目录最后一个，添加到尾部
        IndexNode nowInode = JSON.parseObject(readBlock(getCurDir().getId()), IndexNode.class);
        DirBlock nowDir = JSON.parseObject(readBlock(nowInode.getOffset()), DirBlock.class);
        while(nowDir.getNextDirId() != BLOCKNUM - 1){
            nowInode = JSON.parseObject(readBlock(nowDir.getNextDirId()), IndexNode.class);
            nowDir = JSON.parseObject(readBlock(nowInode.getOffset()), DirBlock.class);
        }
        nowDir.setNextDirId(inodeNum);

        dirBlock.setUsed(true);

        //写入磁盘
        System.out.println(JSON.toJSONString(inode));
        writeBlock(inodeNum, JSON.toJSONString(inode));
        System.out.println(inodeNum+" "+readBlock(inodeNum));
        writeBlock(dirBlockNum, JSON.toJSONString(dirBlock));
        System.out.println(dirBlockNum+" "+readBlock(dirBlockNum));

        return inodeNum;
    }

    private static Pair<Integer,String> cdAutomation(Pair<Integer,String> dirName){
        //目录自动机
        if(dirName.getKey() == -1){
            return dirName; //出错了
        }else if(dirName.getValue().equals( "\\/")){
            return dirName; //找到了
        }else{
            String dir = dirName.getValue();
            String subDir[] = dir.split("\\/");
            int find = findSubDir(dirName.getKey(),subDir[0]);
            if(find != -1){
                if(subDir.length == 1){
                    //就剩一个目录了，直接返回
                    return new Pair<Integer, String>(find,subDir[0]);
                }
                //找到子目录里面有，进入子目录
                String subDirName = dirName.getValue().replace(subDir[0]+"\\/","\\/");
                return cdAutomation(new Pair<Integer, String>(find,subDirName));
            }else{
                if(subDir[0].equals(".")){
                    String subDirName = dirName.getValue().replace(subDir[0]+"\\/","\\/");
                    return cdAutomation(new Pair<Integer, String>(dirName.getKey(),subDirName));
                }else if(subDir[0].equals("..")){
                    String subDirName = dirName.getValue().replace(subDir[0]+"\\/","\\/");
                    IndexNode nowInode = JSON.parseObject(readBlock(dirName.getKey()), IndexNode.class);
                    DirBlock nowDir = JSON.parseObject(readBlock(nowInode.getOffset()), DirBlock.class);
                    return cdAutomation(new Pair<Integer, String>(nowDir.getFaDirId(),subDirName));
                }
                return new Pair<Integer, String>(find,dirName.getValue()); //子目录里面没有
            }
        }
    }

    public static int changeDir(String dirName){
        //更换目录，对应cd指令,返回进入的目录的i节点号
        /*
        if(!dirName.matches("^[\\/](\\w+\\/?)+$")){
            return -1; //输入进来的路径不合法
        }
        */
        int cur = SUPERBLOCKSTART+SUPERBLOCKNUM; //默认从root开始解析
        String subDir[] = dirName.split("\\/");
        if(!subDir[0].equals(".") && !subDir[0].equals("..")) {
            dirName.replace("\\/roor\\/","");
        }else if(subDir[0].equals(".")){
            cur = getCurDir().getId();
        }else{
            IndexNode nowInode = JSON.parseObject(readBlock(getCurDir().getId()), IndexNode.class);
            DirBlock nowDir = JSON.parseObject(readBlock(nowInode.getOffset()), DirBlock.class);
            cur = nowDir.getFaDirId();
        }
        System.out.println(cur+" "+dirName);
        Pair<Integer, String> res = cdAutomation(new Pair<Integer, String>(cur,dirName));
        if(res.getKey() == -1){
            return -2; //
        }else{
            return res.getKey();
        }
    }

    public static List<String> listDir(){
        //对应ls指令，列出所有子文件,存到一个List<String>里面
        //找一个目录的子目录
        List<String> result = new ArrayList<String>();
        IndexNode nowInode = JSON.parseObject(readBlock(getCurDir().getId()), IndexNode.class);
        DirBlock nowDir = JSON.parseObject(readBlock(nowInode.getOffset()), DirBlock.class);
        if(nowDir.getSonDirId() == BLOCKNUM - 1){
            return result; //没有
        }else{
            nowInode = JSON.parseObject(readBlock(nowDir.getSonDirId()), IndexNode.class);
            if(nowInode.getType() == 1){
                //一个目录
                nowDir = JSON.parseObject(readBlock(nowInode.getOffset()), DirBlock.class);
                result.add(nowDir.getDirName()+"\\/");
                DataBlock nowData;
                int nextId = nowDir.getNextDirId();
                while(nextId != BLOCKNUM - 1){
                    nowInode = JSON.parseObject(readBlock(nowDir.getNextDirId()), IndexNode.class);
                    if(nowInode.getType() == 1) {
                        //一个目录
                        nowDir = JSON.parseObject(readBlock(nowInode.getOffset()), DirBlock.class);
                        result.add(nowDir.getDirName()+"\\/");
                        nextId = nowDir.getNextDirId();
                    }else{
                        //一个文件
                        nowData = JSON.parseObject(readBlock(nowInode.getOffset()), DataBlock.class);
                        result.add(nowInode.getName());
                        nextId = nowData.getNextDataId();
                    }
                }
            }else{
                //一个文件
                result.add(nowInode.getName());
            }
            return result;
        }
    }

}
