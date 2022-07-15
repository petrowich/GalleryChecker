package org.petrowich.gallerychecker.models;

public abstract class AbstractModel<K extends Number> implements Model {
    protected abstract K getId();
}
