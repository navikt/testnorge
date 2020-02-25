naiseratorApplicationPipeline {

    // Navnet på applikasjonen.
    applicationName = "testnorge-token-provider"

    // Navnet på Azure AD-teamet som eier denne applikasjonen. Default er registre.
    team = "orkestrator"

    // Hvilken Javaversjon som kodebasen er lagd for. Gyldige verdier er 7, 8 og 11.
    javaVersion = "8"

    // Hvilket Kubernetes namespace applikasjonen skal deployes til. Dersom det ikke er strengt
    // behov for dette, så ikke bruk dette feltet.
    namespace = "default"

    // Hvilke namespaces som applikasjonen skal deployes til i tillegg til det som er spesifisert
    // i namespace-attributtet ovenfor. Dersom det ikke strengt behov for dette, så ikke bruk
    // dette feltet. Eksempelet nedenfor vil deploye applikasjonen til henholdsvis q0- og q1-
    // namespacene.
    branchDeployments = [[branch: "testnorge_token_provider_t6", namespace: "t6"]]

    // Hvorvidt denne applikasjonen kun skal eksistere i dev-fss og IKKE i prod-fss.
    isTestApplication = true
}