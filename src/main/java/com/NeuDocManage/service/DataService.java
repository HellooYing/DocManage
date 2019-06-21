package com.NeuDocManage.service;

import java.util.Stack;

import static com.NeuDocManage.config.MainConfig.BLOCKNUM;
import static com.NeuDocManage.service.DiskService.superBlock;
import static com.NeuDocManage.service.BlockService.*;

public class DataService {
    private static Stack<Integer> blockStack;
    public static int getDataBlock(){
        if(blockStack==null){//如果blockStack未初始化，则说明是第一次调用本函数，要从超级块中获取盘块栈的信息。
            blockStack=new Stack<Integer>();
            int firstDataBlock= superBlock.getEmptyFileBlock();
            blockStack.push(firstDataBlock);//将第一个块的数据push进去
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
}
