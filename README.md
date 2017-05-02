# simpleDB
Modified simpleDB to include Most Recently Modified replacement Policy with a bufferPoolMap structure


The following files were modified. Please replace these files in the server accordingly.


Files Modified:
-------------------


BasicBufferMgr.java
--------------------

Path to the file:
--------------------
/simpleDB/simpledb/buffer/BasicBufferMgr.java

Variables Added/Modified:
-----------------
1. private Stack<Buffer> bufferpool;		//Modified the bufferpool into stack.
2. private Map<Block, Buffer> bufferPoolMap = new HashMap<>();	// Created map Bufferpoolmap that contains block as a key and buffer as the values
3. private Map<Buffer, Block> bufferBlockMap = new HashMap<>();	//  Created map bufferblockmap that contains buffer as the key and block as the value (Reverse of bufferPoolMap)
4. private int readCount;	//Variable to hold readCount
5. private int writeCount;	//Variable to hold writeCount
6. private int numBuffs;	//Varialble to hold number of available buffers

Functions Added/Modified :
--------------------

BasicBufferMgr() : 
------------------
1.Initialised readCount and writeCount to 0.

2.  Also a new Buffer object was created for the number of buffers (numbuffs) and pushed on to the bufferpool stack.

updateBufferPool() :
--------------------

This Function is called whenever the buffer is written. If the written buffer is already present in the stack, it removes it from the buffer Stack and pushes to the top of stack - To keep track of the latest modified buffer.

chooseUnpinnedBuffer() :
------------------------
From the bufferpool stack, this function returns the buffer from the top of the stack,if the buffer is unpinned . The first buffer in the traversal that is not pinned is returned .


void incrementReadCount() :
---------------------------
Function to increment the readCount by 1. (Post-increment)

incrementWriteCount() :
-----------------------
Function to increment the writeCount by 1. (Post-increment)

synchronized Buffer pin() :
---------------------------
> A synchronized method that ensures synchronized access to the buffer while performing the pinning operation.

1. The findExistingBuffer function is called and if the value returned is null, we choose an unpinned buffer.

2. If we are unable to find an unpinned buffer, we return a null value that is assigned to the bufferblock.

3. We then update the bufferPoolMap and also the bufferBlockMap accordingly.

synchronized Buffer pinNew() :
---------------------------
> A synchronized method that ensures synchronized access to the buffer while performing the pinning operation.

1. We choose an unpinned buffer.

2. If we are unable to find an unpinned buffer, we return a null value that is assigned to the bufferblock.

3. We then update the bufferPoolMap and also the bufferBlockMap accordingly.


int available() :
------------------------
1. Added a command to print the bufferPoolMap for reference

private Buffer findExistingBuffer(Block blk):
----------------------------------------------
Modified this function to return the value from the bufferPoolMap if the key/block is present. If not it will return a null

private Buffer chooseUnpinnedBuffer():
-----------------------------------------
Modified the function to iterate from top of the stack to the bottom, and return the first unpinned buffer

public boolean containsMapping(Block blk):
-------------------------------------------
Added this function as requested in the given document

public Buffer getMapping(Block blk):
------------------------------------
Added this function as requested in the given document

public void getBufferStatistics() :
-----------------------------------
This function prints the buffer statistics using the readCount and writeCount variable



Buffer.java
--------------------

Path to the file:
--------------------
/simpleDB/simpledb/buffer/Buffer.java

Variables Added/Modified:
-----------------------------
1. private BasicBufferMgr basicBufferMgr = null;	// Included an instance of basicBufferMgr


Functions Added/Modified :
--------------------------

void setBufferMgr(BasicBufferMgr basicBufferMgr) :
--------------------------------------------------
1. Assigning the basicBufferMgr variable of the current object to the passed argument.

int getInt() :
--------------
> Calls basicBufferMgr.incrementReadCount() to increment the readCount by 1.

String getString() :
--------------------
> Call basicBufferMgr.incrementReadCount() to increment the readCount by 1.

public void setInt() :
----------------------
> Call the functions basicBufferMgr.updateBufferPool(this) and basicBufferMgr.incrementWriteCount() once the setInt() method is called

public void setString() :
----------------------
> Call the functions basicBufferMgr.updateBufferPool(this) and basicBufferMgr.incrementWriteCount() once the setString() method is called


BufferMgr.java
--------------------

Path to the file:
--------------------
/simpleDB/simpledb/buffer/BufferMgr.java

Variables Added/Modified:
-----------------------------

1. private BasicBufferMgr bufferMgr;	// Created an instance  of BasicBufferMgr


Functions Added/Modified :
--------------------------

public void getBufferStatistics():
-----------------------------------

Added this function that would return the bufferStatistics when called

public boolean containsMapping(Block blk)
------------------------------------------
Added this function as requested in the given project document.


public Buffer getMapping(Block blk)
------------------------------------------
Added this function as requested in the given project document.


FileMgr.java :
--------------

Path to the file:
--------------------
/simpleDB/simpledb/file/FileMgr.java

Variables Added/Modified:
-----------------------------
1.private int readCount;	// Varialbe to hold the File readCount
2.private int writeCount;	// Variable to hold the File Write Count


Functions Added/Modified :
--------------------------

public FileMgr(String dbname):
--------------------------------
Initialised readCount and writecount to 0

synchronized void read(Block blk, ByteBuffer bb):
---------------------------------------------------
Added a line to call the function incrementReadCount()

synchronized void write(Block blk, ByteBuffer bb):
---------------------------------------------------
Added a line to call the function incrementWriteCount()

private void incrementReadCount():
-----------------------------------
This function increments the File read count, and is called whenever there is file read


private void incrementWriteCount():
-----------------------------------
This function increments the File write count, and is called whenever there is file write

public void getFileStatistics():
----------------------------------
Prints the file statistics- ReadCount and Write Count


RemoteConnectionImpl.java:
--------------------------
/simpleDB/simpledb/remote/RemoteConnectionImpl.java

Path to the file:
--------------------

Functions  Modified/Added:
--------------------------
public void close() throws RemoteException:
-------------------------------------------
Added lines to invoke getBufferStatistics() and getFileStatistics() method - to print the buffer and file Statistics

void commit():
-------------------------------------------
Added lines to invoke getBufferStatistics() and getFileStatistics() method - to print the buffer and file Statistics

void rollback():
-------------------------------------------
Added lines to invoke getBufferStatistics() and getFileStatistics() method - to print the buffer and file Statistics


