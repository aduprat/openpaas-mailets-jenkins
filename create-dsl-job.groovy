import hudson.model.FreeStyleProject;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.BranchSpec;
import hudson.triggers.SCMTrigger;
import hudson.util.Secret;
import javaposse.jobdsl.plugin.*;
import jenkins.model.Jenkins;
import jenkins.model.JenkinsLocationConfiguration;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import jenkins.model.JenkinsLocationConfiguration;
import org.jenkinsci.plugins.ghprb.GhprbGitHubAuth;
import org.jenkinsci.plugins.ghprb.GhprbTrigger.DescriptorImpl;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl.DescriptorImpl;
import org.jenkinsci.plugins.scriptsecurity.sandbox.Whitelist;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.BlanketWhitelist;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

jenkins = Jenkins.instance;
jenkins.getExtensionList(Whitelist.class).push(new BlanketWhitelist());
jenkins.setNumExecutors(16);

jobName = "create-dsl-job";
gitTrigger = new SCMTrigger("* * * * *");
dslBuilder = new ExecuteDslScripts();
dslBuilder.setUseScriptText(false);
dslBuilder.setTargets("build_github_branch");
dslBuilder.setRemovedJobAction(RemovedJobAction.DISABLE);

secure = Jenkins.instance.getExtensionList(org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.SecureGroovyScript.DescriptorImpl.class)[0];
secure.script = "build_github_branch";
secure.save();

dslProject = new hudson.model.FreeStyleProject(jenkins, jobName);
dslProject.scm = new GitSCM("https://github.com/aduprat/openpaas-mailets-jenkins.git");
dslProject.scm.branches = [new BranchSpec("*/master")];
dslProject.addTrigger(gitTrigger);
dslProject.createTransientActions();
dslProject.getPublishersList().add(dslBuilder);
jenkins.add(dslProject, jobName);
gitTrigger.start(dslProject, true);


credentials = new StringCredentialsImpl(CredentialsScope.GLOBAL, "b1d836bd-25d3-4b58-bee8-4d9906ca1908", "Github credentials", Secret.fromString(System.getenv("GITHUB_TOKEN")));
credentialsStore = Jenkins.instance.getExtensionList(com.cloudbees.plugins.credentials.SystemCredentialsProvider.class)[0];
credentialsStore.store.addCredentials(Domain.global(), credentials);

githubAuth = new GhprbGitHubAuth("https://api.github.com", System.getenv("JENKINS_URL"), credentials.getId(), "Github Auth", null, null);
ghprbDescriptor = Jenkins.instance.getExtensionList(org.jenkinsci.plugins.ghprb.GhprbTrigger.DescriptorImpl.class)[0];
auths = new ArrayList();
auths.add(githubAuth);
ghprbDescriptor.githubAuth = auths;
ghprbDescriptor.useComments = true;
ghprbDescriptor.useDetailedComments = true;
ghprbDescriptor.save();
