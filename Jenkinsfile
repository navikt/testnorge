naiseratorApplicationPipeline {
    applicationName = "dolly-backend"
    team = "registre"
    javaVersion = "1.8"
    namespace = "default"
    branchDeployments = [
        [branch: "dolly-backend-test-u2", namespace: "u2"],
        [branch: "dolly-backend-local-t1", namespace: "t1"],
        [branch: "feature/REG-5715-dolly-2.0-backend", namespace: "t2"]
    ]
    additionalDeployments = [
        [namespace: "t1", environment: "t1"]
    ]
    isTestApplication = true
}