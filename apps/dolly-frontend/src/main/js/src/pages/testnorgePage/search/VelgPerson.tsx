import React from 'react'
import { Checkbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { ImportPerson } from '~/pages/testnorgePage/search/SearchView'
import { PdlData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

interface VelgPersonProps {
	ident: string
	data: PdlData
	valgtePersoner: Array<ImportPerson>
	setValgtePersoner: (personer: Array<ImportPerson>) => void
}

export const VelgPerson = ({ ident, data, valgtePersoner, setValgtePersoner }: VelgPersonProps) => {
	const avhuket = valgtePersoner.map((person) => person?.ident).includes(ident)

	const handleOnChange = () => {
		if (avhuket) {
			setValgtePersoner(valgtePersoner.filter((valgtident) => valgtident.ident !== ident))
		} else {
			setValgtePersoner([...valgtePersoner, { ident: ident, data: data }])
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
