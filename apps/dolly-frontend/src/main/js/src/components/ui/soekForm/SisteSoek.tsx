import { useHentLagredeSoek } from '@/utils/hooks/useSoek'
import { Chips, VStack } from '@navikt/ds-react'
import { useState } from 'react'

export enum soekType {
	dolly = 'DOLLY',
	tenor = 'TENOR',
}

export const SisteSoek = ({ soekType, formMethods, handleChange }) => {
	const { lagredeSoek, loading, error } = useHentLagredeSoek(soekType)
	console.log('lagredeSoek: ', lagredeSoek) //TODO - SLETT MEG

	const [selected, setSelected] = useState([])

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
						// TODO: Kanskje bare la selected gjenspeile det som ligger i soek form, slik at chips blir oppdatert naar man endrer i form?
						selected={selected.includes(option.label)}
						onClick={() => {
							setSelected(
								selected.includes(option.label)
									? selected.filter((x) => x !== option.label)
									: [...selected, option.label],
							)
							handleChange(
								!selected.includes(option.label) ? option.value : null,
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
