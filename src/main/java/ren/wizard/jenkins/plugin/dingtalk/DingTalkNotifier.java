package ren.wizard.jenkins.plugin.dingtalk;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import ren.wizard.jenkins.plugin.Messages;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author uyangjie
 */
public class DingTalkNotifier extends Notifier {
    private String accessToken;
    private String notifyPeople;

    @DataBoundConstructor
    public DingTalkNotifier(String accessToken, String notifyPeople) {
        this.accessToken = accessToken;
        this.notifyPeople = notifyPeople;
    }

    public String getNotifyPeople() {
        return notifyPeople;
    }

    @DataBoundSetter
    public void setNotifyPeople(String notifyPeople) {
        this.notifyPeople = notifyPeople;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @DataBoundSetter
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String[] getNotifyPeopleList() {
        if (StringUtils.isBlank(this.notifyPeople)) {
            return new String[0];
        }
        return this.notifyPeople.split(",");
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return true;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Symbol("dingTalk")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public FormValidation doCheck(@QueryParameter String accessToken, @QueryParameter String notifyPeople) {
            if (StringUtils.isBlank(accessToken)) {
                return FormValidation.error(Messages.DingTalkNotifier_DescriptorImpl_AccessTokenIsNecessary());
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.DingTalkNotifier_DescriptorImpl_DisplayName();
        }
    }
}
