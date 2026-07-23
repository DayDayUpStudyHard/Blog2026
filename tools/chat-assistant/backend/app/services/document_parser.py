"""知识库文档解析与混合切片。"""
from __future__ import annotations

import re
from dataclasses import dataclass
from pathlib import Path


@dataclass
class ParsedBlock:
    text: str
    section_title: str = ""
    source_page: int | None = None


@dataclass
class Chunk:
    text: str
    section_title: str = ""
    source_page: int | None = None


class DocumentParser:
    """解析 Markdown / TXT / PDF 为文本块。"""

    def parse(self, file_path: str, file_type: str) -> list[ParsedBlock]:
        path = Path(file_path)
        normalized = file_type.upper()
        if normalized == "MD":
            return self._parse_markdown(path)
        if normalized == "TXT":
            return self._parse_txt(path)
        if normalized == "PDF":
            return self._parse_pdf(path)
        raise ValueError(f"unsupported file type: {file_type}")

    def _parse_markdown(self, path: Path) -> list[ParsedBlock]:
        text = path.read_text(encoding="utf-8", errors="ignore")
        blocks: list[ParsedBlock] = []
        current_title = ""
        current_lines: list[str] = []

        for line in text.splitlines():
            heading = re.match(r"^(#{1,6})\s+(.+)$", line)
            if heading:
                self._append_block(blocks, "\n".join(current_lines), current_title)
                current_title = heading.group(2).strip()
                current_lines = [line]
            else:
                current_lines.append(line)
        self._append_block(blocks, "\n".join(current_lines), current_title)
        return blocks

    def _parse_txt(self, path: Path) -> list[ParsedBlock]:
        text = path.read_text(encoding="utf-8", errors="ignore")
        return [ParsedBlock(p.strip()) for p in re.split(r"\n\s*\n", text) if p.strip()]

    def _parse_pdf(self, path: Path) -> list[ParsedBlock]:
        try:
            from pypdf import PdfReader
        except ImportError as exc:
            raise RuntimeError("PDF 解析需要安装 pypdf") from exc

        reader = PdfReader(str(path))
        blocks: list[ParsedBlock] = []
        for page_index, page in enumerate(reader.pages, 1):
            text = page.extract_text() or ""
            for part in re.split(r"\n\s*\n", text):
                cleaned = part.strip()
                if cleaned:
                    blocks.append(ParsedBlock(cleaned, source_page=page_index))
        return blocks

    def _append_block(self, blocks: list[ParsedBlock], text: str, title: str) -> None:
        cleaned = text.strip()
        if cleaned:
            blocks.append(ParsedBlock(cleaned, section_title=title))


class HybridChunker:
    """标题/段落优先，固定长度兜底，保留 overlap。"""

    def __init__(self, chunk_size: int = 1000, overlap: int = 150):
        self.chunk_size = chunk_size
        self.overlap = overlap

    def chunk(self, blocks: list[ParsedBlock]) -> list[Chunk]:
        chunks: list[Chunk] = []
        for block in blocks:
            text = self._normalize(block.text)
            if len(text) <= self.chunk_size:
                chunks.append(Chunk(text, block.section_title, block.source_page))
                continue

            start = 0
            while start < len(text):
                end = min(start + self.chunk_size, len(text))
                chunks.append(Chunk(text[start:end], block.section_title, block.source_page))
                if end >= len(text):
                    break
                start = max(end - self.overlap, start + 1)
        return chunks

    def _normalize(self, text: str) -> str:
        lines = [line.strip() for line in text.splitlines()]
        return "\n".join(line for line in lines if line)
