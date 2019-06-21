package model;

public class Superblock {
    //超级块
    private int root;   //根目录位置
    //空闲栈
    private int emptyUserBlock;	//空用户块
    private int emptyDirBlock;	//空目录块
    private int _emptyDirBlock; //空目录块最后一块
    private int emptyFileBlock;	//空文件块
    private int _emptyFileBlock;//空文件块最后一块
    private int emptyIndexBlock;//空索引块
    private int _emptyIndexBlock;//空索引块最后一块

    public int getRoot() {
        return root;
    }

    public void setRoot(int root) {
        this.root = root;
    }

    public int getEmptyUserBlock() {
        return emptyUserBlock;
    }

    public void setEmptyUserBlock(int emptyUserBlock) {
        this.emptyUserBlock = emptyUserBlock;
    }

    public int getEmptyDirBlock() {
        return emptyDirBlock;
    }

    public void setEmptyDirBlock(int emptyDirBlock) {
        this.emptyDirBlock = emptyDirBlock;
    }

    public int get_emptyDirBlock() {
        return _emptyDirBlock;
    }

    public void set_emptyDirBlock(int _emptyDirBlock) {
        this._emptyDirBlock = _emptyDirBlock;
    }

    public int getEmptyFileBlock() {
        return emptyFileBlock;
    }

    public void setEmptyFileBlock(int emptyFileBlock) {
        this.emptyFileBlock = emptyFileBlock;
    }

    public int get_emptyFileBlock() {
        return _emptyFileBlock;
    }

    public void set_emptyFileBlock(int _emptyFileBlock) {
        this._emptyFileBlock = _emptyFileBlock;
    }

    public int getEmptyIndexBlock() {
        return emptyIndexBlock;
    }

    public void setEmptyIndexBlock(int emptyIndexBlock) {
        this.emptyIndexBlock = emptyIndexBlock;
    }

    public int get_emptyIndexBlock() {
        return _emptyIndexBlock;
    }

    public void set_emptyIndexBlock(int _emptyIndexBlock) {
        this._emptyIndexBlock = _emptyIndexBlock;
    }

}
