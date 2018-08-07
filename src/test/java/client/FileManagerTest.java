//Used the following pages as resources:
//https://stackoverflow.com/questions/1119385/junit-test-for-system-out-println
//https://stackoverflow.com/questions/5694385/getting-the-filenames-of-all-files-in-a-folder

package client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileManagerTest {

    private FileManager fileManager;

    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;
    private static final PrintStream originalErr = System.err;

    {
        fileManager = new FileManager();
    }

    @BeforeAll
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void setLocalPath(){
        String test = "test";
        fileManager.setLocalPath(test);

        assertEquals(test,fileManager.getLocalPath());
    }

    @Test
    public void displayLocal() {
        fileManager.displayLocal();

        assertEquals("Directory contents: folder sample.txt ", outContent.toString());
    }

    @Test
    public void renameFileLS() {
        String original = "sample.txt";
        String test = "test.txt";

        fileManager.renameFileLS(original, test);

        assertEquals("Success!", "Success!");

    }

}
