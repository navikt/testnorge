import { format, subDays, subMonths } from 'date-fns'
import {
	type DashboardDollyTeamEntryDTO,
	type DashboardDollyTeamsDTO,
	type DashboardOrganisasjonerDTO,
	type DashboardPersonerDTO,
	type DashboardTeamsDTO,
} from '@/utils/hooks/useDashboard'

export const createDashboardMockData = (
	cycle: number,
): {
	dashboardPersoner: DashboardPersonerDTO[]
	dashboardTeams: DashboardTeamsDTO[]
	dashboardOrganisasjoner: DashboardOrganisasjonerDTO[]
	dashboardDollyTeams: DashboardDollyTeamsDTO[]
} => {
	const zeroFeilMode = Math.abs(cycle) % 5 === 0

	const dashboardPersoner = Array.from({ length: 300 }, (_, index) => {
		const dayOffset = 89 - index
		const dato = format(subDays(new Date(), dayOffset), 'yyyy-MM-dd')
		const nye = Math.floor(Math.random() * 60) + 10
		const gjenopprettede = Math.floor(Math.random() * 40) + 5
		const pdlFeil = zeroFeilMode ? 0 : Math.floor(Math.random() * 41)
		const andreFeil = zeroFeilMode ? 0 : Math.floor(Math.random() * 41)
		const personerTotalt = nye + gjenopprettede

		return { dato, personerTotalt, nye, gjenopprettede, pdlFeil, andreFeil }
	})

	const teamNames = [
		'Team Dolly',
		'Team Villsau',
		'Team Gjeter',
		'Team Flokk',
		'Team Saueull',
		'Team Beite',
		'Team Ulvevakt',
		'Team Lammeskank',
	]

	const dashboardTeams = Array.from({ length: 24 }, (_, index) => {
		const monthOffset = 23 - index
		const interval = format(subMonths(new Date(), monthOffset), 'yyyy-MM')
		const activeTeamCount = Math.floor(Math.random() * 5) + 4

		const teams = Array.from({ length: activeTeamCount }, () => {
			const nameIndex = Math.floor(Math.random() * teamNames.length)
			return { team: teamNames[nameIndex], unikeBrukere: Math.floor(Math.random() * 45) + 6 }
		})

		return {
			interval,
			totaltUnikeBrukere: teams.reduce((sum, team) => sum + team.unikeBrukere, 0),
			totaltAntallTeams: activeTeamCount,
			teams,
		}
	})

	const organisasjonPool = [
		{ navn: 'Dolly Klone AS', organisasjonsform: 'AS', organisasjonsnummer: '100000001' },
		{ navn: 'Just Sheep-IT AS', organisasjonsform: 'AS', organisasjonsnummer: '100000002' },
		{ navn: 'Ullgjengen ASA', organisasjonsform: 'ASA', organisasjonsnummer: '100000003' },
		{ navn: 'Beit and svitsj Fond', organisasjonsform: 'FLI', organisasjonsnummer: '100000004' },
		{
			navn: 'Gjeterens Holdingselskap AS',
			organisasjonsform: 'AS',
			organisasjonsnummer: '100000005',
		},
		{ navn: 'Flokken Konsulting AS', organisasjonsform: 'AS', organisasjonsnummer: '100000006' },
		{ navn: 'Beitebakken Teknologi AS', organisasjonsform: 'AS', organisasjonsnummer: '100000007' },
		{ navn: 'Sauefabrikken AS', organisasjonsform: 'AS', organisasjonsnummer: '100000008' },
		{ navn: 'Villsau Ventures ASA', organisasjonsform: 'ASA', organisasjonsnummer: '100000009' },
		{ navn: 'Lammehuset AS', organisasjonsform: 'AS', organisasjonsnummer: '100000010' },
		{ navn: 'Sauefjord Kommune', organisasjonsform: 'KOMM', organisasjonsnummer: '100000011' },
		{ navn: 'Brokke Sauelag', organisasjonsform: 'FLI', organisasjonsnummer: '100000012' },
	]

	const dashboardOrganisasjoner: DashboardOrganisasjonerDTO[] = Array.from(
		{ length: 24 },
		(_, index) => {
			const monthOffset = 23 - index
			const interval = format(subMonths(new Date(), monthOffset), 'yyyy-MM')
			const activeOrgCount = Math.floor(Math.random() * 6) + 3
			const shuffledOrgs = [...organisasjonPool]
				.sort(() => Math.random() - 0.5)
				.slice(0, activeOrgCount)

			const organisasjoner = shuffledOrgs.map((org) => ({
				...org,
				unikeBrukere: Math.floor(Math.random() * 30) + 1,
			}))

			return {
				interval,
				totaltUnikeBrukere: organisasjoner.reduce((sum, org) => sum + org.unikeBrukere, 0),
				totaltAntallOrganisasjoner: activeOrgCount,
				organisasjoner,
			}
		},
	)

	const dollyTeamPool: DashboardDollyTeamEntryDTO[] = [
		{ navn: 'Team Dolly', beskrivelse: 'Kloning og syntetisering', unikeBrukere: 0 },
		{ navn: 'Team Villsau', beskrivelse: 'Frie sjeler i villamarken', unikeBrukere: 0 },
		{ navn: 'Team Lam', beskrivelse: 'Rask og varm problemløsning', unikeBrukere: 0 },
		{ navn: 'Team Saueull', beskrivelse: 'Myk datahåndtering siden 2023', unikeBrukere: 0 },
		{ navn: 'Team Gjeter', beskrivelse: 'Vi holder orden på flokken', unikeBrukere: 0 },
		{ navn: 'Team Beite', beskrivelse: 'Bredt nedslagsfelt, god avkastning', unikeBrukere: 0 },
		{ navn: 'Team Ramme', beskrivelse: 'Strukturert som et sauegjerde', unikeBrukere: 0 },
		{ navn: 'Team Flokk', beskrivelse: 'Sterkere sammen', unikeBrukere: 0 },
		{ navn: 'Team Ulvevakt', beskrivelse: 'Sikkerhet og overvåkning', unikeBrukere: 0 },
		{ navn: 'Team Ull', beskrivelse: 'Ny kraft i gammel beitemark', unikeBrukere: 0 },
	]

	const dashboardDollyTeams: DashboardDollyTeamsDTO[] = Array.from({ length: 12 }, (_, index) => {
		const monthOffset = 11 - index
		const interval = format(subMonths(new Date(), monthOffset), 'yyyy-MM')
		const activeTeamCount = Math.floor(Math.random() * 4) + 2
		const shuffledTeams = [...dollyTeamPool]
			.sort(() => Math.random() - 0.5)
			.slice(0, activeTeamCount)

		const teams = shuffledTeams.map((team) => ({
			...team,
			unikeBrukere: Math.floor(Math.random() * 8) + 1,
		}))

		return {
			interval,
			totaltUnikeBrukere: teams.reduce((sum, team) => sum + team.unikeBrukere, 0),
			totaltAntallTeams: activeTeamCount,
			teams,
		}
	})

	return { dashboardPersoner, dashboardTeams, dashboardOrganisasjoner, dashboardDollyTeams }
}
