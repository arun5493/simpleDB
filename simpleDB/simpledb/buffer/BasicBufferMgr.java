package simpledb.buffer;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import simpledb.file.*;

/**
 * Manages the pinning and unpinning of buffers to blocks.
 * @author Edward Sciore
 *
 */
class BasicBufferMgr {
   private Stack<Buffer> bufferpool;
   private int numAvailable;
   private Map<Block, Buffer> bufferPoolMap = new HashMap<>();
   private Map<Buffer, Block> bufferBlockMap = new HashMap<>();
   private int readCount;
   private int writeCount;
   private int numBuffs;
   /**
    * Creates a buffer manager having the specified number 
    * of buffer slots.
    * This constructor depends on both the {@link FileMgr} and
    * {@link simpledb.log.LogMgr LogMgr} objects 
    * that it gets from the class
    * {@link simpledb.server.SimpleDB}.
    * Those objects are created during system initialization.
    * Thus this constructor cannot be called until 
    * {@link simpledb.server.SimpleDB#initFileAndLogMgr(String)} or
    * is called first.
    * @param numbuffs the number of buffer slots to allocate
    */
   BasicBufferMgr(int numbuffs) {
      bufferpool = new Stack<>();
      readCount = 0;
      writeCount = 0;
      numBuffs = numbuffs;
      numAvailable = numbuffs;
      for (int i=0; i<numbuffs; i++) {
    	  Buffer buff = new Buffer();
    	  buff.setBufferMgr(this);
    	  bufferpool.push(buff);
      }
   }
   
   void updateBufferPool(Buffer buff) {
	   if(bufferpool.remove(buff))
	     	 bufferpool.push(buff);
   }
   
   /**
    * Flushes the dirty buffers modified by the specified transaction.
    * @param txnum the transaction's id number
    */
   synchronized void flushAll(int txnum) {
      for (Buffer buff : bufferpool)
         if (buff.isModifiedBy(txnum))
         buff.flush();
   }
   
   /**
    * Pins a buffer to the specified block. 
    * If there is already a buffer assigned to that block
    * then that buffer is used;  
    * otherwise, an unpinned buffer from the pool is chosen.
    * Returns a null value if there are no available buffers.
    * @param blk a reference to a disk block
    * @return the pinned buffer
    */
   synchronized Buffer pin(Block blk) {
      Buffer buff = findExistingBuffer(blk);
      if (buff == null) {
         buff = chooseUnpinnedBuffer();
         if (buff == null)
            return null;
         buff.assignToBlock(blk);
         
         Block oldBlk = bufferBlockMap.get(buff);
         bufferPoolMap.remove(oldBlk);
         
         bufferPoolMap.put(blk, buff);
         bufferBlockMap.put(buff, blk);
         
      }
      if (!buff.isPinned())
         numAvailable--;
      buff.pin();
      return buff;
   }
   
   /**
    * Allocates a new block in the specified file, and
    * pins a buffer to it. 
    * Returns null (without allocating the block) if 
    * there are no available buffers.
    * @param filename the name of the file
    * @param fmtr a pageformatter object, used to format the new block
    * @return the pinned buffer
    */
   synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
      Buffer buff = chooseUnpinnedBuffer();
      if (buff == null)
         return null;
      buff.assignToNew(filename, fmtr);
      
      Block oldBlk = bufferBlockMap.get(buff);
      bufferPoolMap.remove(oldBlk);
      
      bufferPoolMap.put(buff.block(), buff);
      bufferBlockMap.put(buff, buff.block());
      
      numAvailable--;
      buff.pin();
      return buff;
   }
   
   /**
    * Unpins the specified buffer.
    * @param buff the buffer to be unpinned
    */
   synchronized void unpin(Buffer buff) {
      buff.unpin();
      if (!buff.isPinned())
         numAvailable++;
   }
   
   /**
    * Returns the number of available (i.e. unpinned) buffers.
    * @return the number of available buffers
    */
   int available() {
	   System.out.println(bufferPoolMap);
      return numAvailable;
   }
   
   private Buffer findExistingBuffer(Block blk) {
	   return bufferPoolMap.get(blk);
   }
   
   
   private Buffer chooseUnpinnedBuffer() { 
	  int sizeOfStack = bufferpool.size();
      for (int i=sizeOfStack-1;i>=0;i--)
    	if(!(bufferpool.get(i).isPinned()))
        	 return bufferpool.get(i);
      
      return null;
   }
   
   /**  
   * Determines whether the map has a mapping from  
   * the block to some buffer.  
   * @paramblk the block to use as a key  
   * @return true if there is a mapping; false otherwise  
   */  
   public boolean containsMapping(Block blk) {  
	   return bufferPoolMap.containsKey(blk);  
   } 
   /**  
   * Returns the buffer that the map maps the specified block to.  
   * @paramblk the block to use as a key  
   * @return the buffer mapped to if there is a mapping; null otherwise  
   */  
   public Buffer getMapping(Block blk) {  
	   return bufferPoolMap.get(blk);  
   } 
   
   void incrementReadCount() {
	   readCount++;
   }
   
   void incrementWriteCount() {
	   writeCount++;
   }
   
   public void getBufferStatistics() {
	   System.out.println("Number of times ");
	   System.out.println("\tBuffers written: " + writeCount);
	   System.out.println("\tBuffers read: " + readCount);
   }

}
