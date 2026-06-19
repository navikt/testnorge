import {
	erStrukturertFeilVerdi,
	fagsystemFeilLabel,
	feilVerdiTilTekst,
	monthNameToNumber,
	monthNumberToName,
	statusFeltTilFeilNokkel,
	toFeilGrupper,
	toFeilPeriodeOptions,
	toFeilSummertView,
} from '@/pages/adminPages/Dashboard/dashboardFeilUtils'
import type {
	DashboardFeilDetaljertRad,
	DashboardFeilSummertRad,
	DashboardOversiktDTO,
} from '@/utils/hooks/useDashboard'

describe('dashboardFeilUtils', () => {
	it('should round-trip month name and number', () => {
		expect(monthNameToNumber('JUNE')).toBe(6)
		expect(monthNumberToName(6)).toBe('JUNE')
		expect(monthNameToNumber('JANUARY')).toBe(1)
		expect(monthNumberToName(12)).toBe('DECEMBER')
		expect(monthNameToNumber('NOTAMONTH')).toBeNull()
		expect(monthNumberToName(0)).toBeNull()
		expect(monthNumberToName(13)).toBeNull()
	})

	it('should translate status fields to feil keys', () => {
		expect(statusFeltTilFeilNokkel('feil')).toBe('andreFeil')
		expect(statusFeltTilFeilNokkel('pdlPersonStatus')).toBe('pdlPersonFeil')
		expect(statusFeltTilFeilNokkel('aaregStatus')).toBe('aaregFeil')
	})

	it('should label known fagsystem keys and humanize unknown ones', () => {
		expect(fagsystemFeilLabel('pdlPersonFeil')).toBe('PDL Person')
		expect(fagsystemFeilLabel('andreFeil')).toBe('Andre feil')
		expect(fagsystemFeilLabel('someNewSystemFeil')).toBe('Some New System')
	})

	it('should build period options from oversikt sorted ascending', () => {
		const oversikt: DashboardOversiktDTO[] = [
			{ aar: 2026, maaned: '06', totaltAntallPersoner: 10, nye: 6, gjenopprettede: 4 },
			{ aar: 2022, maaned: '01', totaltAntallPersoner: 5, nye: 3, gjenopprettede: 2 },
		]

		const options = toFeilPeriodeOptions(oversikt)

		expect(options).toHaveLength(2)
		expect(options[0].interval).toBe('2022-01')
		expect(options[0].aar).toBe(2022)
		expect(options[0].maaned).toBe(1)
		expect(options[0].maanedNavn).toBe('JANUARY')
		expect(options[1].interval).toBe('2026-06')
		expect(options[1].aar).toBe(2026)
		expect(options[1].maaned).toBe(6)
		expect(options[1].maanedNavn).toBe('JUNE')
	})

	it('should aggregate summert rows into per-day points with dynamic fagsystem keys', () => {
		const summert: DashboardFeilSummertRad[] = [
			{ bestillingDato: '2026-06-07', pdlPersonFeil: 2, andreFeil: 0 },
			{ bestillingDato: '2026-06-03', pdlPersonFeil: 1, aaregFeil: 3 },
		]

		const view = toFeilSummertView(summert)

		expect(view.punkter.map((punkt) => punkt.dag)).toEqual([3, 7])
		expect(view.punkter[0].total).toBe(4)
		expect(view.punkter[0].perFagsystem).toEqual({ pdlPersonFeil: 1, aaregFeil: 3 })
		expect(view.punkter[1].perFagsystem).toEqual({ pdlPersonFeil: 2 })
		expect(view.fagsystemNokler).toContain('pdlPersonFeil')
		expect(view.fagsystemNokler).toContain('aaregFeil')
		expect(view.fagsystemNokler).not.toContain('andreFeil')
	})

	it('should group detaljert rows per fagsystem', () => {
		const detaljert: DashboardFeilDetaljertRad[] = [
			{
				sistOppdatert: '2026-06-03T10:00:00',
				bestillingId: 1,
				ident: '02',
				master: 'PDL',
				pdlPersonStatus: 'Feilet',
				feil: 'Ukjent feil',
			},
			{
				sistOppdatert: '2026-06-03T11:00:00',
				bestillingId: 2,
				ident: '01',
				master: 'TPS',
				pdlPersonStatus: 'Feilet igjen',
			},
		]

		const grupper = toFeilGrupper(detaljert)

		const pdlGruppe = grupper.find((gruppe) => gruppe.feilNokkel === 'pdlPersonFeil')
		const andreGruppe = grupper.find((gruppe) => gruppe.feilNokkel === 'andreFeil')

		expect(pdlGruppe?.rader).toHaveLength(2)
		expect(pdlGruppe?.label).toBe('PDL Person')
		expect(pdlGruppe?.rader.map((rad) => rad.ident)).toEqual(['01', '02'])
		expect(andreGruppe?.rader).toHaveLength(1)
		expect(grupper[0].feilNokkel).toBe('pdlPersonFeil')
	})

	it('should pretty-print structured feil values and detect them', () => {
		expect(feilVerdiTilTekst('en feilmelding')).toBe('en feilmelding')
		expect(feilVerdiTilTekst(null)).toBe('')
		expect(erStrukturertFeilVerdi('tekst')).toBe(false)
		expect(erStrukturertFeilVerdi({ status: 'ERROR' })).toBe(true)
		expect(feilVerdiTilTekst({ status: 'ERROR' })).toContain('"status": "ERROR"')
	})
})
