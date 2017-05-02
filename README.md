
Project Overview


Link to simpleDB - 
http://www.cs.bc.edu/~sciore/simpledb/intro.html

###Background
A buffer pool contains a set of buffer and each buffer can hold a page. Data is stored on disk as blocks and after read into main memory are represented as pages. Reading and writing pages from main memory to disk is an important task of a database system.  Main memory is partitioned into collections of pages. Data is stored on disk as blocks and after being read into main memory are represented as pages. Each page can be hold in a buffer and the collection of buffers is called the buffer pool. The buffer manager is responsible for bringing blocks from disk to the buffer pool when they are needed and writing blocks back to the disk when they have been updated.  The buffer manager keeps a pin count and dirty flag for each buffer in the buffer pool.  The pin count records the number of times a pagehas been requested but not released, and the dirty flag records whether the pagehas been updated or not.  As the buffer pool fills, some pages may need to be removed in order to make room for new pages. The buffer manager uses a replacement policy to choose pages to be flushed from the buffer pool.  The strategy used can greatly affect the performance of the system. LRU (least recently used), MRU (most recently used) and Clock are different policies that are appropriate to use under different conditions.

SimpleDB  Buffer Manager (from Dr.EdwardSciore’s notes)
The SimpleDB buffer manager is grossly inefficient in two ways: 
•	When looking for a buffer to replace, it uses the first unpinned buffer it finds, instead of using some intelligent replacement policy. 
•	When checking to see if a block is already in a buffer, it does a sequential scan of the buffers, instead of keeping a data structure (such as a map) to more quickly locate the buffer. 

Project Description

Task 1 (Optional). Extended the FileMgr class so that it includes code for maintaining useful statistics such as number of blocks read/written. Added a new method getFileStatistics() to the class that will return these statistics. Modified the methods commit and rollback of the class RemoteConnectionImpl (in package simpledb.remote) so that they print these statistics. The result will be that the server prints the statistics for each SQL statement it executes. Note that your code can obtain the FileMgr object by calling the static method SimpleDB.fileMgr (in the package simpledb.server).


Task 2: Buffer Management. This task has three subtasks: 
1.	Use a Map data structure to keep track of the buffer pool
Kept a Map of allocated buffers, keyed on the block they contain.  (A buffer is allocated when its contents is not null, and may be pinned or unpinned.  A buffer starts out unallocated; it becomes allocated when it is first assigned to a block, and stays allocated forever after.)  Used this map to determine if a block is currently in a buffer.  When a buffer is replaced, changed the map -- The mapping for the old block must be removed, and the mapping for the new block must be added.  For our convenience, we will be using “bufferPoolMap” as the name of the Map. 


2.	Modify the buffer replacement policy to the Most Recently Modified (MRM)
This suggests a page replacement strategy that chooses the modified page with the highest LSN (Log Sequence Number). Implement this strategy.
   
3.	Buffer Manager Statistics: 
Extended the buffer manager to return useful statistics information like number of times buffers are read and written. Added a new method(s) getBufferStatistics() for this purpose. Looked at the description in Task 1 to see how to set up the testing of the code.



Code snippets 	
Below, methods that we will use to test implementation of the Map for the Buffer pool. Copy them into the respective java files. For our convenience, we have used “bufferPoolMap” as the name of the Map. 

BasicBufferMgr.java 
/**  
* Determines whether the map has a mapping from  
* the block to some buffer.  
* @paramblk the block to use as a key  
* @return true if there is a mapping; false otherwise  
*/  
booleancontainsMapping(Block blk) {  
returnbufferPoolMap.containsKey(blk);  
} 
/**  
* Returns the buffer that the map maps the specified block to.  
* @paramblk the block to use as a key  
* @return the buffer mapped to if there is a mapping; null otherwise  
*/  
Buffer getMapping(Block blk) {  
returnbufferPoolMap.get(blk);  
} 

BufferMgr.java 
/**  
* Determines whether the map has a mapping from  
* the block to some buffer.  
* @paramblk the block to use as a key  
* @return true if there is a mapping; false otherwise  
*/  
publicbooleancontainsMapping(Block blk) {  
returnbufferMgr.containsMapping(blk);  
} 
/**  
* Returns the buffer that the map maps the specified block to.  
* @paramblk the block to use as a key  
* @return the buffer mapped to if there is a mapping; null otherwise  
*/  
public Buffer getMapping(Block blk) {  
returnbufferMgr.getMapping(blk);  
}  
