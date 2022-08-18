import React, { useState } from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ifPresent, requiredString } from '~/utils/YupValidations'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { Mal, useDollyMalerBrukerOgMalnavn } from '~/utils/hooks/useMaler'
import Loading from '~/components/ui/loading/Loading'
import { ToggleGroup } from '@navikt/ds-react'

type Props = {
	brukerId: string
	formikBag: { setFieldValue: (arg0: string, arg1: string) => void }
	opprettetFraMal: string
}

// eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
export const MalForm = ({ brukerId, formikBag: { setFieldValue }, opprettetFraMal }: Props) => {
	enum MalTyper {
		INGEN = 'EGEN',
		OPPRETT = 'OPPRETT',
		ENDRE = 'ENDRE',
	}

	const getMalOptions = (malbestillinger: Mal[]) => {
		if (!malbestillinger) {
			return []
		}
		return malbestillinger.map((mal) => ({
			value: mal.malNavn,
			label: mal.malNavn,
		}))
	}

	const [typeMal, setTypeMal] = useState(MalTyper.INGEN)
	const { maler, loading } = useDollyMalerBrukerOgMalnavn(brukerId, null)

	if (loading) {
		return <Loading label="Laster maler..." />
	}

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

	const handleToggleChange = (value: MalTyper) => {
		setTypeMal(value)
		if (value === MalTyper.INGEN) {
			setFieldValue('malBestillingNavn', undefined)
		} else if (value === MalTyper.OPPRETT) {
			setFieldValue('malBestillingNavn', '')
		} else if (value === MalTyper.ENDRE) {
			setFieldValue('malBestillingNavn', opprettetFraMal || '')
		}
	}

	return (
		<div className="input-oppsummering">
			<h2>Lagre som mal</h2>
			<div className="flexbox--align-center">
				<div className="toggle--wrapper">
					<ToggleGroup
						onChange={(value: MalTyper) => handleToggleChange(value)}
						defaultValue={MalTyper.INGEN}
					>
						{toggleValues.map((type) => (
							<ToggleGroup.Item key={type.value} value={type.value}>
								{type.label}
							</ToggleGroup.Item>
						))}
					</ToggleGroup>
				</div>
			</div>
			{typeMal === MalTyper.ENDRE ? (
				<FormikSelect
					name={'malBestillingNavn'}
					size={'xlarge'}
					label="Malnavn"
					options={getMalOptions(maler)}
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
