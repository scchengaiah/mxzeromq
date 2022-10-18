package zeromq.custom.exceptionhandlers.biconsumer;

import java.util.function.BiConsumer;

import com.mendix.systemwideinterfaces.MendixRuntimeException;

@FunctionalInterface
public interface IBiConsumer<T, U, E extends Exception> {
	void accept(T t, U u) throws E;
	
	public static <T, U, E extends Exception>
	BiConsumer<T, U> handledBiConsumer(IBiConsumer<T, U, E> consumer) {
		return (arg1, arg2) -> {
			try {
				consumer.accept(arg1, arg2);
			} catch (Exception e) {
				throw new MendixRuntimeException(e);
			}
		};
	}
}
