import ConfigService from '../Config'

describe('Config.js', () => {
	const testUrl = 'test'
	global.window = { dollyConfig: { tpsfUrl: testUrl } }
	it('should verify if dollyConfig exist', () => {
		expect(ConfigService.verifyConfig()).toBeTruthy()
	})

	it('should return tpsf url', () => {
		expect(ConfigService.getDatesourceUrl('tpsf')).toBe(testUrl)
	})

	it('should return undefined when requesting a unknown datasource', () => {
		expect(ConfigService.getDatesourceUrl('unknown')).toBe(undefined)
	})
})
