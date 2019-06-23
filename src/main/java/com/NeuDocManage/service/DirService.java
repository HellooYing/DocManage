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

    public static int createFile(String fileName) {
        //创建一个文件,返回的是该文件的i节点号(创建失败返回BLOCKNUM - 1)
        //对应create函数
        int inodeNum = getIndexBlock(); //申请一个空闲i节点
        int dataBlockNum = getDataBlock(); //申请一个空闲数据块

        //写入i节点信息
        IndexNode inode = new IndexNode();
        inode.setId(inodeNum);
        inode.setType(2);

        //mode是空的，表示只有root和自己能有全部权限
        inode.setUsed(true);
        inode.setSize(0); //空文件，若新建文件要在这里标记增加

        inode.setFileName(fileName);
        inode.setCreator(HostHolder.getUser().getUserName());

        inode.setCreateTime(new Date());
        inode.setChangeTime(new Date());

        //inode.setOffset(dataBlockNum); ??
        inode.setIndirectData(dataBlockNum); //指向所指的磁盘块

        //写入dataBlock信息
        DataBlock dataBlock = new DataBlock();
        dataBlock.setData("");
        dataBlock.setNextDataId(BLOCKNUM - 1);   //表示没有
        dataBlock.setFaDirID(getCurDir().getId()); //设置父亲的目录

        //寻找父亲目录的子目录最后一个，添加到尾部
        IndexNode nowInode = JSON.parseObject(readBlock(getCurDir().getId()).trim(), IndexNode.class);
        DirBlock nowDir = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DirBlock.class);
        List<Integer> sons = nowDir.getSonDataId();
        sons.add(inodeNum);
        nowDir.setSonDataId(sons);
        overwriteBlock(getCurDir().getIndirectData(),JSON.toJSONString(nowDir));//写回

        dataBlock.setUsed(true);

        //写入磁盘
        writeBlock(inodeNum, JSON.toJSONString(inode));
        writeBlock(dataBlockNum, JSON.toJSONString(dataBlock));

        return inodeNum;
    }

    public static boolean writeFile(String fileName,String content){
        //往文件里头写东西
        //首先找到这个文件
        int fileId = -1;
        int headId = -1; //表示这个文件的头Id
        IndexNode nowInode = JSON.parseObject(readBlock(getCurDir().getId()).trim(), IndexNode.class);
        DirBlock nowDir = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DirBlock.class);
        for(Integer x : nowDir.getSonDataId()){
            nowInode = JSON.parseObject(readBlock(x).trim(), IndexNode.class);
            if(nowInode.getFileName().equals(fileName)){
                headId = nowInode.getId();
                System.out.println("headId: "+headId);
                fileId = nowInode.getIndirectData();
                 //找到这个文件结尾的Id
                 DataBlock nowData = JSON.parseObject(readBlock(fileId).trim(), DataBlock.class);
                 int nextId = nowData.getNextDataId();
                 while(nextId !=BLOCKNUM - 1){
                     nowInode = JSON.parseObject(readBlock(nextId).trim(), IndexNode.class);
                     nowData =JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DataBlock.class);
                     nextId = nowData.getNextDataId();
                 }
                 fileId = nowInode.getIndirectData();
            }
        }
        if(fileId == -1){
            return false; //没找到
        }
        System.out.println(fileId);
        DataBlock nowData = JSON.parseObject(readBlock(fileId).trim(), DataBlock.class);
        //在文件结尾写，所以先把结尾连接上去
        if(!nowData.getData().equals("")){
            content = nowData.getData().concat(" "+content);
        }
        //设置content
        List<String> contents = new ArrayList<String>();
        int limit = 420;
        //首先判断String 长度，如果大于450，就分隔
        int beginIndex =  0;
        int endIndex = limit;
        int length = content.length();
        while(length > limit){
            contents.add(content.substring(beginIndex,endIndex));
            length -= limit;
            beginIndex = endIndex;
            endIndex = endIndex + limit;
        }
        contents.add(content.substring(beginIndex));

        IndexNode inode = new IndexNode();
        //先写末尾结点
        int newData = -1;
        int newInode = -1;
        if(contents.size() > 1) {
            newData = getDataBlock();
            newInode = getIndexBlock();
            nowData.setNextDataId(newInode);
        }
        nowData.setData(contents.get(0)); //替换数据
        System.out.println("Write in:"+fileId+"\n"+JSON.toJSONString(nowData));
        System.out.println(overwriteBlock(fileId,JSON.toJSONString(nowData)));
        System.out.println("length: "+JSON.toJSONString(nowData).length());

        System.out.println("headid:"+headId);
        nowInode = JSON.parseObject(readBlock(headId).trim(), IndexNode.class);
        nowData = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DataBlock.class);
        System.out.println(JSON.toJSONString(nowInode)+"\nwithout change\n"+JSON.toJSONString(nowData));

        for (int i = 1; i < contents.size(); i++) {
            System.out.println("newData:"+newData+"\nnewInode:"+newInode);
            String nowContent = contents.get(i);
            //首先写入其他信息
            //写入i节点信息
            inode.setId(newInode);
            inode.setType(2);

            //mode是空的，表示只有root和自己能有全部权限
            inode.setUsed(true);
            inode.setSize(512); //一块数据512

            inode.setFileName(fileName);
            inode.setCreator(HostHolder.getUser().getUserName());

            inode.setCreateTime(new Date());
            inode.setChangeTime(new Date());

            //inode.setOffset(dataBlockNum); ??
            inode.setIndirectData(newData); //指向所指的磁盘块

            //写新的数据区
            DataBlock dataBlock = new DataBlock();
            dataBlock.setNextDataId(BLOCKNUM - 1); //先表示成没有
            dataBlock.setFaDirID(BLOCKNUM - 1); //由于是文件的一个部分，只有头结点设置父节点，不然在ls的时候会出现两个文件
            dataBlock.setData(nowContent); //写入当前数据
            dataBlock.setUsed(true);

            writeBlock(newInode,JSON.toJSONString(inode));
            writeBlock(newData,JSON.toJSONString(dataBlock));
            System.out.println("i+1:"+(int)(i+1)+(contents.size() - 1));
            if(i !=  contents.size() - 1) {
                //如果这个结点的下个结点不是最后一个结点
                newInode = getIndexBlock();
                dataBlock.setNextDataId(newInode);
                overwriteBlock(newData,JSON.toJSONString(dataBlock));
                newData = getDataBlock();
                inode = new IndexNode();
            }
        }
        nowInode = JSON.parseObject(readBlock(headId).trim(), IndexNode.class);
        nowData = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DataBlock.class);
        nowInode.setSize(512*(contents.size()-1)+contents.get(contents.size()-1).length()+48); //设置好内存大小
        System.out.println(JSON.toJSONString(nowInode)+"\nwith change\n"+JSON.toJSONString(nowData));
        overwriteBlock(headId,JSON.toJSONString(nowInode));
        return true;
    }

    public static String readFile(String fileName){
        //读当前目录下的某个文件,返回文件内容
        //找子目录
        IndexNode nowInode = JSON.parseObject(readBlock(getCurDir().getId()).trim(), IndexNode.class);
        DirBlock nowDir = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DirBlock.class);
        for(Integer x : nowDir.getSonDataId()){
            nowInode = JSON.parseObject(readBlock(x).trim(), IndexNode.class);
            if(nowInode.getFileName().equals(fileName)){
                System.out.println("Inode: "+nowInode.getIndirectData());
                DataBlock resData = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DataBlock.class);
                StringBuilder res = new StringBuilder();
                System.out.println("resData: "+JSON.toJSONString(resData));
                res.append(resData.getData());
                while(resData.getNextDataId() != BLOCKNUM - 1){
                    nowInode = JSON.parseObject(readBlock(resData.getNextDataId()).trim(), IndexNode.class);
                    resData = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DataBlock.class);
                    res.append(resData.getData());
                }
                return res.toString();
            }
        }
        return null; //没找到
    }

}
