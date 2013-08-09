package com.facebook.presto.cli;

import java.io.IOException;
import java.util.List;

public interface OutputPrinter
{
    void printRows(List<List<?>> rows, boolean complete)
            throws IOException;

    void finish()
            throws IOException;
}