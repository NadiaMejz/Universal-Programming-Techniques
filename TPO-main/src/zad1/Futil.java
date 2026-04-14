package zad1;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;

public class Futil {
    public static void processDir(String dirName, String resultFileName) {
        Path startPath = Paths.get(dirName);
        Path resultPath = Paths.get(resultFileName);

        try {
            Files.deleteIfExists(resultPath);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileChannel outputChannel = FileChannel.open(resultPath,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            Files.walkFileTree(startPath, new FileVisitor<Path>()
            {
                Charset inputCharset = Charset.forName("Cp1250");
                Charset outputCharset = StandardCharsets.UTF_8;
                ByteBuffer buffer = ByteBuffer.allocate(4096);

                @Override
                public FileVisitResult preVisitDirectory (Path dir, BasicFileAttributes attrs){
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile (Path file, BasicFileAttributes attrs){
                    if (!Files.isRegularFile(file) || !file.toString().endsWith(".txt")) {
                        return FileVisitResult.CONTINUE;
                    }


                    try (FileChannel inputChannel = FileChannel.open(file, StandardOpenOption.READ)) { //tylko do odczytu
                        buffer.clear();
                        while (inputChannel.read(buffer) > 0) {
                            buffer.flip();
                            String content = inputCharset.decode(buffer).toString();
                            ByteBuffer outputBuffer = outputCharset.encode(content);
                            outputChannel.write(outputBuffer);
                            buffer.clear();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed (Path file, IOException exc){
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory (Path dir, IOException exc){
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
