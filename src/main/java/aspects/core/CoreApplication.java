package aspects.core;

import aspects.JavaComponent;
import aspects.annotation.*;
import com.sun.xml.internal.rngom.parse.host.Base;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("ReflectionForUnavailableAnnotation")
public class CoreApplication {
    private Logger logger = Logger.getLogger(CoreApplication.class.getName());

    public static void run(String[] args) {
        CoreApplication app = new CoreApplication();
        app.start();
    }

    private CoreApplication() {
        logger.setLevel(Level.OFF);
    }

    private void start() {
        Class<JavaComponent> cls = JavaComponent.class;

        // Component annotation
        if (cls.isAnnotationPresent(Component.class)) {
            Annotation clsAnnotation = cls.getAnnotation(Component.class);
            Component clsComponent = (Component) clsAnnotation;

            logger.log(Level.INFO, clsComponent.name());

            Comparator<Method> comparator = (o1, o2) -> {
                if (o1.isAnnotationPresent(BaseMethod.class) && o2.isAnnotationPresent(BaseMethod.class)) {
                    return o1.getAnnotation(BaseMethod.class).priority() - o2.getAnnotation(BaseMethod.class).priority();
                } else {
                    return Integer.MAX_VALUE;
                }
            };

            PriorityQueue<Method> beforeList = new PriorityQueue<>(comparator);
            PriorityQueue<Method> algorithmList = new PriorityQueue<>(comparator);
            PriorityQueue<Method> afterList = new PriorityQueue<>(comparator);

            // Scan methods
            for (Method method : cls.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Before.class)) {
                    Annotation annotation = method.getAnnotation(Before.class);
                    Before before = (Before) annotation;

                    if (before.enabled()) {
                        beforeList.add(method);
                    }
                }

                if (method.isAnnotationPresent(Algorithm.class)) {
                    Annotation annotation = method.getAnnotation(Algorithm.class);
                    Algorithm algorithm = (Algorithm) annotation;

                    if (algorithm.enabled()) {
                        algorithmList.add(method);
                    }
                }

                if (method.isAnnotationPresent(After.class)) {
                    Annotation annotation = method.getAnnotation(After.class);
                    After after = (After) annotation;

                    if (after.enabled()) {
                        afterList.add(method);
                    }
                }
            }

            methodInvoker(beforeList, cls);
            methodInvoker(algorithmList, cls);
            methodInvoker(afterList, cls);
        }
    }

    private void methodInvoker(PriorityQueue<Method> methods, Class cls) {
        for (Method method : methods) {
            try {
                method.invoke(cls.newInstance());

                logger.log(Level.INFO, "BaseMethod " + method.getName() + " invoked.");
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                logger.log(Level.INFO, "BaseMethod " + method.getName() + " failed to invoke.");
            }
        }
    }
}
