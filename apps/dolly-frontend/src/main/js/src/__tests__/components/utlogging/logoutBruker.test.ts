import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { CSRF_FAILED_EVENT, loadCsrfToken, resetCsrfToken } from '@/api/csrf'
import { getLogoutUrl } from '@/components/utlogging/logoutBruker'
import logoutBruker from '@/components/utlogging/logoutBruker'
import { LogoutErrorStates } from '@/components/utlogging/logoutState'

vi.mock('@/api/csrf', async (importOriginal) => ({
	...(await importOriginal<typeof import('@/api/csrf')>()),
	loadCsrfToken: vi.fn(),
	resetCsrfToken: vi.fn(),
}))

describe('getLogoutUrl', () => {
	it('bruker Wonderwall for Entra', () => {
		expect(getLogoutUrl('dolly.ansatt.dev.nav.no')).toBe('/oauth2/logout')
	})

	it('bruker /logout med state lokalt og for ID-porten', () => {
		expect(getLogoutUrl('localhost')).toBe('/logout')
		expect(getLogoutUrl('dolly-idporten.intern.dev.nav.no', LogoutErrorStates.SESSION_ERROR)).toBe(
			`/logout?state=${LogoutErrorStates.SESSION_ERROR}`,
		)
	})
})

describe('logoutBruker lokalt', () => {
	let submit: ReturnType<typeof vi.spyOn>

	beforeEach(() => {
		submit = vi.spyOn(HTMLFormElement.prototype, 'submit').mockImplementation(() => {})
		vi.mocked(loadCsrfToken).mockReset()
	})

	afterEach(() => {
		submit.mockRestore()
		document.querySelectorAll('form[action*="/logout"]').forEach((form) => form.remove())
	})

	it('poster skjema med CSRF-token', async () => {
		vi.mocked(loadCsrfToken).mockResolvedValue({
			status: 'ok',
			csrfToken: { headerName: 'X-CSRF-TOKEN', parameterName: '_csrf', token: 'abc' },
		})

		await logoutBruker(LogoutErrorStates.SESSION_ERROR)

		const form = document.querySelector<HTMLFormElement>('form[action*="/logout"]')
		expect(form?.method).toBe('post')
		expect(form?.getAttribute('action')).toBe(`/logout?state=${LogoutErrorStates.SESSION_ERROR}`)
		expect(form?.querySelector<HTMLInputElement>('input[name="_csrf"]')?.value).toBe('abc')
		expect(submit).toHaveBeenCalledTimes(1)
		expect(resetCsrfToken).toHaveBeenCalledBefore(vi.mocked(loadCsrfToken))
	})

	it('sender til innloggingssiden med feilstatus når token ikke kan hentes', async () => {
		vi.mocked(loadCsrfToken).mockResolvedValue({ status: 'failed' })
		const redirect = vi.fn()

		await logoutBruker(LogoutErrorStates.SESSION_ERROR, redirect)

		expect(redirect).toHaveBeenCalledWith(`/login?state=${LogoutErrorStates.SESSION_ERROR}`)
		expect(submit).not.toHaveBeenCalled()
	})

	it('varsler når token ikke kan hentes', async () => {
		vi.mocked(loadCsrfToken).mockResolvedValue({ status: 'failed' })
		const listener = vi.fn()
		window.addEventListener(CSRF_FAILED_EVENT, listener)

		await logoutBruker()

		window.removeEventListener(CSRF_FAILED_EVENT, listener)
		expect(listener).toHaveBeenCalledTimes(1)
		expect(submit).not.toHaveBeenCalled()
	})
})
