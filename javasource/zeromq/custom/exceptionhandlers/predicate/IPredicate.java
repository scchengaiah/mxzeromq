package zeromq.custom.exceptionhandlers.predicate;

import com.mendix.systemwideinterfaces.MendixRuntimeException;

import java.util.function.Predicate;

@FunctionalInterface
public interface IPredicate<T, E extends Exception>  {

    boolean test(T t) throws E;

    public static <T, E extends Exception>
    Predicate<T> handledPredicate(IPredicate<T, E> predicate) {
        return arg -> {
            try {
                return predicate.test(arg);
            } catch (Exception e) {
                throw new MendixRuntimeException(e);
            }
        };
    }

}
