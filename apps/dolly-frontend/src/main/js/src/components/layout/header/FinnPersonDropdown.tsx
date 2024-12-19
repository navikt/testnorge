import React from 'react'
import { useLocation, useNavigate } from 'react-router'
import { ActionMenu } from '@navikt/ds-react'
import { ActionMenuWrapper, DropdownStyledLink } from './ActionMenuWrapper'
import Icon from '@/components/ui/icon/Icon'
import { TestComponentSelectors } from '#/mocks/Selectors'

export const FinnPersonDropdown = () => {
	const navigate = useNavigate()
	const location = useLocation()
	const isActive =
		location?.pathname === '/dollysoek' ||
		location?.pathname === '/testnorge' ||
		location?.pathname === '/tenor/personer/'

	return (
		<ActionMenuWrapper title="Finn person" isActive={isActive}>
			<ActionMenu.Item
				data-testid={TestComponentSelectors.BUTTON_HEADER_DOLLYSOEK}
				onClick={() => navigate('/dollysoek')}
				style={{ color: '#212529' }}
			>
				<Icon kind="search" fontSize="1.5rem" />
				<DropdownStyledLink href="/dollysoek">Søk i Dolly</DropdownStyledLink>
			</ActionMenu.Item>
			<ActionMenu.Item
				data-testid={TestComponentSelectors.BUTTON_HEADER_TESTNORGE}
				onClick={() => navigate('/testnorge')}
				style={{ color: '#212529' }}
			>
				<Icon kind="search" fontSize="1.5rem" />
				<DropdownStyledLink href="/testnorge">Søk i Test-Norge</DropdownStyledLink>
			</ActionMenu.Item>
			<ActionMenu.Item
				data-testid={TestComponentSelectors.BUTTON_HEADER_TENOR}
				onClick={() => navigate('/tenor/personer/')}
				style={{ color: '#212529' }}
			>
				<Icon kind="search" fontSize="1.5rem" />
				<DropdownStyledLink href="/tenor/personer/">Søk i Tenor</DropdownStyledLink>
			</ActionMenu.Item>
			<ActionMenu.Item onClick={() => navigate('/nyansettelser/')} style={{ color: '#212529' }}>
				<Icon kind="ansettelse" fontSize="1.5rem" />
				<DropdownStyledLink href="/nyansettelser/">Vis nyansettelser</DropdownStyledLink>
			</ActionMenu.Item>
		</ActionMenuWrapper>
	)
}
