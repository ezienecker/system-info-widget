package com.manuzid.systeminfowidget.category.util;

import com.manuzid.systeminfowidget.category.AbstractCategory;

import java.util.Comparator;

/**
 * Damit die Kategorien immer in der gleichen Reihenfolge präsentiert werden.
 *
 * Created by Emanuel Zienecker on 13.10.17.
 * Copyright (c) 2017 Emanuel Zienecker. All rights reserved.
 */
public class CategoryComparator implements Comparator<AbstractCategory> {

    @Override
    public int compare(AbstractCategory lhs, AbstractCategory rhs) {
        return lhs.getRequestCode() < rhs.getRequestCode() ? -1 : lhs.getRequestCode() == rhs.getRequestCode() ? 0 : 1;
    }
}
