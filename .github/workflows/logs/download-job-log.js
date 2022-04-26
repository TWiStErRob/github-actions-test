module.exports = async ({github, context, core, glob, io, exec, require}, run_id) => {
  // Note: "Download workflow run logs (/actions/runs/{run_id}/logs) is not working, because the workflow run didn't complete yet.
  // Have to use job run instead.
  // Available variables: https://github.com/actions/github-script#actionsgithub-script
  // Rest API Docs: https://docs.github.com/en/rest/actions/workflow-jobs#download-job-logs-for-a-workflow-run
  // Rest JS API: https://octokit.github.io/rest.js/v18#actions-download-job-logs-for-workflow-run
  // Using Rest API example: https://github.com/actions/github-script#using-a-separate-github-token
  const result = await github.rest.actions.downloadJobLogsForWorkflowRun({
    owner: context.repo.owner,
    repo: context.repo.repo,
    job_id: run_id,
  });
  //console.log(result);
  return result.data;
};
