import { useEffect, useRef, useState } from 'react'
import { Button, GlobalAlert } from '@navikt/ds-react'
import styled from 'styled-components'
import { useVersionCheck } from '@/utils/hooks/useVersionCheck'
import { useLocation } from 'react-router'

export const BESTILLING_SAVE_EVENT = 'dolly-save-and-reload'

const isBestillingPath = (pathname: string) =>
	pathname.includes('/bestilling') || pathname.includes('/importer')

export const NewVersionBanner = () => {
	const { isNewVersionAvailable } = useVersionCheck()
	const location = useLocation()
	const isOnBestilling = isBestillingPath(location.pathname)
	const [dismissed, setDismissed] = useState(false)

	const handleReload = () => {
		if (isBestillingPath(window.location.pathname)) {
			window.dispatchEvent(new CustomEvent(BESTILLING_SAVE_EVENT))
		}
		window.location.reload()
	}

	if (!isNewVersionAvailable || dismissed) return null

	return (
		<GlobalAlert status="announcement">
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
		</GlobalAlert>
	)
}

const StyledContent = styled.div`
	display: flex;
	flex-direction: column;
	gap: 0.5rem;
`
