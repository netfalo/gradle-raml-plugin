package com.netfalo.gradle.plugin

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.DirectoryFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import org.gradle.api.Project

class ClassLoaderUtils {

    private static ClassLoader originalClassLoader

    static def addLocationsToClassLoader(Project project) {
        Set<URL> urls = new HashSet<>()
        try {
            urls.add(new URL("file://" + project.getBuildDir().getAbsolutePath() + "/classes/java/main/"))
        } catch (MalformedURLException e) {
            System.err.println(e.getMessage())
        }

        originalClassLoader = Thread.currentThread().getContextClassLoader()

        Thread.currentThread().setContextClassLoader(
                new URLClassLoader(urls.toArray(new URL[urls.size()]), originalClassLoader))
    }

    static def restoreOriginalClassLoader() {
        if (originalClassLoader == null) {
            System.err.println("Original ClassLoader not available.")
        }
        Thread.currentThread().setContextClassLoader(originalClassLoader)
    }

    static def loadPackages(Project project) {
        List<String> packages = new ArrayList<>()
        File rootDir = new File(project.getProjectDir().toString() + File.separator + "src/main/java")
        Collection<File> files = FileUtils.listFilesAndDirs(rootDir, DirectoryFileFilter.DIRECTORY, TrueFileFilter.TRUE)
        files.each {
            String pack = it.toString().replace(rootDir.toString(), "").replace(File.separator, ".")
            if (pack.startsWith(".")) {
                pack = pack.substring(1, pack.length())
            }
            if (!pack.isEmpty()) {
                packages.add(pack)
            }
        }
        return packages
    }
}
