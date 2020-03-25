// TODO Gjør denne om til .tsx!!!

import React, { useState } from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import Button from '~/components/ui/button/Button'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'

export const Telefonnummer = ({ formikBag }) => {
	const [harToTlfnr, setHarToTlfnr] = useState(_get(formikBag.values, 'tpsf.telefonnummer_1'))
	console.log('harToTlfnr :', harToTlfnr)

	const fjernTlfnr = () => {
		setHarToTlfnr(false)
		formikBag.setFieldValue('tpsf.telefonnummer_2', '')
		formikBag.setFieldValue('tpsf.telefonLandskode_2', '')
	}

	return (
		<>
			<FormikSelect
				name="tpsf.telefonLandskode_1"
				label="Telefon landkode"
				kodeverk={PersoninformasjonKodeverk.Retningsnumre}
				size="large"
				// disabled={harToTlfnr}
			/>
			<p>{_get(formikBag.values, 'tpsf.telefonLandskode_1')}</p>
			<FormikTextInput
				name="tpsf.telefonnummer_1"
				label="Telefonnummer"
				type="string" //number???
				size="large"
				// disabled={harToTlfnr}
			/>

			{!harToTlfnr && (
				<Button onClick={() => setHarToTlfnr(true)} kind="add-circle">
					Telefonnummer
				</Button>
			)}

			{harToTlfnr && (
				<>
					<FormikSelect
						name="tpsf.telefonLandskode_2"
						label="Telefon landkode"
						kodeverk={PersoninformasjonKodeverk.Retningsnumre}
						size="large"
					/>
					<p>{_get(formikBag.values, 'tpsf.telefonLandskode_2')}</p>
					<FormikTextInput
						name="tpsf.telefonnummer_2"
						label="Telefonnummer"
						type="string" //number???
						size="large"
					/>
					<Button kind="trashcan" onClick={fjernTlfnr} title="Fjern" />
				</>
			)}
		</>
	)
}
