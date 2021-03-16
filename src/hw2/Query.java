//Howard Lan, Amy Kim
package hw2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import hw1.Catalog;
import hw1.Database;
import hw1.HeapFile;
import hw1.Tuple;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class Query {

	private String q;
	
	public Query(String q) {
		this.q = q;
	}
	
	public Relation SQLFromQuery(PlainSelect sb, Catalog c, String tableName)
	{		
		return new Relation(c.getDbFile(c.getTableId(tableName)).getAllTuples(), 
				 c.getDbFile(c.getTableId(tableName)).getTupleDesc() );
	}
	
	public Relation SQLSelectQuery(PlainSelect sb, Relation r, ColumnVisitor cv)
	{		
		ArrayList<Integer> alltuplesQuery = new ArrayList<Integer>();
		for(int i = 0; i < sb.getSelectItems().size(); i++)
		{
			sb.getSelectItems().get(i).accept(cv);

            if (cv.getColumn() == "*") {
                return r;
            }
			alltuplesQuery.add(r.getDesc().nameToId(cv.getColumn()));
			if(sb.getSelectItems().get(i).toString().contains("AS")) {
				ArrayList<Integer> ren = new ArrayList<Integer>();
				ArrayList<String> names = new ArrayList<String>();
				ren.add(i);
				names.add(sb.getSelectItems().get(i).toString().split(" ")[2]);
				try {
					r = r.rename(ren, names);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return r.project(alltuplesQuery);
	}
	
	public Relation SQLWhereQuery(PlainSelect sb, Relation r,
			String tableName, WhereExpressionVisitor wev)
	{		
		sb.getWhere().accept(wev);
		return r.select(r.getDesc().nameToId(wev.getLeft()), wev.getOp(), wev.getRight());
	}
	
	public Relation SQLJoinsQuery(PlainSelect sb, Relation r, Catalog c, 
			String tableName, String joins)
	{
		int tid = c.getTableId(tableName);
		HeapFile hf = c.getDbFile(tid);
		int tid2 = c.getTableId(joins.split(" ")[1]);
		Relation other = new Relation(c.getDbFile(tid2).getAllTuples(),c.getTupleDesc(tid2));
		String tndotcol = joins.split(" ")[3];
		String tn2dotcol = joins.split(" ")[5];
		if(tndotcol.contains(tableName))
		{
			tndotcol = joins.split(" ")[3].split("[.]")[1];
			tn2dotcol = joins.split(" ")[5].split("[.]")[1];
		}
		else {
			tn2dotcol = joins.split(" ")[3].split("[.]")[1];
			tndotcol = joins.split(" ")[5].split("[.]")[1];
		}
		return r.join(other, r.getDesc().nameToId(tndotcol), other.getDesc().nameToId(tn2dotcol));
	}
	
	
	public Relation execute()  {
		Statement statement = null;
		try {
			statement = CCJSqlParserUtil.parse(q);
		} catch (JSQLParserException e) {
			System.out.println("Unable to parse query");
			e.printStackTrace();
		}
		Select selectStatement = (Select) statement;
		PlainSelect sb = (PlainSelect)selectStatement.getSelectBody();
		//your code here
		Catalog c = Database.getCatalog();
		String tableName = sb.getFromItem().toString().split(" ")[0];
		
		ColumnVisitor cv = new ColumnVisitor();
		WhereExpressionVisitor wev = new WhereExpressionVisitor();
		
		Relation output = SQLFromQuery(sb, c, tableName);
		
		if(sb.getJoins() != null)
		{
			if(sb.getJoins().size() == 1) {
				String singlejoin = sb.getJoins().get(0).toString();
				output = SQLJoinsQuery(sb, output,c, tableName, singlejoin);
			} else {
				for(int i = 0; i < sb.getJoins().size(); i++) {
					String firstTable = sb.getJoins().get(i).getOnExpression().toString().split("[.]")[0];
					output = SQLJoinsQuery(sb, output,c, firstTable, sb.getJoins().get(i).toString());
				}
			}
		}
		
		if(sb.getWhere() != null)
		{
			output = SQLWhereQuery(sb, output, tableName, wev);
		}
		
		output = SQLSelectQuery(sb,output, cv);
		
		boolean hasGroupBy = sb.getGroupByColumnReferences() != null;

		if(cv.getOp() == AggregateOperator.SUM ||
		   cv.getOp() == AggregateOperator.COUNT ||
		   cv.getOp() == AggregateOperator.SUM ||
		   cv.getOp() == AggregateOperator.MIN ||
		   cv.getOp() == AggregateOperator.MAX)
		{
			output = output.aggregate(cv.getOp(), hasGroupBy);
		}
		return output;
	}
}
