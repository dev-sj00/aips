package com.portfolio.aips.project.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BatchUtils {

    public static <T> List<List<T>> chunk(List<T> source, int chunkSize) {
        int total = source.size();
        int numChunks = (total + chunkSize - 1) / chunkSize;
        //total 1099, chunk size 100일경우 total + chunkSize - 1(1198) 올림으로 11번 처리
        //total 1000일 경우 total + chunkSize - 1 (1099)으로 정확히 10번 처리

        return IntStream.range(0, numChunks)//for 0 --> numChunks 만큼 subList List<List<subList>>
                .mapToObj(i -> new ArrayList<>(source.subList(
                        i * chunkSize,
                        Math.min((i + 1) * chunkSize, total)
                )))
                .collect(Collectors.toList());
    }
}
