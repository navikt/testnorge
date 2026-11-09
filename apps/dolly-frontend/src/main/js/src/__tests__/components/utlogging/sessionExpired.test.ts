import { beforeEach, describe, expect, it, vi } from 'vitest'
import { navigateToLogin } from '@/components/utlogging/navigateToLogin'
import {
	createSessionExpiredHandler,
	isSessionExpired,
} from '@/components/utlogging/sessionExpired'
import { BESTILLING_SAVE_EVENT } from '@/utils/bestillingFormPersistence'

vi.mock('@/components/utlogging/navigateToLogin', () => ({
	navigateToLogin: vi.fn(),
}))

describe('sessionExpired', () => {
	beforeEach(() => {
		window.sessionStorage.clear()
		vi.clearAllMocks()
	})

	it('gjenkjenner bare 401 med markør', () => {
		expect(isSessionExpired(401, 'expired')).toBe(true)
		expect(isSessionExpired(401, null)).toBe(false)
		expect(isSessionExpired(403, 'expired')).toBe(false)
	})

	it('sender brukeren til ID-porten ved første utløp', () => {
		const redirect = vi.fn()

		createSessionExpiredHandler(redirect)()

		expect(redirect).toHaveBeenCalledWith('/oauth2/authorization/idporten')
		expect(navigateToLogin).not.toHaveBeenCalled()
	})

	it('ber pågående bestilling lagre seg før omdirigering', () => {
		const calls: string[] = []
		const onSave = () => calls.push('lagre')
		window.addEventListener(BESTILLING_SAVE_EVENT, onSave)

		createSessionExpiredHandler(() => calls.push('omdiriger'))()

		window.removeEventListener(BESTILLING_SAVE_EVENT, onSave)
		expect(calls).toEqual(['lagre', 'omdiriger'])
	})

	it('ber pågående bestilling lagre seg også når innloggingssida vises', () => {
		window.sessionStorage.setItem('dolly-silent-login-at', String(Date.now() - 60_000))
		const onSave = vi.fn()
		window.addEventListener(BESTILLING_SAVE_EVENT, onSave)

		createSessionExpiredHandler(vi.fn())()

		window.removeEventListener(BESTILLING_SAVE_EVENT, onSave)
		expect(onSave).toHaveBeenCalledTimes(1)
		expect(navigateToLogin).toHaveBeenCalledTimes(1)
	})

	it('omdirigerer bare én gang per sidelasting', () => {
		const redirect = vi.fn()
		const handleSessionExpired = createSessionExpiredHandler(redirect)

		handleSessionExpired()
		handleSessionExpired()

		expect(redirect).toHaveBeenCalledTimes(1)
		expect(navigateToLogin).not.toHaveBeenCalled()
	})

	it('viser innloggingssida når forrige forsøk var for under 5 minutter siden', () => {
		window.sessionStorage.setItem('dolly-silent-login-at', String(Date.now() - 60_000))
		const redirect = vi.fn()

		createSessionExpiredHandler(redirect)()

		expect(redirect).not.toHaveBeenCalled()
		expect(navigateToLogin).toHaveBeenCalledTimes(1)
	})

	it('prøver ID-porten igjen etter 5 minutter', () => {
		window.sessionStorage.setItem('dolly-silent-login-at', String(Date.now() - 6 * 60_000))
		const redirect = vi.fn()

		createSessionExpiredHandler(redirect)()

		expect(redirect).toHaveBeenCalledTimes(1)
	})
})
