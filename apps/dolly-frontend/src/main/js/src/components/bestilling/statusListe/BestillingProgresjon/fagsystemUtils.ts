const IN_PROGRESS_MESSAGES = ['Info', 'INFO', 'ADDING_TO_QUEUE', 'RUNNING', 'PENDING_COMPLETE', 'Deployer', 'Pågående']

const PDL_FAGSYSTEMER = [
	{ id: 'PDL_FORVALTER', navn: 'Opprett persondetaljer' },
	{ id: 'PDL_ORDRE', navn: 'Ordre til PDL' },
	{ id: 'PDL_PERSONSTATUS', navn: 'Person finnes i PDL' },
]

const BESTILLING_FAGSYSTEM_MAP: Record<string, { id: string; navn: string }> = {
	aareg: { id: 'AAREG', navn: 'Arbeidsregister (AAREG)' },
	'arenaforvalter': { id: 'ARENA_BRUKER', navn: 'Arena bruker' },
	'arenaforvalter.aap': { id: 'ARENA_AAP', navn: 'Arena AAP ytelse' },
	'arenaforvalter.aap115': { id: 'ARENA_AAP115', navn: 'Arena AAP115 rettighet' },
	'arenaforvalter.dagpenger': { id: 'ARENA_DAGP', navn: 'Arena dagpenger' },
	sigrunstubPensjonsgivende: { id: 'SIGRUN_PENSJONSGIVENDE', navn: 'Pensjonsgivende inntekt (Sigrunstub)' },
	sigrunstubSummertSkattegrunnlag: { id: 'SIGRUN_SUMMERT', navn: 'Summert skattegrunnlag (Sigrunstub)' },
	inntektstub: { id: 'INNTK', navn: 'Inntektstub (INNTK)' },
	krrstub: { id: 'KRRSTUB', navn: 'Digital kontaktinformasjon (DKIF)' },
	fullmakt: { id: 'FULLMAKT', navn: 'Fullmakt (Representasjon)' },
	medl: { id: 'MEDL', navn: 'Medlemskap (MEDL)' },
	instdata: { id: 'INST2', navn: 'Institusjonsopphold (INST2)' },
	udistub: { id: 'UDISTUB', navn: 'Utlendingsdirektoratet (UDI)' },
	'pensjonforvalter': { id: 'PEN_FORVALTER', navn: 'Pensjon persondata (PEN)' },
	'pensjonforvalter.inntekt': { id: 'PEN_INNTEKT', navn: 'Pensjonsopptjening (POPP)' },
	'pensjonforvalter.generertInntekt': { id: 'PEN_INNTEKT', navn: 'Pensjonsopptjening (POPP)' },
	'pensjonforvalter.tp': { id: 'TP_FORVALTER', navn: 'Tjenestepensjon (TP)' },
	'pensjonforvalter.pensjonsavtale': { id: 'PEN_PENSJONSAVTALE', navn: 'Pensjonsavtale (PEN)' },
	'pensjonforvalter.alderspensjon': { id: 'PEN_AP', navn: 'Alderspensjon (AP)' },
	'pensjonforvalter.alderspensjonNyUtaksgrad': { id: 'PEN_AP_NY_UTTAKSGRAD', navn: 'Ny uttaksgrad (AP)' },
	'pensjonforvalter.uforetrygd': { id: 'PEN_UT', navn: 'Uføretrygd (UT)' },
	'pensjonforvalter.afpOffentlig': { id: 'PEN_AFP_OFFENTLIG', navn: 'AFP offentlig (PEN)' },
	inntektsmelding: { id: 'INNTKMELD', navn: 'Inntektsmelding (ALTINN/JOARK)' },
	brregstub: { id: 'BRREGSTUB', navn: 'Brønnøysundregistrene (BRREGSTUB)' },
	dokarkiv: { id: 'DOKARKIV', navn: 'Dokumentarkiv (JOARK)' },
	histark: { id: 'HISTARK', navn: 'Saksmappearkiv (HISTARK)' },
	sykemelding: { id: 'SYKEMELDING', navn: 'Nav sykemelding' },
	tpsMessaging: { id: 'TPS_MESSAGING', navn: 'Meldinger til TPS' },
	bankkonto: { id: 'KONTOREGISTER', navn: 'Bankkontoregister' },
	skjerming: { id: 'SKJERMINGSREGISTER', navn: 'Skjermingsregisteret' },
	arbeidsplassenCV: { id: 'ARBEIDSPLASSENCV', navn: 'Nav CV' },
	skattekort: { id: 'SKATTEKORT', navn: 'Nav skattekort' },
	yrkesskader: { id: 'YRKESSKADE', navn: 'Yrkesskade' },
	nomdata: { id: 'NOM', navn: 'Nav-ansatt (NOM)' },
	arbeidssoekerregisteret: { id: 'ARBEIDSSOEKERREGISTERET', navn: 'Arbeidssøkerregisteret' },
	etterlatteYtelser: { id: 'ETTERLATTE', navn: 'Etterlatte (Gjenny)' },
}

const getNestedValue = (obj: any, path: string) =>
	path.split('.').reduce((acc, key) => acc?.[key], obj)

export const getExpectedFagsystemer = (bestillingRequest: any) => {
	if (!bestillingRequest) return []

	const expected: { id: string; navn: string }[] = []
	const seenIds = new Set<string>()

	if (bestillingRequest.pdldata) {
		for (const pdl of PDL_FAGSYSTEMER) {
			expected.push(pdl)
			seenIds.add(pdl.id)
		}
	}

	for (const [field, fagsystem] of Object.entries(BESTILLING_FAGSYSTEM_MAP)) {
		if (seenIds.has(fagsystem.id)) continue
		const value = getNestedValue(bestillingRequest, field)
		if (value != null && (!Array.isArray(value) || value.length > 0)) {
			expected.push(fagsystem)
			seenIds.add(fagsystem.id)
		}
	}

	return expected
}

const PINNED_IDS = new Set(['PDLIMPORT', 'PDL_FORVALTER', 'PDL_ORDRE', 'PDL_PERSONSTATUS'])

const sortFagsystemer = (list: any[]) => {
	const pinned = list.filter((s) => PINNED_IDS.has(s.id))
	const rest = list.filter((s) => !PINNED_IDS.has(s.id))
	rest.sort((a, b) => (a.navn || '').localeCompare(b.navn || '', 'nb'))
	return [...pinned, ...rest]
}

export const mergeStatusWithExpected = (
	actualStatus: any[],
	expectedFagsystemer: { id: string; navn: string }[],
) => {
	if (expectedFagsystemer.length === 0) return sortFagsystemer(actualStatus)

	const actualIds = new Set(actualStatus.map((s) => s.id))

	const pendingEntries = expectedFagsystemer
		.filter((fs) => !actualIds.has(fs.id))
		.map((fs) => ({ id: fs.id, navn: fs.navn, statuser: [] }))

	return sortFagsystemer([...actualStatus, ...pendingEntries])
}

export const calculateProgress = ({
	antallIdenter,
	antallLevert,
	erOrganisasjon,
	statusList,
	expectedTotal,
}: {
	antallIdenter: number
	antallLevert: number
	erOrganisasjon: boolean
	statusList: any[]
	expectedTotal?: number
}) => {
	let percent: number
	let text: string

	if (!erOrganisasjon && antallIdenter === 1 && (statusList.length > 0 || (expectedTotal && expectedTotal > 0))) {
		const total = Math.max(statusList.length, expectedTotal || 0)
		const completedCount = statusList.filter((fagsystem) => {
			if (!fagsystem?.statuser?.length) return false
			return !fagsystem.statuser.some((s) =>
				IN_PROGRESS_MESSAGES.some((msg) => s?.melding?.includes(msg)),
			)
		}).length
		percent = total > 0 ? (100 / total) * completedCount : 0
		text = `${completedCount} av ${total} fagsystemer fullført`
	} else {
		percent = antallIdenter > 0 ? (100 / antallIdenter) * antallLevert : 0
		text = `Oppretter ${antallLevert || 0} av ${antallIdenter}`
	}

	if (percent === 0) {
		percent = 10
	}

	return { percent, text }
}
