import os
import re
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
INDEX_PATH = REPO_ROOT / 'docs/modules/ROOT/pages/index.adoc'
NAV_PATH = REPO_ROOT / 'docs/modules/ROOT/nav.adoc'

INCLUDE_PATTERN = re.compile(r'^include::([^\[]+)\[([^]]*)]')
ANCHOR_PATTERN = re.compile(r'^\[\[([a-zA-Z0-9_:-]+)]]\s*$')
HEADING_PATTERN = re.compile(r'^(=+)\s+(.+?)\s*$')
OFFSET_PATTERN = re.compile(r'leveloffset=\+(\d+)')

# Max depth of headings to include (after adjustment)
MAX_HEADING_LEVEL = 6  # allow up to ======

class Heading:
    def __init__(self, anchor: str, text: str, level: int):
        self.anchor = anchor
        self.text = text
        self.level = level  # actual AsciiDoc level (# of '=') after offset applied

    @property
    def depth(self) -> int:
        # Depth inside nav: level 2 (==) -> depth 1 (children of root doc entry)
        return self.level - 1

    def bullet(self) -> str:
        stars = '*' * (self.depth + 1)  # root doc entry uses single *
        return f"{stars} xref:index.adoc#{self.anchor}[{self.text}]"


def read_file(path: Path) -> list[str]:
    try:
        return path.read_text(encoding='utf-8').splitlines()
    except FileNotFoundError:
        return []


def parse_headings(lines: list[str], base_offset: int = 0) -> list[Heading]:
    headings = []
    pending_anchor = None
    for line in lines:
        m_anchor = ANCHOR_PATTERN.match(line)
        if m_anchor:
            pending_anchor = m_anchor.group(1)
            continue
        m_head = HEADING_PATTERN.match(line)
        if m_head and pending_anchor:
            level = len(m_head.group(1)) + base_offset
            if level <= MAX_HEADING_LEVEL:
                text = m_head.group(2).strip()
                # Skip top-level document title (=) even with offset 0
                if not (level == 1 and base_offset == 0):
                    headings.append(Heading(pending_anchor, text, level))
            pending_anchor = None
        elif m_head:
            # Heading without explicit anchor; we could derive id but skip to avoid mismatch
            pending_anchor = None
    return headings


def collect_included_files(index_lines: list[str]) -> list[tuple[Path, int]]:
    includes = []
    for line in index_lines:
        m = INCLUDE_PATTERN.match(line.strip())
        if not m:
            continue
        rel_path = m.group(1)
        attrs = m.group(2)
        off_match = OFFSET_PATTERN.search(attrs)
        offset = int(off_match.group(1)) if off_match else 0
        include_path = (INDEX_PATH.parent / rel_path).resolve()
        includes.append((include_path, offset))
    return includes


def build_nav(headings: list[Heading]) -> str:
    lines = []
    lines.append('* xref:index.adoc[Testnav Dokumentasjon]')
    # Sort by original order (already preserved)
    for h in headings:
        # Only include levels >=2 (depth>=1)
        if h.level >= 2:
            lines.append(h.bullet())
    return '\n'.join(lines) + '\n'


def main():
    index_lines = read_file(INDEX_PATH)
    if not index_lines:
        raise SystemExit('Missing index.adoc')
    all_headings: list[Heading] = []
    # First pass: headings in index.adoc
    all_headings.extend(parse_headings(index_lines, base_offset=0))
    # Second pass: included files with offsets
    for inc_path, offset in collect_included_files(index_lines):
        inc_lines = read_file(inc_path)
        if not inc_lines:
            continue
        all_headings.extend(parse_headings(inc_lines, base_offset=offset))
    # Write nav.adoc
    NAV_PATH.write_text(build_nav(all_headings), encoding='utf-8')

if __name__ == '__main__':
    main()

