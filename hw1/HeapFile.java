//Howard Lan, Amy Kim
package hw1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A heap file stores a collection of tuples. It is also responsible for managing pages.
 * It needs to be able to manage page creation as well as correctly manipulating pages
 * when tuples are added or deleted.
 * @author Sam Madden modified by Doug Shook
 *
 */
public class HeapFile {
	
	public static final int PAGE_SIZE = 4096;
	private File f;
	private TupleDesc td;
	/**
	 * Creates a new heap file in the given location that can accept tuples of the given type
	 * @param f location of the heap file
	 * @param types type of tuples contained in the file
	 */
	public HeapFile(File f, TupleDesc type) {
		//your code here
		this.f = f;
		this.td = type;
	}
	
	public File getFile() {
		//your code here
		return this.f;
	}
	
	public TupleDesc getTupleDesc() {
		//your code here
		return this.td;
	}
	
	/**
	 * Creates a HeapPage object representing the page at the given page number.
	 * Because it will be necessary to arbitrarily move around the file, a RandomAccessFile object
	 * should be used here.
	 * @param id the page number to be retrieved
	 * @return a HeapPage at the given page number
	 */
	public HeapPage readPage(int id) {
		//your code here
		try {
				RandomAccessFile file = new RandomAccessFile(this.f, "r");
				file.seek(id*HeapFile.PAGE_SIZE);
				byte[] bt = new byte[HeapFile.PAGE_SIZE];
				file.read(bt);
				HeapPage hp = new HeapPage(id, bt, this.getId());
				file.close();
				return hp;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * Returns a unique id number for this heap file. Consider using
	 * the hash of the File itself.
	 * @return
	 */
	public int getId() {
		//your code here
		return this.f.hashCode();
	}
	
	/**
	 * Writes the given HeapPage to disk. Because of the need to seek through the file,
	 * a RandomAccessFile object should be used in this method.
	 * @param p the page to write to disk
	 */
	public void writePage(HeapPage p) {
		//your code here
			try {
				RandomAccessFile fl = new RandomAccessFile(this.f,"rw");
				fl.seek(p.getId() * HeapFile.PAGE_SIZE);
				fl.write(p.getPageData());
				fl.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	
	/**
	 * Adds a tuple. This method must first find a page with an open slot, creating a new page
	 * if all others are full. It then passes the tuple to this page to be stored. It then writes
	 * the page to disk (see writePage)
	 * @param t The tuple to be stored
	 * @return The HeapPage that contains the tuple
	 */
	public HeapPage addTuple(Tuple t) {
		//your code here
		for(int i = 0; i < this.getNumPages(); i++)
		{
			HeapPage currentPage = this.readPage(i);
			for(int j = 0; j < currentPage.getNumSlots(); j++)
			{
				if(!currentPage.slotOccupied(j))
				{
					try {
						currentPage.addTuple(t);
						this.writePage(currentPage);
						return currentPage;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}
		try {
			HeapPage hp = new HeapPage(this.getNumPages(), new byte[HeapFile.PAGE_SIZE], this.getId());
			hp.addTuple(t);
			this.writePage(hp);
			return hp;
			// **changed IOException to Exception
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This method will examine the tuple to find out where it is stored, then delete it
	 * from the proper HeapPage. It then writes the modified page to disk.
	 * @param t the Tuple to be deleted
	 */
	public void deleteTuple(Tuple t){
		try {
			HeapPage hp = this.readPage(t.getPid());
			hp.deleteTuple(t);
			this.writePage(hp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public HeapPage deleteTupleHP(Tuple t){
		try {
			HeapPage hp = this.readPage(t.getPid());
			hp.deleteTuple(t);
			this.writePage(hp);
			return hp;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns an ArrayList containing all of the tuples in this HeapFile. It must
	 * access each HeapPage to do this (see iterator() in HeapPage)
	 * @return
	 */
	public ArrayList<Tuple> getAllTuples() {
		ArrayList<Tuple> tupleslist = new ArrayList<Tuple>();
		for(int i = 0; i < this.getNumPages(); i++) 
		{
			Iterator<Tuple> it = this.readPage(i).iterator();
			it.forEachRemaining(tupleslist::add);
		}
		return tupleslist;
	}
	
	/**
	 * Computes and returns the total number of pages contained in this HeapFile
	 * @return the number of pages
	 */
	public int getNumPages() {
		return (int) (this.f.length() + HeapFile.PAGE_SIZE - 1)/HeapFile.PAGE_SIZE;
	}
}
