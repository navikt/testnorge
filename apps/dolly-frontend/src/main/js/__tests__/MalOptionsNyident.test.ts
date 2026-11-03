import { describe, expect, it } from 'vitest'
import { initialValuesBasedOnMal } from '@/components/bestillingsveileder/options/malOptions'

const lagMal = (nyident: any[]) => ({
	bestilling: {
		pdldata: {
			person: { nyident },
		},
	},
})

describe('initialValuesBasedOnMal for nyident', () => {
	it('should never keep false conversion flags from a template', () => {
		const verdier = initialValuesBasedOnMal(
			lagMal([{ identtype: 'DNR', syntetisk: false, id2032: false }]),
			[],
		)

		const nyIdent = verdier.pdldata.person.nyident[0]
		expect(nyIdent.syntetisk).toBeUndefined()
		expect(nyIdent.id2032).toBeUndefined()
	})

	it('should keep true conversion flags from a template', () => {
		const verdier = initialValuesBasedOnMal(
			lagMal([{ identtype: 'DNR', syntetisk: true, id2032: true }]),
			[],
		)

		const nyIdent = verdier.pdldata.person.nyident[0]
		expect(nyIdent.syntetisk).toBe(true)
		expect(nyIdent.id2032).toBe(true)
	})

	it('should keep an empty ident type so the destination can decide it', () => {
		const verdier = initialValuesBasedOnMal(lagMal([{ identtype: null }]), [])

		expect(verdier.pdldata.person.nyident[0].identtype).toBeNull()
	})
})
