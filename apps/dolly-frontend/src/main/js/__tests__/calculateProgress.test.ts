import { describe, expect, it } from 'vitest'
import {
	calculateProgress,
	getExpectedFagsystemer,
	mergeStatusWithExpected,
} from '@/components/bestilling/statusListe/BestillingProgresjon/fagsystemUtils'

describe('calculateProgress', () => {
	describe('single person (antallIdenter === 1)', () => {
		it('should show fagsystem progress when ordering 1 person', () => {
			const result = calculateProgress({
				antallIdenter: 1,
				antallLevert: 0,
				erOrganisasjon: false,
				statusList: [
					{ id: 'PDL', navn: 'PDL', statuser: [{ melding: 'OK' }] },
					{ id: 'SKATT', navn: 'Skatteetaten', statuser: [{ melding: 'OK' }] },
					{ id: 'ARENA', navn: 'Arena', statuser: [{ melding: 'Info: Oppretter' }] },
					{ id: 'INST', navn: 'INST', statuser: [] },
				],
			})

			expect(result.percent).toBe(50)
			expect(result.text).toBe('2 av 4 steg fullført')
		})

		it('should return 10% minimum when no fagsystems are completed', () => {
			const result = calculateProgress({
				antallIdenter: 1,
				antallLevert: 0,
				erOrganisasjon: false,
				statusList: [
					{ id: 'PDL', navn: 'PDL', statuser: [{ melding: 'Info: Oppretter' }] },
					{ id: 'SKATT', navn: 'Skatteetaten', statuser: [] },
				],
			})

			expect(result.percent).toBe(10)
			expect(result.text).toBe('0 av 2 steg fullført')
		})

		it('should return 100% when all fagsystems are completed', () => {
			const result = calculateProgress({
				antallIdenter: 1,
				antallLevert: 0,
				erOrganisasjon: false,
				statusList: [
					{ id: 'PDL', navn: 'PDL', statuser: [{ melding: 'OK' }] },
					{ id: 'SKATT', navn: 'Skatteetaten', statuser: [{ melding: 'OK' }] },
					{ id: 'ARENA', navn: 'Arena', statuser: [{ melding: 'OK' }] },
				],
			})

			expect(result.percent).toBe(100)
			expect(result.text).toBe('3 av 3 steg fullført')
		})

		it('should treat fagsystems with empty statuser as in-progress', () => {
			const result = calculateProgress({
				antallIdenter: 1,
				antallLevert: 0,
				erOrganisasjon: false,
				statusList: [
					{ id: 'PDL', navn: 'PDL', statuser: [] },
					{ id: 'SKATT', navn: 'Skatteetaten', statuser: [{ melding: 'OK' }] },
				],
			})

			expect(result.percent).toBe(50)
			expect(result.text).toBe('1 av 2 steg fullført')
		})

		it('should treat ADDING_TO_QUEUE, RUNNING, PENDING_COMPLETE as in-progress', () => {
			const result = calculateProgress({
				antallIdenter: 1,
				antallLevert: 0,
				erOrganisasjon: false,
				statusList: [
					{ id: 'EREG', navn: 'EREG', statuser: [{ melding: 'ADDING_TO_QUEUE' }] },
					{ id: 'EREG2', navn: 'EREG2', statuser: [{ melding: 'RUNNING' }] },
					{ id: 'EREG3', navn: 'EREG3', statuser: [{ melding: 'PENDING_COMPLETE' }] },
					{ id: 'PDL', navn: 'PDL', statuser: [{ melding: 'OK' }] },
				],
			})

			expect(result.percent).toBe(25)
			expect(result.text).toBe('1 av 4 steg fullført')
		})

		it('should treat Deployer and Pågående as in-progress', () => {
			const result = calculateProgress({
				antallIdenter: 1,
				antallLevert: 0,
				erOrganisasjon: false,
				statusList: [
					{ id: 'A', navn: 'A', statuser: [{ melding: 'Deployer' }] },
					{ id: 'B', navn: 'B', statuser: [{ melding: 'Pågående' }] },
					{ id: 'C', navn: 'C', statuser: [{ melding: 'OK' }] },
				],
			})

			expect(result.percent).toBeCloseTo(33.33, 1)
			expect(result.text).toBe('1 av 3 steg fullført')
		})

		it('should treat error statuses as completed', () => {
			const result = calculateProgress({
				antallIdenter: 1,
				antallLevert: 0,
				erOrganisasjon: false,
				statusList: [
					{ id: 'PDL', navn: 'PDL', statuser: [{ melding: 'OK' }] },
					{ id: 'SKATT', navn: 'Skatteetaten', statuser: [{ melding: 'Feil: Noe gikk galt' }] },
				],
			})

			expect(result.percent).toBe(100)
			expect(result.text).toBe('2 av 2 steg fullført')
		})

		it('should use expectedTotal as denominator when larger than statusList', () => {
			const result = calculateProgress({
				antallIdenter: 1,
				antallLevert: 0,
				erOrganisasjon: false,
				statusList: [{ id: 'PDL', navn: 'PDL', statuser: [{ melding: 'OK' }] }],
				expectedTotal: 6,
			})

			expect(result.percent).toBeCloseTo(16.67, 1)
			expect(result.text).toBe('1 av 6 steg fullført')
		})

		it('should use statusList.length when larger than expectedTotal', () => {
			const result = calculateProgress({
				antallIdenter: 1,
				antallLevert: 0,
				erOrganisasjon: false,
				statusList: [
					{ id: 'PDL', navn: 'PDL', statuser: [{ melding: 'OK' }] },
					{ id: 'SKATT', navn: 'Skatt', statuser: [{ melding: 'OK' }] },
					{ id: 'ARENA', navn: 'Arena', statuser: [{ melding: 'OK' }] },
					{ id: 'EXTRA', navn: 'Extra', statuser: [{ melding: 'OK' }] },
				],
				expectedTotal: 3,
			})

			expect(result.percent).toBe(100)
			expect(result.text).toBe('4 av 4 steg fullført')
		})

		it('should use expectedTotal when statusList is empty', () => {
			const result = calculateProgress({
				antallIdenter: 1,
				antallLevert: 0,
				erOrganisasjon: false,
				statusList: [],
				expectedTotal: 6,
			})

			expect(result.percent).toBe(10)
			expect(result.text).toBe('0 av 6 steg fullført')
		})

		it('should fall back to ident progress when both statusList and expectedTotal are empty', () => {
			const result = calculateProgress({
				antallIdenter: 1,
				antallLevert: 0,
				erOrganisasjon: false,
				statusList: [],
			})

			expect(result.percent).toBe(10)
			expect(result.text).toBe('Oppretter 0 av 1')
		})
	})

	describe('multiple persons (antallIdenter > 1)', () => {
		it('should use ident-based progress for multiple persons', () => {
			const result = calculateProgress({
				antallIdenter: 5,
				antallLevert: 3,
				erOrganisasjon: false,
				statusList: [{ id: 'PDL', navn: 'PDL', statuser: [{ melding: 'OK' }] }],
			})

			expect(result.percent).toBe(60)
			expect(result.text).toBe('Oppretter 3 av 5')
		})

		it('should return 10% minimum when no identer are delivered', () => {
			const result = calculateProgress({
				antallIdenter: 5,
				antallLevert: 0,
				erOrganisasjon: false,
				statusList: [],
			})

			expect(result.percent).toBe(10)
			expect(result.text).toBe('Oppretter 0 av 5')
		})
	})

	describe('organisasjon bestilling', () => {
		it('should always use ident-based progress for org bestillinger', () => {
			const result = calculateProgress({
				antallIdenter: 1,
				antallLevert: 0,
				erOrganisasjon: true,
				statusList: [{ id: 'EREG', navn: 'EREG', statuser: [{ melding: 'OK' }] }],
			})

			expect(result.percent).toBe(10)
			expect(result.text).toBe('Oppretter 0 av 1')
		})
	})
})

describe('getExpectedFagsystemer', () => {
	it('should return empty for bestilling without pdldata', () => {
		const result = getExpectedFagsystemer({})

		expect(result).toHaveLength(0)
	})

	it('should return PDL entries when pdldata is present', () => {
		const result = getExpectedFagsystemer({ pdldata: { person: {} } })

		expect(result).toHaveLength(3)
		expect(result.map((f) => f.id)).toEqual(['PDL_FORVALTER', 'PDL_ORDRE', 'PDL_PERSONSTATUS'])
	})

	it('should include fagsystems based on non-null bestilling fields', () => {
		const result = getExpectedFagsystemer({
			pdldata: { person: {} },
			aareg: [{ someData: true }],
			krrstub: { registered: true },
			skattekort: { year: 2024 },
		})

		expect(result).toHaveLength(6)
		const ids = result.map((f) => f.id)
		expect(ids).toContain('PDL_FORVALTER')
		expect(ids).toContain('AAREG')
		expect(ids).toContain('KRRSTUB')
		expect(ids).toContain('SKATTEKORT')
	})

	it('should skip empty arrays', () => {
		const result = getExpectedFagsystemer({
			aareg: [],
			krrstub: { registered: true },
		})

		const ids = result.map((f) => f.id)
		expect(ids).not.toContain('AAREG')
		expect(ids).toContain('KRRSTUB')
	})

	it('should return empty array for null bestilling', () => {
		expect(getExpectedFagsystemer(null)).toEqual([])
		expect(getExpectedFagsystemer(undefined)).toEqual([])
	})

	it('should include nested pensjon sub-fields', () => {
		const result = getExpectedFagsystemer({
			pdldata: { person: {} },
			pensjonforvalter: {
				uforetrygd: { uforetidspunkt: '2024-01-01' },
				alderspensjon: { uttaksgrad: 100 },
			},
		})

		const ids = result.map((f) => f.id)
		expect(ids).toContain('PDL_FORVALTER')
		expect(ids).toContain('PEN_FORVALTER')
		expect(ids).toContain('PEN_UT')
		expect(ids).toContain('PEN_AP')
		expect(ids).not.toContain('PEN_INNTEKT')
		expect(ids).not.toContain('TP_FORVALTER')
	})

	it('should include nested arena sub-fields', () => {
		const result = getExpectedFagsystemer({
			pdldata: { person: {} },
			arenaforvalter: {
				aap: [{ vedtaktype: 'O' }],
				dagpenger: [{ rettighetKode: 'DAGO' }],
			},
		})

		const ids = result.map((f) => f.id)
		expect(ids).toContain('ARENA_BRUKER')
		expect(ids).toContain('ARENA_AAP')
		expect(ids).toContain('ARENA_DAGP')
		expect(ids).not.toContain('ARENA_AAP115')
	})

	it('should deduplicate when both inntekt and generertInntekt are present', () => {
		const result = getExpectedFagsystemer({
			pensjonforvalter: {
				inntekt: { fomAar: 2020 },
				generertInntekt: { generer: {} },
			},
		})

		const penInntektEntries = result.filter((f) => f.id === 'PEN_INNTEKT')
		expect(penInntektEntries).toHaveLength(1)
	})

	it('should not predict fagsystems for Tenor import bestillinger', () => {
		const result = getExpectedFagsystemer({
			importPersoner: [{ ident: '12345678901' }],
		})

		expect(result).toHaveLength(0)
	})
})

describe('mergeStatusWithExpected', () => {
	it('should add pending entries for missing fagsystems', () => {
		const actual = [
			{ id: 'PDL_FORVALTER', navn: 'Opprett persondetaljer', statuser: [{ melding: 'OK' }] },
		]
		const expected = [
			{ id: 'PDL_FORVALTER', navn: 'Opprett persondetaljer' },
			{ id: 'AAREG', navn: 'Arbeidsregister (AAREG)' },
			{ id: 'KRRSTUB', navn: 'Digital kontaktinformasjon (DKIF)' },
		]

		const result = mergeStatusWithExpected(actual, expected)

		expect(result).toHaveLength(3)
		expect(result[0].id).toBe('PDL_FORVALTER')
		expect(result[0].statuser).toEqual([{ melding: 'OK' }])
		expect(result[1].id).toBe('AAREG')
		expect(result[1].statuser).toEqual([])
		expect(result[2].id).toBe('KRRSTUB')
		expect(result[2].statuser).toEqual([])
	})

	it('should not duplicate already present entries', () => {
		const actual = [
			{ id: 'AAREG', navn: 'AAREG', statuser: [{ melding: 'OK' }] },
			{ id: 'KRRSTUB', navn: 'KRRSTUB', statuser: [{ melding: 'OK' }] },
		]
		const expected = [
			{ id: 'AAREG', navn: 'Arbeidsregister (AAREG)' },
			{ id: 'KRRSTUB', navn: 'Digital kontaktinformasjon (DKIF)' },
		]

		const result = mergeStatusWithExpected(actual, expected)

		expect(result).toHaveLength(2)
	})

	it('should return actual status as-is when expectedFagsystemer is empty', () => {
		const actual = [{ id: 'PDL', navn: 'PDL', statuser: [{ melding: 'OK' }] }]
		const result = mergeStatusWithExpected(actual, [])

		expect(result).toStrictEqual(actual)
	})
})
