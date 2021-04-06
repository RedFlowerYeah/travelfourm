package com.travelfourm.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 记录用户的操作日志（访问的Service层）
 * @author 34612
 * @Component 表明这个类为组件类，并注入到bean中
 * @Aspect 切面声明，此处切入点在Service层中，用于记录用户浏览日志*/

@Component
@Aspect
public class ServiceLogAspect {

    /**
     * Logger是slf4j测试包里面的一个方法，用于打印这个包中的相应的信息*/
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * @Pointcut（"execution()"）表示切入到service包中的所有类的所有方法*/
    @Pointcut("execution(* com.travelfourm.service.*.*(..))")
    public void pointcut(){

    }

    /**
     * @Before 表示在切入点之前执行
     * JoinPoint 表示封装目标方法的详细信息*/
    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
        //用户[ip地址]，在[xxx时候]，访问了[com.travelfourm.service中的xxx方法]
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null){
            return;
        }

        /**
         * HttpServletRequest对象代表客户端的请求，
         * 当客户端通过HTTP协议访问服务器时，HTTP请求头中的所有信息都封装在HttpServletRequest中
         * .getRemoteHost()为获取主机名
         * joinPoint.getSignature() 获取包名
         * getDeclaringTypeName() 获取相应的类名
         * getName() 获取方法名
         * 将其组装成一个string在logger中输出
         */
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s]",ip,now,target));
    }
}
