import React, { useEffect, useState } from 'react'
import { useLocation } from 'react-router'
import {
	FingerButtonIcon,
	InformationSquareIcon,
	LineGraphIcon,
	TenancyIcon,
} from '@navikt/aksel-icons'
import { ActionMenuWrapper, DropdownStyledLink } from './ActionMenuWrapper'
import { PreloadableActionMenuItem } from '@/utils/PreloadableActionMenuItem'

export const AdminDropdown = () => {
	const location = useLocation()
	const [isActive, setIsActive] = useState(false)
	useEffect(() => {
		setIsActive(
			location?.pathname === '/orgtilgang' ||
				location?.pathname === '/levendearbeidsforhold' ||
				location?.pathname === '/infostriper' ||
				location?.pathname === '/dashboard',
		)
	}, [location])

	return (
		<ActionMenuWrapper title="Admin" isActive={isActive}>
			<PreloadableActionMenuItem route="/dashboard" style={{ color: '#212529' }}>
				<LineGraphIcon title="a11y-title" fontSize="1.5rem" />
				<DropdownStyledLink href="/dashboard">Dashboard</DropdownStyledLink>
			</PreloadableActionMenuItem>
			<PreloadableActionMenuItem route="/orgtilgang" style={{ color: '#212529' }}>
				<TenancyIcon title="a11y-title" fontSize="1.5rem" />
				<DropdownStyledLink href="/orgtilgang">Organisasjon-tilgang</DropdownStyledLink>
			</PreloadableActionMenuItem>
			<PreloadableActionMenuItem route="/levendearbeidsforhold" style={{ color: '#212529' }}>
				<FingerButtonIcon title="a11y-title" fontSize="1.5rem" />
				<DropdownStyledLink href="/levendearbeidsforhold">
					Levende arbeidsforhold
				</DropdownStyledLink>
			</PreloadableActionMenuItem>
			<PreloadableActionMenuItem route="/infostriper" style={{ color: '#212529' }}>
				<InformationSquareIcon title="a11y-title" fontSize="1.5rem" />
				<DropdownStyledLink href="/infostriper">Administrer infostriper</DropdownStyledLink>
			</PreloadableActionMenuItem>
		</ActionMenuWrapper>
	)
}
