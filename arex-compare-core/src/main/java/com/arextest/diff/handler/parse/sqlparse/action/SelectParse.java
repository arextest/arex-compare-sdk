package com.arextest.diff.handler.parse.sqlparse.action;

import com.arextest.diff.handler.parse.sqlparse.Parse;
import com.arextest.diff.handler.parse.sqlparse.constants.Constants;
import com.arextest.diff.handler.parse.sqlparse.select.ArexSelectVisitorAdapter;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;

/**
 * Created by rchen9 on 2023/1/6.
 * the example of parsed select sql:
 * {
 *   "action": "SELECT",
 *   "columns": {
 *     "a.Salary": ""
 *   },
 *   "from": {
 *     "table": {
 *       "action": "SELECT",
 *       "columns": {
 *         "*": "",
 *         "dense_rank() over(partition by departmentid order by Salary desc) as rnk": ""
 *       },
 *       "from": {
 *         "table": [
 *           "Employee"
 *         ]
 *       }
 *     },
 *     "alias": "a"
 *   },
 *   "join": [
 *     {
 *       "type": "LEFT join",
 *       "table": "department b",
 *       "on": {
 *         "a.departmentid = b.Id AND a.aa = b.aa": ""
 *       }
 *     }
 *   ],
 *   "where": {
 *     "andor": [
 *       "and",
 *       "and"
 *     ],
 *     "columns": {
 *       "a.rnk <= 3": "",
 *       "a.per_id in (select per_id from colle_subject)": ""
 *     }
 *   }
 * }
 *
 */
public class SelectParse implements Parse<Select> {
    @Override
    public ObjectNode parse(Select parseObj) {
        ObjectNode sqlObject = JacksonHelperUtil.getObjectNode();
        sqlObject.put(Constants.ACTION, Constants.SELECT);
        SelectBody selectBody = parseObj.getSelectBody();
        ArexSelectVisitorAdapter arexSelectVisitorAdapter = new ArexSelectVisitorAdapter(sqlObject);
        selectBody.accept(arexSelectVisitorAdapter);
        return sqlObject;
    }
}
