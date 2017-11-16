package test;

import java.util.ArrayList;
import java.util.List;

import simpledb.buffer.Buffer;
import simpledb.buffer.BufferMgr;
import simpledb.file.Block;
import simpledb.server.SimpleDB;

public class BufferTest {

	public static void main(String[] args) {
		SimpleDB.init("simpleDB");
		System.out.println("---------");
		BufferMgr basicBufferMgr = SimpleDB.bufferMgr();
		List<Block> blockList = new ArrayList<>();
		List<Buffer> bufferList = new ArrayList<>();
		for(int i=0;i<8;i++){
			Block blk = new Block("filename" + i + ".tbl", 1);
			blockList.add(blk);
		}
		System.out.println("Available: " + basicBufferMgr.available());
		for(Block blk:blockList){
			bufferList.add(basicBufferMgr.pin(blk));
		}
		System.out.println("Available: " + basicBufferMgr.available());
		basicBufferMgr.unpin(bufferList.get(3));
		System.out.println("Buffer Element to be removed is: " + bufferList.get(3));
		basicBufferMgr.unpin(bufferList.get(2));
		System.out.println("Buffer Element to be removed is: " + bufferList.get(2));
		System.out.println("Available: " + basicBufferMgr.available());
		basicBufferMgr.pin(new Block("filename9.tbl", 1));
		System.out.println("Available: " + basicBufferMgr.available());
		basicBufferMgr.pin(new Block("filename10.tbl", 1));
		System.out.println("Available: " + basicBufferMgr.available());
		
	}

}