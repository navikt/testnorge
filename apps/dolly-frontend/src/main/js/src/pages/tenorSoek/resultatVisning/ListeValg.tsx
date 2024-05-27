import { NavigerTilPerson } from '@/pages/tenorSoek/resultatVisning/NavigerTilPerson'
import { Checkbox } from '@navikt/ds-react'
import React from 'react'

type ListeValgProps = {
	person: {
		id: string
		ibruk: boolean
	}
	markertePersoner: Array<string>
	setMarkertePersoner: Function
}

export const ListeValg = ({ person, markertePersoner, setMarkertePersoner }: ListeValgProps) => {
	const { id, ibruk } = person

	const handleChangeCheckbox = (val: any) => {
		if (val?.target?.checked) {
			setMarkertePersoner([...markertePersoner, val?.target?.value])
		} else {
			setMarkertePersoner(markertePersoner.filter((ident) => ident !== val?.target?.value))
		}
	}

	return ibruk ? (
		<NavigerTilPerson ident={id} />
	) : (
		<div
			style={{ margin: '-8px 0' }}
			onClick={(e) => e.stopPropagation()}
			onKeyDown={(e) => e.stopPropagation()}
		>
			<Checkbox value={id} size="small" onChange={(val: any) => handleChangeCheckbox(val)}>
				Importer person
			</Checkbox>
		</div>
	)
}
