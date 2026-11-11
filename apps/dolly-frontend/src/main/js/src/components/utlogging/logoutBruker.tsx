import type { LogoutErrorState } from '@/components/utlogging/logoutState'
import { CsrfToken, loadCsrfToken, notifyCsrfFailure, resetCsrfToken } from '@/api/csrf'

const WONDERWALL_LOGOUT = '/oauth2/logout'

export const getLogoutUrl = (hostname: string, logoutError?: LogoutErrorState) => {
	const runningLocalOrIdporten = hostname.includes('localhost') || hostname.includes('idporten')

	if (!runningLocalOrIdporten) {
		return WONDERWALL_LOGOUT
	}
	if (!logoutError) {
		return '/logout'
	}
	return `/logout?${new URLSearchParams({ state: logoutError })}`
}

const submitLogoutForm = (action: string, csrfToken: CsrfToken) => {
	const form = document.createElement('form')
	form.method = 'POST'
	form.action = action
	form.style.display = 'none'
	const input = document.createElement('input')
	input.type = 'hidden'
	input.name = csrfToken.parameterName
	input.value = csrfToken.token
	form.appendChild(input)
	document.body.appendChild(form)
	form.submit()
}

const navigate = (url: string) => {
	window.location.href = url
}

const logoutBruker = async (logoutError?: LogoutErrorState, redirect = navigate) => {
	const logoutUrl = getLogoutUrl(window.location.hostname, logoutError)
	if (logoutUrl === WONDERWALL_LOGOUT) {
		redirect(logoutUrl)
		return
	}

	resetCsrfToken()
	const result = await loadCsrfToken()
	if (result.status === 'ok') {
		submitLogoutForm(logoutUrl, result.csrfToken)
		return
	}
	if (result.status === 'expired' || logoutError) {
		redirect(logoutError ? `/login?${new URLSearchParams({ state: logoutError })}` : '/login')
		return
	}
	notifyCsrfFailure()
}
export default logoutBruker
