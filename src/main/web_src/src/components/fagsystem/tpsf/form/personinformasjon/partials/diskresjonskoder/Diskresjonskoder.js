import React, { useState } from 'react'
import _has from 'lodash/has'
import _get from 'lodash/get'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { PersoninformasjonKodeverk, AdresseKodeverk } from '~/config/kodeverk'

import './diskresjonskoder.less'

export const Diskresjonskoder = ({ basePath, formikBag }) => {
	const [harUfb, setHarUfb] = useState(_get(formikBag.values, `${basePath}.utenFastBopel`))

	// For å sjekke om det er valgt boadresse i tillegg til diskresjonskoder
	const harBoadresse = _has(formikBag.values, `${basePath}.boadresse.flyttedato`)

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
					kodeverk={PersoninformasjonKodeverk.Diskresjonskoder}
					size="large"
				/>

				<FormikCheckbox
					name={paths.utenFastBopel}
					label="Uten fast bopel"
					afterChange={handleChangeUFB}
					checkboxMargin
				/>

				{/* Skal kunne velge kommunenummer selv om man ikke har fast bopel, men trenger ikke eget felt når man også har valgt boadresse. */}
				{harUfb && !harBoadresse && (
					<FormikSelect
						name={paths.kommunenr}
						label="Kommunenummer"
						afterChange={handleChangeKommunenr}
						kodeverk={AdresseKodeverk.Kommunenummer}
					/>
				)}
			</div>
		</Vis>
	)
}
