package com.catyee.generator.extend;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class InsertIgnoreDSL<T> implements Buildable<InsertIgnoreModel<T>> {

    private final T record;
    private final SqlTable table;
    private final List<AbstractColumnMapping> columnMappings = new ArrayList<>();

    private InsertIgnoreDSL(T record, SqlTable table) {
        this.record = record;
        this.table = table;
    }

    public <F> ColumnMappingFinisher<F> map(SqlColumn<F> column) {
        return new ColumnMappingFinisher<>(column);
    }

    @Override
    public InsertIgnoreModel<T> build() {
        return InsertIgnoreModel.withRecord(record)
                .withTable(table)
                .withColumnMappings(columnMappings)
                .build();
    }

    public static <T> IntoGatherer<T> insert(T record) {
        return new IntoGatherer<>(record);
    }

    public static class IntoGatherer<T> {
        private final T record;

        private IntoGatherer(T record) {
            this.record = record;
        }

        public InsertIgnoreDSL<T> into(SqlTable table) {
            return new InsertIgnoreDSL<>(record, table);
        }
    }

    public class ColumnMappingFinisher<F> {
        private final SqlColumn<F> column;

        public ColumnMappingFinisher(SqlColumn<F> column) {
            this.column = column;
        }

        public InsertIgnoreDSL<T> toProperty(String property) {
            columnMappings.add(PropertyMapping.of(column, property));
            return InsertIgnoreDSL.this;
        }

        public InsertIgnoreDSL<T> toPropertyWhenPresent(String property, Supplier<?> valueSupplier) {
            columnMappings.add(PropertyWhenPresentMapping.of(column, property, valueSupplier));
            return InsertIgnoreDSL.this;
        }

        public InsertIgnoreDSL<T> toNull() {
            columnMappings.add(NullMapping.of(column));
            return InsertIgnoreDSL.this;
        }

        public InsertIgnoreDSL<T> toConstant(String constant) {
            columnMappings.add(ConstantMapping.of(column, constant));
            return InsertIgnoreDSL.this;
        }

        public InsertIgnoreDSL<T> toStringConstant(String constant) {
            columnMappings.add(StringConstantMapping.of(column, constant));
            return InsertIgnoreDSL.this;
        }
    }
}