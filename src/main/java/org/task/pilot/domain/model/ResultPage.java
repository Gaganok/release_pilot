package org.task.pilot.domain.model;

import java.util.List;

public record ResultPage<T>(int pageIndex, int pageSize, long totalCount, int totalPages, List<T> items) {
}
