package zeromq.custom.exceptionhandlers.intconsumer;

import java.util.function.IntConsumer;

import com.mendix.systemwideinterfaces.MendixRuntimeException;



@FunctionalInterface
public interface IIntConsumer<E extends Exception> {
	void accept(int t) throws E;

	public static <E extends Exception>
	IntConsumer handledIntConsumer(IIntConsumer<E> consumer) {
		return arg -> {
			try {
				consumer.accept(arg);
			} catch (Exception e) {
				throw new MendixRuntimeException(e);
			}
		};
	}
}

