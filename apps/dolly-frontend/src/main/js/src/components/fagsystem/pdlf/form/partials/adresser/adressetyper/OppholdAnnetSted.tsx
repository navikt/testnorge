import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const OppholdAnnetSted = ({ formikBag, path }) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikSelect name={path} label="Opphold annet sted" options={Options('oppholdAnnetSted')} />
		</div>
	)
}
