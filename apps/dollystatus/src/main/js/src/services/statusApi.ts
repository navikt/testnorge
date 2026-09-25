import Api, { ApiError } from '@/services/api'

export type FunctionalTestEnvironment = 'Q1' | 'Q2' | 'GLOBAL'

export type FunctionalTestState =
	| 'NOT_RUN'
	| 'RUNNING'
	| 'PREFLIGHT'
	| 'PREFLIGHT_FAILED'
	| 'CREATE'
	| 'CREATE_FAILED'
	| 'VERIFY'
	| 'VERIFY_FAILED'
	| 'VERIFY_TIMEOUT'
	| 'CLEANUP'
	| 'CLEANUP_FAILED'
	| 'OK'
	| 'BLOCKED'
	| 'TECHNICAL_ONLY'

export type TechnicalStatusState = 'UNKNOWN' | 'UP' | 'DOWN'

export interface FunctionalTestError {
	category: string
	message: string
}

export interface TechnicalStatus {
	state: TechnicalStatusState
	checkedAt: string | null
}

export interface FagsystemStatus {
	systemId: string
	displayName: string
	environment: FunctionalTestEnvironment
	runId: string | null
	state: FunctionalTestState
	startedAt: string | null
	completedAt: string | null
	cachedUntil: string | null
	cleanupAttempts: number
	error: FunctionalTestError | null
	technicalStatus: TechnicalStatus
}

export interface RunAccepted {
	runId: string
}

export interface ProblemDetail {
	message?: string
	retryAfter?: string
}

const getStatuses = () =>
	Api.fetchJson<FagsystemStatus[]>('/api/v1/fagsystem-statuser', { method: 'GET' })

const startExpiredTests = () =>
	Api.fetchJson<RunAccepted>('/api/v1/testkjoringer', { method: 'POST' })

const startSystemTest = (systemId: string) =>
	Api.fetchJson<RunAccepted>(`/api/v1/fagsystemer/${systemId}/testkjoringer`, {
		method: 'POST',
	})

const readProblem = async (error: unknown): Promise<ProblemDetail | null> => {
	if (!(error instanceof ApiError)) {
		return null
	}
	try {
		return (await error.response.json()) as ProblemDetail
	} catch {
		return null
	}
}

export default {
	getStatuses,
	startExpiredTests,
	startSystemTest,
	readProblem,
}
