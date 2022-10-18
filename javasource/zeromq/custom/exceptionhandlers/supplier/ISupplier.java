package zeromq.custom.exceptionhandlers.supplier;

import java.util.function.Supplier;

import com.mendix.systemwideinterfaces.MendixRuntimeException;

@FunctionalInterface
public interface ISupplier <T, E extends Exception> {
	T get() throws E;
	
	public static <T, U, E extends Exception>
	Supplier<T> handledSupplier(ISupplier<T, E> consumer) {
		return () -> {
			try {
				return consumer.get();
			} catch (Exception e) {
				throw new MendixRuntimeException(e);
			}
			
		};
	}
}
