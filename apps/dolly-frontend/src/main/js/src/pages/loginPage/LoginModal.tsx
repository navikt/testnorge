import NavButton from '@/components/ui/button/NavButton/NavButton'
import { Link, LocalAlert } from '@navikt/ds-react'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { top } from '@popperjs/core'
import React from 'react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import type { LogoutErrorState } from '@/components/utlogging/logoutState'
import { isLogoutErrorState, LogoutErrorStates } from '@/components/utlogging/logoutState'

interface LogoutAlert {
	title: string
	message: string
}

const logoutAlerts: Record<LogoutErrorState, LogoutAlert> = {
	[LogoutErrorStates.ORGANISATION_ERROR]: {
		title: 'Du har ikke tilgang til Dolly',
		message:
			'Vi kunne ikke finne en organisasjon du kan representere i Dolly. Hvis du mener dette er feil, kan du kontakte Dolly-teamet.',
	},
	[LogoutErrorStates.UNKNOWN_ERROR]: {
		title: 'Du ble logget ut på grunn av en feil',
		message: 'Noe gikk galt under innloggingen. Vedvarer feilen, kan du kontakte Dolly-teamet.',
	},
	[LogoutErrorStates.MILJOE_ERROR]: {
		title: 'Du ble logget ut på grunn av en feil',
		message: 'Vi kunne ikke hente gyldige miljøer. Vedvarer feilen, kan du kontakte Dolly-teamet.',
	},
	[LogoutErrorStates.PERSON_ORG_ERROR]: {
		title: 'Du ble logget ut på grunn av en feil',
		message:
			'Vi kunne ikke bekrefte organisasjonstilgangen din, og du ble derfor logget ut. Vedvarer feilen, kan du kontakte Dolly-teamet.',
	},
	[LogoutErrorStates.AZURE_ERROR]: {
		title: 'Du ble logget ut på grunn av en feil',
		message:
			'Vi kunne ikke hente nødvendig innloggingsinformasjon. Vedvarer feilen, kan du kontakte Dolly-teamet.',
	},
	[LogoutErrorStates.SESSION_ERROR]: {
		title: 'Du ble logget ut på grunn av en feil',
		message:
			'Vi kunne ikke opprette en gyldig Dolly-sesjon, og du ble derfor logget ut. Vedvarer feilen, kan du kontakte Dolly-teamet.',
	},
}

export const getLogoutAlert = (search: string): LogoutAlert | null => {
	const logoutState = new URLSearchParams(search).get('state')
	return isLogoutErrorState(logoutState) ? logoutAlerts[logoutState] : null
}

export const redirectOnClick =
	(path: string, toIdporten: boolean) => (event: React.MouseEvent<HTMLButtonElement>) => {
		event.preventDefault()
		redirectTo(path, toIdporten)
	}

const redirectTo = (path: string, toIdporten: boolean) => {
	const navigateToIdporten = toIdporten && !window.location.hostname?.includes('localhost')
	const isIdporten =
		window.location.hostname?.includes('idporten') &&
		!window.location.hostname?.includes('localhost')

	if (navigateToIdporten && !isIdporten) {
		location.replace('https://dolly-idporten.ekstern.dev.nav.no/login')
		return
	}
	if (!navigateToIdporten && isIdporten) {
		location.replace('https://dolly.ekstern.dev.nav.no/login')
		return
	}
	location.replace(location.protocol + '//' + location.host + path)
}

export default () => {
	const logoutAlert = getLogoutAlert(location.search)
	const modalHeight = logoutAlert ? 450 : 350
	const runningLocal = window.location.hostname.includes('localhost')

	return (
		<div className="login-container">
			<div className="login-modal" style={{ height: modalHeight + 'px' }}>
				<h1>Velkommen til Dolly</h1>
				<p className="login-modal_description">
					Dolly er NAVs selvbetjeningsløsning for å opprette syntetiske data. I Dolly kan du
					opprette syntetiske personer med forskjellige egenskaper, og tilgjengeliggjøre dataene i
					valgte testmiljøer.
				</p>
				{logoutAlert && (
					<LocalAlert status="error">
						<LocalAlert.Header>
							<LocalAlert.Title as="h2">{logoutAlert.title}</LocalAlert.Title>
						</LocalAlert.Header>
						<LocalAlert.Content>
							{logoutAlert.message} Sjekk{' '}
							<Link
								href="https://navikt.github.io/testnorge/testnav/latest/index.html#feil_innlogging"
								target="_blank"
								rel="noreferrer"
								referrerPolicy="no-referrer"
							>
								brukerveiledning
							</Link>{' '}
							for mer informasjon.
						</LocalAlert.Content>
					</LocalAlert>
				)}
				<div className="flexbox--justify-center flexbox--align-center">
					<NavButton
						data-testid={TestComponentSelectors.BUTTON_LOGIN_NAV}
						className="login-modal_button-nav"
						variant={'primary'}
						onClick={redirectOnClick(
							runningLocal ? '/oauth2/authorization/aad' : '/oauth2/login',
							false,
						)}
					>
						Logg inn med Nav-e-post
					</NavButton>
					<NavButton
						data-testid={TestComponentSelectors.BUTTON_LOGIN_BANKID}
						className="login-modal_button-bankid"
						variant={'primary'}
						onClick={redirectOnClick('/oauth2/authorization/idporten', true)}
					>
						Logg inn med BankId
					</NavButton>
					<Hjelpetekst placement={top} requestFeedback={false}>
						For å ta i bruk BankId innlogging må du være tilknyttet en organisasjon som har tilgang
						til Dolly. Ta kontakt med en administrator i din organisasjon hvis tilgang mangler.
					</Hjelpetekst>
				</div>
			</div>
		</div>
	)
}
