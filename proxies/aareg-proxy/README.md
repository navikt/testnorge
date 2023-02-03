En proxy for lesetilgang til [aareg-services](https://github.com/navikt/aareg-services), og skrivetilgang til [aareg-vedlikehold](https://github.com/navikt/aareg-vedlikehold).

For å kjøre lokalt (Spring profile _local_) så må en secret hentes fra pod og legges inn i `application-local.yaml` for `AZURE_TRYGDEETATEN_APP_CLIENT_SECRET`:
```powershell
POWERSHELL> $mypod = &{kubectl get pods -l app=testnav-aareg-proxy --no-headers -o custom-columns=":metadata.name"}; `
            Write-Output "AZURE_TRYGDEETATEN_APP_CLIENT_SECRET=$(kubectl exec $mypod -- printenv AZURE_APP_CLIENT_SECRET)"
```
Ellers gjelder vanlig rutine med [Vault](https://vault.adeo.no/) token i `-Dspring.cloud.vault.token=<token>`.

En del ekstra actuators aktiveres i Spring profile _local_, ref. http://localhost:8080/actuator, se. f.eks. http://localhost:8080/actuator/gateway/routes.