import { useEffect, useState } from 'react'
import { Alert, Button, GlobalAlert } from '@navikt/ds-react'
import styled from 'styled-components'
import { useVersionCheck } from '@/utils/hooks/useVersionCheck'
import { useLocation } from 'react-router'

export const BESTILLING_SAVE_EVENT = 'dolly-save-and-reload'
export const BESTILLING_RESTORED_EVENT = 'dolly-form-restored'

const isBestillingPath = (pathname: string) =>
	pathname.includes('/bestilling') || pathname.includes('/importer')

export const AppBanner = () => {
	const { isNewVersionAvailable } = useVersionCheck()
	const location = useLocation()
	const isOnBestilling = isBestillingPath(location.pathname)
	const [dismissed, setDismissed] = useState(false)
	const [showRestored, setShowRestored] = useState(false)

	useEffect(() => {
		const handler = () => setShowRestored(true)
		window.addEventListener(BESTILLING_RESTORED_EVENT, handler)
		return () => window.removeEventListener(BESTILLING_RESTORED_EVENT, handler)
	}, [])

	const handleReload = () => {
		if (isBestillingPath(window.location.pathname)) {
			window.dispatchEvent(new CustomEvent(BESTILLING_SAVE_EVENT))
		}
		window.location.reload()
	}

	const showVersion = isNewVersionAvailable && !dismissed

	if (!showVersion && !showRestored) return null

	return (
		<StickyAlertWrapper>
			{showVersion && (
				<StyledGlobalAlert status="announcement">
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
							<Button variant="tertiary" size="xsmall" onClick={handleReload}>
								Oppdater nå
							</Button>
						</StyledContent>
					</GlobalAlert.Content>
				</StyledGlobalAlert>
			)}
			{showRestored && (
				<Alert variant="success" closeButton onClose={() => setShowRestored(false)}>
					Endringene dine ble gjenopprettet!
				</Alert>
			)}
		</StickyAlertWrapper>
	)
}

const StickyAlertWrapper = styled.div`
	position: sticky;
	top: 0;
	z-index: 1000;
	max-width: 940px;
	margin: 10px auto 10px;
`

const StyledGlobalAlert = styled(GlobalAlert)`
	.aksel-base-alert__header {
		justify-content: center;
		position: relative;
	}

	.aksel-base-alert__title {
		text-align: center;
	}

	.aksel-base-alert__close-button {
		position: absolute;
		right: 0;
		top: 0;
	}
`

const StyledContent = styled.div`
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 0.5rem;
`
