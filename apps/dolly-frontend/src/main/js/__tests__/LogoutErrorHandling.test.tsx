import { render, screen } from '@testing-library/react'
import { afterEach, describe, expect, it } from 'vitest'
import LoginModal, { getLogoutAlert } from '@/pages/loginPage/LoginModal'
import { getLogoutUrl } from '@/components/utlogging/logoutBruker'
import { LogoutErrorStates } from '@/components/utlogging/logoutState'

describe('ID-porten logout error handling', () => {
	afterEach(() => {
		window.history.replaceState({}, '', '/')
	})

	it('should include error state for ID-porten logout', () => {
		expect(
			getLogoutUrl('dolly-idporten.ekstern.dev.nav.no', LogoutErrorStates.PERSON_ORG_ERROR),
		).toBe('/logout?state=person_org_error')
	})

	it('should keep Azure logout unchanged', () => {
		expect(
			getLogoutUrl('dolly.ekstern.dev.nav.no', LogoutErrorStates.PERSON_ORG_ERROR),
		).toBe('/oauth2/logout')
	})

	it('should not include error state for manual logout', () => {
		expect(getLogoutUrl('dolly-idporten.ekstern.dev.nav.no')).toBe('/logout')
	})

	it('should reject unknown and inherited state values', () => {
		expect(getLogoutAlert('?state=constructor')).toBeNull()
		expect(getLogoutAlert('?state=unknown_value')).toBeNull()
	})

	it('should show a local error alert for organisation access failures', () => {
		window.history.replaceState({}, '', '/login?state=person_org_error')

		render(<LoginModal />)

		expect(screen.getByRole('alert')).toBeInTheDocument()
		expect(
			screen.getByRole('heading', {
				level: 2,
				name: /Du ble logget ut på grunn av en feil/,
			}),
		).toBeInTheDocument()
		expect(screen.getByText(/Vi kunne ikke bekrefte organisasjonstilgangen din/)).toBeInTheDocument()
	})
})
