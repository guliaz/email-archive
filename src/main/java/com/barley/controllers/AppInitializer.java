/*
package com.barley.controllers;

public class AppInitializer extends AbstractDispatcherServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppInitializer.class);

    @Override
    protected WebApplicationContext createRootApplicationContext() {
        return null;
    }

    @Override
    protected WebApplicationContext createServletApplicationContext() {
        LOGGER.info("STARTING EVENT REST PROXY.....");
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.setConfigLocations("com.dish.des.proxy");
        return ctx;
    }

    @Override
    protected String[] getServletMappings() {
        LOGGER.info("Setting servlet mapping to '/'");
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{new HiddenHttpMethodFilter(), new CharacterEncodingFilter(), new LoggingFilter()};
    }
}


@Configuration
@EnableWebMvc
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    InetAddress serverLocalAddress() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        addr.getHostName();
        addr.getHostAddress();
        return addr;
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver jspResolver = new InternalResourceViewResolver();
        jspResolver.setPrefix("/WEB-INF/pages/");
        jspResolver.setSuffix(".jsp");
        return jspResolver;
    }


    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
       */
/* registry.addResourceHandler("/css*/
/**").addResourceLocations("/WEB-INF/css/");
        registry.addResourceHandler("/img*/
/**").addResourceLocations("/WEB-INF/img/");
        registry.addResourceHandler("/js*/
/**").addResourceLocations("/WEB-INF/js/");
        registry.addResourceHandler("/dist*/
/**").addResourceLocations("/WEB-INF/dist/");
        registry.addResourceHandler("/less*/
/**").addResourceLocations("/WEB-INF/less/");
        registry.addResourceHandler("/bower_components*/
/**").addResourceLocations("/WEB-INF/bower_components/");*//*

        registry.addResourceHandler("/pages*/
/**").addResourceLocations("/WEB-INF/pages/");
        registry.addResourceHandler("/css*/
/**").addResourceLocations("/WEB-INF/pages/css/");
        registry.addResourceHandler("/img*/
/**").addResourceLocations("/WEB-INF/pages/img/");
        registry.addResourceHandler("/js*/
/**").addResourceLocations("/WEB-INF/pages/js/");
        registry.addResourceHandler("/fonts*/
/**").addResourceLocations("/WEB-INF/pages/fonts/");
    }
}

*/
