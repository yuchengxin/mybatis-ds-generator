package com.catyee.generator.utils;

import com.catyee.generator.entity.JoinDetail;
import com.catyee.generator.extend.InsertIgnoreDSL;
import com.catyee.generator.extend.MultiRowIgnoreInsertDSL;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.render.DefaultSelectStatementProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;

import static org.mybatis.dynamic.sql.SqlBuilder.equalTo;

/**
 * author: catyee
 */
public class MyBatis3CustomUtils {

    /**
     * 构造left join查询语句并获得单条查询结果
     *
     * @param mapper
     * @param joinSelectList
     * @param leftTable
     * @param joinDetails
     * @param completer
     * @param <R>
     * @return
     */
    public static <R> R leftJoinSelectOne(Function<SelectStatementProvider, R> mapper, BasicColumn[] joinSelectList,
                                          SqlTable leftTable, List<JoinDetail> joinDetails, SelectDSLCompleter completer) {
        return mapper.apply(leftJoinSelect(joinSelectList, leftTable, joinDetails, completer));
    }

    /**
     * 构造left join查询语句并获得查询结果列表
     *
     * @param mapper
     * @param joinSelectList
     * @param leftTable
     * @param joinDetails
     * @param completer
     * @param <R>
     * @return
     */
    public static <R> List<R> leftJoinSelectList(Function<SelectStatementProvider, List<R>> mapper, BasicColumn[] joinSelectList,
                                                 SqlTable leftTable, List<JoinDetail> joinDetails, SelectDSLCompleter completer) {
        return mapper.apply(leftJoinSelect(joinSelectList, leftTable, joinDetails, completer));
    }

    /**
     * 构造group by查询语句并获得查询结果
     *
     * @param mapper
     * @param selectList
     * @param table
     * @param groupByColumns
     * @param completer
     * @param <R>
     * @return
     */
    public static <R> List<R> groupBySelectList(Function<SelectStatementProvider, List<R>> mapper, BasicColumn[] selectList,
                                                SqlTable table, BasicColumn[] groupByColumns, SortSpecification[] orderByColumns,
                                                SelectDSLCompleter completer) {
        return mapper.apply(groupBySelect(selectList, table, groupByColumns, orderByColumns, completer));
    }

    /**
     * 计算select sql语句执行结果的数量. e.g. select count(*) from (select id, user from table) temp_alias_table
     *
     * @param mapper
     * @param selectStatementProvider select语句供应商
     * @return
     */
    public static long countFromSelectQuery(ToLongFunction<SelectStatementProvider> mapper, SelectStatementProvider selectStatementProvider) {
        String sql = String.format("select count(*) from (%s) temp_alias_table", selectStatementProvider.getSelectStatement());
        DefaultSelectStatementProvider provider = DefaultSelectStatementProvider.withSelectStatement(sql).withParameters(selectStatementProvider.getParameters()).build();
        return mapper.applyAsLong(provider);
    }

    /**
     * 构造limit offset语句, offset按照语法从0开始计数, 如果limit为空或小于0则查询全部
     *
     * @param completer
     * @param limit null or >= 0
     * @param offset null or >= 0
     * @return
     */
    public static QueryExpressionDSL<SelectModel> buildLimitOffset(QueryExpressionDSL<SelectModel> completer, Long limit, Long offset) {
        if (limit != null && limit >= 0) {
            completer.limit(limit);
            if (offset != null && offset >= 0) {
                completer.offset(offset);
            }
        }
        return completer;
    }

    /**
     * 构造分页语句, page从1开始计数, 如果size为空或小于0则查询全部
     * @param completer
     * @param page
     * @param size
     * @return
     */
    public static QueryExpressionDSL<SelectModel> buildPagination(QueryExpressionDSL<SelectModel> completer, Integer page, Integer size) {
        if (size != null && size >= 0) {
            completer.limit(size);
            if (page != null && page - 1 >= 0) {
                int offset = (page - 1) * size;
                completer.offset(offset);
            }
        }
        return completer;
    }

    /**
     * mysql的insert ignore into语句, 使用时先确认数据库是否支持该语法
     *
     * @param mapper
     * @param record
     * @param table
     * @param completer
     * @param <R>
     * @return
     */
    public static <R> int ignoreInsert(ToIntFunction<InsertStatementProvider<R>> mapper, R record,
                                 SqlTable table, UnaryOperator<InsertIgnoreDSL<R>> completer) {
        return mapper.applyAsInt(ignoreInsert(record, table, completer));
    }

    /**
     * mysql的insert ignore into语句, 使用时先确认数据库是否支持该语法
     * @param record
     * @param table
     * @param completer
     * @param <R>
     * @return
     */
    public static <R> InsertStatementProvider<R> ignoreInsert(R record, SqlTable table,
                                                        UnaryOperator<InsertIgnoreDSL<R>> completer) {
        return completer.apply(InsertIgnoreDSL.insert(record).into(table))
                .build()
                .render(RenderingStrategies.MYBATIS3);
    }

    /**
     * mysql的insert ignore into语句, 使用时先确认数据库是否支持该语法, 批量插入
     *
     * @param mapper
     * @param records
     * @param table
     * @param completer
     * @param <R>
     * @return
     */
    public static <R> int ignoreInsertMultiple(ToIntFunction<MultiRowInsertStatementProvider<R>> mapper,
                                         Collection<R> records, SqlTable table, UnaryOperator<MultiRowIgnoreInsertDSL<R>> completer) {
        return mapper.applyAsInt(ignoreInsertMultiple(records, table, completer));
    }

    /**
     * mysql的insert ignore into语句, 使用时先确认数据库是否支持该语法, 批量插入
     *
     * @param records
     * @param table
     * @param completer
     * @param <R>
     * @return
     */
    public static <R> MultiRowInsertStatementProvider<R> ignoreInsertMultiple(Collection<R> records, SqlTable table,
                                                                        UnaryOperator<MultiRowIgnoreInsertDSL<R>> completer) {
        return completer.apply(MultiRowIgnoreInsertDSL.insert(records).into(table))
                .build()
                .render(RenderingStrategies.MYBATIS3);
    }

    public static SelectStatementProvider groupBySelect(BasicColumn[] selectList, SqlTable table, BasicColumn[] groupByColumns,
                                                        SortSpecification[] orderByColumns, SelectDSLCompleter completer) {
        QueryExpressionDSL<SelectModel> start = SqlBuilder.select(selectList).from(table);
        start.groupBy(groupByColumns);
        if (orderByColumns != null && orderByColumns.length != 0) {
            start.orderBy(orderByColumns);
        }
        return MyBatis3Utils.select(start, completer);
    }

    public static SelectStatementProvider leftJoinSelect(BasicColumn[] joinSelectList, SqlTable leftTable,
                                                         List<JoinDetail> joinDetails, SelectDSLCompleter completer) {
        QueryExpressionDSL<SelectModel> start = SqlBuilder.select(joinSelectList).from(leftTable);
        joinDetails.forEach(detail -> start.leftJoin(detail.getRightJoinTable())
                .on(detail.getLeftTableJoinColumn(), equalTo(detail.getRightTableJoinColumn())));
        return MyBatis3Utils.select(start, completer);
    }
}
