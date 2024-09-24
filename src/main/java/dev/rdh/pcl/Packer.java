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

	/**
	 * Concatenates all the files in the specified stream into the specified output.
	 *
	 * The output file is structured as such:
	 * <ul>
	 *     <li>for each item:
	 *     <ol>
	 *         <li>length of the name of the file (4 bytes)</li>
	 *         <li>length of the contents of the file (4 bytes)</li>
	 *         <li>name of the file</li>
	 *         <li>contents of the file</li>
	 *     </ol>
	 *     </li>
	 * </ul>
	 *
	 * @param root the root to relativize the paths to
	 * @param output the file to write the packed bundle to
	 * @param paths the paths that contain files to add to the bundle. May contain directories, they will be skipped.
	 */
	public static void pack(Path root, Path output, Stream<Path> paths) {
		try(DataOutputStream out = new DataOutputStream(Files.newOutputStream(output))) {
			paths.sorted().forEach(path -> {
				if(Files.isDirectory(path)) return;

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
