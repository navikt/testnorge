import DataSourceMapper from '../DataSourceMapper'

describe('DataSourceMapper.js', () => {
	describe('format registers source', () => {
		const sigrun = 'SIGRUN'
		const krr = 'KRR'
		const arena = 'ARENA'
		const inst = 'INST'
		const udi = 'UDI'

		it('should return sigrun-stub', () => {
			const res = 'sigrunstub'
			expect(DataSourceMapper(sigrun)).toBe(res)
		})

		it('should return krr-stub', () => {
			const res = 'krrstub'
			expect(DataSourceMapper(krr)).toBe(res)
		})

		it('should return arena', () => {
			const res = 'arenaforvalter'
			expect(DataSourceMapper(arena)).toBe(res)
		})

		it('should return institusjonsopphold', () => {
			const res = 'instdata'
			expect(DataSourceMapper(inst)).toBe(res)
		})

		it('should return udi-stub', () => {
			const res = 'udistub'
			expect(DataSourceMapper(udi)).toBe(res)
		})

		it('should return tpsf on default', () => {
			const res = 'tpsf'
			expect(DataSourceMapper()).toBe(res)
		})
	})
})
