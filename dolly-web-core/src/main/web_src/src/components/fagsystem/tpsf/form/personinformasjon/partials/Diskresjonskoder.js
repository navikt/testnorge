import React from 'react'
import _has from 'lodash/has'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export const Diskresjonskoder = ({ formikBag }) => {
	const harBoadresse = _has(formikBag.values, 'tpsf.boadresse')

	const handleChangeUFB = selected => {
		if (!harBoadresse && !selected) {
			formikBag.setFieldValue('tpsf.boadresse', null)
		}
	}

	const handleChangeKommunenr = val => {
		formikBag.setFieldValue('tpsf.boadresse.adressetype', 'GATE')
	}

	return (
		<Vis attributt="tpsf.spesreg">
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
		</Vis>
	)
}
