import { useHentLagredeSoek } from '@/utils/hooks/useSoek'
import { Chips, VStack } from '@navikt/ds-react'
import { useState } from 'react'

export enum soekType {
	dolly = 'DOLLY',
	tenor = 'TENOR',
}

export const SisteSoek = ({ soekType }) => {
	const { lagredeSoek, loading, error } = useHentLagredeSoek(soekType)
	console.log('lagredeSoek: ', lagredeSoek) //TODO - SLETT MEG
	//TODO: Map igjennom lagredeSoek, hvor siste soek ligger foerst, og push til nytt objekt, kun hvis likt objekt (eller key?) ikke finnes fra foer
	//TODO: Max 10 items lang

	const [selected, setSelected] = useState([])

	const lagredeSoekData = {
		kjoenn: { path: 'personRequest.kjoenn', value: 'KVINNE', label: 'Kjønn: Kvinne' },
		alderFom: { path: 'personRequest.alderFom', value: '20', label: 'Alder f.o.m: 20' },
		alderTom: { path: 'personRequest.alderTom', value: '30', label: 'Alder t.o.m: 30' },
	}

	const options = Object.values(lagredeSoekData)

	return (
		<VStack gap="3" style={{ marginBottom: '15px' }}>
			{/*<h4>SISTE SØK:</h4>*/}
			<Chips>
				{options.map((option) => (
					<Chips.Toggle
						key={option.label}
						selected={selected.includes(option.label)}
						onClick={() =>
							setSelected(
								selected.includes(option.label)
									? selected.filter((x) => x !== option.label)
									: [...selected, option.label],
							)
						}
					>
						{option.label}
					</Chips.Toggle>
				))}
			</Chips>
		</VStack>
	)
}
