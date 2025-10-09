Enkelte applikasjoner trenger OpenSearch kjørende lokalt i Docker:
```
> docker run -p 9200:9200 -p 9600:9600 -e "discovery.type=single-node" -e "plugins.security.disabled=true" -e "OPENSEARCH_INITIAL_ADMIN_PASSWORD=YLAgOm}rz#o6#Aq" --name opensearch -d opensearchproject/opensearch:latest
```
Le 6#Aq` (tilfeldig [generert](https://www.strongpasswordgenerator.org/), men må være "sterkt" ellers vil ikke OpenSearch starte).