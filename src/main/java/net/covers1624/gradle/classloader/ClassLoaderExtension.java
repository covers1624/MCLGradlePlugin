package net.covers1624.gradle.classloader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 10/11/18.
 */
public class ClassLoaderExtension {

    private String resolverDirectory = "runtime";
    private boolean makePack = false;
    private List<String> extraResolvers = new ArrayList<>();

    public String getResolverDirectory() {
        return resolverDirectory;
    }

    public void setResolverDirectory(String resolverDirectory) {
        this.resolverDirectory = resolverDirectory;
    }

    public boolean getMakePack() {
        return makePack;
    }

    public void setMakePack(boolean makePack) {
        this.makePack = makePack;
    }

    public void addExtraResolver(String clazz) {
        extraResolvers.add(clazz);
    }

    public List<String> getExtraResolvers() {
        return extraResolvers;
    }
}
