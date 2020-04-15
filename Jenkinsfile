naiseratorApplicationPipeline {
    applicationName = "dolly"
    team = "registre"
    javaVersion = "1.8"
    namespace = "default"
    branchDeployments = [
        [branch: "dolly-test-u2", namespace: "u2"],
        [branch: "feature/dolly-test-t1", namespace: "t1"],
        [branch: "dolly-t2", namespace: "t2"]
    ]
    additionalDeployments = [
        [namespace: "t1", environment: "t1"],
        [namespace: "t2", environment: "t2"]
    ]
}