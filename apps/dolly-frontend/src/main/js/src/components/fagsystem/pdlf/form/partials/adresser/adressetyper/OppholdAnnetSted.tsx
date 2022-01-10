import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikProps } from 'formik'

interface OppholdAnnetStedValues {
	formikBag: FormikProps<{}>
	path: string
}

export const OppholdAnnetSted = ({ formikBag, path }: OppholdAnnetStedValues) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikSelect name={path} label="Opphold annet sted" options={Options('oppholdAnnetSted')} />
		</div>
	)
}
