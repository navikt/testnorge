import React, { BaseSyntheticEvent, useEffect, useState } from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ifPresent, requiredString } from '~/utils/YupValidations'
import { ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { ToggleGruppe } from 'nav-frontend-skjema'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { malerApi } from '~/pages/minSide/maler/MalerApi'

// @ts-ignore
export const MalForm = ({ formikBag, brukerId }) => {
	enum MalTyper {
		INGEN = 'EGEN',
		OPPRETT = 'OPPRETT',
		ENDRE = 'ENDRE',
	}
	console.log('brukerId: ', brukerId) //TODO - SLETT MEG

	useEffect(() => {
		malerApi.hentMalerForBrukerMedOptionalNavn(brukerId, null).then((response) => {
			console.log('response: ', response) //TODO - SLETT MEG
		})
	}, [])

	const toggleValues = [
		{
			value: MalTyper.INGEN,
			label: 'Ikke opprett',
		},
		{
			value: MalTyper.OPPRETT,
			label: 'Legg til ny mal',
		},
		{
			value: MalTyper.ENDRE,
			label: 'Endre eksisterende mal',
		},
	]
	const [typeMal, setTypeMal] = useState(MalTyper.INGEN)

	const handleToggleChange = (value: MalTyper) => {
		setTypeMal(value)
		if (value === MalTyper.INGEN) {
			formikBag.setFieldValue('malBestillingNavn', undefined)
		} else if (value === MalTyper.OPPRETT || value === MalTyper.ENDRE) {
			formikBag.setFieldValue('malBestillingNavn', '')
		}
	}

	//TODO: Sjekke om malnavn allerede finnes
	return (
		<div className="input-oppsummering">
			<h2>Lagre som mal</h2>
			<div className="flexbox--align-center">
				<div className="toggle--wrapper">
					<ToggleGruppe
						onChange={(e: BaseSyntheticEvent) => handleToggleChange(e.target.value)}
						name={'arbeidsforhold'}
					>
						{toggleValues.map((type) => (
							<ToggleKnapp key={type.value} value={type.value} checked={type.value === typeMal}>
								{type.label}
							</ToggleKnapp>
						))}
					</ToggleGruppe>
				</div>
			</div>
			{typeMal === MalTyper.ENDRE ? (
				<FormikSelect name={'malBestillingNavn'} label="Malnavn" options={null} />
			) : (
				<FormikTextInput name="malBestillingNavn" label="Malnavn" />
			)}
		</div>
	)
}

MalForm.validation = {
	malBestillingNavn: ifPresent('$malBestillingNavn', requiredString),
}
