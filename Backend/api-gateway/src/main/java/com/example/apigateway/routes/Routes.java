package com.example.apigateway.routes;

import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.https;

@Configuration // Marks this class as a Spring configuration class
public class Routes {

    // Routes all requests starting with /api/user/** to the user service on port 9000
    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return GatewayRouterFunctions.route("User-Service")
                .route(RequestPredicates.path("/api/user/**"), http())
                .before(uri("https://user-service-production-2e52.up.railway.app")) // Forward to user-service
                .build();
    }

    // Routes Swagger documentation requests for user service
    @Bean
    public RouterFunction<ServerResponse> userServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("User-Service_Swagger")
                .route(RequestPredicates.path("/aggregate/user/v3/api-docs"), HandlerFunctions.http("https://user-service-production-2e52.up.railway.app"))
                .filter(setPath("/v3/api-docs")) // Rewrite path before forwarding
                .build();
    }

    // Routes /api/place/** to the place service on port 9001
    @Bean
    public RouterFunction<ServerResponse> placeServiceRoute() {
        return GatewayRouterFunctions.route("Place-Service")
                .route(RequestPredicates.path("/api/place/**"), http())
                .before(uri("place-service.railway.internal"))
                .build();
    }

    // Routes Swagger documentation for place service
    @Bean
    public RouterFunction<ServerResponse> placeServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("Place-Service_Swagger")
                .route(RequestPredicates.path("/aggregate/place/v3/api-docs"), HandlerFunctions.http("place-service.railway.internal"))
                .filter(setPath("/v3/api-docs"))
                .build();
    }

    // Routes /api/search/** to the search service on port 9002
    @Bean
    public RouterFunction<ServerResponse> searchPlaceServiceRoute() {
        return GatewayRouterFunctions.route("Search-Service")
                .route(RequestPredicates.path("/api/search/**"), http())
                .before(uri("search-service.railway.internal"))
                .build();
    }

    // Routes Swagger documentation for search service
    @Bean
    public RouterFunction<ServerResponse> searchPlaceServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("Search-Service_Swagger")
                .route(RequestPredicates.path("/aggregate/search/v3/api-docs"), HandlerFunctions.http("search-service.railway.internal"))
                .filter(setPath("/v3/api-docs"))
                .build();
    }

    // Routes /api/saved/** to the saved place service on port 9003
    @Bean
    public RouterFunction<ServerResponse> savedPlaceServiceRoute() {
        return GatewayRouterFunctions.route("SavedPlace-Service")
                .route(RequestPredicates.path("/api/saved/**"), http())
                .before(uri("SavedPlace-Service.railway.internal"))
                .build();
    }

    // Routes Swagger documentation for saved place service
    @Bean
    public RouterFunction<ServerResponse> savedPlaceServiceSwaggerRoute() {
        return GatewayRouterFunctions.route("SavedPlace-Service_Swagger")
                .route(RequestPredicates.path("/aggregate/saved/v3/api-docs"), HandlerFunctions.http("SavedPlace-Service.railway.internal"))
                .filter(setPath("/v3/api-docs"))
                .build();
    }
}
