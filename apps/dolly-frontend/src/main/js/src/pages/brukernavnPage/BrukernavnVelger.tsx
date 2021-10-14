import React, { useState } from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { Organisasjon } from '~/pages/brukernavnPage/BrukernavnModel'
import styled from 'styled-components'

type BrukernavnVelgerProps = {
	organisasjon: Organisasjon
}

const Selector = styled.div`
	display: flexbox;
	align-items: center;
	text-align: left;
	justify-content: center;
	margin-bottom: 20px;
`

export default ({ organisasjon }: BrukernavnVelgerProps) => {
	const [brukernavn, setBrukernavn] = useState<string>('')

	const clickVidere = () => {}

	return (
		<React.Fragment>
			<h3>
				Lag ditt eget brukernavn som brukes når du representerer <b>{organisasjon.navn}</b>. Neste
				gang du logger inn for denne organisasjonen skjer det automatisk med dette brukernavnet.
			</h3>

			<Selector>
				<DollyTextInput
					name="brukernavn"
					label="Brukernavn"
					value={brukernavn}
					// @ts-ignore
					size="xlarge"
					onChange={(e: any) => setBrukernavn(e.target.value)}
				/>
				<NavButton type="hoved" className="videre-button">
					Gå videre til Dolly
				</NavButton>
			</Selector>
		</React.Fragment>
	)
}
