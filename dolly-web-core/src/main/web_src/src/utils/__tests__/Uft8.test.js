import { encode_utf8, decode_utf8 } from '../Utf8'

describe('Utf8.js', () => {
	it('should encode utf8', () => {
		const testData = 'øl'
		const res = '\xc3\xb8\x6c'
		expect(encode_utf8(testData)).toBe(res)
	})
})

describe('Utf8.js', () => {
	it('should decode utf8', () => {
		const testData = '\x62\x72\xc3\xb8\x64'
		const res = 'brød'
		expect(decode_utf8(testData)).toBe(res)
	})
})
