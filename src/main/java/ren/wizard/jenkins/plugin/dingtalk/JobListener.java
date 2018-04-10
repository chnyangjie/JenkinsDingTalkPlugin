package ren.wizard.jenkins.plugin.dingtalk;

import hudson.Extension;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import hudson.tasks.Publisher;
import org.apache.commons.lang3.StringUtils;
import ren.wizard.dingtalkclient.DingTalkClient;
import ren.wizard.dingtalkclient.message.MarkdownMessage;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author uyangjie
 */
@Extension
public class JobListener extends RunListener<AbstractBuild> {
    private DingTalkClient dingTalkClient = DingTalkClient.getInstance();
    private Logger logger = Logger.getLogger(JobListener.class.getName());

    public JobListener() {
        super(AbstractBuild.class);
    }

    @Override
    public void onCompleted(AbstractBuild abstractBuild, @Nonnull TaskListener listener) {
        Result result = abstractBuild.getResult();
        logger.info("on Completed");
        DingTalkNotifier dingTalkNotifier = getNotifier(abstractBuild.getProject());
        if (dingTalkNotifier == null || StringUtils.isBlank(dingTalkNotifier.getAccessToken())) {
            logger.warning("access token is null");
            return;
        }
        MarkdownMessage.MarkdownMessageBuilder markdownMessageBuilder;
        boolean isSuccess = Result.SUCCESS.equals(result);
        markdownMessageBuilder = MarkdownMessage.builder()
                .title(String.format("%s's #%d build %s", abstractBuild.getProject().getDisplayName(), abstractBuild.getNumber(), isSuccess ? "SUCCESS" : "FAILED"))
                .item(MarkdownMessage.getHeaderText(1, String.format("%s's #%d build %s", abstractBuild.getProject().getDisplayName(), abstractBuild.getNumber(), isSuccess ? "SUCCESS" : "FAILED")))
        ;
        sendMessage(dingTalkNotifier, markdownMessageBuilder);
    }

    private void sendMessage(DingTalkNotifier dingTalkNotifier, MarkdownMessage.MarkdownMessageBuilder markdownMessageBuilder) {
        try {
            dingTalkClient.sendMessage(dingTalkNotifier.getAccessToken(), markdownMessageBuilder.build());
            markdownMessageBuilder.atMobiles(Arrays.asList(dingTalkNotifier.getNotifyPeopleList()));
            logger.info("send ding talk message success");
        } catch (IOException e) {
            logger.warning("send ding talk message failed");
            e.printStackTrace();
        }
    }

    @Override
    public void onStarted(AbstractBuild abstractBuild, TaskListener listener) {
        DingTalkNotifier dingTalkNotifier = getNotifier(abstractBuild.getProject());
        logger.info("on start");
        if (dingTalkNotifier == null || StringUtils.isBlank(dingTalkNotifier.getAccessToken())) {
            logger.warning("access token is null");
            return;
        }
        MarkdownMessage.MarkdownMessageBuilder markdownMessageBuilder;
        markdownMessageBuilder = MarkdownMessage.builder()
                .title(String.format("%s's #%d build %s", abstractBuild.getProject().getDisplayName(), abstractBuild.getNumber(), "START"))
                .item(MarkdownMessage.getHeaderText(1, String.format("%s's #%d build %s", abstractBuild.getProject().getDisplayName(), abstractBuild.getNumber(), "START")))
        ;
        markdownMessageBuilder.atMobiles(Arrays.asList(dingTalkNotifier.getNotifyPeopleList()));
        sendMessage(dingTalkNotifier, markdownMessageBuilder);
    }

    @SuppressWarnings("unchecked")
    private DingTalkNotifier getNotifier(AbstractProject abstractProject) {
        Map<Descriptor<Publisher>, Publisher> map = abstractProject.getPublishersList().toMap();
        for (Publisher publisher : map.values()) {
            if (publisher instanceof DingTalkNotifier) {
                return ((DingTalkNotifier) publisher);
            }
        }
        return null;
    }
}
