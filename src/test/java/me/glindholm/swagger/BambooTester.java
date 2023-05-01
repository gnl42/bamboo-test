package me.glindholm.swagger;

import java.util.Base64;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.glindholm.bamboo.api.BuildApi;
import me.glindholm.bamboo.api.CoreApi;
import me.glindholm.bamboo.api.DefaultApi;
import me.glindholm.bamboo.api.UserManagementApi;
import me.glindholm.bamboo.invoker.ApiClient;
import me.glindholm.bamboo.invoker.ApiException;
import me.glindholm.bamboo.model.FindUsersInGroup200Response;
import me.glindholm.bamboo.model.RestInfo;
import me.glindholm.bamboo.model.RestPlan;
import me.glindholm.bamboo.model.RestPlans;
import me.glindholm.bamboo.model.RestProjects;
import me.glindholm.bamboo.model.RestResources;
import me.glindholm.bamboo.model.RestResults;
import me.glindholm.bamboo.model.Result;
import me.glindholm.bamboo.model.UserBean;

public class BambooTester {
	private static final String BUILD = "6";

	private static final String PLAN = "CON0";

	private static final String PROJECT = "AP";

	static ApiClient apiClient;

	@BeforeAll
	public static void connect() {
		apiClient = new ApiClient();
		apiClient.updateBaseUri("https://192.168.0.120:9683/rest");
		final ObjectMapper mapper = apiClient.getObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		apiClient.setRequestInterceptor(
				authorize -> authorize.header("Authorization", basicAuth("gnl", System.getenv("PW"))));

	}

	private static String basicAuth(final String username, final String password) {
		return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
	}

	@Test
	public void testServer() throws InterruptedException, ExecutionException, ApiException {
		final DefaultApi def = new DefaultApi(apiClient);

		final RestInfo serverInfo = def.getInfo().get();
		System.out.println(serverInfo);
	}

	@Test
	public void testAllPlans() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);

		final RestPlans plans = build.getAllPlanList("plans.plan.actions,plans.plan.stages.stage.plans,plans.plan.branches.branch.latestResult").get();
		System.out.println(plans);
	}

	@Test
	public void testPlan() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);

		final RestPlan plans = build.getPlan("AP-CON", "", null).get();
		System.out.println(plans);

	}

	@Test
	public void testPlan2() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);

		final RestPlan plans = build.getPlan("AP-CON0", "", null).get();
		System.out.println(plans);

	}

	@Test
	public void testNoBuildPlan() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);

		final RestPlan plans = build.getPlan("JT-STSUIQ", "", null).get();
		System.out.println(plans);

	}

	@Test
	public void testBranch() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);

		final Object plans = build.getPlanBranches(PROJECT, PLAN, null, null, null, null).get();
		System.out.println(plans);

	}

//    @Test
//    public void testResult() throws InterruptedException, ExecutionException, ApiException {
//        final DefaultApi def = new DefaultApi(apiClient);
//
////        final RestResultList plan = def.getLatestBuildResultsForBuild("AP-CON0", "2", "changes", null, null, null, null, null, null, null, null, null).get();
//
//        final Result res = def.getBuild("AP", "CON0", "2", "changes", null).get();
//        final BuildPlanResults res2 = def.getLatestBuildResultsForProject("AP-CON", "results", null, null, null, null, null, null, null, null, null).get();
//        System.out.println(res2);
//    }

	@Test
	public void testUsers() throws InterruptedException, ExecutionException, ApiException {
		final UserManagementApi user = new UserManagementApi(apiClient);

		final FindUsersInGroup200Response users = user.getUsers(null, null, null).get();
		System.out.println(users);

	}

	@Test
	public void testCurrentUser() throws InterruptedException, ExecutionException, ApiException {
		final DefaultApi def = new DefaultApi(apiClient);

		final UserBean myself = def.getCurrentUser().get();
		System.out.println(myself);
	}

	@Test
	public void testResults() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);
		final CoreApi core = new CoreApi(apiClient);
		final DefaultApi def = new DefaultApi(apiClient);

		final RestResults res = def.getLatestBuildResults(null, null, null, null, null, null, null, null, null, null)
				.get();
		System.out.println(res);
	}

	@Test
	public void testResultProject() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);
		final CoreApi core = new CoreApi(apiClient);
		final DefaultApi def = new DefaultApi(apiClient);

		String expand = "changes,changes.change,changes.change.files,comments,comments.comment,labels,stages.stage[0],stages.stage[0].results.result.testResults.allTests.testResult.errors,stages.stage.results.result.testResults.failedTests.testResult.errors";
		Boolean allStates = null;
		Boolean continuable = null;
		String issueKey = null;
		Integer maxResuts = null;
		Integer startIndex = null;
		String label = null;
		String buildState = null;
		String favourite = null;
		String lifeCycteState = null;
		RestResults res = def.getLatestBuildResultsForProject("AP-CON0", allStates, continuable, expand, issueKey,
				maxResuts, startIndex, label, buildState, favourite, lifeCycteState).get();

		System.out.println(res);
	}

	@Test
	public void testResultMissingPlan() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);
		final CoreApi core = new CoreApi(apiClient);
		final DefaultApi def = new DefaultApi(apiClient);

		try {
			String expand = "changes,changes.change,changes.change.files,comments,comments.comment,labels,stages.stage[0],stages.stage[0].results.result.testResults.allTests.testResult.errors,stages.stage.results.result.testResults.failedTests.testResult.errors";
			Boolean allStates = null;
			Boolean continuable = null;
			String issueKey = null;
			Integer maxResuts = null;
			Integer startIndex = null;
			String label = null;
			String buildState = null;
			String favourite = null;
			String lifeCycteState = null;
			RestResults res = def.getLatestBuildResultsForProject("AP-CON05", allStates, continuable, expand, issueKey,
					maxResuts, startIndex, label, buildState, favourite, lifeCycteState).get();
			System.out.println(res);
		} catch (InterruptedException | ExecutionException | ApiException e) {
			if (e.getCause() != null && e.getCause() instanceof ApiException) {
				final ApiException realE = (ApiException) e.getCause();
				if (realE.getCode() == 404) {
					return;
				}
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testResultBuildKey() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);
		final CoreApi core = new CoreApi(apiClient);
		final DefaultApi def = new DefaultApi(apiClient);

		final RestResults res = def.getBuildHistory(PROJECT, "CON0", null, null, "results[0].result", null, null, null,
				null, null, null, null).get();
		System.out.println(res);
	}

	@Test
	public void testResultBuild() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);
		final CoreApi core = new CoreApi(apiClient);
		final DefaultApi def = new DefaultApi(apiClient);

//        final RestResults history = def.getBuildHistory("AP", "CON0", true, true, "changes", null, null, null, null, null, null, null).get();
		final Result res = def.getBuild(PROJECT, PLAN, BUILD,
				"changes.change,metadata,plan,master,vcsRevisions,artifacts,comments.comment,labels,jiraIssues,variables,stages.stage.results.result",
				null).get();
		System.out.println(res);
	}

	@Test
	public void testResultStatus() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);
		final CoreApi core = new CoreApi(apiClient);
		final DefaultApi def = new DefaultApi(apiClient);

//        final RestResults history = def.getBuildHistory("AP", "CON0", true, true, "changes", null, null, null, null, null, null, null).get();
		final Result history = def.getBuild(PROJECT, PLAN, BUILD, "testResults.allTests.testResult.errors", null).get();
		System.out.println(history);
	}

	@Test
	public void testTestErrors() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);
		final CoreApi core = new CoreApi(apiClient);
		final DefaultApi def = new DefaultApi(apiClient);

//        final RestResults history = def.getBuildHistory("AP", "CON0", true, true, "changes", null, null, null, null, null, null, null).get();
		final Result history = def.getBuild(PROJECT, "CON0", "2", "testResults.allTests.testResult.errors", null).get();
		System.out.println(history);
	}

	@Test
	public void testServices() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);
		final CoreApi core = new CoreApi(apiClient);
		final DefaultApi def = new DefaultApi(apiClient);

//        final RestResults history = def.getBuildHistory("AP", "CON0", true, true, "changes", null, null, null, null, null, null, null).get();
		final RestResources history = def.getAllServices().get();
		System.out.println(history);
	}

	@Test
	public void testProjects() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);
		final CoreApi core = new CoreApi(apiClient);
		final DefaultApi def = new DefaultApi(apiClient);

		final RestProjects res = def.getProjects(null, true).get();
		System.out.println(res);
	}

}
