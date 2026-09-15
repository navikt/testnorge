export const LogoutErrorStates = {
	ORGANISATION_ERROR: 'organisation_error',
	UNKNOWN_ERROR: 'unknown_error',
	MILJOE_ERROR: 'miljoe_error',
	PERSON_ORG_ERROR: 'person_org_error',
	AZURE_ERROR: 'azure_error',
	SESSION_ERROR: 'session_error',
} as const

export type LogoutErrorState = (typeof LogoutErrorStates)[keyof typeof LogoutErrorStates]

export const isLogoutErrorState = (state: string | null): state is LogoutErrorState =>
	state !== null && Object.values(LogoutErrorStates).some((logoutState) => logoutState === state)
