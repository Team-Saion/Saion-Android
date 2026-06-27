#!/usr/bin/env python3
import argparse
import json
import sys
from pathlib import Path


def load_spec(path: Path) -> dict:
    with path.open("r", encoding="utf-8") as file:
        return json.load(file)


def iter_operations(spec: dict):
    for path, path_item in spec.get("paths", {}).items():
        for method, operation in path_item.items():
            if method.lower() not in {"get", "post", "put", "patch", "delete"}:
                continue
            yield path, method.lower(), operation


def matches(args, path: str, operation: dict) -> bool:
    if args.tag and args.tag not in operation.get("tags", []):
        return False
    if args.path and args.path != path:
        return False
    if args.operation_id and args.operation_id != operation.get("operationId"):
        return False
    return True


def describe_request(operation: dict) -> list[str]:
    request_body = operation.get("requestBody") or {}
    content = request_body.get("content") or {}
    lines = []
    for content_type, payload in content.items():
        schema = (payload or {}).get("schema") or {}
        if not schema:
            continue
        lines.append(f"request: {content_type} -> {schema_name(schema)}")
    return lines


def describe_response(operation: dict) -> list[str]:
    responses = operation.get("responses") or {}
    success = responses.get("200") or responses.get("201") or responses.get("202") or {}
    content = success.get("content") or {}
    lines = []
    for content_type, payload in content.items():
        schema = (payload or {}).get("schema") or {}
        if not schema:
            continue
        lines.append(f"response: {content_type} -> {schema_name(schema)}")
    return lines


def schema_name(schema: dict) -> str:
    ref = schema.get("$ref")
    if ref:
        return ref.split("/")[-1]
    if "items" in schema:
        return f"List[{schema_name(schema['items'])}]"
    if "type" in schema:
        return str(schema["type"])
    if "oneOf" in schema:
        return "oneOf(" + ", ".join(schema_name(item) for item in schema["oneOf"]) + ")"
    return "unknown"


def auth_summary(spec: dict, operation: dict) -> str:
    security = operation.get("security", spec.get("security"))
    return "auth-required" if security else "public"


def main() -> int:
    parser = argparse.ArgumentParser(description="Inspect Saion OpenAPI operations.")
    parser.add_argument(
        "--spec",
        default=str(Path(__file__).resolve().parent.parent / "references" / "saion-openapi.json"),
        help="Path to the OpenAPI JSON file.",
    )
    parser.add_argument("--tag", help="Filter by tag.")
    parser.add_argument("--path", help="Filter by exact path.")
    parser.add_argument("--operation-id", help="Filter by operationId.")
    args = parser.parse_args()

    spec_path = Path(args.spec)
    if not spec_path.exists():
        print(f"spec file not found: {spec_path}", file=sys.stderr)
        print("run fetch_openapi_spec.sh first or pass --spec", file=sys.stderr)
        return 1

    spec = load_spec(spec_path)
    matched = False
    for path, method, operation in iter_operations(spec):
        if not matches(args, path, operation):
            continue
        matched = True
        tags = ", ".join(operation.get("tags", [])) or "-"
        print(f"{method.upper()} {path}")
        print(f"  operationId: {operation.get('operationId', '-')}")
        print(f"  tags: {tags}")
        print(f"  auth: {auth_summary(spec, operation)}")
        summary = operation.get("summary")
        if summary:
            print(f"  summary: {summary}")
        for line in describe_request(operation):
            print(f"  {line}")
        for line in describe_response(operation):
            print(f"  {line}")
        print()

    if not matched:
        print("no operations matched", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
