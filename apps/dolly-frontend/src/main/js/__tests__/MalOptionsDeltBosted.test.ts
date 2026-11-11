import { describe, expect, it } from 'vitest'
import { initialValuesBasedOnMal } from '@/components/bestillingsveileder/options/malOptions'

const lagMal = (forelderBarnRelasjon: any[]) => ({
	bestilling: {
		pdldata: {
			person: { forelderBarnRelasjon },
		},
	},
})

const lagBarn = (deltBosted?: any) => ({
	minRolleForPerson: 'FORELDER',
	relatertPersonsRolle: 'BARN',
	partnerErIkkeForelder: false,
	...(deltBosted !== undefined && { deltBosted }),
})

describe('initialValuesBasedOnMal for delt bosted', () => {
	it('should set delt bosted to null when the template has no delt bosted', () => {
		const verdier = initialValuesBasedOnMal(lagMal([lagBarn()]), [])

		expect(verdier.pdldata.person.forelderBarnRelasjon[0].deltBosted).toBeNull()
	})

	it('should set delt bosted to null when the template has an empty delt bosted', () => {
		const verdier = initialValuesBasedOnMal(lagMal([lagBarn({})]), [])

		expect(verdier.pdldata.person.forelderBarnRelasjon[0].deltBosted).toBeNull()
	})

	it('should keep delt bosted with address type when the template has delt bosted', () => {
		const verdier = initialValuesBasedOnMal(
			lagMal([lagBarn({ startdatoForKontrakt: '2024-01-01T00:00:00', kilde: 'Dolly' })]),
			[],
		)

		const deltBosted = verdier.pdldata.person.forelderBarnRelasjon[0].deltBosted
		expect(deltBosted.startdatoForKontrakt).toBe('2024-01-01T00:00:00')
		expect(deltBosted.adressetype).toBe('PARTNER_ADRESSE')
	})
})
