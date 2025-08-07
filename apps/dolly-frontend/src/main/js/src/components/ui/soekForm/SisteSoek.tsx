import { useHentLagredeSoek } from '@/utils/hooks/useSoek'
import { Chips, VStack } from '@navikt/ds-react'
import * as _ from 'lodash-es'

export enum soekType {
	dolly = 'DOLLY',
	tenor = 'TENOR',
}

export const SisteSoek = ({ soekType, formValues, handleChange }) => {
	// TODO: Funker det med registreRequest?
	// TODO: Sjekk ulike typer felter, at de fungerer som forventet

	const { lagredeSoek, loading, error } = useHentLagredeSoek(soekType)

	const lagredeSoekData = {}
	lagredeSoek?.forEach((soek, idx) => {
		Object.entries(soek?.soekVerdi)?.forEach((verdi) => {
			if (!lagredeSoekData[verdi[0]]) {
				lagredeSoekData[verdi[0]] = verdi[1]
			}
		})
	})

	const options = Object.values(lagredeSoekData)

	return (
		<VStack gap="3" style={{ marginBottom: '15px' }}>
			{/*<h4>SISTE SØK:</h4>*/}
			<Chips>
				{options.map((option) => (
					<Chips.Toggle
						key={option.label}
						selected={_.get(formValues, option.path) === option.value}
						onClick={() => {
							handleChange(
								_.get(formValues, option.path) !== option.value ? option.value : null,
								option.path?.split('.')[1].trim(),
							)
						}}
					>
						{option.label}
					</Chips.Toggle>
				))}
			</Chips>
		</VStack>
	)
}
