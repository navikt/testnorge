import React from 'react'
import { useLocation } from 'react-router'
import { ActionMenuWrapper, DropdownStyledLink } from './ActionMenuWrapper'
import Icon from '@/components/ui/icon/Icon'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { PreloadableActionMenuItem } from '@/utils/PreloadableActionMenuItem'

export const FinnPersonDropdown = () => {
	const location = useLocation()
	const isActive =
		location?.pathname === '/dollysoek' ||
		location?.pathname === '/testnorge' ||
		location?.pathname === '/tenor/personer/'

	return (
		<ActionMenuWrapper title="Finn person" isActive={isActive}>
			<PreloadableActionMenuItem
				route="/dollysoek"
				dataTestId={TestComponentSelectors.BUTTON_HEADER_DOLLYSOEK}
				style={{ color: '#212529' }}
			>
				<Icon kind="search" fontSize="1.5rem" />
				<DropdownStyledLink href="/dollysoek">Søk i Dolly</DropdownStyledLink>
			</PreloadableActionMenuItem>
			<PreloadableActionMenuItem
				route="/testnorge"
				dataTestId={TestComponentSelectors.BUTTON_HEADER_TESTNORGE}
				style={{ color: '#212529' }}
			>
				<Icon kind="search" fontSize="1.5rem" />
				<DropdownStyledLink href="/testnorge">Søk i Test-Norge</DropdownStyledLink>
			</PreloadableActionMenuItem>
			<PreloadableActionMenuItem
				route="/tenor/personer"
				dataTestId={TestComponentSelectors.BUTTON_HEADER_TENOR}
				style={{ color: '#212529' }}
			>
				<Icon kind="search" fontSize="1.5rem" />
				<DropdownStyledLink href="/tenor/personer/">Søk i Tenor</DropdownStyledLink>
			</PreloadableActionMenuItem>
			<PreloadableActionMenuItem route="/nyansettelser" style={{ color: '#212529' }}>
				<Icon kind="ansettelse" fontSize="1.5rem" />
				<DropdownStyledLink href="/nyansettelser/">Vis nyansettelser</DropdownStyledLink>
			</PreloadableActionMenuItem>
		</ActionMenuWrapper>
	)
}
