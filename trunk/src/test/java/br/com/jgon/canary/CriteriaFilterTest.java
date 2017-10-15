package br.com.jgon.canary;

public class CriteriaFilterTest {

/*
	public void checarRegex(){

		Pattern p = Pattern.compile("(?<=^in(\\(|\\s\\())[a-zA-Z0-9,\\s_-\u00C0-\u00FF]+(?=\\)$)");
		Matcher m = p.matcher("in(12,10,15)");
		//"
		if(m.find()){
			System.out.println(m.group());
		}

		CriteriaFilter<Object> c = new CriteriaFilterImpl(null);
		c.addWhereValueWithExpression("a", ">=2000-10-10<=2015-10-10", false)
		.addWhereValueWithExpression("b", "=10", false)
		.addWhereValueWithExpression("c", "<=15", false)
		.addWhereValueWithExpression("d", ">=10", false)
		.addWhereValueWithExpression("e", "!=5", false)
		.addWhereValueWithExpression("f", ">=10<=30", false)
		.addWhereValueWithExpression("g", "%jura", false)
		.addWhereValueWithExpression("h", "%jura%", false)
		.addWhereValueWithExpression("i", "jura%", false)
		.addWhereValueWithExpression("j", "*jura", false)
		.addWhereValueWithExpression("k", "*jura*", false)
		.addWhereValueWithExpression("l", "jura*", false)
		.addWhereValueWithExpression("m", "=%jura", false)
		.addWhereValueWithExpression("n", "=*jura", false)
		.addWhereValueWithExpression("o", "null", false)
		.addWhereValueWithExpression("p", "not null", false)
		.addWhereValueWithExpression("q", "in(12,10,15)", false)
		.addWhereValueWithExpression("r", "not in(10,5,15)", false);

		for(String f : c.getWhereRestriction().getRestrictions().keySet()){
			System.out.println( f + ": " + c.getWhereRestriction(f));
		}
		for(String f: c.getListWhere().keySet()){
			System.out.println( f + ": " + c.getWhere(f));
		}
	}
	}*/
}
