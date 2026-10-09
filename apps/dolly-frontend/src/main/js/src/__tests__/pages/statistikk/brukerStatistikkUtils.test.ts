import {
	aarSammendragTittel,
	filterMaanedPunkter,
	harAktivitet,
	parseDetaljNoekkel,
	parsePeriode,
	toAarOptions,
	toAarSammendrag,
	toDagFagsystemMatrise,
	toDagPersonMatrise,
	toFagsystemDetaljer,
	toFagsystemerMedDetaljer,
	toFagsystemSummer,
	toFagsystemSummerUtenUspesifisert,
	toMaanedOptions,
	toMaanedPunkter,
} from '@/pages/statistikk/brukerStatistikkUtils'
import { MONTH_SCOPE_ALL, MONTH_SCOPE_LAST_12 } from '@/pages/adminPages/Dashboard/dashboardUtils'
import { type BrukerBestillingerDTO } from '@/utils/hooks/useBrukerStatistikk'

const detaljert: BrukerBestillingerDTO[] = [
	{
		dato: '2026-09-02',
		kriterier: [
			{
				fagsystem: 'PdlData',
				antall: 5,
				detaljer: { 'Syntetisk-true': 4, 'Syntetisk-false': 1, Navn: 2 },
			},
			{ fagsystem: 'Aareg', antall: 2, detaljer: { 'Antall arbeidsforhold': 6 } },
			{ fagsystem: 'Beskrivelse', antall: 5, detaljer: null },
		],
	},
	{
		dato: '2026-09-15',
		kriterier: [
			{
				fagsystem: 'PdlData',
				antall: 3,
				detaljer: { 'Syntetisk-true': 3, 'Legg-til/endre-true': 1 },
			},
			{ fagsystem: 'Krrstub', antall: 3, detaljer: null },
			{
				fagsystem: 'Arena',
				antall: 1,
				detaljer: { 'ArenaBrukertype-MED_SERVICEBEHOV': 1 },
			},
		],
	},
]

describe('brukerStatistikkUtils', () => {
	it('should parse periode from ISO string and array formats', () => {
		expect(parsePeriode('2026-09')).toEqual({ year: 2026, month: 9 })
		expect(parsePeriode([2026, 9])).toEqual({ year: 2026, month: 9 })
		expect(parsePeriode('2026-13')).toBeNull()
		expect(parsePeriode(null)).toBeNull()
	})

	it('should fill missing months up to reference date and sort ascending', () => {
		const punkter = toMaanedPunkter(
			[
				{
					periode: '2026-07',
					antallNyeBestillinger: 3,
					antallGjenopprettedeBestillinger: 1,
					antallNyePersoner: 10,
				},
				{
					periode: '2026-05',
					antallNyeBestillinger: 1,
					antallGjenopprettedeBestillinger: 0,
					antallNyePersoner: 2,
				},
			],
			new Date(2026, 8, 15),
		)

		expect(punkter.map((punkt) => punkt.key)).toEqual([
			'2026-05',
			'2026-06',
			'2026-07',
			'2026-08',
			'2026-09',
		])
		expect(punkter[2]).toMatchObject({ nyeBestillinger: 3, gjenopprettinger: 1, nyePersoner: 10 })
		expect(punkter.filter(harAktivitet).map((punkt) => punkt.key)).toEqual(['2026-05', '2026-07'])
	})

	it('should return no points when oversikt is empty', () => {
		expect(toMaanedPunkter([])).toEqual([])
	})

	it('should limit trend to last 12 months unless all history is selected', () => {
		const punkter = toMaanedPunkter(
			[{ periode: '2024-01', antallNyeBestillinger: 1 }],
			new Date(2026, 0, 1),
		)

		expect(filterMaanedPunkter(punkter, MONTH_SCOPE_LAST_12)).toHaveLength(12)
		expect(filterMaanedPunkter(punkter, MONTH_SCOPE_ALL)).toHaveLength(25)
	})

	it('should build year and month options from active periods', () => {
		const aktive = toMaanedPunkter(
			[
				{ periode: '2025-11', antallNyeBestillinger: 1 },
				{ periode: '2026-02', antallGjenopprettedeBestillinger: 2 },
			],
			new Date(2026, 1, 1),
		).filter(harAktivitet)

		expect(toAarOptions(aktive)).toEqual(['2026', '2025'])
		expect(toMaanedOptions(aktive, 2026)).toEqual([{ value: '2026-02', label: 'Februar' }])
	})

	it('should accumulate yearly totals in one pass and pick the most active month', () => {
		const punkter = toMaanedPunkter(
			[
				{ periode: '2025-03', antallNyeBestillinger: 1, antallNyePersoner: 5 },
				{
					periode: '2025-11',
					antallNyeBestillinger: 6,
					antallGjenopprettedeBestillinger: 1,
					antallNyePersoner: 40,
				},
				{ periode: '2026-02', antallNyeBestillinger: 2, antallGjenopprettedeBestillinger: 3 },
			],
			new Date(2026, 9, 1),
		)

		const sammendrag = toAarSammendrag(punkter)

		expect(sammendrag.get(2025)).toMatchObject({
			nyePersoner: 45,
			nyeBestillinger: 7,
			gjenopprettinger: 1,
			aktiveMaaneder: 2,
		})
		expect(sammendrag.get(2025)?.mestAktiveMaaned?.key).toBe('2025-11')
		expect(sammendrag.get(2026)).toMatchObject({ gjenopprettinger: 3, aktiveMaaneder: 1 })
		expect(sammendrag.get(2026)?.mestAktiveMaaned?.key).toBe('2026-02')
	})

	it('should accumulate persons created in Dolly and imported from Testnorge per month and year', () => {
		const punkter = toMaanedPunkter(
			[
				{
					periode: '2026-09',
					antallNyeBestillinger: 2,
					antallNyePersoner: 5,
					andelOpprettedeDollyPersoner: 4,
					andelImporterteTestnorgePersoner: 1,
				},
				{
					periode: '2026-10',
					antallNyeBestillinger: 1,
					antallNyePersoner: 3,
					andelOpprettedeDollyPersoner: 0,
					andelImporterteTestnorgePersoner: 3,
				},
			],
			new Date(2026, 9, 1),
		)

		expect(punkter[0]).toMatchObject({ dollyPersoner: 4, testnorgePersoner: 1 })
		expect(toAarSammendrag(punkter).get(2026)).toMatchObject({
			dollyPersoner: 4,
			testnorgePersoner: 4,
		})
	})

	it('should build new persons per day split by origin', () => {
		const matrise = toDagPersonMatrise(
			[
				{
					dato: '2026-10-02',
					antallNyeBestillinger: 3,
					andelOpprettedeDollyPersoner: 3,
					andelImporterteTestnorgePersoner: 0,
				},
				{
					dato: '2026-10-31',
					antallNyeBestillinger: 5,
					andelOpprettedeDollyPersoner: 10,
					andelImporterteTestnorgePersoner: 3,
				},
				{ dato: '2026-10-06', kriterier: [] },
			],
			{ year: 2026, month: 10 },
		)

		expect(matrise.dager).toHaveLength(31)
		expect(matrise.dollyPersoner[1]).toBe(3)
		expect(matrise.dollyPersoner[30]).toBe(10)
		expect(matrise.testnorgePersoner[30]).toBe(3)
		expect(matrise.nyeBestillinger[30]).toBe(5)
		expect(matrise.dollyPersoner[5]).toBe(0)
	})

	it('should leave out unspecified fagsystem and fagsystem with empty details', () => {
		const detaljertMedTommeDetaljer: BrukerBestillingerDTO[] = [
			{
				dato: '2026-10-02',
				kriterier: [
					{ fagsystem: 'Uspesifisert', antall: 4, detaljer: {} },
					{ fagsystem: 'Skattekort', antall: 2, detaljer: {} },
					{ fagsystem: 'Pensjon', antall: 1, detaljer: { 'Uforetrygd-true': 1 } },
				],
			},
		]

		expect(
			toFagsystemSummerUtenUspesifisert(toFagsystemSummer(detaljertMedTommeDetaljer)).map(
				(sum) => sum.fagsystem,
			),
		).toEqual(['Skattekort', 'Pensjon'])
		expect(toFagsystemerMedDetaljer(detaljertMedTommeDetaljer).map((sum) => sum.fagsystem)).toEqual(
			['Pensjon'],
		)
	})

	it('should title the current year as so far and earlier years as whole', () => {
		const referanseDato = new Date(2026, 9, 7)

		expect(aarSammendragTittel(2026, referanseDato)).toBe('Hittil i 2026')
		expect(aarSammendragTittel(2025, referanseDato)).toBe('Hele 2025')
	})

	it('should sum fagsystemer and hide Beskrivelse', () => {
		const summer = toFagsystemSummer(detaljert)

		expect(summer.map((sum) => [sum.fagsystem, sum.antall])).toEqual([
			['PdlData', 8],
			['Krrstub', 3],
			['Aareg', 2],
			['Arena', 1],
		])
		expect(summer[0].label).toBe('Persondata (PDL)')
	})

	it('should build a day by fagsystem matrix and group the rest into Andre', () => {
		const matrise = toDagFagsystemMatrise(detaljert, { year: 2026, month: 9 }, 3)

		expect(matrise.dager).toHaveLength(30)
		expect(matrise.serier.map((serie) => serie.label)).toEqual([
			'Persondata (PDL)',
			'Kontakt- og reservasjonsregisteret',
			'Andre',
		])
		expect(matrise.serier[0].data[1]).toBe(5)
		expect(matrise.serier[0].data[14]).toBe(3)
		expect(matrise.serier[2].data[1]).toBe(2)
		expect(matrise.serier[2].data[14]).toBe(1)
	})

	it('should parse detail keys on the last hyphen', () => {
		expect(parseDetaljNoekkel('Syntetisk-true')).toEqual({ label: 'Syntetisk', type: 'valg' })
		expect(parseDetaljNoekkel('Syntetisk-false')).toEqual({
			label: 'Ikke syntetisk',
			type: 'valg',
		})
		expect(parseDetaljNoekkel('Legg-til/endre-true')).toEqual({
			label: 'Legg til/endre på eksisterende person',
			type: 'valg',
		})
		expect(parseDetaljNoekkel('ArenaBrukertype-MED_SERVICEBEHOV')).toEqual({
			label: 'Brukertype: MED_SERVICEBEHOV',
			type: 'valg',
		})
		expect(parseDetaljNoekkel('Antall arbeidsforhold')).toEqual({
			label: 'Antall arbeidsforhold',
			type: 'antall',
		})
	})

	it('should list only fagsystemer with details', () => {
		expect(toFagsystemerMedDetaljer(detaljert).map((sum) => sum.fagsystem)).toEqual([
			'PdlData',
			'Aareg',
			'Arena',
		])
	})

	it('should sum details for a fagsystem with choices before counts', () => {
		expect(
			toFagsystemDetaljer(detaljert, 'PdlData').map((detalj) => [
				detalj.label,
				detalj.antall,
				detalj.type,
			]),
		).toEqual([
			['Syntetisk', 7, 'valg'],
			['Ikke syntetisk', 1, 'valg'],
			['Legg til/endre på eksisterende person', 1, 'valg'],
			['Navn', 2, 'antall'],
		])
	})
})
