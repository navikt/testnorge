$image_base = "docker.adeo.no:5000/registre/"

$g = [guid]::NewGuid()
$v = [string]$g
$v = $v.Replace("-", "")
$tag = $v.substring(0, 10)

$nais_original = Get-Content -Raw -Path .\nais.yaml

$nais_original -match "name: (?<name>.*)\r"
$application = $matches['name']

$image = $image_base + $application + ":" + $tag

docker build . -t $image
docker push $image

$nais_updated = $nais_original -replace "$image_base.+?`"", "$image`""
$nais_updated | Out-File -FilePath .\nais.yaml -Encoding utf8 -NoNewLine

kubectl apply -f nais.yaml