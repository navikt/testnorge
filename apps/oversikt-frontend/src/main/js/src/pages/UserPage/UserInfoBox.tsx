import React, { useState } from 'react'
import { Box } from '@/components/Box'
import styled from 'styled-components'
import { Hovedknapp } from 'nav-frontend-knapper'
import { Input } from 'nav-frontend-skjema'
import BrukerService, { Bruker } from '@/services/BrukerService'

const Bold = styled.td`
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

	return (
		<Box header="Brukerinfo">
			<table>
				<tr>
					<td>
						<Bold>Brukernavn</Bold>
					</td>
					{bruker && <td>{bruker.brukernavn}</td>}
				</tr>
				<tr>
					<td>
						<Bold>Organisasjonsnummer</Bold>
					</td>
					{bruker && <td>{bruker.organisasjonsnummer}</td>}
				</tr>
			</table>
			<Input label="Orgnummer" type="text" onBlur={(event) => setOrgnummer(event.target.value)} />
			<StyledHovedknapp onClick={onSubmit}>Hent bruker</StyledHovedknapp>
		</Box>
	)
}
