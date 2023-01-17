package messenger.messenger.business.aop;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
public class BusinessCommonPointcuts {

    @Pointcut("execution(* messenger.messenger.business..*.*(..))")
    public void loginMemberLogTrace() {};

}
