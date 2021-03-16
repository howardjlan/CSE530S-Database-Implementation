package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import hw1.Catalog;
import hw1.Database;
import hw1.HeapFile;
import hw1.HeapPage;
import hw1.IntField;
import hw1.StringField;
import hw1.Tuple;
import hw1.TupleDesc;
import hw4.BufferPool;
import hw4.Permissions;

public class YourHW4Tests {

	private Catalog c;
	private BufferPool bp;
	private HeapFile hf;
	private TupleDesc td;
	private int tid;
	private int tid2;
	
	@Before
	public void setup() {
		
		try {
			Files.copy(new File("testfiles/test.dat.bak").toPath(), new File("testfiles/test.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("unable to copy files");
			e.printStackTrace();
		}
		
		Database.reset();
		c = Database.getCatalog();
		c.loadSchema("testfiles/test.txt");
		c.loadSchema("testfiles/test2.txt");
		
		int tableId = c.getTableId("test");
		td = c.getTupleDesc(tableId);
		hf = c.getDbFile(tableId);
		
		Database.resetBufferPool(BufferPool.DEFAULT_PAGES);

		bp = Database.getBufferPool();
		
		
		tid = c.getTableId("test");
		tid2 = c.getTableId("test2");
	}
	
	@Test
	public void testReleaseLocksAfterAbort() throws Exception {
			Tuple t = new Tuple(td);
			t.setField(0, new IntField(new byte[] {0, 0, 0, (byte)131}));
			byte[] s = new byte[129];
			s[0] = 1;
			s[1] = 3;
			s[2] = 55;
			t.setField(1, new StringField(s));
			
			bp.getPage(0, tid, 0, Permissions.READ_WRITE); //acquire lock for the page
			bp.insertTuple(0, tid, t); //insert the tuple into the page
			bp.transactionComplete(0, false); //should abort, discard changes
			
			assertFalse(bp.holdsLock(0, tid, 0));
	}
	

	@Test
	public void testInsertReadOnly() throws Exception{
		Tuple t = new Tuple(td);
		t.setField(0, new IntField(new byte[] {0, 0, 0, (byte)131}));
		byte[] s = new byte[129];
		s[0] = 2;
		s[1] = 98;
		s[2] = 121;
		t.setField(1, new StringField(s));
		
		bp.getPage(0, tid, 0, Permissions.READ_ONLY); 
		
		try {
			bp.insertTuple(0, tid, t); 
		}
		catch(Exception e) {
			fail("Do not have write Permission.");
		}
		assertTrue(true);
	}

}
