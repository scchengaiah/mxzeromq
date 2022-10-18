package zeromq.custom.exceptionhandlers.function;

import java.util.function.Function;

import com.mendix.systemwideinterfaces.MendixRuntimeException;

@FunctionalInterface
public interface IFunction<T, R, E extends Exception> {

    R apply(T t) throws E;
    
    public static <T, R, E extends Exception>
	Function<T, R> handledFunction(IFunction<T, R, E> fe) {
		return arg -> {
			try {
				return fe.apply(arg);
			} catch (Exception e) {
				throw new MendixRuntimeException(e);
			}
		};
	}
}
