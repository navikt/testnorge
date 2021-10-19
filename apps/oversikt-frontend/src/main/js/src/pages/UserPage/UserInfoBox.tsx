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
	const [bruker, setBruker] = useState<Bruker>()

	const onSubmit = () => {
		setBruker(null)
		return BrukerService.getBruker(orgnummer).then((value) => {
			setBruker(value)
		})
	}

	const formatDate = (value: string) => {
		const date = new Date(Date.parse(value))
		return date.toLocaleString('no-NO')
	}

	return (
		<Box header="Brukerinfo">
			<StyledTable>
				<tbody>
					<tr>
						<td>
							<Bold>Id:</Bold>
						</td>
						{bruker && (
							<td>
								<CopyToClipboard text={bruker.id}>
									<a href="" onClick={(event) => event.preventDefault()}>
										(Copy id)
									</a>
								</CopyToClipboard>
							</td>
						)}
					</tr>
					<tr>
						<td>
							<Bold>Brukernavn:</Bold>
						</td>
						{bruker && <td>{bruker.brukernavn}</td>}
					</tr>
					<tr>
						<td>
							<Bold>Orgnummer:</Bold>
						</td>
						{bruker && <td>{bruker.organisasjonsnummer}</td>}
					</tr>
					<tr>
						<td>
							<Bold>Opprettet:</Bold>
						</td>
						{bruker && <td>{formatDate(bruker.opprettet)}</td>}
					</tr>
					<tr>
						<td>
							<Bold>Sist Innlogget:</Bold>
						</td>
						{bruker && <td>{formatDate(bruker.sistInnlogget)}</td>}
					</tr>
				</tbody>
			</StyledTable>
			<Search
				onBlur={(event) => setOrgnummer(event.target.value)}
				onSubmit={onSubmit}
				texts={{ label: 'Orgnummer', button: 'Hent' }}
			/>
		</Box>
	)
}
