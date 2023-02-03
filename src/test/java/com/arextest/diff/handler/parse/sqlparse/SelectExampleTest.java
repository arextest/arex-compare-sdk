package com.arextest.diff.handler.parse.sqlparse;

import com.arextest.diff.handler.parse.sqlparse.action.ActionFactory;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by rchen9 on 2023/1/11.
 */
@Ignore
public class SelectExampleTest {
    @Test
    public void testSelect() throws JSQLParserException {
        String sql = "select b.Name as Department,a.Name as Employee,a.Salary\n" +
                "from (select *,dense_rank() over(partition by departmentid order by Salary desc) as rnk from Employee) a \n" +
                "left join department b \n" +
                "on a.departmentid = b.Id and a.aa = b.aa and a.cc = b.cc\n" +
                "where a.rnk <= 3 and a.per_id in (select per_id from colle_subject);\n" +
                "\n";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testSelect2() throws JSQLParserException {
        String sql = "select name,per_id,dept_name from univ_subject a " +
                "where per_id in (select per_id from colle_subject);";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testSelect3() throws JSQLParserException {
        String sql = "select st_id from students union select st_id from student_skill;";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    // @Test
    // public void testSelect4() throws JSQLParserException {
    //     String sql = "SELECT \n" +
    //             "   \n" +
    //             "      y, \n" +
    //             "      sum(c1) AS m1, \n" +
    //             "      sum(c2) AS m2, \n" +
    //             "      sum(c3) AS m3, \n" +
    //             "      sum(c4) AS m4, \n" +
    //             "      sum(c5) AS m5, \n" +
    //             "      sum(c6) AS m6, \n" +
    //             "      sum(c7) AS m7, \n" +
    //             "      sum(c8) AS m8, \n" +
    //             "      sum(c9) AS m9, \n" +
    //             "      sum(c10) AS m10, \n" +
    //             "      sum(c11) AS m11, \n" +
    //             "      sum(c12) AS m12\n" +
    //             "FROM \n" +
    //             "   (\n" +
    //             "      SELECT \n" +
    //             "         \n" +
    //             "            y, \n" +
    //             "            \n" +
    //             "               CASE \n" +
    //             "                  m\n" +
    //             "                     WHEN 1 THEN c\n" +
    //             "                     ELSE 0\n" +
    //             "               END AS c1, \n" +
    //             "            \n" +
    //             "               CASE \n" +
    //             "                  m\n" +
    //             "                     WHEN 2 THEN c\n" +
    //             "                     ELSE 0\n" +
    //             "               END AS c2, \n" +
    //             "            \n" +
    //             "               CASE \n" +
    //             "                  m\n" +
    //             "                     WHEN 3 THEN c\n" +
    //             "                     ELSE 0\n" +
    //             "               END AS c3, \n" +
    //             "            \n" +
    //             "               CASE \n" +
    //             "                  m\n" +
    //             "                     WHEN 4 THEN c\n" +
    //             "                     ELSE 0\n" +
    //             "               END AS c4, \n" +
    //             "            \n" +
    //             "               CASE \n" +
    //             "                  m\n" +
    //             "                     WHEN 5 THEN c\n" +
    //             "                     ELSE 0\n" +
    //             "               END AS c5, \n" +
    //             "            \n" +
    //             "               CASE \n" +
    //             "                  m\n" +
    //             "                     WHEN 6 THEN c\n" +
    //             "                     ELSE 0\n" +
    //             "               END AS c6, \n" +
    //             "            \n" +
    //             "               CASE \n" +
    //             "                  m\n" +
    //             "                     WHEN 7 THEN c\n" +
    //             "                     ELSE 0\n" +
    //             "               END AS c7, \n" +
    //             "            \n" +
    //             "               CASE \n" +
    //             "                  m\n" +
    //             "                     WHEN 8 THEN c\n" +
    //             "                     ELSE 0\n" +
    //             "               END AS c8, \n" +
    //             "            \n" +
    //             "               CASE \n" +
    //             "                  m\n" +
    //             "                     WHEN 9 THEN c\n" +
    //             "                     ELSE 0\n" +
    //             "               END AS c9, \n" +
    //             "            \n" +
    //             "               CASE \n" +
    //             "                  m\n" +
    //             "                     WHEN 10 THEN c\n" +
    //             "                     ELSE 0\n" +
    //             "               END AS c10, \n" +
    //             "            \n" +
    //             "               CASE \n" +
    //             "                  m\n" +
    //             "                     WHEN 11 THEN c\n" +
    //             "                     ELSE 0\n" +
    //             "               END AS c11, \n" +
    //             "            \n" +
    //             "               CASE \n" +
    //             "                  m\n" +
    //             "                     WHEN 12 THEN c\n" +
    //             "                     ELSE 0\n" +
    //             "               END AS c12\n" +
    //             "      FROM \n" +
    //             "         (\n" +
    //             "            SELECT \n" +
    //             "               y, m, count(s_date) AS c\n" +
    //             "            FROM \n" +
    //             "               (\n" +
    //             "                  SELECT \n" +
    //             "                     datepart(year, CONVERT(DateTime, s_date)) AS y, datepart(month, CONVERT(DateTime, s_date)) AS m, s_date\n" +
    //             "                  FROM exam\n" +
    //             "               )  AS T1\n" +
    //             "            GROUP BY T1.y, T1.m\n" +
    //             "         )  AS T2\n" +
    //             "   )  AS T3\n" +
    //             "GROUP BY T3.y;";
    //     Statement parse = CCJSqlParserUtil.parse(sql);
    //     System.out.println();
    // }

    @Test
    public void testSelect5() throws JSQLParserException {
        String sql = "SELECT \n" +
                "   \n" +
                "      y1 年, \n" +
                "      m1 月, \n" +
                "      c1 本月销售额, \n" +
                "      c2 上月销售额, \n" +
                "      \n" +
                "         CASE \n" +
                "            WHEN c2 IS NULL OR c2 = 0 THEN '无穷大'\n" +
                "            ELSE CAST(CAST((isnull(c1, 0) - isnull(c2, 0)) * 100 / isnull(c2, 0) AS decimal(10, 2)) AS varchar(50)) + '%'\n" +
                "         END AS 环比增长, \n" +
                "      c3 去年本月销售额, \n" +
                "      \n" +
                "         CASE \n" +
                "            WHEN c3 IS NULL OR c3 = 0 THEN '无穷大'\n" +
                "            ELSE CAST(CAST((isnull(c1, 0) - isnull(c3, 0)) * 100 / isnull(c3, 0) AS decimal(10, 2)) AS varchar(50)) + '%'\n" +
                "         END AS 同比增长\n" +
                "FROM \n" +
                "   (\n" +
                "      SELECT \n" +
                "         \n" +
                "            y1, \n" +
                "            m1, \n" +
                "            c1, \n" +
                "            c2, \n" +
                "            c3\n" +
                "      FROM \n" +
                "         (\n" +
                "            SELECT \n" +
                "               y1, m1, c1, c2\n" +
                "            FROM \n" +
                "               (\n" +
                "                  SELECT \n" +
                "                     y1, m1, sum(Amt) AS c1\n" +
                "                  FROM \n" +
                "                     (\n" +
                "                        SELECT \n" +
                "                           datepart(year, CONVERT(DateTime, s_date)) AS y1, datepart(month, CONVERT(DateTime, s_date)) AS m1, Amt\n" +
                "                        FROM orders\n" +
                "                     )  AS T1\n" +
                "                  GROUP BY T1.y1, T1.m1\n" +
                "               )  o2 \n" +
                "               LEFT JOIN \n" +
                "               (\n" +
                "                  SELECT \n" +
                "                     y2, m2, sum(Amt) AS c2\n" +
                "                  FROM \n" +
                "                     (\n" +
                "                        SELECT \n" +
                "                           datepart(year, CONVERT(DateTime, s_date)) AS y2, datepart(month, CONVERT(DateTime, s_date)) AS m2, Amt\n" +
                "                        FROM orders\n" +
                "                     )  AS T1\n" +
                "                  GROUP BY T1.y2, T1.m2\n" +
                "               )  o3 ON o2.y1 = o3.y2 AND o2.m1 = o3.m2 - 1\n" +
                "         )  AS o4 \n" +
                "         LEFT JOIN \n" +
                "         (\n" +
                "            SELECT \n" +
                "               y3, m3, sum(Amt) AS c3\n" +
                "            FROM \n" +
                "               (\n" +
                "                  SELECT \n" +
                "                     datepart(year, CONVERT(DateTime, s_date)) AS y3, datepart(month, CONVERT(DateTime, s_date)) AS m3, Amt\n" +
                "                  FROM orders\n" +
                "               )  AS T1\n" +
                "            GROUP BY T1.y3, T1.m3\n" +
                "         )  AS o5 ON o4.y1 = o5.y3 - 1 AND o4.m1 = o5.m3\n" +
                "   )  AS o6;";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testSelect6() throws JSQLParserException {
        String sql = "select \n" +
                "\t* \n" +
                "from \n" +
                "\temployees \n" +
                "where \n" +
                "\tnot(department_id >= 90 and department_id <= 110) or salary > 15000;\n";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testSelect7() throws JSQLParserException {

        String sql = "select s.Name,C.Cname from student_course as sc " +
                "left join student as s on s.Sno=sc.Sno" +
                "left join course as c on c.Cno=sc.Cno on s.Sno=sc.Sno";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testSelect8() throws JSQLParserException {
        String sql = " SELECT\n" +
                "  DEP,\n" +
                "  JOB,\n" +
                "  SAL\n" +
                "FROM EMPL\n" +
                "WHERE JOB <> 'M'\n" +
                "GROUP BY DEP, JOB\n" +
                "HAVING AVG(SAL) > 28000\n" +
                "ORDER BY 3 DESC\n" +
                "limit 10\n" +
                "offset 10;";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testSelect9() throws JSQLParserException {
        String sql = "SELECT DISTINCT \n" +
                "   column1, column2\n" +
                "FROM table_name;";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testSelect10() throws JSQLParserException {
        String sql = "SELECT last_name, first_name \n" +
                "INTO persons_backup FROM persons \n" +
                "WHERE city='Beijing'";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testSelect11() throws JSQLParserException {
        String sql = "SELECT\n" +
                "  film_id,\n" +
                "  title,\n" +
                "  release_year\n" +
                "FROM film\n" +
                "ORDER BY film_id\n" +
                "FETCH FIRST 5 ROWS ONLY;\n";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testSelect12() throws JSQLParserException {
        String sql = "   select lastname, firstnme, empno, salary\n" +
                "     from employee\n" +
                "     order by salary desc\n" +
                "     optimize for 20 rows";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testSelect13() throws JSQLParserException {
        String sql = "SELECT * FROM t WHERE i = 2 FOR UPDATE WAIT 2";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testSelect14() throws JSQLParserException {
        String sql = "select skip 2 top 5 Node from alerts.status order by Node asc;";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testSelect15() throws JSQLParserException {
        String sql = "select first 5 Node from alerts.status order by Node asc;";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);

    }

    @Test
    public void testSelect16() throws JSQLParserException {
        String sql = "SELECT e.c3, e.c4, e.c5 FROM t1 e JOIN t2 d USING (id) WHERE c2 = 'SA_REP' AND c6 = 2500  ORDER BY e.c3 FOR UPDATE OF e ;";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testSelect17() throws JSQLParserException {
        String sql = "SELECT \n" +
                "   \n" +
                "      CASE \n" +
                "         WHEN (\n" +
                "            CASE \n" +
                "               WHEN (\n" +
                "                  CASE \n" +
                "                     WHEN (\n" +
                "                        CASE \n" +
                "                           WHEN (1) THEN 0\n" +
                "                        END) THEN 0\n" +
                "                  END) THEN 0\n" +
                "            END) THEN 0\n" +
                "      END\n" +
                "FROM a;";
        Statement parse = CCJSqlParserUtil.parse(sql);
        System.out.println();
    }

    private static void testSqlParse(Statement statement) {
        Parse parse = ActionFactory.selectParse(statement);
        JSONObject jsonObject = (JSONObject) parse.parse(statement);
        System.out.println();
    }
}
