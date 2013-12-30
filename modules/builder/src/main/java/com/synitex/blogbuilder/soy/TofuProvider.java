package com.synitex.blogbuilder.soy;

import com.google.common.base.Stopwatch;
import com.google.inject.Singleton;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;
import com.synitex.blogbuilder.props.IBlogProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Singleton
public class TofuProvider extends AbstractTofuProvider implements ITofuProvider {

    private static final Logger log = LoggerFactory.getLogger(TofuProvider.class);

    private final IBlogProperties props;

    private SoyTofu tofu;

    @Inject
    public TofuProvider(IBlogProperties props) {
        this.props = props;
        reloadTofu();
    }

    @Override
    public SoyTofu getTofu() {
        return tofu;
    }

    private void setTofu(SoyTofu tofu) {
        this.tofu = tofu;
    }

    private synchronized void reloadTofu() {
        log.info("Reloading tofu...");
        Stopwatch stopwatch = Stopwatch.createStarted();

        List<SoyFile> soyFiles = null;
        try {
            soyFiles = listSoyFilesFromDirectory(props.getTemplatesPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to get soy templates from " + props.getTemplatesPath(), e);
        }
        SoyFileSet sfs = collectAllSoyTemplates(soyFiles);
        SoyTofu tf = sfs.compileToTofu();
        setTofu(tf);

        log.info("Tofu reloaded in " + stopwatch.elapsed(MILLISECONDS) + "ms");
    }

    private SoyFileSet collectAllSoyTemplates(List<SoyFile> soyFiles) {
        SoyFileSet.Builder soyBuilder = new SoyFileSet.Builder();
        for (SoyFile soyFile : soyFiles) {
            soyBuilder.add(soyFile.file);
            if(log.isDebugEnabled()) {
                log.debug("Soy file found and added to tofu: " + soyFile.file.getAbsolutePath());
            }
        }
        return soyBuilder.build();
    }

}
