import {
	fillMissingPersonDays,
	filterMonthlyTeamPoints,
	getPreviousBusinessPeriod,
	MONTH_SCOPE_ALL,
	MONTH_SCOPE_LAST_12,
	toGenericTrendDataset,
	toMonthlyDollyTeamPoints,
	toMonthlyIntervalOptions,
	toMonthlyOrganisasjonPoints,
	toMonthlyTeamPoints,
	toMonthlyTrendData,
	toPersonTrendData,
	toTeamDistributionForInterval,
	withinDateRange,
} from '@/pages/adminPages/Dashboard/dashboardUtils'
import { createDashboardMockData } from '@/pages/adminPages/Dashboard/dashboardMockData'

describe('dashboardUtils', () => {
	it('should include dates within selected range even when dates are reversed', () => {
		expect(withinDateRange('2026-05-10', '2026-05-12', '2026-05-01')).toBe(true)
		expect(withinDateRange('2026-05-20', '2026-05-12', '2026-05-01')).toBe(false)
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
				personerTotalt: 10,
				nye: 4,
				gjenopprettede: 1,
				pdlFeil: 0,
				andreFeil: 2,
			},
		])

		expect(trend).toHaveLength(1)
		expect(trend[0].personerTotalt).toBe(10)
		expect(trend[0].nye).toBe(4)
		expect(trend[0].gjenopprettede).toBe(1)
		expect(trend[0].pdlFeil).toBe(0)
		expect(trend[0].andreFeil).toBe(2)
	})

	it('should fill missing days in selected period with zero values', () => {
		const completedDays = fillMissingPersonDays(
			[
				{
					dato: '2026-05-01',
					personerTotalt: 10,
					nye: 4,
					gjenopprettede: 1,
					pdlFeil: 0,
					andreFeil: 2,
				},
				{
					dato: '2026-05-03',
					personerTotalt: 5,
					nye: 2,
					gjenopprettede: 0,
					pdlFeil: 1,
					andreFeil: 0,
				},
			],
			'2026-05-01',
			'2026-05-03',
		)

		expect(completedDays).toEqual([
			{
				dato: '2026-05-01',
				personerTotalt: 10,
				nye: 4,
				gjenopprettede: 1,
				pdlFeil: 0,
				andreFeil: 2,
			},
			{
				dato: '2026-05-02',
				personerTotalt: 0,
				nye: 0,
				gjenopprettede: 0,
				pdlFeil: 0,
				andreFeil: 0,
			},
			{
				dato: '2026-05-03',
				personerTotalt: 5,
				nye: 2,
				gjenopprettede: 0,
				pdlFeil: 1,
				andreFeil: 0,
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
		expect(options.map((option) => option.value)).toEqual(['2026-06', '2026-05'])
		expect(distribution).toEqual([
			{ team: 'Team A', unikeBrukere: 3 },
			{ team: 'Team B', unikeBrukere: 2 },
		])
	})

	it('should generate rich mock data and vary by cycle', () => {
		const first = createDashboardMockData(0)
		const second = createDashboardMockData(1)

		expect(first.dashboardPersoner.length).toBe(300)
		expect(first.dashboardTeams.length).toBe(24)
		expect(first.dashboardOrganisasjoner.length).toBe(24)
		expect(first.dashboardDollyTeams.length).toBe(12)
		expect(first.dashboardPersoner[0].dato <= first.dashboardPersoner[299].dato).toBe(true)
		expect(first.dashboardTeams[0].interval <= first.dashboardTeams[23].interval).toBe(true)
		expect(first.dashboardOrganisasjoner[0].organisasjoner[0]).toHaveProperty('navn')
		expect(first.dashboardOrganisasjoner[0].organisasjoner[0]).toHaveProperty('organisasjonsnummer')
		expect(first.dashboardOrganisasjoner[0].organisasjoner[0]).toHaveProperty('organisasjonsform')
		expect(first.dashboardDollyTeams[0].teams[0]).toHaveProperty('navn')
		expect(first.dashboardDollyTeams[0].teams[0]).toHaveProperty('beskrivelse')
		expect(
			first.dashboardPersoner.every((person) => person.pdlFeil === 0 && person.andreFeil === 0),
		).toBe(true)
		expect(
			second.dashboardPersoner.some((person) => person.pdlFeil > 0 || person.andreFeil > 0),
		).toBe(true)
		expect(first.dashboardPersoner[0].personerTotalt).not.toBe(
			second.dashboardPersoner[0].personerTotalt,
		)
		expect(first.dashboardTeams[0].totaltUnikeBrukere).not.toBe(
			second.dashboardTeams[0].totaltUnikeBrukere,
		)
	})

	it('should map generic trend dataset and fill missing days with zeroes', () => {
		const dataset = toGenericTrendDataset(
			[
				{ dato: '2026-06-01', opprettet: 2, feil: 1 },
				{ dato: '2026-06-03', opprettet: 4, feil: 0 },
			],
			'2026-06-01',
			'2026-06-03',
		)

		expect(dataset.labels).toEqual(['01.06.2026', '02.06.2026', '03.06.2026'])
		expect(dataset.series.find((series) => series.key === 'opprettet')?.data).toEqual([2, 0, 4])
		expect(dataset.series.find((series) => series.key === 'feil')?.data).toEqual([1, 0, 0])
	})

	it('should map monthly interval datasets without day-filling', () => {
		const dataset = toGenericTrendDataset(
			[
				{ interval: '2026-04', totaltUnikeBrukere: 5, totaltAntallTeams: 2 },
				{ interval: '2026-05', totaltUnikeBrukere: 8, totaltAntallTeams: 3 },
			],
			'',
			'',
		)

		expect(dataset.labels).toEqual(['apr. 2026', 'mai 2026'])
		expect(dataset.series.find((series) => series.key === 'totaltUnikeBrukere')?.data).toEqual([
			5, 8,
		])
		expect(dataset.series.find((series) => series.key === 'totaltAntallTeams')?.data).toEqual([
			2, 3,
		])
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
})
