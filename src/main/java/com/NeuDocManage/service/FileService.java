package com.NeuDocManage.service;

import com.NeuDocManage.model.*;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.NeuDocManage.config.MainConfig.BLOCKNUM;
import static com.NeuDocManage.config.MainConfig.INODEBLOCKSTART;
import static com.NeuDocManage.model.HostHolder.getCurDir;
import static com.NeuDocManage.service.BlockService.*;
import static com.NeuDocManage.service.BlockService.readBlock;
import static com.NeuDocManage.service.DataService.getDataBlock;
import static com.NeuDocManage.service.DataService.recoverDataBlock;
import static com.NeuDocManage.service.INodeServie.getIndexBlock;
import static com.NeuDocManage.service.INodeServie.recoverIndexBlock;

public class FileService {
    public static INode root;
    /**
     * 获取整个文件系统目录结构，从磁盘中将i节点都读到内存，以后都从内存中查找
     */
    public static void getTree(){
        root=JSON.parseObject(readBlock(INODEBLOCKSTART).trim(),INode.class);
        getSon(root);
    }
    private static void getSon(INode node){
        DirBlock node2=JSON.parseObject(readBlock(node.getIndirectData()).trim(),DirBlock.class);
        for(int i:node2.getSonDataId()){
            INode fileSon=JSON.parseObject(readBlock(i).trim(),INode.class);
            fileSon.setFather(node);
            node.addFileSon(fileSon);
        }
        for(int i:node2.getSonDirId()){
            INode dirSon=JSON.parseObject(readBlock(i).trim(),INode.class);
            dirSon.setFather(node);
            getSon(dirSon);
            node.addDirSon(dirSon);
        }
    }

    /**
     * 打印目录结构
     */
    public static void printTree(){
        if(root==null) getTree();
        System.out.println(root.getFileName());
        printTree(1,root.getDirSon(),root.getFileSon());
    }

    private static void printTree(int level,List<INode> dir,List<INode> file){
        for(INode node:file){
            for (int i = 0; i <level ; i++) {
                System.out.print("  ");
            }
            System.out.println(node.getFileName());
        }
        for(INode node:dir){
            for (int i = 0; i <level ; i++) {
                System.out.print("--");
            }
            System.out.println(node.getFileName());
            printTree(level+1,node.getDirSon(),node.getFileSon());
        }
    }

    /**
     * 查找文件
     * @param fileName
     * @return 文件在内存中的索引节点
     */
    public static List<INode> findFile(String fileName){
        if(root==null) getTree();
        return find(fileName,root);
    }
    private static List<INode> find(String fileName,INode node){
        List<INode> r=new ArrayList<>();
        List<INode> file=node.getFileSon();
        List<INode> dir=node.getDirSon();
        for(INode iNode:file){
            if(iNode.getFileName().equals(fileName)) r.add(iNode);
        }
        for(INode iNode:dir){
            if(iNode.getFileName().equals(fileName)) r.add(iNode);
            r.addAll(find(fileName,iNode));
        }
        return r;
    }

//    public static INode findFileOnCur(String fullName){
//
//    }

    /**
     * 根据文件全名（/root/hhh）查找内存中的索引节点
     * @param fullName
     * @return
     */
    public static INode findFileByFullName(String fullName){
        if(!fullName.substring(0,5).equals("/root")){
            System.out.println("输入非全名！");
            return null;
        }
        fullName=fullName.substring(1);
        String[] names=fullName.split("/");
        if(root==null) getTree();
        INode node=root;
        for (int i = 1; i <names.length-1 ; i++) {
            String dir=names[i];
            INode dir2=null;
            for(INode n:node.getDirSon()){
                if(n.getFileName().equals(dir)){
                    dir2=n;
                    break;
                }
            }
            if(dir2==null){
                System.out.println("该目录不存在！");
                return null;
            }
            else{
                node=dir2;
                continue;
            }
        }
        String file=names[names.length-1];
        for(INode n:node.getFileSon()){
            if(n.getFileName().equals(file)){
                return n;
            }
        }
        for(INode n:node.getDirSon()){
            if(n.getFileName().equals(file)){
                return n;
            }
        }
        System.out.println("该目录不存在！");
        return null;
    }

    public static String getFullName(INode node){
        StringBuilder r=new StringBuilder();
        while (node.getFather()!=null){
            r.insert(0,node.getFileName());
            r.insert(0,"/");
            node=node.getFather();
        }
        r.insert(0,node.getFileName());
        r.insert(0,"/");
        return r.toString();
    }

    /**
     * 根据id查找内存i节点
     * @param id
     * @param node
     * @return 内存i节点
     */
    public static INode getINodeById(int id,INode node){
        if(node.getId()==id) return node;
        List<INode> file=node.getFileSon();
        List<INode> dir=node.getDirSon();
        for(INode iNode:file){
            if(iNode.getId()==id) return iNode;
        }
        for(INode iNode:dir){
            if(iNode.getId()==id) return iNode;
            else {
                INode node1=getINodeById(id,iNode);
                if(node1!=null) return node1;
            }
        }
        return null;
    }

    /**
     * 获取内存i节点类型的当前目录
     * @return INode CurDir
     */
    public static INode getCurDir(){
        IndexNode node=HostHolder.getCurDir();
        INode r=getINodeById(node.getId(),root);
        return r;
    }

    /**
     * 在当前目录删除一个文件或空目录
     * @param fileName
     * @return false代表没有找到该文件名对应文件，true代表已删除
     */
    public static boolean deleteOneFile(String fileName){
        INode cur=getCurDir();
        List<INode> file=cur.getFileSon();
        List<INode> dir=cur.getDirSon();
        for(INode iNode:file){
            if(iNode.getFileName().equals(fileName)){
                deleteFileByINode(iNode);
                getTree();
                return true;
            }
        }
        for(INode iNode:dir){
            if(iNode.getFileName().equals(fileName)){
                deleteFileByINode(iNode);
                getTree();
                return true;
            }
        }
        return false;
    }

//    public static boolean deleteAllFile(String fileName){
//
//    }

    /**
     * 递归的删除文件
     * @param node
     */
    public static void deleteAllFileByINode(INode node){
        if(node.getType()==2){
            deleteFileByINode(node);
        }
        else if(node.getType()==1) {
            List<INode> file = node.getFileSon();
            List<INode> dir = node.getDirSon();
            for (INode iNode : file) {
                deleteFileByINode(iNode);
            }
            for (INode iNode : dir) {
                deleteAllFileByINode(iNode);
            }
            deleteFileByINode(node);
        }
    }

    /**
     * 根据内存i节点，格式化索引区及内存区相关的块，并回收这些块以供下次分配
     * 只针对文件及空目录，没有进行递归操作
     * @param node
     */
    public static void deleteFileByINode(INode node){
        recoverIndexBlock(node.getId());//回收索引块
        List<Integer> deleteList=new ArrayList<>();
        int id=node.getIndirectData();
        while(id!=BLOCKNUM-1){
            deleteList.add(id);
            String blockContent=readBlock(id).trim();
            String type=JSON.parseObject(blockContent,Block.class).getName();
            if(type.equals("DataBlock")) id=JSON.parseObject(blockContent,DataBlock.class).getNextDataId();
            else if(type.equals("DirBlock")) id=JSON.parseObject(blockContent,DirBlock.class).getNextDirId();
        }
        for(int i:deleteList){
            recoverDataBlock(i);//回收数据块
        }
    }
    //文件有关操作
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
        getTree();
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
                //找到同名文件
                headId = nowInode.getId(); //headId表示文件的I结点了
                System.out.println("headId: "+headId);
                fileId = nowInode.getIndirectData();
                //找到这个文件结尾的Id
                DataBlock nowData = JSON.parseObject(readBlock(fileId).trim(), DataBlock.class);
                int nextId = nowData.getNextDataId();
                while(nextId !=BLOCKNUM - 1){
                    //nowInode = JSON.parseObject(readBlock().trim(), IndexNode.class);
                    nowData =JSON.parseObject(readBlock(nextId).trim(), DataBlock.class);
                    fileId = nextId; //fileId表示当前文件的磁盘块号
                    nextId = nowData.getNextDataId();
                }
            }
        }
        if(headId == -1){
            return false; //没找到
        }
        //System.out.println(fileId);
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

        //IndexNode inode = new IndexNode();
        //先写末尾结点
        int newData = -1;
        int oldData = -1;
        //int newInode = -1;
        if(contents.size() > 1) {
            newData = getDataBlock();
            //newInode = getIndexBlock();
            nowData.setNextDataId(newData);
        }
        nowData.setData(contents.get(0)); //替换数据
        System.out.println("Write in:"+fileId+"\n"+JSON.toJSONString(nowData));
        overwriteBlock(fileId,JSON.toJSONString(nowData)); //写入
        System.out.println("length: "+JSON.toJSONString(nowData).length());

//        System.out.println("headid:"+headId);
//        nowInode = JSON.parseObject(readBlock(headId).trim(), IndexNode.class);
//        nowData = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DataBlock.class);
//        System.out.println(JSON.toJSONString(nowInode)+"\nwithout change\n"+JSON.toJSONString(nowData));

        for (int i = 1; i < contents.size(); i++) {
            //System.out.println("newData:"+newData+"\nnewInode:"+newInode);
            String nowContent = contents.get(i);
            //首先写入其他信息
//            //写入i节点信息
//            inode.setId(newInode);
//            inode.setType(2);
//
//            //mode是空的，表示只有root和自己能有全部权限
//            inode.setUsed(true);
//            inode.setSize(512); //一块数据512
//
//            inode.setFileName(fileName);
//            inode.setCreator(HostHolder.getUser().getUserName());
//
//            inode.setCreateTime(new Date());
//            inode.setChangeTime(new Date());
//
//            //inode.setOffset(dataBlockNum); ??
//            inode.setIndirectData(newData); //指向所指的磁盘块

            //写新的数据区
            DataBlock dataBlock = new DataBlock();
            dataBlock.setNextDataId(BLOCKNUM - 1); //先表示成没有
            dataBlock.setFaDirID(BLOCKNUM - 1); //由于是文件的一个部分，只有头结点设置父节点，不然在ls的时候会出现两个文件
            dataBlock.setData(nowContent); //写入当前数据
            dataBlock.setUsed(true);

            //writeBlock(newInode,JSON.toJSONString(inode));
            writeBlock(newData,JSON.toJSONString(dataBlock));
            //System.out.println("i+1:"+(int)(i+1)+(contents.size() - 1));
            if(i !=  contents.size() - 1) {
                //如果这个结点的下个结点不是最后一个结点
                //newInode = getIndexBlock();
                oldData = newData;
                newData = getDataBlock();
                dataBlock.setNextDataId(newData);
                overwriteBlock(oldData,JSON.toJSONString(dataBlock));
            }
        }
        nowInode = JSON.parseObject(readBlock(headId).trim(), IndexNode.class);
        nowData = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DataBlock.class);
        nowInode.setSize(512*(contents.size()-1)+contents.get(contents.size()-1).length()+48); //设置好内存大小
        System.out.println(JSON.toJSONString(nowInode)+"\nwith change\n"+JSON.toJSONString(nowData));
        overwriteBlock(headId,JSON.toJSONString(nowInode));
        getTree();
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
                //System.out.println("Inode: "+nowInode.getIndirectData());
                DataBlock resData = JSON.parseObject(readBlock(nowInode.getIndirectData()).trim(), DataBlock.class);
                StringBuilder res = new StringBuilder();
                //System.out.println("resData: "+JSON.toJSONString(resData));
                res.append(resData.getData());
                while(resData.getNextDataId() != BLOCKNUM - 1){
                    //nowInode = JSON.parseObject(readBlock().trim(), IndexNode.class);
                    resData = JSON.parseObject(readBlock(resData.getNextDataId()).trim(), DataBlock.class);
                    res.append(resData.getData());
                }
                return res.toString();
            }
        }
        return null; //没找到
    }
//    boolean rename(String oldName, String newName){
//
//    }
}