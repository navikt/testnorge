import Formatter from '../DataFormatter'
import { FORMERR } from 'dns'

describe('DataFormatter.js', () => {
	describe('formatIdentNr', () => {
		const testNr = '12345612345'
		const res = '123456 12345'

		it('should format ident nr to correct format: ###### #####', () => {
			expect(Formatter.formatIdentNr(testNr)).toBe(res)
		})

		it('should return without parsing if undefined', () => {
			const testUndefined = undefined
			expect(Formatter.formatIdentNr(testUndefined)).toEqual(testUndefined)
		})
	})

	describe('formatAlder', () => {
		const dodsdato = new Date()
		const testAlder = 70

		it('should return a living persons age', () => {
			expect(Formatter.formatAlder(testAlder)).toBe(testAlder.toString())
		})

		it('should return av dead persons age with a text', () => {
			const res = '70 (død)'
			expect(Formatter.formatAlder(testAlder, dodsdato)).toBe(res)
		})
	})

	describe('formatDate', () => {
		const testDate = new Date('2000-01-01')
		it('should return date in a readable format', () => {
			expect(Formatter.formatDate(testDate)).toBe('01.01.2000')
		})

		it('should return without parsing if undefined', () => {
			const testUndefined = undefined
			expect(Formatter.formatDate(testUndefined)).toEqual(testUndefined)
		})
	})

	describe('parseDate', () => {
		const dateString = '01.01.2000'
		const dateResult = new Date('2000-01-01')
		it('it should parse date to a date-obj', () => {
			expect(Formatter.parseDate(dateString)).toEqual(dateResult)
		})

		it('should return without parsing if undefined', () => {
			const testUndefined = undefined
			expect(Formatter.parseDate(testUndefined)).toEqual(testUndefined)
		})
	})

	describe('kjonnToString', () => {
		it('it should return Mann', () => {
			expect(Formatter.kjonnToString('m')).toBe('Mann')
		})
		it('it should return Kvinne', () => {
			expect(Formatter.kjonnToString('k')).toBe('Kvinne')
		})
		it('it should return udefinert because no match', () => {
			expect(Formatter.kjonnToString('x')).toBe('udefinert')
		})
	})

	describe('kjonnToStringBarn', () => {
		it('it should return Gutt', () => {
			expect(Formatter.kjonnToStringBarn('m')).toBe('Gutt')
		})
		it('it should return Jente', () => {
			expect(Formatter.kjonnToStringBarn('k')).toBe('Jente')
		})
		it('it should return udefinert because no match', () => {
			expect(Formatter.kjonnToStringBarn('x')).toBe('udefinert')
		})
	})

	describe('arrayToString', () => {
		const testArr = ['1', 'a', '2', 'b', '3', 'c']
		const res = '1, A, 2, B, 3, C'

		it('should transform array to a comma separated string', () => {
			expect(Formatter.arrayToString(testArr, ',')).toBe(res)
		})
	})
})
