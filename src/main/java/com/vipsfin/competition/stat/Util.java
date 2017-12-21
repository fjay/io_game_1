package com.vipsfin.competition.stat;

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

    public static List<File> split(String filePath, String targetPath, int fileSize,
                                   Func<String, Pair<Integer, String>> data) {
        List<File> files = new ArrayList<>();
        Writer[] writers = new Writer[fileSize];
        File file = FileUtil.file(filePath);

        for (int i = 0; i < writers.length; i++) {
            File targetFile = new File(targetPath + File.separator + i + "." + FileUtil.extName(file));
            writers[i] = FileUtil.getWriter(targetFile, CharsetUtil.UTF_8, true);
            files.add(targetFile);
        }

        AtomicLong counter = new AtomicLong();
        FileUtil.readUtf8Lines(file, (LineHandler) line -> {
            Pair<Integer, String> x = data.call(line);
            if (x == null) {
                return;
            }

            try {
                writers[x.getKey()].append(x.getValue());
            } catch (IOException e) {
                log.error(e, e.getMessage());
            }

            if (counter.incrementAndGet() % 1000000 == 0) {
                log.info("Splitting {}, size: {}", filePath, counter.get());
            }
        });

        for (Writer writer : writers) {
            IoUtil.close(writer);
        }

        return files;
    }
}