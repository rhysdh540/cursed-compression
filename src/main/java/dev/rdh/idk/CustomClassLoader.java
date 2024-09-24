package dev.rdh.idk;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomClassLoader extends ClassLoader {
	private final Map<String, byte[]> index = new ConcurrentHashMap<>();

	static {
		registerAsParallelCapable();
	}

	public CustomClassLoader(ClassLoader parent, String bundleName) {
		super(parent);

		try (InputStream is = parent.getResourceAsStream(bundleName)) {
			if (is == null) {
				throw new IllegalArgumentException("Bundle not found: " + bundleName);
			}

			splitClassFiles(new DataInputStream(is));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load bundle: " + bundleName, e);
		}
	}

	@Override
	protected Class<?> findClass(String name) {
		String path = name.replace('.', '/') + ".class";
		byte[] bytes = index.get(path);
		if (bytes == null) {
			throw new ClassNotFoundException(name);
		}

		return defineClass(name, bytes, 0, bytes.length);
	}

	@Override
	protected URL findResource(String name) {
		byte[] bytes = index.get(name);
		if (bytes == null) {
			return null;
		}

		return new URL(null, "bytes", new ByteArrayURLStreamHandler(bytes));
	}

	private void splitClassFiles(DataInputStream input) {
		while(input.available() > 0) {
			byte[] nameBytes = new byte[input.readInt()];
			byte[] data = new byte[input.readInt()];

			input.readFully(nameBytes);

			input.readFully(data);

			index.put(new String(nameBytes), data);
		}
	}
}

