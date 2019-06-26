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
import static com.NeuDocManage.service.BlockService.*;
import static com.NeuDocManage.service.DataService.getDataBlock;
import static com.NeuDocManage.service.INodeServie.getIndexBlock;

public class DirService {
    public static void showInfo(int inodeId) {
        IndexNode inode = JSON.parseObject(readBlock(inodeId).trim(), IndexNode.class);
        System.out.println( "Name: "+inode.getFileName()+"\t"+
                "Type: "+inode.getType()+"\t"+
                "Size: "+inode.getSize()+"\t"+
                "Creator: "+inode.getCreator()+"\t"+
                "CreateTime: "+inode.getChangeTime()+"\t"+
                "ChangeTime: "+inode.getChangeTime());
    }

    private static int findSubDir(int dirId, String dirName){
        //找一个目录的子目录
        //System.out.println(dirName);
        IndexNode nowInode = JSON.parseObject(readBlock(dirId).trim(), IndexNode.class);
        DirBlock nowDir = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DirBlock.class);
        if(nowDir.getSonDirId().size() == 0){
            return -1; //没有
        }else{
            for(Integer x : nowDir.getSonDirId()){
                nowInode = JSON.parseObject(readBlock(x).trim(), IndexNode.class);
                nowDir = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DirBlock.class);
                if(nowDir.getDirName().equals(dirName)){
                    return nowInode.getId();
                }
            }
            return -1; //没有
        }
    }

    public static int mkdir(String dirName) {
        //创建一个目录,返回的是该目录的i节点号(创建失败返回 - 1)

        //当前目录下已经有同名文件，返回-2
        if(findSubDir(getCurDir().getId(),dirName) != -1){
            return -2;
        }

        int inodeNum = getIndexBlock(); //申请一个空闲i节点
        int dirBlockNum = getDataBlock(); //申请一个空闲数据块
        //System.out.println("hhh "+inodeNum+" "+dirBlockNum);
        if(inodeNum == INODEBLOCKSTART+INODEBLOCKNUM-1 && dirBlockNum ==BLOCKNUM - 1 ){
            return -1;
        }

        //写入i节点信息
        IndexNode inode = new IndexNode();
        inode.setId(inodeNum);
        inode.setType(1);
        //mode是空的，表示只有root和自己能有全部权限
        inode.setUsed(true);
        inode.setSize(0); //空目录，若新建文件要在这里标记增加

        inode.setFileName(dirName);
        inode.setCreator(HostHolder.getUser().getUserName());

        inode.setCreateTime(new Date());
        inode.setChangeTime(new Date());

        //inode.setOffset(); ??
        inode.setIndirectData(dirBlockNum); //指向自己，如果一个目录存不下，可能会扩增，indirect指向下一个

        //写入dirBlock信息
        DirBlock dirBlock = new DirBlock();
        dirBlock.setDirName(dirName);
        dirBlock.setIndexId(inodeNum);

        //System.out.println(getCurDir().getId());
        dirBlock.setFaDirId(getCurDir().getId()); //父目录设置为当前目录，如果在初始化root目录时需要设置为空

        //新建目录以下两项都是空
        //dirBlock.setSonDirId(BLOCKNUM - 1); //表示没有
        dirBlock.setNextDirId(BLOCKNUM - 1);   //表示没有


        //寻找父亲目录的子目录，添加到子目录(root还没有)
        IndexNode nowInode = JSON.parseObject(readBlock(getCurDir().getId()).trim(), IndexNode.class);
        DirBlock nowDir = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DirBlock.class);
        List<Integer> sonDir = nowDir.getSonDirId();
        sonDir.add(inodeNum);
        //System.out.println(JSON.toJSONString(sonDir));
        nowDir.setSonDirId(sonDir);
        //System.out.println(getCurDir().getIndirectData());
        overwriteBlock(getCurDir().getIndirectData(),JSON.toJSONString(nowDir));



        dirBlock.setUsed(true);

        //写入磁盘
        //System.out.println(JSON.toJSONString(inode));
        writeBlock(inodeNum, JSON.toJSONString(inode));
        //System.out.println(inodeNum+" "+readBlock(inodeNum));
        writeBlock(dirBlockNum, JSON.toJSONString(dirBlock));
        // System.out.println(dirBlockNum+" "+readBlock(dirBlockNum));

        return inodeNum;
    }

    private static Pair<Integer,String> cdAutomation(Pair<Integer,String> dirName){
        //目录自动机
        System.out.println(dirName.getKey()+" "+dirName.getValue());
        if(dirName.getKey() == -1){
            return dirName; //出错了
        }else if(dirName.getValue().equals( "\\/")){
            return dirName; //找到了
        }else{
            String dir = dirName.getValue().trim();
            String subDir[] = dir.split("\\/");
            /*
            if(subDir[0].equals("")&&subDir.length == 2){
                String temp = subDir[0];
                subDir[0] = subDir[1];
                subDir[1] = temp;
            }
            /
             */
            int find = findSubDir(dirName.getKey(),subDir[0]);
            if(find != -1){
                //System.out.println("fuckyou");
                if(subDir.length == 1){

                    //就剩一个目录了，直接返回
                    return new Pair<Integer, String>(find,subDir[0]);
                }
                //找到子目录里面有，进入子目录
                String subDirName = dirName.getValue().replaceFirst(subDir[0]+"\\/","");
                return cdAutomation(new Pair<Integer, String>(find,subDirName.trim()));
            }else{
                if(subDir[0].equals(".")){
                    String subDirName = dirName.getValue().replaceFirst(subDir[0]+"\\/","");
                    return cdAutomation(new Pair<Integer, String>(dirName.getKey(),subDirName.trim()));
                }else if(subDir[0].equals("..")){
                    String subDirName = dirName.getValue().replaceFirst(subDir[0]+"\\/","");
                    IndexNode nowInode = JSON.parseObject(readBlock(dirName.getKey()).trim(), IndexNode.class);
                    DirBlock nowDir = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DirBlock.class);
                    return cdAutomation(new Pair<Integer, String>(nowDir.getFaDirId(),subDirName.trim()));
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
            dirName.replaceFirst("\\/roor\\/","");
        }else if(subDir[0].equals(".")){
            cur = getCurDir().getId();
        }else{
            IndexNode nowInode = JSON.parseObject(readBlock(getCurDir().getId()).trim(), IndexNode.class);
            DirBlock nowDir = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DirBlock.class);
            cur = nowDir.getFaDirId();
        }
        //System.out.println(cur+" "+dirName);
        Pair<Integer, String> res = cdAutomation(new Pair<Integer, String>(cur,dirName));
        if(res.getKey() == -1){
            return -2; //没找到
        }else{
            return res.getKey();
        }
    }

    public static List<String> listDir(){
        //对应ls指令，列出所有子文件,存到一个List<String>里面
        //找一个目录的子目录
        List<String> result = new ArrayList<String>();
        IndexNode nowInode = JSON.parseObject(readBlock(getCurDir().getId()).trim(), IndexNode.class);
        DirBlock nowDir = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DirBlock.class);
        //先读取所有目录输出
        for(Integer x : nowDir.getSonDirId()){
            nowInode = JSON.parseObject(readBlock(x).trim(), IndexNode.class);
            result.add(nowInode.getFileName()+"/");
        }
        //再读取所有子文件输出
        for(Integer x : nowDir.getSonDataId()){
            nowInode = JSON.parseObject(readBlock(x).trim(), IndexNode.class);
            result.add(nowInode.getFileName());
        }
        return result;
    }
}