package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import hw1.Catalog;
import hw1.Database;
import hw1.HeapFile;
import hw1.IntField;
import hw1.RelationalOperator;
import hw1.TupleDesc;
import hw2.AggregateOperator;
import hw2.Relation;

public class YourHW2Tests {

	private HeapFile testhf;
	private TupleDesc testtd;
	private HeapFile ahf;
	private TupleDesc atd;
	private Catalog c;

	@Before
	public void setup() {
		
		try {
			Files.copy(new File("testfiles/test.dat.bak").toPath(), new File("testfiles/test.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
			Files.copy(new File("testfiles/A.dat.bak").toPath(), new File("testfiles/A.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("unable to copy files");
			e.printStackTrace();
		}
		
		c = Database.getCatalog();
		c.loadSchema("testfiles/test.txt");
		
		int tableId = c.getTableId("test");
		testtd = c.getTupleDesc(tableId);
		testhf = c.getDbFile(tableId);
		
		c = Database.getCatalog();
		c.loadSchema("testfiles/A.txt");
		
		tableId = c.getTableId("A");
		atd = c.getTupleDesc(tableId);
		ahf = c.getDbFile(tableId);
	}
	
	@Test
	public void test() {
		Relation ar = new Relation(ahf.getAllTuples(), atd);
		
		ArrayList<Integer> f = new ArrayList<Integer>();
		ArrayList<String> n = new ArrayList<String>();
		
		f.add(0);
		n.add(ar.getDesc().getFieldName(1));
		
		ar = ar.rename(f, n);
		fail("Column name already exists");
	}
	
	@Test
	public void test2() {
		Relation ar = new Relation(ahf.getAllTuples(), atd);
		ar = ar.select(0, RelationalOperator.LT, new IntField(530));
		
		assertTrue("Should be 5 tuples after select operation", ar.getTuples().size() == 3);
		assertTrue("select operation does not change tuple description", ar.getDesc().equals(atd));
	
	}

}
