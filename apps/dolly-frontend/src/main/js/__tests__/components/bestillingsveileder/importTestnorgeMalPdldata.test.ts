import { describe, expect, it } from 'vitest'
import { deriveBestillingsveilederState } from '@/components/bestillingsveileder/options/deriveBestillingsveilederState'

const envs: any[] = []

describe('importTestnorge + mal pdldata suppression', () => {
	it('keeps only person inside pdldata and removes opprettNyPerson', () => {
		const mal = {
			bestilling: {
				pdldata: {
					opprettNyPerson: { identtype: 'DNR', syntetisk: true },
					person: { navn: [{ fornavn: 'Import', etternavn: 'Person' }] },
					fullmakt: [{ type: 'Y' }],
				},
			},
		}
		const config = {
			importPersoner: [{ ident: '123' }],
			mal,
			gruppeId: 77,
		}
		const state = deriveBestillingsveilederState(config as any, envs)
		expect(state.is.importTestnorge).toBe(true)
		expect(state.initialValues.pdldata?.opprettNyPerson).toBeUndefined()
		expect(state.initialValues.pdldata?.person).toBeDefined()
	})
})
