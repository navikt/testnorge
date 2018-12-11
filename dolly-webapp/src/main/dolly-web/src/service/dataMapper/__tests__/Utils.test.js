import { createHeader, relasjonTranslator } from '../Utils'
import { create } from 'domain'

describe('createHeader in Utils', () => {
	const testLabel = 'testlabel'
	const testWidth = '10'

	const testObj = {
		width: testWidth,
		label: testLabel
	}

	it('should return a object with label and width', () => {
		expect(createHeader(testLabel, testWidth)).toMatchObject(testObj)
	})
})

describe('relasjonTranslator in Utils', () => {
	const testRelasjon = 'MOR'

	it('should return correct label for MOR', () => {
		expect(relasjonTranslator(testRelasjon)).toBe('Mor')
	})
})
