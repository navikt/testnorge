import { describe, expect, it } from 'vitest'
import { oppdaterNyidentForIdenttype } from '@/components/fagsystem/pdlf/form/partials/nyIdent/nyIdentUtils'

describe('oppdaterNyidentForIdenttype', () => {
	it('should keep entries and their chosen values when the base type changes', () => {
		const nyident = [
			{ identtype: 'DNR', master: 'FREG', kilde: 'Dolly', syntetisk: true },
			{ eksisterendePerson: true, eksisterendeIdent: '12345678901', master: 'FREG' },
		]

		expect(oppdaterNyidentForIdenttype(nyident, 'FNR', 'DNR')).toEqual(nyident)
	})

	it('should follow the base type for entries using the previous default type', () => {
		expect(
			oppdaterNyidentForIdenttype([{ identtype: 'FNR', master: 'FREG' }], 'FNR', 'DNR'),
		).toEqual([{ identtype: 'DNR', master: 'FREG' }])
	})

	it('should switch default master when changing to and from NPID', () => {
		expect(
			oppdaterNyidentForIdenttype([{ identtype: 'DNR', master: 'FREG' }], 'FNR', 'NPID'),
		).toEqual([{ identtype: 'DNR', master: 'PDL' }])
		expect(
			oppdaterNyidentForIdenttype([{ identtype: 'NPID', master: 'PDL' }], 'NPID', 'FNR'),
		).toEqual([{ identtype: 'FNR', master: 'FREG' }])
	})

	it('should keep a manually chosen master that differs from the previous default', () => {
		expect(oppdaterNyidentForIdenttype([{ identtype: null, master: 'PDL' }], 'FNR', 'DNR')).toEqual(
			[{ identtype: null, master: 'PDL' }],
		)
	})
})
