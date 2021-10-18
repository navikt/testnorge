import { Box } from '@/components/Box'
import React from 'react'
import { LoadableComponent } from '@navikt/dolly-komponenter'
import OrganisasjonService from '@/services/OrganisasjonService'
import styled from 'styled-components'

const Bold = styled.td`
	font-weight: bold;
`

const OrgTableBox = () => (
	<Box header="Organisasjonstilgang">
		<LoadableComponent
			onFetch={OrganisasjonService.getOrganisasjoner}
			render={(list) => (
				<table>
					<tbody>
						<tr>
							<td>
								<Bold>Navn</Bold>
							</td>
							<td>
								<Bold>Orgnummer</Bold>
							</td>
						</tr>
						{list &&
							list.map((item) => (
								<tr>
									<td>{item.navn + (item.organisasjonsfrom === 'AS' ? ' AS' : '')}</td>
									<td>{item.organisasjonsnummer}</td>
								</tr>
							))}
					</tbody>
				</table>
			)}
		/>
	</Box>
)

OrgTableBox.displayName = 'OrgTableBox'

export default OrgTableBox
