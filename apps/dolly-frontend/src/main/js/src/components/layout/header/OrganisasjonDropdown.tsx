import React from 'react'
import { useNavigate } from 'react-router-dom'
import { ActionMenu } from '@navikt/ds-react'
import { ActionMenuWrapper, DropdownStyledLink } from './ActionMenuWrapper'
import Icon from '@/components/ui/icon/Icon'
import { TestComponentSelectors } from '#/mocks/Selectors'

export const OrganisasjonDropdown = () => {
	const navigate = useNavigate()
	const isActive =
		location?.pathname === '/organisasjoner' || location?.pathname === '/tenor/organisasjoner'

	return (
		<ActionMenuWrapper title="Organisasjoner" isActive={isActive}>
			<ActionMenu.Item
				data-testid={TestComponentSelectors.BUTTON_HEADER_OPPRETT_ORGANISASJONER}
				onClick={() => navigate('/organisasjoner')}
				style={{ color: '#212529' }}
			>
				<Icon kind="organisasjon" fontSize="1.5rem" />
				<DropdownStyledLink href="/organisasjoner">Mine organisasjoner</DropdownStyledLink>
			</ActionMenu.Item>
			<ActionMenu.Item
				data-testid={TestComponentSelectors.BUTTON_HEADER_TENOR_ORGANISASJONER}
				onClick={() => navigate('/tenor/organisasjoner')}
				style={{ color: '#212529' }}
			>
				<Icon kind="search" fontSize="1.5rem" />
				<DropdownStyledLink href="/tenor/organisasjoner">Søk i Tenor</DropdownStyledLink>
			</ActionMenu.Item>
		</ActionMenuWrapper>
	)
}
