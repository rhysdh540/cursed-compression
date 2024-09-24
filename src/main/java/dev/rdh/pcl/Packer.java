package dev.rdh.pcl;

import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Packer {
	public static void main(String[] args) {
		Path output = Paths.get(args[0]);
		Path root = Paths.get(args[1]);

		pack(root, output, Stream.of(args).skip(2).map(Paths::get));
	}

	public static void pack(Path root, Path output, Stream<Path> paths) {
		try(DataOutputStream out = new DataOutputStream(Files.newOutputStream(output))) {
			paths.sorted().forEach(path -> {
				System.out.println("Packing: " + root.relativize(path));
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
