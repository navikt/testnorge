import React, { useEffect, useState } from 'react'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { ActionMenu } from '@navikt/ds-react'
import { ActionMenuWrapper, DropdownStyledLink } from './ActionMenuWrapper'
import Icon from '@/components/ui/icon/Icon'
import { PreloadableActionMenuItem } from '@/utils/PreloadableActionMenuItem'
import { useLocation } from 'react-router'
import { TestComponentSelectors } from '#/mocks/Selectors'

export const DokumentasjonDropdown = () => {
	const { currentBruker } = useCurrentBruker()

	const location = useLocation()
	const [isActive, setIsActive] = useState(false)
	useEffect(() => {
		setIsActive(location?.pathname === '/oversikt')
	}, [location])

	const isDevVersion =
		window.location.hostname.includes('frontend') || window.location.hostname.includes('localhost')
	const apiUrl = isDevVersion
		? 'https://dolly-backend-dev.intern.dev.nav.no/swagger'
		: 'https://dolly-backend.ekstern.dev.nav.no/swagger'

	return (
		<ActionMenuWrapper
			title="Dokumentasjon"
			isActive={isActive}
			dataTestId={TestComponentSelectors.BUTTON_HEADER_DOKUMENTASJON}
		>
			<>
				<ActionMenu.Item
					onClick={() =>
						window.open(
							'https://navikt.github.io/testnorge/docs/applications/dolly/brukerveiledning',
							'_blank',
							'noopener',
						)
					}
				>
					<Icon kind="file-new" fontSize="1.5rem" style={{ color: 'black' }} />
					<DropdownStyledLink
						href="https://navikt.github.io/testnorge/docs/applications/dolly/brukerveiledning"
						target="_blank"
						rel="noopener noreferrer"
					>
						Brukerdokumentasjon
					</DropdownStyledLink>
				</ActionMenu.Item>
				<ActionMenu.Item onClick={() => window.open(apiUrl, '_blank', 'noopener')}>
					<Icon kind="file-code" fontSize="1.5rem" style={{ color: 'black' }} />
					<DropdownStyledLink href={apiUrl} target="_blank" rel="noopener noreferrer">
						API-dokumentasjon (Dolly backend)
					</DropdownStyledLink>
				</ActionMenu.Item>
				{currentBruker?.brukertype === 'AZURE' && (
					<>
						<PreloadableActionMenuItem
							route="/oversikt"
							dataTestId={TestComponentSelectors.BUTTON_HEADER_OVERSIKT}
							style={{ color: '#212529' }}
						>
							<Icon kind="file-code" fontSize="1.5rem" />
							<DropdownStyledLink href="/oversikt">API-oversikt</DropdownStyledLink>
						</PreloadableActionMenuItem>
					</>
				)}
			</>
		</ActionMenuWrapper>
	)
}
