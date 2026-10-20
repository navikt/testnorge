import type { LogoutErrorState } from '@/components/utlogging/logoutState'

export const getLogoutUrl = (hostname: string, logoutError?: LogoutErrorState) => {
	const runningLocalOrIdporten =
		hostname.includes('localhost') || hostname.includes('idporten')

	if (!runningLocalOrIdporten) {
		return '/oauth2/logout'
	}
	if (!logoutError) {
		return '/logout'
	}
	return `/logout?${new URLSearchParams({ state: logoutError })}`
}

const logoutBruker = (logoutError?: LogoutErrorState) => {
	window.location.href = getLogoutUrl(window.location.hostname, logoutError)
}
export default logoutBruker
