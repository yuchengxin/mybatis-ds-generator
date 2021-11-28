package com.catyee.generator.extend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.util.AbstractColumnMapping;
import org.mybatis.dynamic.sql.util.Buildable;
import org.mybatis.dynamic.sql.util.ConstantMapping;
import org.mybatis.dynamic.sql.util.NullMapping;
import org.mybatis.dynamic.sql.util.PropertyMapping;
import org.mybatis.dynamic.sql.util.StringConstantMapping;

public class MultiRowIgnoreInsertDSL<T> implements Buildable<MultiRowIgnoreInsertModel<T>> {

    private final Collection<T> records;
    private final SqlTable table;
    private final List<AbstractColumnMapping> columnMappings = new ArrayList<>();

    private MultiRowIgnoreInsertDSL(Collection<T> records, SqlTable table) {
        this.records = records;
        this.table = table;
    }

    public <F> ColumnMappingFinisher<F> map(SqlColumn<F> column) {
        return new ColumnMappingFinisher<>(column);
    }

    @Override
    public MultiRowIgnoreInsertModel<T> build() {
        return MultiRowIgnoreInsertModel.withRecords(records)
                .withTable(table)
                .withColumnMappings(columnMappings)
                .build();
    }

    @SafeVarargs
    public static <T> IntoGatherer<T> insert(T...records) {
        return MultiRowIgnoreInsertDSL.insert(Arrays.asList(records));
    }

    public static <T> IntoGatherer<T> insert(Collection<T> records) {
        return new IntoGatherer<>(records);
    }

    public static class IntoGatherer<T> {
        private final Collection<T> records;

        private IntoGatherer(Collection<T> records) {
            this.records = records;
        }

        public MultiRowIgnoreInsertDSL<T> into(SqlTable table) {
            return new MultiRowIgnoreInsertDSL<>(records, table);
        }
    }

    public class ColumnMappingFinisher<F> {
        private final SqlColumn<F> column;

        public ColumnMappingFinisher(SqlColumn<F> column) {
            this.column = column;
        }

        public MultiRowIgnoreInsertDSL<T> toProperty(String property) {
            columnMappings.add(PropertyMapping.of(column, property));
            return MultiRowIgnoreInsertDSL.this;
        }

        public MultiRowIgnoreInsertDSL<T> toNull() {
            columnMappings.add(NullMapping.of(column));
            return MultiRowIgnoreInsertDSL.this;
        }

        public MultiRowIgnoreInsertDSL<T> toConstant(String constant) {
            columnMappings.add(ConstantMapping.of(column, constant));
            return MultiRowIgnoreInsertDSL.this;
        }

        public MultiRowIgnoreInsertDSL<T> toStringConstant(String constant) {
            columnMappings.add(StringConstantMapping.of(column, constant));
            return MultiRowIgnoreInsertDSL.this;
        }
    }
}