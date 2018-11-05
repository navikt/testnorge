import failure from '../FailureAction'

describe('FailureAction.js', () => {
	const testAction = 'TEST_ACTION'
	it('should take a action and append "FAILURE" to its string', () => {
		expect(failure(testAction)).toEqual(`${testAction}_FAILURE`)
	})
})
