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
		'Team Pensjon',
		'Team Dagpenger',
		'Team Sykepenger',
		'Team Foreldrepenger',
		'Team AAP',
		'Team Arbeidsoppfølging',
		'Team Kontaktsenter',
		'Team Plattform',
		'Team Inntekt',
		'Team Registerdata',
		'Team Tilgangsstyring',
		'Team Rekruttering',
		'Team Oppfølging',
		'Team Vedtak',
		'Team Utbetaling',
		'Team Integrasjoner',
		'Team Kodeverk',
		'Team Fakturering',
		'Team Klage',
		'Team Brev',
		'Team Portal',
		'Team Modernisering',
		'Team Søknad',
		'Team Feriepenger',
		'Team Barnetrygd',
		'Team Enslig forsørger',
		'Team Kontantstøtte',
		'Team Grunnstønad',
		'Team Hjelpemidler',
		'Team Uføretrygd',
		'Team Alderspensjon',
		'Team Ytelsesutredning',
		'Team Behandling',
		'Team Statistikk',
		'Team Ettersendelse',
		'Team Datadeling',
		'Team Brukerflate',
		'Team Nettløsninger',
		'Team Mobilapp',
		'Team Dokumentasjon',
		'Team Sikkerhet',
		'Team Infrastruktur',
		'Team DevOps',
		'Team Analyse',
		'Team Maskinlæring',
		'Team Testdata',
		'Team Oversikt',
	]

	const dashboardTeams = Array.from({ length: 24 }, (_, index) => {
		const monthOffset = 23 - index
		const interval = format(subMonths(new Date(), monthOffset), 'yyyy-MM')
		const isLargeMonth = Math.random() < 0.35
		const activeTeamCount = isLargeMonth
			? Math.floor(Math.random() * 15) + 50
			: Math.floor(Math.random() * 5) + 4
		const shuffledNames = [...teamNames].sort(() => Math.random() - 0.5).slice(0, activeTeamCount)

		const teams = shuffledNames.map((name) => ({
			team: name,
			unikeBrukere: Math.floor(Math.random() * 45) + 6,
		}))

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
		{ navn: 'Norsk Ull og Tekstil AS', organisasjonsform: 'AS', organisasjonsnummer: '100000013' },
		{ navn: 'Beitemarks Gruppen ASA', organisasjonsform: 'ASA', organisasjonsnummer: '100000014' },
		{ navn: 'Flokktjenester DA', organisasjonsform: 'DA', organisasjonsnummer: '100000015' },
		{ navn: 'Saueklipp og Partners AS', organisasjonsform: 'AS', organisasjonsnummer: '100000016' },
		{ navn: 'Rammeverkstedet AS', organisasjonsform: 'AS', organisasjonsnummer: '100000017' },
		{ navn: 'Gjeterhund Consulting AS', organisasjonsform: 'AS', organisasjonsnummer: '100000018' },
		{ navn: 'Mjølkeveien BA', organisasjonsform: 'BA', organisasjonsnummer: '100000019' },
		{ navn: 'Fjellbonde IT AS', organisasjonsform: 'AS', organisasjonsnummer: '100000020' },
		{ navn: 'Lammevillan Holding AS', organisasjonsform: 'AS', organisasjonsnummer: '100000021' },
		{ navn: 'Norges Saueforening', organisasjonsform: 'FLI', organisasjonsnummer: '100000022' },
		{ navn: 'Ullstrikk og Data AS', organisasjonsform: 'AS', organisasjonsnummer: '100000023' },
		{ navn: 'Beitedata ANS', organisasjonsform: 'ANS', organisasjonsnummer: '100000024' },
		{ navn: 'Sauerøkt Invest AS', organisasjonsform: 'AS', organisasjonsnummer: '100000025' },
		{ navn: 'Høyfjell Systemer AS', organisasjonsform: 'AS', organisasjonsnummer: '100000026' },
		{ navn: 'Bakkebø Teknologi AS', organisasjonsform: 'AS', organisasjonsnummer: '100000027' },
		{ navn: 'Klonehuset NUF', organisasjonsform: 'NUF', organisasjonsnummer: '100000028' },
		{ navn: 'Testdyrene ASA', organisasjonsform: 'ASA', organisasjonsnummer: '100000029' },
		{ navn: 'Hage og Beite AS', organisasjonsform: 'AS', organisasjonsnummer: '100000030' },
		{ navn: 'Rugde Regnskap AS', organisasjonsform: 'AS', organisasjonsnummer: '100000031' },
		{ navn: 'Polarull Finans AS', organisasjonsform: 'AS', organisasjonsnummer: '100000032' },
		{ navn: 'Kloningslaboratoriet AS', organisasjonsform: 'AS', organisasjonsnummer: '100000033' },
		{ navn: 'Sauestad Kommune', organisasjonsform: 'KOMM', organisasjonsnummer: '100000034' },
		{ navn: 'Ulvegard Sikkerhet AS', organisasjonsform: 'AS', organisasjonsnummer: '100000035' },
		{ navn: 'Vinterbeite Kraft AS', organisasjonsform: 'AS', organisasjonsnummer: '100000036' },
		{ navn: 'Digitale Sauer AS', organisasjonsform: 'AS', organisasjonsnummer: '100000037' },
		{ navn: 'Ramme og Bærekraft FLI', organisasjonsform: 'FLI', organisasjonsnummer: '100000038' },
		{ navn: 'Gjeterdata AS', organisasjonsform: 'AS', organisasjonsnummer: '100000039' },
		{ navn: 'Fleecetech ASA', organisasjonsform: 'ASA', organisasjonsnummer: '100000040' },
		{ navn: 'Lamskin Solutions AS', organisasjonsform: 'AS', organisasjonsnummer: '100000041' },
		{ navn: 'Merinoull Kapital AS', organisasjonsform: 'AS', organisasjonsnummer: '100000042' },
		{ navn: 'Fårehus Gruppen AS', organisasjonsform: 'AS', organisasjonsnummer: '100000043' },
		{ navn: 'Sauemelk BA', organisasjonsform: 'BA', organisasjonsnummer: '100000044' },
		{ navn: 'Heimberg Konsern ASA', organisasjonsform: 'ASA', organisasjonsnummer: '100000045' },
		{ navn: 'Klepp og Klone DA', organisasjonsform: 'DA', organisasjonsnummer: '100000046' },
		{ navn: 'Norsk Syntetisering AS', organisasjonsform: 'AS', organisasjonsnummer: '100000047' },
		{ navn: 'Bekkefaret Utvikling AS', organisasjonsform: 'AS', organisasjonsnummer: '100000048' },
		{ navn: 'Fjellfår IT AS', organisasjonsform: 'AS', organisasjonsnummer: '100000049' },
		{ navn: 'Åsebø Holding AS', organisasjonsform: 'AS', organisasjonsnummer: '100000050' },
		{ navn: 'Grønnfôr Systems AS', organisasjonsform: 'AS', organisasjonsnummer: '100000051' },
		{ navn: 'Svarttrost Data AS', organisasjonsform: 'AS', organisasjonsnummer: '100000052' },
		{ navn: 'Tindeland Teknologi AS', organisasjonsform: 'AS', organisasjonsnummer: '100000053' },
		{ navn: 'Fjordeng Regnskap AS', organisasjonsform: 'AS', organisasjonsnummer: '100000054' },
		{ navn: 'Lyngsmo Konsulting AS', organisasjonsform: 'AS', organisasjonsnummer: '100000055' },
	]

	const dashboardOrganisasjoner: DashboardOrganisasjonerDTO[] = Array.from(
		{ length: 24 },
		(_, index) => {
			const monthOffset = 23 - index
			const interval = format(subMonths(new Date(), monthOffset), 'yyyy-MM')
			const isLargeMonth = Math.random() < 0.35
			const activeOrgCount = isLargeMonth
				? Math.floor(Math.random() * 15) + 50
				: Math.floor(Math.random() * 6) + 3
			const shuffledOrgs = [...organisasjonPool]
				.sort(() => Math.random() - 0.5)
				.slice(0, Math.min(activeOrgCount, organisasjonPool.length))

			const organisasjoner = shuffledOrgs.map((org) => ({
				...org,
				unikeBrukere: Math.floor(Math.random() * 30) + 1,
			}))

			return {
				interval,
				totaltUnikeBrukere: organisasjoner.reduce((sum, org) => sum + org.unikeBrukere, 0),
				totaltAntallOrganisasjoner: organisasjoner.length,
				organisasjoner,
			}
		},
	)

	const dollyTeamPool: DashboardDollyTeamEntryDTO[] = [
		{ navn: 'Team Dolly', beskrivelse: 'Kloning og syntetisering', antallMedlemmer: 0 },
		{ navn: 'Team Villsau', beskrivelse: 'Frie sjeler i villmarken', antallMedlemmer: 0 },
		{ navn: 'Team Lam', beskrivelse: 'Rask og varm problemløsning', antallMedlemmer: 0 },
		{ navn: 'Team Saueull', beskrivelse: 'Myk datahåndtering siden 2023', antallMedlemmer: 0 },
		{ navn: 'Team Gjeter', beskrivelse: 'Vi holder orden på flokken', antallMedlemmer: 0 },
		{ navn: 'Team Beite', beskrivelse: 'Bredt nedslagsfelt, god avkastning', antallMedlemmer: 0 },
		{ navn: 'Team Ramme', beskrivelse: 'Strukturert som et sauegjerde', antallMedlemmer: 0 },
		{ navn: 'Team Flokk', beskrivelse: 'Sterkere sammen', antallMedlemmer: 0 },
		{ navn: 'Team Ulvevakt', beskrivelse: 'Sikkerhet og overvåkning', antallMedlemmer: 0 },
		{ navn: 'Team Ull', beskrivelse: 'Ny kraft i gammel beitemark', antallMedlemmer: 0 },
		{ navn: 'Team Pensjon', beskrivelse: 'Alderspensjon og uføretrygd', antallMedlemmer: 0 },
		{ navn: 'Team Dagpenger', beskrivelse: 'Dagpenger og arbeidsavklaring', antallMedlemmer: 0 },
		{ navn: 'Team Sykepenger', beskrivelse: 'Sykepenger og sykemelding', antallMedlemmer: 0 },
		{
			navn: 'Team Foreldrepenger',
			beskrivelse: 'Foreldrepenger og engangsstønad',
			antallMedlemmer: 0,
		},
		{ navn: 'Team AAP', beskrivelse: 'Arbeidsavklaringspenger', antallMedlemmer: 0 },
		{
			navn: 'Team Arbeidsoppfølging',
			beskrivelse: 'Oppfølging av arbeidssøkere',
			antallMedlemmer: 0,
		},
		{ navn: 'Team Plattform', beskrivelse: 'Nais og infrastruktur', antallMedlemmer: 0 },
		{ navn: 'Team Inntekt', beskrivelse: 'Inntektsregisteret', antallMedlemmer: 0 },
		{
			navn: 'Team Registerdata',
			beskrivelse: 'Folkeregisteret og adressedata',
			antallMedlemmer: 0,
		},
		{ navn: 'Team Tilgangsstyring', beskrivelse: 'Tilgang og autorisasjon', antallMedlemmer: 0 },
		{ navn: 'Team Rekruttering', beskrivelse: 'Rekruttering og stillinger', antallMedlemmer: 0 },
		{ navn: 'Team Oppfølging', beskrivelse: 'Brukeroppfølging og veiledning', antallMedlemmer: 0 },
		{ navn: 'Team Vedtak', beskrivelse: 'Vedtaksbehandling og klage', antallMedlemmer: 0 },
		{ navn: 'Team Utbetaling', beskrivelse: 'Utbetalinger og fakturering', antallMedlemmer: 0 },
		{ navn: 'Team Integrasjoner', beskrivelse: 'Eksterne integrasjoner', antallMedlemmer: 0 },
		{ navn: 'Team Kodeverk', beskrivelse: 'Kodeverk og klassifikasjoner', antallMedlemmer: 0 },
		{ navn: 'Team Brev', beskrivelse: 'Brevproduksjon og dokumentarkiv', antallMedlemmer: 0 },
		{ navn: 'Team Portal', beskrivelse: 'nav.no og innloggingsflate', antallMedlemmer: 0 },
		{ navn: 'Team Søknad', beskrivelse: 'Digitale søknadsflater', antallMedlemmer: 0 },
		{ navn: 'Team Barnetrygd', beskrivelse: 'Barnetrygd og kontantstøtte', antallMedlemmer: 0 },
		{
			navn: 'Team Hjelpemidler',
			beskrivelse: 'Hjelpemidler og tilrettelegging',
			antallMedlemmer: 0,
		},
		{ navn: 'Team Statistikk', beskrivelse: 'Statistikk og analyse', antallMedlemmer: 0 },
		{ navn: 'Team Datadeling', beskrivelse: 'Datadeling og API-er', antallMedlemmer: 0 },
		{ navn: 'Team Sikkerhet', beskrivelse: 'Sikkerhet og overvåkning', antallMedlemmer: 0 },
		{ navn: 'Team DevOps', beskrivelse: 'Bygg, deploy og drift', antallMedlemmer: 0 },
		{ navn: 'Team Analyse', beskrivelse: 'Dataanalyse og innsikt', antallMedlemmer: 0 },
		{ navn: 'Team Maskinlæring', beskrivelse: 'ML og kunstig intelligens', antallMedlemmer: 0 },
		{ navn: 'Team Testdata', beskrivelse: 'Testdata og syntetisering', antallMedlemmer: 0 },
		{
			navn: 'Team Modernisering',
			beskrivelse: 'Modernisering av eldre systemer',
			antallMedlemmer: 0,
		},
		{ navn: 'Team Mobilapp', beskrivelse: 'NAV-appen for iOS og Android', antallMedlemmer: 0 },
		{ navn: 'Team Kontaktsenter', beskrivelse: 'Telefon og chat-støtte', antallMedlemmer: 0 },
		{ navn: 'Team Feriepenger', beskrivelse: 'Feriepenger og opptjening', antallMedlemmer: 0 },
		{ navn: 'Team Klage', beskrivelse: 'Klage- og ankesaksbehandling', antallMedlemmer: 0 },
		{ navn: 'Team Oversikt', beskrivelse: 'Min side og selvbetjening', antallMedlemmer: 0 },
		{
			navn: 'Team Dokumentasjon',
			beskrivelse: 'Teknisk dokumentasjon og API-spec',
			antallMedlemmer: 0,
		},
		{ navn: 'Team Grunnstønad', beskrivelse: 'Grunnstønad og hjelpestønad', antallMedlemmer: 0 },
		{
			navn: 'Team Enslig forsørger',
			beskrivelse: 'Stønad til enslige forsørgere',
			antallMedlemmer: 0,
		},
		{
			navn: 'Team Uføretrygd',
			beskrivelse: 'Uføretrygd og arbeidsevnevurdering',
			antallMedlemmer: 0,
		},
		{ navn: 'Team Alderspensjon', beskrivelse: 'Alderspensjon og AFP', antallMedlemmer: 0 },
		{
			navn: 'Team Behandling',
			beskrivelse: 'Saksbehandling og automatisering',
			antallMedlemmer: 0,
		},
		{ navn: 'Team Ettersendelse', beskrivelse: 'Ettersendelse av dokumenter', antallMedlemmer: 0 },
		{ navn: 'Team Brukerflate', beskrivelse: 'Designsystem og UX', antallMedlemmer: 0 },
		{
			navn: 'Team Nettløsninger',
			beskrivelse: 'Nettbaserte løsninger og tilgjengelighet',
			antallMedlemmer: 0,
		},
		{ navn: 'Team Infrastruktur', beskrivelse: 'Serverpark og nettverksdrift', antallMedlemmer: 0 },
		{
			navn: 'Team Ytelsesutredning',
			beskrivelse: 'Ytelsesberegning og rettighetsutredning',
			antallMedlemmer: 0,
		},
		{ navn: 'Team Kontantstøtte', beskrivelse: 'Kontantstøtte og barnepass', antallMedlemmer: 0 },
	]

	const dashboardDollyTeams: DashboardDollyTeamsDTO[] = Array.from({ length: 12 }, (_, index) => {
		const monthOffset = 11 - index
		const interval = format(subMonths(new Date(), monthOffset), 'yyyy-MM')
		const isLargeMonth = Math.random() < 0.35
		const activeTeamCount = isLargeMonth
			? Math.floor(Math.random() * 10) + 50
			: Math.floor(Math.random() * 4) + 2
		const shuffledTeams = [...dollyTeamPool]
			.sort(() => Math.random() - 0.5)
			.slice(0, Math.min(activeTeamCount, dollyTeamPool.length))

		const teams = shuffledTeams.map((team) => ({
			...team,
			antallMedlemmer: Math.floor(Math.random() * 8) + 1,
		}))

		return {
			interval,
			totaltAntallMedlemmer: teams.reduce((sum, team) => sum + team.antallMedlemmer, 0),
			totaltAntallTeams: teams.length,
			teams,
		}
	})

	return { dashboardPersoner, dashboardTeams, dashboardOrganisasjoner, dashboardDollyTeams }
}
