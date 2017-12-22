package com.vipsfin.competition.stat.util;

import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.io.IoUtil;
import com.xiaoleilu.hutool.io.LineHandler;
import com.xiaoleilu.hutool.lang.Func;
import com.xiaoleilu.hutool.log.Log;
import com.xiaoleilu.hutool.log.LogFactory;
import com.xiaoleilu.hutool.util.CharsetUtil;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;

public class Util {

    private final static Log log = LogFactory.get();

    private final static char[] array = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final static String numStr = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    //byte 数组与 int 的相互转换
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    //10进制转为其他进制，除留取余，逆序排列
    public static String tenToN(long number, int N) {
        Long rest = number;
        Stack<Character> stack = new Stack<Character>();
        StringBuilder result = new StringBuilder(0);
        while (rest != 0) {
            stack.add(array[new Long((rest % N)).intValue()]);
            rest = rest / N;
        }
        for (; !stack.isEmpty(); ) {
            result.append(stack.pop());
        }
        return result.length() == 0 ? "0" : result.toString();
    }

    public static List<File> split(String filePath,
                                   String targetPath,
                                   int writerBufferLength,
                                   int fileSize,
                                   Func<String, Pair<Integer, String>> data) {
        Stopwatch stopwatch = Stopwatch.create().start();

        List<File> files = new ArrayList<>();
        List<Writer> writers = new ArrayList<>();
        File file = FileUtil.file(filePath);

        List<StringBuilder> writerBuffers = new ArrayList<>();

        for (int i = 0; i < fileSize; i++) {
            File targetFile = new File(targetPath + File.separator + i + "." + FileUtil.extName(file));
            FileUtil.del(targetFile);
            files.add(targetFile);

            Writer writer = FileUtil.getWriter(targetFile.getAbsolutePath(), CharsetUtil.UTF_8, true);
            writers.add(writer);

            StringBuilder writerBuilder = new StringBuilder();
            writerBuffers.add(writerBuilder);
        }

        AtomicLong counter = new AtomicLong();
        FileUtil.readUtf8Lines(file, (LineHandler) (String line) -> {
            Pair<Integer, String> indexAndValue = data.call(line);
            if (indexAndValue == null) {
                return;
            }

            StringBuilder writerBuffer = writerBuffers.get(indexAndValue.getKey());
            writerBuffer.append(indexAndValue.getValue());

            append(writers, writerBuffers, indexAndValue.getKey(), writerBufferLength);

            if (counter.incrementAndGet() % 1000000 == 0) {
                log.info("Splitting {}, size: {}", filePath, counter.get());
            }
        });

        for (int i = 0; i < writers.size(); i++) {
            append(writers, writerBuffers, i, 0);
            IoUtil.close(writers.get(i));
        }

        stopwatch.stop();
        log.info("Split {}, size: {}, duration:{}", filePath, counter.get(), stopwatch.duration());
        return files;
    }

    public static void append(List<Writer> writers, List<StringBuilder> writerBuffers, int index, int maxBufferLength) {
        StringBuilder writerBuffer = writerBuffers.get(index);

        if (writerBuffer.length() >= maxBufferLength) {
            try {
                writers.get(index).append(writerBuffer.toString());
            } catch (IOException e) {
                log.error(e, e.getMessage());
            }

            writerBuffer.delete(0, writerBuffer.length());
        }
    }
}