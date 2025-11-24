import { describe, expect, it } from 'vitest'
import { deriveBestillingsveilederState } from '@/components/bestillingsveileder/options/deriveBestillingsveilederState'

describe('opprettFraIdenter mal pdldata sanitization', () => {
	it('excludes opprettNyPerson and keeps other pdldata fields (pdldata undefined)', () => {
		const mal = {
			bestilling: {
				pdldata: {
					opprettNyPerson: { identtype: 'DNR' },
					person: { navn: [{ fornavn: 'Fra', etternavn: 'Identer' }] },
					fullmakt: [{ type: 'Z' }],
				},
			},
		}
		const config = { opprettFraIdenter: ['1', '2'], mal, gruppeId: 55 }
		const state = deriveBestillingsveilederState(config as any, [])
		expect(state.is.opprettFraIdenter).toBe(true)
		expect(state.initialValues.pdldata).toBeUndefined()
	})
})
