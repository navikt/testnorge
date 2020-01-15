import React, { useState } from 'react'
import _has from 'lodash/has'
import _get from 'lodash/get'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

import './diskresjonskoder.less'

export const Diskresjonskoder = ({ basePath, formikBag }) => {
	const [harUfb, setHarUfb] = useState(_get(formikBag.values, `${basePath}.utenFastBopel`))

	// For å sjekke om det er valgt boadresse i tillegg til diskresjonskoder
	const harBoadresse = _has(formikBag.values, 'tpsf.boadresse.flyttedato')

	const paths = {
		spesreg: `${basePath}.spesreg`,
		utenFastBopel: `${basePath}.utenFastBopel`,
		kommunenr: `${basePath}.boadresse.kommunenr`
	}

	const handleChangeUFB = selected => {
		setHarUfb(selected)
		if (!harBoadresse && !selected) {
			formikBag.setFieldValue(`${basePath}.boadresse`, null)
		}
	}

	const handleChangeKommunenr = val => {
		formikBag.setFieldValue(`${basePath}.boadresse.adressetype`, 'GATE')
	}

	return (
		<Vis attributt={Object.values(paths)}>
			<div className="spesreg-component">
				<FormikSelect
					name={paths.spesreg}
					label="Diskresjonskode"
					kodeverk="Diskresjonskoder"
					size="large"
				/>

				<FormikCheckbox
					name={paths.utenFastBopel}
					label="Uten fast bopel"
					afterChange={handleChangeUFB}
				/>

				{harUfb && (
					<FormikSelect
						name={paths.kommunenr}
						label="Kommunenummer"
						afterChange={handleChangeKommunenr}
						kodeverk="Kommuner"
					/>
				)}
			</div>
		</Vis>
	)
}
