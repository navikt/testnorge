import { Alert, Button } from '@navikt/ds-react'
import styled from 'styled-components'
import { useVersionCheck } from '@/utils/hooks/useVersionCheck'

const StyledBanner = styled.div`
	position: sticky;
	top: 0;
	z-index: 1000;
`

export const NewVersionBanner = () => {
	const { isNewVersionAvailable } = useVersionCheck()

	if (!isNewVersionAvailable) return null

	return (
		<StyledBanner>
			<Alert variant="info" size="small">
				En ny versjon av Dolly er tilgjengelig.{' '}
				<Button
					variant="tertiary"
					size="xsmall"
					onClick={() => window.location.reload()}
				>
					Last inn siden på nytt
				</Button>
			</Alert>
		</StyledBanner>
	)
}
