package com.catyee.generator.extend;

import org.mybatis.dynamic.sql.insert.render.DefaultInsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.FieldAndValue;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.ValuePhraseVisitor;
import org.mybatis.dynamic.sql.render.RenderingStrategy;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mybatis.dynamic.sql.util.StringUtilities.spaceBefore;

public class InsertIgnoreRenderer<T> {

    private final InsertIgnoreModel<T> model;
    private final RenderingStrategy renderingStrategy;

    private InsertIgnoreRenderer(Builder<T> builder) {
        model = Objects.requireNonNull(builder.model);
        renderingStrategy = Objects.requireNonNull(builder.renderingStrategy);
    }

    public InsertStatementProvider<T> render() {
        ValuePhraseVisitor visitor = new ValuePhraseVisitor(renderingStrategy);

        List<Optional<FieldAndValue>> fieldsAndValues = model.mapColumnMappings(m -> m.accept(visitor))
                .collect(Collectors.toList());

        return DefaultInsertStatementProvider.withRecord(model.record())
                .withInsertStatement(calculateInsertStatement(fieldsAndValues))
                .build();
    }

    private String calculateInsertStatement(List<Optional<FieldAndValue>> fieldsAndValues) {
        return "insert ignore into" //$NON-NLS-1$
                + spaceBefore(model.table().tableNameAtRuntime())
                + spaceBefore(calculateColumnsPhrase(fieldsAndValues))
                + spaceBefore(calculateValuesPhrase(fieldsAndValues));
    }

    private String calculateColumnsPhrase(List<Optional<FieldAndValue>> fieldsAndValues) {
        return fieldsAndValues.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(FieldAndValue::fieldName)
                .collect(Collectors.joining(", ", "(", ")")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    private String calculateValuesPhrase(List<Optional<FieldAndValue>> fieldsAndValues) {
        return fieldsAndValues.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(FieldAndValue::valuePhrase)
                .collect(Collectors.joining(", ", "values (", ")")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public static <T> Builder<T> withInsertIgnoreModel(InsertIgnoreModel<T> model) {
        return new Builder<T>().withInsertIgnoreModel(model);
    }

    public static class Builder<T> {
        private InsertIgnoreModel<T> model;
        private RenderingStrategy renderingStrategy;

        public Builder<T> withInsertIgnoreModel(InsertIgnoreModel<T> model) {
            this.model = model;
            return this;
        }

        public Builder<T> withRenderingStrategy(RenderingStrategy renderingStrategy) {
            this.renderingStrategy = renderingStrategy;
            return this;
        }

        public InsertIgnoreRenderer<T> build() {
            return new InsertIgnoreRenderer<>(this);
        }
    }
}