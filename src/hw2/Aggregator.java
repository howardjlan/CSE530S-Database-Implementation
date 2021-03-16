//Howard Lan, Amy Kim
package hw2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import hw1.Field;
import hw1.IntField;
import hw1.RelationalOperator;
import hw1.StringField;
import hw1.Tuple;
import hw1.TupleDesc;
import hw1.Type;

/**
 * A class to perform various aggregations, by accepting one tuple at a time
 * @author Doug Shook
 *
 */
public class Aggregator {
	
	private AggregateOperator o;
	private boolean groupBy;
	private TupleDesc td;
	private ArrayList<Tuple> aggTuples;
	int total;
	boolean isIntField = true;
	
	public Aggregator(AggregateOperator o, boolean groupBy, TupleDesc td) {
		//your code here
		this.o = o;
		this.groupBy = groupBy;
		this.td = td;
		this.aggTuples = new ArrayList<Tuple>();
		this.total = 0;
	}

	/**
	 * Merges the given tuple into the current aggregation
	 * @param t the tuple to be aggregated
	 */
	public void merge(Tuple t) {
		//your code here
		if(!this.groupBy)
		{
			if(t.getDesc().getType(0) == Type.INT){
				this.isIntField = true;
			}else {
				this.isIntField = false;
			}

			if(this.o == AggregateOperator.AVG) {
					this.total++;	
			}
			this.aggTuples.add(t);	
		}
		else 
		{
			this.aggTuples.add(t);	//!!
		}
	}
	
	public ArrayList<Tuple> createIntTupleAndAdd(int fieldVal)
	{
		ArrayList<Tuple> aggTuplesResult = new ArrayList<Tuple>();
		Tuple aTuple = new Tuple(this.td);
		aTuple.setField(0, new IntField(fieldVal));
		aggTuplesResult.add(aTuple);
		return aggTuplesResult;
	}
	
	public ArrayList<Tuple> createStrTupleAndAdd(String str)
	{
		ArrayList<Tuple> aggTuplesResult = new ArrayList<Tuple>();
		Tuple aTuple = new Tuple(this.td);
		aTuple.setField(0, new StringField(str));
		aggTuplesResult.add(aTuple);
		return aggTuplesResult;
	}
	
	public ArrayList<Tuple> IntmergeMinResult(boolean groupBy)
	{
		if(!this.groupBy){
			int minval = Integer.MAX_VALUE;
			for(int i = 0; i < aggTuples.size(); i++){
				if(((IntField)(aggTuples.get(i).getField(0))).getValue() < minval){
					minval = ((IntField)(aggTuples.get(i).getField(0))).getValue();
				}
			}
			return createIntTupleAndAdd(minval);
		}
		else
		{
			//!!
			ArrayList<Tuple> aggTuplesResult = new ArrayList<Tuple>();
			HashMap<Integer, Integer> groupsAndMins = new HashMap<>();
			for(Tuple t: aggTuples) {
				int key = ((IntField)(t.getField(0))).getValue();
				int val = ((IntField)(t.getField(1))).getValue();
				if(groupsAndMins.containsKey(key) && val < groupsAndMins.get(key)) {
					groupsAndMins.replace(key, val);
				} else if(!groupsAndMins.containsKey(key)){
					groupsAndMins.put(key, val);
				}
			}
			for(Map.Entry<Integer, Integer> entry: groupsAndMins.entrySet()) {
				Tuple aTuple = new Tuple(this.td);
				aTuple.setField(0, new IntField(entry.getKey()));
				aTuple.setField(1, new IntField(entry.getValue()));
				aggTuplesResult.add(aTuple);
			}
			return aggTuplesResult;
		}
		//return null;
	}
	
	public ArrayList<Tuple> StrmergeMinResult(boolean groupBy)
	{
		if(!this.groupBy)
		{
			String minstr = ((StringField)(aggTuples.get(0).getField(0))).getValue();
			for(int i = 0; i < aggTuples.size(); i++){
				if(((StringField)(aggTuples.get(i).getField(0))).getValue().compareTo(minstr) < 0)
				{
					minstr = ((StringField)(aggTuples.get(i).getField(0))).getValue();
				}
		    }
			return createStrTupleAndAdd(minstr);
		} else {
			ArrayList<Tuple> aggTuplesResult = new ArrayList<Tuple>();
			HashMap<Integer, String> groupsAndMins = new HashMap<>();
			for(Tuple t: aggTuples) {
				int key = ((IntField)(t.getField(0))).getValue();
				String val = ((StringField)(t.getField(1))).getValue();
				if(groupsAndMins.containsKey(key) && val.compareTo(groupsAndMins.get(key)) > 0) {
					groupsAndMins.replace(key, val);
				} else if(!groupsAndMins.containsKey(key)){
					groupsAndMins.put(key, val);
				}
			}
			for(Map.Entry<Integer, String> entry: groupsAndMins.entrySet()) {
				Tuple aTuple = new Tuple(this.td);
				aTuple.setField(0, new IntField(entry.getKey()));
				aTuple.setField(1, new StringField(entry.getValue()));
				aggTuplesResult.add(aTuple);
			}
			return aggTuplesResult;
		}
		//return null;
	}
	
	public ArrayList<Tuple> IntmergeMaxResult(boolean groupBy)
	{
		if(!this.groupBy)
		{
				int maxval = Integer.MIN_VALUE;
				for(int i = 0; i < aggTuples.size(); i++){
					if(((IntField)(aggTuples.get(i).getField(0))).getValue() > maxval){
						maxval = ((IntField)(aggTuples.get(i).getField(0))).getValue();
					}
				}
				return createIntTupleAndAdd(maxval);
		}
		else
		{
			ArrayList<Tuple> aggTuplesResult = new ArrayList<Tuple>();
			HashMap<Integer, Integer> groupsAndMaxs = new HashMap<>();
			for(Tuple t: aggTuples) {
				int key = ((IntField)(t.getField(0))).getValue();
				int val = ((IntField)(t.getField(1))).getValue();
				if(groupsAndMaxs.containsKey(key) && val > groupsAndMaxs.get(key)) {
					groupsAndMaxs.replace(key, val);
				} else if(!groupsAndMaxs.containsKey(key)){
					groupsAndMaxs.put(key, val);
				}
			}
			for(Map.Entry<Integer, Integer> entry: groupsAndMaxs.entrySet()) {
				Tuple aTuple = new Tuple(this.td);
				aTuple.setField(0, new IntField(entry.getKey()));
				aTuple.setField(1, new IntField(entry.getValue()));
				aggTuplesResult.add(aTuple);
			}
			return aggTuplesResult;
		}
		//return null;
	}
	
	public ArrayList<Tuple> StrmergeMaxResult(boolean groupBy)
	{
		if(!this.groupBy)
		{
			String maxstr = ((StringField)(aggTuples.get(0).getField(0))).getValue();
			for(int i = 0; i < aggTuples.size(); i++){
				if(((StringField)(aggTuples.get(i).getField(0))).getValue().compareTo(maxstr) > 0)
				{
					maxstr = ((StringField)(aggTuples.get(i).getField(0))).getValue();
				}
		    }
			return createStrTupleAndAdd(maxstr);
		} else {
			ArrayList<Tuple> aggTuplesResult = new ArrayList<Tuple>();
			HashMap<Integer, String> groupsAndMaxs = new HashMap<>();
			for(Tuple t: aggTuples) {
				int key = ((IntField)(t.getField(0))).getValue();
				String val = ((StringField)(t.getField(1))).getValue();
				if(groupsAndMaxs.containsKey(key) && val.compareTo(groupsAndMaxs.get(key)) < 0) {
					groupsAndMaxs.replace(key, val);
				} else if(!groupsAndMaxs.containsKey(key)){
					groupsAndMaxs.put(key, val);
				}
			}
			for(Map.Entry<Integer, String> entry: groupsAndMaxs.entrySet()) {
				Tuple aTuple = new Tuple(this.td);
				aTuple.setField(0, new IntField(entry.getKey()));
				aTuple.setField(1, new StringField(entry.getValue()));
				aggTuplesResult.add(aTuple);
			}
			return aggTuplesResult;
		}
		//return null;
	}
	
	public ArrayList<Tuple> mergeCountResult(boolean groupBy)
	{
		if(!this.groupBy)
		{
			return createIntTupleAndAdd(this.aggTuples.size());
		}
		else
		{
			ArrayList<Tuple> aggTuplesResult = new ArrayList<Tuple>();
			HashMap<Integer, Integer> groupsAndCounts = new HashMap<>();
			for(Tuple t: aggTuples) {
				int key = ((IntField)(t.getField(0))).getValue();
				//int val = ((IntField)(t.getField(1))).getValue();
				if(groupsAndCounts.containsKey(key)) {
					groupsAndCounts.replace(key, groupsAndCounts.get(key)+1);
				} else {
					groupsAndCounts.put(key, 1);
				}
			}
			for(Map.Entry<Integer, Integer> entry: groupsAndCounts.entrySet()) {
				Tuple aTuple = new Tuple(this.td);
				aTuple.setField(0, new IntField(entry.getKey()));
				aTuple.setField(1, new IntField(entry.getValue()));
				aggTuplesResult.add(aTuple);
			}
			return aggTuplesResult;
		}
		//return null;
	}
	
	public ArrayList<Tuple> mergeSumResult(boolean groupBy)
	{
		if(!this.groupBy)
		{
			int sum = 0;
			for(int i = 0; i < aggTuples.size(); i++){
				sum += ((IntField)(aggTuples.get(i).getField(0))).getValue();
			}
			return createIntTupleAndAdd(sum);
		}
		else
		{
			//!! code tested by testGroupBy
			ArrayList<Tuple> aggTuplesResult = new ArrayList<Tuple>();
			HashMap<Integer, Integer> groupsAndSums = new HashMap<>();
			for(Tuple t: aggTuples) {
				int key = ((IntField)(t.getField(0))).getValue();
				int val = ((IntField)(t.getField(1))).getValue();
				if(groupsAndSums.containsKey(key)) {
					groupsAndSums.replace(key, groupsAndSums.get(key)+val);
				} else {
					groupsAndSums.put(key, val);
				}
			}
			for(Map.Entry<Integer, Integer> entry: groupsAndSums.entrySet()) {
				Tuple aTuple = new Tuple(this.td);
				aTuple.setField(0, new IntField(entry.getKey()));
				aTuple.setField(1, new IntField(entry.getValue()));
				aggTuplesResult.add(aTuple);
			}
			return aggTuplesResult;
		}
		//return null;
	}
	
	public ArrayList<Tuple>mergeAvgResult(boolean groupBy)
	{
		if(!this.groupBy)
		{
			int totalval = 0;
			for(int i = 0; i < aggTuples.size(); i++)
			{
				totalval += ((IntField)(aggTuples.get(i).getField(0))).getValue();
			}
			totalval = (int) totalval/this.total;
			return createIntTupleAndAdd(totalval);

		}
		else
		{
			// think about it
			ArrayList<Tuple> aggTuplesResult = new ArrayList<Tuple>();
			HashMap<Integer, Integer> groupsAndSums = new HashMap<>();
			HashMap<Integer, Integer> groupsAndCounts = new HashMap<>();
			for(Tuple t: aggTuples) {
				int key = ((IntField)(t.getField(0))).getValue();
				int val = ((IntField)(t.getField(1))).getValue();
				if(groupsAndSums.containsKey(key)) {
					groupsAndSums.replace(key, groupsAndSums.get(key)+val);
				} else {
					groupsAndSums.put(key, val);
				}
				
				if(groupsAndCounts.containsKey(key)) {
					groupsAndCounts.replace(key, groupsAndCounts.get(key)+1);
				} else {
					groupsAndCounts.put(key, 1);
				}
			}
			for(Map.Entry<Integer, Integer> entry: groupsAndSums.entrySet()) {
				Tuple aTuple = new Tuple(this.td);
				aTuple.setField(0, new IntField(entry.getKey()));
				aTuple.setField(1, new IntField(entry.getValue()/groupsAndCounts.get(entry.getKey())));
				aggTuplesResult.add(aTuple);
			}
			return aggTuplesResult;
		}
		//return null;
	}
	
	/**
	 * Returns the result of the aggregation
	 * @return a list containing the tuples after aggregation
	 */
	public ArrayList<Tuple> getResults() {

		if(this.o == AggregateOperator.MIN) {
			if(this.isIntField){
				return IntmergeMinResult(this.groupBy);
			}
			else {
				return StrmergeMinResult(this.groupBy);
			}
		}
		else if(this.o == AggregateOperator.MAX) {
			if(this.isIntField) {
				return IntmergeMaxResult(this.groupBy);
			}else {
				return StrmergeMaxResult(this.groupBy);
			}
		}
		else if(this.o == AggregateOperator.COUNT) {
			return mergeCountResult(this.groupBy);
		}
		else if(this.o == AggregateOperator.SUM) {
			return mergeSumResult(this.groupBy);
		}
		else if(this.o == AggregateOperator.AVG) {
			return mergeAvgResult(this.groupBy);
		}
		return null;

	}

}
