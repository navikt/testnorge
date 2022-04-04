import React from 'react'
import { Checkbox } from '~/components/ui/form/inputs/checbox/Checkbox'

interface VelgPersonProps {
	ident: string
	valgtePersoner: Array<string>
	setValgtePersoner: (personer: Array<string>) => void
}

export const VelgPerson = ({ ident, valgtePersoner, setValgtePersoner }: VelgPersonProps) => {
	const avhuket = valgtePersoner.includes(ident)

	const handleOnChange = () => {
		if (avhuket) {
			setValgtePersoner(valgtePersoner.filter((valgtident) => valgtident !== ident))
		} else {
			setValgtePersoner([...valgtePersoner, ident])
		}
	}

	return (
		<div
			className="velg-person"
			onClick={(event: React.MouseEvent<any>) => {
				event.stopPropagation()
			}}
		>
			<Checkbox
				id={ident}
				title={'Marker'}
				label={''}
				checked={avhuket}
				onChange={handleOnChange}
			/>
		</div>
	)
}
