package com.synitex.blogbuilder.soy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTofuProvider {

    private static final Logger log = LoggerFactory.getLogger(AbstractTofuProvider.class);

    protected List<SoyFile> listSoyFilesFromClasspath() throws IOException {
        List<SoyFile> soyFiles = new ArrayList<SoyFile>();
        String pattern = "classpath:templates/**.soy";
        log.debug("Searching soy files in classpath using pattern: " + pattern);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(pattern);
        if (resources != null && resources.length > 0) {
            for (Resource r : resources) {
                soyFiles.add(new SoyFile(r.getURL()));
            }
        }
        return soyFiles;
    }

    protected List<SoyFile> listSoyFilesFromDirectory(String soyPath) throws IOException {
        log.debug("Searching soy files in directory: " + soyPath);
        File path = new File(soyPath);
        List<SoyFile> soyFiles = new ArrayList<SoyFile>();
        listSoyFilesFromDirectoryImpl(path, soyFiles);
        return soyFiles;
    }

    private void listSoyFilesFromDirectoryImpl(File path, List<SoyFile> soyFiles) throws IOException {
        File[] files = path.listFiles(new FileFilter(){
            @Override
            public boolean accept(File pathname) {
                return !pathname.isDirectory() && pathname.getName().endsWith(".soy");
            }
        });
        if(files != null && files.length > 0) {
            for(File file : files) {
                soyFiles.add(new SoyFile(file.toURI().toURL()));
            }
        }
        File[] dirs = path.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        for(File dir : dirs) {
            listSoyFilesFromDirectoryImpl(dir, soyFiles);
        }
    }

    // --------------------------------------------------------

    public class SoyFile {
        protected URL url;
        public SoyFile(URL url) {
            this.url = url;
        }
    }
}
