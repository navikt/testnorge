import {
	arrayToString,
	formatAlder,
	formatDate,
	formatKjonnToString,
	parseDate,
} from '../DataFormatter'

describe('DataFormatter.js', () => {
	describe('formatAlder', () => {
		const dodsdato = new Date()
		const testAlder = 70

		it('should return a living persons age', () => {
			expect(formatAlder(testAlder)).toBe(testAlder.toString())
		})

		it('should return av dead persons age with a text', () => {
			const res = '70 (død)'
			expect(formatAlder(testAlder, dodsdato)).toBe(res)
		})
	})

	describe('formatDate', () => {
		const testDate = new Date('2000-01-01')
		it('should return date in a readable format', () => {
			expect(formatDate(testDate)).toBe('01.01.2000')
		})

		it('should return without parsing if undefined', () => {
			const testUndefined = undefined
			expect(formatDate(testUndefined)).toEqual(testUndefined)
		})
	})

	describe('parseDate', () => {
		const dateString = '01.01.2000'
		const dateResult = new Date('2000-01-01')
		it('it should parse date to a date-obj', () => {
			expect(parseDate(dateString)).toEqual(dateResult)
		})

		it('should return without parsing if undefined', () => {
			const testUndefined = undefined
			expect(parseDate(testUndefined)).toEqual(testUndefined)
		})
	})

	describe('kjonnToString', () => {
		it('it should return Mann', () => {
			expect(formatKjonnToString('m')).toBe('MANN')
		})
		it('it should return Kvinne', () => {
			expect(formatKjonnToString('k')).toBe('KVINNE')
		})
		it('it should return udefinert because no match', () => {
			expect(formatKjonnToString('x')).toBe('UDEFINERT')
		})
	})

	describe('arrayToString', () => {
		const testArr = ['1', 'a', '2', 'b', '3', 'c']
		const res = '1, A, 2, B, 3, C'

		it('should transform array to a comma separated string', () => {
			expect(arrayToString(testArr, ',')).toBe(res)
		})
	})
})
