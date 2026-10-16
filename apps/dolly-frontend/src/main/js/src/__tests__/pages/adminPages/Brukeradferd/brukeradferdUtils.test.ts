import {
	erForkortetLabel,
	fagsystemKortLabel,
	filtrerAdferd,
	filtrerPaaPunkt,
	toAdferdTidView,
	toFagsystemDetaljer,
	toFagsystemTotaler,
	toMaanederForVisning,
	toNoekkeltall,
	toPersonNoekkeltall,
	toPersonTidView,
	USPESIFISERT,
} from '@/pages/adminPages/Brukeradferd/brukeradferdUtils'
import type { DashboardBrukeradferdDTO } from '@/utils/hooks/useDashboard'

const RADER: DashboardBrukeradferdDTO[] = [
	{
		dato: '2026-09-02',
		antallNyeBestillinger: 4,
		antallNyePersoner: 16,
		andelOpprettedeDollyPersoner: 12,
		andelImporterteTestnorgePersoner: 4,
		kriterier: [
			{
				fagsystem: 'PdlData',
				antall: 10,
				detaljer: { 'Syntetisk-true': 8, 'Syntetisk-false': 2, Statsborgerskap: 3 },
			},
			{ fagsystem: 'Aareg', antall: 4, detaljer: { 'Antall arbeidsforhold': 6 } },
			{ fagsystem: USPESIFISERT, antall: 5, detaljer: {} },
		],
	},
	{
		dato: '2026-09-01',
		antallNyeBestillinger: 2,
		antallNyePersoner: 6,
		andelOpprettedeDollyPersoner: 6,
		andelImporterteTestnorgePersoner: 0,
		kriterier: [
			{
				fagsystem: 'PdlData',
				antall: 5,
				detaljer: { 'Syntetisk-true': 5, 'Legg-til/endre-true': 1 },
			},
			{ fagsystem: 'Krrstub', antall: 1, detaljer: {} },
		],
	},
	{
		dato: '2026-08-15',
		antallNyeBestillinger: 1,
		antallNyePersoner: 3,
		andelOpprettedeDollyPersoner: 0,
		andelImporterteTestnorgePersoner: 3,
		kriterier: [{ fagsystem: USPESIFISERT, antall: 3, detaljer: {} }],
	},
]

describe('brukeradferdUtils', () => {
	describe('filtrerAdferd', () => {
		it('should remove Uspesifisert and days without remaining kriterier', () => {
			const filtrert = filtrerAdferd(RADER)

			expect(filtrert.map((rad) => rad.dato)).toEqual(['2026-09-01', '2026-09-02'])
			expect(
				filtrert.flatMap((rad) => rad.kriterier).some((k) => k.fagsystem === USPESIFISERT),
			).toBe(false)
		})

		it('should also remove legacy Ingen data', () => {
			const filtrert = filtrerAdferd([
				{ dato: '2026-09-01', kriterier: [{ fagsystem: 'Ingen data', antall: 2, detaljer: null }] },
			])

			expect(filtrert).toEqual([])
		})

		it('should remove Beskrivelse since it is not a fagsystem', () => {
			const filtrert = filtrerAdferd([
				{ dato: '2026-09-01', kriterier: [{ fagsystem: 'Beskrivelse', antall: 3, detaljer: {} }] },
			])

			expect(filtrert).toEqual([])
		})

		it('should remove kriterier with zero antall', () => {
			const filtrert = filtrerAdferd([
				{ dato: '2026-09-01', kriterier: [{ fagsystem: 'Aareg', antall: 0, detaljer: null }] },
			])

			expect(filtrert).toEqual([])
		})
	})

	describe('toFagsystemTotaler', () => {
		it('should sum antall per fagsystem across days and sort descending', () => {
			const totaler = toFagsystemTotaler(filtrerAdferd(RADER))

			expect(totaler.map((total) => [total.fagsystem, total.antall])).toEqual([
				['PdlData', 15],
				['Aareg', 4],
				['Krrstub', 1],
			])
			expect(totaler[0].label).toBe('Persondata (PDL)')
			expect(totaler.reduce((sum, total) => sum + total.andel, 0)).toBeCloseTo(1)
		})
	})

	describe('toNoekkeltall', () => {
		it('should compute key figures from filtered data', () => {
			const filtrert = filtrerAdferd(RADER)
			const noekkeltall = toNoekkeltall(filtrert, toFagsystemTotaler(filtrert))

			expect(noekkeltall.totaltFagsystemvalg).toBe(20)
			expect(noekkeltall.fagsystemerIBruk).toBe(3)
			expect(noekkeltall.mestBrukt?.fagsystem).toBe('PdlData')
			expect(noekkeltall.dagerMedBestillinger).toBe(2)
		})

		it('should handle empty data', () => {
			const noekkeltall = toNoekkeltall([], [])

			expect(noekkeltall.totaltFagsystemvalg).toBe(0)
			expect(noekkeltall.mestBrukt).toBeNull()
		})
	})

	describe('toAdferdTidView', () => {
		it('should group per day when granularity is DAG', () => {
			const view = toAdferdTidView(filtrerAdferd(RADER), 'DAG')

			expect(view.punkter.map((punkt) => punkt.noekkel)).toEqual(['2026-09-01', '2026-09-02'])
			expect(view.punkter[0].kategori).toBe('01.09')
			expect(view.punkter[1].visning).toBe('02.09.2026')
			expect(view.punkter[1].total).toBe(14)
			expect(view.fagsystemNoekler).toEqual(['PdlData', 'Aareg', 'Krrstub'])
		})

		it('should group per month when granularity is MAANED', () => {
			const view = toAdferdTidView(filtrerAdferd(RADER), 'MAANED')

			expect(view.punkter).toHaveLength(1)
			expect(view.punkter[0].noekkel).toBe('2026-09')
			expect(view.punkter[0].perFagsystem.PdlData).toBe(15)
		})

		it('should keep every fagsystem as its own series without grouping into Andre', () => {
			const view = toAdferdTidView(filtrerAdferd(RADER), 'DAG')

			expect(view.punkter[0].perFagsystem).toEqual({ PdlData: 5, Krrstub: 1 })
			expect(view.punkter[1].perFagsystem).toEqual({ PdlData: 10, Aareg: 4 })
		})
	})

	describe('filtrerPaaPunkt', () => {
		it('should return rows for the selected day or month', () => {
			const filtrert = filtrerAdferd(RADER)

			expect(filtrerPaaPunkt(filtrert, '2026-09-02', 'DAG')).toHaveLength(1)
			expect(filtrerPaaPunkt(filtrert, '2026-09', 'MAANED')).toHaveLength(2)
		})
	})

	describe('toFagsystemDetaljer', () => {
		const tilPar = (punkter: { label: string; antall: number }[] = []) =>
			punkter.map((punkt) => [punkt.label, punkt.antall])

		it('should sum details across days and split choices from counts like Min statistikk', () => {
			const detaljer = toFagsystemDetaljer(filtrerAdferd(RADER))
			const pdl = detaljer.find((detalj) => detalj.fagsystem === 'PdlData')

			expect(pdl?.antall).toBe(15)
			expect(pdl?.label).toBe('Persondata (PDL)')
			expect(tilPar(pdl?.valg)).toEqual([
				['Syntetisk', 13],
				['Ikke syntetisk', 2],
				['Legg til/endre på eksisterende person', 1],
			])
			expect(tilPar(pdl?.oppfoeringer)).toEqual([['Statsborgerskap', 3]])
		})

		it('should treat Array-størrelse/antall as a count, not a choice', () => {
			const detaljer = toFagsystemDetaljer([
				{
					dato: '2026-09-01',
					kriterier: [
						{ fagsystem: 'Dokarkiv', antall: 2, detaljer: { 'Array-størrelse/antall': 3 } },
						{ fagsystem: 'Inntektstub', antall: 1, detaljer: { 'Array/matrise antall': 4 } },
					],
				},
			])

			const dokarkiv = detaljer.find((detalj) => detalj.fagsystem === 'Dokarkiv')
			expect(dokarkiv?.valg).toEqual([])
			expect(tilPar(dokarkiv?.oppfoeringer)).toEqual([['Antall oppføringer', 3]])
			expect(
				tilPar(detaljer.find((detalj) => detalj.fagsystem === 'Inntektstub')?.oppfoeringer),
			).toEqual([['Antall oppføringer', 4]])
		})

		it('should treat keys without dash as counts', () => {
			const detaljer = toFagsystemDetaljer(filtrerAdferd(RADER))

			expect(tilPar(detaljer.find((detalj) => detalj.fagsystem === 'Aareg')?.oppfoeringer)).toEqual(
				[['Antall arbeidsforhold', 6]],
			)
		})

		it('should leave out fagsystemer without any details and sort by antall', () => {
			const detaljer = toFagsystemDetaljer(filtrerAdferd(RADER))

			expect(detaljer.map((detalj) => detalj.fagsystem)).toEqual(['PdlData', 'Aareg'])
		})

		it('should accept numeric strings and ignore zero values', () => {
			const detaljer = toFagsystemDetaljer([
				{
					dato: '2026-09-01',
					kriterier: [
						{
							fagsystem: 'Arena',
							antall: 2,
							detaljer: { 'ArenaBrukertype-UTEN_SERVICEBEHOV': '2', 'Arena-ingen': 0 },
						},
					],
				},
			])

			expect(detaljer[0].valg.map((punkt) => punkt.antall)).toEqual([2])
		})
	})

	describe('toMaanederForVisning', () => {
		const tilgjengelige = ['2025-11', '2025-12', '2026-01', '2026-02']

		it('should return only the selected month for MAANED', () => {
			expect(toMaanederForVisning('2026-01', 'maaned', tilgjengelige)).toEqual([
				{ year: 2026, month: 'JANUARY', interval: '2026-01' },
			])
		})

		it('should return every available month in the selected year for AAR', () => {
			expect(
				toMaanederForVisning('2026-01', 'aar', [...tilgjengelige].reverse()).map((m) => m.interval),
			).toEqual(['2026-01', '2026-02'])
			expect(toMaanederForVisning('2025-11', 'aar', tilgjengelige).map((m) => m.month)).toEqual([
				'NOVEMBER',
				'DECEMBER',
			])
		})

		it('should return empty list without an available selected interval', () => {
			expect(toMaanederForVisning('', 'aar', tilgjengelige)).toEqual([])
			expect(toMaanederForVisning('2024-05', 'maaned', tilgjengelige)).toEqual([])
		})
	})

	describe('toPersonNoekkeltall', () => {
		it('should sum person fields from all days, including days with only Uspesifisert', () => {
			expect(toPersonNoekkeltall(RADER)).toEqual({
				nyeBestillinger: 7,
				nyePersoner: 25,
				dollyPersoner: 18,
				testnorgePersoner: 7,
			})
		})

		it('should treat missing person fields as zero', () => {
			expect(toPersonNoekkeltall([{ dato: '2026-09-01', kriterier: [] }])).toEqual({
				nyeBestillinger: 0,
				nyePersoner: 0,
				dollyPersoner: 0,
				testnorgePersoner: 0,
			})
		})
	})

	describe('toPersonTidView', () => {
		it('should group person fields per day sorted by date', () => {
			const punkter = toPersonTidView(RADER, 'DAG')

			expect(punkter.map((punkt) => punkt.noekkel)).toEqual([
				'2026-08-15',
				'2026-09-01',
				'2026-09-02',
			])
			expect(punkter[2]).toMatchObject({
				visning: '02.09.2026',
				nyePersoner: 16,
				dollyPersoner: 12,
				testnorgePersoner: 4,
			})
		})

		it('should group per month and skip points without new persons or orders', () => {
			const punkter = toPersonTidView(
				[...RADER, { dato: '2026-07-01', antallNyePersoner: 0, kriterier: [] }],
				'MAANED',
			)

			expect(punkter.map((punkt) => [punkt.noekkel, punkt.nyePersoner])).toEqual([
				['2026-08', 3],
				['2026-09', 22],
			])
		})
	})

	describe('fagsystemKortLabel', () => {
		it('should abbreviate long fagsystem names for KPI cards', () => {
			expect(fagsystemKortLabel('Krrstub')).toBe('KRR')
			expect(erForkortetLabel('Krrstub')).toBe(true)
		})

		it('should add soft hyphens without counting it as an abbreviation', () => {
			expect(fagsystemKortLabel('Arbeidssoekerregisteret')).toBe('Arbeidssøker\u00ADregisteret')
			expect(erForkortetLabel('Arbeidssoekerregisteret')).toBe(false)
		})

		it('should fall back to the full label', () => {
			expect(fagsystemKortLabel('PdlData')).toBe('Persondata (PDL)')
			expect(erForkortetLabel('PdlData')).toBe(false)
		})

		it('should describe Inntekter as A-ordningen', () => {
			expect(fagsystemKortLabel('Inntekter')).toBe('Inntekter (A-ordningen)')
			expect(erForkortetLabel('Inntekter')).toBe(false)
		})
	})
})
