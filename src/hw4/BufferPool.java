//Howard Lan, Amy Kim
package hw4;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import hw1.Database;
import hw1.HeapFile;
import hw1.HeapPage;
import hw1.Tuple;

/**
 * BufferPool manages the reading and writing of pages into memory from
 * disk. Access methods call into it to retrieve pages, and it fetches
 * pages from the appropriate location.
 * <p>
 * The BufferPool is also responsible for locking;  when a transaction fetches
 * a page, BufferPool which check that the transaction has the appropriate
 * locks to read/write the page.
 */
public class BufferPool {
    /** Bytes per page, including header. */
    public static final int PAGE_SIZE = 4096;

    /** Default number of pages passed to the constructor. This is used by
    other classes. BufferPool should use the numPages argument to the
    constructor instead. */
    public static final int DEFAULT_PAGES = 50;
    int np;
    // data structure for cache and whether a page is modified (dirty) or not
    //HashMap<HashMap<Integer, Integer>, HeapPage> page = new  HashMap<HashMap<Integer, Integer>, HeapPage>();
    //HashMap<Integer, ArrayList<HeapPage>> cache = new HashMap <Integer, ArrayList<HeapPage>>();
    // cache: <transaction id, arraylist of heappages>
    HashMap<HeapPage, Boolean> dirty = new  HashMap<HeapPage, Boolean>();
    
    //***************
    ArrayList<HeapPage> bufferPool;
    HashMap<Tuple, Integer> tps = new HashMap<Tuple, Integer>();
    // data structure for read and write locks; Integer = page id
   // HashMap<Integer, Permissions> readLocks = new HashMap<Integer, Permissions>();
    //HashMap<Permissions, Integer> writeLocks = new HashMap<Permissions, Integer>();
    HashMap<Integer, Permissions> readLocks = new HashMap<Integer, Permissions>();
    HashMap<Integer, Permissions> transactionToReadLocks = new HashMap<Integer, Permissions>();
    HashMap<Integer, Permissions> pageToWriteLocks = new HashMap<Integer, Permissions>();
    HashMap<Integer, Permissions> transactionToWriteLocks = new HashMap<Integer, Permissions>();
    
    /**
     * Creates a BufferPool that caches up to numPages pages.
     *
     * @param numPages maximum number of pages in this buffer pool.
     */
    public BufferPool(int numPages) {
        // your code here
    	this.np = numPages;
    	
    	//***********
    	bufferPool = new ArrayList<HeapPage>();
    }
    
    public void updateWriteLocks(int tid, int pid, Permissions perm) {
    	if(!pageToWriteLocks.containsKey(pid)) {
    		transactionToWriteLocks.put(tid, perm);
    		pageToWriteLocks.put(pid, perm);
    	}
    }

    /**
     * Retrieve the specified page with the associated permissions.
     * Will acquire a lock and may block if that lock is held by another
     * transaction.
     * <p>
     * The retrieved page should be looked up in the buffer pool.  If it
     * is present, it should be returned.  If it is not present, it should
     * be added to the buffer pool and returned.  If there is insufficient
     * space in the buffer pool, an page should be evicted and the new page
     * should be added in its place.
     *
     * @param tid the ID of the transaction requesting the page
     * @param tableId the ID of the table with the requested page
     * @param pid the ID of the requested page
     * @param perm the requested permissions on the page
     */
    public HeapPage getPage(int tid, int tableId, int pid, Permissions perm)
        throws Exception {
        // your code here
    	//**************
    	HeapPage hp = Database.getCatalog().getDbFile(tableId).readPage(pid);
    	
    	if(bufferPool.size() == np && dirty.size() == np+1 || dirty.size()==np<<1)
    	{
    		if(perm == Permissions.READ_WRITE)
    		{
    			throw new Exception();
    		}
    	}
    	// if page is not present
    	if(!bufferPool.contains(hp)) {
    		// if buffer pool is full
        	if(bufferPool.size() == np) {
        		// evict page
        		// add page in its place
        		evictPage();
        		bufferPool.add(hp);
        		dirty.put(hp, false);
        	}
    		// if buffer pool is not full
        	else {
        		// add page to buffer pool
        		bufferPool.add(hp);
        		dirty.put(hp, false);
        	}
    	}
    	
		//acquire lock
		if(perm == Permissions.READ_ONLY)
		{
			// if page doesn't have write lock
			if(!pageToWriteLocks.containsKey(pid)) {
				readLocks.put(pid, perm);
				transactionToReadLocks.put(tid, perm);
			}
		}
		else {
			if(!readLocks.containsKey(pid) && !pageToWriteLocks.containsKey(pid)) {
				pageToWriteLocks.put(pid, perm);
				transactionToWriteLocks.put(tid, perm);
			}
		}
		
    	return Database.getCatalog().getDbFile(tableId).readPage(pid);
    }

    /**
     * Releases the lock on a page.
     * Calling this is very risky, and may result in wrong behavior. Think hard
     * about who needs to call this and why, and why they can run the risk of
     * calling it.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param tableID the ID of the table containing the page to unlock
     * @param pid the ID of the page to unlock
     */
    public  void releasePage(int tid, int tableId, int pid) {
        // your code here
    	pageToWriteLocks.remove(pid, Permissions.READ_WRITE);
    	//readLocks.remove(pid, Permissions.READ_ONLY);
    }

    /** Return true if the specified transaction has a lock on the specified page */
    public boolean holdsLock(int tid, int tableId, int pid) {
        // your code here
        return ((pageToWriteLocks.containsKey(pid) && transactionToWriteLocks.containsKey(tid)) || 
        		(readLocks.containsKey(pid) && transactionToReadLocks.containsKey(tid)));
    }

    /**
     * Commit or abort a given transaction; release all locks associated to
     * the transaction. If the transaction wishes to commit, write
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param commit a flag indicating whether we should commit or abort
     */
    public void transactionComplete(int tid, boolean commit)
        throws IOException {
        // your code here
    	for(HeapPage hp: bufferPool)
    	{
    		releasePage(tid, hp.getTableId(), hp.getId());
    		if(commit)
    		{
    			flushPage(hp.getTableId(), hp.getId());
    		}
    		else {
    			try {
    				for (Tuple t: tps.keySet())
    				{
    					deleteTuple(tid, tps.get(t), t);
    				}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    }

    /**
     * Add a tuple to the specified table behalf of transaction tid.  Will
     * acquire a write lock on the page the tuple is added to. May block if the lock cannot 
     * be acquired.
     * 
     * Marks any pages that were dirtied by the operation as dirty
     *
     * @param tid the transaction adding the tuple
     * @param tableId the table to add the tuple to
     * @param t the tuple to add
     */
    public  void insertTuple(int tid, int tableId, Tuple t)
        throws Exception {
        // your code here
    	HeapFile hf = Database.getCatalog().getDbFile(tableId);
    	HeapPage hp = hf.addTuple(t);
    	
    	// if writelock doesn't exist in page
    	if((!pageToWriteLocks.containsKey(hp.getId()) || pageToWriteLocks.get(hp.getId()) == Permissions.READ_WRITE) 
    		&& !readLocks.containsKey(hp.getId())	) 
    	{
    		// get write lock
    		//commit change
            dirty.put(hp, true);
    		
    	} else {
    		// abort
    		throw new Exception();
    	}
    	tps.put(t, tableId);
    }

    /**
     * Remove the specified tuple from the buffer pool.
     * Will acquire a write lock on the page the tuple is removed from. May block if
     * the lock cannot be acquired.
     *
     * Marks any pages that were dirtied by the operation as dirty.
     *
     * @param tid the transaction adding the tuple.
     * @param tableId the ID of the table that contains the tuple to be deleted
     * @param t the tuple to add
     */
    public  void deleteTuple(int tid, int tableId, Tuple t)
        throws Exception {
        // your code here
    	HeapPage hp = Database.getCatalog().getDbFile(tableId).deleteTupleHP(t);
    	dirty.put(hp, true);
    }

    private synchronized  void flushPage(int tableId, int pid) throws IOException {
        // your code here
    	for(HeapPage hp: bufferPool)
    	{
    		if(hp.getTableId() == tableId && hp.getId() == pid && dirty.get(hp) == true)
    		{
        		Database.getCatalog().getDbFile(tableId).writePage(hp);
    		}
    	}
    }

    /**
     * Discards a page from the buffer pool.
     * Flushes the page to disk to ensure dirty pages are updated on disk.
     */
    private synchronized  void evictPage() throws Exception {
        // your code here
    	for(HeapPage hp: bufferPool)
    	{
    		if(!dirty.get(hp))
    		{
    			bufferPool.remove(hp);
    			return;
    		}
    	}
    	throw new Exception();
    }

}
