package com.catyee.generator.extend;

import java.util.Collection;

import org.mybatis.dynamic.sql.insert.AbstractMultiRowInsertModel;
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategy;

public class MultiRowIgnoreInsertModel<T> extends AbstractMultiRowInsertModel<T> {

    private MultiRowIgnoreInsertModel(Builder<T> builder) {
        super(builder);
    }

    public MultiRowInsertStatementProvider<T> render(RenderingStrategy renderingStrategy) {
        return MultiRowIgnoreInsertRenderer.withMultiRowIgnoreInsertModel(this)
                .withRenderingStrategy(renderingStrategy)
                .build()
                .render();
    }

    public static <T> Builder<T> withRecords(Collection<T> records) {
        return new Builder<T>().withRecords(records);
    }

    public static class Builder<T> extends AbstractBuilder<T, Builder<T>> {
        @Override
        protected Builder<T> getThis() {
            return this;
        }

        public MultiRowIgnoreInsertModel<T> build() {
            return new MultiRowIgnoreInsertModel<>(this);
        }
    }
}