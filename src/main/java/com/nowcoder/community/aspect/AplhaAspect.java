package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AplhaAspect {
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut() {}

    @Before("pointcut()")
    public void before() {
        System.out.println("before");
    }
    @After("pointcut()")
    public void after() {
        System.out.println("after");
    }
    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("after returning");
    }
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("before proceed");
        Object object = joinPoint.proceed();
        System.out.println("after proceed");
        return object;
    }
}
