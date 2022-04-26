module.exports = async ({github, context, core, glob, io, exec, require}, job_name) => {
  // Available variables: https://github.com/actions/github-script#actionsgithub-script
  // Rest API Docs: https://docs.github.com/en/rest/actions/workflow-jobs#list-jobs-for-a-workflow-run
  // Rest JS API: https://octokit.github.io/rest.js/v18#actions-list-jobs-for-workflow-run
  //console.log(context);
  const result = await github.rest.actions.listJobsForWorkflowRun({
    owner: context.repo.owner,
    repo: context.repo.repo,
    run_id: context.runId,
  });
  //console.log(result);
  //console.log(result.jobs);
  // Filter for "self".
  // Note: have to use hardcoded value for name, because:
  //  * the key in yaml ('build:') is not available in this data
  //  * the name of current job is not available in github/jobs context
  return result.data.jobs.filter(it => it.status === 'in_progress' && it.name === job_name)[0].id;
};
