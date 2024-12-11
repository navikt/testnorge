import React from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { FingerButtonIcon, TenancyIcon } from '@navikt/aksel-icons'
import { ActionMenu } from '@navikt/ds-react'
import { ActionMenuWrapper, DropdownStyledLink } from './ActionMenuWrapper'

export const AdminDropdown = () => {
	const navigate = useNavigate()
	const location = useLocation()
	const isActive =
		location?.pathname === '/admin/orgtilgang' ||
		location?.pathname === '/admin/levendearbeidsforhold'

	return (
		<ActionMenuWrapper title="Admin" isActive={isActive}>
			<ActionMenu.Item onClick={() => navigate('/admin/orgtilgang')} style={{ color: '#212529' }}>
				<TenancyIcon title="a11y-title" fontSize="1.5rem" />
				<DropdownStyledLink href="/admin/orgtilgang">Organisasjon-tilgang</DropdownStyledLink>
			</ActionMenu.Item>
			<ActionMenu.Item
				onClick={() => navigate('/admin/levendearbeidsforhold')}
				style={{ color: '#212529' }}
			>
				<FingerButtonIcon title="a11y-title" fontSize="1.5rem" />
				<DropdownStyledLink href="/admin/levendearbeidsforhold">
					Levende arbeidsforhold
				</DropdownStyledLink>
			</ActionMenu.Item>
		</ActionMenuWrapper>
	)
}
