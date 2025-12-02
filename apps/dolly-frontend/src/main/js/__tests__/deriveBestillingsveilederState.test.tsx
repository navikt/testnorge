import { describe, expect, it } from 'vitest'
import { deriveBestillingsveilederState } from '@/components/bestillingsveileder/options/deriveBestillingsveilederState'

const environmentsStub = [{ value: 'q1' }, { value: 'q2' }]

describe('deriveBestillingsveilederState', () => {
	it('should derive NY_BESTILLING defaults', () => {
		const state = deriveBestillingsveilederState({ gruppeId: 1 }, environmentsStub)
		expect(state.is.nyBestilling).toBe(true)
		expect(state.gruppeId).toBe(1)
		expect(state.initialValues.pdldata.opprettNyPerson.identtype).toBe('FNR')
	})

	it('should derive NY_BESTILLING_FRA_MAL when mal provided', () => {
		const mal = {
			bestilling: { pdldata: { opprettNyPerson: { identtype: 'DNR' } }, environments: [] },
		}
		const state = deriveBestillingsveilederState({ mal, gruppeId: 2 }, environmentsStub)
		expect(state.is.nyBestillingFraMal).toBe(true)
		expect(state.initialValues.pdldata.opprettNyPerson.identtype).toBe('DNR')
	})

	it('should derive OPPRETT_FRA_IDENTER', () => {
		const state = deriveBestillingsveilederState(
			{ opprettFraIdenter: ['1', '2'], gruppeId: 3 },
			environmentsStub,
		)
		expect(state.is.opprettFraIdenter).toBe(true)
		expect(state.initialValues.opprettFraIdenter).toEqual(['1', '2'])
	})

	it('should derive LEGG_TIL', () => {
		const state = deriveBestillingsveilederState(
			{ personFoerLeggTil: { id: 'x' }, gruppeId: 4 },
			environmentsStub,
		)
		expect(state.is.leggTil).toBe(true)
		expect(state.initialValues.pdldata).toBeUndefined()
	})

	it('should derive LEGG_TIL_PAA_GRUPPE', () => {
		const state = deriveBestillingsveilederState(
			{ leggTilPaaGruppe: { id: 'g' }, gruppeId: 5 },
			environmentsStub,
		)
		expect(state.is.leggTilPaaGruppe).toBe(true)
		expect(state.initialValues.pdldata).toBeUndefined()
	})

	it('should derive IMPORT_TESTNORGE', () => {
		const importPersoner = [{ ident: 'a' }, { ident: 'b' }]
		const state = deriveBestillingsveilederState({ importPersoner, gruppeId: 6 }, environmentsStub)
		expect(state.is.importTestnorge).toBe(true)
		expect(state.initialValues.importPersoner).toEqual(importPersoner)
		expect(state.initialValues.pdldata).toBeUndefined()
		expect(state.antall).toBe(2)
	})

	it('should sanitize pdldata for IMPORT_TESTNORGE with mal', () => {
		const mal = {
			bestilling: {
				pdldata: {
					opprettNyPerson: { identtype: 'DNR' },
					person: { navn: [{ fornavn: 'X', etternavn: 'Y' }] },
				},
			},
		}
		const importPersoner = [{ ident: 'a' }]
		const state = deriveBestillingsveilederState(
			{ importPersoner, mal, gruppeId: 10 },
			environmentsStub,
		)
		expect(state.is.importTestnorge).toBe(true)
		expect(state.initialValues.pdldata?.opprettNyPerson).toBeUndefined()
		expect(state.initialValues.pdldata?.person).toBeDefined()
	})

	it('should derive NY_STANDARD_ORGANISASJON', () => {
		const state = deriveBestillingsveilederState(
			{ opprettOrganisasjon: 'STANDARD', gruppeId: 7 },
			environmentsStub,
		)
		expect(state.is.nyStandardOrganisasjon).toBe(true)
		expect(state.initialValues.organisasjon.enhetstype).toBe('AS')
	})

	it('should derive NY_ORGANISASJON', () => {
		const state = deriveBestillingsveilederState(
			{ opprettOrganisasjon: 'ANNEN', gruppeId: 8 },
			environmentsStub,
		)
		expect(state.is.nyOrganisasjon).toBe(true)
		expect(state.initialValues.organisasjon.enhetstype).toBe('')
	})

	it('should derive NY_ORGANISASJON_FRA_MAL', () => {
		const mal = { bestilling: { pdldata: { opprettNyPerson: { identtype: 'FNR' } } } }
		const state = deriveBestillingsveilederState(
			{ opprettOrganisasjon: 'ANNEN', mal, gruppeId: 9 },
			environmentsStub,
		)
		expect(state.is.nyOrganisasjonFraMal).toBe(true)
	})
})
