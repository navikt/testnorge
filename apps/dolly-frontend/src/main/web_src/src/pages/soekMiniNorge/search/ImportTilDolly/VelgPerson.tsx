import React, { Dispatch, SetStateAction } from 'react'
import { Checkbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { Innhold } from '../../hodejegeren/types'

interface VelgPerson {
	personinfo: Innhold
	valgtePersoner: Array<string>
	setValgtePersoner: Dispatch<SetStateAction<Array<string>>>
}

export const VelgPerson = ({ personinfo, valgtePersoner, setValgtePersoner }: VelgPerson) => {
	const gjeldendePerson = personinfo.personIdent.id
	const avhuket = valgtePersoner.some(id => id === gjeldendePerson)

	const handleOnChange = () => {
		if (avhuket) {
			setValgtePersoner(valgtePersoner.filter(ident => ident !== personinfo.personIdent.id))
		} else {
			setValgtePersoner([...valgtePersoner, personinfo.personIdent.id])
		}
	}

	return (
		<div className="velg-person">
			{
				<Checkbox
					id={personinfo.personIdent.id}
					title={'Marker'}
					label={''}
					checked={avhuket}
					onChange={handleOnChange}
				/>
			}
		</div>
	)
}
