(function() {
  function debounce(fn, wait) {
    let t;
    return function() {
      clearTimeout(t);
      const a = arguments, c = this;
      t = setTimeout(() => fn.apply(c, a), wait);
    };
  }

  function escapeHtml(str) {
    return str.replace(/[&<>"]/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', '\'': '&#39;' }[c]));
  }

  function levenshtein(a, b) {
    if (a === b) return 0;
    const al = a.length, bl = b.length;
    if (!al) return bl;
    if (!bl) return al;
    const v0 = new Array(bl + 1), v1 = new Array(bl + 1);
    for (let i = 0; i <= bl; i++) v0[i] = i;
    for (let i = 0; i < al; i++) {
      v1[0] = i + 1;
      for (let j = 0; j < bl; j++) {
        const cost = a.charCodeAt(i) === b.charCodeAt(j) ? 0 : 1;
        let m = v0[j + 1] + 1;
        const ins = v1[j] + 1;
        if (ins < m) m = ins;
        const sub = v0[j] + cost;
        if (sub < m) m = sub;
        v1[j + 1] = m;
      }
      for (let j = 0; j <= bl; j++) v0[j] = v1[j];
    }
    return v1[bl];
  }

  function clearSectionHighlights() {
    document.querySelectorAll('mark.section-term-highlight').forEach(m => {
      const p = m.parentNode;
      p.replaceChild(document.createTextNode(m.textContent), m);
      p.normalize();
    });
  }

  function removeHeadingHighlights() {
    document.querySelectorAll('.inpage-search-highlight').forEach(el => el.classList.remove('inpage-search-highlight'));
  }

  function scrollWithFixedOffset(el) {
    const header = document.querySelector('.header') || document.querySelector('.navbar');
    const headerHeight = header ? header.getBoundingClientRect().height : 0;
    const gap = 120;
    const y = el.getBoundingClientRect().top + window.pageYOffset - (headerHeight + gap);
    window.scrollTo({ top: y < 0 ? 0 : y, behavior: 'smooth' });
  }

  function buildSections(root) {
    const heads = [...root.querySelectorAll('h1[id],h2[id],h3[id],h4[id],h5[id],h6[id]')];
    const sections = [];
    const allowedSel = 'p,ul,ol,li,pre,code,table,blockquote';
    for (let i = 0; i < heads.length; i++) {
      const h = heads[i];
      const next = heads[i + 1];
      let cur = h.nextSibling, body = '';
      let chars = 0;
      while (cur && cur !== next && chars < 40000) {
        if (cur.nodeType === 1) {
          if (cur.matches && cur.matches(allowedSel)) {
            const txt = cur.textContent;
            if (txt) {
              body += txt + ' ';
              chars = body.length;
            }
          } else {
            const inner = cur.querySelectorAll(allowedSel);
            inner.forEach(el => {
              const txt = el.textContent;
              if (txt) {
                body += txt + ' ';
                chars = body.length;
              }
            });
          }
        }
        cur = cur.nextSibling;
      }
      body = body.trim();
      sections.push({
        id: h.id,
        title: h.textContent.trim(),
        titleLower: h.textContent.trim().toLowerCase(),
        body,
        bodyLower: body.toLowerCase(),
        words: body.toLowerCase().split(/[^a-z0-9æøåäöüßœç\-]+/).filter(Boolean)
      });
    }
    return sections;
  }

  function resolveIndexUrl() {
    const link = document.querySelector('.nav-menu h3.title a[href]');
    return link && link.href ? link.href : location.href.replace(/(#.*)?$/, '');
  }

  function isIndexPage(u) {
    return location.href.split('#')[0] === u.split('#')[0];
  }

  const indexUrl = resolveIndexUrl();
  const onIndex = isIndexPage(indexUrl);
  let cache = null;

  function loadSections() {
    if (cache) return Promise.resolve(cache);
    if (onIndex) {
      cache = buildSections(document);
      return Promise.resolve(cache);
    }
    return fetch(indexUrl, { credentials: 'same-origin' }).then(r => r.text()).then(html => {
      const p = new DOMParser().parseFromString(html, 'text/html');
      cache = buildSections(p);
      return cache;
    }).catch(() => {
      cache = [];
      return cache;
    });
  }

  function parseQuery(q) {
    const tokens = [];
    const re = /"([^"]+)"|(\S+)/g;
    let m;
    while ((m = re.exec(q)) !== null) {
      tokens.push(m[1] || m[2]);
    }
    return tokens;
  }

  function termMatch(section, term) {
    const t = term.toLowerCase();
    if (section.titleLower.includes(t) || section.bodyLower.includes(t)) return true;
    for (const w of section.words) {
      if (Math.abs(w.length - t.length) > 2) continue;
      const d = levenshtein(w, t);
      if (d <= 1 || w.startsWith(t)) return true;
    }
    return false;
  }

  function searchSections(sections, query) {
    const raw = parseQuery(query);
    if (!raw.length) return [];
    const terms = raw.map(t => t.toLowerCase());
    let hits = [];
    for (const s of sections) {
      if (terms.every(t => termMatch(s, t))) {
        let titlePos = Infinity;
        terms.forEach(t => {
          const p = s.titleLower.indexOf(t);
          if (p > -1 && p < titlePos) titlePos = p;
        });
        const bodyPosList = terms.map(t => s.bodyLower.indexOf(t)).filter(p => p > -1);
        const bestBody = Math.min.apply(null, bodyPosList.length ? bodyPosList : [Infinity]);
        const score = (titlePos < Infinity ? 0 : 1000) + (titlePos < Infinity ? titlePos : (isFinite(bestBody) ? bestBody : 99999));
        hits.push({ section: s, score, terms });
      }
    }
    if (!hits.length && terms.length > 1) {
      for (const s of sections) {
        if (terms.some(t => termMatch(s, t))) {
          hits.push({ section: s, score: 500000, terms });
        }
      }
    }
    hits.sort((a, b) => a.score - b.score);
    return hits.slice(0, 50);
  }

  function buildSnippet(section, terms) {
    const body = section.body;
    if (!body) return '';
    let first = Infinity;
    terms.forEach(t => {
      const p = section.bodyLower.indexOf(t);
      if (p > -1 && p < first) first = p;
    });
    if (!isFinite(first)) first = 0;
    const start = Math.max(0, first - 60);
    const end = Math.min(body.length, start + 220);
    let frag = body.substring(start, end);
    if (start > 0) frag = '...' + frag;
    if (end < body.length) frag = frag + '...';
    let esc = escapeHtml(frag);
    terms.forEach(t => {
      esc = esc.replace(new RegExp('(' + t.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + ')', 'gi'), '<mark class="search-hit">$1</mark>');
    });
    return esc;
  }

  function renderResults(results, container, q, isIndex) {
    if (!results.length) {
      container.innerHTML = '<div class="search-result-item no-results">Ingen treff for "' + escapeHtml(q) + '"</div>';
      container.style.display = 'block';
      announce('Ingen treff');
      return;
    }
    container.innerHTML = '';
    const list = document.createElement('div');
    list.className = 'search-result-list';
    results.forEach((r, i) => {
      const it = document.createElement('div');
      it.className = 'search-result-item';
      it.id = 'search-opt-' + i;
      it.setAttribute('role', 'option');
      it.dataset.targetId = r.section.id;
      it.dataset.terms = r.terms.join(' ');
      const title = document.createElement('div');
      title.className = 'search-res-title';
      let tHtml = escapeHtml(r.section.title);
      r.terms.forEach(t => {
        tHtml = tHtml.replace(new RegExp('(' + t.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + ')', 'gi'), '<mark class="search-hit">$1</mark>');
      });
      title.innerHTML = tHtml;
      const snippet = document.createElement('div');
      snippet.className = 'search-res-snippet';
      snippet.innerHTML = buildSnippet(r.section, r.terms);
      it.appendChild(title);
      if (snippet.innerHTML) it.appendChild(snippet);
      it.addEventListener('mousedown', e => e.preventDefault());
      it.addEventListener('click', e => {
        e.preventDefault();
        highlightAndScroll(r.section.id, isIndex, r.terms);
      });
      list.appendChild(it);
    });
    container.appendChild(list);
    container.style.display = 'block';
    announce(results.length + ' treff');
  }

  function activate(items, pos, input) {
    items.forEach(n => n.classList.remove('active'));
    let cur = null;
    if (pos >= 0 && pos < items.length) {
      cur = items[pos];
      cur.classList.add('active');
      input.setAttribute('aria-activedescendant', cur.id);
    } else input.removeAttribute('aria-activedescendant');
    return cur;
  }

  function stripMarks(root) {
    root.querySelectorAll('mark.section-term-highlight').forEach(m => {
      const p = m.parentNode;
      p.replaceChild(document.createTextNode(m.textContent), m);
      p.normalize();
    });
  }

  function highlightSectionTerms(headerEl, terms) {
    const next = (() => {
      let n = headerEl.nextElementSibling;
      while (n) {
        if (/^H[1-6]$/.test(n.tagName)) return n;
        n = n.nextElementSibling;
      }
      return null;
    })();
    const els = [];
    let cur = headerEl.nextSibling;
    while (cur && cur !== next) {
      if (cur.nodeType === 1) els.push(cur);
      cur = cur.nextSibling;
    }
    terms.forEach(t => {
      const rx = new RegExp('(' + t.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + ')', 'gi');
      els.forEach(el => {
        stripMarks(el);
        const walker = document.createTreeWalker(el, NodeFilter.SHOW_TEXT, null);
        const nodes = [];
        while (walker.nextNode()) nodes.push(walker.currentNode);
        nodes.forEach(n => {
          if (!n.parentNode || /(script|style)/i.test(n.parentNode.tagName)) return;
          const val = n.nodeValue;
          if (!rx.test(val)) return;
          rx.lastIndex = 0;
          const frag = document.createDocumentFragment();
          let last = 0, m;
          while ((m = rx.exec(val)) !== null) {
            const before = val.slice(last, m.index);
            if (before) frag.appendChild(document.createTextNode(before));
            const mark = document.createElement('mark');
            mark.className = 'section-term-highlight';
            mark.textContent = m[0];
            frag.appendChild(mark);
            last = m.index + m[0].length;
          }
          const after = val.slice(last);
          if (after) frag.appendChild(document.createTextNode(after));
          n.parentNode.replaceChild(frag, n);
        });
      });
    });
  }

  function highlightAndScroll(id, isIndex, terms) {
    if (!isIndex) {
      location.href = indexUrl + '#' + id;
      return;
    }
    const target = document.getElementById(id);
    if (!target) return;
    clearSectionHighlights();
    if (location.hash !== '#' + id) location.hash = id;
    highlightSectionTerms(target, terms);
    scrollWithFixedOffset(target);
  }

  let announce = () => {
  };
  document.addEventListener('DOMContentLoaded', () => {
    const input = document.getElementById('search-input');
    if (!input) return;
    const isMac = /Mac|iPod|iPhone|iPad/.test(navigator.platform);
    input.setAttribute('placeholder', 'Søk (' + (isMac ? '⌘K' : 'Ctrl+K') + ')');
    document.addEventListener('keydown', e => {
      if ((isMac && e.metaKey && e.key.toLowerCase() === 'k') || (!isMac && e.ctrlKey && e.key.toLowerCase() === 'k')) {
        if (e.target instanceof HTMLInputElement || e.target instanceof HTMLTextAreaElement || e.target.isContentEditable) return;
        e.preventDefault();
        input.focus();
        input.select();
      }
    });
    const parent = input.parentNode;
    if (!parent) return;
    const container = document.createElement('div');
    container.className = 'search-result-dropdown-menu inpage-dropdown';
    container.id = 'search-results';
    container.setAttribute('role', 'listbox');
    container.style.display = 'none';
    parent.appendChild(container);
    const status = document.createElement('div');
    status.id = 'search-status';
    status.className = 'sr-only';
    status.setAttribute('aria-live', 'polite');
    parent.appendChild(status);
    announce = msg => status.textContent = msg;
    const clearBtn = document.createElement('button');
    clearBtn.type = 'button';
    clearBtn.className = 'search-clear-btn';
    clearBtn.setAttribute('aria-label', 'Tøm søk');
    clearBtn.innerHTML = '×';
    clearBtn.style.display = 'none';
    parent.appendChild(clearBtn);

    function updateClear() {
      clearBtn.style.display = input.value ? 'block' : 'none';
    }

    clearBtn.addEventListener('click', () => {
      input.value = '';
      container.innerHTML = '';
      container.style.display = 'none';
      input.setAttribute('aria-expanded', 'false');
      announce('');
      updateClear();
      removeHeadingHighlights();
      clearSectionHighlights();
      input.focus();
    });
    input.setAttribute('autocomplete', 'off');
    input.setAttribute('spellcheck', 'false');
    input.setAttribute('role', 'combobox');
    input.setAttribute('aria-autocomplete', 'list');
    input.setAttribute('aria-expanded', 'false');
    input.setAttribute('aria-owns', 'search-results');
    input.disabled = true;
    let active = -1;
    loadSections().then(() => {
      input.disabled = false;
    });

    function perform(q) {
      if (!q) {
        container.innerHTML = '';
        container.style.display = 'none';
        active = -1;
        input.setAttribute('aria-expanded', 'false');
        announce('');
        updateClear();
        return;
      }
      loadSections().then(sections => {
        const hits = searchSections(sections, q);
        active = -1;
        renderResults(hits, container, q, onIndex);
        input.setAttribute('aria-expanded', hits.length > 0 ? 'true' : 'false');
        updateClear();
      });
    }

    input.addEventListener('input', debounce(() => perform(input.value.trim()), 130));
    input.addEventListener('focus', () => {
      if (input.value.trim()) perform(input.value.trim());
      updateClear();
    });
    input.addEventListener('keydown', e => {
      const items = [...container.querySelectorAll('.search-result-item')];
      if (e.key === 'Escape') {
        input.value = '';
        container.innerHTML = '';
        container.style.display = 'none';
        active = -1;
        input.setAttribute('aria-expanded', 'false');
        announce('');
        updateClear();
        return;
      }
      if (e.key === 'ArrowDown') {
        e.preventDefault();
        if (!items.length) return;
        active = (active + 1) % items.length;
        activate(items, active, input);
        return;
      }
      if (e.key === 'ArrowUp') {
        e.preventDefault();
        if (!items.length) return;
        active = (active - 1 + items.length) % items.length;
        activate(items, active, input);
        return;
      }
      if (e.key === 'Enter') {
        const q = input.value.trim();
        if (active >= 0 && items[active]) {
          items[active].click();
          return;
        }
        if (!q) return;
        loadSections().then(sections => {
          const first = searchSections(sections, q)[0];
          if (first) highlightAndScroll(first.section.id, onIndex, first.terms);
        });
      }
    });
    document.documentElement.addEventListener('click', e => {
      if (!container.contains(e.target) && e.target !== input && e.target !== clearBtn) {
        container.innerHTML = '';
        container.style.display = 'none';
        input.setAttribute('aria-expanded', 'false');
      }
    });
    if (onIndex && location.hash.length > 1) {
      setTimeout(() => highlightAndScroll(location.hash.substring(1), true, []), 80);
    }
  });
})();
