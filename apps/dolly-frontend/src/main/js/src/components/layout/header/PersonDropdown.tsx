import React, { useEffect, useState } from 'react'
import { useLocation } from 'react-router'
import { ActionMenuWrapper, DropdownStyledLink } from './ActionMenuWrapper'
import Icon from '@/components/ui/icon/Icon'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { PreloadableActionMenuItem } from '@/utils/PreloadableActionMenuItem'
import { dollySoekLocalStorageKey } from '@/pages/dollySoek/SoekForm'
import {
	tenorSoekLocalStorageKey,
	tenorSoekStateLocalStorageKey,
} from '@/pages/tenorSoek/TenorSoekPage'
import { useCurrentBruker } from '@/utils/hooks/useBruker'

export const PersonDropdown = () => {
	const location = useLocation()
	const [isActive, setIsActive] = useState(false)

	const { currentBruker } = useCurrentBruker()

	useEffect(() => {
		setIsActive(
			location?.pathname === '/gruppe' ||
				location?.pathname === '/dollysoek' ||
				location?.pathname === '/nyansettelser' ||
				location?.pathname === '/tenorpersoner' ||
				location?.pathname === '/identvalidator',
		)
	}, [location])

	return (
		<ActionMenuWrapper
			title="Personer"
			isActive={isActive}
			dataTestId={TestComponentSelectors.BUTTON_HEADER_FINNPERSON}
		>
			<>
				<PreloadableActionMenuItem
					route="/gruppe"
					dataTestId={TestComponentSelectors.BUTTON_HEADER_PERSONER}
					style={{ color: '#212529' }}
				>
					<Icon kind="person" fontSize="1.5rem" />
					<DropdownStyledLink href="/gruppe">Mine personer</DropdownStyledLink>
				</PreloadableActionMenuItem>
				<PreloadableActionMenuItem
					route="/dollysoek"
					dataTestId={TestComponentSelectors.BUTTON_HEADER_DOLLYSOEK}
					style={{ color: '#212529' }}
				>
					<Icon kind="search" fontSize="1.5rem" />
					<DropdownStyledLink
						onClick={() => localStorage.removeItem(dollySoekLocalStorageKey)}
						href="/dollysoek"
					>
						Søk i Dolly
					</DropdownStyledLink>
				</PreloadableActionMenuItem>
				<PreloadableActionMenuItem
					route="/tenorpersoner"
					dataTestId={TestComponentSelectors.BUTTON_HEADER_TENOR}
					style={{ color: '#212529' }}
				>
					<Icon kind="search" fontSize="1.5rem" />
					<DropdownStyledLink
						onClick={() => {
							localStorage.removeItem(tenorSoekLocalStorageKey)
							localStorage.removeItem(tenorSoekStateLocalStorageKey)
						}}
						href="/tenorpersoner"
					>
						Søk i Tenor (Test-Norge)
					</DropdownStyledLink>
				</PreloadableActionMenuItem>
				{currentBruker?.brukertype === 'AZURE' && (
					<PreloadableActionMenuItem route="/identvalidator" style={{ color: '#212529' }}>
						<Icon kind="arena" fontSize="1.5rem" />
						<DropdownStyledLink href="/identvalidator">Valider identer</DropdownStyledLink>
					</PreloadableActionMenuItem>
				)}
				<PreloadableActionMenuItem route="/nyansettelser" style={{ color: '#212529' }}>
					<Icon kind="ansettelse" fontSize="1.5rem" />
					<DropdownStyledLink href="/nyansettelser">Vis nyansettelser</DropdownStyledLink>
				</PreloadableActionMenuItem>
			</>
		</ActionMenuWrapper>
	)
}
