package com.synitex.blogbuilder.rest;

import com.google.common.base.Joiner;
import com.synitex.blogbuilder.builder.IBlogBuilder;
import com.synitex.blogbuilder.props.IBlogProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@RestController
public class BlogRest {

    private static final Logger log = LoggerFactory.getLogger(BlogRest.class);

    private final IBlogBuilder blogBuilder;
    private final IBlogProperties props;

    @Autowired
    public BlogRest(IBlogBuilder blogBuilder,
                    IBlogProperties props) {
        this.blogBuilder = blogBuilder;
        this.props = props;
    }

    @RequestMapping(value="/api/rebuild", method= RequestMethod.GET)
    public String rebuild() {
        blogBuilder.build(false);
        return "ok";
    }

    @RequestMapping(value="/api/commit", method= RequestMethod.GET)
    public String commit() {
        String branch = props.getGithubProperties().getBranch();
        String outpath = props.getOutPath();
        try {
            executeCommand("git", "-C", outpath, "checkout", branch);
            executeCommand("git", "-C", outpath, "add", "--all");
            executeCommand("git", "-C", outpath, "commit", "-a", "-m", "\"blog updated\"");
            return "ok";
        } catch (IOException | InterruptedException | TimeoutException e) {
            log.error("Failed to commit changes.", e);
            return "error";
        }
    }

    @RequestMapping(value="/api/push", method= RequestMethod.GET)
    public String push() {
        String outpath = props.getOutPath();
        String branch = props.getGithubProperties().getBranch();
        try {
            executeCommand("git", "-C", outpath, "push", "origin", branch);
            return "ok";
        } catch (IOException | InterruptedException | TimeoutException e) {
            log.error("Failed to commit changes.", e);
            return "error";
        }
    }

    @RequestMapping(value="/api/status", method= RequestMethod.GET)
    public String status() {
        String outpath = props.getOutPath();
        try {
            return executeCommand("git", "-C", outpath, "status");
        } catch (IOException | InterruptedException | TimeoutException e) {
            log.error("Failed to get git status.", e);
            return "error";
        }
    }

    private String executeCommand(String ...cmd) throws IOException, InterruptedException, TimeoutException {
        log.info("Executing {}...", Joiner.on(" ").join(cmd));
        ProcessResult res = new ProcessExecutor(cmd).readOutput(true).execute();
        String output = res.outputUTF8();
        int exitValue = res.getExitValue();
        log.info("Exit value: {}. Output: {}", exitValue, output);
        if(exitValue > 0) {
            throw new IllegalStateException(String.format("Command %s exited with code %s.", Joiner.on(" ").join(cmd), exitValue));
        }
        return output;
    }
    
}
