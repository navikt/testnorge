import React, { BaseSyntheticEvent, useEffect, useState } from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ifPresent, requiredString } from '~/utils/YupValidations'
import { ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { ToggleGruppe } from 'nav-frontend-skjema'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { malerApi } from '~/pages/minSide/maler/MalerApi'

// @ts-ignore
export const MalForm = ({ formikBag, brukerId, opprettetFraMal }) => {
	enum MalTyper {
		INGEN = 'EGEN',
		OPPRETT = 'OPPRETT',
		ENDRE = 'ENDRE',
	}

	const [malOptions, setMalOptions] = useState([])

	const getMalOptions = (malbestillinger: [{ malNavn: string; malId: number }]) => {
		if (!malbestillinger) return []
		return malbestillinger.map((mal) => ({
			value: mal.malNavn,
			label: mal.malNavn,
		}))
	}

	useEffect(() => {
		malerApi.hentMalerForBrukerMedOptionalNavn(brukerId, null).then((response) => {
			setMalOptions(getMalOptions(response))
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
			label: 'Overskriv eksisterende mal',
		},
	]
	const [typeMal, setTypeMal] = useState(MalTyper.INGEN)

	const handleToggleChange = (value: MalTyper) => {
		setTypeMal(value)
		if (value === MalTyper.INGEN) {
			formikBag.setFieldValue('malBestillingNavn', undefined)
		} else if (value === MalTyper.OPPRETT) {
			formikBag.setFieldValue('malBestillingNavn', '')
		} else if (value === MalTyper.ENDRE) {
			formikBag.setFieldValue('malBestillingNavn', opprettetFraMal ? opprettetFraMal : '')
		}
	}

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
				<FormikSelect
					name={'malBestillingNavn'}
					size={'xlarge'}
					label="Malnavn"
					options={malOptions}
					fastfield={false}
				/>
			) : (
				<FormikTextInput name="malBestillingNavn" size={'xlarge'} label="Malnavn" />
			)}
		</div>
	)
}

MalForm.validation = {
	malBestillingNavn: ifPresent('$malBestillingNavn', requiredString.nullable()),
}
