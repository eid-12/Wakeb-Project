@Bean
SecurityFilterChain filter(HttpSecurity http) throws Exception {
    return http
            // 1. الحل الجذري: تعطيل CSRF بالكامل لكل مسارات الخدمة
            // في هندسة الـ Microservices، الـ Gateway هو من يتولى الحماية والـ APIs عادة تكون Stateless
            .csrf(csrf -> csrf.disable()) 

            // تمكين إعدادات CORS الافتراضية
            .cors(withDefaults())

            // تعريف قواعد التصريح
            .authorizeHttpRequests(auth -> auth
                    // السماح بطلبات OPTIONS الضرورية لـ CORS
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // السماح بالوصول لجميع مسارات الخدمة بما فيها الحذف والرفع والـ Swagger
                    .requestMatchers(
                            "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
                            "/api/auth/**", "/api/place/**", "/error", "/app/uploads/**"
                    ).permitAll()

                    // تأمين أي طلبات أخرى
                    .anyRequest().authenticated()
            )
            .build();
}
