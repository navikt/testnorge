import React, { useEffect, useState } from 'react'
import { ActionMenuWrapper, DropdownStyledLink } from './ActionMenuWrapper'
import Icon from '@/components/ui/icon/Icon'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { PreloadableActionMenuItem } from '@/utils/PreloadableActionMenuItem'
import { useLocation } from 'react-router'

export const OrganisasjonDropdown = () => {
	const location = useLocation()
	const [isActive, setIsActive] = useState(false)
	useEffect(() => {
		setIsActive(
			location?.pathname === '/organisasjoner' || location?.pathname === '/tenor/organisasjoner',
		)
	}, [location])

	return (
		<ActionMenuWrapper
			title="Organisasjoner"
			isActive={isActive}
			dataTestId={TestComponentSelectors.BUTTON_HEADER_ORGANISASJONER}
		>
			<PreloadableActionMenuItem
				route="/organisasjoner"
				dataTestId={TestComponentSelectors.BUTTON_HEADER_OPPRETT_ORGANISASJONER}
				style={{ color: '#212529' }}
			>
				<Icon kind="organisasjon" fontSize="1.5rem" />
				<DropdownStyledLink href="/organisasjoner">Mine organisasjoner</DropdownStyledLink>
			</PreloadableActionMenuItem>
			<PreloadableActionMenuItem
				route="/tenor/organisasjoner"
				dataTestId={TestComponentSelectors.BUTTON_HEADER_TENOR_ORGANISASJONER}
				style={{ color: '#212529' }}
			>
				<Icon kind="search" fontSize="1.5rem" />
				<DropdownStyledLink href="/tenor/organisasjoner">Søk i Tenor</DropdownStyledLink>
			</PreloadableActionMenuItem>
		</ActionMenuWrapper>
	)
}
