import React from 'react'
import _has from 'lodash/has'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export const Diskresjonskoder = ({ formikBag }) => {
	const harBoadresse = _has(formikBag.initialValues, 'tpsf.boadresse')

	const handleChangeUFB = selected => {
		if (!harBoadresse && !selected) {
			formikBag.setFieldValue('tpsf.boadresse', null)
		}
	}

	const handleChangeKommunenr = val => {
		formikBag.setFieldValue('tpsf.boadresse.adressetype', 'GATE')
	}

	return (
		<div>
			<FormikSelect
				name="tpsf.spesreg"
				label="Diskresjonskode"
				kodeverk="Diskresjonskoder"
				size="large"
			/>

			<FormikCheckbox
				name="tpsf.utenFastBopel"
				label="Uten fast bopel"
				afterChange={handleChangeUFB}
			/>

			{formikBag.values.tpsf.utenFastBopel && (
				<FormikSelect
					name="tpsf.boadresse.kommunenr"
					label="Kommunenummer"
					afterChange={handleChangeKommunenr}
					kodeverk="Kommuner"
				/>
			)}
		</div>
	)
}
