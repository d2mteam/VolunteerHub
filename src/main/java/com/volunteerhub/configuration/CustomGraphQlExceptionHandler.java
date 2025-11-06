package com.volunteerhub.configuration;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class CustomGraphQlExceptionHandler extends DataFetcherExceptionResolverAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CustomGraphQlExceptionHandler.class);

    @Override
    protected GraphQLError resolveToSingleError(@NonNull Throwable ex, @NonNull DataFetchingEnvironment env) {
        logger.error(ex.getMessage(), ex);

        if (ex instanceof IllegalArgumentException) {
            return GraphqlErrorBuilder.newError(env)
                    .message("Validation failed for one or more fields.")
                    .errorType(ErrorType.BAD_REQUEST)
                    .build();
        }

        return GraphqlErrorBuilder.newError(env)
                .message("An unexpected error occurred. Please try again later.")
                .errorType(ErrorType.INTERNAL_ERROR)
                .build();
    }
}
