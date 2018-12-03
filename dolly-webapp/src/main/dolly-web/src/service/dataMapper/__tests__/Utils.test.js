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
	const testRelasjonEktefelle = 'EKTEFELLE'
	const testRelasjonMor = 'MOR'
	const testRelasjonFar = 'FAR'
	const testRelasjonBarn = 'BARN'
	const testRelasjonFoedsel = 'FOEDSEL'
	const testRelasjonUkjent = 'RANDOM'

	it('should return correct label for ektefelle', () => {
		const res = 'Partner'
		expect(relasjonTranslator(testRelasjonEktefelle)).toBe(res)
	})
	it('should return correct label for mor', () => {
		const res = 'Mor'
		expect(relasjonTranslator(testRelasjonMor)).toBe(res)
	})
	it('should return correct label for far', () => {
		const res = 'Far'
		expect(relasjonTranslator(testRelasjonFar)).toBe(res)
	})

	it('should return correct label for barn', () => {
		const res = 'Barn'
		expect(relasjonTranslator(testRelasjonBarn)).toBe(res)
	})
	it('should return correct label for foedsel', () => {
		const res = 'Barn'
		expect(relasjonTranslator(testRelasjonFoedsel)).toBe(res)
	})
	it('should return correct label for MOR', () => {
		const res = 'Ukjent relasjon'
		expect(relasjonTranslator(testRelasjonUkjent)).toBe(res)
	})
})
