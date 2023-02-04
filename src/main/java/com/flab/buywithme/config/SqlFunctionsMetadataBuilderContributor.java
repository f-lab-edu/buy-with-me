package com.flab.buywithme.config;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.DoubleType;

public class SqlFunctionsMetadataBuilderContributor implements MetadataBuilderContributor {

    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        metadataBuilder.applySqlFunction("match_against",
                new SQLFunctionTemplate(DoubleType.INSTANCE,
                        "match (?1, ?2) against (?3 in boolean mode)"));
    }
}
