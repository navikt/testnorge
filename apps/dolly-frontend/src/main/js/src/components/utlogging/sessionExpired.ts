import { navigateToLogin } from '@/components/utlogging/navigateToLogin'
import { requestBestillingSave } from '@/utils/bestillingFormPersistence'

export const SESSION_HEADER = 'x-dolly-session'

const BACKGROUND_HEADER = 'X-Dolly-Background'

export const BACKGROUND_REQUEST_HEADERS = { [BACKGROUND_HEADER]: 'true' }

const SESSION_EXPIRED = 'expired'
const SILENT_LOGIN_KEY = 'dolly-silent-login-at'
const SILENT_LOGIN_COOLDOWN_MS = 5 * 60 * 1000
const IDPORTEN_LOGIN_URL = '/oauth2/authorization/idporten'

export const isSessionExpired = (status?: number, headerValue?: string | null) =>
	status === 401 && headerValue === SESSION_EXPIRED

export const isBackgroundRequest = (headers?: Record<string, string> | null) =>
	headers?.[BACKGROUND_HEADER] === 'true'

const redirectToIdporten = (url: string) => {
	window.location.href = url
}

export const createSessionExpiredHandler = (
	redirect: (url: string) => void = redirectToIdporten,
) => {
	let redirecting = false

	return () => {
		if (redirecting) {
			return
		}
		redirecting = true
		requestBestillingSave()

		const lastAttempt = Number(window.sessionStorage.getItem(SILENT_LOGIN_KEY) ?? 0)
		if (Date.now() - lastAttempt < SILENT_LOGIN_COOLDOWN_MS) {
			navigateToLogin('Sesjonen er utløpt, og ny innlogging feilet nylig')
			return
		}
		window.sessionStorage.setItem(SILENT_LOGIN_KEY, String(Date.now()))
		redirect(IDPORTEN_LOGIN_URL)
	}
}

export const handleSessionExpired = createSessionExpiredHandler()
