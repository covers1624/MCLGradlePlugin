package net.covers1624.gradle.classloader;

import net.covers1624.gradlestuff.xz.TarXZ;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.CopySpec;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.Jar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 10/11/18.
 */
public class ClassLoaderPlugin implements Plugin<Project> {

    TaskContainer tasks;
    private ClassLoaderExtension extension;

    @Override
    public void apply(Project project) {
        tasks = project.getTasks();
        project.afterEvaluate(this::afterEvaluate);
        extension = project.getExtensions().create("mcl", ClassLoaderExtension.class);
    }

    public void afterEvaluate(Project project) {
        Configuration compile = project.getConfigurations().getAt("compile");
        Jar jar = project.getTasks().withType(Jar.class).getByName("jar");

        List<File> files = new ArrayList<>();
        compile.getResolvedConfiguration().getResolvedArtifacts().forEach(d -> {
            if (d.getName().equals("ModularClassLoader") && files.isEmpty()) {
                files.add(d.getFile());
            }
        });
        if (files.isEmpty()) {
            throw new GradleException("Unable to find ModularClassLoader in dependencies.");
        }
        File mcl = files.get(0);
        project.getLogger().info("Using MCL Jar: {}", mcl);

        CopySpec libsOut = project.copySpec();
        libsOut.from(compile).exclude("**/ModularClassLoader*.jar");

        Task task = tasks.create("genService");
        task.doFirst((t) -> {
            List<String> list = new ArrayList<>();
            list.add("net.covers1624.classloader.resolvers.SimpleResolver");
            list.addAll(extension.getExtraResolvers());
            File f = new File(t.getTemporaryDir(), "net.covers1624.classloader.IResourceResolverFactory");
            try(PrintStream stream = new PrintStream(f)) {
                for(String str : list) {
                    stream.println(str);
                }
            } catch (FileNotFoundException e) {
                throw new GradleException("Unable to write services file.", e);
            }
            jar.from(f, c -> c.into("META-INF/services"));
        });
        jar.dependsOn(task);


        jar.from(project.zipTree(mcl));
        jar.getManifest().getAttributes().put("Resolver-Path", extension.getResolverDirectory());
        jar.getManifest().getAttributes().put("Main-Class", "net.covers1624.classloader.LaunchBouncer");
        if (extension.getMakePack()) {
            jar.setDestinationDir(jar.getTemporaryDir());
            TarXZ packRuntime = tasks.create("pack_runtime", TarXZ.class);
            packRuntime.setDestinationDir(project.file("build/libs"));
            packRuntime.setBaseName(jar.getBaseName());
            packRuntime.setVersion(jar.getVersion());
            packRuntime.setClassifier("runtime");
            packRuntime.from(jar.getArchivePath());
            packRuntime.from(libsOut, e -> e.into(extension.getResolverDirectory()));
            tasks.getByName("build").dependsOn(packRuntime);
        } else {
            Copy copyTask = tasks.create("copyStuffs", Copy.class);
            copyTask.setDestinationDir(project.file(pathCombine("build/libs", extension.getResolverDirectory())));
            copyTask.with(libsOut);
            tasks.getByName("build").dependsOn(copyTask);
        }
    }

    private static String pathCombine(String a, String b) {
        if (!a.endsWith("/") && !b.startsWith("/")) {
            a += "/";
        }
        return a + b;
    }
}
