package com.synitex.blogbuilder.soy;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;
import com.synitex.blogbuilder.props.IBlogProperties;
import com.synitex.blogbuilder.props.IDevProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Service
public class TofuProvider implements ITofuProvider {

    private static final Logger log = LoggerFactory.getLogger(TofuProvider.class);

    private final IBlogProperties props;

    private SoyTofu tofu;

    @Autowired
    public TofuProvider(IBlogProperties props) {
        this.props = props;
    }

    @Override
    public SoyTofu getTofu() {
        if(tofu == null || props.getDevProperties().isDevMode()) {
            reloadTofu();
        }
        return tofu;
    }

    private void reloadTofu() {
        log.info("Reloading tofu...");
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            IDevProperties devProps = props.getDevProperties();
            String templatesPath = devProps.getTemplatesPath();
            List<SoyFile> soyFiles = devProps.isDevMode() && !Strings.isNullOrEmpty(templatesPath)
                    ? listSoyFilesFromDirectory(templatesPath)
                    : listSoyFilesFromClasspath();
            SoyFileSet sfs = collectAllSoyTemplates(soyFiles);
            tofu = sfs.compileToTofu();
            log.info("Tofu reloaded in " + stopwatch.elapsed(MILLISECONDS) + "ms");
        } catch (IOException e) {
            log.error("Failed to list soy templates", e);
            throw new RuntimeException("Failed to list soy templates", e);
        }
    }

    private SoyFileSet collectAllSoyTemplates(List<SoyFile> soyFiles) {
        SoyFileSet.Builder soyBuilder = new SoyFileSet.Builder();
        for (SoyFile soyFile : soyFiles) {
            soyBuilder.add(soyFile.url);
            if(log.isDebugEnabled()) {
                log.debug("Soy file found and added to tofu: " + soyFile.url.toString());
            }
        }
        return soyBuilder.build();
    }

    private List<SoyFile> listSoyFilesFromClasspath() throws IOException {
        List<SoyFile> soyFiles = new ArrayList<SoyFile>();
        String pattern = "classpath:templates/**.soy";
        if(log.isDebugEnabled()) {
            log.debug("Searching soy files in classpath using pattern: {}...", pattern);
        }
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(pattern);
        if (resources != null && resources.length > 0) {
            for (Resource r : resources) {
                soyFiles.add(new SoyFile(r.getURL()));
            }
        }
        return soyFiles;
    }

    private List<SoyFile> listSoyFilesFromDirectory(String soyPath) throws IOException {
        if(log.isDebugEnabled()) {
            log.debug("Searching soy files in directory: {}...", soyPath);
        }
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
