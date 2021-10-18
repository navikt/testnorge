import React, { useState } from 'react'
import { Box } from '@/components/Box'
import styled from 'styled-components'
import { Hovedknapp } from 'nav-frontend-knapper'
import { Input } from 'nav-frontend-skjema'
import BrukerService, { Bruker } from '@/services/BrukerService'

const HeaderTd = styled.td`
	font-weight: bold;
`

const StyledHovedknapp = styled(Hovedknapp)`
	margin: 5px 0;
`

export default () => {
	const [orgnummer, setOrgnummer] = useState<string>()
	const [bruker, setBruker] = useState<Bruker>()

	const onSubmit = () =>
		BrukerService.getBruker(orgnummer).then((value) => {
			setBruker(value)
		})

	const formatDate = (value: string) => {
		const date = new Date(Date.parse(value))
		return date.toLocaleString('no-NO')
	}

	return (
		<Box header="Brukerinfo">
			<table>
				<tbody>
					<tr>
						<HeaderTd>Brukernavn:</HeaderTd>
						{bruker && <td>{bruker.brukernavn}</td>}
					</tr>
					<tr>
						<HeaderTd>Orgnummer:</HeaderTd>
						{bruker && <td>{bruker.organisasjonsnummer}</td>}
					</tr>
					<tr>
						<HeaderTd>Opprettet:</HeaderTd>
						{bruker && <td>{bruker.sistInnlogget}</td>}
					</tr>
					<tr>
						<HeaderTd>Sist Innlogget:</HeaderTd>
						{bruker && <td>{formatDate(bruker.sistInnlogget)}</td>}
					</tr>
				</tbody>
			</table>
			<Input label="Orgnummer" type="text" onBlur={(event) => setOrgnummer(event.target.value)} />
			<StyledHovedknapp onClick={onSubmit}>Hent bruker</StyledHovedknapp>
		</Box>
	)
}
