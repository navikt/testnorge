import React, { BaseSyntheticEvent, useState } from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ifPresent, requiredString } from '~/utils/YupValidations'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { Mal, useDollyMalerBrukerOgMalnavn } from '~/utils/hooks/useMaler'
import Loading from '~/components/ui/loading/Loading'
import { ToggleGroup } from '@navikt/ds-react'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

type Props = {
	brukerId: string
	formikBag: { setFieldValue: (arg0: string, arg1: string) => void }
	opprettetFraMal: string
}

// eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
export const MalForm = ({ brukerId, formikBag: { setFieldValue }, opprettetFraMal }: Props) => {
	enum MalTyper {
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

	const [typeMal, setTypeMal] = useState(MalTyper.OPPRETT)
	const [opprettMal, setOpprettMal] = useState(false)
	const { maler, loading } = useDollyMalerBrukerOgMalnavn(brukerId, null)

	if (loading) {
		return <Loading label="Laster maler..." />
	}

	const toggleValues = [
		{
			value: MalTyper.OPPRETT,
			label: 'Legg til ny',
		},
		{
			value: MalTyper.ENDRE,
			label: 'Overskriv eksisterende',
		},
	]

	const handleToggleChange = (value: MalTyper) => {
		setTypeMal(value)
		if (value === MalTyper.OPPRETT) {
			setFieldValue('malBestillingNavn', '')
		} else if (value === MalTyper.ENDRE) {
			setFieldValue('malBestillingNavn', opprettetFraMal || '')
		}
	}

	const handleCheckboxChange = (value: BaseSyntheticEvent) => {
		setFieldValue('malBestillingNavn', value.target?.checked ? '' : undefined)
		setOpprettMal(value.target?.checked)
	}

	return (
		<div className="input-oppsummering">
			<DollyCheckbox value={opprettMal} onChange={handleCheckboxChange} label={'Lagre som mal'} />
			{opprettMal && (
				<span>
					<div className="flexbox--align-center">
						<div className="toggle--wrapper">
							<ToggleGroup
								size={'small'}
								onChange={(value: MalTyper) => handleToggleChange(value)}
								defaultValue={MalTyper.OPPRETT}
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
				</span>
			)}
		</div>
	)
}

MalForm.validation = {
	malBestillingNavn: ifPresent('$malBestillingNavn', requiredString.nullable()),
}
