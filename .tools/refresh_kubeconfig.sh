#!/usr/bin/env bash

### Husk å oppdatere kubectl dersom docker blir oppdatert ved å kjøre med -d
docker_update=false
KUBECONFIGS_PATH="$HOME/kubeconfigs"

while getopts "d?:k:" opt; do
    case "$opt" in
        d) docker_update=true ;;
	k) KUBECONFIGS_PATH=$2 ;;
    esac
done

## Lager riktig KUBECONFIG system-variabel. 
## Denne bør også settes i ~/.bash_profile eller ~/.zshrc ettersom hvilket shell en kjører
export KUBECONFIG="$KUBECONFIGS_PATH/config"

if [ $docker_update = true ]
then
    echo "Updating kubectl..."
    sudo curl -L "https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/darwin/amd64/kubectl" -o /usr/local/bin/kubectl && chmod +x /usr/local/bin/kubectl
fi

echo "Updating kubeconfigs in $KUBECONFIGS_PATH..."
cd $KUBECONFIGS_PATH
git reset --hard
git pull && git checkout naisdevice
cd ..

echo "Logging in..."
kubectl config use-context dev-fss
open https://microsoft.com/devicelogin
kubectl get pods

