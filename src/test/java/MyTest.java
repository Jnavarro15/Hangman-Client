import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.Serializable;
import java.util.function.Consumer;

class MyTest {

	@Test
	@DisplayName("Test constructor with valid inputs")
	public void testConstructorWithValidInputs() {
		Consumer<Serializable> callback = new Consumer<Serializable>() {
			@Override
			public void accept(Serializable message) {
				System.out.println("Received message: " + message);
			}
		};
		int port = 12345;
		Client client = new Client(callback, port);
		assertNotNull(client);
	}

}
