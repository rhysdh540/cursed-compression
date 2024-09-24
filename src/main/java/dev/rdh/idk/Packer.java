package dev.rdh.idk;

import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Packer {
	public static void main(String[] args) {
		Path output = Paths.get(args[0]);
		Path input = Paths.get(args[1]);
		Path root = input.getParent();
		Path[] inputs = Stream.of(args).skip(2).map(Paths::get).toArray(Path[]::new);
		pack(root, output, inputs);
	}

	public static void pack(Path root, Path output, Path... inputs) {
		try(DataOutputStream out = new DataOutputStream(Files.newOutputStream(output))) {
			Stream.of(inputs).skip(1).sorted().forEach(path -> {
				byte[] name = root.relativize(path).toString().getBytes(StandardCharsets.UTF_8);
				byte[] data = Files.readAllBytes(path);

				out.writeInt(name.length);
				out.writeInt(data.length);
				out.write(name);
				out.write(data);
			});
		}
	}
}
