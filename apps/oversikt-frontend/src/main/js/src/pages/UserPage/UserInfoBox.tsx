import React, { useState } from 'react'
import { Box } from '@/components/Box'
import styled from 'styled-components'
import BrukerService, { Bruker } from '@/services/BrukerService'
// @ts-ignore
import { CopyToClipboard } from 'react-copy-to-clipboard/lib/Component'
// @ts-ignore
import Search from '@/pages/UserPage/Search'

const Bold = styled.span`
	font-weight: bold;
`

const StyledTable = styled.table`
	padding-bottom: 10px;
`

export default () => {
	const [orgnummer, setOrgnummer] = useState<string>()

	const formatDate = (value: string) => {
		if (!value) {
			return 'Ikke satt'
		}

		const date = new Date(Date.parse(value))
		return date.toLocaleString('no-NO')
	}

	return (
		<Box
			onSubmit={() => BrukerService.getBruker(orgnummer)}
			header="Brukerinfo"
			onRender={({ onSubmit, value, loading }) => (
				<>
					<StyledTable>
						<tbody>
							<tr>
								<td>
									<Bold>Id:</Bold>
								</td>
								{value && (
									<td>
										<CopyToClipboard text={value.id}>
											<a href="" onClick={(event) => event.preventDefault()}>
												[Copy]
											</a>
										</CopyToClipboard>
									</td>
								)}
							</tr>
							<tr>
								<td>
									<Bold>Brukernavn:</Bold>
								</td>
								{value && <td>{value.brukernavn}</td>}
							</tr>
							<tr>
								<td>
									<Bold>Orgnummer:</Bold>
								</td>
								{value && <td>{value.organisasjonsnummer}</td>}
							</tr>
							<tr>
								<td>
									<Bold>Opprettet:</Bold>
								</td>
								{value && <td>{formatDate(value.opprettet)}</td>}
							</tr>
							<tr>
								<td>
									<Bold>Sist Innlogget:</Bold>
								</td>
								{value && <td>{formatDate(value.sistInnlogget)}</td>}
							</tr>
						</tbody>
					</StyledTable>
					<Search
						onBlur={(event) => setOrgnummer(event.target.value)}
						onSubmit={onSubmit}
						loading={loading}
						texts={{ label: 'Orgnummer', button: 'Hent' }}
					/>
				</>
			)}
		/>
	)
}
