import { Box } from '@/components/Box'
import React from 'react'
import { LoadableComponent } from '@navikt/dolly-komponenter'
import OrganisasjonService from '@/services/OrganisasjonService'
import styled from 'styled-components'
// @ts-ignore
import { CopyToClipboard } from 'react-copy-to-clipboard/lib/Component'
// @ts-ignore

const Bold = styled.span`
	font-weight: bold;
`

const OrgTableBox = () => (
	<Box
		header="Organisasjonstilgang"
		onRender={() => (
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
								list.map((item, index) => (
									<tr key={index}>
										<td>{item.navn + (item.organisasjonsfrom === 'AS' ? ' AS' : '')}</td>
										<td>
											<CopyToClipboard text={item.organisasjonsnummer}>
												<a href="" onClick={(event) => event.preventDefault()}>
													{item.organisasjonsnummer}
												</a>
											</CopyToClipboard>
										</td>
									</tr>
								))}
						</tbody>
					</table>
				)}
			/>
		)}
	/>
)

OrgTableBox.displayName = 'OrgTableBox'

export default OrgTableBox
