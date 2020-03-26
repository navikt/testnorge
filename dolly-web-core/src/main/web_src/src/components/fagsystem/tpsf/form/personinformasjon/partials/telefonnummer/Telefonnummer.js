// TODO Gjør denne om til .tsx!!!

import React, { useState } from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import Button from '~/components/ui/button/Button'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'

export const Telefonnummer = ({ formikBag }) => {
	const [harToTlfnr, setHarToTlfnr] = useState(_get(formikBag.values, 'tpsf.telefonnummer_2'))

	const paths = {
		telefonLandskode_1: 'tpsf.telefonLandskode_1',
		telefonnummer_1: 'tpsf.telefonnummer_1',
		telefonLandskode_2: 'tpsf.telefonLandskode_2',
		telefonnummer_2: 'tpsf.telefonnummer_2'
	}

	const leggTilTlfnr = () => {
		formikBag.setFieldValue('tpsf.telefonLandskode_2', '')
		formikBag.setFieldValue('tpsf.telefonnummer_2', '')
		setHarToTlfnr(true)
	}

	const fjernTlfnr = () => {
		formikBag.setFieldValue('tpsf.telefonLandskode_2', undefined)
		formikBag.setFieldValue('tpsf.telefonnummer_2', undefined)
		setHarToTlfnr(false)
	}

	// TODO sjekk om jeg kan ha landkode som i label (Norge (+47))
	return (
		<Vis attributt={Object.values(paths)}>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name="tpsf.telefonLandskode_1"
					label="Telefon landkode"
					kodeverk={PersoninformasjonKodeverk.Retningsnumre}
					size="large"
					isClearable={false}
				/>
				<FormikTextInput
					name="tpsf.telefonnummer_1"
					label="Telefonnummer"
					type="string"
					size="large"
				/>

				{!harToTlfnr && (
					<Button onClick={leggTilTlfnr} kind="add-circle">
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
							isClearable={false}
						/>
						<FormikTextInput
							name="tpsf.telefonnummer_2"
							label="Telefonnummer"
							type="string"
							size="large"
						/>
						<Button kind="trashcan" onClick={fjernTlfnr} title="Fjern" />
					</>
				)}
			</div>
		</Vis>
	)
}
