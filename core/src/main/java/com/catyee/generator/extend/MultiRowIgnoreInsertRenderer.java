package com.catyee.generator.extend;

import static org.mybatis.dynamic.sql.util.StringUtilities.spaceBefore;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.mybatis.dynamic.sql.insert.render.DefaultMultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.FieldAndValue;
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.MultiRowValuePhraseVisitor;
import org.mybatis.dynamic.sql.render.RenderingStrategy;

public class MultiRowIgnoreInsertRenderer<T> {

    private final MultiRowIgnoreInsertModel<T> model;
    private final RenderingStrategy renderingStrategy;

    private MultiRowIgnoreInsertRenderer(Builder<T> builder) {
        model = Objects.requireNonNull(builder.model);
        renderingStrategy = Objects.requireNonNull(builder.renderingStrategy);
    }

    public MultiRowInsertStatementProvider<T> render() {
        // the prefix is a generic format that will be resolved below with String.format(...)
        MultiRowValuePhraseVisitor visitor =
                new MultiRowValuePhraseVisitor(renderingStrategy, "records[%s]"); //$NON-NLS-1$
        List<FieldAndValue> fieldsAndValues = model
                .mapColumnMappings(m -> m.accept(visitor))
                .collect(Collectors.toList());

        return new DefaultMultiRowInsertStatementProvider.Builder<T>().withRecords(model.records())
                .withInsertStatement(calculateInsertStatement(fieldsAndValues))
                .build();
    }

    private String calculateInsertStatement(List<FieldAndValue> fieldsAndValues) {
        return "insert ignore into" //$NON-NLS-1$
                + spaceBefore(model.table().tableNameAtRuntime())
                + spaceBefore(calculateColumnsPhrase(fieldsAndValues))
                + spaceBefore(calculateMultiRowInsertValuesPhrase(fieldsAndValues, model.recordCount()));
    }

    private String calculateColumnsPhrase(List<FieldAndValue> fieldsAndValues) {
        return fieldsAndValues.stream()
                .map(FieldAndValue::fieldName)
                .collect(Collectors.joining(", ", "(", ")")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    private String calculateMultiRowInsertValuesPhrase(List<FieldAndValue> fieldsAndValues, int rowCount) {
        return IntStream.range(0, rowCount)
                .mapToObj(i -> toSingleRowOfValues(fieldsAndValues, i))
                .collect(Collectors.joining(", ", "values ", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    private String toSingleRowOfValues(List<FieldAndValue> fieldsAndValues, int row) {
        return fieldsAndValues.stream()
                .map(FieldAndValue::valuePhrase)
                .map(s -> String.format(s, row))
                .collect(Collectors.joining(", ", "(", ")")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public static <T> Builder<T> withMultiRowIgnoreInsertModel(MultiRowIgnoreInsertModel<T> model) {
        return new Builder<T>().withMultiRowIgnoreInsertModel(model);
    }

    public static class Builder<T> {
        private MultiRowIgnoreInsertModel<T> model;
        private RenderingStrategy renderingStrategy;

        public Builder<T> withMultiRowIgnoreInsertModel(MultiRowIgnoreInsertModel<T> model) {
            this.model = model;
            return this;
        }

        public Builder<T> withRenderingStrategy(RenderingStrategy renderingStrategy) {
            this.renderingStrategy = renderingStrategy;
            return this;
        }

        public MultiRowIgnoreInsertRenderer<T> build() {
            return new MultiRowIgnoreInsertRenderer<>(this);
        }
    }
}