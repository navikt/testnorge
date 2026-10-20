import { delay, http, HttpResponse } from 'msw'

import {
	FagsystemStatus,
	FunctionalTestEnvironment,
	FunctionalTestError,
	FunctionalTestState,
	TechnicalStatusState,
} from '@/services/statusApi'

interface MockSystem {
	systemId: string
	displayName: string
	environments: FunctionalTestEnvironment[]
	technical: boolean
}

interface MockOverride {
	state: FunctionalTestState
	error?: FunctionalTestError
	technicalState?: TechnicalStatusState
}

const RUN_DURATION_MILLISECONDS = 4_000
const MINUTE_MILLISECONDS = 60 * 1_000
const MOCK_RUN_ID = 'aaf62d6f-eb87-49ce-bcef-b82ca3fd940d'

const BOTH: FunctionalTestEnvironment[] = ['Q1', 'Q2']
const GLOBAL: FunctionalTestEnvironment[] = ['GLOBAL']

const functional = (
	systemId: string,
	displayName: string,
	environments: FunctionalTestEnvironment[],
): MockSystem => ({ systemId, displayName, environments, technical: false })

const technical = (
	systemId: string,
	displayName: string,
	environments: FunctionalTestEnvironment[] = GLOBAL,
): MockSystem => ({ systemId, displayName, environments, technical: true })

const SYSTEMS: MockSystem[] = [
	functional('pdl', 'PDL', BOTH),
	functional('arbeidssoekerregisteret', 'Arbeidssøkerregisteret', GLOBAL),
	functional('arena', 'Arena', BOTH),
	functional('brregstub', 'Brregstub', GLOBAL),
	functional('inntektstub', 'Inntektstub', GLOBAL),
	functional('instdata', 'Instdata', BOTH),
	functional('kontoregister', 'Kontoregister', GLOBAL),
	functional('krr', 'KRR', GLOBAL),
	functional('nom', 'NOM', GLOBAL),
	functional('pensjon-afp-offentlig', 'Pensjon AFP offentlig', BOTH),
	functional('pensjon-pensjonsavtale', 'Pensjon pensjonsavtale', GLOBAL),
	functional('pensjon-popp', 'Pensjon POPP', BOTH),
	functional('pensjon-tp', 'Pensjon TP-forhold', BOTH),
	functional('skattekort', 'Skattekort', BOTH),
	functional('skjermingsregister', 'Skjermingsregister', GLOBAL),
	functional('udi', 'UDI', GLOBAL),
	technical('arbeidsplassen-cv', 'Arbeidsplassen CV'),
	technical('aareg', 'Arbeidsregisteret (AAREG)'),
	technical('dokarkiv', 'Dokumentarkiv (JOARK)'),
	technical('fullmakt', 'Fullmakt'),
	technical('inntektsmelding', 'Inntektsmelding'),
	technical('kdi', 'KDI', ['Q2']),
	technical('kelvin-aap', 'Kelvin AAP'),
	technical('medl', 'MEDL'),
	technical('oppfoelgingsvedtak-14a', 'Oppfølgingsvedtak 14a'),
	technical('organisasjon-forvalter', 'Organisasjon-forvalter'),
	technical('pensjon-oevrige', 'Pensjon, øvrige profiler'),
	technical('sigrun', 'Sigrun'),
	technical('sykemelding', 'Sykemelding'),
	technical('tags', 'Tags/Hendelseslager'),
	technical('yrkesskade', 'Yrkesskade'),
]

const INITIAL_OVERRIDES: Record<string, MockOverride> = {
	'arena:Q2': {
		state: 'VERIFY_FAILED',
		error: { category: 'VALIDATION', message: 'Testdata kunne ikke verifiseres.' },
	},
	'pensjon-popp:Q1': {
		state: 'BLOCKED',
		error: {
			category: 'EXISTING_DATA',
			message: 'Eksisterende data hindrer en trygg testkjøring.',
		},
	},
	'kelvin-aap:GLOBAL': { state: 'TECHNICAL_ONLY', technicalState: 'DOWN' },
}

const keyOf = (systemId: string, environment: FunctionalTestEnvironment) =>
	`${systemId}:${environment}`

const isoMinutesAgo = (minutes: number) =>
	new Date(Date.now() - minutes * MINUTE_MILLISECONDS).toISOString()

const createStatus = (
	system: MockSystem,
	environment: FunctionalTestEnvironment,
): FagsystemStatus => {
	const override = INITIAL_OVERRIDES[keyOf(system.systemId, environment)]
	const completedAt = isoMinutesAgo(50)
	return {
		systemId: system.systemId,
		displayName: system.displayName,
		environment,
		runId: MOCK_RUN_ID,
		state: override?.state ?? (system.technical ? 'TECHNICAL_ONLY' : 'OK'),
		startedAt: isoMinutesAgo(51),
		completedAt,
		cachedUntil: isoMinutesAgo(-10),
		cleanupAttempts: 0,
		error: override?.error ?? null,
		technicalStatus: {
			state: system.technical ? (override?.technicalState ?? 'UP') : 'UNKNOWN',
			checkedAt: system.technical ? completedAt : null,
		},
	}
}

const statuses = new Map<string, FagsystemStatus>(
	SYSTEMS.flatMap((system) =>
		system.environments.map(
			(environment) =>
				[keyOf(system.systemId, environment), createStatus(system, environment)] as const,
		),
	),
)
const runningUntil = new Map<string, number>()

const finishRun = (status: FagsystemStatus, finishedAt: number): FagsystemStatus => {
	const technicalSystem = SYSTEMS.find((system) => system.systemId === status.systemId)?.technical
	const completedAt = new Date(finishedAt).toISOString()
	return {
		...status,
		state: technicalSystem ? 'TECHNICAL_ONLY' : 'OK',
		completedAt,
		error: null,
		technicalStatus: technicalSystem
			? { state: 'UP', checkedAt: completedAt }
			: status.technicalStatus,
	}
}

const currentStatuses = () => {
	const now = Date.now()
	runningUntil.forEach((finishedAt, key) => {
		if (now >= finishedAt) {
			statuses.set(key, finishRun(statuses.get(key)!, finishedAt))
			runningUntil.delete(key)
		}
	})
	return [...statuses.values()]
}

const startRun = (systemIds: string[]) => {
	const now = Date.now()
	statuses.forEach((status, key) => {
		if (systemIds.includes(status.systemId)) {
			statuses.set(key, {
				...status,
				state: 'VERIFY',
				startedAt: new Date(now).toISOString(),
				error: null,
			})
			runningUntil.set(key, now + RUN_DURATION_MILLISECONDS)
		}
	})
	return HttpResponse.json({ runId: MOCK_RUN_ID }, { status: 202 })
}

const systemIdsWhere = (technicalSystems: boolean) =>
	SYSTEMS.filter((system) => system.technical === technicalSystems).map((system) => system.systemId)

export const handlers = [
	http.get('/api/v1/fagsystem-statuser', async () => {
		await delay(300)
		return HttpResponse.json(currentStatuses())
	}),
	http.post('/api/v1/testkjoringer', () => new HttpResponse(null, { status: 404 })),
	http.post('/api/v1/fagsystemer/:systemId/testkjoringer', ({ params }) =>
		startRun([String(params.systemId)]),
	),
	http.post('/api/v1/funksjonstester/testkjoringer', () => startRun(systemIdsWhere(false))),
	http.post('/api/v1/interne-sjekker/testkjoringer', () => startRun(systemIdsWhere(true))),
]
