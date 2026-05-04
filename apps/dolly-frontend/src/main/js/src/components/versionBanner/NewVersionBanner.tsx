import { useEffect, useRef } from 'react'
import { toast } from 'react-toastify'
import { Button } from '@navikt/ds-react'
import styled from 'styled-components'
import { useVersionCheck } from '@/utils/hooks/useVersionCheck'
import { useLocation } from 'react-router'

export const BESTILLING_SAVE_EVENT = 'dolly-save-and-reload'

const TOAST_ID = 'dolly-new-version'

const isBestillingPath = (pathname: string) =>
	pathname.includes('/bestilling') || pathname.includes('/importer')

const VersionToastContent = ({
	onReload,
	showSaveMessage,
}: {
	onReload: () => void
	showSaveMessage: boolean
}) => (
	<StyledContent>
		<span>
			En ny versjon av Dolly er tilgjengelig.
			{showSaveMessage && ' Fremgangen din lagres automatisk.'}
		</span>
		<Button variant="tertiary" size="xsmall" onClick={onReload}>
			Oppdater nå
		</Button>
	</StyledContent>
)

const StyledContent = styled.div`
	display: flex;
	flex-direction: column;
	gap: 0.5rem;
`

export const NewVersionToast = () => {
	const { isNewVersionAvailable } = useVersionCheck()
	const location = useLocation()
	const isOnBestilling = isBestillingPath(location.pathname)
	const toastShownRef = useRef(false)

	useEffect(() => {
		if (!isNewVersionAvailable) return

		const handleReload = () => {
			if (isBestillingPath(window.location.pathname)) {
				window.dispatchEvent(new CustomEvent(BESTILLING_SAVE_EVENT))
			}
			window.location.reload()
		}

		if (toastShownRef.current) {
			toast.update(TOAST_ID, {
				render: (
					<VersionToastContent onReload={handleReload} showSaveMessage={isOnBestilling} />
				),
			})
		} else {
			toastShownRef.current = true
			toast.info(
				<VersionToastContent onReload={handleReload} showSaveMessage={isOnBestilling} />,
				{
					toastId: TOAST_ID,
					autoClose: false,
					closeOnClick: false,
					position: 'bottom-right',
					draggable: false,
				},
			)
		}
	}, [isNewVersionAvailable, isOnBestilling])

	return null
}
