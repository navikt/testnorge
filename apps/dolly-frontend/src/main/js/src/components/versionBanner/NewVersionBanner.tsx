import { useEffect, useState } from 'react'
import { Button, GlobalAlert } from '@navikt/ds-react'
import styled from 'styled-components'
import { useVersionCheck } from '@/utils/hooks/useVersionCheck'
import { useLocation } from 'react-router'
import {
	BESTILLING_SAVE_EVENT,
	savePendingBestillingFormState,
} from '@/utils/bestillingFormPersistence'
import { CSRF_FAILED_EVENT } from '@/api/csrf'

export { BESTILLING_SAVE_EVENT }
export const BESTILLING_RESTORED_EVENT = 'dolly-form-restored'

const isBestillingPath = (pathname: string) =>
	pathname.includes('/bestilling') || pathname.includes('/importer')

export const AppBanner = () => {
	const { isNewVersionAvailable } = useVersionCheck()
	const location = useLocation()
	const isOnBestilling = isBestillingPath(location.pathname)
	const [dismissed, setDismissed] = useState(false)
	const [showRestored, setShowRestored] = useState(false)
	const [reloadRequired, setReloadRequired] = useState(false)

	useEffect(() => {
		const handler = () => setShowRestored(true)
		window.addEventListener(BESTILLING_RESTORED_EVENT, handler)
		return () => window.removeEventListener(BESTILLING_RESTORED_EVENT, handler)
	}, [])

	useEffect(() => {
		const handler = () => setReloadRequired(true)
		window.addEventListener(CSRF_FAILED_EVENT, handler)
		return () => window.removeEventListener(CSRF_FAILED_EVENT, handler)
	}, [])

	const handleReload = () => {
		if (isBestillingPath(window.location.pathname)) {
			savePendingBestillingFormState()
			window.dispatchEvent(new CustomEvent(BESTILLING_SAVE_EVENT))
		}
		window.location.reload()
	}

	const showVersion = isNewVersionAvailable && !dismissed && !reloadRequired

	if (!showVersion && !showRestored && !reloadRequired) return null

	return (
		<>
			{reloadRequired && (
				<GlobalAlert status="warning" size="small">
					<GlobalAlert.Header>
						<GlobalAlert.Title as="h2">Last inn siden på nytt</GlobalAlert.Title>
					</GlobalAlert.Header>
					<GlobalAlert.Content>
						<StyledContent>
							<span>
								Dolly kunne ikke fullføre handlingen din. Last inn siden på nytt og prøv igjen.
								{isOnBestilling && ' Bestillingen din blir lagret før siden lastes inn på nytt.'}
							</span>
							<Button variant="secondary" size="xsmall" onClick={handleReload}>
								Last inn på nytt
							</Button>
						</StyledContent>
					</GlobalAlert.Content>
				</GlobalAlert>
			)}
			{showVersion && (
				<GlobalAlert status="announcement" size="small">
					<GlobalAlert.Header>
						<GlobalAlert.Title as="h2">Ny versjon tilgjengelig</GlobalAlert.Title>
						<GlobalAlert.CloseButton onClick={() => setDismissed(true)} />
					</GlobalAlert.Header>
					<GlobalAlert.Content>
						<StyledContent>
							<span>
								En ny versjon av Dolly er tilgjengelig.
								{isOnBestilling &&
									' Alle endringene dine vil bli lagret før siden lastes inn igjen.'}
							</span>
							<Button variant="secondary" size="xsmall" onClick={handleReload}>
								Oppdater nå
							</Button>
						</StyledContent>
					</GlobalAlert.Content>
				</GlobalAlert>
			)}
			{showRestored && (
				<GlobalAlert status="success" size="small">
					<GlobalAlert.Header>
						<GlobalAlert.Title as="h2">Endringene dine ble gjenopprettet!</GlobalAlert.Title>
						<GlobalAlert.CloseButton onClick={() => setShowRestored(false)} />
					</GlobalAlert.Header>
				</GlobalAlert>
			)}
		</>
	)
}

const StyledContent = styled.div`
	display: flex;
	flex-direction: column;
	align-items: flex-start;
	gap: 0.5rem;
`
