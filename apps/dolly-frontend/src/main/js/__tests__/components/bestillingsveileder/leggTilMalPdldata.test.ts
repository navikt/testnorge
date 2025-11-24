import { describe, expect, it } from 'vitest'
import { deriveBestillingsveilederState } from '@/components/bestillingsveileder/options/deriveBestillingsveilederState'

describe('leggTil mal pdldata sanitization', () => {
	it('removes opprettNyPerson but keeps other pdldata fields (now undefined in state)', () => {
		const mal = {
			bestilling: {
				pdldata: {
					opprettNyPerson: { identtype: 'DNR', syntetisk: true, id2032: true },
					person: { navn: [{ fornavn: 'Test', etternavn: 'Person' }] },
					fullmakt: [{ type: 'X' }],
				},
			},
		}
		const state = deriveBestillingsveilederState(
			{ personFoerLeggTil: { id: 'abc' }, mal, gruppeId: 10 } as any,
			[],
		)
		expect(state.is.leggTil).toBe(true)
		expect(state.initialValues.pdldata).toBeUndefined()
	})
})
