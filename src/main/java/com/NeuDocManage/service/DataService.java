package com.NeuDocManage.service;

import java.util.Stack;

import static com.NeuDocManage.config.MainConfig.BLOCKNUM;
import static com.NeuDocManage.service.DiskService.superBlock;
import static com.NeuDocManage.service.BlockService.*;

public class DataService {
    private static Stack<Integer> blockStack;

    /**
     * 获取一个新的可以存数据的空盘块的id
     * @return new dataBlock id
     */
    public static int getDataBlock(){
        if(blockStack==null){//如果blockStack未初始化，则说明是重启后第一次调用本函数，要从超级块中获取盘块栈的信息。
            initDataBlockStack();
        }
        while(blockStack.size()==1){//如果栈里只有一个blockId了的话
            int status=addBlockToStack();//把这一个block里存的所有blockId都加到栈里
            if(status!=0) return BLOCKNUM-1;//如果addBlockToStack没有正常返回,getDataBlock也返回错误码1
        }
        if(blockStack.size()>1) return blockStack.pop();
        else{
            return BLOCKNUM-1;
        }
    }

    /**
     * 当栈里只有一个盘块时，这个盘块里应该存着新的一批盘块的id。本函数就是把那新一批盘块id加到栈中。
     * @return status(0代表正常，1代表未知错误，2代表磁盘已满)
     */
    private static int addBlockToStack(){
        if(blockStack==null||blockStack.size()!=1){
            System.out.println("addBlockToStack出现错误！");
            return 1;
        }
        int dataBlockId=blockStack.pop();
        if(dataBlockId==0){
            blockStack.push(dataBlockId);
            System.out.println("磁盘已满，无法继续存储！");
            return 2;
        }
        String[] dataBlockIds=readBlock(dataBlockId).trim().split(",");
        for (int i = dataBlockIds.length-1; i >=0 ; i--) {
            blockStack.push(Integer.parseInt(dataBlockIds[i]));
        }
        blockStack.push(dataBlockId);
        return 0;//正常返回
    }

    /**
     * 回收盘块，将盘块清空，再将盘块号加到栈中，以供下次重新分配。
     * @param blockId
     */
    public static void recoverDataBlock(int blockId){
        if(blockStack==null){//如果blockStack未初始化，则说明是重启后第一次调用本函数，要从超级块中获取盘块栈的信息。
            initDataBlockStack();
        }
        formatBlock(blockId);
        if(blockStack.size()>=100){
            removeBlockFromStack(blockId);
        }
        blockStack.push(blockId);
    }

    /**
     * 当栈内盘块多于100个时，将100个盘块的id写到最后那个盘块中，栈中只保留最新要回收的盘块
     */
    private static void removeBlockFromStack(int blockId){
        StringBuilder dataBlockIds=new StringBuilder();
        for (int i = 0; i <99 ; i++) {
            dataBlockIds.append(blockStack.pop()+",");
        }
        dataBlockIds.append(blockStack.pop());
        writeBlock(blockId,dataBlockIds.toString());
    }

    /**
     * 从超级块中获取盘块栈的信息
     */
    private static void initDataBlockStack(){
        blockStack=new Stack<Integer>();
        int firstDataBlock= superBlock.getEmptyFileBlock();
        blockStack.push(firstDataBlock);//将第一个块的数据push进去
    }
}
