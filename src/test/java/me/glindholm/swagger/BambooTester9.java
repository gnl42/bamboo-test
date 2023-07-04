package me.glindholm.swagger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.glindholm.mylyn.build.bamboo.api.BuildApi;
import me.glindholm.mylyn.build.bamboo.api.CoreApi;
import me.glindholm.mylyn.build.bamboo.api.DefaultApi;
import me.glindholm.mylyn.build.bamboo.api.DeploymentApi;
import me.glindholm.mylyn.build.bamboo.api.PermissionsApi;
import me.glindholm.mylyn.build.bamboo.api.ResourceApi;
import me.glindholm.mylyn.build.bamboo.api.UserManagementApi;
import me.glindholm.mylyn.build.bamboo.invoker.ApiClient;
import me.glindholm.mylyn.build.bamboo.invoker.ApiException;
import me.glindholm.mylyn.build.bamboo.invoker.Pair;
import me.glindholm.mylyn.build.bamboo.model.CreateCommentRequest;
import me.glindholm.mylyn.build.bamboo.model.DashboardProjectWithEnvironmentStatus;
import me.glindholm.mylyn.build.bamboo.model.DirectoryInformationResponse;
import me.glindholm.mylyn.build.bamboo.model.FindAssignedGroups200Response;
import me.glindholm.mylyn.build.bamboo.model.FindUnassignedUserRepositoryAliases200Response;
import me.glindholm.mylyn.build.bamboo.model.GetAgents200Response;
import me.glindholm.mylyn.build.bamboo.model.GetEnvironmentsExecutableByAgent200Response;
import me.glindholm.mylyn.build.bamboo.model.GetPaginatedProjectRepositories200Response;
import me.glindholm.mylyn.build.bamboo.model.GetUserAccessTokens200Response;
import me.glindholm.mylyn.build.bamboo.model.GetUsers200Response;
import me.glindholm.mylyn.build.bamboo.model.ListGroupPermissions6200Response;
import me.glindholm.mylyn.build.bamboo.model.ListRolePermissions6200Response;
import me.glindholm.mylyn.build.bamboo.model.RestAgentAssignmentExecutorDetails;
import me.glindholm.mylyn.build.bamboo.model.RestAgentAssignmentExecutorDetailsList;
import me.glindholm.mylyn.build.bamboo.model.RestAgentInformation;
import me.glindholm.mylyn.build.bamboo.model.RestAgentStatus;
import me.glindholm.mylyn.build.bamboo.model.RestArtifactDefinitions;
import me.glindholm.mylyn.build.bamboo.model.RestAuditLogConfiguration;
import me.glindholm.mylyn.build.bamboo.model.RestBranches;
import me.glindholm.mylyn.build.bamboo.model.RestBuildLabel;
import me.glindholm.mylyn.build.bamboo.model.RestBuildLabels;
import me.glindholm.mylyn.build.bamboo.model.RestChart;
import me.glindholm.mylyn.build.bamboo.model.RestDedicatedAgent;
import me.glindholm.mylyn.build.bamboo.model.RestDeploymentProject;
import me.glindholm.mylyn.build.bamboo.model.RestDeploymentSpec;
import me.glindholm.mylyn.build.bamboo.model.RestElasticConfiguration;
import me.glindholm.mylyn.build.bamboo.model.RestEnableContainer;
import me.glindholm.mylyn.build.bamboo.model.RestIdContainer;
import me.glindholm.mylyn.build.bamboo.model.RestInfo;
import me.glindholm.mylyn.build.bamboo.model.RestJiraIssue;
import me.glindholm.mylyn.build.bamboo.model.RestPlan;
import me.glindholm.mylyn.build.bamboo.model.RestPlanBranch;
import me.glindholm.mylyn.build.bamboo.model.RestPlanLabel;
import me.glindholm.mylyn.build.bamboo.model.RestPlanSpec;
import me.glindholm.mylyn.build.bamboo.model.RestPlans;
import me.glindholm.mylyn.build.bamboo.model.RestProject;
import me.glindholm.mylyn.build.bamboo.model.RestProjectSpec;
import me.glindholm.mylyn.build.bamboo.model.RestProjects;
import me.glindholm.mylyn.build.bamboo.model.RestQuarantineConfig;
import me.glindholm.mylyn.build.bamboo.model.RestQuarantineExpiry;
import me.glindholm.mylyn.build.bamboo.model.RestQueuedBuild;
import me.glindholm.mylyn.build.bamboo.model.RestQueuedBuilds;
import me.glindholm.mylyn.build.bamboo.model.RestQueuedDeployments;
import me.glindholm.mylyn.build.bamboo.model.RestQuickFilter;
import me.glindholm.mylyn.build.bamboo.model.RestReports;
import me.glindholm.mylyn.build.bamboo.model.RestRepository;
import me.glindholm.mylyn.build.bamboo.model.RestRepositoryList;
import me.glindholm.mylyn.build.bamboo.model.RestRepositoryMinimal;
import me.glindholm.mylyn.build.bamboo.model.RestResources;
import me.glindholm.mylyn.build.bamboo.model.RestResponsibleUsers;
import me.glindholm.mylyn.build.bamboo.model.RestResultStatus;
import me.glindholm.mylyn.build.bamboo.model.RestResultsResults;
import me.glindholm.mylyn.build.bamboo.model.RestServerNodesInfo;
import me.glindholm.mylyn.build.bamboo.model.RestVariable;
import me.glindholm.mylyn.build.bamboo.model.RestVariableDefinitionContext;
import me.glindholm.mylyn.build.bamboo.model.Result;
import me.glindholm.mylyn.build.bamboo.model.SearchResultsList;
import me.glindholm.mylyn.build.bamboo.model.StartBuildRequest;
import me.glindholm.mylyn.build.bamboo.model.UserBean;

public class BambooTester9 {
	private static final long AGENT_ID_LONG = 1736705;
	private static final String AGENT_ID = AGENT_ID_LONG + "";
	private static final String AUTHOR = "George";
	private static final String DEPLOYMENT_PROJECT_ID = "4194305";
	private static final String TEST_ID = "1998849";
	private static final String PROJECT_KEY = "MYL9";
	private static final String PLAN_KEY_MASTER = "CON9";
	private static final String PLAN_KEY_BRANCH = "CON90";
	private static final String PLAN_PROJECT_COMPOUND_KEY = PROJECT_KEY + "-" + PLAN_KEY_MASTER;
	private static final String JOB_KEY = PLAN_KEY_BRANCH + "-2";
	private static final String ENVIRONEMENT_ID = "5898241";
	static ApiClient apiClient;

	private static BuildApi build;
	private static CoreApi core;
	private static DefaultApi def;
	private static DeploymentApi deployment;
	private static PermissionsApi permissions;
	private static ResourceApi resource;
	private static UserManagementApi user;

	@BeforeAll
	public static void connect() {
		apiClient = new ApiClient();
		final ObjectMapper mapper = apiClient.getObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		apiClient.updateBaseUri("https://192.168.0.120:9683/rest");
		apiClient.setRequestInterceptor(
				authorize -> authorize.header("Authorization", basicAuth("rincewind", "rincewind")));

		build = new BuildApi(apiClient);
		core = new CoreApi(apiClient);
		def = new DefaultApi(apiClient);
		deployment = new DeploymentApi(apiClient);
		permissions = new PermissionsApi(apiClient);
		resource = new ResourceApi(apiClient);
		user = new UserManagementApi(apiClient);

	}

	private static String basicAuth(final String username, final String password) {
		return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
	}

	@BeforeEach
	void setUp(TestInfo testInfo) {
		System.out.printf("==> %s%n", testInfo.getDisplayName());
	}

	@Test
	public void getInfo() throws InterruptedException, ExecutionException, ApiException {

		final RestInfo serverInfo = def.getInfo().get();
		System.out.println(serverInfo);
	}

	@Test
	public void getUsers() throws InterruptedException, ExecutionException, ApiException {
		final UserManagementApi user = new UserManagementApi(apiClient);

		GetUsers200Response users = user.getUsers(null, null, null).get();
		System.out.println(users);

	}

	@Test
	public void getCurrentUser() throws InterruptedException, ExecutionException, ApiException {
		final DefaultApi def = new DefaultApi(apiClient);

		final UserBean myself = def.getCurrentUser().get();
		System.out.println(myself);
	}

	@Test
	public void getAllPlanList() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);

		String expand = String.join(",", "plans", "plans.plan", "plans.plan.actions",
				"plans.plan.stages.stage.plans.plan", "plans.plan.branches.branch.latestResult.labels.label",
				"plans.plan.branches.branch.latestResult.artifacts", "plans.plan.branches.branch.latestResult.labels",
				"plans.plan.branches.branch.latestResult.variables", "plans.plan.branches.branch.latestResult.comments",
				"plans.plan.branches.branch.latestResult.jiraIssues",
				"plans.plan.branches.branch.latestResult.vcsRevisions");
		RestPlans plans = build.getAllPlanList(expand).get();
		System.out.println(plans);

	}

	@Test
	public void getIssueDetails() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);

		RestJiraIssue plans = build.getIssueDetails(PROJECT_KEY, "MYL-2", PLAN_KEY_BRANCH).get();

		System.out.println(plans);
	}

	@Test
	public void getPlan() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);

		final RestPlan plans = build.getPlan(PROJECT_KEY, PLAN_KEY_MASTER, "actions,stages,branches,variableContext")
				.get();
		System.out.println(plans);
	}

	@Test
	public void getPlanArtifactDefinition() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);
		RestArtifactDefinitions res = build.getPlanArtifactDefinition(PROJECT_KEY, PLAN_KEY_MASTER, null, null).get();
		System.out.println(res);
	}

	@Test
	public void getPlanVariables() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);
		List<RestVariable> res = build.getPlanVariables(PROJECT_KEY, PLAN_KEY_MASTER).get();
		System.out.println(res);
	}

	@Test
	public void getPlanVariable() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);
		RestVariable res = build.getPlanVariable(PROJECT_KEY, "TEST", PLAN_KEY_MASTER).get();
		System.out.println(res);
	}

	@Test
	public void getPlanLabels() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);
		RestBuildLabels res = build.getPlanLabels(PROJECT_KEY, PLAN_KEY_BRANCH).get();
		System.out.println(res);
	}

	@Test
	public void getPlanBranches() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);

		RestBranches plans = build.getPlanBranches(PROJECT_KEY, PLAN_KEY_MASTER, null, null, null, null).get();
		System.out.println(plans);
	}

	@Test
	public void getPlanBranch() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);

		RestPlanBranch plans = build.getPlanBranch(PROJECT_KEY, PLAN_KEY_MASTER, "BranchName", "latestResult,master")
				.get();
		System.out.println(plans);
	}

	@Test
	public void getVcsBranches() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);

		Object plans = build.getVcsBranches(PROJECT_KEY, PLAN_KEY_MASTER, null, null, null).get();
		System.out.println(plans);
	}

	@Test
	public void getGroups() throws InterruptedException, ExecutionException, ApiException {
		final UserManagementApi user = new UserManagementApi(apiClient);
		FindAssignedGroups200Response res = user.getGroups(null, null, null).get();
		System.out.println(res);
	}

	@Test
	public void getAgents() throws InterruptedException, ExecutionException, ApiException {
		final DefaultApi def = new DefaultApi(apiClient);
		GetAgents200Response res = def.getAgents().get();
		System.out.println(res);
	}

	@Test
	public void getAgentInformation() throws InterruptedException, ExecutionException, ApiException {
		final DefaultApi def = new DefaultApi(apiClient);
		String expand = "executableJobs";
		RestAgentInformation res = def.getAgentInformation(1736705L, null, expand, null, null).get();
		System.out.println(res);
	}

	@Test
	public void getNodesStatus() throws InterruptedException, ExecutionException, ApiException {
		final DefaultApi def = new DefaultApi(apiClient);
		RestServerNodesInfo res = def.getNodesStatus().get();
		System.out.println(res);
	}

	@Test
	public void getBuildQueue() throws InterruptedException, ExecutionException, ApiException {
		RestQueuedBuilds res = def.getBuildQueue("queuedBuilds.queuedBuild", null, null).get();
		System.out.println(res);
	}

	@Test
	public void startBuild() throws InterruptedException, ExecutionException, ApiException {
		try {
			RestQueuedBuild res = def
					.startBuild(PROJECT_KEY, PLAN_KEY_BRANCH, true, null, null, new StartBuildRequest()).join();
			System.out.println(res);
		} catch (CompletionException e) {
			if (e.getCause() instanceof ApiException) {
				ApiException ex = (ApiException) e.getCause();
				if (ex.getCode() == 400) {
					System.out.println(ex.getMessage());
				} else {
					throw e;
				}
			} else {
				throw new ExecutionException(e);
			}
		}
	}

	@Test
	public void startBuildMaster() throws InterruptedException, ExecutionException, ApiException {
		try {
			RestQueuedBuild res = def
					.startBuild(PROJECT_KEY, PLAN_KEY_MASTER, true, null, null, new StartBuildRequest()).join();
			System.out.println(res);
		} catch (CompletionException e) {
			if (e.getCause() instanceof ApiException) {
				ApiException ex = (ApiException) e.getCause();
				if (ex.getCode() == 400) {
					System.out.println(ex.getMessage());
				} else {
					throw e;
				}
			} else {
				throw new ExecutionException(e);
			}
		}
	}

	@Test
	public void startBuildWithVariable() throws InterruptedException, ExecutionException, ApiException {
		Map<String, String> variables = new HashMap<>();

		variables.put("TEST", "yes");
		variables.put("ULTIMATE", "buld");

		try {
			RestQueuedBuild res = startBuild(PROJECT_KEY, PLAN_KEY_BRANCH, true, null, null, variables).join();
			System.out.println(res);
		} catch (CompletionException e) {
			if (e.getCause() instanceof ApiException) {
				ApiException ex = (ApiException) e.getCause();
				if (ex.getCode() == 400) {
					System.out.println(ex.getMessage());
				} else {
					throw e;
				}
			} else {
				throw new ExecutionException(e);
			}
		}
	}

	private static String formatExceptionMessage(String operationId, int statusCode, String body) {
		if (body == null || body.isEmpty()) {
			body = "[no body]";
		}
		return operationId + " call failed with: " + statusCode + " - " + body;
	}

	private static ApiException getApiException(String operationId, HttpResponse<String> response) {
		String message = formatExceptionMessage(operationId, response.statusCode(), response.body());
		return new ApiException(response.statusCode(), message, response.headers(), response.body());
	}

	private CompletableFuture<RestQueuedBuild> startBuild(String projectKey, String buildKey, Boolean executeAllStages,
			String customRevision, String stage, Map<String, String> variables) throws ApiException {
		try {
			HttpRequest.Builder localVarRequestBuilder = startBuildRequestBuilder(projectKey, buildKey,
					executeAllStages, customRevision, stage, variables);
			HttpClient memberVarHttpClient = apiClient.getHttpClient();
			ObjectMapper memberVarObjectMapper = apiClient.getObjectMapper();

			return memberVarHttpClient.sendAsync(localVarRequestBuilder.build(), HttpResponse.BodyHandlers.ofString())
					.thenComposeAsync(localVarResponse -> {
						if (localVarResponse.statusCode() / 100 != 2) {
							return CompletableFuture.failedFuture(getApiException("startBuild", localVarResponse));
						}
						try {
							return CompletableFuture.completedFuture(memberVarObjectMapper
									.readValue(localVarResponse.body(), new TypeReference<RestQueuedBuild>() {
									}));
						} catch (IOException e) {
							return CompletableFuture.failedFuture(new ApiException(e));
						}
					});
		} catch (ApiException e) {
			return CompletableFuture.failedFuture(e);
		}
	}

	private HttpRequest.Builder startBuildRequestBuilder(String projectKey, String buildKey, Boolean executeAllStages,
			String customRevision, String stage, Map<String, String> variables) throws ApiException {
		// verify the required parameter 'projectKey' is set
		if (projectKey == null) {
			throw new ApiException(400, "Missing the required parameter 'projectKey' when calling startBuild");
		}
		// verify the required parameter 'buildKey' is set
		if (buildKey == null) {
			throw new ApiException(400, "Missing the required parameter 'buildKey' when calling startBuild");
		}

		HttpRequest.Builder localVarRequestBuilder = HttpRequest.newBuilder();

		String localVarPath = "/api/latest/queue/{projectKey}-{buildKey}"
				.replace("{projectKey}", ApiClient.urlEncode(projectKey.toString()))
				.replace("{buildKey}", ApiClient.urlEncode(buildKey.toString()));

		List<Pair> localVarQueryParams = new ArrayList<>();
		localVarQueryParams.addAll(ApiClient.parameterToPairs("executeAllStages", executeAllStages));
		localVarQueryParams.addAll(ApiClient.parameterToPairs("customRevision", customRevision));
		localVarQueryParams.addAll(ApiClient.parameterToPairs("stage", stage));

		for (Entry<String, String> var : variables.entrySet()) {
			localVarQueryParams.addAll(ApiClient.parameterToPairs("bamboo.variable." + var.getKey(), var.getValue()));
		}

		ObjectMapper memberVarObjectMapper = apiClient.getObjectMapper();
		Duration memberVarReadTimeout = apiClient.getReadTimeout();
		String memberVarBaseUri = apiClient.getBaseUri();
		Consumer<Builder> memberVarInterceptor = apiClient.getRequestInterceptor();

		if (!localVarQueryParams.isEmpty()) {
			StringJoiner queryJoiner = new StringJoiner("&");
			localVarQueryParams.forEach(p -> queryJoiner.add(p.getName() + '=' + p.getValue()));
			localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath + '?' + queryJoiner.toString()));
		} else {
			localVarRequestBuilder.uri(URI.create(memberVarBaseUri + localVarPath));
		}

		localVarRequestBuilder.header("Content-Type", "application/json");
		localVarRequestBuilder.header("Accept", "application/json");

		try {
			byte[] localVarPostBody = memberVarObjectMapper.writeValueAsBytes(new StartBuildRequest());
			localVarRequestBuilder.method("POST", HttpRequest.BodyPublishers.ofByteArray(localVarPostBody));
		} catch (IOException e) {
			throw new ApiException(e);
		}

		if (memberVarReadTimeout != null) {
			localVarRequestBuilder.timeout(memberVarReadTimeout);
		}
		if (memberVarInterceptor != null) {
			memberVarInterceptor.accept(localVarRequestBuilder);
		}
		return localVarRequestBuilder;
	}

	@Test
	public void getResponsibleForPlanResult() throws InterruptedException, ExecutionException, ApiException {
		RestResponsibleUsers res = def.getResponsibleForPlanResult("MYL9-CON9").get();
		System.out.println(res);
	}

	@Test
	public void createOrUpdateVariable() throws InterruptedException, ExecutionException, ApiException {
		RestVariable var = new RestVariable();
		var.setName("WIZZARD");
		var.setValue("Rincewind");
		RestVariableDefinitionContext res = def.createOrUpdateVariable(PROJECT_KEY, var).get();
		System.out.println(res);
	}

	@Test
	public void addPlanVariable() throws InterruptedException, ExecutionException, ApiException {
		RestVariable var = new RestVariable();
		var.setName("EXPORT");
		var.setValue("Yes");
		try {
			RestVariable res = build.addPlanVariable(PROJECT_KEY, PLAN_KEY_BRANCH, var).join();
			System.out.println(res);
		} catch (CompletionException e) {
			if (e.getCause() instanceof ApiException) {
				ApiException ex = (ApiException) e.getCause();
				if (ex.getCode() == 400) {
					System.out.println(ex.getMessage());
				} else {
					throw e;
				}
			} else {
				throw new ExecutionException(e);
			}
		}
	}

	@Test
	public void editPlanVariable() throws InterruptedException, ExecutionException, ApiException {
		final BuildApi build = new BuildApi(apiClient);

		RestVariable var = new RestVariable();
		var.setName("EXPORT");
		var.setValue("No");
		RestVariable res = build.editPlanVariable(PROJECT_KEY, "EXPORT", PLAN_KEY_BRANCH, var).join();
		System.out.println(res);
	}

	@Test
	public void addComment() throws ApiException {
		CreateCommentRequest newComment = new CreateCommentRequest();
		newComment.setContent("This is a REST comment");
		Object res = def.addBuildComment(PROJECT_KEY, PLAN_KEY_BRANCH, "6", newComment).join();
		System.out.println(res);
	}

	@Test
	public void addLabel() throws ApiException {
		RestBuildLabel newLabel = new RestBuildLabel();
		newLabel.setName("API Label");
		Object res = def.addBuildLabel(PROJECT_KEY, PLAN_KEY_BRANCH, "6", newLabel).join();
		System.out.println(res);
	}

	@Test
	public void addPlanLabel() throws ApiException {
		RestPlanLabel newLabel = new RestPlanLabel();
		newLabel.setName("PlanLabel");
		build.addPlanLabel(PROJECT_KEY, PLAN_KEY_BRANCH, newLabel).join();
	}

	@Test
	public void getQuarantineSettings() throws InterruptedException, ExecutionException, ApiException {
		RestQuarantineConfig res = def.getQuarantineSettings().get();
		System.out.println(res);
	}

	@Test
	public void unleashTest() throws InterruptedException, ExecutionException, ApiException {
		try {
			build.unleashTest(PROJECT_KEY, PLAN_KEY_MASTER, TEST_ID).get();
		} catch (ExecutionException e) {
			if (e.getCause() instanceof ApiException) {
				ApiException ex = (ApiException) e.getCause();
				if (ex.getCode() == 400) {
					System.out.println(ex.getMessage());
				} else {
					throw e;
				}
			}
		}
	}

	@Test
	public void quarantineTest() throws InterruptedException, ExecutionException, ApiException {
		try {
			RestQuarantineExpiry expire = new RestQuarantineExpiry();
			expire.setExpiryDuration(20);
			build.quarantineTest(PROJECT_KEY, PLAN_KEY_MASTER, TEST_ID, expire).get();
		} catch (ExecutionException e) {
			if (e.getCause() instanceof ApiException) {
				ApiException ex = (ApiException) e.getCause();
				if (ex.getCode() == 400) {
					System.out.println(ex.getMessage());
				} else {
					throw e;
				}
			}
		}
	}

	@Test
	@Disabled("Need spec")
	public void addAssignedRepository1() throws ApiException {
		RestIdContainer container = new RestIdContainer();
		RestRepositoryMinimal res = def.addAssignedRepository1(PROJECT_KEY, container).join();
		System.out.println(res);
	}

	@Test
	public void getPaginatedProjectRepositories() throws InterruptedException, ExecutionException, ApiException {
		GetPaginatedProjectRepositories200Response res = def
				.getPaginatedProjectRepositories(PROJECT_KEY, null, null, null).get();
		System.out.println(res);
	}

	@Test
	@Disabled("Need spec")
	public void listAssignedRepositories() throws InterruptedException, ExecutionException, ApiException {
		List<RestRepository> res = def.listAssignedRepositories(DEPLOYMENT_PROJECT_ID).get();
		System.out.println(res);
	}

	@Test
	public void searchAvailableRepositories_1() throws ApiException, InterruptedException, ExecutionException {
		RestRepositoryList res = def.searchAvailableRepositories1(PROJECT_KEY, null).get();
		System.out.println(res);
	}

	@Test
	@Disabled("Need repository id")
	public void enableAllRepositoriesAccess() throws ApiException {
		RestEnableContainer container = new RestEnableContainer();
		Void res = def.enableAllRepositoriesAccess(PROJECT_KEY, 0L, container).join();
		System.out.println(res);
	}

	@Test
	public void createProject() throws ApiException {
		RestProject project = new RestProject();
		project.setName("API Project");
		project.setKey("API1");
		project.setDescription("API Created project");
		try {
			RestProject res = def.createProject(project).join();
			System.out.println(res);
		} catch (CompletionException e) {
			if (e.getCause() instanceof ApiException) {
				ApiException ex = (ApiException) e.getCause();
				if (ex.getCode() == 400) {
					System.out.println(ex.getMessage());
				} else {
					throw e;
				}
			}
		}
	}

	@Test
	public void getProjectVariables() throws ApiException {
		List<RestVariable> res = def.getProjectVariables(PROJECT_KEY).join();
		System.out.println(res);
	}

	@Test
	@Disabled("host.docker.internal missing")
	public void getAuditLogConfiguration() throws InterruptedException, ExecutionException, ApiException {
		RestAuditLogConfiguration res = def.getAuditLogConfiguration().get();
		System.out.println(res);
	}

	@Test
	public void getRestElasticConfiguration() throws InterruptedException, ExecutionException, ApiException {
		RestElasticConfiguration res = def.getRestElasticConfiguration().get();
		System.out.println(res);
	}

	@Test
	public void getActiveFilters() throws InterruptedException, ExecutionException, ApiException {
		List<RestQuickFilter> res = def.getActiveFilters().get();
		System.out.println(res);
	}

	@Test
	public void getDefDeploymentProject() throws InterruptedException, ExecutionException, ApiException {
		List<DashboardProjectWithEnvironmentStatus> res = def.getDeploymentProject(DEPLOYMENT_PROJECT_ID).get();
		System.out.println(res);
	}

	@Test
	public void getDefDeploymentProject1() throws InterruptedException, ExecutionException, ApiException {
		RestDeploymentProject res = def.getDeploymentProject1(DEPLOYMENT_PROJECT_ID).get();
		System.out.println(res);
	}

	@Test
	public void getUserTokens() throws InterruptedException, ExecutionException, ApiException {
		GetUserAccessTokens200Response res = resource.getUserTokens(null, null).get();
		System.out.println(res);
	}

	@Test
	public void getAvailableGroups2() throws InterruptedException, ExecutionException, ApiException {
		FindAssignedGroups200Response res = permissions.getAvailableGroups2(null, null, null, null).get();
		System.out.println(res);
	}

	@Test
	public void listGroupPermissions2() throws InterruptedException, ExecutionException, ApiException {
		ListGroupPermissions6200Response res = permissions.listGroupPermissions2(null, null, null, null).get();
		System.out.println(res);
	}

	@Test
	public void listRolePermissions2() throws InterruptedException, ExecutionException, ApiException {
		ListRolePermissions6200Response res = permissions.listRolePermissions2(null, null, null).get();
		System.out.println(res);
	}

	@Test
	public void getAgentAssignments() throws InterruptedException, ExecutionException, ApiException {
		List<RestDedicatedAgent> res = resource.getAgentAssignments("AGENT", null).get();
		System.out.println(res);
	}

	@Test
	public void getUserRepositoryAliases() throws InterruptedException, ExecutionException, ApiException {
		FindUnassignedUserRepositoryAliases200Response res = user.getUserRepositoryAliases("rincewind", null, null)
				.get();
		System.out.println(res);
	}

	@Test
	public void getLatestBuildResultsForProject() throws InterruptedException, ExecutionException, ApiException {
		String expand = String.join(",", "results.result.stages.stage");
		RestResultsResults res = def.getLatestBuildResultsForProject(PROJECT_KEY, null, null, expand, null, null, null,
				null, null, null, null).get();

		System.out.println(res);
	}

	@Test
	public void getBuildAlias() throws InterruptedException, ExecutionException, ApiException {
		String expand = String.join(",",
//        		"result",
//        		"changes.change.files",
//        		"comments.comment",
//        		"labels.label",
//        		"variables",
//        		"artifacts.artifact",
				"testResults", "artifacts", "comments", "labels", "logFiles", "changes.change.files", "metadata",
				"testResults", "metadata", "jiraIssues", "vcsRevisions", "variables",
				"stages.stage.results.result.artifacts", "stages.stage.results.result.variables",
				"stages.stage.results.result.changes", "stages.stage.results.result.comments",
				"stages.stage.results.result.testResults.allTests.testResult.errors.error");
		Result res = def.getBuildAlias(PROJECT_KEY, PLAN_KEY_BRANCH, "34", expand).get();
		System.out.println(res);
	}

	@Test
	public void getLatestBuildResults() throws InterruptedException, ExecutionException, ApiException {
		String expand = String.join(",", "results.result.artifacts", "results.result.variables",
				"results.result.stages");
		final RestResultsResults res = def
				.getLatestBuildResults(null, null, expand, null, null, null, null, null, null, null).get();
		System.out.println(res);
	}

	@Test
	public void getLatestBuildResults2() throws InterruptedException, ExecutionException, ApiException {
		String expand = String.join(",", "result", "changes.change.files", "comments.comment", "labels.label",
				"variables", "artifacts.artifact", "stages.stage.results.result.artifacts.artifact");
		RestResultsResults res = def.getLatestBuildResults(null, null, expand, null, null, null, null, null, null, null)
				.get();

		System.out.println(res);
	}

	@Test
	public void getBuild() throws InterruptedException, ExecutionException, ApiException {
		String expand = String.join(",", "changes", "metadata", "plan", "master", "vcsRevisions", "artifacts",
				"comments", "labels", "jiraIssues", "variables", "stages");
		final Result res = def.getBuild(PROJECT_KEY, PLAN_KEY_MASTER, "2", expand, null).get();
		System.out.println(res);
	}

	@Test
	public void getBuildTestResults() throws InterruptedException, ExecutionException, ApiException {
		String expand = String.join(",", "testResults.allTests.testResult.errors");
		final Result history = def.getBuild(PROJECT_KEY, PLAN_KEY_MASTER, "25", expand, null).get();
		System.out.println(history);
	}

	@Test
	public void getBuildHistory() throws InterruptedException, ExecutionException, ApiException {
		String expand = "results.result";
		final RestResultsResults res = def.getBuildHistory(PROJECT_KEY, PLAN_KEY_BRANCH, null, null, expand, null, null,
				null, null, null, null, null).get();
		System.out.println(res);
	}

	@Test
	public void getBuildHistoryRun() throws InterruptedException, ExecutionException, ApiException {
		String label = null;
		String buildState = null;
		String favourite = null;
		String expand = String.join(",", "changes", "metadata", "plan", "master", "vcsRevisions", "artifacts",
				"comments", "labels", "jiraIssues", "variables", "stages");
		String issueKey = null;
		String lifeCycleState = null;
		RestResultsResults res = def.getBuildHistory(PROJECT_KEY, JOB_KEY, true, true, expand, issueKey, null, null,
				label, buildState, favourite, lifeCycleState).get();
		System.out.println(res);
	}

	@Test
	public void getAllServices() throws InterruptedException, ExecutionException, ApiException {
		final RestResources history = def.getAllServices().get();
		System.out.println(history);
	}

	@Test
	public void getProjects() throws InterruptedException, ExecutionException, ApiException {
		final RestProjects res = def.getProjects(null, true).get();
		System.out.println(res);
	}

	public void checkRestElasticConfiguration() {

	}

	@Test
	public void searchPlans() throws InterruptedException, ExecutionException, ApiException {
		SearchResultsList res = def.searchPlans(null, "CON", null, null, null, null).get();
		System.out.println(res);
	}

	@Test
	public void getAvailableReports() throws InterruptedException, ExecutionException, ApiException {
		RestReports res = def.getAvailableReports(null, null, null).get();
		System.out.println(res);
	}

	@Test
	public void getResultStatus() throws InterruptedException, ExecutionException, ApiException {
		try {
			String expand = String.join(",", "stages.stage");
			RestResultStatus res = def.getResultStatus(PROJECT_KEY, PLAN_KEY_BRANCH, "38", expand).get();
			System.out.println(res);
		} catch (ExecutionException e) {
			if (e.getCause() instanceof ApiException) {
				ApiException ex = (ApiException) e.getCause();
				if (ex.getCode() == 404) {
					System.out.println(ex.getMessage());
				} else {
					throw e;
				}
			} else {
				throw e;
			}
		}
	}

	@Test
	public void getResultStatusMaster() throws InterruptedException, ApiException {
		try {
			String expand = String.join(",", "stages.stage");
			RestResultStatus res = def.getResultStatus(PROJECT_KEY, PLAN_KEY_MASTER, "9", expand).get();
			System.out.println(res);
		} catch (ExecutionException e) {
			System.out.println(e.getMessage());
		} catch (CompletionException e) {
			if (e.getCause() instanceof ApiException) {
				ApiException ex = (ApiException) e.getCause();
				if (ex.getCode() == 404) {
					System.out.println(ex.getResponseBody());
				} else {
					throw e;
				}
			}
		}
	}

	@Test
	public void searchAuthors() throws InterruptedException, ExecutionException, ApiException {
		SearchResultsList res = def.searchAuthors(AUTHOR, null, null, null).get();
		System.out.println(res);
	}

	@Test
	public void getPlanDirectory() throws InterruptedException, ExecutionException, ApiException {
		DirectoryInformationResponse res = build.getPlanDirectory(PLAN_PROJECT_COMPOUND_KEY).get();
		System.out.println(res);
	}

	@Test
	public void reports() throws InterruptedException, ExecutionException, ApiException {
		RestReports res = def.getAvailableReports(null, "reports", null).get();
		System.out.println(res);
	}

	@Test
	public void getImageUrl() throws InterruptedException, ExecutionException, ApiException {
		RestChart res = def
				.getImageUrl(PLAN_PROJECT_COMPOUND_KEY, "com.atlassian.bamboo.plugin.system.reports:agentUtilization")
				.get();
		System.out.println(res);
	}

	@Test
	public void getEnvironmentsExecutableByAgent() throws InterruptedException, ExecutionException, ApiException {
		GetEnvironmentsExecutableByAgent200Response res = core
				.getEnvironmentsExecutableByAgent(AGENT_ID, null, null, null).get();
		System.out.println(res);
	}

	@Test
	public void findAssignedAgentsByJob() throws InterruptedException, ExecutionException, ApiException {
		List<RestAgentAssignmentExecutorDetails> res = core.findAssignedAgentsByJob("MYL9-CON9-JOB1-25").get();
		System.out.println(res);
	}

	@Test
	public void findPossibleAgentsForJob() throws InterruptedException, ExecutionException, ApiException {
		RestAgentAssignmentExecutorDetailsList res = core
				.findPossibleAgentsForJob("MYL9-CON9-JOB1-25", null, null, null).get();
		System.out.println(res);
	}

	@Test
	public void getAllEnvironmentVariables() throws InterruptedException, ExecutionException, ApiException {
		List<RestVariable> res = core.getAllEnvironmentVariables(ENVIRONEMENT_ID).get();
		System.out.println(res);

	}

	@Test
	public void exportProjectSpecs() throws InterruptedException, ExecutionException, ApiException {
		RestProjectSpec res = def.exportProjectSpecs(PROJECT_KEY, "me.glindholm", "YAML").get();
		System.out.println(res);

	}

	@Test
	public void exportPlanSpec() throws InterruptedException, ExecutionException, ApiException {
		RestPlanSpec res = build.exportPlanSpec(PROJECT_KEY, PLAN_KEY_MASTER, "me.glindholm", "JAVA").get();
		System.out.println(res);
	}

	@Test
	public void exportDeploymentSpec() throws InterruptedException, ExecutionException, ApiException {
		RestDeploymentSpec res = def.exportDeploymentSpec(DEPLOYMENT_PROJECT_ID, "me.glindholm", "YAML").get();
		System.out.println(res);
	}

	@Test
	public void findPossibleAgentsForEnvironment() throws InterruptedException, ExecutionException, ApiException {
		RestAgentAssignmentExecutorDetailsList res = core.findPossibleAgentsForEnvironment(ENVIRONEMENT_ID, null, null, null).get();
		System.out.println(res);
	}

	@Test
	public void getAgentStatus() throws InterruptedException, ExecutionException, ApiException {
		RestAgentStatus res = def.getAgentStatus(AGENT_ID_LONG).get();
		System.out.println(res);
	}

	@Test
	public void getBuildQueueDeployment() throws InterruptedException, ExecutionException, ApiException {
		String expand = "queuedDeployments";
		RestQueuedDeployments res = deployment.getBuildQueueDeployment(expand).get();
		System.out.println(res);
	}
}