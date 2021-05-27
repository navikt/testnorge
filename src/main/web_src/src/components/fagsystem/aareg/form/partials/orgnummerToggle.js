import React, { useState } from 'react'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { OrganisasjonMedArbeidsforholdSelect } from '~/components/organisasjonSelect'
import { OrgserviceApi } from '~/service/Api'
import { useBoolean } from 'react-use'
import Icon from '~/components/ui/icon/Icon'

const inputValg = { fraListe: 'velg', skrivSelv: 'skriv' }

export const OrgnummerToggle = ({ formikBag, path, opplysningspliktigPath }) => {
	const [inputType, setInputType] = useState(inputValg.fraListe)
	const [error, setError] = useState(null)
	const [success, setSuccess] = useBoolean(false)

	const handleToggleChange = event => {
		setInputType(event.target.value)
		formikBag.setFieldValue(path, '')
	}

	const handleChange = value => {
		opplysningspliktigPath &&
			formikBag.setFieldValue(`${opplysningspliktigPath}`, value.juridiskEnhet)
		formikBag.setFieldValue(`${path}`, value.orgnr)
	}

	const handleBlur = event => {
		setError(null)
		setSuccess(false)
		OrgserviceApi.getOrganisasjonInfo(event.target.value)
			.then(response => {
				setSuccess(true)
				opplysningspliktigPath &&
					formikBag.setFieldValue(`${opplysningspliktigPath}`, response.data.juridiskEnhet)
				formikBag.setFieldValue(`${path}`, response.data.orgnummer)
			})
			.catch(err => setError(err))
	}

	return (
		<div className="toggle--wrapper">
			<ToggleGruppe onChange={handleToggleChange} name={path}>
				<ToggleKnapp
					key={inputValg.fraListe}
					value={inputValg.fraListe}
					checked={inputType === inputValg.fraListe}
				>
					Velg organisasjonsnummer
				</ToggleKnapp>
				<ToggleKnapp
					key={inputValg.skrivSelv}
					value={inputValg.skrivSelv}
					checked={inputType === inputValg.skrivSelv}
				>
					Skriv inn organisasjonsnummer
				</ToggleKnapp>
			</ToggleGruppe>

			{inputType === inputValg.fraListe ? (
				<OrganisasjonMedArbeidsforholdSelect
					afterChange={handleChange}
					path={path}
					label={'Organisasjonsnummer'}
				/>
			) : (
				<div className={'flexbox--align-center'}>
					<DollyTextInput
						name={path}
						type={'number'}
						size="xlarge"
						label={'Organisasjonsnummer'}
						onBlur={handleBlur}
						feil={error && { feilmelding: 'Fant ikke organisasjonen i Q1' }}
					/>
					{success && (
						<>
							<Icon kind="feedback-check-circle" /> Organisasjon funnet
						</>
					)}
				</div>
			)}
		</div>
	)
}
