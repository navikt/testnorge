import DataSourceMapper from '../DataSourceMapper'

describe('DataSourceMapper.js', () => {
	describe('format registers source', () => {
		const sigrun = 'SIGRUN'
		const krr = 'KRR'

		it('should return sigrun-stub', () => {
			const res = 'sigrunstub'
			expect(DataSourceMapper(sigrun)).toBe(res)
		})

		it('should return krr-stub', () => {
			const res = 'krrstub'
			expect(DataSourceMapper(krr)).toBe(res)
		})

		it('should return tpsf on default', () => {
			const res = 'tpsf'
			expect(DataSourceMapper()).toBe(res)
		})
	})
})
