//Howard Lan, Amy Kim
package hw2;

import java.util.ArrayList;
import java.util.Arrays;

import hw1.Field;
import hw1.RelationalOperator;
import hw1.Tuple;
import hw1.TupleDesc;
import hw1.Type;

/**
 * This class provides methods to perform relational algebra operations. It will be used
 * to implement SQL queries.
 * @author Doug Shook
 *
 */
public class Relation {

	private ArrayList<Tuple> tuples;
	private TupleDesc td;
	
	public Relation(ArrayList<Tuple> l, TupleDesc td) {
		//your code here
		this.tuples = l;
		this.td = td;
	}
	
	/**
	 * This method performs a select operation on a relation
	 * @param field number (refer to TupleDesc) of the field to be compared, left side of comparison
	 * @param op the comparison operator
	 * @param operand a constant to be compared against the given column
	 * @return
	 */
	public Relation select(int field, RelationalOperator op, Field operand) {
		//your code here
		ArrayList<Tuple> tu = new ArrayList<Tuple>();
		
		for(Tuple t: this.tuples) 
		{
			if(t.getField(field).compare(op, operand)) 
			{
				tu.add(t);
			}
		}
		return new Relation(tu, this.td);
	}
	
	/**
	 * This method performs a rename operation on a relation
	 * @param fields the field numbers (refer to TupleDesc) of the fields to be renamed
	 * @param names a list of new names. The order of these names is the same as the order of field numbers in the field list
	 * @return
	 * @throws Exception 
	 */
	public Relation rename(ArrayList<Integer> fields, ArrayList<String> names) throws Exception {
		//your code here
		//make sure to check cannot rename to another col and ""
		
		// set myFields equal to td.fields and myType equal to td.types
		Type[] myType = new Type[this.td.numFields()];
		String[] myFields = new String[this.td.numFields()];
		for(int i = 0; i < this.td.numFields(); i++) 
		{
			myFields[i] = this.td.getFieldName(i);
			myType[i] = this.td.getType(i);
		}
		
		for(int i = 0; i < fields.size(); i++) 
		{
			if(Arrays.asList(myFields).contains(names.get(i)))
			{
				throw new Exception();
			}
			if(names.get(i).equals(""))
			{
				continue;
			}
			myFields[fields.get(i)] = names.get(i);
		}
		return new Relation(this.tuples, new TupleDesc(myType, myFields));
	}
	
	/**
	 * This method performs a project operation on a relation
	 * @param fields a list of field numbers (refer to TupleDesc) that should be in the result
	 * @return
	 */
	public Relation project(ArrayList<Integer> fields) {
		//your code here
		Type[] myType = new Type[fields.size()];
		String[] myFields = new String[fields.size()];
		
		try {
			for(int i = 0; i < fields.size(); i++) 
			{
				myFields[i] = this.td.getFieldName(fields.get(i));
				myType[i] = this.td.getType(fields.get(i));	
			}
			ArrayList<Tuple> tu = new ArrayList<Tuple>();
		
	        for (Tuple t: this.tuples) 
	        {
	            Tuple tup = new Tuple(new TupleDesc(myType, myFields));
	            for(int i = 0; i < fields.size(); i++) 
	            {
	                tup.setField(i, t.getField(fields.get(i)));
	            }
	            tu.add(tup);
	        }
	            
			if(fields.size() == 0)
			{
				ArrayList<Tuple> tuEmpty = new ArrayList<Tuple>();
				return new Relation(tuEmpty, new TupleDesc(myType, myFields));
			}
			else
			{
				return new Relation(tu, new TupleDesc(myType, myFields));
			}
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException();
		}

	}
	
	/**
	 * This method performs a join between this relation and a second relation.
	 * The resulting relation will contain all of the columns from both of the given relations,
	 * joined using the equality operator (=)
	 * @param other the relation to be joined
	 * @param field1 the field number (refer to TupleDesc) from this relation to be used in the join condition
	 * @param field2 the field number (refer to TupleDesc) from other to be used in the join condition
	 * @return
	 */
	public Relation join(Relation other, int field1, int field2) {
		//your code here
		ArrayList<Tuple> tu = new ArrayList<Tuple>();
		Type[] allTypes = new Type[this.td.numFields() + other.td.numFields()];
		String[] allFields = new String[this.td.numFields() + other.td.numFields()];
		
		for(int i = 0; i < this.td.numFields(); i++) 
		{
			allFields[i] = this.td.getFieldName(i);
			allTypes[i] = this.td.getType(i);
		}
		
		for(int j = this.td.numFields(); j < this.td.numFields() + other.td.numFields(); j++) 
		{
			allFields[j] = other.td.getFieldName(j - this.td.numFields());
			allTypes[j] = other.td.getType(j - this.td.numFields());
		}
		
		for(int i = 0; i < this.tuples.size(); i++) {
			for(int j = 0; j < other.tuples.size(); j++) {
				if(this.tuples.get(i).getField(field1).equals(other.tuples.get(j).getField(field2))) 
				{
					Tuple tup = new Tuple(new TupleDesc(allTypes, allFields));
                    for (int f1 = 0; f1 < this.getDesc().numFields(); f1++) {
                        tup.setField(f1, this.getTuples().get(i).getField(f1));
                    }
                    for (int f2 = this.getDesc().numFields(); 
                    		f2 < this.getDesc().numFields() +other.getDesc().numFields(); f2++) {
                        tup.setField(f2, 
                        		other.getTuples().get(j).getField(f2 - this.getDesc().numFields()));
                    }
					tu.add(tup);
				}
			}
		}
	
		return new Relation(tu, new TupleDesc(allTypes, allFields));
	}
	
	/**
	 * Performs an aggregation operation on a relation. See the lab write up for details.
	 * @param op the aggregation operation to be performed
	 * @param groupBy whether or not a grouping should be performed
	 * @return
	 */
	public Relation aggregate(AggregateOperator op, boolean groupBy) {
		//your code here
		Aggregator agg = new Aggregator(op, groupBy, this.td);
		for(Tuple t: tuples) {
			//System.out.println(t.getDesc().numFields());
			agg.merge(t);
		}
		ArrayList<Tuple> ourAgg = agg.getResults();
		
		return new Relation(agg.getResults(), this.td);
	}
	
	public TupleDesc getDesc() {
		//your code here
		return this.td;
	}
	
	public ArrayList<Tuple> getTuples() {
		//your code here
		return this.tuples;
	}
	
	/**
	 * Returns a string representation of this relation. The string representation should
	 * first contain the TupleDesc, followed by each of the tuples in this relation
	 */
	public String toString() {
		//your code here
		String output = "";
		output = output + this.td.toString() + " ";

		for(int i = 0; i < this.tuples.size(); i++)
		{
			output = output + this.tuples.get(i).toString() + " ";
		}
		return output;
	}
}
