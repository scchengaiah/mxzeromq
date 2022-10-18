package zeromq.custom.exceptionhandlers.consumer;

import java.util.function.Consumer;

import com.mendix.systemwideinterfaces.MendixRuntimeException;

@FunctionalInterface
public interface IConsumer<T, E extends Exception> {
	void accept(T t) throws E;
	
	public static <T, E extends Exception>
	Consumer<T> handledConsumer(IConsumer<T, E> consumer) {
		return arg -> {
			try {
				consumer.accept(arg);
			} catch (Exception e) {
				throw new MendixRuntimeException(e);
			}
		};
	}
}
