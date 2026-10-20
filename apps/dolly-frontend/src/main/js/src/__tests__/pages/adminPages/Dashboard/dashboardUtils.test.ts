import {
	asNumber,
	buildMonthlyDistributionView,
	fillMissingPersonDays,
	filterMonthlyTeamPoints,
	getPreviousBusinessPeriod,
	MONTH_SCOPE_ALL,
	MONTH_SCOPE_LAST_12,
	monthsInRange,
	toMonthlyDollyTeamPoints,
	toMonthlyIntervalOptions,
	toMonthlyOrganisasjonPoints,
	toMonthlyPersonPoints,
	toMonthlyTeamPoints,
	toMonthlyTrendData,
	toPersonTrendData,
	toTeamDistributionForInterval,
	withinDateRange,
} from '@/pages/adminPages/Dashboard/dashboardUtils'
import { toFeilGrupper } from '@/pages/adminPages/Dashboard/dashboardFeilUtils'
import { createDashboardMockData } from '@/pages/adminPages/Dashboard/dashboardMockData'

describe('dashboardUtils', () => {
	it('should include dates within selected range even when dates are reversed', () => {
		expect(withinDateRange('2026-05-10', '2026-05-12', '2026-05-01')).toBe(true)
		expect(withinDateRange('2026-05-20', '2026-05-12', '2026-05-01')).toBe(false)
	})

	it('should list every calendar month spanned by a multi-month range', () => {
		expect(monthsInRange('2026-04-15', '2026-06-10')).toEqual([
			{ year: 2026, month: 'APRIL' },
			{ year: 2026, month: 'MAY' },
			{ year: 2026, month: 'JUNE' },
		])
	})

	it('should span a year boundary and return a single month for a same-month range', () => {
		expect(monthsInRange('2025-12-20', '2026-01-05')).toEqual([
			{ year: 2025, month: 'DECEMBER' },
			{ year: 2026, month: 'JANUARY' },
		])
		expect(monthsInRange('2026-06-01', '2026-06-23')).toEqual([{ year: 2026, month: 'JUNE' }])
	})

	it('should return no months for empty or invalid ranges', () => {
		expect(monthsInRange('', '2026-06-10')).toEqual([])
		expect(monthsInRange('2026-06-10', '')).toEqual([])
		expect(monthsInRange('2026-06-10', '2026-06-01')).toEqual([])
	})

	it('should use the previous business period with monday covering the weekend', () => {
		const mondayPeriod = getPreviousBusinessPeriod(new Date('2026-06-08T12:00:00'))
		const tuesdayPeriod = getPreviousBusinessPeriod(new Date('2026-06-09T12:00:00'))

		expect(mondayPeriod.dates).toEqual(['2026-06-05', '2026-06-06', '2026-06-07'])
		expect(mondayPeriod.label).toBe('05.06.2026–07.06.2026')
		expect(mondayPeriod.title).toBe('Siste hverdag + helg')
		expect(mondayPeriod.buttonLabel).toBe('Siste hverdag + helg')
		expect(tuesdayPeriod.dates).toEqual(['2026-06-08'])
		expect(tuesdayPeriod.label).toBe('08.06.2026')
		expect(tuesdayPeriod.title).toBe('Siste hverdag')
		expect(tuesdayPeriod.buttonLabel).toBe('Siste hverdag')
	})

	it('should map monthly teams payload and keep points sorted by month', () => {
		const points = toMonthlyTeamPoints([
			{
				interval: '2026-06',
				totaltUnikeBrukere: 1,
				totaltAntallTeams: 1,
				teams: [{ team: 'Team Dolly', unikeBrukere: 2 }],
			},
			{
				interval: '2026-05',
				totaltUnikeBrukere: 3,
				totaltAntallTeams: 2,
				teams: [
					{ team: 'Team A', unikeBrukere: 4 },
					{ team: 'Team B', unikeBrukere: 2 },
				],
			},
		])

		expect(points).toHaveLength(2)
		expect(points.map((point) => point.interval)).toEqual(['2026-05', '2026-06'])
		expect(points[0].teams).toEqual([
			{ team: 'Team A', unikeBrukere: 4 },
			{ team: 'Team B', unikeBrukere: 2 },
		])
	})

	it('should support legacy teams payload by deriving month and totals', () => {
		const points = toMonthlyTeamPoints([
			{
				dato: '2026-04-15',
				entries: [
					{ teams: ['Team A'], antall: 2 },
					{ teams: ['Team A', 'Team B'], antall: 1 },
				],
			},
		])

		expect(points).toHaveLength(1)
		expect(points[0].interval).toBe('2026-04')
		expect(points[0].totaltAntallTeams).toBe(2)
		expect(points[0].totaltUnikeBrukere).toBe(4)
	})

	it('should map person data to trend points and normalize numbers', () => {
		const trend = toPersonTrendData([
			{
				dato: '2026-05-01',
				antallNyeBestillinger: 12,
				antallGjenopprettedeBestillinger: 1,
				antallNyePersoner: 10,
				andelOpprettedeDollyPersoner: 7,
				andelImporterteTestnorgePersoner: 5,
			},
		])

		expect(trend).toHaveLength(1)
		expect(trend[0].nyeBestillinger).toBe(12)
		expect(trend[0].gjenopprettedeBestillinger).toBe(1)
		expect(trend[0].nyePersoner).toBe(10)
		expect(trend[0].dollyPersoner).toBe(7)
		expect(trend[0].testnorgePersoner).toBe(5)
	})

	it('should fill missing days in selected period with zero values', () => {
		const completedDays = fillMissingPersonDays(
			[
				{
					dato: '2026-05-01',
					antallNyeBestillinger: 8,
					antallGjenopprettedeBestillinger: 1,
					antallNyePersoner: 10,
					andelOpprettedeDollyPersoner: 6,
					andelImporterteTestnorgePersoner: 2,
				},
				{
					dato: '2026-05-03',
					antallNyeBestillinger: 4,
					antallGjenopprettedeBestillinger: 0,
					antallNyePersoner: 5,
					andelOpprettedeDollyPersoner: 3,
					andelImporterteTestnorgePersoner: 1,
				},
			],
			'2026-05-01',
			'2026-05-03',
		)

		expect(completedDays).toEqual([
			{
				dato: '2026-05-01',
				antallNyeBestillinger: 8,
				antallGjenopprettedeBestillinger: 1,
				antallNyePersoner: 10,
				andelOpprettedeDollyPersoner: 6,
				andelImporterteTestnorgePersoner: 2,
			},
			{
				dato: '2026-05-02',
				antallNyeBestillinger: 0,
				antallGjenopprettedeBestillinger: 0,
				antallNyePersoner: 0,
				andelOpprettedeDollyPersoner: 0,
				andelImporterteTestnorgePersoner: 0,
			},
			{
				dato: '2026-05-03',
				antallNyeBestillinger: 4,
				antallGjenopprettedeBestillinger: 0,
				antallNyePersoner: 5,
				andelOpprettedeDollyPersoner: 3,
				andelImporterteTestnorgePersoner: 1,
			},
		])
	})

	it('should filter monthly points to last 12 months by default', () => {
		const points = Array.from({ length: 14 }, (_, index) => ({
			interval: `2025-${String(index + 1).padStart(2, '0')}`,
			intervalVisning: `måned ${index + 1}`,
			totaltUnikeBrukere: index + 1,
			totaltAntallTeams: index + 1,
			teams: [],
		}))

		expect(filterMonthlyTeamPoints(points, MONTH_SCOPE_LAST_12)).toHaveLength(12)
		expect(filterMonthlyTeamPoints(points, MONTH_SCOPE_ALL)).toHaveLength(14)
	})

	it('should return interval options and distribution for selected month', () => {
		const points = toMonthlyTeamPoints([
			{
				interval: '2026-06',
				totaltUnikeBrukere: 5,
				totaltAntallTeams: 2,
				teams: [
					{ team: 'Team A', unikeBrukere: 3 },
					{ team: 'Team B', unikeBrukere: 2 },
				],
			},
			{
				interval: '2026-05',
				totaltUnikeBrukere: 2,
				totaltAntallTeams: 1,
				teams: [{ team: 'Team C', unikeBrukere: 2 }],
			},
		])

		const trend = toMonthlyTrendData(points)
		const options = toMonthlyIntervalOptions(points)
		const distribution = toTeamDistributionForInterval(points, '2026-06')

		expect(trend.map((point) => point.interval)).toEqual(['2026-05', '2026-06'])
		expect(options.map((option) => option.value)).toEqual(['2026-05', '2026-06'])
		expect(distribution).toEqual([
			{ team: 'Team A', unikeBrukere: 3 },
			{ team: 'Team B', unikeBrukere: 2 },
		])
	})

	it('should build monthly distribution view with ordered years and month buttons', () => {
		const points = toMonthlyTeamPoints([
			{
				interval: '2024-05',
				totaltUnikeBrukere: 1,
				totaltAntallTeams: 1,
				teams: [{ team: 'Team Old', unikeBrukere: 1 }],
			},
			{
				interval: '2025-01',
				totaltUnikeBrukere: 2,
				totaltAntallTeams: 1,
				teams: [{ team: 'Team Mid', unikeBrukere: 2 }],
			},
			{
				interval: '2025-06',
				totaltUnikeBrukere: 3,
				totaltAntallTeams: 2,
				teams: [
					{ team: 'Team A', unikeBrukere: 2 },
					{ team: 'Team B', unikeBrukere: 1 },
				],
			},
		])

		const view = buildMonthlyDistributionView(points, '2025-06')

		expect(view.yearOptions).toEqual(['2024', '2025'])
		expect(view.selectedYear).toBe('2025')
		expect(view.monthOptions.map((option) => option.value)).toEqual(['2025-01', '2025-06'])
		expect(view.monthOptions.every((option) => !/\d{4}$/.test(option.label))).toBe(true)
		expect(view.selectedPoint?.interval).toBe('2025-06')
		expect(view.distribution).toEqual([
			{ team: 'Team A', unikeBrukere: 2 },
			{ team: 'Team B', unikeBrukere: 1 },
		])
	})

	it('should generate rich mock data and vary by cycle', () => {
		const first = createDashboardMockData(0)
		const second = createDashboardMockData(1)

		expect(first.dashboardBestillinger.length).toBe(300)
		expect(first.dashboardTeams.length).toBe(24)
		expect(first.dashboardOrganisasjoner.length).toBe(24)
		expect(first.dashboardDollyTeams.length).toBe(12)
		expect(first.dashboardBestillinger[0].dato <= first.dashboardBestillinger[299].dato).toBe(true)
		expect(first.dashboardTeams[0].interval <= first.dashboardTeams[23].interval).toBe(true)
		expect(first.dashboardOrganisasjoner[0].organisasjoner[0]).toHaveProperty('navn')
		expect(first.dashboardOrganisasjoner[0].organisasjoner[0]).toHaveProperty('organisasjonsnummer')
		expect(first.dashboardOrganisasjoner[0].organisasjoner[0]).toHaveProperty('organisasjonsform')
		expect(first.dashboardDollyTeams[0].teams[0]).toHaveProperty('navn')
		expect(first.dashboardDollyTeams[0].teams[0]).toHaveProperty('beskrivelse')
		expect(first.dashboardBestillinger[0]).toHaveProperty('antallNyeBestillinger')
		expect(first.dashboardBestillinger[0]).toHaveProperty('andelOpprettedeDollyPersoner')
		expect(first.dashboardBestillinger[0]).toHaveProperty('andelImporterteTestnorgePersoner')
		expect(first.dashboardBestillinger[0].antallNyePersoner).toBe(
			first.dashboardBestillinger[0].andelOpprettedeDollyPersoner +
				first.dashboardBestillinger[0].andelImporterteTestnorgePersoner,
		)
		expect(first.dashboardTeams[0].totaltUnikeBrukere).not.toBe(
			second.dashboardTeams[0].totaltUnikeBrukere,
		)
	})

	it('should map organisasjoner and dolly teams payloads to monthly points', () => {
		const organisasjoner = toMonthlyOrganisasjonPoints([
			{
				interval: '2026-05',
				totaltUnikeBrukere: 9,
				totaltAntallOrganisasjoner: 4,
				organisasjoner: [
					{
						navn: 'Org A',
						organisasjonsnummer: '123456789',
						organisasjonsform: 'AS',
						unikeBrukere: 3,
					},
				],
			},
		])
		const dollyTeams = toMonthlyDollyTeamPoints([
			{
				interval: '2026-05',
				totaltUnikeBrukere: 7,
				totaltAntallTeams: 2,
				teams: [{ navn: 'Team X', beskrivelse: 'Beskrivelse', unikeBrukere: 5 }],
			},
		])

		expect(organisasjoner[0].totaltAntallTeams).toBe(4)
		expect(organisasjoner[0].teams[0].team).toBe('Org A')
		expect(dollyTeams[0].totaltAntallTeams).toBe(2)
		expect(dollyTeams[0].teams[0].team).toBe('Team X')
	})

	it('should describe organisasjoner and disambiguate duplicate names', () => {
		const [punkt] = toMonthlyOrganisasjonPoints([
			{
				interval: '2026-05',
				totaltUnikeBrukere: 3,
				totaltAntallOrganisasjoner: 3,
				organisasjoner: [
					{
						navn: 'Org A',
						organisasjonsnummer: '111111111',
						organisasjonsform: 'AS',
						unikeBrukere: 3,
					},
					{
						navn: 'Ukjent organisasjon',
						organisasjonsnummer: '222222222',
						organisasjonsform: 'Ukjent organisasjonsform',
						unikeBrukere: 2,
					},
					{
						navn: 'Ukjent organisasjon',
						organisasjonsnummer: '333333333',
						organisasjonsform: 'Ukjent organisasjonsform',
						unikeBrukere: 1,
					},
				],
			},
		])

		expect(punkt.teams).toEqual([
			{ team: 'Org A', unikeBrukere: 3, beskrivelse: 'AS, org.nr. 111111111' },
			{
				team: 'Ukjent organisasjon (222222222)',
				unikeBrukere: 2,
				beskrivelse: 'Ukjent organisasjonsform, org.nr. 222222222',
			},
			{
				team: 'Ukjent organisasjon (333333333)',
				unikeBrukere: 1,
				beskrivelse: 'Ukjent organisasjonsform, org.nr. 333333333',
			},
		])
	})

	it('should keep Dolly team descriptions and drop blank ones', () => {
		const [punkt] = toMonthlyDollyTeamPoints([
			{
				interval: '2026-05',
				totaltAntallMedlemmer: 5,
				totaltAntallTeams: 2,
				teams: [
					{ navn: 'Team X', beskrivelse: 'Tester pensjon', antallMedlemmer: 3 },
					{ navn: 'Team Y', beskrivelse: '  ', antallMedlemmer: 2 },
				],
			},
		])

		expect(punkt.teams.map((team) => team.beskrivelse)).toEqual(['Tester pensjon', undefined])
		expect(toTeamDistributionForInterval([punkt], '2026-05')[0].beskrivelse).toBe('Tester pensjon')
	})

	it('should map oversikt to sorted monthly person points', () => {
		const punkter = toMonthlyPersonPoints([
			{ aar: 2026, maaned: 'JUNE', totaltAntallPersoner: 12, nye: 10, gjenopprettede: 2 },
			{ aar: 2026, maaned: 'MAY', totaltAntallPersoner: 5, nye: 5, gjenopprettede: 0 },
			{ aar: 2026, maaned: 'UKJENT', totaltAntallPersoner: 1, nye: 1, gjenopprettede: 0 },
		])

		expect(punkter.map((punkt) => punkt.interval)).toEqual(['2026-05', '2026-06'])
		expect(punkter[1]).toMatchObject({ totalt: 12, nye: 10, gjenopprettede: 2 })
		expect(filterMonthlyTeamPoints(punkter, MONTH_SCOPE_LAST_12)).toHaveLength(2)
	})

	it('should normalize date-interval values and filter to latest 12 months', () => {
		const points = toMonthlyTeamPoints([
			{
				interval: '2025-01-01',
				totaltUnikeBrukere: 1,
				totaltAntallTeams: 1,
				teams: [],
			},
			{
				interval: '2026-02-01',
				totaltUnikeBrukere: 2,
				totaltAntallTeams: 1,
				teams: [],
			},
		])

		expect(points.map((point) => point.interval)).toEqual(['2025-01', '2026-02'])
		expect(
			filterMonthlyTeamPoints(points, MONTH_SCOPE_LAST_12).map((point) => point.interval),
		).toEqual(['2025-01', '2026-02'])
	})

	describe('Monday period aggregation (siste hverdag + helg)', () => {
		const monday = new Date('2026-06-08T08:00:00')
		const period = getPreviousBusinessPeriod(monday)

		const fridayData = {
			dato: '2026-06-05',
			antallNyeBestillinger: 3,
			antallGjenopprettedeBestillinger: 1,
			antallNyePersoner: 4,
			andelOpprettedeDollyPersoner: 3,
			andelImporterteTestnorgePersoner: 1,
		}
		const saturdayData = {
			dato: '2026-06-06',
			antallNyeBestillinger: 0,
			antallGjenopprettedeBestillinger: 0,
			antallNyePersoner: 0,
			andelOpprettedeDollyPersoner: 0,
			andelImporterteTestnorgePersoner: 0,
		}
		const sundayData = {
			dato: '2026-06-07',
			antallNyeBestillinger: 0,
			antallGjenopprettedeBestillinger: 0,
			antallNyePersoner: 0,
			andelOpprettedeDollyPersoner: 0,
			andelImporterteTestnorgePersoner: 0,
		}

		it('should return three dates covering friday, saturday and sunday', () => {
			expect(period.dates).toEqual(['2026-06-05', '2026-06-06', '2026-06-07'])
			expect(period.title).toBe('Siste hverdag + helg')
		})

		it('should aggregate bestillinger summary correctly across all three days', () => {
			const periodData = [fridayData, saturdayData, sundayData].filter((row) =>
				period.dates.includes(row.dato),
			)

			const summary = periodData.reduce(
				(acc, row) => {
					acc.nye += asNumber(row.antallNyeBestillinger)
					acc.gjenopprettede += asNumber(row.antallGjenopprettedeBestillinger)
					return acc
				},
				{ nye: 0, gjenopprettede: 0 },
			)

			expect(summary.nye).toBe(3)
			expect(summary.gjenopprettede).toBe(1)
			expect(summary.nye + summary.gjenopprettede).toBe(4)
		})

		it('should aggregate feil groups from all three days', () => {
			const feilRader = [
				{
					ident: '10000000001',
					bestillingId: 10,
					sistOppdatert: '2026-06-05T09:00:00',
					type: 'PDL',
					pdlForvalterFeil: 'Feil på fredag',
				},
				{
					ident: '10000000002',
					bestillingId: 20,
					sistOppdatert: '2026-06-07T11:00:00',
					type: 'Aareg TESTNORGE-IDENT',
					aaregFeil: 'Feil på søndag',
				},
			]

			const feilGrupper = toFeilGrupper(feilRader)
			const noekler = feilGrupper.map((gruppe) => gruppe.feilNoekkel)

			expect(feilGrupper).toHaveLength(2)
			expect(noekler).toContain('pdlForvalterFeil')
			expect(noekler).toContain('aaregFeil')

			const pdlGruppe = feilGrupper.find((gruppe) => gruppe.feilNoekkel === 'pdlForvalterFeil')!
			const aaregGruppe = feilGrupper.find((gruppe) => gruppe.feilNoekkel === 'aaregFeil')!

			expect(pdlGruppe.rader).toHaveLength(1)
			expect(pdlGruppe.rader[0].ident).toBe('10000000001')
			expect(aaregGruppe.rader).toHaveLength(1)
			expect(aaregGruppe.rader[0].ident).toBe('10000000002')

			const uniqueBestillingIds = new Set(
				feilGrupper.flatMap((gruppe) => gruppe.rader.map((rad) => rad.bestillingId)),
			)
			expect(uniqueBestillingIds.size).toBe(2)
		})
	})
})
