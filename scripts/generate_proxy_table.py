import os
import re
import pathlib

REPO_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
PROXIES_DIR = os.path.join(REPO_ROOT, 'proxies')
INDEX_FILE = os.path.join(REPO_ROOT, 'docs/modules/ROOT/pages/index.adoc')
BEGIN_MARK = '// BEGIN GENERATED PROXY TABLE'
END_MARK = '// END GENERATED PROXY TABLE'

SKIP_APPS = {'logging'}
SKIP_PROXIES = {'aareg-synt-services-proxy'}
URL_PATTERN = re.compile(r'https?://[a-zA-Z0-9_.\-/:]+')
HOST_PAT = re.compile(r'https?://([^/]+)')
RELEVANT_HOST_SUFFIXES = (
    '.intern.dev.nav.no',
    '.dev-fss-pub.nais.io',
    '.intern.nav.no',
    '.svc.nais.local',
    '.svc.cluster.local',
    '.dev.adeo.no',
    '.nav.no'
)

WORKFLOWS_DIR = os.path.join(REPO_ROOT, '.github', 'workflows')

def read_file(path):
    try:
        with open(path, 'r', encoding='utf-8') as f:
            return f.read()
    except FileNotFoundError:
        return ''

def extract_ingresses(config_text):
    ingresses = []
    if not config_text:
        return ingresses
    lines = config_text.splitlines()
    for i,l in enumerate(lines):
        if re.match(r'^\s*ingresses:\s*$', l):
            j = i+1
            while j < len(lines) and re.match(r'^\s*-\s+"?https?://', lines[j]):
                url = lines[j].strip().lstrip('-').strip().strip('"')
                ingresses.append(url)
                j += 1
    return ingresses

def extract_tenants(config_text, app_text):
    tenants = set()
    for t in re.findall(r'tenant:\s*([a-zA-Z0-9_.-]+)', config_text):
        tenants.add(t.strip())
    for t in re.findall(r'tenant:\s*([a-zA-Z0-9_.-]+)', app_text):
        tenants.add(t.strip())
    return tenants

def extract_consumers_application(app_text):
    if not app_text:
        return []
    lines = app_text.splitlines()
    in_block = False
    current = None
    consumers = []
    for line in lines:
        if re.match(r'^consumers:\s*$', line):
            in_block = True
            continue
        if in_block:
            if re.match(r'^\S', line):
                break
            if re.match(r'^  [^\s].*:$', line):
                if current:
                    consumers.append(current)
                key = line.strip().rstrip(':')
                current = {'key': key, 'name': None, 'url': None}
            elif current and 'url:' in line:
                current['url'] = line.split('url:',1)[1].strip()
            elif current and re.search(r'\bname:', line):
                current['name'] = line.split('name:',1)[1].strip()
    if current:
        consumers.append(current)
    out = []
    for c in consumers:
        name = (c['name'] or c['key']).strip()
        if name in SKIP_APPS:
            continue
        url = (c['url'] or '-').strip()
        out.append((name, url))
    return out

def extract_consumers_outbound(config_text):
    if not config_text:
        return []
    lines = config_text.splitlines()
    consumers = []
    in_access = in_outbound = in_rules = False
    current = None
    for line in lines:
        # track nesting
        if re.match(r'^\s*accessPolicy:\s*$', line):
            in_access = True
            continue
        if in_access and re.match(r'^\s*outbound:\s*$', line):
            in_outbound = True
            continue
        if in_outbound and re.match(r'^\s*rules:\s*$', line):
            in_rules = True
            continue
        if in_rules:
            if re.match(r'^\s*-\s+application:', line):
                if current:
                    consumers.append(current)
                app = line.split('application:',1)[1].strip()
                current = {'name': app, 'url': None, 'cluster': None}
                continue
            if current and 'cluster:' in line:
                current['cluster'] = line.split('cluster:',1)[1].strip()
            if current and 'namespace:' in line:
                # we don't currently store namespace, could be added later
                pass
            # end of outbound rules block heuristic
            if re.match(r'^\s*[a-zA-Z0-9_-]+:', line) and not re.match(r'^\s*-', line):
                # new top-level key
                break
    if current:
        consumers.append(current)
    out = []
    for c in consumers:
        name = c['name']
        if name in SKIP_APPS:
            continue
        out.append((name, c.get('cluster') or '-'))
    return out

def guess_cross_tenant(tenants):
    return 'true' if ('nav.no' in tenants and 'trygdeetaten.no' in tenants) else 'false'

def guess_proxy_cluster(config_text):
    # derive from inbound rule clusters if present; fallback dev-gcp
    clusters = re.findall(r'cluster:\s*(dev-[a-z]+)', config_text)
    if clusters:
        # choose most common
        from collections import Counter
        return Counter(clusters).most_common(1)[0][0]
    return 'dev-gcp'

def build_table(rows):
    header = '[cols="1,1,2,2,2,1,1", options="header"]\n|===\n|Proxy |Cluster |Ingress |Downstream apper |Downstream URLer |Cross-tenant |Status'
    lines = [header]
    for r in rows:
        lines.append(
            f"|link:https://github.com/navikt/testnorge/tree/master/proxies/{r['dir']}[{r['dir']}] |{r['cluster']} |{r['ingress']} |{r['consumer_names']} |{r['consumer_urls']} |{r['cross']} |{r['status']}"
        )
    lines.append('|===')
    return '\n'.join(lines)

def merge_consumers(app_consumers, outbound_consumers):
    # app_consumers: list of (name,url); outbound_consumers: list of (name,cluster)
    merged = {}
    for name,url in app_consumers:
        merged.setdefault(name, {'url': set(), 'cluster': set()})['url'].add(url)
    for name,cluster in outbound_consumers:
        merged.setdefault(name, {'url': set(), 'cluster': set()})['cluster'].add(cluster)
    names = []
    urls = []
    for name in sorted(merged):
        names.append(name)
        url_part = []
        if merged[name]['url']:
            url_part.extend(sorted(merged[name]['url']))
        if merged[name]['cluster'] and not merged[name]['url']:
            # if only cluster known, represent cluster marker
            url_part.extend(sorted(merged[name]['cluster']))
        urls.append('; '.join(url_part) if url_part else '-')
    return ', '.join(names) if names else '-', ', '.join(urls) if urls else '-'

def fallback_discover(entry_dir, existing_urls):
    found = set()
    # scan yml/yaml and java/kotlin files
    for root, _, files in os.walk(entry_dir):
        for fname in files:
            if not fname.endswith(('.yml', '.yaml', '.java', '.kt', '.properties')):
                continue
            path = os.path.join(root, fname)
            try:
                with open(path, 'r', encoding='utf-8') as f:
                    text = f.read()
            except Exception:
                continue
            for m in URL_PATTERN.finditer(text):
                url = m.group(0)
                if url in existing_urls:
                    continue
                host_match = HOST_PAT.match(url)
                if not host_match:
                    continue
                host = host_match.group(1)
                if any(host.endswith(suf) for suf in RELEVANT_HOST_SUFFIXES):
                    # ignore likely self references (proxy ingress pattern containing entry dir name)
                    if entry_dir.replace('_','-') in host:
                        continue
                    found.add(url)
    consumers = []
    for url in sorted(found):
        host = HOST_PAT.match(url).group(1)
        name = host.split('.')[0]
        if name in SKIP_APPS:
            continue
        consumers.append((f'detektert-{name}', url))
    return consumers

def sanitize_placeholder(text: str) -> str:
    return text.replace('{miljoe}', '\\{miljoe\\}')

def get_cluster_from_workflow(proxy_dir_name: str) -> str | None:
    # workflow filer følger mønster proxy.<navn>.yml
    candidate = os.path.join(WORKFLOWS_DIR, f'proxy.{proxy_dir_name}.yml')
    if not os.path.isfile(candidate):
        return None
    try:
        text = read_file(candidate)
        # Finn første forekomst av cluster: "value" inni with-blokk
        m = re.search(r'with:\s*(?:\n|.)*?cluster:\s*"([a-z0-9\-]+)"', text)
        if m:
            return m.group(1)
    except Exception:
        return None
    return None

def main():
    rows = []
    if not os.path.isdir(PROXIES_DIR):
        raise SystemExit('Missing proxies dir')
    for entry in sorted(os.listdir(PROXIES_DIR)):
        p = os.path.join(PROXIES_DIR, entry)
        if not os.path.isdir(p):
            continue
        if entry in SKIP_PROXIES:
            continue
        config_path = os.path.join(p, 'config.yml')
        app_path = os.path.join(p, 'src/main/resources/application.yml')
        config_text = read_file(config_path)
        app_text = read_file(app_path)
        ingresses = extract_ingresses(config_text)
        ingress = ingresses[0] if ingresses else '-'
        app_consumers = extract_consumers_application(app_text)
        outbound_consumers = extract_consumers_outbound(config_text)
        # Fallback discovery
        if not app_consumers and not outbound_consumers:
            fallback = fallback_discover(p, set())
            app_consumers = fallback
        consumer_names, consumer_urls = merge_consumers(app_consumers, outbound_consumers)
        consumer_names = sanitize_placeholder(consumer_names)
        consumer_urls = sanitize_placeholder(consumer_urls)
        # Spesialhåndtering for texas-proxy: vis kort forklaring istedenfor full liste
        if entry == 'texas-proxy':
            consumer_names = 'samler alle andre proxyer (WIP)'
            consumer_urls = '-'
        tenants = extract_tenants(config_text, app_text)
        cross = guess_cross_tenant(tenants)
        # Hent cluster fra workflow, fallback til tidligere heuristikk
        workflow_cluster = get_cluster_from_workflow(entry)
        if workflow_cluster:
            cluster = workflow_cluster
        else:
            cluster = guess_proxy_cluster(config_text)
        rows.append({
            'dir': entry,
            'cluster': cluster,
            'ingress': ingress,
            'consumer_names': consumer_names,
            'consumer_urls': consumer_urls,
            'cross': cross,
            'status': 'aktiv'
        })
    table = build_table(rows)
    index_text = read_file(INDEX_FILE)
    if BEGIN_MARK not in index_text or END_MARK not in index_text:
        raise SystemExit('Markers not found in index.adoc')
    pattern = re.compile(re.escape(BEGIN_MARK) + r'.*?' + re.escape(END_MARK), re.DOTALL)
    replacement = BEGIN_MARK + '\n' + table + '\n' + END_MARK
    new_text = pattern.sub(replacement, index_text)
    if new_text != index_text:
        with open(INDEX_FILE, 'w', encoding='utf-8') as f:
            f.write(new_text)

if __name__ == '__main__':
    main()
