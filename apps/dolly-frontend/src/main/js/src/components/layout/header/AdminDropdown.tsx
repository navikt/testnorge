import React from 'react'
import { useLocation } from 'react-router'
import { FingerButtonIcon, TenancyIcon } from '@navikt/aksel-icons'
import { ActionMenuWrapper, DropdownStyledLink } from './ActionMenuWrapper'
import { PreloadableActionMenuItem } from '@/utils/PreloadableActionMenuItem'

export const AdminDropdown = () => {
	const location = useLocation()
	const isActive =
		location?.pathname === '/admin/orgtilgang' ||
		location?.pathname === '/admin/levendearbeidsforhold'

	return (
		<ActionMenuWrapper title="Admin" isActive={isActive}>
			<PreloadableActionMenuItem route="/admin/orgtilgang" style={{ color: '#212529' }}>
				<TenancyIcon title="a11y-title" fontSize="1.5rem" />
				<DropdownStyledLink href="/admin/orgtilgang">Organisasjon-tilgang</DropdownStyledLink>
			</PreloadableActionMenuItem>
			<PreloadableActionMenuItem route="/admin/levendearbeidsforhold" style={{ color: '#212529' }}>
				<FingerButtonIcon title="a11y-title" fontSize="1.5rem" />
				<DropdownStyledLink href="/admin/levendearbeidsforhold">
					Levende arbeidsforhold
				</DropdownStyledLink>
			</PreloadableActionMenuItem>
		</ActionMenuWrapper>
	)
}
