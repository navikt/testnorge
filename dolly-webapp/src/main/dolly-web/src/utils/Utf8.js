export function encode_utf8(s) {
	return unescape(encodeURIComponent(s))
}

export function decode_utf8(s) {
	return decodeURIComponent(escape(s))
}
