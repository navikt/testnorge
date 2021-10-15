import { Box } from '@/components/Box'
import React from 'react'
import { LoadableComponent } from '@navikt/dolly-komponenter'
import OrganisasjonService from '@/services/OrganisasjonService'
import styled from 'styled-components'

const Bold = styled.td`
	font-weight: bold;
`

export default () => (
	<Box header="Organisasjonstilgang">
		<LoadableComponent
			onFetch={OrganisasjonService.getOrganisasjoner}
			render={(list) => (
				<table>
					<tr>
						<td>
							<Bold>Navn</Bold>
						</td>
						<td>
							<Bold>Orgnummer</Bold>
						</td>
					</tr>
					{list.map((item) => (
						<tr>
							<td>{item.navn + (item.organisasjonsfrom === 'AS' ? ' AS' : '')}</td>
							<td>{item.organisasjonsnummer}</td>
						</tr>
					))}
				</table>
			)}
		/>
	</Box>
)
