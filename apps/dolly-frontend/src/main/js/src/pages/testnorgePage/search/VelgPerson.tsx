import React, { Dispatch, SetStateAction } from 'react'
import { Checkbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { Person } from '~/service/services/personsearch/types'

interface VelgPersonProps {
	personinfo: Person
	valgtePersoner: Array<string>
	setValgtePersoner: Dispatch<SetStateAction<Array<string>>>
}

export const VelgPerson = ({ personinfo, valgtePersoner, setValgtePersoner }: VelgPersonProps) => {
	const gjeldendePerson = personinfo.ident
	const avhuket = valgtePersoner.some((id) => id === gjeldendePerson)

	const handleOnChange = () => {
		if (avhuket) {
			setValgtePersoner(valgtePersoner.filter((ident) => ident !== personinfo.ident))
		} else {
			setValgtePersoner([...valgtePersoner, personinfo.ident])
		}
	}

	return (
		<div className="velg-person">
			{
				<Checkbox
					id={personinfo.ident}
					title={'Marker'}
					label={''}
					checked={avhuket}
					onChange={handleOnChange}
				/>
			}
		</div>
	)
}
