package ua.tonkoshkur.cloudstorage.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class SynchronizedOnUserAspect {

    private final Map<Long, Object> userLocks = new ConcurrentHashMap<>();
    private final ThreadLocal<Long> currentThreadUserId = new ThreadLocal<>();

    @Around("@annotation(ua.tonkoshkur.cloudstorage.common.SynchronizedOnUser) && args(userId,..)")
    public Object lock(ProceedingJoinPoint joinPoint, Long userId) throws Throwable {
        if (userId.equals(currentThreadUserId.get())) {
            return joinPoint.proceed();
        }

        currentThreadUserId.set(userId);
        synchronized (getLock(userId)) {
            Object proceed = joinPoint.proceed();
            currentThreadUserId.remove();
            return proceed;
        }
    }

    private Object getLock(long userId) {
        return userLocks.computeIfAbsent(userId, id -> new Object());
    }
}
