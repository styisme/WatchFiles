package org.watchfiles.core;

import java.util.Arrays;
import java.util.Comparator;

public final class FileSorter {
    public static final int NAME = 0, SIZE = 1, TIME = 2, TYPE = 3;
    public static final int ASC = 0, DESC = 1;

    private FileSorter() {}

    public static void sort(FileItem[] items, final int mode, final int order) {
        Arrays.sort(items, new Comparator<FileItem>() {
            public int compare(FileItem a, FileItem b) {
                if (a.dir != b.dir) return a.dir ? -1 : 1;
                int r;
                switch (mode) {
                    case SIZE: r = a.size < b.size ? -1 : (a.size > b.size ? 1 : 0); break;
                    case TIME: r = a.modified < b.modified ? -1 : (a.modified > b.modified ? 1 : 0); break;
                    case TYPE: r = a.ext().compareTo(b.ext()); break;
                    default:   r = a.lower.compareTo(b.lower); break;
                }
                return order == DESC ? -r : r;
            }
        });
    }
}
